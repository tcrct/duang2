package com.duangframework.server.netty;

import com.duangframework.server.common.BootStrap;
import com.duangframework.server.netty.handler.ChannelBaseHandler;
import com.duangframework.server.netty.handler.CorsHandler;
import com.duangframework.server.netty.handler.MqttServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author laotang
 * @date 2017/10/30
 */
public class MqttChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static Logger logger = LoggerFactory.getLogger(MqttChannelInitializer.class);

    private BootStrap bootStrap;
    private SslContext sslContext;

    public MqttChannelInitializer(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipeline = socketChannel.pipeline();
        channelPipeline.addLast(new MqttDecoder(81920));
        channelPipeline.addLast(MqttEncoder.INSTANCE);
//        channelPipeline.addLast("timeout", new IdleStateHandler(30, 0, 20,  TimeUnit.SECONDS));
        // 真正处理业务逻辑的地方,针对每个TCP连接创建一个新的ChannelHandler实例
        channelPipeline.addLast(new MqttServerHandler(bootStrap));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause.getMessage(), cause);
        ctx.fireExceptionCaught(cause);
    }
}
