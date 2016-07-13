package com.yesup.ad.framework;

import android.content.Context;


/**
 * Created by derek on 4/20/16.
 */
public abstract class YesupAdRequest extends YesupHttpRequest {

    public static final String TAG = "YesupAdRequest";
    public static final int REQ_TYPE_NORMAL_FILE = 0;
    public static final int REQ_TYPE_OFFER_WALL = 1;
    public static final int REQ_TYPE_INTERSTITIAL_PAGE = 2;
    public static final int REQ_TYPE_INTERSTITIAL_IMAGE = 3;
    public static final int REQ_TYPE_IMPRESS = 4;
    public static final int REQ_TYPE_OFFER_ICON = 5;
    public static final int REQ_TYPE_OFFER_JUMPURL = 6;
    public static final int REQ_TYPE_INTERSTITIAL_RES_IMG = 7;
    public static final int REQ_TYPE_BANNER_LIST = 8;
    public static final int REQ_TYPE_BANNER_CLICK_URL = 9;
    public static final int REQ_TYPE_IMAGE_CLICK_URL = 10;

    protected DBHelper dbHelper;
    protected Context context;
    protected AdConfig adConfig;
    protected AdZone adZone = new AdZone();
    protected String subId = "";
    protected String opt1 = "";
    protected String opt2 = "";
    protected String opt3 = "";
    protected int adType = -1;

    public final void initAdConfig(Context context, AdConfig adConfig, AdZone zone, String subId,
                                   Listener listener, String opt1, String opt2, String opt3) {
        this.context = context;
        this.adConfig = adConfig;
        this.subId = subId;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = opt3;
        this.listener = listener;
        this.dbHelper = DataCenter.getInstance().getDbHelper();
        if (zone != null) {
            adZone.id = zone.id;
            adZone.formats = zone.formats;
            adZone.display = zone.display;
            adZone.size = zone.size;

            if (zone.formats.equals("4")) {
                adType = Define.AD_TYPE_OFFER_WALL;
            } else if (zone.display.equals("105") && zone.formats.equals("3")) {
                adType = Define.AD_TYPE_INTERSTITIAL_WEBPAGE;
            } else if (zone.display.equals("105") && zone.formats.equals("2")) {
                adType = Define.AD_TYPE_INTERSTITIAL_IMAGE;
            } else {
                adType = Define.AD_TYPE_BANNER_IMAGE;
            }
        }
    }

    public final void setAdType(int adType) {
        this.adType = adType;
    }

    public final void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public final DBHelper getDbHelper() { return this.dbHelper; }

    public final int getAdType() {
        return adType;
    }

    public final int getAdZoneId() {
        if (adZone == null) {
            return 0;
        } else {
            return adZone.id;
        }
    }

    public final AdZone getAdZone() {
        return adZone;
    }

    public abstract void initRequestData();
    public abstract boolean parseResponseDataWithJson();
    public abstract boolean isReady();

    private boolean isRequesting = false;
    public boolean isBusy() {
        return isRequesting;
    }

    public void sendRequest(DownloadManagerLite downloadManager) {
        int ret = downloadManager.newDownload(this);
        if (ret < 0) {
            isRequesting = false;
        } else {
            isRequesting = true;
        }
    }

    @Override
    public void onRequestCompleted(int result) {
        int realResult = result;
        if (YesupHttpRequest.STATUS_SUCCESSED == result) {
            if (!parseResponseDataWithJson()) {
                realResult = YesupHttpRequest.STATUS_DATA_ERROR;
            }
        }
        isRequesting = false;
        super.onRequestCompleted(realResult);
    }
}
