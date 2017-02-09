package cn.liang.nativecache.news;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 接口说明：K的类型为String 或者基本类型，如果用其它引用类型没有意义
 * T的类型如果需要用到getPage，必须是List<E>类型的，需要用到Asc和Desc的还需要实现排序函数compares
 *  @Override
    protected int compares(Object o1, Object o2) {
        TestEntity t1 = (TestEntity) o1;
        TestEntity t2 = (TestEntity) o2;
        if (t1.getNumber() > t2.getNumber()){
            return 1;
        }else if (t1.getNumber() < t2.getNumber()){
            return -1;
        }else {
            return 0;
        }
    }
 * Created by mc-050 on 2016/3/16.
 */
public abstract class AbstractLoadingCacheService<K, T> extends AbstractCacheService<K, T> implements Cache<K, T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLoadingCacheService.class);

    private LoadingCache<K, T> data;

    private int size = 100000;

    public AbstractLoadingCacheService(int size,long timer) {
        if (size <= 0){
            size = this.size;
        }
        if (timer <= 0){
            timer = this.timer;
        }
        data = CacheBuilder.newBuilder().maximumSize(size).initialCapacity(size).recordStats().
                expireAfterWrite(timer, TimeUnit.MILLISECONDS).build(new CacheLoader<K, T>() {
            @Override
            public T load(K key) throws Exception {
                return refresh(key);
            }
        });
    }

    public T get(K key) {
        try {
            return (T) data.get(key);
        } catch (Exception e) {
            LOG.error("查询缓存出错", e);
        }
        return null;
    }

    @Override
    public List getPage(K key, int pageNo) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        List list = (List) get(key);
        return getPageList(list,pageNo);
    }

    @Override
    public List getPageAsc(K key, int pageNo) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        List list = (List) get(key);
        list.sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return compares(o1, o2);
            }
        });
        return getPageList(list, pageNo);
    }

    @Override
    public List getPageDesc(K key, int pageNo) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        List list = (List) get(key);
        list.sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return -compares(o1, o2);
            }
        });
        return getPageList(list, pageNo);
    }

    @Override
    public Collection<T> values() {
        return data.asMap().values();
    }

    @Override
    public int size() {
        return data.asMap().size();
    }

    @Override
    public Map getStats() {
        return toMap(data);
    }

    private static Map<String, Object> toMap(com.google.common.cache.Cache cache) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("requestCount", cache.stats().requestCount());
        map.put("hitCount", cache.stats().hitCount());
        map.put("missCount", cache.stats().missCount());
        map.put("hitRate", cache.stats().hitRate());
        map.put("loadSuccessCount", cache.stats().loadSuccessCount());
        map.put("loadExceptionCount", cache.stats().loadExceptionCount());
        map.put("totalLoadTime", cache.stats().totalLoadTime() / 1000 * 1000);
        map.put("evictionCount", cache.stats().evictionCount());
        map.put("count", cache.size());
        return map;
    }
}
