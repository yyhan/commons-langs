package com.cloudin.commons.langs.support.aliyun;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

/**
 * 阿里云STS服务临时访问凭证实体
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class STSCredential implements Serializable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String accessKeyId;
	
	private String accessKeySecret;
	
	private String securityToken;
	
	/**
	 * 过期时间
	 */
	private Date expirationDate;
	
	public STSCredential(String accessKeyId, String accessKeySecret, String securityToken, String expiration) {
		this.securityToken = securityToken;
		this.accessKeySecret = accessKeySecret;
		this.accessKeyId = accessKeyId;
		try {
			this.expirationDate = DateUtils.parseDate(expiration, "yyyy-MM-dd'T'HH:mm:ssZZ");
		} catch (ParseException e) {
			logger.error("", e);
		}
	}
	
	public String getSecurityToken() {
		return this.securityToken;
	}
	
	public String getAccessKeySecret() {
		return this.accessKeySecret;
	}
	
	public String getAccessKeyId() {
		return this.accessKeyId;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public static void main(String[] args) {
		try {
			Date date = null;
			date = DateUtils.parseDate("2017-09-15T08:02:57Z", "yyyy-MM-dd'T'HH:mm:ssZZ");
			System.out.println(date);
			
			date = DateUtils.parseDate("2017-09-15T08:02:57+00:00", "yyyy-MM-dd'T'HH:mm:ssZZ");
			System.out.println(date);
			
			STSCredential stsCredential = new STSCredential(null, null, null, "2017-09-20T10:38:56Z");
			System.out.println(stsCredential.getExpirationDate());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
