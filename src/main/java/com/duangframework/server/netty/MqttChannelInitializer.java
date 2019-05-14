package com.duangframework.server.netty;

import com.duangframework.server.common.BootStrap;
import com.duangframework.server.netty.decoder.MqttWebSocketCodec;
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
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngine;
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
//        channelPipeline.addLast("protocol", new WebSocketServerProtocolHandler("/mqtt", "mqtt,mqttv3.1,mqttv3.1.1", true, 65536));
//        channelPipeline.addLast("mqttWebSocket", new MqttWebSocketCodec());
        channelPipeline.addLast(new MqttDecoder(81920));
        channelPipeline.addLast(MqttEncoder.INSTANCE);
//        channelPipeline.addLast("timeout", new IdleStateHandler(30, 0, 20,  TimeUnit.SECONDS));
        // Netty提供的SSL处理
        if (bootStrap.isSslEnabled()) {
            SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
            // 服务端模式
            sslEngine.setUseClientMode(false);
            // 不需要验证客户端
            sslEngine.setNeedClientAuth(false);
            channelPipeline.addLast("ssl", new SslHandler(sslEngine));
        }
        // 真正处理业务逻辑的地方,针对每个TCP连接创建一个新的ChannelHandler实例
        channelPipeline.addLast(new MqttServerHandler(bootStrap));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(cause.getMessage(), cause);
        ctx.fireExceptionCaught(cause);
    }
}
