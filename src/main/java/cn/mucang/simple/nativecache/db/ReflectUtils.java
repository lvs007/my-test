package cn.mucang.simple.nativecache.db;

import java.lang.reflect.Field;

/**
 * Created by mc-050 on 2017/2/4 19:15.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class ReflectUtils {

    public static Object getValue(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(object);
        field.setAccessible(false);
        return value;
    }
}
