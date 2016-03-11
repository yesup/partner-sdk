package com.yesup.partner.tools;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

/**
 * Created by derek on 3/1/16.
 */
public class AppTool {

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
    }

    public static final String getIncentiveLocalDataPath(Context context) {
        return context.getFilesDir() + "/incentiveApi.json";
    }
}
