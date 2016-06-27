package com.yesup.ad.framework;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yesup.ad.framework.YesupHttpRequest.Listener;

/**
 * Created by derek on 6/20/16.
 */
public abstract class AdController {
    protected YesupAdRequest.Listener reqListener;
    private Handler exHandler;
    protected Context context;
    protected AdConfig adConfig;
    protected AdZone adZone;
    protected String subId;
    protected String option1;
    protected String option2;
    protected String option3;

    protected void onInit(Context context, AdConfig config, AdZone zone, String subId,
                          Handler handler, String opt1, String opt2, String opt3) {
        this.context = context;
        this.exHandler = handler;
        this.adConfig = config;
        this.adZone = zone;
        this.subId = subId;
        this.option1 = opt1;
        this.option2 = opt2;
        this.option3 = opt3;
        this.reqListener = new RequestListener();
    }

    protected void setExHandler(Handler handler) {
        this.exHandler = handler;
    }

    protected void initRequestConfig(YesupAdRequest request) {
        request.initAdConfig(context, adConfig, adZone, subId, reqListener, option1, option2, option3);
    }

    protected abstract void onResume();

    protected abstract void onPause();

    protected abstract void onRequestProgressed(YesupAdRequest adRequest, int percent);

    protected abstract void onRequestSuccess(YesupAdRequest adRequest, int result);

    protected abstract void onRequestFailed(YesupAdRequest adRequest, int result);

    protected boolean dataReady = false;

    public boolean isDataReady() {
        return dataReady;
    }

    public void setDataReady(boolean dataReady) {
        this.dataReady = dataReady;
    }

    protected void messageView(int msg, int arg1, int arg2, Object obj) {
        if (null != exHandler) {
            Message newMsg = new Message();
            newMsg.what = msg;
            newMsg.arg1 = arg1;
            newMsg.arg2 = arg2;
            newMsg.obj = obj;
            exHandler.sendMessage(newMsg);
        }
    }

    public class RequestListener extends Listener {
        @Override
        public void onProgressed(YesupHttpRequest request, int percent) {
            onRequestProgressed((YesupAdRequest)request, percent);
        }
        @Override
        public void onCompleted(YesupHttpRequest request, int result) {
            if (YesupHttpRequest.STATUS_SUCCESSED == result) {
                onRequestSuccess((YesupAdRequest) request, result);
            } else {
                onRequestFailed((YesupAdRequest) request, result);
            }
        }
    }

}
