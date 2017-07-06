package com.dunkin.customer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.models.NotificationModel;

import java.util.List;

/**
 * Created by Admin on 3/7/2016.
 */
public class NotificationAdapter extends BaseAdapter {

    public List<NotificationModel> notificationList;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        NotificationModel nm = (NotificationModel) getItem(position);
        View view;

        if (convertView == null) {
            final ViewHolder viewHolder = new ViewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.custom_notification_layout, null);

            viewHolder.itemLayout = (RelativeLayout) view.findViewById(R.id.itemLayout);
            viewHolder.txtView = (TextView) view.findViewById(R.id.txtNotification);
            viewHolder.cbCheckNotification = (CheckBox) view.findViewById(R.id.cbCheckNotification);

            view.setTag(viewHolder);
            viewHolder.cbCheckNotification.setTag(notificationList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).cbCheckNotification.setTag(notificationList.get(position));
        }

        final ViewHolder h = (ViewHolder) view.getTag();

        /*if (position % 2 == 0) {
            h.itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            h.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
        }*/
        h.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        if (nm.getIs_read() == 0)
            h.txtView.setTypeface(null, Typeface.BOLD);
        else
            h.txtView.setTypeface(null, Typeface.NORMAL);

        h.txtView.setText(Html.fromHtml(nm.getResponse().getMessage()));

//code for smiley
//        h.txtView.setText(StringEscapeUtils.
//                unescapeJava((nm.getResponse().getMessage())));

        h.cbCheckNotification.setFocusable(false);
        h.cbCheckNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationModel element = (NotificationModel) h.cbCheckNotification.getTag();
                element.setSelected(isChecked);
            }
        });

        h.cbCheckNotification.setChecked(notificationList.get(position).isSelected());

        return view;
    }

    static class ViewHolder {
        TextView txtView;
        CheckBox cbCheckNotification;
        RelativeLayout itemLayout;
    }

    public String getEmojiByUnicode(){
        return new String(Character.toChars(0x1F60A));
    }
}
