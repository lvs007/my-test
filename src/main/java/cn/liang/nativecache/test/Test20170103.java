package cn.liang.nativecache.test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liangzhiyan on 2017/1/3.
 */
public class Test20170103 {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);

    private static class Lock {

        private ThreadLocal<ReentrantLock> threadLocal = new ThreadLocal<>();

        private static final LoadingCache<String, ReentrantLock> loadingCache = CacheBuilder.newBuilder().maximumSize(50000)
                .initialCapacity(50000).recordStats().expireAfterAccess(5, TimeUnit.SECONDS).build(new CacheLoader<String, ReentrantLock>() {
                    @Override
                    public ReentrantLock load(String s) throws Exception {
                        return new ReentrantLock();
                    }
                });

        public void lock(String userId, String product) throws ExecutionException {
            ReentrantLock lock = getLock(userId, product);
            lock.lock();
        }

        public void unlock(String userId, String product) {
            ReentrantLock lock = null;
            try {
                lock = getLock(userId, product);
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.out.println("[SynCache.unlock]获取同步锁出错,userId:" + userId + ",product=" + product);
            }
            if (lock != null) {
                lock.unlock();
            }
        }

        public boolean isLock(String userId, String product) {
            try {
                return getLock(userId, product).isLocked();
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.out.println("[SynCache.isLock]判断是否锁上出错,userId:" + userId + ",product=" + product);
            }
            return false;
        }

        public ReentrantLock getLock(String userId, String product) throws ExecutionException {
            ReentrantLock lock = threadLocal.get();
            if (lock == null) {
                lock = loadingCache.get(buildKey(userId, product));
                threadLocal.set(lock);
            }
            return lock;
        }

        public ReentrantLock getReentrantLock(String userId, String product) throws ExecutionException {
            return loadingCache.get(buildKey(userId, product));
        }

        private String buildKey(String userId, String product) {
            return "lock-" + userId + "-" + product;
        }
    }

    private static int number = 0;

    public static void main(String[] args) {
        final Lock lock = new Lock();
        for (int i = 0; i < 1000; i++) {

            final int finalI = 1;
//            executorService.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
////                        lock.lock("test-" + finalI, "test");
////                        ReentrantLock reentrantLock = lock.getReentrantLock("test-" + finalI, "test");
////                        reentrantLock.lock();
//                        ++number;
////                        reentrantLock.unlock();
////                        lock.unlock("test-" + finalI, "test");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int n = new Random().nextInt(100) + 100;
                    for (int j = 0; j < n; j++) {

                    }
                        number = number + 1;
                    try {
                        lock.lock("test-" + finalI, "test");
                        lock.unlock("test-" + finalI, "test");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("number = " + number);
    }
}
