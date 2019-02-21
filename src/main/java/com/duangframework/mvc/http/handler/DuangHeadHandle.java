package com.duangframework.mvc.http.handler;


import com.duangframework.exception.MvcException;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.enums.ConstEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class DuangHeadHandle implements IHandler {

    private static Logger logger = LoggerFactory.getLogger(DuangHeadHandle.class);
    private static String TOKENID_FIELD_NAME;

    public DuangHeadHandle() {
    }


    @Override
    public void doHandler(String target, IRequest request, IResponse response) throws MvcException {
        if(ToolsKit.isEmpty(TOKENID_FIELD_NAME)) {
            TOKENID_FIELD_NAME = PropKit.get(ConstEnums.PROPERTIES.TOKENID_FIELD.getValue(), ConstEnums.PROPERTIES.TOKENID_FIELD.getValue());
        }
        String tokenId = request.getHeader(TOKENID_FIELD_NAME);
        if(ToolsKit.isEmpty(tokenId)) {
            tokenId = request.getParameter(TOKENID_FIELD_NAME);
        }
        if(ToolsKit.isEmpty(tokenId)) {
            logger.error("current request is not exist tokenid!");
        } else {
            HeadDto headDto = new HeadDto();
            headDto.setClientIp(request.getRemoteIp());
            headDto.setHeaderMap(request.getHeaderMap());
            headDto.setMethod(request.getMethod());
            headDto.setToken(tokenId);
            headDto.setUri(target);
            headDto.setRequestId(request.getRequestId());
            ToolsKit.setThreadLocalDto(headDto);
        }
    }
}
