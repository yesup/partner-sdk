package com.yesup.partner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.yesup.partner.activities.DetailFragment;
import com.yesup.partner.activities.OfferWallFragment;
import com.yesup.partner.module.DataCenter;
import com.yesup.partner.module.OfferModel;
import com.yesup.partner.tools.AppTool;


public class OfferWallActivity extends AppCompatActivity
    implements OfferWallFragment.OnUserSelectedListener, DetailFragment.OnUserOperationListener {

    private static final int SHOW_FRAGMENT_OFFERWALL = 0;
    private static final int SHOW_FRAGMENT_DETAIL = 1;

    private DataCenter dataCenter = DataCenter.getInstance();
    private int offerIndex = 0;
    private Menu menuBar;
    private int curShowFragment = SHOW_FRAGMENT_OFFERWALL;
    private int curJumpUrlRequestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_wall);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //setTitle("Offer Wall");

        if (findViewById(R.id.OfferWallFragmentContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }
            curShowFragment = SHOW_FRAGMENT_OFFERWALL;
            OfferWallFragment firstFragment = new OfferWallFragment();
            firstFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.OfferWallFragmentContainer, firstFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_top, menu);
        menuBar = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (android.R.id.home == id) {
            onBackPressed();
        } else if (R.id.action_detail_done == id) {
            doProceed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ProgressDialog progressDialog = null;
    public void showProgressDialog(String name) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
        }
        progressDialog = ProgressDialog.show(this, "Prepare Offer", name+"\nis loading ...", true);
    }
    public void hideProgressDialog(int requestId) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (requestId < 0) {
            String showInfo = "Server is busy, please try again!";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(showInfo);
            builder.setNegativeButton("OK", null);
            builder.show();
        } else {
            doProceed();
        }
    }

    @Override
    public void onUserSelected(int opt) {
        Log.v("Meshbean", "Callback onUserSelected:" + opt);
        offerIndex = opt;

        OfferModel offer = dataCenter.getOfferWallAd().getOfferAt(offerIndex);
        if (offer != null) {
            if (offer.getJumpUrl() == null || offer.getJumpUrl().length() <= 0){
                curJumpUrlRequestId = dataCenter.requestOfferJumpUrlFromWebsite(offer);
                if (curJumpUrlRequestId >= 0) {
                    showProgressDialog(offer.getTitle());
                }
            } else {
                doProceed();
            }
        }

        /** Do not display detail fragment
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Index", opt);
        detailFragment.setArguments(bundle);

        curShowFragment = SHOW_FRAGMENT_DETAIL;
        updateMenu();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.OfferWallFragmentContainer, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
         */
    }

    @Override
    public void onUserOperate(int opt) {
        Log.v("Meshbean", "Callback onUserOperate:" + opt);
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    gotoInstall();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }
        }
    };

    protected void updateMenu() {
        MenuItem item = menuBar.findItem(R.id.action_detail_done);
        switch (curShowFragment) {
            case SHOW_FRAGMENT_DETAIL:
                item.setVisible(true);
                break;
            case SHOW_FRAGMENT_OFFERWALL:
                item.setVisible(false);
                break;
            default:
                item.setVisible(false);
                break;
        }
        //invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (curShowFragment) {
            case SHOW_FRAGMENT_DETAIL:
                curShowFragment = SHOW_FRAGMENT_OFFERWALL;
                getSupportFragmentManager().popBackStackImmediate();
                updateMenu();
                break;
            case SHOW_FRAGMENT_OFFERWALL:
                finish();
                break;
            default:
                break;
        }
    }

    private void doProceed() {
        OfferModel offer = dataCenter.getOfferWallAd().getOfferAt(offerIndex);
        String jumpResult = offer.getJumpResult();
        String jumpUrl = offer.getJumpUrl();
        // check if this app has been installed.
        boolean installed = AppTool.isAppInstalled(this, offer.getAppStoreId());
        String showInfo;
        if (installed) {
            showInfo = "This app has been installed in your device!";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(showInfo);
            builder.setNegativeButton("OK", null);
            builder.show();
        }else if (jumpResult == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Downloading data, try it later!");
            builder.setNegativeButton("OK", null);
            builder.show();
        }else if (jumpResult.toLowerCase().equals("ready")) {
            if (jumpUrl != null && jumpUrl.substring(0, 4).toLowerCase().equals("http")) {
                // check if this jump url has been clicked.
                if (offer.isHasClicked()) {
                    showInfo = "Maybe you have installed this app, you will not earn points for more times!\r\nContinue?";
                    // tips yes or no
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(showInfo);
                    builder.setNegativeButton("No", dialogClickListener);
                    builder.setPositiveButton("Yes", dialogClickListener);
                    builder.show();
                } else {
                    /**
                    showInfo = "Are you sure proceed this offer?";
                    // tips yes or no
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(showInfo);
                    builder.setNegativeButton("No", dialogClickListener);
                    builder.setPositiveButton("Yes", dialogClickListener);
                    builder.show();
                     */
                    gotoInstall();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Data Exception!");
                builder.setNegativeButton("OK", null);
                builder.show();
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app has not been sold!");
            builder.setNegativeButton("OK", null);
            builder.show();
        }
    }

    private void gotoInstall() {
        OfferModel offer = dataCenter.getOfferWallAd().getOfferAt(offerIndex);
        if (offer != null) {
            // mark this offer has jumped
            dataCenter.saveOfferHasBeenClicked(offer);
            // jump to ad url
            String jumpUrl = offer.getJumpUrl();
            if (jumpUrl != null && jumpUrl.substring(0, 4).toLowerCase().equals("http")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(jumpUrl));
                startActivity(browserIntent);
            }
        }
    }
}
