package com.cloudin.commons.langs.support.spring;

import com.cloudin.commons.langs.CookieUtils;
import com.cloudin.commons.langs.ServletUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 小天
 * @version 1.0.0, 2018/3/30 0030 14:11
 */
public class BasicInterceptor implements HandlerInterceptor, InitializingBean {
	
	private Logger logger;
	
	public final static String REMOTE_IP           = "remote.ip";
	public final static String REMOTE_FEATURE_CODE = "remote.feature.code";
	public final static String COOKIE_NAME_REMOTE_FEATURE_CODE = "FID";
	
	private Set<String> accessControlAllowOriginSet;
	private String      accessControlAllowMethods;
	private String      accessControlAllowCredentials;
	private String      accessControlAllowHeaders;
	
	/**
	 * 是否开启特征检查，包括： ip、ua
	 */
	private boolean enableRemoteFeatureCheck = false;
	
	/**
	 * 特征码 哈希盐值
	 */
	private String featureCodeHashSalt;
	
	/**
	 * 设定允许的跨域域名集合
	 *
	 * @param accessControlAllowOrigins 域名集合字符串，多个域名以逗号分隔
	 */
	public void setAccessControlAllowOrigins(String accessControlAllowOrigins) {
		accessControlAllowOriginSet = new HashSet<>();
		if (StringUtils.isNotEmpty(accessControlAllowOrigins)) {
			String[] arr = accessControlAllowOrigins.split(",");
			for (String item : arr) {
				if (StringUtils.isNotEmpty(item)) {
					accessControlAllowOriginSet.add(item);
				}
			}
		}
	}
	
	public void setAccessControlAllowMethods(String accessControlAllowMethods) {
		this.accessControlAllowMethods = accessControlAllowMethods;
	}
	
	public void setAccessControlAllowCredentials(String accessControlAllowCredentials) {
		this.accessControlAllowCredentials = accessControlAllowCredentials;
	}
	
	public void setAccessControlAllowHeaders(String accessControlAllowHeaders) {
		this.accessControlAllowHeaders = accessControlAllowHeaders;
	}
	
	public void setEnableRemoteFeatureCheck(boolean enableRemoteFeatureCheck) {
		this.enableRemoteFeatureCheck = enableRemoteFeatureCheck;
	}
	
	public void setFeatureCodeHashSalt(String featureCodeHashSalt) {
		this.featureCodeHashSalt = featureCodeHashSalt;
	}
	
	/**
	 * 设置日志
	 *
	 * @param loggerName 日志名字
	 */
	public void setLogger(String loggerName) {
		this.logger = LoggerFactory.getLogger(loggerName);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (featureCodeHashSalt == null) {
			featureCodeHashSalt = RandomStringUtils.random(20);
		}
		
		if (accessControlAllowMethods == null) {
			accessControlAllowMethods = "POST, GET, OPTIONS";
		}
		
		if (accessControlAllowCredentials == null) {
			accessControlAllowCredentials = "true";
		}
		
		if (accessControlAllowHeaders == null) {
			accessControlAllowHeaders = "Content-Type, X-Requested-With";
		}
		
		if (logger == null) {
			logger = LoggerFactory.getLogger(getClass());
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		// 获取客户端IP
		
		String ip = ServletUtils.getIP(request);
		
		request.setAttribute(REMOTE_IP, ip);
		
		if (enableRemoteFeatureCheck) {
			// 处理客户端特征码
			handleClientFeature(request, response, ip);
		}
		
		// 跨域处理
		if (ServletUtils.isCrossOriginIgnoreHttps(request)) {
			String origin = ServletUtils.getOrigin(request);
			if (CollectionUtils.isNotEmpty(accessControlAllowOriginSet) && accessControlAllowOriginSet
				.contains(origin)) {
				logger.info("支持的跨域请求 | isCrossOrigin=true,origin={}", origin);
				response.setHeader("Access-Control-Allow-Origin", origin);
				response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
				response.setHeader("Access-Control-Allow-Credentials", accessControlAllowCredentials);
				response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
			} else {
				logger.warn("不支持的跨域请求 | isCrossOrigin=true,origin={}", origin);
				return false;
			}
		} else {
			logger.debug("isCrossOrigin=false");
		}
		
		// 记录访问日志
		logAccess(request, ip);
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception {
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
		throws Exception {
		logger.debug("completion");
	}
	
	/**
	 * 记录请求细节到日志中
	 *
	 * @param request {@link javax.servlet.http.HttpServletRequest}
	 */
	private void logAccess(HttpServletRequest request, String ip) {
		String url = request.getRequestURL().toString();
		String queryStr = request.getQueryString();
		if (StringUtils.isNotEmpty(queryStr)) {
			url = url + "?" + queryStr;
		}
		String refer = request.getHeader("referer");
		String ua = request.getHeader("user-agent");
		
		logger.info("FrontInterceptor |ip=[{}],url=[{}],refer=[{}],ua=[{}]", ip, url, refer, ua);
	}
	
	private void handleClientFeature(HttpServletRequest request, HttpServletResponse response, String ip) {
		String featureCode = CookieUtils.getValue(request, COOKIE_NAME_REMOTE_FEATURE_CODE);
		if (StringUtils.isEmpty(featureCode)) {
			featureCode = generateFeatureCode(request, ip);
			CookieUtils.set(response, COOKIE_NAME_REMOTE_FEATURE_CODE, featureCode, -1, true);
		} else {
			String expectedFeatureCode = generateFeatureCode(request, ip);
			if (!expectedFeatureCode.equals(featureCode)) {
				featureCode = expectedFeatureCode;
				logger.warn("客户端已经发生改变，重新生成了特征码|featureCode={}", expectedFeatureCode);
				
				// 如果特征码发生了改变，就清除所有cookie
				CookieUtils.clear(request, response);
				CookieUtils.set(response, COOKIE_NAME_REMOTE_FEATURE_CODE, featureCode, -1, true);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("featureCode={}", featureCode);
		}
	}
	
	/**
	 * 生成特征码
	 *
	 * @param request {@link javax.servlet.http.HttpServletRequest}
	 *
	 * @return 特征码
	 */
	private String generateFeatureCode(HttpServletRequest request, String ip) {
		String ua = request.getHeader("user-agent");
		return Base64.encodeBase64String(DigestUtils.sha256(ip + featureCodeHashSalt + ua));
	}
}
