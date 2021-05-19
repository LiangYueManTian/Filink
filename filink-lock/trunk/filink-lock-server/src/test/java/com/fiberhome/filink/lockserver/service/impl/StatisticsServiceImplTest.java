package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoBase;
import com.fiberhome.filink.deviceapi.bean.DeviceParam;
import com.fiberhome.filink.lockserver.bean.*;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.mongodb.DBObject;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.bson.BSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class StatisticsServiceImplTest {

    @Tested
    private StatisticsServiceImpl statisticsService;

    @Injectable
    private MongoTemplate mongoTemplate;

    @Injectable
    private ControlService controlService;

    @Injectable
    private DeviceFeign deviceFeign;

    @Injectable
    private Integer expiredDay;

    @Before
    public void setUp() {
        expiredDay = 1;
    }

    @Test
    public void queryDeviceSensorValues() {
        SensorStatReq sensorStatReq = new SensorStatReq();
        sensorStatReq.setDeviceId("deviceId");
        sensorStatReq.setStartTime(1341343241L);
        sensorStatReq.setEndTime(1341344241L);
        List<ControlParam> controlInfoByDeviceId = new ArrayList<>();
        ControlParam controlParam = new ControlParam();
        controlParam.setDeviceId("deviceId");
        controlParam.setHostName("test");
        controlParam.setHostId("213");
        controlInfoByDeviceId.add(controlParam);
        List<Sensor> deviceLogList = new ArrayList<>();
        Sensor sensor = new Sensor();
        sensor.setLeach(12);
        sensor.setLean(15);
        sensor.setElectricity(25);
        sensor.setHumidity(12);
        sensor.setTemperature(22);
        deviceLogList.add(sensor);
        new Expectations() {
            {
                controlService.getControlInfoByDeviceId(anyString);
                result = controlInfoByDeviceId;
            }

            {
                mongoTemplate.find((Query) any, Sensor.class);
                result = deviceLogList;
            }
        };
        statisticsService.queryDeviceSensorValues(sensorStatReq);
    }

    @Test
    public void queryDeviceSensorTopNum() {
        //areaList为空
        SensorTopNumReq sensorTopNumReq = new SensorTopNumReq();
        sensorTopNumReq.setSensorType("1");
        sensorTopNumReq.setDeviceType("001");
        sensorTopNumReq.setTopTotal(1);
        statisticsService.queryDeviceSensorTopNum(sensorTopNumReq);
        //areaList不为空
        List<String> areaList = new ArrayList<>();
        areaList.add("3242143");
        areaList.add("24214324");
        sensorTopNumReq.setAreaIdList(areaList);
        List<DeviceInfoBase> deviceInfoBaseList = new ArrayList<>();
        DeviceInfoBase deviceInfoBase = new DeviceInfoBase();
        deviceInfoBase.setAreaId("234");
        deviceInfoBase.setDeviceId("32424");
        deviceInfoBase.setDeviceName("test");
        deviceInfoBaseList.add(deviceInfoBase);
        AggregationResults<SensorTopNum> results = new AggregationResults(new ArrayList(), new DBObject() {
            @Override
            public void markAsPartialObject() {

            }

            @Override
            public boolean isPartialObject() {
                return false;
            }

            @Override
            public Object put(String s, Object o) {
                return null;
            }

            @Override
            public void putAll(BSONObject bsonObject) {

            }

            @Override
            public void putAll(Map map) {

            }

            @Override
            public Object get(String s) {
                return null;
            }

            @Override
            public Map toMap() {
                return null;
            }

            @Override
            public Object removeField(String s) {
                return null;
            }

            @Override
            public boolean containsKey(String s) {
                return false;
            }

            @Override
            public boolean containsField(String s) {
                return false;
            }

            @Override
            public Set<String> keySet() {
                return null;
            }
        });
        new MockUp<AggregationResults>() {
            @Mock
            public List<SensorTopNum> getMappedResults() {
                SensorTopNum sensorTopNum = new SensorTopNum();
                sensorTopNum.setDeviceId("123");
                sensorTopNum.setDeviceName("test");
                sensorTopNum.setSensorType("1");
                sensorTopNum.setRowNum(1);
                sensorTopNum.setSensorValue("213");
                List<SensorTopNum> topMapList = new ArrayList<>();
                topMapList.add(sensorTopNum);
                return topMapList;
            }
        };
        new Expectations() {
            {
                deviceFeign.queryDeviceInfoBaseByParam((DeviceParam) any);
                result = deviceInfoBaseList;
            }

            {
                mongoTemplate.aggregate((Aggregation) any, "sensor", SensorTopNum.class);
                result = results;
            }
        };
        statisticsService.queryDeviceSensorTopNum(sensorTopNumReq);
    }

    @Test
    public void deleteExpiredData() {
        statisticsService.deleteExpiredData();
    }
}