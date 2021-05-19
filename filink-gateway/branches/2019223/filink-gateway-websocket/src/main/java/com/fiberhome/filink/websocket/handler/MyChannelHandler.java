package com.fiberhome.filink.websocket.handler;

import com.fiberhome.filink.websocket.GlobalWebsocketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.util.Set;

/**
 * websocket通道处理
 *
 * @author yaoyuan
 */
@Slf4j
public class MyChannelHandler extends SimpleChannelInboundHandler<Object> {


    private static final String URI = "websocket";

    /**
     * url处理
     */
    private AntPathMatcher matcher;

    private WebSocketServerHandshaker handshaker ;

    /**
     * 连接上服务器
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("websocket连接上服务器：：通道id为:{}"+ctx.channel().id());
        GlobalWebsocketUtil.channels.add(ctx.channel());
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("websocket断开服务器：通道id为:{}"+ctx.channel().id());
        GlobalWebsocketUtil.channels.remove(ctx);
        Set<String> chennelIds = GlobalWebsocketUtil.channelConcurrentMap.keySet();
        for (String chennelId : chennelIds) {
            Channel channel = GlobalWebsocketUtil.channelConcurrentMap.get(chennelId);
            if (channel == ctx.channel()) {
                GlobalWebsocketUtil.channelConcurrentMap.remove(chennelId, channel);
            }
        }
    }

    /**
     * 连接异常   需要关闭相关资源
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("websocket系统异常"+cause.toString());
        ctx.close();
        ctx.channel().close();
    }

    /**
     * 活跃的通道  也可以当作用户连接上客户端进行使用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("webscoket当前活跃通道"+ctx.channel());
    }

    /**
     * 不活跃的通道  就说明用户失去连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * 这里只要完成 flush
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 这里是保持服务器与客户端长连接  进行心跳检测 避免连接断开
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
             IdleStateEvent stateEvent = (IdleStateEvent) evt;
            PingWebSocketFrame ping = new PingWebSocketFrame();
            switch (stateEvent.state()){
                //读空闲（服务器端）
                case READER_IDLE:
                    log.info("【"+ctx.channel().remoteAddress()+"】读空闲（服务器端）");
                    ctx.writeAndFlush(ping);
                    break;
                    //写空闲（客户端）
                case WRITER_IDLE:
                    log.info("【"+ctx.channel().remoteAddress()+"】写空闲（客户端）");
                    ctx.writeAndFlush(ping);
                    break;
                case ALL_IDLE:
                    log.info("【"+ctx.channel().remoteAddress()+"】读写空闲");
                    break;
                default:
                    log.info("");
                    break;
            }
        }
    }

    /**
     * 收发消息处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            // 传统的HTTP接入
            doHandlerHttpRequest(ctx,(HttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){
            // WebSocket接入
            doHandlerWebSocketFrame(ctx,(WebSocketFrame) msg);
        }
    }


    /**
     * websocket消息处理
     * @param ctx
     * @param msg
     */
    private void doHandlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) {
        //判断msg 是哪一种类型  分别做出不同的反应
        if(msg instanceof CloseWebSocketFrame){
            log.info("【关闭】");
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) msg);
            return ;
        }
        if(msg instanceof PingWebSocketFrame){
            log.info("【ping】");
            PongWebSocketFrame pong = new PongWebSocketFrame(msg.content().retain());
            ctx.channel().writeAndFlush(pong);
            return ;
        }
        if(msg instanceof PongWebSocketFrame){
            PingWebSocketFrame ping = new PingWebSocketFrame(msg.content().retain());
            ctx.channel().writeAndFlush(ping);
            return ;
        }
        if(!(msg instanceof TextWebSocketFrame)){
            log.info("【不支持二进制】");
            throw new UnsupportedOperationException("不支持二进制");
        }
        //可以对消息进行处理
        //群发
        for (Channel channel : GlobalWebsocketUtil.channels) {
            String socketId = (String) channel.attr(AttributeKey.valueOf("socketId")).get();
                channel.writeAndFlush(socketId);
        }

    }

    private static final String WEBSOCKET = "websocket";
    private static final String UPGRADE = "Upgrade";
    private static final int CODE = 200;

    /**
     * wetsocket第一次连接握手
     *
     * 注意：WebSocket连接第一次请求使用的是Http
     * @param ctx
     * @param msg
     */
    private void doHandlerHttpRequest(ChannelHandlerContext ctx, HttpRequest msg) {
        // 如果HTTP解码失败，返回HTTP异常
        if(!msg.getDecoderResult().isSuccess() || (!WEBSOCKET.equals(msg.headers().get(UPGRADE)))){
            sendHttpResponse(ctx,
                    (FullHttpRequest) msg,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
        }

        String uri = msg.getUri();
        String userId = uri.substring(11);

        // 设置唯一连接标识
        ctx.channel().attr(AttributeKey.valueOf("socketId")).set(userId);

        // 正常WebSocket的Http连接请求，构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://" + msg.headers().get(HttpHeaders.Names.HOST), null, false);
        handshaker = wsFactory.newHandshaker(msg);
        // 无法处理的websocket版本
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else { // 向客户端发送websocket握手,完成握手
            handshaker.handshake(ctx.channel(), (FullHttpRequest) msg);
            // 记录管道处理上下文，便于服务器推送数据到客户端
//            this.ctx = ctx;
            GlobalWebsocketUtil.channelConcurrentMap.put(userId, ctx.channel());
        }

//        //可以获取msg的uri来判断
//        String uri = msg.getUri();
////        if(!uri.substring(1).equals(URI)){
////            ctx.close();
////        }
//        ctx.attr(AttributeKey.valueOf("type")).set(uri);
//        //可以通过url获取其他参数
//        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
//                "ws://"+msg.headers().get("Host")+"/"+URI+"",null,false
//        );
//        handshaker = factory.newHandshaker(msg);
//        if(handshaker == null){
//            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
//        }
//        //进行连接
//        handshaker.handshake(ctx.channel(), (FullHttpRequest) msg);
//        //可以做其他处理
    }



    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != CODE) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != CODE) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
