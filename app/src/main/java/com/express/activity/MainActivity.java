package com.express.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.express.Const;
import com.express.database.DBManager;
import com.express.R;
import com.express.entity.RulesEntity;
import com.express.entity.SmsEntity;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnClickListener {
    public String mTelNum;
    private FragmentFetch fragmentFetch;
    private FragmentDone fragmentDone;
    private static MainActivity mInstance;
    public Toolbar mToolbar;

    @SuppressLint({"HardwareIds", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //设置短信权限
        String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.REQUEST_INSTALL_PACKAGES};
        ActivityCompat.requestPermissions(this, permissions, 2);
        mInstance = this;
        this.initToolbar();
        this.appInit();//初始化控件

        boolean tmp = checkFromPhoneAndInsert();
        this.selectTab(0);
    }


    //获取Activity实例
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
            default:
        }
    }


    private void initToolbar() {

        mToolbar = findViewById(R.id.toolbar);
        //设置menu
        mToolbar.inflateMenu(R.menu.menu_main_activity);
        //设置menu的点击事件
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.menu_rules) {
                    Intent i = new Intent(MainActivity.this , RulesActivity.class);
                    startActivity(i);
                } else if (menuItemId == R.id.menu_web) {
                    Toast.makeText(MainActivity.this, "网络", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

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
                fragmentFetch.refresh(DBManager.getSmsFromDB(Const.FECTH_STATE_NOT_DONE));
                break;
            case 1:
                if (fragmentDone == null) {
                    fragmentDone = new FragmentDone();
                    transaction.add(R.id.frameLayout, fragmentDone);
                } else {
                    transaction.show(fragmentDone);
                }
                fragmentDone.refresh(DBManager.getSmsFromDB(Const.FECTH_STATE_DONE));
                break;
            default:
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
        List<RulesEntity> rules = DBManager.getRules();
        if (rules.size() > 0) {
            StringBuilder where = null;
            String[] args = new String[rules.size()];
            for (RulesEntity rule:rules ) {
                where.append(" body like '%?%' OR ");
                args[rules.indexOf(rule)] = rule.getKeyword();

            }
            where.append("1=0").toString();

            try {
                Uri uri = Uri.parse("content://sms/");
                String[] columns = new String[]{"body", "date"};
//                where = new StringBuilder("(body like '%?%'  OR  body like '%?%'  OR  body like '%?%')");
//            String[] args = new String[]{"馒头房","丰巢","日日顺"};
                Cursor cur = MainActivity.getInstance().getContentResolver().query(uri, columns, where.toString(), args, "date desc"); // 获取手机内部短信

                if (cur != null && cur.moveToFirst()) {
                    int indexBody = cur.getColumnIndex("body");
                    int indexDate = cur.getColumnIndex("date");
                    String strCode = null;
                    String position = null;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd");

                    do {
                        String longBody = cur.getString(indexBody);
                        SmsEntity tmpSms;
                        long longDate = cur.getLong(indexDate);
                        String smsID = String.valueOf(longDate);
                        String strDate = dateFormat.format(new Date(longDate));

                        if (!DBManager.existInDatabase(smsID)) {
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
                            tmpSms = new SmsEntity(smsID, strDate, strCode, MainActivity.getInstance().mTelNum, position, null, Const.FECTH_STATE_NOT_DONE);
                            DBManager.insertSms(tmpSms);
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

    }


}

