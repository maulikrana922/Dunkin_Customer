package com.dunkin.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class WalletRedeemPoint implements Parcelable{
    public String success;
    public String userid;
    public RedeemPoint redeemPoint;
    public Wallet wallet;

    protected WalletRedeemPoint(Parcel in) {
        success = in.readString();
        userid = in.readString();
        redeemPoint = in.readParcelable(RedeemPoint.class.getClassLoader());
        wallet = in.readParcelable(Wallet.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(success);
        dest.writeString(userid);
        dest.writeParcelable(redeemPoint, flags);
        dest.writeParcelable(wallet, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WalletRedeemPoint> CREATOR = new Creator<WalletRedeemPoint>() {
        @Override
        public WalletRedeemPoint createFromParcel(Parcel in) {
            return new WalletRedeemPoint(in);
        }

        @Override
        public WalletRedeemPoint[] newArray(int size) {
            return new WalletRedeemPoint[size];
        }
    };
}
