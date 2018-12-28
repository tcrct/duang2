package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 *  权限验证时异常
 * @author laotang
 * @date 2017/11/2
 */
public class SecurityException extends AbstractDuangException implements IException {

    public SecurityException() {
        super();
    }

    public SecurityException( String message) {
        super(ExceptionEnums.SECURITY_ERROR.getCode(), message);
    }

    public SecurityException(Integer code, String message) {
        super(code, message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.SECURITY_ERROR.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.SECURITY_ERROR.getMessage();
        } else {
            return super.getMessage();
        }
    }

}
