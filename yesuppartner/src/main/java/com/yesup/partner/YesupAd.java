package com.yesup.partner;

import android.app.Activity;
import android.content.Context;

import com.yesup.ad.framework.AdZone;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;

import java.util.ArrayList;

/**
 * Created by derek on 4/20/16.
 */
public class YesupAd {
    protected Activity parentActivity;
    protected Context context;

    public YesupAd(Context context) {
        this.context = context;
        DataCenter center = DataCenter.getInstance();
        center.init(context);
    }

    public YesupAd(Activity parentActivity) {
        this.parentActivity = parentActivity;
        this.context = parentActivity;
        DataCenter center = DataCenter.getInstance();
        center.init(context);
    }

    public String getVersion() {
        return Define.SDK_VERSION;
    }

    public void setDebugMode(boolean debugMode) {
        DataCenter center = DataCenter.getInstance();
        center.setDebugMode(debugMode);
    }

    public void setSubId(String subId) {
        DataCenter center = DataCenter.getInstance();
        center.setSubId(subId);
    }

    public void setCuid(String cuid) {
        DataCenter center = DataCenter.getInstance();
        center.setCuid(cuid);
    }

    public void setOption(String opt1, String opt2, String opt3) {
        DataCenter center = DataCenter.getInstance();
        center.setOption(opt1, opt2, opt3);
    }

    public void onResume() {
        DataCenter center = DataCenter.getInstance();
        center.onResume();
    }

    public static final ArrayList<AdZone> getAllZoneList() {
        DataCenter center = DataCenter.getInstance();
        return center.getZoneList();
    }

    public static final int getAdTypeByZoneId(int zoneId) {
        DataCenter center = DataCenter.getInstance();
        AdZone zone = center.getAdConfig().getZoneById(zoneId);
        if (null == zone) {
            return Define.AD_TYPE_UNKNOWN;
        } else {
            return zone.getAdType();
        }
    }
}
