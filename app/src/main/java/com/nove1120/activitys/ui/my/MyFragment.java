package com.nove1120.activitys.ui.my;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.nove1120.R;
import com.nove1120.activitys.AddressActivity;
import com.nove1120.activitys.CreateOrderCompleteActivity;
import com.nove1120.activitys.DealModeActivity;
import com.nove1120.activitys.LoginSMSActivity;
import com.nove1120.activitys.MainActivity;
import com.nove1120.activitys.OrderListActivity;
import com.nove1120.activitys.PersonalInfoActivity;
import com.nove1120.activitys.PersonalPageActivity;
import com.nove1120.pojo.User;
import com.nove1120.utils.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

public class MyFragment extends Fragment {
    private String serverUrl;
    View root;
    ConstraintLayout personalPageLayout;
    ImageView faceImage;
    CardView faceCardView;
    TextView userNameText;
    LinearLayout sellLayout;
    LinearLayout buyLayout;
    LinearLayout collectLayout;
    LinearLayout personalInfoLayout;
    LinearLayout addressLayout;
    TextView logOutText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_my, container, false);
        init();
        initListener();



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = SPUtil.getUser(getActivity());
        if(user!=null && user.getUid()!=0 ){
            userNameText.setText(user.getUser_name());
            serverUrl = getResources().getString(R.string.server_url);
            if(user.getFace()!=null && user.getFace().length()!=0) {
                String url = serverUrl + "/shbtpFile/userImage/" + user.getUid() + "/" + user.getFace();
                Glide.with(this)
                        .load(url)
                        .into(faceImage);
            }
        }
    }

    private void init(){
        faceCardView = (CardView) root.findViewById(R.id.faceCardView);
        personalPageLayout = root.findViewById(R.id.personalPageLayout);
        sellLayout = (LinearLayout) root.findViewById(R.id.sellLayout);
        buyLayout = (LinearLayout) root.findViewById(R.id.buyLayout);
        collectLayout = (LinearLayout) root.findViewById(R.id.collectLayout);
        personalInfoLayout = (LinearLayout) root.findViewById(R.id.personalInfoLayout);
        addressLayout = (LinearLayout) root.findViewById(R.id.addressLayout);
        faceImage = (ImageView) root.findViewById(R.id.faceImage);
        userNameText = (TextView) root.findViewById(R.id.userNameText);
        logOutText = (TextView) root.findViewById(R.id.logOutText);
    }

    private void initListener(){
        personalPageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalPageActivity.class);
                intent.putExtra("uid",SPUtil.getUser(getActivity()).getUid());
                startActivity(intent);
            }
        });
        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalPageLayout.performClick();
            }
        });
        faceCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalPageLayout.performClick();
            }
        });

        personalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddressActivity.class);
                startActivity(intent);
            }
        });
        sellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderListActivity.class);
                intent.putExtra("flag",2);
                startActivity(intent);
            }
        });
        buyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderListActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity()).setTitle("确定退出登录吗？")
                        .setMessage("确认后订单将退出登录").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EMClient.getInstance().logout(true);

                                SharedPreferences sp = getActivity().getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.commit();

                                SPUtil.putUser(getActivity(),null);

                                Intent intent=new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(getActivity(), LoginSMSActivity.class);
                                startActivity(intent);
                                getActivity().finish();

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create().show();
            }
        });
    }
}