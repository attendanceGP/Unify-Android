package com.example.attendance.Course;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.attendance.Announcement.Announcement;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CourseDAO {
    @Query("SELECT course_code FROM Course ")
    List<String> getAll();

    @Insert
    void insertAll(Course... courses);

    @Query("DELETE FROM course")
    void nukeTable();

}
