package com.express;

public class smsTable {
    public int id;
    public int smsID;
    public String smsDate;
    public String code;
    public String phone;
    public String type;
    public String fetchDate;
    public int status;  // 0未取，1已取

    public smsTable(int id,int smsID,String smsDate,String code,String phone,String type,String fetchDate,int status) {
        this.id = id;
        this.smsID = smsID;
        this.smsDate = smsDate;
        this.code = code;
        this.phone = phone;
        this.type = type;
        this.fetchDate = fetchDate;
        this.status = status;  // 0未取，1已取

    }

    public boolean compare(smsTable newTable) {
        return false;
    }


    public String getFetchDate() {
        return fetchDate;
    }

    public void setFetchDate(String str) {
        this.fetchDate = str;
    }


}
