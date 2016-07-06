package com.yesup.ad.interstitial;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
import com.yesup.ad.utils.StringTool;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by derek on 4/21/16.
 */
public class InterstitialImpressRequest extends YesupAdRequest {
    private String impressUrl;
    private boolean impressDone = false;
    private boolean credit = false;

    public boolean isCredit() {
        return credit;
    }

    public void setImpressUrl(String impressUrl) {
        this.impressUrl = impressUrl;
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_WEBPAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_IMPRESS);
        setRequestMethod("GET");
        setDownloadUrl(impressUrl);
        setSaveFileName(AppTool.getInterstitialImpressResponseLocalDataPath(context));
        setRequestHeaderParameter("Authorization", "key=" + adConfig.getKey());
        setRequestHeaderParameter("Accept", "application/json");
        setRequestHeaderParameter("User-Agent", AppTool.makeUserAgent());
        setRequestHeaderParameter("Content-Type", "application/x-www-form-urlencoded");
    }

    @Override
    public boolean parseResponseDataWithJson() {
        boolean success = false;
        // read data from file
        String jsonData;
        try {
            jsonData = StringTool.readStringFromFile(AppTool.getInterstitialImpressResponseLocalDataPath(context));
        } catch (IOException e) {
            e.printStackTrace();
            jsonData = "";
        }
        if (jsonData.length() <= 50) {
            Log.w(TAG, "Read resource data: " + jsonData);
        }
        // parse json
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("done")) {
                    impressDone = reader.nextBoolean();
                } else if (name.equals("credit")) {
                    credit = reader.nextBoolean();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e){
                //
            }
        }
        //
        return success;
    }

    @Override
    public boolean isReady() {
        return impressDone;
    }
}
