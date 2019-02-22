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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class DuangHeadHandle implements IHandler {

    private static Logger logger = LoggerFactory.getLogger(DuangHeadHandle.class);
    private static String TOKENID_FIELD_NAME;
    private Set<String> FILTER_TARGET_SET = new HashSet<>();

    public DuangHeadHandle() {
        List<String> tmpList = PropKit.getList(ConstEnums.PROPERTIES.FILTER_URI_FIELD.getValue());
        System.err.println("filter tokenId url: " + ToolsKit.toJsonString(tmpList));
        FILTER_TARGET_SET.addAll(tmpList);
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
        // 如果不存在tokenId,则判断该请求URI是否允许访问，如果不允许，则抛出异常返回
        if(ToolsKit.isEmpty(tokenId)) {
            if (!FILTER_TARGET_SET.contains(target)) {
                throw new MvcException("current request[ " + target + " ] is not exist tokenid, access is not allowed so exit...");
            }
        }
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
