package com.yesup.partner.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yesup.partner.R;
import com.yesup.partner.interstitial.IInterstitialListener;
import com.yesup.partner.interstitial.Interstitial;
import com.yesup.partner.interstitial.PartnerBaseView;
import com.yesup.partner.module.DataCenter;
import com.yesup.partner.module.Define;
import com.yesup.partner.module.ImageInterstitialAd;
import com.yesup.partner.module.ImageInterstitialModel;
import com.yesup.partner.module.PageInterstitialAd;
import com.yesup.partner.module.YesupAdBase;

import java.io.File;


/**
 * Created by derek on 4/12/16.
 */
public class InterstitialFragment extends DialogFragment {
    private static final String TAG = "InterstitialFragment";
    private DataCenter dataCenter = DataCenter.getInstance();

    private RelativeLayout layoutContainer;
    private RelativeLayout layoutAdContainer;
    private FrameLayout layoutPartner;
    private ImageView imageLoading;
    private ImageView imageViewAd;
    private WebView webViewAd;
    private Button btnClose;

    private IInterstitialListener iListener;
    private MyWebViewClient viewClient = new MyWebViewClient();
    private Animation operatingAnim;
    private int curOrientation;
    private boolean isShowed = false;

    public boolean isShowed() {
        return isShowed;
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

        public YesupAdBase yesupAd;
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
    private DialogConfig dialogConfig;

    public DialogConfig getDialogConfig() {
        if (dialogConfig == null) {
            Interstitial interstitial = Interstitial.getInstance();
            dialogConfig = interstitial.getDialogConfig();
            if (dialogConfig == null) {
                // default config
                dialogConfig = new DialogConfig();
            }
        }
        return dialogConfig;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    //dialog.cancel();
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        DialogConfig config = getDialogConfig();

        View view = inflater.inflate(R.layout.yesup_fragment_interstitial, container, false);
        layoutContainer = (RelativeLayout)view.findViewById(R.id.layout_container);
        layoutAdContainer = (RelativeLayout)view.findViewById(R.id.layout_ad_container);
        layoutPartner = (FrameLayout)view.findViewById(R.id.layout_partner);
        if (config.partnerView != null) {
            View v = config.partnerView.getView(null, layoutPartner);
            config.partnerView.saveView(v);
            config.partnerView.saveParentView(layoutPartner);
            layoutPartner.addView(v);
        }

        btnClose = (Button) view.findViewById(R.id.btn_close);
        btnClose.setAlpha(0.5f);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        btnClose.setVisibility(View.GONE);

        imageLoading = (ImageView)view.findViewById(R.id.image_loading);
        operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.yesup_loading);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        imageViewAd = (ImageView)view.findViewById(R.id.image_ad);
        imageViewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clickUrl = dataCenter.getImageInterstitialClickUrl();
                if (clickUrl != null && !clickUrl.isEmpty()) {
                    stopCloseTimer();
                    getDialogConfig().adClicked = true;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
                    startActivity(browserIntent);
                }
            }
        });

        webViewAd = (WebView)view.findViewById(R.id.web_ad);
        webViewAd.setWebViewClient(viewClient);
        WebSettings webSettings = webViewAd.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        return view;
    }

    public boolean disableRotateScreen() {
        DialogConfig config = getDialogConfig();
        if (config.parentActivity != null) {
            Activity activity = config.parentActivity;
            // get current display orientation
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            // disable rotate screen
            if (point.x <= point.y) {
                curOrientation = Configuration.ORIENTATION_PORTRAIT;
                config.oldOrientation = config.parentActivity.getRequestedOrientation();
                config.parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                curOrientation = Configuration.ORIENTATION_LANDSCAPE;
                config.oldOrientation = config.parentActivity.getRequestedOrientation();
                config.parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean restoreRotateScreen() {
        DialogConfig config = getDialogConfig();
        // restore old orientation
        if (config.parentActivity != null) {
            config.parentActivity.setRequestedOrientation(config.oldOrientation);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getDialogConfig().parentActivity = activity;

        try {
            iListener = (IInterstitialListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    +" must implement IInterstitialListener!");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dataCenter.setMsgHandler(msgHandler);
        DialogConfig config = getDialogConfig();

        if (config.adClicked) {
            getDialog().cancel();
        } else {
            viewControl();
            iListener.onInterstitialShown();
            dataCenter.requestInterstitialFromWebsite(config.adZoneId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dataCenter.setMsgHandler(null);
        isShowed = false;
        stopLoadingAnimation();
        stopCloseTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        iListener.onInterstitialClosed();
        // restore old orientation
        restoreRotateScreen();
    }

    private void startLoadingAnimation() {
        if (operatingAnim != null && imageLoading != null && !operatingAnim.hasStarted()) {
            imageLoading.startAnimation(operatingAnim);
        }
    }
    private void stopLoadingAnimation() {
        if (operatingAnim != null && imageLoading != null && operatingAnim.hasStarted()) {
            imageLoading.clearAnimation();
        }
    }

    private void viewControl() {
        DialogConfig config = getDialogConfig();
        isShowed = true;
        resizeView();
        switch (config.viewState) {
            case VIEW_STATE_INIT:
                imageViewAd.setVisibility(View.GONE);
                webViewAd.setVisibility(View.GONE);
                imageLoading.setVisibility(View.VISIBLE);
                startLoadingAnimation();
                break;

            case VIEW_STATE_GOT_AD:
                imageViewAd.setVisibility(View.GONE);
                webViewAd.setVisibility(View.GONE);
                imageLoading.setVisibility(View.VISIBLE);
                startLoadingAnimation();
                if (config.yesupAd != null) {
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == config.yesupAd.getAdType()) {
                        PageInterstitialAd pageAd = (PageInterstitialAd)config.yesupAd;
                        String pageUrl = null;
                        if (pageAd.getPageAd() != null) {
                            pageUrl = pageAd.getPageAd().adUrl;
                        }
                        if (!pageUrl.isEmpty()) {
                            webViewAd.loadUrl(pageUrl);
                        }
                    }
                }
                break;

            case VIEW_STATE_LOADED_AD:
                stopLoadingAnimation();
                if (config.yesupAd != null) {
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == config.yesupAd.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.VISIBLE);
                        // begin timer to impression
                        PageInterstitialAd pageAd = (PageInterstitialAd)config.yesupAd;
                        pageAd.impressInterstitialAfterWait();
                    } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == config.yesupAd.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.VISIBLE);
                        // display image
                        ImageInterstitialAd pageAd = (ImageInterstitialAd)config.yesupAd;
                        String filename = pageAd.getImageInterstitialModel().localImageFilename;
                        File imageFile = new File(filename);
                        if (imageFile.exists()){
                            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            imageViewAd.setImageBitmap(bmp);
                        }
                        // begin timer to impression
                        pageAd.impressInterstitialAfterWait();
                    }
                }
                break;

            case VIEW_STATE_IMPRESSED:
                if (config.yesupAd != null) {
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == config.yesupAd.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.VISIBLE);
                    } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == config.yesupAd.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.VISIBLE);
                    }
                }
                if (config.allowUserCloseAfterImpressed) {
                    // show close button
                    btnClose.setVisibility(View.VISIBLE);
                }
                break;

            case VIEW_STATE_NO_AD:
            default:
                imageViewAd.setVisibility(View.GONE);
                webViewAd.setVisibility(View.GONE);
                imageLoading.setVisibility(View.VISIBLE);
                stopLoadingAnimation();
                // show close button
                btnClose.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static final int partnerViewHeight = 110;
    private void resizeView() {
        DialogConfig config = getDialogConfig();
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        wm.getDefaultDisplay().getSize(p);

        Point pTotal = new Point();
        if (config.showFullScreen) {
            resizeFullscreenView(p.x, p.y, pTotal);
        } else {
            resizePopView(p.x, p.y, pTotal);
        }

        int adWidth = pTotal.x;
        int adHeight = pTotal.y - partnerViewHeight;
        layoutAdContainer.setLayoutParams(new RelativeLayout.LayoutParams(adWidth, adHeight));

        layoutContainer.setLayoutParams(new FrameLayout.LayoutParams(pTotal.x, pTotal.y));
    }
    private void resizeFullscreenView(int screenWidth, int screenHeight, Point p) {
        p.x = screenWidth - 2;
        p.y = screenHeight - 80;
    }
    private void resizePopView(int screenWidth, int screenHeight, Point p) {
        DialogConfig config = getDialogConfig();
        double targetScale = 300.0 / 250.0;
        double screenScale = (double)screenWidth / (double)screenHeight;
        if (config.yesupAd == null) {
            if (screenScale > targetScale) {
                p.y = screenHeight * 4 / 5;
                p.x = (int) (((double)p.y - partnerViewHeight) * targetScale);
            } else {
                p.x = screenWidth * 4 / 5;
                p.y = (int) ((double)p.x / targetScale + partnerViewHeight);
            }
            Log.d(TAG, "ResizePopView 1:["+p.x+"]-["+p.y+"]");
            return;
        }

        if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == config.yesupAd.getAdType()) {
            // web page
            p.x = screenWidth * 4 / 5;
            p.y = screenHeight * 4 / 5;
        } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == config.yesupAd.getAdType()) {
            // image
            ImageInterstitialAd pageAd = (ImageInterstitialAd)config.yesupAd;
            ImageInterstitialModel.PageAd imageAd = pageAd.getPageAd();
            if (imageAd != null) {
                targetScale = (double)imageAd.width / (double)imageAd.height;
            }
            if (screenScale > targetScale) {
                p.y = screenHeight * 4 / 5;
                p.x = (int) (((double)p.y - partnerViewHeight) * targetScale);
            } else {
                p.x = screenWidth * 4 / 5;
                p.y = (int) ((double)p.x / targetScale + partnerViewHeight);
            }
        } else {
            if (screenScale > targetScale) {
                p.y = screenHeight * 4 / 5;
                p.x = (int) (((double)p.y - partnerViewHeight) * targetScale);
            } else {
                p.x = screenWidth * 4 / 5;
                p.y = (int) ((double)p.x / targetScale + partnerViewHeight);
            }
        }
        Log.d(TAG, "ResizePopView 2:["+p.x+"]-["+p.y+"]");
    }

    private void onImpressed(int credit) {
        DialogConfig config = getDialogConfig();
        if (config.allowUserCloseAfterImpressed) {
            // show close button
            btnClose.setVisibility(View.VISIBLE);
        }
        if (config.closeAfterImpressed) {
            getDialog().cancel();
        }
    }

    /**
     * Web View Client
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.v(TAG, "PageStarted: " + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.v(TAG, "PageFinished: " + url);
            DialogConfig config = getDialogConfig();
            config.viewState = VIEW_STATE_LOADED_AD;
            viewControl();
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            //Log.v(TAG, "LoadResource: " + url);
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.v(TAG, "JumpTo: " + url);
            DialogConfig config = getDialogConfig();
            config.adClicked = true;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
            //return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * timer for close dialog in time
     */
    private CountDownTimer closeTimer;

    public void startCloseTimer() {
        DialogConfig config = getDialogConfig();
        if (config.allowUserCloseAfterImpressed) {
            closeTimer = new CountDownTimer(5*1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    onImpressed(0);
                }
            }.start();
        }
    }

    public void stopCloseTimer() {
        if (closeTimer != null) {
            closeTimer.cancel();
            closeTimer = null;
        }
    }

    /**
     * message handler
     */
    private MessageHandler msgHandler = new MessageHandler();
    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            DialogConfig config = getDialogConfig();

            switch (msg.what) {
                case Define.MSG_AD_REQUEST_SUCCESSED:
                    config.yesupAd = (YesupAdBase)msg.obj;
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == config.yesupAd.getAdType()) {
                        config.viewState = VIEW_STATE_GOT_AD;
                    } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == config.yesupAd.getAdType()) {
                        config.viewState = VIEW_STATE_LOADED_AD;
                    }
                    viewControl();
                    break;
                case Define.MSG_AD_REQUEST_FAILED:
                    config.viewState = VIEW_STATE_NO_AD;
                    viewControl();
                    break;
                case Define.MSG_AD_REQUEST_IMPRESSED:
                    int credit = msg.arg1;
                    onImpressed(credit);
                    break;
                case Define.MSG_AD_REQUEST_PROGRESSED:
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
