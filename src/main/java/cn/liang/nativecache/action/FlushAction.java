package cn.liang.nativecache.action;

import java.util.Comparator;

/**
 * Created by mc-050 on 2016/3/15.
 */
public interface FlushAction {

    public Object flush();

    public Comparator sort();
}
