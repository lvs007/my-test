package cn.mucang.simple.nativecache.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
public abstract class AbstractHashCacheService<K,T> extends AbstractCacheService<K,T> implements Cache<K,T>{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractHashCacheService.class);

    Map data = new HashMap();

    private volatile long lastUpdateTime = 0;

    private volatile boolean tmp = true;

    private Object lock = new Object();
    private Object exitLock = new Object();

    public T get(K key){
        isExit(key);
        check(key);
        return (T) data.get(key);
    }

    @Override
    public List getPage(K key, int pageNo) {
        isExit(key);
        check(key);
        if (pageNo <= 0){
            pageNo = 1;
        }
        List list = (List) get(key);
        return getPageList(list, pageNo);
    }

    @Override
    public List getPageAsc(K key,int pageNo) {
        isExit(key);
        check(key);
        if (pageNo <= 0){
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
        isExit(key);
        check(key);
        if (pageNo <= 0){
            pageNo = 1;
        }
        List list = (List) get(key);
        list.sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return -compares(o1, o2);
            }
        });
        return getPageList(list,pageNo);
    }

    @Override
    public Collection<T> values() {
        return data.values();
    }

    @Override
    public int size() {
        return data.size();
    }

    private void check(K key){
        if (timer + lastUpdateTime < System.currentTimeMillis()){
            lastUpdateTime = System.currentTimeMillis();
            synchronized (lock){
                if (tmp){
                    tmp = false;
                }else {
                    return;
                }
            }
            LOG.info("[start]缓存过期，从数据库中同步数据");
            data.put(key,refresh(key));
            tmp = true;
        }
    }

    private void isExit(K key){
        if (!data.containsKey(key)){
            LOG.info("[nocache]没有命中缓存");
            synchronized (exitLock){
                if (data.containsKey(key)){
                    return;
                }
                data.put(key,refresh(key));
            }
        }
    }

    @Override
    public Map getStats() {
        return null;
    }

}
