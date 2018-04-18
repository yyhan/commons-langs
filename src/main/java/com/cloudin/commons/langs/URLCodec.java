package com.cloudin.commons.langs;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * 提供 <CODE>application/x-www-form-urlencoded</CODE> 标准的字符串编解码工具
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class URLCodec {
	
	public static String encode(String plainText) {
		return encode(plainText, Charsets.UTF_8);
	}
	
	public static String encode(String plainText, Charset charset) {
		try {
			return URLEncoder.encode(plainText, charset.displayName());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return plainText;
	}
	
	
	public static String decode(String plainText) {
		return decode(plainText, Charsets.UTF_8);
	}
	
	public static String decode(String plainText, Charset charset) {
		try {
			return URLDecoder.decode(plainText, charset.displayName());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return plainText;
	}
}
