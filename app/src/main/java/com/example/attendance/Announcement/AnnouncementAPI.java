package com.example.attendance.Announcement;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AnnouncementAPI {

    @POST("announcement/postannouncement")
    Call<Integer> postDeadline(@Query("userId") int userId, @Query("courseId") String courseId,
                               @Query("postedDate") String postedDate,@Query("title") String title,
                               @Query("post") String post);
}
