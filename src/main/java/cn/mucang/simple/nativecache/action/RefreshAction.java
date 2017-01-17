package cn.mucang.simple.nativecache.action;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by mc-050 on 2016/3/16.
 */
public abstract class RefreshAction<T> {

    AtomicReference<T> data = new AtomicReference<>();

    private long lastUpdateTime = 0;

    private long timer = 10*60*1000;

    private boolean tmp = true;

    private Object lock = new Object();

    public T get(){
        LongAdder longAdder = new LongAdder();
        check();
        return data.get();
    }

    private void check(){
        if (timer + lastUpdateTime < System.currentTimeMillis()){
            lastUpdateTime = System.currentTimeMillis();
            synchronized (lock){
                if (tmp){
                    tmp = false;
                }else {
                    return;
                }
            }
            data.set(refresh());
            tmp = true;
        }
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public abstract T refresh();
}
