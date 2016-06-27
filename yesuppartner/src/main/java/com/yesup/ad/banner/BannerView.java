package com.yesup.ad.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yesup.ad.view.HtmlPageView;
import com.yesup.partner.R;
import com.yesup.ad.framework.Define;
import com.yesup.ad.framework.DataCenter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by derek on 6/14/16.
 */
public class BannerView extends FrameLayout {
    private final String TAG = "BannerView";
    private String yesupBannerZoneId;
    private BannerController bannerController;

    private TextView bannerTextView;
    private ViewPager mViewPager;
    private BannerSlidePagerAdapter mPagerAdapter;

    /**
     * be used for new in code
     * @param context
     */
    public BannerView(Context context) {
        super(context);
        Log.i(TAG, "New YesupBannerAd 1");
        initBannerView(context);
    }

    /**
     * be used in layout.xml
     * @param context
     * @param attrs
     */
    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "New YesupBannerAd 2");
        initAttrs(context, attrs);
        initBannerView(context);
    }

    /**
     * be used in layout.xml
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "New YesupBannerAd 3");
        initAttrs(context, attrs);
        initBannerView(context);
    }

    public String getYesupBannerId() {
        return yesupBannerZoneId;
    }

    public BannerView setYesupBannerId(String yesupBannerId) {
        this.yesupBannerZoneId = yesupBannerId;
        return this;
    }

    public void onResume() {
        if (null != bannerController) {
            bannerController.onResume();
            startSwitchTimer();
        }
    }

    public void onPause() {
        if (null != bannerController) {
            bannerController.onPause();
            stopSwitchTimer();
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.YesupBannerAd);
        if (null != a) {
            yesupBannerZoneId = a.getString(R.styleable.YesupBannerAd_yesup_banner_zone_id);
        }
    }

    private void initBannerAd() {
        DataCenter dataCenter = DataCenter.getInstance();
        int adZoneId = Integer.parseInt(yesupBannerZoneId);
        bannerController = (BannerController)dataCenter.getAdController(adZoneId, msgHandler);
    }

    private void initBannerView(Context context) {
        initBannerAd();
        Log.i(TAG, "Init View");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.yesup_ad_banner, this);

        bannerTextView = (TextView)view.findViewById(R.id.yesupTextView);
        bannerTextView.setText("Yesup banner AD:"+yesupBannerZoneId);

        mViewPager = (ViewPager)view.findViewById(R.id.view_pager);
        mPagerAdapter = new BannerSlidePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new OnBannerPageChangeListener());
        bannerController.setBannerSlidePagerAdapter(mPagerAdapter);
    }

    /**
     * message handler
     */
    private MessageHandler msgHandler = new MessageHandler();
    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Define.MSG_AD_REQUEST_SUCCESSED:
                    if (null != bannerController && bannerController.isDataReady()) {
                        startSwitchTimer();
                    }
                    break;
                case Define.MSG_AD_REQUEST_FAILED:
                    Log.d(TAG, "Request failed");
                    break;
                case Define.MSG_AD_REQUEST_IMPRESSED:
                case Define.MSG_AD_REQUEST_PROGRESSED:
                    break;
                case Define.MSG_BANNERVIEW_SLIDE_NEXT:
                    mViewPager.setCurrentItem(msg.arg1);
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    public class BannerSlidePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            int count = 0;
            if (null != bannerController && bannerController.isDataReady()) {
                count = bannerController.getBannerSize();
            }
            //Log.i(TAG, "Page count: "+count);
            return count;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            //HtmlPageView pageView = (HtmlPageView)object;
            //Log.i(TAG, "isViewFromObject: "+pageView.getId());
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i(TAG, "instantiateItem: "+position);
            HtmlPageView view = new HtmlPageView(getContext());
            view.setId(position);
            if (null != bannerController && bannerController.isDataReady()) {
                BannerModel.Banner banner = bannerController.getBanner(position);
                view.setUrl(banner.imageUrl, banner.clickUrl);
                view.onResume();
            }
            container.addView(view);
            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //Log.i(TAG, "destroyItem: "+position);
            container.removeView((View)object);
        }
    }
    private class OnBannerPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            //Log.i(TAG, "onPageSelected: "+position);
            if (null != bannerController) {
                bannerController.setCurBannerIndex(position);
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private Timer mSwitchTimer;
    private void startSwitchTimer() {
        if (null != bannerController && bannerController.getBannerSize() > 0) {
            mSwitchTimer = new Timer();
            mSwitchTimer.schedule(new SwitchTask(), 60000, 60000);
        }
    }
    private void stopSwitchTimer() {
        if (null != mSwitchTimer) {
            mSwitchTimer.cancel();
        }
    }
    private class SwitchTask extends TimerTask {
        @Override
        public void run() {
            int nextPos = bannerController.getNextBannerIndex();
            Log.i(TAG, "SwitchTask Executed, next pos: "+nextPos);
            messageToView(Define.MSG_BANNERVIEW_SLIDE_NEXT, nextPos, 0);
        }
    }
    protected void messageToView(int msg, int arg1, int arg2) {
        if (null != msgHandler) {
            Message newMsg = new Message();
            newMsg.what = msg;
            newMsg.arg1 = arg1;
            newMsg.arg2 = arg2;
            newMsg.obj = null;
            msgHandler.sendMessage(newMsg);
        }
    }
}