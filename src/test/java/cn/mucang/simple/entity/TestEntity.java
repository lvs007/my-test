package cn.mucang.simple.entity;

/**
 * Created by mc-050 on 2016/3/15.
 */
public class TestEntity {
    private String name;
    private int number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
