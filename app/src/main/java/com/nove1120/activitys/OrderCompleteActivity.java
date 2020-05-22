package com.nove1120.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nove1120.R;

public class OrderCompleteActivity extends Activity {
    private ImageView returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);
        init();
        initListener();

    }
    private void init(){
        returnButton = (ImageView) findViewById(R.id.returnButton);
    }
    private void initListener(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(OrderCompleteActivity.this,OrderListActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
