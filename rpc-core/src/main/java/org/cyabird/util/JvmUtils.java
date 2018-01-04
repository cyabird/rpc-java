package org.cyabird.util;

import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @create: 2018-01-03
 * @description:
 */
public class JvmUtils {
    public static void jstack(OutputStream stream) {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo threadInfo : threadMxBean.dumpAllThreads(true, true)) {

        }
    }
}
