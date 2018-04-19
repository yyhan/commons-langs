package com.cloudin.commons.langs;

import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class ContentTypes {

    /**
     * 将form表单转换为 urlencoded 处理后的键值对
     */
    public static final ContentType APPLICATION_FORM_URLENCODED_UTF8 = ContentType.create("application/x-www-form-urlencoded", StandardCharsets.UTF_8);
    /**
     * json
     */
    public static final ContentType APPLICATION_JSON_UTF8 = ContentType.create("application/json", StandardCharsets.UTF_8);
    /**
     * xml
     */
    public static final ContentType APPLICATION_XML_UTF8 = ContentType.create("application/xml", StandardCharsets.UTF_8);
    /**
     *
     */
    public static final ContentType MULTIPART_FORM_DATA_UTF8 = ContentType.create("multipart/form-data", StandardCharsets.UTF_8);
    /**
     * html
     */
    public static final ContentType TEXT_HTML_UTF8 = ContentType.create("text/html", StandardCharsets.UTF_8);
    /**
     * 明文文本
     */
    public static final ContentType TEXT_PLAIN_UTF8 = ContentType.create("text/plain", StandardCharsets.UTF_8);
    /**
     * xml文本
     */
    public static final ContentType TEXT_XML_UTF8 = ContentType.create("text/xml", StandardCharsets.UTF_8);
    /**
     * 通配符
     */
    public static final ContentType WILDCARD = ContentType.create("*/*", (Charset) null);

}
