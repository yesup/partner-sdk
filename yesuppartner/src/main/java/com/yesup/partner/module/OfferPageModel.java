package com.yesup.partner.module;

import android.util.Log;

import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.List;

/**
 * Created by derek on 2/24/16.
 */
public class OfferPageModel extends DBObject {
    private static final String TAG="OfferPageModel";
    public static final int PAGE_TYPE_OFFER = 0;

    private int pageType;
    private int zoneId;
    private long expire;
    private int refresh;
    private int total;
    private String cr_host;
    private int incentRate;
    private List<OfferModel> list;

    public void clean() {
        expire = 0;
        refresh = 0;
        cr_host = "";
        if (list != null) {
            list.clear();
        }
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public int getRefresh() {
        return refresh;
    }

    public void setRefresh(int refresh) {
        this.refresh = refresh;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCr_host() {
        return cr_host;
    }

    public void setCr_host(String cr_host) {
        this.cr_host = cr_host;
    }

    public int getIncentRate() {
        return incentRate;
    }

    public void setIncentRate(int incentRate) {
        this.incentRate = incentRate;
    }

    public List<OfferModel> getList() {
        return list;
    }

    public void setList(List<OfferModel> list) {
        this.list = list;
    }

    public static OfferPageModel getMeshbeanOfferPageFromJson(String jsonData) {
        int count = 0;
        OfferPageModel offerPage = null;
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            offerPage = new OfferPageModel();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("total")) {
                    int iv = reader.nextInt();
                    offerPage.setTotal(iv);
                } else if (name.equals("cr_host")) {
                    String sv = reader.nextString();
                    offerPage.setCr_host(sv);
                } else if (name.equals("incent_rate")) {
                    offerPage.setIncentRate( reader.nextInt() );
                } else if (name.equals("list")) {
                    List<OfferModel> list = OfferModel.getMeshbeanOfferListFromJson(reader);
                    offerPage.setList(list);
                    count = list.size();
                    Log.i(TAG, "List Size:" + count);
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e){
                //
            }
        }
        return offerPage;
    }

}
