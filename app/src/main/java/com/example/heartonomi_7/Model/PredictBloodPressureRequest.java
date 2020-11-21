package com.example.heartonomi_7.Model;

public class PredictBloodPressureRequest {
    private String userName;
    private int systolic;
    private int diastolic;
    private int heartRate;
    private String readingTime;

    public PredictBloodPressureRequest() {
    }

    public PredictBloodPressureRequest(String userName, int systolic, int diastolic, int heartRate, String readingTime) {
        this.userName = userName;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.readingTime = readingTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(String readingTime) {
        this.readingTime = readingTime;
    }
}
