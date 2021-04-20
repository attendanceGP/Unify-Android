package com.example.attendance.Deadline;

import com.example.attendance.User;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DeadlineDao {
    @Query("SELECT * FROM Deadline")
    List<Deadline> getAll();

    @Query("SELECT * FROM Deadline WHERE is_done = 0")
    List<Deadline> getAllUpcoming();

    @Query("SELECT * FROM Deadline WHERE is_done = 1")
    List<Deadline> getAllDone();

    @Query("SELECT * FROM Deadline WHERE id IN (:ids)")
    List<Deadline> loadAllByIds(int[] ids);

    @Query("SELECT * FROM Deadline WHERE id = :id")
    Deadline findById(int id);

    @Insert
    void insertAll(Deadline... deadlines);

    @Delete
    void delete(Deadline deadline);
}
