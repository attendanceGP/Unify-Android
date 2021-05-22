package com.example.attendance.Absence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

@Entity(tableName = "TARecent")
@TypeConverters(Converters.class)
public class TARecentRoom {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    @ColumnInfo(name = "courseCode")
    String courseCode;
    @ColumnInfo(name = "date")
    String date;
    @ColumnInfo(name = "attended")
    Integer attended;
    @ColumnInfo(name = "absent")
    Integer absent;

    public TARecentRoom() {
    }

    public TARecentRoom(String courseCode, String date, Integer attended, Integer absent) {
        this.courseCode = courseCode;
        this.date = date;
        this.attended = attended;
        this.absent = absent;
    }

    public TARecentRoom(Integer id, String courseCode, String date, Integer attended, Integer absent) {
        this.id = id;
        this.courseCode = courseCode;
        this.date = date;
        this.attended = attended;
        this.absent = absent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAttended() {
        return attended;
    }

    public void setAttended(Integer attended) {
        this.attended = attended;
    }

    public Integer getAbsent() {
        return absent;
    }

    public void setAbsent(Integer absent) {
        this.absent = absent;
    }
}
