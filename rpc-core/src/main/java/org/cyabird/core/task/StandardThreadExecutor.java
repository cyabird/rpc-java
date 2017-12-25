package org.cyabird.core.task;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrencyThrottleSupport;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * StandardThreadExecutor execute执行策略：	优先扩充线程到maxThread，再offer到queue，如果满了就reject
 * <p>
 * 适应场景：  比较适合于业务处理需要远程资源的场景
 */
public class StandardThreadExecutor {

    /**
     * 核心线程池大小
     * <p>
     * 当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，
     * 即使此时线程池中存在空闲线程。
     * <p>
     * 当线程池达到corePoolSize时，新提交任务将被放入任务队列中.
     *
     * @see ThreadPoolTaskExecutor#corePoolSize
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 4;

    /**
     * 最大线程池大小
     * <p>
     * 如果任务队列已满,将创建最大线程池的数量执行任务,如果超出最大线程池的大小,
     * 将提交给RejectedExecutionHandler处理
     *
     * @see ThreadPoolTaskExecutor#maxPoolSize
     */
    private static final int DEFAULT_MAX_POOL_SIZE = 4;

    /**
     * 阻塞任务队列容量(默认为int的最大值)
     *
     * @see ThreadPoolTaskExecutor#queueCapacity
     */
    private static final int DEFAULT_QUEUE_CAPACITY = 15;

    /**
     * 线程池中超过核心线程数目的空闲线程最大存活时间；
     * 可以allowCoreThreadTimeOut(true)使得核心线程有效时间
     */
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;

    /**
     * 线程处理控制
     */
    private final ThrottleSupport throttleSupport = new ThrottleSupport();

    /** 线程池对象 */
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

    public StandardThreadExecutor() {
        this(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE);
    }

    public StandardThreadExecutor(int corePoolSize, int maxPoolSize) {
        this(corePoolSize, maxPoolSize, DEFAULT_QUEUE_CAPACITY);
    }

    public StandardThreadExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds) {
        this(corePoolSize, maxPoolSize, keepAliveSeconds, DEFAULT_QUEUE_CAPACITY);
    }

    public StandardThreadExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds, int queueCapacity) {
        this(corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity, new CustomizableThreadFactory());
    }

    public StandardThreadExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds, int queueCapacity, ThreadFactory threadFactory) {
        this(corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity, threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public StandardThreadExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds, int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setThreadFactory(threadFactory);
        threadPoolTaskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        // 设置最大并发数 maxPoolSize + queueCapacity
        throttleSupport.setConcurrencyLimit(maxPoolSize + queueCapacity);
        threadPoolTaskExecutor.initialize();
    }

    public void destroy() {
        Assert.state(threadPoolTaskExecutor != null, "线程池没有初始化");
        throttleSupport.awaitFinish();
        threadPoolTaskExecutor.destroy();
    }

    public void execute(Runnable task) {
        Assert.state(threadPoolTaskExecutor != null, "线程池没有初始化");
        throttleSupport.beforeAccess();
        threadPoolTaskExecutor.execute(() -> {
            try {
                task.run();
            } finally {
                throttleSupport.afterAccess();
            }
        });
    }

    public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
        Assert.state(threadPoolTaskExecutor != null, "线程池没有初始化");
        return threadPoolTaskExecutor.getThreadPoolExecutor();
    }

    private static class ThrottleSupport extends ConcurrencyThrottleSupport {

        /** 正在处理的任务数 */
        public AtomicInteger submittedTasksCount;

        /** 锁对象 */
        private Object monitor = new Object();

        /** 是否异常 */
        private boolean interrupted = false;

        /**
         * 运行前
         *
         * @see ConcurrencyThrottleSupport#beforeAccess()
         */
        @Override
        protected void beforeAccess() {
            super.beforeAccess();
            submittedTasksCount.getAndIncrement();
        }

        /**
         * 运行后
         *
         * @see ConcurrencyThrottleSupport#afterAccess()
         */
        @Override
        protected void afterAccess() {
            super.afterAccess();
            submittedTasksCount.getAndDecrement();
            monitor.notify();
        }

        /**
         * 等待所有线程完成
         *
         * @see #concurrencyCount
         */
        public void awaitFinish() {
            synchronized (monitor) {
                // 如果超过,则无限循环此方法
                while (submittedTasksCount.get() != 0) {
                    try {
                        // 线程等待
                        this.monitor.wait();
                    } catch (InterruptedException ex) {
                        // 发生异常,尝试终止线程
                        Thread.currentThread().interrupt();
                        interrupted = true;
                    }
                }
            }
        }
    }

}