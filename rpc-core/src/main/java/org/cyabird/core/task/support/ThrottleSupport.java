package org.cyabird.core.task.support;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @see org.springframework.util.ConcurrencyThrottleSupport
 */
public class ThrottleSupport {

    /** 当前线程数量 （{@link ThreadPoolExecutor#getActiveCount()} 返回值并不准确） */
    private AtomicInteger concurrencyCount = new AtomicInteger(0);

    /** 运行前 */
    protected int beforeAccess() {
        return concurrencyCount.incrementAndGet();
    }

    /** 运行后 */
    protected int afterAccess() {
        return this.concurrencyCount.decrementAndGet();
    }

    /**
     * 返回当前任务数量
     *
     * @return
     */
    public AtomicInteger getConcurrencyCount() {
        return concurrencyCount;
    }
}
