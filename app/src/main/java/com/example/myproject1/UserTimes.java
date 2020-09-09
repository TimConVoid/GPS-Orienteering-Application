package com.example.myproject1;

public class UserTimes {

    private String name;
    private String course;
    private double time;

    private UserTimes(){

    }

    public UserTimes(String name, String course, double time) {
        this.name = name;
        this.course = course;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
