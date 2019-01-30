package com.express;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnClickListener {
    public DatabaseHelper databaseHelper;
    public SqlScoutServer sqlScoutServer;
    public SQLiteDatabase db;
    public String tel;

    //Tab分别对应的Fragment
    private FragmentFetch fragmentFetch;
    private Fragment fragmentDone;
    private static MainActivity mInstance;


    @SuppressLint({"HardwareIds", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sqlScoutServer = SqlScoutServer.create(this, getPackageName());
        super.onCreate(savedInstanceState);
        mInstance = this;

        //设置短信权限
        String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.REQUEST_INSTALL_PACKAGES};
        ActivityCompat.requestPermissions(this, permissions, 1);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        this.appInit();//初始化控件
        checkSmsFromPhone();
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
        //初始化Tab的布局文件
        //Tab的布局文件
        LinearLayout layoutFetch = findViewById(R.id.layoutFetch);
        LinearLayout layoutDone = findViewById(R.id.layoutDone);
        //初始化Tab的点击事件
        layoutFetch.setOnClickListener(this);
        layoutDone.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        tel = ((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        tel = StringUtils.right(tel, 4);
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
                    fragmentFetch.refresh(getSmsFromDB("未取"));
                    transaction.add(R.id.frameLayout, fragmentFetch);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    fragmentFetch.refresh(getSmsFromDB("未取"));
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


    //同步本地数据到服务器。返回boolean
    public void syncDB() {
    }


    //从本地数据库抓短信，放到List，
    public List<Sms> getSmsFromDB(String fetchStatus) {
        Cursor cur =db.query(DatabaseHelper.dbTableName,new String[]{"smsID,smsDate,code,phone,position,fetchDate"},
                "fetchStatus = ?",new String[]{fetchStatus},null,null,"smsDate desc");
        List<Sms> mItem = new ArrayList<>();
        if (cur.moveToFirst()) {
            Sms tmpSms = null;
            int indexSmsID = cur.getColumnIndex("smsID");
            int indexSmsDate = cur.getColumnIndex("smsDate");
            int indexCode = cur.getColumnIndex("code");
            int indexPhone = cur.getColumnIndex("phone");
            int indexPosition = cur.getColumnIndex("position");
            int indexFetchDate = cur.getColumnIndex("fetchDate");
            do {
                String smsID =  cur.getString(indexSmsID);
                String smsDate =  cur.getString(indexSmsDate);
                String code =  cur.getString(indexCode);
                String phone =  cur.getString(indexPhone);
                String position =  cur.getString(indexPosition);
                String fetchDate =  cur.getString(indexFetchDate);

                tmpSms = new Sms(smsID,smsDate,code,phone,position,fetchDate, fetchStatus);

                mItem.add(tmpSms);
            } while (cur.moveToNext());

            if (!cur.isClosed()) {
                cur.close();
            }
        }
        return mItem;
    }


    /**
     * 从手机按时间，抓短信，对比数据库
     */
    public void checkSmsFromPhone() {
        try {
            Uri uri = Uri.parse("content://sms/");
            String[] projection = new String[]{"body", "date"};
            //后期改写selection，实现只抓大于同步时间的
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {

                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                String strCode = null;
                String position = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");


                do {
                    String longBody = cur.getString(index_Body);
                    if (longBody.contains("馒头房") || longBody.contains("丰巢") || longBody.contains("日日顺")) {
                        Sms tmpSms;
                        long longDate = cur.getLong(index_Date);
                        String smsID = String.valueOf(longDate);
                        String strDate = dateFormat.format(new Date(longDate));

                        if (!existInDB(smsID)) {
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
                            tmpSms = new Sms(smsID, strDate, strCode, tel, position, "", "未取");//不能用null，不然报错
                            boolean a = insertSms(tmpSms);

                        }

                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            }
        } catch (Exception ignored) {
        }

    }




    //逐条，将手机短信与本地数据库对比。返回boolean
    public boolean existInDB(String smsID) {

        @SuppressLint("Recycle") Cursor cur = db.query(true, DatabaseHelper.dbTableName, new String[]{"smsID"},
                "smsID = ?", new String[]{smsID}, null, null, "smsID desc", "1");
        return cur.moveToFirst();
    }


    //对比之后，往数据里写短信
    public boolean insertSms(Sms sms) {
        ContentValues cv = new ContentValues();
        cv.put("smsID", sms.getSmsID());
        cv.put("smsDate", sms.getSmsDate());
        cv.put("code", sms.getCode());
        cv.put("phone", sms.getPhone());
        cv.put("position", sms.getPosition());
        cv.put("fetchDate", sms.getFetchDate());
        cv.put("fetchStatus", sms.getFetchStatus());

        long rowId = db.insert(DatabaseHelper.dbTableName, null, cv);
        return (rowId == -1);
    }



    public void dbSample() {

        SimpleDateFormat df1 = new SimpleDateFormat("MMdd HHmmss");//设置日期格式
        SimpleDateFormat df2 = new SimpleDateFormat("M-dd");//设置日期格式

        ContentValues contentValues = new ContentValues();
        contentValues.put("smsID", df1.format(new Date()));
        contentValues.put("smsDate", df2.format(new Date()));
        contentValues.put("code", "12345678");
        contentValues.put("phone", tel);
        contentValues.put("position", "日日顺");
        contentValues.put("fetchDate", "1-28");
        contentValues.put("fetchStatus", "未取");
        long rowId = db.insert(DatabaseHelper.dbTableName, null, contentValues);
        Toast.makeText(MainActivity.this, String.valueOf(rowId), Toast.LENGTH_SHORT).show();
    }


    class btnLister implements OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

}

