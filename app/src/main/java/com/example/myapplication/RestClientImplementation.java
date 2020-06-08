package com.example.myapplication;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import static java.lang.Math.round;

public class RestClientImplementation {

    public static Result result = new Result();

    public static String url = "http://192.168.43.90:3030";

    public static String getAbsoluteURL(String relativeURL) {
        return url + relativeURL;
    }

    public static void getResponse(final UserDetails userDetails, final ResponseEntity.restInterface restInterface, final Context ctx) {

        RequestQueue queue = VolleySingleton.getInstance(ctx).getRequestQueue();
        JSONObject obj = userDetails.getJsonObjectAsParams();
        JsonBaseRequest request = new JsonBaseRequest(Request.Method.POST, getAbsoluteURL("/post"), obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                ResponseEntity entity = new ResponseEntity();
                entity = gson.fromJson(response.toString(), ResponseEntity.class);
                restInterface.onResponse(entity, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });
        queue.add(request);
    }

    public static void getMLStatus(final ResponseEntity responseEntity, final ResponseEntity.restInterface restInterface, final Context context) {
        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        final Gson gson = new Gson();
        JsonBaseRequest getRequest = new JsonBaseRequest(Request.Method.GET, getAbsoluteURL("/getoutput"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ResponseEntity rEntity = gson.fromJson(response.toString(), ResponseEntity.class);
                    responseEntity.setConstant(rEntity.getConstant());
                    responseEntity.setOutput(rEntity.getOutput());
                    restInterface.onResponse(responseEntity, null);
                } catch (Exception e) {
                    restInterface.onResponse(responseEntity, new VolleyError());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                restInterface.onResponse(null, error);
            }
        });
        queue.add(getRequest);
    }

    /*private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }*/
}

