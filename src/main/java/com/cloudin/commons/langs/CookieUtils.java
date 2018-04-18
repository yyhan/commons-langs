package com.cloudin.commons.langs;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie 工具类
 *
 * @author 小天
 * @version 1.0.0, 2017/11/28 0028 19:28
 */
public class CookieUtils {
	
	/**
	 * 设置cookie
	 *
	 * @param response {@link javax.servlet.http.HttpServletResponse}
	 * @param key      cookie 名称
	 * @param value    cookie 值
	 */
	public static void set(HttpServletResponse response, String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
	}
	
	/**
	 * 设置cookie
	 *
	 * @param response   {@link javax.servlet.http.HttpServletResponse}
	 * @param key        cookie 名称
	 * @param value      cookie 值
	 * @param maxAge     有效期，单位：秒。负数：存活到浏览器关闭；0：删除cookie
	 * @param isHttpOnly 是否为HTTPOnly模式，该模式下，客户端脚本不能修改该cookie
	 */
	public static void set(HttpServletResponse response, String key, String value, int maxAge, boolean isHttpOnly) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(isHttpOnly);
		response.addCookie(cookie);
	}
	
	/**
	 * 设置cookie
	 *
	 * @param response   {@link javax.servlet.http.HttpServletResponse}
	 * @param key        cookie 名称
	 * @param value      cookie 值
	 * @param maxAge     有效期，单位：秒。负数：存活到浏览器关闭；0：删除cookie
	 * @param isHttpOnly 是否为HTTPOnly模式，该模式下，客户端脚本不能修改该cookie
	 * @param domain     域名
	 * @param path       路径
	 */
	public static void set(HttpServletResponse response, String key, String value, int maxAge, boolean isHttpOnly,
		String domain, String path) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(isHttpOnly);
		
		if (path != null) {
			cookie.setPath(path);
		}
		if (domain != null) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}
	
	public static void delete(HttpServletResponse response, String key) {
		set(response, key, null, 0, true, null, null);
	}
	
	/**
	 * 清除cookie
	 *
	 * @param request
	 * @param response
	 */
	public static void clear(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}
	
	public static String getValue(HttpServletRequest request, String key) {
		Cookie cookie = get(request, key);
		if (cookie != null) {
			return cookie.getValue();
		}
		return null;
	}
	
	public static Cookie get(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (StringUtils.equalsIgnoreCase(cookie.getName(), key)) {
				return cookie;
			}
		}
		return null;
	}
	
}
