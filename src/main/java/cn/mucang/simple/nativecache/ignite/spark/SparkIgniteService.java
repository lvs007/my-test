package cn.mucang.simple.nativecache.ignite.spark;

import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.ApplicationContext;

/**
 * Created by mc-050 on 2016/6/22.
 */
public interface SparkIgniteService {
    public JavaSparkContext getSparkContext();
    public ApplicationContext getSpringContext();
}
