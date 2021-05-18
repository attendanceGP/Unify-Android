package com.example.attendance.Forums;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.attendance.Deadline.Deadline;

import java.util.List;

@Dao
public interface ForumsDao {
//    Posts
    @Query("SELECT * FROM Post ORDER BY date DESC ")
    List<Post> getAllPosts();

    @Query("SELECT * FROM Post WHERE id IN (:postsIds) ORDER BY date DESC ")
    List<Post> loadPostsAllByIds(Integer[] postsIds);

    @Query("SELECT * FROM Post WHERE id = :postId ")
    Post loadPostById(Integer postId);

    @Insert
    void insertAllPosts(Post... posts);

    @Delete
    void deletePosts(Post post);

    @Query("DELETE FROM Post WHERE id = :postId ")
    void deletePostById(Integer postId);

    @Query("SELECT * FROM Post WHERE is_starred = 1")
    List<Post> getAllStarred();

    @Query("SELECT * FROM Post WHERE fk_user_id = :user_id")
    List<Post> getAllPostedByMe(Integer user_id);

    @Query("SELECT * FROM Post WHERE fk_course_code = :course_code")
    List<Post> getAllPostsByCourse(String course_code);

    @Query("SELECT COUNT(*) FROM Post WHERE id = :id")
    boolean isExistsPosts(Integer id);

    @Query("UPDATE Post SET is_starred = 1 WHERE id = :id")
    void changeToStarred(Integer id);

    @Query("UPDATE Post SET is_starred = 0 WHERE id = :id")
    void changeToUnStarred(Integer id);


//    Replies
    @Query("SELECT * FROM Reply")
    List<Reply> getAllReplies();

    @Query("SELECT * FROM Reply WHERE id IN (:repliesIds)")
    List<Reply> loadAllReplyByIds(Integer[] repliesIds);

    @Insert
    void insertAllReply(Reply... replies);

    @Delete
    void deleteReply(Reply reply);

    @Query("DELETE FROM Reply WHERE id = :replyId ")
    void deleteReplyById(Integer replyId);

    @Query("DELETE FROM Reply WHERE fk_post_id = :postId ")
    void deleteRepliesByPostId(Integer postId);

    @Query("SELECT * FROM Reply WHERE fk_post_id = :postId ORDER BY date ASC ")
    List<Reply> getAllRepliesForPost(Integer postId);

    @Query("SELECT COUNT(*) FROM Reply WHERE id = :id")
    boolean isExistsReply (Integer id);

}
