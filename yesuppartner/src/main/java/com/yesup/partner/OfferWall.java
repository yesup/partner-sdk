package com.yesup.partner;


import android.content.Context;
import android.content.Intent;

import com.yesup.partner.module.DataCenter;

public class OfferWall {
    private static final String version = "1.0.160309.02";
    private static Context context;

    public OfferWall(Context context) {
        this.context = context;
        DataCenter center = DataCenter.getInstance();
        center.init(context);
    }

    public String getVersion() {
        return version;
    }

    public void setDebugMode(boolean debugMode) {
        DataCenter center = DataCenter.getInstance();
        center.setDebugMode(debugMode);
    }

    public void setSubId(String subId) {
        DataCenter center = DataCenter.getInstance();
        center.setSubId(subId);
    }

    public void onResume() {
        DataCenter center = DataCenter.getInstance();
        center.onResume();
    }

    public void show(String subId) {
        setSubId(subId);
        // show offer wall activity
        Intent intent = new Intent(context, OfferWallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
