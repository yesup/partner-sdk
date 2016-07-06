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
 * Created by derek on 6/15/16.
 */
public class BannerRequest extends YesupAdRequest {
    private static final String TAG = "BannerRequest";
    private BannerModel bannerModel = new BannerModel();

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_BANNER_IMAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_BANNER_LIST);
        setRequestMethod("POST");
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_BANNER_LIST);
        setSaveFileName(AppTool.getBannerListLocalDataPath(context, adZone.id));

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
        setRequestDataParameter("size", "3");
    }

    @Override
    public boolean parseResponseDataWithJson() {
        boolean success = false;
        // read data from file
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(AppTool.getBannerListLocalDataPath(context, adZone.id));
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        if (jsonData.length() <= 50) {
            Log.w(TAG, "Read resource data: " + jsonData);
        }
        // parse json
        success = bannerModel.parseBannerFromJson(jsonData);
        if (success) {
            if (!bannerModel.result.equals("ready")) {
                success = false;
            }
        }
        // save data to local database
        // ...
        // delete temp file for api
        File file = new File(AppTool.getBannerListLocalDataPath(context, adZone.id));
        file.delete();
        return success;
    }

    public boolean isReady() {
        if (bannerModel.result != null && bannerModel.result.equals("ready")) {
            return true;
        } else  {
            return false;
        }
    }

    public int getBannerSize() {
        return bannerModel.bannerList.size();
    }

    public BannerModel.Banner getBanner(int index) {
        BannerModel.Banner banner = null;
        if (index >= 0 && index < getBannerSize()) {
            banner = bannerModel.bannerList.get(index);
        }
        return banner;
    }

    public BannerModel getBannerModel() {
        return bannerModel;
    }
}
