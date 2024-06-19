package com.example.facerecognition20.Model;

public class ShareModel {

    private String reportText,userId,key, imageUrl;
    private long timestamp;

    public ShareModel() {
    }

    public ShareModel( String reportText, String imageUrl) {
        this.reportText = reportText;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long Timestamp) {
        this.timestamp = Timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String itemId) {
        this.userId = itemId;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

}
