package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 *  框架签名时异常
 * @author laotang
 * @date 2017/11/2
 */
public class SignException extends AbstractDuangException implements IException {

    public SignException() {
        super();
    }

    public SignException(String message) {
        super(message);
    }

    public SignException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.SIGN_ERROR.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.SIGN_ERROR.getMessage();
        } else {
            return super.getMessage();
        }
    }

}
