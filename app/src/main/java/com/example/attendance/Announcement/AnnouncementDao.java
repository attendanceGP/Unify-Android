package com.example.attendance.Announcement;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.attendance.Deadline.Deadline;

import java.util.List;

@Dao
public interface AnnouncementDao {

    @Query("SELECT * FROM Announcement ORDER BY postedDate DESC ")
    List<Announcement> getAll();

    @Insert
    void insertAll(Announcement... announcements);

    @Delete
    void delete(Announcement announcement);

    @Query("DELETE FROM announcement")
    void nukeTable();

}
