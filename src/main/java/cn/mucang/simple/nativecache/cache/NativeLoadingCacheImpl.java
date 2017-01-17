package cn.mucang.simple.nativecache.cache;

import cn.mucang.simple.nativecache.action.FlushKeyAction;
import cn.mucang.simple.nativecache.common.CacheEntity;
import cn.mucang.simple.nativecache.common.CacheUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by mc-050 on 2016/3/15.
 */
public class NativeLoadingCacheImpl<T> implements NativeCache<T> {

    private final String key = "key_"+new Random().nextInt(1000)+"_"+Math.abs(this.hashCode());

    private LoadingCache<String,T> cache;

    private Logger logger = LoggerFactory.getLogger(NativeLoadingCacheImpl.class);

    public NativeLoadingCacheImpl(CacheEntity cacheEntity, final FlushKeyAction action) {
        cache = (LoadingCache<String, T>) CacheBuilder.newBuilder().maximumSize(cacheEntity.getSize())
                .initialCapacity(cacheEntity.getSize())
                .recordStats().expireAfterAccess(cacheEntity.getTimeOut(), TimeUnit.SECONDS)
                .build(new CacheLoader<String,Object>() {
                    @Override
                    public Object load(String key) throws Exception {
                        logger.info("缓存未命中，从action中获取");
                        return action.flush(key);
                    }
                });
    }

    @Override
    public T get(String key) {
        try {
            return cache.get(key);
        } catch (Exception e) {
            logger.error("调用get方法的时候发生异常",e);
        }
        return null;
    }

    @Override
    public T getEx(String key) throws Exception {
        return cache.get(key);
    }

    @Override
    public boolean put(String key, T value) {
        cache.put(key,value);
        return true;
    }

    @Override
    public boolean putObj(String key, Object value) {
        cache.put(key,(T)value);
        return true;
    }

    @Override
    public boolean delete(String key) {
        return false;
    }

    @Override
    public Map<String, Object> getStats() {
        return CacheUtils.toMap(cache);
    }

    @Override
    public String getCacheName() {
        return key;
    }
}
