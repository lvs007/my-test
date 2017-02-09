package cn.mucang.simple.nativecache.db;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by mc-050 on 2017/2/7 17:48.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class CacheQueryImpl implements Query {

    private DB db;

    public CacheQueryImpl(DB db) {
        this.db = db;
    }

    @Override
    public <T> List<T> get(Class table, Map<String, Object> columnAndValueMap) throws Exception {
        List<T> result = getByUnionIndex(table, columnAndValueMap);
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

    private <T> List<T> getByUnionIndex(Class table, Map<String, Object> columnAndValueMap) {
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
        return result;
    }

    @Override
    public <T> List<T> get(Class table, String columnName, Object param) {
        String key = db.buildKey(table.getName(), columnName, param);
        Set<Long> lineSet = db.lineMap.get(key);
        List<T> result = new ArrayList<>();
        for (Long line : lineSet) {
            result.add((T) db.tableLineData.get(table, line));
        }
        return result;
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
    public <T> List<T> get(Class table, List<QueryCondition> conditionList) throws Exception {
        Map<String, Object> columnValueMap = new HashMap<>();
        Iterator<QueryCondition> iterator = conditionList.iterator();
        QueryCondition groupByCondition = null;
        QueryCondition orderByCondition = null;
        while (iterator.hasNext()) {
            QueryCondition queryCondition = iterator.next();
            if (queryCondition.getCondition() == Condition.EQ) {
                columnValueMap.put(queryCondition.getColumn(), queryCondition.getValue());
                iterator.remove();
            } else if (queryCondition.getCondition() == Condition.GROUP_BY) {
                groupByCondition = queryCondition;
            } else if (queryCondition.getCondition() == Condition.ORDER_BY) {
                orderByCondition = queryCondition;
            }
        }
        List<T> midResult;
        if (columnValueMap.size() > 1) {
            midResult = getByUnionIndex(table, columnValueMap);
        } else if (columnValueMap.size() == 1) {
            Map.Entry<String, Object> entry = columnValueMap.entrySet().iterator().next();
            midResult = get(table, entry.getKey(), entry.getValue());
        } else {
            midResult = getAll(table);
        }
        filterResultByCondition(midResult, conditionList);
        groupBy(midResult, groupByCondition);
        orderBy(midResult, orderByCondition);
        return midResult;
    }

    private <T> void groupBy(List<T> midResult, QueryCondition groupByCondition) {
        if (groupByCondition == null) {
            return;
        }

    }

    private <T> void orderBy(List<T> midResult, QueryCondition orderByCondition) {
        if (orderByCondition == null) {
            return;
        }
    }

    private <T> void filterResultByCondition(List<T> midResult, List<QueryCondition> conditionList) throws NoSuchFieldException, IllegalAccessException {
        if (CollectionUtils.isNotEmpty(conditionList)) {
            Iterator<T> iterator = midResult.iterator();
            while (iterator.hasNext()) {
                T t = iterator.next();
                boolean tag = true;
                for (QueryCondition queryCondition : conditionList) {
                    Object value = ReflectUtils.getValue(t, queryCondition.getColumn());
                    switch (queryCondition.getCondition()) {
                        case GT:
                        case GTE:
                        case LT:
                        case LTE: {
                            if (!compare(value, queryCondition.getValue(), queryCondition.getCondition())) {
                                tag = false;
                            }
                        }
                        break;
                        case IN: {
                            if (!in(value, queryCondition.getValue())) {
                                tag = false;
                            }
                        }
                        break;
                        case LIKE: {
                            if (!like(value, queryCondition.getValue())) {
                                tag = false;
                            }
                        }
                        break;
                    }
                    if (!tag) {
                        break;
                    }
                }
                if (!tag) {
                    iterator.remove();
                }
            }
        }
    }

    private boolean like(Object value, Object compareValue) {
        if (StringUtils.contains(String.valueOf(value), String.valueOf(compareValue))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean in(Object value, Object compareValue) {
        String valueType = compareValue.getClass().getTypeName();
        Set<Object> set = new HashSet<>();
        if (StringUtils.contains(valueType, "List")) {
            List list = (List) compareValue;
            set.addAll(list);
        } else {
            Object[] objects = (Object[]) compareValue;
            set.addAll(Arrays.asList(objects));
        }
        if (set.contains(value)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean compare(Object value, Object compareValue, Condition condition) {
        String valueType = value.getClass().getTypeName();
        if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Integer") ||
                StringUtils.equalsIgnoreCase(valueType, "int")) {
            if (condition == Condition.GT) {
                return Integer.parseInt(value.toString()) > Integer.parseInt(compareValue.toString());
            } else if (condition == Condition.GTE) {
                return Integer.parseInt(value.toString()) >= Integer.parseInt(compareValue.toString());
            } else if (condition == Condition.LT) {
                return Integer.parseInt(value.toString()) < Integer.parseInt(compareValue.toString());
            } else if (condition == Condition.LTE) {
                return Integer.parseInt(value.toString()) <= Integer.parseInt(compareValue.toString());
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.String")) {
            if (condition == Condition.GT) {
                return value.toString().compareTo(compareValue.toString()) > 0;
            } else if (condition == Condition.GTE) {
                return value.toString().compareTo(compareValue.toString()) >= 0;
            } else if (condition == Condition.LT) {
                return value.toString().compareTo(compareValue.toString()) < 0;
            } else if (condition == Condition.LTE) {
                return value.toString().compareTo(compareValue.toString()) <= 0;
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.util.Date")) {
            Date date = (Date) value;
            Date compareDate = (Date) compareValue;
            if (condition == Condition.GT) {
                return date.compareTo(compareDate) > 0;
            } else if (condition == Condition.GTE) {
                return date.compareTo(compareDate) >= 0;
            } else if (condition == Condition.LT) {
                return date.compareTo(compareDate) < 0;
            } else if (condition == Condition.LTE) {
                return date.compareTo(compareDate) <= 0;
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Long") ||
                StringUtils.equalsIgnoreCase(valueType, "long")) {
            if (condition == Condition.GT) {
                return Long.parseLong(value.toString()) > Long.parseLong(compareValue.toString());
            } else if (condition == Condition.GTE) {
                return Long.parseLong(value.toString()) >= Long.parseLong(compareValue.toString());
            } else if (condition == Condition.LT) {
                return Long.parseLong(value.toString()) < Long.parseLong(compareValue.toString());
            } else if (condition == Condition.LTE) {
                return Long.parseLong(value.toString()) <= Long.parseLong(compareValue.toString());
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Double") ||
                StringUtils.equalsIgnoreCase(valueType, "double")) {
            if (condition == Condition.GT) {
                return Double.parseDouble(value.toString()) > Double.parseDouble(compareValue.toString());
            } else if (condition == Condition.GTE) {
                return Double.parseDouble(value.toString()) >= Double.parseDouble(compareValue.toString());
            } else if (condition == Condition.LT) {
                return Double.parseDouble(value.toString()) < Double.parseDouble(compareValue.toString());
            } else if (condition == Condition.LTE) {
                return Double.parseDouble(value.toString()) <= Double.parseDouble(compareValue.toString());
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Float") ||
                StringUtils.equalsIgnoreCase(valueType, "float")) {
            if (condition == Condition.GT) {
                return Float.parseFloat(value.toString()) > Float.parseFloat(compareValue.toString());
            } else if (condition == Condition.GTE) {
                return Float.parseFloat(value.toString()) >= Float.parseFloat(compareValue.toString());
            } else if (condition == Condition.LT) {
                return Float.parseFloat(value.toString()) < Float.parseFloat(compareValue.toString());
            } else if (condition == Condition.LTE) {
                return Float.parseFloat(value.toString()) <= Float.parseFloat(compareValue.toString());
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Character") ||
                StringUtils.equalsIgnoreCase(valueType, "char")) {
            Character valueChar = (Character) value;
            Character compareValueChar = (Character) compareValue;
            if (condition == Condition.GT) {
                return valueChar > compareValueChar;
            } else if (condition == Condition.GTE) {
                return valueChar >= compareValueChar;
            } else if (condition == Condition.LT) {
                return valueChar < compareValueChar;
            } else if (condition == Condition.LTE) {
                return valueChar <= compareValueChar;
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Short") ||
                StringUtils.equalsIgnoreCase(valueType, "short")) {
            if (condition == Condition.GT) {
                return Short.parseShort(value.toString()) > Short.parseShort(compareValue.toString());
            } else if (condition == Condition.GTE) {
                return Short.parseShort(value.toString()) >= Short.parseShort(compareValue.toString());
            } else if (condition == Condition.LT) {
                return Short.parseShort(value.toString()) < Short.parseShort(compareValue.toString());
            } else if (condition == Condition.LTE) {
                return Short.parseShort(value.toString()) <= Short.parseShort(compareValue.toString());
            }
        } else if (StringUtils.equalsIgnoreCase(valueType, "java.lang.Byte") ||
                StringUtils.equalsIgnoreCase(valueType, "byte")) {
            if (condition == Condition.GT) {
                return Byte.parseByte(value.toString()) > Byte.parseByte(compareValue.toString());
            } else if (condition == Condition.GTE) {
                return Byte.parseByte(value.toString()) >= Byte.parseByte(compareValue.toString());
            } else if (condition == Condition.LT) {
                return Byte.parseByte(value.toString()) < Byte.parseByte(compareValue.toString());
            } else if (condition == Condition.LTE) {
                return Byte.parseByte(value.toString()) <= Byte.parseByte(compareValue.toString());
            }
        }
        return false;
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
        Set<Long> lineSet = db.trie.searchLine(table, bestIndex.getIndex(), indexValue + DB.END_SYMBOL);
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
