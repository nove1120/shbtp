package com.nove1120.activitys;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.nove1120.R;
import com.nove1120.widget.MySpinner;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class PersonalInfoActivity extends Activity {

    private String serverUrl;
    private static final int REQUEST_CODE = 201;

    ImageView returnButton;

    private TextView faceText;
    private ImageView faceImage;
    private ImageView faceRightImage;

    private TextView userNameText;
    private TextView userNameEdit;
    private ImageView userNameRightImage;

    private TextView genderText;
    private TextView genderEdit;
    private MySpinner genderSpinner;
    private ImageView genderRightImage;

    private TextView birthdayText;
    private TextView birthdayEdit;
    private ImageView birthdayRightImage;

    private TextView locationText;
    private TextView locationEdit;
    private ImageView locationRightImage;

    private TextView schoolText;
    private TextView schoolEdit;
    private ImageView schoolRightImage;

    private TextView confirmButton;

    private boolean isGenderSpinnerFirst = true;

    private String schoolAddr="";

    private User oldUser;
    private User newUser;

    private TextChange textChange;

    private LocalMedia faceLocalMedia;

    //记录加载之后各个选项的状态 在textwatcher中判断选项是否被修改
    private String oldUserName="";
    private String oldGender="";
    private String oldBirthday="";
    private String oldLocation="";
    private String oldSchool="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        init();
        initData();
        initListener();
        saveOriginalState();
    }

    private  void init(){
        serverUrl = getResources().getString(R.string.server_url);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        faceText = (TextView) findViewById(R.id.faceText);
        faceImage = (ImageView) findViewById(R.id.faceImage);
        faceRightImage = (ImageView) findViewById(R.id.faceRightImage);

        userNameText = (TextView) findViewById(R.id.userNameText);
        userNameEdit = (TextView) findViewById(R.id.userNameEdit);
        userNameRightImage = (ImageView) findViewById(R.id.userNameRightImage);

        genderText = (TextView) findViewById(R.id.genderText);
        genderEdit = (TextView) findViewById(R.id.genderEdit);
        genderSpinner = (MySpinner) findViewById(R.id.genderSpinner);
        genderRightImage = (ImageView) findViewById(R.id.genderRightImage);

        birthdayText = (TextView) findViewById(R.id.birthdayText);
        birthdayEdit = (TextView) findViewById(R.id.birthdayEdit);
        birthdayRightImage = (ImageView) findViewById(R.id.birthdayRightImage);

        locationText = (TextView) findViewById(R.id.locationText);
        locationEdit = (TextView) findViewById(R.id.locationEdit);
        locationRightImage = (ImageView) findViewById(R.id.locationRightImage);

        schoolText = (TextView) findViewById(R.id.schoolText);
        schoolEdit = (TextView) findViewById(R.id.schoolEdit);
        schoolRightImage = (ImageView) findViewById(R.id.schoolRightImage);

        confirmButton = (TextView) findViewById(R.id.confirmButton);

        textChange = new TextChange();
    }

    private  void initListener(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        faceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相册
                PictureSelector.create(PersonalInfoActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .imageSpanCount(4)
                        .selectionMode(PictureConfig.SINGLE)
                        .compress(true)

                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceText.performClick();
            }
        });
        faceRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceText.performClick();
            }
        });

        userNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalInfoActivity.this,UserNameInputActivity.class);
                intent.putExtra("oldUserName",oldUser.getUser_name().trim());
                intent.putExtra("nowUserName",userNameEdit.getText().toString().trim());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });


        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameEdit.performClick();
            }
        });
        userNameRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameEdit.performClick();
            }
        });

        genderEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderSpinner.setVisibility(View.VISIBLE);
                genderSpinner.performClick();
            }
        });

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderSpinner.setVisibility(View.INVISIBLE);
                if(isGenderSpinnerFirst){
                    isGenderSpinnerFirst=false;
                    return;
                }
                String[] genderSelect  = getResources().getStringArray(R.array.genderSelect);
                String provinceSelect = genderSelect[position];
                genderEdit.setText(provinceSelect);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        genderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderEdit.performClick();
            }
        });

        genderRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderEdit.performClick();
            }
        });

        birthdayEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = birthdayEdit.getText().toString();
                String[] ts = {"2000","0","1"};
                if(t!=null&&!"未选择".equals(t)){
                    String[] strs = t.split("-");
                    if(strs.length==3)
                        ts = strs;
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalInfoActivity.this,DatePickerDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        birthdayEdit.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                    }
                },Integer.valueOf(ts[0]),Integer.valueOf(ts[1])-1,Integer.valueOf(ts[2]));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(new Date().getTime());
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date;
                try {
                    date = sdf.parse("1900-0-31");
                    datePicker.setMinDate(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                datePickerDialog.show();
            }
        });

        birthdayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdayEdit.performClick();
            }
        });

        birthdayRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdayEdit.performClick();
            }
        });

        locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalInfoActivity.this,ShowRegionActivity.class);
                intent.putExtra("address",locationEdit.getText().toString());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationEdit.performClick();
            }
        });

        locationRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationEdit.performClick();
            }
        });

        schoolEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalInfoActivity.this,SelectSchoolActivity.class);
                intent.putExtra("school",schoolAddr);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        schoolText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolEdit.performClick();
            }
        });

        schoolRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolEdit.performClick();
            }
        });



        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString().trim();
                String gender = genderEdit.getText().toString().trim();
                String birthday = birthdayEdit.getText().toString();
                String location = locationEdit.getText().toString();
                String school = schoolEdit.getText().toString();
                newUser.setUid(oldUser.getUid());
