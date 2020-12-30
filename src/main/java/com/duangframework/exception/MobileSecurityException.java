package com.duangframework.exception;

/**
 * @author zvae
 * @date 2020/3/14 10:14
 */
public class MobileSecurityException extends AbstractDuangException implements IException {
    private IException iException;

    public MobileSecurityException() {
        this(ExceptionEnums.ERROR.getCode(), ExceptionEnums.ERROR.getMessage());
    }

    public MobileSecurityException(String msg) {
        this(ExceptionEnums.ERROR.getCode(), msg);
    }

    public MobileSecurityException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public MobileSecurityException(String msg , Throwable cause) {
        super(msg, cause);
        this.code = ExceptionEnums.ERROR.getCode();
        if (cause instanceof ServiceException){
            this.code = ((ServiceException) cause).getCode();
        }
    }

    public MobileSecurityException(int code, String msg , Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public MobileSecurityException(IException exception) {
        this(exception.getCode(), exception.getMessage());
        this.iException = exception;
    }

    public MobileSecurityException(IException exception , Throwable cause) {
        this(exception.getCode(), exception.getMessage(), cause);
        this.iException = exception;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
