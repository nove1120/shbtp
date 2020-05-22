package com.hyphenate.easeui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class EaseUserUtils {
    
    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();

    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return userProvider.getUser(username);
        
        return null;
    }
    
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	EaseUser user = getUserInfo(username);
        String serverUrl = context.getResources().getString(R.string.server_url);
        if(serverUrl!=null&&serverUrl.length()!=0) {
            String url = serverUrl + "/getUserFace?uid=" + username.trim();
            try {
                Glide.with(context).load(url).into(imageView);
            }catch (Exception e){
                Glide.with(context).load(user.getAvatar())
                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imageView);
            }
//            Glide.with(context).load("http://192.168.0.106:8081/getUserFace?uid=1").into(imageView);
        }
//        if(user != null && user.getAvatar() != null && ){
//            try {
//                int avatarResId = Integer.parseInt(user.getAvatar());
//
//                Glide.with(context).load(url).into(imageView);
//            } catch (Exception e) {
//                //use default avatar
//                Glide.with(context).load(user.getAvatar())
//                        .apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)
//                                .diskCacheStrategy(DiskCacheStrategy.ALL))
//                        .into(imageView);
////                Glide.with(context).load("http://192.168.0.106:8081/image/get").into(imageView);
//            }
//        }else{
////            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
////            Glide.with(context).load(url).into(imageView);
//
//            Glide.with(context).load(url)
//                    .apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL))
//                    .into(imageView);
//        }
    }
    
    /**
     * set user's nickname
     */
    public static void setUserNick(Context context ,final String username, final TextView textView){
        if(textView != null){
//        	EaseUser user = getUserInfo(username);
//        	if(user != null && user.getNickname() != null){
//        		textView.setText(user.getNickname());
//        	}else{
//        		textView.setText(username);
//        	}

            final SharedPreferences sharedPreferences = context.getSharedPreferences("esusername",Context.MODE_PRIVATE);
            String realUserName = sharedPreferences.getString("es"+username,"");
            if(realUserName!=null&&realUserName.length()!=0){
                textView.setText(realUserName);
            }
            String serverUrl = context.getResources().getString(R.string.server_url);
            if(serverUrl!=null&&serverUrl.length()!=0) {
                String url = serverUrl + "/getUserName?uid=" + username;

                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("uid", username)
                        .build()
                        .execute(new StringCallback() {

                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                textView.setText(response);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("es"+username,response);
                                editor.commit();
                            }
                        });
            }

        }
    }
    
}
