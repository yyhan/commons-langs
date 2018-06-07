package com.cloudin.commons.langs.support.qcloud;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 腾讯云短信服务
 *
 * @author 小天
 * @date 2018/6/7 0007 22:56
 */
public class QcloudSmsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 短信应用SDK AppID
     */
    private int appId;
    /**
     * 短信应用SDK AppKey
     */
    private String appKey;
    /**
     * 默认国家码
     */
    private String defaultNationCode = "86";
    /**
     * 默认短信签名
     */
    private String defaultSmsSign = "";

    public QcloudSmsService(int appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    public QcloudSmsService(int appId, String appKey, String defaultNationCode, String defaultSmsSign) {
        this.appId = appId;
        this.appKey = appKey;
        this.defaultNationCode = defaultNationCode;
        this.defaultSmsSign = defaultSmsSign;
    }

    public void setDefaultNationCode(String defaultNationCode) {
        this.defaultNationCode = defaultNationCode;
    }

    public void setDefaultSmsSign(String defaultSmsSign) {
        this.defaultSmsSign = defaultSmsSign;
    }

    /**
     * 模板短信单发接口
     *
     * @param mobile     手机号
     * @param templateId 短信模板Id
     * @param params     短信模板参数数组
     *
     * @return true：发送成功
     */
    public boolean send(String mobile, int templateId, String params[]) {
        return send(mobile, templateId, params, defaultNationCode, defaultSmsSign, "", "");
    }

    /**
     * 模板短信单发接口
     *
     * @param mobile     手机号
     * @param templateId 短信模板Id
     * @param params     短信模板参数数组
     * @param smsSign    短信签名
     *
     * @return true：发送成功
     */
    public boolean send(String mobile, int templateId, String params[], String smsSign) {
        return send(mobile, templateId, params, defaultNationCode, smsSign, "", "");
    }

    /**
     * 模板短信单发接口
     *
     * @param mobile     手机号
     * @param templateId 短信模板Id
     * @param params     短信模板参数数组
     * @param nationCode 手机区号，例如：中国 “86”
     * @param smsSign    短信签名（为空时，会使用默认签名发送短信）
     * @param ext        扩展信息，腾讯短信服务会原样返回该信息（可选）
     * @param extend     短信码号扩展号，格式为纯数字串，其他格式无效。默认没有开通（可选）
     *
     * @return true：发送成功
     */
    public boolean send(String mobile, int templateId, String params[], String nationCode, String smsSign, String ext, String extend) {
        try {
            if(logger.isDebugEnabled()) {
                logger.debug("【腾讯云-短信服务】发送短信：mobile={},templateId={},params={},nationCode={},smsSign={},ext={},extend={}",
                        mobile, templateId, params, nationCode, smsSign, ext, extend);
            }

            SmsSingleSender sender = new SmsSingleSender(appId, appKey);
            SmsSingleSenderResult result = sender.sendWithParam(nationCode, mobile, templateId, params, smsSign, extend, ext);
            if (logger.isDebugEnabled()) {
                logger.error("腾讯云短信发送结果：result={}", result.toString());
            }
            if (result.result == 0) {
                logger.debug("腾讯云短信发送成功：sid={}", result.sid);
                return true;
            } else {
                logger.error("腾讯云短信发送失败：sid={},errmsg={}", result.sid, result.errMsg);
                return false;
            }
        } catch (Exception e) {
            logger.error("腾讯云短信发送异常", e);
            return false;
        }
    }
}
