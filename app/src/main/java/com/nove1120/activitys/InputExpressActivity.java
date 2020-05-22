package com.nove1120.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.A;
import com.baidu.platform.comapi.map.B;
import com.google.gson.Gson;
import com.nove1120.R;
import com.nove1120.pojo.BookOrder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

public class InputExpressActivity extends Activity {
    private String serverUrl;
    EditText expressCompanyEdit;
    EditText expressNumberEdit;
    TextView confirmText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_express);
        serverUrl = getResources().getString(R.string.server_url);
        Intent intent = getIntent();
        String bookOrderJson = intent.getStringExtra("bookOrderJson");
        BookOrder bookOrder = new Gson().fromJson(bookOrderJson,BookOrder.class);

        expressCompanyEdit = (EditText) findViewById(R.id.expressCompanyEdit);
        expressNumberEdit = (EditText) findViewById(R.id.expressNumberEdit);


        confirmText = (TextView) findViewById(R.id.confirmText);
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expressCompanyEdit.getText()!=null&&expressCompanyEdit.getText().toString().length()!=0&&
                    expressNumberEdit.getText()!=null&&expressNumberEdit.getText().toString().length()!=0) {
                    bookOrder.setExpress_company(expressCompanyEdit.getText().toString());
                    bookOrder.setExpress_number(expressNumberEdit.getText().toString());

                    bookOrder.setState(2);
                    OkHttpUtils
                            .postString()
                            .url(serverUrl+"/bookOrderUpdate")
                            .content(new Gson().toJson(bookOrder))
                            .mediaType(MediaType.parse("application/json; charset=utf-8"))
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(InputExpressActivity.this,"发货失败",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    if(Integer.valueOf(response)<=0){
                                        Toast.makeText(InputExpressActivity.this,"发货失败",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    finish();
                                    Intent intent=new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(InputExpressActivity.this, OrderListActivity.class);
                                    intent.putExtra("flag",2);
                                    startActivity(intent);
                                }
                            });


                }
            }
        });
    }
}
