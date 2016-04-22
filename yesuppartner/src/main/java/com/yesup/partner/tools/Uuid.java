package com.yesup.partner.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by derek on 4/19/16.
 */
public class Uuid {
    private Context context;
    private final static String MOBI_FOLDER = "/yesup";
    private final static String MOBI_SETTING_FILE = "/settings.prop";
    private final static String UUID_KEY = "uuid";

    public Uuid(Context context) {
        this.context = context;
    }

    public String getUUID() {
        synchronized (Uuid.class) {
            String uuid = readUuid();
            if (uuid.isEmpty()) {
                // read device id from system
                uuid = getDeviceUUID2();
                if (uuid.isEmpty()) {
                    uuid = getDeviceUUID1(context);
                }
                if (uuid.isEmpty()) {
                    // generate a uuid
                    UUID id = UUID.randomUUID();
                    uuid = id.toString();
                }
                // save uuid to config file
                if (!uuid.isEmpty()) {
                    saveUuid(uuid);
                }
            }
            return uuid;
        }
    }

    private String readUuid() {
        String uuid = "";
        boolean externalStorageOK = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            externalStorageOK = true;
            String filename = Environment.getExternalStoragePublicDirectory(MOBI_FOLDER).getPath()+MOBI_SETTING_FILE;
            Properties configuration = new Properties();
            try {
                configuration.load(new FileInputStream(filename));
                uuid = configuration.getProperty(UUID_KEY, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (uuid.isEmpty()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            uuid = sp.getString(UUID_KEY, "");
            if (!uuid.isEmpty() && externalStorageOK) {
                String path = Environment.getExternalStoragePublicDirectory(MOBI_FOLDER).getPath();
                File file = new File(path);
                if (!file.exists()) {
                    boolean ret = file.mkdirs();
                    Log.i("DSFDSFD", "fdfd"+ret);
                }
                String filename = path + MOBI_SETTING_FILE;
                Properties configuration = new Properties();
                try {
                    configuration.setProperty(UUID_KEY, uuid);
                    configuration.store(new FileOutputStream(filename), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return uuid;
    }

    private void saveUuid(String uuid) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(UUID_KEY, uuid).commit();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            String path = Environment.getExternalStoragePublicDirectory(MOBI_FOLDER).getPath();
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filename = path + MOBI_SETTING_FILE;
            Properties configuration = new Properties();
            try {
                configuration.setProperty(UUID_KEY, uuid);
                configuration.store(new FileOutputStream(filename), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static final String getDeviceUUID1(Context context) {
        String uuid = "";
        // getDeviceId need android.Manifest.permission.READ_PHONE_STATE permission
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            uuid = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }
        return uuid;
    }

    public static final String getDeviceUUID2() {
        Class<?> c;
        String uuid = "";
        try {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            uuid = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uuid;
    }
}
