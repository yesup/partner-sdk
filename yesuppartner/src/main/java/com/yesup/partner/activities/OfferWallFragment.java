package com.yesup.partner.activities;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yesup.partner.OfferWallActivity;
import com.yesup.partner.R;
import com.yesup.partner.module.DataCenter;
import com.yesup.partner.module.Define;
import com.yesup.partner.module.OfferModel;
import com.yesup.partner.module.OfferPageModel;
import com.yesup.partner.module.OfferWallAd;
import com.yesup.partner.module.YesupAdBase;

import java.io.File;

/**
 * Created by derek on 3/4/16.
 */
public class OfferWallFragment extends Fragment {
    private static final String TAG = "OfferWallFragment";

    private DataCenter dataCenter = DataCenter.getInstance();
    private int zoneId;
    private int listCount = 0;
    private ListView dataListView;
    private MessageHandler msgHandler = new MessageHandler();

    // container Activity must implement this interface
    OnUserSelectedListener mCallback;
    public interface OnUserSelectedListener {
        void onUserSelected(int opt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.yesup_fragment_offer_wall, container, false);

        // init data center
        dataCenter.init(getActivity());
        OfferWallAd offerWallAd = dataCenter.getOfferWallAd();
        if (dataCenter.offerPageHasLoaded()) {
            listCount = offerWallAd.getOfferCount();
        } else {
            if (offerWallAd == null) {
                listCount = 0;
            } else {
                listCount = offerWallAd.loadOfferListFromLocalDatabase();
            }
        }

