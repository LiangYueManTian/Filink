package com.fiberhome.filink.lockserver.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoBase;
import com.fiberhome.filink.deviceapi.bean.DeviceParam;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.Sensor;
import com.fiberhome.filink.lockserver.bean.SensorStatReq;
import com.fiberhome.filink.lockserver.bean.SensorTopNum;
import com.fiberhome.filink.lockserver.bean.SensorTopNumReq;
import com.fiberhome.filink.lockserver.constant.control.ControlI18n;
import com.fiberhome.filink.lockserver.constant.control.ControlResultCode;
import com.fiberhome.filink.lockserver.service.ControlService;
import com.fiberhome.filink.lockserver.service.StatisticsService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: zhaoliang
 * @Date: 2019/5/31 15:58
 * @Description: com.fiberhome.filink.lockserver.service.impl
 * @version: 1.0
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ControlService controlService;

    @Autowired
    private DeviceFeign deviceFeign;
    /**
     * ??????????????????
     */
    @Value("${expiredDay}")
    private Integer expiredDay;

    /**
     * ????????????????????????
     *
     * @param sensorStatReq
     * @return
     */
    @Override
    public Result queryDeviceSensorValues(SensorStatReq sensorStatReq) {
        //????????????
        if (sensorStatReq == null || ObjectUtils.isEmpty(sensorStatReq.getDeviceId())
                || sensorStatReq.getStartTime() == null || sensorStatReq.getEndTime() == null) {
            return ResultUtils.warn(ControlResultCode.PARAMETER_ERROR,
                    I18nUtils.getSystemString(ControlI18n.PARAMETER_ERROR));
        }

        //??????????????????
        List<ControlParam> controlInfoByDeviceId = controlService.getControlInfoByDeviceId(sensorStatReq.getDeviceId());
        if (ObjectUtils.isEmpty(controlInfoByDeviceId)) {
            return ResultUtils.success(new HashMap<>(0));
        }
        //?????????Map
        Map<String, String> controlNameMap = new HashMap<>(64);
        for (ControlParam controlParam : controlInfoByDeviceId) {
            controlNameMap.put(controlParam.getHostId(), controlParam.getHostName());
        }

        //?????????????????????
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("deviceId", sensorStatReq.getDeviceId());
        dbObject.put("currentTime", new BasicDBObject("$gte", sensorStatReq.getStartTime())
                .append("$lte", sensorStatReq.getEndTime()));

        BasicDBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("controlId", true);
        fieldsObject.put("currentTime", true);
        if (StringUtils.isEmpty(sensorStatReq.getSensorType())) {
            fieldsObject.put("temperature", true);
            fieldsObject.put("humidity", true);
            fieldsObject.put("electricity", true);
            fieldsObject.put("lean", true);
            fieldsObject.put("leach", true);
        } else {
            fieldsObject.put(sensorStatReq.getSensorType(), true);
        }

        Query query = new BasicQuery(dbObject, fieldsObject);
        query.with(new Sort(Sort.Direction.ASC, "currentTime"));
        List<Sensor> deviceLogList = mongoTemplate.find(query, Sensor.class);

        //???????????????
        for (Sensor sensor : deviceLogList) {
            sensor.setControlName(controlNameMap.get(sensor.getControlId()));
            sensor.setControlId(null);
            sensor.setSensorId(null);
        }
        //?????????
        Map<String, List<Sensor>> sensorMap = new HashMap<>(32);
        for (Sensor sensor : deviceLogList) {
            if (sensorMap.containsKey(sensor.getControlName())) {
                sensorMap.get(sensor.getControlName()).add(sensor);
                sensor.setControlName(null);
            } else {
                List<Sensor> sensorList = new ArrayList<>();
                sensorList.add(sensor);
                sensorMap.put(sensor.getControlName(), sensorList);
                sensor.setControlName(null);
            }
        }
        return ResultUtils.success(sensorMap);
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param sensorTopNumReq
     * @return
     */
    @Override
    public Result queryDeviceSensorTopNum(SensorTopNumReq sensorTopNumReq) {
        //????????????
        if (sensorTopNumReq == null || ObjectUtils.isEmpty(sensorTopNumReq.getSensorType())
                || sensorTopNumReq.getAreaIdList() == null || ObjectUtils.isEmpty(sensorTopNumReq.getDeviceType())
                || sensorTopNumReq.getTopTotal() == null) {
            return ResultUtils.warn(ControlResultCode.PARAMETER_ERROR,
                    I18nUtils.getSystemString(ControlI18n.PARAMETER_ERROR));
        }
        //???????????????topNum???0????????????
        if (sensorTopNumReq.getAreaIdList().size() == 0 || sensorTopNumReq.getTopTotal() == 0) {
            return ResultUtils.success(new HashMap<>(0));
        }

        //??????????????????????????????????????????
        List<DeviceInfoBase> deviceInfoBaseList = queryDeviceInfoBase(sensorTopNumReq);
        if (ObjectUtils.isEmpty(deviceInfoBaseList)) {
            return ResultUtils.success(new HashMap<>(0));
        }

        List<String> deviceIdList = new ArrayList<>();
        Map<String, DeviceInfoBase> deviceMap = new HashMap<>(64);
        for (DeviceInfoBase deviceInfoBase : deviceInfoBaseList) {
            deviceIdList.add(deviceInfoBase.getDeviceId());
            deviceMap.put(deviceInfoBase.getDeviceId(), deviceInfoBase);
        }

        //??????????????????
        Criteria criteria = Criteria.where("deviceId").in(deviceIdList);
        //???????????????
        GroupOperation groupOperation = Aggregation.group("deviceId").max(sensorTopNumReq.getSensorType())
                .as("sensorValue");
        LimitOperation limitOperation = Aggregation.limit(sensorTopNumReq.getTopTotal());
        SortOperation sortOperation = Aggregation.sort(new Sort(Sort.Direction.DESC, "sensorValue"));
        ProjectionOperation projectionOperation = Aggregation.project().and("_id").as("deviceId")
                .and("sensorValue").as("sensorValue");
        Aggregation aggregation = Aggregation.newAggregation(Sensor.class, Aggregation.match(criteria),
                groupOperation, projectionOperation, sortOperation, limitOperation);
        AggregationResults<SensorTopNum> results = mongoTemplate.aggregate(aggregation, "sensor", SensorTopNum.class);
        List<SensorTopNum> topMapList = results.getMappedResults();
        for (SensorTopNum sensorTopNum : topMapList) {
            fillDeviceFieldFromDeviceInfoBase(sensorTopNum, deviceMap);
        }

        //???????????????
        Aggregation aggregationMin = Aggregation.newAggregation(Sensor.class, Aggregation.match(criteria),
                Aggregation.group("deviceId").min(sensorTopNumReq.getSensorType()).as("sensorValue"),
                Aggregation.project().and("_id").as("deviceId").and("sensorValue").as("sensorValue"),
                Aggregation.sort(new Sort(Sort.Direction.ASC, "sensorValue")),
                Aggregation.limit(sensorTopNumReq.getTopTotal()));
        List<SensorTopNum> bottomMapList = mongoTemplate.aggregate(aggregationMin, "sensor", SensorTopNum.class).getMappedResults();
        for (SensorTopNum sensorTopNum : bottomMapList) {
            fillDeviceFieldFromDeviceInfoBase(sensorTopNum, deviceMap);
        }

        Map<String, List<SensorTopNum>> topNumMap = new HashMap<>();
        topNumMap.put("top", topMapList);
        topNumMap.put("bottom", bottomMapList);
        return ResultUtils.success(topNumMap);
    }

    @Override
    public void deleteExpiredData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - expiredDay);
        Date time = calendar.getTime();
        //??????????????????????????????
        Query query = Query.query(Criteria.where("currentTime").lt(time));
        mongoTemplate.findAllAndRemove(query,Sensor.class);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param sensorTopNumReq
     * @return
     */
    private List<DeviceInfoBase> queryDeviceInfoBase(SensorTopNumReq sensorTopNumReq) {
        DeviceParam deviceParam = new DeviceParam();
        deviceParam.setAreaIds(sensorTopNumReq.getAreaIdList());
        deviceParam.setDeviceType(sensorTopNumReq.getDeviceType());
        return deviceFeign.queryDeviceInfoBaseByParam(deviceParam);
    }

    /**
     * ??????????????????
     *
     * @param sensorTopNum
     * @param deviceMap
     */
    private void fillDeviceFieldFromDeviceInfoBase(SensorTopNum sensorTopNum, Map<String, DeviceInfoBase> deviceMap) {
        DeviceInfoBase deviceInfoBase = deviceMap.get(sensorTopNum.getDeviceId());
        if (deviceInfoBase != null) {
            sensorTopNum.setDeviceName(deviceInfoBase.getDeviceName());
        }
    }

}
