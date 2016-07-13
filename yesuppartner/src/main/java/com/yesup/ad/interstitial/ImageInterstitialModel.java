package com.yesup.ad.interstitial;

import com.google.gson.stream.JsonReader;
import com.yesup.ad.framework.AdData;
import com.yesup.ad.framework.Define;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by derek on 4/20/16.
 */
public class ImageInterstitialModel extends AdData {
    public static final String IMAGE_INTERSTITIAL_TYPE_HTML = "rich";
    public static final String IMAGE_INTERSTITIAL_TYPE_LINK = "link";

    public class PageAd {
        public String adUrl;
        public String impressionUrl;
        public String clickUrl;
        public int adType;
        public String mime;
        public int waitSec = Define.IMPRESS_AFTER_SHOW_WAIT;
        public int width;
        public int height;
        public String adNid;
        public String adSid;
        public String cid;
        public String clickUid;
        public String appType;
        public String appStoreId;
        public String appStoreScheme;
    }

    public String localImageFilename;

    public String result;
    public List<PageAd> adList = new ArrayList<>();

    public PageAd getPageAd() {
        if (adList.size() > 0) {
            return adList.get(0);
        } else {
            return null;
        }
    }

    public boolean parseImageInterstitialFromJson(String jsonData) {
        boolean success = false;
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    result = reader.nextString();
                } else if (name.equals("list")) {
                    parseImageAdFromJson(reader);
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

    private void parseImageAdFromJson(JsonReader reader) {
        PageAd ad = null;
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                ad = new PageAd();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("ad")) {
                        ad.adUrl = reader.nextString();
                    } else if (name.equals("impression")) {
                        ad.impressionUrl = reader.nextString();
                    } else if (name.equals("click")) {
                        ad.clickUrl = reader.nextString();
                    } else if (name.equals("adtype")) {
                        ad.adType = reader.nextInt();
                    } else if (name.equals("mime")) {
                        ad.mime = reader.nextString();
                    } else if (name.equals("wait")) {
                        ad.waitSec = reader.nextInt();
                    } else if (name.equals("w")) {
                        ad.width = reader.nextInt();
                    } else if (name.equals("h")) {
                        ad.height = reader.nextInt();
                    } else if (name.equals("adnid")) {
                        ad.adNid = reader.nextString();
                    } else if (name.equals("user_ad")) {
                        ad.adSid = reader.nextString();
                    } else if (name.equals("uid")) {
                        ad.clickUid = reader.nextString();
                    } else if (name.equals("cid")) {
                        ad.cid = reader.nextString();
                    } else if (name.equals("app_type")) {
                        ad.appType = reader.nextString();
                    } else if (name.equals("app_store_id")) {
                        ad.appStoreId = reader.nextString();
                    } else if (name.equals("app_scheme")) {
                        ad.appStoreScheme = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                adList.add(ad);
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
