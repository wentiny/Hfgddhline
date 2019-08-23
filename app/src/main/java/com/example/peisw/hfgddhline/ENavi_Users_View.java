package com.example.peisw.hfgddhline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by wentiny on 2019/8/5.
 */

public class ENavi_Users_View extends AppCompatActivity implements View.OnClickListener{

    private TextView tv1,tv2,tv4;
    private FrameLayout fragment_container;
    private FirstFragment f1,f2;
    //权限数组
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
    public static ENavi_Users_View instance;

    public static String orgid,empid,empname;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        orgid = getIntent().getStringExtra("orgid");
        empid = getIntent().getStringExtra("empid");
        empname = getIntent().getStringExtra("empname");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                showDialogRequestPermission();
            }
        }

        method.url01 = getIntent().getStringExtra("urlpath")+"/newlxjiaserver1";

        tv1 = (TextView)findViewById(R.id.textView);
        tv2 = (TextView)findViewById(R.id.textView2);
        //tv4 = (TextView)findViewById(R.id.textView4);tv4.setText(getIntent().getStringExtra("empname"));
        fragment_container = (FrameLayout)findViewById(R.id.frame_container);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                hideAllFragment(transaction);
                setSelectedFalse();
                tv1.setSelected(true);
                if(f1==null){
                    f1 = FirstFragment.newInstance("tab01");
                    transaction.add(R.id.frame_container,f1);
                }else{
                    transaction.show(f1);
                }
                transaction.commit();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                hideAllFragment(transaction);
                setSelectedFalse();
                tv2.setSelected(true);
                if(f2==null){
                    f2 = FirstFragment.newInstance("tab02");
                    transaction.add(R.id.frame_container,f2);
                }else{
                    transaction.show(f2);
                }
                transaction.commit();
            }
        });
        tv1.performClick();

    }

    private void showDialogRequestPermission() {
        new android.app.AlertDialog.Builder(this).setTitle("存储及网络定位权限不可用").setMessage("需要获取相关操作权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    @Override
    public void onClick(View view) {

    }

    public void setSelectedFalse(){
        tv1.setSelected(false);
        tv2.setSelected(false);
    }

    public void hideAllFragment(android.app.FragmentTransaction transaction){
        if(f1!=null){transaction.hide(f1);}
        if(f2!=null){transaction.hide(f2);}
    }

}
