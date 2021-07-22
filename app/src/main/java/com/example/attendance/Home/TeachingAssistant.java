package com.example.attendance.Home;

public class TeachingAssistant {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public TeachingAssistant() {
    }

    public TeachingAssistant(Integer id, String type, String name, String token, String username, String password, String lastLoginDate, Double longitude, Double latitude) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.token = token;
        this.username = username;
        this.password = password;
        this.lastLoginDate = lastLoginDate;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    private String type;    // "TeachingAssistant", "student", "professor"
    private String name;
    private String token;
    private String username;
    private String password;
    private String lastLoginDate;
    private Double longitude;
    private Double latitude;

}
