package com.yesup.ad.offerwall;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.yesup.ad.framework.AdData;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by derek on 2/24/16.
 */
public class OfferModel extends AdData {
    private static final String TAG = "OfferModel";
    private int localReference; // download id
    private String localIconPath; // /data/data/com.yesup.meshbeansdk/files/appicon/...ico
    private boolean hasClicked;
    private boolean hasInstalled;
    private boolean hasReport;

    private int nid;
    private int adsid;
    private int cid;
    private int cvid;
    private String iconUrl;
    private String appStoreName;
    private String appType;
    private String appStoreId;
    private String appScheme;
    private int appStorePrice;
    private String chargePeriod;
    private String title;
    private String shortDesc;
    private long createdAt;
    private boolean isNew;
    private boolean isRecommend;
    private String detail;
    private String tc;
    private String convertCondition;

    private int rate;
    private int payout;

    private String jumpResult;
    private String jumpUrl;
    private String jumpUid;

    public int getLocalReference() {
        return localReference;
    }

    public void setLocalReference(int localReference) {
        this.localReference = localReference;
    }

    public String getLocalIconPath() {
        return localIconPath;
    }

    public void setLocalIconPath(String localIconPath) {
        this.localIconPath = localIconPath;
    }

    public boolean isHasClicked() {
        return hasClicked;
    }

    public void setHasClicked(boolean hasClicked) {
        this.hasClicked = hasClicked;
    }

    public boolean isHasInstalled() {
        return hasInstalled;
    }

    public void setHasInstalled(boolean hasInstalled) {
        this.hasInstalled = hasInstalled;
    }

    public boolean isHasReport() {
        return hasReport;
    }

    public void setHasReport(boolean hasReport) {
        this.hasReport = hasReport;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getAdsid() {
        return adsid;
    }

    public void setAdsid(int adsid) {
        this.adsid = adsid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getCvid() {
        return cvid;
    }

    public void setCvid(int cvid) {
        this.cvid = cvid;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAppStoreName() {
        return appStoreName;
    }

    public void setAppStoreName(String appStoreName) {
        this.appStoreName = appStoreName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppStoreId() {
        return appStoreId;
    }

    public void setAppStoreId(String appStoreId) {
        this.appStoreId = appStoreId;
    }

    public String getAppScheme() {
        return appScheme;
    }

    public void setAppScheme(String appScheme) {
        this.appScheme = appScheme;
    }

    public int getAppStorePrice() {
        return appStorePrice;
    }

    public void setAppStorePrice(int appStorePrice) {
        this.appStorePrice = appStorePrice;
    }

    public String getChargePeriod() {
        return chargePeriod;
    }

    public void setChargePeriod(String chargePeriod) {
        this.chargePeriod = chargePeriod;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isRecommend() {
        return isRecommend;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public void setIsRecommend(boolean isRecommend) {
        this.isRecommend = isRecommend;
    }

    public String getConvertCondition() {
        return convertCondition;
    }

    public void setConvertCondition(String convertCondition) {
        this.convertCondition = convertCondition;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getPayout() {
        return payout;
    }

    public void setPayout(int payout) {
        this.payout = payout;
    }

    public String getJumpResult() {
        return jumpResult;
    }

    public void setJumpResult(String jumpResult) {
        this.jumpResult = jumpResult;
    }

    public String getJumpUid() {
        return jumpUid;
    }

    public void setJumpUid(String jumpUid) {
        this.jumpUid = jumpUid;
    }

    public static List<OfferModel> getMeshbeanOfferListFromJson(JsonReader reader) {
        int count = 0;
        List<OfferModel> list = new ArrayList<>();
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                OfferModel offer = new OfferModel();
                offer.setLocalReference(count);
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("NID")) {
                        offer.setNid(reader.nextInt());
                    } else if (name.equals("ADSID")) {
                        offer.setAdsid(reader.nextInt());
                    } else if (name.equals("CID")) {
                        offer.setCid(reader.nextInt());
                    } else if (name.equals("PO")) {
                        offer.setPayout(reader.nextInt());
                    } else if (name.equals("CVID")) {
                        offer.setCvid(reader.nextInt());
                    } else if (name.equals("is_recommend")) {
                        offer.setIsRecommend(reader.nextBoolean());
                    } else if (name.equals("rate")) {
                        offer.setRate(reader.nextInt());
                    } else if (name.equals("icon_url")) {
                        offer.setIconUrl(reader.nextString());
                    } else if (name.equals("app_store_name")) {
                        offer.setAppStoreName(reader.nextString());
                    } else if (name.equals("app_type")) {
                        offer.setAppType(reader.nextString());
                    } else if (name.equals("app_store_id")) {
                        offer.setAppStoreId(reader.nextString());
                    } else if (name.equals("app_scheme")) {
                        offer.setAppScheme(reader.nextString());
                    } else if (name.equals("app_store_price")) {
                        offer.setAppStorePrice(reader.nextInt());
                    } else if (name.equals("charge_period")) {
                        offer.setChargePeriod(reader.nextString());
                    } else if (name.equals("title")) {
                        offer.setTitle(reader.nextString());
                    } else if (name.equals("short_desc")) {
                        offer.setShortDesc(reader.nextString());
                    } else if (name.equals("app_tc")) {
                        offer.setTc(reader.nextString());
                    } else if (name.equals("convert_cond")) {
                        offer.setConvertCondition(reader.nextString());
                    } else if (name.equals("created_at")) {
                        offer.setCreatedAt(reader.nextLong());
                    } else if (name.equals("is_new")) {
                        offer.setIsNew(reader.nextBoolean());
                    } else {
                        reader.skipValue();
                    }
                }
                list.add(count, offer);
                reader.endObject();
                count++;
                Log.v(TAG, "=========== The " + count + "th object ===========");
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean parseOfferJumpUrlFromJson(String jsonData, OfferModel toOffer) {
        boolean success;
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("result")) {
                    String sv = reader.nextString();
                    toOffer.setJumpResult(sv);
                } else if (name.equals("curl")) {
                    String sv = reader.nextString();
                    toOffer.setJumpUrl(sv);
                } else if (name.equals("uid")) {
                    String sv = reader.nextString();
                    toOffer.setJumpUid(sv);
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

    public String getLocalImageFileName() {
        String imageFileName = "";
        if (iconUrl.length() > 4){
            int pos = iconUrl.lastIndexOf('.');
            if (pos >= 0){
                imageFileName = nid + "-" + cid + "-" + cvid + iconUrl.substring(pos);
            }
        }
        return imageFileName;
    }

    public String getLocalJumpUrlFileName() {
        String jumpUrlFileName = nid + "-" + cid + "-" + cvid + "-api.json";
        return jumpUrlFileName;
    }

}
