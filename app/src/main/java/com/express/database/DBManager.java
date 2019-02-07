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
    private static  DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.getInstance());
    private static SQLiteDatabase db = databaseHelper.getWritableDatabase();

    static final String TABLE_SMS = "SMS";
    static final String TABLE_PARAM = "param";
    static final String TABLE_RULES = "rules";
    static final String COL_NAME_KEYWORD = "keyword";
    static final String COL_NAME_LEFT = "code_left";
    static final String COL_NAME_RIGHT = "code_right";

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
        return (rowId == -1);
    }



    //往数据里写短信
    public static boolean insertRule(RulesEntity rulesEntity) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME_KEYWORD, rulesEntity.getKeyword());
        cv.put(COL_NAME_LEFT, rulesEntity.getCodeLeft());
        cv.put(COL_NAME_RIGHT, rulesEntity.getCodeRight());

        long rowId = db.insert(TABLE_SMS, null, cv);
        return (rowId == -1);
    }

    //从本地数据库抓短信，返回list，展示在fragment
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
        return true;
    }


    public String getDownloadDate() {
        String strDate = "2019-1-1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long dateLong = null;

        @SuppressLint("Recycle") Cursor cur = db.query(true, TABLE_PARAM, new String[]{"download_date"},
                null, null, null, null, null, "1");
        if (cur.moveToFirst()) {
            strDate = cur.getString(cur.getColumnIndex("download_date"));
            try {
                dateLong = sdf.parse(strDate).getTime();
            } catch (ParseException ignored) {
                return "1546272000000";     //即2019-1-1
            }
        }
        return dateLong.toString();
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
