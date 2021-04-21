package com.example.attendance.Deadline;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeadlineAPI {
    @GET("deadline/getStudentDeadlines")
    Call< List<Deadline> > getDeadlines(@Query("userId") Integer userId);

    @POST("deadline/updateDueDate")
    Call<Integer> updateDueDate(@Query("id") int id, @Query("date") String date);
}
