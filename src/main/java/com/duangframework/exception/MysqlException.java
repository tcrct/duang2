package com.duangframework.exception;

import com.duangframework.kit.ToolsKit;

/**
 *  Mysql 执行异常
 * @author laotang
 * @date 2018-9-14
 */
public class MysqlException extends AbstractDuangException implements IException {

    public MysqlException() {
        super();
    }

    public MysqlException(String msg) {
        super(msg);
    }

    public MysqlException(String msg , Throwable cause) {
        super(msg, cause);
    }

    @Override
    public int getCode() {
        return ExceptionEnums.MYSQL_ERROR.getCode();
    }

    @Override
    public String getMessage() {
        if(ToolsKit.isEmpty(super.getMessage())) {
            return ExceptionEnums.MYSQL_ERROR.getMessage();
        } else {
            return super.getMessage();
        }
    }

}
