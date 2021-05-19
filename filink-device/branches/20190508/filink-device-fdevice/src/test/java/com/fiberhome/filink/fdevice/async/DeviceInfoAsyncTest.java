package com.fiberhome.filink.fdevice.async;

import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.DevicePicFeign;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;
import com.fiberhome.filink.fdevice.bean.device.DeviceInfo;
import com.fiberhome.filink.fdevice.bean.device.RetryDeleteInfo;
import com.fiberhome.filink.fdevice.dao.area.AreaDeptInfoDao;
import com.fiberhome.filink.fdevice.dao.device.DeviceCollectingDao;
import com.fiberhome.filink.fdevice.dao.device.RetryDeleteInfoDao;
import com.fiberhome.filink.fdevice.dto.HomeDeviceInfoDto;
import com.fiberhome.filink.fdevice.stream.DeviceStreams;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.DataPermission;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import com.fiberhome.filink.workflowbusinessapi.req.inspectiontask.DeleteInspectionTaskForDeviceReq;
import com.fiberhome.filink.workflowbusinessapi.req.procbase.DeleteProcBaseForDeviceReq;
import mockit.Expectations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * DeviceInfoAsyncTest
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/9
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceInfoAsyncTest {

    @InjectMocks
    private DeviceInfoAsync deviceInfoAsync;
    @Mock
    private DeviceStreams deviceStreams;
    @Mock
    private UserFeign userFeign;
    @Mock
    private AreaDeptInfoDao areaDeptInfoDao;
    @Mock
    private ProcBaseFeign procBaseFeign;
    @Mock
    private RetryDeleteInfoDao retryDeleteInfoDao;
    @Mock
    private DeviceCollectingDao deviceCollectingDao;
    @Mock
    private InspectionTaskFeign inspectionTaskFeign;
    @Mock
    private AlarmCurrentFeign alarmCurrentFeign;
    @Mock
    private DevicePicFeign devicePicFeign;

    @Test
    public void sendDeviceMsg() {
        String channelId = "channelId";
        String channelKey = "channelKey";
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setAreaId("wuhan");
        homeDeviceInfoDto.setDeviceType("001");
        queryTokensByAreaDevice(channelId, channelKey);
        webSocketMsgSend();
        deviceInfoAsync.sendDeviceMsg(channelId, channelKey, homeDeviceInfoDto);


    }

    @Test
    public void sendAreaDeviceMsg() {
        String newAreaId = "huBei";
        String newAreaName = "wuhan";
        List<HomeDeviceInfoDto> homeDeviceInfoDtoList = new ArrayList<>();
        HomeDeviceInfoDto homeDeviceInfoDto = new HomeDeviceInfoDto();
        homeDeviceInfoDto.setAreaId("wuhan");
        homeDeviceInfoDto.setDeviceType("030");
        homeDeviceInfoDtoList.add(homeDeviceInfoDto);
        queryTokensByAreaDevice("wuhan", "030");
        queryTokensByAreaDevice("huBei", "030");
        webSocketMsgSend();
        deviceInfoAsync.sendAreaDeviceMsg(newAreaId, newAreaName, homeDeviceInfoDtoList);

    }

    @Test
    public void sendUpdatingAreaMsg() {


    }

    @Test
    public void sendDeleteDevicesMsg() {
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("deviceId");
        deviceInfo.setDeviceType("001");
        deviceInfo.setAreaId("wuhan");
        deviceInfoList.add(deviceInfo);

        queryTokensByAreaDevice("wuhan", "001");
        Map<String, List<String>> tokenDeviceIdMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("xx");
        list.add("vv");
        tokenDeviceIdMap.put("cc", list);
        when(userFeign.queryUserByDevice(any())).thenReturn(tokenDeviceIdMap);
        webSocketMsgSend();
        deviceInfoAsync.sendDeleteDevicesMsg(deviceInfoList);
    }

    @Test
    public void deleteProcBaseForDevice() {
        List<String> idList = new ArrayList<>();
        RetryDeleteInfo retryDeleteInfo = new RetryDeleteInfo();
        DeleteProcBaseForDeviceReq deleteProcBaseForDeviceReq = new DeleteProcBaseForDeviceReq();
        deleteProcBaseForDeviceReq.setDeviceIdList(idList);
        when(procBaseFeign.deleteProcBaseForDeviceList(deleteProcBaseForDeviceReq)).thenThrow(new RuntimeException());
        deviceInfoAsync.deleteProcBaseForDevice(idList, retryDeleteInfo);
        deviceInfoAsync.deleteProcBaseForDevice(idList, null);

        Result result = new Result();
        when(procBaseFeign.deleteProcBaseForDeviceList(any())).thenReturn(result);
        deviceInfoAsync.deleteProcBaseForDevice(idList, retryDeleteInfo);

    }

    @Test
    public void deleteInspectionTask() {
        List<String> idList = new ArrayList<>();
        RetryDeleteInfo retryDeleteInfo = new RetryDeleteInfo();
        DeleteInspectionTaskForDeviceReq deleteInspectionTaskForDeviceReq = new DeleteInspectionTaskForDeviceReq();
        deleteInspectionTaskForDeviceReq.setDeviceIdList(idList);
        when(inspectionTaskFeign.deleteInspectionTaskForDeviceList(deleteInspectionTaskForDeviceReq)).thenThrow(new RuntimeException());
        deviceInfoAsync.deleteInspectionTask(idList, retryDeleteInfo);
        deviceInfoAsync.deleteInspectionTask(idList, null);

        Result result = new Result();
        when(inspectionTaskFeign.deleteInspectionTaskForDeviceList(any())).thenReturn(result);
        deviceInfoAsync.deleteInspectionTask(idList, retryDeleteInfo);


    }

    @Test
    public void deleteAlarmCurrent() {
        List<String> idList = new ArrayList<>();
        RetryDeleteInfo retryDeleteInfo = new RetryDeleteInfo();
        deviceInfoAsync.deleteAlarmCurrent(idList, retryDeleteInfo);
        deviceInfoAsync.deleteAlarmCurrent(idList, null);
        when(alarmCurrentFeign.batchDeleteAlarmsFeign(idList)).thenReturn(new Result());
        deviceInfoAsync.deleteAlarmCurrent(idList, retryDeleteInfo);
    }

    @Test
    public void deleteDevicePic() {
        List<String> idList = new ArrayList<>();
        RetryDeleteInfo retryDeleteInfo = new RetryDeleteInfo();
        deviceInfoAsync.deleteDevicePic(idList, retryDeleteInfo);
        deviceInfoAsync.deleteDevicePic(idList, null);
        when(devicePicFeign.deleteImageByDeviceIds(new HashSet<>(idList))).thenReturn(new Result());
        deviceInfoAsync.deleteDevicePic(idList, retryDeleteInfo);

    }

    @Test
    public void deleteCollecting() {
        List<String> idList = new ArrayList<>();
        RetryDeleteInfo retryDeleteInfo = new RetryDeleteInfo();
        deviceInfoAsync.deleteCollecting(idList, retryDeleteInfo);
        new Expectations() {
            {
                deviceCollectingDao.deleteAttentionByDeviceIds(idList);
                result = new RuntimeException();
            }
        };
        deviceInfoAsync.deleteCollecting(idList, null);
        deviceInfoAsync.deleteCollecting(idList, retryDeleteInfo);


    }


    private void webSocketMsgSend() {
        MessageChannel messageChannel = new MessageChannel() {
            @Override
            public boolean send(Message<?> message) {
                return false;
            }

            @Override
            public boolean send(Message<?> message, long timeout) {
                return true;
            }
        };
        when(deviceStreams.deviceWebSocketOutput()).thenReturn(messageChannel);
    }

    private void queryTokensByAreaDevice(String areaId, String deviceType) {
        AreaDeptInfo areaDeptInfo = new AreaDeptInfo();
        areaDeptInfo.setAreaId(areaId);
        areaDeptInfo.setDeptId(areaId);
        List<AreaDeptInfo> areaDeptInfos = new ArrayList<>();
        areaDeptInfos.add(areaDeptInfo);

        List<String> areaIdList = new ArrayList<>(Arrays.asList(areaId));
        when(areaDeptInfoDao.selectAreaDeptInfoByAreaIds(areaIdList)).thenReturn(areaDeptInfos);

        List<String> tokenStringList = new ArrayList<>();
        tokenStringList.add(areaId);
        List<String> deptList = new ArrayList<>();
        deptList.add(areaId);
        DataPermission dataPermission = new DataPermission();
        dataPermission.setDeptList(deptList);
        dataPermission.setDeviceTypes(new ArrayList<>(Arrays.asList(deviceType)));
        when(userFeign.queryDeviceTypeByPermission(dataPermission)).thenReturn(tokenStringList);
    }

}