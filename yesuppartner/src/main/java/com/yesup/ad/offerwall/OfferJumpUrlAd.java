package com.yesup.ad.offerwall;

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
public class OfferJumpUrlAd extends YesupAdRequest {
    private static final String TAG = "OfferJumpUrlAd";

    private OfferModel offer;
    private String cuid = "";

    public void setOffer(OfferModel offer) {
        this.offer = offer;
    }

    public void setCuid(String cuid) {
        if (cuid != null && !cuid.isEmpty()) {
            this.cuid = cuid;
        } else {
            this.cuid = "";
        }
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_OFFER_WALL);

        setRequestType(YesupAdRequest.REQ_TYPE_OFFER_JUMPURL);
        setRequestMethod("POST");
        setRequestId(offer.getLocalReference());
        setDownloadUrl(Define.SERVER_HOST + Define.URL_IF_INCENTIVE_API);
        setSaveFileName(AppTool.getIncentiveLocalDataPath(context, offer));
        setRequestHeaderParameter("Authorization", "key=" + adConfig.getKey());
        setRequestHeaderParameter("ACCEPT", "application/json");
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
        setRequestDataParameter("adnid", Integer.toString(offer.getNid()));
        setRequestDataParameter("cid", Integer.toString(offer.getCid()));
        setRequestDataParameter("ad", Integer.toString(offer.getCvid()));
        setRequestDataParameter("fmt", "4");
        setRequestDataParameter("cuid", cuid);
    }

    @Override
    public boolean parseResponseDataWithJson() {
        boolean success;
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
        success = OfferModel.parseOfferJumpUrlFromJson(jsonData, offer);
        // save detail data to local database
        String jumpResult = offer.getJumpResult();
        if (success && jumpResult != null && jumpResult.length() > 0) {
            success = dbHelper.updateOfferJumpUrl(offer);
        } else {
            success = false;
        }
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
