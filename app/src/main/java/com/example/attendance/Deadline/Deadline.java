package com.example.attendance.Deadline;

import com.example.attendance.Database.Converters;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
@TypeConverters(Converters.class)
public class Deadline {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "assignment_name")
    private String assignmentName;

    @ColumnInfo(name = "course_code")
    private String courseCode;

    @ColumnInfo(name = "due_date")
    private Date dueDate;

    @ColumnInfo(name = "is_done")
    private boolean isDone;

    public Deadline() {
    }

    public Deadline(int id, String assignmentName, String courseCode, Date dueDate) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.courseCode = courseCode;
        this.dueDate = dueDate;
        this.isDone = false;
    }

    public Deadline(int id, String assignmentName, String courseCode, Date dueDate, boolean isDone) {
        this.id = id;
        this.assignmentName = assignmentName;
        this.courseCode = courseCode;
        this.dueDate = dueDate;
        this.isDone = isDone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
