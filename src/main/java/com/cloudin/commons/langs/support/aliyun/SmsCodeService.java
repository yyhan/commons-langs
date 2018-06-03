package com.cloudin.commons.langs.support.aliyun;

/**
 * 短信验证码服务
 *
 * <p>
 *    短信模板示例：您的验证码${code}，该验证码5分钟内有效，请勿泄漏于他人！
 * </p>
 *
 * @author 小天
 * @date 2018/6/3 0003 22:06
 */
public class SmsCodeService extends BasicSmsService {

    public SmsCodeService(String accessKeyId, String accessKeySecret, String regionId, String endpointName, String signName, String templateCode) throws Exception {
        super(accessKeyId, accessKeySecret, regionId, endpointName, signName, templateCode);
    }

    /**
     * 发送手机短信验证码
     *
     * @param mobile 手机号
     * @param code   验证码
     *
     * @return true：发送成功
     */
    @Override
    public boolean send(String mobile, String code) {
        return send(mobile, new SmsCodeTemplateParam(code));
    }
}
