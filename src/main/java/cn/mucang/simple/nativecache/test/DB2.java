package cn.mucang.simple.nativecache.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mc-050 on 2017/1/12 18:52.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class DB2 {

    private Map<String, Note> tableMap = new HashMap<>();
    private Map<String, Note> dataMap = new HashMap<>();


    private class Note {
        private long count;
        private Set<Long> line;
        private List<Note> pre;
        private List<Note> next;
        private String table;
        private Object data;
        private String columnName;
        private ColumnType columnType;
    }

    private enum ColumnType {
        BOOLEAN, BIT, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, DATE, DATE_TIME, LIST, SET, MAP
    }
}
