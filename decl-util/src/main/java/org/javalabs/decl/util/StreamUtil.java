package org.javalabs.decl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class to handle IO operation.
 *
 * @author Sudiptasish Chanda
 */
public class StreamUtil {
    
    /**
     * Read the file and return a handle {@link InputStream} to this file.
     * If the file is not found in the specific directory or in the classpath, then
     * this API will return <code>null</code>.
     * 
     * @param filename      Name of the file to be read.
     * @return InputStream  The input stream connected to file handler.
     * @throws FileNotFoundException  if any error occurs while opening the file.
     */
    public static InputStream stream(final String filename) throws FileNotFoundException {
        // File input stream for the file to be read
        InputStream in = null;

        File file = new File(filename);
        if (file.exists()) {
            in = new FileInputStream(file);
        }
        else {
            in = StreamUtil.class.getClassLoader().getResourceAsStream(filename);
            if (in != null) {

            }
            else {
                URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                if (url != null) {
                    String urlFile = url.getFile();
                    urlFile = urlFile.replaceAll("%20", " ");
                    in = new FileInputStream(new File(urlFile));
                }
            }
        }
        return in;
    }
    
    /**
     * Read the content of a file in a byte array.
     * 
     * @param filename      Name of the file to be read.
     * @return byte[]       The raw binary content of the file
     * @throws IOException  if any error occurs while reading/opening the file.
     */
    public static byte[] read(final String filename) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(8192);
        byte[] buff = new byte[8192];
        int read = -1;
        
        try (InputStream in = stream(filename)) {
            if (in != null) {
                while ((read = in.read(buff)) != -1) {
                    bOut.write(buff, 0, read);
                }
            }
            bOut.close();
        };
        return bOut.toByteArray();
    }
    
    /**
     * API to read a zip file content.
     * 
     * @param input         The raw zip binary
     * @return List         Return the individual zip entry content.
     * @throws IOException  If any error occurs.
     */
    public static Map<String, byte[]> readZip(byte[] input) throws IOException {
        byte[] buff = new byte[128];
        int read = 0;
        ZipInputStream zis = null;
        
        Map<String, byte[]> map = new HashMap<>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            zis = new ZipInputStream(new ByteArrayInputStream(input));
            
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                while ((read = zis.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
                out.close();
                
                // Add only files, and not directory.
                if (! zipEntry.getName().endsWith("/")) {
                    map.put(zipEntry.getName(), out.toByteArray());
                }
                out.reset();
                zipEntry = zis.getNextEntry();
            }
            return map;
        }
        finally {
            if (zis != null) {
                zis.closeEntry();
                zis.close();
            }
        }
    }
    
    /**
     * Write the content to the file.
     * If the file is already present, it will override the file.
     * 
     * @param filename      Name of the file where the content is to be written
     * @param buff          Content to be written.
     * @throws IOException  If any error occurs.
     */
    public static void write(final String filename, byte[] buff) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            out.write(buff);
        }
    }
}
