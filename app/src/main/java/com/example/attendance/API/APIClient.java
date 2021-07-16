package com.example.attendance.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

}