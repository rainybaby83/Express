package com.express.database;




import com.express.activity.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.express.Const.APP_MODE_NET;
import static com.express.Const.APP_MODE_SINGLE;

public class NetDatabaseHelper {
    private static String mysqlDriver = "com.mysql.jdbc.Driver";
    private static String mysqlUrl = "jdbc:mysql://myinstance.czraoc72sujg.ap-northeast-1.rds.amazonaws.com:3306/express";//MYSQL数据库连接Url
    private static String user = "Rainybaby";//用户名
    private static String password = "19830512";//密码
    private static boolean testResult = false;

    /**
     * 连接数据库
     * */
    public static Connection getConn(){
        Connection conn = null;
        testResult = false;
        //连接前先判断app运行模式
        if (MainActivity.getInstance().mAppMode.equals(APP_MODE_NET)) {
//        mysqlUrl = DBManager.dbNetUrl + "/" + DatabaseHelper.DB_FILENAME;
            try {
                Class.forName(mysqlDriver);
                conn = DriverManager.getConnection(mysqlUrl, user, password);//获取连接
                testResult = true;
            } catch (Exception e) {
                MainActivity.getInstance().mAppMode = APP_MODE_SINGLE;
            }
        }
        return conn;
    }


}
