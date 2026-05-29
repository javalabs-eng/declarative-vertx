package org.javalabs.decl.container.diag;

import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.management.MBeanServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class fro taking the heap dump.
 * 
 * <p>
 * A Java heap dump is a snapshot of the Java heap memory at a specific point in time, containing 
 * information about all objects residing in the heap. It is primarily used for diagnosing memory-related 
 * issues like memory leaks and OutOfMemoryError exceptions.
 * 
 * <p>
 * If the jvm already has started with the options <code>-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath={file-path}</code>
 * then whenever an OutOfMemoryError occurs, jvm will create the heap dump.
 * 
 * Using this utility class one can generate the heap dump on the fly. All you have to do is, plug-in with 
 * a REST end point.
 *
 * @author Sudiptasish Chanda
 */
public final class VMDumpUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VMDumpUtil.class);
    
    private static final NumberFormat N_FORMAT = new DecimalFormat("0.00");
    private static final int MB = (1024 * 1024);
    
    // This is the name of the HotSpot Diagnostic MBean
    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
    
    private static final Lock HEAP_LOCK = new ReentrantLock();
    
    /**
     * API that takes a heap dump.
     * 
     * This API calls upon the sun's management API to initiate the heap dump.
     * the generated dump file will be kept in the directory as specified by the
     * <code>filePath</code>. If there is an existing dump file, then it will delete
     * the file before creating a fresh one.
     * 
     * @param filePath  Fully qualified dump file
     * @param live      Whether to include the live objects currently residing in the memory.
     * @return boolean  True, if the dump has been successfully taken. False otherwise
     */
    public static boolean dumpHeap(String filePath, boolean live) {
        long start = System.currentTimeMillis();
        
        try {
            HEAP_LOCK.lock();
            
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            
            // First get the current memory size.
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            LOGGER.info("OutofMemory occured. Heap size (MB): {}, Non-Heap size (MB): {}, Pending finalization: {}"
                    , memoryBean.getHeapMemoryUsage()
                    , memoryBean.getNonHeapMemoryUsage()
                    , memoryBean.getObjectPendingFinalizationCount());
            
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                LOGGER.info("Deleted existing heap dump: {}", filePath);
            }
            HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                    server, HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean.class);

            mxBean.dumpHeap(filePath, live);
            
            String size = "UNKNOWN";
            file = new File(filePath);
            if (file.exists()) {
                size = N_FORMAT.format(file.length() / MB);
            }
            
            LOGGER.info("Created new Heap Dump {}. File size (MB): {}. Elapsed time(ms): {}"
                    , filePath, size, (System.currentTimeMillis() - start));
            return true;
        }
        catch (Throwable th) {
            LOGGER.error("Error while taking JVM heap dump", th);
            return false;
        }
        finally {
            HEAP_LOCK.unlock();
        }
    }
}
