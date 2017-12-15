package com.example.marlon.demsu;

/**
 * Created by Marlon on 18/06/2017.
 */

public class Product {
    private String name;
    private int id;
    private float value;
    private double latitude;
    private double longitude;

    public Product(String name, int id, float value, double latitude, double longitude){
        this.name = name;
        this.id = id;
        this.value = value;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }
    public float getValue(){
        return value;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }
}
