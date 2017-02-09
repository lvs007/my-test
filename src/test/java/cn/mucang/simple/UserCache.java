package cn.mucang.simple;

import cn.mucang.simple.entity.TestEntity;
import cn.liang.nativecache.news.AbstractLoadingCacheService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/3/16.
 */
public class UserCache extends AbstractLoadingCacheService<String,List<TestEntity>>{

    public UserCache(){
        super(123450,0);
        setPageSize(25);
    }


    @Override
    protected List<TestEntity> refresh(String key) {
        List<TestEntity> list = new ArrayList<>();
        for (int i = 0; i < 123450; i++) {
            TestEntity test = new TestEntity();
            test.setName(key);
            test.setNumber(i);
            list.add(test);
        }
        return list;
    }

    @Override
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
}
