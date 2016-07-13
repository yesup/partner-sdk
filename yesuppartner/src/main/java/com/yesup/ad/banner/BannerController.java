package com.yesup.ad.banner;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.yesup.ad.framework.AdClickModel;
import com.yesup.ad.framework.AdConfig;
import com.yesup.ad.framework.AdController;
import com.yesup.ad.framework.AdZone;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.DownloadManagerLite;
import com.yesup.ad.framework.YesupAdRequest;

import java.util.HashMap;

/**
 * Created by derek on 6/20/16.
 */
public class BannerController extends AdController {
    private String TAG = "BannerController";
    private BannerRequest bannerRequest = new BannerRequest();
    private int mBannerSize = 0;
    private int mCurBannerIndex = 0;
    private HashMap<Integer, BannerModel.Banner> bannerMap = new HashMap<>();

    @Override
    protected void onInit(Context context, AdConfig config, AdZone zone, String subId,
                          Handler handler, String opt1, String opt2, String opt3) {
        super.onInit(context, config, zone, subId, handler, opt1, opt2, opt3);
        initRequestConfig(bannerRequest);
        setDataReady(false);
    }

    @Override
    protected void onResume() {
        DownloadManagerLite downloader = DataCenter.getInstance().getDownloadManager();
        if (bannerRequest.isReady()) {
            messageView(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0, bannerRequest);
            setDataReady(true);
        } else {
            setDataReady(false);
            if (!bannerRequest.isBusy()) {
                bannerRequest.initRequestData();
                bannerRequest.sendRequest(downloader);
            }
        }
    }

    public boolean isRequestingBannerClickUrl() {
        return mRequestingBannerClickUrl;
    }
    private boolean mRequestingBannerClickUrl = false;
    public void requestBannerClickUrl(BannerModel.Banner banner) {
        mRequestingBannerClickUrl = true;
        DownloadManagerLite downloader = DataCenter.getInstance().getDownloadManager();
        ClickUrlRequest request = new ClickUrlRequest();
        request.setBanner(banner);
        initRequestConfig(request);
        request.initRequestData();
        request.sendRequest(downloader);
    }

    @Override
    protected void onPause() {
        offsetBannerMap();
    }

    @Override
    protected void onRequestProgressed(YesupAdRequest adRequest, int percent) {
    }

    @Override
    protected void onRequestSuccess(YesupAdRequest adRequest, int result) {
        if (YesupAdRequest.REQ_TYPE_BANNER_LIST == adRequest.getRequestType()) {
            initBannerMap();
            if (bannerRequest.isReady()) {
                Log.d(TAG, "onRequestSuccess set data ready");
                setDataReady(true);
                Log.d(TAG, "onRequestSuccess and send message");
                messageView(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0, adRequest);
            } else {
                setDataReady(false);
                messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
            }
        } else if (YesupAdRequest.REQ_TYPE_BANNER_CLICK_URL == adRequest.getRequestType()) {
            saveBannerHasBeenClicked();
            mRequestingBannerClickUrl = false;
            messageView(Define.MSG_AD_REQUEST_SUCCESSED, result, 0, adRequest);
        }
    }

    @Override
    protected void onRequestFailed(YesupAdRequest adRequest, int result) {
        if (YesupAdRequest.REQ_TYPE_BANNER_LIST == adRequest.getRequestType()) {
            setDataReady(false);
            initBannerMap();
            messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
        } else if (YesupAdRequest.REQ_TYPE_BANNER_CLICK_URL == adRequest.getRequestType()) {
            mRequestingBannerClickUrl = false;
            messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
        }
    }

    private void initBannerMap() {
        BannerModel bannerModel = bannerRequest.getBannerModel();
        mBannerSize = bannerModel.bannerList.size();
        if (mBannerSize > 0) {
            mCurBannerIndex = 0;
            for (int i=0; i<mBannerSize; i++) {
                bannerMap.put(i, bannerModel.bannerList.get(i));
            }
        }
    }

    private void offsetBannerMap() {
        int size = bannerMap.size();
        if (size > 1) {
            BannerModel.Banner firstBanner = bannerMap.get(0);
            for (int i=0; i<size; i++) {
                BannerModel.Banner banner;
                if (i < size-1) {
                    banner = bannerMap.get(i+1);
                    bannerMap.put(i, banner);
                }
            }
            bannerMap.put(size-1, firstBanner);
        }
    }

    public BannerModel.Banner getBanner(int index) {
        if (index >= 0 && index < bannerMap.size()) {
            return bannerMap.get(index);
        } else {
            return null;
        }
    }

    public void setCurBannerIndex(int index) {
        if (index >= 0 && index < mBannerSize) {
            mCurBannerIndex = index;
        }
    }

    public int getNextBannerIndex() {
        mCurBannerIndex++;
        if (mCurBannerIndex >= mBannerSize) {
            mCurBannerIndex = 0;
        }
        return mCurBannerIndex;
    }

    public int getBannerSize() {
        return mBannerSize;
    }

    public void saveBannerHasBeenClicked() {
        BannerModel.Banner banner = bannerMap.get(mCurBannerIndex);
        if (null != banner.appStoreId && !banner.appStoreId.isEmpty()) {
            AdClickModel click = new AdClickModel();
            click.adType = AdClickModel.AD_TYPE_BANNER;
            click.adNid = String.valueOf(banner.adnid);
            click.adSid = String.valueOf(banner.adsid);
            click.cid = String.valueOf(banner.cid);
            click.appStoreName = "";
            click.appType = banner.appType;
            click.appStoreId = banner.appStoreId;
            click.jumpUid = banner.clickUid;

            if (!bannerRequest.getDbHelper().adClickedIsExist(click)) {
                bannerRequest.getDbHelper().addAdClicked(click);
            }
        }
    }
}
