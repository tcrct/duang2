package com.duangframework.mvc.http.handler;


import com.duangframework.exception.MvcException;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.dto.HeadDto;
import com.duangframework.mvc.http.IRequest;
import com.duangframework.mvc.http.IResponse;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.utils.WebKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 检验该请求是否存在tokenId参数
 * 如果存在则验证，验证通过后继续执行后续代码
 * 如果不存在，则判断该请求URI是否允许的，如果是属于不允许不存在的URI，则抛出异常结束请求访问
 *
 * @author Created by laotang
 * @date createed in 2018/6/8.
 */
public class DuangHeadHandle implements IHandler {

    public static final Set<String> FILTER_TARGET_SET = new HashSet<>();

    public DuangHeadHandle() {
        List<String> tmpList = PropKit.getList(ConstEnums.PROPERTIES.FILTER_URI_FIELD.getValue());
        System.err.println("filter tokenId url: " + ToolsKit.toJsonString(tmpList));
        FILTER_TARGET_SET.addAll(tmpList);
    }


    @Override
    public void doHandler(String target, IRequest request, IResponse response) throws MvcException {
       String tokenId = WebKit.getRequestTokenId(request);
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
        headDto.setTokenId(tokenId);
        headDto.setUri(target);
        headDto.setRequestId(request.getRequestId());
        ToolsKit.setThreadLocalDto(headDto);
    }
}
