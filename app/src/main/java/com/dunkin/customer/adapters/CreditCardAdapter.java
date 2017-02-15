package com.dunkin.customer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dunkin.customer.EditCardActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.fragments.MyCreditCardFragment;
import com.dunkin.customer.models.CreditCardModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Admin on 10/21/2015.
 */
public class CreditCardAdapter extends BaseAdapter {

    private Context context;
    private List<CreditCardModel> creditCardList;
    private MyCreditCardFragment fragment;
    private SimpleDateFormat dateFormaatter;

    public CreditCardAdapter(Context context, List<CreditCardModel> creditCardList, MyCreditCardFragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.creditCardList = creditCardList;
        dateFormaatter = new SimpleDateFormat(AppConstants.YYYY_MM);
    }

    @Override
    public int getCount() {
        return creditCardList.size();
    }

    @Override
    public Object getItem(int position) {
        return creditCardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.custom_credit_card_layout, null);
            viewHolder.txtCardHolderName = (TextView) convertView.findViewById(R.id.txtCardHolderName);
            viewHolder.txtExpiryDate = (TextView) convertView.findViewById(R.id.txtExpiryDate);
            viewHolder.txtCardNumber = (TextView) convertView.findViewById(R.id.txtCardNumber);
            viewHolder.btnDelete = (RelativeLayoutButton) convertView.findViewById(R.id.btnDelete);
            viewHolder.btnEdit = (RelativeLayoutButton) convertView.findViewById(R.id.btnEdit);

            viewHolder.btnDelete.btnText.setText(context.getString(R.string.lbl_delete_card));
            viewHolder.btnDelete.imgIcon.setImageResource(R.drawable.ic_delete_card);
            viewHolder.btnEdit.btnText.setText(context.getString(R.string.lbl_edit_card));

            viewHolder.btnEdit.imgIcon.setImageResource(R.drawable.ic_edit_card);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtCardHolderName.setText(creditCardList.get(position).getCardHolderName());

        try {
            viewHolder.txtExpiryDate.setText(new SimpleDateFormat(AppConstants.MM_YY).format(dateFormaatter.parse(creditCardList.get(position).getExpiry())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.txtCardNumber.setText(AppUtils.maskCardNumber(creditCardList.get(position).getCardNumber(), "xxxx-xxxx-xxxx" + "-" + creditCardList.get(position).getCardNumber().substring(creditCardList.get(position).getCardNumber().length() - 4, creditCardList.get(position).getCardNumber().length())));

        viewHolder.btnEdit.setOnClickListener(new clicklistener(viewHolder, position));
        viewHolder.btnDelete.setOnClickListener(new clicklistener(viewHolder, position));

        return convertView;
    }

    class clicklistener implements View.OnClickListener {

        private ViewHolder viewHolder;
        private int position;

        public clicklistener(ViewHolder viewHolder, int position) {
            this.position = position;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            if (v == viewHolder.btnEdit) {
                CreditCardModel creditCardModel = creditCardList.get(position);

                Intent i = new Intent(context, EditCardActivity.class);
                i.putExtra("creditcard", creditCardModel);
                fragment.startActivityForResult(i, 110);

            }

            if (v == viewHolder.btnDelete) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getString(R.string.al_warning));
                dialog.setMessage(context.getString(R.string.txt_al_remove_message));
                dialog.setPositiveButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        CreditCardModel creditCardModel = creditCardList.get(position);

                        AppController.removeCard(context, creditCardModel.getId(), new Callback() {
                            @Override
                            public void run(Object result) throws JSONException, IOException {
                                JSONObject jsonResponse = new JSONObject((String) result);
                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));

                                if (jsonResponse.getInt("success") == 1) {
                                    creditCardList.remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                                else if (jsonResponse.getInt("success") == 100) {
                                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton(context.getString(R.string.al_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.create().show();
            }
        }
    }

    class ViewHolder {
        TextView txtCardHolderName, txtExpiryDate, txtCardNumber;
        RelativeLayoutButton btnDelete, btnEdit;
    }


}
