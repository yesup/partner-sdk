package com.yesup.ad.banner;

import com.google.gson.stream.JsonReader;
import com.yesup.ad.framework.AdData;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by derek on 6/15/16.
 */
public class BannerModel extends AdData {

    public class Banner {
        public String imageUrl;
        public String clickUrl;
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
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                if (null != banner) {
                    bannerList.add(banner);
                }
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
