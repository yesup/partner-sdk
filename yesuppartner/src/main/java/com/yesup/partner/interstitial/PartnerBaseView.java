package com.yesup.partner.interstitial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by derek on 4/14/16.
 */
public abstract class PartnerBaseView {
    //private String TAG = "PartnerBaseView";
    protected LayoutInflater mInflater;
    private View view;
    private ViewGroup parentView;

    public abstract View getView(View convertView, ViewGroup parentView);

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
