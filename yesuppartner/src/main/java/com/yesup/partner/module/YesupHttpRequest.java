package com.yesup.partner.module;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by derek on 4/20/16.
 */
public class YesupHttpRequest {
    public static final int STATUS_CONNECT_FAILED = -2;
    public static final int STATUS_FAILED = -1;
    public static final int STATUS_NEW = 0;
    public static final int STATUS_PROGRESSED = 1;
    public static final int STATUS_SUCCESSED = 2;

    private int requestId;
    private int requestType;
    private String requestMethod = "GET"; // GET or POST
    private String downloadUrl;
    private String saveFileName;
    private int status;
    private int percent; // the progress of download file,0~100
    private Map<String, String> reqHeaderParameter = new HashMap<>();
    private Map<String, String> reqDataParameter = new HashMap<>();

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public Map<String, String> getRequestHeaderParameter() {
        return reqHeaderParameter;
    }
    public void setRequestHeaderParameter(String key, String value) {
        reqHeaderParameter.put(key, value);
    }

    public Map<String, String> getRequestDataParameter() {
        return reqDataParameter;
    }
    public void setRequestDataParameter(String key, String value) {
        reqDataParameter.put(key, value);
    }

    public void onRequestProgressed(int percent) {
        setPercent(percent);
    }

    public void onRequestCompleted(int result) {
        setStatus(result);
    }
}
