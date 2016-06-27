package com.yesup.ad.framework;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public final class AdConfig {
    public static final String TAG = "ADCONFIGURE";

    private String nid;
    private String pid;
    private String sid;
    private String key;
    private String serving = "";
    private ArrayList<AdZone> zones = new ArrayList<>();

    public AdConfig(Context context) {
        int resId =  context.getResources().getIdentifier("adconfigure", "xml", context.getPackageName());

        if ( resId == 0 ) {
            Log.e(TAG, "Missing res/xml/adconfigure.xml" );
            return;
        }

        XmlResourceParser parser = context.getResources().getXml(resId);

        try {
            while ( true ) {
                int eventType = parser.next();
                if ( eventType == XmlPullParser.END_DOCUMENT ) {
                    break;
                }

                if ( eventType == XmlPullParser.START_TAG ) {
                    String tagName = parser.getName();
                    if ( tagName.equals("cpxcenter") ) {
                        processCpxCenterTag(parser);
                    }
                }
            }
        } catch (XmlPullParserException e) {
            Log.e("adconfigure", "Configure error " + e.getMessage());
        } catch (IOException e) {
            Log.e("adconfigure", "Configure read error " + e.getMessage());
        }
    }

    private void processCpxCenterTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        while (true) {
            int eventType = parser.next();
            String tagName = parser.getName();
            if ( eventType == XmlPullParser.END_TAG && tagName.equals("cpxcenter") ) {
                break;
            }

            if ( eventType == XmlPullParser.START_TAG ) {
                if ( tagName.equals("site") ) {
                    processSiteTag(parser);
                } else if ( tagName.equals("zones") ) {
                    processZonesTag(parser);
                }
            }
        }
    }

    private void processSiteTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        while (true) {
            int eventType = parser.next();
            String tagName = parser.getName();
            if ( eventType == XmlPullParser.END_TAG && tagName.equals("site") ) {
                break;
            }

            if ( eventType == XmlPullParser.START_TAG ) {
                if ( tagName.equals("nid") ) {
                    nid = flattenText(parser);
                } else if ( tagName.equals("pid") ) {
                    pid = flattenText(parser);
                } else if ( tagName.equals("sid") ) {
                    sid = flattenText(parser);
                } else if ( tagName.equals("key") ) {
                    key = flattenText(parser);
                } else if ( tagName.equals("serving") ) {
                    serving = flattenText(parser);
                }
            }
        }
    }

    private void processZonesTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        while (true) {
            int eventType = parser.next();
            String tagName = parser.getName();
            if ( eventType == XmlPullParser.END_TAG && tagName.equals("zones") ) {
                break;
            }

            if ( eventType == XmlPullParser.START_TAG ) {
                if ( tagName.equals("zone") ) {
                    processZoneTag(parser);
                }
            }
        }
    }

    private void processZoneTag(XmlResourceParser parser) throws IOException, XmlPullParserException {
        String str;
        AdZone zone = new AdZone();
        while (true) {
            int eventType = parser.next();
            String tagName = parser.getName();
            if ( eventType == XmlPullParser.END_TAG && tagName.equals("zone") ) {
                break;
            }

            if ( eventType == XmlPullParser.START_TAG ) {
                if ( tagName.equals("id") ) {
                    str = flattenText(parser);
                    zone.id = Integer.parseInt(str);
                } else if ( tagName.equals("formats") ) {
                    str = flattenText(parser);
                    zone.formats = str;
                } else if ( tagName.equals("display") ) {
                    str = flattenText(parser);
                    zone.display = str;
                } else if ( tagName.equals("size") ) {
                    str = flattenText(parser);
                    zone.size = str;
                }
            }
        }
        zones.add(zone);
    }

    private String flattenText(XmlResourceParser parser) throws IOException, XmlPullParserException {
        String text = "";

        while (true) {
            int eventType = parser.next();
            if ( eventType == XmlPullParser.START_TAG ) {
                text = text + flattenText(parser);
            } else if ( eventType == XmlPullParser.TEXT ) {
                text = text + parser.getText();
            } else if ( eventType == XmlPullParser.END_TAG ) {
                break;
            }
        }

        return text;
    }

    public String getNid() {
        return nid;
    }

    public String getPid() {
        return pid;
    }

    public String getSid() {
        return sid;
    }

    public String getKey() {
        return key;
    }

    public String getServing() {
        String httpHost = "";
        if (serving != null && serving.length() > 4){
            if (serving.substring(0, 4).toLowerCase().equals("http")) {
                httpHost = serving;
            }else{
                httpHost = "http://" + serving;
            }
        }
        return httpHost;
    }

    public String getDefaultOfferWallZoneId() {
        int zoneId = 0;
        for (AdZone z : zones) {
            if (z.formats.equals("4")){
                zoneId = z.id;
                break;
            }
        }
        if (0 == zoneId) {
            return "";
        } else {
            return Integer.toString(zoneId);
        }
    }

    public AdZone getZoneById(int zoneId) {
        AdZone zone = null;
        for (AdZone z : zones) {
            if (zoneId == z.id){
                zone = z;
                break;
            }
        }
        return zone;
    }

    public ArrayList<AdZone> getZoneList() {
        return zones;
    }
}
