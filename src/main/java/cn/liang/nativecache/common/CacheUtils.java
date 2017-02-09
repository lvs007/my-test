package cn.liang.nativecache.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by wangcheng<gates@mucang.cn> on 15/12/15.
 */
public class CacheUtils {

    public static Map<String, Object> toMap(CacheStats stats) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("requestCount", stats.requestCount());
        map.put("hitCount", stats.hitCount());
        map.put("missCount", stats.missCount());
        map.put("hitRate", stats.hitRate());
        map.put("loadSuccessCount", stats.loadSuccessCount());
        map.put("loadExceptionCount", stats.loadExceptionCount());
        map.put("totalLoadTime", stats.totalLoadTime() / 1000 * 1000);
        map.put("evictionCount", stats.evictionCount());
        return map;
    }

    public static Map<String, Object> toMap(Cache cache) {
        Map<String, Object> map = toMap(cache.stats());
        map.put("count", cache.size());
        return map;
    }
}
