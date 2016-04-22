package com.yesup.partner.tools;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by derek on 2/24/16.
 */
public class StringTool {
    private StringTool(){}

    public static final String getStringFromRaw(Context context, int resId) {
        String result = "";
        try {
            InputStream in = context.getResources().openRawResource(resId);
            int len = in.available();
            if (len > 0){
                byte[] buf = new byte[len];
                in.read(buf);
                result = new String(buf, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "ERROR";
        }
        return result;
    }

    public static String readStringFromFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    public static final String getCompletedUrl(String url) {
        String completedUrl;
        if (url.substring(0, 4).equals("http")){
            completedUrl = url;
        }else{
            completedUrl = "http://app.meshbean.com" + url;
        }
        return completedUrl;
    }

    public static final String getFilenameExtension(String filename) {
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        return extension;
    }
}
