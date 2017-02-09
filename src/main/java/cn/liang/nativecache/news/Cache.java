package cn.liang.nativecache.news;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 接口说明：K的类型为String 或者基本类型，如果用其它引用类型没有意义
 * T的类型如果需要用到getPage，必须是List类型的，需要用到Asc和Desc的还需要实现排序函数
 * Created by mc-050 on 2016/3/16.
 */
public interface Cache<K,T> {

    public T get(K key);

    /**
     * 性能最优，因为不需要进行排序
     * @param key
     * @param pageNo
     * @return
     */
    public List getPage(K key,int pageNo);

    /**
     * 每次获取的时候先进行排序
     * @param key
     * @param pageNo
     * @return
     */
    public List getPageAsc(K key,int pageNo);

    /**
     * 每次获取的时候先进行排序
     * @param key
     * @param pageNo
     * @return
     */
    public List getPageDesc(K key,int pageNo);

    public int size(K key);

    public Collection<T> values();

    public int size();

    public Map getStats();
}
