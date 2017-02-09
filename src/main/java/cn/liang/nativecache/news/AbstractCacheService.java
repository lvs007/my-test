package cn.liang.nativecache.news;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Created by mc-050 on 2016/3/17.
 */
public abstract class AbstractCacheService<K,T> implements Cache<K,T>{

    protected int pageSize = 20;
    protected long timer = 10*60*1000;

    protected List getPageList(List list,int pageNo){
        int size = list.size();
        int fromIndex = (pageNo - 1) * pageSize;
        if (fromIndex >= size) {
            return Lists.newArrayList();
        }
        int toIndex = fromIndex + pageSize;
        if (toIndex > size) {
            toIndex = size;
        }
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public int size(K key) {
        T t = get(key);
        if (t instanceof List || t instanceof Collection){
            return ((List) t).size();
        }
        return 1;
    }

    /**
     * 如果需要排序请重写此方法
     * @param o1
     * @param o2
     * @return
     */
    protected int compares(Object o1, Object o2){
        return 0;
    }

    protected void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected void setTimer(long timer) {
        this.timer = timer;
    }

    protected abstract T refresh(K key);
}
