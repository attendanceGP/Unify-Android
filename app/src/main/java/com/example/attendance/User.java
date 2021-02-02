package com.example.attendance;

public class User {
    Long id;
    String type;    // "TA", "student", "professor"
    String name;
    String token;


    //only if a student
    Double GPA;
    Integer level;

    //for error codes
    int errorCode;

    public User(){

    }

    public User(Long id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;

        this.GPA = null;
        this.level = null;
    }

    public User(Long id, String type, String name, String token, Double GPA, Integer level) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Double getGPA() {
        return GPA;
    }

    public void setGPA(Double GPA) {
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
