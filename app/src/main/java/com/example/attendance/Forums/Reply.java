package com.example.attendance.Forums;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

import java.util.Date;

@Entity
@TypeConverters(Converters.class)
public class Reply {
    @PrimaryKey
    private Integer id;

    @ColumnInfo(name = "fk_post_id")
    private Integer postId;

    @ColumnInfo(name = "fk_user_id")
    private Integer userId;

    @ColumnInfo(name = "fk_user_name")
    private String userName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "date")
    private Date date;

    public Reply() {
    }

    public Reply(Integer id, Integer postId, Integer userId, String userName, String description, Date date) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.description = description;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostId() {
        return postId;
    }
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
