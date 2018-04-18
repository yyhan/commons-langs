package com.cloudin.commons.langs.support.log4j2;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * log4j2 追踪Id，重置和清除拦截器
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class TraceIdFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		TraceIdPatternConverter.reset();
		try {
			chain.doFilter(request, response);
		} finally {
			TraceIdPatternConverter.clear();
		}
	}
	
	@Override
	public void destroy() {
	
	}
}
