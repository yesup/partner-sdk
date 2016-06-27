package com.yesup.ad.interstitial;

import com.yesup.ad.framework.YesupAdRequest;

/**
 * Created by derek on 4/20/16.
 */
public abstract class InterstitialRequest extends YesupAdRequest {
    @Override
    public abstract void initRequestData();
    @Override
    public abstract boolean parseResponseDataWithJson();
    @Override
    public abstract boolean isReady();
    public abstract void impressInterstitialAfterWait();
}
