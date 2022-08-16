package com.dunkin.customer.adapters;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private List<CategoryModel> categoryList;

    private LayoutInflater inflater;
    private FrameLayout.LayoutParams params;

    private Context context;
    private int screenWidth, screenHeight;

    public CategoryAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 300);

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            screenWidth = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
        }

        //screenHeight = (int) (display.getHeight() / (3.8 + 0.05));
        screenHeight = (int) (display.getHeight() / 4.00);

        /*DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int deviceWidth = metrics.widthPixels;
        int deviceHeight = metrics.heightPixels;

        float widthInPercentage =  ( (float) 280 / deviceWidth )  * 100; // 280 is the width of my LinearLayout and 320 is device screen width as i know my current device resolution are 320 x 480 so i'm calculating how much space (in percentage my layout is covering so that it should cover same area (in percentage) on any other device having different resolution
        float heightInPercentage =  ( (float) 300 / deviceHeight ) * 100; // same procedure 300 is the height of the LinearLayout and i'm converting it into percentage

        screenWidth = (int) ( (widthInPercentage * deviceWidth) / 100 );
        screenHeight = (int) ( (heightInPercentage * deviceHeight) / 100 );*/

        params = new FrameLayout.LayoutParams(screenWidth, screenHeight);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CategoryModel cm = (CategoryModel) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.custom_gift_screen, parent, false);
            viewHolder.txtCategoryName = (TextView) convertView.findViewById(R.id.txtPoints);
            viewHolder.imgCatImage = (ImageView) convertView.findViewById(R.id.imgLogo);
            //viewHolder.imgCatImage.setLayoutParams(params);
            //viewHolder.imgCatImage.getLayoutParams().width = screenWidth;
            viewHolder.imgCatImage.getLayoutParams().height = screenHeight;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtCategoryName.setText(cm.getCategoryName());
        viewHolder.txtCategoryName.setSelected(true);
        AppUtils.setImage(viewHolder.imgCatImage, cm.getCategoryImage());

        return convertView;
    }

    static class ViewHolder {
        TextView txtCategoryName;
        ImageView imgCatImage;
        LinearLayout productLayout;
    }
}
