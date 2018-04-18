package com.cloudin.commons.langs.ex;

/**
 * @author 小天
 * @date 2018/4/6 0006 12:16
 */
public class BaseException extends Exception {

    private Integer code;

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
