package com.example.heartonomi_7;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiClient mInstance;
    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        //replaced "http://192.168.0.180:8000/api/"
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.145:8000/api/") //insert the API URL here
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    public static synchronized ApiClient getInstance(){
        if(mInstance == null){
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public static JsonPlaceHolderApi getUserService(){
        JsonPlaceHolderApi userservice = getRetrofit().create(JsonPlaceHolderApi.class);
        return userservice;
    }

    public static JsonPlaceHolderApiBP getBPService(){
        return getRetrofit().create(JsonPlaceHolderApiBP.class);
    }
}
