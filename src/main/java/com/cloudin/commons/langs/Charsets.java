package com.cloudin.commons.langs;

import java.nio.charset.Charset;

/**
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 * @deprecated 请使用 {@link java.nio.charset.StandardCharsets}
 */
@Deprecated
public class Charsets {
	
	public static final Charset GBK   = Charset.forName("GBK");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final Charset ASCII = Charset.forName("ASCII");
	
}
