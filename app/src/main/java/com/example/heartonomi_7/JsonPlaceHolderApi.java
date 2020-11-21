package com.example.heartonomi_7;

import com.example.heartonomi_7.Model.Patient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("loginusers")
    Call<Patient> getPatients(
            @Query("username") String username
    );

    @GET("loginusers")
    Call<List<LoginResponse>> loginUser();

    @POST("loginusers")
    Call<Patient> createPatient (@Body Patient patient);

    @FormUrlEncoded
    @POST("loginusers")
    Call<Patient> createPatient(
            @Field("name") String name,
            @Field("username") String username,
            @Field("password") String password,
            @Field("weight") String weight,
            @Field("height") String height
    );
}
