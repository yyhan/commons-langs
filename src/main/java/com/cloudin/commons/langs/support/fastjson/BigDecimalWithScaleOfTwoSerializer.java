package com.cloudin.commons.langs.support.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Bigdecimal 序列化，保留两位小数，超过两位小数会被截断。null值会序列化为 "0.00"。
 *
 * @author 小天
 * @version 1.0.0, 2017/12/26 0026 10:17
 */
public class BigDecimalWithScaleOfTwoSerializer implements ObjectSerializer {
	
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
		throws IOException {
		SerializeWriter out = serializer.out;
		if (object == null) {
			out.write("0.00");
		} else {
			BigDecimal value = (BigDecimal) object;
			out.writeString(value.setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
		}
	}
}
