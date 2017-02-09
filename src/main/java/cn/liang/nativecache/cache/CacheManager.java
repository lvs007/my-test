package cn.liang.nativecache.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mc-050 on 2016/3/15.
 */
public class CacheManager {

    private static Map<String,BaseCache> cacheMap = new HashMap<>();

    public static BaseCache get(String key){
        return cacheMap.get(key);
    }

    public static BaseCache put(String key,BaseCache nativeCache){
        return cacheMap.put(key,nativeCache);
    }
}
