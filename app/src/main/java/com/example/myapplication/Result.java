package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class Result {
    private double gaitScore, gpsScore, trustScore;

    public Result(double gaitScore, double gpsScore) {
        this.gaitScore = gaitScore;
        this.gpsScore = gpsScore;
     //   this.trustScore = trustScore;
    }

    public Result() {
    }

    public double getGaitScore() {
        return gaitScore;
    }

    public void setGaitScore(double gaitScore) {
        this.gaitScore = gaitScore;
    }

    public double getGpsScore() {
        return gpsScore;
    }

    public void setGpsScore(double gpsScore) {
        this.gpsScore = gpsScore;
    }

    public double getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(double trustScore) {
        this.trustScore = trustScore;
    }
}
