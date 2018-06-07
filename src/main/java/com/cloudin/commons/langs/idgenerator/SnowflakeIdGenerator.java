package com.cloudin.commons.langs.idgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Twitter 雪花算法的 Id 生成器
 * <p>
 * SnowFlake 的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在 1024 个节点，包括 5 位 dataCenterId 和 5 位 workerId <br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * <p>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 *
 * @author 小天
 * @version 1.0.0, 2018/6/5 0005 10:41
 */
public class SnowflakeIdGenerator {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 开始时间截（基准时间戳） (2018-01-01，固定值首次开始使用后，不应该再修改)
	 */
	private final long baseTimeStamp = 1514736000000L;
	
	/**
	 * 时间截向左移22位（固定值）
	 */
	private final static long TIMESTAMP_LEFT_SHIFT = 22;
	
	/**
	 * 机器 id 所占的位数
	 */
	private long workerIdBits;
	
	/**
	 * 数据中心标识 id 所占的位数
	 */
	private long dataCenterIdBits;
	
	/**
	 * 序列在 id 中占的位数
	 */
	private long sequenceBits;
	
	/**
	 * 支持的最大机器id，结果是31
	 */
	private long maxWorkerId;
	
	/**
	 * 支持的最大数据标识id，结果是31
	 */
	private long maxDataCenterId;
	
	/**
	 * 序列的掩码
	 */
	private long sequenceMask;
	
	/**
	 * 机器ID(0~31)
	 */
	private long workerId;
	/**
	 * 机器ID掩码
	 */
	private long workerIdMask;
	
	/**
	 * 数据中心ID(0~31)
	 */
	private long dataCenterId;
	/**
	 * 数据中心ID掩码
	 */
	private long dataCenterIdMask;
	
	/**
	 * 毫秒内序列(0~4095)
	 */
	private long sequence = 0L;
	
	/**
	 * 上次生成ID的时间截
	 */
	private long lastTimeStamp = -1L;
	
	/**
	 * 构造函数。5位数据中心Id，5位机器Id
	 *
	 * @param dataCenterId 数据中心ID (0~31)
	 * @param workerId     工作ID (0~31)
	 */
	public SnowflakeIdGenerator(long dataCenterId, long workerId) {
		this(dataCenterId, 5L, workerId, 5L);
	}
	
	/**
	 * 构造函数
	 *
	 * @param dataCenterId     数据中心ID
	 * @param dataCenterIdBits 数据中心ID位数
	 * @param workerId         机器ID
	 * @param workerIdBits     机器ID位数
	 */
	public SnowflakeIdGenerator(long dataCenterId, long dataCenterIdBits, long workerId, long workerIdBits) {
		if ((workerIdBits + dataCenterIdBits) >= TIMESTAMP_LEFT_SHIFT) {
			throw new IllegalArgumentException(String
				.format("dataCenterIdBits + workerIdBits can't be greater than or equal to %d", TIMESTAMP_LEFT_SHIFT));
		}
		this.dataCenterId = dataCenterId;
		this.dataCenterIdBits = dataCenterIdBits;
		this.workerId = workerId;
		this.workerIdBits = workerIdBits;
		
		this.maxDataCenterId = ~(-1L << dataCenterIdBits);
		this.maxWorkerId = ~(-1L << workerIdBits);
		
		if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
			throw new IllegalArgumentException(
				String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
		}
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
				String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
		}
		
		this.sequenceBits = TIMESTAMP_LEFT_SHIFT - dataCenterIdBits - workerIdBits;
		
		this.dataCenterIdMask = dataCenterId << (sequenceBits + workerIdBits);
		this.workerIdMask = workerId << (sequenceBits);
		
		this.sequenceMask = ~(-1L << sequenceBits);
		
		logger.info("dataCenterId={},workerId={},dataCenterIdBits={},workerIdBits={}", dataCenterId, workerId,
			dataCenterIdBits, workerIdBits);
	}
	
	public long getMaxWorkerId() {
		return maxWorkerId;
	}
	
	public long getMaxDataCenterId() {
		return maxDataCenterId;
	}
	
	public long getWorkerId() {
		return workerId;
	}
	
	public long getDataCenterId() {
		return dataCenterId;
	}
	
	public long getBaseTimeStamp() {
		return baseTimeStamp;
	}
	
	public long getWorkerIdBits() {
		return workerIdBits;
	}
	
	public long getDataCenterIdBits() {
		return dataCenterIdBits;
	}
	
	public long getSequenceBits() {
		return sequenceBits;
	}
	
	public long getLastTimeStamp() {
		return lastTimeStamp;
	}
	
	/**
	 * 获得下一个ID (该方法是线程安全的)
	 *
	 * @return SnowflakeId
	 */
	public synchronized long nextId() {
		long timestamp = getTime();
		
		//如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
		if (timestamp < lastTimeStamp) {
			throw new RuntimeException(String
				.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
					lastTimeStamp - timestamp));
		}
		
		//如果是同一时间生成的，则进行毫秒内序列
		if (lastTimeStamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			//毫秒内序列溢出
			if (sequence == 0) {
				//阻塞到下一个毫秒,获得新的时间戳
				timestamp = getTime();
				while (timestamp <= lastTimeStamp) {
					timestamp = getTime();
				}
			}
		} else {
			//时间戳改变，毫秒内序列重置
			sequence = 0L;
		}
		
		//上次生成ID的时间截
		lastTimeStamp = timestamp;
		
		//移位并通过或运算拼到一起组成64位的ID
		return ((timestamp - baseTimeStamp) << TIMESTAMP_LEFT_SHIFT) | dataCenterIdMask | workerIdMask | sequence;
	}
	
	/**
	 * 返回以毫秒为单位的当前时间
	 *
	 * @return 当前时间(毫秒)
	 */
	private long getTime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 从雪花算法生成的 Id 中提取生成时间戳
	 *
	 * @param id            雪花算法生成的Id
	 * @param baseTimeStamp 雪花算法开始时间截（基准时间戳）
	 *
	 * @return 生成时间戳（单位：毫秒）
	 */
	public static long parseTimeStamp(long id, long baseTimeStamp) {
		return (id >> TIMESTAMP_LEFT_SHIFT) + baseTimeStamp;
	}
	
	/**
	 * 从雪花算法生成的 Id 中提取数据中心Id
	 *
	 * @param id               雪花算法生成的Id
	 * @param dataCenterIdBits 数据中心Id位数
	 *
	 * @return 数据中心Id
	 */
	public static long parseDataCenterId(long id, long dataCenterIdBits) {
		return id >> (TIMESTAMP_LEFT_SHIFT - dataCenterIdBits) & (~(-1 << dataCenterIdBits));
	}
	
	/**
	 * 从雪花算法生成的 Id 中提取机器Id
	 *
	 * @param id               雪花算法生成的Id
	 * @param dataCenterIdBits 数据中心Id位数
	 * @param workerIdBits     机器Id位数
	 *
	 * @return 机器Id
	 */
	public static long parseWorkId(long id, long dataCenterIdBits, long workerIdBits) {
		return id >> (TIMESTAMP_LEFT_SHIFT - dataCenterIdBits - workerIdBits) & (~(-1 << workerIdBits));
	}
}
