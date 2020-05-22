package com.nove1120.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nove1120.R;
import com.nove1120.utils.ISBNUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookBaseEditActivity extends Activity {
    private int requestCode=101;//请求码

    private int resultCode=112;

    LinearLayout positionLayout;
    LinearLayout publishTimeLayout;
    LinearLayout categoryLayout;
    TextView confirmButton;
    ImageView returnButton;


    EditText bookNameEdit;
    EditText authorEdit;
    EditText publisherEdit;
    Spinner publishTimeSpinner;
    EditText ISBNEdit;
    EditText schoolEdit;
    TextView positionEdit;
    ImageView positionImage;
    Spinner categorySpinner;
    int saveYear;
    TextChange textChange;
    int categorySelectPosition=-1;
    boolean isCatecorySpinnerFirst=true;
    int publishTimeSelectPosition=-1;
    boolean isPublishTimeSpinnerFirst=true;
    private ArrayList<String> dataYear = new ArrayList<String>();
    private ArrayAdapter<String> adapterSpYear;
    private boolean ISBNDialogShowFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_base_edit);
        SharedPreferences sp = getApplication().getSharedPreferences("bookBaseEdit", Context.MODE_PRIVATE);
        saveYear = sp.getInt("saveYear",-1);

        init();
        initOnclick();


        bookNameEdit.setText(sp.getString("bookName", ""));
        authorEdit.setText(sp.getString("author", ""));
        publisherEdit.setText(sp.getString("publisher", ""));
        ISBNEdit.setText(sp.getString("ISBN", ""));
        positionEdit.setText(sp.getString("position", ""));
        schoolEdit.setText(sp.getString("school", ""));
        categorySelectPosition = sp.getInt("category",-1);
        ISBNDialogShowFlag = sp.getBoolean("ISBNDialogShowFlag",false);

        publishTimeSelectPosition = sp.getInt("publishTime",-1);
        if(categorySelectPosition!=-1) {
            isCatecorySpinnerFirst=false;
            categorySpinner.setSelection(categorySelectPosition, true);
        }


        if(publishTimeSelectPosition!=-1) {
            isPublishTimeSpinnerFirst=false;
            publishTimeSpinner.setSelection(publishTimeSelectPosition, true);
        }
        if(positionEdit.getText()!=""){
            positionImage.setVisibility(View.GONE);
        }
    }

    private void init(){
        positionLayout = (LinearLayout) findViewById(R.id.positionLayout);
        publishTimeLayout = (LinearLayout) findViewById(R.id.publishTimeLayout);
        categoryLayout = (LinearLayout) findViewById(R.id.categoryLayout);
        positionEdit = (TextView) findViewById(R.id.positionEdit);
        positionImage = (ImageView) findViewById(R.id.positionImage);

        bookNameEdit = (EditText) findViewById(R.id.bookNameEdit);
        authorEdit = (EditText) findViewById(R.id.authorEdit);
        publisherEdit = (EditText) findViewById(R.id.publisherEdit);
        publishTimeSpinner = (Spinner) findViewById(R.id.publishTimeSpinner);
        ISBNEdit = (EditText) findViewById(R.id.ISBNEdit);
        schoolEdit = (EditText) findViewById(R.id.schoolEdit);
        confirmButton = (TextView) findViewById(R.id.confirmButton);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        setEditTextInhibitInputSpace(bookNameEdit);


        setEditTextInhibitInputSpeChat(authorEdit);
        setEditTextInhibitInputSpeChat(publisherEdit);
        setEditTextInhibitInputSpeChat(schoolEdit);

        textChange = new TextChange();
        bookNameEdit.addTextChangedListener(textChange);
        authorEdit.addTextChangedListener(textChange);
        publisherEdit.addTextChangedListener(textChange);
        ISBNEdit.addTextChangedListener(textChange);
        positionEdit.addTextChangedListener(textChange);

        categorySpinner.setVisibility(View.INVISIBLE);
        publishTimeSpinner.setVisibility(View.INVISIBLE);

        Calendar cal = Calendar.getInstance();
        int year;
        if(saveYear==-1){
            year = cal.get(Calendar.YEAR);
        }else{
            year = saveYear;
        }
        for (int i = 0; i <=100; i++) {
            dataYear.add("" + (year-i));
        }
        adapterSpYear = new ArrayAdapter<String>(this, R.layout.item_publishtime_spinner, dataYear);
        publishTimeSpinner.setAdapter(adapterSpYear);

    }

    private void initOnclick(){
        positionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookBaseEditActivity.this,ShowRegionActivity.class);
                intent.putExtra("address",positionEdit.getText());
                startActivityForResult(intent,requestCode);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final String bookname = bookNameEdit.getText().toString();
                String author = authorEdit.getText().toString();
                String publisher = publisherEdit.getText().toString();
