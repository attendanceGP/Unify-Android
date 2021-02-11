package com.example.attendance;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("user/login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @GET("user/gettoken")
    Call<String> getToken(@Query("id") Integer id);

    @GET("attendance/getStudentsAttendanceList")
    Call<List<Attendance>> getStudentsList(@Query("courseID") String courseID, @Query("group") String Group, @Query("date") String date);
}
