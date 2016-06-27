package com.yesup.ad.framework;


/**
 * Created by derek on 4/21/16.
 */
public class AdResourceFile extends YesupAdRequest {
    private String resourceUrl;
    private String localPath;

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    @Override
    public void initRequestData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_IMAGE);

        setRequestType(YesupAdRequest.REQ_TYPE_NORMAL_FILE);
        setRequestMethod("GET");
        setDownloadUrl(resourceUrl);
        setSaveFileName(localPath);
    }

    @Override
    public boolean parseResponseDataWithJson() {
        return true;
    }

    @Override
    public boolean isReady() {
        return false;
    }

}
