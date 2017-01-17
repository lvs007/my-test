package cn.mucang.simple.nativecache.news;

import java.util.List;
import java.util.Map;

/**
 * Created by mc-050 on 2016/2/29.
 */
public interface RedisCache {

    public long incr(String key, long value);
    public long decr(String key, long value);
    public boolean exist(String key);
    public void set(String key, String value);
    public void set(String key, String value, long t);
    public boolean setNx(String key, String value);
    public boolean setNx(String key, String value, long t);
    public void setObject(String key, Object obj);
    public void setObject(String key, Object obj, long t);
    public String get(String key);
    public Object getObject(String key);
    public boolean delete(String key);
    //

    /**
     * 获取key对应的list
     * @param key
     * @return
     */
    public List<String> getListStr(String key);

    /**
     * 获取key对应的list
     * @param key
     * @return
     */
    public List<Object> getListObj(String key);
    public String getListStrIndex(String key,long index);
    public Object getListObjIndex(String key,long index);

    //
    public void set(String key,Map<String,Map<String,String>> mapMap);
    public <T> void set(String key,String key1,String key2,T value);

    public <T> void incr(String key,String key1,String key2,T value);

    public <T> void set(T value,String key,String ... keys);
    public <T> double incr(T value,String key,String ... keys);
}
