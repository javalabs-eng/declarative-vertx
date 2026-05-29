package org.javalabs.decl.container.diag;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for taking the thread dump.
 * 
 * <p>
 * Taking a Java thread dump involves capturing the state of all threads running within a Java Virtual Machine 
 * (JVM) at a specific point in time. This information is crucial for diagnosing performance issues, deadlocks, 
 * and other concurrency-related problems.
 * 
 * <p>
 * Using this utility class one can generate the thread dump on the fly. All you have to do is, plug-in with 
 * a REST end point.
 *
 * @author Sudiptasish Chanda
 */
public final class VMThreadUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VMThreadUtil.class);
    
    private static final Lock THREAD_LOCK = new ReentrantLock();
    
    /**
     * API to take the thread dump.
     * @return String
     */
    public static String dumpThread() {
        long start = System.currentTimeMillis();
        
        try {
            THREAD_LOCK.lock();
            
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] infos = threadBean.dumpAllThreads(true, true);
            
            StringBuilder buff = new StringBuilder(infos.length * 200);
            buff.append("JVM Thread dump. Date: ").append(new Date()).append("\n");
                
            for (ThreadInfo info : infos) {
                buff.append("\n")
                        .append("Name: ")
                        .append(info.getThreadName())
                        .append(". Id: ")
                        .append(info.getThreadId())
                        .append("\n")
                        .append("State: ")
                        .append(info.getThreadState())
                        .append("\n")
                        .append("Waited Count: ")
                        .append(info.getWaitedCount())
                        .append("\n")
                        .append("Blocked count: ")
                        .append(info.getBlockedCount());
                
                for (StackTraceElement ste : info.getStackTrace()) {
                    buff.append("\n\tat ").append(ste);
                }
            }
            LOGGER.info("Created new Thread Dump. Size: {}. Elapsed time(ms): {}"
                    , buff.length(), (System.currentTimeMillis() - start));
            return buff.toString();
        }
        catch (Throwable th) {
            LOGGER.error("Error while taking JVM thread dump", th);
            return null;
        }
        finally {
            THREAD_LOCK.unlock();
        }
    }
}
