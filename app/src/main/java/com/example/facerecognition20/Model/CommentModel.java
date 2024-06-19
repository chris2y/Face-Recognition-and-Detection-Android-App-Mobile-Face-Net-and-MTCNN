package com.example.facerecognition20.Model;

public class CommentModel {

    private String commentText,userId;
    private long timestamp;

    public CommentModel() {
    }


    public CommentModel(String commentText, String userId, long timestamp) {
        this.commentText = commentText;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long Timestamp) {
        this.timestamp = Timestamp;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String itemId) {
        this.userId = itemId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

}
