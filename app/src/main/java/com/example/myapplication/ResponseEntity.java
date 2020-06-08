package com.example.myapplication;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseEntity {

    String output,constant;

    public ResponseEntity(String output, String constant) {
        this.output = output;
        this.constant = constant;
    }

    public ResponseEntity() {
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public interface restInterface {
        void onResponse(ResponseEntity entity, VolleyError error);
    }


}
