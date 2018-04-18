package com.cloudin.commons.langs.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * spring 上下文工具类
 *
 * @author 小天
 * @version 1.0.0, 2017/10/18 0018 09:57
 */
public class SpringContextUtil implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
		return (T) applicationContext.getBean(name);
	}
	
	public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException{
		return applicationContext.getBeansOfType(type);
	}
	
}
