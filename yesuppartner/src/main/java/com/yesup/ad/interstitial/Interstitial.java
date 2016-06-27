package com.yesup.ad.interstitial;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.yesup.ad.framework.DataCenter;

/**
 * Created by derek on 4/12/16.
 */
public class Interstitial {
    private static String TAG = "INTERSTITIAL";
    private DataCenter dataCenter = DataCenter.getInstance();
    private InterstitialView interstitialDialog = new InterstitialView();

    // single pattern
    private static Interstitial instance;
    private Interstitial() {
    }
    public static Interstitial getInstance() {
        if (instance == null) {
            instance = new Interstitial();
        }
        return instance;
    }

    private FragmentActivity parentActivity;
    public void setParentActivity(FragmentActivity activity) {
        parentActivity = activity;
        getDialogConfig().parentActivity = activity;
    }

    private InterstitialView.DialogConfig dialogConfig;
    public InterstitialView.DialogConfig getDialogConfig() {
        if (dialogConfig == null) {
            dialogConfig = new InterstitialView.DialogConfig();
        }
        return dialogConfig;
    }

    public void setPartnerView(PartnerBaseView partnerView) {
        getDialogConfig().partnerView = partnerView;
    }

    public void show(int adZoneId, boolean fullScreen, boolean allowUserCloseAfterImpressed) {
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        InterstitialView.DialogConfig config = getDialogConfig();
        config.showFullScreen = fullScreen;
        config.allowUserCloseAfterImpressed = allowUserCloseAfterImpressed;
        config.adClicked = false;
        config.adZoneId = adZoneId;
        config.viewState = InterstitialView.VIEW_STATE_INIT;
        config.interstitialController = null;

        interstitialDialog.disableRotateScreen();
        interstitialDialog.setCancelable(false);
        interstitialDialog.show(fragmentManager, TAG);
    }

    public void closeNow() {
        if (interstitialDialog != null) {
            if (interstitialDialog.isShowed()) {
                interstitialDialog.dismiss();
            }
        }
    }

    public void closeAfterCredited() {
        InterstitialView.DialogConfig config = getDialogConfig();
        if (config.isImpressed()) {
            closeNow();
        } else {
            config.closeAfterImpressed = true;
        }
    }
}