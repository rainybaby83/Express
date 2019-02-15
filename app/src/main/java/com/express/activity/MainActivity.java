package com.express.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
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
import com.express.database.NetDBManager;
import com.express.entity.RulesEntity;
import com.express.entity.SmsEntity;

import org.apache.commons.lang3.StringUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.express.Const.APP_MODE_NET;
import static com.express.Const.SDF_YYYY_M_D;
import static java.lang.String.valueOf;

public class MainActivity extends FragmentActivity implements OnClickListener {
    public String mTelNum;
    private FragmentFetch fragmentFetch;
    private FragmentDone fragmentDone;
    private static MainActivity mInstance;
    public Toolbar mToolbar;
    public String mAppMode ;


    @SuppressLint({"HardwareIds", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
                detectDiskWrites().detectNetwork().penaltyLog().build());

        //设置短信权限
        String[] permissions = new String[]{Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.INTERNET};
        ActivityCompat.requestPermissions(this, permissions, 2);
        mInstance = this;



        this.initAppMode();
        this.initToolbar();
        this.appInit();//初始化控件

    }


    @Override
    protected void onStart() {
        super.onStart();
        //放在这里可以保证返回该activity时刷新页面
        this.checkPhoneAndInsert();//此时开始访问数据库
        this.selectTab(0);
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


    //获取Activity实例
    public static MainActivity getInstance() {
        return mInstance;
    }



    private void initAppMode() {
//        mAppMode = DBManager.getAppMode();这句回头要取消注释
        mAppMode = APP_MODE_NET;//方便测试，先设置为网络
        //如果运行模式为网络，则判断一下网络是否通，连接成功的，进入同步模块
        if (mAppMode == APP_MODE_NET) {
            new Thread(taskSyncDB).start();
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.mInstance).setTitle("弹屏测试").setMessage(val);
            builder.show();
        }
    };



    Runnable taskSyncDB = () -> {
        String a = "";
        //连接远程mysql
        if (NetDBManager.getConnectStatus()) {
            //如果远程mysql连接成功 ，则判断数据库是否存在
            if (!NetDBManager.getDbExistStatus()) {
                //如果数据库不存在，则创建数据库
                a = String.valueOf(NetDBManager.CreateAndInsert());
            } else {
                //如果数据库存在，则开始同步
                a = DBManager.syncDB();
            }
        }
        Bundle data = new Bundle();
        data.putString("value", a);
        Message msg = new Message();
        msg.setData(data);
        handler.sendMessage(msg);
    };



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
    public void checkPhoneAndInsert() {
        List<RulesEntity> rules = DBManager.getRules();
        if (rules.size() > 0) {
            StringBuilder where = new StringBuilder();
            String[] args = new String[rules.size()];
            int i = 0;

            //如果用'%?%'的形式，单引号会出错。使用args可以不用单引号
            for (RulesEntity rule : rules) {
                where.append(" body like ? OR ");
                args[i] = "%" + rule.getKeyword() + "%";
                i++;
            }
            where.append("1=0");

            Uri uri = Uri.parse("content://sms/");
            String[] columns = new String[]{"body", "date"};
            // 获取手机内部短信
            Cursor cur = MainActivity.getInstance().getContentResolver().query
                    (uri, columns, where.toString(), args, "date desc");

            if (cur != null && cur.moveToFirst()) {
                int indexBody = cur.getColumnIndex("body");
                int indexDate = cur.getColumnIndex("date");
                String strCode = null;
                String position = null;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("M-dd");

                do {
                    String longBody = cur.getString(indexBody);
                    SmsEntity tmpSms;
                    Long smsID = cur.getLong(indexDate);
                    String strDate = dateFormat.format(new Date(smsID));

                    if (!DBManager.existInDatabase(smsID.toString())) {
                        for (RulesEntity rule : rules) {
                            if (longBody.contains(rule.getKeyword())) {
                                position = rule.getKeyword();
                                strCode = StringUtils.substringBetween(longBody, rule.getCodeLeft(), rule.getCodeRight());
                            }
                        }
                        //如果取到的验证码不为空，则写入数据库
                        if (strCode != null) {
                            tmpSms = new SmsEntity(smsID, strDate, strCode, MainActivity.getInstance().mTelNum, position, null, Const.FECTH_STATE_NOT_DONE);
                            DBManager.insertSms(tmpSms);
                        }
                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                }
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstance = null;
        System.exit(0);
    }



}

