package cn.mucang.simple.nativecache.db;

import java.util.List;
import java.util.Map;

/**
 * Created by mc-050 on 2017/2/7 10:10.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public interface Query {

    /**
     * 支持联合索引
     *
     * @param table
     * @param columnAndValueMap 联合索引集合，必须是两个或两个以上的字段
     * @param <T>
     * @return
     */
    public <T> List<T> get(Class table, Map<String, Object> columnAndValueMap) throws Exception;

    public <T> List<T> get(Class table, String columnName, Object param);

    public <T> List<T> getLike(Class table, String columnName, String param);

    public <T> List<T> getAll(Class table);

    public <T> List<T> get(Class table, List<QueryCondition> conditionList) throws Exception;
}
