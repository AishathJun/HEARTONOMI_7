package com.example.heartonomi_7;

import com.example.heartonomi_7.Model.BloodPressureAPI;
import com.example.heartonomi_7.Model.PredictBloodPressureRequest;
import com.example.heartonomi_7.Model.PredictBloodPressureResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApiBP {

    @GET("readings")
    Call<List<BloodPressureAPI>> getBP();

    @POST("readings")
    Call<BloodPressureAPI> createBP (@Body BloodPressureAPI bloodPressureAPI);

    @POST("predict")
    Call<PredictBloodPressureResponse> displayPredict (@Body PredictBloodPressureRequest predictBloodPressureRequest);

    @FormUrlEncoded
    @POST("readings")
    Call<BloodPressureAPI> createBP(
            @Field("userName") String username,
            @Field("systolic") String systaolic,
            @Field("diastolic") String diastolic,
            @Field("heartRate") String hearrate,
            @Field("readingTime") String currentTime
    );
}
