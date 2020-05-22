package com.nove1120.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.nove1120.R;
import com.nove1120.activitys.BookDetailsActivity;
import com.nove1120.activitys.PersonalPageActivity;
import com.nove1120.pojo.Book;
import com.nove1120.utils.pixelSwitchUtil;

import java.util.List;



public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> bookList;
    private Context context;
    private String serverUrl;


    static class ViewHolder extends RecyclerView.ViewHolder {
        View bookView;
        ImageView bookImageView;
        TextView bookNameText;
        TextView symbolText;

        TextView priceText;
        TextView flagNewText;
        View line1;
        TextView locationText;
        LinearLayout infoLayout;

        public ViewHolder(View view) {
            super(view);
            bookView = view;
            bookImageView = view.findViewById(R.id.bookImageView);
            bookNameText = view.findViewById(R.id.bookNameText);
            priceText = (TextView) view.findViewById(R.id.priceText);
            flagNewText = (TextView) view.findViewById(R.id.flagNewText);
            line1 = (View) view.findViewById(R.id.line1);
            locationText = (TextView) view.findViewById(R.id.locationText);
            symbolText = (TextView) view.findViewById(R.id.symbolText);
            infoLayout = (LinearLayout) view.findViewById(R.id.infoLayout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        serverUrl = context.getResources().getString(R.string.server_url);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Book book = bookList.get(position);
                Intent intent = new Intent(context, BookDetailsActivity.class);
                Gson gson = new Gson();
                intent.putExtra("bookJson",gson.toJson(book));
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        Book book = bookList.get(position);
////        holder.fruitImage.setImageResource(fruit.getImageId());
//        String imageURL = book.getBookDesc().getPicture_url();

//        holder.bookName.setText(book.getBook_name());

        Book book = bookList.get(position);
//        holder.fruitImage.setImageResource(fruit.getImageId());
        String[] imageNames = book.getBookDesc().getPicture_urlArray();
        String url = serverUrl+"/shbtpFile/bookImage/"+book.getBid()+"/"+imageNames[0];
        Log.e("url",url,null);

//        Glide.with(context)
//                .load(url)
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        holder.bookImageView.setImageDrawable(resource);
//
//                        double h = resource.getIntrinsicHeight();
//                        double w = resource.getIntrinsicWidth();
//
//                        double imageViewWidth = holder.bookImageView.getWidth();
//                        double ratio = w/h;
//                        double imageViewMinRatio = imageViewWidth/(double) pixelSwitchUtil.dip2px(context,200);
//                        if(ratio<imageViewMinRatio){
//                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixelSwitchUtil.dip2px(context,200));
//                            holder.bookImageView.setLayoutParams(params);
//                            holder.bookImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//                        }else{
//                            holder.bookImageView.setAdjustViewBounds(true);
//                            holder.bookImageView.setMaxHeight(pixelSwitchUtil.dip2px(context ,200));
//
//                        }
//                    }
//                });

        Glide.with(context)
                .load(url)
                .into(holder.bookImageView);



        holder.bookNameText.setText(book.getBook_name());

        if(book.getPrice()==0){
            holder.symbolText.setVisibility(View.GONE);
            holder.priceText.setText("免费送");
            holder.priceText.setTextSize(14);
        }else{
            holder.priceText.setText(""+book.getPrice());
        }
        if(book.getGrade()==0){
            holder.flagNewText.setVisibility(View.VISIBLE);
        }
        if(book.getLocation()!=null&&book.getLocation().length()!=0) {
            String[] locs = book.getLocation().split("\\$");
            String location = locs[1];
            if (locs.length == 4) {
                location += "•"+locs[3];
            }
            holder.locationText.setText(location);
        }
        if(context instanceof PersonalPageActivity){
            holder.line1.setVisibility(View.GONE);
            holder.locationText.setVisibility(View.GONE);
        }
    }





    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    public void setFruitList(List<Book> bookList){
//        this.mFruitList.clear();
        this.bookList = bookList;
        System.out.println(this.bookList);
    }


}
