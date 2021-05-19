package com.example.attendance.Absence;

public class Recent {
    private String courseCode;
    private String taName;
    private String date;
    private boolean pen;

    public Recent() {
    }

    public Recent(String courseCode, String taName, String date, boolean pen) {
        this.courseCode = courseCode;
        this.taName = taName;
        this.date = date;
        this.pen = pen;
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
