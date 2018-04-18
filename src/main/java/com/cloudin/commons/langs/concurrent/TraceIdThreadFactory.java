package com.cloudin.commons.langs.concurrent;

/**
 * 支持追踪Id的线程工厂
 *
 * @author 小天
 * @version 1.0.0, 2018/1/22 0022 20:39
 */
public class TraceIdThreadFactory extends DefaultThreadFactory {
	
	public TraceIdThreadFactory() {
		super();
	}
	
	public TraceIdThreadFactory(String prefix) {
		super(prefix);
	}
	
	@Override
	public Thread newThread(Runnable r) {
		if (r instanceof TraceIdRunnable) {
			return super.newThread(r);
		}
		return super.newThread(new TraceIdRunnable(r));
	}
}
