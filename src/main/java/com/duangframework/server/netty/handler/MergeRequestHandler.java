/**
 * Copyright (c) 2018, biezhi 王爵 nice (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.duangframework.server.netty.handler;

import com.duangframework.mvc.http.HttpRequest;
import com.duangframework.server.common.BootStrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;


import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Merge Netty HttpObject as {@link HttpRequest}
 *
 * @author laotang
 * @date 2019/8/15
 */
public class MergeRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpRequest httpRequest;
    private BootStrap bootStrap;

    public MergeRequestHandler (BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            httpRequest = HttpRequest.build(ctx, (io.netty.handler.codec.http.HttpRequest)msg);
            return;
        }
        if (null != httpRequest && msg instanceof HttpContent) {
            httpRequest.appendContent((HttpContent) msg);
        }
        if (msg instanceof LastHttpContent) {
            if (null != httpRequest) {
                ctx.fireChannelRead(httpRequest);
            } else {
                ctx.fireChannelRead(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}