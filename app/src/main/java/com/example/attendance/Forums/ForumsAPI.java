package com.example.attendance.Forums;


import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ForumsAPI {
    @GET("forums/getStudentForums")
    Call<List<Post>> getForums(@Query("userId") Integer userId);

    @GET("forums/getPostReplies")
    Call<List<Post>> getReplies(@Query("postId") Integer postId);

    @POST("forums/addPost")
    Call<String> addPost(@Query("userId") Integer userId, @Query("fk_course_code") String courseCode,
                         @Query("date") String date, @Query("title") String title, @Query("content") String content);

    @POST("forums/removePost")
    Call<String> removePost(@Query("postId") Integer postId);


    @POST("forums/addReply")
    Call<String> addReply(@Query("userId") int userId, @Query("postId") Integer postId,
                         @Query("date") String date, @Query("description") String description);

    @POST("forums/removeReply")
    Call<String> removeReply(@Query("postId") Integer postId, @Query("replyId") Integer replyId);
}
