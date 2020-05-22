package com.nove1120.activitys.ui.home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nove1120.R;
import com.nove1120.activitys.MainActivity;
import com.nove1120.activitys.PersonalPageActivity;
import com.nove1120.activitys.SearchResultActivity;
import com.nove1120.adapter.BookAdapter;
import com.nove1120.dto.BookSelectParam;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookDesc;
import com.nove1120.utils.ISBNUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;


public class HomeFragment extends Fragment {
    private BookAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<Book> bookList = new ArrayList<>();
    private StaggeredGridLayoutManager layoutManager;
    private String serverUrl;
    View root;

    private int rankStateFlag = 0; //标记价格上下角标 -1为价格降序 1为升序 0为综合排序


    private static BookSelectParam bookSelectParam;
    private int lastBid = 0; //查询列表中最后一个项的bid用于分页
    private int loadLastBid = 0;//标记已经请求的lastbid防止在拿到新数据前重复请求
    private int loadingAscDescLast=1;//记录升序降序状态 状态变化后limit起始点清零
    private boolean loadingFlag = false;
    private boolean refreshFlag = false;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        init();
        initListener();

        refreshBooks();
        return root;
    }


    private void init(){
        serverUrl = getResources().getString(R.string.server_url);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);



        layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(getActivity(),bookList);
        recyclerView.setAdapter(adapter);

        bookSelectParam = new BookSelectParam();
        SharedPreferences sp = getActivity().getSharedPreferences("searchHistory",Context.MODE_PRIVATE);
        String history = sp.getString("history","");
        if(history.length()!=0){
            String[] hs = history.split("\\$");
            String content="";
            for(int i=0 ;i<hs.length&&i<3;i++){
                content+=" "+hs[i];
            }
            bookSelectParam.setContent(content);
        }
    }

    private void initListener() {

        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(getActivity(), bookList);
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
                        loadMoreBooks();
                    }
                } else {
                    //小于等于0 表示停止或向上滚动
                    isSlidingToLast = false;


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


    //每次上拉加载的时候，给RecyclerView的后面添加了10条数据数据
    private void loadMoreData(){
        int start = bookList.size();
//        bookList.add();
        adapter.notifyItemInserted(start);
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