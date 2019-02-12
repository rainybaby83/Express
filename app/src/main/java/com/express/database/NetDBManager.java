package com.express.database;




import com.express.activity.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static com.express.Const.APP_MODE_NET;
import static com.express.Const.APP_MODE_SINGLE;

public class NetDBManager {
    private static String mDriver = "com.mysql.jdbc.Driver";
    private static String mUrl = "jdbc:mysql://rainybaby.mysql.rds.aliyuncs.com:3306/";//MYSQL数据库连接Url
    private static String mUser="root";
    private static String mPassword = "Raiky_2002";
    private static Boolean statusConn = false, statusDbExist = false;
    private static Connection mConn;
    private static Statement stmt;



    /**
     * 测试网络连接是否成功，若失败，app运行模式改为单机
     * @return 返回是否成功，Boolean
     * 验证成功
     */
    public static Boolean getConnectStatus() {
        if (MainActivity.getInstance().mAppMode == APP_MODE_NET) {
            try {
                Class.forName(mDriver);
                Connection tmpConn = DriverManager.getConnection(mUrl + "information_schema", mUser, mPassword);//获取连接
                tmpConn.close();
                statusConn = true;
            } catch (Exception e) {
                statusConn = false;
                MainActivity.getInstance().mAppMode = APP_MODE_SINGLE;
            }
        }
        return statusConn;
    }


    /**
     * 检查数据库是否存在
     * @return 返回是否成功，Boolean
     * 验证成功
     */
    public static Boolean getDbExistStatus() {
        if (MainActivity.getInstance().mAppMode == APP_MODE_NET && statusConn == true) {
            try {
                mConn = DriverManager.getConnection(mUrl + DBManager.DB_NAME, mUser, mPassword);//获取连接
                stmt = mConn.createStatement();
                statusDbExist = true;

            } catch (Exception e) {
                statusDbExist = false;
            }
        }
        return statusDbExist;
    }


    public static Boolean createDB() {
        Boolean status = false;
        String sql;








        return status;
    }

}