package com.yesup.ad.interstitial;

/**
 * Created by derek on 4/12/16.
 */
public interface IInterstitialListener {
    void onInterstitialShown();
    void onInterstitialCredited();
    void onInterstitialClosed();
    void onInterstitialError();
}
