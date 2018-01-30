package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.listener.StaffRatingListener;
import com.dunkin.customer.models.CatalogQuestionModel;
import com.dunkin.customer.models.RatingModel;

import java.util.List;

public class StaffRatingAdapter extends BaseAdapter {

    private Context context;
    private List<CatalogQuestionModel> catalogQuestionModelList;
    private LayoutInflater inflater;
    private StaffRatingListener staffRatingListener;

    public StaffRatingAdapter(Context context, List<CatalogQuestionModel> catalogQuestionModelList, StaffRatingListener listener) {
        this.context = context;
        this.catalogQuestionModelList = catalogQuestionModelList;
        this.staffRatingListener = listener;
    }

    @Override
    public int getCount() {
        return catalogQuestionModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return catalogQuestionModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_staff_rating_layout, null);
            viewHolder = new ViewHolder();

            viewHolder.tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);
            viewHolder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);
            viewHolder.seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);
            viewHolder.ratingLayout = (LinearLayout) convertView.findViewById(R.id.ratingLayout);

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
        viewHolder.tvCount.setText(String.valueOf(catalogQuestionModelList.get(position).getRating()+"/"+20));
        viewHolder.tvQuestion.setText(catalogQuestionModelList.get(position).getQueTitle());
        viewHolder.seekBar.setOnSeekBarChangeListener(null);
        viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewHolder.seekBar.setSelected(i > 0);
                staffRatingListener.onRatingChanged(position, i, b);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return convertView;
    }

    public List<CatalogQuestionModel> getUpdatedList() {
        return catalogQuestionModelList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvQuestion, tvCount;
        SeekBar seekBar;
        LinearLayout ratingLayout;
    }
}
