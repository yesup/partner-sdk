package com.yesup.partner.module;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yesup.partner.tools.AppTool;
import com.yesup.partner.tools.StringTool;
import com.yesup.partner.tools.Uuid;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by derek on 4/20/16.
 */
public class ImageInterstitialAd extends YesupAdBase {
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
    public void initAdData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_IMAGE);

        setRequestType(YesupAdBase.REQ_TYPE_INTERSTITIAL_IMAGE);
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
        setRequestDataParameter("zone", String.valueOf(adZone.getZoneId()));
        setRequestDataParameter("subid", subId);
        setRequestDataParameter("opt1", opt1);
        setRequestDataParameter("opt2", opt2);
        setRequestDataParameter("opt3", opt3);
        setRequestDataParameter("uuid", new Uuid(context).getUUID());
        setRequestDataParameter("adtype", adZone.getSize());
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
                        AdImpress impress = new AdImpress();
                        impress.setImpressUrl(impressUrl);
                        impress.initAdConfig(context, adConfig, null, subId, handler, opt1, opt2);
                        impress.initAdData();
                        impress.sendRequest(DataCenter.getInstance().getDownloadManager());
                        Log.e(TAG, "Send request impression.");
                    }
                }, wait);
            }
        }
    }

    @Override
    public void onRequestCompleted(int result) {
        setStatus(result);
        if (YesupHttpRequest.STATUS_SUCCESSED == result) {
            if (parseResponseDataWithJson()) {
                //sendMsgToHandler(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0, this);
                Log.i(TAG, "AD[" + adZone.getZoneId() + "] TYPE[" + adZone.getAdType() + "] request success.");
                // not complete yet, continue download image
                requestImageFile();
            } else {
                sendMsgToHandler(Define.MSG_AD_REQUEST_FAILED, 0, 0, this);
                Log.e(TAG, "AD["+adZone.getZoneId()+"] TYPE["+adZone.getAdType()+"] parse failed!");
            }
        } else {
            sendMsgToHandler(Define.MSG_AD_REQUEST_FAILED, result, 0, this);
            Log.e(TAG, "AD["+adZone.getZoneId()+"] TYPE["+adZone.getAdType()+"] request failed["+result+"]!");
        }
    }

    private void requestImageFile() {
        String imageUrl = imageInterstitialModel.adList.get(0).adUrl;
        String extension = StringTool.getFilenameExtension(imageUrl);
        String localFilename = AppTool.getImageInterstitialResourceLocalDataPath(context, extension);
        imageInterstitialModel.localImageFilename = localFilename;

        AdResourceFile resFile = new AdResourceFile();
        resFile.setResourceUrl(imageInterstitialModel.adList.get(0).adUrl);
        resFile.setLocalPath(localFilename);
        resFile.initAdConfig(context, adConfig, null, subId, handler, opt1, opt2);
        resFile.initAdData();
        resFile.setRequestType(REQ_TYPE_INTERSTITIAL_RES_IMG);
        resFile.sendRequest(DataCenter.getInstance().getDownloadManager());
        Log.d(TAG, "Send request image file: ");
    }

    private MessageHandler handler = new MessageHandler();
    private void transferMsg(int what, int arg1, int arg2) {
        this.sendMsgToHandler(what, arg1, arg2, this);
    }
    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            YesupAdBase adBase = (YesupAdBase)msg.obj;
            if (YesupAdBase.REQ_TYPE_INTERSTITIAL_RES_IMG == adBase.getRequestType()) {
                // image download completed
                if (Define.MSG_AD_REQUEST_SUCCESSED == msg.what) {
                    transferMsg(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0);
                } else if (Define.MSG_AD_REQUEST_FAILED == msg.what) {
                    transferMsg(Define.MSG_AD_REQUEST_FAILED, msg.arg1, 0);
                }
            } else if (YesupAdBase.REQ_TYPE_IMPRESS == adBase.getRequestType()) {
                // impression competed
                if (Define.MSG_AD_REQUEST_SUCCESSED == msg.what) {
                    AdImpress impress = (AdImpress) msg.obj;
                    if (impress.isCredit()) {
                        transferMsg(Define.MSG_AD_REQUEST_IMPRESSED, 1, 0);
                        Log.i(TAG, "Page interstitial impressed - CREDIT.");
                    } else {
                        transferMsg(Define.MSG_AD_REQUEST_IMPRESSED, 0, 0);
                        Log.i(TAG, "Page interstitial impressed - not credit." + msg.obj.toString());
                    }
                } else if (Define.MSG_AD_REQUEST_FAILED == msg.what) {
                    transferMsg(Define.MSG_AD_REQUEST_IMPRESSED, -1, 0);
                    Log.i(TAG, "Page interstitial impressed failed -1.");
                }
            }
        }
    }

}
