package com.example.attendance;

import java.util.Date;

public class Attendance {
    private User user;
    private Course course;
    private String userGroup;
    private boolean absent;
    private Date date;

    Attendance() {

    }

    public Attendance(User user, Course course, String userGroup, Date date, boolean absent) {
        this.user = user;
        this.course = course;
        this.userGroup = userGroup;
        this.date = date;
        this.absent = absent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
