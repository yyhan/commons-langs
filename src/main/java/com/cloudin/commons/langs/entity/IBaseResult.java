package com.cloudin.commons.langs.entity;

import java.io.Serializable;

/**
 * 基本响应结果接口
 *
 * @author 小天
 * @version 1.0.0, 2017/7/18 0018 09:57
 */
public interface IBaseResult extends Serializable {
	
	/**
	 * @return 错误码
	 */
	Integer getCode();
	
	/**
	 * @return 描述信息
	 */
	String getMessage();
	
	/**
	 * @return 返回数据
	 */
	Object getData();
	
}
