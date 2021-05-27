package com.example.attendance.Database;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.attendance.Absence.AbsenceDAO;
import com.example.attendance.Absence.AbsenceRoom;
import com.example.attendance.Absence.RecentRoom;
import com.example.attendance.Absence.TARecentRoom;


import com.example.attendance.Course.Course;
import com.example.attendance.Course.CourseDAO;
import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;
import com.example.attendance.Forums.ForumsDao;
import com.example.attendance.Forums.Post;
import com.example.attendance.Forums.Reply;
import com.example.attendance.Announcement.Announcement;
import com.example.attendance.Announcement.AnnouncementDao;
import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.example.attendance.Announcement.Announcement;
import com.example.attendance.Announcement.AnnouncementDao;
import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;



@Database(entities = {Deadline.class, Post.class, Reply.class, Announcement.class,AbsenceRoom.class, RecentRoom.class, TARecentRoom.class, Course.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeadlineDao deadlineDao();
    public abstract ForumsDao forumsDao();
    public abstract AnnouncementDao announcementDao();
    public abstract AbsenceDAO absenceDAO();
    public abstract CourseDAO courseDAO();

}
