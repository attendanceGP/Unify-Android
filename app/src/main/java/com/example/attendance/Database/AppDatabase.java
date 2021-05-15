package com.example.attendance.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.attendance.Announcement.Announcement;
import com.example.attendance.Announcement.AnnouncementDao;
import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;

@Database(entities = {Deadline.class, Announcement.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeadlineDao deadlineDao();
    public abstract AnnouncementDao announcementDao();
}
