package com.nove1120.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Handler;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.nove1120.R;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class WelcomeActivity extends Activity implements EasyPermissions.PermissionCallbacks  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
//        User user = SPUtil.getUser(this);
//        if(user==null){
//            User newUser  = new User();
//            newUser.setUser_name("nove1120");
//            newUser.setUid(1);
//            SPUtil.putUser(this,newUser);
//        }



        requireSomePermission();

    }




    private static final int num = 123;//用于验证获取的权

    @AfterPermissionGranted(num)
    private void requireSomePermission() {
        String[] perms = {

                // 把你想要申请的权限放进这里就行，注意用逗号隔开

//                Manifest.permission.CALL_PHONE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_NETWORK_STATE,

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA

//                Manifest.permission.CALL_PHONE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.VIBRATE,
//                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.GET_TASKS,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE,
//                Manifest.permission.WAKE_LOCK,
//                Manifest.permission.MODIFY_AUDIO_SETTINGS,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.RECEIVE_BOOT_COMPLETED
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...

            EMClient.getInstance().login("1","1",new EMCallBack() {//回调
                @Override
                public void onSuccess() {
//                        EMClient.getInstance().groupManager().loadAllGroups();
//                        EMClient.getInstance().chatManager().loadAllConversations();
                    Log.e("login","success",null);
                }

                @Override
                public void onProgress(int progress, String status) {
                    Log.e("login","progress",null);
                }

                @Override
                public void onError(int code, String message) {
                    Log.e("login","onError"+code+" "+message,null);
                }
            });

            if(SPUtil.getUser(WelcomeActivity.this)!=null) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);//跳转
                        finish();//销毁当前页面
//                    }
//                }, 1500);
            }else{
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        Intent intent = new Intent(WelcomeActivity.this, LoginSMSActivity.class);
                        startActivity(intent);//跳转
                        finish();//销毁当前页面
//                    }
//                }, 1500);
            }

            //启动
//            Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_LONG).show();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "应用缺乏必要的权限", num, perms);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_LONG).show();

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限已经被您拒绝")
                    .setRationale("如果不打开权限则无法使用该功能,点击确定去打开权限")
                    .setRequestCode(10001)//用于onActivityResult回调做其它对应相关的操作
                    .build()
                    .show();
        }
    }
}
