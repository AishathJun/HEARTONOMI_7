package com.example.heartonomi_7.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Patient extends RealmObject {
    private String name;
    private String username;
    private String password;
    private String weight;
    private String height;
    public RealmList<BloodPressure> bp;

    public Patient() {
    }
//    public Patient(RealmList<BloodPressure> bp) {
//        this.bp = bp;
//    }
    public Patient(String name, String username, String password, String weight, String height) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.weight = weight;
        this.height = height;
    }

    public RealmList<BloodPressure> getBp() {
        return bp;
    }

    public void setBp(RealmList<BloodPressure> bp) {
        this.bp = bp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
