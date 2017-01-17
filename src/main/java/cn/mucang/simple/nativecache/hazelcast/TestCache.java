package cn.mucang.simple.nativecache.hazelcast;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import com.hazelcast.core.Client;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by mc-050 on 2016/6/7.
 */
public class TestCache {

    public static void main(String[] args) {
        Config config = new ClasspathXmlConfig(TestCache.class.getClassLoader(),"hazelcast.xml");
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);
        System.out.println("cluster:"+hazelcastInstance.getCluster());
        hazelcastInstance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
                System.out.println("client connet");
                System.out.println("client type : "+client.getClientType());
                System.out.println("socket address : "+client.getSocketAddress());
                System.out.println("uuid : "+client.getUuid());
            }

            @Override
            public void clientDisconnected(Client client) {
                System.out.println("client disconnect");
            }
        });
//        System.out.println("config : "+hazelcastInstance.getConfig());
        System.out.println("name : "+hazelcastInstance.getName());
        System.out.println("end point : "+hazelcastInstance.getLocalEndpoint());
        System.out.println("topic : "+hazelcastInstance.getTopic("test").getName());
        ITopic<User> iTopic = hazelcastInstance.getTopic("test");
        User user = new User("lili"+new Random().nextInt(10),new Random().nextInt(100));
        iTopic.publish(user);
        IMap<String,User> iMap = hazelcastInstance.getMap("test");
        iMap.put("user1",user);
        System.out.println("get map : "+iMap.get("user1"));
    }

    public static class User implements Serializable{
        private String name;
        private int number;

        public User(String name, int number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", number=" + number +
                    '}';
        }
    }
}
