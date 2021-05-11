package com.example.attendance.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;
import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;

@Database(entities = {Deadline.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeadlineDao deadlineDao();
}
