package com.cloudin.commons.langs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

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
	public static void close(Reader reader) throws IOException {
		if (null != reader) {
			reader.close();
		}
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
	
	/**
	 * 获取文件内容，以字符串返回
	 *
	 * @param basePath 目录
	 * @param fileName 文件名
	 *
	 * @return 文件内容
	 *
	 * @throws IOException IO 异常
	 */
	public static String getFileContent(String basePath, String fileName) throws IOException {
		return IOUtils.toString(new FileInputStream(getFile(basePath, fileName)), Charset.defaultCharset());
	}
	
	/**
	 * 获取文件内容，以字符串返回
	 *
	 * @param basePath 目录
	 * @param fileName 文件名
	 * @param charset  文件编码
	 *
	 * @return 文件内容
	 *
	 * @throws IOException IO 异常
	 */
	public static String getFileContent(String basePath, String fileName, Charset charset) throws IOException {
		return IOUtils.toString(new FileInputStream(getFile(basePath, fileName)), charset);
	}
	
	/**
	 * 获取文件/目录
	 *
	 * @param basePath 目录
	 * @param fileName 文件名
	 *
	 * @return
	 */
	public static File getFile(String basePath, String fileName) {
		// 如果文件名为空，则直接返回
		if (null == fileName || fileName.length() == 0) {
			return null;
		}
		
		File file;
		
		// 如果目录文件，则使用相对路径
		if (null == basePath || basePath.length() == 0) {
			file = new File(fileName);
		} else {
			file = new File(basePath, fileName);
		}
		
		return file;
	}
}
