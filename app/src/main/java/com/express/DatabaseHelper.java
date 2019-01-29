package com.express;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //类没有实例化,是不能用作父类构造器的参数,必须声明为静态
    private static final String dbFileName = "1.db"; //数据库名称
    private static final int version = 1; //数据库版本
    public static final String dbTableName = "express";

    public DatabaseHelper(Context context) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, dbFileName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlCreate =
                "CREATE TABLE IF NOT EXISTS " + dbTableName +
                        " (smsID     TEXT      PRIMARY KEY NOT NULL, " +
                        " smsDate    TEXT      NOT NULL, " +
                        " code       TEXT      NOT NULL, " +
                        " phone      TEXT      NOT NULL, " +
                        " position   TEXT      NOT NULL, " +
                        " fetchDate  TEXT      NOT NULL, " +
                        " fetchStatus     TEXT      NOT NULL )";
        db.execSQL(sqlCreate);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }






}
