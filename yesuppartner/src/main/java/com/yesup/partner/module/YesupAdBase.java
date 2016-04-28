package com.yesup.partner.module;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yesup.partner.tools.DownloadManagerLite;

/**
 * Created by derek on 4/20/16.
 */
public abstract class YesupAdBase extends YesupHttpRequest {

    public static final String TAG = "YesupAdBase";
    public static final int REQ_TYPE_NORMAL_FILE = 0;
    public static final int REQ_TYPE_OFFER_WALL = 1;
    public static final int REQ_TYPE_INTERSTITIAL_PAGE = 2;
    public static final int REQ_TYPE_INTERSTITIAL_IMAGE = 3;
    public static final int REQ_TYPE_IMPRESS = 4;
    public static final int REQ_TYPE_OFFER_ICON = 5;
    public static final int REQ_TYPE_OFFER_JUMPURL = 6;
    public static final int REQ_TYPE_INTERSTITIAL_RES_IMG = 7;

    protected DBHelper dbHelper;
    private Handler msgHandler = null;
    protected Context context;
    protected PartnerAdConfig adConfig;
    protected AdZone adZone = new AdZone();
    protected String subId = "";
    protected String opt1 = "";
    protected String opt2 = "";
    protected String opt3 = "";

    public final void initAdConfig(Context context, PartnerAdConfig adConfig, PartnerAdConfig.Zone zone,
                                   String subId, Handler handler, String opt1, String opt2) {
        this.context = context;
        this.adConfig = adConfig;
        this.subId = subId;
        this.opt1 = opt1;
        this.opt2 = opt2;
        this.opt3 = "";
        this.msgHandler = handler;
        if (zone != null) {
            adZone.setZoneId(zone.id);
            adZone.setFormats(zone.formats);
            adZone.setDisplay(zone.display);
            adZone.setSize(zone.size);

            if (zone.formats.equals("4")) {
                adZone.setAdType(Define.AD_TYPE_OFFER_WALL);
            } else if (zone.display.equals("105") && zone.formats.equals("3")) {
                adZone.setAdType(Define.AD_TYPE_INTERSTITIAL_WEBPAGE);
            } else if (zone.display.equals("105") && zone.formats.equals("2")) {
                adZone.setAdType(Define.AD_TYPE_INTERSTITIAL_IMAGE);
            }
        }
    }

    public final void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public final void setAdType(int adType) {
        if (adZone != null) {
            adZone.setAdType(adType);
        }
    }

    public final int getAdType() {
        if (adZone == null) {
            return -1;
        } else {
            return adZone.getAdType();
        }
    }

    public void sendMsgToHandler(int what, int arg1, int arg2, Object object) {
        if (msgHandler == null){
            return;
        }
        Message msg = msgHandler.obtainMessage(what);
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = object;
        msgHandler.sendMessage(msg);
    }

    public abstract void initAdData();
    public abstract boolean parseResponseDataWithJson();

    public void sendRequest(DownloadManagerLite downloadManager) {
        downloadManager.newDownload(this);
    }

    @Override
    public void onRequestProgressed(int percent) {
        setPercent(percent);
        sendMsgToHandler(Define.MSG_AD_REQUEST_PROGRESSED, percent, 0, this);
    }

    @Override
    public void onRequestCompleted(int result) {
        setStatus(result);
        if (YesupHttpRequest.STATUS_SUCCESSED == result) {
            if (parseResponseDataWithJson()) {
                sendMsgToHandler(Define.MSG_AD_REQUEST_SUCCESSED, getRequestType(), 0, this);
                Log.i(TAG, "AD[" + adZone.getZoneId() + "] TYPE[" + adZone.getAdType() + "] request success.");
            } else {
                sendMsgToHandler(Define.MSG_AD_REQUEST_FAILED, getRequestType(), 0, this);
                Log.e(TAG, "AD["+adZone.getZoneId()+"] TYPE["+adZone.getAdType()+"] parse failed!");
            }
        } else {
            sendMsgToHandler(Define.MSG_AD_REQUEST_FAILED, getRequestType(), result, this);
            Log.e(TAG, "AD["+adZone.getZoneId()+"] TYPE["+adZone.getAdType()+"] request failed["+result+"]!");
        }
    }
}
