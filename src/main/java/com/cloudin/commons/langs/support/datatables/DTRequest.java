package com.cloudin.commons.langs.support.datatables;

import java.io.Serializable;

/**
 * jquery DataTables 插件请求实体
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class DTRequest implements Serializable {
	
	/**
	 * 当前查询标识 datatables使用，必传但是不需要修改
	 */
	private Integer draw   = 0;
	/**
	 * 每页容量
	 */
	private Integer length = 10;
	/**
	 * 起始编号（例：第9页，每页10条的话，start = 90，length = 10）
	 */
	private Integer start  = 0;
	/**
	 * 排序字段
	 */
	private String orderField;
	/**
	 * 升序或降序标识
	 */
	private String orderDir;
	
	public Integer getDraw() {
		return draw;
	}
	
	public void setDraw(Integer draw) {
		this.draw = draw;
	}
	
	public Integer getLength() {
		return length;
	}
	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	/**
	 * 起始记录行号，从0开始
	 *
	 * @return
	 */
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
	
	/**
	 * 返回当前页编号，从0开始
	 *
	 * @return 当前页编号
	 */
	public Integer getPageNo() {
		return start == null || length == null || length == 0 ? 0 : start / length;
	}
}
