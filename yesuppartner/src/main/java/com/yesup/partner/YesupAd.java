package com.yesup.partner;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.yesup.partner.activities.OfferWallPartnerHelper;
import com.yesup.partner.interstitial.Interstitial;
import com.yesup.partner.interstitial.PartnerBaseView;
import com.yesup.partner.interstitial.YesupBaseView;
import com.yesup.partner.module.DataCenter;
import com.yesup.partner.module.Define;
import com.yesup.partner.module.PartnerAdConfig;

import java.util.ArrayList;

/**
 * Created by derek on 4/20/16.
 */
public class YesupAd {
    private FragmentActivity parentActivity;
    private static Context context;
    private Interstitial interstitial = Interstitial.getInstance();

    public YesupAd(FragmentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.context = parentActivity;
        DataCenter center = DataCenter.getInstance();
        center.init(context);
    }

    public void setOfferWallPartnerHelper(OfferWallPartnerHelper offerWallPartnerHelper) {
        DataCenter center = DataCenter.getInstance();
        center.setOfferWallPartnerHelper(offerWallPartnerHelper);
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

    public void setOption(String opt1, String opt2) {
        DataCenter center = DataCenter.getInstance();
        center.setOption(opt1, opt2);
    }

    public void onResume() {
        DataCenter center = DataCenter.getInstance();
        center.onResume();
    }

    public ArrayList<PartnerAdConfig.Zone> getAllZoneList() {
        DataCenter center = DataCenter.getInstance();
        return center.getZoneList();
    }

    public int getAdTypeByZoneId(int zoneId) {
        DataCenter center = DataCenter.getInstance();
        return center.getAdTypeByZoneId(zoneId);
    }

    public void showOfferWall(String subId, int zoneId, String opt1, String opt2) {
        int adType = getAdTypeByZoneId(zoneId);
        switch (adType) {
            case Define.AD_TYPE_OFFER_WALL:
                DataCenter center = DataCenter.getInstance();
                setSubId(subId);
                setOption(opt1, opt2);
                center.initOfferWallAd(zoneId);
                // show offer wall activity
                Intent intent = new Intent(context, OfferWallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ZONE_ID", zoneId);
                context.startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void showInterstitial(String subId, int zoneId, boolean fullScreen,
                                 boolean allowUserCloseAfterImpressed,
                                 PartnerBaseView partnerView,
                                 String opt1, String opt2) {
        PartnerBaseView partnerBaseView = null;
        if (partnerView == null) {
            partnerBaseView = new YesupBaseView(parentActivity);
        } else {
            partnerBaseView = partnerView;
        }
        int adType = getAdTypeByZoneId(zoneId);
        switch (adType) {
            case Define.AD_TYPE_INTERSTITIAL_WEBPAGE:
            case Define.AD_TYPE_INTERSTITIAL_IMAGE:
                DataCenter center = DataCenter.getInstance();
                setSubId(subId);
                setOption(opt1, opt2);
                center.initInterstitialAd(zoneId);
                // interstitial
                interstitial.setParentActivity(parentActivity);
                interstitial.setPartnerView(partnerBaseView);
                interstitial.show(zoneId, fullScreen, allowUserCloseAfterImpressed);
                break;
            default:
                break;
        }
    }
}
