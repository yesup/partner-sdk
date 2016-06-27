package com.yesup.sdk.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yesup.ad.banner.BannerView;

public class SecondActivity extends AppCompatActivity {

    private BannerView bannerAdTop;
    private BannerView bannerAdBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        bannerAdTop = (BannerView)findViewById(R.id.yesupBannerAdTop);
        bannerAdBottom = (BannerView)findViewById(R.id.yesupBannerAdBottom);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != bannerAdTop) {
            bannerAdTop.onResume();
        }
        if (null != bannerAdBottom) {
            bannerAdBottom.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != bannerAdTop) {
            bannerAdTop.onPause();
        }
        if (null != bannerAdBottom) {
            bannerAdBottom.onPause();
        }
    }
}
