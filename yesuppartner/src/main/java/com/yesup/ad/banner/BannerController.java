package com.yesup.ad.banner;

import android.content.Context;
import android.os.Handler;

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
    private BannerRequest bannerRequest = new BannerRequest();
    private int mBannerSize = 0;
    private int mCurBannerIndex = 0;
    private HashMap<Integer, BannerModel.Banner> bannerMap = new HashMap<>();
    // in the view
    BannerView.BannerSlidePagerAdapter mPagerAdapter;

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
        } else {
            if (!bannerRequest.isBusy()) {
                bannerRequest.initRequestData();
                bannerRequest.sendRequest(downloader);
            }
        }
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
        if (bannerRequest.isReady()) {
            initBannerMap();
            messageView(Define.MSG_AD_REQUEST_SUCCESSED, 0, 0, adRequest);
        } else {
            setDataReady(false);
            messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
        }
    }

    @Override
    protected void onRequestFailed(YesupAdRequest adRequest, int result) {
        setDataReady(false);
        messageView(Define.MSG_AD_REQUEST_FAILED, result, 0, adRequest);
    }

    public void setBannerSlidePagerAdapter(BannerView.BannerSlidePagerAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
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
        setDataReady(true);
        mPagerAdapter.notifyDataSetChanged();
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
}
