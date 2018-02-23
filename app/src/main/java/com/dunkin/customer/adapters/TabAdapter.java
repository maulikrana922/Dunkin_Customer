package com.dunkin.customer.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.ViewHolder> implements OnGetTabTextColor {

    private List<HomeCatModel> homeList;
    private NewHomeActivity newHomeActivity;
    private OnTabClick onTabClick;
    private String tabColor = "#000000", tabHighlightColor = "#000000";

    public TabAdapter(List<HomeCatModel> homeList, NewHomeActivity newHomeActivity, OnTabClick onTabClick) {
        this.homeList = homeList;
        this.newHomeActivity = newHomeActivity;
        this.onTabClick = onTabClick;
        newHomeActivity.setOnGetTabTextColor(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(newHomeActivity).inflate(R.layout.tab_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HomeCatModel homeCatModel = homeList.get(position);
        holder.tvTitle.setText(homeCatModel.getTitle());
        if (homeCatModel.isSelect()) {
            holder.tvTitle.setTextColor(Color.parseColor(tabHighlightColor));
            if (!TextUtils.isEmpty(homeCatModel.getHighLightImage()))
                Glide.with(newHomeActivity).load(homeCatModel.getHighLightImage()).into(holder.ivIcon);
        } else {
            holder.tvTitle.setTextColor(Color.parseColor(tabColor));
            if (!TextUtils.isEmpty(homeCatModel.getImage()))
                Glide.with(newHomeActivity).load(homeCatModel.getImage()).into(holder.ivIcon);
        }

        holder.llTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTabClick != null) {
                    homeList.get(holder.getAdapterPosition()).setSelect(!homeList.get(holder.getAdapterPosition()).isSelect());
                    onTabClick.onTabClick(homeList.get(holder.getAdapterPosition()).getId(), homeList.get(holder.getAdapterPosition()).getTitle());
                    for (int i = 0; i < homeList.size(); i++) {
                        if (holder.getAdapterPosition() != i) {
                            homeList.get(i).setSelect(false);
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    @Override
    public void onGetTabTextColor(String tabDefaultColor, String tabSelectedColor) {
        tabColor = tabDefaultColor;
        tabHighlightColor = tabSelectedColor;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivIcon;
        private LinearLayout llTab;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            llTab = (LinearLayout) itemView.findViewById(R.id.llTab);
        }
    }
}

