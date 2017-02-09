package cn.liang.nativecache.hazelcast;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mc-050 on 2016/6/7.
 */
public class Test {

    /**
     * 对比Ignite性能，hazelcast没有优势！
     * @param args
     */
    public static void main(String[] args) {
//        Config config = new ClasspathXmlConfig(TestCache.class.getClassLoader(),"hazelcast.xml");
//        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
//        ITopic<TestCache.User> iTopic = hazelcastInstance.getTopic("test");
//        for (int i = 0; i < 100000; i++) {
//            TestCache.User user = new TestCache.User("lili"+new Random().nextInt(10),new Random().nextInt(100));
//            iTopic.publish(user);
//        }
        System.out.println(DateTime.now().monthOfYear().getAsString());
        System.out.println(DateTime.now().getYear()*100+DateTime.now().getMonthOfYear());
        System.out.println("end");
        String regex = "[0-9]{13}";
        String line = "                 968       2016-03-30 16:56:25.436    8081105581885";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()){
            line = matcher.group();
            System.out.println("line = "+line);
        }
        System.out.println("fafa/fad/fsdf".substring("fafa/fad/fsdf".lastIndexOf("/")+1));
        int sendType = 0;
        for (int i=1;i<=3;i++){
            sendType += (1 << i);
            System.out.println(sendType);
        }
        System.out.println(1 << 3);
        System.out.println("sendType = "+sendType);
    }
}
