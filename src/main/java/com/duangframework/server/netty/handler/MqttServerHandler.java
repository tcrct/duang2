package com.duangframework.server.netty.handler;

import com.duangframework.exception.NettyStartUpException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mqtt.core.IMqttMessageListener;
import com.duangframework.mqtt.core.MqttContext;
import com.duangframework.mqtt.core.MqttOptions;
import com.duangframework.mqtt.core.MqttProts;
import com.duangframework.mqtt.pool.MqttPoolFactory;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @description mqtt消息处理实现类
 * @author binggu
 * @date 2017-03-03
 */
@Sharable
public class MqttServerHandler extends SimpleChannelInboundHandler<Object>
{

    private static final Logger logger = LoggerFactory.getLogger(MqttServerHandler.class);

    public static final MqttFixedHeader CONNACK_HEADER = new MqttFixedHeader(MqttMessageType.CONNACK, false,MqttQoS.AT_LEAST_ONCE,false,0);
    public static final MqttFixedHeader SUBACK_HEADER = new MqttFixedHeader(MqttMessageType.SUBACK, false,MqttQoS.AT_LEAST_ONCE,false,0);
    public static final MqttFixedHeader PUBACK_HEADER = new MqttFixedHeader(MqttMessageType.PUBACK, false,MqttQoS.AT_LEAST_ONCE,false,0);

    private BootStrap bootStrap;

    private final AttributeKey<String> CLIENTID_ATTRIBUTE = AttributeKey.valueOf(ConstEnums.FRAMEWORK_OWNER+"." + ConstEnums.MQTT.CLIENT_ID);

    public static Map<String,Long> unconnectMap=new HashMap<String, Long>();

    // 所有该上报的消息集合   mac+plan
    //    public static Map<Integer,Map<String, UpMessage>> upMap=new ConcurrentHashMap<Integer,Map<String, UpMessage>>();

    // 在线用户与MQTT Context, key为用户ID
    public static Map<String, MqttContext> TERMINAL_ONLINE_MAP = new ConcurrentHashMap<>();

    public MqttServerHandler(BootStrap bootStrap) {
        this.bootStrap = bootStrap;
    }

