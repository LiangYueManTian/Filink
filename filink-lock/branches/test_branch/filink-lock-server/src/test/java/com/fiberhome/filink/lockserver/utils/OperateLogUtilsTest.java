package com.fiberhome.filink.lockserver.utils;

import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.lockserver.bean.Lock;
import com.fiberhome.filink.lockserver.util.OperateLogUtils;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.log.LogCastProcess;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * OperateLogUtilsTest
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/7/1
 */
@RunWith(JMockit.class)
public class OperateLogUtilsTest {

    @Tested
    private OperateLogUtils operateLogUtils;
    @Injectable
    private LogProcess logProcess;
    @Injectable
    private LogCastProcess logCastProcess;
    @Injectable
    private SystemLanguageUtil systemLanguageUtil;


    private DeviceInfoDto deviceInfoDto;

    private XmlParseBean xmlParseBean;

    private XmlParseBean xmlParseBean2;

    private AddLogBean addLogBean;

    private List<Lock> locks;

    private Lock lock;

    List<DeviceInfoDto> deviceInfoList;

    String template = "${optUserName} update the control info of the device:${deviceName}";
    String template2 = "${optUserName} update num : ${slotNum} of the device ${deviceName}";

    @Before
    public void setUp() {
        deviceInfoDto = new DeviceInfoDto();
        deviceInfoDto.setDeviceId("pWtqQjoI7drgDgns5Fb");
        deviceInfoDto.setDeviceName("testpdadevice1243");
        deviceInfoDto.setDeviceType("001");
        deviceInfoDto.setDeviceStatus("1");
        deviceInfoDto.setDeployStatus("6");
        deviceInfoDto.setDeviceCode("41610010431079");
        deviceInfoDto.setAddress("jinitaimei");


        xmlParseBean = new XmlParseBean();
        xmlParseBean.setDetailInfoTemplate(template);
        xmlParseBean.setOptName("optName");

        xmlParseBean2 = new XmlParseBean();
        xmlParseBean2.setDetailInfoTemplate(template2);
        xmlParseBean2.setOptName("optName2");

        addLogBean = new AddLogBean();
        addLogBean.setOptUserName("admin");

        lock = new Lock();
        lock.setControlId("3DFFC3413A3F11E9B3520242AC110004");
        lock.setDoorName("door-002");
        lock.setDoorStatus("1");
        lock.setLockNum("1");
        lock.setLockStatus("1");
        lock.setQrCode("a");
        lock.setDeviceId("3dffc3413a3f4356r3520242ac110003");
        lock.setDoorNum("1");
        lock.setDoorName("门箱1");
        lock.setLockCode("lockewdwdx");

        locks = new ArrayList<>();
        locks.add(lock);

        deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfoDto);
    }

    @Test
    public void addSimpleOperateLog() {
        //正常
        new Expectations() {
            {
                logProcess.generateAddLogToCallParam(anyString);
                result = addLogBean;
                try {

                    logCastProcess.dom4jParseXml(anyString, anyString);
                } catch (Exception r) {

                }
                result = xmlParseBean;

            }
        };
        operateLogUtils.addSimpleOperateLog(deviceInfoDto, "functionCode1", "pda");

        //正常
        new Expectations() {
            {
                logProcess.generateAddLogToCallParam(anyString);
                result = addLogBean;
                try {

                    logCastProcess.dom4jParseXml(anyString, anyString);
                } catch (Exception r) {

                }
                result = new Exception();

            }
        };
        try {
            operateLogUtils.addSimpleOperateLog(deviceInfoDto, "functionCode1", "pda");
        } catch (Exception e) {

        }

    }

    @Test
    public void addMediumOperateLog() {
        //正常
        new Expectations() {
            {
                logProcess.generateAddLogToCallParam(anyString);
                result = addLogBean;
                try {

                    logCastProcess.dom4jParseXml(anyString, anyString);
                } catch (Exception r) {

                }
                result = xmlParseBean2;

            }
        };
        operateLogUtils.addMediumOperateLog(locks, "functionCode1", deviceInfoDto, "pda");

        //异常
        new Expectations() {
            {
                logProcess.generateAddLogToCallParam(anyString);
                result = addLogBean;
                try {

                    logCastProcess.dom4jParseXml(anyString, anyString);
                } catch (Exception r) {

                }
                result = new Exception();

            }
        };
        try {
            operateLogUtils.addMediumOperateLog(locks, "functionCode1", deviceInfoDto, "pda");
        } catch (Exception e) {

        }


    }

    @Test
    public void batchAddOperationLog() {
          operateLogUtils.batchAddOperationLog(deviceInfoList, "functionCode3", "pda");
    }

}
