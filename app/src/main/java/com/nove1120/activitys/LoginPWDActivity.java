
package com.nove1120.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.nove1120.R;
import com.nove1120.dto.LoginParam;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;
import com.nove1120.utils.ShaUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class LoginPWDActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private String serverUrl;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button delPwdButton;
    private Button loginButton;
    private Button resetButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pwd);
        accountEdit = (EditText) findViewById(R.id.accountEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        delPwdButton = (Button) findViewById(R.id.delPwdButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        serverUrl = getResources().getString(R.string.server_url);

        delPwdButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.loginButton:
                loginButtonClicked();
                break;
        }
    }


    public void loginButtonClicked(){
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String patternUserName = "^(?!_)[\\u4e00-\\u9fa5a-zA-Z0-9_-]{4,16}$";
        String patternPhoneNumber = "[1][358]\\d{9}";
        String patternEmail = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        String patternPassword = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{8,16}$";

        LoginParam loginParam = new LoginParam();
        if(!Pattern.matches("^\\d{1,}",account)&&Pattern.matches(patternUserName, account)){
            loginParam.setAccountType(LoginParam.AccountType.USER_NAME);
        }else if(Pattern.matches(patternPhoneNumber, account)){
            loginParam.setAccountType(LoginParam.AccountType.PHONE_NUMBER);
        }else if(Pattern.matches(patternEmail, account)){
            loginParam.setAccountType(LoginParam.AccountType.EMAIL);
        }else{
            Toast.makeText(LoginPWDActivity.this , "账号输入有误",Toast.LENGTH_LONG).show();
            return;
        }
        if(!Pattern.matches(patternPassword, password)) {
            Toast.makeText(LoginPWDActivity.this , "密码错误",Toast.LENGTH_LONG).show();
            return;
        }
        loginParam.setAccount(account);
        loginParam.setPassword(ShaUtil.getSha256(password));
        OkHttpUtils
                .postString()
                .url(serverUrl+"/loginAccount")
                .content(new Gson().toJson(loginParam))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
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
//            textView.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            Log.e(TAG,"res:"+response);
            Gson gson = new Gson();
            User user = gson.fromJson(response, User.class);
            if(user == null){
                Toast.makeText(LoginPWDActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }else{

//                Toast.makeText(LoginPWDActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                SPUtil.putUser(LoginPWDActivity.this,user);
                EMClient.getInstance().login(""+user.getUid(), ""+user.getUid(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Intent intent=new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(LoginPWDActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(int code, String error) {
                        Log.e("login","loginError",null);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });


            }

            switch (id) {
                case 100:
                    Toast.makeText(LoginPWDActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(LoginPWDActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}