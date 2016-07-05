package com.yesup.partner;

import android.app.Activity;

import com.yesup.ad.framework.Define;
import com.yesup.ad.interstitial.Interstitial;
import com.yesup.ad.interstitial.PartnerBaseView;
import com.yesup.ad.interstitial.PartnerSampleView;

/**
 * Created by derek on 6/27/16.
 */
public class YesupInterstitial extends YesupAd {
    private Interstitial interstitial = Interstitial.getInstance();

    public YesupInterstitial(Activity parentActivity) {
        super(parentActivity);
    }

    public void showInterstitial(int zoneId, boolean fullScreen,
                                 boolean allowUserCloseAfterImpressed,
                                 PartnerBaseView partnerView) {
        PartnerBaseView partnerBaseView;
        if (partnerView == null) {
            partnerBaseView = new PartnerSampleView(parentActivity);
        } else {
            partnerBaseView = partnerView;
        }
        int adType = getAdTypeByZoneId(zoneId);
        switch (adType) {
            case Define.AD_TYPE_INTERSTITIAL_WEBPAGE:
            case Define.AD_TYPE_INTERSTITIAL_IMAGE:
                // interstitial
                interstitial.setParentActivity(parentActivity);
                interstitial.setPartnerView(partnerBaseView);
                interstitial.show(zoneId, fullScreen, allowUserCloseAfterImpressed);
                break;
            default:
                break;
        }
    }

    public void closeNow() {
        interstitial.closeNow();
    }

    public void safeClose() {
        interstitial.closeAfterCredited();
    }

}
