package com.express.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.express.Const.APP_MODE_SINGLE;
import static com.express.Const.INIT_DATE_STRING;
import static com.express.database.DBManager.COL_PARAM_NAME;
import static com.express.database.DBManager.COL_PARAM_VALUE;
import static com.express.database.DBManager.COL_RULES_ID;
import static com.express.database.DBManager.COL_SMS_CODE;
import static com.express.database.DBManager.COL_SMS_SHORT_DATE;
import static com.express.database.DBManager.COL_SMS_FETCH_DATE;
import static com.express.database.DBManager.COL_SMS_FETCH_STATUS;
import static com.express.database.DBManager.COL_SMS_ID;
import static com.express.database.DBManager.COL_SMS_PHONE;
import static com.express.database.DBManager.COL_SMS_POSITION;
import static com.express.database.DBManager.DB_NAME;
import static com.express.database.DBManager.PARAM_NAME_PASSWORD;
import static com.express.database.DBManager.PARAM_NAME_PORT;
import static com.express.database.DBManager.PARAM_NAME_USER;
import static com.express.database.DBManager.TABLE_PARAM;
import static com.express.database.DBManager.COL_RULES_KEYWORD;
import static com.express.database.DBManager.COL_RULES_LEFT;
import static com.express.database.DBManager.COL_RULES_RIGHT;
import static com.express.database.DBManager.PARAM_NAME_APP_MODE;
import static com.express.database.DBManager.PARAM_NAME_DB_URL;
import static com.express.database.DBManager.PARAM_NAME_SYNC_TIME;


public class DatabaseHelper extends SQLiteOpenHelper {
    //类没有实例化,是不能用作父类构造器的参数,必须声明为静态
    public static final String DB_FILENAME = DB_NAME+".db"; //数据库文件名称
    private static final int VERSION = 1; //数据库版本


    DatabaseHelper(Context context) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, DB_FILENAME, null, VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlCreateDB =
                "CREATE TABLE IF NOT EXISTS " + DBManager.TABLE_SMS +
                        " (" + COL_SMS_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        COL_SMS_SHORT_DATE + " TEXT, " +
                        COL_SMS_CODE + " TEXT, " +
                        COL_SMS_PHONE + " TEXT, " +
                        COL_SMS_POSITION + " TEXT, " +
                        COL_SMS_FETCH_DATE + " TEXT, " +
                        COL_SMS_FETCH_STATUS + " TEXT )";

        String sqlCreateParam =
                "CREATE TABLE IF NOT EXISTS " + TABLE_PARAM +
                        " (id           INTEGER     PRIMARY KEY AUTOINCREMENT, " +
                        COL_PARAM_NAME + " TEXT    NOT NULL ," +
                        COL_PARAM_VALUE + " TEXT    NOT NULL )";

        String sqlCreateRules =
                "CREATE TABLE IF NOT EXISTS " + DBManager.TABLE_RULES +
                        " (" + COL_RULES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_RULES_KEYWORD + " TEXT NOT NULL, " +
                        COL_RULES_LEFT + " TEXT NOT NULL, " +
                        COL_RULES_RIGHT + " TEXT NOT NULL )";

        db.execSQL(sqlCreateDB);
        db.execSQL(sqlCreateParam);
        db.execSQL(sqlCreateRules);

        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"1", PARAM_NAME_SYNC_TIME, INIT_DATE_STRING});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"2", PARAM_NAME_APP_MODE, APP_MODE_SINGLE});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"3", PARAM_NAME_DB_URL, ""});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"4", PARAM_NAME_USER, ""});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"5", PARAM_NAME_PASSWORD, ""});
        db.execSQL("INSERT INTO " + TABLE_PARAM + " VALUES (?,?,?)", new Object[]{"6", PARAM_NAME_PORT, ""});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
