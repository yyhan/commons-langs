package com.cloudin.commons.langs.idgenerator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 
 * @author 小天
 * @date 2018/4/6 0006 12:23
*/
public class SimpleIdGenerator implements IdGenerator {

    @Override
    public String newId() {
        return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + RandomStringUtils.random(8, true, true);
    }
}
