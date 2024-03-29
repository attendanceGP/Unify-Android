package com.example.attendance.Forums;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

import java.util.Date;

@Entity
@TypeConverters(Converters.class)
public class Post {
    @PrimaryKey
    private Integer id;

    @ColumnInfo(name = "fk_user_id")
    private Integer userId;

    @ColumnInfo(name = "fk_user_name")
    private String userName;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "fk_course_code")
    private String courseCode;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "is_starred")
    private boolean isStarred;

    public Post() {
    }

    public Post(Integer id, Integer userId, String userName, String title, String content, String courseCode, Date date, boolean isStarred) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.courseCode = courseCode;
        this.date = date;
        this.isStarred = isStarred;
    }

    public Post(Integer id, Integer userId, String userName, String title, String content, String courseCode, Date date) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.courseCode = courseCode;
        this.date = date;
        isStarred = false;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isStarred() {
        return isStarred;
    }
    public void setStarred(boolean starred) {
        isStarred = starred;
    }
}
