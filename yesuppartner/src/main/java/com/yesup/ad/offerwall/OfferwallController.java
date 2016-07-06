package com.yesup.ad.offerwall;

import android.content.Context;
import android.os.Handler;

import com.yesup.ad.framework.AdConfig;
import com.yesup.ad.framework.AdController;
import com.yesup.ad.framework.AdResourceFile;
import com.yesup.ad.framework.AdZone;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.DownloadManagerLite;
import com.yesup.ad.framework.YesupAdRequest;

import java.util.List;

/**
 * Created by derek on 6/21/16.
 */
public class OfferwallController extends AdController {
    private OfferWallRequest offerWallRequest = new OfferWallRequest();
    private boolean offerWallIsUpdating = false;

    @Override
    protected void onInit(Context context, AdConfig config, AdZone zone, String subId,
                          Handler handler, String opt1, String opt2, String opt3) {
        super.onInit(context, config, zone, subId, handler, opt1, opt2, opt3);
        initRequestConfig(offerWallRequest);
    }

    @Override
    protected void onResume() {
        DownloadManagerLite downloader = DataCenter.getInstance().getDownloadManager();
        if (!offerWallIsUpdating && offerWallRequest.offerPageHasExpired(adZone.id)) {
            offerWallIsUpdating = true;
            offerWallRequest.initRequestData();
            offerWallRequest.sendRequest(downloader);
        }
    }

    @Override
    protected void onPause() {
    }

    @Override
    protected void onRequestProgressed(YesupAdRequest adRequest, int percent) {
    }

    @Override
    protected void onRequestSuccess(YesupAdRequest adRequest, int result) {
        if (YesupAdRequest.REQ_TYPE_OFFER_WALL == adRequest.getRequestType()) {
            setDataReady(true);
            messageView(Define.MSG_AD_REQUEST_SUCCESSED, YesupAdRequest.REQ_TYPE_OFFER_WALL, 0, adRequest);
            offerWallIsUpdating = false;
        } else if (YesupAdRequest.REQ_TYPE_OFFER_ICON == adRequest.getRequestType()) {
            AdResourceFile adResourceFile = (AdResourceFile)adRequest;
            // icon download completed
            OfferPageModel offerPageModel = offerWallRequest.getOfferPage();
            if (offerPageModel != null && offerPageModel.getList() != null) {
                List<OfferModel> offerList = offerPageModel.getList();
                int index = adRequest.getRequestId();
                OfferModel offer = offerList.get(index);
                if (offer != null) {
                    offer.setLocalIconPath(adResourceFile.getLocalPath());
                    adRequest.getDbHelper().updateOfferLocalIconPath(offer);
                    messageView(Define.MSG_AD_REQUEST_SUCCESSED, YesupAdRequest.REQ_TYPE_OFFER_ICON, adRequest.getRequestId(), adRequest);
                }
            }
        } else if (YesupAdRequest.REQ_TYPE_OFFER_JUMPURL == adRequest.getRequestType()) {
            messageView(Define.MSG_AD_REQUEST_SUCCESSED, YesupAdRequest.REQ_TYPE_OFFER_JUMPURL, adRequest.getRequestId(), adRequest);
        }
    }

    @Override
    protected void onRequestFailed(YesupAdRequest adRequest, int result) {
        if (YesupAdRequest.REQ_TYPE_OFFER_WALL == adRequest.getRequestType()) {
            messageView(Define.MSG_AD_REQUEST_FAILED, YesupAdRequest.REQ_TYPE_OFFER_WALL, result, adRequest);
            offerWallIsUpdating = false;
        } else if (YesupAdRequest.REQ_TYPE_OFFER_ICON == adRequest.getRequestType()) {
            messageView(Define.MSG_AD_REQUEST_FAILED, YesupAdRequest.REQ_TYPE_OFFER_ICON, result, adRequest);
        } else if (YesupAdRequest.REQ_TYPE_OFFER_JUMPURL == adRequest.getRequestType()) {
            messageView(Define.MSG_AD_REQUEST_FAILED, YesupAdRequest.REQ_TYPE_OFFER_JUMPURL, adRequest.getRequestId(), adRequest);
        }
    }

    public OfferWallRequest getOfferWallRequest() {
        return offerWallRequest;
    }

    public boolean doesOfferPageHasExpired() {
        return offerWallRequest.offerPageHasExpired(adZone.id);
    }

    public boolean offerPageHasLoaded() {
        if (offerWallRequest == null || offerWallRequest.getOfferPage() == null) {
            return false;
        }else {
            return true;
        }
    }

    public int requestOfferJumpUrlFromWebsite(OfferModel offer) {
        OfferJumpUrlAd jumpUrlAd = new OfferJumpUrlAd();
        jumpUrlAd.setRequestId(offer.getLocalReference());
        jumpUrlAd.setOffer(offer);
        jumpUrlAd.setCuid(DataCenter.getInstance().getCuid());
        jumpUrlAd.initAdConfig(context, adConfig, offerWallRequest.getAdZone(), subId, reqListener, option1, option2, option3);
        jumpUrlAd.initRequestData();
        jumpUrlAd.sendRequest(DataCenter.getInstance().getDownloadManager());
        return 0;
    }

    public boolean saveOfferHasBeenClicked(OfferModel offer) {
        offer.setHasClicked(true);
        boolean success = offerWallRequest.getDbHelper().updateOfferClicked(offer);
        if (success && offer.getConvertCondition().toLowerCase().equals("install")) {
            // save this record to offers_clicked table
            if (!offerWallRequest.getDbHelper().offerClickedIsExist(offer)) {
                success = offerWallRequest.getDbHelper().addOfferClicked(offer);
            }
        }
        return success;
    }

}
