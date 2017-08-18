package com.readboy.showappdemo.entry;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by klivitam on 17-8-4.
 *
 */
@Entity
public class TxtEntry {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private int offset;
    @NotNull
    private String mDatas;
    @Generated(hash = 1735853468)
    public TxtEntry(Long id, int offset, @NotNull String mDatas) {
        this.id = id;
        this.offset = offset;
        this.mDatas = mDatas;
    }
    @Generated(hash = 1996799166)
    public TxtEntry() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getOffset() {
        return this.offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }
    public String getMDatas() {
        return this.mDatas;
    }
    public void setMDatas(String mDatas) {
        this.mDatas = mDatas;
    }
}
