package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SaveMyAppsService extends android.app.Service
{

    String CURRENT_PACKAGE_NAME = "com.example.myapplication";
    String lastAppPN = "";
    boolean noDelay = false;
    public static SaveMyAppsService instance;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleMethod();
        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
        Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);
        instance = this;
        return START_NOT_STICKY;
    }

    private void scheduleMethod() {
        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();
       Runnable IterateInstructions;

       /*
       final Handler h = new Handler();
       IterateInstructions = new Runnable() {
           @Override
           public void run() {
               Log.d("Handler", "Checking");
               SharedPreferences sharedPreferences = getSharedPreferences("SERVICE", MODE_PRIVATE);
               int flagStatus = sharedPreferences.getInt("FLAG", 0);
               Log.d("Flag Status", ""+flagStatus);
               if(flagStatus == 1){
                   Log.d("gfg","stopped");
                   stopSelf();
               }else{
                   checkRunningApps();
                   //h.postDelayed(this, 1000);
               }

           }
       };
       h.postDelayed(IterateInstructions, 100);

       */

        final Handler h = new Handler();
        IterateInstructions = new Runnable() {
            @Override
            public void run() {
                Log.d("Handler", "Checking");
                SharedPreferences sharedPreferences = getSharedPreferences("SERVICE", MODE_PRIVATE);
                int flagStatus = sharedPreferences.getInt("FLAG", 0);
                Log.d("Flag Status", ""+flagStatus);
                if(flagStatus == 1){
                    Log.d("gfg","stopped");
                    stopSelf();
                } else {
                    checkRunningApps();
                    h.postDelayed(this, 1000);}
            }
        };
        h.postDelayed(IterateInstructions, 100);
    }

    public void checkRunningApps() {
       // double gp = 0.0;
        SharedPreferences sharedPreferences = getSharedPreferences("SCORE", MODE_PRIVATE);
        String scoreString = sharedPreferences.getString("SS","0");
        double trustScoreshared  = Double.parseDouble(scoreString);

        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> RunningTask = mActivityManager.getRunningAppProcesses();
        String appPackage = printForegroundTask();

        if(trustScoreshared>0.85){
/*
            if(appPackage.contains("phonepe")||appPackage.contains("paytm")||appPackage.contains("paisa")||appPackage.contains("paypal")){
*/
             //   Toast.makeText(getApplicationContext(),"Safe to Access", Toast.LENGTH_SHORT).show();

               /* Intent intent = new Intent(this,AppRestrictionHomeScreen.class);
                startActivity(intent);
            }*/
        }else if(0.5<trustScoreshared && trustScoreshared<0.85) {
            if(appPackage.contains("phonepe")||appPackage.contains("paytm")||appPackage.contains("paisa")||appPackage.contains("paypal")){
                Toast.makeText(getApplicationContext(),"Secure Apps Restricted", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this,AppRestrictionHomeScreen.class);
                startActivity(intent);
            }
        }else if(0.1<trustScoreshared && trustScoreshared<0.5) {
            if(appPackage.contains("phonepe")||appPackage.contains("paytm")||appPackage.contains("paisa")||appPackage.contains("paypal")||appPackage.contains("whatsapp")||appPackage.contains("insta")||appPackage.contains("face")||appPackage.contains("gallery")){
                Toast.makeText(getApplicationContext(),"Secure & Medium Level Apps Restricted", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this,AppRestrictionHomeScreen.class);
                startActivity(intent);
            }
        }else{
            if(appPackage.contains("phonepe")||appPackage.contains("paytm")||appPackage.contains("paisa")||appPackage.contains("paypal")||appPackage.contains("whatsapp")||appPackage.contains("insta")||appPackage.contains("face")||appPackage.contains("gallery")||appPackage.contains("ola")||appPackage.contains("zomato")||appPackage.contains("clock") ||appPackage.contains("diner")||appPackage.contains("swiggy")){
                Toast.makeText(getApplicationContext(),"All Apps Restricted", Toast.LENGTH_LONG).show();

               /* Intent intent = new Intent(this,Lock.class);
                startActivity(intent);*/
                Intent intent = new Intent(this,AppRestrictionHomeScreen.class);
                startActivity(intent);
            }
        }
        /*if(appPackage.contains("phonepe")||appPackage.contains("paytm")||appPackage.contains("paisa")||appPackage.contains("paypal")){
            Toast.makeText(getApplicationContext(),"Secure Apps Restricted", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this,AppRestrictionHomeScreen.class);
            startActivity(intent);
        }*/
        for(ActivityManager.RunningAppProcessInfo iterator: RunningTask){
            String pname = iterator.processName;
            Log.d("Running", pname);
        }
    }
    private String printForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager)this.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("adapter", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    public static void stop() {
        if (instance != null) {
            instance.stopSelf();
        }
    }
}