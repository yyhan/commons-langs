package com.cloudin.commons.langs.support.mybatisgenerator;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 分页插件
 *
 * @author 小天
 * @version 1.0.0, 2018/1/5 0005 11:40
 */
public class PaginationPlugin extends PluginAdapter {
	
	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		String paginationClassName = getContext().getProperty("paginationClassName");
		
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(paginationClassName);
		topLevelClass.setSuperClass(type);
		topLevelClass.addImportedType(type);
		return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
	}
	
	@Override
	public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return super.providerGenerated(topLevelClass, introspectedTable);
	}
	
	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
		IntrospectedTable introspectedTable) {
		XmlElement isNotNullElement = new XmlElement("if");
		isNotNullElement.addAttribute(new Attribute("test", "offset != null and limit != null"));
		isNotNullElement.addElement(new TextElement("limit ${offset}, ${limit}"));
		element.addElement(isNotNullElement);
		return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
	}
	
	public boolean validate(List<String> warnings) {
		return true;
	}
}
