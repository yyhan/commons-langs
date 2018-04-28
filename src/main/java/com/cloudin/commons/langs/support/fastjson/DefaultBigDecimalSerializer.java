package com.cloudin.commons.langs.support.fastjson;

import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextObjectSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 使用 {@link DecimalFormat} 对 BigDecimal 序列化。
 * <ul>
 * <li>未指定 {@link com.alibaba.fastjson.annotation.JSONField#format() } 时，使用{@link #defaultFormat } 进行序列化</li>
 * <li>启用 {@link SerializerFeature#WriteNullNumberAsZero} 时，null 值会序列化为 "0.00"。</li>
 * </ul>
 *
 * 例如：
 * <pre>
 *     // 以下代码，默认对 BigDecimal 保留 2 位小数
 *     SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer());
 *     SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer("0.00"));
 *     SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer(new DecimalFormat("0.00")));
 *
 *     // 以下代码，默认对 BigDecimal 保留 4 位小数
 *     SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer("0.0000"));
 *     SerializeConfig.getGlobalInstance().put(BigDecimal.class, new DefaultBigDecimalSerializer(new DecimalFormat("0.0000")));
 *
 *     // 以下代码，单独指定 'price' 字段保留 2 位小数
 *     class DemoObject{
 *         	&#064;JSONField(format = "0.00")
 *         	private BigDecimal price;
 *     }
 * </pre>
 *
 * 注意：
 * <ul>
 *     <li>遇到 泛型 和 嵌套泛型 时，fastjson 有 bug，建议禁用 asm 特性</li>
 * </ul>
 *
 * @author 小天
 * @version 1.0.0, 2017/12/26 0026 10:17
 * @see DecimalFormat
 */
public class DefaultBigDecimalSerializer implements ContextObjectSerializer {
	
	private DecimalFormat defaultFormat;
	
	public DefaultBigDecimalSerializer() {
		defaultFormat = new DecimalFormat("0.00");
	}
	
	/**
	 * @param defaultFormat 默认 decimal 格式
	 *
	 * @see DecimalFormat
	 */
	public DefaultBigDecimalSerializer(DecimalFormat defaultFormat) {
		this.defaultFormat = defaultFormat;
	}
	
	/**
	 * @param defaultFormat 默认 decimal 格式
	 *
	 * @see DecimalFormat
	 */
	public DefaultBigDecimalSerializer(String defaultFormat) {
		this.defaultFormat = new DecimalFormat(defaultFormat);
	}
	
	@Override
	public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
		DecimalFormat annotationFormat = null;
		if (context.getBeanClass() != null) {
			if (context.getFormat().length() > 0) {
				annotationFormat = new DecimalFormat(context.getFormat());
			}
		}
		if (annotationFormat == null) {
			annotationFormat = defaultFormat;
		}
		SerializeWriter out = serializer.out;
		if (object == null) {
			if (serializer.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
				out.write(annotationFormat.format(BigDecimal.ZERO));
			}
		} else {
			out.writeString(annotationFormat.format(object));
		}
	}
	
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
		serializer.out.writeString(defaultFormat.format(object));
	}
}
