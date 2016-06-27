package com.yesup.ad.interstitial;

import android.util.Log;

import com.yesup.ad.framework.AdResourceFile;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
import com.yesup.ad.utils.StringTool;
import com.yesup.ad.utils.Uuid;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by derek on 4/20/16.
 */
public class ImageInterstitialRequest extends InterstitialRequest {
    private static final String TAG = "ImageInterstitialAd";

    private ImageInterstitialModel imageInterstitialModel = new ImageInterstitialModel();

    public ImageInterstitialModel.PageAd getPageAd() {
        ImageInterstitialModel.PageAd ad = null;
        if (imageInterstitialModel.adList.size() > 0) {
            ad = imageInterstitialModel.adList.get(0);
        }
        return ad;
    }

    public ImageInterstitialModel getImageInterstitialModel() {
        return imageInterstitialModel;
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_IMAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_INTERSTITIAL_IMAGE);
        setRequestMethod("POST");
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_INTERSTITIAL);
        setSaveFileName(AppTool.getImageInterstitialLocalDataPath(context));
        setRequestHeaderParameter("Authorization", "key=" + adConfig.getKey());
        setRequestHeaderParameter("ACCEPT", "application/json");
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

    @Override
    public boolean parseResponseDataWithJson() {
        boolean success = false;
        // read data from file
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(AppTool.getImageInterstitialLocalDataPath(context));
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        if (jsonData.length() <= 50) {
            Log.w(TAG, "Read resource data: " + jsonData);
        }
        // parse json
        success = imageInterstitialModel.parsePageInterstitialFromJson(jsonData);
        if (success) {
            if (!imageInterstitialModel.result.equals("ready")) {
                success = false;
            }
        }
        // save detail data to local database
        // ...
        // delete temp file for api
        //File file = new File(AppTool.getPageInterstitialLocalDataPath(context));
        //file.delete();
        return success;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void impressInterstitialAfterWait() {
        ImageInterstitialModel.PageAd ad = null;
        if (imageInterstitialModel.adList.size() > 0) {
            ad = imageInterstitialModel.adList.get(0);
        }
        if (ad != null) {
            String impressUrl = ad.impressionUrl;
            int wait = ad.waitSec * 1000;
            if (!impressUrl.isEmpty()) {
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String impressUrl = imageInterstitialModel.adList.get(0).impressionUrl;
                        InterstitialImpressRequest impress = new InterstitialImpressRequest();
                        impress.setImpressUrl(impressUrl);
                        impress.initAdConfig(context, adConfig, null, subId, listener, opt1, opt2, opt3);
                        impress.initRequestData();
                        impress.sendRequest(DataCenter.getInstance().getDownloadManager());
                        Log.e(TAG, "Send request impression.");
                    }
                }, wait);
            }
        }
    }

    public void requestImageFile() {
        String imageUrl = imageInterstitialModel.adList.get(0).adUrl;
        String extension = StringTool.getFilenameExtension(imageUrl);
        String localFilename = AppTool.getImageInterstitialResourceLocalDataPath(context, extension);
        imageInterstitialModel.localImageFilename = localFilename;

        AdResourceFile resFile = new AdResourceFile();
        resFile.setResourceUrl(imageInterstitialModel.adList.get(0).adUrl);
        resFile.setLocalPath(localFilename);
        resFile.initAdConfig(context, adConfig, null, subId, listener, opt1, opt2, opt3);
        resFile.initRequestData();
        resFile.setRequestType(REQ_TYPE_INTERSTITIAL_RES_IMG);
        resFile.sendRequest(DataCenter.getInstance().getDownloadManager());
        Log.d(TAG, "Send request image file: ");
    }

}
