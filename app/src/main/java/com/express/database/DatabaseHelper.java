package com.express.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.express.database.DBManager.COL_NAME_KEYWORD;
import static com.express.database.DBManager.COL_NAME_LEFT;
import static com.express.database.DBManager.COL_NAME_RIGHT;

public class DatabaseHelper extends SQLiteOpenHelper {
    //类没有实例化,是不能用作父类构造器的参数,必须声明为静态
    private static final String dbFileName = "express.db"; //数据库名称
    private static final int version = 1; //数据库版本


    DatabaseHelper(Context context) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, dbFileName, null, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlCreateDB =
                "CREATE TABLE IF NOT EXISTS " + DBManager.TABLE_SMS +
                        " (sms_id           TEXT      PRIMARY KEY NOT NULL, " +
                        " sms_date          TEXT, " +
                        " sms_code          TEXT, " +
                        " sms_phone         TEXT, " +
                        " sms_position      TEXT, " +
                        " sms_fetch_date    TEXT, " +
                        " sms_fetch_status  TEXT )";

        String sqlCreateParam =
                "CREATE TABLE IF NOT EXISTS " + DBManager.TABLE_PARAM +
                        " (param_id      INT      PRIMARY KEY NOT NULL, " +
                        " app_mode       TEXT     NOT NULL, " +
                        " db_url         TEXT     NOT NULL, " +
                        " upload_date    TEXT     NOT NULL, " +
                        " download_date  TEXT     NOT NULL )";

        String sqlCreateRules =
                "CREATE TABLE IF NOT EXISTS " + DBManager.TABLE_RULES +
                        " ( " + COL_NAME_KEYWORD + " TEXT PRIMARY KEY NOT NULL, " +
                        COL_NAME_LEFT + " TEXT NOT NULL, " +
                        COL_NAME_RIGHT + " TEXT NOT NULL )";


        db.execSQL(sqlCreateDB);
        db.execSQL(sqlCreateParam);
        db.execSQL(sqlCreateRules);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
