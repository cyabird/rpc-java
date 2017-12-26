package org.cyabird.core.task;

import org.junit.Test;

/**
 * @create: 2017-12-26
 * @description:
 */
public class StandardThreadExecutorTest {

    @Test
    public void testStandardThreadExecutor() {
        StandardThreadExecutor standardThreadExecutor = new StandardThreadExecutor();
        standardThreadExecutor.execute(() -> {
            System.out.println("测试。。。");
        });
    }
}
