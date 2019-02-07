package com.express.entity;


@lombok.Getter
public class SmsEntity {
    private String smsID;
    private String smsDate;
    private String code;
    private String phone;
    private String position;
    private String fetchDate;
    private String fetchStatus;

    public SmsEntity(String smsID, String smsDate, String code, String phone, String position, String fetchDate, String fetchStatus) {
        this.smsID = smsID;
        this.smsDate = smsDate;
        this.code = code;
        this.phone = phone;
        this.position = position;
        this.fetchDate = fetchDate;
        this.fetchStatus = fetchStatus;
    }

    public boolean compare(SmsEntity newTable) {
        return false;
    }

}
