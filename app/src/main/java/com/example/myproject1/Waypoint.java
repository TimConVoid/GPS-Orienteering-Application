package com.example.myproject1;

import com.google.android.gms.maps.model.LatLng;

public class Waypoint {

    @Override
    public String toString() {
        return "Waypoint{" +
                "id=" + id +
                ", coordinates=" + coordinates +
                ", description='" + description + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                '}';
    }

    private int id;
    private LatLng coordinates;
    private String description;
    private String imgSrc;
    private  String courseName;

    public Waypoint(int id, LatLng coordinates, String description, String imgSrc, String courseName) {
        this.id = id;
        this.coordinates = coordinates;
        this.description = description;
        this.imgSrc = imgSrc;
        this.courseName = courseName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
