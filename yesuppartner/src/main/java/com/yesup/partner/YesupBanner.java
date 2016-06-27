package com.yesup.partner;

import android.content.Context;
import android.util.AttributeSet;

import com.yesup.ad.banner.BannerView;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;

/**
 * Created by derek on 6/27/16.
 */
public class YesupBanner extends BannerView {

    public YesupBanner(Context context) {
        super(context);
    }

    public YesupBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YesupBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getVersion() {
        return Define.SDK_VERSION;
    }

    public void setDebugMode(boolean debugMode) {
        DataCenter center = DataCenter.getInstance();
        center.setDebugMode(debugMode);
    }

    public void setSubId(String subId) {
        DataCenter center = DataCenter.getInstance();
        center.setSubId(subId);
    }

    public void setCuid(String cuid) {
        DataCenter center = DataCenter.getInstance();
        center.setCuid(cuid);
    }

    public void setOption(String opt1, String opt2, String opt3) {
        DataCenter center = DataCenter.getInstance();
        center.setOption(opt1, opt2, opt3);
    }

    @Override
    public void onResume() {
        DataCenter center = DataCenter.getInstance();
        center.onResume();
        super.onResume();
    }
}
