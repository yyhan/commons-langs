package com.cloudin.commons.langs.concurrent;

import com.cloudin.commons.langs.support.log4j2.TraceIdPatternConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支持追踪Id的调度线程。 提供延迟执行 和 循环执行功能
 *
 * @author 小天
 * @version 1.0.0, 2018/1/22 0022 20:29
 */
public class TraceIdScheduleThread extends Thread {
	
	private Logger logger   = LoggerFactory.getLogger(getClass());
	/**
	 * 启动延迟时间，单位：毫秒
	 */
	private long   delay    = 60 * 1000;
	/**
	 * 循环间隔时间，单位：毫秒
	 */
	private long   interval = 60 * 1000;
	
	private boolean ignoreException = false;
	
	private boolean ignoreInterruptedException = false;
	
	/**
	 * 单次间隔时间：
	 * <ul>
	 * <li>正数： 仅在本次间隔 {@link #onceInterval} 毫秒后，开始下次执行</li>
	 * <li>负数： 间隔 {@link #interval} 毫秒后，开始下次执行</li>
	 * <li>0： 仅在本次立即执行</li>
	 * </ul>
	 */
	private int onceInterval = -1;
	
	public TraceIdScheduleThread(Runnable target) {
		super(target);
	}
	
	/**
	 * @param name     线程名称
	 * @param delay    启动延迟时间，单位：毫秒
	 * @param interval 循环间隔时间，单位：毫秒
	 */
	public TraceIdScheduleThread(String name, long delay, long interval) {
		super(name);
		this.delay = delay;
		this.interval = interval;
	}
	
	/**
	 * @param target   {@link java.lang.Runnable}
	 * @param delay    启动延迟时间，单位：毫秒
	 * @param interval 循环间隔时间，单位：毫秒
	 */
	public TraceIdScheduleThread(Runnable target, long delay, long interval) {
		super(target);
		this.delay = delay;
		this.interval = interval;
	}
	
	/**
	 * @param target   {@link java.lang.Runnable}
	 * @param name     线程名称
	 * @param delay    启动延迟时间，单位：毫秒
	 * @param interval 循环间隔时间，单位：毫秒
	 */
	public TraceIdScheduleThread(Runnable target, String name, long delay, long interval) {
		super(target, name);
		this.delay = delay;
		this.interval = interval;
	}
	
	@Override
	public final void run() {
		TraceIdPatternConverter.reset();
		try {
			actualRun();
		} finally {
			TraceIdPatternConverter.clear();
		}
	}
	
	public void exec() {
		super.run();
	}
	
	protected void nextSpin() {
		onceInterval = 0;
	}
	
	protected void nextSpin(int interval) {
		onceInterval = interval;
	}
	
	/**
	 * 实际运行的方法
	 */
	private void actualRun() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			logger.error("{} 在延迟启动时被中断", getName());
			return;
		}
		while (!Thread.interrupted()) {
			TraceIdPatternConverter.reset();
			try {
				
				exec();
				
				if (onceInterval < 0) {
					Thread.sleep(interval);
				} else if (onceInterval > 0) {
					Thread.sleep(onceInterval);
					onceInterval = -1;
				} else {
					onceInterval = -1;
				}
				
			} catch (InterruptedException ignore) {
				if (!ignoreInterruptedException) {
					break;
				}
			} catch (Exception e) {
				logger.error(getName() + " 执行过程出现异常", e);
				if (!ignoreException) {
					break;
				}
			}
		}
		logger.error("{} 在执行中被中断", getName());
	}
}