//                String publishTime = publishTimeEdit.getText().toString();
                String ISBN = ISBNEdit.getText().toString();
                String position = positionEdit.getText().toString();
                String school = schoolEdit.getText().toString();

                String[] categorys  = getResources().getStringArray(R.array.bookCategory);
                String categorySelect = categorys[categorySelectPosition];
                String category = categorySelect.split(" ")[0];
                String publishTime = dataYear.get(publishTimeSelectPosition);

                if(ISBNDialogShowFlag==false&&(ISBN==null||ISBN.length()==0)){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(BookBaseEditActivity.this).setTitle("提示")
                            .setMessage("填写ISBN可以让更多人看到您发布的商品哦~").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                    ISBNDialogShowFlag=true;
                    return;
                }

                if(ISBN!=null&&ISBN.length()!=0&&!ISBNUtil.testISBN(ISBN)){
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(BookBaseEditActivity.this).setTitle("提示")
                            .setMessage("ISBN格式不正确").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                    return;
                }

                if(bookname!=null&&bookname!=""&& author!=null&&author!=""&&
                        publisher!=null&&publisher!=""&& position!=null&&position!=""&&category.trim()!="") {
                    Bundle bundle = new Bundle();        //得到bundle对象
                    bundle.putString("bookName", bookname);
                    bundle.putString("author", author);
                    bundle.putString("publisher", publisher);
                    bundle.putString("publishTime", publishTime);
                    if(ISBN!=null)
                    bundle.putString("ISBN", ISBN);
                    bundle.putString("category",category);
                    String location = position.replace(" ","$");
                    if(school!=null){
                        location+="$"+school;
                    }
                    bundle.putString("location", location);

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(resultCode, intent);
                    finish();
                }
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                if (isCatecorySpinnerFirst) {
                    isCatecorySpinnerFirst = false ;
                    return;
                }
                categorySpinner.setVisibility(View.VISIBLE);
                categorySelectPosition = position;
                textChange.afterTextChanged(null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        publishTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                if (isPublishTimeSpinnerFirst) {
                    isPublishTimeSpinnerFirst = false ;
                    return;
                }
                publishTimeSpinner.setVisibility(View.VISIBLE);
                publishTimeSelectPosition = position;
                textChange.afterTextChanged(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        publishTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishTimeSpinner.performClick();
            }
        });
        categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categorySpinner.performClick();
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
            if(bookNameEdit.length()>0&& authorEdit.length()>0&&
                    publisherEdit.length()>0&&positionEdit.length()>0&&categorySelectPosition!=-1&&publishTimeSelectPosition!=-1){
                confirmButton.setTextColor(Color.BLACK);
                confirmButton.setClickable(true);
            }else{
                confirmButton.setTextColor(Color.argb(77,00,00,00));
                confirmButton.setClickable(false);
            }

        }
    }

    /**
     * 将返回数据给tv
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==102){
            positionEdit.setText(data.getStringExtra("address"));
            positionImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sp = getApplication().getSharedPreferences("bookBaseEdit", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("bookName",bookNameEdit.getText().toString());
        editor.putString("author",authorEdit.getText().toString());
        editor.putString("publisher",publisherEdit.getText().toString());

        editor.putString("ISBN",ISBNEdit.getText().toString());
        editor.putString("position",positionEdit.getText().toString());
        editor.putString("school",schoolEdit.getText().toString());
        editor.putInt("publishTime",publishTimeSelectPosition);
        editor.putInt("category",categorySelectPosition);
        editor.putInt("saveYear",Integer.valueOf(dataYear.get(0)));
        editor.putBoolean("ISBNDialogShowFlag",ISBNDialogShowFlag);
        editor.commit();
    }



    /**
     * 禁止EditText输入空格
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(" ".equals(source)){
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
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
                if(matcher.find()||source.equals(" ")){
                    return "";
                }
                else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});

    }
}
