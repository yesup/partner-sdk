package com.yesup.ad.framework;


/**
 * Created by derek on 6/20/16.
 */
public class AdData {

    private AdType adType;
    private long dbId;

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public void setAdType(AdType adType) {
        this.adType = adType;
    }

    public AdType getAdType() {
        return adType;
    }

    public enum AdType {
        AdBanner, AdIntersitialImage, AdIntersitialPage, AdOfferWall
    }
}
