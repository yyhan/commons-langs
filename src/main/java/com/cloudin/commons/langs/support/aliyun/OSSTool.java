package com.cloudin.commons.langs.support.aliyun;

/**
 * oss 静态工具类，支持静态初始化 和 spring 两种初始化方式
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class OSSTool {
	
	private static UserImageOSSService userImageOSSService;
	
	/**
	 * 静态初始化方法
	 *
	 * @param userImageOSSService
	 */
	public static void init(UserImageOSSService userImageOSSService) {
		OSSTool.userImageOSSService = userImageOSSService;
	}
	
	/**
	 * spring 方式初始化时使用
	 *
	 * @param userImageOSSService
	 */
	public void setUserImageOSSService(UserImageOSSService userImageOSSService) {
		OSSTool.userImageOSSService = userImageOSSService;
	}
	
	/**
	 * 针对用户生成oss授权访问链接
	 *
	 * @param url    原始oss url
	 * @param userId 用户ID
	 *
	 * @return
	 */
	public static String generateURLForUser(String url, Integer userId) {
		if(userImageOSSService == null){
			return url;
		}
		return userImageOSSService.generateURLForUser(url, userId);
	}
	
	/**
	 * 针对管理员用户生成oss授权访问链接
	 *
	 * @param url         原始oss url
	 * @param adminUserId OA 用户Id
	 *
	 * @return
	 */
	public static String generateURLForAdmin(String url, Integer adminUserId) {
		if(url == null){
			return null;
		}
		if(adminUserId == null){
			return userImageOSSService.generateURLForAdmin(url, -1);
		}
		if(userImageOSSService == null){
			return url;
		}
		return userImageOSSService.generateURLForAdmin(url, adminUserId);
	}
}
