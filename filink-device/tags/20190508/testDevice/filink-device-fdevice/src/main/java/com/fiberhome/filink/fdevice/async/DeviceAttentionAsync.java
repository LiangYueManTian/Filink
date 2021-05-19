package com.fiberhome.filink.fdevice.async;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.fdevice.dto.DeviceAttentionDto;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import com.fiberhome.filink.userapi.api.UserFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fiberhome.filink.fdevice.constant.device.DeviceConstant.FOCUS_DEVICE;
import static com.fiberhome.filink.fdevice.constant.device.DeviceConstant.UNFOLLOW_DEVICE;

/**
 * <p>
 *
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/26
 */
@Component
public class DeviceAttentionAsync {

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private DeviceStreams deviceStreams;

    /**
     * 关注、取消关注时推送websocket
     *
     * @param type 0-关注 1-取消关注
     */
    @Async
    public void sendAttentionInfo(String type, DeviceAttentionDto deviceAttentionDto) {
        WebSocketMessage socketMessage = new WebSocketMessage();
        if (FOCUS_DEVICE.equals(type)) {
            socketMessage.setChannelKey("focusDevice");
        } else if (UNFOLLOW_DEVICE.equals(type)) {
            socketMessage.setChannelKey("unFollowDevice");
        }
        //向指定的token推送
        socketMessage.setMsgType(999);
        socketMessage.setMsg(JSON.toJSONString(deviceAttentionDto));
        //取得当前userId的所有token
        List<String> tokenList = userFeign.queryTokenByUserId(deviceAttentionDto.getUserId());
        if (tokenList != null && tokenList.size() > 0) {
            socketMessage.setKeys(tokenList);
            Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
            deviceStreams.deviceWebSocketOutput().send(message);

        }

    }

}
