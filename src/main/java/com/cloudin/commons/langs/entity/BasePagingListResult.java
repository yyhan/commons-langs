package com.cloudin.commons.langs.entity;

import java.util.List;

/**
 * 基本分页查询结果实体
 *
 * @author 小天
 * @version 1.0.0, 2017/11/28 0028 15:24
 */
public class BasePagingListResult<T> implements IBaseResult {
	
	private Integer   code;
	private String    message;
	private List<T>   data;
	private Exception exception;
	
	/**
	 * 总记录数
	 */
	private Integer total;
	/**
	 * 筛选后的总记录数
	 */
	private Integer filteredTotal;
	
	protected BasePagingListResult() {
	}
	
	protected BasePagingListResult(List<T> data) {
		this.data = data;
	}
	
	public static <U> BasePagingListResult<U> build() {
		return new BasePagingListResult<U>();
	}
	
	public static <U> BasePagingListResult<U> build(List<U> data) {
		return new BasePagingListResult<U>(data);
	}
	
	public BasePagingListResult<T> success(List<T> data, Integer total) {
		this.code = 0;
		this.data = data;
		this.total = total;
		this.filteredTotal = total;
		return this;
	}
	
	public BasePagingListResult<T> success(List<T> data, Integer total, Integer filteredTotal) {
		this.code = 0;
		this.data = data;
		this.total = total;
		this.filteredTotal = filteredTotal;
		return this;
	}
	
	public BasePagingListResult<T> error(int code, String message) {
		this.code = code;
		this.message = message;
		return this;
	}
	
	public BasePagingListResult<T> error(int code, String message, Exception exception) {
		this.code = code;
		this.message = message;
		this.exception = exception;
		return this;
	}
	
	public BasePagingListResult<T> withCode(int code) {
		this.code = code;
		return this;
	}
	
	public BasePagingListResult<T> withMessage(String message) {
		this.message = message;
		return this;
	}
	
	public BasePagingListResult<T> withData(List<T> data) {
		this.data = data;
		return this;
	}
	
	@Override
	public Integer getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public List<T> getData() {
		return data;
	}

	public boolean isSuccess(){
		return code == 0;
	}

	public Exception getException() {
		return exception;
	}
	
	public Integer getTotal() {
		return total;
	}
	
	public Integer getFilteredTotal() {
		return filteredTotal;
	}
}
