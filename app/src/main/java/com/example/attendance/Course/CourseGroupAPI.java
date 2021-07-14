package com.example.attendance.Course;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CourseGroupAPI {
    @GET("getUserCoursesAndGroups")
    Call<List<CourseGroup>> getUserCoursesAndGroups(@Query("userId") Integer userId);
}
