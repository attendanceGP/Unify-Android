package com.example.attendance.Course;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

@Entity
@TypeConverters(Converters.class)
public class Course {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "course_code")
    private String courseCode;

    public Course() {

    }
    public Course(int id,String courseCode){
        this.id = id;
        this.courseCode = courseCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
