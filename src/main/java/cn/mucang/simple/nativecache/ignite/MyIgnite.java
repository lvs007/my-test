package cn.mucang.simple.nativecache.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteBiPredicate;
import org.apache.ignite.lang.IgniteCallable;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by mc-050 on 2016/6/8.
 */
public class MyIgnite {

    public static void main(String[] args) {
//        Ignition.setClientMode(true);
        try {
            URL url = MyIgnite.class.getResource("/example-ignite.xml");
            InputStream is = MyIgnite.class.getResourceAsStream("/example-ignite.xml");
            System.out.println("is = " + is+",url="+url);
            Ignition.start(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Ignite ignite = Ignition.start("example-ignite.xml");
        Ignite ignite = Ignition.ignite("myGrid");
        IgniteCache<String,Person> cache = ignite.cache("personCache");
        cache.loadCache(null, 1000);
//        Collection<String> collection = ignite.cacheNames();
//        System.out.println("cache names = "+collection);

        String[] arrays = {"nihaoma","adgasad","fsdf","gasdfadefasf","fsaae"};
        Collection<IgniteCallable<Integer>> callables = new ArrayList<>();
        for (final String str : arrays){
            callables.add(new IgniteCallable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return str.length();
                }
            });
        }
        Collection<Integer> result = ignite.compute().call(callables);
        System.out.println("result = "+result+",name = "+ignite.name());
    }
}
