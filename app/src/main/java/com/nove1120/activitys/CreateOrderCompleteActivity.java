package com.nove1120.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nove1120.R;

public class CreateOrderCompleteActivity extends Activity {
    private ImageView returnButton;
    private TextView returnButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order_complete);
        init();
        initListener();

    }
    private void init(){
        returnButton = (ImageView) findViewById(R.id.returnButton);
        returnButton2 = (TextView) findViewById(R.id.returnButton2);
    }
    private void initListener(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(CreateOrderCompleteActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        returnButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(CreateOrderCompleteActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //1.重写onBackPressed方法组织super即可实现禁止返回上一层页面
    public void onBackPressed(){
        //super.onBackPressed();
    }
}
