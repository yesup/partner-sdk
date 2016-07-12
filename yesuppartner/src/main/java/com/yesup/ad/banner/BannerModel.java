package com.yesup.ad.banner;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.yesup.ad.framework.AdData;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by derek on 6/15/16.
 */
public class BannerModel extends AdData {
    private static String TAG = "BannerModel";

    public class Banner {
        public String imageUrl;
        public String clickUrl;
        public String appType;
        public String appStoreId;
        public String appScheme;
        public String adnid;
        public String cid;
        public String adsid;
        public String cuid;

        public String clickResult;
        public String clickUid;
    }
    public String result;
    public int refresh;
    public List<Banner> bannerList = new ArrayList<>();

    public BannerModel() {
        setAdType(AdType.AdBanner);
    }

    public boolean parseBannerFromJson(String jsonData) {
        boolean success = false;
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    result = reader.nextString();
                } else if (name.equals("refresh")) {
                    refresh = reader.nextInt();
                } else if (name.equals("list")) {
                    parseBannerList(reader);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e){
                //
            }
        }
        return success;
    }

    private void parseBannerList(JsonReader reader) {
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                Banner banner = new Banner();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("img")) {
                        banner.imageUrl = reader.nextString();
                    } else if (name.equals("click")) {
                        banner.clickUrl = reader.nextString();
                    } else if (name.equals("adnid")) {
                        banner.adnid = reader.nextString();
                    } else if (name.equals("cid")) {
                        banner.cid = reader.nextString();
                    } else if (name.equals("ad")) {
                        banner.adsid = reader.nextString();
                    } else if (name.equals("cuid")) {
                        banner.cuid = reader.nextString();
                    } else if (name.equals("app_type")) {
                        banner.appType = reader.nextString();
                    } else if (name.equals("app_store_id")) {
                        banner.appStoreId = reader.nextString();
                    } else if (name.equals("app_scheme")) {
                        banner.appScheme = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                if (null != banner) {
                    if (banner.appStoreId != null && !banner.appStoreId.isEmpty()) {
                        banner.clickUrl = "";
                    }
                    bannerList.add(banner);
                    Log.i(TAG, "ParseBannerAd:"+banner.imageUrl);
                }
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean parseBannerClickUrlFromJson(String jsonData, Banner banner) {
        boolean success;
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    String sv = reader.nextString();
                    banner.clickResult = sv;
                } else if (name.equals("curl")) {
                    String sv = reader.nextString();
                    banner.clickUrl = sv;
                } else if (name.equals("uid")) {
                    String sv = reader.nextString();
                    banner.clickUid = sv;
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e){
                //
            }
        }
        return success;
    }

}
