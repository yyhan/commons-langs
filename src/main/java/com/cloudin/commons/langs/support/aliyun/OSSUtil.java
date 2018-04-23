package com.cloudin.commons.langs.support.aliyun;

import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云OSS服务工具类
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class OSSUtil {
	
	/**
	 * 使用指定的访问Id和密钥，生成 path 参数指定文件的签名后的访问链接。建议使用阿里云子账号的访问ID和密钥
	 *
	 * @param endPoint        OSS 服务的 Endpoint。
	 * @param accessKeyId     STS 提供的临时访问ID。
	 * @param secretAccessKey STS 提供的临时访问密钥。
	 * @param bucket          oss bucket
	 * @param path            oss 文件路径，例如： "/image/user/1/201801010001.jpg"
	 * @param expir           生成的链接有效期，单位：秒
	 *
	 * @return 签名后的访问链接
	 *
	 * @see <a href="https://help.aliyun.com/document_detail/32016.html?spm=5176.doc32009.6.667.8cXSaA">OSS 授权访问</a>
	 */
	public static String generateUrl(String endPoint, String accessKeyId, String secretAccessKey, String bucket,
		String path, int expir) {
		OSSClient client = new OSSClient(endPoint, accessKeyId, secretAccessKey);
		try {
			URL url = client.generatePresignedUrl(bucket, path, DateUtils.addSeconds(new Date(), expir));
			return url.toString();
		} finally {
			client.shutdown();
		}
	}
	
	/**
	 * 使用STS服务临时授权的安全令牌，生成 path 参数指定文件的签名后的访问链接
	 *
	 * @param endPoint        OSS 服务的 Endpoint。
	 * @param accessKeyId     STS 提供的临时访问ID。
	 * @param secretAccessKey STS 提供的临时访问密钥。
	 * @param securityToken   STS 提供的临时安全令牌。
	 * @param bucket          oss bucket
	 * @param path            oss 文件路径，例如： "/image/user/1/201801010001.jpg"
	 * @param expir           生成的链接有效期，单位：秒
	 *
	 * @return 签名后的访问链接
	 *
	 * @see <a href="https://help.aliyun.com/document_detail/32016.html?spm=5176.doc32009.6.667.8cXSaA">OSS 授权访问</a>
	 */
	public static String generateUrl(String endPoint, String accessKeyId, String secretAccessKey, String securityToken,
		String bucket, String path, int expir) {
		
		OSSClient client = new OSSClient(endPoint, accessKeyId, secretAccessKey, securityToken);
		try {
			URL url = client.generatePresignedUrl(bucket, path, DateUtils.addSeconds(new Date(), expir));
			return url.toString();
		} finally {
			client.shutdown();
		}
	}
	
	/**
	 * 上传文件。请确保 accessKeyId 具备 bucket 的 putObject 权限。依赖 “putObject” 权限
	 *
	 * @param endPoint        OSS服务的Endpoint。
	 * @param accessKeyId     访问ID。
	 * @param secretAccessKey 访问密钥。
	 * @param bucket          oss bucket
	 * @param path            oss 文件路径，例如： "/image/user/1/201801010001.jpg"
	 * @param file            文件
	 */
	public static void uploadFile(String endPoint, String accessKeyId, String secretAccessKey, String bucket,
		String path, File file) {
		OSSClient client = new OSSClient(endPoint, accessKeyId, secretAccessKey);
		try {
			client.putObject(bucket, path, file);
		} finally {
			client.shutdown();
		}
	}
	
	/**
	 * 使用STS服务临时授权的安全令牌，上传文件。请确保指定的安全令牌具备 bucket 的 putObject 权限。
	 *
	 * 不抛出异常，即是上传成功。依赖 “putObject” 权限
	 *
	 * @param endPoint        OSS 服务的 Endpoint。
	 * @param accessKeyId     STS 提供的临时访问ID。
	 * @param secretAccessKey STS 提供的临时访问密钥。
	 * @param securityToken   STS 提供的临时安全令牌。
	 * @param bucket          oss bucket
	 * @param path            oss 文件路径，例如： "/image/user/1/201801010001.jpg"
	 * @param inputStream     输入流
	 */
	public static void uploadFile(String endPoint, String accessKeyId, String secretAccessKey, String securityToken, String bucket,
		String path, InputStream inputStream) {
		OSSClient client = new OSSClient(endPoint, accessKeyId, secretAccessKey, securityToken);
		try {
			client.putObject(bucket, path, inputStream);
		} finally {
			client.shutdown();
		}
	}
	
	/**
	 * 使用STS服务临时授权的安全令牌，上传文件。请确保指定的安全令牌具备 bucket 的 putObject 权限。
	 *
	 * 不抛出异常，即是上传成功。依赖 “doesObjectExist” 权限
	 *
	 * @param endPoint        OSS 服务的Endpoint。
	 * @param accessKeyId     STS 提供的临时访问ID。
	 * @param secretAccessKey STS 提供的临时访问密钥。
	 * @param securityToken   STS 提供的临时安全令牌。
	 * @param bucket          oss bucket
	 * @param path            oss 文件路径，例如： "/image/user/1/201801010001.jpg"
	 *
	 * @return 返回path对应的文件是否存在
	 */
	public static boolean isExist(String endPoint, String accessKeyId, String secretAccessKey, String securityToken, String bucket,
		String path) {
		OSSClient client = new OSSClient(endPoint, accessKeyId, secretAccessKey, securityToken);
		try {
			return client.doesObjectExist(bucket, path);
		} finally {
			client.shutdown();
		}
	}
}
