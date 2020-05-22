package com.nove1120.activitys;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nove1120.R;
import com.nove1120.adapter.BookAdapter;
import com.nove1120.dto.BookSelectParam;
import com.nove1120.pojo.Book;
import com.nove1120.utils.ISBNUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

public class SearchResultActivity extends Activity {
    private ImageView returnButton;
    private LinearLayout editLayout;
    private TextView searchText;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StaggeredGridLayoutManager layoutManager;
    private String serverUrl;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private String searchContent;

    private TextView compRankText;
    private LinearLayout priceLayout;
    private LinearLayout locationLayout;
    private Spinner locationSpinner;
    private TextView locationText;
    private TextView screenText;
    private ImageView screenImage;
    private boolean isLocationSpinnerFirst = true;

    private ImageView priceUpImage;
    private ImageView priceDownImage;
    private int rankStateFlag = 0; //标记价格上下角标 -1为价格降序 1为升序 0为综合排序
    private TextView priceRankText;
    private DrawerLayout drawerLayout;

    private TextView newText;
    private TextView freeText;
    private EditText priceMinEdit;
    private EditText priceMaxEdit;
    private TextView day1Text;
    private TextView day7Text;
    private TextView day14Text;
    private TextView resetText;
    private TextView confirmText;

    private boolean isNewTextClick = false;
    private boolean isFreeTextClick = false;
    private boolean isDay1TextClick = false;
    private boolean isDay7TextClick = false;
    private boolean isDay14TextClick = false;
    private boolean drawerConfirmFlag = false;


