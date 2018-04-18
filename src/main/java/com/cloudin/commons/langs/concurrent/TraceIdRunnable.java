package com.cloudin.commons.langs.concurrent;

import com.cloudin.commons.langs.support.log4j2.TraceIdPatternConverter;

/**
 * 支持追踪Id的 Runnable
 *
 * @author 小天
 * @version 1.0.0, 2018/1/22 0022 20:35
 * @see TraceIdPatternConverter
 * @see Runnable
 */
class TraceIdRunnable implements Runnable {
	
	private Runnable target;
	
	TraceIdRunnable(Runnable target) {
		if (target instanceof TraceIdRunnable) {
			this.target = ((TraceIdRunnable) target).getTarget();
		} else {
			this.target = target;
		}
	}
	
	@Override
	public void run() {
		TraceIdPatternConverter.reset();
		try {
			target.run();
		} finally {
			TraceIdPatternConverter.clear();
		}
	}
	
	public Runnable getTarget() {
		return target;
	}
}
