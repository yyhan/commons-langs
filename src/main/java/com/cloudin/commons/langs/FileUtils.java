package com.cloudin.commons.langs;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * @author 小天
 * @version 1.0.0, 2017/11/3 0003 11:12
 */
public class FileUtils {

    public static String newTempFilePath(String exName) {
        return System.getProperty("java.io.tmpdir") + File.separatorChar + System
                .currentTimeMillis() + "_" + RandomStringUtils.random(5, true, true) + exName;
    }

    public static int createFile(InputStream stream, File tagFile) throws IOException {
        FileOutputStream fs = new FileOutputStream(tagFile);
        byte[] buffer = new byte[1024 * 1024];
        int bytesum = 0;
        int byteread = 0;
        while ((byteread = stream.read(buffer)) != -1) {
            bytesum += byteread;
            fs.write(buffer, 0, byteread);
            fs.flush();
        }
        close(fs);
        close(stream);
        return bytesum;
    }

    /**
     * 关闭输入流
     *
     * @param reader
     *
     * @throws java.io.IOException
     */
    public static void close(InputStream reader) throws IOException {
        if (null != reader) {
            reader.close();
        }
    }

    /**
     * 关闭输出流
     *
     * @param writer
     *
     * @throws java.io.IOException
     */
    public static void close(Writer writer) throws IOException {
        if (null != writer) {
            writer.close();
        }
    }

    /**
     * 关闭输出流
     *
     * @param outputStream
     *
     * @throws java.io.IOException
     */
    public static void close(OutputStream outputStream) throws IOException {
        if (null != outputStream) {
            outputStream.close();
        }
    }

    /**
     * 将输入流写入一个临时文件，该文件在 ${java.io.tmpdir} 目录下。 使用完后，请注意及时删除该临时文件
     *
     * @param stream 输入流
     * @param exName 文件扩展名
     *
     * @return 临时文件对象
     *
     * @throws java.io.IOException
     */
    public static File createTempFile(InputStream stream, String exName) throws IOException {
        String tempPath = newTempFilePath(exName);
        File tempFile = new File(tempPath);
        if (!tempFile.getParentFile().exists()) {
            if (tempFile.mkdirs()) {
                FileUtils.createFile(stream, tempFile);
            } else {
                throw new IOException("mkdirs fail : " + tempFile.getParentFile().getPath());
            }
        } else {
            FileUtils.createFile(stream, tempFile);
        }
        return tempFile;
    }

    /**
     * 获取文件扩展名
     *
     * @param originalFilename
     *
     * @return 文件扩展名
     */
    public static String getExName(String originalFilename) {
        if (StringUtils.isBlank(originalFilename)) {
            return "";
        }
        int idx = originalFilename.lastIndexOf(".");
        return originalFilename.substring(idx);
    }
}
