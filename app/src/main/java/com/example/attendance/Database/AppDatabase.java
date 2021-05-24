package com.example.attendance.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.attendance.Absence.AbsenceDAO;
import com.example.attendance.Absence.AbsenceRoom;
import com.example.attendance.Absence.RecentRoom;
import com.example.attendance.Absence.TARecentRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.attendance.Announcement.Announcement;
import com.example.attendance.Announcement.AnnouncementDao;
import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;

@Database(entities = {Deadline.class, Announcement.class,AbsenceRoom.class, RecentRoom.class, TARecentRoom.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeadlineDao deadlineDao();
    public abstract AnnouncementDao announcementDao();
    public abstract AbsenceDAO absenceDAO();

}
