package com.express.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.express.Const.APP_MODE_SINGLE;
import static com.express.database.DBManager.COL_RULES_ID;
import static com.express.database.DBManager.TABLE_PARAM;
import static com.express.database.DBManager.COL_RULES_KEYWORD;
import static com.express.database.DBManager.COL_RULES_LEFT;
import static com.express.database.DBManager.COL_RULES_RIGHT;
import static com.express.database.DBManager.ROW_PARAM_UP_DATE;
import static com.express.database.DBManager.ROW_PARAM_APP_MODE;
import static com.express.database.DBManager.ROW_PARAM_DB_URL;
import static com.express.database.DBManager.ROW_PARAM_DOWN_DATE;


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
                "CREATE TABLE IF NOT EXISTS " + TABLE_PARAM +
                        " (id           INTEGER     PRIMARY KEY AUTOINCREMENT, " +
                        " param_name    TEXT    NOT NULL ,"+
                        " value         TEXT    NOT NULL )";

        String sqlCreateRules =
                "CREATE TABLE IF NOT EXISTS " + DBManager.TABLE_RULES +
                        " (" + COL_RULES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_RULES_KEYWORD + " TEXT NOT NULL, " +
                        COL_RULES_LEFT + " TEXT NOT NULL, " +
                        COL_RULES_RIGHT + " TEXT NOT NULL )";

        db.execSQL(sqlCreateDB);
        db.execSQL(sqlCreateParam);
        db.execSQL(sqlCreateRules);

        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"1", ROW_PARAM_APP_MODE, APP_MODE_SINGLE});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"2", ROW_PARAM_DB_URL, ""});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"3", ROW_PARAM_UP_DATE, "2019-2-1 00:00:00"});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"4", ROW_PARAM_DOWN_DATE, "2019-2-1 00:00:00"});

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