    @Override
    //连接成功后调用的方法
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = ToolsKit.formatDate(new Date(), ConstEnums.DEFAULT_DATE_FORMAT_VALUE.getValue()) + " connection eqmm is success: " + InetAddress.getLocalHost().getHostName();
        logger.warn("channelActive:" + msg);
        ReturnDto dto = ToolsKit.buildReturnDto(null, msg);
        ctx.writeAndFlush(ToolsKit.toJsonString(dto));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        MqttMessage request = (MqttMessage)obj;
        //处理mqtt消息
        try {
            // 解码成功
            if (request.decoderResult().isSuccess()) {
                switch (request.fixedHeader().messageType()) {
                    case CONNECT:
                        doConnect(ctx, request);
                        return;
                    case SUBSCRIBE:
                        doSubscribe(ctx, request);
                        return;
                    case PUBLISH:
                        doPublish(ctx, request);
                        return;
                    case PINGREQ:
                        doPingreo(ctx, request);
                        return;
                    case PUBACK:
                        doPubAck(ctx, request);
                        return;
                    case PUBREC:
                    case PUBREL:
                    case PUBCOMP:
                    case UNSUBACK:
                        return;
                    case PINGRESP:
                        doPingresp(ctx, request);
                        return;
                    case DISCONNECT:
                        ctx.close();
                        return;
                    case UNSUBSCRIBE:
                        doUnSubscribe(ctx, request);
                        return;
                    default:
                        logger.warn("暂不支持[" + request.fixedHeader().messageType() + "]操作");
                        return;
                }
            }
        } catch (Exception e) {
            throw new NettyStartUpException(e.getMessage(), e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        logger.warn(ctx.channel().remoteAddress().toString().substring(1,ctx.channel().remoteAddress().toString().lastIndexOf(":")) + "is close!");
        //清理用户缓存
        if (ctx.channel().hasAttr(CLIENTID_ATTRIBUTE)) {
            String user = ctx.channel().attr(CLIENTID_ATTRIBUTE).get();
//            userMap.remove(user);
//            userOnlineMap.remove(user);
        }
    }

    /**
     * 超时处理
     * 服务器端 设置超时 ALL_IDLE  <  READER_IDLE ， ALL_IDLE 触发时发送心跳，客户端需响应，
     * 如果客户端没有响应 说明 掉线了 ，然后触发 READER_IDLE ，
     * READER_IDLE 里 关闭链接
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        if (evt instanceof IdleStateEvent)
        {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
            	if (ctx.channel().hasAttr(CLIENTID_ATTRIBUTE)) {
            		String user = ctx.channel().attr(CLIENTID_ATTRIBUTE).get();
            		 logger.warn("ctx heartbeat timeout,close!"+user);//+ctx);
                    logger.warn("ctx heartbeat timeout,close!");//+ctx);
                     if(unconnectMap.containsKey(user)) {
                     	unconnectMap.put(user, unconnectMap.get(user)+1);
                     }else {
                     	unconnectMap.put(user, new Long(1));
                     }
            	}
                ctx.fireChannelInactive();
                ctx.close();
            }else if(event.state().equals(IdleState.ALL_IDLE)) {
            	logger.debug("发送心跳给客户端");
            	buildHearBeat(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 心跳响应
     * @param ctx
     * @param request
     */
    private void doPingreo(ChannelHandlerContext ctx, Object request) {
        MqttMessage mqttMessage = (MqttMessage)request;
        logger.debug("响应心跳！");
        MqttFixedHeader header = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        ctx.writeAndFlush(new MqttMessage(header));
    }

    private void doPingresp(ChannelHandlerContext ctx, Object request)  {
        MqttMessage mqttMessage = (MqttMessage)request;
        logger.debug("收到心跳请求: " + ToolsKit.toJsonString(mqttMessage));
    }

    /**
     * 封装心跳请求
     * @param ctx
     */
    private void buildHearBeat(ChannelHandlerContext ctx) {
        MqttFixedHeader mqttFixedHeader=new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0);
        ctx.writeAndFlush(new MqttMessage(mqttFixedHeader));
    }

    /**
     * 客户端发送取消订阅请求
     * @param ctx
     * @param request
     * @return
     */
    private void doUnSubscribe(ChannelHandlerContext ctx, Object request) {
        MqttUnsubscribeMessage message = (MqttUnsubscribeMessage)request;
        List<String> topicList = message.payload().topics();
        String clientId = ctx.channel().attr(CLIENTID_ATTRIBUTE).get();
        ctx.channel().attr(CLIENTID_ATTRIBUTE).remove();
        for(String topic : topicList) {
            MqttPoolFactory.removeMqttContext(clientId, topic);
        }
        logger.warn(clientId + " unsubscribe success");
    }

    /**
     * 处理连接请求
     * @param ctx
     * @param request
     */
    private void doConnect(ChannelHandlerContext ctx, Object request) {
        MqttConnectMessage message = (MqttConnectMessage)request;
        MqttConnAckVariableHeader variableheader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(CONNACK_HEADER, variableheader);
        MqttConnectPayload connectPayload = message.payload();
        String clientId = connectPayload.clientIdentifier();
        logger.debug("connect ,clientId is :" + clientId);
        //将用户信息写入变量
        if (!ctx.channel().hasAttr(CLIENTID_ATTRIBUTE)) {
            ctx.channel().attr(CLIENTID_ATTRIBUTE).set(clientId);
        }
        //将连接上下文写入缓存
        MqttConnectVariableHeader variableHeader = message.variableHeader();
        String account="";
        String password="";
        if(variableHeader.hasUserName() && variableHeader.hasPassword()) {
            account= connectPayload.userName();
            password = new String(connectPayload.passwordInBytes(), CharsetUtil.UTF_8);
        }
        MqttOptions options = new MqttOptions.Builder()
                .clientId(clientId)
                .account(account)
                .password(password)
                .ports(new MqttProts.Builder().build())
                .qos(MqttQoS.valueOf(variableHeader.willQos()))
                .build();
        options.setCleanSession(variableHeader.isCleanSession());
        options.setKeepAliveTimeSeconds(variableHeader.keepAliveTimeSeconds());
        options.setRetain(variableHeader.isWillRetain());
        options.setWillFlag(variableHeader.isWillFlag());
        options.setVersion(variableHeader.version());
        options.setDup(message.fixedHeader().isDup());
        MqttPoolFactory.setMqttContext(ctx, clientId, "", options);
        ctx.writeAndFlush(connAckMessage);
    }

