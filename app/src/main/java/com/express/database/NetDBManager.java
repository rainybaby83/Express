package com.express.database;




import com.express.activity.MainActivity;

import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.express.Const.APP_MODE_NET;
import static com.express.Const.APP_MODE_SINGLE;

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
    public static Boolean getConnectStatus() {
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
     * 不能单独使用，必须保证statusConn ==true，因此需要先执行getConnectStatus()
     * @return 返回是否成功，Boolean
     */
    public static Boolean getDbExistStatus() {
        if (MainActivity.getInstance().mAppMode == APP_MODE_NET && statusConn == true) {
            try {
                dbConn = DriverManager.getConnection(mUrl + DBManager.DB_NAME, mUser, mPassword);//获取连接
                dbStmt = dbConn.createStatement();
                statusDbExist = true;
            } catch (Exception e) {
                statusDbExist = false;
            }
        }
        return statusDbExist;
    }


    /**
     * 创建数据库及表结构。
     * 不能单独使用，必须保证dbConn !=null，因此需要先执行getDbExistStatus()
     * @return 返回是否成功，Boolean
     */
    public static Boolean CreateDb() {
        Boolean status = false;
        //远程mysql可以连接，则开始创建
        if (testConn != null) {
            String sqlCreateDb = "CREATE DATABASE IF NOT EXISTS " + DBManager.DB_NAME + " DEFAULT CHARSET utf8 COLLATE utf8_general_ci";
            try {
                testStmt = testConn.createStatement();
                testStmt.execute(sqlCreateDb);
                InputStreamReader inputReader = null;

                inputReader = new InputStreamReader( MainActivity.getInstance().getResources().getAssets().open("express.sql") );
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





    public static Boolean initTable() {
        Boolean status = false;
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader( MainActivity.getInstance().getResources().getAssets().open("express.sql") );
            Reader reader = new BufferedReader(inputReader);
            ScriptRunner runner = new ScriptRunner(dbConn);
            runner.setSendFullScript(false);
            runner.runScript(reader);
            status = true;
        } catch (IOException e) {
            status = false;
        }
        return status;
    }

}