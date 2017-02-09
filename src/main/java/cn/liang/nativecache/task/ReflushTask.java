package cn.liang.nativecache.task;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mc-050 on 2016/3/16.
 */
public class ReflushTask {

    private static Logger LOG = LoggerFactory.getLogger(ReflushTask.class);

    private static Map<String,TaskEntity> taskMap = new HashMap();

    public static void regist(TaskEntity task){
        taskMap.put(task.getName(),task);
        doTask(task);
    }

    private static void doTask(TaskEntity task){
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            JobDetail job = new JobDetail(task.getName(), Scheduler.DEFAULT_GROUP, Task.class);
            job.getJobDataMap().put("action", task);
            CronTrigger trigger = new CronTrigger("trigger_" + task.getName(),
                    Scheduler.DEFAULT_GROUP, task.getTimer());
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
            scheduler.triggerJob(task.getName(), Scheduler.DEFAULT_GROUP, job.getJobDataMap());
            LOG.info("缓存定时刷新线程启动");
        } catch (SchedulerException e) {
            LOG.error("",e);
        } catch (ParseException e){
            LOG.error("",e);
        }
    }

    public static void doTask(){
        Thread thread = new Thread(new DoTask());
        thread.start();
    }

    static class DoTask implements Runnable{
        public Scheduler scheduler;
        @Override
        public void run() {
            try {
                scheduler = new StdSchedulerFactory().getScheduler();
                for (String key : taskMap.keySet()){
                    TaskEntity task = taskMap.get(key);
                    JobDetail job = new JobDetail(task.getName(), Scheduler.DEFAULT_GROUP, Task.class);
                    job.getJobDataMap().put("action",task);
                    CronTrigger trigger = new CronTrigger("trigger_" + task.getName(),
                            Scheduler.DEFAULT_GROUP, task.getTimer());
                    scheduler.scheduleJob(job, trigger);
                }
                scheduler.start();
                LOG.info("缓存定时刷新线程启动");
            } catch (SchedulerException e) {
                LOG.error("",e);
            } catch (ParseException e){
                LOG.error("",e);
            }
        }
    }

}
