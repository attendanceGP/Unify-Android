package com.example.attendance;

import java.util.Date;

public class Attendance {
    private Integer userId;
    private String courseCode;
    private String name;
    private String userGroup;

    private int error_code;
    Attendance() {

    }

    public Attendance(Integer userId, String courseCode, String name, String userGroup) {
        this.userId = userId;
        this.courseCode = courseCode;
        this.name = name;
        this.userGroup = userGroup;
    }

    public Attendance(int errorCode){
        this.error_code = errorCode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
}
