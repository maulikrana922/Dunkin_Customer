package com.dunkin.customer.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RedeemPoint implements Parcelable {
    public String totalPoint ;
    public String totalUsedPoint ;
    public String remainRedeemPoint ;

    protected RedeemPoint(Parcel in) {
        totalPoint = in.readString();
        totalUsedPoint = in.readString();
        remainRedeemPoint = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalPoint);
        dest.writeString(totalUsedPoint);
        dest.writeString(remainRedeemPoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RedeemPoint> CREATOR = new Creator<RedeemPoint>() {
        @Override
        public RedeemPoint createFromParcel(Parcel in) {
            return new RedeemPoint(in);
        }

        @Override
        public RedeemPoint[] newArray(int size) {
            return new RedeemPoint[size];
        }
    };
}
