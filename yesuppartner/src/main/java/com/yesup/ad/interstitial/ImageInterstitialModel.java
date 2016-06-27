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

    public class PageAd {
        public String adUrl;
        public String impressionUrl;
        public String clickUrl;
        public int adType;
        public int waitSec = Define.IMPRESS_AFTER_SHOW_WAIT;
        public int width;
        public int height;
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

    public boolean parsePageInterstitialFromJson(String jsonData) {
        boolean success = false;
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    result = reader.nextString();
                } else if (name.equals("list")) {
                    parsePageAdFromJson(reader);
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

    private void parsePageAdFromJson(JsonReader reader) {
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
                    } else if (name.equals("wait")) {
                        ad.waitSec = reader.nextInt();
                    } else if (name.equals("w")) {
                        ad.width = reader.nextInt();
                    } else if (name.equals("h")) {
                        ad.height = reader.nextInt();
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
