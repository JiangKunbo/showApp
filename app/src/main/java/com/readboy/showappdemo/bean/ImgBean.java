package com.readboy.showappdemo.bean;

/**
 * Created by klivitam on 17-8-7.
 */

public class ImgBean {
    public byte[] getDatas() {
        return datas;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    private byte[] datas;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
