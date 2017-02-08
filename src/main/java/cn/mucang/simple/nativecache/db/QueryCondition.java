package cn.mucang.simple.nativecache.db;

/**
 * Created by mc-050 on 2017/2/7 10:14.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class QueryCondition {
    private String column;
    private Object value;
    private Condition condition;

    public QueryCondition() {
    }

    public QueryCondition(String column, Object value, Condition condition) {
        this.column = column;
        this.value = value;
        this.condition = condition;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
