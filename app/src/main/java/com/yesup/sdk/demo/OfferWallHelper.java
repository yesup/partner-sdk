package com.yesup.sdk.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.yesup.partner.activities.OfferWallPartnerHelper;

import java.text.DecimalFormat;

/**
 * Created by derek on 4/28/16.
 */
public class OfferWallHelper extends OfferWallPartnerHelper {

    public OfferWallHelper(Context context) {
        super(context);
    }

    @Override
    public String calculateReward(int payout, int incentRate) {
        String result = "0";
        double reward = (double)payout * (double)incentRate / 100000.0D;
        if(0.0D == reward) {
            result = "";
        } else {
            result = (new DecimalFormat("#.##")).format(reward);
        }

        return result;
    }

    @Override
    public Drawable getRewardIcon() {
        Drawable drawable = context.getResources().getDrawable(R.drawable.coins);
        return drawable;
    }

}
