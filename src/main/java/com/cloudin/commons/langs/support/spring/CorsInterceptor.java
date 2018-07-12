package com.cloudin.commons.langs.support.spring;

import com.cloudin.commons.langs.ServletUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * 跨域处理拦截器
 *
 * @author 小天
 * @version 1.0.0, 2018/3/30 0030 14:11
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Access_control_CORS">HTTP访问控制（CORS）</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials">Access-Control-Allow-Credentials</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Headers">Access-Control-Allow-Headers</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Methods">Access-Control-Allow-Methods</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Allow-Origin">Access-Control-Allow-Origin</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Expose-Headers">Access-Control-Expose-Headers</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Max-Age">Access-Control-Max-Age</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Request-Headers">Access-Control-Request-Headers</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Access-Control-Request-Method">Access-Control-Request-Method</a>
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Origin">Origin</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Glossary/preflight_request">预检请求</a>
 */
public class CorsInterceptor implements HandlerInterceptor, InitializingBean {

    private Logger logger;

    private static final String METHOD_OPTIONS = "OPTIONS";

    /**
     * 跨域请求时，允许的来源域名集合。<br>
     * 域名格式： scheme + "://" + host + ":" + port。 例如：https://developer.mozilla.org
     */
    private Set<String> accessControlAllowOriginSet;

    /**
     * 跨域请求时，允许使用的方法或方法列表
     */
    private String accessControlAllowMethods;
    /**
     * 跨域请求时，是否允许发送 cookie
     */
    private boolean accessControlAllowCredentials = false;
    /**
     * 跨域请求时，可以支持的请求头部集合
     */
    private String accessControlAllowHeaders;
    /**
     * 跨域请求时，可以暴露给客户端的响应头
     */
    private String accessControlExposeHeaders;
    /**
     * 指定预检请求的有效期，单位：秒
     */
    private int accessControlMaxAge = 24 * 3600;

    /**
     * 跨域请求时，允许的来源域名集合<br>
     * 域名格式： scheme + "://" + host + ":" + port。 例如：https://developer.mozilla.org
     *
     * @param accessControlAllowOrigins 域名集合字符串，多个域名以逗号分隔。
     *                                  例如： https://developer.mozilla.org,https://developer.apache.org
     */
    public void setAccessControlAllowOrigins(String accessControlAllowOrigins) {
        accessControlAllowOriginSet = new HashSet<>();
        if (StringUtils.isNotEmpty(accessControlAllowOrigins)) {
            String[] arr = accessControlAllowOrigins.split(",");
            for (String item : arr) {
                if (StringUtils.isNotEmpty(item)) {
                    accessControlAllowOriginSet.add(item);
                }
            }
        }
    }

    public void setAccessControlAllowMethods(String accessControlAllowMethods) {
        this.accessControlAllowMethods = accessControlAllowMethods;
    }

    public void setAccessControlAllowCredentials(boolean accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
    }

    public void setAccessControlAllowHeaders(String accessControlAllowHeaders) {
        this.accessControlAllowHeaders = accessControlAllowHeaders;
    }

    public void setAccessControlExposeHeaders(String accessControlExposeHeaders) {
        this.accessControlExposeHeaders = accessControlExposeHeaders;
    }

    public void setAccessControlMaxAge(int accessControlMaxAge) {
        this.accessControlMaxAge = accessControlMaxAge;
    }

    /**
     * 设置日志
     *
     * @param loggerName 日志名字
     */
    public void setLogger(String loggerName) {
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (accessControlAllowMethods == null) {
            accessControlAllowMethods = "POST, GET, OPTIONS";
        }

        if (accessControlAllowHeaders == null) {
            accessControlAllowHeaders = "Content-Type, X-Requested-With";
        }

        if (logger == null) {
            logger = LoggerFactory.getLogger(getClass());
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 判断是否为预检请求
        if (METHOD_OPTIONS.equalsIgnoreCase(request.getMethod())) {
            String origin = ServletUtils.getOrigin(request);
            if (isAllowOrign(origin)) {
                logger.info("【预检请求】支持的跨域请求 | isCrossOrigin=true,origin={}", origin);

                if (accessControlAllowCredentials) {
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                }

                response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
                response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Max-Age", accessControlMaxAge + "");

                return true;
            } else {
                logger.warn("【预检请求】不支持的跨域请求 | isCrossOrigin=true,origin={}", origin);
                return false;
            }
        }

        // 跨域处理
        if (ServletUtils.isCrossOriginIgnoreHttps(request)) {
            String origin = ServletUtils.getOrigin(request);
            if (isAllowOrign(origin)) {
                logger.info("支持的跨域请求 | isCrossOrigin=true,origin={}", origin);
                if (accessControlAllowCredentials) {
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                }

                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Vary", "Origin");

                if (StringUtils.isNotEmpty(accessControlExposeHeaders)) {
                    response.setHeader("Access-Control-Expose-Headers", accessControlExposeHeaders);
                }
            } else {
                logger.warn("不支持的跨域请求 | isCrossOrigin=true,origin={}", origin);
                return false;
            }
        } else {
            logger.debug("isCrossOrigin=false");
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

    private boolean isAllowOrign(String origin) {
        return CollectionUtils.isNotEmpty(accessControlAllowOriginSet) && accessControlAllowOriginSet
                .contains(origin);
    }
}
