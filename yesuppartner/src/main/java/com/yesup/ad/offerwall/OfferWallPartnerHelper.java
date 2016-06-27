package com.yesup.ad.offerwall;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;

/**
 * Created by derek on 4/27/16.
 */
public class OfferWallPartnerHelper {
    protected Context context;

    public OfferWallPartnerHelper(Context context) {
        this.context = context;
    }

    public String calculateReward(int payout, int incentRate) {
        String result = "0";
        double reward = (double)payout * (double)incentRate / 100000.0D;
        if (0.0D == reward) {
            result = "";
        } else {
            result = new DecimalFormat("#.##").format(reward);
        }
        return result;
    }

    public Drawable getRewardIcon() {
        return null;
    }

}
