package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 *  MongoDB执行异常
 * @author laotang
 * @date 2017/11/2
 */
public class MongodbException extends AbstractDuangException implements IException {

    public MongodbException() {
        super();
    }

    public MongodbException(String msg) {
        super(msg);
    }

    public MongodbException(String msg , Throwable cause) {
        super(msg, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.MONGODB_ERROR.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.MONGODB_ERROR.getMessage();
        } else {
            return ExceptionEnums.MONGODB_ERROR.getMessage() + ": " + super.getMessage();
        }
    }

}