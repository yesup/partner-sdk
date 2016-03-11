package com.yesup.partner.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yesup.partner.module.Define;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by derek on 2/26/16.
 */
public class DownloadManagerLite {
    public static final int MAX_CONCURRENT_LIMIT = 20;

    private boolean managerHasStarted = false;
    private int maxConcurrentSize;
    private Downloader[] workers;
    private final List<Request> requestQueue = new LinkedList<>();

    public DownloadManagerLite(int maxConcurrentSize) {
        if (maxConcurrentSize > 0 && maxConcurrentSize <= MAX_CONCURRENT_LIMIT) {
            this.maxConcurrentSize = maxConcurrentSize;
        }else{
            this.maxConcurrentSize = 1;
        }
        initThreadPool();
        Log.v("DownloadManagerLite", "New DownloadManagerLite, Pool Size:"+this.maxConcurrentSize);
    }

    public int getMaxConcurrentSize() {
        return maxConcurrentSize;
    }

    public int newDownload(Request request) {
        if (request.getDownloadUrl() == null || request.getSaveFileName() == null){
            request.requestId = -1;
            return -1;
        }
        synchronized (requestQueue) {
            request.status = Request.STATUS_NEW;
            requestQueue.add(request);
            requestQueue.notifyAll();
        }
        Log.v("DownloadManagerLite", "New Request["+request.requestId+"]:"
                +request.getDownloadUrl()+"->"+request.getSaveFileName());
        return request.requestId;
    }

    public boolean start() {
        if (managerHasStarted) {
            return true;
        }
        if (maxConcurrentSize <= 0 || maxConcurrentSize > MAX_CONCURRENT_LIMIT
                && workers == null){
            Log.v("DownloadManagerLite", "Start Failed");
            return false;
        }
        for (int i=0; i<maxConcurrentSize; i++){
            workers[i].start();
        }
        managerHasStarted = true;
        Log.v("DownloadManagerLite", "Start Success");
        return true;
    }

    public void cleanAllRequests() {
        synchronized (requestQueue) {
            requestQueue.clear();
        }
    }

    public boolean nowStop() {
        if (maxConcurrentSize <= 0 || maxConcurrentSize > MAX_CONCURRENT_LIMIT
                && workers == null){
            return false;
        }
        for (int i=0; i<maxConcurrentSize; i++){
            workers[i].stop();
        }
        managerHasStarted = false;
        return true;
    }
    public boolean safeStop(long waitTime) {
        if (maxConcurrentSize <= 0 || maxConcurrentSize > MAX_CONCURRENT_LIMIT
                && workers == null){
            return false;
        }
        for (int i=0; i<maxConcurrentSize; i++){
            workers[i].stopRun();
        }
        // wait until thread exit
        for (int i=0; i<maxConcurrentSize; i++){
            try {
                workers[i].join();
            }catch (InterruptedException e){}
        }
        managerHasStarted = false;
        return true;
    }

    private void initThreadPool() {
        workers = new Downloader[maxConcurrentSize];
        for (int i=0; i<maxConcurrentSize; i++){
            workers[i] = new Downloader();
        }
    }


    public static class Request {
        public static final int STATUS_CONNECT_FAILED = -2;
        public static final int STATUS_FAILED = -1;
        public static final int STATUS_NEW = 0;
        public static final int STATUS_PROGRESS = 1;
        public static final int STATUS_COMPLETED = 2;

        private int requestId;
        private int type;
        private String requestMethod = "GET"; // GET or POST
        private String downloadUrl;
        private String saveFileName;
        private int status;
        private int percent; // the progress of download file,0~100
        private Map<String, String> reqHeaderParameter = new HashMap<>();
        private Map<String, String> reqDataParameter = new HashMap<>();

        public int getRequestId() {
            return requestId;
        }

        public void setRequestId(int requestId) {
            this.requestId = requestId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getSaveFileName() {
            return saveFileName;
        }

        public void setSaveFileName(String saveFileName) {
            this.saveFileName = saveFileName;
        }

        public int getStatus() {
            return status;
        }

        public int getPercent() {
            return percent;
        }

        public void setRequestHeaderParameter(String key, String value) {
            reqHeaderParameter.put(key, value);
        }

        public void setRequestDataParameter(String key, String value) {
            reqDataParameter.put(key, value);
        }
    }

