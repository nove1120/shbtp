package com.nove1120.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nove1120.pojo.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SPUtil {
    private static SharedPreferences sp;

    public static boolean putUser(Context context ,User user){
        if(user!=null){
            Gson gson = new Gson();
            String userJson = gson.toJson(user);

            long time = new Date().getTime();

            sp = context.getSharedPreferences("user",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("userJson",userJson);
            editor.putLong("createTime",time);
            editor.commit();
            return true;
        }
        return false;
    }

    public static User getUser(Context context){
        sp = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        String userJson = sp.getString("userJson","");
        long createTime = sp.getLong("createTime",0);
        if(userJson.length()!=0&&createTime!=0){
            long time = new Date().getTime();
            Gson gson = new Gson();
//            return gson.fromJson(userJson, User.class);
            if(((time-createTime)/43200000)<=90) {
                return gson.fromJson(userJson, User.class);
            }
        }
        return null;

    }



}
