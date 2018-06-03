package com.cloudin.commons.langs.support.aliyun;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 阿里云短信服务类
 *
 * @author 小天
 * @date 2018/6/3 0003 21:40
 * @see <a href="https://help.aliyun.com/document_detail/55284.html?spm=a2c4g.11186623.6.557.oVW3zT">阿里云短信发送API(SendSms) —— JAVA</a>
 */
public class BasicSmsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String CODE_OK = "OK";

    /**
     * 短信API产品名称（短信产品名固定，无需修改）
     */
    private final String product = "Dysmsapi";

    /**
     * 短信API产品域名（接口地址固定，无需修改）
     */
    private final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 建议使用子账户的 accessKeyId
     */
    private String accessKeyId;
    /**
     * 建议使用子账户的 accessKeySecret
     */
    private String accessKeySecret;

    /**
     * 区域Id，例如：cn-hangzhou
     */
    private String regionId;

    /**
     * 短信签名
     */
    private String signName;
    /**
     * 短信模板编号
     */
    private String templateCode;

    /**
     * @param accessKeyId     建议使用子账户的 accessKeyId
     * @param accessKeySecret 建议使用子账户的 accessKeySecret
     * @param regionId        区域Id
     * @param endpointName    坐标名称
     * @param signName        短信签名
     * @param templateCode    短信模板Id
     *
     * @throws Exception 阿里云短信 SDK 初始化异常时，抛出该异常
     */
    public BasicSmsService(String accessKeyId, String accessKeySecret, String regionId, String endpointName, String signName, String templateCode) throws Exception {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.regionId = regionId;
        this.signName = signName;
        this.templateCode = templateCode;

        try {
            DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
        } catch (ClientException e) {
            logger.error("阿里云短信 SDK 初始化异常", e);
            throw new Exception(e);
        }
    }

    /**
     * @param mobile        多个手机号，以半角逗号分隔，最多1000个手机号码。
     * @param templateParam 短信模板参数，{@link SmsCodeTemplateParam}
     *
     * @return true：发送成功
     */
    public boolean send(String mobile, SmsTemplateParam templateParam) {
        return send(mobile, templateParam.getSmsTemplateParamJSON(), null, null);
    }

    /**
     * @param mobile           多个手机号，以半角逗号分隔，最多1000个手机号码。
     * @param templateParamMap 短信模板参数
     *
     * @return true：发送成功
     */
    public boolean send(String mobile, Map<String, String> templateParamMap) {
        return send(mobile, JSON.toJSONString(templateParamMap), null, null);
    }

    /**
     * @param mobile               多个手机号，以半角逗号分隔，最多1000个手机号码。
     * @param templateParamJsonStr 短信模板参数 json 字符串
     *
     * @return true：发送成功
     */
    public boolean send(String mobile, String templateParamJsonStr) {
        return send(mobile, templateParamJsonStr, null, null);
    }

    /**
     * @param mobile               多个手机号，以半角逗号分隔，最多1000个手机号码。
     * @param templateParamJsonStr 短信模板参数 json 字符串
     * @param smsUpExtendCode      上行短信扩展码，无特殊需求时忽略此字段
     * @param smsSendId            短信发送记录Id，可选
     *
     * @return true：发送成功
     */
    protected boolean send(String mobile, String templateParamJsonStr, String smsUpExtendCode, String smsSendId) {

        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);

        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号
        request.setPhoneNumbers(mobile);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(templateParamJsonStr);
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        request.setSmsUpExtendCode(smsUpExtendCode);
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(smsSendId);
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
            if (CODE_OK.equals(sendSmsResponse.getCode())) {
                return true;
            }
        } catch (ClientException e) {
            //请求失败这里会抛 ClientException 异常，视为发送失败
            logger.error("发送短信时出现异常", e);
        }
        return false;
    }


}
