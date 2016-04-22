package com.yesup.partner.module;


/**
 * Created by derek on 4/21/16.
 */
public class AdResourceFile extends YesupAdBase {
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
    public void initAdData() {
        setAdType(Define.AD_TYPE_INTERSTITIAL_IMAGE);

        setRequestType(YesupAdBase.REQ_TYPE_NORMAL_FILE);
        setRequestMethod("GET");
        setDownloadUrl(resourceUrl);
        setSaveFileName(localPath);
    }

    @Override
    public boolean parseResponseDataWithJson() {
        return true;
    }
}
