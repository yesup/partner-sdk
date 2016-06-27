package com.yesup.ad.interstitial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yesup.partner.R;

/**
 * Created by derek on 4/14/16.
 */
public class PartnerSampleView extends PartnerBaseView {
    //private String TAG = "YesupBaseView";
    protected LayoutInflater mInflater;
    private View view;
    private ViewGroup parentView;

    public PartnerSampleView(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public View getView(View convertView, ViewGroup parentView) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.yesup_partner_base, null);
        }
        return convertView;
    }

    public void saveView(View v) {
        this.view = v;
    }

    public void saveParentView(ViewGroup parent) {
        parentView = parent;
    }

    public View loadView() {
        return this.view;
    }

    public void updateView() {
        if (view != null) {
            getView(view, parentView);
        }
    }
}
