package com.example.attendance;

public class User {
    private Integer id;
    private String type;    // "TA", "student", "professor"
    private String name;
    private String token;


    //only if a student
    private Float GPA;
    private Integer level;

    //for error codes
    private int errorCode;

    public User(){

    }

    public User(Integer id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;

        this.GPA = null;
        this.level = null;
    }

    public User(Integer id, String type, String name, String token, Float GPA, Integer level) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.token = token;
        this.GPA = GPA;
        this.level = level;
    }

    public User(int errorCode){
        this.errorCode = errorCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getGPA() {
        return GPA;
    }

    public void setGPA(Float GPA) {
        this.GPA = GPA;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
