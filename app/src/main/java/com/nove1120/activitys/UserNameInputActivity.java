package com.nove1120.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nove1120.R;
import com.nove1120.pojo.Book;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;

public class UserNameInputActivity extends Activity {

    private String serverUrl;
    private int resultCode = 104;

    private String oldUserName;
    private String nowUserName;
    private EditText userNameEdit;
    private TextView confirmButton;
    private ImageView returnButton;

    TextChange textChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_input);
        init();
        initListener();
    }
    private void init(){

        serverUrl = getResources().getString(R.string.server_url);

        userNameEdit = (EditText) findViewById(R.id.userNameEdit);
        confirmButton = (TextView) findViewById(R.id.confirmButton);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        textChange  = new TextChange();

        Intent intent = getIntent();
        oldUserName = intent.getStringExtra("oldUserName");
        nowUserName = intent.getStringExtra("nowUserName");
        if(nowUserName!=null&&nowUserName.length()>=4){
            userNameEdit.setText(nowUserName);
            userNameEdit.setSelection(nowUserName.length());
        }
    }
    private void initListener(){
        userNameEdit.addTextChangedListener(textChange);
        textChange.afterTextChanged(null);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString();
                if(oldUserName.equals(userName)){
                    Intent intent = getIntent();
                    intent.putExtra("username", userNameEdit.getText().toString());
                    setResult(resultCode, intent);
                    finish();
                }

                String url = serverUrl+"/checkUserName";
                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("userName", userName)
                        .build()
                        .execute(new MyStringCallback());
            }
        });
    }

    //EditText的监听器
    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String userName = userNameEdit.getText().toString();
            String patternUserName = "^(?!_)[\\u4e00-\\u9fa5a-zA-Z0-9_-]{4,16}$";
            if(userName!=null&&Pattern.matches(patternUserName,userName)){
                confirmButton.setTextColor(getColor(R.color.textColorBlack));
                confirmButton.setClickable(true);
            }else{
                confirmButton.setTextColor(getColor(R.color.textColorGray));
                confirmButton.setClickable(false);
            }
        }
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

        }

        @Override
        public void onResponse(String response, int id) {
            if("true".equals(response)){
                androidx.appcompat.app.AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(UserNameInputActivity.this).setTitle("提示")
                        .setMessage("用户名已经存在").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
                confirmButton.setClickable(false);
                confirmButton.setTextColor(getColor(R.color.textColorGray));
            }else{
                Intent intent = getIntent();
                intent.putExtra("username", userNameEdit.getText().toString());
                setResult(resultCode, intent);
                finish();
            }
        }

    }
}