        dataListView = (ListView) view.findViewById(R.id.dataListView);
        MyAdapter adapter = new MyAdapter(getActivity());
        dataListView.setAdapter(adapter);
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // jump to detail fragment
                mCallback.onUserSelected(position);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This make sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnUserSelectedListener)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    +" must implement OnUserOperationListener");
        }

        Bundle extras = getArguments();
        if (extras != null) {
            zoneId = extras.getInt("ZONE_ID");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dataCenter.setMsgHandler(msgHandler);
        // if it's expired
        if (listCount <= 0 || dataCenter.getOfferWallAd().offerPageHasExpired(zoneId)) {
            // set 0 to avoid access null data
            listCount = 0;
            // reload data from website
            dataCenter.requestOfferWallFromWebsite(zoneId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dataCenter.setMsgHandler(null);
    }


    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private OfferWallPartnerHelper defaultPartnerHelper;
        private int incentRate = 100;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);

            defaultPartnerHelper = dataCenter.getOfferWallPartnerHelper();
            if (defaultPartnerHelper == null) {
                defaultPartnerHelper = new OfferWallPartnerHelper(getContext());
            }
        }

        @Override
        public int getCount() {
            OfferWallAd offerWallAd = dataCenter.getOfferWallAd();
            if (offerWallAd != null) {
                OfferPageModel offerPage = offerWallAd.getOfferPage();
                if (offerPage != null) {
                    incentRate = offerPage.getIncentRate();
                }
            }
            Log.i(TAG, "UpdateList getCount " + listCount);
            return listCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.yesup_item_data_list, null);
                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.ItemImageLeft);
                holder.title = (TextView)convertView.findViewById(R.id.ItemMainTitle);
                holder.description = (TextView)convertView.findViewById(R.id.ItemDescription);
                holder.ratingBar = (RatingBar)convertView.findViewById(R.id.ratingBar);
                holder.reward = (Button)convertView.findViewById(R.id.BtnReward);
                holder.reward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallback.onUserSelected(position);
                    }
                });
                holder.imageCoins = (ImageView)convertView.findViewById(R.id.ImageCoins);
                convertView.setTag(holder);
                float z = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    z = holder.reward.getElevation() + 100.0f;
                    holder.imageCoins.setZ(z);
                }
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            // get display data
            OfferModel offer = dataCenter.getOfferWallAd().getOfferAt(position);
            if (offer == null) {
                Log.i(TAG, "UpdateItemView " + position + " Offer:NULL");
                return convertView;
            } else  {
                Log.i(TAG, "UpdateItemView " + position + " Image:" + offer.getLocalIconPath());
            }
            // set display content
            String iconPath = offer.getLocalIconPath();
            if (iconPath != null && !iconPath.isEmpty()) {
                File imageFile = new File(iconPath);
                if (imageFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    Drawable drawable = new BitmapDrawable(getResources(), bmp);
                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.image.setBackgroundDrawable(drawable);
                    } else {
                        holder.image.setBackground(drawable);
                    }
                } else {
                    holder.image.setBackgroundResource(R.drawable.yesup_placeholder);
                }
            } else {
                holder.image.setBackgroundResource(R.drawable.yesup_placeholder);
            }
            holder.image.setImageResource(R.drawable.yesup_appicon_shadow);
            holder.title.setText(offer.getTitle());
            holder.description.setText( offer.getShortDesc() );
            float rate = offer.getRate() / 20.0f;
            holder.ratingBar.setRating(rate);
            if (offer.isRecommend()) {
                holder.reward.setBackgroundResource(R.drawable.yesup_reward_btn2);
            } else {
                holder.reward.setBackgroundResource(R.drawable.yesup_reward_btn);
            }
            // reward
            String str;
            str = defaultPartnerHelper.calculateReward(offer.getPayout(), incentRate);
            Drawable drawable = defaultPartnerHelper.getRewardIcon();
            if (str == null || str.isEmpty()) {
                str = "Install  ";
                holder.imageCoins.setVisibility(View.GONE);
            } else {
                holder.imageCoins.setVisibility(View.VISIBLE);
                if (drawable == null) {
                    holder.imageCoins.setImageResource(R.drawable.yesup_coins);
                } else {
                    holder.imageCoins.setImageDrawable(drawable);
                }
            }
            holder.reward.setText(str);

            return convertView;
        }
    }

    public class ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView description;
        public RatingBar ratingBar;
        public Button reward;
        public ImageView imageCoins;
    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Define.MSG_AD_REQUEST_SUCCESSED:
                    if (YesupAdBase.REQ_TYPE_OFFER_WALL == msg.arg1) {
                        // update OfferWall success
                        listCount = dataCenter.getOfferWallAd().getOfferCount();
                        dataListView.invalidateViews();
                        Log.i(TAG, "Offer Wall Download Completed.");
                    } else if (YesupAdBase.REQ_TYPE_OFFER_ICON == msg.arg1) {
                        int itemIndex = msg.arg2;
                        int first = dataListView.getFirstVisiblePosition();
                        int last = dataListView.getLastVisiblePosition();
                        if (itemIndex >= first && itemIndex <= last) {
                            View v = dataListView.getChildAt(itemIndex - first);
                            ViewHolder holder = (ViewHolder)v.getTag();
                            if (holder != null) {
                                // get display data
                                OfferModel offer = dataCenter.getOfferWallAd().getOfferAt(itemIndex);
                                // set display content
                                File imageFile = new File(offer.getLocalIconPath());
                                if (imageFile.exists()) {
                                    Log.i(TAG, "ICON:" + offer.getLocalIconPath());
                                    Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                                    Drawable drawable = new BitmapDrawable(getResources(), bmp);
                                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                        holder.image.setBackgroundDrawable(drawable);
                                    } else {
                                        holder.image.setBackground(drawable);
                                    }
                                    holder.image.setImageResource(R.drawable.yesup_appicon_shadow);
                                }
                                Log.i(TAG, "Download Completed, update " + itemIndex + " item of view.");
                            }
                        }
                        Log.i(TAG, "Offer Icon Download Completed.");
                    } else if (YesupAdBase.REQ_TYPE_OFFER_JUMPURL == msg.arg1) {
                        // get offer jump ok
                        OfferWallActivity oa = (OfferWallActivity)getActivity();
                        oa.hideProgressDialog(0);
                        Log.i(TAG, "Offer Jump Download Completed.");
                    }
                    break;
                case Define.MSG_AD_REQUEST_FAILED:
                    if (YesupAdBase.REQ_TYPE_OFFER_WALL == msg.arg1) {
                        // update OfferWall failed
                        Log.i(TAG, "Offer Wall Download Failed.");
                    } else if (YesupAdBase.REQ_TYPE_OFFER_ICON == msg.arg1) {
                        Log.i(TAG, "Offer Icon Download Failed.");
                    } else if (YesupAdBase.REQ_TYPE_OFFER_JUMPURL == msg.arg1) {
                        OfferWallActivity oa = (OfferWallActivity)getActivity();
                        oa.hideProgressDialog(msg.arg2);
                        Log.i(TAG, "Offer Jump Download Failed.");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
