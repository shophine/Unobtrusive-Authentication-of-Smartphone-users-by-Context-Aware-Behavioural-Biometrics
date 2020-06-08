package com.example.myapplication;

import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private LineChart lineChart;
    DataFromCSV dataFromCSV;
    ArrayList<Entry> yValues = new ArrayList<>();

    private Button validate;
    private String userIDValue, segmentNoValue;
    EditText userID,segmentNo;
    public UserDetails userDetails;
    private Button btnSecurityStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*lineChart = (LineChart)findViewById(R.id.linechart);
       *//* lineChart.setOnChartGestureListener(MainActivity.this);
        lineChart.setOnChartValueSelectedListener(MainActivity.this);*//*

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        *//*ArrayList<Entry> yValues = new ArrayList<>();*//*

        //mylogic

        readData();

        *//*
        yValues.add(new Entry(0,60f));
        yValues.add(new Entry(1,50f));
        yValues.add(new Entry(2,55f));
        yValues.add(new Entry(3,40f));
        yValues.add(new Entry(4,45f));
        yValues.add(new Entry(5,30f));*//*

        LineDataSet set1 = new LineDataSet(yValues,"Data Set 1");
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
*/




        // set flag for service
        SharedPreferences sharedPreferences = getSharedPreferences("SERVICE", MODE_PRIVATE);
        SharedPreferences.Editor flagEditor = sharedPreferences.edit();
        flagEditor.putInt("FLAG", 0);
        flagEditor.commit();

        Intent intent = new Intent(this,SaveMyAppsService.class);
        if (!isAccessGranted()) {
            Intent optionIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(optionIntent);
        }
        startService(intent);

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Fetching");

        userID = (EditText) findViewById(R.id.userID);
        segmentNo = (EditText) findViewById(R.id.segmentNo);
        validate = (Button)findViewById(R.id.validate);
        btnSecurityStatus = (Button) findViewById(R.id.btnSecurityStatus);

        lineChart = (LineChart)findViewById(R.id.linechart);
       /* lineChart.setOnChartGestureListener(MainActivity.this);
        // lineChart.setOnChartValueSelectedListener(MainActivity.this);*/

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);


        ArrayList<Entry> yValues = new ArrayList<>();

        /*readData();


        LineDataSet set1 = new LineDataSet(yValues,"Data Set 1");
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
*/




        btnSecurityStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResponseEntity responseEntity = new ResponseEntity();
                RestClientImplementation.getMLStatus(responseEntity, new ResponseEntity.restInterface() {
                    @Override
                    public void onResponse(ResponseEntity entity, VolleyError error) {
                        if(error == null){
                            double score = generateTrustScore(entity.getOutput(), entity.getConstant());
                            // writing score to shared preference
                            SharedPreferences sharedPreferences = getSharedPreferences("SCORE",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("SS", ""+score);
                            editor.commit();
                           /* if(score>0.8){*/
                                Intent serviceIntent = new Intent(MainActivity.this, SaveMyAppsService.class);
                                startService(serviceIntent);
                          //  }
                            Toast.makeText(MainActivity.this, "Score retrieved", Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, ""+entity.getConstant(),Toast.LENGTH_SHORT).show();
                        }else {
                            Log.d("resError : ",""+error.toString());
                            Toast.makeText(MainActivity.this, "Unable to retrieve Security Status"+error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, MainActivity.this);
            }
        });
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
 //              dialog.show();
                RestClientImplementation.getResponse(userDetails, new ResponseEntity.restInterface() {
                    @Override
                    public void onResponse(ResponseEntity entity, VolleyError error) {
//                       dialog.hide();
                        btnSecurityStatus.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,entity.toString(),Toast.LENGTH_LONG).show();
                        //Intent intent = new Intent(MainActivity.this, DotsLoader.class);
                        //startActivity(intent);
                    }
                },MainActivity.this);

            }
        });



//        //mylogic
//
//        readData(tempSegment);
//
//
//        yValues.add(new Entry(0,60f));
//        yValues.add(new Entry(1,50f));
//        yValues.add(new Entry(2,55f));
//        yValues.add(new Entry(3,40f));
//        yValues.add(new Entry(4,45f));
//        yValues.add(new Entry(5,30f));
//
//        LineDataSet set1 = new LineDataSet(yValues,"Data Set 1");
//        set1.setFillAlpha(110);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//
//        LineData lineData = new LineData(dataSets);
//        lineChart.setData(lineData);



    }
    public  double generateTrustScore(String output, String constant) {
        int o;
        double c,gaitScore,gpsScore,trustScore;
        Log.d("result/output : ",""+output);
        double g = 1.0;
        double g1 = 0.0;
        Log.d("result/constant : ",""+constant);
        o = Integer.parseInt(output);
        c = Double.parseDouble(constant);
        gaitScore = c * 0.9;
        if(gaitScore<0.08){
            gpsScore = g1*0.1;
        }else{
            gpsScore = g*0.1;
        }

        trustScore = gaitScore + gpsScore;
        return trustScore;


    }

    public void getInput(){
        userIDValue = userID.getText().toString();
        segmentNoValue = segmentNo.getText().toString();

        readData(segmentNoValue);


        /*LineDataSet set1 = new LineDataSet(yValues,"Data Set 1");
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);


        userDetails = new UserDetails(userIDValue,segmentNoValue);
*/
        userDetails = new UserDetails(userIDValue,segmentNoValue);
        Toast.makeText(this,"UserID : "+userDetails.getUserID()+"\tSegmentNo : "+userDetails.getSegmentNo(),Toast.LENGTH_LONG).show();
    }
    private boolean isAccessGranted() {
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
    }

    private List<DataFromCSV> list = new ArrayList<>();
    private void readData(String seg){
        int segNo,count;
        count=0;
        segNo= Integer.parseInt(seg);


        InputStream inputStream = getResources().openRawResource(R.raw.featuretest);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

        //int seg =1;
        String line = "";
        try {

            while((line = bufferedReader.readLine())!=null){
                count++;

                Log.d("MainActivity/Line","Line : "+line);

                if(segNo==count) {

                    String[] tokens = line.split(",");



                    dataFromCSV = new DataFromCSV();
                    int j=0;
                    int x=1;
                    while(j<42){
                        dataFromCSV.setX(j);

                        dataFromCSV.setY(Float.parseFloat(tokens[j]));
                        list.add(dataFromCSV);

                        Log.d("MainActivity/Data", "JustCreated : " + dataFromCSV);


                        yValues.add(new Entry(dataFromCSV.getX(), dataFromCSV.getY()));

                        j++;
                        x++;

                    }
                   /* dataFromCSV.setX(Float.parseFloat(tokens[0]));
                    dataFromCSV.setY(Float.parseFloat(tokens[1]));*/

                    /*list.add(dataFromCSV);

                    Log.d("MainActivity/Data", "JustCreated : " + dataFromCSV);


                    yValues.add(new Entry(dataFromCSV.getX(), dataFromCSV.getY()));*/
                }

             //   count++;


            }
        }catch (IOException e){
            Log.d("MainActivity/Error","Error reading data"+line,e);
            e.printStackTrace();
        }

        LineDataSet set1 = new LineDataSet(yValues,"Feature value for the selected segment");
        set1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);



    }



    }

