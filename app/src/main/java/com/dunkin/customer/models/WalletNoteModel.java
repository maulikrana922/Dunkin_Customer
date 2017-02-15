package com.dunkin.customer.models;

import java.io.Serializable;

/**
 * Created by Admin on 9/9/2015.
 */
public class WalletNoteModel implements Serializable {

    private String noteId;
    private String serialNumber;
    private String noteAmount;
    private String created_datetime;
    private int earnType;
    //@JsonFormat(pattern = AppConstants.YYYY_MM_DD_HH_MM_SS)
    private String rechargeDate;
    private String rechargeBy;

    public int getEarnType() {
        return earnType;
    }

    public void setEarnType(int earnType) {
        this.earnType = earnType;
    }

    public String getRechargeDate() {
        return rechargeDate;
    }

    public void setRechargeDate(String rechargeDate) {
        this.rechargeDate = rechargeDate;
    }

    public String getRechargeBy() {
        return rechargeBy;
    }

    public void setRechargeBy(String rechargeBy) {
        this.rechargeBy = rechargeBy;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNoteAmount() {
        return noteAmount;
    }

    public void setNoteAmount(String noteAmount) {
        this.noteAmount = noteAmount;
    }

    public String getCreated_datetime() {
        return created_datetime;
    }

    public void setCreated_datetime(String created_datetime) {
        this.created_datetime = created_datetime;
    }
}
