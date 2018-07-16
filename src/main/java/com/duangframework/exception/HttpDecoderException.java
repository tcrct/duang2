package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 * 对netty请求对象解码时异常
 * Created by laotang on 2018/6/9.
 */
public class HttpDecoderException extends AbstractDaggerException implements IException {

    public HttpDecoderException() {
        super();
    }

    public HttpDecoderException(String msg) {
        super(msg);
    }

    public HttpDecoderException(String msg , Throwable cause) {
        super(msg, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.TRANSFORMEXCEPTION.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.TRANSFORMEXCEPTION.getMessage();
        } else {
            return ExceptionEnums.TRANSFORMEXCEPTION.getMessage() + ": " + super.getMessage();
        }
    }
}


