package com.cloudin.commons.langs.support.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

/**
 * 阿里云STS (Security Token Service) 工具类
 *
 * @see <a href="https://help.aliyun.com/document_detail/28756.html?spm=5176.doc28786.2.2.wcP5rN">阿里云STS</a>
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class STSUtil {
	
	public static final String STS_API_VERSION = "2015-04-01";
	
	/**
	 * 调用阿里云STS (Security Token Service) 获取临时访问凭证。 凭证有效期： 1800秒
	 * @see {@link #assumeRole(String, String, String, String, String, String, long) }
	 */
	public static AssumeRoleResponse assumeRole(String accessKeyID, String accessKeySecret, String region,
		String roleArn, String roleSessionName, String policy) throws ClientException {
		return assumeRole(accessKeyID, accessKeySecret, region, roleArn, roleSessionName, policy, 30 * 60);
	}
	
	/**
	 * 调用阿里云STS (Security Token Service) 获取临时访问凭证
	 *
	 * @param accessKeyID     访问ID
	 * @param accessKeySecret 访问密钥
	 * @param region          区域
	 * @param roleArn         RoleArn 需要在 RAM 控制台上获取
	 * @param roleSessionName RoleSessionName 调用方自行指定临时Token的会话名称，用于标识你的用户，主要用于审计，或者用于区分Token颁发给谁
	 * @param policy          权限策略
	 * @param durationSeconds 临时访问凭证的有效时间，单位：秒
	 *
	 * @return com.aliyuncs.sts.model.v20150401.AssumeRoleResponse
	 *
	 * @throws com.aliyuncs.exceptions.ClientException
	 */
	public static AssumeRoleResponse assumeRole(String accessKeyID, String accessKeySecret, String region,
		String roleArn, String roleSessionName, String policy, long durationSeconds) throws ClientException {
		IClientProfile profile = DefaultProfile.getProfile(region, accessKeyID, accessKeySecret);
		DefaultAcsClient client = new DefaultAcsClient(profile);
		final AssumeRoleRequest request = new AssumeRoleRequest();
		request.setVersion(STS_API_VERSION);
		request.setMethod(MethodType.POST);
		request.setProtocol(ProtocolType.HTTPS);
		request.setRoleArn(roleArn);
		request.setRoleSessionName(roleSessionName);
		request.setPolicy(policy);
		request.setDurationSeconds(durationSeconds);
		// 发起请求，并得到response
		AssumeRoleResponse response = client.getAcsResponse(request);
		return response;
	}
}
