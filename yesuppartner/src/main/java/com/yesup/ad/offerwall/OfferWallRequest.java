package com.yesup.ad.offerwall;

import android.util.Log;

import com.yesup.ad.framework.AdResourceFile;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
import com.yesup.ad.utils.StringTool;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by derek on 4/20/16.
 */
public class OfferWallRequest extends YesupAdRequest {
    private static final String TAG = "OfferWallAd";

    private OfferPageModel offerPage = null;

    public OfferPageModel getOfferPage() {
        return offerPage;
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_OFFER_WALL);

        setRequestType(YesupAdRequest.REQ_TYPE_OFFER_WALL);
        setRequestMethod("POST");
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_OFFER_WALL);
        setSaveFileName(AppTool.getOfferWallLocalDataPath(context));
        setRequestHeaderParameter("Authorization", "key=" + adConfig.getKey()); // "key=9dc3a4e269bf88b8e7d6983bcd95f2cd"
        setRequestHeaderParameter("ACCEPT", "application/json");
        setRequestHeaderParameter("User-Agent", AppTool.makeUserAgent());
        setRequestHeaderParameter("Content-Type", "application/x-www-form-urlencoded");
        setRequestDataParameter("nid", adConfig.getNid()); // "1520"
        setRequestDataParameter("pid", adConfig.getPid()); // "42800"
        setRequestDataParameter("sid", adConfig.getSid()); // "45852"
        setRequestDataParameter("zone", String.valueOf(adZone.id));
        setRequestDataParameter("subid", subId);
        setRequestDataParameter("opt1", opt1);
        setRequestDataParameter("opt2", opt2);
    }

    @Override
    public boolean parseResponseDataWithJson() {
        // read data from file
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(AppTool.getOfferWallLocalDataPath(context));
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        //Log.v(TAG, "Read resource data: " + jsonData);
        int count = 0;
        if (jsonData.length() <= 100) {
            count = -1;
            Log.v(TAG, "Read resource data: " + jsonData);
        }
        // make sure that download path exist.
        File downloadPath = new File(context.getFilesDir()+Define.LOCAL_IMAGE_DIR);
        if (!downloadPath.exists()) {
            downloadPath.mkdirs();
        }
        // parse json
        OfferPageModel tmpOfferPage = OfferPageModel.getMeshbeanOfferPageFromJson(jsonData);
        if (tmpOfferPage.getTotal() > 0) {
            if (offerPage != null) {
                offerPage.clean();
            }
            offerPage = tmpOfferPage;
            offerPage.setZoneId(adZone.id);
        } else {
            if (offerPage == null) {
                return false;
            }else{
                return true;
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
            dbHelper.addOffer(offer);

            File imageFile = new File(context.getFilesDir()+Define.LOCAL_IMAGE_DIR+"/"
                    +offer.getLocalImageFileName());
            if (!imageFile.exists()) {
                requestIconFile(offer, downloadHost);
            }
        }
        return true;
    }

    private void requestIconFile(OfferModel offer, String downloadHost) {
        String downloadUrl = "http://" + downloadHost + offer.getIconUrl();
        Log.v(TAG, "Begin download "+downloadUrl);
        //File saveFile = new File(offer.getLocalIconPath());
        File saveFile = new File(context.getFilesDir()+Define.LOCAL_IMAGE_DIR+"/"
                +offer.getLocalImageFileName());

        AdResourceFile resFile = new AdResourceFile();
        resFile.setRequestId(offer.getLocalReference());
        resFile.setResourceUrl(downloadUrl);
        resFile.setLocalPath(saveFile.getAbsolutePath());
        resFile.initAdConfig(context, adConfig, null, subId, listener, opt1, opt2, opt3);
        resFile.initRequestData();
        resFile.setRequestType(YesupAdRequest.REQ_TYPE_OFFER_ICON);
        resFile.sendRequest(DataCenter.getInstance().getDownloadManager());
        Log.d(TAG, "Send request ICON file: "+downloadUrl);
    }

    public int getOfferCount() {
        if (offerPage == null) {
            return 0;
        }else {
            return offerPage.getList().size();
        }
    }

    public boolean isReady() {
        if (null != offerPage) {
            return true;
        } else  {
            return false;
        }
    }

    public OfferModel getOfferAt(int index) {
        return offerPage.getList().get(index);
    }

    public boolean offerPageHasExpired(int curZoneId) {
        //return true;
        ///*
        boolean expired = false;
        if (offerPage == null) {
            expired = true;
        }else {
            if (curZoneId == offerPage.getZoneId()) {
                long curTime = getCurTimeByExpireFormat(); // change to minites
                long expireTime = offerPage.getExpire();
                if (curTime > expireTime) {
                    expired = true;
                }
            } else {
                expired = true;
            }
        }
        return expired;
        //*/
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

    // return current time with expire format
    // unit: minite
    public static long getCurTimeByExpireFormat() {
        return System.currentTimeMillis()/1000/60;
    }

}
