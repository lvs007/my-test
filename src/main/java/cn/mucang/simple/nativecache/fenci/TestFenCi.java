package cn.mucang.simple.nativecache.fenci;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzhiyan on 2016/12/27.
 */
public class TestFenCi {
    public static void main(String[] args) {
        Map<String,Integer> map = new HashMap<>();
        map.put("value1",20);
        map.put("value2",60);
        System.out.println(JSON.toJSONString(map));
    }
}
