package com.dunkin.customer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.fragments.GiftFragment;
import com.dunkin.customer.listener.OnGiftClick;
import com.dunkin.customer.models.GiftModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class GiftAdapter extends BaseAdapter {
    private List<GiftModel> giftModelList;
    private LayoutInflater inflater;
    private String userPoints;
    private Context context;
    private GiftFragment giftFragment;
    private int screenWidth, screenHeight;
    private OnGiftClick onGiftClick;

    public GiftAdapter(Context context, List<GiftModel> giftModelList, String userPoints, GiftFragment giftFragment, OnGiftClick onGiftClick) {
        this.giftModelList = giftModelList;
        this.giftFragment = giftFragment;
        this.userPoints = userPoints;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onGiftClick = onGiftClick;

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = (int) (display.getHeight() / 4.00);
    }

    @Override
    public int getCount() {
        return giftModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return giftModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final GiftModel gift = (GiftModel) getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.custom_gift_screen, null);

            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.imgLogo.getLayoutParams().height = screenHeight;
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtPoints);
            viewHolder.frameLayout = (FrameLayout) convertView.findViewById(R.id.frameLayout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            AppUtils.setImage(viewHolder.imgLogo, gift.getGiftImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.txtDesc.setText(context.getString(R.string.txt_redeem_for_point, AppUtils.CurrencyFormat(Double.parseDouble(gift.getPoints()))));

        viewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(context.getString(R.string.al_warning_redeem));

                Spanned strGiftMessage;

                if (gift.getGiftType() == 1) {
                    strGiftMessage = Html.fromHtml("<b>" + context.getString(R.string.lbl_redeem_popup_title2) + "</b> " + gift.getTitle() + "<br>" +
                            "<b>" + context.getString(R.string.lbl_redeem_popup_point2) + "</b> " + AppUtils.CurrencyFormat(Double.parseDouble(gift.getPoints())) + "<br>" +
                            "<b>" + context.getString(R.string.lbl_redeem_popup_desc) + "</b> " + gift.getGiftDesc() + "<br><br>" +
                            context.getString(R.string.msg_confirm_redeem));

                    alert.setMessage(strGiftMessage);
                } else {
                    strGiftMessage = Html.fromHtml("<b>" + context.getString(R.string.lbl_redeem_popup_title2) + "</b> " + gift.getTitle() + "<br>" +
                            "<b>" + context.getString(R.string.lbl_redeem_popup_point2) + "</b> " + AppUtils.CurrencyFormat(Double.parseDouble(gift.getPoints())) + "<br>" +
                            "<b>" + context.getString(R.string.lbl_redeem_popup_desc) + "</b> " + gift.getGiftDesc() + "<br><br>" +
                            context.getString(R.string.msg_confirm_redeem_wait_for_me2));

                    alert.setMessage(strGiftMessage);
                }
                alert.setPositiveButton(context.getString(R.string.al_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(onGiftClick != null){
                            onGiftClick.onGiftConfirm(gift);
                        }
                    }
                });
                alert.setNegativeButton(context.getString(R.string.al_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView imgLogo;
        TextView txtDesc;
        FrameLayout frameLayout;
    }
}

