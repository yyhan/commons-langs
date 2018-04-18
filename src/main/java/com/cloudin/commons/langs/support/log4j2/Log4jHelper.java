package com.cloudin.commons.langs.support.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.Map;

/**
 * log4j2工具类，支持 2.4+版本
 * @author 小天
 * @version 1.0.0, 2017/11/7 0007 14:42
 */
public class Log4jHelper {
	private static LoggerContext loggerContext = null;
	
	public static final String DEFAULT_APPENDER = "STDOUT";
	
	public static void resetSimpleRootLogger(String rootLoggerLevel) {
		resetSimpleRootLogger(rootLoggerLevel, true);
	}
	
	/**
	 * 重置一个简化的rootLogger，将所有日志输出的控制台。当无法确定 log4j2 是否已经加载时，可用此方法。
	 *
	 * @param rootLoggerLevel     rootLogger 日志级别
	 * @param isRemoveOldAppender 是否移除现有的appender
	 */
	public static void resetSimpleRootLogger(String rootLoggerLevel, boolean isRemoveOldAppender) {
		loggerContext = (LoggerContext) LogManager.getContext(false);
		Configuration config = loggerContext.getConfiguration();
		
		ConsoleAppender.Builder builder = ConsoleAppender.newBuilder();
		builder.withName(DEFAULT_APPENDER).withLayout(PatternLayout.newBuilder().withPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger - %msg %ex %n").build());
		Appender stdoutAppender = builder.setTarget(ConsoleAppender.Target.SYSTEM_OUT).build();
		
		config.addAppender(stdoutAppender);
		
		LoggerConfig rootLoggerConfig = config.getRootLogger();
		if (isRemoveOldAppender) {
			Map<String, Appender> appenderMap = rootLoggerConfig.getAppenders();
			if (!appenderMap.isEmpty()) {
				for (String key : appenderMap.keySet()) {
					rootLoggerConfig.removeAppender(key);
				}
			}
		}
		rootLoggerConfig.setLevel(Level.valueOf(rootLoggerLevel));
		rootLoggerConfig.addAppender(stdoutAppender, Level.DEBUG, null);
		
		loggerContext.updateLoggers();
	}
	
	/**
	 * 初始化Log4j2 ，需要确保该方法在所有日志操作前执行
	 *
	 * @param rootLoggerLevel 顶级logger 日志级别
	 */
	public static void init(String rootLoggerLevel) {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setStatusLevel(Level.WARN);
		builder.setConfigurationName("Log4jHelper");
		builder.add(
			builder.newAppender(DEFAULT_APPENDER, "CONSOLE")
				.addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
				.add(
					builder.newLayout("PatternLayout")
						.addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg %ex %n")
				));
		builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG)
						.add(builder.newAppenderRef(DEFAULT_APPENDER)));
		
		builder.add(builder.newRootLogger(Level.DEBUG).add(builder.newAppenderRef("STDOUT")));
		loggerContext = Configurator.initialize(builder.build());
	}
	
	public static void init() {
		init("DEBUG");
	}
	
	/**
	 * 向当前 log4j2 上下文中添加一个logger，该logger会被输出到控制台。
	 *
	 * @param name  logger 名称
	 * @param level 日志级别
	 */
	public static void addLogger(String name, String level) {
		addLogger(name, level, DEFAULT_APPENDER);
	}
	
	/**
	 * 向当前 log4j2 上下文中添加一个logger
	 *
	 * @param name         logger 名称
	 * @param level        日志级别
	 * @param appenderName appenderName
	 */
	public static void addLogger(String name, String level, String appenderName) {
		final Configuration config = loggerContext.getConfiguration();
		
		Appender stdoutAppender = config.getAppender(appenderName);
		
		LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.DEBUG, name, "false", new AppenderRef[]{}, new Property[]{}, config, null);
		loggerConfig.addAppender(stdoutAppender, Level.valueOf(level), null);
		loggerConfig.setLevel(Level.valueOf(level));
		loggerConfig.setAdditive(false);
		
		config.addLogger(name, loggerConfig);
		
		loggerContext.updateLoggers();
	}
	
	/**
	 * 设置root日志级别
	 *
	 * @param level
	 */
	public static void setRootLevel(String level) {
		Configuration config = loggerContext.getConfiguration();
		LoggerConfig loggerConfig = config.getRootLogger();
		loggerConfig.setLevel(Level.valueOf(level));
		loggerContext.updateLoggers();
	}
	
	/**
	 * 设置日志级别
	 *
	 * @param loggerName
	 * @param level
	 */
	public static void setLevel(String loggerName, String level) {
		Configuration config = loggerContext.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
		loggerConfig.setLevel(Level.valueOf(level));
		loggerContext.updateLoggers();
	}
	
	public static void main(String[] args) {
		
		//        Log4jHelper.resetSimpleRootLogger("debug");
		Log4jHelper.init();
		
		//        Logger httpLogger = LogManager.getLogger("org.apache.http");
		//        httpLogger.debug("httpLogger: debug");
		//        httpLogger.info("httpLogger: info");
		//        httpLogger.error("httpLogger: error");
		//
		Log4jHelper.addLogger("accessLog", "info");
		Logger accessLog = LogManager.getLogger("accessLog");
		accessLog.debug("accessLog: debug");
		accessLog.info("accessLog: info");
		accessLog.error("accessLog: error");
		
		Logger rootLogger = LogManager.getRootLogger();
		
		rootLogger.debug("rootLogger: debug");
		rootLogger.info("rootLogger: info");
		rootLogger.error("rootLogger: error");
		
	}
}
