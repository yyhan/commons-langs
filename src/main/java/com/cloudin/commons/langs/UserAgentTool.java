package com.cloudin.commons.langs;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 浏览器ua工具
 *
 * @author 小天
 * @version 1.0.0, 2017/11/2 0002 16:15
 */
public class UserAgentTool {
	
	private static Pattern pattern = Pattern.compile(
		"Mozilla\\/5\\.0 \\((([a-zA-Z0-9_\\/\\-\\.\\s]+)(; [a-zA-Z0-9_\\/\\-\\.\\s]+)+)\\)(( [\\(\\),a-zA-Z0-9_\\/\\-\\.\\:\\|\\s]+)+)");
	
	private static Pattern androidPattern = Pattern.compile("Android ([0-9\\.]+)");
	private static Pattern iosPattern     = Pattern.compile("iPhone; CPU iPhone OS ([0-9\\_]+) like Mac OS X");
	private static Pattern wxPattern      = Pattern.compile("(MicroMessenger\\/([0-9\\.]+))");
	private static Pattern alipayPattern  = Pattern.compile("(AlipayClient\\/([0-9\\.]+))");
	
	public static void main(String[] args) {
		
		String[] uas = new String[]{
			
			// oppo 支付宝
			"Mozilla/5.0 (Linux; U; Android 5.1.1; zh-CN; OPPO R9 Plustm A Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.89 UCBrowser/11.6.4.950 UWS/2.11.0.32 Mobile Safari/537.36 UCBS/2.11.0.32 Nebula AlipayDefined(nt:UNKNOWN,ws:360|0|3.0) AliApp(AP/10.1.5.102509) AlipayClient/10.1.5.102509 Language/zh-Hans useStatusBar/true",
			// oppo 微信
			"Mozilla/5.0 (Linux; Android 5.1.1; OPPO R9 Plustm A Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043610 Safari/537.36 MicroMessenger/6.5.16.1120 NetType/WIFI Language/zh_CN",
			// iphone 6s 微信
			"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60 MicroMessenger/6.5.20 NetType/WIFI Language/zh_CN",
			// iphone 6s 支付宝
			"Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_3 like Mac OS X) AppleWebKit/603.3.8 (KHTML, like Gecko) Mobile/14G60 Nebula PSDType(1) AlipayDefined(nt:WIFI,ws:375|603|2.0) AliApp(AP/10.1.5.102407) AlipayClient/10.1.5.102407 Language/zh-Hans",
			// iphone 8 微信
			"Mozilla/5.0 (iPhone; CPU iPhone OS 11_1 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Mobile/15B93 MicroMessenger/6.5.20 NetType/WIFI Language/zh_CN",
			// iphone 8 支付宝
			"Mozilla/5.0 (iPhone; CPU iPhone OS 11_1 like Mac OS X) AppleWebKit/604.3.5 (KHTML, like Gecko) Mobile/15B93 Nebula PSDType(1) AlipayDefined(nt:WIFI,ws:375|603|2.0) AliApp(AP/10.1.5.102407) AlipayClient/10.1.5.102407 Language/zh-Hans"
		};
		for (String ua : uas) {
			parse(ua);
		}
	}
	
	public static class UserAgent implements Serializable {
		
		private String  os;
		private String  osVersion;
		private String  kernel;
		private boolean isAlipayClient;
		private boolean isWeiXin;
		private String  browser;
		private String  browserVersion;
		
		public String getOs() {
			return os;
		}
		
		public void setOs(String os) {
			this.os = os;
		}
		
		public String getOsVersion() {
			return osVersion;
		}
		
		public void setOsVersion(String osVersion) {
			this.osVersion = osVersion;
		}
		
		public String getKernel() {
			return kernel;
		}
		
		public void setKernel(String kernel) {
			this.kernel = kernel;
		}
		
		public boolean isAlipayClient() {
			return isAlipayClient;
		}
		
		public void setAlipayClient(boolean alipayClient) {
			isAlipayClient = alipayClient;
		}
		
		public boolean isWeiXin() {
			return isWeiXin;
		}
		
		public void setWeiXin(boolean weiXin) {
			isWeiXin = weiXin;
		}
		
		public String getBrowser() {
			return browser;
		}
		
		public void setBrowser(String browser) {
			this.browser = browser;
		}
		
		public String getBrowserVersion() {
			return browserVersion;
		}
		
		public void setBrowserVersion(String browserVersion) {
			this.browserVersion = browserVersion;
		}
	}
	
	public static UserAgent parse(String uaStr) {
		UserAgent userAgent = null;
		Matcher matcher = pattern.matcher(uaStr);
		if (matcher.find()) {
			userAgent = new UserAgent();
			userAgent.setKernel(matcher.group(2));
			if (StringUtils.equalsIgnoreCase("Linux", userAgent.getKernel())) {
				String osStr = matcher.group(1);
				if (osStr.matches("android")) {
					userAgent.setOs("Android");
					Matcher androidMatcher = androidPattern.matcher(matcher.group(1));
					if (androidMatcher.find()) {
						userAgent.setOsVersion(androidMatcher.group(1));
					}
				} else {
					userAgent.setOs("Linux");
				}
			} else if (StringUtils.equalsIgnoreCase("iPhone", userAgent.getKernel())) {
				userAgent.setOs("iPhone OS");
				Matcher iosMatcher = iosPattern.matcher(matcher.group(1));
				if (iosMatcher.find()) {
					userAgent.setOsVersion(iosMatcher.group(1).replace("_", "."));
				}
			}
			parseBrowser(userAgent, matcher.group(4));
		}
		return userAgent;
	}
	
	private static void parseBrowser(UserAgent ua, String str) {
		Matcher wxMatcher = wxPattern.matcher(str);
		if (wxMatcher.find()) {
			ua.setWeiXin(true);
			ua.setBrowser("MicroMessenger");
			ua.setBrowserVersion(wxMatcher.group(2));
		} else {
			Matcher alipayMatcher = alipayPattern.matcher(str);
			if (alipayMatcher.find()) {
				ua.setAlipayClient(true);
				ua.setBrowser("AlipayClient");
				ua.setBrowserVersion(alipayMatcher.group(2));
			}
		}
		
	}
}
