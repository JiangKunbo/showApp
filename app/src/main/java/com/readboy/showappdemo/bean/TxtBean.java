package com.readboy.showappdemo.bean;

/**
 * Created by klivitam on 17-8-4.
 */

public class TxtBean {
    private String name;
    private String means;
    private String explain;

    public TxtBean(String name, String means, String explain) {
        this.name = name;
        this.means = means;
        this.explain = explain;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeans() {
        return means;
    }

    public void setMeans(String means) {
        this.means = means;
    }

}
