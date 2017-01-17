package cn.mucang.simple.nativecache.javaexpect;

import com.nemo.javaexpect.shell.Shell;
import com.nemo.javaexpect.shell.driver.TelnetDriver;

/**
 * Created by mc-050 on 2017/1/13 14:48.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class TelnetDemo {
    public static void main(String[] args) {
        TelnetDriver driver = new TelnetDriver("120.26.208.189");
        driver.setPort(22);
        driver.setAutoLogin("liangzhiyan", "2saLtWpqwdN56D2N", "]$");
        Shell shell = driver.open();
        shell.execute("ls");
        System.out.println(shell.execute("pwd"));
        shell.close();
    }

}
