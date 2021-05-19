package com.fiberhome.filink.fdevice.async;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiberhome.filink.bean.WebSocketMessage;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.constant.device.DeviceWebSocketCode;
import com.fiberhome.filink.fdevice.dao.area.AreaDeptInfoDao;
import com.fiberhome.filink.fdevice.dto.HomeDeviceInfoDto;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.DataPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: zl
 * @Date: 2019/4/16 21:42
 * @Description: com.fiberhome.filink.fdevice.websocket
 * @version: 1.0
 */
@Component
public class DeviceInfoAsync {

    /**
     * 自动注入设施消息流接口
     */
    @Autowired
    private DeviceStreams deviceStreams;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private AreaDeptInfoDao areaDeptInfoDao;


    /**
     * 单个设施推送webSocket消息指定用户
     *
     * @param homeDeviceInfoDto
     */
    @Async
    public void sendDeviceMsg(String channelId, String channelKey, HomeDeviceInfoDto homeDeviceInfoDto) {
        List<String> tokenStringList = queryTokensByAreaDevice(homeDeviceInfoDto.getAreaId(), homeDeviceInfoDto.getDeviceType());
        webSocketMsgSend(channelId, channelKey, JSONObject.toJSONString(homeDeviceInfoDto), 999, tokenStringList);
    }


