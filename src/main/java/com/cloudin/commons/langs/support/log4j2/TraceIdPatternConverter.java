package com.cloudin.commons.langs.support.log4j2;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

/**
 * 追踪Id模式转换器。
 *
 * 对于 version 2.3+， 当用于AsyncAppender时，需要指定 {@link org.apache.logging.log4j.core.util.Constants#FORMAT_MESSAGES_IN_BACKGROUND} 为false。
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
@Plugin(name = "TraceIdPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"traceId"})
public class TraceIdPatternConverter extends LogEventPatternConverter {
	
	private static Logger logger = LogManager.getLogger(TraceIdPatternConverter.class);
	
	private static ThreadLocal<String> traceIdLocal = new ThreadLocal<String>();
	
	public TraceIdPatternConverter() {
		super("traceId", "traceId");
	}
	
	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) {
		String traceId = getTraceId(true);
		toAppendTo.append(traceId);
	}
	
	/**
	 * Obtains an instance of SequencePatternConverter.
	 *
	 * @param options options, currently ignored, may be null.
	 *
	 * @return instance of SequencePatternConverter.
	 */
	public static TraceIdPatternConverter newInstance(final String[] options) {
		return new TraceIdPatternConverter();
	}
	
	/**
	 * 生成追踪Id
	 *
	 * @return
	 */
	public static String newTraceId(){
		return String.format("%d_%d_%s", System.currentTimeMillis(), Thread.currentThread().getId(), RandomStringUtils.random(5, true, true));
	}
	
	/**
	 * 获取当前追踪Id
	 *
	 * @param isCreate 如果当前未设置追踪Id，是否新创建一个
	 * @return
	 */
	public static String getTraceId(boolean isCreate){
		String oldTraceId = traceIdLocal.get();
		if(oldTraceId == null){
			if(isCreate){
				oldTraceId = newTraceId();
				traceIdLocal.set(oldTraceId);
			}
		}
		return oldTraceId;
	}
	
	/**
	 * 切换traceId
	 */
	public static void switchTraceId(){
		switchTraceId(newTraceId());
	}
	
	/**
	 * 切换traceId。切换和重置的区别：切换时记录切换前后的追踪Id
	 *
	 * @param traceId，新追踪Id
	 */
	public static void switchTraceId(final String traceId){
		String oldTraceId = getTraceId(false);
		logger.info("reset traceId : {} -> {}", oldTraceId, traceId);
		reset(traceId);
	}
	
	/**
	 * 重置追踪Id，默认值：{date:yyyyMMddHHmmssSSS} + ThreadId
	 *
	 * @see TraceIdPatternConverter#reset
	 */
	public static void reset() {
		String oldTraceId = getTraceId(false);
		String newTraceId = newTraceId();
		if(newTraceId.equalsIgnoreCase(oldTraceId)){
			newTraceId += "0";
		}
		reset(newTraceId);
	}
	
	/**
	 * 重置追踪Id.<br/>
	 * 当切换追踪上下文时，调用该方法重置追踪Id
	 *
	 * @param traceId，追踪Id
	 */
	public static void reset(final String traceId) {
		if (traceId != null) {
			traceIdLocal.set(traceId);
		}
	}
	
	/**
	 * 清除追踪Id。一般当线程结束时，需要调用该方法清除已生成的追踪Id
	 */
	public static void clear() {
		String oldTraceId = getTraceId(false);
		if(oldTraceId != null){
			logger.info("remove traceId : {}", oldTraceId);
			traceIdLocal.remove();
		}
	}
	
}