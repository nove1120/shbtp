package com.nove1120.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.easeui.EaseConstant;
import com.nove1120.R;
import com.nove1120.activitys.ChatSingleActivity;
import com.nove1120.activitys.DealModeActivity;
import com.nove1120.pojo.Book;
import com.nove1120.pojo.BookOrder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class SellerOrderAdapter extends ArrayAdapter {
    private String serverUrl;
    private final int resourceId;



    public SellerOrderAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BookOrder bookOrder = (BookOrder) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        serverUrl = getContext().getResources().getString(R.string.server_url);

        ImageView bookImage = (ImageView) view.findViewById(R.id.bookImage);
        ImageView orderStateImage = (ImageView) view.findViewById(R.id.orderStateImage);

        TextView bookNameText = (TextView) view.findViewById(R.id.bookNameText);
        TextView priceText = (TextView) view.findViewById(R.id.priceText);
        TextView orderStateText = (TextView) view.findViewById(R.id.orderStateText);
        TextView chatText = (TextView) view.findViewById(R.id.chatText);
        TextView functionText = (TextView) view.findViewById(R.id.functionText);

        chatText.setText("联系买家");
        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), ChatSingleActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, ""+bookOrder.getBuy_uid()));
            }
        });


        if(bookOrder.getState()==1){
            orderStateImage.setBackground(getContext().getDrawable(R.drawable.ic_time));
            orderStateText.setText("等待卖家发货");
            functionText.setText("去发货");
            functionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DealModeActivity.class);
                    intent.putExtra("bookOrderJson",new Gson().toJson(bookOrder));
                    getContext().startActivity(intent);

                }
            });

        }
        if(bookOrder.getState()==2){
            orderStateImage.setBackground(getContext().getDrawable(R.drawable.ic_time));
            orderStateText.setText("等待买家收货");
            functionText.setText("查看订单");
        }
        if(bookOrder.getState()==3){
            orderStateImage.setBackground(getContext().getDrawable(R.drawable.ic_complete));
            orderStateText.setText("交易成功");
            functionText.setText("评价");
        }
        if(bookOrder.getState()==4){
            orderStateImage.setBackground(getContext().getDrawable(R.drawable.ic_complete));
            orderStateText.setText("交易关闭");
            functionText.setText("查看订单");
        }

        String url = serverUrl + "/getBookByBid";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("bid", ""+bookOrder.getBid())
                .build()
                .execute(new  StringCallback() {
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

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("123","asdfasfweff",null);
                if(response!=null){
                    Book book = new Gson().fromJson(response,Book.class);
                    if(book!=null){
                        bookNameText.setText(book.getBook_name());
                        priceText.setText(""+book.getPrice());
                        Glide.with(getContext())
                                .load(serverUrl+"/shbtpFile/bookImage/"+book.getBid()+"/"+book.getBookDesc().getPicture_url().split("\\$")[0])
                                .into(bookImage);


                    }
                }
            }
        });

        return view;
    }


}
