package com.dunkin.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Wallet implements Parcelable {
    public String currency;
    public String total;
    public String usedWalletPoint;
    public String totalWalletPoint;

    protected Wallet(Parcel in) {
        currency = in.readString();
        total = in.readString();
        usedWalletPoint = in.readString();
        totalWalletPoint = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currency);
        dest.writeString(total);
        dest.writeString(usedWalletPoint);
        dest.writeString(totalWalletPoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
        }
    };
}
