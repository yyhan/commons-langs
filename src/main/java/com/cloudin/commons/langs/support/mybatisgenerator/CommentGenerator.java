package com.cloudin.commons.langs.support.mybatisgenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 自动生成注释插件
 *
 * @author 小天
 * @version 1.0.0, 2018/1/5 0005 11:40
 */
public class CommentGenerator implements org.mybatis.generator.api.CommentGenerator {

	private Properties properties;
	private boolean    suppressDate;
	private boolean    suppressAllComments;
	private String     currentDateStr;

	public CommentGenerator() {
		super();
		properties = new Properties();
		suppressDate = false;
		suppressAllComments = false;
		currentDateStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
	}
	
	@Override
	public void addJavaFileComment(CompilationUnit compilationUnit) {
		String className = compilationUnit.getType().getFullyQualifiedName();
		if(className.endsWith("Example") || className.endsWith("DAO")){
			JavaElement topLevelClass = (JavaElement)compilationUnit;
			topLevelClass.addJavaDocLine("/**");
			topLevelClass.addJavaDocLine(" * @author MyBatis Generator");
			topLevelClass.addJavaDocLine(" * @version 1.0.0, " + getDateString());
			topLevelClass.addJavaDocLine(" */");
		}
	}
	
	@Override
	public void addComment(XmlElement xmlElement) {
	}
	
	@Override
	public void addRootComment(XmlElement rootElement) {
	
	}
	
	@Override
	public void addConfigurationProperties(Properties properties) {
		this.properties.putAll(properties);
		suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
		suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
	}
	
	private String getDateString() {
		String result = null;
		if (!suppressDate) {
			result = currentDateStr;
		}
		return result;
	}
	
	@Override
	public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		innerEnum.addJavaDocLine("/**");
		innerEnum.addJavaDocLine(" * 枚举。" + introspectedTable.getRemarks());
		innerEnum.addJavaDocLine(" */");
	}
	
	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
		IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}
		
		field.addJavaDocLine("/**");
		field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
		field.addJavaDocLine(" * ");
		field.addJavaDocLine(" * @column " + introspectedTable.getFullyQualifiedTable() + "." + introspectedColumn
			.getActualColumnName());
		field.addJavaDocLine(" */");
	}
	
	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

	}
	
	@Override
	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		
		if(!method.isConstructor()){
			method.addJavaDocLine("/**");
			method.addJavaDocLine(" * ");
			method.addJavaDocLine(" * " + method.getName());
			method.addJavaDocLine(" * ");
			
			if(!method.getParameters().isEmpty()){
				for (Parameter parameter : method.getParameters()) {
					method.addJavaDocLine(" * @param " + parameter.getName());
				}
				method.addJavaDocLine(" * ");
			}
			if(method.getReturnType() != null){
				method.addJavaDocLine(" * @return {@link " + method.getReturnType().getFullyQualifiedName() + "}");
			}
			method.addJavaDocLine(" */");
		}
	}
	
	@Override
	public void addGetterComment(Method method, IntrospectedTable introspectedTable,
		IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}
		method.addJavaDocLine("/**");
		method.addJavaDocLine(" * @column " + introspectedTable.getFullyQualifiedTable() + "." + introspectedColumn
			.getActualColumnName());
		method.addJavaDocLine(" * ");
		method.addJavaDocLine(" * @return " + introspectedColumn.getRemarks());
		method.addJavaDocLine(" */");
	}
	
	@Override
	public void addSetterComment(Method method, IntrospectedTable introspectedTable,
		IntrospectedColumn introspectedColumn) {
		if (suppressAllComments) {
			return;
		}
		method.addJavaDocLine("/**");
		method.addJavaDocLine(" * @column " + introspectedTable.getFullyQualifiedTable() + "." + introspectedColumn
			.getActualColumnName());
		method.addJavaDocLine(" * ");
		method.addJavaDocLine(
			" * @param " + method.getParameters().get(0).getName() + " " + introspectedColumn.getRemarks());
		method.addJavaDocLine(" */");
	}
	
	@Override
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		topLevelClass.addJavaDocLine("/**");
		topLevelClass.addJavaDocLine(" * " + introspectedTable.getRemarks());
		topLevelClass.addJavaDocLine(" * ");
		topLevelClass.addJavaDocLine(" * @table " + introspectedTable.getFullyQualifiedTable());
		topLevelClass.addJavaDocLine(" * @author MyBatis Generator");
		topLevelClass.addJavaDocLine(" * @version 1.0.0, " + getDateString());
		topLevelClass.addJavaDocLine(" */");
	}
	
	@Override
	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
		if (suppressAllComments) {
			return;
		}
		innerClass.addJavaDocLine("/**");
		innerClass.addJavaDocLine(" * @author MyBatis Generator");
		innerClass.addJavaDocLine(" * @version 1.0.0, " + getDateString());
		innerClass.addJavaDocLine(" */");
	}
	
	@Override
	public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
		if (suppressAllComments) {
			return;
		}
		innerClass.addJavaDocLine("/**");
		innerClass.addJavaDocLine(" * @author MyBatis Generator");
		innerClass.addJavaDocLine(" * @version 1.0.0, " + getDateString());
		innerClass.addJavaDocLine(" */");
	}
}

