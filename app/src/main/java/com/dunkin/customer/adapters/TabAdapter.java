package com.dunkin.customer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.listener.OnGetTabTextColor;
import com.dunkin.customer.listener.OnTabClick;
import com.dunkin.customer.models.HomeCatModel;

import java.util.List;

/**
 * Created by kinal on 20/2/18.
 */

public class TabAdapter extends BaseAdapter implements OnGetTabTextColor {

    private List<HomeCatModel> homeList;
    private NewHomeActivity newHomeActivity;
    private OnTabClick onTabClick;
    private String tabColor = "#000000", tabHighlightColor = "#000000";
    private LayoutInflater inflater;

    public TabAdapter(List<HomeCatModel> homeList, NewHomeActivity newHomeActivity, OnTabClick onTabClick) {
        this.homeList = homeList;
        this.newHomeActivity = newHomeActivity;
        this.onTabClick = onTabClick;
        inflater = (LayoutInflater) newHomeActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        newHomeActivity.setOnGetTabTextColor(this);
    }

//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(newHomeActivity).inflate(R.layout.tab_view, parent, false);
//        return new ViewHolder(view);
//    }

//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        HomeCatModel homeCatModel = homeList.get(position);
//        holder.tvTitle.setText(homeCatModel.getTitle());
//        if (homeCatModel.isSelect()) {
//            holder.tvTitle.setTextColor(Color.parseColor(tabHighlightColor));
//            if (!TextUtils.isEmpty(homeCatModel.getHighLightImage()))
//                Glide.with(newHomeActivity).load(homeCatModel.getHighLightImage()).into(holder.ivIcon);
//        } else {
//            holder.tvTitle.setTextColor(Color.parseColor(tabColor));
//            if (!TextUtils.isEmpty(homeCatModel.getImage()))
//                Glide.with(newHomeActivity).load(homeCatModel.getImage()).into(holder.ivIcon);
//        }
//
//        holder.llTab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.llTab.startAnimation(animRotate);
//                if (onTabClick != null) {
//                    homeList.get(holder.getAdapterPosition()).setSelect(!homeList.get(holder.getAdapterPosition()).isSelect());
//                    onTabClick.onTabClick(homeList.get(holder.getAdapterPosition()).getId(), homeList.get(holder.getAdapterPosition()).getTitle());
//                    for (int i = 0; i < homeList.size(); i++) {
//                        if (holder.getAdapterPosition() != i) {
//                            homeList.get(i).setSelect(false);
//                        }
//                    }
//                    notifyDataSetChanged();
//                }
//            }
//        });
//    }

//    @Override
//    public int getItemCount() {
//        return homeList.size();
//    }

    @Override
    public void onGetTabTextColor(String tabDefaultColor, String tabSelectedColor) {
        tabColor = tabDefaultColor;
        tabHighlightColor = tabSelectedColor;
    }

    @Override
    public int getCount() {
        return homeList.size();
    }

    @Override
    public Object getItem(int position) {
        return homeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeCatModel homeCatModel = homeList.get(position);
        ViewHolder viewHolder;

        viewHolder = new ViewHolder();

        convertView = inflater.inflate(R.layout.tab_view, parent, false);

        viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
        viewHolder.llTab = (LinearLayout) convertView.findViewById(R.id.llTab);
        convertView.setTag(viewHolder);

        viewHolder.tvTitle.setText(homeCatModel.getTitle());
        if (homeCatModel.isSelect()) {
            viewHolder.tvTitle.setTextColor(Color.parseColor(tabHighlightColor));
            if (!TextUtils.isEmpty(homeCatModel.getHighLightImage()))
                Glide.with(newHomeActivity).load(homeCatModel.getHighLightImage()).
                        into(viewHolder.ivIcon);
        } else {
            viewHolder.tvTitle.setTextColor(Color.parseColor(tabColor));
            if (!TextUtils.isEmpty(homeCatModel.getImage()))
                Glide.with(newHomeActivity).load(homeCatModel.getImage()).into(viewHolder.ivIcon);
        }

        return convertView;
    }

    static class ViewHolder {

        private TextView tvTitle;
        private ImageView ivIcon;
        private LinearLayout llTab;
    }
}

