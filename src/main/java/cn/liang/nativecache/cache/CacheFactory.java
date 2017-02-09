package cn.liang.nativecache.cache;

import cn.liang.nativecache.action.FlushAction;
import cn.liang.nativecache.action.FlushKeyAction;
import cn.liang.nativecache.common.CacheEntity;
import cn.liang.nativecache.task.ReflushTask;
import cn.liang.nativecache.task.TaskEntity;

/**
 * Created by mc-050 on 2016/3/15.
 */
public class CacheFactory{

    public CacheFactory(){
    }

    public static <T> NativeCacheOne<T> createNativeCache(FlushAction action){
        CacheEntity cacheEntity = new CacheEntity();
        NativeCacheOne<T> nativeCache = new NativeCacheImpl(cacheEntity,action);
        cacheEntity.setName(nativeCache.getCacheName());
        CacheManager.put(cacheEntity.getName(),nativeCache);
//        TaskEntity task = new TaskEntity(cacheEntity.getName(),cacheEntity.getTimer(),action);
//        ReflushTask.regist(task);
        return nativeCache;
    }

    public static <T> NativeCacheOne<T> createNativeCache(String name,FlushAction action){
        CacheEntity cacheEntity = new CacheEntity();
        cacheEntity.setName(name);
        return create(cacheEntity,action);
    }

    public static <T> NativeCacheOne<T> createNativeCache(CacheEntity cacheEntity,FlushAction action){
        return create(cacheEntity,action);
    }

    private static <T> NativeCacheOne<T> create(CacheEntity cacheEntity,FlushAction action){
        NativeCacheOne<T> nativeCache = new NativeCacheImpl(cacheEntity,action);
        CacheManager.put(cacheEntity.getName(),nativeCache);
        TaskEntity task = new TaskEntity(cacheEntity.getName(),cacheEntity.getTimer(),action);
        ReflushTask.regist(task);
        return nativeCache;
    }

    public static <T> NativeCache<T> createNativeLoadingCache(FlushKeyAction action){
        CacheEntity cacheEntity = new CacheEntity();
        NativeCache<T> nativeCache = new NativeLoadingCacheImpl(cacheEntity,action);
        cacheEntity.setName(nativeCache.getCacheName());
        CacheManager.put(cacheEntity.getName(), nativeCache);
        return nativeCache;
    }

    public static <T> NativeCache<T> createNativeLoadingCache(String name,FlushKeyAction action){
        CacheEntity cacheEntity = new CacheEntity();
        cacheEntity.setName(name);
        return createLoading(cacheEntity, action);
    }

    public static <T> NativeCache<T> createNativeLoadingCache(CacheEntity cacheEntity,FlushKeyAction action){
        return createLoading(cacheEntity, action);
    }

    private static <T> NativeCache<T> createLoading(CacheEntity cacheEntity,FlushKeyAction action){
        NativeCache<T> nativeCache = new NativeLoadingCacheImpl(cacheEntity,action);
        CacheManager.put(cacheEntity.getName(),nativeCache);
        return nativeCache;
    }
}
