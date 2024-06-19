package com.example.facerecognition20.Model;
import com.google.firebase.Timestamp;
public class UserModel {
    private String userId;
    private String email;
    Timestamp joinedTimestamp;

    public UserModel() {
    }

    public UserModel(String email , String userId) {
        this.userId = userId;
        this.email = email;
        this.joinedTimestamp = Timestamp.now();
    }

    public Timestamp getJoinedTimestamp() {
        return joinedTimestamp;
    }

    public void setJoinedTimestamp(Timestamp joinedTimestamp) {
        this.joinedTimestamp = joinedTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
