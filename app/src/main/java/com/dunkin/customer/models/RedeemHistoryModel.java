package com.dunkin.customer.models;

import android.content.Context;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;

import java.io.Serializable;

/**
 * Created by Admin on 3/8/2016.
 */
public class RedeemHistoryModel implements Serializable {

    private int main_id;
    private String reference;
    private double redeem_point;
    /*JsonFormat(pattern = AppConstants.MM_DD_YYYY_SERVER)
    private Date redeem_date;*/
    private String redeem_date;
    private String source;
    private int type;
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getMain_id() {
        return main_id;
    }

    public void setMain_id(int main_id) {
        this.main_id = main_id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getRedeem_point() {
        return redeem_point;
    }

    public void setRedeem_point(double redeem_point) {
        this.redeem_point = redeem_point;
    }

    public String getRedeem_date() {
        return redeem_date;
    }

    public void setRedeem_date(String redeem_date) {
        this.redeem_date = redeem_date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage(Context context) {
        String tempSource, strMsg = null;

        if (getSource().contains("counter_order"))
            tempSource = getSource().replace("counter_order", "counter order");
        else
            tempSource = getSource();
        //return context.getString(R.string.msg_redeem_gift, "" + AppUtils.CurrencyFormat(getRedeem_point()), getType() == 1 ? context.getString(R.string.lbl_deduct) : context.getString(R.string.lbl_earned), tempSource);

        if (getType() == 0) {
            strMsg = context.getString(R.string.msg_redeem_gift, "" + AppUtils.CurrencyFormat(getRedeem_point()), context.getString(R.string.lbl_earned), tempSource);
        } else if (getType() == 1) {
            strMsg = context.getString(R.string.msg_redeem_gift, "" + AppUtils.CurrencyFormat(getRedeem_point()), context.getString(R.string.lbl_deduct), tempSource);
        } else if (getType() == 2) {
            strMsg = context.getString(R.string.msg_redeem_gift2, "" + AppUtils.CurrencyFormat(getRedeem_point()), tempSource);
        } else if (getType() == 3) {
            strMsg = context.getString(R.string.msg_redeem_gift3, "" + AppUtils.CurrencyFormat(getRedeem_point()), tempSource);
        }

        return strMsg;
    }
}
