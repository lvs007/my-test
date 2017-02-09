package cn.liang.nativecache.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.*;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.marshaller.optimized.OptimizedMarshaller;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import javax.cache.Cache;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * Created by mc-050 on 2016/6/8.
 */
public class MyClient {

    private static Ignite ignite;
    static {
        Ignition.setClientMode(true);
        URL url = MyIgnite.class.getResource("/example-ignite.xml");
        Ignition.start(url);
        ignite = Ignition.ignite("myGrid");
    }

    public static void test1(){
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        List<String> addressList = new ArrayList<>();
        addressList.add("127.0.0.1:47500");
        addressList.add("127.0.0.1:47501");
        addressList.add("127.0.0.1:47502");

        ipFinder.setAddresses(addressList);
        tcpDiscoverySpi.setIpFinder(ipFinder);
        tcpDiscoverySpi.setLocalAddress("127.0.0.1");

        OptimizedMarshaller optimize = new OptimizedMarshaller();
        optimize.setRequireSerializable(false);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setLocalHost("127.0.0.1");

        cfg.setClientMode(true);
        cfg.setPeerClassLoadingEnabled(false);
        cfg.setMarshaller(optimize);
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(cfg);
        Collection<String> collection = ignite.cacheNames();
        System.out.println("cache names = "+collection);
    }

    /**
     * 通过IgniteCluster接口可以：

     启动和停止一个远程集群节点；
     获取集群成员的列表；
     创建逻辑集群组；
     */
    public static void test2(){
        Ignition.setClientMode(true);
        URL url = MyIgnite.class.getResource("/example-ignite.xml");
        Ignition.start(url);
        Ignite ignite = Ignition.ignite("myGrid");
        IgniteCluster igniteCluster = ignite.cluster();
        //
        Collection<ClusterNode> clusterNodes = igniteCluster.nodes();
        for (ClusterNode node : clusterNodes) {
            System.out.println("id : "+node.id());
        }
        //
        ClusterNode clusterNode = igniteCluster.localNode();
        System.out.println("address : "+clusterNode.addresses());
        System.out.println("host : "+clusterNode.hostNames());
        System.out.println("local id : "+clusterNode.id());
        IgniteCache<String,Object> igniteCache = ignite.getOrCreateCache("myCache");
        igniteCache.put("name","liang");
        igniteCache.put("score",100);
        ignite.close();
    }

    public static void test3(){
        Ignite ignite = null;
        try {
            Ignition.setClientMode(true);
//        Ignition.setDaemon(true);
            URL url = MyIgnite.class.getResource("/example-ignite.xml");
            Ignition.start(url);
            ignite = Ignition.ignite("myGrid");
            IgniteCache<String,Object> igniteCache = ignite.getOrCreateCache("score");
            System.out.println("igniteCache = "+igniteCache);
            System.out.println("score_user_1 = "+igniteCache.get("score_user_1"));
            System.out.println("score_user_1_math = "+igniteCache.get("score_user_1_math"));
//        igniteCache.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (ignite != null)
                ignite.close();
        }
    }

    public static void test4(){
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        List<String> addressList = new ArrayList<>();
        addressList.add("127.0.0.1:47500");
        spi.setIpFinder(ipFinder);
        IgniteConfiguration cfg = new IgniteConfiguration();
        // Override default discovery SPI.
        cfg.setDiscoverySpi(spi);
        // Start Ignite node.
        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String,Object> cache = ignite.getOrCreateCache("score");
        cache.put("score_user_1","first one");
        cache.put("score_user_1_math",100);
        ignite.close();
    }

    /**
     * 原子性,注意只要第一次调用了invoke，把processor传进去之后，
     * 即使之后再次修改processor都不会起作用，所以必须得重新启动服务，保证修改后的processor重新获取
     */
    public static void test5(){

        Ignite ignite = null;
        String key = "score_user_1_math";
        try {
            Ignition.setClientMode(true);
//            URL url = MyIgnite.class.getResource("/example-ignite.xml");
//            Ignition.start(url);
            ignite = Ignition.ignite("myGrid");
            IgniteCache<String,Integer> cache = ignite.cache("score");
            System.out.println("first value = "+cache.get(key));

            Integer value = cache.invoke(key, new EntryProcessor<String, Integer, Integer>() {
                @Override
                public Integer process(MutableEntry<String, Integer> mutableEntry, Object... objects) throws EntryProcessorException {
                    System.out.println("now entry = "+mutableEntry);
                    Integer value = mutableEntry.getValue();
                    mutableEntry.setValue(value == null ? 1: value + 1);
                    return mutableEntry.getValue();
                }
            });
            System.out.println("gen value = "+value);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (ignite != null)
                ignite.close();
        }
    }