    /**
     * 根据区域ID查询部门集合
     *
     * @param areaId
     * @return
     */
    private List<String> queryDeptInfosByAreaId(String areaId) {
        List<String> areaIdList = new ArrayList<>(Arrays.asList(areaId));
        //查询区域关联的部门
        List<AreaDeptInfo> areaDeptInfos = areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIdList);
        List<String> deptList = new ArrayList<>();
        for (AreaDeptInfo areaDeptInfo : areaDeptInfos) {
            deptList.add(areaDeptInfo.getDeptId());
        }
        return deptList;
    }

    /**
     * 根据区域ID，设施类型，查询对应的在线用户
     *
     * @param areaId
     * @param deviceType
     * @return
     */
    private List<String> queryTokensByAreaDevice(String areaId, String deviceType) {
        //查询设施关联区域
        List<String> deptList = queryDeptInfosByAreaId(areaId);
        //查询指定部门及设施类型的在线token
        DataPermission dataPermission = new DataPermission();
        dataPermission.setDeptList(deptList);
        dataPermission.setDeviceTypes(new ArrayList<>(Arrays.asList(deviceType)));
        List<String> tokenStringList = userFeign.queryDeviceTypeByPermission(dataPermission);
        return tokenStringList;
    }

    /**
     * WebSocket消息推送
     *
     * @param channelId
     * @param channelKey
     * @param msg        推送消息内容
     * @param msgType    消息类型 0-当前 1-全部在线 999-指定token
     * @param tokens
     */
    private void webSocketMsgSend(String channelId, String channelKey, String msg, int msgType, List<String> tokens) {
        WebSocketMessage socketMessage = new WebSocketMessage();
        socketMessage.setChannelId(channelId);
        socketMessage.setChannelKey(channelKey);
        socketMessage.setMsg(msg);
        socketMessage.setMsgType(msgType);
        socketMessage.setKeys(tokens);
        Message<WebSocketMessage> message = MessageBuilder.withPayload(socketMessage).build();
        deviceStreams.deviceWebSocketOutput().send(message);
    }


    /**
     * 批量更新设施的区域发送消息推送
     *
     * @param newAreaId
     * @param homeDeviceInfoDtoList
     */
    @Async
    public void sendAreaDeviceMsg(String newAreaId, String newAreaName, List<HomeDeviceInfoDto> homeDeviceInfoDtoList) {
        for (HomeDeviceInfoDto homeDeviceInfoDto : homeDeviceInfoDtoList) {
            String oldAreaId = homeDeviceInfoDto.getAreaId();
            String oldDeviceType = homeDeviceInfoDto.getDeviceType();
            homeDeviceInfoDto.setAreaId(newAreaId);
            homeDeviceInfoDto.setAreaName(newAreaName);
            sendUpdatingAreaMsg(oldAreaId, oldDeviceType, homeDeviceInfoDto);
        }
    }

    /**
     * 更新区域后发送消息推送
     *
     * @param oriAreaId         更新前区域ID
     * @param oriDeviceType     更新前设施类型
     * @param homeDeviceInfoDto 更新后区域设施信息
     */
    @Async
    public void sendUpdatingAreaMsg(String oriAreaId, String oriDeviceType, HomeDeviceInfoDto homeDeviceInfoDto) {
        List<String> oldTokens = queryTokensByAreaDevice(oriAreaId, oriDeviceType);
        List<String> newTokens = queryTokensByAreaDevice(homeDeviceInfoDto.getAreaId(), homeDeviceInfoDto.getDeviceType());
        //并集
        List<String> intersection = new ArrayList<>();
        for (String oldToken : oldTokens) {
            if (newTokens.contains(oldToken)) {
                intersection.add(oldToken);
                newTokens.remove(oldToken);
            }
        }
        oldTokens.removeAll(intersection);

        //发送webSocket
        List<String> deleteIds = new ArrayList<>(Arrays.asList(homeDeviceInfoDto.getDeviceId()));
        webSocketMsgSend(DeviceWebSocketCode.DELETE_DEVICE_CHANNEL_ID, DeviceWebSocketCode.DELETE_DEVICE_CHANNEL_KEY, JSONObject.toJSONString(deleteIds), 999, oldTokens);
        webSocketMsgSend(DeviceWebSocketCode.ADD_DEVICE_CHANNEL_ID, DeviceWebSocketCode.ADD_DEVICE_CHANNEL_KEY, JSONObject.toJSONString(homeDeviceInfoDto), 999, newTokens);
        webSocketMsgSend(DeviceWebSocketCode.UPDATE_DEVICE_CHANNEL_ID, DeviceWebSocketCode.UPDATE_DEVICE_CHANNEL_KEY, JSONObject.toJSONString(homeDeviceInfoDto), 999, intersection);
    }

    /**
     * 删除设施后发送消息推送
     *
     * @param deviceInfoList 删除的设施
     */
    @Async
    public void sendDeleteDevicesMsg(List<DeviceInfo> deviceInfoList) {
        List<DataPermission> dataPermissionList = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfoList) {
            DataPermission dataPermission = new DataPermission();
            dataPermission.setDeviceId(deviceInfo.getDeviceId());
            dataPermission.setDeviceTypes(new ArrayList<>(Arrays.asList(deviceInfo.getDeviceType())));
            dataPermission.setDeptList(queryDeptInfosByAreaId(deviceInfo.getAreaId()));
            dataPermissionList.add(dataPermission);
        }
        //返回值Map，key为用户token，value为deviceId集合
        Map<String, List<String>> tokenDeviceIdMap = userFeign.queryUserByDevice(dataPermissionList);
        //转换为用户token集合
        Map<List<String>, List<String>> listListMap = convertTokenList(tokenDeviceIdMap);
        for (Map.Entry<List<String>, List<String>> entry : listListMap.entrySet()) {
            webSocketMsgSend(DeviceWebSocketCode.DELETE_DEVICE_CHANNEL_ID,
                    DeviceWebSocketCode.DELETE_DEVICE_CHANNEL_KEY, JSONArray.toJSONString(entry.getValue()),
                    999, entry.getKey());
        }
    }

    /**
     * 合并同相同设施集合的Token
     *
     * @param listMap
     * @return
     */
    private static Map<List<String>, List<String>> convertTokenList(Map<String, List<String>> listMap) {
        Map<List<String>, List<String>> returnListMap = new HashMap<>(64);
        outer:
        for (Map.Entry<String, List<String>> listEntry : listMap.entrySet()) {
            List<String> listEntryValue = listEntry.getValue();
            for (Map.Entry<List<String>, List<String>> returnListEntry : returnListMap.entrySet()) {
                if (listEntryValue.size() == returnListEntry.getValue().size() && listEntryValue.containsAll(returnListEntry.getValue())) {
                    returnListEntry.getKey().add(listEntry.getKey());
                    continue outer;
                }
            }
            List<String> listString = new ArrayList<>();
            listString.add(listEntry.getKey());
            returnListMap.put(listString, listEntry.getValue());
        }
        return returnListMap;
    }

}
