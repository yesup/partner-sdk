package com.yesup.partner.tools;

import android.util.Log;

import com.yesup.partner.module.YesupHttpRequest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private final List<YesupHttpRequest> requestQueue = new LinkedList<>();

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

    public int newDownload(YesupHttpRequest request) {
        if (request.getDownloadUrl() == null || request.getSaveFileName() == null){
            request.setRequestId(-1);
            return -1;
        }
        synchronized (requestQueue) {
            request.setStatus(YesupHttpRequest.STATUS_NEW);
            requestQueue.add(request);
            requestQueue.notifyAll();
        }
        Log.v("DownloadManagerLite", "New Request["+request.getRequestId()+"]:"
                +request.getDownloadUrl()+"->"+request.getSaveFileName());
        return request.getRequestId();
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
            YesupHttpRequest request = null;
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
                        if (request.getRequestMethod().length() <= 0){
                            request.setRequestMethod("GET");
                        }
                        // set connection parameters
                        connection.setRequestMethod(request.getRequestMethod());
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        connection.setUseCaches(false);
                        connection.setDoInput(true);
                        if (request.getRequestDataParameter().size() > 0) {
                            connection.setDoOutput(true);
                        }
                        // set HTTP header parameters
                        for (Map.Entry<String, String> entry : request.getRequestHeaderParameter().entrySet()){
                            connection.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                        // begin connecting
                        connection.connect();
                        // send data body
                        if (request.getRequestDataParameter().size() > 0) {
                            OutputStream os = connection.getOutputStream();
                            int index = 0;
                            String parameter;
                            for (Map.Entry<String, String> entry : request.getRequestDataParameter().entrySet()){
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
                            request.setStatus(YesupHttpRequest.STATUS_PROGRESSED);
                            request.setPercent(0);
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
                                    request.onRequestProgressed(percent);
                                }
                                output.write(data, 0, count);
                            }
                            request.onRequestProgressed(100);
                            request.onRequestCompleted(YesupHttpRequest.STATUS_SUCCESSED);
                            Log.v("DownloadManagerLite", "Download completed:"+request.getDownloadUrl());
                        }else{
                            request.onRequestCompleted(YesupHttpRequest.STATUS_CONNECT_FAILED);
                            Log.v("DownloadManagerLite", "Connect failed[HTTP-"+connection.getResponseCode()+"]:"+request.getDownloadUrl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.onRequestCompleted(YesupHttpRequest.STATUS_FAILED);
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
