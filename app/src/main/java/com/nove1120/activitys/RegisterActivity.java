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
import com.hyphenate.exceptions.HyphenateException;
import com.nove1120.R;
import com.nove1120.dto.RegisterParam;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;
import com.nove1120.utils.ShaUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class RegisterActivity extends Activity  implements View.OnClickListener{
    private static final String TAG = "RegisterActivity";

    String serverUrl;
    EditText password1Edit;
    EditText password2Edit;
    Button delPassword1Button;
    Button delPassword2Button;
    Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        password1Edit = (EditText) findViewById(R.id.password1Edit);
        password2Edit = (EditText) findViewById(R.id.password2Edit);
        delPassword1Button = (Button) findViewById(R.id.delPassword1Button);
        delPassword2Button = (Button) findViewById(R.id.delPassword2Button);
        registerButton = (Button) findViewById(R.id.registerButton);
        serverUrl = getResources().getString(R.string.server_url);




        delPassword1Button.setOnClickListener(this);
        delPassword2Button.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.registerButton:
                registerButtonClicked();
                break;
        }
    }

    private void registerButtonClicked(){
        String password1 = password1Edit.getText().toString();
        String password2 = password2Edit.getText().toString();

        String pattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{8,16}$";
        if(Pattern.matches(pattern, password1)){
            if(password1.equals(password2)){
                Intent intent = getIntent();
                String phoneNumber = intent.getStringExtra("phoneNumber");
                Toast.makeText(RegisterActivity.this,phoneNumber,Toast.LENGTH_LONG).show();
                if(phoneNumber==null){
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_LONG).show();
                }
                RegisterParam registerParam = new RegisterParam();
                registerParam.setPhoneNumber(phoneNumber);
                registerParam.setPassword(ShaUtil.getSha256(password1));
                OkHttpUtils
                        .postString()
                        .url(serverUrl+"/register")
                        .content(new Gson().toJson(registerParam))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new MyStringCallback());
            }else{
                Toast.makeText(RegisterActivity.this,"密码输入不一致",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(RegisterActivity.this,"密码过于简单",Toast.LENGTH_LONG).show();
        }

    }


    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
        }

        @Override
        public void onAfter(int id) {

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
                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().createAccount(""+user.getUid(),""+user.getUid());
                        } catch (HyphenateException e) {
                            Log.e("login","registerError",null);
                            e.printStackTrace();
                        }
                    }
                }).start();




                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                SPUtil.putUser(RegisterActivity.this,user);

                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(RegisterActivity.this,MainActivity.class);
                startActivity(intent);

            }

            switch (id) {
                case 100:
                    Toast.makeText(RegisterActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(RegisterActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
