package com.example.attendance.Announcement;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AnnouncementAPI {

    @POST("announcement/postannouncement")
    Call<Integer> postAnnouncement(@Query("userId") int userId, @Query("courseId") String courseId,
                                   @Query("postedDate") String postedDate, @Query("title") String title,
                                   @Query("post") String post);

    @DELETE("announcement/deletePostedAnnouncement")
    Call<Integer> deleteAnnouncement(@Query("id") int id);

    @GET("announcement/getTaAnnouncements")
    Call< List<Announcement> > getTaAnnouncements(@Query("userId") Integer userId);

    @GET("announcement/getStudentAnnouncements")
    Call< List<Announcement> > getStudentAnnouncements(@Query("userId") Integer userId);

    @GET("announcement/getFilteredStudentAnnouncements")
    Call< List<Announcement> > getFilteredStudentAnnouncements(@Query("userId") Integer userId,@Query("courseId") String[] courseId);
}
