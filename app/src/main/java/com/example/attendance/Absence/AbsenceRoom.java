package com.example.attendance.Absence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.attendance.Database.Converters;

@Entity(tableName = "Absence")
@TypeConverters(Converters.class)
public class AbsenceRoom {
    @PrimaryKey(autoGenerate = true)
    Integer id;
    @ColumnInfo(name = "courseCode")
    private String CourseCode;
    @ColumnInfo(name = "absCounter")
    private int absCounter;
    @ColumnInfo(name = "pen")
    private int pen;

    public AbsenceRoom() {
    }

    public AbsenceRoom(String courseCode, int absCounter, int pen) {
        CourseCode = courseCode;
        this.absCounter = absCounter;
        this.pen = pen;
    }

    public AbsenceRoom(Integer id, String courseCode, int absCounter, int pen) {
        this.id = id;
        CourseCode = courseCode;
        this.absCounter = absCounter;
        this.pen = pen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public int getAbsCounter() {
        return absCounter;
    }

    public void setAbsCounter(int absCounter) {
        this.absCounter = absCounter;
    }

    public int getPen() {
        return pen;
    }

    public void setPen(int pen) {
        this.pen = pen;
    }
}
