package com.cloudin.commons.langs.support.aliyun;

/**
 * 短信模板参数
 *
 * @author 小天
 * @date 2018/6/3 0003 22:03
 */
public class SmsCodeTemplateParam implements SmsTemplateParam {

    private String code;

    public SmsCodeTemplateParam(String code) {
        this.code = code;
    }

    @Override
    public String getSmsTemplateParamJSON() {
        return "{\"code\":\"" + code + "\"}";
    }
}
