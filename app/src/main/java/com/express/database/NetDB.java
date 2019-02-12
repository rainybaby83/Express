package com.express.database;




import android.widget.Toast;

import com.express.activity.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.express.Const.APP_MODE_NET;
import static com.express.Const.APP_MODE_SINGLE;

public class NetDB {
    private static String mDriver = "com.mysql.jdbc.Driver";
    private static String mUrl = "jdbc:mysql://rainybaby.mysql.rds.aliyuncs.com:3306/information_schema";//MYSQL数据库连接Url
    private static String mUser = "root";
    private static String mPassword = "Raiky_2002";
    private String mysqlDbName;
    private String mysqlPort = "3306";
    private boolean testResult = false;


    /**
     * 连接数据库
     */
//    public Connection getConn(){
//        Connection conn = null;
//        testResult = false;
//        //连接前先判断app运行模式
//        if (MainActivity.getInstance().mAppMode == APP_MODE_NET) {
////        mUrl = DBManager.dbNetUrl + "/" + DatabaseHelper.DB_FILENAME;
//            try {
//                Class.forName(mDriver);
//                conn = (Connection) DriverManager.getConnection(mUrl, mUser, mPassword);//获取连接
//                testResult = true;
//            } catch (Exception e) {
//                MainActivity.getInstance().mAppMode = APP_MODE_SINGLE;
//            }
//        }
//        return conn;
//    }
    public static Boolean getTest() {
        Connection conn = null;
        if (MainActivity.getInstance().mAppMode == APP_MODE_NET) {
            try {
                Class.forName(mDriver).newInstance();
                conn = DriverManager.getConnection(mUrl, mUser, mPassword);//获取连接
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}