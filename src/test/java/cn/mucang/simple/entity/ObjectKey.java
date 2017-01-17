package cn.mucang.simple.entity;

import java.util.Map;

/**
 * Created by mc-050 on 2016/4/1.
 */
public class ObjectKey {

    private String key;//唯一标识
    private String key1;//图表名称
    private String key2;//曲线名称
    private double value;

    private Map<String,Map<String,String>> map;
}
