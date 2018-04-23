package com.cloudin.commons.langs.support.aliyun;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.cloudin.commons.langs.cache.MemcachedTemplate;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * <br> Created by xiaotian on 2017/9/5 0005.
 *
 * @see <a href="https://help.aliyun.com/document_detail/31867.html?spm=5176.doc31834.6.578.pLbBfC">阿里云 OSS 支持的操作 </a>
 * @see <a href="https://help.aliyun.com/document_detail/32016.html?spm=5176.doc31935.6.667.ml3zof">OSS Java SDK授权访问</a>
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class OSSService {
	
	private Logger logger = LogManager.getLogger(this);
	
	/**
	 * 建议使用子账户的 accessKeyID
	 */
	private String accessKeyID;
	/**
	 * 建议使用子账户的 accessKeySecret
	 */
	private String accessKeySecret;
	/**
	 * 存储用户敏感图片的bucket
	 */
	private String bucket;
	/**
	 * 角色的全局资源描述符，例如：acs:ram:*:1234567890123456:role/devops
	 * RoleArn 需要在 RAM 控制台上获取
	 */
	private String roleArn;
	
	/**
	 * OSS 所属区域，例如：cn-hangzhou
	 */
	private String region;
	/**
	 * OSS 访问坐标，例如：http://oss-cn-hangzhou.aliyuncs.com
	 */
	private String endPoint;
	/**
	 * OSS bucket 访问坐标，例如：http://bucket.oss-cn-hangzhou.aliyuncs.com
	 */
	private String ossDomain;
	/**
	 * STS临时访问凭证有效时间，单位：秒
	 */
	private int expir = 30 * 60;
	
	/**
	 * 距离过期时间的最小时间间隔，如果小于该时间，即使STS临时访问凭证未过期，也需要重新申请。单位：秒。默认120秒<br>
	 *     该值应当考虑：服务器与阿里云 STS 服务的时间不同步、服务器请求阿里云 STS 服务耗时、用户请求服务器耗时、浏览器渲染耗时等因素
	 */
	private int minTimeToExpir = 120;
	/**
	 * 存放STS临时令牌的缓存，缓存时间应小于或等于 {@link #expir}
	 */
	private MemcachedTemplate credentialsCache;
	/**
	 * 签名url默认使用时长，单位：秒，默认120秒
	 */
	private int defaultUseTime = 120;
	
	/**
	 * @param accessKeyID     访问ID
	 * @param accessKeySecret 访问密钥
	 * @param ossDomain       OSS 访问域名，格式： http://{bucket}.oss-{region}.aliyuncs.com
	 * @param roleArn         角色的全局资源描述符，在 RAM 控制台上获取，例如：acs:ram:*:1234567890123456:role/devops
	 * @param expir           STS临时访问凭证有效时间
	 */
	public OSSService(String accessKeyID, String accessKeySecret, String ossDomain, String roleArn, int expir) {
		this.accessKeyID = accessKeyID;
		this.accessKeySecret = accessKeySecret;
		this.roleArn = roleArn;
		this.expir = expir;
		this.ossDomain = ossDomain;
		
		Pattern pattern = Pattern.compile("^http[s]?:\\/\\/(\\S+).oss-(\\S+).aliyuncs.com$");
		Matcher matcher = pattern.matcher(ossDomain);
		if (matcher.matches() && matcher.groupCount() == 2) {
			this.region = matcher.group(2);
			this.bucket = matcher.group(1);
			this.endPoint = String.format("http://oss-%s.aliyuncs.com", this.region);
		} else {
			throw new IllegalArgumentException("ossDomain 格式错误");
		}
	}
	
	/**
	 *
	 * @param minTimeToExpir 距离过期时间的最小时间间隔
	 *
	 * @see #minTimeToExpir
	 */
	public void setMinTimeToExpir(int minTimeToExpir) {
		this.minTimeToExpir = minTimeToExpir;
	}
	
	/**
	 *
	 * @param defaultUseTime 签名url默认使用时长
	 *
	 * @see #defaultUseTime
	 */
	public void setDefaultUseTime(int defaultUseTime) {
		this.defaultUseTime = defaultUseTime;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public String getAccessKeyID() {
		return accessKeyID;
	}
	
	public String getAccessKeySecret() {
		return accessKeySecret;
	}
	
	public String getBucket() {
		return bucket;
	}
	
	public String getRoleArn() {
		return roleArn;
	}
	
	public String getRegion() {
		return region;
	}
	
	public String getEndPoint() {
		return endPoint;
	}
	
	public String getOssDomain() {
		return ossDomain;
	}
	
	public int getExpir() {
		return expir;
	}
	
	public int getMinTimeToExpir() {
		return minTimeToExpir;
	}
	
	public int getDefaultUseTime() {
		return defaultUseTime;
	}
	
	public MemcachedTemplate getCredentialsCache() {
		return credentialsCache;
	}
	
	public void setCredentialsCache(MemcachedTemplate credentialsCache) {
		this.credentialsCache = credentialsCache;
	}
	
	/**
	 * 获取只读权限安全令牌
	 *
	 * @param resource        每次授权的资源路径（请尽量缩小资源范围），不能以 '/' 开头，例如： image/user/123/*
	 * @param roleSessionName 角色session名称，用于审计
	 * @param expectToUseTime  期望使用的时长，单位：秒。 expectToUseTime + {@link #minTimeToExpir} <= {@link #expir}
	 *
	 * @return {@link com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials}
	 *
	 * @throws com.aliyuncs.exceptions.ClientException
	 */
	public STSCredential getReadOnlyCredential(String resource, String roleSessionName, int expectToUseTime) throws ClientException {
		return getCredential(resource, roleSessionName, expectToUseTime, new String[]{"oss:GetObject"});
	}
	
	/**
	 * 获取只写权限安全令牌
	 *
	 * @param resource        每次授权的资源路径（请尽量缩小资源范围），不能以 '/' 开头，例如： image/user/123/*
	 * @param roleSessionName 角色session名称，用于审计
	 * @param expectToUseTime  期望使用的时长，单位：秒。 expectToUseTime + {@link #minTimeToExpir} <= {@link #expir}
	 *
	 * @return {@link com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials}
	 *
	 * @throws com.aliyuncs.exceptions.ClientException
	 */
	public STSCredential getWriteOnlyCredential(String resource, String roleSessionName, int expectToUseTime) throws ClientException {
		return getCredential(resource, roleSessionName, expectToUseTime, new String[]{"oss:PutObject"});
	}
	
	/**
	 * 获取只读权限安全令牌
	 *
	 * @param resource        每次授权的资源路径（请尽量缩小资源范围），不能以 '/' 开头，例如： image/user/123/*
	 * @param roleSessionName 角色session名称，用于审计
	 * @param expectToUseTime  期望使用的时长，单位：秒。 expectToUseTime + {@link #minTimeToExpir} <= {@link #expir}
	 * @param actions   授权的操作，参考阿里云文档
	 *
	 * @return {@link com.aliyuncs.sts.model.v20150401.AssumeRoleResponse.Credentials}
	 *
	 * @throws com.aliyuncs.exceptions.ClientException
	 */
	private STSCredential getCredential(String resource, String roleSessionName, int expectToUseTime, String[] actions) throws ClientException {
		if(resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		StringBuilder builder = new StringBuilder();
		for(String action : actions){
			builder.append(action);
		}
		builder.append(resource);
		builder.append(roleSessionName);
		
		String cacheKey = DigestUtils.sha1Hex(builder.toString());
		
		if (credentialsCache != null) {
			STSCredential credentials = (STSCredential) credentialsCache.get(cacheKey);
			if (credentials != null) {
				// 检查缓存里的 token 的有效期是否能能满足使用要求
				if(DateUtils.addSeconds(new Date(), minTimeToExpir + expectToUseTime).before(credentials.getExpirationDate())){
					return credentials;
				}
			}
		}
		
		String policy = buildPolicy(actions, new String[]{String.format("acs:oss:*:*:%s/%s", bucket, resource)});
		
		// 获取安全令牌
		AssumeRoleResponse assumeRoleResponse = STSUtil
			.assumeRole(accessKeyID, accessKeySecret, region, roleArn, roleSessionName, policy, expir);
		if (assumeRoleResponse != null) {
			STSCredential credential = new STSCredential(assumeRoleResponse.getCredentials().getAccessKeyId(),
														 assumeRoleResponse.getCredentials().getAccessKeySecret(),
														 assumeRoleResponse.getCredentials().getSecurityToken(),
														 assumeRoleResponse.getCredentials().getExpiration());
			if (credentialsCache != null) {
				credentialsCache.set(cacheKey, credential);
			}
			return credential;
		}
		return null;
	}
	
	/**
	 * 对原始oss访问链接签名，生成携带授权签名的访问链接
	 *
	 * @param srcUrl          原始oss访问链接，格式： http://{bucket}.oss-{region}.aliyuncs.com/image/user/test.jpg
	 * @param resource        每次授权的资源路径，不能以 '/' 开头，例如： image/user/123/*
	 * @param roleSessionName 角色session名称，用于审计
	 * @param useTime         生成的链接使用时长，单位：秒
	 *
	 * @return 携带授权签名的访问链接，链接的真实有效时长为： useTime + {@link #minTimeToExpir}
	 */
	public String generateReadOnlyURL(String srcUrl, String resource, String roleSessionName, int useTime) {
		if(StringUtils.isEmpty(srcUrl)){
			return null;
		}
		if (!srcUrl.startsWith(ossDomain)) {
			logger.warn("srcUrl[{}] 和 ossDomain[{}] 的域名不一致", srcUrl, ossDomain);
			return srcUrl;
		}
		if(resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		String ossKey = getOssKey(srcUrl);

		try {
			STSCredential credential = getReadOnlyCredential(resource, roleSessionName, useTime);
			if (credential != null) {
				// 使用安全令牌生成签名后的oss访问链接
				String generatedUrl = OSSUtil
					.generateUrl(endPoint, credential.getAccessKeyId(), credential.getAccessKeySecret(),
								 credential.getSecurityToken(), bucket, ossKey, useTime + minTimeToExpir);
				if (logger.isDebugEnabled()) {
					logger.debug("srcUrl={}", srcUrl);
					logger.debug("generatedUrl={}", generatedUrl);
				}
				return generatedUrl;
			} else {
				return srcUrl;
			}
		} catch (ClientException e) {
			logger.error(e.getMessage(), e);
		}
		return srcUrl;
	}
	
	/**
	 * 对原始oss访问链接签名，生成携带授权签名的访问链接
	 *
	 * @param srcUrl          原始oss访问链接，格式： http://{bucket}.oss-{region}.aliyuncs.com/image/user/test.jpg
	 * @param resource        每次授权的资源路径，不能以 '/' 开头，例如： image/user/123/*
	 * @param roleSessionName 角色session名称，用于审计
	 *
	 * @return 携带授权签名的访问链接
	 */
	protected String generateReadOnlyURL(String srcUrl, String resource, String roleSessionName) {
		return generateReadOnlyURL(srcUrl, resource, roleSessionName, defaultUseTime);
	}
	
	/**
	 * 上传文件到oss
	 *
	 * @param path            oss 文件路径，例如： "image/test.jpg"， 不需要 '/' 开头
	 * @param inputStream     输入流
	 *
	 * @return 图片在oss上的访问路径
	 */
	public String uploadFile(String path, InputStream inputStream) throws Exception {
		try {
			if(path.startsWith("/")) {
				path = path.substring(1);
			}
			STSCredential credential = getWriteOnlyCredential("", bucket + "_default", defaultUseTime);
			OSSUtil.uploadFile(endPoint, credential.getAccessKeyId(), credential.getAccessKeySecret(), credential.getSecurityToken(), bucket, path, inputStream);
			return String.format("http://%s.oss-%s.aliyuncs.com/%s", getBucket(), getRegion(), path);
		} catch (ClientException e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 判断指定文件是否存在
	 *
	 * @param path oss 文件路径，例如： "image/test.jpg"， 不需要 '/' 开头
	 *
	 * @return 文件是否存在
	 *
	 * @throws Exception
	 */
	public boolean isExist(String path) throws Exception {
		try {
			STSCredential credential = getReadOnlyCredential("", bucket + "_default", defaultUseTime);
			return OSSUtil.isExist(endPoint, credential.getAccessKeyId(), credential.getAccessKeySecret(), credential.getSecurityToken(), bucket, path);
		} catch (ClientException e) {
			throw new Exception(e);
		}
	}
	
	/**
	 * 根据完整的oss文件链接，得到对应的ossKey，返回结果不能以 '/' 开头
	 *
	 * @param srcUrl 完整的oss文件链接
	 *
	 * @return ossKey
	 */
	private String getOssKey(String srcUrl) {
		String temp = srcUrl.substring(ossDomain.length());
		if (temp.startsWith("/")) {
			return temp.substring(1);
		}
		return temp;
	}
	
	/**
	 * 构建授权策略字符串
	 *
	 * @param actions   授权的操作，参考阿里云文档
	 * @param resources 授权的OSS资源，格式参考阿里云文档
	 *
	 * @return 授权策略字符串，格式参考 <a href="https://help.aliyun.com/document_detail/28664.html?spm=5176.doc28665.6.573.liyZTq">Policy 语法结构</a>
	 */
	private String buildPolicy(String[] actions, String[] resources) {
		
		JSONObject builder = new JSONObject();
		JSONArray statementJson = new JSONArray();
		
		JSONObject allowJson = new JSONObject();
		allowJson.put("Effect", "Allow");
		allowJson.put("Action", actions);
		allowJson.put("Resource", resources);
		
		statementJson.add(allowJson);
		builder.put("Statement", statementJson);
		builder.put("Version", "1");
		return builder.toJSONString();
	}
	
}
