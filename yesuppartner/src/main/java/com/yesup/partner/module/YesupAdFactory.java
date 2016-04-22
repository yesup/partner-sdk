package com.yesup.partner.module;

import android.content.Context;
import android.os.Handler;

/**
 * Created by derek on 4/20/16.
 */
public final class YesupAdFactory {

    public static YesupAdBase createYesupAd(Context context, PartnerAdConfig.Zone zone, PartnerAdConfig adConfig, String subId, Handler handler) {
        YesupAdBase ad = null;

        if (zone.formats.equals("4")) {
            ad = new OfferWallAd();
        } else if (zone.display.equals("105") && zone.formats.equals("3")) {
            ad = new PageInterstitialAd();
        } else if (zone.display.equals("105") && zone.formats.equals("2")) {
            ad = new ImageInterstitialAd();
        }

        if (ad != null) {
            ad.initAdConfig(context, adConfig, zone, subId, handler);
            ad.initAdData();
        }
        return ad;
    }

}
