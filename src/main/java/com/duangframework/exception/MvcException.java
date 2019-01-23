package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 *  mvc组件运行异常
 * @author laotang
 * @date 2017/11/2
 */
public class MvcException extends AbstractDuangException implements IException {

    public MvcException() {
        super();
    }

    public MvcException(String msg) {
        super(msg);
    }

    public MvcException(String msg , Throwable cause) {
        super(msg, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.MVC_ERROR.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.MVC_ERROR.getMessage();
        } else {
            return super.getMessage();
        }
    }
}
