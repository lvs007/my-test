package cn.mucang.simple.nativecache.common;

import java.io.Serializable;

/**
 * Created by mc-050 on 2016/3/15.
 */
public class CacheEntity implements Serializable{

    private String name;//缓存名称
    private String timer = "0 0/1 * * * ?";//默认更新时间10分钟
    private long timeOut = 60*60*24;//默认过期时间1天
    private int size = 1000;//默认的缓存大小

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "CacheEntity{" +
                "name='" + name + '\'' +
                ", timer='" + timer + '\'' +
                ", timeOut=" + timeOut +
                ", size=" + size +
                '}';
    }
}
