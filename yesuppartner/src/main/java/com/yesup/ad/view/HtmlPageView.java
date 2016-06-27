package com.yesup.ad.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.yesup.ad.banner.BannerModel;
import com.yesup.partner.R;

/**
 * Created by derek on 6/23/16.
 */
public class HtmlPageView extends FrameLayout {
    private final String TAG = "HtmlPageView";
    private WebView mWebView;
    private MyWebViewClient viewClient = new MyWebViewClient();

    private int mId;
    private String mShowUrl;
    private String mClickUrl;

    public HtmlPageView(Context context) {
        super(context);
        initPageView(context);
    }

    public HtmlPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPageView(context);
    }

    public HtmlPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPageView(context);
    }

    private void initPageView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.yesup_fragment_htmlpage1, this);

        mWebView = (WebView)view.findViewById(R.id.fragment_webview);
        mWebView.setWebViewClient(viewClient);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "webview on click");
            }
        });
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "webview on touch down");
                        // onClick
                        //onWebviewClick();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public void onResume() {
        if (mShowUrl.length() > 0) {
            mWebView.loadUrl(mShowUrl);
        }
    }

    public void onPause() {
    }

    /**
     * set a identify if you need
     * @param id
     */
    public void setId(int id) {
        mId = id;
    }
    public int getId() {
        return mId;
    }

    public void setUrl(String showUrl, String clickUrl) {
        mShowUrl = showUrl;
        mClickUrl = clickUrl;
    }

    private void onWebviewClick() {
        if (null == mClickUrl || mClickUrl.length() <= 0) {
            return;
        }
        Log.d(TAG, "OnClick:"+mClickUrl);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mClickUrl));
        getContext().startActivity(browserIntent);
    }

    /**
     * Web View Client
     */
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //Log.v(TAG, "PageStarted: " + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //Log.v(TAG, "PageFinished: " + url);
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            //Log.v(TAG, "LoadResource: " + url);
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Log.v(TAG, "JumpTo: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
