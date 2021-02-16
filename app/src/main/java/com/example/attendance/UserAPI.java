package com.example.attendance;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("user/login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @GET("user/gettoken")
    Call<String> getToken(@Query("id") Integer id);

    @GET("attendance/getStudentsAttendanceList")
    Call<List<Attendance>> getStudentsList(@Query("courseID") String courseID, @Query("group") String Group, @Query("date") String date);

    @FormUrlEncoded
    @POST("attendance/SetStudentAbsent")
    Call<ResponseBody> setAbsent(@Field("courseID") String courseID, @Field("group") String group,
                           @Field("date") String date, @Field("studentID") Integer studentID);

    @FormUrlEncoded
    @POST("attendance/SetStudentPresent")
    Call<ResponseBody> setPresent(@Field("courseID") String courseID,
                                  @Field("group") String group, @Field("date") String date, @Field("studentID") Integer studentID);

    @GET("attendance/getStudent")
    Call<Attendance> getStudent(@Query("courseID") String courseID, @Query("group") String Group,
                                @Query("date") String date, @Query("studentID") Integer studentID);
}
