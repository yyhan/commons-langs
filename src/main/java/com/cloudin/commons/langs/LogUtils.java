package com.cloudin.commons.langs;

import org.slf4j.helpers.MessageFormatter;

/**
 * 日志消息
 *
 * @author 小天
 * @version 1.0.0, 2018/1/23 0023 20:52
 */
public class LogUtils {
	
	public static String format(String pattern, Object... args){
		return MessageFormatter.arrayFormat(pattern, args).getMessage();
	}
	
}
