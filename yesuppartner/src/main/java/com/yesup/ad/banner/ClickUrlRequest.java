package com.yesup.ad.banner;

import android.util.Log;

import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
import com.yesup.ad.utils.StringTool;
import com.yesup.ad.utils.Uuid;

import java.io.File;
import java.io.IOException;

/**
 * Created by derek on 4/21/16.
 */
public class ClickUrlRequest extends YesupAdRequest {
    private static final String TAG = "ClickUrlRequest";

    private BannerModel.Banner mBanner;
    //private String cuid = "";

    public void setOffer(BannerModel.Banner banner) {
        this.mBanner = banner;
    }

    /*public void setCuid(String cuid) {
        if (cuid != null && !cuid.isEmpty()) {
            this.cuid = cuid;
        } else {
            this.cuid = "";
        }
    }*/

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_BANNER_IMAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_BANNER_CLICK_URL);
        setRequestMethod("POST");
        //setRequestId(0);
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_INCENTIVE_API);
        setSaveFileName(AppTool.getBannerClickLocalDataPath(context, adZone.id));
        setRequestHeaderParameter("Authorization", "key=" + adConfig.getKey());
        setRequestHeaderParameter("Accept", "application/json");
        setRequestHeaderParameter("User-Agent", AppTool.makeUserAgent());
        setRequestHeaderParameter("Content-Type", "application/x-www-form-urlencoded");
        setRequestDataParameter("nid", adConfig.getNid()); // "1520"
        setRequestDataParameter("pid", adConfig.getPid()); // "42800"
        setRequestDataParameter("sid", adConfig.getSid()); // "45852"
        setRequestDataParameter("zone", String.valueOf(adZone.id));
        setRequestDataParameter("uuid", new Uuid(context).getUUID());
        setRequestDataParameter("subid", subId);
        setRequestDataParameter("opt1", opt1);
        setRequestDataParameter("opt2", opt2);
        setRequestDataParameter("opt3", opt3);
        setRequestDataParameter("adnid", mBanner.adnid);
        setRequestDataParameter("cid", mBanner.cid);
        setRequestDataParameter("ad", mBanner.adsid);
        setRequestDataParameter("fmt", "4");
        setRequestDataParameter("cuid", mBanner.cuid);
    }

    @Override
    public boolean parseResponseDataWithJson() {
        boolean success;
        if (mBanner == null) {
            return false;
        }
        // read data from file
        String jumpUrlPath = AppTool.getBannerClickLocalDataPath(context, adZone.id);
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
        success = BannerModel.parseBannerClickUrlFromJson(jsonData, mBanner);
        // save detail data to local database
        // ...
        // delete temp file for jumpurl api
        File file = new File(jumpUrlPath);
        file.delete();
        return success;
    }

    @Override
    public boolean isReady() {
        return true;
    }

}
