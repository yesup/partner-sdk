package com.yesup.partner.module;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.yesup.partner.tools.AppTool;
import com.yesup.partner.tools.Config;
import com.yesup.partner.tools.DownloadManagerLite;
import com.yesup.partner.tools.StringTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by derek on 2/24/16.
 */
public class DataCenter {
    private DataCenter() {}
    public static DataCenter getInstance() {
        return dataCenter;
    }

    private DownloadHandler downloadHandler = new DownloadHandler();
    private static boolean isDebugMode = false;
    private static Config config;
    private static Context context;
    private static Handler msgHandler;
    private static DownloadManagerLite downloadManager = new DownloadManagerLite(2);
    private static DBHelper dbHelper;
    private static DataCenter dataCenter = new DataCenter();
    private OfferPageModel offerPage = null;
    private static String subId;


    public void init(Context context) {
        if (this.context != null){
            return;
        }
        this.context = context;
        downloadManager.setMsgHandler(downloadHandler);
        downloadManager.start();
        dbHelper = new DBHelper(context);
        config = new Config(context);
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

    public void onResume() {
        if (!installCheckerIsRunning) {
            AppIfInstalledChecker checker = new AppIfInstalledChecker();
            checker.start();
        }
    }

    public void setMsgHandler(Handler handler) {
        msgHandler = handler;
    }

    public OfferPageModel getOfferPage() {
        return offerPage;
    }

    public int getOfferCount() {
        if (offerPage == null) {
            return 0;
        }else {
            return offerPage.getList().size();
        }
    }

    public OfferModel getOfferAt(int index) {
        return offerPage.getList().get(index);
    }

    public boolean offerPageHasExpired() {
        //return true;
        ///*
        boolean expired = false;
        if (offerPage == null) {
            expired = true;
        }else {
            long curTime = getCurTimeByExpireFormat(); // change to minites
            long expireTime = offerPage.getExpire();
            if (curTime > expireTime){
                expired = true;
            }
        }
        return expired;
        //*/
    }

    public boolean offerPageHasLoaded() {
        if (offerPage == null){
            return false;
        }else {
            return true;
        }
    }

    public int loadOfferListFromLocalDatabase() {
        int count = 0;
        if (offerPage != null) {
            offerPage.clean();
        }
        offerPage = dbHelper.readOfferPageByType(OfferPageModel.PAGE_TYPE_OFFER);
        if (offerPage != null){
            List<OfferModel> list = dbHelper.readOffersByType(OfferPageModel.PAGE_TYPE_OFFER);
            offerPage.setList(list);
            count = list.size();
        }
        return count;
    }

    private static boolean offerWallIsUpdating = false;
    public void updateOfferListFromWebsite() {
        if (offerWallIsUpdating) {
            return;
        }
        offerWallIsUpdating = true;
        DownloadManagerLite.Request request = new DownloadManagerLite.Request();
        request.setType(Define.DOWNLOAD_TYPE_IF_OFFERWALL);
        request.setRequestMethod("POST");
        request.setRequestId(-2);
        request.setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_OFFER_WALL);
        request.setSaveFileName(AppTool.getOfferWallLocalDataPath(context));
        request.setRequestHeaderParameter("Authorization", "key=" + config.getKey()); // "key=9dc3a4e269bf88b8e7d6983bcd95f2cd"
        request.setRequestHeaderParameter("ACCEPT", "application/json");
        request.setRequestHeaderParameter("User-Agent", makeUserAgent());
        request.setRequestHeaderParameter("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestDataParameter("nid", config.getNid()); // "1520"
        request.setRequestDataParameter("pid", config.getPid()); // "42800"
        request.setRequestDataParameter("sid", config.getSid()); // "45852"
        request.setRequestDataParameter("zone", config.getZoneId()); // "61899"
        request.setRequestDataParameter("subid", subId);
        request.setRequestDataParameter("opt1", "123");
        request.setRequestDataParameter("opt2", "123");
        downloadManager.newDownload(request);
    }

    public void updateOfferJumpUrlFromWebsite(OfferModel offer) {
        DownloadManagerLite.Request request = new DownloadManagerLite.Request();
        request.setType(Define.DOWNLOAD_TYPE_IF_INCENTIVEAPI);
        request.setRequestMethod("POST");
        request.setRequestId(offer.getLocalReference());
        request.setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_INCENTIVE_API);
        request.setSaveFileName(AppTool.getIncentiveLocalDataPath(context, offer));
        request.setRequestHeaderParameter("Authorization", "key=" + config.getKey());
        request.setRequestHeaderParameter("ACCEPT", "application/json");
        request.setRequestHeaderParameter("User-Agent", makeUserAgent());
        request.setRequestHeaderParameter("Content-Type", "application/x-www-form-urlencoded");
        request.setRequestDataParameter("nid", config.getNid()); // "1520"
        request.setRequestDataParameter("pid", config.getPid()); // "42800"
        request.setRequestDataParameter("sid", config.getSid()); // "45852"
        request.setRequestDataParameter("zone", config.getZoneId()); // "61899"
        request.setRequestDataParameter("subid", subId);
        request.setRequestDataParameter("opt1", "123");
        request.setRequestDataParameter("opt2", "123");
        request.setRequestDataParameter("adnid", Integer.toString(offer.getNid()));
        request.setRequestDataParameter("cid", Integer.toString(offer.getCid()));
        request.setRequestDataParameter("ad", Integer.toString(offer.getCvid()));
        request.setRequestDataParameter("fmt", "4");
        request.setRequestDataParameter("cuid", "123");
        downloadManager.newDownload(request);
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

    private int parseOfferWallJsonData() {
        // read data from file
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(AppTool.getOfferWallLocalDataPath(context));
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        //String jsonData = StringTool.getStringFromRaw(context, R.raw.meshbean_data);
        int count = 0;
        if (jsonData.length() <= 100) {
            count = -1;
            Log.v("Meshbean", "Read resource data: " + jsonData);
        }
        // make sure that download path exist.
        File downloadPath = new File(context.getFilesDir()+Define.LOCAL_IMAGE_DIR);
        if (!downloadPath.exists()){
            downloadPath.mkdirs();
        }
        // parse json
        OfferPageModel tmpOfferPage = OfferPageModel.getMeshbeanOfferPageFromJson(jsonData);
        if (tmpOfferPage.getTotal() > 0) {
            if (offerPage != null) {
                offerPage.clean();
            }
            offerPage = tmpOfferPage;
        }else{
            if (offerPage == null) {
                return 0;
            }else{
                return offerPage.getTotal();
            }
        }
        if (offerPage != null){
            count = getOfferCount();
            dbHelper.delOffer(OfferPageModel.PAGE_TYPE_OFFER);
            dbHelper.delOfferPage(OfferPageModel.PAGE_TYPE_OFFER);
            AppTool.deleteFolder(context.getFilesDir()+Define.LOCAL_IMAGE_DIR);
            // set current timestamp
            long curTime = getCurTimeByExpireFormat() + Define.VALUE_EXPIRE_TIME;
            offerPage.setExpire(curTime);
        }
        dbHelper.addOfferPage(offerPage);

        String downloadHost = offerPage.getCr_host();
        for (int i=0; i<count; i++) {
            // download pictures
            OfferModel offer = offerPage.getList().get(i);

            File imageFile = new File(context.getFilesDir()+Define.LOCAL_IMAGE_DIR
                    +offer.getLocalImageFileName());
            offer.setLocalIconPath(imageFile.getAbsolutePath());
            dbHelper.addOffer(offer);

            if (!imageFile.exists()) {
                requestDownloadFile(offer, downloadHost);
            }
        }
        return count;
    }

    private boolean parseOfferJumpUrlJsonData(int index) {
        OfferModel offer = dataCenter.getOfferAt(index);
        if (offer == null) {
            return false;
        }
        // read data from file
        String jumpUrlPath = AppTool.getIncentiveLocalDataPath(context, offer);
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(jumpUrlPath);
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        if (jsonData.length() <= 50) {
            Log.v("Meshbean", "Read detail data: " + jsonData);
        }
        // parse json
        boolean success;
        success = OfferModel.parseOfferJumpUrlFromJson(jsonData, offer);
        // save detail data to local database
        String jumpResult = offer.getJumpResult();
        if (success && jumpResult != null && jumpResult.length() > 0){
            success = dbHelper.updateOfferJumpUrl(offer);
        }else{
            success = false;
        }
        // delete temp file for jumpurl api
        File file = new File(jumpUrlPath);
        file.delete();
        return success;
    }

    protected long requestDownloadFile(OfferModel offer, String downloadHost) {
        String downloadUrl = "http://" + downloadHost + offer.getIconUrl();
        Log.v("Meshbean", "Begin download "+downloadUrl);
        long downloadId = -1;
        File saveFile = new File(offer.getLocalIconPath());

        DownloadManagerLite.Request request = new DownloadManagerLite.Request();
        request.setType(Define.DOWNLOAD_TYPE_FILE_NORMAL);
        request.setRequestMethod("GET");
        request.setRequestId(offer.getLocalReference());
        request.setDownloadUrl(downloadUrl);
        request.setSaveFileName(saveFile.getAbsolutePath());
        downloadManager.newDownload(request);
        return downloadId;
    }

    private class DownloadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (Define.MSG_DOWNLOAD_STATUS_CHANGED == msg.what) {
                DownloadManagerLite.Request request = (DownloadManagerLite.Request)msg.obj;
                if (Define.DOWNLOAD_TYPE_FILE_NORMAL == request.getType()) {
                    // status code
                    switch (msg.arg1) {
                        case DownloadManagerLite.Request.STATUS_COMPLETED:
                            request = (DownloadManagerLite.Request) msg.obj;
                            // notice interface update view
                            if (msgHandler != null) {
                                Message notice = msgHandler.obtainMessage(Define.MSG_DOWNLOAD_FILE_COMPLETED);
                                notice.arg1 = request.getRequestId();
                                msgHandler.sendMessage(notice);
                            }
                            Log.v("DataCenter", "[" + request.getRequestId() + "] download success.");
                            break;
                        case DownloadManagerLite.Request.STATUS_PROGRESS:
                            request = (DownloadManagerLite.Request) msg.obj;
                            //Log.v("DataCenter", "["+request.getRequestId()+"] download "+request.getPercent()+"%");
                            break;
                        default:
                            // download failed
                            break;
                    }
                } else if (Define.DOWNLOAD_TYPE_IF_OFFERWALL == request.getType()) {
                    // offer wall
                    if (DownloadManagerLite.Request.STATUS_COMPLETED == msg.arg1) {
                        // process json data downloaded
                        parseOfferWallJsonData();
                        if (msgHandler != null) {
                            Message notice = msgHandler.obtainMessage(Define.MSG_DOWNLOAD_OFFERWALL_COMPLETED);
                            notice.arg1 = 0;
                            msgHandler.sendMessage(notice);
                        }
                        Log.v("DataCenter", "OfferWall download success.");
                    }else{
                        Log.v("DataCenter", "OfferWall download failed.");
                    }
                    offerWallIsUpdating = false;
                } else if (Define.DOWNLOAD_TYPE_IF_INCENTIVEAPI == request.getType()) {
                    // incentive api
                    request = (DownloadManagerLite.Request) msg.obj;
                    if (DownloadManagerLite.Request.STATUS_COMPLETED == msg.arg1) {
                        parseOfferJumpUrlJsonData(request.getRequestId());
                        if (msgHandler != null) {
                            Message notice = msgHandler.obtainMessage(Define.MSG_DOWNLOAD_OFFERDETAIL_COMPLETED);
                            notice.arg1 = request.getRequestId();
                            msgHandler.sendMessage(notice);
                        }
                    }
                    Log.v("DataCenter", "IncentiveAPI download success.");
                }
            }
            super.handleMessage(msg);
        }
    }

    // return current time with expire format
    // unit: minite
    private long getCurTimeByExpireFormat() {
        return System.currentTimeMillis()/1000/60;
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

    private String makeUserAgent() {
        String ua = "CpxCenterSDK/" + Define.SDK_VERSION
                + " (Linux; Android " + Build.VERSION.RELEASE + "; "
                + Build.MODEL + " Build/" + Build.ID +")";
        return ua;
    }
}
