package cn.mucang.simple.nativecache.cache;

import cn.mucang.simple.nativecache.action.FlushAction;
import cn.mucang.simple.nativecache.common.CacheEntity;
import cn.mucang.simple.nativecache.common.CacheUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mc-050 on 2016/3/16.
 */
public class NativeCacheImpl<T> implements NativeCacheOne<T>{
    private final String key = "key_"+new Random().nextInt(1000)+"_"+Math.abs(this.hashCode());

    private Cache<String,T> cache;

    private FlushAction action;

    private CacheEntity cacheEntity;

    private long lastUpdateTime = 0;

    public NativeCacheImpl(CacheEntity cacheEntity,FlushAction action){
        this.action = action;
        this.cacheEntity = cacheEntity;
        cache = CacheBuilder.newBuilder().maximumSize(cacheEntity.getSize())
                .initialCapacity(cacheEntity.getSize()).recordStats()
                .expireAfterAccess(cacheEntity.getTimeOut(), TimeUnit.SECONDS).build();
    }

    @Override
    public T get() {
        check();
        return cache.getIfPresent(key);
    }

    @Override
    public boolean put(T value) {
        cache.put(key,value);
        return true;
    }

    @Override
    public List getPage(int pageNo, int pageSize) {
        if (pageNo <= 0){
            pageNo = 1;
        }
        List list = (List) get();
        int size = list.size();
        int fromIndex = (pageNo-1) * pageSize;
        if (fromIndex >= size){
            return Lists.newArrayList();
        }
        int toIndex = fromIndex + pageSize;
        if (toIndex > size){
            toIndex = size;
        }
        return list.subList(fromIndex,toIndex);
    }

    @Override
    public void putObj(Object value) {
        List list = (List) get();
        list.add(value);
        Collections.sort(list, action.sort());
        if (list.size() > cacheEntity.getSize()){
            list.remove(list.size()-1);
        }
    }

    @Override
    public Map<String, Object> getStats() {
        return CacheUtils.toMap(cache);
    }

    @Override
    public String getCacheName() {
        return key;
    }

    private void check(){
        if (cacheEntity.getTimeOut() + lastUpdateTime < System.currentTimeMillis()){
            lastUpdateTime = System.currentTimeMillis();
            put((T)action.flush());
        }
    }
}
