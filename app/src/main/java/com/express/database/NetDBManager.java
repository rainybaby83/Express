package com.express.database;




import android.content.ContentValues;

import com.express.activity.MainActivity;
import com.express.entity.SmsEntity;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.express.Const.APP_MODE_NET;
import static com.express.Const.APP_MODE_SINGLE;
import static com.express.Const.INIT_DATE_LONG;
import static com.express.Const.SDF_YYYY_M_D;
import static com.express.database.DBManager.COL_SMS_CODE;
import static com.express.database.DBManager.COL_SMS_FETCH_DATE;
import static com.express.database.DBManager.COL_SMS_FETCH_STATUS;
import static com.express.database.DBManager.COL_SMS_ID;
import static com.express.database.DBManager.COL_SMS_PHONE;
import static com.express.database.DBManager.COL_SMS_POSITION;
import static com.express.database.DBManager.COL_SMS_SHORT_DATE;
import static com.express.database.DBManager.DB_NAME;
import static com.express.database.DBManager.VALUE_SYNC_TIME;
import static com.express.database.DBManager.TABLE_PARAM;
import static com.express.database.DBManager.TABLE_SMS;

public class NetDBManager {
    private static String mDriver = "com.mysql.jdbc.Driver";
    private static String mUrl = "jdbc:mysql://rainybaby.mysql.rds.aliyuncs.com:3306/";//MYSQL数据库连接Url
    private static String mUser="root";
    private static String mPassword = "Raiky_2002";
    private static Boolean statusConn = false, statusDbExist = false;
    private static Connection dbConn,testConn;
    private static Statement dbStmt,testStmt;



