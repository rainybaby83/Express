package com.express.entity;


import com.express.Const;

@lombok.Getter
public class SmsEntity {
    private Long smsID;
    private String smsDate;
    private String code;
    private String phone;
    private String position;
    private String fetchDate;
    private String fetchStatus;

    public SmsEntity(Long smsID, String smsDate, String code, String phone, String position, String fetchDate, String fetchStatus) {
        this.smsID = smsID;
        this.smsDate = smsDate;
        this.code = code;
        this.phone = phone;
        this.position = position;
        this.fetchDate = fetchDate;
        this.fetchStatus = fetchStatus;
    }

    public int compareIDandFetch(SmsEntity entity) {
        int value;
        if (this.smsID.equals(entity.smsID)) {
            if (this.fetchStatus.equals(entity.fetchStatus)) {
                value = Const.COMPARE_EQUAL;
            } else {
                value = Const.COMPARE_HALF_EQUAL;
            }
        } else {
            value = Const.COMPARE_NOT_EQUAL;
        }
        return value;
    }

}
