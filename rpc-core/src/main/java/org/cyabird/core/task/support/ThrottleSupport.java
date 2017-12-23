package org.cyabird.core.task.support;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @create: 2017-11-21
 * @description:
 */
public class ThrottleSupport {

    /**
     * 当前线程数量
     */
    private AtomicInteger concurrencyCount = new AtomicInteger(0);

    /**
     * 锁对象
     */
    private Object monitor = new Object();

    /**
     * 是否异常
     */
    private boolean interrupted = false;

    /**
     * 运行前
     */
    public void beforeAccess() {
        concurrencyCount.getAndIncrement();
    }

    /**
     * 运行后
     */
    public void afterAccess() {
        this.concurrencyCount.getAndDecrement();
        synchronized (this.monitor) {
            if (this.concurrencyCount.get() == 0) {
                this.monitor.notify();
            }
        }
    }

    /**
     * 等待所有线程完成
     *
     * @see #concurrencyCount
     */
    public void awaitFinish() {
        synchronized (this.monitor) {
            // 如果超过,则无限循环此方法
            while (concurrencyCount.get() != 0) {
                try {
                    // 线程等待
                    this.monitor.wait();
                } catch (InterruptedException ex) {
                    // 发生异常,尝试终止线程
                    Thread.currentThread().interrupt();
                    interrupted = true;
                }
            }
            this.concurrencyCount.getAndIncrement();
        }
    }
}
