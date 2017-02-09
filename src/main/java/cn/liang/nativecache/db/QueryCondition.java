package cn.liang.nativecache.db;

/**
 * Created by mc-050 on 2017/2/7 10:14.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class QueryCondition {
    private String column;
    private Object value;
    private Condition condition;

    public QueryCondition(String column, Object value, Condition condition) {
        validValue(value, condition);
        this.column = column;
        this.value = value;
        this.condition = condition;
    }

    private void validValue(Object value, Condition condition) {
        String valueType = value.getClass().getTypeName();
        if (condition == Condition.IN) {
            if (!valueType.contains("List") && !valueType.contains("[]")) {
                throw new RuntimeException("错误的in参数，只能是List或者数组");
            }
        }
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
