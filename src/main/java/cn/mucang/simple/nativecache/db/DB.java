package cn.mucang.simple.nativecache.db;

import cn.mucang.simple.utils.CollectionUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liangzhiyan on 2017/1/9.
 */
public class DB {

    private static final String INDEX_SPLIT = "-";
    private static final String INDEX_VALUE_SPLIT = "#";

    /**
     * 存储每一列值对应的行号
     */
    private Map<String, Set<Long>> lineMap = new HashMap<>();

    /**
     * 存储每一行的数据
     */
    private Table<Class, Long, Object> tableLineData = HashBasedTable.create();

    /**
     * 字典树，用了对String做前缀匹配
     */
    private Trie trie = new Trie();

    /**
     * 存储每个表的索引信息，key是全表名，value是索引值
     * 联合索引，用-隔开
     */
    private Map<String, Set<String>> indexMap = new HashMap<>();

    //临时的索引存储，用来生成索引数据
    private Map<String, String> linshiIndexMap = new HashMap<>();

    private class Note {
        private long line;
        private String table;
        private Object data;
        private String columnName;
        private ColumnType columnType;

        public Note(long line, String table, Object data, String columnName, ColumnType columnType) {
            this.line = line;
            this.table = table;
            this.data = data;
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public long getLine() {
            return line;
        }

        public void setLine(long line) {
            this.line = line;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public ColumnType getColumnType() {
            return columnType;
        }

        public void setColumnType(ColumnType columnType) {
            this.columnType = columnType;
        }
    }


    public String buildKey(String tableName, String columnName, Object columnValue) {
        return tableName + "-" + columnName + "-" + columnValue == null ? "" : columnValue.toString();
    }


    private enum ColumnType {
        BOOLEAN, BIT, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, DATE, DATE_TIME, LIST, SET, MAP;

        private static ColumnType getColumnType(String name) {
            if (StringUtils.equals(name, "int") || StringUtils.equals(name, "java.lang.Integer")) {
                return INT;
            } else if (StringUtils.equals(name, "java.lang.String")) {
                return STRING;
            } else if (StringUtils.equals(name, "java.util.Date")) {
                return DATE;
            }
            return BIT;
        }
    }

    public static void main(String[] args) {
        DB db = new DB();
        String[] address = {"北京", "上海", "深圳", "广州", "南宁"};
        try {
            for (int i = 0; i < 100; i++) {
                String add = address[i % 5] ;
//                System.out.println(add);
                Emp emp = new Emp("test" + i % 10, add, i, new Date());
                db.insert(emp);
            }
            db.createUnionIndex(Emp.class, "address", "userName");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
//        System.out.println(db.get(Emp.class, "address", "beijing1"));
//        System.out.println(db.getLike(Emp.class, "address", "南宁"));
        Map<String, Object> map = new HashMap<>();
        map.put("address", "南宁");
        map.put("userName", "test9");
        map.put("age",79);
        try {
            System.out.println(db.get(Emp.class, map));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> get(Class table, String columnName, Object param) {
        String key = buildKey(table.getName(), columnName, param);
        Set<Long> lineSet = lineMap.get(key);
        List<T> result = new ArrayList<>();
        for (Long line : lineSet) {
            result.add((T) tableLineData.get(table, line));
        }
        return result;
    }

    public <T> List<T> getLike(Class table, String columnName, String param) {
        Set<Long> lineSet = trie.searchLine(table, columnName, param);
        if (CollectionUtils.isEmpty(lineSet)) {
            return Collections.emptyList();
        } else {
            List<T> result = new ArrayList<>();
            for (Long line : lineSet) {
                result.add((T) tableLineData.get(table, line));
            }
            return result;
        }
    }

    public <T> List<T> getAll(Class table) {
        Collection<Object> collection = tableLineData.row(table).values();
        List<T> result = new ArrayList<>();
        for (Object object : collection) {
            result.add((T) object);
        }
        return result;
    }

    /**
     * 支持联合索引
     *
     * @param table
     * @param columnAndValueMap 联合索引集合，必须是两个或两个以上的字段
     * @param <T>
     * @return
     */
    public <T> List<T> get(Class table, Map<String, Object> columnAndValueMap) throws NoSuchFieldException, IllegalAccessException {

        if (columnAndValueMap == null || columnAndValueMap.size() <= 1) {
            throw new RuntimeException("不支持这种方式参数的查询");
        }
        Set<String> keySet = columnAndValueMap.keySet();
        Set<String> tableIndex = indexMap.get(table.getName());
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

    private <T> List<T> matchOnlyIndex(BestIndex bestIndex, Class table, Map<String, Object> columnAndValueMap) {
        return get(table, bestIndex.getIndex(), columnAndValueMap.remove(bestIndex.getIndex()));
    }

    private <T> List<T> matchUnionIndex(BestIndex bestIndex, Class table, Map<String, Object> columnAndValueMap) {
        String[] indexArray = StringUtils.split(bestIndex.getIndex(), INDEX_SPLIT);
        String indexValue = "";
        for (int i = 0; i < bestIndex.getCount(); i++) {
            Object value = columnAndValueMap.remove(indexArray[i]);
            if (i == 0) {
                indexValue += (value == null ? "NULL" : value.toString());
            } else {
                indexValue += INDEX_VALUE_SPLIT + (value == null ? "NULL" : value.toString());
            }
        }
        Set<Long> lineSet = trie.searchLine(table, bestIndex.getIndex(), indexValue);
        List<T> result = new ArrayList<>();
        for (Long line : lineSet) {
            result.add((T) tableLineData.get(table, line));
        }
        return result;
    }

    private BestIndex findBestMatch(Set<String> tableIndex, Set<String> keySet) {
        int best = 0;
        String result = "";
        BestIndex bestIndex = new BestIndex();
        for (String index : tableIndex) {
            String[] indexArray = StringUtils.split(index, INDEX_SPLIT);
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

    private Map<Long, Object> getAllInfo(Class table) {
        return tableLineData.row(table);
    }

    public <T> T join(Join join) {
        Map<String, Object> leftColumnValue = join.getLeftColumnValue();
        if (CollectionUtils.isNotEmpty(leftColumnValue)) {

        }
        return null;
    }

    /**
     * 插入一条数据
     *
     * @param object
     * @throws IllegalAccessException
     */
    public void insert(Object object) throws IllegalAccessException {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        long line = generateLine();
        String table = clazz.getName();
        for (Field field : fields) {
            String fieldName = field.getName();
            field.setAccessible(true);
            Object value = field.get(object);
            field.setAccessible(false);
            String typeName = field.getGenericType().getTypeName();
            ColumnType columnType = ColumnType.getColumnType(typeName);
            Note note = createNote(line, table, value, fieldName, columnType);
            putColumn(line, table, fieldName, value);
            if (columnType == ColumnType.STRING && value != null) {
                trie.insert(clazz, fieldName, value.toString(), line);
            }
        }
        putTable(clazz, line, object);
    }

    private void putColumn(long line, String tableName, String columnName, Object columnValue) {
        String key = buildKey(tableName, columnName, columnValue);
        if (lineMap.containsKey(key)) {
            lineMap.get(key).add(line);
        } else {
            Set<Long> set = new HashSet<>();
            set.add(line);
            lineMap.put(key, set);
        }
    }

    private void putTable(Class table, long line, Object value) {
        tableLineData.put(table, line, value);
    }

    private Note createNote(long line, String table, Object data, String columnName, ColumnType columnType) {
        Note note = new Note(line, table, data, columnName, columnType);
        return note;
    }

    public synchronized void createUnionIndex(Class table, String... columnNames) throws NoSuchFieldException, IllegalAccessException {
        if (columnNames == null || columnNames.length <= 1) {
            return;
        }
        String value = "";
        for (String columnName : columnNames) {
            value += columnName + INDEX_SPLIT;
        }
        value = value.substring(0, value.length() - 1);
        String key = table.getName();
        linshiIndexMap.put(key, value);
        if (indexMap.containsKey(key)) {
            indexMap.get(key).add(value);
        } else {
            Set<String> set = new HashSet<>();
            set.add(value);
            indexMap.put(key, set);
        }
        //生成索引数据
        createIndexData(table);
    }

    public void createIndexData(Class table) throws NoSuchFieldException, IllegalAccessException {
        String index = linshiIndexMap.get(table.getName());
        if (StringUtils.isBlank(index)) {
            return;
        }
        Map<Long, Object> lineDataMap = getAllInfo(table);
        for (Map.Entry<Long, Object> entry : lineDataMap.entrySet()) {
            long line = entry.getKey();
            Object object = entry.getValue();
            createIndexData(object, index, line);
        }
    }

    private void createIndexData(Object object, String index, long line) throws NoSuchFieldException, IllegalAccessException {
        String[] columns = index.split(INDEX_SPLIT);
        Class clazz = object.getClass();
        String indexValue = "";
        boolean tag = false;
        for (String column : columns) {
            Field field = clazz.getDeclaredField(column);
            field.setAccessible(true);
            Object value = field.get(object);
            if (tag) {
                indexValue += INDEX_VALUE_SPLIT + (value == null ? "NULL" : value.toString());
            } else {
                indexValue += (value == null ? "NULL" : value.toString());
            }
            field.setAccessible(false);
            tag = true;
        }
        System.out.println(index + "\t" + indexValue + "\t" + line);
        trie.insert(clazz, index, indexValue, line);
    }

    private AtomicLong line = new AtomicLong(0);

    private long generateLine() {
        return line.incrementAndGet();
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

    public static class Join {
        private Class leftTable;
        private Class rightTable;
        private List<Pair> pairList;
        private Map<String, Object> leftColumnValue;
        private Map<String, Object> rightColumnValue;

        public Class getLeftTable() {
            return leftTable;
        }

        public void setLeftTable(Class leftTable) {
            this.leftTable = leftTable;
        }

        public Class getRightTable() {
            return rightTable;
        }

        public void setRightTable(Class rightTable) {
            this.rightTable = rightTable;
        }

        public List<Pair> getPairList() {
            return pairList;
        }

        public void setPairList(List<Pair> pairList) {
            this.pairList = pairList;
        }

        public Map<String, Object> getLeftColumnValue() {
            return leftColumnValue;
        }

        public void setLeftColumnValue(Map<String, Object> leftColumnValue) {
            this.leftColumnValue = leftColumnValue;
        }

        public Map<String, Object> getRightColumnValue() {
            return rightColumnValue;
        }

        public void setRightColumnValue(Map<String, Object> rightColumnValue) {
            this.rightColumnValue = rightColumnValue;
        }
    }

    public static class Pair {
        private String leftColumn;
        private String rightColumn;

        public String getLeftColumn() {
            return leftColumn;
        }

        public void setLeftColumn(String leftColumn) {
            this.leftColumn = leftColumn;
        }

        public String getRightColumn() {
            return rightColumn;
        }

        public void setRightColumn(String rightColumn) {
            this.rightColumn = rightColumn;
        }
    }

    private static class Emp {
        private String userName;
        private String address;
        private int age;
        private Date birthDay;

        public Emp() {
        }

        public Emp(String userName, String address, int age, Date birthDay) {
            this.userName = userName;
            this.address = address;
            this.age = age;
            this.birthDay = birthDay;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(Date birthDay) {
            this.birthDay = birthDay;
        }

        @Override
        public String toString() {
            return "Emp{" +
                    "userName='" + userName + '\'' +
                    ", address='" + address + '\'' +
                    ", age=" + age +
                    ", birthDay=" + birthDay +
                    '}';
        }
    }
}
