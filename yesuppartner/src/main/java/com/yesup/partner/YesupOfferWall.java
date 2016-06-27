package com.yesup.partner;

import android.content.Context;
import android.content.Intent;

import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.offerwall.OfferWallActivity;
import com.yesup.ad.offerwall.OfferWallPartnerHelper;

/**
 * Created by derek on 6/27/16.
 */
public class YesupOfferWall extends YesupAd {

    public YesupOfferWall(Context context) {
        super(context);
    }

    public void setOfferWallPartnerHelper(OfferWallPartnerHelper offerWallPartnerHelper) {
        DataCenter center = DataCenter.getInstance();
        center.setOfferWallPartnerHelper(offerWallPartnerHelper);
    }

    public void showDefaultOfferWall() {
        DataCenter center = DataCenter.getInstance();
        String sZoneId = center.getAdConfig().getDefaultOfferWallZoneId();
        if (!sZoneId.isEmpty()) {
            int zoneId = Integer.parseInt(sZoneId);
            // show offer wall activity
            Intent intent = new Intent(context, OfferWallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ZONE_ID", zoneId);
            context.startActivity(intent);
        }
    }

    public void showOfferWall(int zoneId) {
        int adType = getAdTypeByZoneId(zoneId);
        switch (adType) {
            case Define.AD_TYPE_OFFER_WALL:
                // show offer wall activity
                Intent intent = new Intent(context, OfferWallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ZONE_ID", zoneId);
                context.startActivity(intent);
                break;
            default:
                break;
        }
    }

}
