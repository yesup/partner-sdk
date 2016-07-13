package com.yesup.ad.framework;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.yesup.ad.offerwall.OfferWallPartnerHelper;
import com.yesup.ad.offerwall.OfferModel;
import com.yesup.ad.offerwall.OfferPageModel;
import com.yesup.ad.utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by derek on 6/20/16.
 */
public class DataCenter {
    private String TAG = "DataCenter";
    private static DataCenter dataCenter = new DataCenter();
    private DataCenter() {}
    public static DataCenter getInstance() {
        return dataCenter;
    }

    private boolean isDebugMode = false;
    private AdConfig config;
    private Context context;
    private DownloadManagerLite downloadManager = new DownloadManagerLite(2);
    private DBHelper dbHelper;
    //private MessageTransfer msgTransfer = new MessageTransfer();

    private String subId;
    private String option1 = "";
    private String option2 = "";
    private String option3 = "";
    private String cuid = "";

    private Map adControllerMap = new HashMap();

    private OfferWallPartnerHelper offerWallPartnerHelper = null;

    public void init(Context context) {
        if (this.context != null) {
            return;
        }
        this.context = context;
        downloadManager.start();
        dbHelper = new DBHelper(context);
        config = new AdConfig(context);
        if (isDebugMode) {
            Define.SERVER_HOST = Define.SERVER_HOST_DEBUG;
        } else {
            Define.SERVER_HOST = config.getServing();
        }
        if (subId == null || subId.length() <= 0) {
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

    public void setOption(String opt1, String opt2, String opt3) {
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
        if (opt3 == null || opt3.isEmpty()) {
            option3 = "";
        } else if (opt3.length() > 80) {
            option3 = opt3.substring(0, 80);
        } else {
            option3 = opt3;
        }
    }

    public void setCuid(String cuid) {
        if (cuid != null && !cuid.isEmpty()) {
            this.cuid = cuid;
        } else {
            this.cuid = "";
        }
    }

    public String getCuid() {
        return this.cuid;
    }

    public AdConfig getAdConfig() {
        return config;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    public AdController getAdController(int zoneId, Handler msgHandler) {
        AdController controller = (AdController)adControllerMap.get(zoneId);
        if (null == controller) {
            AdZone zone = config.getZoneById(zoneId);
            if (null != zone) {
                controller = YesupAdFactory.createYesupAd(context, config, zone, subId, msgHandler, option1, option2, option3);
                if (null != controller) {
                    adControllerMap.put(zoneId, controller);
                }
            } else {
                throw new RuntimeException("[YESUP SDK]Can not find the zone which zoneid="+zoneId);
            }
        } else {
            controller.setExHandler(msgHandler);
        }
        return controller;
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

    public ArrayList<AdZone> getZoneList() {
        return config.getZoneList();
    }

    /**
     *  Offer Wall Section
     */
    private static boolean installCheckerIsRunning = false;
    private class AppIfInstalledChecker extends Thread {
        @Override
        public void run() {
            installCheckerIsRunning = true;
            List<AdClickModel> list = dbHelper.readAdsClickedWhichNeedToCheck();
            Iterator<AdClickModel> iter = list.iterator();
            while (iter.hasNext()) {
                AdClickModel click = iter.next();
                // check if the app has been installed
                if (AppTool.isAppInstalled(context, click.appStoreId)) {
                    click.installed = true;
                    dbHelper.updateAdInstalled(click);
                    Log.v("Meshbean", click.appStoreId + " has installed.");
                }else {
                    Log.v("Meshbean", click.appStoreId + " has not installed.");
                }
            }
            list = dbHelper.readAdsClickedWhichNeedToReport();
            iter = list.iterator();
            while (iter.hasNext()) {
                AdClickModel click = iter.next();
                if (report(click)){
                    click.reported = true;
                    dbHelper.updateAdReported(click);
                }
            }
            installCheckerIsRunning = false;
        }

        private boolean report(AdClickModel click) {
            boolean success = false;
            HttpURLConnection connection = null;
            InputStream input = null;
            String reportUrl = Define.SERVER_HOST + Define.URL_IF_AD_REPORT
                    + "?nid=" + click.adNid
                    + "&ad=" + click.adSid
                    + "&cid=" + click.cid
                    + "&tz=" + config.getNid() + "-" + config.getSid()
                    + "&cluid=" + click.jumpUid;
            Log.i(TAG, "Report Url:"+reportUrl);
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
                    Log.v(TAG, "Report Success, Response:"+response);
                }else{
                    success = false;
                    Log.v(TAG, "Connect failed[HTTP-"+connection.getResponseCode()+"]:"+Define.SERVER_HOST);
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

}
