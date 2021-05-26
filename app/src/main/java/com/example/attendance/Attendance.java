package com.example.attendance;

public class Attendance {
    private Long id;
    private int error_code;
    private Integer userID;
    private String courseName;
    private String name;
    private String userGroup;
    private String courseCode;

    public Attendance() {
    }

    private boolean absent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userID;
    }


    public void setUserID(Integer userID) {
        this.userID = userID;
    }


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public Attendance(Long id, Integer userID, String courseName, String userGroup, boolean absent) {
        this.id = id;
        this.userID = userID;
        this.courseName = courseName;
        this.userGroup = userGroup;
        this.absent = absent;
    }

    public Attendance(Integer userID, String courseCode, String name, String userGroup) {
        this.userID = userID;
        this.courseCode = courseCode;
        this.name = name;
        this.userGroup = userGroup;
    }

    public Attendance(int errorCode){
        this.error_code = errorCode;
    }


    public boolean isAbsent() {
        return absent;
    }

    public void setAbsent(boolean absent) {
        this.absent = absent;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
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

    /*public Attendance() {
    }

    public Attendance(Integer userID, Course course, String userGroup, Date date) {
        this.user = user;
        this.course = course;
        this.userGroup = userGroup;
      //  this.date = date;
    }

    public Attendance(User user, Course course, String userGroup, Date date, boolean absent) {
        this.user = user;
        this.course = course;
        this.userGroup = userGroup;
      //  this.date = date;
        this.absent = absent;
    }

    public Attendance(Long id, User user, Course course, String userGroup, Date date,boolean absent) {
        this.id = id;
        this.user = user;
        this.course = course;
        this.userGroup = userGroup;
    //    this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
import java.util.Date;

public class Attendance {
    private Integer userId;
    private String fk_course_code;
    private String name;
    private String userGroup;


    Attendance() {

    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }


    *//*public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }*/


}
