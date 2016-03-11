package com.yesup.partner.module;


/**
 * Created by derek on 3/2/16.
 */
public class Define {
    public static final String SDK_VERSION = "1.0";

    public static final int MSG_DOWNLOAD_STATUS_CHANGED = 100;
    public static final int MSG_DOWNLOAD_FILE_COMPLETED = 200;
    public static final int MSG_DOWNLOAD_OFFERWALL_COMPLETED = 201;
    public static final int MSG_DOWNLOAD_OFFERDETAIL_COMPLETED = 202;

    public static final long VALUE_EXPIRE_TIME = 60; // 60 minites

    public static final int DOWNLOAD_TYPE_FILE_NORMAL = 0;
    public static final int DOWNLOAD_TYPE_IF_OFFERWALL = 1;
    public static final int DOWNLOAD_TYPE_IF_INCENTIVEAPI = 2;

    public static final String LOCAL_IMAGE_DIR = "/appicon";

    public static String SERVER_HOST = "http://ads.jsmtv.com"; // ads.jsmtv.com - 199.21.148.25
    public static final String SERVER_HOST_DEBUG = "http://ads.jsmtv.com"; //
    public static final String URL_IF_OFFER_WALL = "/cpxcenter/offerWall.php";
    public static final String URL_IF_INCENTIVE_API = "/cpxcenter/incentiveApi.php";
    public static final String URL_IF_AD_REPORT = "/cpxcenter/track.php";

}
