package com.express;


import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;

import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnClickListener{
    public DatabaseHelper databaseHelper;
    public SqlScoutServer sqlScoutServer;
    public SQLiteDatabase db;

    //Tab分别对应的Fragment
    private FragmentFetch fragmentFetch;
    private Fragment fragmentDone;

//    public AlertDialog.Builder builder = new AlertDialog.Builder(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sqlScoutServer = SqlScoutServer.create(this, getPackageName());
        super.onCreate(savedInstanceState);
        //设置短信权限
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS},    1);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        this.dbInit();
        this.dbSample();
        this.viewInit();//初始化控件
        this.selectTab(0);//默认选中第一个Tab
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

    public void dbInit() {
        databaseHelper = new DatabaseHelper(this);
    }

    public void dbSample() {
        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("smsID", "2016546");
        contentValues.put("smsDate", "1-17");
        contentValues.put("code", "code");
        contentValues.put("phone", "phone");
        contentValues.put("position", "position");
        contentValues.put("fetchDate", "1-28");
        contentValues.put("status", 0);
        long rowId = db.insert("express", null, contentValues);
        Toast.makeText(MainActivity.this, String.valueOf(rowId), Toast.LENGTH_SHORT).show();
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





    //要作为公共方法，抓本地短信，分3类后，每条短信对比本地数据库ID，写入数据库
    public String getSmsInPhone() {

        StringBuilder smsBuilder = new StringBuilder();
        try {
            Uri uri = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type",};
            Cursor cur = getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {
//                int index_Address = cur.getColumnIndex("address");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                Sms tmpSms;
                String smsId;
                String strCode;


                do {
                    String strBody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);
//                    StringBuilder tmpStr = new StringBuilder();
                    tmpSms = null;
                    smsId = String.valueOf(d);
                    strCode = null;


                    if (strBody.contains("馒头房")) {

                        strCode = StringUtils.substringBetween(strBody, "提货码", "来取");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","馒头房",null,0);


                    } else if (strBody.contains("丰巢")) {
                        strCode = StringUtils.substringBetween(strBody, "请凭取件码『", "』前往明珠西苑");


                    } else if (strBody.contains("日日顺")) {
                        strCode = StringUtils.substringBetween(strBody, "凭取件码", "到明珠西苑");

                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                smsBuilder.append("no result!");
            }


        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        } catch (NullPointerException ignored) {
        }

        return smsBuilder.toString();
    }





    //同步本地数据到服务器。返回boolean
    public void syncDB() {

    }

    //逐条，将手机短信与本地数据库对比。返回boolean
    public void compareDB(Sms sms) {

    }


    //从数据抓短信，放到List，
    public List<Sms> getSmsFromDB(int smsStatus) {

        return null;
    }


    /**
     * 从手机按时间，抓短信，对比数据库
     * @return
     */
    public  String getSmsFromPhone() {

        StringBuilder smsBuilder = new StringBuilder();
        try {
            Uri uri = Uri.parse("content://sms/inbox");
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type",};
            //后期改写selection，实现只抓大于同步时间的
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc"); // 获取手机内部短信

            if (cur.moveToFirst()) {
//                int index_Address = cur.getColumnIndex("address");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                Sms tmpSms;
                String smsId;
                String strCode;


                do {
                    String strBody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);
//                    StringBuilder tmpStr = new StringBuilder();
                    tmpSms = null;
                    smsId = String.valueOf(d);
                    strCode = null;


                    if (strBody.contains("馒头房")) {

                        strCode = StringUtils.substringBetween(strBody, "提货码", "来取");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","馒头房",null,0);
                        fragmentFetch.mSmsList.add(tmpSms);
//                        tmpStr.append(strDate).append("：馒头房：").append(expressCode);
//                        smsBuilder.append(tmpStr).append(" \n\n");

                    } else if (strBody.contains("丰巢")) {
                        strCode = StringUtils.substringBetween(strBody, "请凭取件码『", "』前往明珠西苑");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","丰巢",null,0);
                        fragmentFetch.mSmsList.add(tmpSms);
//                        tmpStr.append(strDate).append("：丰巢：").append(expressCode);
//                        smsBuilder.append(tmpStr).append(" \n\n");

                    } else if (strBody.contains("日日顺")) {
                        strCode = StringUtils.substringBetween(strBody, "凭取件码", "到明珠西苑");
                        tmpSms = new Sms(0,smsId,strDate,strCode,"7350","日日顺",null,0);
                        fragmentFetch.mSmsList.add(tmpSms);
//                        tmpStr.append(strDate).append("：日日顺：").append(expressCode);
//                        smsBuilder.append(tmpStr).append(" \n\n");
                    }

                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            } else {
                smsBuilder.append("no result!");
            }


        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsFromPhone", ex.getMessage());
        } catch (NullPointerException ignored) {
        }

        return smsBuilder.toString();
    }


    //对比之后，往数据里写短信
    public void insertSms(Sms sms) {

    }


}