    /**
     * 处理 客户端订阅消息
     * @param ctx
     * @param request
     */
    private void doSubscribe(ChannelHandlerContext ctx, Object request) {
        MqttSubscribeMessage message = (MqttSubscribeMessage)request;
        int msgId = message.variableHeader().messageId();
        if (msgId <= -1) {
            msgId = 1;
        }
        MqttMessageIdVariableHeader header = MqttMessageIdVariableHeader.from(msgId);
        MqttSubAckPayload payload = new MqttSubAckPayload();
        MqttSubAckMessage suback = new MqttSubAckMessage(SUBACK_HEADER, header, payload);
        ctx.writeAndFlush(suback);
    }

    /**
     * 处理客户端回执消息
     * @param ctx
     * @param request
     */
    private void doPubAck(ChannelHandlerContext ctx, Object request) {
        MqttPubAckMessage message = (MqttPubAckMessage)request;
    }

    /**
     * 处理 客户端发布消息。此处只有终端上报的 指令消息
     * 终端上报 指令执行结果。
     * @param ctx
     * @param request
     */
    private void doPublish(ChannelHandlerContext ctx, Object request) {
        MqttPublishMessage message = (MqttPublishMessage)request;
        ByteBuf buf = message.payload();
        String clientId = ctx.channel().attr(CLIENTID_ATTRIBUTE).get();
        logger.debug("client publish message id："+clientId+"   message："+new String(ByteBufUtil.getBytes(buf)));
        int packetId = message.variableHeader().packetId();
        if (packetId <= -1) {
            packetId = 1;
        }
        //主题, 需要保证唯一性
        String topic = message.variableHeader().topicName();
        // 取出上下文对象
        MqttContext context = MqttPoolFactory.getSubscribeMqttContext(clientId, topic);
        if(ToolsKit.isNotEmpty(context)) {
            IMqttMessageListener<com.duangframework.mqtt.model.MqttMessage> listener = context.getListener();
            if(ToolsKit.isNotEmpty(listener)) {
                com.duangframework.mqtt.model.MqttMessage mqttMessage = new com.duangframework.mqtt.model.MqttMessage();
                try {
                    mqttMessage.setMessage(new String(ByteBufUtil.getBytes(buf), ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
                    MqttFixedHeader mqttFixedHeader = message.fixedHeader();
                    mqttMessage.setMessageId(packetId);
                    mqttMessage.setTopic(topic);
                    mqttMessage.setQos(mqttFixedHeader.qosLevel().value());
                    mqttMessage.setDup(mqttFixedHeader.isDup());
                    mqttMessage.setRetained(mqttFixedHeader.isRetain());
                } catch (Exception e) {
                    logger.warn("doPublish is fail: " + e.getMessage(), e);
                }
                // 回调监听器方法
                listener.messageArrived(mqttMessage);
            }
        }
        if (message.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE) {
            MqttMessageIdVariableHeader header = MqttMessageIdVariableHeader.from(packetId);
            MqttPubAckMessage puback = new MqttPubAckMessage(PUBACK_HEADER, header);
            ctx.write(puback);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn(cause.getMessage(), cause);
        ctx.close();
    }

}
