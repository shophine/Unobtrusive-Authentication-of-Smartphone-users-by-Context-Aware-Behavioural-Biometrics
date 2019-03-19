package com.example.myapplication;
//to_be_predicted.txt
import java.util.List;

public class UserDetails{
    private String userID, segmentNo;

    public UserDetails(String userID, String segmentNo) {
        this.userID = userID;
        this.segmentNo = segmentNo;

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSegmentNo() {
        return segmentNo;
    }

    public void setSegmentNo(String segmentNo) {
        this.segmentNo = segmentNo;
    }


}
