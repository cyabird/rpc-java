package org.cyabird.core.task.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyabird.core.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @create: 2018-01-03
 * @description: 线程池满载拒绝策略
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {

    protected final Log log = LogFactory.getLog(getClass());

    /** 最后打印时间 */
    private static volatile long lastPrintTime = 0;

    /** 处理间隔时间 */
    private final static long INTERVAL_TIME = 10 * 60 * 1000;

    /** 只能同时处理一次 */
    private static Semaphore guard = new Semaphore(1);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("线程池资源耗尽!" +
                        " Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)!",
                e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        if (log.isWarnEnabled()) {
            log.warn(msg);
        }
        dumpJStack();
        throw new RejectedExecutionException(msg);
    }

    private void dumpJStack() {
        long now = System.currentTimeMillis();

        if (now - lastPrintTime < INTERVAL_TIME) {
            return;
        }
        // 尝试获取执行许可
        if (!guard.tryAcquire()) {
            // 获取失败则不执行
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            SimpleDateFormat sdf;
            // 用户路径
            // todo 自定义保存
            String dumpPath = System.getProperty("user.home").toLowerCase();

            // 获取所属系统
            String OS = System.getProperty("os.name").toLowerCase();

            // window 系统的文件不支持 ":"
            if (OS.contains(Constants.WINDOWS_SYS_PREFIX)) {
                sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            }

            String dateStr = sdf.format(new Date());
            FileOutputStream jstackStream = null;
            try {
                jstackStream = new FileOutputStream(new File(dumpPath, "JStack.log" + "." + dateStr));
            } catch (IOException e) {

            }

        });
    }
}
