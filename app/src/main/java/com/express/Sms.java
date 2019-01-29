package com.express;

public class Sms {
    public int id;
    public String smsID;
    public String smsDate;
    public String code;
    public String phone;
    public String position;
    public String fetchDate;
    public int status;  // 0未取，1已取

    public Sms(int id, String smsID, String smsDate, String code, String phone, String position, String fetchDate, int status) {
        this.id = id;
        this.smsID = smsID;
        this.smsDate = smsDate;
        this.code = code;
        this.phone = phone;
        this.position = position;
        this.fetchDate = fetchDate;
        this.status = status;  // 0未取，1已取

    }

    public boolean compare(Sms newTable) {
        return false;
    }


    public String getFetchDate() {
        return fetchDate;
    }

    public String getCode() {
        return code;
    }

    public void setFetchDate(String str) {
        this.fetchDate = str;
    }



}
