package com.nove1120.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.Permission;
import com.luck.picture.lib.permissions.RxPermissions;
import com.nove1120.R;
import com.nove1120.adapter.GridImageAdapter;
import com.nove1120.filter.PriceFilter;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookDesc;
import com.nove1120.pojo.User;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class UploadActivity extends Activity  {
    private int requestCode=111;//请求码
    private Boolean resultDescFlag = false;
    private Boolean resultImageFlag = false;


    private int maxSelectNum = 9;
    private List<LocalMedia> selectList = new ArrayList<>();
    private GridImageAdapter adapter;
    private RecyclerView mRecyclerView;
    private PopupWindow pop;

    TextView bookBaseText;
    ImageView returnButton;
    TextView uploadButton;
    EditText descEdit;
    EditText priceEdit;
    Spinner gradeSpinner;
    TextChange textChange;
    LinearLayout bookBaseLayout;
    LinearLayout gradeLayout;

    private static Book bookUpload;
    int gradeSpinnerSelect = -1;
    boolean isGradeSpinnerFirst=true;
    String serverUrl;

    boolean priceDialogShowFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        bookUpload = new Book();
        init();
        initWidget();
        setListener();
    }
    private void init(){
        serverUrl= getResources().getString(R.string.server_url);
        mRecyclerView = findViewById(R.id.recycler);
        bookBaseLayout = (LinearLayout) findViewById(R.id.bookBaseLayout);
        bookBaseText = (TextView) findViewById(R.id.bookBaseText);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        uploadButton = (TextView) findViewById(R.id.uploadButton);
        descEdit = (EditText) findViewById(R.id.descEdit);
        priceEdit = (EditText) findViewById(R.id.priceEdit);
        gradeSpinner = (Spinner) findViewById(R.id.gradeSpinner);
        gradeLayout = (LinearLayout) findViewById(R.id.gradeLayout);

        PriceFilter priceFilter= new PriceFilter();
        priceFilter.setDecimalLength(1);
        priceFilter.setMaxValue(99999999);
        priceEdit.setFilters(new InputFilter[]{priceFilter});
        gradeSpinner.setVisibility(View.INVISIBLE);



    }

    private void setListener(){

        textChange = new TextChange();
        descEdit.addTextChangedListener(textChange);
        priceEdit.addTextChangedListener(textChange);

        bookBaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this,BookBaseEditActivity.class);
                startActivityForResult(intent,requestCode);
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(UploadActivity.this).setTitle("要退出编辑吗？")
                        .setMessage("已编写的内容将不会保存").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
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
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectList.size()==0){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(UploadActivity.this).setTitle("提示")
                            .setMessage("至少上传一张图片").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();

                    resultImageFlag=false;
                    textChange.afterTextChanged(null);
                    return;
                }

                if(priceDialogShowFlag==false&&Double.valueOf(priceEdit.getText().toString())==0){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(UploadActivity.this).setTitle("提示")
                            .setMessage("商品标价为0元\n再次点击发布按钮确认发布").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                    priceDialogShowFlag=true;
                }

                BookDesc bookDesc = new BookDesc();
                bookDesc.setText_desc(descEdit.getText().toString());
                for (LocalMedia image:selectList) {
                    String path = image.getCompressPath();
                    String str[] = path.split("/");
                    String imageName = str[str.length-1];
                    if(bookDesc.getPicture_url()==null){
                        bookDesc.setPicture_url(imageName);
                    }else {
                        bookDesc.setPicture_url(bookDesc.getPicture_url() + "$" + imageName);
                    }
                }
                bookUpload.setUid(1);
                bookUpload.setGrade(gradeSpinnerSelect);
                bookUpload.setPrice(Double.valueOf(priceEdit.getText().toString()));
                bookUpload.setBookDesc(bookDesc);
                Log.e("book",bookUpload.toString(),null);
                Log.e("bookJson",new Gson().toJson(bookUpload),null);
                OkHttpUtils
                        .postString()
                        .url(serverUrl+"/bookUpload")
                        .content(new Gson().toJson(bookUpload))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new MyStringCallback());
            }
        });
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isGradeSpinnerFirst) {
                    isGradeSpinnerFirst = false ;
                    return;
                }
                gradeSpinner.setVisibility(View.VISIBLE);
                gradeSpinnerSelect = position;
                textChange.afterTextChanged(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        gradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gradeSpinner.performClick();

            }
        });


    }



    private void initWidget() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(UploadActivity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(UploadActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(UploadActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @SuppressLint("CheckResult")
        @Override
        public void onAddPicClick() {
            //获取写的权限
            RxPermissions rxPermission = new RxPermissions(UploadActivity.this);
            rxPermission.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Permission>() {
                        @Override
                        public void accept(Permission permission) {
                            if (permission.granted) {// 用户已经同意该权限
                                //第一种方式，弹出选择和拍照的dialog
                                showPop();

                                //第二种方式，直接进入相册，但是 是有拍照得按钮的
//                                showAlbum();
                            } else {
                                Toast.makeText(UploadActivity.this, "拒绝", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    };



    private void showPop() {
        View bottomView = View.inflate(UploadActivity.this, R.layout.layout_bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        //隐藏输入法
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_album:
                        //相册
                        PictureSelector.create(UploadActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .maxSelectNum(maxSelectNum)
                                .minSelectNum(1)
                                .imageSpanCount(4)
                                .compress(true)
                                .selectionMode(PictureConfig.MULTIPLE)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_camera:
                        //拍照
                        PictureSelector.create(UploadActivity.this)
                                .openCamera(PictureMimeType.ofImage())
                                .forResult(PictureConfig.CHOOSE_REQUEST);
                        break;
                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };

        mAlbum.setOnClickListener(clickListener);
        mCamera.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调

                images = PictureSelector.obtainMultipleResult(data);

                if(images.size()>0){
                    resultImageFlag=true;
                    textChange.afterTextChanged(null);
                }else{
                    resultImageFlag=false;
                    textChange.afterTextChanged(null);
                }
                selectList.addAll(images);


                //selectList = PictureSelector.obtainMultipleResult(data);

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                adapter.setList(selectList);
                adapter.notifyDataSetChanged();
            }
        }

        if(requestCode==111&&resultCode==112){
            Bundle bundle = data.getExtras();



            String bookName = bundle.getString("bookName");
            String author = bundle.getString("author", null);
            String publisher = bundle.getString("publisher", null);
            String publishTime = bundle.getString("publishTime", null);
            String category = bundle.getString("category", null);
            String ISBN = bundle.getString("ISBN", null);
            String location = bundle.getString("location", null);

            if(bookName!=null&&author!=null&&publisher!=null&&publishTime!=null&&location!=null&&category!=null){
                bookUpload.setBook_name(bookName);
                bookUpload.setAuthor(author);
                bookUpload.setPublisher(publisher);
                bookUpload.setPublishingTime(publishTime);
                bookUpload.setISBN(ISBN);
                bookUpload.setLocation(location);
                bookUpload.setCategory(category);
                resultDescFlag = true;
                textChange.afterTextChanged(null);
            }else{
                resultDescFlag = false;
                textChange.afterTextChanged(null);
            }
        }
    }
////////////////////////////////////////////////////////////

    //EditText的监听器
    class TextChange implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(descEdit.length()>0&& priceEdit.length()>0&&resultDescFlag&&resultImageFlag&&gradeSpinnerSelect!=-1){
                uploadButton.setTextColor(Color.BLACK);
                uploadButton.setClickable(true);
            }else{
                uploadButton.setTextColor(Color.argb(77,00,00,00));
                uploadButton.setClickable(false);
            }

        }
    }
//////////////////////////////////////////////////////////////////////////////

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
            Toast.makeText(UploadActivity.this,"服务器错误",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(String response, int id) {
            if(response!=null&&Integer.valueOf(response)!=0){
                int bid = Integer.valueOf(response);
                Map<String, File> imageMap = new HashMap<>();
                for(LocalMedia localMedia:selectList){

                    if(localMedia.isCompressed()) {
                        File file = new File(localMedia.getCompressPath());
                        imageMap.put(file.getName(), file);
                    }else{
                        File file = new File(localMedia.getPath());
                        imageMap.put(file.getName(), file);
                    }
                }
                OkHttpUtils.post()//
                        .addParams("bid",""+bid)
                        .files("files",  imageMap)//
                        .url(serverUrl+"/bookImageUpload")
                        .build()//
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                if("true".equals(response)){
                                    Toast.makeText(UploadActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(UploadActivity.this,"发布失败",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }else{
                Toast.makeText(UploadActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
            }
            Log.e("d",response,null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getApplication().getSharedPreferences("bookBaseEdit", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
