package com.example.attendance.Absence;

public class Absence {
    private int penCounter;
    private int absenceCounter;
    private String courseCode;

    public Absence() {
    }

    public Absence(int penCounter, int absenceCounter, String courseCode) {
        this.penCounter = penCounter;
        this.absenceCounter = absenceCounter;
        this.courseCode = courseCode;
    }

    public int getPenCounter() {
        return penCounter;
    }

    public void setPenCounter(int penCounter) {
        this.penCounter = penCounter;
    }

    public int getAbsenceCounter() {
        return absenceCounter;
    }

    public void setAbsenceCounter(int absenceCounter) {
        this.absenceCounter = absenceCounter;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
