package com.yesup.ad.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.yesup.ad.framework.Define;
import com.yesup.ad.offerwall.OfferModel;

import java.io.File;


/**
 * Created by derek on 3/1/16.
 */
public class AppTool {

    public static final String makeUserAgent() {
        String ua = "CpxCenterSDK/" + Define.SDK_VERSION
                + " (Linux; Android " + Build.VERSION.RELEASE + "; "
                + Build.MODEL + " Build/" + Build.ID +")";
        return ua;
    }

    public static final boolean isAppInstalled(Context context, String packageName) {
        boolean installed;
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static final void deleteFolder(String dir) {
        File delFolder = new File(dir);
        File oldFile[] = delFolder.listFiles();
        try {
            for (int i=0; i<oldFile.length; i++){
                if (oldFile[i].isDirectory()){
                    deleteFolder(dir+oldFile[i].getName()+"//");
                }
                oldFile[i].delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String getOfferWallLocalDataPath(Context context) {
        return context.getFilesDir() + "/offerWall.json";
        // just for debug in real device
        //return "/sdcard/offerWall.json";
    }

    public static final String getIncentiveLocalDataPath(Context context, OfferModel offer) {
        return context.getFilesDir() + "/" + offer.getLocalJumpUrlFileName();
    }

    public static final String getPageInterstitialLocalDataPath(Context context) {
        return context.getFilesDir() + "/pageitrs.json";
    }

    public static final String getImageInterstitialLocalDataPath(Context context) {
        return context.getFilesDir() + "/imageitrs.json";
    }

    public static final String getInterstitialImpressResponseLocalDataPath(Context context) {
        return context.getFilesDir() + "/itrsimpressed.json";
    }

    public static final String getImageInterstitialResourceLocalDataPath(Context context, String extension) {
        if (extension == null || extension.isEmpty()) {
            return context.getFilesDir() + "/adimage";
        } else {
            return context.getFilesDir() + "/adimage." + extension;
        }
    }

    public static final String getBannerListLocalDataPath(Context context, int zoneId) {
        return context.getFilesDir() + "/banners"+zoneId+".json";
    }

    public static final String getBannerClickLocalDataPath(Context context, int zoneId) {
        return context.getFilesDir() + "/bannerclick"+zoneId+".json";
    }

}
