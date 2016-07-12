package com.yesup.ad.framework;

/**
 * Created by derek on 4/20/16.
 */
public final class AdZone {
    public int id;
    public String formats;
    public String display;
    public String size;

    public int getAdType() {
        if (formats.equals("4")) {
            return Define.AD_TYPE_OFFER_WALL;
        } else if (display.equals("105") && formats.equals("3")) {
            return Define.AD_TYPE_INTERSTITIAL_WEBPAGE;
        } else if (display.equals("105") && formats.equals("2")) {
            return Define.AD_TYPE_INTERSTITIAL_IMAGE;
        } else if (formats.equals("1")) {
            return Define.AD_TYPE_BANNER_IMAGE;
        } else if (display.equals("2") && formats.equals("2")) {
            return Define.AD_TYPE_BANNER_IMAGE;
        } else {
            return Define.AD_TYPE_UNKNOWN;
        }
    }
}
