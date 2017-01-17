package cn.mucang.simple;

import cn.mucang.polaris.manager.MonitorManager;
import cn.mucang.simple.entity.TestEntity;
import cn.mucang.simple.nativecache.action.FlushAction;
import cn.mucang.simple.nativecache.action.FlushKeyAction;
import cn.mucang.simple.nativecache.cache.CacheFactory;
import cn.mucang.simple.nativecache.cache.CacheManager;
import cn.mucang.simple.nativecache.cache.NativeCache;
import cn.mucang.simple.nativecache.cache.NativeCacheOne;
import cn.mucang.simple.nativecache.news.RedisCache;
import cn.mucang.simple.nativecache.news.RedisCacheImpl;
import org.junit.Test;

import java.util.*;

/**
 * Created by mc-050 on 2016/3/15.
 */
public class TestCache {

    @Test
    public void test(){
        NativeCache nativeCache = CacheFactory.createNativeLoadingCache(
                new FlushKeyAction() {
                    @Override
                    public Object flush(String key) {
                        TestEntity testEntity = new TestEntity();
                        testEntity.setName("new-value"+key);
                        testEntity.setNumber(new Random().nextInt(10000));
                        return testEntity;
                    }
                });
        for (int i = 0; i < 10; i++) {
            nativeCache.get("t"+i);
            TestEntity testEntity = new TestEntity();
            testEntity.setName("new-value"+i);
            testEntity.setNumber(new Random().nextInt(10000));
            nativeCache.put("t"+i,testEntity);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(nativeCache.get("t"+i));
        }
        System.out.println(nativeCache.getStats());
        System.out.println(CacheManager.get(nativeCache.getCacheName()).getStats());
    }

    @Test
    public void test2(){
        UserCache userCache = new UserCache();
        long start = System.currentTimeMillis();
        for (int i = 1; i <= 1000; i++) {
            System.out.println(userCache.getPageDesc("key"+i,1));
//            userCache.getPageDesc("key",i);
        }
        long end = System.currentTimeMillis();
        System.out.println("time="+(end-start));
        for (int i = 1; i <= 10; i++) {
            System.out.println(userCache.getPageAsc("key", i));
        }
    }

    public static void main(String[] args){
//        NativeCacheOne<List<TestEntity>> nativeCache = CacheFactory.createNativeCache(
//                new FlushAction() {
//                    @Override
//                    public Object flush() {
//                        List list = new ArrayList();
//                        for (int i = 0; i < 1000; i++) {
//                            TestEntity testEntity = new TestEntity();
//                            testEntity.setName("new-value"+i);
//                            testEntity.setNumber(new Random().nextInt(10000));
//                            list.add(testEntity);
//                        }
//                        return list;
//                    }
//
//                    @Override
//                    public Comparator sort() {
//                        return new Comparator<TestEntity>() {
//                            @Override
//                            public int compare(TestEntity o1, TestEntity o2) {
//                                if (o1.getNumber() == o2.getNumber()){
//                                    return 0;
//                                }else if (o1.getNumber() > o2.getNumber()){
//                                    return 1;
//                                }else {
//                                    return -1;
//                                }
//                            }
//                        };
//                    }
//                });
//        System.out.println(nativeCache.get());
//        for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(nativeCache.getPage(i,20));
//        }
//        Map<Thread,StackTraceElement[]> stackInfos = Thread.getAllStackTraces();
//        System.out.println(stackInfos);
//        Set<Thread> keySet = stackInfos.keySet();
//        for(Thread tempThread: keySet) {
//            StackTraceElement[] tempElement = stackInfos.get(tempThread);
//            for(StackTraceElement element:tempElement){
//                System.out.println(element.getLineNumber()+"--"+element.getFileName()+"--"+element.getMethodName());
//            }
//        }
//        RedisCache redisCache = new RedisCacheImpl();
//        redisCache.set("","","",1);
        String oid = "01010203";
//        int length = oid.length();
//        int k = 2;
//        for (int i=2;k<length;i++){
//            System.out.println(oid.substring(0,k));
//            k=i*2;
//        }
        int count = 0;
        for (int i = 0; i < 100; i++) {
            count = new Random().nextInt(12);
            System.out.println(count);
        }
    }

    @Test
    public void testTTT(){
        MonitorManager monitorManager = new MonitorManager();
//        monitorManager.setHost("http://polaris.kakamobi.cn");
        monitorManager.set("key-13","test",10);
    }
}
