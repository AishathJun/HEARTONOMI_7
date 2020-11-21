package com.example.heartonomi_7.Model;

import java.io.Serializable;

public class PredictBloodPressureResponse implements Serializable {
    private int Systolic;
    private int Diastolic;

    public PredictBloodPressureResponse(int systolic, int diastolic) {
        Systolic = systolic;
        Diastolic = diastolic;
    }

    public int getSystolic() {
        return Systolic;
    }

    public void setSystolic(int systolic) {
        Systolic = systolic;
    }

    public int getDiastolic() {
        return Diastolic;
    }

    public void setDiastolic(int diastolic) {
        Diastolic = diastolic;
    }
}
