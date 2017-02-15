package com.dunkin.customer.listener;

import com.dunkin.customer.models.RatingModel;

/**
 * Created by qtm-c-android on 14/9/16.
 */
public interface RatingListener {

    public void onRatingChanged(int position, RatingModel model);
}
