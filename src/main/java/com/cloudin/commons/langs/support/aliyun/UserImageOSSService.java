package com.cloudin.commons.langs.support.aliyun;

import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

/**
 * 用户图片 OSS Service
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class UserImageOSSService extends OSSService {
	
	private String rootDir = "";
	
	public UserImageOSSService(String rootDir, String accessKeyID, String accessKeySecret, String ossDomain,
		String roleArn, int expir) {
		super(accessKeyID, accessKeySecret, ossDomain, roleArn, expir);

		if(rootDir.startsWith("/")) {
			this.rootDir = rootDir.substring(1);
		} else {
			this.rootDir = rootDir;
		}
		
	}
	
	/**
	 * 针对用户生成oss授权访问链接
	 *
	 * @param url    原始oss url
	 * @param userId 用户ID
	 *
	 * @return
	 */
	public String generateURLForUser(String url, Integer userId) {
		return generateReadOnlyURL(url, rootDir + userId + "/*", buildRoleSessionName(userId));
	}
	
	/**
	 * 针对管理员用户生成oss授权访问链接
	 *
	 * @param url         原始oss url
	 * @param adminUserId OA 用户Id
	 *
	 * @return
	 */
	public String generateURLForAdmin(String url, Integer adminUserId) {
		return generateReadOnlyURL(url, rootDir + "*", buildRoleSessionNameForAdmin(adminUserId));
	}
	
	/**
	 * 上传用户文件到oss。
	 * <p>
	 * 文件名： userId + "_" + currentTimeMillis + exName <br>
	 * 文件路径： {@link #rootDir} + userId + "/" + 文件名
	 * </p>
	 *
	 * @param userId      用户Id
	 * @param inputStream 文件输入流
	 * @param exName      文件扩展名，以 '.' 开头
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	public String uploadFile(Integer userId, InputStream inputStream, String exName) throws Exception {
		try {
			if (userId == null) {
				userId = 0;
			}
			
			String ossFileName = userId + "_" + System.currentTimeMillis() + exName;
			String path = userId + "/" + ossFileName;
			
			STSCredential credential = getWriteOnlyCredential(rootDir + "*", getBucket() + "_default",
															  getDefaultUseTime());
			OSSUtil.uploadFile(getEndPoint(), credential.getAccessKeyId(), credential.getAccessKeySecret(),
							   credential.getSecurityToken(), getBucket(), rootDir + path, inputStream);
			return String.format("http://%s.oss-%s.aliyuncs.com/%s", getBucket(), getRegion(), rootDir + path);
		} catch (ClientException e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 验证url格式
	 *
	 * @param url
	 * @param userId
	 * @return
	 */
	public boolean verifyUrl(String url, Integer userId){
		if(StringUtils.isEmpty(url) || userId == null){
			return false;
		}
		
		String prefix = getOssDomain() + "/" + rootDir + userId + "/";
		
		int index = url.indexOf(prefix);
		
		if(index == 0){
			String fileName = url.substring(prefix.length());
			if(fileName.matches("^([a-zA-Z0-9_]{5,30}).[a-zA-Z0-9]{2,10}$")){
				return true;
			}
		}
		return false;
	}
	
	private String generateOssKey(Integer userId) {
		return rootDir + userId + "/" + System.currentTimeMillis() + RandomStringUtils.random(4, true, true);
	}
	
	private String buildRoleSessionName(Integer userId) {
		return "OSS_SESSION_JUSER_" + String.valueOf(userId);
	}
	
	private String buildRoleSessionNameForAdmin(Integer adminUserId) {
		return "OSS_SESSION_ADMIN_" + String.valueOf(adminUserId);
	}
}
