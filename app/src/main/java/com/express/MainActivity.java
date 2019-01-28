package com.express;


import android.Manifest;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;


public class MainActivity extends FragmentActivity implements OnClickListener{

    //Tab分别对应的Fragment
    private Fragment fragmentFetch;
    private Fragment fragmentDone;

    public DatabaseHelper databaseHelper;
    public SQLiteDatabase db;
    private SqlScoutServer sqlScoutServer;
//    public AlertDialog.Builder builder = new AlertDialog.Builder(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sqlScoutServer = SqlScoutServer.create(this, getPackageName());
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},    1);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dbInit();
        viewInit();//初始化控件
        selectTab(0);//默认选中第一个Tab
    }


    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutFetch:
                selectTab(0);
                break;
            case R.id.layoutDone:
                selectTab(1);
                break;
        }
    }


    private void viewInit() {
        //初始化Tab的布局文件
        //Tab的布局文件
        LinearLayout layoutFetch = findViewById(R.id.layoutFetch);
        LinearLayout layoutDone = findViewById(R.id.layoutDone);
        //初始化Tab的点击事件
        layoutFetch.setOnClickListener(this);
        layoutDone.setOnClickListener(this);
    }



    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (fragmentFetch == null) {
                    fragmentFetch = new FragmentFetch();
                    transaction.add(R.id.frameLayout, fragmentFetch);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(fragmentFetch);
                }
                break;
            case 1:
                if (fragmentDone == null) {
                    fragmentDone = new FragmentDone();
                    transaction.add(R.id.frameLayout, fragmentDone);
                } else {
                    transaction.show(fragmentDone);
                }
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }


    //将Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (fragmentFetch != null) {
            transaction.hide(fragmentFetch);
        }
        if (fragmentDone != null) {
            transaction.hide(fragmentDone);
        }
    }


    private void dbInit() {

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("smsID", 2);
        contentValues.put("smsDate", "1-17");
        contentValues.put("code", "code");
        contentValues.put("phone", "phone");
        contentValues.put("type", "type");
        contentValues.put("fetchDate", "1-28");
        contentValues.put("status", 0);

        long rowId = db.insert("express", null, contentValues);

        Toast.makeText(MainActivity.this, String.valueOf(rowId), Toast.LENGTH_SHORT).show();
    }



}