    /**
     * 测试网络连接是否成功，若失败，app运行模式改为单机
     * @return 返回是否成功，Boolean
     */
    public static boolean getConnectStatus() {
        if (MainActivity.getInstance().mAppMode == APP_MODE_NET) {
            try {
                Class.forName(mDriver);
                testConn = DriverManager.getConnection(mUrl + "information_schema", mUser, mPassword);//获取连接
                statusConn = true;
            } catch (ClassNotFoundException e) {
                statusConn = false;
                MainActivity.getInstance().mAppMode = APP_MODE_SINGLE;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statusConn;
    }


    /**
     * 检查数据库是否存在
     * 不要单独使用，必须先执行getConnectStatus()，保证statusConn=true
     * @return 返回是否成功，Boolean
     */
    public static boolean getDbExistStatus() {
        if (MainActivity.getInstance().mAppMode == APP_MODE_NET && statusConn == true) {
            try {
                ResultSet rs1,rs2;
                String sqlTable1 = "SELECT  DISTINCT `TABLE_SCHEMA` FROM `TABLES` " +
                        "WHERE `TABLE_SCHEMA` = '" + DB_NAME + "' AND `TABLE_NAME` = '" + TABLE_SMS + "';";
                String sqlTable2 = "SELECT  DISTINCT `TABLE_SCHEMA` FROM `TABLES` " +
                        "WHERE `TABLE_SCHEMA` = '" + DB_NAME + "' AND `TABLE_NAME` = '" + TABLE_PARAM + "';";

                //初始化testStmt，后边CreateDb()还需要用
                testStmt = testConn.createStatement();
                //分别调用，不然会操作rs2时会导致rs1关闭
                rs1 = testStmt.executeQuery(sqlTable1);
                boolean sta1 = rs1.next();
                rs2 = testStmt.executeQuery(sqlTable2);
                boolean sta2 = rs2.next();
                //参数表和短信表都存在的话，返回true
                statusDbExist = sta1 && sta2;
            } catch (Exception e) {
                statusDbExist = false;
            }
        }
        return statusDbExist;
    }


    /**
     * 创建数据库及表结构。
     * @return 返回是否成功，Boolean
     */
    public static boolean CreateAndInsert() {
        Boolean status = false;
        //如果远程mysql可以连接，则开始创建
        if (testConn != null) {
            String sqlCreateDb = "CREATE DATABASE IF NOT EXISTS " + DB_NAME + " DEFAULT CHARSET utf8 COLLATE utf8_general_ci";
            try {
                //testStmt在getDbExistStatus()已经初始化
                testStmt.execute(sqlCreateDb);
                //创建数据库后，dbConn必须重新获取一次连接。
                dbConn = DriverManager.getConnection(mUrl + DB_NAME, mUser, mPassword);
                //如果可以获取连接，则初始化dbStmt
                dbStmt = dbConn.createStatement();
                //获取assets目录下的.sql文件，用于创建数据库及标机构
                InputStreamReader inputReader = new InputStreamReader(
                        MainActivity.getInstance().getResources().getAssets().open("express.sql"));
                Reader reader = new BufferedReader(inputReader);
                ScriptRunner runner = new ScriptRunner(dbConn);
                runner.setSendFullScript(false);
                runner.runScript(reader);
                status = true;
            } catch (SQLException e) {
                status = false;
            } catch (IOException e) {
                status = false;
            }
        }
        return status;
    }


    /**
     * 获取远程mysql数据库的同步时间
     * @return String格式的时间
     */
    static Long getNetSyncTime() {
        String sqlGetTime = "SELECT DISTINCT value FROM " + TABLE_PARAM + " WHERE param_name = '" + VALUE_SYNC_TIME + "';";
        Long value = INIT_DATE_LONG;
        try {
            dbConn = DriverManager.getConnection(mUrl + DB_NAME, mUser, mPassword);
            dbStmt = dbConn.createStatement();
            ResultSet rs= dbStmt.executeQuery(sqlGetTime);
            String str;
            Date d;
            if (rs.next()) {
                str = rs.getString("value");
                d = SDF_YYYY_M_D.parse(str);
                value = d.getTime();
            }
        } catch (SQLException e) {
            //如果抛出异常，则value值为INIT_DATE_LONG
        } catch (ParseException e) {
            //如果抛出异常，则value值为INIT_DATE_LONG
            e.printStackTrace();
        }
        return value;
    }


    /**
     * @param beginDate
     * @return
     */
    static List<SmsEntity> getSmsFromNet(Long beginDate) {
        List<SmsEntity> mItem = new ArrayList<>();
        String sqlSelectSms = "SELECT DISTINCT * FROM " + TABLE_SMS + " WHERE " + COL_SMS_ID + " >= " + beginDate;
        try {
            ResultSet rs= dbStmt.executeQuery(sqlSelectSms);
            while (rs.next()) {
                SmsEntity tmpSms;
                Long smsID = rs.getLong(COL_SMS_ID);
                String smsShortDate = rs.getString(COL_SMS_SHORT_DATE);
                String code = rs.getString(COL_SMS_CODE);
                String phone = rs.getString(COL_SMS_PHONE);
                String position = rs.getString(COL_SMS_POSITION);
                String fetchDate = rs.getString(COL_SMS_FETCH_DATE);
                String fetchStatus = rs.getString(COL_SMS_FETCH_STATUS);
                tmpSms = new SmsEntity(smsID, smsShortDate, code, phone, position, fetchDate, fetchStatus);
                mItem.add(tmpSms);
            }
        } catch (SQLException e) {

        }
        return mItem;
    }



    //往数据里写短信
    public static boolean insertSms(SmsEntity sms) {

        try {
            String sqlInsertSms = "INSERT INTO `" + TABLE_SMS + "` (" +
                    COL_SMS_ID + ", " +
                    COL_SMS_SHORT_DATE + ", " +
                    COL_SMS_CODE + ", " +
                    COL_SMS_PHONE + ", " +
                    COL_SMS_POSITION + ", " +
                    COL_SMS_FETCH_DATE + ", " +
                    COL_SMS_FETCH_STATUS + ") " +
                    " VALUES ( " +
                    sms.getSmsID() + "," +
                    sms.getSmsDate() + "," +
                    sms.getCode() + "," +
                    sms.getPhone() + "," +
                    sms.getPosition() + "," +
                    sms.getFetchDate() + "," +
                    sms.getFetchStatus() + ");";
                dbStmt.execute(sqlInsertSms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}