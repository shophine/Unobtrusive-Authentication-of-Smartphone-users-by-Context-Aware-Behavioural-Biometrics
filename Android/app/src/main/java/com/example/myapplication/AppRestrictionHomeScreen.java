package com.example.myapplication;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AppRestrictionHomeScreen extends AppCompatActivity {

    private Button sumbit,lockPhone,masterLock;
    private String securityQuestionValue;
    private EditText securityQuestion;
    private TextView trustScoreDisplay;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    Result result = new Result();
    private String temp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_restricted_home);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        sumbit = (Button)findViewById(R.id.submit);
        securityQuestion = (EditText)findViewById(R.id.securityQuestion);
        trustScoreDisplay = (TextView)findViewById(R.id.trustScoreDisplay);
        masterLock = (Button)findViewById(R.id.lock);

      //  temp = result.getTrustScore()l;

        SharedPreferences sharedPreferences = getSharedPreferences("SCORE", MODE_PRIVATE);
        String scoreString = sharedPreferences.getString("SS","0");
        double trustScoreshared  = Double.parseDouble(scoreString);


        trustScoreDisplay.setText("Trust Score : "+Double.toString(trustScoreshared));

        lockPhone = (Button)findViewById(R.id.lockphone);
        lockPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = devicePolicyManager.isAdminActive(compName);

                if (active) {
                    devicePolicyManager.lockNow();
                } else {
                    Toast.makeText(AppRestrictionHomeScreen.this, "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
                }


            }
        });
       // trustScoreDisplay.setText("Blocked");

        sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                securityQuestionValue = securityQuestion.getText().toString();
                if(securityQuestionValue.equals("2020")){
                    //stop the service
                    Toast.makeText(getApplicationContext(),"Access Granted",Toast.LENGTH_LONG).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("SERVICE", MODE_PRIVATE);
                    SharedPreferences.Editor flagEditor = sharedPreferences.edit();
                    flagEditor.putInt("FLAG", 1);
                    flagEditor.commit();


                }else {
                    securityQuestion.setError("Wrong Answer!!!");
                    devicePolicyManager.lockNow();
                }

            }
        });
        lockPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicePolicyManager.lockNow();


               /* Intent intent = new Intent(AppRestrictionHomeScreen.this,Lock.class);
                startActivity(intent);*/
            }
        });




    }
}
