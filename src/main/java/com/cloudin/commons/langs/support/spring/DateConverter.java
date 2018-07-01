package com.cloudin.commons.langs.support.spring;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.util.Date;

/**
 * spring 日期转换器
 *
 * @author 小天
 * @date 2018/7/1 10:34
*/
public class DateConverter implements Converter<String, Date> {

    private String[] supportDatePatterns = new String[] {
            "yyyy-MM",
            "yyyy-MM-dd",
            "yyyy-MM-dd hh",
            "yyyy-MM-dd hh:mm",
            "yyyy-MM-dd hh:mm:ss",
    };

    public String[] getSupportDatePatterns() {
        return supportDatePatterns;
    }

    public void setSupportDatePatterns(String[] supportDatePatterns) {
        this.supportDatePatterns = supportDatePatterns;
    }

    @Override
    public Date convert(String source){
        try {
            return DateUtils.parseDate(source, supportDatePatterns);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
