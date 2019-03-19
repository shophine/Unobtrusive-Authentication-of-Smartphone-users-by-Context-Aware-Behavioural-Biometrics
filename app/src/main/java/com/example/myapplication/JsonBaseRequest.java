package com.example.myapplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class JsonBaseRequest extends JsonObjectRequest {
    public JsonBaseRequest(int method, String url, JSONObject jsonObj, Listener<JSONObject> listener,
                           ErrorListener errorListener) {
        super(method, url, jsonObj, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(
                10000, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public JsonBaseRequest(int method, String url, JSONObject jsonObj, Listener<JSONObject> listener,
                           ErrorListener errorListener, int timeOut, int retries) {
        super(method, url, jsonObj, listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(
                timeOut, retries,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }

}
