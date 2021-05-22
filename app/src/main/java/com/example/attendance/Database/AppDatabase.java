package com.example.attendance.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.attendance.Absence.AbsenceDAO;
import com.example.attendance.Absence.AbsenceRoom;
import com.example.attendance.Absence.RecentRoom;
import com.example.attendance.Absence.TARecentRoom;
@Database(entities = {AbsenceRoom.class, RecentRoom.class, TARecentRoom.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AbsenceDAO absenceDAO();
}
