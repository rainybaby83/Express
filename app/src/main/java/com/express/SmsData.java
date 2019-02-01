package com.express;

public class SmsData {
    private String smsID;
    private String smsDate;
    private String code;
    private String phone;
    private String position;
    private String fetchDate;
    private String fetchStatus;  // 未取，已取

    SmsData(String smsID, String smsDate, String code, String phone, String position, String fetchDate, String fetchStatus) {
        this.smsID = smsID;
        this.smsDate = smsDate;
        this.code = code;
        this.phone = phone;
        this.position = position;
        this.fetchDate = fetchDate;
        this.fetchStatus = fetchStatus;  // 未取，已取
    }

    public boolean compare(SmsData newTable) {
        return false;
    }


    String getSmsID() {
        return smsID;
    }

    String getSmsDate() {
        return smsDate;
    }

    String getCode() {
        return code;
    }

    String getPhone() {
        return phone;
    }

    String getPosition() {
        return position;
    }

    String getFetchDate() {
        return fetchDate;
    }

    String getFetchStatus() {
        return fetchStatus;
    }



    public void setFetchDate(String str) {
        this.fetchDate = str;
    }



}
