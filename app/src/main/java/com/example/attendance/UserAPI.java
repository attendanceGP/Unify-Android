package com.example.attendance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("user/login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @GET("user/gettoken")
    Call<String> getToken(@Query("id") Integer id);

    @POST("ta/postattendance")
    Call<String> taStartAttendance(@Query("date") String date,
                          @Query("userGroup") String userGroup, @Query("courseId") String courseId, @Query("userId") Integer userId);

    @GET("ta/getTaughtCourses")
    Call<ArrayList<String>> getTaughtCourses(@Query("userId") Integer userId);

}
