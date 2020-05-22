package com.nove1120.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.wrappers.PaySDKWrapper;
import com.nove1120.R;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookOrder;
import com.nove1120.pojo.User;
import com.nove1120.pojo.UserAddress;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class CreateOrderActivity extends Activity {
    private String serverUrl;
    private final int REQUEST_CODE = 601;

    private ImageView returnButton;
    private ImageView bookImage;
    private TextView bookNameText;
    private TextView symbolText;
    private TextView priceText;
    private TextView addressTitleText;
    private ImageView addressRightImage;
    private TextView consigneeText;
    private TextView addressText;
    private TextView priceBottomText;
    private TextView confirmText;
    private TextView publisherText;

    private Book book;
    private User user;
    UserAddress selectAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        init();
        initBook();
        initAddress();
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
        addressRightImage = (ImageView) findViewById(R.id.addressRightImage);
        consigneeText = (TextView) findViewById(R.id.consigneeText);
        addressText = (TextView) findViewById(R.id.addressText);
        priceBottomText = (TextView) findViewById(R.id.priceBottomText);
        confirmText = (TextView) findViewById(R.id.confirmText);
        publisherText = (TextView) findViewById(R.id.publisherText);
    }
    private void initListener(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addressTitleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateOrderActivity.this,SelectAddressActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressTitleText.performClick();
            }
        });
        consigneeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressTitleText.performClick();
            }
        });
        addressRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressTitleText.performClick();
            }
        });

        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectAddress==null){
                    Toast.makeText(CreateOrderActivity.this,"请选选择收货地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                BookOrder bookOrder = new BookOrder();
                bookOrder.setBid(book.getBid());
                bookOrder.setSell_uid(book.getUid());
                bookOrder.setBuy_uid(user.getUid());
                bookOrder.setConsignee(selectAddress.getConsignee());
                bookOrder.setPhone_number(selectAddress.getPhone_number());
                bookOrder.setLocation(selectAddress.getLocation());
                bookOrder.setAddress(selectAddress.getAddress());
                Log.e("test", bookOrder.toString(),null );

                OkHttpUtils
                        .postString()
                        .url(serverUrl+"/bookOrderUpload")
                        .content(new Gson().toJson(bookOrder))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new MyOrderCallback());
            }
        });

    }
    private void initBook(){
        Intent intent = getIntent();
        String bookJson = intent.getStringExtra("bookJson");
        if(bookJson==null){
            finish();
        }
        book = new Gson().fromJson(bookJson,Book.class);
        if(book==null||book.getBid()==0){
            finish();
        }

        String[] pictureUrls = book.getBookDesc().getPicture_url().split("\\$");
        String url = serverUrl + "/shbtpFile/bookImage/"+book.getBid()+"/"+pictureUrls[0];
        Glide.with(this)
                .load(url)
                .into(bookImage);
        bookNameText.setText(book.getBook_name());
        priceText.setText(""+book.getPrice());
        priceBottomText.setText(""+book.getPrice());
        publisherText.setText(book.getPublisher());

    }

    private void initAddress(){
        user = SPUtil.getUser(CreateOrderActivity.this);
        if(user==null||user.getUid()==0){
            finish();
        }
        String url = serverUrl + "/getUserDefaultAddress";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("uid", ""+user.getUid())
                .build()
                .execute(new MyAddressCallback());
    }


    public class MyAddressCallback extends StringCallback {
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
            Toast.makeText(CreateOrderActivity.this,"获取地址失败",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response, int id) {
            if(response!=null){
                selectAddress = new Gson().fromJson(response,UserAddress.class);
                if(selectAddress!=null) {
                    consigneeText.setText(selectAddress.getConsignee() + " " + selectAddress.getPhone_number());
                    addressText.setText(selectAddress.getLocation() + " " + selectAddress.getAddress());
                }
            }
        }
    }

    public class MyOrderCallback extends StringCallback {
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
            Toast.makeText(CreateOrderActivity.this,"创建订单失败",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response, int id) {
            Log.e("test", response,null );
            if(response!=null){
                BookOrder order = new Gson().fromJson(response,BookOrder.class);
                if(order!=null) {
                    Intent intent = new Intent(CreateOrderActivity.this,CreateOrderCompleteActivity.class);
                    startActivity(intent);
                    return;
                }
            }
            Toast.makeText(CreateOrderActivity.this,"创建订单失败",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==602){
            String userAddressJson = data.getStringExtra("userAddressJson");
            if(userAddressJson!=null&&userAddressJson.length()!=0){
                selectAddress = new Gson().fromJson(userAddressJson, UserAddress.class);
                consigneeText.setText(selectAddress.getConsignee()+" "+selectAddress.getPhone_number());
                addressText.setText(selectAddress.getLocation()+" "+selectAddress.getAddress());
            }

        }
    }
}
