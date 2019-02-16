package com.express.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.express.Const;
import com.express.entity.RulesEntity;
import com.express.entity.SmsEntity;
import com.express.activity.MainActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.express.Const.COMPARE_EQUAL;
import static com.express.Const.COMPARE_HALF_EQUAL;
import static com.express.Const.COMPARE_NOT_EQUAL;
import static com.express.Const.FECTH_STATE_NOT_DONE;
import static com.express.Const.INIT_DATE_LONG;
import static com.express.Const.SDF_yyyy_M_d;


public class DBManager {
    public static String dbNetUrl;
    private static DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.getInstance());
    private static SQLiteDatabase db = databaseHelper.getWritableDatabase();
    public final static String DB_NAME = "express";


    static final String TABLE_SMS = "SMS";
    static final String TABLE_PARAM = "param";
    static final String TABLE_RULES = "rules";
    static final String COL_RULES_ID = "id";
    static final String COL_RULES_KEYWORD = "keyword";
    static final String COL_RULES_LEFT = "code_left";
    static final String COL_RULES_RIGHT = "code_right";
    static final String COL_SMS_ID = "sms_id";
    static final String COL_SMS_SHORT_DATE = "sms_short_date";
    static final String COL_SMS_POSITION = "sms_position";
    static final String COL_SMS_CODE = "sms_code";
    static final String COL_SMS_PHONE = "sms_phone";
    static final String COL_SMS_FETCH_DATE = "sms_fetch_date";
    static final String COL_SMS_FETCH_STATUS = "sms_fetch_status";
    static final String VALUE_APP_MODE = "app_mode";
    static final String VALUE_DB_URL = "db_url";
    static final String VALUE_SYNC_TIME = "sync_time";


    //往数据库里写短信
    public static boolean insertSms(SmsEntity sms) {
        ContentValues cv = new ContentValues();
        cv.put(COL_SMS_ID, sms.getSmsID());
        cv.put(COL_SMS_SHORT_DATE, sms.getSmsDate());
        cv.put(COL_SMS_CODE, sms.getCode());
        cv.put(COL_SMS_PHONE, sms.getPhone());
        cv.put(COL_SMS_POSITION, sms.getPosition());
        cv.put(COL_SMS_FETCH_DATE, sms.getFetchDate());
        cv.put(COL_SMS_FETCH_STATUS, sms.getFetchStatus());

        long rowId = db.insert(TABLE_SMS, null, cv);
        return (rowId != -1);
    }


    /**
     * 往数据库里写规则
     *
     * @param rulesEntity 规则实体
     * @return 是否插入成功
     */
    public static boolean insertRule(RulesEntity rulesEntity) {
        return (insertRule(rulesEntity.getKeyword(), rulesEntity.getCodeLeft(), rulesEntity.getCodeRight()));
    }


    /**
     * @param keyword   被拦截短信的关键字
     * @param codeLeft  验证码左侧字符串
     * @param codeRight 验证码右侧字符串
     * @return 是否插入成功
     */
    public static boolean insertRule(String keyword, String codeLeft, String codeRight) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RULES_KEYWORD, keyword);
        cv.put(COL_RULES_LEFT, codeLeft);
        cv.put(COL_RULES_RIGHT, codeRight);
        long rowId = db.insert(TABLE_RULES, null, cv);
        return (rowId != -1);
    }


    //从数据库里删除规则rule
    public static boolean deleteRule(RulesEntity r) {
        String where = new StringBuffer(COL_RULES_KEYWORD).append(" = ? AND ")
                .append(COL_RULES_LEFT).append(" = ? AND ")
                .append(COL_RULES_RIGHT).append(" = ? ").toString();
        String[] values = new String[]{r.getKeyword(), r.getCodeLeft(), r.getCodeRight()};
        long rowId = db.delete(TABLE_RULES, where, values);
        return (rowId != -1);
    }


    /**
     * 按照日期查找本手机数据库，返回List集合
     * @param date 查询参数：日期
     * @return 短信List结合
     */
    public static List<SmsEntity> getSmsFromDB(Long date) {
        String where = COL_SMS_ID + " >= ?";
        String[] params = {date.toString()};
        String orderby = COL_SMS_POSITION + "," + COL_SMS_SHORT_DATE + " desc";
        return getSmsFromDB(where, params, orderby);
    }


    /**
     * 按照取件状态查找本手机数据库，返回List集合
     * @param fetchStatus 查询参数，取件状态
     * @return 短信List结合
     */
    public static List<SmsEntity> getSmsFromDB(String fetchStatus) {
        return getSmsFromDB(0L, fetchStatus);
    }


    /**
     * 按照日期和取件状态查找本手机数据库，返回List集合
     * @param date        查询参数，查询的起始日期
     * @param fetchStatus 查询参数，取件状态
     * @return 短信List结合
     */
    private static List<SmsEntity> getSmsFromDB(Long date, String fetchStatus) {
        String where = COL_SMS_FETCH_STATUS + " = ? AND " + COL_SMS_SHORT_DATE + " >= ?";
        String[] params = {fetchStatus, date.toString()};
        String orderby = COL_SMS_POSITION + "," + COL_SMS_SHORT_DATE + " desc";
        return getSmsFromDB(where, params, orderby);
    }


    /**
     * @param where   查询条件
     * @param params  查询参数
     * @param orderby 排序规则
     * @return 短信List集合
     */
    private static List<SmsEntity> getSmsFromDB(String where, String[] params, String orderby) {
        String[] columns = {COL_SMS_ID, COL_SMS_SHORT_DATE, COL_SMS_CODE, COL_SMS_PHONE, COL_SMS_POSITION, COL_SMS_FETCH_DATE,COL_SMS_FETCH_STATUS};
        Cursor cur = db.query(TABLE_SMS, columns, where, params, null, null, orderby);
        List<SmsEntity> mItem = new ArrayList<>();
        if (cur.moveToFirst()) {
            SmsEntity tmpSms;
            int indexSmsID = cur.getColumnIndex(COL_SMS_ID);
            int indexSmsDate = cur.getColumnIndex(COL_SMS_SHORT_DATE);
            int indexCode = cur.getColumnIndex(COL_SMS_CODE);
            int indexPhone = cur.getColumnIndex(COL_SMS_PHONE);
            int indexPosition = cur.getColumnIndex(COL_SMS_POSITION);
            int indexFetchDate = cur.getColumnIndex(COL_SMS_FETCH_DATE);
            int indexFetchStatus = cur.getColumnIndex(COL_SMS_FETCH_STATUS);
            do {
                Long smsID = cur.getLong(indexSmsID);
                String smsShortDate = cur.getString(indexSmsDate);
                String code = cur.getString(indexCode);
                String phone = cur.getString(indexPhone);
                String position = cur.getString(indexPosition);
                String fetchDate = cur.getString(indexFetchDate);
                String fetchStatus = cur.getString(indexFetchStatus);
                tmpSms = new SmsEntity(smsID, smsShortDate, code, phone, position, fetchDate, fetchStatus);
                mItem.add(tmpSms);
            } while (cur.moveToNext());
            if (!cur.isClosed()) {
                cur.close();
            }
        }
        return mItem;
    }


    //从本地数据库读取规则，返回List
    public static List<RulesEntity> getRules() {
        String[] columns = {COL_RULES_KEYWORD, COL_RULES_LEFT, COL_RULES_RIGHT};
        Cursor cur = db.query(TABLE_RULES, columns, null, null, null, null, null);
        List<RulesEntity> mItem = new ArrayList<>();
        if (cur.moveToFirst()) {
            RulesEntity tmpRules ;
            int indexKeyword = cur.getColumnIndex(COL_RULES_KEYWORD);
            int indexCodeLeft = cur.getColumnIndex(COL_RULES_LEFT);
            int indexCodeRight = cur.getColumnIndex(COL_RULES_RIGHT);
            do {
                String strKeyword = cur.getString(indexKeyword);
                String strCodeLeft = cur.getString(indexCodeLeft);
                String strCodeRight = cur.getString(indexCodeRight);

                tmpRules = new RulesEntity(strKeyword, strCodeLeft, strCodeRight);
                mItem.add(tmpRules);
            } while (cur.moveToNext());
            if (!cur.isClosed()) {
                cur.close();
            }
        }
        return mItem;
    }


    /**
     * 按手机短信ID对比本地数据库，查找是否存在
     * @param smsID 要对比的短信ID
     * @return true false
     */
    public static boolean existInDatabase(String smsID) {
        String[] columns = {COL_SMS_ID};
        String where = COL_SMS_ID + " = ?";
        String[] params = {smsID};
        String orderby = COL_SMS_ID + " desc";
        Cursor cur = db.query(true, TABLE_SMS, columns, where, params, null, null, orderby, "1");
        boolean a = cur.moveToFirst();
        cur.close();
        return a;
    }


    //同步本地数据到服务器。返回boolean
    public static String syncDB() {

        //取得本机、服务器的同步时间，取最小值
        Long netSyncTime = NetDBManager.getNetSyncTime();
        Long dbSyncTime = DBManager.getDbSyncTime();
        Long minSyncTime = Math.min(netSyncTime, dbSyncTime);

        //按最小值分别获取本机、服务器的短信list
        List<SmsEntity> listSmsDB = getSmsFromDB(minSyncTime);
        List<SmsEntity> listSmsNet = NetDBManager.getSmsFromNet(minSyncTime);

        //调用对比方法
        compareSmsList(listSmsDB, listSmsNet);

        //更新本机和服务器的同步时间

        return Long.toString(minSyncTime);
    }



    /**
     * @return
     */
    public static String getAppMode() {
        //获取运行模式

        //默认单机模式
        return Const.APP_MODE_SINGLE;
    }



    /**
     * @return
     */
    private static Long getDbSyncTime() {
        Long value = INIT_DATE_LONG;
        Cursor cur = db.query(true, TABLE_PARAM, new String[]{"value"}, "param_name = ? ",
                new String[]{VALUE_SYNC_TIME}, null, null, null, "1");
        try {
            if (cur.moveToFirst()) {
                Date d = SDF_yyyy_M_d.parse(cur.getString(cur.getColumnIndex("value")));
                value =d.getTime() ;
                cur.close();
            }
        } catch (ParseException ignored) {
            //如果抛出异常，则value值为INIT_DATE_LONG
        }
          return value;
    }


    /**
     * @param smsID
     * @param fetchDate
     * @return
     */
    public static boolean updateFetch(Long smsID, String fetchDate) {
        if (smsID != null) {
            ContentValues cv = new ContentValues();
            cv.put(COL_SMS_FETCH_STATUS, Const.FECTH_STATE_DONE);
            cv.put(COL_SMS_FETCH_DATE, fetchDate);
            int returnValue = db.update(TABLE_SMS, cv, COL_SMS_ID + " = " + smsID.toString(), null);
            if (returnValue == -1) {
                return false;
            }
        }
        return true;
    }


    /**
     * 1 本机有，服务器有，则对比状态。
     *   1.1状态相同，跳到1.3
     *   1.2状态不同，把服务器或本地的那个“未取”改为“已取”，然后1.3
     *   1.3从两个集合里删掉
     * 2 本机有，服务器无，
     *   2.1 上传到服务器
     *   2.2 从本地集合删除，最终本地集合应该为空
     * 3 服务器集合如果还有数据，写入到本地
     * @param listDB 本地集合
     * @param listNet 服务器集合
     */
    private static void compareSmsList(List<SmsEntity> listDB, List<SmsEntity> listNet) {

//        //如果2个集合都是null，返回true
//        if (listDB == null && listNet == null) {
//            return true;
//        } else if (listDB == null) {
//            //本地集合为空，则把服务器集合全部写入本地
//            listNet.forEach(DBManager::insertSms);
//        } else if (listNet == null) {
//            //服务器集合为空，则把本地集合全部写入服务器
//            listDB.forEach(NetDBManager::insertSms);
//        } else {
            //两个集合都不是null
            for (int i = 0; i < listDB.size(); i++) {
                SmsEntity smsDB = listDB.get(i);

                for (int j = 0; j < listNet.size(); j++) {
                    SmsEntity smsNet = listNet.get(j);
                    //循环服务器集合，
                    int status = smsDB.compareIDandFetch(smsNet);
                    switch (status) {
                        case COMPARE_NOT_EQUAL:
                            //两个元素不相同，保留在集合，跳过
                            break;
                        case COMPARE_HALF_EQUAL:
                            //ID相同，但是取件状态不同，找到未取的那个元素，更新状态，然后从2个集合中删除元素
                            if (smsDB.getFetchStatus().equals(FECTH_STATE_NOT_DONE)) {
                                updateFetch(smsDB.getSmsID(), smsNet.getFetchDate());
                            } else if (smsNet.getFetchStatus().equals(FECTH_STATE_NOT_DONE)) {
                                NetDBManager.updateFetch(smsNet.getSmsID(), smsDB.getFetchDate());
                            }
                            listDB.remove(i);
                            listNet.remove(j);
                            i--;
                            j--;
                            break;
                        case COMPARE_EQUAL:
                            //完全相同，从2个集合中删除元素
                            listDB.remove(i);
                            listNet.remove(j);
                            i--;
                            j--;
                            break;
                    }
                } //end for listNet

            } //end for listDB

            //listDB、listNet全部循环完毕并且remove后，剩下的元素需要互相同步
            listDB.forEach(NetDBManager::insertSms);
            listNet.forEach(DBManager::insertSms);

    }

}
