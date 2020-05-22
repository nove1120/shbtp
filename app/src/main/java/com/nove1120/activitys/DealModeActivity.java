package com.nove1120.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.E;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nove1120.R;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookOrder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.OptionalInt;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class DealModeActivity extends Activity {

    private String serverUrl;
    private final int REQUEST_CODE = 601;

    private ImageView returnButton;
    private ImageView bookImage;
    private TextView bookNameText;
    private TextView symbolText;
    private TextView priceText;
    private TextView addressTitleText;
    private TextView consigneeText;
    private TextView addressText;
    private TextView publisherText;

    private TextView faceDeal;
    private TextView expressDeal;

    private BookOrder bookOrder;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_mode);

        init();
        initData();
        initListener();
    }

    private void init(){
        serverUrl = getResources().getString(R.string.server_url);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        bookImage = (ImageView) findViewById(R.id.bookImage);
        bookNameText = (TextView) findViewById(R.id.bookNameText);
        symbolText = (TextView) findViewById(R.id.symbolText);
        priceText = (TextView) findViewById(R.id.priceText);
        addressTitleText = (TextView) findViewById(R.id.addressTitleText);
        consigneeText = (TextView) findViewById(R.id.consigneeText);
        addressText = (TextView) findViewById(R.id.addressText);
        publisherText = (TextView) findViewById(R.id.publisherText);

        faceDeal = (TextView) findViewById(R.id.faceDeal);
        expressDeal = (TextView) findViewById(R.id.expressDeal);
    }
    private void initListener(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        faceDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(DealModeActivity.this).setTitle("确定发货吗？")
                        .setMessage("确认后订单将进入已发货状态").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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
                                                Toast.makeText(DealModeActivity.this,"发货失败",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onResponse(String response, int id) {
                                                if(Integer.valueOf(response)<=0){
                                                    Toast.makeText(DealModeActivity.this,"发货失败",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                finish();
                                            }
                                        });
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
        expressDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DealModeActivity.this,InputExpressActivity.class);
                intent.putExtra("bookOrderJson",new Gson().toJson(bookOrder));
                startActivity(intent);
            }
        });
    }

    private void initData(){
        Intent intent = getIntent();
        String bookOrderJson = intent.getStringExtra("bookOrderJson");

        try{
            bookOrder = new Gson().fromJson(bookOrderJson,BookOrder.class);
            String url = serverUrl + "/getBookByBid";
            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("bid", ""+bookOrder.getBid())
                    .build()
                    .execute(new  StringCallback() {


                        @Override
                        public void onError(Call call, Exception e, int id) {
                            e.printStackTrace();

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if(response!=null){
                                book = new Gson().fromJson(response,Book.class);
                                if(book!=null){
                                    bookNameText.setText(book.getBook_name());
                                    priceText.setText(""+book.getPrice());
                                    publisherText.setText(book.getPublisher());
                                    Glide.with(DealModeActivity.this)
                                            .load(serverUrl+"/shbtpFile/bookImage/"+book.getBid()+"/"+book.getBookDesc().getPicture_url().split("\\$")[0])
                                            .into(bookImage);


                                }
                            }
                        }
                    });

            consigneeText.setText(bookOrder.getConsignee()+" "+bookOrder.getPhone_number());
            addressText.setText(bookOrder.getLocation()+" "+bookOrder.getAddress());

        }catch (Exception e){
            finish();
        }


    }
}
