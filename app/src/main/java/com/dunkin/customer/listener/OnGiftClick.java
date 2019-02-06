package com.dunkin.customer.listener;

import com.dunkin.customer.models.GiftModel;

/**
 * Created by kinal on 22/2/18.
 */

public interface OnGiftClick {

    void onGiftConfirm(GiftModel gift,int position);
}
