package com.example.attendance.Home;

import com.example.attendance.Home.Attendance;
import com.example.attendance.Home.TeachingAssistant;
import com.example.attendance.Home.User;

import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
    @GET("ta/updateTALocation")
    Call<Void>updateTaLocation(@Query("id") Integer id, @Query("longitude")double longitude,@Query("latitude")double latitude);





    @GET("attendance/setStudentAbsence")
    Call<Attendance> setAbsence(@Query("courseID") String courseID, @Query("group") String Group,
                                @Query("date") String date, @Query("studentID") Integer studentID,
                                @Query("absent") boolean absent, @Query ("penalty") boolean penalty);

    @POST("attendance/confirmAttendance")
    Call<Void> confirmList(@Query("courseID") String courseID, @Query("group") String Group,
                                 @Query("date") String date);

    //getting already recorded groups for attendance for a certain day and course
    @GET("ta/getExistingAttendanceGroups")
    Call<ArrayList<String>> getExistingAttendanceGroups(@Query("date") String date,@Query("userGroup") String userGroup,
                                                   @Query("courseId") String courseId);

}
