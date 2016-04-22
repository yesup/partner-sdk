package com.yesup.sdk.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yesup.partner.YesupAd;
import com.yesup.partner.interstitial.IInterstitialListener;
import com.yesup.partner.interstitial.PartnerBaseView;
import com.yesup.partner.module.Define;
import com.yesup.partner.module.PartnerAdConfig;

import java.util.ArrayList;


public class DemoActivity extends AppCompatActivity implements IInterstitialListener {
    private final String TAG = "DemoActivity";
    private YesupAd yesupAd;
    private ArrayList<PartnerAdConfig.Zone> zoneList;

    private MyStatusView statusView;
    private String statusMsg = "  Message";
    private int progressPos = 0;
    private CountDownTimer updateTimer;
    private Animation operatingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // new yesup ad instance
        yesupAd = new YesupAd(this);
        Log.v(TAG, "YesupAD Version:"+yesupAd.getVersion());
        yesupAd.setDebugMode(true);
        statusView = new MyStatusView(this);

        zoneList = yesupAd.getAllZoneList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        ListView listZone = (ListView)findViewById(R.id.list_zone);
        ZoneListAdapter adapter = new ZoneListAdapter(this);
        listZone.setAdapter(adapter);
        listZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subId = "123123";
                int zoneId = zoneList.get(position).id;
                int adType = yesupAd.getAdTypeByZoneId(zoneId);
                switch (adType) {
                    case Define.AD_TYPE_OFFER_WALL:
                        yesupAd.showOfferWall(subId, zoneId);
                        break;
                    case Define.AD_TYPE_INTERSTITIAL_WEBPAGE:
                        yesupAd.showInterstitial(subId, zoneId, true, true, statusView);
                        break;
                    case Define.AD_TYPE_INTERSTITIAL_IMAGE:
                        yesupAd.showInterstitial(subId, zoneId, false, true, statusView);
                        break;
                    default:
                        Snackbar.make(view, "Unknown AD type", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        yesupAd.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ViewHolder holder;
    @Override
    public void onInterstitialShown() {
        Log.d(TAG, "On Show");
        if (holder != null) {
            holder.imageView.startAnimation(operatingAnim);
        }
        updateTimer = new CountDownTimer(10*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int)millisUntilFinished/1000;
                Log.i(TAG, "    Left time: "+seconds);
                switch (seconds) {
                    case 9:
                        statusMsg = "Connecting to server ...";
                        break;
                    case 7:
                        statusMsg = "Connect to server OK.";
                        break;
                    case 6:
                        statusMsg = "Sending request ...";
                        break;
                    case 4:
                        statusMsg = "Receiving response ...";
                        break;
                    case 2:
                        statusMsg = "Completed.";
                        break;
                    default:
                        break;
                }
                progressPos = (10-seconds)*10;
                statusView.updateView();
            }
            @Override
            public void onFinish() {
                progressPos = 100;
                statusView.updateView();
                //interstitial.closeAfterCredited();
                //interstitial.closeNow();
            }
        }.start();
    }

    @Override
    public void onInterstitialCredited() {
        Log.d(TAG, "On Credited");
    }

    @Override
    public void onInterstitialClosed() {
        Log.d(TAG, "On Closed");
        if (holder != null && operatingAnim != null && operatingAnim.hasStarted()) {
            holder.imageView.clearAnimation();
        }
        if (updateTimer != null) {
            updateTimer.cancel();
        }

        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    public class ViewHolder {
        public ImageView imageView;
        public ProgressBar progressBar;
        public TextView msg;
        public Button btn;
    }

    class MyStatusView extends PartnerBaseView {

        public MyStatusView(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(View convertView, ViewGroup parent) {
            //ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.ad_status_view, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
                operatingAnim = AnimationUtils.loadAnimation(convertView.getContext(), R.anim.yesup_loading);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);

                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
                holder.msg = (TextView)convertView.findViewById(R.id.text_status);
                holder.btn = (Button)convertView.findViewById(R.id.button);
                holder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "MyStatusView button clicked.");
                        if (updateTimer != null) {
                            updateTimer.cancel();
                        }
                        if (holder != null && operatingAnim != null && operatingAnim.hasStarted()) {
                            holder.imageView.clearAnimation();
                        }
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            // set display content
            holder.progressBar.setProgress(progressPos);
            holder.msg.setText(statusMsg);

            return convertView;
        }
    }

    public class ZoneViewHolder {
        public TextView adType;
        public TextView zoneId;
        public TextView show;
    }

    private class ZoneListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ZoneListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return zoneList.size();
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
            ZoneViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_zone_list, null);
                holder = new ZoneViewHolder();
                holder.adType = (TextView)convertView.findViewById(R.id.text_adtype);
                holder.zoneId = (TextView)convertView.findViewById(R.id.text_zoneid);
                holder.show = (TextView)convertView.findViewById(R.id.text_show);
                convertView.setTag(holder);
            } else {
                holder = (ZoneViewHolder)convertView.getTag();
            }
            // set display content
            int zoneId = zoneList.get(position).id;
            int adType = yesupAd.getAdTypeByZoneId(zoneId);
            switch (adType) {
                case Define.AD_TYPE_OFFER_WALL:
                    holder.adType.setText("OFFER WALL");
                    break;
                case Define.AD_TYPE_INTERSTITIAL_WEBPAGE:
                    holder.adType.setText("PAGE INTERSTITIAL");
                    break;
                case Define.AD_TYPE_INTERSTITIAL_IMAGE:
                    holder.adType.setText("IMAGE INTERSTITIAL");
                    break;
                default:
                    holder.adType.setText("UNKNOWN");
                    break;
            }
            holder.zoneId.setText( Integer.toString(zoneId) );
            holder.show.setText("SHOW");

            return convertView;
        }
    }

}
