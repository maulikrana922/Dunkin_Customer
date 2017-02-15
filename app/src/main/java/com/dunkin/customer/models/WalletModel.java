package com.dunkin.customer.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android2 on 7/10/2015.
 */
public class WalletModel implements Serializable {

    private String currency;
    private String total;
    private List<WalletNoteModel> notes;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<WalletNoteModel> getNotes() {
        return notes;
    }

    public void setNotes(List<WalletNoteModel> notes) {
        this.notes = notes;
    }
}
