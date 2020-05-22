package com.nove1120.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nove1120.R;
import com.nove1120.widget.WarpLinearLayout;
import com.nove1120.utils.pixelSwitchUtil;

public class SearchActivity extends Activity {
    ImageView returnButton;
    EditText inputEdit;
    TextView searchButton;
    ImageView delInputImage;
    WarpLinearLayout historyLayout;
    ImageView delHistoryImage;
    TextView historyTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyLayout.removeAllViews();
        SharedPreferences sp = getSharedPreferences("searchHistory",Context.MODE_PRIVATE);
        String history = sp.getString("history","");
        if(history!=null&&history.length()!=0){
            historyTitle.setVisibility(View.VISIBLE);
            delHistoryImage.setVisibility(View.VISIBLE);
            String[] hs = history.split("\\$");
            for(String h:hs) {
                final TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMarginStart(pixelSwitchUtil.dip2px(this,10));
                textView.setLayoutParams(params);
                textView.setBackground(getDrawable(R.drawable.search_history_text));
                textView.setText(h);
                textView.setTextSize(14);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputEdit.setText(textView.getText().toString());
                        inputEdit.setSelection(textView.getText().length());
                        searchButton.performClick();
                    }
                });
                historyLayout.addView(textView);
            }
        } else{
            historyTitle.setVisibility(View.GONE);
            delHistoryImage.setVisibility(View.GONE);
        }
    }

    private void init(){
        returnButton = (ImageView) findViewById(R.id.returnButton);
        inputEdit = (EditText) findViewById(R.id.inputEdit);
        searchButton = (TextView) findViewById(R.id.searchButton);
        delInputImage = (ImageView) findViewById(R.id.delInputImage);
        historyLayout = (WarpLinearLayout) findViewById(R.id.historyLayout);
        delHistoryImage = (ImageView) findViewById(R.id.delHistoryImage);
        historyTitle = (TextView) findViewById(R.id.historyTitle);

        Intent intent = getIntent();
        String searchHint = intent.getStringExtra("SearchHint");
        if(searchHint!=null&&searchHint.length()!=0){
            inputEdit.setHint(searchHint);
        }




    }
    private void initListener(){
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);

                SharedPreferences sp = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String history = sp.getString("history","");

                if(inputEdit.getText()!=null&&inputEdit.getText().length()!=0) {
                    String inputText = inputEdit.getText().toString();
                    String newhs = inputText;
                    int newCount = 0;
                    if(history!=null&&history.length()!=0){
                        String[] hs = history.split("\\$");
                        for(int i=0 ; i<hs.length&&newCount<9 ; i++){
                            if(!inputText.equals(hs[i])){
                                newhs+="$"+hs[i];
                                newCount++;
                            }
                        }
                    }
                    editor.putString("history",newhs);
                    editor.commit();

                    intent.putExtra("searchContent", inputText);
                    startActivity(intent);
                    return;
                }else if(inputEdit.getHint()!=null&&inputEdit.getHint().length()!=0){
                    String inputHint = inputEdit.getHint().toString();
                    String newhs = inputHint;
                    int newCount = 0;
                    if(history!=null&&history.length()!=0){
                        String[] hs = history.split("\\$");
                        for(int i=0 ; i<hs.length&&newCount<10 ; i++){
                            if(!inputHint.equals(hs[i])){
                                newhs+="$"+hs[i];
                                newCount++;
                            }
                        }
                    }
                    editor.putString("history",newhs);

                    intent.putExtra("searchContent", inputHint);
                    startActivity(intent);
                    return;
                }else{
                    Toast.makeText(SearchActivity.this,"请输入搜索关键字",Toast.LENGTH_LONG).show();
                }
            }
        });
        inputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputEdit.length()>0){
                    delInputImage.setVisibility(View.VISIBLE);
                }else{
                    delInputImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // 输入法中点击搜索
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    searchButton.performClick();
                    return true;
                }
                return false;
            }
        });
        delInputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEdit.setText("");
                delInputImage.setVisibility(View.GONE);
            }
        });
        delHistoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                historyLayout.removeAllViews();
                AlertDialog.Builder builder=new AlertDialog.Builder(SearchActivity.this)
                        .setMessage("确认删除全部历史记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                historyLayout.removeAllViews();
                                SharedPreferences sp = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.commit();
                                historyTitle.setVisibility(View.GONE);
                                delHistoryImage.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("取消",null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


}
