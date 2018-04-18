package com.cloudin.commons.langs.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * @author 小天
 * @version 1.0.0, 2018/1/3 0003 10:21
 */
public class DefaultThreadFactory implements ThreadFactory {
	
	private String prefix = "default-pool-thread-";
	private AtomicInteger threadIndex;
	
	public DefaultThreadFactory() {
		this.threadIndex = new AtomicInteger(1);
	}
	
	/**
	 * @param prefix 线程名称前缀，线程名字由 前缀 + 数字编号 组成
	 */
	public DefaultThreadFactory(String prefix) {
		this.prefix = prefix;
		this.threadIndex = new AtomicInteger(1);
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(Thread.currentThread().getThreadGroup(), r, newThreadName());
		thread.setDaemon(false);
		return thread;
	}
	
	private String newThreadName() {
		return prefix + threadIndex.getAndIncrement();
	}
}
