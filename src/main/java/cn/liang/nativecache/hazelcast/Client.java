package cn.liang.nativecache.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Created by mc-050 on 2016/6/7.
 */
public class Client {

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        System.out.println("user = "+client.getMap("test").get("user1"));
        client.shutdown();
    }
}
