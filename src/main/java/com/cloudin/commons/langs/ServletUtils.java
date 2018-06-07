package com.cloudin.commons.langs;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 小天
 * @version 1.0.0, 2017/11/27 0027 14:59
 */
public class ServletUtils {
	
	/**
	 * 获取真实的IP地址
	 *
	 * @param request
	 *
	 * @return
	 */
	public static String getIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ipArr = ip.split(",");
			for (String item : ipArr) {
				if (!("unknown".equalsIgnoreCase(item))) {
					ip = item;
					break;
				}
			}
		}
		return StringUtils.trim(ip);
	}
	
	public static String getOrigin(HttpServletRequest request) {
		return request.getHeader("origin");
	}
	
	/**
	 * 根据 'origin' 头判断请求是否跨域
	 *
	 * @param request {@link javax.servlet.http.HttpServletRequest}
	 *
	 * @return 是否跨域
	 */
	public static boolean isCrossOrigin(HttpServletRequest request) {
		// 来源域
		String origin = request.getHeader("origin");
		if (StringUtils.isEmpty(origin) || StringUtils.equalsIgnoreCase(origin, "null")) {
			return false;
		}
		
		String url = request.getRequestURL().toString();
		
		return !url.startsWith(origin);
	}
	
	/**
	 * 根据 'origin' 头判断请求是否跨域。判断时忽略https和http的差异。如果使用了反向代理，实际收到的请求时http的，建议使用该方法
	 *
	 * @param request {@link javax.servlet.http.HttpServletRequest}
	 *
	 * @return 是否跨域
	 */
	public static boolean isCrossOriginIgnoreHttps(HttpServletRequest request) {
		// 来源域
		String origin = request.getHeader("origin");
		if (StringUtils.isEmpty(origin) || StringUtils.equalsIgnoreCase(origin, "null")) {
			return false;
		}
		
		String url = request.getRequestURL().toString();
		
		if (!url.startsWith(origin)) {
			// 如果使用了反向代理，实际收到的请求时http的
			if (origin.startsWith("https")) {
				String temp = StringUtils.replaceFirst(origin, "https", "http");
				return !url.startsWith(temp);
			} else {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否为 ajax 请求
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request) {
		String v = request.getHeader("x-requested-with");
		return v != null && "XMLHttpRequest".equals(v);
	}
}
