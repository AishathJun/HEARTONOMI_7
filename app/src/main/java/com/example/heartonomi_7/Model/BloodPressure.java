package com.example.heartonomi_7.Model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BloodPressure extends RealmObject {
    @PrimaryKey
    private int id;
    private String username;
    private int systolic;
    private int diastolic;
    private int hearrate;
    private String currentTime;

    public BloodPressure() {
    }

    public BloodPressure(String username, int systolic, int diastolic, int hearrate, String currentTime) {
        this.username = username;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.hearrate = hearrate;
        this.currentTime = currentTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getHearrate() {
        return hearrate;
    }

    public void setHearrate(int hearrate) {
        this.hearrate = hearrate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
