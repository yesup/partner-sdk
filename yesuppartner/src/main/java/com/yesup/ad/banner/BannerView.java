package com.yesup.ad.banner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yesup.ad.framework.YesupAdRequest;
import com.yesup.ad.utils.AppTool;
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
        dataCenter.init(getContext());
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
        bannerTextView.setVisibility(GONE);

        mViewPager = (ViewPager)view.findViewById(R.id.view_pager);
        mPagerAdapter = new BannerSlidePagerAdapter();
        //mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new OnBannerPageChangeListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.i(TAG, "OnTouch");
                return false;
            }
        });
    }

    private int mCurClickBannerId = 0;
    private void onBannerClick(int id) {
        BannerModel.Banner banner = bannerController.getBanner(id);
        if (null != banner && !banner.clickUrl.isEmpty()) {
            //Log.d(TAG, "onBannerClick:" + banner.clickUrl);
            // check if this app has been installed.
            boolean thisIsApp,installed;
            if (null != banner.appStoreId && !banner.appStoreId.isEmpty()) {
                thisIsApp = true;
                installed = AppTool.isAppInstalled(getContext(), banner.appStoreId);
            } else {
                thisIsApp = false;
                installed = false;
            }
            //
            if (installed) {
                String showInfo = "This app has been installed in your device!";
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(showInfo);
                builder.setNegativeButton("OK", null);
                builder.show();
            } else {
                if (thisIsApp && (null==banner.clickUrl || banner.clickUrl.isEmpty())) {
                    // request jump url
                    if (!bannerController.isRequestingBannerClickUrl()) {
                        mCurClickBannerId = id;
                        bannerController.requestBannerClickUrl(banner);
                    }
                } else {
                    // click url
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.clickUrl));
                    getContext().startActivity(browserIntent);
                }
            }
        }
    }

    private static final float MOVE_MAX_DISTANCE = (float) 10.1;
    private float touchDownX, touchDownY;
    private View.OnTouchListener onBannerTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HtmlPageView pageView = (HtmlPageView)v;
            int id = pageView.getId();
            //Log.i(TAG, "View ID: "+id);
            float x,y;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    //Log.d(TAG, "ACTION_DOWN:"+touchDownX+"-"+touchDownY);
                    break;
                case MotionEvent.ACTION_UP:
                    x = event.getX();
                    y = event.getY();
                    //Log.d(TAG, "ACTION_UP:"+x+"-"+y);
                    float xx = Math.abs(x - touchDownX);
                    float yy = Math.abs(y - touchDownY);
                    //Log.d(TAG, "MOVE DISTANCE:"+xx+"-"+yy);
                    if (xx < MOVE_MAX_DISTANCE && yy < MOVE_MAX_DISTANCE) {
                        // on click event
                        onBannerClick(id);
                    }
                    break;
                //case MotionEvent.ACTION_MOVE:
                //    x = event.getX();
                //    y = event.getY();
                //    Log.d(TAG, "ACTION_MOVE:"+x+"-"+y);
                //    break;
                default:
                    //Log.d(TAG, "ACTION_ "+event.getActionMasked());
                    break;
            }
            return false;
        }
    };

    /**
     * message handler
     */
    private MessageHandler msgHandler = new MessageHandler();
    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            YesupAdRequest adRequest = (YesupAdRequest)msg.obj;
            switch (msg.what) {
                case Define.MSG_AD_REQUEST_SUCCESSED:
                    if (YesupAdRequest.REQ_TYPE_BANNER_LIST == adRequest.getRequestType()) {
                        //Log.d(TAG, "Request success and notify data set changed");
                        mViewPager.setAdapter(mPagerAdapter);
                        mPagerAdapter.notifyDataSetChanged();
                        if (null != bannerController && bannerController.isDataReady()) {
                            startSwitchTimer();
                        }
                    } else if (YesupAdRequest.REQ_TYPE_BANNER_CLICK_URL == adRequest.getRequestType()) {
                        // on click banner
                        onBannerClick(mCurClickBannerId);
                    }
                    break;
                case Define.MSG_AD_REQUEST_FAILED:
                    //mPagerAdapter.notifyDataSetChanged();
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
            //Log.i(TAG, "instantiateItem: "+position);
            HtmlPageView view = new HtmlPageView(getContext());
            view.setId(position);
            view.setOnTouchListener(onBannerTouchListener);
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
            //Log.i(TAG, "SwitchTask Executed, next pos: "+nextPos);
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
