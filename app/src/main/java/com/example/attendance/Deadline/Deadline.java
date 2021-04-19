package com.example.attendance.Deadline;

public class Deadline {
    String assignmentName;

    String courseCode;

    String dueDate;

    public Deadline() {
    }

    public Deadline(String assignmentName, String courseCode, String dueDate) {
        this.assignmentName = assignmentName;
        this.courseCode = courseCode;
        this.dueDate = dueDate;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
