package com.example.myapplication;
//to_be_predicted.txt
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject getJsonObjectAsParams()
    {
        JSONObject jsonObject=null;
        Gson gson=new Gson();
        String objectString=gson.toJson(this);
        if(jsonObject==null)
        {
            try {
                jsonObject=new JSONObject(objectString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


}
