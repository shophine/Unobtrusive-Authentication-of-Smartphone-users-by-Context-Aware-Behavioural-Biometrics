package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button validate;
    private String userIDValue, segmentNoValue;
    EditText userID,segmentNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = (EditText) findViewById(R.id.userID);
        segmentNo = (EditText) findViewById(R.id.segmentNo);
        validate = (Button)findViewById(R.id.validate);
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
                Intent intent = new Intent(MainActivity.this, DotsLoader.class);
                startActivity(intent);

            }
        });


    }

    public void getInput(){
        userIDValue = userID.getText().toString();
        segmentNoValue = segmentNo.getText().toString();

        UserDetails userDetails = new UserDetails(userIDValue,segmentNoValue);

        Toast.makeText(this,"UserID : "+userDetails.getUserID()+"\tSegmentNo : "+userDetails.getSegmentNo(),Toast.LENGTH_LONG).show();
    }


    }

