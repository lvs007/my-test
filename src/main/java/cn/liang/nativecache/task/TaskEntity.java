package cn.liang.nativecache.task;

import cn.liang.nativecache.action.FlushAction;

/**
 * Created by mc-050 on 2016/3/16.
 */
public class TaskEntity {
    private String name;
    private String timer;
    private FlushAction action;

    public TaskEntity() {
    }

    public TaskEntity(String name, String timer, FlushAction action) {
        this.name = name;
        this.timer = timer;
        this.action = action;
    }

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

    public FlushAction getAction() {
        return action;
    }

    public void setAction(FlushAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", timer='" + timer + '\'' +
                ", action=" + action +
                '}';
    }
}
