package cn.mucang.simple.nativecache.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by mc-050 on 2016/2/29.
 */
@Component
public class RedisCacheImpl implements RedisCache {

    @Override
    public void set(String key, Map<String, Map<String, String>> mapMap) {

    }

    @Override
    public <T> void set(String key, String key1, String key2, T value) {

    }

    @Override
    public <T> void incr(String key, String key1, String key2, T value) {

    }

    @Override
    public <T> void set(T value, String key, String... keys) {
        redisTemplate.opsForHash().put(key,buildKey(keys),value.toString());
    }

    @Override
    public <T> double incr(T value, String key, String... keys) {
        return redisTemplate.opsForHash().increment(key,buildKey(keys),Double.valueOf(value.toString()));
    }

    private String buildKey(String ... keys){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String k : keys){
            if (i != 0) {
                sb.append("#");
            }
            sb.append(k);
            ++i;
        }
        return sb.toString();
    }

    private RedisTemplate redisTemplate;
    private static Logger logger = LoggerFactory.getLogger(RedisCacheImpl.class);

    @Override
    public long incr(final String key, final long value) {
        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                byte[] k = keySerializer.serialize(key);
                return redisConnection.incrBy(k, value);
            }
        });
        return result == null ? 0 : (long) result;
    }

    @Override
    public long decr(final String key, final long value) {
        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                byte[] k = keySerializer.serialize(key);
                return redisConnection.decrBy(k, value);
            }
        });
        return result == null ? 0 : (long) result;
    }

    @Override
    public boolean exist(final String key) {
        Boolean bool = (Boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                byte[] k = keySerializer.serialize(key);
                return redisConnection.exists(k);
            }
        });
        return bool == null ? false : bool;
    }

    @Override
    public void set(final String key, final String value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                byte[] k = keySerializer.serialize(key);
                byte[] v = keySerializer.serialize(value);
                redisConnection.set(k, v);
                return null;
            }
        });
    }

    @Override
    public void set(final String key, final String value, final long t) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                byte[] k = keySerializer.serialize(key);
                byte[] v = keySerializer.serialize(value);
                redisConnection.setEx(k, TimeoutUtils.toSeconds(t, TimeUnit.SECONDS), v);
                return null;
            }
        });
    }

    @Override
    public boolean setNx(final String key, final String value) {

        return (boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                byte[] v = redisTemplate.getStringSerializer().serialize(value);
                return redisConnection.setNX(k, v);
            }
        });

    }

    @Override
    public boolean setNx(final String key, final String value, final long t) {
        return (boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                byte[] v = redisTemplate.getStringSerializer().serialize(value);
                boolean tmp = redisConnection.setNX(k, v);
                if (!tmp) {
                    return tmp;
                }
                tmp = redisConnection.expire(k, t);
                return tmp;
            }
        });

    }

    @Override
    public void setObject(final String key, final Object obj) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                RedisSerializer<Object> valueSerializer = new JdkSerializationRedisSerializer();
                byte[] k = keySerializer.serialize(key);
                byte[] value = valueSerializer.serialize(obj);
                redisConnection.set(k, value);
                return null;
            }
        });
    }

    @Override
    public void setObject(final String key, final Object obj, final long t) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                RedisSerializer<Object> valueSerializer = new JdkSerializationRedisSerializer();
                byte[] k = keySerializer.serialize(key);
                byte[] value = valueSerializer.serialize(obj);
                redisConnection.setEx(k, TimeoutUtils.toSeconds(t, TimeUnit.SECONDS), value);
                return null;
            }
        });
    }

    @Override
    public String get(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                byte[] k = keySerializer.serialize(key);
                byte[] result = redisConnection.get(k);
                if (result == null) {
                    return null;
                }
                return new String(result);
            }
        });
    }

    @Override
    public Object getObject(final String key) {

        return redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                RedisSerializer<Object> jdkserialize = new JdkSerializationRedisSerializer();
                RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
                byte[] k = keySerializer.serialize(key);
                byte[] v = redisConnection.get(k);
                Object value = jdkserialize.deserialize(v);
                return value;
            }
        });

    }

    @Override
    public boolean delete(final String key) {
        return (boolean) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                Long result = redisConnection.del(k);
                if (result != null && result > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public List<String> getListStr(final String key) {
        return (List<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                Long size = connection.lLen(k);
                List<byte[]> listByte = connection.lRange(k,0,size-1);
                List<String> result = new ArrayList<String>();
                for (byte[] b : listByte){
                    result.add(new String(b));
                }
                return result;
            }
        });
    }

    @Override
    public List<Object> getListObj(final String key) {
        return (List<Object>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<Object> jdkserialize = new JdkSerializationRedisSerializer();
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                Long size = connection.lLen(k);
                List<byte[]> listByte = connection.lRange(k,0,size-1);
                List<Object> result = new ArrayList<Object>();
                for (byte[] b : listByte){
                    result.add(jdkserialize.deserialize(b));
                }
                return result;
            }
        });
    }

    @Override
    public String getListStrIndex(final String key, final long index) {
        return (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                byte[] result = connection.lIndex(k,index);
                return new String(result);
            }
        });
    }

    @Override
    public Object getListObjIndex(final String key, final long index) {
        return redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<Object> jdkserialize = new JdkSerializationRedisSerializer();
                byte[] k = redisTemplate.getStringSerializer().serialize(key);
                byte[] result = connection.lIndex(k,index);
                return jdkserialize.deserialize(result);
            }
        });
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
    }
}
