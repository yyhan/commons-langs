package com.cloudin.commons.langs.support.yunpian;

import com.cloudin.commons.langs.URLCodec;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsBatchSend;
import com.yunpian.sdk.model.SmsSingleSend;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 云片短信服务
 *
 * @author 小天
 * @date 2018/6/19 0019 12:05
 */
public class YunpianSmsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private YunpianClient yunpianClient;

    private String apiKey;

    /**
     * 模板短信参数名称前缀
     */
    private String prefixOfTemplateParamName = "#";
    /**
     * 模板短信参数名称后缀
     */
    private String suffixOfTemplateParamName = "#";

    public YunpianSmsService(String apiKey) {
        this.apiKey = apiKey;
    }

    public YunpianSmsService(String apiKey, String prefixOfTemplateParamName, String suffixOfTemplateParamName) {
        this.apiKey = apiKey;
        this.prefixOfTemplateParamName = prefixOfTemplateParamName;
        this.suffixOfTemplateParamName = suffixOfTemplateParamName;
    }

    public void setPrefixOfTemplateParamName(String prefixOfTemplateParamName) {
        this.prefixOfTemplateParamName = prefixOfTemplateParamName;
    }

    public void setSuffixOfTemplateParamName(String suffixOfTemplateParamName) {
        this.suffixOfTemplateParamName = suffixOfTemplateParamName;
    }

    public void init() {
        yunpianClient = new YunpianClient(apiKey).init();
    }

    public void destory() {
        yunpianClient.close();
    }

    /**
     * 模板短信单发接口（不推荐使用）
     *
     * @param mobile         手机号
     * @param templateId     短信模板Id
     * @param templateParams 短信模板参数，参数名请勿包含前缀或后缀
     *
     * @return 是否发送成功
     *
     * @see <a href="https://www.yunpian.com/doc/zh_CN/domestic/tpl_single_send.html">指定模板单发</a>
     */
    public boolean sendSingleTemplateSms(String mobile, int templateId, Map<String, Object> templateParams) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", mobile);
        params.put("tpl_id", "" + templateId);
        params.put("tpl_value", generateTplValue(templateParams));
        Result<SmsSingleSend> r = yunpianClient.sms().tpl_single_send(params);
        if (r.getThrowable() == null) {
            logger.info("【sendSingleTemplateSms】短信发送结果： {}", r);
        } else {
            logger.error("【sendSingleTemplateSms】短信发送结果： " + r.toString(), r.getThrowable());
        }
        return r.isSucc();
    }

    /**
     * 模板短信群发接口
     *
     * @param mobile         手机号，多个手机号以“,”分隔
     * @param templateId     短信模板Id
     * @param templateParams 短信模板参数，参数名请勿包含前缀或后缀
     *
     * @return 是否发送成功
     *
     * @see <a href="https://www.yunpian.com/doc/zh_CN/domestic/tpl_batch_send.html">指定模板群发</a>
     */
    public boolean sendBatchTemplateSms(String mobile, int templateId, Map<String, Object> templateParams) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("mobile", mobile);
        params.put("tpl_id", "" + templateId);
        params.put("tpl_value", generateTplValue(templateParams));
        Result<SmsBatchSend> r = yunpianClient.sms().tpl_batch_send(params);
        if (r.getThrowable() == null) {
            logger.info("【sendBatchTemplateSms】短信发送结果： {}", r);
        } else {
            logger.error("【sendBatchTemplateSms】短信发送结果： " + r.toString(), r.getThrowable());
        }
        return r.isSucc();
    }

    /**
     * 根据模板参数，生成云片要求的格式的字符串
     *
     * @param templateParams 短信模板参数
     *
     * @return 指定格式的字符串
     */
    private String generateTplValue(Map<String, Object> templateParams) {
        if (MapUtils.isNotEmpty(templateParams)) {
            StringBuilder tplValueBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : templateParams.entrySet()) {
                Object v = entry.getValue();
                String k = prefixOfTemplateParamName + entry.getKey() + suffixOfTemplateParamName;
                tplValueBuilder.append(URLCodec.encode(k)).append("=");
                if (v != null) {
                    tplValueBuilder.append(URLCodec.encode(String.valueOf(v)));
                }
                tplValueBuilder.append("&");
            }
            tplValueBuilder.deleteCharAt(tplValueBuilder.length() - 1);
            return tplValueBuilder.toString();
        }
        return "";
    }
}
