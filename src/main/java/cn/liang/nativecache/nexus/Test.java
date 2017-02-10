package cn.liang.nativecache.nexus;

import cn.mucang.simple.utils.Digests;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by liangzhiyan on 2016/12/7.
 */
public class Test {
    private static boolean close = false;

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> map = new HashMap<>();
        map.put("value1", 20);
        map.put("value2", 20);
        System.out.println(JSON.toJSONString(map));
        for (int i = 1; i < 21; i++) {
            File file = new File("D:\\tt\\score-backend-web-1.0-SNAPSHOT-" + i + ".rar");
            try (FileInputStream fileInputStream = new FileInputStream(file);){
                try (FileOutputStream fileOutputStream = new FileOutputStream("D:\\tt\\test"+i)){
                    IOUtils.copy(fileInputStream, fileOutputStream);
                }
                Digests.md5(file);
                System.out.println(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
