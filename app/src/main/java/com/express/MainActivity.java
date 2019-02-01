package com.express;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;


public class MainActivity extends FragmentActivity implements OnClickListener {
//    public DatabaseHelper dbHelperExpress;
//    public SQLiteDatabase db;
    public String mTelNum;

    //Tab分别对应的Fragment
    private FragmentFetch fragmentFetch;
    private FragmentDone fragmentDone;
    private static MainActivity mInstance;

    @SuppressLint({"HardwareIds", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //设置短信权限
        String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.REQUEST_INSTALL_PACKAGES};
        ActivityCompat.requestPermissions(this, permissions, 2);
        mInstance = this;
        this.appInit();//初始化控件

        boolean tmp  = checkFromPhoneAndInsert();
        this.selectTab(0);
    }


    public static MainActivity getInstance() {
        return mInstance;
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


    @SuppressLint({"MissingPermission", "HardwareIds"})
    private void appInit() {
        //Tab的布局文件
        LinearLayout layoutFetch = findViewById(R.id.layoutFetch);
        LinearLayout layoutDone = findViewById(R.id.layoutDone);
        //初始化Tab的点击事件
        layoutFetch.setOnClickListener(this);
        layoutDone.setOnClickListener(this);

        mTelNum = ((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        mTelNum = StringUtils.right(mTelNum, 4);
    }


    //进行选中Tab的处理
    private void selectTab(int i) {
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
//        checkFromPhoneAndInsert();
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
                fragmentFetch.refresh(DBManager.getSmsFromDB("未取"));
                break;
            case 1:
                if (fragmentDone == null) {
                    fragmentDone = new FragmentDone();
                    transaction.add(R.id.frameLayout, fragmentDone);
                } else {
                    transaction.show(fragmentDone);
                }
                fragmentDone.refresh(DBManager.getSmsFromDB("已取"));
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




    /**
     * 从手机抓短信，对比本地数据库，把数据库不存在的短信，插入进去
     */
    public static boolean checkFromPhoneAndInsert() {
        try {
            Uri uri = Uri.parse("content://sms/");
            String[] columns = new String[]{"body", "date"};
            String where = "(body like '%馒头房%'  OR  body like '%丰巢%'  OR  body like '%日日顺%')";
//            String[] args = new String[]{"馒头房","丰巢","日日顺"};
            Cursor cur = MainActivity.getInstance().getContentResolver().query(uri, columns, where, null, "date desc"); // 获取手机内部短信

            if (cur != null && cur.moveToFirst()) {
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                String strCode = null;
                String position = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");

                do {
                    String longBody = cur.getString(index_Body);
                    SmsData tmpSms;
                    long longDate = cur.getLong(index_Date);
                    String smsID = String.valueOf(longDate);
                    String strDate = dateFormat.format(new Date(longDate));

                    if (DBManager.existInDatabase(smsID)==false) {
                        if (longBody.contains("馒头房")) {
                            position = "馒头房";
                            strCode = StringUtils.substringBetween(longBody, "提货码", "来取");
                        } else if (longBody.contains("丰巢")) {
                            position = "丰巢";
                            strCode = StringUtils.substringBetween(longBody, "请凭取件码『", "』前往明珠西苑");
                        } else if (longBody.contains("日日顺")) {
                            position = "日日顺";
                            strCode = StringUtils.substringBetween(longBody, "凭取件码", "到明珠西苑");
                        }
                        tmpSms = new SmsData(smsID, strDate, strCode, MainActivity.getInstance().mTelNum, position, null, "未取");
                        boolean a = DBManager.insertSms(tmpSms);
                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }


//    //逐条，将手机短信与本地数据库对比。返回boolean
//    public boolean existInDatabase(String smsID) {
//
//        @SuppressLint("Recycle") Cursor cur = db.query(true, DatabaseHelper.dbTableName, new String[]{"sms_id"},
//                "sms_id = ?", new String[]{smsID}, null, null, "sms_id desc", "1");
//        return cur.moveToFirst();
//    }


    //对比之后，往数据里写短信
//    public boolean insertSms(SmsData sms) {
//        ContentValues cv = new ContentValues();
//        cv.put("sms_id", sms.getSmsID());
//        cv.put("sms_date", sms.getSmsDate());
//        cv.put("sms_code", sms.getCode());
//        cv.put("sms_phone", sms.getPhone());
//        cv.put("sms_position", sms.getPosition());
//        cv.put("sms_fetch_date", sms.getFetchDate());
//        cv.put("sms_fetch_status", sms.getFetchStatus());
//
//        long rowId = db.insert(DatabaseHelper.dbTableName, null, cv);
//        return (rowId == -1);
//    }


}

