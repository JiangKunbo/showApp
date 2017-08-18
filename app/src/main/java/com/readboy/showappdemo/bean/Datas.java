package com.readboy.showappdemo.bean;

/**
 * Created by klivitam on 17-8-4.
 */

public class Datas {
    private String name;
    private String mean;
    private int belong;

    public Datas(String name, String mean, int belong) {
        this.name = name;
        this.mean = mean;
        this.belong = belong;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public int getBelong() {
        return belong;
    }

    public void setBelong(int belong) {
        this.belong = belong;
    }
}
