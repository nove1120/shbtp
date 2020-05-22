package com.nove1120.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nove1120.R;
import com.nove1120.adapter.BookAdapter;
import com.nove1120.dto.BookSelectParam;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class PersonalPageActivity extends Activity {
    private String serverUrl;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();

    private ImageView returnButton;

    private ImageView faceImage;
    private TextView userNameText;
    private TextView userLocationText;
    private TextView friendText;

    private boolean friendTextClickedFlag = false;


    private int lastBid = 0; //查询列表中最后一个项的bid用于分页
    private int loadLastBid = 0;//标记已经请求的lastbid防止在拿到新数据前重复请求

    private boolean loadingFlag = false;

    private static BookSelectParam bookSelectParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);


        init();
        initListener();
        refreshBooks();
    }

    private void init(){
        serverUrl = getResources().getString(R.string.server_url);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(PersonalPageActivity.this,bookList);
        recyclerView.setAdapter(adapter);

        returnButton = (ImageView) findViewById(R.id.returnButton);

        faceImage = (ImageView) findViewById(R.id.faceImage);
        userNameText = (TextView) findViewById(R.id.userNameText);
        userLocationText = (TextView) findViewById(R.id.userLocationText);
        friendText = (TextView) findViewById(R.id.friendText);

        Intent intent = getIntent();
        int uid = intent.getIntExtra("uid",0);
        if(uid==0){
            finish();
        }

        bookSelectParam = new BookSelectParam();
        bookSelectParam.setUid(uid);


        User user = SPUtil.getUser(PersonalPageActivity.this);
        if(user==null){
            finish();
        }
        if(user.getUid()==uid) {
            userNameText.setText(user.getUser_name());
            if (user.getLocation() != null && user.getLocation().length() != 0) {
                String[] locs = user.getLocation().split("\\$");
                if (locs.length == 3) {
                    userLocationText.setText(locs[1]);
                }
                if (locs.length == 4) {
                    userLocationText.setText(locs[1] + "•" + locs[3]);
                }
            }
            Glide.with(PersonalPageActivity.this).load(serverUrl + "/shbtpFile/userImage/" + user.getUid() + "/" + user.getFace()).into(faceImage);
        }else{
            String url = serverUrl+"/getUser";
            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("uid", ""+uid)
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
                                        userLocationText.setText(locs[1]);
                                    }
                                    if(locs.length==4){
                                        userLocationText.setText(locs[1]+"•"+locs[3]);
                                    }
                                }
                                Glide.with(PersonalPageActivity.this).load(serverUrl+"/shbtpFile/userImage/"+user.getUid()+"/"+user.getFace()).into(faceImage);
                            }catch (Exception e){

                            }
                        }
                    });
        }
    }
    private void initListener() {

        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(PersonalPageActivity.this, bookList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动，既是否向下滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0) {
                    //大于0表示，正在向下滚动
                    isSlidingToLast = true;
                    //获取最后一个完全显示的ItemPosition
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
                    int lastVisiblePos = getMaxElem(lastVisiblePositions);
                    int totalItemCount = layoutManager.getItemCount();

                    // 判断是否滚动到底部
                    if (lastVisiblePos == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
//                        refreshBooks();
//                        Log.e("howes right="+manager.findLastCompletelyVisibleItemPosition());
                        Toast.makeText(PersonalPageActivity.this, "加载更多", Toast.LENGTH_SHORT).show();
                        loadMoreBooks();
                    }
                } else {
                    //小于等于0 表示停止或向上滚动
                    isSlidingToLast = false;


                }

            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        friendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendTextClickedFlag){
                    friendTextClickedFlag = false;
                    friendText.setText("+ 关注");
                    friendText.setBackground(getDrawable(R.drawable.personal_page_friend_text));
                    friendText.setTextColor(getColor(R.color.friendTextColor));
                    friendText.setTypeface(Typeface.DEFAULT);

                }else{
                    friendTextClickedFlag = true;
                    friendText.setText("已关注");
                    friendText.setBackground(getDrawable(R.drawable.personal_page_friend_text_clicked));
                    friendText.setTextColor(getColor(R.color.textColor));
                    friendText.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        });
    }

    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i]>maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }



    private void loadMoreBooks(){
        if(!loadingFlag) {
            if(loadLastBid != lastBid){
                loadingFlag = true;
                loadLastBid = lastBid;
                Book lastBook = bookList.get(bookList.size()-1);
                lastBid = lastBook.getBid();
                bookSelectParam.setLimitBidStart(lastBid + 1);
                bookSelectParam.setLimitStart(0);
                searchBook(bookSelectParam);
                return;
            }
        }
    }
    private void refreshBooks(){


            lastBid = 0;
            loadLastBid = 0;
            bookSelectParam.setLimitStart(0);
            bookSelectParam.setLimitBidStart(0);
//            recyclerView.removeAllViews();
            bookList.clear();
            adapter.notifyDataSetChanged();
            searchBook(bookSelectParam);
    }


    private void searchBook(BookSelectParam bookSelectParam){
        if(bookSelectParam==null) return;
        OkHttpUtils
                .postString()
                .url(serverUrl+"/searchBook")
                .content(new Gson().toJson(bookSelectParam))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new MyStringCallback());
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
            loadingFlag = false;

        }

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            List<Book> books = gson.fromJson(response, new TypeToken<List<Book>>() {}.getType());
            if(books!=null&&books.size()!=0){
                Log.e("books",books.toString(),null);
                int lastSize = bookList.size();
                bookList.addAll(books);
                lastBid = bookList.get(bookList.size()-1).getBid();
                adapter.notifyItemRangeChanged(lastSize,books.size());

            }
            loadingFlag = false;

        }

    }
}
