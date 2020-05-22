package com.nove1120.activitys;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.nove1120.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    ImageView actionbarHomeScanImage;
    LinearLayout actionbarHomeSearch;
    TextView actionbarHomeSearchText;
    ImageView getActionbarHomeSearchImage;
    ImageView actionbarHomeUploadImage;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.hide();
        view = View.inflate(getApplicationContext(), R.layout.actionbar_chat_layout,null);


        init();
//        setListener();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_my)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    private void init(){
        actionbarHomeScanImage = view.findViewById(R.id.actionbarHomeScanImage);
        actionbarHomeSearch = view.findViewById(R.id.actionbarHomeSearch);
        actionbarHomeUploadImage = view.findViewById(R.id.actionbarHomeUploadImage);
    }

//    private void setListener(){
//        actionbarHomeScanImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        actionbarHomeSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
//                startActivity(intent);
//            }
//        });
//        actionbarHomeUploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

//    public void setActionBarHome(){
//        actionBar.setShowHideAnimationEnabled(false);
//        actionBar.hide();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
//        actionBar.setCustomView(R.layout.actionbar_home_layout);//设置自定义的布局：actionbar_custom
//    }
//
//    public void setActionBarChat(){
////        actionBar.setDisplayOptions(ActionBar.);
//        actionBar.setShowHideAnimationEnabled(false);
//        actionBar.hide();
//
////        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
////        actionBar.setCustomView(R.layout.actionbar_chat_layout);//设置自定义的布局：actionbar_custom
//    }
//    public void setActionBarMy(){
//        actionBar.setShowHideAnimationEnabled(false);
//        actionBar.hide();
////        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
////        actionBar.setCustomView(R.layout.actionbar_my_layout);//设置自定义的布局：actionbar_custom
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.actionbarHomeScanImage:
                Intent intent = new Intent(MainActivity.this,ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.actionbarHomeSearch:
                Intent intentSearch = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intentSearch);



                break;
            case R.id.actionbarHomeSearchText:
                break;
            case R.id.actionbarHomeSearchImage:
                break;
            case R.id.actionbarHomeUploadImage:
                Intent intentUpload = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intentUpload);
                break;


        }

    }
}
