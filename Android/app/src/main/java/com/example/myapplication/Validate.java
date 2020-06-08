package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Validate extends AppCompatActivity {
    private String userIDValue, segmentNoValue;
    EditText userID,segmentNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = (EditText) findViewById(R.id.userID);
        segmentNo = (EditText) findViewById(R.id.segmentNo);
    }

    public void validate(View view){
        getInput();

    }
    public void getInput() {
        userIDValue = userID.getText().toString();
        segmentNoValue = segmentNo.getText().toString();

        Toast.makeText(this,"Success",Toast.LENGTH_LONG).show();
    }

}
