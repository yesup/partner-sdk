package com.yesup.partner.module;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "meshbean.db";

    private static final String SQL_CREATE_TABLE_OFFERPAGES =
            "CREATE TABLE "+OfferPages.TABLE_NAME+" ("+OfferPages._ID+" INTEGER PRIMARY KEY,"
            + OfferPages.COLUMN_NAME_PAGETYPE + " INTEGER,"
            + OfferPages.COLUMN_NAME_EXPIRE + " INTEGER,"
            + OfferPages.COLUMN_NAME_REFRESH + " INTEGER,"
            + OfferPages.COLUMN_NAME_TOTAL + " INTEGER,"
            + OfferPages.COLUMN_NAME_CR_HOST + " TEXT)";
    private static final String SQL_CREATE_TABLE_OFFERS =
            "CREATE TABLE "+Offers.TABLE_NAME+" ("+Offers._ID+" INTEGER PRIMARY KEY,"
                    + Offers.COLUMN_NAME_BELONG_PAGETYPE + " INTEGER,"
                    + Offers.COLUMN_NAME_NID + " INTEGER,"
                    + Offers.COLUMN_NAME_ADSID + " INTEGER,"
                    + Offers.COLUMN_NAME_CID + " INTEGER,"
                    + Offers.COLUMN_NAME_CVID + " INTEGER,"
                    + Offers.COLUMN_NAME_ICONURL + " TEXT,"
                    + Offers.COLUMN_NAME_APP_STORE_NAME + " TEXT,"
                    + Offers.COLUMN_NAME_APP_TYPE + " TEXT,"
                    + Offers.COLUMN_NAME_APP_STORE_ID + " TEXT,"
                    + Offers.COLUMN_NAME_APP_SCHEME + " TEXT,"
                    + Offers.COLUMN_NAME_APP_STORE_PRICE + " INTEGER,"
                    + Offers.COLUMN_NAME_CHARGE_PERIOD + " TEXT,"
                    + Offers.COLUMN_NAME_TITLE + " TEXT,"
                    + Offers.COLUMN_NAME_SHORT_DESC + " INTEGER,"
                    + Offers.COLUMN_NAME_CREATEAT + " INTEGER,"
                    + Offers.COLUMN_NAME_IS_NEW + " INTEGER,"
                    + Offers.COLUMN_NAME_IS_RECOMMEND + " INTEGER,"
                    + Offers.COLUMN_NAME_LOCALPATH_ICON + " INTEGER,"
                    + Offers.COLUMN_NAME_JUMP_RESULT + " TEXT,"
                    + Offers.COLUMN_NAME_JUMP_UID + " TEXT,"
                    + Offers.COLUMN_NAME_JUMP_URL + " TEXT,"
                    + Offers.COLUMN_NAME_DETAIL + " TEXT,"
                    + Offers.COLUMN_NAME_TC + " TEXT,"
                    + Offers.COLUMN_NAME_CONVERT_COND + " TEXT,"
                    + Offers.COLUMN_NAME_CLICKED + " INTEGER)";
    private static final String SQL_CREATE_TABLE_OFFERS_CLICKED =
            "CREATE TABLE "+OffersClicked.TABLE_NAME+" ("+Offers._ID+" INTEGER PRIMARY KEY,"
                    + OffersClicked.COLUMN_NAME_BELONG_PAGETYPE + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_NID + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_ADSID + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_CID + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_CVID + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_APP_STORE_NAME + " TEXT,"
                    + OffersClicked.COLUMN_NAME_APP_TYPE + " TEXT,"
                    + OffersClicked.COLUMN_NAME_APP_STORE_ID + " TEXT,"
                    + OffersClicked.COLUMN_NAME_APP_STORE_PRICE + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_CHARGE_PERIOD + " TEXT,"
                    + OffersClicked.COLUMN_NAME_TITLE + " TEXT,"
                    + OffersClicked.COLUMN_NAME_CONVERT_COND + " TEXT,"
                    + OffersClicked.COLUMN_NAME_CREATEAT + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_INSTALLED + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_REPORT + " INTEGER,"
                    + OffersClicked.COLUMN_NAME_JUMP_RESULT + " TEXT,"
                    + OffersClicked.COLUMN_NAME_JUMP_UID + " TEXT,"
                    + OffersClicked.COLUMN_NAME_JUMP_URL + " TEXT)";

    private static final String SQL_DELETE_TABLE_OFFERPAGES =
            "DROP TABLE IF EXISTS " + OfferPages.TABLE_NAME;
    private static final String SQL_DELETE_TABLE_OFFERS =
            "DROP TABLE IF EXISTS " + Offers.TABLE_NAME;
    private static final String SQL_DELETE_TABLE_OFFERS_CLICKED =
            "DROP TABLE IF EXISTS " + OffersClicked.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_OFFERPAGES);
        db.execSQL(SQL_CREATE_TABLE_OFFERS);
        db.execSQL(SQL_CREATE_TABLE_OFFERS_CLICKED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_OFFERS_CLICKED);
        db.execSQL(SQL_DELETE_TABLE_OFFERS);
        db.execSQL(SQL_DELETE_TABLE_OFFERPAGES);
        onCreate(db);
    }

    public boolean addOfferPage(OfferPageModel offerPage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OfferPages.COLUMN_NAME_PAGETYPE, OfferPageModel.PAGE_TYPE_OFFER);
        values.put(OfferPages.COLUMN_NAME_EXPIRE, offerPage.getExpire());
        values.put(OfferPages.COLUMN_NAME_REFRESH, offerPage.getRefresh());
        values.put(OfferPages.COLUMN_NAME_TOTAL, offerPage.getTotal());
        values.put(OfferPages.COLUMN_NAME_CR_HOST, offerPage.getCr_host());

        long newRowId;
        newRowId = db.insert(OfferPages.TABLE_NAME, null, values);
        if (newRowId < 0){
            return false;
        }else {
            offerPage.setDbId(newRowId);
            return true;
        }
    }

    public OfferPageModel readOfferPageByType(int pageType) {
        OfferPageModel offerPage = null;
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {OfferPages.COLUMN_NAME_EXPIRE,
                OfferPages.COLUMN_NAME_REFRESH,
                OfferPages.COLUMN_NAME_TOTAL,
                OfferPages.COLUMN_NAME_CR_HOST};
        String selection = OfferPages.COLUMN_NAME_PAGETYPE + " = ?";
        String[] selectionArgs = {String.valueOf(pageType)};
        String sortOrder = Offers._ID;

        Cursor cursor = db.query(OfferPages.TABLE_NAME, projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            offerPage = new OfferPageModel();
            offerPage.setPageType(pageType);
            offerPage.setExpire(cursor.getLong(cursor.getColumnIndex(OfferPages.COLUMN_NAME_EXPIRE)));
            offerPage.setRefresh(cursor.getInt(cursor.getColumnIndex(OfferPages.COLUMN_NAME_REFRESH)));
            offerPage.setTotal(cursor.getInt(cursor.getColumnIndex(OfferPages.COLUMN_NAME_TOTAL)));
            offerPage.setCr_host(cursor.getString(cursor.getColumnIndex(OfferPages.COLUMN_NAME_CR_HOST)));
            break;
        }

        return offerPage;
    }

    public boolean delOfferPage(int pageType) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = OfferPages.COLUMN_NAME_PAGETYPE + " = ?";
        String[] selectionArgs = {String.valueOf(pageType)};
        db.delete(OfferPages.TABLE_NAME, selection, selectionArgs);
        return true;
    }

    public boolean addOffer(OfferModel offer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Offers.COLUMN_NAME_BELONG_PAGETYPE, OfferPageModel.PAGE_TYPE_OFFER);
        values.put(Offers.COLUMN_NAME_NID, offer.getNid());
        values.put(Offers.COLUMN_NAME_ADSID, offer.getAdsid());
        values.put(Offers.COLUMN_NAME_CID, offer.getCid());
        values.put(Offers.COLUMN_NAME_CVID, offer.getCvid());
        values.put(Offers.COLUMN_NAME_ICONURL, offer.getIconUrl());
        values.put(Offers.COLUMN_NAME_APP_STORE_NAME, offer.getAppStoreName());
        values.put(Offers.COLUMN_NAME_APP_TYPE, offer.getAppType());
        values.put(Offers.COLUMN_NAME_APP_STORE_ID, offer.getAppStoreId());
        values.put(Offers.COLUMN_NAME_APP_SCHEME, offer.getAppScheme());
        values.put(Offers.COLUMN_NAME_APP_STORE_PRICE, offer.getAppStorePrice());
        values.put(Offers.COLUMN_NAME_CHARGE_PERIOD, offer.getChargePeriod());
        values.put(Offers.COLUMN_NAME_TITLE, offer.getTitle());
        values.put(Offers.COLUMN_NAME_SHORT_DESC, offer.getShortDesc());
        values.put(Offers.COLUMN_NAME_CREATEAT, offer.getCreatedAt());
        values.put(Offers.COLUMN_NAME_IS_NEW, offer.isNew());
        values.put(Offers.COLUMN_NAME_IS_RECOMMEND, offer.isRecommend());
        values.put(Offers.COLUMN_NAME_LOCALPATH_ICON, offer.getLocalIconPath());
        values.put(Offers.COLUMN_NAME_JUMP_RESULT, offer.getJumpResult());
        values.put(Offers.COLUMN_NAME_JUMP_UID, offer.getJumpUid());
        values.put(Offers.COLUMN_NAME_JUMP_URL, offer.getJumpUrl());
        values.put(Offers.COLUMN_NAME_DETAIL, offer.getDetail());
        values.put(Offers.COLUMN_NAME_TC, offer.getTc());
        values.put(Offers.COLUMN_NAME_CLICKED, offer.isHasClicked());
        values.put(Offers.COLUMN_NAME_CONVERT_COND, offer.getConvertCondition());

        long newRowId;
        newRowId = db.insert(Offers.TABLE_NAME, null, values);
        if (newRowId < 0){
            return false;
        }else {
            offer.setDbId(newRowId);
            return true;
        }
    }

    public boolean offerClickedIsExist(OfferModel offer) {
        boolean exist = false;
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {"COUNT(*)"};
        String selection = OffersClicked.COLUMN_NAME_NID+"=? AND "
                +OffersClicked.COLUMN_NAME_CID+"=? AND "
                +OffersClicked.COLUMN_NAME_CVID+"=?";
        String[] selectionArgs = {String.valueOf(offer.getNid()),
                                String.valueOf(offer.getCid()),
                                String.valueOf(offer.getCvid())};

        Cursor cursor = db.query(OffersClicked.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getInt(0) > 0) {
                exist = true;
            }else{
                exist = false;
            }
            break;
        }
        return exist;
    }

    public boolean addOfferClicked(OfferModel offer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OffersClicked.COLUMN_NAME_BELONG_PAGETYPE, OfferPageModel.PAGE_TYPE_OFFER);
        values.put(OffersClicked.COLUMN_NAME_NID, offer.getNid());
        values.put(OffersClicked.COLUMN_NAME_ADSID, offer.getAdsid());
        values.put(OffersClicked.COLUMN_NAME_CID, offer.getCid());
        values.put(OffersClicked.COLUMN_NAME_CVID, offer.getCvid());
        values.put(OffersClicked.COLUMN_NAME_APP_STORE_NAME, offer.getAppStoreName());
        values.put(OffersClicked.COLUMN_NAME_APP_TYPE, offer.getAppType());
        values.put(OffersClicked.COLUMN_NAME_APP_STORE_ID, offer.getAppStoreId());
        values.put(OffersClicked.COLUMN_NAME_APP_STORE_PRICE, offer.getAppStorePrice());
        values.put(OffersClicked.COLUMN_NAME_CHARGE_PERIOD, offer.getChargePeriod());
        values.put(OffersClicked.COLUMN_NAME_TITLE, offer.getTitle());
        values.put(OffersClicked.COLUMN_NAME_CONVERT_COND, offer.getConvertCondition());
        values.put(OffersClicked.COLUMN_NAME_CREATEAT, offer.getCreatedAt());
        values.put(OffersClicked.COLUMN_NAME_INSTALLED, false);
        values.put(OffersClicked.COLUMN_NAME_REPORT, false);
        values.put(OffersClicked.COLUMN_NAME_JUMP_RESULT, offer.getJumpResult());
        values.put(OffersClicked.COLUMN_NAME_JUMP_UID, offer.getJumpUid());
        values.put(OffersClicked.COLUMN_NAME_JUMP_URL, offer.getJumpUrl());

        long newRowId;
        newRowId = db.insert(OffersClicked.TABLE_NAME, null, values);
        if (newRowId < 0){
            return false;
        }else {
            return true;
        }
    }

    public List<OfferModel> readOffersByType(int pageType) {
        List<OfferModel> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = null;
        String selection = Offers.COLUMN_NAME_BELONG_PAGETYPE + " = ?";
        String[] selectionArgs = {String.valueOf(pageType)};
        String sortOrder = Offers.COLUMN_NAME_CREATEAT + " DESC";

        Cursor cursor = db.query(Offers.TABLE_NAME, projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast()) {
            OfferModel offer = new OfferModel();
            offer.setLocalReference(count);
            offer.setDbId(cursor.getInt(cursor.getColumnIndex(Offers._ID)));
            //offer.set( cursor.getString( cursor.getColumnIndex(Offers.COLUMN_NAME_BELONG_PAGETYPE) ) );
            offer.setNid(cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_NID)));
            offer.setAdsid(cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_ADSID)));
            offer.setCid(cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_CID)));
            offer.setCvid(cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_CVID)));
            offer.setIconUrl(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_ICONURL)));
            offer.setAppStoreName(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_APP_STORE_NAME)));
            offer.setAppType(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_APP_TYPE)));
            offer.setAppStoreId(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_APP_STORE_ID)));
            offer.setAppScheme(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_APP_SCHEME)));
            offer.setAppStorePrice(cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_APP_STORE_PRICE)));
            offer.setChargePeriod(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_CHARGE_PERIOD)));
            offer.setTitle(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_TITLE)));
            offer.setShortDesc(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_SHORT_DESC)));
            offer.setCreatedAt(cursor.getLong(cursor.getColumnIndex(Offers.COLUMN_NAME_CREATEAT)));
            if (cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_IS_NEW)) == 0) {
                offer.setIsNew(false);
            }else{
                offer.setIsNew(true);
            }
            if (cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_IS_RECOMMEND)) == 0) {
                offer.setIsRecommend(false);
            }else{
                offer.setIsRecommend(true);
            }
            offer.setLocalIconPath(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_LOCALPATH_ICON)));
            offer.setDetail(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_DETAIL)));
            offer.setTc(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_TC)));
            offer.setConvertCondition(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_CONVERT_COND)));
            offer.setJumpResult(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_JUMP_RESULT)));
            offer.setJumpUid(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_JUMP_UID)));
            offer.setJumpUrl(cursor.getString(cursor.getColumnIndex(Offers.COLUMN_NAME_JUMP_URL)));
            if (cursor.getInt(cursor.getColumnIndex(Offers.COLUMN_NAME_CLICKED)) == 0) {
                offer.setHasClicked(false);
            }else{
                offer.setHasClicked(true);
            }

            cursor.moveToNext();
            list.add(count, offer);
            count++;
        }

        return list;
    }

    public List<OfferModel> readOffersClickedWhichNeedToCheckByType(int pageType) {
        List<OfferModel> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = null;
        String selection = OffersClicked.COLUMN_NAME_BELONG_PAGETYPE + "=? AND "
                + OffersClicked.COLUMN_NAME_INSTALLED + "=0";
        String[] selectionArgs = {String.valueOf(pageType)};
        String sortOrder = OffersClicked._ID;

        Cursor cursor = db.query(OffersClicked.TABLE_NAME, projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast()) {
            OfferModel offer = new OfferModel();
            offer.setLocalReference(count);
            offer.setDbId(cursor.getInt(cursor.getColumnIndex(OffersClicked._ID)));
            //offer.set( cursor.getString( cursor.getColumnIndex(OffersClicked.COLUMN_NAME_BELONG_PAGETYPE) ) );
            offer.setNid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_NID)));
            offer.setAdsid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_ADSID)));
            offer.setCid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CID)));
            offer.setCvid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CVID)));
            offer.setAppStoreName(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_STORE_NAME)));
            offer.setAppType(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_TYPE)));
            offer.setAppStoreId(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_STORE_ID)));
            offer.setAppStorePrice(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_STORE_PRICE)));
            offer.setChargePeriod(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CHARGE_PERIOD)));
            offer.setTitle(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_TITLE)));
            offer.setConvertCondition(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CONVERT_COND)));
            offer.setCreatedAt(cursor.getLong(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CREATEAT)));
            if (cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_INSTALLED)) == 0) {
                offer.setHasInstalled(false);
            }else{
                offer.setHasInstalled(true);
            }
            if (cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_REPORT)) == 0) {
                offer.setHasReport(false);
            }else{
                offer.setHasReport(true);
            }
            offer.setJumpResult(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_JUMP_RESULT)));
            offer.setJumpUid(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_JUMP_UID)));
            offer.setJumpUrl(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_JUMP_URL)));

            cursor.moveToNext();
            list.add(count, offer);
            count++;
        }

        return list;
    }

    public List<OfferModel> readOffersClickedWhichNeedToReportByType(int pageType) {
        List<OfferModel> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = null;
        String selection = OffersClicked.COLUMN_NAME_BELONG_PAGETYPE + "=? AND "
                + OffersClicked.COLUMN_NAME_INSTALLED + "=1 AND "
                + OffersClicked.COLUMN_NAME_REPORT + "=0";
        String[] selectionArgs = {String.valueOf(pageType)};
        String sortOrder = OffersClicked._ID;

        Cursor cursor = db.query(OffersClicked.TABLE_NAME, projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast()) {
            OfferModel offer = new OfferModel();
            offer.setLocalReference(count);
            offer.setDbId(cursor.getInt(cursor.getColumnIndex(OffersClicked._ID)));
            //offer.set( cursor.getString( cursor.getColumnIndex(OffersClicked.COLUMN_NAME_BELONG_PAGETYPE) ) );
            offer.setNid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_NID)));
            offer.setAdsid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_ADSID)));
            offer.setCid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CID)));
            offer.setCvid(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CVID)));
            offer.setAppStoreName(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_STORE_NAME)));
            offer.setAppType(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_TYPE)));
            offer.setAppStoreId(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_STORE_ID)));
            offer.setAppStorePrice(cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_APP_STORE_PRICE)));
            offer.setChargePeriod(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CHARGE_PERIOD)));
            offer.setTitle(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_TITLE)));
            offer.setConvertCondition(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CONVERT_COND)));
            offer.setCreatedAt(cursor.getLong(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_CREATEAT)));
            if (cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_INSTALLED)) == 0) {
                offer.setHasInstalled(false);
            }else{
                offer.setHasInstalled(true);
            }
            if (cursor.getInt(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_REPORT)) == 0) {
                offer.setHasReport(false);
            }else{
                offer.setHasReport(true);
            }
            offer.setJumpResult(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_JUMP_RESULT)));
            offer.setJumpUid(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_JUMP_UID)));
            offer.setJumpUrl(cursor.getString(cursor.getColumnIndex(OffersClicked.COLUMN_NAME_JUMP_URL)));

            cursor.moveToNext();
            list.add(count, offer);
            count++;
        }

        return list;
    }

    public boolean updateOfferJumpUrl(OfferModel offer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Offers.COLUMN_NAME_JUMP_RESULT, offer.getJumpResult());
        values.put(Offers.COLUMN_NAME_JUMP_UID, offer.getJumpUid());
        values.put(Offers.COLUMN_NAME_JUMP_URL, offer.getJumpUrl());
        String where = Offers._ID + " = ?";
        String[] whereArgs = {String.valueOf(offer.getDbId())};

        int rows = db.update(Offers.TABLE_NAME, values, where, whereArgs);
        if (rows > 0){
            return true;
        }else {
            return false;
        }
    }

    public boolean updateOfferClicked(OfferModel offer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Offers.COLUMN_NAME_CLICKED, offer.isHasClicked());

        String where = Offers._ID + " = ?";
        String[] whereArgs = {String.valueOf(offer.getDbId())};
        int rows = db.update(Offers.TABLE_NAME, values, where, whereArgs);
        if (rows > 0){
            return true;
        }else {
            return false;
        }
    }

    public boolean updateOfferInstalled(OfferModel offer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OffersClicked.COLUMN_NAME_INSTALLED, offer.isHasInstalled());

        String where = Offers._ID + " = ?";
        String[] whereArgs = {String.valueOf(offer.getDbId())};
        int rows = db.update(OffersClicked.TABLE_NAME, values, where, whereArgs);
        if (rows > 0){
            return true;
        }else {
            return false;
        }
    }

    public boolean updateOfferReported(OfferModel offer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(OffersClicked.COLUMN_NAME_REPORT, offer.isHasReport());

        String where = Offers._ID + " = ?";
        String[] whereArgs = {String.valueOf(offer.getDbId())};
        int rows = db.update(OffersClicked.TABLE_NAME, values, where, whereArgs);
        if (rows > 0){
            return true;
        }else {
            return false;
        }
    }

    public boolean delOffer(int pageType) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = Offers.COLUMN_NAME_BELONG_PAGETYPE + " = ?";
        String[] selectionArgs = {String.valueOf(pageType)};
        db.delete(Offers.TABLE_NAME, selection, selectionArgs);
        return true;
    }

    /* Inner class that defines the table contents */
    public static abstract class OfferPages implements BaseColumns {
        public static final String TABLE_NAME = "offerpages";
        public static final String COLUMN_NAME_PAGETYPE = "pagetype";
        public static final String COLUMN_NAME_EXPIRE = "expire";
        public static final String COLUMN_NAME_REFRESH = "refresh";
        public static final String COLUMN_NAME_TOTAL = "total";
        public static final String COLUMN_NAME_CR_HOST = "cr_host";
    }
    public static abstract class Offers implements BaseColumns {
        public static final String TABLE_NAME = "offers";
        public static final String COLUMN_NAME_BELONG_PAGETYPE = "page_type";
        public static final String COLUMN_NAME_NID = "nid";
        public static final String COLUMN_NAME_ADSID = "adsid";
        public static final String COLUMN_NAME_CID = "cid";
        public static final String COLUMN_NAME_CVID = "cvid";
        public static final String COLUMN_NAME_ICONURL = "icon_url";
        public static final String COLUMN_NAME_APP_STORE_NAME = "app_store_name";
        public static final String COLUMN_NAME_APP_TYPE = "app_type";
        public static final String COLUMN_NAME_APP_STORE_ID = "app_store_id";
        public static final String COLUMN_NAME_APP_SCHEME = "app_scheme";
        public static final String COLUMN_NAME_APP_STORE_PRICE = "app_store_price";
        public static final String COLUMN_NAME_CHARGE_PERIOD = "charge_period";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SHORT_DESC = "short_desc";
        public static final String COLUMN_NAME_CREATEAT = "create_at";
        public static final String COLUMN_NAME_IS_NEW = "is_new";
        public static final String COLUMN_NAME_IS_RECOMMEND = "is_recommend";
        public static final String COLUMN_NAME_LOCALPATH_ICON = "icon_path";
        public static final String COLUMN_NAME_JUMP_RESULT = "jump_result";
        public static final String COLUMN_NAME_JUMP_UID = "jump_uid";
        public static final String COLUMN_NAME_JUMP_URL = "jump_url";
        public static final String COLUMN_NAME_DETAIL = "detail";
        public static final String COLUMN_NAME_TC = "tc";
        public static final String COLUMN_NAME_CONVERT_COND = "convert_cond";
        public static final String COLUMN_NAME_CLICKED = "clicked";
    }
    public static abstract class OffersClicked implements BaseColumns {
        public static final String TABLE_NAME = "offers_clicked";
        public static final String COLUMN_NAME_BELONG_PAGETYPE = "page_type";
        public static final String COLUMN_NAME_NID = "nid";
        public static final String COLUMN_NAME_ADSID = "adsid";
        public static final String COLUMN_NAME_CID = "cid";
        public static final String COLUMN_NAME_CVID = "cvid";
        public static final String COLUMN_NAME_APP_STORE_NAME = "app_store_name";
        public static final String COLUMN_NAME_APP_TYPE = "app_type";
        public static final String COLUMN_NAME_APP_STORE_ID = "app_store_id";
        public static final String COLUMN_NAME_APP_STORE_PRICE = "app_store_price";
        public static final String COLUMN_NAME_CHARGE_PERIOD = "charge_period";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONVERT_COND = "convert_cond";
        public static final String COLUMN_NAME_CREATEAT = "create_at";
        public static final String COLUMN_NAME_INSTALLED = "installed";
        public static final String COLUMN_NAME_REPORT = "report";
        public static final String COLUMN_NAME_JUMP_RESULT = "jump_result";
        public static final String COLUMN_NAME_JUMP_UID = "jump_uid";
        public static final String COLUMN_NAME_JUMP_URL = "jump_url";
    }
}
