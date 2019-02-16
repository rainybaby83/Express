package com.express;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

public class Const {
    public static final String APP_MODE_SINGLE = "单机";
    public static final String APP_MODE_NET = "网络";

    public static final String FECTH_STATE_NOT_DONE = "未取";
    public static final String FECTH_STATE_DONE = "已取";

    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat SDF_yyyy_M_d = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
    public static final String INIT_DATE_STRING = "2019-3-1 00:00:00";
    public static final Long INIT_DATE_LONG = 1546272000000L; //2019-1-1 00:00:00

    public static final int COMPARE_EQUAL = 2;
    public static final int COMPARE_NOT_EQUAL = 0;
    public static final int COMPARE_HALF_EQUAL = 1;


//      public static final Long INIT_DATE_LONG = 1548473807789L;
}
