package com.example.attendance.Absence;

public class TaRecent {
    String courseCode;
    String date;
    int attended,absent;

    public TaRecent() {
    }

    public TaRecent(String courseCode, String date, int attended,int absent) {
        this.courseCode = courseCode;
        this.date = date;
        this.absent = absent;
        this.attended = attended;
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

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }
}
