package com.yesup.ad.interstitial;

import android.app.Activity;
import android.view.Window;

import com.yesup.partner.R;

/**
 * Created by derek on 4/12/16.
 */
public class Interstitial {
    private static String TAG = "INTERSTITIAL";
    private InterstitialView interstitialDialog;

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

    private Activity parentActivity;
    public void setParentActivity(Activity activity) {
        parentActivity = activity;
        getDialogConfig().parentActivity = activity;
    }

    private DialogConfig dialogConfig;
    public DialogConfig getDialogConfig() {
        if (dialogConfig == null) {
            dialogConfig = new DialogConfig();
        }
        return dialogConfig;
    }

    public void setPartnerView(PartnerBaseView partnerView) {
        getDialogConfig().partnerView = partnerView;
    }

    public void show(int adZoneId, boolean fullScreen, boolean allowUserCloseAfterImpressed) {
        if (null == parentActivity) {
            return;
        }
        DialogConfig config = getDialogConfig();
        config.showFullScreen = fullScreen;
        config.allowUserCloseAfterImpressed = allowUserCloseAfterImpressed;
        config.adClicked = false;
        config.adZoneId = adZoneId;
        config.viewState = VIEW_STATE_INIT;
        config.interstitialController = null;

        interstitialDialog = new InterstitialView(parentActivity);
        interstitialDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        interstitialDialog.disableRotateScreen();
        interstitialDialog.setCancelable(false);
        interstitialDialog.setContentView(R.layout.yesup_fragment_interstitial);
        interstitialDialog.show();
    }

    public void closeNow() {
        if (interstitialDialog != null) {
            if (interstitialDialog.isShowed()) {
                interstitialDialog.dismiss();
            }
        }
    }

    public void closeAfterCredited() {
        DialogConfig config = getDialogConfig();
        if (config.isImpressed()) {
            closeNow();
        } else {
            config.closeAfterImpressed = true;
        }
    }

    /**
     * dialog config parameters
     */
    public static final int VIEW_STATE_INIT = 0;
    public static final int VIEW_STATE_GOT_AD = 1;
    public static final int VIEW_STATE_LOADED_AD = 2;
    public static final int VIEW_STATE_IMPRESSED = 3;
    public static final int VIEW_STATE_NO_AD = 4;

    public static class DialogConfig {
        public Activity parentActivity;
        public int oldOrientation;
        public PartnerBaseView partnerView = null;
        public boolean closeAfterImpressed = false;
        public boolean allowUserCloseAfterImpressed = true;
        public boolean showFullScreen = false;

        public InterstitialController interstitialController;
        public int adZoneId;
        public boolean adClicked = false;
        public int viewState = VIEW_STATE_INIT;

        public boolean isImpressed() {
            if (viewState >= VIEW_STATE_IMPRESSED) {
                return true;
            } else {
                return false;
            }
        }
    }

}
