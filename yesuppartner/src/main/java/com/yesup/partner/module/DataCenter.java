package com.yesup.partner.module;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.yesup.partner.activities.OfferWallPartnerHelper;
import com.yesup.partner.tools.AppTool;
import com.yesup.partner.tools.DownloadManagerLite;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by derek on 2/24/16.
 */
public class DataCenter {
    private String TAG = "DataCenter";
    private DataCenter() {}
    public static DataCenter getInstance() {
        return dataCenter;
    }

    private MessageTransfer msgTransfer = new MessageTransfer();
    private static boolean isDebugMode = false;
    private static PartnerAdConfig config;
    private static Context context;
    private static Handler msgHandler;
    private static DownloadManagerLite downloadManager = new DownloadManagerLite(2);
    private static DBHelper dbHelper;
    private static DataCenter dataCenter = new DataCenter();
    private static String subId;
    private static String option1 = "";
    private static String option2 = "";

    private OfferWallAd offerWallAd;
    private YesupAdBase interstitialAd;

    private OfferWallPartnerHelper offerWallPartnerHelper = null;

    public void init(Context context) {
        if (this.context != null){
            return;
        }
        this.context = context;
        downloadManager.start();
        dbHelper = new DBHelper(context);
        config = new PartnerAdConfig(context);
        if (isDebugMode) {
            Define.SERVER_HOST = Define.SERVER_HOST_DEBUG;
        }else{
            Define.SERVER_HOST = config.getServing();
        }
        if (subId == null || subId.length() <= 0){
            subId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
        if (isDebugMode) {
            Define.SERVER_HOST = Define.SERVER_HOST_DEBUG;
        }else{
            Define.SERVER_HOST = config.getServing();
        }
    }

    public void setSubId(String id) {
        if (id != null && id.length() > 0) {
            subId = id;
        }
    }

    public void setOption(String opt1, String opt2) {
        if (opt1 == null || opt1.isEmpty()) {
            option1 = "";
        } else if (opt1.length() > 80) {
            option1 = opt1.substring(0, 80);
        } else {
            option1 = opt1;
        }
        if (opt2 == null || opt2.isEmpty()) {
            option2 = "";
        } else if (opt2.length() > 80) {
            option2 = opt2.substring(0, 80);
        } else {
            option2 = opt2;
        }
    }

    public OfferWallPartnerHelper getOfferWallPartnerHelper() {
        return offerWallPartnerHelper;
    }

    public void setOfferWallPartnerHelper(OfferWallPartnerHelper offerWallPartnerHelper) {
        this.offerWallPartnerHelper = offerWallPartnerHelper;
    }

    public void onResume() {
        if (!installCheckerIsRunning) {
            AppIfInstalledChecker checker = new AppIfInstalledChecker();
            checker.start();
        }
    }

    public DownloadManagerLite getDownloadManager() {
        return downloadManager;
    }

    public void setMsgHandler(Handler handler) {
        msgHandler = handler;
    }

    public ArrayList<PartnerAdConfig.Zone> getZoneList() {
        return config.getZoneList();
    }

    public int getAdTypeByZoneId(int zoneId) {
        int adType = Define.AD_TYPE_OFFER_WALL;
        PartnerAdConfig.Zone zone = config.getZoneById(zoneId);
        if (zone != null) {
            if (zone.formats.equals("4")) {
                adType = Define.AD_TYPE_OFFER_WALL;
            } else if (zone.display.equals("105") && zone.formats.equals("3")) {
                adType = Define.AD_TYPE_INTERSTITIAL_WEBPAGE;
            } else if (zone.display.equals("105") && zone.formats.equals("2")) {
                adType = Define.AD_TYPE_INTERSTITIAL_IMAGE;
            }
        }
        return adType;
    }

    /**
     *  Offer Wall Section
     */
    public void initOfferWallAd(int adZoneId) {
        PartnerAdConfig.Zone zone = config.getZoneById(adZoneId);
        if (zone != null) {
            YesupAdBase ad = YesupAdFactory.createYesupAd(context, zone, config, subId, msgTransfer, option1, option2);
            if (ad != null) {
                offerWallAd = (OfferWallAd)ad;
                ad.setDbHelper(dbHelper);
            }
        }
    }

    public OfferWallAd getOfferWallAd() {
        return offerWallAd;
    }

    public boolean offerPageHasLoaded() {
        if (offerWallAd == null || offerWallAd.getOfferPage() == null) {
            return false;
        }else {
            return true;
        }
    }

    private static boolean offerWallIsUpdating = false;
    public void requestOfferWallFromWebsite(int adZoneId) {
        if (offerWallIsUpdating) {
            return;
        }
        offerWallIsUpdating = true;
        if (offerWallAd == null) {
            initOfferWallAd(adZoneId);
        }
        offerWallAd.sendRequest(downloadManager);
    }

    public int requestOfferJumpUrlFromWebsite(OfferModel offer) {
        OfferJumpUrlAd jumpUrlAd = new OfferJumpUrlAd();
        jumpUrlAd.setRequestId(offer.getLocalReference());
        jumpUrlAd.setOffer(offer);
        jumpUrlAd.setDbHelper(dbHelper);
        jumpUrlAd.initAdConfig(context, config, null, subId, msgTransfer, option1, option2);
        jumpUrlAd.initAdData();
        jumpUrlAd.sendRequest(DataCenter.getInstance().getDownloadManager());
        return 0;
    }

    public boolean saveOfferHasBeenClicked(OfferModel offer) {
        offer.setHasClicked(true);
        boolean success = dbHelper.updateOfferClicked(offer);
        if (success && offer.getConvertCondition().toLowerCase().equals("install")) {
            // save this record to offers_clicked table
            if (!dbHelper.offerClickedIsExist(offer)) {
                success = dbHelper.addOfferClicked(offer);
            }
        }
        return success;
    }


    private static boolean installCheckerIsRunning = false;
    private class AppIfInstalledChecker extends Thread {
        @Override
        public void run() {
            installCheckerIsRunning = true;
            List<OfferModel> list = dbHelper.readOffersClickedWhichNeedToCheckByType(OfferPageModel.PAGE_TYPE_OFFER);
            Iterator<OfferModel> iter = list.iterator();
            while (iter.hasNext()) {
                OfferModel offer = iter.next();
                // check if the app has been installed
                if (AppTool.isAppInstalled(context, offer.getAppStoreId())) {
                    offer.setHasInstalled(true);
                    dbHelper.updateOfferInstalled(offer);
                    Log.v("Meshbean", offer.getAppStoreId() + " has installed.");
                }else {
                    Log.v("Meshbean", offer.getAppStoreId() + " has not installed.");
                }
            }
            list = dbHelper.readOffersClickedWhichNeedToReportByType(OfferPageModel.PAGE_TYPE_OFFER);
            iter = list.iterator();
            while (iter.hasNext()) {
                OfferModel offer = iter.next();
                if (report(offer)){
                    offer.setHasReport(true);
                    dbHelper.updateOfferReported(offer);
                }
            }
            installCheckerIsRunning = false;
        }

        private boolean report(OfferModel offer) {
            boolean success = false;
            HttpURLConnection connection = null;
            InputStream input = null;
            String reportUrl = Define.SERVER_HOST + Define.URL_IF_AD_REPORT
                    + "?nid=" + offer.getNid()
                    + "&ad=" + offer.getAdsid()
                    + "&cid=" + offer.getCid()
                    + "&tz=" + config.getNid() + "-" + config.getSid()
                    + "&cluid=" + offer.getJumpUid();
            Log.v("Meshbean", "Report Url:"+reportUrl);
            try {
                URL url = new URL(reportUrl);
                connection = (HttpURLConnection)url.openConnection();
                // set connection parameters
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setDoInput(true);
                // set HTTP header parameters
                // ...
                // begin connecting
                connection.connect();
                // send data body
                // null
                // process response
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String response = "";
                    input = connection.getInputStream();
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count=input.read(data)) != -1) {
                        total += count;
                        String s = new String(data);
                        response = response + s;
                    }
                    success = true;
                    Log.v("Meshbean", "Report Success, Response:"+response);
                }else{
                    success = false;
                    Log.v("Meshbean", "Connect failed[HTTP-"+connection.getResponseCode()+"]:"+Define.SERVER_HOST);
                }
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            } finally {
                try{
                    if (input != null){
                        input.close();
                    }
                }catch (IOException e){
                }
                if (connection != null){
                    connection.disconnect();
                }
            }
            return success;
        }
    }

    /**
     * Interstitial Section
     */
    public void initInterstitialAd(int adZoneId) {
        PartnerAdConfig.Zone zone = config.getZoneById(adZoneId);
        if (zone != null) {
            interstitialAd = YesupAdFactory.createYesupAd(context, zone, config, subId, msgTransfer, option1, option2);
        }
    }

    public void requestInterstitialFromWebsite(int adZoneId) {
        if (interstitialAd == null) {
            initInterstitialAd(adZoneId);
        }
        interstitialAd.sendRequest(downloadManager);
    }

    public String getImageInterstitialClickUrl() {
        String clickUrl = null;
        if (interstitialAd != null) {
            if (Define.AD_TYPE_INTERSTITIAL_IMAGE == interstitialAd.getAdType()) {
                ImageInterstitialAd imageAd = (ImageInterstitialAd)interstitialAd;
                clickUrl = imageAd.getImageInterstitialModel().getPageAd().clickUrl;
            }
        }
        return clickUrl;
    }


    /**
     * Message Handler
     */
    private void transferMessage(Message msg) {
        if (msgHandler != null) {
            msgHandler.sendMessage(msg);
            Log.d(TAG, "Transfer Message "+msg.what+", Object "+msg.obj.toString());
        }
    }
    private class MessageTransfer extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Message newMsg = new Message();
            newMsg.what = msg.what;
            newMsg.arg1 = msg.arg1;
            newMsg.arg2 = msg.arg2;
            newMsg.obj = msg.obj;
            transferMessage(newMsg);
        }
    }

}
