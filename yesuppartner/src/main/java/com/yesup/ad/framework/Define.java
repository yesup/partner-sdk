package com.yesup.ad.framework;


/**
 * Created by derek on 3/2/16.
 */
public class Define {
    public static final String SDK_VERSION = "1.2.6";

    /**
     * Message Parameters Define
     * what: message value below
     * arg1: request type
     * arg2: option
     * obj:  object who requested
     */
    public static final int MSG_AD_REQUEST_PROGRESSED = 2001;
    public static final int MSG_AD_REQUEST_SUCCESSED = 2002;
    public static final int MSG_AD_REQUEST_FAILED = 2003;
    public static final int MSG_AD_REQUEST_IMPRESSED = 2004;
    public static final int MSG_BANNERVIEW_SLIDE_NEXT = 3001;

    public static final long VALUE_EXPIRE_TIME = 60; // 60 minites

    public static final String LOCAL_IMAGE_DIR = "/appicon";

    public static String SERVER_HOST = "http://ads.jsmtv.com"; // ads.jsmtv.com - 199.21.148.25
    public static final String SERVER_HOST_DEBUG = "http://ads.jsmtv.com"; //
    public static final String URL_IF_OFFER_WALL = "/cpxcenter/offerWall.php";
    public static final String URL_IF_INCENTIVE_API = "/cpxcenter/incentiveApi.php";
    public static final String URL_IF_AD_REPORT = "/cpxcenter/track.php";
    public static final String URL_IF_INTERSTITIAL = "/cpxcenter/interstitialApi.php";
    public static final String URL_IF_BANNER_LIST = "/cpxcenter/appBannerApi.php";

    // AD type
    public static final int AD_TYPE_UNKNOWN = -1;
    public static final int AD_TYPE_OFFER_WALL = 0;
    public static final int AD_TYPE_INTERSTITIAL_WEBPAGE = 1;
    public static final int AD_TYPE_INTERSTITIAL_IMAGE = 2;
    public static final int AD_TYPE_BANNER_IMAGE = 3;

    public static final int IMPRESS_AFTER_SHOW_WAIT = 5;

}
