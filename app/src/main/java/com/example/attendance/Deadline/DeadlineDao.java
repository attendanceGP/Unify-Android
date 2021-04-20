package com.example.attendance.Deadline;

import com.example.attendance.User;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("SELECT COUNT(*) FROM Deadline WHERE id = :id")
    boolean isExists (int id);

    @Insert
    void insertAll(Deadline... deadlines);

    @Update
    void update(Deadline deadline);

    @Query("UPDATE deadline SET due_date = :date WHERE id = :id")
    void updateDate(int id, Date date);

    @Query("UPDATE deadline SET is_done = 1 WHERE id = :id")
    void changeToDone(int id);

    @Delete
    void delete(Deadline deadline);

    @Query("DELETE FROM deadline")
    void nukeTable();
}
