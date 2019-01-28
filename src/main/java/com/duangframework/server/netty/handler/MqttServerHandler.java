package com.duangframework.server.netty.handler;

import com.duangframework.exception.NettyStartUpException;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mqtt.core.MqttContext;
import com.duangframework.mvc.dto.ReturnDto;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
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

    public static final MqttFixedHeader CONNACK_HEADER = new MqttFixedHeader(MqttMessageType.CONNACK, false,MqttQoS.AT_MOST_ONCE,false,0);
    public static final MqttFixedHeader SUBACK_HEADER = new MqttFixedHeader(MqttMessageType.SUBACK, false,MqttQoS.AT_MOST_ONCE,false,0);
    public static final MqttFixedHeader PUBACK_HEADER = new MqttFixedHeader(MqttMessageType.PUBACK, false,MqttQoS.AT_MOST_ONCE,false,0);

    private BootStrap bootStrap;
    private final AttributeKey<String> USER = AttributeKey.valueOf("user");

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
                        doConnectMessage(ctx, request);
                        return;
                    case SUBSCRIBE:
                        doSubMessage(ctx, request);
                        return;
                    case PUBLISH:
                        doPublishMessage(ctx, request);
                        return;
                    case PINGREQ:
                        doPingreoMessage(ctx, request);
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
                        doPingrespMessage(ctx, request);
                        return;
                    case DISCONNECT:
                        ctx.close();
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
        if (ctx.channel().hasAttr(USER))
        {
            String user = ctx.channel().attr(USER).get();
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
            	if (ctx.channel().hasAttr(USER)) {
            		String user = ctx.channel().attr(USER).get();
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
    private void doPingreoMessage(ChannelHandlerContext ctx, MqttMessage request)
    {
        logger.debug("响应心跳！");
        MqttFixedHeader header = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        ctx.writeAndFlush(new MqttMessage(header));
    }

    private void doPingrespMessage(ChannelHandlerContext ctx, MqttMessage request)  {
        logger.debug("收到心跳请求: " + ToolsKit.toJsonString(request));
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
     * 封装发布
     * @param message
     * @param topicName
     * @return
     */
//    public static MqttPublishMessage buildPublish(String message, String topicName, Integer messageId) {
//        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_LEAST_ONCE, false, message.length());
//        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topicName, messageId);//("MQIsdp",3,false,false,false,0,false,false,60);
//        ByteBuf payload = Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8));
//        MqttPublishMessage msg = new MqttPublishMessage(mqttFixedHeader, variableHeader, payload);
//        return msg;
//    }

    /**
     * 处理连接请求
     * @param ctx
     * @param request
     */
    private void doConnectMessage(ChannelHandlerContext ctx, Object request) {
        MqttConnectMessage message = (MqttConnectMessage)request;
        System.out.println(ToolsKit.toJsonString(message));
        System.out.println(message.payload());
        MqttConnAckVariableHeader variableheader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(CONNACK_HEADER, variableheader);
        //ctx.write(MQEncoder.doEncode(ctx.alloc(),connAckMessage));
        ctx.writeAndFlush(connAckMessage);
        //String user = message.variableHeader().name();
        String stbCode = message.payload().clientIdentifier();
        logger.debug("connect ,stb_code is :" + stbCode);
        //将用户信息写入变量
        if (!ctx.channel().hasAttr(USER))
        {
            ctx.channel().attr(USER).set(stbCode);
        }
        //将连接信息写入缓存
        if(!TERMINAL_ONLINE_MAP.containsKey(stbCode)) {
            MqttContext mqttContext = new MqttContext(ctx, stbCode, bootStrap.getMqttOptions());
            TERMINAL_ONLINE_MAP.put(stbCode, mqttContext);
        }
//        log.debug("the user num is " + userMap.size());

        /**
         * 用户上线时，处理离线消息
         */
//        for (String key : HttpServerHandler.OffLineUserMsgMap.keySet())
//        {
//            if (HttpServerHandler.OffLineUserMsgMap.get(key).contains(stb_code))
//            {
//                MsgToNode msg = HttpServerHandler.messageMap.get(key);
//                SendOfflineMessageThread t = new SendOfflineMessageThread(msg, stb_code);
//                HttpServerHandler.scheduledExecutorService.execute(t);
//            }
//        }
    }

    /**
     * 处理 客户端订阅消息
     * @param ctx
     * @param request
     */
    private void doSubMessage(ChannelHandlerContext ctx, Object request) {
        MqttSubscribeMessage message = (MqttSubscribeMessage)request;
        int msgId = message.variableHeader().messageId();
        if (msgId == -1) {
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
    private void doPubAck(ChannelHandlerContext ctx, Object request)
    {
        MqttPubAckMessage message = (MqttPubAckMessage)request;
//        log.debug(request);
        /* String user = ctx.channel().attr(USER).get();
         Map<String, UpMessage> requestMap=upMap.get(message.variableHeader().messageId());
         if(requestMap!=null&&requestMap.size()>0)
         {
             UpMessage upmessage=requestMap.get(user);
             if(upmessage!=null)
             {
                 upmessage.setStatus(Constants.SENDSUCESS);
                 requestMap.put(user, upmessage);
                 upMap.put(message.variableHeader().messageId(), requestMap);
             }
         }*/
    }

    /**
     * 处理 客户端发布消息。此处只有终端上报的 指令消息
     * 终端上报 指令执行结果。
     * @param ctx
     * @param request
     */
    private void doPublishMessage(ChannelHandlerContext ctx, Object request)
    {
        //        long time = System.currentTimeMillis();
        MqttPublishMessage message = (MqttPublishMessage)request;
        ByteBuf buf = message.payload();
        String msg = new String(ByteBufUtil.getBytes(buf));
        logger.debug("终端消息上报 start，终端编码为："+ctx.channel().attr(USER).get()+" 终端上报消息体："+msg);
        int msgId = message.variableHeader().messageId();
        if (msgId == -1)
            msgId = 1;
        //主题名
        String topicName = message.variableHeader().topicName();
        //test code
    /*    if(topicName.equals("test"))
        {

            MsgToNode msgs=new MsgToNode();
            MsgPublish pub=new MsgPublish();
            pub.setMqttQos(1);
            pub.setMsgPushType(1);
            pub.setMsgPushDst("111");
            msgs.setMsgPublish(pub);

            MsgInfo info=new MsgInfo();
            info.setMsgCode("mm123");
            msgs.setMsgInfo(info);
            SendOnlineMessageThread t = new SendOnlineMessageThread(msgs);
            HttpServerHandler.scheduledExecutorService.execute(t);
        }
        */
//        try
//        {
//            //上报消息写入文件
//            StbReportMsg stbmsg=GsonJsonUtil.fromJson(msg, StbReportMsg.class);
//            //机顶盒编号||消息编号||发送状态||点击状态 ||更新时间||消息应下发用户总数
//            if(!StringUtils.isEmpty(stbmsg.getMsgId()))
//            {
//                UpMessage upmessage=new UpMessage();
//                upmessage.setDeviceId(StringUtils.isEmpty(stbmsg.getDeviceNum())?ctx.channel().attr(USER).get():stbmsg.getDeviceNum());
//                upmessage.setMsgCode(stbmsg.getMsgId());
//                upmessage.setStatus(stbmsg.getStatus());
//                upmessage.setIsOnclick(stbmsg.getJumpFlag());
//                upmessage.setDate(UpMessage.getCurrentDate());
//                upmessage.setMsgType(stbmsg.getMsgType());
//                if(HttpServerHandler.messageMap.containsKey(stbmsg.getMsgId()))
//                {
//                    upmessage.setUserNums(HttpServerHandler.messageMap.get(stbmsg.getMsgId()).getUserNumbers());
//                }
//                log.debug("终端消息上报 end 终端上报消息成功。终端编号："+ctx.channel().attr(USER).get()+" 消息编码："+stbmsg.getMsgId()+"消息状态："+stbmsg.getStatus());
//                HttpServerHandler.reportMsgLog.debug(upmessage.getDeviceId()+"||"+upmessage.getMsgCode()+"||"
//                        +upmessage.getStatus()+"||"+upmessage.getIsOnclick()+"||"+upmessage.getDate()
//                        +"||"+upmessage.getUserNums()+"||"+upmessage.getMsgType());
//            }else
//            {
//                log.error("终端消息上报 end 终端上报消息编码为空！终端编号为: "+ctx.channel().attr(USER).get()+" 上报消息为： "+msg);
//            }
//        }
//        catch (JsonSyntaxException e)
//        {
//            log.error("终端消息上报 end 终端上报消息格式错误！终端编号为: "+ctx.channel().attr(USER).get()+" 上报消息为： "+msg);
//        }
//
//        if (message.fixedHeader().qosLevel() == MqttQoS.AT_LEAST_ONCE)
//        {
//            MqttMessageIdVariableHeader header = MqttMessageIdVariableHeader.from(msgId);
//            MqttPubAckMessage puback = new MqttPubAckMessage(Constants.PUBACK_HEADER, header);
//            ctx.write(puback);
//        }
        msg = null;
        topicName = null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn(cause.getMessage(), cause);
        ctx.close();
    }

//    public static Map<String, ChannelHandlerContext> getUserMap()
//    {
//        return userMap;
//    }
//
//    public static void setUserMap(Map<String, ChannelHandlerContext> userMap)
//    {
//        MQTTServerHandler.userMap = userMap;
//    }

}
