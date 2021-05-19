package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.device_api.api.DeviceFeign;
import com.fiberhome.filink.device_api.bean.AreaInfo;
import com.fiberhome.filink.device_api.bean.DeviceInfoDto;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.bean.OpenLockBean;
import com.fiberhome.filink.lockserver.dao.LockDao;
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
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * LockServiceImpl测试类
 * @author CongcaiYu
 */
@RunWith(JMockit.class)
public class LockServiceImplTest {
    /**
     * 电子锁对象
     */
    private Lock lock;
    /**
     * 设施信息
     */
    private DeviceInfoDto deviceInfoDto;

    /**
     * 测试对象LockServiceImpl
     */
    @Tested
    private LockServiceImpl lockService;

    /**
     * 模拟对象lockDao
     */
    @Injectable
    private LockDao lockDao;

    /**
     * 模拟对象FiLinkStationFeign
     */
    @Injectable
    private FiLinkStationFeign fiLinkStationFeign;

    /**
     * 模拟对象LogProcess
     */
    @Injectable
    private LogProcess logProcess;

    /**
     * 模拟对象
     */
    @Injectable
    private LogCastProcess logCastProcess;

    /**
     * 模拟对象DeviceFeign
     */
    @Injectable
    private DeviceFeign deviceFeign;


    /**
     * 初始化对象属性
     */
    @Before
    public void setUp() {
        //构造电子锁对象
        lock = new Lock();
        lock.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        lock.setDoorName("door-002");
        lock.setDoorStatus("1");
        lock.setLockNum("1");
        lock.setLockStatus("1");
        //构造设施对象
        //构造device对象
        deviceInfoDto = new DeviceInfoDto();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setAreaName("区域001");
        deviceInfoDto.setAreaInfo(areaInfo);
        deviceInfoDto.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        //构造AddLogBean
        AddLogBean addLogBean = new AddLogBean();
    }

    /**
     * 保存电子锁测试方法
     */
    @Test
    public void saveLockInfo() {
        lockService.saveLockInfo(lock);
    }

    /**
     * 根据设施id查询电子锁信息测试方法
     */
    @Test
    public void queryLockByDeviceId() {
        lockService.queryLockByDeviceId("3dffc3413a3f11e9b3520242ac110003");
    }

    /**
     * 更新电子锁状态测试方法
     */
    @Test
    public void updateLockStatus() {
        lock.setLockId("3dffc3413a3f11e9b3520242ac110003");
        lockService.updateLockStatus(lock);
    }

    /**
     * 批量更新电子锁状态测试方法
     */
    @Test
    public void batchUpdateLockStatus() {
        List<Lock> locks = new ArrayList<>();
        Lock lock1 = new Lock();
        lock1.setLockNum("1");
        lock1.setLockStatus("1");
        lock1.setDoorStatus("2");
        Lock lock2 = new Lock();
        lock2.setLockNum("1");
        lock2.setLockStatus("1");
        lock2.setDoorStatus("2");
        locks.add(lock1);
        locks.add(lock2);
        lockService.batchUpdateLockStatus(locks);
    }

    /**
     * 远程开锁测试方法
     *
     * @throws Exception 异常
     */
    @Test
    public void openLock() throws Exception{
        //构造开锁参数
        OpenLockBean openLockBean = new OpenLockBean();
        openLockBean.setDeviceId("3dffc3413a3f11e9b3520242ac110003");
        List<String> slotNumList = new ArrayList<>();
        slotNumList.add("1");
        slotNumList.add("2");
        openLockBean.setSlotNumList(slotNumList);
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
            {
                //模拟lockDao返回参数
                lockDao.queryLockByDeviceIdAndSlotNum(anyString,anyString);
                result = lock;
            }
            {
                //模拟deviceFeign返回参数
                deviceFeign.getDeviceById(anyString);
                result = deviceInfoDto;
            }
            {
                //模拟fiLinkStationFeign返回参数
                fiLinkStationFeign.sendUdpReq((FiLinkReqParamsDto) any);
                result = ResultUtils.success();
            }
            {
                //模拟logProcess返回参数
                logProcess.generateAddLogToCallParam(anyString);
                result = new AddLogBean();
            }
            {
                //模拟logCastProcess返回值
                logCastProcess.dom4jParseXml(anyString,anyString);
                result = new XmlParseBean();
            }
        };
        lockService.openLock(openLockBean);
    }
}