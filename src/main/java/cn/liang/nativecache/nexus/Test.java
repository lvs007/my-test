package cn.liang.nativecache.nexus;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Created by liangzhiyan on 2016/12/7.
 */
public class Test {
    private static boolean close = false;

    public static void main(String[] args) throws InterruptedException {
        Map<String,Integer> map = new HashMap<>();
        map.put("value1",20);
        map.put("value2",20);
        System.out.println(JSON.toJSONString(map));
    }
}
