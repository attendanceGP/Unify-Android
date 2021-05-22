package com.example.attendance.Absence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AbsenceDAO {
    @Insert
    void insertAllToAbsence(AbsenceRoom... absenceRoom);

    @Insert
    void insertAllToRecent(RecentRoom ... recentRooms);

    @Insert
    void insertAllToTARecent(TARecentRoom...taRecentRooms);

    @Query("SELECT * FROM Absence")
    AbsenceRoom[] readAllAbsence();

    @Query("SELECT * FROM Recent")
    RecentRoom[] readAllRecent();

    @Query("SELECT * FROM TARecent")
    TARecentRoom[] readAllTARecent();

    @Query("DELETE FROM Absence")
    void deleteAbsence();

    @Query("DELETE FROM Recent")
    void deleteRecent();

    @Query("DELETE FROM TARecent")
    void deleteTARecent();

}
