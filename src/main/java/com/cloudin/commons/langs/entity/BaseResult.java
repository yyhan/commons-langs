package com.cloudin.commons.langs.entity;

/**
 * 基本结果
 *
 * @author 小天
 * @version 1.0.0, 2017/7/18 0018 09:57
 */
public class BaseResult<T> implements IBaseResult {
	private Integer   code;
	private String    message;
	private T         data;
	private transient Exception exception;
	
	public BaseResult(){
	
	}
	
	public BaseResult(T data) {
		this.data = data;
	}
	
	public BaseResult(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public BaseResult(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	public static <U> BaseResult<U> build() {
		return new BaseResult<U>();
	}
	
	public static <U> BaseResult<U> build(Class<U> uClass) {
		return new BaseResult<U>();
	}
	
	public static <U> BaseResult<U> build(U data) {
		return new BaseResult<U>(data);
	}
	
	public BaseResult<T> success() {
		this.code = 0;
		return this;
	}
	
	public BaseResult<T> success(T data) {
		this.code = 0;
		this.data = data;
		return this;
	}
	
	public BaseResult<T> error(int code, String message) {
		this.code = code;
		this.message = message;
		return this;
	}
	
	public BaseResult<T> error(int code, String message, Exception exception) {
		this.code = code;
		this.message = message;
		this.exception = exception;
		return this;
	}
	
	public BaseResult<T> withCode(int code) {
		this.code = code;
		return this;
	}
	
	public BaseResult<T> withMessage(String message) {
		this.message = message;
		return this;
	}
	
	public BaseResult<T> withData(T data) {
		this.data = data;
		return this;
	}
	
	public boolean isSuccess(){
		return code == 0;
	}
	
	@Override
	public Integer getCode() {
		return code;
	}
	
	public void setCode(Integer code) {
		this.code = code;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public Exception getException() {
		return exception;
	}
	
	@Override
	public String toString() {
		if(data == null){
			if(exception == null){
				return String.format("code=%s,message=%s", code, message);
			} else {
				return String.format("code=%s,message=%s,exception=%s", code, message, exception.getMessage());
			}
		} else {
			return String.format("code=%s,message=%s,data=%s", code, message, data.toString());
		}
	}
}
