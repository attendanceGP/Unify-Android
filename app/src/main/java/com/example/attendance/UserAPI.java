package com.example.attendance;

import java.util.ArrayList;
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


    @POST("ta/postattendance")
    Call<Void> taStartAttendance(@Query("date") String date,
                          @Query("userGroup") String userGroup, @Query("courseId") String courseId, @Query("userId") Integer userId);

    @GET("ta/getTaughtCourses")
    Call<ArrayList<String>> getTaughtCourses(@Query("userId") Integer userId);

    @POST("ta/closeTAattendance")
    Call<Void> closeTAattendance(@Query("date") String date,
                                 @Query("userGroup") String userGroup, @Query("courseId") String courseId, @Query("userId") Integer userId);

    @GET("student/getStudentCourses")
    Call<String[]> getStudentCourses(@Query("studentID")Integer studentID);

    @GET("student/attend")
    Call<Void> attend(@Query("studentID")Integer studentID, @Query("courseName")String courseName, @Query("date")String date,@Query("TAAttendanceID") long attendanceID);

    @GET("student/checkAttendance")
    Call<Attendance> checkAttendance(@Query("studentID")Integer studentID, @Query("courses")String[] courses, @Query("date")String date);

    @GET("attendance/getStudentsAttendanceList")
    Call<List<Attendance>> getStudentsList(@Query("courseID") String courseID, @Query("group") String Group, @Query("date") String date);


    @GET("attendance/setStudentAbsence")
    Call<Attendance> setAbsence(@Query("courseID") String courseID, @Query("group") String Group,
                                @Query("date") String date, @Query("studentID") Integer studentID,
                                @Query("absent") boolean absent);
    @GET("ta/getTA")
    Call<TeachingAssistant> getTA(@Query("id") Integer id);
}
