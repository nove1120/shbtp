package com.nove1120.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nove1120.R;
import com.nove1120.pojo.User;
import com.nove1120.pojo.UserAccount;
import com.nove1120.pojo.UserAddress;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;



public class AddressInputActivity extends Activity {
    private final int REQUEST_CODE = 501;
    private String serverUrl;

    private UserAddress userAddress;
    private ImageView returnButton;
    private TextView confirmButton;
    private EditText consigneeEdit;
    private EditText phoneNumberEdit;
    private TextView locationText;
    private EditText addressEdit;
    private CheckBox defaultCheck;
    private TextView deleteText;
    private TextChange textChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_input);
        init();
        initData();
        initListener();
    }
    private void init(){

        returnButton = (ImageView) findViewById(R.id.returnButton);
        confirmButton = (TextView) findViewById(R.id.confirmButton);
        consigneeEdit = (EditText) findViewById(R.id.consigneeEdit);
        phoneNumberEdit = (EditText) findViewById(R.id.phoneNumberEdit);
        locationText = (TextView) findViewById(R.id.locationText);
        addressEdit = (EditText) findViewById(R.id.addressEdit);
        defaultCheck = (CheckBox) findViewById(R.id.defaultCheck);
        deleteText = (TextView) findViewById(R.id.deleteText);
    }

    private void initListener(){
        textChange = new TextChange();
        consigneeEdit.addTextChangedListener(textChange);
        phoneNumberEdit.addTextChangedListener(textChange);
        locationText.addTextChangedListener(textChange);
        addressEdit.addTextChangedListener(textChange);
        textChange.afterTextChanged(null);
        setEditTextInhibitInputSpeChat(consigneeEdit);
        setEditTextInhibitInputSpeChat(addressEdit);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressInputActivity.this,ShowRegionActivity.class);
                intent.putExtra("address",locationText.getText().toString());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String consignee = consigneeEdit.getText().toString().trim();
                String phoneNumber = phoneNumberEdit.getText().toString().trim();
                String location = locationText.getText().toString().trim();
                String address = addressEdit.getText().toString().trim();
                if (!judgePhoneNums(phoneNumber)){
                    return;
                }
                if(address.length()<5){
                    Toast.makeText(AddressInputActivity.this,"详细地址过短",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(consignee.length()>15){
                    Toast.makeText(AddressInputActivity.this,"收货人姓名过长",Toast.LENGTH_SHORT).show();
                    return;
                }
                userAddress.setUid(SPUtil.getUser(AddressInputActivity.this).getUid());
                userAddress.setConsignee(consignee);
                userAddress.setPhone_number(phoneNumber);
                userAddress.setLocation(location.replaceAll(" ","$"));
                userAddress.setAddress(address);
                if(defaultCheck.isChecked()){
                    userAddress.setDefault_flag(1);
                }

                String url = serverUrl+"/uploadUserAddress";
                OkHttpUtils
                        .postString()
                        .url(url)
                        .content(new Gson().toJson(userAddress))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new MyStringCallback());
            }
        });
    }



    private void initData(){
        serverUrl = getResources().getString(R.string.server_url);
        userAddress = new UserAddress();
        Intent intent = getIntent();
        String userAddressJson = intent.getStringExtra("userAddressJson");
        if(userAddressJson!=null&&userAddressJson.length()!=0) {
            try {
                userAddress = new Gson().fromJson(userAddressJson, UserAddress.class);
            } catch (Exception e) {
                Log.e("addressinput", "解析错误 ",null );
                finish();
            }
//            Log.e("addressinput", userAddress.getConsignee(),null );
////            consigneeEdit.setText("asdasd");
            consigneeEdit.setText(userAddress.getConsignee());
            phoneNumberEdit.setText(userAddress.getPhone_number());
            locationText.setText(userAddress.getLocation().replaceAll("\\$", " ").trim());
            addressEdit.setText(userAddress.getAddress());
            if (userAddress.getDefault_flag() == 1) {
                defaultCheck.setChecked(true);
            }

        }

    }
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }
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
    /**
     * 禁止EditText输入特殊字符
     * @param editText
     */
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find()||editText.getText().length()>40){
                    return "";
                }
                else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});

    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(consigneeEdit.getText()==null|| phoneNumberEdit.getText()==null||
                    locationText.getText()==null|| addressEdit.getText()==null||
                    consigneeEdit.getText().toString().trim().length()==0||
                    phoneNumberEdit.getText().toString().length()!=11||
                    locationText.getText().toString().trim().length()==0||
                    addressEdit.getText().toString().trim().length()==0){
                confirmButton.setTextColor(getColor(R.color.textColorGray));
                confirmButton.setClickable(false);
            }else{
                confirmButton.setTextColor(getColor(R.color.textColorBlack));
                confirmButton.setClickable(true);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==102){
            locationText.setText(data.getStringExtra("address"));
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
            Toast.makeText(AddressInputActivity.this,"修改失败",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onResponse(String response, int id) {
            if(response!=null&&response.length()!=0) {
                finish();
            }else{
                Toast.makeText(AddressInputActivity.this,"修改失败",Toast.LENGTH_LONG).show();
            }
        }

    }
}
