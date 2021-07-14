package com.example.attendance.Course;

public class CourseGroup {
    private String courseCode;
    private String userGroup;

    public CourseGroup() {
    }

    public CourseGroup(String courseCode, String userGroup) {
        this.courseCode = courseCode;
        this.userGroup = userGroup;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }
}
