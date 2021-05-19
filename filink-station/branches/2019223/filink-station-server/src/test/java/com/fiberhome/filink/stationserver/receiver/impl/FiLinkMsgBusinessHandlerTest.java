package com.fiberhome.filink.stationserver.receiver.impl;

import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.api.DeviceLogFeign;
import com.fiberhome.filink.device_api.bean.AreaInfo;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.filinklockapi.bean.Control;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.filinklockapi.feign.LockFeign;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResOutputParams;
import com.fiberhome.filink.stationserver.sender.AbstractInstructSender;
import com.fiberhome.filink.stationserver.util.lockenum.CmdId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


/**
 * FiLinkMsgBusinessHandler测试类
 * @author CongcaiYu
 */
@RunWith(MockitoJUnitRunner.class)
public class FiLinkMsgBusinessHandlerTest {

    private DeviceInfoDto deviceInfoDto;

    private FiLinkResOutputParams respOutputParams;

    private Control control;

    /**
     * 测试对象
     */
    @InjectMocks
    private FiLinkMsgBusinessHandler fiLinkMsgBusinessHandler;

    /**
     * 模拟sender
     */
    @Mock
    private AbstractInstructSender sender;

    /**
     * 模拟lockFeign
     */
    @Mock
    private LockFeign lockFeign;

    /**
     * 模拟controlFeign
     */
    @Mock
    private ControlFeign controlFeign;

    /**
     * 模拟deviceLogFeign
     */
    @Mock
    private DeviceLogFeign deviceLogFeign;

    /**
     * 模拟deviceFeign
     */
    @Mock
    private DeviceFeign deviceFeign;

    /**
     * 模拟mongoTemplate
     */
    @Mock
    private MongoTemplate mongoTemplate;

    /**
     * 初始化对象
     */
    @Before
    public void setUp() {
        //构造device对象
        deviceInfoDto = new DeviceInfoDto();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("区域001");
        deviceInfoDto.setAreaInfo(areaInfo);
        deviceInfoDto.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        //构造control对象
        control = new Control();
        control.setDeviceId("09046c433b3111e9b3520242ac110003");
        control.setActualValue("{\"violenceClose\":\"1\",\"shake\":\"0\",\"moduleType\":\"1\",\"lean\":\"46\",\"temperature\":\"22\",\"humidity\":\"41\",\"pryDoor\":\"1\",\"electricity\":\"96\",\"supplyElectricityWay\":\"0\",\"leach\":\"0\",\"wirelessModuleSignal\":\"31\"}");
        //构造params
        respOutputParams = new FiLinkResOutputParams();
        respOutputParams.setDeviceId("5a302d85350811e992ed519d1fc57e29");
    }

    /**
     * 业务处理测试方法
     * @throws Exception 异常信息
     */
    @Test
    public void handleMsg() throws Exception{
        //模拟deviceFeign返回值
        when(deviceFeign.findDeviceBySeriaNumber(anyString())).thenReturn(deviceInfoDto);
        //模拟controlFeign返回值
        when(controlFeign.getControlParams(anyString())).thenReturn(control);
        FiLinkResOutputParams heartBeatOutputParams = new FiLinkResOutputParams();
        //心跳帧
        heartBeatOutputParams.setHeartBeat(true);
        fiLinkMsgBusinessHandler.handleMsg(heartBeatOutputParams);


        //开锁响应帧
        respOutputParams.setCmdId(CmdId.UNLOCK);
        Map<String,Object> unlockRespParams = new HashMap<>();
        List<Map<String,Object>> unlockRespParamList = new ArrayList<>();
        Map<String,Object> unlockRespParam = new HashMap<>();
        unlockRespParam.put("result","success");
        unlockRespParam.put("slotNum","1");
        unlockRespParamList.add(unlockRespParam);
        unlockRespParams.put("params",unlockRespParamList);
        respOutputParams.setParams(unlockRespParams);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //参数上报
        respOutputParams.setCmdId(CmdId.PARAMS_UPLOAD);
        Map<String,Object> uploadParams = new HashMap<>();
        List<Map<String,Object>> uploadParamList = new ArrayList<>();
        Map<String,Object> uploadRespParam = new HashMap<>();
        Map<String,Object> uploadRespAlarmParam = new HashMap<>();
        uploadRespAlarmParam.put("dataClass","humidity");
        uploadRespAlarmParam.put("data","50");
        uploadRespAlarmParam.put("alarmFlag","1");
        uploadRespParam.put("dataClass","notClosed");
        Map<String,String> lockMap = new HashMap<>();
        lockMap.put("1","11");
        lockMap.put("2","12");
        lockMap.put("3","21");
        lockMap.put("4","22");
        uploadRespParam.put("data",lockMap);
        uploadRespParam.put("alarmFlag","1");
        uploadParamList.add(uploadRespParam);
        uploadParamList.add(uploadRespAlarmParam);
        uploadParams.put("params",uploadParamList);
        respOutputParams.setParams(uploadParams);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //激活事件
        respOutputParams.setCmdId(CmdId.ACTIVE);
        Map<String,Object> activeParams = new HashMap<>();
        activeParams.put("softwareVersion","RP9003.002F.bin");
        activeParams.put("hardwareVersion","stm32L4-v001");
        respOutputParams.setParams(activeParams);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //配置设施策略响应帧
        respOutputParams.setCmdId(CmdId.SET_CONFIG);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //休眠事件
        respOutputParams.setCmdId(CmdId.SLEEP);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //开锁上报事件
        respOutputParams.setCmdId(CmdId.OPEN_LOCK_UPLOAD);
        Map<String,Object> openLockParams = new HashMap<>();
        openLockParams.put("lockState","11");
        openLockParams.put("lockNum","1");
        openLockParams.put("lockType","1");
        openLockParams.put("time","123423543");
        respOutputParams.setParams(openLockParams);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //关锁上报事件
        respOutputParams.setCmdId(CmdId.CLOSE_LOCK_UPLOAD);
        Map<String,Object> closeLockParams = new HashMap<>();
        closeLockParams.put("lockState","11");
        closeLockParams.put("lockNum","1");
        closeLockParams.put("lockType","1");
        closeLockParams.put("time","123423543");
        respOutputParams.setParams(closeLockParams);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);

        //箱门变化事件
        respOutputParams.setCmdId(CmdId.DOOR_STATE_CHANGE);
        Map<String,Object> doorParams = new HashMap<>();
        doorParams.put("doorNum","1");
        doorParams.put("doorLockState","11");
        doorParams.put("time","123423543");
        respOutputParams.setParams(doorParams);
        fiLinkMsgBusinessHandler.handleMsg(respOutputParams);
    }
}