package cn.liang.nativecache.task;

import cn.liang.nativecache.cache.CacheManager;
import cn.liang.nativecache.cache.NativeCacheOne;
import cn.liang.nativecache.action.FlushAction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mc-050 on 2016/3/16.
 */
public class Task implements Job{
    private static Logger LOG = LoggerFactory.getLogger(Task.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info("[start]缓存刷新任务开始，缓存name="+jobExecutionContext.getJobDetail().getName());
        TaskEntity taskEntity = (TaskEntity) jobExecutionContext
                .getJobDetail().getJobDataMap().get("action");
        FlushAction action = taskEntity.getAction();
        Object obj = action.flush();
        NativeCacheOne nativeCache = (NativeCacheOne) CacheManager.get(taskEntity.getName());
        nativeCache.put(obj);
        LOG.info("[end]缓存刷新任务结束,缓存name="+jobExecutionContext.getJobDetail().getName());
    }
}
