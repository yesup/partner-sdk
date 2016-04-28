package com.yesup.partner.module;


/**
 * Created by derek on 3/2/16.
 */
public class Define {
    public static final String SDK_VERSION = "1.1.0";

    public static final int MSG_AD_REQUEST_PROGRESSED = 2001;
    public static final int MSG_AD_REQUEST_SUCCESSED = 2002;
    public static final int MSG_AD_REQUEST_FAILED = 2003;
    public static final int MSG_AD_REQUEST_IMPRESSED = 2004;

    public static final long VALUE_EXPIRE_TIME = 60; // 60 minites

    public static final String LOCAL_IMAGE_DIR = "/appicon";

    public static String SERVER_HOST = "http://ads.jsmtv.com"; // ads.jsmtv.com - 199.21.148.25
    public static final String SERVER_HOST_DEBUG = "http://ads.jsmtv.com"; //
    public static final String URL_IF_OFFER_WALL = "/cpxcenter/offerWall.php";
    public static final String URL_IF_INCENTIVE_API = "/cpxcenter/incentiveApi.php";
    public static final String URL_IF_AD_REPORT = "/cpxcenter/track.php";
    public static final String URL_IF_INTERSTITIAL = "/cpxcenter/interstitialApi.php";

    // AD type
    public static final int AD_TYPE_OFFER_WALL = 0;
    public static final int AD_TYPE_INTERSTITIAL_WEBPAGE = 1;
    public static final int AD_TYPE_INTERSTITIAL_IMAGE = 2;

    public static final int IMPRESS_AFTER_SHOW_WAIT = 5;

}
