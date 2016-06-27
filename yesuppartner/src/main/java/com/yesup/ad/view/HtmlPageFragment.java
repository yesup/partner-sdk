package com.yesup.ad.view;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yesup.partner.R;

/**
 * Created by derek on 6/23/16.
 */
public class HtmlPageFragment extends Fragment {
    private final String TAG = "HtmlPageFragment";
    private WebView mWebView;
    private MyWebViewClient viewClient = new MyWebViewClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.yesup_fragment_htmlpage1, container, false);

        mWebView = (WebView)rootView.findViewById(R.id.fragment_webview);
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
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mWebView.loadUrl("http://ads.jsmtv.com/cpxcenter/cr_image.php?nid=1520&pid=42800&sid=54365&zone=72586&subid=derek&adnid=4&cid=17751&ad=1215&adtype=17&cuid=72586.17751.9638&token=VHsxJc_XUn7KfHvU1SNK-3v7INIgIdLSfSB-UiB70nt-eyl7wCFY2s8i_dHOfNLRJSHALFvazyR-0NL80NUl_Q&opt1=&opt2=&opt3=");
    }

    @Override
    public void onStop() {
        super.onStop();
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
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
