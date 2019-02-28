package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 *  Token异常
 * @author laotang
 * @date 2017/11/2
 */
public class TokenException extends AbstractDuangException implements IException {

    public TokenException() {
        super();
    }

    public TokenException(String msg) {
        super(msg);
    }

    public TokenException(String msg , Throwable cause) {
        super(msg, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.TOKEN_EXPIRE.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.TOKEN_EXPIRE.getMessage();
        } else {
            return super.getMessage();
        }
    }
}
