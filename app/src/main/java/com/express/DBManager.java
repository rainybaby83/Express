package com.express;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static  DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.getInstance());
    private static SQLiteDatabase db = databaseHelper.getWritableDatabase();


    //对比之后，往数据里写短信
    static boolean insertSms(SmsData sms) {
        ContentValues cv = new ContentValues();
        cv.put("sms_id", sms.getSmsID());
        cv.put("sms_date", sms.getSmsDate());
        cv.put("sms_code", sms.getCode());
        cv.put("sms_phone", sms.getPhone());
        cv.put("sms_position", sms.getPosition());
        cv.put("sms_fetch_date", sms.getFetchDate());
        cv.put("sms_fetch_status", sms.getFetchStatus());

        long rowId = db.insert(DatabaseHelper.dbTableName, null, cv);
        return (rowId == -1);
    }


    //从本地数据库抓短信，返回list，展示在fragment
    static List<SmsData> getSmsFromDB(String fetchStatus) {
        Cursor cur = db.query(DatabaseHelper.dbTableName, new String[]{"sms_id,sms_date,sms_code,sms_phone,sms_position,sms_fetch_date"},
                "sms_fetch_status = ?", new String[]{fetchStatus}, null, null, "sms_phone,sms_date desc");
        List<SmsData> mItem = new ArrayList<>();
        if (cur.moveToFirst()) {
            SmsData tmpSms = null;
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

                tmpSms = new SmsData(smsID, smsDate, code, phone, position, fetchDate, fetchStatus);

                mItem.add(tmpSms);
            } while (cur.moveToNext());

            if (!cur.isClosed()) {
                cur.close();
            }
        }
        return mItem;
    }


    //逐条，将手机短信与本地数据库对比。返回boolean
    static boolean existInDatabase(String smsID) {
        @SuppressLint("Recycle") Cursor cur = db.query(true, DatabaseHelper.dbTableName, new String[]{"sms_id"},
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

        @SuppressLint("Recycle") Cursor cur = db.query(true, DatabaseHelper.dbParaTableName, new String[]{"download_date"},
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

    static boolean updateFetch(String smsID, String fetchDate) {
        if (smsID != null) {
            ContentValues cv = new ContentValues();
            cv.put("sms_fetch_status","已取");
            cv.put("sms_fetch_date",fetchDate);
            int returnValue = db.update(DatabaseHelper.dbTableName, cv, "sms_id = " + smsID, null);
            if (returnValue == -1) {
                return false;
            }
        }
        return true;
    }
}
