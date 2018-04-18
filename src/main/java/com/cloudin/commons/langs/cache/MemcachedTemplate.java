package com.cloudin.commons.langs.cache;

import net.spy.memcached.MemcachedClientIF;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.Future;

/**
 * 缓存模板类
 *
 * @author 小天
 * @version 1.0.0, 2017/11/27 0027 17:47
 */
public class MemcachedTemplate {
	
	private MemcachedClientIF memcachedClient;
	
	/**
	 * 应用APP
	 */
	private String app;
	/**
	 * 缓存过期时间，单位：秒。默认30分钟
	 */
	private int defaultExpire = 30 * 60;
	
	public void setApp(String app) {
		this.app = app;
	}
	
	public void setDefaultExpire(int expir) {
		this.defaultExpire = expir;
	}
	
	public void setMemcachedClient(MemcachedClientIF memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	
	/**
	 * 对明文的key进行转换
	 *
	 * @param key 明文的key
	 *
	 * @return 转换后的key
	 */
	private String convertKey(String key) {
		return DigestUtils.md5Hex(app + key);
	}
	
	/**
	 * 获取指定key的值
	 *
	 * @param key 缓存主键
	 *
	 * @return 值
	 */
	public Object get(String key) {
		return memcachedClient.get(convertKey(key));
	}
	
	/**
	 * 缓存KEY 和 value
	 *
	 * @param key   缓存的key
	 * @param value 缓存的值
	 *
	 * @return 缓存结果
	 */
	public Future<Boolean> set(String key, Object value) {
		return memcachedClient.set(convertKey(key), defaultExpire, value);
	}
	
	/**
	 * 缓存KEY 和 value
	 *
	 * @param key    缓存的key
	 * @param value  缓存的值
	 * @param expire 过期时间，单位：秒
	 *
	 * @return 缓存结果
	 */
	public Future<Boolean> set(String key, Object value, int expire) {
		return memcachedClient.set(convertKey(key), expire, value);
	}
	
	/**
	 * 重置指定key的过期时间
	 *
	 * @param key    缓存的key
	 * @param expire 过期时间，单位：秒
	 *
	 * @return 重置结果
	 */
	public Future<Boolean> touch(String key, int expire) {
		return memcachedClient.touch(convertKey(key), expire);
	}
	
	/**
	 * 删除指定的key
	 *
	 * @param key key
	 *
	 * @return 缓存是否成功的Future对象
	 */
	public Future<Boolean> delete(String key) {
		return memcachedClient.delete(convertKey(key));
	}
	
	/**
	 * 添加缓存
	 *
	 * @param key   主键
	 * @param value 值
	 * @param expir 过期时间，单位：秒
	 *
	 * @return 缓存是否成功的Future对象
	 */
	public Future<Boolean> add(String key, Object value, int expir) {
		return memcachedClient.add(convertKey(key), expir, value);
	}
	
	/**
	 * 添加缓存
	 *
	 * @param key   主键
	 * @param value 值
	 *
	 * @return 缓存是否成功的Future对象
	 */
	public Future<Boolean> add(String key, Object value) {
		return memcachedClient.add(convertKey(key), defaultExpire, value);
	}
	
}
