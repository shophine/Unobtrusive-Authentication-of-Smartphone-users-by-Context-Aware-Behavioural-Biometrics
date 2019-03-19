package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import steelkiwi.com.library.DotsLoaderView;

public class DotsLoader extends AppCompatActivity {

    DotsLoaderView dotsLoaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dots_loader);

        dotsLoaderView = (DotsLoaderView)findViewById(R.id.dotsLoader);

        downloadDemo();
    }

    private void downloadDemo() {
        AsyncTask<String,String,String> demoAsync = new AsyncTask<String, String, String>() {
            @Override
            protected void onPreExecute() {
                dotsLoaderView.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return "done";
            }

            @Override
            protected void onPostExecute(String s) {
                if(s.equals("done"))
                    dotsLoaderView.hide();

                Intent intent = new Intent(DotsLoader.this,Result.class);
                startActivity(intent);

            }
        };

        demoAsync.execute();
    }
}
