package cn.mucang.simple.nativecache.cache;

import java.util.List;
import java.util.Random;

/**
 * Created by mc-050 on 2016/3/16.
 */
public interface NativeCacheOne<T> extends BaseCache{

    public T get();
    public boolean put(T value);

    public List getPage(int pageNo,int pageSize);
    public void putObj(Object value);
}
