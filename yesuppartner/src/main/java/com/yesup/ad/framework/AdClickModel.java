package com.yesup.ad.framework;

/**
 * Created by derek on 7/13/16.
 */
public class AdClickModel extends AdData {
    public static final String AD_TYPE_OFFERWALL = "offerwall";
    public static final String AD_TYPE_INTERSTITIAL = "interstitial";
    public static final String AD_TYPE_BANNER = "banner";

    public String adType;
    public String adNid;
    public String adSid;
    public String cid;
    public String appStoreName;
    public String appType;
    public String appStoreId;
    public String jumpUid;
    public boolean installed;
    public boolean reported;
}
