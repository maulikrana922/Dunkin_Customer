package com.dunkin.customer.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class ScratchCardAdapter extends BaseAdapter {

    int limit = 3;
    private Context context;
    private List<String> digitList;
    private List<String> selectedNumbers;
    private LayoutInflater inflater;

    public ScratchCardAdapter(Context context, List<String> digitList) {
        this.digitList = digitList;
        this.context = context;
        selectedNumbers = new ArrayList<>();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return digitList.size();
    }

    @Override
    public Object getItem(int position) {
        return digitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_scratch_card_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.txtView = (TextView) convertView.findViewById(R.id.txtView);
            viewHolder.imbView = (ImageButton) convertView.findViewById(R.id.imbView);
            viewHolder.isClicked = false;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtView.setText(digitList.get(position));

        viewHolder.imbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.isClicked && limit > 0) {

                    selectedNumbers.add(viewHolder.txtView.getText().toString());

                    Interpolator accelerator = new AccelerateInterpolator();
                    Interpolator decelerator = new DecelerateInterpolator();

                    ObjectAnimator visToInvis = ObjectAnimator.ofFloat(viewHolder.imbView, "rotationY", 0f, 90f);
                    visToInvis.setDuration(500);
                    visToInvis.setInterpolator(accelerator);
                    final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(viewHolder.txtView, "rotationY",
                            -90f, 0f);
                    invisToVis.setDuration(500);
                    invisToVis.setInterpolator(decelerator);
                    visToInvis.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator anim) {
                            viewHolder.imbView.setVisibility(View.GONE);
                            invisToVis.start();
                            viewHolder.txtView.setVisibility(View.VISIBLE);
                        }
                    });
                    visToInvis.start();
                    limit = limit - 1;
                    viewHolder.isClicked = true;

                    if (limit == 0) {

                        boolean matched = false;

                        final String prevNum = selectedNumbers.get(0);
                        for (int i = 1; i < selectedNumbers.size(); i++) {
                            if (prevNum.equals(selectedNumbers.get(i))) {
                                matched = true;
                            } else {
                                matched = false;
                                break;
                            }
                        }

                        if (matched) {
                            // Now make webservice call to enter the redeem points.
                            try {
                                AppController.addRedeemPoints(context, prevNum, new Callback() {
                                    @Override
                                    public void run(Object result) throws JSONException, IOException {
                                        AppUtils.showToastMessage(context, context.getString(R.string.txt_win_game, prevNum));
                                    }
                                });
                            } catch (JSONException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AppUtils.showToastMessage(context, context.getString(R.string.txt_lost_game));
                        }
                    }

                    if (limit == 2) {
                        // Now make webservice call to enter the redeem points.
                        try {
                            AppController.getScratFirstchCard(context, new Callback() {
                                @Override
                                public void run(Object result) throws JSONException, IOException {
                                }
                            });
                        } catch (JSONException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageButton imbView;
        TextView txtView;
        boolean isClicked = false;
    }
}
