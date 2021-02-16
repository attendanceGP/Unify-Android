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

    @GET("student/getStudentCourses")
    Call<String[]> getStudentCourses(@Query("studentID")Integer studentID);

    @GET("student/attend")
    Call<Void> attend(@Query("studentID")Integer studentID, @Query("courseName")String courseName, @Query("date")String date,@Query("TAAttendanceID") long attendanceID);

    @GET("student/checkAttendance")
    Call<Attendance> checkAttendance(@Query("studentID")Integer studentID, @Query("courses")String[] courses, @Query("date")String date);
}
