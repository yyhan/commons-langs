package com.cloudin.commons.langs;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 小天
 * @version 1.0.0, 2017/11/27 0027 16:34
 */
public class RegexUtil {
	
	public static boolean isEmail(String plainTxt){
		if(StringUtils.isNotEmpty(plainTxt)){
			return plainTxt.matches("[0-9a-zA-Z\\-]+(\\.[0-9a-zA-Z\\-]+)?@[0-9a-zA-Z\\-]+(\\.[0-9a-zA-Z\\-]+)+$");
		}
		return false;
	}
	
}
