package com.cloudin.commons.langs;


import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * BigDecimal扩展功能
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class BigDecimals {
	
	/**
	 * 100
	 */
	public final static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	/**
	 * 百分之一
	 */
	public final static BigDecimal ONE_PERCENT = new BigDecimal("0.01");
	/**
	 * 十分之一
	 */
	public final static BigDecimal ONE_TENTH   = new BigDecimal("0.1");
	/**
	 * 千分之一
	 */
	public final static BigDecimal ONE_THOUSANDTH = new BigDecimal("0.001");
	
	public static BigDecimal fromString(String txt, int scale) {
		if (StringUtils.isBlank(txt)) {
			return null;
		}
		return new BigDecimal(txt).setScale(scale, BigDecimal.ROUND_DOWN);
	}
	
	public static boolean isSame(BigDecimal v1, BigDecimal v2) {
		if (v1 == null || v2 == null) {
			return false;
		}
		return v1.compareTo(v2) == 0;
	}
	
	/**
	 * 将Double类型转换为BigDecimal
	 * if value == null return 0
	 *
	 * @param value
	 * @param scale
	 * @param roundingMode
	 * @return
	 */
	public static BigDecimal valueOf(Double value, int scale, int roundingMode) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(value).setScale(scale, roundingMode);
	}
	
	/**
	 * 比较v1和v2的值，返回v1是否大于v2。
	 * 如果v1==null，return false
	 * 否则，如果v2==null，return true
	 * 否则比较v1和v2的值，返回v1是否大于v2
	 *
	 * @param v1
	 * @param v2
	 * @return 返回v1是否大于v2
	 */
	public static boolean greaterThan(BigDecimal v1, BigDecimal v2) {
		return v1 != null && (v2 == null || v1.compareTo(v2) > 0);
	}
	
	/**
	 * 比较v1和v2的值，返回v1是否小于v2。
	 * 如果v1==null，return false
	 * 否则，如果v2==null，return false
	 * 否则比较v1和v2的值，返回v1是否小于v2。
	 *
	 * @param v1
	 * @param v2
	 * @return 返回v1是否小于v2。
	 */
	public static boolean lessThan(BigDecimal v1, BigDecimal v2) {
		if (v1 != null) {
			if (v2 == null) {
				return false;
			} else {
				return v1.compareTo(v2) < 0;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 比较v1和v2的值，返回v1是否大于等于v2。
	 * 如果v1==null，return false
	 * 否则，如果v2==null，return true
	 * 否则比较v1和v2的值，返回v1是否大于v2
	 *
	 * @param v1
	 * @param v2
	 * @return 返回v1是否大于v2
	 */
	public static boolean greaterOrSameThan(BigDecimal v1, BigDecimal v2) {
		return v1 != null && (v2 == null || v1.compareTo(v2) >= 0);
	}
	
	public static BigDecimal fromString(String txt, int scale, int roundingMode) {
		if (StringUtils.isBlank(txt)) {
			return null;
		}
		return new BigDecimal(txt).setScale(scale, roundingMode);
	}
	
	/**
	 * 比较value是否是否介于left（包含）和right（包含）之间
	 *
	 * @param value
	 * @param left
	 * @param right
	 * @return
	 */
	public static boolean between(BigDecimal value, BigDecimal left, BigDecimal right) {
		if (value == null) {
			return false;
		}
		if (left == null) {
			return right != null && value.compareTo(right) <= 0;
		} else {
			if (right == null) {
				return value.compareTo(left) >= 0;
			} else {
				return value.compareTo(left) >= 0 && value.compareTo(right) <= 0;
			}
		}
	}
}