    private static BookSelectParam bookSelectParam;
    private int lastBid = 0; //查询列表中最后一个项的bid用于分页
    private int loadLastBid = 0;//标记已经请求的lastbid防止在拿到新数据前重复请求
    private int loadingAscDescLast=1;//记录升序降序状态 状态变化后limit起始点清零
    private boolean loadingFlag = false;
    private boolean refreshFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        init();
        initListener();
        refreshBooks();
    }

    private void init(){
        serverUrl = getResources().getString(R.string.server_url);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        editLayout = (LinearLayout) findViewById(R.id.editLayout);
        returnButton = (ImageView) findViewById(R.id.returnButton);
        searchText = (TextView) findViewById(R.id.searchText);

        layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(SearchResultActivity.this,bookList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        searchContent = intent.getStringExtra("searchContent");
        if(searchContent==null||searchContent.length()==0){
            finish();
        }else{
            searchText.setText(searchContent);
        }
        //搜索选项相关
        compRankText = (TextView) findViewById(R.id.compRankText);
        priceLayout = (LinearLayout) findViewById(R.id.priceLayout);
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
        screenText = (TextView) findViewById(R.id.screenText);
        screenImage = (ImageView) findViewById(R.id.screenImage);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        locationText = (TextView) findViewById(R.id.locationText);
        priceUpImage = (ImageView) findViewById(R.id.priceUpImage);
        priceDownImage = (ImageView) findViewById(R.id.priceDownImage);
        priceRankText = (TextView) findViewById(R.id.priceRankText);
        //侧拉筛选相关
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        newText = (TextView) findViewById(R.id.newText);
        freeText = (TextView) findViewById(R.id.freeText);
        priceMinEdit = (EditText) findViewById(R.id.priceMinEdit);
        priceMaxEdit = (EditText) findViewById(R.id.priceMaxEdit);
        day1Text = (TextView) findViewById(R.id.day1Text);
        day7Text = (TextView) findViewById(R.id.day7Text);
        day14Text = (TextView) findViewById(R.id.day14Text);
        resetText = (TextView) findViewById(R.id.resetText);
        confirmText = (TextView) findViewById(R.id.confirmText);

        //设置侧拉不能通过滑动打开和关闭
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        bookSelectParam = new BookSelectParam();
        if(ISBNUtil.testISBN(searchContent)){
            bookSelectParam.setISBN(searchContent);
        }else{
            bookSelectParam.setContent(searchContent);
        }

    }
    private void initListener(){

        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(SearchResultActivity.this,bookList);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshBooks();
                swipeRefreshLayout.setRefreshing(false);

                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                layoutManager.invalidateSpanAssignments();
            }
        });



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            //用来标记是否正在向最后一个滑动，既是否向下滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if(dy > 0){
                    //大于0表示，正在向下滚动
                    isSlidingToLast = true;
                    //获取最后一个完全显示的ItemPosition
                    int[] lastVisiblePositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
                    int lastVisiblePos = getMaxElem(lastVisiblePositions);
                    int totalItemCount = layoutManager.getItemCount();

                    // 判断是否滚动到底部
                    if (lastVisiblePos == (totalItemCount -1) && isSlidingToLast) {
                        //加载更多功能的代码
                        loadMoreBooks();
                    }
                }else{
                    //小于等于0 表示停止或向上滚动
                    isSlidingToLast = false;


                }

            }
        });
        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });



        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationSpinner.setVisibility(View.VISIBLE);
                locationSpinner.performClick();
            }
        });
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locationSpinner.setVisibility(View.INVISIBLE);
                if(isLocationSpinnerFirst){
                    isLocationSpinnerFirst=false;
                    return;
                }
                String[] province  = getResources().getStringArray(R.array.province);
                String provinceSelect = province[position];
                locationText.setText(provinceSelect);
                locationText.setTypeface(Typeface.DEFAULT_BOLD);
                locationText.setTextColor(getResources().getColor(R.color.searchResultTextBlack,null));

                String loc = provinceSelect;
                if(loc!=null&&!"区域".equals(loc)&&!"全国".equals(loc)){
                    bookSelectParam.setLocation(loc);
                }else{
                    bookSelectParam.setLocation("");
                }
                refreshBooks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        priceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                priceRankText.setTextColor(getResources().getColor(R.color.searchResultTextBlack,null));
                priceRankText.setTypeface(Typeface.DEFAULT_BOLD);

                compRankText.setTextColor(getResources().getColor(R.color.searchResultTextGray,null));
                compRankText.setTypeface(Typeface.DEFAULT);


                if(rankStateFlag==-1 ||rankStateFlag==0){
                    rankStateFlag=1;
                    priceDownImage.setVisibility(View.GONE);
                    priceUpImage.setVisibility(View.VISIBLE);
                    bookSelectParam.setOrderByClause(BookSelectParam.OrderByClause.PRICE_ASC);
                }else if(rankStateFlag==1){
                    rankStateFlag=-1;
                    priceUpImage.setVisibility(View.GONE);
                    priceDownImage.setVisibility(View.VISIBLE);
                    bookSelectParam.setOrderByClause(BookSelectParam.OrderByClause.PRICE_DESC);

                }

                refreshBooks();


            }
        });
        compRankText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rankStateFlag!=0) {
                    rankStateFlag = 0;
                    priceRankText.setTypeface(Typeface.DEFAULT);
                    priceRankText.setTextColor(getResources().getColor(R.color.searchResultTextGray, null));

                    priceDownImage.setVisibility(View.VISIBLE);
                    priceUpImage.setVisibility(View.VISIBLE);

                    compRankText.setTextColor(getResources().getColor(R.color.searchResultTextBlack, null));
                    compRankText.setTypeface(Typeface.DEFAULT_BOLD);

                    bookSelectParam.setOrderByClause(null);
                    refreshBooks();
                }
            }
        });

        screenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        screenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });


        //侧拉相关
        newText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewTextClick){
                    isNewTextClick = false;
                    newText.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                    newText.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                    newText.setTypeface(Typeface.DEFAULT);
                }else {
                    isNewTextClick = true;
                    newText.setBackground(getDrawable(R.drawable.rounded_corner_search_button_click));
                    newText.setTextColor(getColor(R.color.drawerLayoutButtonTextClick));
                    newText.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
        });
        freeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFreeTextClick){
                    isFreeTextClick = false;
                    freeText.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                    freeText.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                    freeText.setTypeface(Typeface.DEFAULT);
                    priceMinEdit.setClickable(true);
                    priceMaxEdit.setClickable(true);

                }else {
                    isFreeTextClick = true;
                    freeText.setBackground(getDrawable(R.drawable.rounded_corner_search_button_click));
                    freeText.setTextColor(getColor(R.color.drawerLayoutButtonTextClick));
                    freeText.setTypeface(Typeface.DEFAULT_BOLD);
                    priceMinEdit.setText("");
                    priceMinEdit.setClickable(false);
                    priceMaxEdit.setText("");
                    priceMaxEdit.setClickable(false);
                }
            }
        });
        day1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDay1TextClick){
                    isDay1TextClick = false;
                    day1Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                    day1Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                    day1Text.setTypeface(Typeface.DEFAULT);
                }else {
                    isDay1TextClick = true;
                    day1Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button_click));
                    day1Text.setTextColor(getColor(R.color.drawerLayoutButtonTextClick));
                    day1Text.setTypeface(Typeface.DEFAULT_BOLD);
                    if(isDay7TextClick) {
                        day7Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                        day7Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                        day7Text.setTypeface(Typeface.DEFAULT);
                        isDay7TextClick = false;

                    }
                    if(isDay14TextClick){
                        day14Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                        day14Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                        day14Text.setTypeface(Typeface.DEFAULT);
                        isDay14TextClick = false;
                    }

                }
            }
        });
        day7Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDay7TextClick){
                    isDay7TextClick = false;
                    day7Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                    day7Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                    day7Text.setTypeface(Typeface.DEFAULT);
                }else {
                    isDay7TextClick = true;
                    day7Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button_click));
                    day7Text.setTextColor(getColor(R.color.drawerLayoutButtonTextClick));
                    day7Text.setTypeface(Typeface.DEFAULT_BOLD);
                    if(isDay1TextClick){
                        day1Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                        day1Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                        day1Text.setTypeface(Typeface.DEFAULT);
                        isDay1TextClick = false;
                    }
                    if(isDay14TextClick){
                        day14Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                        day14Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                        day14Text.setTypeface(Typeface.DEFAULT);
                        isDay14TextClick = false;
                    }

                }
            }
        });
        day14Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDay14TextClick){
                    isDay14TextClick = false;
                    day14Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                    day14Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                    day14Text.setTypeface(Typeface.DEFAULT);
                }else {
                    isDay14TextClick = true;
                    day14Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button_click));
                    day14Text.setTextColor(getColor(R.color.drawerLayoutButtonTextClick));
                    day14Text.setTypeface(Typeface.DEFAULT_BOLD);
                    if(isDay1TextClick){
                        day1Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                        day1Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                        day1Text.setTypeface(Typeface.DEFAULT);
                        isDay1TextClick = false;
                    }
                    if(isDay7TextClick){
                        day7Text.setBackground(getDrawable(R.drawable.rounded_corner_search_button));
                        day7Text.setTextColor(getColor(R.color.drawerLayoutButtonTextGray));
                        day7Text.setTypeface(Typeface.DEFAULT);
                        isDay7TextClick = false;
                    }
                }
            }
        });
        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNewTextClick){
                    newText.performClick();
                }
                if(isFreeTextClick){
                    freeText.performClick();
                }
                if(isDay1TextClick){
                    day1Text.performClick();
                }
                if(isDay7TextClick){
                    day7Text.performClick();
                }
                if(isDay14TextClick){
                    day14Text.performClick();
                }
                priceMinEdit.setText("");
                priceMaxEdit.setText("");
                drawerConfirmFlag = false;

            }
        });
        confirmText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookSelectParam.setGrade(-1);
                bookSelectParam.setPriceMin(-1);
                bookSelectParam.setPriceMax(-1);
                bookSelectParam.setUploadTime(-1);
                drawerConfirmFlag = true;
                drawerLayout.closeDrawers();

                String priceMinStr;
                String priceMaxStr;
                int priceMin = -1;
                int priceMax = -1;
                if(priceMinEdit.getText()!=null){
                    priceMinStr = priceMinEdit.getText().toString();
                    if(priceMinStr.length()!=0){
                        priceMin = Integer.valueOf(priceMinStr);
                    }
                }
                if(priceMaxEdit.getText()!=null){
                    priceMaxStr = priceMaxEdit.getText().toString();
                    if(priceMaxStr.length()!=0){
                        priceMax = Integer.valueOf(priceMaxStr);
                    }
                }

                if(priceMin!=-1&&priceMax!=-1&&priceMin>priceMax){
                    int temp = priceMin;
                    priceMin = priceMax;
                    priceMax = temp;

                    priceMinEdit.setText(""+priceMin);
                    priceMaxEdit.setText(""+priceMax);
                }



                if(isNewTextClick){
                    bookSelectParam.setGrade(0);
                }
                if(isFreeTextClick){
                    bookSelectParam.setPriceMax(0);
                }


                if(priceMin!=-1){
                    bookSelectParam.setPriceMin(priceMin);
                }
                if(priceMax!=-1){
                    bookSelectParam.setPriceMax(priceMax);
                }
                if(isDay1TextClick){
                    bookSelectParam.setUploadTime(1);
                }else if(isDay7TextClick){
                    bookSelectParam.setUploadTime(7);
                }else if(isDay14TextClick){
                    bookSelectParam.setUploadTime(14);
                }

                refreshBooks();
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(!drawerConfirmFlag){
                    resetText.performClick();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

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


    //每次上拉加载的时候，给RecyclerView的后面添加了10条数据数据
    private void loadMoreData(){
        int start = bookList.size();
//        bookList.add();
        adapter.notifyItemInserted(start);
    }

    private void initBooks() {
        BookSelectParam bookSelectParam = new BookSelectParam();
        if(ISBNUtil.testISBN(searchContent)){
            bookSelectParam.setISBN(searchContent);
        }else{
            bookSelectParam.setContent(searchContent);
        }
        Log.e("bookparam",searchContent.toString(),null);
        searchBook(bookSelectParam);
    }

    private void clearBooks(){
        recyclerView.removeAllViews();
        bookList.clear();
    }

    private void loadMoreBooks(){
        if(!loadingFlag&&!refreshFlag) {
            if(rankStateFlag==0&&loadLastBid != lastBid){
                loadingFlag = true;
                loadLastBid = lastBid;
                Book lastBook = bookList.get(bookList.size()-1);
                lastBid = lastBook.getBid();
                bookSelectParam.setLimitBidStart(lastBid + 1);
                bookSelectParam.setLimitStart(0);
                searchBook(bookSelectParam);
                return;
            }
            if(rankStateFlag!=0){
                loadingFlag = true;
                int count = bookList.size();
                bookSelectParam.setLimitStart(count);
                bookSelectParam.setLimitBidStart(0);
                searchBook(bookSelectParam);
            }
        }
    }

    private void refreshBooks(){


        Log.e("flags",""+loadingFlag+","+refreshFlag,null);
        if(!loadingFlag&&!refreshFlag) {
            refreshFlag = true;
            lastBid = 0;
            loadLastBid = 0;
            bookSelectParam.setLimitStart(0);
            bookSelectParam.setLimitBidStart(0);
//            recyclerView.removeAllViews();
            bookList.clear();
            adapter.notifyDataSetChanged();
            searchBook(bookSelectParam);
        }
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
            loadingFlag = false;
            refreshFlag = false;
        }

        @Override
        public void onResponse(String response, int id) {
            Gson gson = new Gson();
            List<Book> books = gson.fromJson(response, new TypeToken<List<Book>>() {}.getType());
            if(books!=null&&books.size()!=0){
                Log.e("books",books.toString(),null);
//                Log.e("loadingFlag",""+loadingFlag,null);
//                Log.e("refreshFlag",""+refreshFlag,null);
//                bookList.clear();
                int lastSize = bookList.size();
                bookList.addAll(books);
                lastBid = bookList.get(bookList.size()-1).getBid();
//                判断是刷新还是加载 起始点不同
                if(loadingFlag) {
                    adapter.notifyItemRangeChanged(lastSize,books.size());
                }else if(refreshFlag){
                    adapter.notifyDataSetChanged();
                }
            }
            loadingFlag = false;
            refreshFlag = false;
            Log.e("resflag",loadingFlag+","+refreshFlag,null);
        }

    }
}
