package cn.liang.nativecache.nexus;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Created by liangzhiyan on 2016/12/7.
 */
public class Test {
    private static boolean close = false;

    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals("2")){
                iterator.remove();
            }
        }
        System.out.println(list);
        Map<String,Integer> map = new HashMap<>();
        map.put("value1",20);
        map.put("value2",20);
        System.out.println(JSON.toJSONString(map));
    }
}
