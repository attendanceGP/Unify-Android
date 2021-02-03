package com.example.attendance;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("user/login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @GET("user/gettoken")
    Call<String> getToken(@Query("id") Integer id);
}