//                newUser.setPhone_number(oldUser.getPhone_number());
                if(newUser.getFace()==null||newUser.getFace().length()==0){
                    newUser.setFace("");
                }
                if(userName!=null&&userName.length()!=0)
                    newUser.setUser_name(userName);
                if(gender!=null&&gender.length()!=0&&!"未选择".equals(gender)) {
                    int genderInt = 0;
                    if("男".equals(gender)){
                        genderInt=1;
                    }else if("女".equals(gender)){
                        genderInt=2;
                    }
                    newUser.setGender(genderInt);
                }
                if(birthday!=null&&birthday.length()!=0&&!"未选择".equals(birthday))
                    newUser.setBirthday(birthday.trim());

                if(location!=null&&location.length()!=0&&!"未选择".equals(location))
                    newUser.setLocation( location.replace(" ","$").trim());

                if(school!=null&&school.length()!=0&&!"未选择".equals(school))
                    newUser.setSchool(school.trim());

                String url = serverUrl+"/updateUser";
                OkHttpUtils
                        .postString()
                        .url(url)
                        .content(new Gson().toJson(newUser))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new MyStringCallback());
            }
        });

        userNameEdit.addTextChangedListener(textChange);
        genderEdit.addTextChangedListener(textChange);
        birthdayEdit.addTextChangedListener(textChange);
        locationEdit.addTextChangedListener(textChange);
        schoolEdit.addTextChangedListener(textChange);

    }

    private void initData(){
        newUser = new User();
        oldUser = SPUtil.getUser(this);
        if(oldUser!=null&&oldUser.getUid()!=0){
            if(oldUser.getUser_name()!=null){
                userNameEdit.setText(oldUser.getUser_name().trim());
            }
            if(oldUser.getGender()==1){
                genderEdit.setText("男");
            }
            if(oldUser.getGender()==2){
                genderEdit.setText("女");
            }
            if(oldUser.getBirthday()!=null&&oldUser.getBirthday().length()!=0){
                birthdayEdit.setText(oldUser.getBirthday().trim());
            }
            if(oldUser.getLocation()!=null){
                locationEdit.setText(oldUser.getLocation().replaceAll("\\$"," ").trim());
            }
            if(oldUser.getSchool()!=null){
                schoolEdit.setText(oldUser.getSchool().trim());
            }
            if(oldUser.getFace()!=null&&oldUser.getFace().length()!=0){
                String url = serverUrl+"/shbtpFile/userImage/"+oldUser.getUid()+"/"+oldUser.getFace();
                Glide.with(PersonalInfoActivity.this)
                        .load(url)
                        .into(faceImage);
            }
        }


    }

    private void saveOriginalState(){
        oldUserName = userNameEdit.getText().toString().trim();
        oldGender = genderEdit.getText().toString().trim();
        oldBirthday = birthdayEdit.getText().toString().trim();
        oldLocation = locationEdit.getText().toString().trim();
        oldSchool = schoolEdit.getText().toString().trim();
        textChange.afterTextChanged(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                images = PictureSelector.obtainMultipleResult(data);

                if(images.size()>0){
                    faceLocalMedia = images.get(0);
                    String path = faceLocalMedia.getCompressPath();
                    faceImage.setImageURI(Uri.fromFile(new File(path)));
                    String str[] = path.split("/");
                    String imageName = str[str.length-1];
                    newUser.setFace(imageName);
                    textChange.afterTextChanged(null);
                }


                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

            }
        }

        if(requestCode==REQUEST_CODE&&resultCode==102){
            locationEdit.setText(data.getStringExtra("address"));
        }
        if(requestCode==REQUEST_CODE&&resultCode==103){
            schoolAddr = data.getStringExtra("school");
            String[] addrs = schoolAddr.split(" ");
            schoolEdit.setText(addrs[2]);
        }
        if(requestCode==REQUEST_CODE&&resultCode==104){
            String username = data.getStringExtra("username");
            userNameEdit.setText(username);
        }
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
            if(!oldUserName.equals(userNameEdit.getText().toString())||
                    !oldGender.equals(genderEdit.getText().toString())||
                    !oldBirthday.equals(birthdayEdit.getText().toString())||
                    !oldLocation.equals(locationEdit.getText().toString())||
                    !oldSchool.equals(schoolEdit.getText().toString())||
                    !(newUser.getFace()==null||newUser.getFace().length()==0)){
                confirmButton.setClickable(true);
                confirmButton.setTextColor(getColor(R.color.textColorBlack));
            }else{
                confirmButton.setClickable(false);
                confirmButton.setTextColor(getColor(R.color.textColorGray));
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
            Toast.makeText(PersonalInfoActivity.this,"修改失败",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            User user = gson.fromJson(response, User.class);
            if(user==null){
                Toast.makeText(PersonalInfoActivity.this,"修改失败",Toast.LENGTH_LONG).show();
            }else{
                SPUtil.putUser(PersonalInfoActivity.this,user);
                if(newUser.getFace()!=null&&newUser.getFace().length()!=0) {
                    File file = null;
                    if (faceLocalMedia.isCompressed()) {
                        file = new File(faceLocalMedia.getCompressPath());

                    } else {
                        file = new File(faceLocalMedia.getPath());
                    }


                    OkHttpUtils.post()//
                            .addParams("uid", "" + user.getUid())
                            .addFile("file", file.getName(), file)//
                            .url(serverUrl + "/userFaceUpload")
                            .build()//
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    Toast.makeText(PersonalInfoActivity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    if ("true".equals(response)) {
                                        finish();
                                    } else {
                                        Toast.makeText(PersonalInfoActivity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                finish();
            }
        }

    }
}
