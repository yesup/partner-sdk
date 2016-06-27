package com.yesup.ad.framework;

import android.content.Context;
import android.os.Handler;

import com.yesup.ad.banner.BannerController;
import com.yesup.ad.interstitial.InterstitialController;
import com.yesup.ad.offerwall.OfferwallController;

/**
 * Created by derek on 4/20/16.
 */
public final class YesupAdFactory {

    public static AdController createYesupAd(Context context, AdConfig adConfig, AdZone zone, String subId,
                                             Handler handler, String opt1, String opt2, String opt3) {
        AdController controller = null;

        switch (zone.getAdType()) {
            case Define.AD_TYPE_OFFER_WALL:
                controller = new OfferwallController();
                break;
            case Define.AD_TYPE_INTERSTITIAL_WEBPAGE:
                controller = new InterstitialController();
                break;
            case Define.AD_TYPE_INTERSTITIAL_IMAGE:
                controller = new InterstitialController();
                break;
            case Define.AD_TYPE_BANNER_IMAGE:
                controller = new BannerController();
                break;
            default:
                throw new RuntimeException("[YESUP SDK]Not support AD type["+zone.formats+","+zone.display+"]");
        }

        if (controller != null) {
            controller.onInit(context, adConfig, zone, subId, handler, opt1, opt2, opt3);
        }
        return controller;
    }

}
