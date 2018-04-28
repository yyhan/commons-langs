package com.cloudin.commons.langs.support.fastjson;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.springframework.beans.factory.FactoryBean;

import java.util.Map;

/**
 * fastjson SerializeConfig 工厂 bean
 *
 * @author 小天
 * @version 1.0.0, 2018/4/26 0026 11:18
 */
public class SerializeConfigFactoryBean implements FactoryBean<SerializeConfig> {
	
	private boolean asm        = false;
	private boolean fieldBased = false;
	private Map<String, ObjectSerializer> serializerMap;
	
	@Override
	public SerializeConfig getObject() throws Exception {
		SerializeConfig config = new SerializeConfig(fieldBased);
		config.setAsmEnable(asm);
		if (serializerMap != null) {
			for (Map.Entry<String, ObjectSerializer> serializerEntry : serializerMap.entrySet()) {
				config.put(Class.forName(serializerEntry.getKey()), serializerEntry.getValue());
			}
		}
		return config;
	}
	
	@Override
	public Class<?> getObjectType() {
		return SerializeConfig.class;
	}
	
	@Override
	public boolean isSingleton() {
		return false;
	}
	
	/**
	 * 是否禁用 asm
	 *
	 * @param asm 是否禁用 asm
	 *
	 * @see <a href='https://github.com/alibaba/fastjson/wiki/ASMDeserializerFactory%E8%AE%BE%E8%AE%A1'>ASMDeserializerFactory设计</a>
	 */
	public void setAsm(boolean asm) {
		this.asm = asm;
	}
	
	/**
	 * @param fieldBased
	 *
	 * @see <a href='https://github.com/alibaba/fastjson/wiki/FieldBased_cn'>FieldBased</a>
	 */
	public void setFieldBased(boolean fieldBased) {
		this.fieldBased = fieldBased;
	}
	
	/**
	 * 添加自定义的序列化类
	 *
	 * @param serializerMap 自定义的序列化类集合
	 */
	public void setSerializer(Map<String, ObjectSerializer> serializerMap) {
		this.serializerMap = serializerMap;
	}
}
