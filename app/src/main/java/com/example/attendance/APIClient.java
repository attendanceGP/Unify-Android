package com.example.attendance;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    static Retrofit getClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.7:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit;
    }

}
