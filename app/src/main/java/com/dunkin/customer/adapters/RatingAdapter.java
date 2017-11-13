package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.listener.RatingListener;
import com.dunkin.customer.models.RatingModel;

import java.util.List;

public class RatingAdapter extends BaseAdapter {

    private Context context;
    private List<RatingModel> ratingModelList;
    private LayoutInflater inflater;
    private RatingListener ratingListener;

    public RatingAdapter(Context context, List<RatingModel> ratingModelList, RatingListener listener) {
        this.context = context;
        this.ratingModelList = ratingModelList;
        this.ratingListener = listener;
    }

    @Override
    public int getCount() {
        return ratingModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return ratingModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_rating_layout, null);
            viewHolder = new ViewHolder();

            viewHolder.tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            viewHolder.ratingLayout = (RelativeLayout) convertView.findViewById(R.id.ratingLayout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*if (position % 2 == 0) {
            viewHolder.ratingLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            viewHolder.ratingLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
        }*/

        viewHolder.ratingLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        viewHolder.tvQuestion.setText(ratingModelList.get(position).getQuestion());

        viewHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                RatingModel element = new RatingModel();
                element.setSelected(true);
                element.setRates((double) rating);
                element.setQuestion(ratingModelList.get(position).getQuestion());
                element.setAverageRating(Double.parseDouble(String.valueOf(ratingModelList.get(position).getAverageRating())));
                Dunkin_Log.e("FromUser", "" + fromUser);
                ratingListener.onRatingChanged(position, element);
            }
        });

        viewHolder.ratingBar.setRating((float) (ratingModelList.get(position).isSelected() ? ratingModelList.get(position).getRates() : 0.0));

        return convertView;
    }

    public List<RatingModel> getUpdatedList() {
        return ratingModelList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvQuestion;
        RatingBar ratingBar;
        RelativeLayout ratingLayout;
    }
}
