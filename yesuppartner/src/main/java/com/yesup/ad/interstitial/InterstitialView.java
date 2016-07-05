package com.yesup.ad.interstitial;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
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

import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.partner.R;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;

import java.io.File;

/**
 * Created by derek on 4/12/16.
 */
public class InterstitialView extends Dialog {
    private static final String TAG = "InterstitialView";
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
    private boolean isShowed = false;

    public InterstitialView(Context context) {
        super(context);
    }

    public InterstitialView(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected InterstitialView(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public boolean isShowed() {
        return isShowed;
    }

    private Interstitial.DialogConfig dialogConfig;

    private Interstitial.DialogConfig getDialogConfig() {
        if (dialogConfig == null) {
            Interstitial interstitial = Interstitial.getInstance();
            dialogConfig = interstitial.getDialogConfig();
            if (dialogConfig == null) {
                // default config
                dialogConfig = new Interstitial.DialogConfig();
            }
        }
        return dialogConfig;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    //dialog.cancel();
                    return true;
                }
                return false;
            }
        });
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initListener();
        onCreateView();
    }

    private void onCreateView() {
        Interstitial.DialogConfig config = getDialogConfig();

        layoutContainer = (RelativeLayout)findViewById(R.id.layout_container);
        layoutAdContainer = (RelativeLayout)findViewById(R.id.layout_ad_container);
        layoutPartner = (FrameLayout)findViewById(R.id.layout_partner);
        if (config.partnerView != null) {
            View v = config.partnerView.getView(null, layoutPartner);
            config.partnerView.saveView(v);
            config.partnerView.saveParentView(layoutPartner);
            layoutPartner.addView(v);
        }

        btnClose = (Button)findViewById(R.id.btn_close);
        btnClose.setAlpha(0.5f);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnClose.setVisibility(View.GONE);

        imageLoading = (ImageView)findViewById(R.id.image_loading);
        operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.yesup_loading);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        imageViewAd = (ImageView)findViewById(R.id.image_ad);
        imageViewAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageInterstitialRequest interstitialRequest = (ImageInterstitialRequest)dialogConfig.interstitialController.getInterstitialRequest();
                String clickUrl = interstitialRequest.getImageInterstitialModel().adList.get(0).clickUrl;
                if (clickUrl != null && !clickUrl.isEmpty()) {
                    stopCloseTimer();
                    getDialogConfig().adClicked = true;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
                    getContext().startActivity(browserIntent);
                }
            }
        });

        webViewAd = (WebView)findViewById(R.id.web_ad);
        webViewAd.setWebViewClient(viewClient);
        WebSettings webSettings = webViewAd.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
    }

    public boolean disableRotateScreen() {
        Interstitial.DialogConfig config = getDialogConfig();
        if (config.parentActivity != null) {
            Activity activity = config.parentActivity;
            // get current display orientation
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            // disable rotate screen
            if (point.x <= point.y) {
                config.oldOrientation = config.parentActivity.getRequestedOrientation();
                config.parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                config.oldOrientation = config.parentActivity.getRequestedOrientation();
                config.parentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean restoreRotateScreen() {
        Interstitial.DialogConfig config = getDialogConfig();
        // restore old orientation
        if (config.parentActivity != null) {
            config.parentActivity.setRequestedOrientation(config.oldOrientation);
            return true;
        } else {
            return false;
        }
    }

    private void initListener() {
        Interstitial.DialogConfig config = getDialogConfig();
        try {
            iListener = (IInterstitialListener)config.parentActivity;
        } catch (ClassCastException e) {
            throw new ClassCastException(config.parentActivity.toString()
                    +" must implement IInterstitialListener!");
        }
    }

    @Override
    public void onStart() {
        //Log.i(TAG, "OnStart");
        super.onStart();
        Interstitial.DialogConfig config = getDialogConfig();
        config.interstitialController = (InterstitialController)dataCenter.getAdController(config.adZoneId, msgHandler);

        if (config.adClicked) {
            dismiss();
        } else {
            viewControl();
            iListener.onInterstitialShown();
            config.interstitialController.onResume();
        }
    }

    @Override
    public void onStop() {
        //Log.i(TAG, "OnStop");
        super.onStop();
        dialogConfig.interstitialController.onPause();
        isShowed = false;
        stopLoadingAnimation();
        stopCloseTimer();

        onDestroyView();
    }

    public void onDestroyView() {
        //super.onDestroyView();
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
        Interstitial.DialogConfig config = getDialogConfig();
        InterstitialRequest interstitialRequest = config.interstitialController.getInterstitialRequest();
        isShowed = true;
        resizeView();
        switch (config.viewState) {
            case Interstitial.VIEW_STATE_INIT:
                imageViewAd.setVisibility(View.GONE);
                webViewAd.setVisibility(View.GONE);
                imageLoading.setVisibility(View.VISIBLE);
                startLoadingAnimation();
                break;

            case Interstitial.VIEW_STATE_GOT_AD:
                imageViewAd.setVisibility(View.GONE);
                webViewAd.setVisibility(View.GONE);
                imageLoading.setVisibility(View.VISIBLE);
                startLoadingAnimation();
                if (interstitialRequest != null) {
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == interstitialRequest.getAdType()) {
                        PageInterstitialRequest pageAd = (PageInterstitialRequest)interstitialRequest;
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

            case Interstitial.VIEW_STATE_LOADED_AD:
                stopLoadingAnimation();
                if (interstitialRequest != null) {
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == interstitialRequest.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.VISIBLE);
                        // begin timer to impression
                        PageInterstitialRequest pageAd = (PageInterstitialRequest)interstitialRequest;
                        pageAd.impressInterstitialAfterWait();
                    } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == interstitialRequest.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.VISIBLE);
                        // display image
                        ImageInterstitialRequest pageAd = (ImageInterstitialRequest)interstitialRequest;
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

            case Interstitial.VIEW_STATE_IMPRESSED:
                if (interstitialRequest != null) {
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == interstitialRequest.getAdType()) {
                        imageLoading.setVisibility(View.GONE);
                        imageViewAd.setVisibility(View.GONE);
                        webViewAd.setVisibility(View.VISIBLE);
                    } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == interstitialRequest.getAdType()) {
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

            case Interstitial.VIEW_STATE_NO_AD:
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
        Interstitial.DialogConfig config = getDialogConfig();
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
        Interstitial.DialogConfig config = getDialogConfig();
        InterstitialRequest interstitialRequest = config.interstitialController.getInterstitialRequest();
        double targetScale = 300.0 / 250.0;
        double screenScale = (double)screenWidth / (double)screenHeight;
        if (interstitialRequest == null) {
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

        if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == interstitialRequest.getAdType()) {
            // web page
            p.x = screenWidth * 4 / 5;
            p.y = screenHeight * 4 / 5;
        } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == interstitialRequest.getAdType()) {
            // image
            ImageInterstitialRequest pageAd = (ImageInterstitialRequest)interstitialRequest;
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
        Interstitial.DialogConfig config = getDialogConfig();
        if (config.allowUserCloseAfterImpressed) {
            // show close button
            btnClose.setVisibility(View.VISIBLE);
        }
        if (config.closeAfterImpressed) {
            dismiss();
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
            Interstitial.DialogConfig config = getDialogConfig();
            config.viewState = Interstitial.VIEW_STATE_LOADED_AD;
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
            Interstitial.DialogConfig config = getDialogConfig();
            config.adClicked = true;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(intent);
            return true;
            //return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * timer for close dialog in time
     */
    private CountDownTimer closeTimer;

    public void startCloseTimer() {
        Interstitial.DialogConfig config = getDialogConfig();
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
            Interstitial.DialogConfig config = getDialogConfig();
            YesupAdRequest request;

            switch (msg.what) {
                case Define.MSG_AD_REQUEST_SUCCESSED:
                    request = (YesupAdRequest)msg.obj;
                    if (Define.AD_TYPE_INTERSTITIAL_WEBPAGE == request.getAdType()) {
                        config.viewState = Interstitial.VIEW_STATE_GOT_AD;
                    } else if (Define.AD_TYPE_INTERSTITIAL_IMAGE == request.getAdType()) {
                        config.viewState = Interstitial.VIEW_STATE_LOADED_AD;
                    }
                    viewControl();
                    break;
                case Define.MSG_AD_REQUEST_FAILED:
                    config.viewState = Interstitial.VIEW_STATE_NO_AD;
                    viewControl();
                    iListener.onInterstitialError();
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