    public static long incr(String key){
        try {
            IgniteCache<String,Object> cache = ignite.getOrCreateCache("score");
//            System.out.println("first value = "+cache.get(key));

            Object value = cache.invoke(key, new EntryProcessor<String, Object, Long>() {
                @Override
                public Long process(MutableEntry<String, Object> mutableEntry, Object... objects) throws EntryProcessorException {
//                    System.out.println("now entry = "+mutableEntry);
                    Object value = mutableEntry.getValue();
                    long longValue = value == null ? 1: Long.parseLong(value.toString()) + 1;
                    mutableEntry.setValue(longValue);
                    return longValue;
                }
            });
//            System.out.println("gen value = "+value);
            return Long.parseLong(value.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            if (ignite != null)
//                ignite.close();
        }
        return -1;
    }

//    static CacheConfiguration cacheConfiguration = new CacheConfiguration("testCache");
//    static IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
//    static Ignite ignite1 ;
//    static {
//        cacheConfiguration = new CacheConfiguration("testCache");
//        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
//        cacheConfiguration.setIndexedTypes(String.class,Person.class);
//        igniteConfiguration = new IgniteConfiguration();
//        igniteConfiguration.setClientMode(true);
//        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
//        igniteConfiguration.setGridName("cache2");
//        ignite1 = Ignition.start(igniteConfiguration);
//    }

//    public static void test6(String key,Person person){
//
//        IgniteCache<String,Person> cache = ignite1.cache("testCache");
//        cache.put(key,person);
//    }

    public static void test7(){
        CacheConfiguration cacheConfiguration = new CacheConfiguration("testCache");
        cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
        cacheConfiguration.setIndexedTypes(
                String.class, String.class,
                String.class, String.class,
                String.class, String.class,
                String.class, Integer.class
        );
        cacheConfiguration.setTypes(String.class,Person.class);
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setClientMode(true);
        igniteConfiguration.setCacheConfiguration(cacheConfiguration);
        Ignite ignite1 = Ignition.start(igniteConfiguration);
        IgniteCache<String,Person> cache = ignite1.cache("testCache");
        IgniteBiPredicate<String,Person> filter = new IgniteBiPredicate<String, Person>() {
            @Override
            public boolean apply(String s, Person person) {
                return person.getAge() > 20 && person.getAge() < 35;
            }
        };
        Query query = new ScanQuery<>(filter);
        TextQuery textQuery = new TextQuery(Person.class,"1465868667340");
        SqlQuery sqlQuery = new SqlQuery(Person.class," name like '%8150%'");

        QueryCursor<Person> cursor = cache.query(textQuery);
        cursor.close();
//        System.out.println("person size = "+cursor.getAll().size());
        System.out.println("all = "+cursor.getAll());
    }

    public static void testAdd(String key,Person person){

        IgniteCache<String,Person> cache = Ignition.ignite("myGrid").cache("personCache");
//        cache.withAsync();
        cache.put(key,person);
    }

    public static void test8(){
        Ignite ignite;

        Ignition.setClientMode(true);
//        URL url = MyIgnite.class.getResource("/example-cache.xml");
//        Ignition.start(url);
        ignite = Ignition.ignite("myGrid");
        IgniteCache<String,Person> cache = ignite.cache("personCache");
        IgniteBiPredicate<String,Person> filter = new IgniteBiPredicate<String, Person>() {
            @Override
            public boolean apply(String s, Person person) {
                return person.getAge() > 20 && person.getAge() < 35;
            }
        };
        Query query = new ScanQuery<>(filter);
        TextQuery textQuery = new TextQuery(Person.class,"1466419046102");
        SqlQuery sqlQuery = new SqlQuery(Person.class," name like '%497%'");

        QueryCursor<Person> cursor = cache.query(sqlQuery);
        cursor.close();
//        System.out.println("person size = "+cursor.getAll().size());
        System.out.println("all = "+cursor.getAll());
    }

    public static void test9(){
        IgniteCache<String,Object> cache = ignite.cache("");
        QueryMetrics queryMetrics = cache.queryMetrics();
    }

    public static void test10(String key){
        Ignite ignite;
        Ignition.setClientMode(true);
        ignite = Ignition.ignite("myGrid");
        IgniteCache<String,Person> cache = ignite.cache("personCache");
        System.out.println(cache.get(key));
        ignite.close();
    }

    public static void test11(){
        long startTime = System.currentTimeMillis();
        IgniteCache<String,Person> cache = ignite.cache("personCache");
        Iterator<Cache.Entry<String,Person>> iterator = cache.iterator();
        long midTime = System.currentTimeMillis();
        System.out.println("mid times = "+(midTime-startTime));
        Map<String,Person> map = new HashMap<>();
        while (iterator.hasNext()){
            Cache.Entry<String,Person> entry = iterator.next();
            map.put(entry.getKey(),entry.getValue());
        }
        long endTime = System.currentTimeMillis();

        System.out.println("times = "+(endTime-startTime));
        ignite.close();
    }

    public static void test12(){
        long startTime = System.currentTimeMillis();

        IgniteCache<String,Person> cache = ignite.cache("personCache");
        String sql = "age > 18 and age <= 30  order by age limit 0,10";
        Query<Person> query = new SqlQuery(Person.class,sql);
        QueryCursor<Person> cursor = cache.query(query);
        List<Person> list = new ArrayList<>();
        Iterator<Person> iterator = cursor.iterator();
//        System.out.println("one times = "+(System.currentTimeMillis()-startTime));
        for (;iterator.hasNext();){
            list.add(iterator.next());
        }

        long endTime = System.currentTimeMillis();
//        System.out.println(list);
//        System.out.println("times = "+(endTime-startTime));

        long time = System.currentTimeMillis();
//        System.out.println("all times = "+(time-endTime)+",size = "+ cache.size());
//        ignite.close();
    }

    public static void test13(){
        IgniteCache<String,Object> cache = ignite.cache("personCache");
        Lock lock = cache.lock("test1");
        try {
            System.out.println("we come");
            lock.lock();
            System.out.println("i'm sleeping");
            Thread.sleep(20000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
//        test2();
//        test3();
//        test4();
//        test5();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        long startTime = System.currentTimeMillis();
//        for (int i=0;i<100000;i++){
//
//            int age = new Random().nextInt(100);
//            String uuid = UUID.randomUUID().toString();
//            String key = uuid+"-name-"+System.currentTimeMillis()+"-"+new Random().nextInt(10000);
////            System.out.println("now key = "+key);
//            testAdd(key, new Person(key, age, uuid, "", System.currentTimeMillis()));
////                    incr("score_user_1_math"+new Random().nextInt(1000000));
////                    System.out.println("value = "+incr("score_user_1_math"));
//        }
//        test7();
//        test8();
//        testAdd("cb33e247-54a1-4b61-a2d2-a0fb1c5d6a44-name-1466505444133-7480", new Person("cb33e247-54a1-4b61-a2d2-a0fb1c5d6a44-name-1466505444133-7480", 100, "cb33e247-54a1-4b61-a2d2-a0fb1c5d6a44", "test 替换", System.currentTimeMillis()));
//        test10("cdf97a09-dcb8-4a27-9ebe-607611394c5e-name-1466505444559-4208");
//        test10("4f79ce7d-f92a-4bb8-b1e8-29029da5af01-name-1466595496840-6344");
//        test11();
//        ExecutorService es = Executors.newFixedThreadPool(8);
//
//        Future future = es.submit(new Runnable() {
//            @Override
//            public void run() {
//                for (int n = 0;n<1000000;n++){
//                    test12();
//                }
//            }
//        });
//        try {
//            System.out.println("result : "+future.get());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        test12();
        test13();
        long endTime = System.currentTimeMillis();
        System.out.println("spend time : "+(endTime-startTime));
    }

}
