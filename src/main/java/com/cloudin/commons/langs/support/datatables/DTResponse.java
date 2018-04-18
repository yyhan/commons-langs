package com.cloudin.commons.langs.support.datatables;


import com.cloudin.commons.langs.entity.IBaseResult;

import java.util.List;

/**
 * jquery DataTables 插件响应实体
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class DTResponse<T> implements IBaseResult {
	
	private List<T> data;
	private int     draw;
	private int     recordsTotal;
	private int     recordsFiltered;
	
	private Integer code;
	private String  message;
	
	public DTResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public DTResponse(List<T> data, int draw, int recordsTotal) {
		this.data = data;
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsTotal;
		this.code = 0;
	}
	
	public DTResponse(List<T> data, int draw, int recordsTotal, int recordsFiltered) {
		this.data = data;
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
	}
	
	public int getDraw() {
		return draw;
	}
	
	public int getRecordsTotal() {
		return recordsTotal;
	}
	
	public int getRecordsFiltered() {
		return recordsFiltered;
	}
	
	public String getError() {
		return message;
	}
	
	@Override
	public List<T> getData() {
		return data;
	}
	
	@Override
	public Integer getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public boolean isSuccess() {
		return code == 0;
	}
}
