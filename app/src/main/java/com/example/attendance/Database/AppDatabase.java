package com.example.attendance.Database;

import com.example.attendance.Deadline.Deadline;
import com.example.attendance.Deadline.DeadlineDao;
import com.example.attendance.Forums.ForumsDao;
import com.example.attendance.Forums.Post;
import com.example.attendance.Forums.Reply;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Deadline.class, Post.class, Reply.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeadlineDao deadlineDao();
    public abstract ForumsDao forumsDao();
}
