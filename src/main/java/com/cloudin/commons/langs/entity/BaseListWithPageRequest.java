package com.cloudin.commons.langs.entity;

import java.io.Serializable;

/**
 * @author 小天
 * @version 1.0.0, 2017/11/28 0028 15:38
 */
public class BaseListWithPageRequest implements Serializable {
	
	/**
	 * 每页容量，默认10
	 */
	private Integer length = 10;
	/**
	 * 起始编号，从0开始，默认0（例：第0页，start = 0；第9页，每页10条的话，start = 90，length = 10）
	 * 例如：
	 * <uL>
	 *     <li>第0页，start = 0</li>
	 *     <li>第9页，每页10条的话，start = 90，length = 10</li>
	 * </uL>
	 */
	private Integer start  = 0;
	/**
	 * 排序字段
	 */
	private String orderField;
	/**
	 * 升序或降序标识：asc 或 desc
	 * <uL>
	 *     <li>升序：asc</li>
	 *     <li>降序：desc</li>
	 * </uL>
	 */
	private String orderDir;
	
	public Integer getLength() {
		return length;
	}
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	public Integer getStart() {
		return start;
	}
	
	public void setStart(Integer start) {
		this.start = start;
	}
	
	public String getOrderField() {
		return orderField;
	}
	
	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
	
	public String getOrderDir() {
		return orderDir;
	}
	
	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	}
}
