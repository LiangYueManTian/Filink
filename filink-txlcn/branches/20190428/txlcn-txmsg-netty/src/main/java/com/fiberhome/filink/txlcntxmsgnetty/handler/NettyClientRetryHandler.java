/*
 * Copyright 2017-2019 CodingApi .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fiberhome.filink.txlcntxmsgnetty.handler;

import com.fiberhome.filink.txlcncommon.util.id.RandomUtils;
import com.fiberhome.filink.txlcntxmsg.MessageConstants;
import com.fiberhome.filink.txlcntxmsg.dto.MessageDto;
import com.fiberhome.filink.txlcntxmsg.listener.ClientInitCallBack;
import com.fiberhome.filink.txlcntxmsgnetty.bean.NettyRpcCmd;
import com.fiberhome.filink.txlcntxmsgnetty.bean.SocketManager;
import com.fiberhome.filink.txlcntxmsgnetty.impl.NettyContext;
import com.fiberhome.filink.txlcntxmsgnetty.impl.NettyRpcClientInitializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.List;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/21
 *
 * @author codingapi
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyClientRetryHandler extends ChannelInboundHandlerAdapter {
    private final ClientInitCallBack clientInitCallBack;

    private int keepSize;

    private NettyRpcCmd heartCmd;

    @Autowired
    public NettyClientRetryHandler(ClientInitCallBack clientInitCallBack) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new NettyRpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(RandomUtils.simpleKey());
        this.clientInitCallBack = clientInitCallBack;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        keepSize = NettyContext.currentParam(List.class).size();

        clientInitCallBack.connected(ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("keepSize:{},nowSize:{}", keepSize, SocketManager.getInstance().currentSize());

        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.error("socketAddress:{} ", socketAddress);
        NettyRpcClientInitializer.reConnect(socketAddress);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientRetryHandler - exception . ", cause);

        if (cause instanceof ConnectException) {
            int size = SocketManager.getInstance().currentSize();
            Thread.sleep(1000 * 15);
            log.error("current size:{}  ", size);
            log.error("try connect tx-manager:{} ", ctx.channel().remoteAddress());
            NettyRpcClientInitializer.reConnect(ctx.channel().remoteAddress());
        }
        //???????????????????????????????????????.
        ctx.writeAndFlush(heartCmd);

    }
}
