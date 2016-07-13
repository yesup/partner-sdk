package com.yesup.ad.interstitial;

import android.util.Log;

import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
import com.yesup.ad.utils.StringTool;
import com.yesup.ad.utils.Uuid;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by derek on 4/20/16.
 */
public class PageInterstitialRequest extends InterstitialRequest {
    private static final String TAG = "PageInterstitialAd";

    private PageInterstitialModel pageInterstitialModel = new PageInterstitialModel();

    public PageInterstitialModel.PageAd getPageAd() {
        PageInterstitialModel.PageAd ad = null;
        if (pageInterstitialModel.adList.size() > 0) {
            ad = pageInterstitialModel.adList.get(0);
        }
        return ad;
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_WEBPAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_INTERSTITIAL_PAGE);
        setRequestMethod("POST");
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_INTERSTITIAL);
        setSaveFileName(AppTool.getPageInterstitialLocalDataPath(context));
        setRequestHeaderParameter("Authorization", "key=" + adConfig.getKey());
        setRequestHeaderParameter("Accept", "application/json");
        setRequestHeaderParameter("User-Agent", AppTool.makeUserAgent());
        setRequestHeaderParameter("Content-Type", "application/x-www-form-urlencoded");
        setRequestDataParameter("nid", adConfig.getNid());
        setRequestDataParameter("pid", adConfig.getPid());
        setRequestDataParameter("sid", adConfig.getSid());
        setRequestDataParameter("zone", String.valueOf(adZone.id));
        setRequestDataParameter("subid", subId);
        setRequestDataParameter("opt1", opt1);
        setRequestDataParameter("opt2", opt2);
        setRequestDataParameter("opt3", opt3);
        setRequestDataParameter("uuid", new Uuid(context).getUUID());
        setRequestDataParameter("adtype", adZone.size);
    }

    public boolean parseResponseDataWithJson() {
        boolean success = false;
        // read data from file
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(AppTool.getPageInterstitialLocalDataPath(context));
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        if (jsonData.length() <= 50) {
            Log.w(TAG, "Read resource data: " + jsonData);
        }
        // parse json
        success = pageInterstitialModel.parsePageInterstitialFromJson(jsonData);
        if (success) {
            if (!pageInterstitialModel.result.equals("ready")) {
                success = false;
            }
        }
        // save detail data to local database
        // ...
        // delete temp file for api
        File file = new File(AppTool.getPageInterstitialLocalDataPath(context));
        file.delete();
        return success;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void impressInterstitialAfterWait() {
        PageInterstitialModel.PageAd ad = null;
        if (pageInterstitialModel.adList.size() > 0) {
            ad = pageInterstitialModel.adList.get(0);
        }
        if (ad != null) {
            String impressUrl = ad.impressionUrl;
            int wait = ad.waitSec * 1000;
            if (!impressUrl.isEmpty()) {
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String impressUrl = pageInterstitialModel.adList.get(0).impressionUrl;
                        InterstitialImpressRequest impress = new InterstitialImpressRequest();
                        impress.setImpressUrl(impressUrl);
                        impress.initAdConfig(context, adConfig, null, subId, listener, opt1, opt2, opt3);
                        impress.initRequestData();
                        impress.sendRequest(DataCenter.getInstance().getDownloadManager());
                        Log.d(TAG, "Send request impression.");
                    }
                }, wait);
            }
        }
    }

}
