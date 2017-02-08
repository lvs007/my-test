package cn.mucang.simple.nativecache.db;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by mc-050 on 2017/2/7 17:48.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class CacheQueryImpl implements Query{

    private DB db;

    public CacheQueryImpl(DB db) {
        this.db = db;
    }

    @Override
    public <T> List<T> get(Class table, Map<String, Object> columnAndValueMap) throws Exception {
        if (columnAndValueMap == null || columnAndValueMap.size() <= 1) {
            throw new RuntimeException("不支持这种方式参数的查询");
        }
        Set<String> keySet = columnAndValueMap.keySet();
        Set<String> tableIndex = db.indexMap.get(table.getName());
        BestIndex bestIndex = findBestMatch(tableIndex, keySet);//找到最佳的索引
        List<T> result = null;
        if (bestIndex.getCount() > 1) {//匹配上联合索引
            result = matchUnionIndex(bestIndex, table, columnAndValueMap);
        } else if (bestIndex.getCount() == 1) {//匹配上单一索引
            result = matchOnlyIndex(bestIndex, table, columnAndValueMap);
        } else {//没有匹配上索引
            result = getAll(table);
        }
        if (MapUtils.isNotEmpty(columnAndValueMap)) {
            Iterator<T> iterator = result.iterator();
            while (iterator.hasNext()) {
                T t = iterator.next();
                boolean tag = true;
                for (String column : columnAndValueMap.keySet()) {
                    Object value = ReflectUtils.getValue(t, column);
                    if (value == null && columnAndValueMap.get(column) == null) {
                        continue;
                    } else if (value == null || columnAndValueMap.get(column) == null
                            || !StringUtils.equals(value.toString(), columnAndValueMap.get(column).toString())) {
                        tag = false;
                        break;
                    }
                }
                if (!tag) {
                    iterator.remove();
                }
            }
        }
        return result;
    }

    @Override
    public <T> List<T> get(Class table, String columnName, Object param) {
        return null;
    }

    @Override
    public <T> List<T> getLike(Class table, String columnName, String param) {
        return null;
    }

    @Override
    public <T> List<T> getAll(Class table) {
        return null;
    }

    @Override
    public <T> List<T> get(Class table, List<QueryCondition> conditionList) {
        return null;
    }

    private <T> List<T> matchOnlyIndex(BestIndex bestIndex, Class table, Map<String, Object> columnAndValueMap) {
        return get(table, bestIndex.getIndex(), columnAndValueMap.remove(bestIndex.getIndex()));
    }

    private <T> List<T> matchUnionIndex(BestIndex bestIndex, Class table, Map<String, Object> columnAndValueMap) {
        String[] indexArray = StringUtils.split(bestIndex.getIndex(), DB.INDEX_SPLIT);
        String indexValue = "";
        for (int i = 0; i < bestIndex.getCount(); i++) {
            Object value = columnAndValueMap.remove(indexArray[i]);
            if (i == 0) {
                indexValue += (value == null ? "NULL" : value.toString());
            } else {
                indexValue += DB.INDEX_VALUE_SPLIT + (value == null ? "NULL" : value.toString());
            }
        }
        Set<Long> lineSet = db.trie.searchLine(table, bestIndex.getIndex(), indexValue);
        List<T> result = new ArrayList<>();
        for (Long line : lineSet) {
            result.add((T) db.tableLineData.get(table, line));
        }
        return result;
    }

    private BestIndex findBestMatch(Set<String> tableIndex, Set<String> keySet) {
        int best = 0;
        String result = "";
        BestIndex bestIndex = new BestIndex();
        for (String index : tableIndex) {
            String[] indexArray = StringUtils.split(index, DB.INDEX_SPLIT);
            int count = 0;
            for (String column : indexArray) {
                if (keySet.contains(column)) {
                    ++count;
                } else {
                    break;
                }
            }
            if (count > best) {
                best = count;
                result = index;
            }
        }
        bestIndex.setCount(best);
        bestIndex.setIndex(result);
        return bestIndex;
    }

    private class BestIndex {
        private int count;//匹配上的列数量
        private String index;//完整的索引

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }
}
