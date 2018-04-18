package com.cloudin.commons.langs.support.mybatis;

import com.cloudin.commons.langs.AESUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密字段处理器
 *
 * @author 小天
 * @version 1.0.0, 2017/10/20 0020 09:40
 */
public class SecurityFieldTypeHandler implements TypeHandler<String> {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private static String	SEC_FIELD_KEY = "a0Eyhv9IdqBMhS4N";
	
	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		try {
			String value = AESUtils.encryptToBase64(SEC_FIELD_KEY, parameter);
			ps.setString(i, value);
		} catch (Exception e) {
			logger.error(MessageFormatter.format("i={},parameter={}", i, parameter).getMessage(), e);
		}
	}
	
	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return AESUtils.decryptBase64(SEC_FIELD_KEY, value);
			} catch (Throwable t) {
				logger.error(MessageFormatter.format("columnName={},value={}", columnName, value).getMessage(), t);
			}
		}
		return value;
	}
	
	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return AESUtils.decryptBase64(SEC_FIELD_KEY, value);
			} catch (Throwable t) {
				logger.error(MessageFormatter.format("columnIndex={},value={}", columnIndex, value).getMessage(), t);
			}
		}
		return value;
	}
	
	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return AESUtils.decryptBase64(SEC_FIELD_KEY, value);
			} catch (Throwable t) {
				logger.error(MessageFormatter.format("columnIndex={},value={}", columnIndex, value).getMessage(), t);
			}
		}
		return value;
	}
}
