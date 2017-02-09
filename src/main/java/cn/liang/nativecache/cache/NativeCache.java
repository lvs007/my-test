package cn.liang.nativecache.cache;

/**
 * Created by mc-050 on 2016/3/15.
 */
public interface NativeCache<T> extends BaseCache{

    public T get(String key);
    public T getEx(String key) throws Exception;

    public boolean put(String key,T value);

    public boolean putObj(String key, Object value);

    public boolean delete(String key);

}
