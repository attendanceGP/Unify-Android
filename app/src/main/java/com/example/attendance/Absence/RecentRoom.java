package com.example.attendance.Absence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

@Entity(tableName = "Recent")
@TypeConverters(Converters.class)
public class RecentRoom {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    @ColumnInfo(name = "courseCode")
    private String courseCode;
    @ColumnInfo(name = "taName")
    private String taName;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "pen")
    private boolean pen;

    public RecentRoom() {
    }

    public RecentRoom(Integer id, String courseCode, String taName, String date, boolean pen) {
        this.id = id;
        this.courseCode = courseCode;
        this.taName = taName;
        this.date = date;
        this.pen = pen;
    }

    public RecentRoom(String courseCode, String taName, String date, boolean pen) {
        this.courseCode = courseCode;
        this.taName = taName;
        this.date = date;
        this.pen = pen;
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

    public String getTaName() {
        return taName;
    }

    public void setTaName(String taName) {
        this.taName = taName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPen() {
        return pen;
    }

    public void setPen(boolean pen) {
        this.pen = pen;
    }
}
