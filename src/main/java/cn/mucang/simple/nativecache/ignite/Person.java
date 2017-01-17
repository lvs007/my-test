package cn.mucang.simple.nativecache.ignite;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.query.annotations.QueryTextField;

import java.io.Serializable;

/**
 * Created by mc-050 on 2016/6/14.
 */
public class Person implements Serializable{

    private long id;
    @QuerySqlField(index = true)
    @QueryTextField
    private String name;
    private int age;
    @QueryTextField
    private String address;
    @QueryTextField
    private String context;
    @QueryTextField
    private long number;

    public Person() {
    }

    public Person(String name, int age, String address, String context, long number) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.context = context;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", context='" + context + '\'' +
                ", number=" + number +
                '}';
    }
}
