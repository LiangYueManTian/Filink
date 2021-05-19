package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.lockserver.bean.Control;
import com.fiberhome.filink.lockserver.bean.SetConfigBean;
import com.fiberhome.filink.lockserver.dao.MasterControlDao;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.configuration.LanguageConfig;
import com.fiberhome.filink.server_common.utils.SpringUtils;
import com.fiberhome.filink.stationapi.bean.FiLinkReqParamsDto;
import com.fiberhome.filink.stationapi.feign.FiLinkStationFeign;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * MasterControlServiceImpl测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class MasterControlServiceImplTest {
    /**
     * 主控对象
     */
    private Control control;

    /**
     * 模拟MasterControlServiceImpl
     */
    @Tested
    private MasterControlServiceImpl masterControlService;

    /**
     * 模拟MasterControlDao
     */
    @Injectable
    private MasterControlDao controlDao;

    /**
     * 模拟FiLinkStationFeign
     */
    @Injectable
    private FiLinkStationFeign stationFeign;

    /**
     * 模拟DeviceFeign
     */
    @Injectable
    private DeviceFeign deviceFeign;

    /**
     * 模拟LogProcess
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 模拟LogCastProcess
     */
    @Injectable
    private LogCastProcess logCastProcess;

    /**
     * 初始化主控对象
     */
    @Before
    public void setUp() {
        control = new Control();
        control.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        control.setConfigValue("{\"electricity\":\"20\",\"highTemperature\":\"50\",\"lowTemperature\":\"-1\",\"lean\":\"35\",\"humidity\":\"60\",\"pixel\":\"160x128\",\"heartbeatCycle\":\"5\",\"unlockAlarmCycle\":\"300\",\"exceptionHeartbeatCycle\":\"4\"}");
        control.setSyncStatus("1");
        control.setHardwareVersion("stm32L4-v001");
        control.setSoftwareVersion("RP9003.002G.bin");
    }

    /**
     * 根据设施id查询主控测试方法
     */
    @Test
    public void getControlInfoByDeviceId() {
        masterControlService.getControlInfoByDeviceId("2df750a8377f11e9b3520242ac110003");
    }

    /**
     * 保存主控信息测试方法
     */
    @Test
    public void saveControlParams() {
        masterControlService.saveControlParams(control);
    }

    /**
     * 根据设施id更新主控测试方法
     */
    @Test
    public void updateControlParamsByDeviceId() {
        new Expectations(SpringUtils.class){
            {
                LanguageConfig languageConfig = new LanguageConfig();
                languageConfig.setEnvironment("CN");
                SpringUtils.getBean(LanguageConfig.class);
                result = languageConfig;
            }
        };
        masterControlService.updateControlParamsByDeviceId(control);
    }

    /**
     * 配置设施策略测试方法
     *
     * @throws Exception 异常
     */
    @Test
    public void setConfig() throws Exception{
        //构造配置策略参数实体
        SetConfigBean setConfigBean = new SetConfigBean();
        List<String> deviceIds = new ArrayList<>();
        deviceIds.add("3dffc3413a3f11e9b3520242ac110003");
        Map<String,String> configParamMap = new HashMap<>();
        configParamMap.put("electricity","30");
        configParamMap.put("temperature","43");
        setConfigBean.setConfigParams(configParamMap);
        setConfigBean.setDeviceIds(deviceIds);
        //模拟SpringUtils返回值
        new Expectations(SpringUtils.class){
            {
                LanguageConfig languageConfig = new LanguageConfig();
                languageConfig.setEnvironment("CN");
                SpringUtils.getBean(LanguageConfig.class);
                result = languageConfig;
            }
        };
        new Expectations(){
            //模拟controlDao
            {
                controlDao.getControlCountByDeviceIds(deviceIds);
                result = 1;
            }
            {
                controlDao.batchSetConfig(deviceIds,anyString,anyString);
            }
            //模拟stationFeign
            {
                stationFeign.sendUdpReq((FiLinkReqParamsDto)any);
            }
            //模拟logProcess
            {
                AddLogBean addLogBean = new AddLogBean();
                logProcess.generateAddLogToCallParam(anyString);
                result = addLogBean;
            }
            //模拟logCastProcess
            {
                XmlParseBean xmlParseBean = new XmlParseBean();
                logCastProcess.dom4jParseXml(anyString,anyString);
                result = xmlParseBean;
            }
        };
        masterControlService.setConfig(setConfigBean);
    }
}