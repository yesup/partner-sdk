package com.yesup.ad.interstitial;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.yesup.ad.framework.AdConfig;
import com.yesup.ad.framework.AdController;
import com.yesup.ad.framework.AdZone;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.DownloadManagerLite;
import com.yesup.ad.framework.YesupAdRequest;


/**
 * Created by derek on 6/21/16.
 */
public class InterstitialController extends AdController {
    private String TAG = "InterstitialController";
    private InterstitialRequest interstitialRequest;

    public InterstitialRequest getInterstitialRequest() {
        return interstitialRequest;
    }

    @Override
    protected void onInit(Context context, AdConfig config, AdZone zone, String subId,
                          Handler handler, String opt1, String opt2, String opt3) {
        super.onInit(context, config, zone, subId, handler, opt1, opt2, opt3);
        switch (zone.getAdType()) {
            case Define.AD_TYPE_INTERSTITIAL_WEBPAGE:
                interstitialRequest = new PageInterstitialRequest();
                break;
            case Define.AD_TYPE_INTERSTITIAL_IMAGE:
                interstitialRequest = new ImageInterstitialRequest();
                break;
            default:
                break;
        }
        if (null != interstitialRequest) {
            initRequestConfig(interstitialRequest);
        }
        setDataReady(false);
    }

    @Override
    protected void onResume() {
        DownloadManagerLite downloader = DataCenter.getInstance().getDownloadManager();
        if (!interstitialRequest.isReady() && !interstitialRequest.isBusy()) {
            setDataReady(false);
            interstitialRequest.initRequestData();
            interstitialRequest.sendRequest(downloader);
        }
    }

    @Override
    protected void onPause() {
    }

    @Override
    protected void onRequestProgressed(YesupAdRequest adRequest, int percent) {
    }

    @Override
    protected void onRequestSuccess(YesupAdRequest adRequest, int result) {
        switch (adRequest.getRequestType()) {
            case YesupAdRequest.REQ_TYPE_INTERSTITIAL_PAGE:
                messageView(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0, adRequest);
                break;
            case YesupAdRequest.REQ_TYPE_INTERSTITIAL_IMAGE:
                ImageInterstitialRequest req = (ImageInterstitialRequest)adRequest;
                req.requestImageFile();
                break;
            case YesupAdRequest.REQ_TYPE_IMPRESS:
                InterstitialImpressRequest impress = (InterstitialImpressRequest)adRequest;
                if (impress.isCredit()) {
                    messageView(Define.MSG_AD_REQUEST_IMPRESSED, 1, 0, adRequest);
                    Log.i(TAG, "Page interstitial impressed - CREDIT.");
                } else {
                    messageView(Define.MSG_AD_REQUEST_IMPRESSED, 0, 0, adRequest);
                    Log.i(TAG, "Page interstitial impressed - not credit.");
                }
                break;
            case YesupAdRequest.REQ_TYPE_INTERSTITIAL_RES_IMG:
                messageView(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0, adRequest);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRequestFailed(YesupAdRequest adRequest, int result) {
        switch (adRequest.getRequestType()) {
            case YesupAdRequest.REQ_TYPE_INTERSTITIAL_PAGE:
                messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
                break;
            case YesupAdRequest.REQ_TYPE_INTERSTITIAL_IMAGE:
                messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
                break;
            case YesupAdRequest.REQ_TYPE_IMPRESS:
                messageView(Define.MSG_AD_REQUEST_IMPRESSED, -1, 0, adRequest);
                Log.i(TAG, "Page interstitial impressed failed -1.");
                break;
            case YesupAdRequest.REQ_TYPE_INTERSTITIAL_RES_IMG:
                messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
                break;
            default:
                break;
        }
    }
}
