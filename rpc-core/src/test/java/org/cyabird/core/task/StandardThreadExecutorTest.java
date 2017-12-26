package org.cyabird.core.task;

import org.junit.Test;

/**
 * @create: 2017-12-26
 * @description:
 */
public class StandardThreadExecutorTest {

    public static void main(String[] args) {
        StandardThreadExecutor standardThreadExecutor = new StandardThreadExecutor();
        standardThreadExecutor.execute(() -> {
            try {
                Thread.sleep(10000);
                System.out.println("测试1");
            }catch (InterruptedException e){
                Thread.interrupted();
            }
        });
        standardThreadExecutor.execute(() -> {
            try {
                System.out.println("测试2");
                Thread.sleep(10000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }
        });
        standardThreadExecutor.execute(() -> {
            try {
                System.out.println("测试3");
                Thread.sleep(10000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }
        });
        standardThreadExecutor.execute(() -> {
            try {
                System.out.println("测试4");
                Thread.sleep(10000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }
        });
        standardThreadExecutor.execute(() -> {
            try {
                System.out.println("测试5");
                Thread.sleep(10000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }
        });
    }

    @Test
    public void testStandardThreadExecutor() {

    }
}
