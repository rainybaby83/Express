//package com.express;
//
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.database.DatabaseErrorHandler;
//import android.database.sqlite.SQLiteDatabase;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.IOException;
//
//
//public class MyContextWrapper extends ContextWrapper {
//
//    private Context mContext;
//    private String dirName;
//
//    /**
//     * 构造函数
//     ** @param base 上下文环境
//     */
//    public MyContextWrapper(Context base) {
//        super(base);
//        this.mContext = base;
//    }
//    /**
//     * 构造函数
//     * @param base
//     * @param dirName
//     */
//    public MyContextWrapper(Context base, String dirName) {
//        super(base);
//        this.mContext = base;
//        this.dirName = dirName;
//    }
//
//    /**
//     * 获得数据库路径，如果不存在，则创建对象对象
//     * @param name
//     */
//    @Override
//    public File getDatabasePath(String name) {
//        // 判断是否存在sd卡
//        boolean sdExist = android.os.Environment.MEDIA_MOUNTED
//                .equals(android.os.Environment.getExternalStorageState());
//        if (!sdExist) {// 如果不存在,
//            Toast.makeText(this,"SD卡管理：SD卡不存在，请加载SD卡",Toast.LENGTH_LONG).show();
//            return null;
//        } else {// 如果存在
//            Toast.makeText(this,"222",Toast.LENGTH_LONG).show();
////            // 获取sd卡路径
////            String dbDir = android.os.Environment.getExternalStorageDirectory()
////                    .getAbsolutePath();
////            dbDir += "/" + ((dirName == null || "".equals(dirName)) ?
////                    mContext.getPackageName() : dirName);// 数据库所在目录
////            String dbPath = dbDir + "/" + name;// 数据库路径
////            // 判断目录是否存在，不存在则创建该目录
////            File dirFile = new File(dbDir);
////            if (!dirFile.exists())
////                dirFile.mkdirs();
////
////            // 数据库文件是否创建成功
////            boolean isFileCreateSuccess = false;
////            // 判断文件是否存在，不存在则创建该文件
////            File dbFile = new File(dbPath);
////            if (!dbFile.exists()) {
////                try {
////                    isFileCreateSuccess = dbFile.createNewFile();// 创建文件
////                } catch (IOException e) {
////
////                }
////            } else
////                isFileCreateSuccess = true;
////
////            // 返回数据库文件对象
////            if (isFileCreateSuccess)
////                return dbFile;
////            else
//                return null;
//        }
//    }
//
//    /**
//     * Android 4.0会调用此方法获取数据库。
//     * @param name
//     * @param mode
//     * @param factory
//     * @param errorHandler
//     */
//    @Override
//    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
//                                               SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
//        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
//                getDatabasePath(name), null);
//        return result;
//    }
//
//}
