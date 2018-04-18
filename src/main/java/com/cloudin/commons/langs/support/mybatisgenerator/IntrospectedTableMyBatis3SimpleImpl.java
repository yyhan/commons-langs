package com.cloudin.commons.langs.support.mybatisgenerator;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚宝吧生成mybatis的时候，数据库接口用DAO结尾
 *
 * @author wuyajun
 */
public class IntrospectedTableMyBatis3SimpleImpl extends IntrospectedTableMyBatis3Impl {
	
	@Override
	protected void calculateJavaClientAttributes() {
		if (context.getJavaClientGeneratorConfiguration() == null) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(calculateJavaClientImplementationPackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("DAOImpl");
		setDAOImplementationType(sb.toString());
		
		sb.setLength(0);
		sb.append(calculateJavaClientInterfacePackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("DAO");
		setDAOInterfaceType(sb.toString());
		
		sb.setLength(0);
		sb.append(calculateJavaClientInterfacePackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("DAO");
		setMyBatis3JavaMapperType(sb.toString());
		
		sb.setLength(0);
		sb.append(calculateJavaClientInterfacePackage());
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("SqlProvider");
		setMyBatis3SqlProviderType(sb.toString());
	}
	
	/**
	 * xml 文件生成时覆盖
	 *
	 * @return
	 */
	@Override
	public List<GeneratedXmlFile> getGeneratedXmlFiles() {
		List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();
		
		if (xmlMapperGenerator != null) {
			// 覆盖xml文件
			Document document = xmlMapperGenerator.getDocument();
			GeneratedXmlFile gxf = new GeneratedXmlFile(document, getMyBatis3XmlMapperFileName(),
				getMyBatis3XmlMapperPackage(), context.getSqlMapGeneratorConfiguration().getTargetProject(), false,
				context.getXmlFormatter());
			if (context.getPlugins().sqlMapGenerated(gxf, this)) {
				answer.add(gxf);
			}
		}
		
		return answer;
	}
}
