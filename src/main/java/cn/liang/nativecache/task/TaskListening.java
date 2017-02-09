package cn.liang.nativecache.task;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by mc-050 on 2016/3/16.
 */
public class TaskListening implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ReflushTask.doTask();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
