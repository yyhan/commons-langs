package com.cloudin.commons.langs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求工具
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class HttpUtils {
	
	private static Logger logger = null;
	
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0";
	
	/**
	 * 连接超时时间
	 */
	private static int DEFAULT_CONNECTION_TIMEOUT = 2 * 60 * 1000;
	
	/**
	 * socket 超时时间
	 */
	private static int DEFAULT_SOCKET_TIMEOUT = 10 * 60 * 1000;
	
	private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	public static ContentType DEFAULT_CONTENT_TYPE = ContentType.create("text/plain", DEFAULT_CHARSET);
	
	public static SSLConnectionSocketFactory sslConnectionSocketFactory;
	
	static {
		logger = LoggerFactory.getLogger(HttpUtils.class);
		try {
			SSLContext sslcontext = SSLContext.getInstance("SSLv3");
			sslcontext.init(null, new TrustManager[]{new SimpleTrustManager()}, null);
			sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext, new String[]{
				"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"
			}, null, new SimpleTrustManager());
		} catch (NoSuchAlgorithmException e) {
			logger.error("", e);
		} catch (KeyManagementException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 发送get请求，并返回响应内容
	 *
	 * @param url 请求url
	 *
	 * @return 响应内容，UTF-8编码
	 *
	 * @throws java.io.IOException
	 */
	public static String get(String url) throws IOException, HttpException {
		return get(url, DEFAULT_CHARSET);
	}
	
	/**
	 * 发送 get 请求，并返回响应内容。
	 *
	 * @param url     请求url
	 * @param charset 响应结果编码
	 *
	 * @return 响应内容，
	 *
	 * @throws java.io.IOException
	 */
	public static String get(String url, Charset charset) throws IOException, HttpException {
		return get(url, charset, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
	}
	
	/**
	 * 发送get请求，并返回响应内容
	 *
	 * @param url               请求url
	 * @param charset           响应结果编码
	 * @param connectionTimeout 链接建立超时时间
	 * @param socketTimeout     数据传输超时时间
	 *
	 * @return 响应内容
	 *
	 * @throws java.io.IOException
	 */
	public static String get(String url, Charset charset, Integer connectionTimeout, Integer socketTimeout)
		throws IOException, HttpException {
		URL requestUrl = new URL(url);
		HttpGet httpGet = new HttpGet(url);
		
		CloseableHttpClient httpsClient = null;
		try {
			httpsClient = getHttpClient(connectionTimeout, socketTimeout);
			CloseableHttpResponse response = httpsClient.execute(httpGet);
			try {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					if (charset == null) {
						return EntityUtils.toString(entity, DEFAULT_CHARSET);
					} else {
						return EntityUtils.toString(entity, charset);
					}
				} else {
					throw new HttpException("请求失败： status=" + statusLine.toString());
				}
			} finally {
				org.apache.http.client.utils.HttpClientUtils.closeQuietly(response);
			}
		} finally {
			org.apache.http.client.utils.HttpClientUtils.closeQuietly(httpsClient);
		}
	}
	
	/**
	 * 发送post请求，并返回响应内容。使用默认ContentType {@link #DEFAULT_CONTENT_TYPE}
	 *
	 * @param url  请求url
	 * @param body 请求内容
	 *
	 * @return 响应内容，UTF-8编码
	 *
	 * @throws java.io.IOException
	 */
	public static String post(String url, String body) throws IOException, HttpException {
		return post(url, body, DEFAULT_CONTENT_TYPE);
	}
	
	/**
	 * 发送post请求，并返回响应内容。使用默认ContentType {@link #DEFAULT_CONTENT_TYPE}
	 *
	 * @param url     请求url
	 * @param body    请求内容
	 * @param charset 编码
	 *
	 * @return 响应内容
	 *
	 * @throws java.io.IOException
	 */
	public static String post(String url, String body, Charset charset) throws IOException, HttpException {
		return post(url, body, ContentType.TEXT_PLAIN.withCharset(charset));
	}
	
	/**
	 * 发送post请求，并返回响应内容。
	 *
	 * @param url         请求url
	 * @param body        请求内容
	 * @param contentType http请求内容类型。例如： "text/plain; charset=UTF-8"
	 *
	 * @return 响应内容，
	 *
	 * @throws java.io.IOException
	 */
	public static String post(String url, String body, String contentType) throws IOException, HttpException {
		return post(url, body, ContentType.parse(contentType));
	}
	
	/**
	 * 发送post请求，并返回响应内容。
	 *
	 * @param url         请求url
	 * @param body        请求内容
	 * @param contentType http请求内容类型。例如： "text/plain; charset=UTF-8"
	 *
	 * @return 响应内容，
	 *
	 * @throws java.io.IOException
	 */
	public static String post(String url, String body, ContentType contentType) throws IOException, HttpException {
		return post(url, body, contentType, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
	}
	
	/**
	 * 发送post请求，并返回响应内容。
	 *
	 * @param url         请求url
	 * @param formData    表单数据
	 * @param contentType http请求内容类型。例如： "text/plain; charset=UTF-8"
	 *
	 * @return 响应内容，
	 *
	 * @throws java.io.IOException
	 */
	public static String postForm(String url, Map<String, String> formData, ContentType contentType)
		throws IOException, HttpException {
		return postForm(url, formData, contentType, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
	}
	
	/**
	 * 发送post请求，并返回响应内容。
	 *
	 * @param url               请求url
	 * @param formData          表单数据
	 * @param contentType       http请求内容类型。例如： "text/plain; charset=UTF-8"
	 * @param connectionTimeout 链接建立超时时间
	 * @param socketTimeout     数据传输超时时间
	 *
	 * @return 响应内容文本
	 *
	 * @throws java.io.IOException
	 * @throws org.apache.http.HttpException
	 */
	public static String postForm(String url, Map<String, String> formData, ContentType contentType,
		Integer connectionTimeout, Integer socketTimeout) throws IOException, HttpException {
		
		List<NameValuePair> nameValuePairList = new ArrayList<>();
		for (Map.Entry<String, String> item : formData.entrySet()) {
			nameValuePairList.add(new BasicNameValuePair(item.getKey(), item.getValue()));
		}
		return post(url, EntityBuilder.create().setParameters(nameValuePairList).setContentType(contentType).build(), contentType,
			connectionTimeout, socketTimeout);
	}
	
	/**
	 * 发送post请求，并返回响应内容。
	 *
	 * @param url               请求url
	 * @param body              请求内容
	 * @param contentType       http请求内容类型。例如： "text/plain; charset=UTF-8"
	 * @param connectionTimeout 链接建立超时时间
	 * @param socketTimeout     数据传输超时时间
	 *
	 * @return 响应内容文本
	 *
	 * @throws java.io.IOException
	 * @throws org.apache.http.HttpException
	 */
	public static String post(String url, String body, ContentType contentType, Integer connectionTimeout,
		Integer socketTimeout) throws IOException, HttpException {
		return post(url, new StringEntity(body, contentType), contentType, connectionTimeout, socketTimeout);
	}
	
	/**
	 * 发送post请求，并返回响应内容。
	 *
	 * @param url               请求url
	 * @param httpEntity        请求内容
	 * @param contentType       http请求内容类型。例如： "text/plain; charset=UTF-8"
	 * @param connectionTimeout 链接建立超时时间
	 * @param socketTimeout     数据传输超时时间
	 *
	 * @return 响应内容，
	 *
	 * @throws java.io.IOException
	 */
	public static String post(String url, final HttpEntity httpEntity, ContentType contentType,
		Integer connectionTimeout, Integer socketTimeout) throws IOException, HttpException {
		logger.debug("requestUrl={}", url);
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(httpEntity);
		
		CloseableHttpClient httpsClient = getHttpClient(connectionTimeout, socketTimeout);
		try {
			CloseableHttpResponse response = httpsClient.execute(httpPost);
			try {
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();
					return EntityUtils.toString(entity, contentType.getCharset());
				} else {
					throw new HttpException("请求失败： status=" + statusLine.toString());
				}
			} finally {
				org.apache.http.client.utils.HttpClientUtils.closeQuietly(response);
			}
		} finally {
			org.apache.http.client.utils.HttpClientUtils.closeQuietly(httpsClient);
		}
	}
	
	/**
	 * 创建一个新的 CloseableHttpClient，该HttpClient只能用于单个请求，不支持cookie，不支持压缩。<br>
	 * 如果jvm 不支持 SSLv3 算法，该Client将不能用于https请求
	 *
	 * @param connectionTimeout 链接建立超时时间
	 * @param socketTimeout     数据传输超时时间
	 *
	 * @return CloseableHttpClient
	 */
	public static CloseableHttpClient getHttpClient(Integer connectionTimeout, Integer socketTimeout) {
		
		return getHttpClient(connectionTimeout, socketTimeout, null);
	}
	
	/**
	 * 创建一个新的 CloseableHttpClient，该HttpClient只能用于单个请求，不支持cookie，不支持压缩。<br>
	 * 如果jvm 不支持 SSLv3 算法，该Client将不能用于https请求
	 *
	 * @param connectionTimeout 链接建立超时时间
	 * @param socketTimeout     数据传输超时时间
	 * @param connectionManager 连接管理器
	 *
	 * @return CloseableHttpClient
	 */
	public static CloseableHttpClient getHttpClient(Integer connectionTimeout, Integer socketTimeout,
		HttpClientConnectionManager connectionManager) {
		
		RequestConfig.Builder requestConfigBuiler = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
			.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
		
		if (connectionTimeout != null) {
			requestConfigBuiler.setConnectTimeout(connectionTimeout);
		}
		if (socketTimeout != null) {
			requestConfigBuiler.setSocketTimeout(socketTimeout);
		}
		
		HttpClientBuilder builder = HttpClients.custom().setUserAgent(DEFAULT_USER_AGENT).disableCookieManagement()
			.disableAuthCaching().disableAutomaticRetries().disableConnectionState().disableContentCompression()
			.disableRedirectHandling().setDefaultRequestConfig(requestConfigBuiler.build());
		if (connectionManager != null) {
			builder.setConnectionManager(connectionManager);
		}
		builder.setSSLSocketFactory(sslConnectionSocketFactory);
		return builder.build();
	}
	
	/**
	 * 简单ssl 处理， 不作任何验证处理
	 */
	private static class SimpleTrustManager implements X509TrustManager, HostnameVerifier {
		
		private SimpleTrustManager() {
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}
		
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		
		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
	}
	
	public static void main(String[] args) {
		
		try {
			String requestBody = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "<AIPG><INFO><TRX_CODE>211003</TRX_CODE><VERSION>03</VERSION><DATA_TYPE>2</DATA_TYPE><LEVEL>5</LEVEL><USER_NAME>20060400000044502</USER_NAME><USER_PASS>`12qwe</USER_PASS><REQ_SN>1505207167678-K0wkdr</REQ_SN><SIGNED_MSG>89dec912f51f5ef8ab06cc503bddfec076de72b515b5802f38a1f7c0ac76c63f900220fb7db4d7928d4d4218108316e32d6b86e01ab56160e11cb511b62593a291816854437d9a1577e53ae32aa575bb4fd0f52bac03094a215508e8352587e0bb289d30dcd0c73d6fc85091d8f69ff3e75a3b78873b092b204846be1a1437bc</SIGNED_MSG></INFO><VALIDR><MERCHANT_ID>200604000000445</MERCHANT_ID><SUBMIT_TIME>20170912170607</SUBMIT_TIME><BANK_CODE>0105</BANK_CODE><ACCOUNT_NO>6217000340000565982</ACCOUNT_NO><ACCOUNT_NAME>杨晓晖</ACCOUNT_NAME><ACCOUNT_PROP>0</ACCOUNT_PROP><ID_TYPE>0</ID_TYPE><ID>110101198601040010</ID><TEL></TEL><REMARK></REMARK></VALIDR></AIPG>";
			String res = post("https://113.108.182.3/aipg/ProcessServlet", requestBody, ContentTypes.APPLICATION_JSON_UTF8);
			System.out.println(res);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}
	}
}
