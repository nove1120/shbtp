package com.nove1120.activitys;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nove1120.R;
import com.nove1120.adapter.AddressAdapter;
import com.nove1120.pojo.User;
import com.nove1120.pojo.UserAddress;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class AddressActivity extends Activity {
    private String serverUrl;
    private ListView addressListView;
    private List<UserAddress> addressList;
    private AddressAdapter addressAdapter;
    private ImageView returnButton;
    private TextView addAddressButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        init();
        initAdapter();
        initListener();
    }

    private void init(){
        addressListView = (ListView) findViewById(R.id.addressListView);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        addAddressButton = (TextView) findViewById(R.id.addAddressButton);
    }

    private void initListener(){
        addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddressActivity.this,AddressInputActivity.class);
                Gson gson = new Gson();
                String userAddressJson="";
                if(addressList.get(position)!=null) {
                    userAddressJson = gson.toJson(addressList.get(position));
                }
                if(userAddressJson!=null&&userAddressJson.length()!=0){
                    intent.putExtra("userAddressJson",userAddressJson);
                    startActivity(intent);
                }else{
                    return;
                }
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this,AddressInputActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initAdapter(){
        serverUrl = getResources().getString(R.string.server_url);
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this,R.layout.activity_address,R.layout.address_item,addressList);
        addressListView.setAdapter(addressAdapter);
    }

    private void requestUserAddressList(){
        User user = SPUtil.getUser(this);
        if(user == null||user.getUid()==0){
            finish();
        }
        String url = serverUrl + "/getUserAddress";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("uid", ""+user.getUid())
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
//            setTitle("loading...");
        }

        @Override
        public void onAfter(int id) {
//            setTitle("Sample-okHttp");

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            Toast.makeText(AddressActivity.this,"获取地址列表失败",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response, int id) {
            try {
                addressList.addAll(new Gson().fromJson(response, new TypeToken<List<UserAddress>>() {}.getType()));
                addressAdapter.notifyDataSetChanged();
            }catch (Exception e){
                Toast.makeText(AddressActivity.this,"获取地址列表失败",Toast.LENGTH_LONG).show();
            }
        }



    }


    @Override
    protected void onResume() {
        super.onResume();
        addressList.clear();
        requestUserAddressList();
    }
}
