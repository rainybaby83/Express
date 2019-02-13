package com.express.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import com.express.Const;
import com.express.entity.RulesEntity;
import com.express.entity.SmsEntity;
import com.express.activity.MainActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    public static String dbNetUrl;
    private static  DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.getInstance());
    private static SQLiteDatabase db = databaseHelper.getWritableDatabase();
    public final static String DB_NAME = "express";
//    private static NetDBManager netDatabaseHelper = new NetDBManager();
//    private  Connection conn = NetDBManager.getConn();


    static final String TABLE_SMS = "SMS";
    static final String TABLE_PARAM = "param";
    static final String TABLE_RULES = "rules";
    static final String COL_RULES_ID = "id";
    static final String COL_RULES_KEYWORD = "keyword";
    static final String COL_RULES_LEFT = "code_left";
    static final String COL_RULES_RIGHT = "code_right";
    static final String ROW_PARAM_APP_MODE = "app_mode";
    static final String ROW_PARAM_DB_URL = "db_url";
//    static final String ROW_PARAM_UP_DATE = "up_date";
    static final String ROW_PARAM_SYNCTIME = "sync_time";


    //往数据里写短信
    public static boolean insertSms(SmsEntity sms) {
        ContentValues cv = new ContentValues();
        cv.put("sms_id", sms.getSmsID());
        cv.put("sms_date", sms.getSmsDate());
        cv.put("sms_code", sms.getCode());
        cv.put("sms_phone", sms.getPhone());
        cv.put("sms_position", sms.getPosition());
        cv.put("sms_fetch_date", sms.getFetchDate());
        cv.put("sms_fetch_status", sms.getFetchStatus());

        long rowId = db.insert(TABLE_SMS, null, cv);
        return (rowId != -1);
    }


    /**
     * 往数据库里写规则
     * @param rulesEntity 规则实体
     * @return 是否插入成功
     */
    public static boolean insertRule(RulesEntity rulesEntity) {
        return (insertRule(rulesEntity.getKeyword(), rulesEntity.getCodeLeft(), rulesEntity.getCodeRight()));
    }


    /**
     * @param keyword 被拦截短信的关键字
     * @param codeLeft 验证码左侧字符串
     * @param codeRight 验证码右侧字符串
     * @return 是否插入成功
     */
    public static boolean insertRule(String keyword, String codeLeft, String codeRight) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RULES_KEYWORD, keyword);
        cv.put(COL_RULES_LEFT, codeLeft);
        cv.put(COL_RULES_RIGHT, codeRight);
        long rowId = db.insert(TABLE_RULES, null, cv);
        return (rowId != -1);
    }


    //从数据库里删除规则rule
    public static boolean deleteRule(RulesEntity r) {
        String where = new StringBuffer(COL_RULES_KEYWORD).append(" = ? AND ")
                .append(COL_RULES_LEFT).append(" = ? AND ")
                .append(COL_RULES_RIGHT).append(" = ? ").toString();
        String[] values = new String[]{r.getKeyword(), r.getCodeLeft(), r.getCodeRight()};
        long rowId = db.delete(TABLE_RULES, where, values);
        return (rowId != -1);
    }


    //从本地数据库抓短信，返回List
    public static List<SmsEntity> getSmsFromDB(String fetchStatus) {
        Cursor cur = db.query(TABLE_SMS, new String[]{"sms_id,sms_date,sms_code,sms_phone,sms_position,sms_fetch_date"},
                "sms_fetch_status = ?", new String[]{fetchStatus}, null, null, "sms_position,sms_date desc");
        List<SmsEntity> mItem = new ArrayList<>();
        if (cur.moveToFirst()) {
            SmsEntity tmpSms = null;
            int indexSmsID = cur.getColumnIndex("sms_id");
            int indexSmsDate = cur.getColumnIndex("sms_date");
            int indexCode = cur.getColumnIndex("sms_code");
            int indexPhone = cur.getColumnIndex("sms_phone");
            int indexPosition = cur.getColumnIndex("sms_position");
            int indexFetchDate = cur.getColumnIndex("sms_fetch_date");
            do {
                String smsID = cur.getString(indexSmsID);
                String smsDate = cur.getString(indexSmsDate);
                String code = cur.getString(indexCode);
                String phone = cur.getString(indexPhone);
                String position = cur.getString(indexPosition);
                String fetchDate = cur.getString(indexFetchDate);
                tmpSms = new SmsEntity(smsID, smsDate, code, phone, position, fetchDate, fetchStatus);
                mItem.add(tmpSms);
            } while (cur.moveToNext());
            if (!cur.isClosed()) {
                cur.close();
            }
        }
        return mItem;
    }


    //从本地数据库读取规则，返回List
    public static List<RulesEntity> getRules() {
        Cursor cur = db.query(TABLE_RULES, new String[]{COL_RULES_KEYWORD, COL_RULES_LEFT, COL_RULES_RIGHT},
                null, null, null, null, null);

        List<RulesEntity> mItem = new ArrayList<>();
        if (cur.moveToFirst()) {
            RulesEntity tmpRules = null;
            int indexKeyword = cur.getColumnIndex(COL_RULES_KEYWORD);
            int indexCodeLeft = cur.getColumnIndex(COL_RULES_LEFT);
            int indexCodeRight = cur.getColumnIndex(COL_RULES_RIGHT);

            do {
                String strKeyword = cur.getString(indexKeyword);
                String strCodeLeft = cur.getString(indexCodeLeft);
                String strCodeRight = cur.getString(indexCodeRight);

                tmpRules = new RulesEntity(strKeyword, strCodeLeft, strCodeRight);
                mItem.add(tmpRules);
            } while (cur.moveToNext());
            if (!cur.isClosed()) {
                cur.close();
            }
        }
        return mItem;
    }



    /**
     * 按手机短信ID对比本地数据库，查找是否存在
     * @param smsID 要对比的短信ID
     * @return true false
     */
    public static boolean existInDatabase(String smsID) {
        @SuppressLint("Recycle") Cursor cur = db.query(true, TABLE_SMS, new String[]{"sms_id"},
                "sms_id = ?", new String[]{smsID}, null, null, "sms_id desc", "1");
        return cur.moveToFirst();
    }


    //同步本地数据到服务器。返回boolean
    public static boolean syncDB() {
        /*
        同步数据库
        →取得本机、服务器的同步时间，取最小值
        →按最小值分别获取本机、服务器的短信list，调用对比方法
        →本机有，服务器有，则对比状态。状态相同不做处理，状态不同，把“未取”改为“已取”
        →本机有，服务器无，上传
        →本机无，服务器有，下载
        →更新本机和服务器的同步时间
         */





        return true;
    }

    public static String getAppMode() {
        //获取运行模式


        //默认单机模式
        return Const.APP_MODE_SINGLE;
    }


    public String getSyncTime() {
        String strLongDate = "2019-1-1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long longDate = null;

        @SuppressLint("Recycle") Cursor cur = db.query(true, TABLE_PARAM, new String[]{ROW_PARAM_SYNCTIME},
                null, null, null, null, null, "1");
        if (cur.moveToFirst()) {
            try {
                longDate = sdf.parse(cur.getString(cur.getColumnIndex(ROW_PARAM_SYNCTIME))).getTime();
                strLongDate = longDate.toString();
            } catch (ParseException ignored) {
                strLongDate = "1546272000000";     //即2019-1-1
            }
        }
        return strLongDate;
    }

    public static boolean updateFetch(String smsID, String fetchDate) {
        if (smsID != null) {
            ContentValues cv = new ContentValues();
            cv.put("sms_fetch_status", Const.FECTH_STATE_DONE);
            cv.put("sms_fetch_date",fetchDate);
            int returnValue = db.update(TABLE_SMS, cv, "sms_id = " + smsID, null);
            if (returnValue == -1) {
                return false;
            }
        }
        return true;
    }
}
