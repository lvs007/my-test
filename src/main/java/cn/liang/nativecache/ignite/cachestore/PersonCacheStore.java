package cn.liang.nativecache.ignite.cachestore;

import cn.liang.nativecache.ignite.Person;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mc-050 on 2016/6/21.
 */
//@Singleton
public class PersonCacheStore extends CacheStoreAdapter<String,Person> implements CacheStore<String,Person>{

    /** Auto-injected store session. */
    @CacheStoreSessionResource
    private CacheStoreSession storeSession;

    // This mehtod is called whenever "loadCache()" and "localLoadCache()"
    // methods are called on IgniteCache. It is used for bulk-loading the cache.
    // If you don't need to bulk-load the cache, skip this method.
    // 批量装载;
    @Override
    public void loadCache(IgniteBiInClosure<String, Person> igniteBiInClosure, Object... args) throws CacheLoaderException {
        if (args == null || args.length == 0 || args[0] == null)
            throw new CacheLoaderException("Expected entry count parameter is not provided.");
        final int entryCnt = (Integer)args[0];
        try (Connection conn = connection()) {
            try (PreparedStatement st = conn.prepareStatement("select name,age,address,context,number from t_person")) {
                try (ResultSet rs = st.executeQuery()) {
                    int cnt = 0;
                    while (cnt < entryCnt && rs.next()) {
                        Person person = new Person(rs.getString(1),rs.getInt(2), rs.getString(3),rs.getString(4),rs.getLong(5));
                        igniteBiInClosure.apply(person.getName(), person);
                        cnt++;
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new CacheLoaderException("Failed to load values from cache store.", e);
        }
    }

    // Complete transaction or simply close connection if there is no transaction.
    @Override
    public void sessionEnd(boolean commit) throws CacheWriterException {
        Connection conn = storeSession.attachment();
        try{
            System.out.println("sessionEnd connection isClose : "+(conn == null ?"null":conn.isClosed()));

            if (conn != null && storeSession.isWithinTransaction() && !conn.isClosed()){
                if (commit){
                    conn.commit();
                }
//                else {
//                    conn.rollback();
//                }
            }
        } catch (SQLException e) {
            throw new CacheWriterException("Failed to end store session.", e);
        }
    }

    // This mehtod is called whenever "get(...)" methods are called on IgniteCache.
    @Override
    public Person load(String key) throws CacheLoaderException {
        /**
         * todo:相当于reflesh
         */
        System.out.println("缓存中找不到："+key+",从数据库中获取");
        try (Connection conn = connection()){
            PreparedStatement ps = conn.prepareStatement("select name,age,address,context,number from t_person where name = ?");
            ps.setString(1,key);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Person person = new Person(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getLong(5));
                return person;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This mehtod is called whenever "getAll(...)" methods are called on IgniteCache.
    @Override
    public Map<String, Person> loadAll(Iterable<? extends String> keys) throws CacheLoaderException {
        Map<String, Person> loaded = new HashMap<>();
        //todo:通过keys去获取person，然后分别按照key put到loaded中
        return null;
    }

    // This mehtod is called whenever "put(...)" methods are called on IgniteCache.
    @Override
    public void write(Cache.Entry<? extends String, ? extends Person> entry) throws CacheWriterException {
        //todo:调用数据库持久操作，把对象持久到数据库中,此处最好用：（mysql使用"replace into"，oracle使用merge into）
        //有自增主键不能使用replace
        System.out.println("insert into db");
        Person person = entry.getValue();

        try (Connection conn = connection()){
            conn.prepareStatement("replace into t_person (name,age,address,context,number) values("
                    +"'"+ person.getName()+"',"+
                    person.getAge()+","
                    +"'"+person.getAddress()+"',"
                    +"'"+person.getContext()+"',"+
                    person.getNumber()+
                    ")").executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static AtomicInteger value = new AtomicInteger(0);

    // This mehtod is called whenever "putAll(...)" methods are called on IgniteCache.
    @Override
    public void writeAll(Collection<Cache.Entry<? extends String, ? extends Person>> collection) throws CacheWriterException {
        //todo:调用批量更新接口,此处最好用：（mysql使用"replace into"，oracle使用merge into）
        System.out.println("come here oh no : "+value.get());
        if (collection.size() < 1000){
            value.incrementAndGet();
        }
        try (Connection conn = connection()){
            PreparedStatement statement = conn.prepareStatement("replace into t_person (name,age,address,context,number) values(?,?,?,?,?)");
            for (Cache.Entry<? extends String, ? extends Person> entry : collection) {
                Person person = entry.getValue();
                statement.setString(1, person.getName());
                statement.setInt(2,person.getAge());
                statement.setString(3,person.getAddress());
                statement.setString(4,person.getContext());
                statement.setLong(5,person.getNumber());
                statement.addBatch();
            }
            System.out.println("exe count = "+count(statement.executeBatch()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int count(int[] array){
        int count = 0;
        for (int i : array){
            count += i;
        }
        return count;
    }

    // This mehtod is called whenever "remove(...)" methods are called on IgniteCache.
    @Override
    public void delete(Object key) throws CacheWriterException {
        //todo:调用删除接口

    }

    // This mehtod is called whenever "removeAll(...)" methods are called on IgniteCache.
    @Override
    public void deleteAll(Collection<?> keys) throws CacheWriterException {
        //todo:调用批量删除接口

    }

    private static List<Connection> queue = new ArrayList<>(15);
    private static AtomicInteger count = new AtomicInteger(0);
    private static Random random = new Random();

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
    // Opens JDBC connection and attaches it to the ongoing
    // session if within a transaction.
    private Connection connection() throws SQLException  {
        Connection conn = null;
        if (storeSession.isWithinTransaction()) {
            conn = storeSession.attachment();
            System.out.println("connection = "+conn+",session = "+storeSession);
            if (conn == null) {
                conn = openConnection(true);
                // Store connection in the session, so it can be accessed
                // for other operations within the same transaction.
//                storeSession.attach(conn);
            }
        }
        // Transaction can be null in case of simple load or put operation.
        else {
            if (conn == null) {
                conn = openConnection(true);
            }
        }
        storeSession.attach(conn);
        return conn;
    }

    private Connection getConnection() throws SQLException  {
        Connection conn = null;
        if (storeSession.isWithinTransaction()) {
            conn = threadLocal.get();
            System.out.println("connection = "+conn+",session = "+storeSession+",this thread : "+Thread.currentThread());
            if (conn == null) {
                conn = openConnection(false);
                threadLocal.set(conn);
                storeSession.attach(conn);
            }
        }else {
            if (conn == null) {
                conn = openConnection(true);
            }
        }
        return conn;
    }
    // Opens JDBC connection.
    private Connection openConnection(boolean autocommit) throws SQLException {
        // Open connection to your RDBMS systems (Oracle, MySQL, Postgres, DB2, Microsoft SQL, etc.)
        // In this example we use H2 Database for simplification.
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&noAccessToProcedureBodies=true", "root", "123456");
        conn.setAutoCommit(autocommit);
        return conn;
    }
}
