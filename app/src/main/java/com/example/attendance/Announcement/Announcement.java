package com.example.attendance.Announcement;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

import java.util.Date;

@Entity
@TypeConverters(Converters.class)
public class Announcement {
    @PrimaryKey
    private int id;

    private String title;

    private String courseId;

    private Date postedDate;

    private String postedBy;

    private String description;

    public Announcement(int id, String title, String courseId, Date postedDate, String postedBy, String description) {
        this.id = id;
        this.title = title;
        this.courseId = courseId;
        this.postedDate = postedDate;
        this.postedBy = postedBy;
        this.description = description;
    }

    public Announcement(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
