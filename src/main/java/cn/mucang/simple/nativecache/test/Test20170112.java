package cn.mucang.simple.nativecache.test;

/**
 * Created by mc-050 on 2017/1/12 12:01.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class Test20170112 {

    public static void main(String[] args) {
        String packageName = "com.liang.controller";
        String packagePattern = "\\.*controller.?";
        packagePattern = packagePattern.replaceAll("\\.+$", "");

        packageName = packageName.replaceAll(packagePattern,"/v1/");
        System.out.println(packagePattern);
        System.out.println(packageName);
    }
}
