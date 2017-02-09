package cn.liang.nativecache.hazelcast;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;

/**
 * Created by mc-050 on 2016/6/7.
 */
public class Customer {

    public static int count = 0;

    public static void main(String[] args) {
        final Config config = new ClasspathXmlConfig(TestCache.class.getClassLoader(),"hazelcast.xml");
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        ITopic<TestCache.User> iTopic = hazelcastInstance.getTopic("test");
        iTopic.addMessageListener(new MessageListener<TestCache.User>() {
            @Override
            public void onMessage(Message<TestCache.User> message) {
                System.out.println("customer message : "+message.getMessageObject());
                ++count;
                System.out.println("now count = "+count);
            }
        });
        System.out.println("public count = "+iTopic.getLocalTopicStats().getPublishOperationCount());
        System.out.println("receive count = "+iTopic.getLocalTopicStats().getReceiveOperationCount());
    }
}
