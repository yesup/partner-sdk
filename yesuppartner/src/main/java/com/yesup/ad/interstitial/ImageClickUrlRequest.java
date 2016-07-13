package com.yesup.ad.interstitial;

import android.util.Log;

import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
import com.yesup.ad.utils.StringTool;
import com.yesup.ad.utils.Uuid;

import java.io.File;
import java.io.IOException;

/**
 * Created by derek on 2016.7.13.
 */
public class ImageClickUrlRequest extends YesupAdRequest {
    private static final String TAG = "ImageClickUrlRequest";

    private ImageInterstitialModel.PageAd mImageAd;

    public void setImageAd(ImageInterstitialModel.PageAd imageAd) {
        this.mImageAd = imageAd;
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_IMAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_IMAGE_CLICK_URL);
        setRequestMethod("POST");
        //setRequestId(0);
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_INCENTIVE_API);
        setSaveFileName(AppTool.getImageClickLocalDataPath(context, adZone.id));
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
        setRequestDataParameter("adnid", mImageAd.adNid);
        setRequestDataParameter("cid", mImageAd.cid);
        setRequestDataParameter("ad", mImageAd.adSid);
        setRequestDataParameter("fmt", "4");
        setRequestDataParameter("cuid", mImageAd.clickUid);
    }

    @Override
    public boolean parseResponseDataWithJson() {
        boolean success = false;
        if (mImageAd == null) {
            return false;
        }
        // read data from file
        String jumpUrlPath = AppTool.getImageClickLocalDataPath(context, adZone.id);
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
        //success = BannerModel.parseBannerClickUrlFromJson(jsonData, mImageAd);
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
