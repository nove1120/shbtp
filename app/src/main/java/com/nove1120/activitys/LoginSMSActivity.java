package com.nove1120.activitys;

        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.text.Editable;
        import android.text.TextUtils;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import androidx.annotation.Nullable;

        import com.google.gson.Gson;
        import com.hyphenate.EMCallBack;
        import com.hyphenate.chat.EMClient;
        import com.mob.MobSDK;
        import com.nove1120.R;
        import com.nove1120.pojo.User;
        import com.nove1120.utils.SPUtil;
        import com.zhy.http.okhttp.OkHttpUtils;
        import com.zhy.http.okhttp.callback.StringCallback;

        import cn.smssdk.EventHandler;
        import cn.smssdk.SMSSDK;
        import okhttp3.Call;
        import okhttp3.Request;

public class LoginSMSActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LoginSMSActivity";

    String serverUrl;
    EditText phoneNumberEdit;
    EditText verificationCodeEdit;
    Button deletePhoneNumberButton;
    Button getVerificationCodeButton;
    Button loginButton;
    Button passwordLoginButton;
    String phoneNumber;


    int countDown = 59;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms);

        init();
        MobSDKinit();
    }


    private void init(){
        phoneNumberEdit = (EditText) findViewById(R.id.phoneNumberEdit);
        deletePhoneNumberButton = (Button) findViewById(R.id.deletePhoneNumberButton);
        verificationCodeEdit = (EditText) findViewById(R.id.verificationCodeEdit);
        getVerificationCodeButton = (Button) findViewById(R.id.getVerificationCodeButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        passwordLoginButton = (Button) findViewById(R.id.passwordLoginButton);

        serverUrl = getResources().getString(R.string.server_url);

        deletePhoneNumberButton.setOnClickListener(this);
        getVerificationCodeButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        passwordLoginButton.setOnClickListener(this);

        phoneNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    deletePhoneNumberButton.setVisibility(View.VISIBLE);
                } else{
                    deletePhoneNumberButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick (View view){
        phoneNumber = phoneNumberEdit.getText().toString();
        switch (view.getId()) {
            case R.id.getVerificationCodeButton:
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(phoneNumber)) {
                    return;
                }
                // 2. 通过sdk发送短信验证
                // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
                SMSSDK.getVerificationCode("86", phoneNumber);

                // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                getVerificationCodeButton.setClickable(false);
                getVerificationCodeButton.setText("重新发送(" + countDown + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; countDown > 0; countDown--) {
                            handler.sendEmptyMessage(-9);
                            if (countDown <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                break;

            case R.id.loginButton:
                //将收到的验证码和手机号提交再次核对
                SMSSDK.submitVerificationCode("86", phoneNumber, verificationCodeEdit.getText().toString());
                break;
            case R.id.deletePhoneNumberButton:
                phoneNumberEdit.setText("");
                verificationCodeEdit.setText("");
                break;
            case R.id.passwordLoginButton:
                Intent intent = new Intent(LoginSMSActivity.this,LoginPWDActivity.class);
                startActivity(intent);

        }
    }












    private void MobSDKinit() {

        // 启动短信验证sdk
        MobSDK.init(this);
        // SMSSDK.initSDK(this, APPKEY, APPSECRETE);
//        SMSSDK.setAskPermisionOnReadContact(false);

        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
// 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     *
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                getVerificationCodeButton.setText("重新发送(" + countDown + ")");
            }
            else if (msg.what == -8) {
                getVerificationCodeButton.setText("获取验证码");
                getVerificationCodeButton.setClickable(true);
                countDown = 59;
            }
            else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
//                        Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
//                        向服务器验证手机号码是否存在

                        OkHttpUtils
                        .post()
                        .url(serverUrl + "/loginSMS")
                        .addParams("phone_number", phoneNumber)
                        .build()
                        .execute(new MyStringCallback());



                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码",
                                Toast.LENGTH_SHORT).show();
                    }
                }else if(result == SMSSDK.RESULT_ERROR){
                    Toast.makeText(getApplicationContext(), "验证码错误",
                            Toast.LENGTH_SHORT).show();
                }

            }

//            //测试代码
//            OkHttpUtils
//                    .post()
//                    .url(serverUrl + "/loginSMS")
//                    .addParams("phone_number", phoneNumber)
//                    .build()
//                    .execute(new MyStringCallback());
        }
    };


    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186
         * 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }
    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
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
//            Log.e(TAG, "onResponse：complete");

            Log.e(TAG,"res:"+response);
            Gson gson = new Gson();
            User user = gson.fromJson(response, User.class);
            if(user == null){
                Toast.makeText(LoginSMSActivity.this, "需要注册", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra("phoneNumber",phoneNumber);
                intent.setClass(LoginSMSActivity.this,RegisterActivity.class);
                startActivity(intent);
            }else{
//                Toast.makeText(LoginSMSActivity.this, "已经注册", Toast.LENGTH_SHORT).show();
//                将用户信息存储到sp
                SPUtil.putUser(LoginSMSActivity.this,user);

                EMClient.getInstance().login(""+user.getUid(), ""+user.getUid(), new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Intent intent=new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(LoginSMSActivity.this,MainActivity.class);
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
                    Toast.makeText(LoginSMSActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(LoginSMSActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
