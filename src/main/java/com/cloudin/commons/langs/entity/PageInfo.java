package com.cloudin.commons.langs.entity;

/**
 * mybatis 分页信息对象
 *
 * @author 小天
 * @version 1.0.0, 2018/1/11 0018 09:57
 */
public class PageInfo {
	
	/**
	 * 需要获取条数
	 */
	private Integer limit;
	
	/**
	 * 偏移量
	 */
	private Integer offset;
	
	public Integer getLimit() {
		return limit;
	}
	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
}
