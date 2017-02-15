package com.dunkin.customer.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Handler mHandler;
    int mDay;
    int mMonth;
    int mYear;
    String strMinDate;
    Calendar calMinDate;
    private DatePickerDialog datePickerDialog;

    public DatePickerDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();

        mHandler = AppUtils.getHandler();

        mDay = b.getInt("set_day");
        mMonth = b.getInt("set_month");
        mYear = b.getInt("set_year");
        strMinDate = b.getString("MinDate");

        Log.e("Min Date in Frag", strMinDate);

        DateFormat dateFormat = new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH, Locale.US);

        try {
            Date minDate = dateFormat.parse(strMinDate);

            calMinDate = Calendar.getInstance();
            //Change to Calendar Date
            calMinDate.setTime(minDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        datePickerDialog = new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);

        // Set the Min date into DatePicker Dialog
        datePickerDialog.getDatePicker().setMinDate(calMinDate.getTimeInMillis());

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;

        Bundle b = new Bundle();
        b.putInt("set_day", mDay);
        b.putInt("set_month", (mMonth + 1));
        b.putInt("set_year", mYear);
        String hms = String.format("%02d/%02d/%04d", mDay, (mMonth + 1), mYear);
        b.putString("set_date", "" + hms);

        Message m = new Message();
        m.setData(b);

        mHandler.sendMessage(m);
    }
}