    private Handler handler;
    public void setMsgHandler(Handler handler) {
        this.handler = handler;
    }
    public void sendMsgToHandler(int what, int status, Object object) {
        if (handler == null){
            return;
        }
        Message msg = handler.obtainMessage(what);
        msg.arg1 = status;
        msg.obj = object;
        handler.sendMessage(msg);
    }

    public class Downloader extends Thread {

        private boolean isRunning = false;
        public boolean isRunning() {
            return isRunning;
        }
        public void stopRun() {
            isRunning = false;
        }

        @Override
        public void run() {
            isRunning = true;
            Request request = null;
            // execute teak
            while (isRunning) {
                synchronized (requestQueue) {
                    if (requestQueue.isEmpty()){
                        try {
                            requestQueue.wait(100);
                        }catch (InterruptedException e){}
                    } else {
                        request = requestQueue.remove(0);
                    }
                }
                if (request != null) {
                    Log.v("DownloadManagerLite", "Begin download:"+request.getDownloadUrl());
                    // download file
                    HttpURLConnection connection = null;
                    InputStream input = null;
                    OutputStream output = null;
                    try {
                        URL url = new URL(request.getDownloadUrl());
                        connection = (HttpURLConnection)url.openConnection();
                        if (request.requestMethod.length() <= 0){
                            request.requestMethod = "GET";
                        }
                        // set connection parameters
                        connection.setRequestMethod(request.requestMethod);
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        connection.setUseCaches(false);
                        connection.setDoInput(true);
                        if (request.reqDataParameter.size() > 0) {
                            connection.setDoOutput(true);
                        }
                        // set HTTP header parameters
                        for (Map.Entry<String, String> entry : request.reqHeaderParameter.entrySet()){
                            connection.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                        // begin connecting
                        connection.connect();
                        // send data body
                        if (request.reqDataParameter.size() > 0) {
                            OutputStream os = connection.getOutputStream();
                            int index = 0;
                            String parameter;
                            for (Map.Entry<String, String> entry : request.reqDataParameter.entrySet()){
                                if (0 == index) {
                                    parameter = entry.getKey() + "=" + entry.getValue();
                                }else{
                                    parameter = "&" + entry.getKey() + "=" + entry.getValue();
                                }
                                index++;
                                os.write(parameter.getBytes());
                            }
                            //os.write( ("\r\n").getBytes() );
                        }
                        // process response
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            request.status = Request.STATUS_PROGRESS;
                            request.percent = 0;
                            // this value might be -1 when server did not report the length
                            int fileLength = connection.getContentLength();
                            // download the file
                            input = connection.getInputStream();
                            output = new FileOutputStream(request.getSaveFileName());
                            byte data[] = new byte[4096];
                            long total = 0;
                            int count;
                            while ((count=input.read(data)) != -1) {
                                total += count;
                                // compute progress
                                if (fileLength > 0) {
                                    int percent = (int) (total * 100 / fileLength);
                                    request.percent = percent;
                                    sendMsgToHandler(Define.MSG_DOWNLOAD_STATUS_CHANGED, Request.STATUS_PROGRESS, request);
                                }
                                output.write(data, 0, count);
                            }
                            request.status = Request.STATUS_COMPLETED;
                            sendMsgToHandler(Define.MSG_DOWNLOAD_STATUS_CHANGED, Request.STATUS_COMPLETED, request);
                            Log.v("DownloadManagerLite", "Download completed:"+request.getDownloadUrl());
                        }else{
                            request.status = Request.STATUS_CONNECT_FAILED;
                            sendMsgToHandler(Define.MSG_DOWNLOAD_STATUS_CHANGED, Request.STATUS_CONNECT_FAILED, request);
                            Log.v("DownloadManagerLite", "Connect failed[HTTP-"+connection.getResponseCode()+"]:"+request.getDownloadUrl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.status = Request.STATUS_FAILED;
                        sendMsgToHandler(Define.MSG_DOWNLOAD_STATUS_CHANGED, Request.STATUS_FAILED, request);
                        Log.v("DownloadManagerLite", "Download failed:"+request.getDownloadUrl());
                    } finally {
                        try{
                            if (output != null) {
                                output.close();
                            }
                            if (input != null){
                                input.close();
                            }
                        }catch (IOException e){
                        }
                        if (connection != null){
                            connection.disconnect();
                        }
                    }
                }
                // over
                request = null;
            }
            isRunning = false;
        }
    }
}
