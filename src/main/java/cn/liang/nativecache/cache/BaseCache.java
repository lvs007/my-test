package cn.liang.nativecache.cache;

import java.util.Map;

/**
 * Created by mc-050 on 2016/3/16.
 */
public interface BaseCache {

    public Map<String, Object> getStats();

    public String getCacheName();
}
