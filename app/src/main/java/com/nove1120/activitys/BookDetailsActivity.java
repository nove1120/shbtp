package com.nove1120.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.nove1120.R;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookDesc;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.utils.L;

import okhttp3.Call;
import okhttp3.MediaType;

public class BookDetailsActivity extends Activity {

    private String serverUrl;
    private Book book;

    private ConstraintLayout constraintLayout;

    private CardView faceCardView;
    private ImageView faceImage;
    private TextView userNameText;
    private TextView locationText;

    private TextView bookNameText;
    private TextView symbolText;
    private TextView priceText;
    private TextView flagNewText;

    private TextView authorText;
    private TextView publisherText;
    private TextView publishTimeText;
    private TextView ISBNTitleText;
    private TextView ISBNText;

    private TextView bookDescText;
    private LinearLayout descImageLayout;

    private ImageView collectImage;
    private TextView collectText;
    private TextView chatText;
    private TextView buyNowText;
    private TextView updateText;
    private TextView deleteText;

    private ImageView returnButton;

    private boolean collectTextClickFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);



        init();
        initListener();
        initBookText();
        initBookImage();
    }

    private void init(){
        serverUrl = getResources().getString(R.string.server_url);

        faceCardView = (CardView) findViewById(R.id.faceCardView);
        faceImage = (ImageView) findViewById(R.id.faceImage);
        userNameText = (TextView) findViewById(R.id.userNameText);
        locationText = (TextView) findViewById(R.id.locationText);

        bookNameText = (TextView) findViewById(R.id.bookNameText);
        symbolText = (TextView) findViewById(R.id.symbolText);
        priceText = (TextView) findViewById(R.id.priceText);
        flagNewText = (TextView) findViewById(R.id.flagNewText);

        authorText = (TextView) findViewById(R.id.authorText);
        publisherText = (TextView) findViewById(R.id.publisherText);
        publishTimeText = (TextView) findViewById(R.id.publishTimeText);
        ISBNTitleText = (TextView) findViewById(R.id.ISBNTitleText);
        ISBNText = (TextView) findViewById(R.id.ISBNText);

        bookDescText = (TextView) findViewById(R.id.bookDescText);
        descImageLayout = (LinearLayout) findViewById(R.id.descImageLayout);

        collectImage = (ImageView) findViewById(R.id.collectImage);
        collectText = (TextView) findViewById(R.id.collectText);
        chatText = (TextView) findViewById(R.id.chatText);
        buyNowText = (TextView) findViewById(R.id.buyNowText);
        updateText = (TextView) findViewById(R.id.updateText);
        deleteText = (TextView) findViewById(R.id.deleteText);

        returnButton = (ImageView) findViewById(R.id.returnButton);

        String bookJson = getIntent().getStringExtra("bookJson");
        if(bookJson==null||bookJson.length()==0){
            finish();
        }
        Gson gson = new Gson();
        book = gson.fromJson(bookJson,Book.class);
        if(book==null||book.getBid()==0|| book.getBook_name()==null||
                book.getBook_name().length()==0|| book.getBookDesc()==null||
                book.getBookDesc().getText_desc()==null|| book.getBookDesc().getText_desc().length()==0||
                book.getBookDesc().getPicture_url()==null|| book.getBookDesc().getPicture_url().length()==0){
            finish();
        }
        User user = SPUtil.getUser(BookDetailsActivity.this);
        if(user==null){
            finish();
        }
        if(user.getUid()==book.getUid()){
            chatText.setVisibility(View.GONE);
            buyNowText.setVisibility(View.GONE);
            collectImage.setVisibility(View.GONE);
            collectText.setVisibility(View.GONE);
            updateText.setVisibility(View.VISIBLE);
            deleteText.setVisibility(View.VISIBLE);


            userNameText.setText(user.getUser_name());
            if(user.getLocation()!=null&&user.getLocation().length()!=0){
                String[] locs = user.getLocation().split("\\$");
                if(locs.length==3) {
                    locationText.setText(locs[1]);
                }
                if(locs.length==4){
                    locationText.setText(locs[1]+"•"+locs[3]);
                }
            }
            Glide.with(BookDetailsActivity.this).load(serverUrl+"/shbtpFile/userImage/"+user.getUid()+"/"+user.getFace()).into(faceImage);
        }else{
            String url = serverUrl+"/getUser";
            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("uid", ""+book.getUid())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try{
                                User user = new Gson().fromJson(response,User.class);
                                userNameText.setText(user.getUser_name());
                                if(user.getLocation()!=null&&user.getLocation().length()!=0){
                                    String[] locs = user.getLocation().split("\\$");
                                    if(locs.length==3) {
                                        locationText.setText(locs[1]);
                                    }
                                    if(locs.length==4){
                                        locationText.setText(locs[1]+"•"+locs[3]);
                                    }
                                }
                                Glide.with(BookDetailsActivity.this).load(serverUrl+"/shbtpFile/userImage/"+user.getUid()+"/"+user.getFace()).into(faceImage);
                            }catch (Exception e){

                            }
                        }
                    });
        }

    }

    private void initListener(){
        faceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid",book.getUid());
                startActivity(intent);
            }
        });
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceCardView.performClick();
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookDetailsActivity.this, ChatSingleActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, ""+book.getUid()));
            }
        });
        collectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(collectTextClickFlag){
                    collectImage.setBackground(getDrawable(R.drawable.ic_collect));
                    collectTextClickFlag=false;
                }else{
                    collectImage.setBackground(getDrawable(R.drawable.ic_collect_click));
                    collectTextClickFlag=true;
                }
            }
        });
        collectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectImage.performClick();
            }
        });

        buyNowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this,CreateOrderActivity.class);
                intent.putExtra("bookJson",new Gson().toJson(book));
                startActivity(intent);
            }
        });
        updateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(BookDetailsActivity.this).setTitle("确定删除吗？")
                        .setMessage("确认后将删除商品").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                book.setState(1);
                                OkHttpUtils
                                        .postString()
                                        .url(serverUrl+"/updateBook")
                                        .content(new Gson().toJson(book))
                                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                        .build()
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onError(Call call, Exception e, int id) {
                                                Toast.makeText(BookDetailsActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onResponse(String response, int id) {
                                                if(Integer.valueOf(response)<=0){
                                                    Toast.makeText(BookDetailsActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
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
            }
        });



    }
    private void initBookText(){
        bookNameText.setText(book.getBook_name());
        if(book.getPrice()!=0){
            priceText.setText(""+book.getPrice());
        }else{
            symbolText.setVisibility(View.INVISIBLE);
            priceText.setText("免费送");
        }
        if(book.getGrade()==0){
            flagNewText.setVisibility(View.VISIBLE);
        }
        authorText.setText(book.getAuthor());
        publisherText.setText(book.getPublisher());
        publishTimeText.setText(book.getPublishingTime());
        if(book.getISBN()==null||book.getISBN().length()==0){
            ISBNTitleText.setVisibility(View.GONE);
            ISBNText.setVisibility(View.GONE);
        }else{
            ISBNText.setText(book.getISBN());
        }
        bookDescText.setText(book.getBookDesc().getText_desc());

    }
    private void initBookImage(){
        BookDesc bookDesc = book.getBookDesc();
        String imageNames[] = bookDesc.getPicture_urlArray();
        for(int i=0 ; i<imageNames.length ; i++){
            String imageName = imageNames[i];
            String url = serverUrl+"/shbtpFile/bookImage/"+book.getBid()+"/"+imageName;
            ImageView imageView = new ImageView(this);
//        imageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.t01, null));
            imageView.setId(View.generateViewId());
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            Glide.with(this)
                    .load(url)
                    .into(imageView);
            descImageLayout.addView(imageView);
            if(i!=imageNames.length-1){
                View view = new View(this);
                view.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 2));
                descImageLayout.addView(view);
            }
        }

    }
}
