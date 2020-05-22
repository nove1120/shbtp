package com.nove1120.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.wrappers.PaySDKWrapper;
import com.nove1120.R;
import com.nove1120.adapter.AddressAdapter;
import com.nove1120.adapter.BuyerOrderAdapter;
import com.nove1120.adapter.OrderAdapter;
import com.nove1120.adapter.SellerOrderAdapter;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookOrder;
import com.nove1120.pojo.User;
import com.nove1120.pojo.UserAddress;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class OrderListActivity extends Activity {

    private String serverUrl;
    List<BookOrder> bookOrderList ;
    private ArrayAdapter adapter;

    private ImageView returnButton;
    private TextView titleText;
    private ListView orderListView;

    private int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",0);
        if(flag!=1&&flag!=2){
            finish();
        }
        init();
        initAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bookOrderList.clear();
        initData();
    }

    private void init(){
        serverUrl = getResources().getString(R.string.server_url);
        bookOrderList = new ArrayList<>();
        returnButton = (ImageView) findViewById(R.id.returnButton);
        titleText = (TextView) findViewById(R.id.titleText);
        orderListView = (ListView) findViewById(R.id.orderListView);
        if(flag==1){
            titleText.setText("我买到的");
        }else{
            titleText.setText("我卖出的");
        }
    }

    private void initAdapter(){
        if(flag==1) {
            adapter = new BuyerOrderAdapter(this, R.layout.activity_order_list, R.layout.order_item, bookOrderList);
        }else{
            adapter = new SellerOrderAdapter(this, R.layout.activity_order_list, R.layout.order_item, bookOrderList);
        }

        orderListView.setAdapter(adapter);

    }
    private void initData(){
        User user = SPUtil.getUser(OrderListActivity.this);
        String url=null;
        if(flag==1){
            url = serverUrl + "/getOrderByBuyerUid";
        }else{
            url = serverUrl + "/getOrderBySellerUid";
        }
        OkHttpUtils
                .post()
                .url(url)
                .addParams("uid", ""+user.getUid())
                .build()
                .execute(new  MyStringCallback() );
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
            Toast.makeText(OrderListActivity.this,"获取订单列表失败",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response, int id) {
            try {
                bookOrderList.addAll(new Gson().fromJson(response, new TypeToken<List<BookOrder>>() {}.getType()));
                adapter.notifyDataSetChanged();
            }catch (Exception e){
                Toast.makeText(OrderListActivity.this,"获取订单列表失败",Toast.LENGTH_LONG).show();
            }
        }

    }
}
