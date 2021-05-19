package com.fiberhome.filink.alarmcurrentserver.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.alarmrecive.AlarmMsgSender;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrentInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmFilter;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmFilterNewInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmMessage;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmReceive;
import com.fiberhome.filink.alarmcurrentserver.bean.DoorInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.OrderDeviceInfo;
import com.fiberhome.filink.alarmcurrentserver.bean.RepeatedOnNotFilter;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmReceiveDao;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmReceiveService;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.alarmsetapi.api.AlarmSetFeign;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetapi.bean.AlarmLevel;
import com.fiberhome.filink.alarmsetapi.bean.AlarmName;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetapi.bean.AlarmOrderRule;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.deviceapi.util.DeviceStatus;
import com.fiberhome.filink.filinklockapi.bean.ControlParam;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * <p>
 * 告警接收和解析服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 14:07 2019/2/27 0027
 */
@Service
@Slf4j
public class AlarmReceiveServiceImpl extends ServiceImpl<AlarmReceiveDao, AlarmReceive> implements AlarmReceiveService {

    /**
     * 工单超时告警编码,跟数据库一致
     */
    @Value("${orderOutOfTime}")
    private String orderOutOfTime;

    /**
     * mongodb实现类
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 告警设置服务接口类
     */
    @Autowired
    private AlarmSetFeign alarmSetFeign;

    /**
     * 设施服务接口类
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 消息推送
     */
    @Autowired
    private AlarmMsgSender alarmMsgSender;

    @Autowired
    private AreaFeign areaFeign;

    /**
     * lock
     */
    @Autowired
    private ControlFeign controlFeign;

    /**
     * 获取websocket需要推送的消息
     *
     * @param toAlarmLevel 告警级别信息
     */
    private AlarmMessage getMessage(AlarmLevel toAlarmLevel) {
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setAlarmLevel(toAlarmLevel.getAlarmLevelName());
        alarmMessage.setAlarmColor(toAlarmLevel.getAlarmLevelColor());
        alarmMessage.setPrompt(toAlarmLevel.getAlarmLevelSound());
        alarmMessage.setIsPrompt(toAlarmLevel.getIsPlay().toString());
        alarmMessage.setPlayCount(toAlarmLevel.getPlayCount());
        alarmMessage.setAlarmLevelCode(toAlarmLevel.getAlarmLevelCode());
        log.info("---websocket" + alarmMessage.toString());
        return alarmMessage;
    }

    /**
     * 工单转告警解析
     * 工单可能有多个设施，但是工单只存一条信息
     *
     * @param alarmCurrentInfoList 告警信息
     */
    @Override
    public void orderCastAlarm(List<AlarmCurrentInfo> alarmCurrentInfoList) {
        //工单超时告警的 alarmType 默认等于2，不会重复，只产生一条告警信息
        int alarmType = LogFunctionCodeConstant.ALARM_STATUS_TWO;
        //遍历所有工单告警
        if (!ListUtil.isEmpty(alarmCurrentInfoList)) {
            alarmCurrentInfoList.forEach((AlarmCurrentInfo alarmCurrentInfo) -> {
                alarmCurrentInfo.getOrderDeviceInfoList().get(0).setAlarmObject(I18nUtils.getString(AppConstant.ORDER_OBJEC));
                //工单超时告警的只会有一条设施信息
                List<OrderDeviceInfo> orderDeviceInfoList = alarmCurrentInfo.getOrderDeviceInfoList();
                if (ListUtil.isListNotExpty(orderDeviceInfoList)) {
                    OrderDeviceInfo orderDeviceInfo = orderDeviceInfoList.get(0);
                    // 获取区域
                    String areaId = orderDeviceInfo.getAreaId();
                    // 获取详细地址
                    Result result = areaFeign.queryAreaById(areaId);
                    if (result.getData() != null) {
                        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(result.getData()));
                        String provinceName = "";
                        String cityName = "";
                        String districtName = "";
                        String address = "";
                        if (!ObjectUtils.isEmpty(jsonObject)) {
                            provinceName = getProvinceName(jsonObject);
                            cityName = getCityName(jsonObject);
                            districtName = getDistrictName(jsonObject);
                            address = getAddress(jsonObject);
                        }
                        orderDeviceInfo.setAddress(provinceName + cityName + districtName + address);
                    }
                    // 工单超时告警不考虑门的情况
                    orderOutOfTime(alarmCurrentInfo, orderDeviceInfo, alarmType);
                }
            });
        }
    }

    private String getProvinceName(JSONObject jsonObject) {
        String provinceName = "";
        if (!ObjectUtils.isEmpty(jsonObject.get("provinceName"))) {
            provinceName = jsonObject.get("provinceName").toString();
        }
        return provinceName;
    }

    private String getCityName(JSONObject jsonObject) {
        String cityName = "";
        if (!ObjectUtils.isEmpty(jsonObject.get("cityName"))) {
            cityName = jsonObject.get("cityName").toString();
        }
        return cityName;
    }

    private String getDistrictName(JSONObject jsonObject) {
        String districtName = "";
        if (!ObjectUtils.isEmpty(jsonObject.get("districtName"))) {
            districtName = jsonObject.get("districtName").toString();
        }
        return districtName;
    }

    private String getAddress(JSONObject jsonObject) {
        String address = "";
        if (!ObjectUtils.isEmpty(jsonObject.get("address"))) {
            address = jsonObject.get("address").toString();
        }
        return address;
    }

    /**
     * 工单超时告警解析
     *
     * @param alarmCurrentInfo 告警信息
     * @param orderDeviceInfo  设施信息
     * @param alarmType        告警类型，1 是设施告警，2 是工单告警
     */
    private void orderOutOfTime(AlarmCurrentInfo alarmCurrentInfo, OrderDeviceInfo orderDeviceInfo, int alarmType) {
        //查询告警名称信息
        AlarmName alarmNameInfo = queryAlarmNameInfo(alarmCurrentInfo, alarmType);
        if (alarmNameInfo == null) {
            return;
        }
        //查询当前告警信息是否被过滤
        AlarmFilterRule alarmFiltered = isAlarmFiltered(alarmCurrentInfo.getAlarmBeginTime(),
                alarmNameInfo.getId(), orderDeviceInfo.getAlarmSource());
        List<AlarmFilter> alarmFilterNewInfoList = new ArrayList<>();
        if (null != alarmFiltered) {
            //被过滤，不用判断是否重复，插入告警过滤信息表
            AlarmFilterNewInfo alarmFilterNewInfo = getAlarmFilterNewInfo(orderDeviceInfo, alarmNameInfo,
                    alarmCurrentInfo, alarmFilterNewInfoList, alarmFiltered);
            alarmFilterNewInfoList = isRepeatedOnFilter(alarmFilterNewInfo, alarmType, null);
        } else {
            //没有被过滤，插入当前告警表
            //保存设施相关信息
            AlarmCurrent alarmCurrent = addDeviceInfo(orderDeviceInfo, null, alarmNameInfo, alarmCurrentInfo, alarmType);
            mongoTemplate.insert(alarmCurrent);
            //获取告警推送，告警远程通知，告警转工单信息
            AlarmInfo alarmInfo = alarmAdviceAndForward(alarmType, alarmCurrentInfo, alarmCurrent);
            //告警推送，告警远程通知，告警转工单信息存入 kafka
            alarmMsgSender.sendAdvice(alarmInfo);
        }
    }

    /**
     * 告警符合过滤信息
     *
     * @param alarmFilterNewInfoList 告警信息
     * @param orderDeviceInfo        设施信息
     * @param alarmNameInfo          告警名称信息
     * @param alarmCurrentInfo       告警信息
     * @param alarmFilterRule        告警过滤信息
     * @return 判断结果
     */
    private AlarmFilterNewInfo getAlarmFilterNewInfo(OrderDeviceInfo orderDeviceInfo, AlarmName alarmNameInfo,
                                                     AlarmCurrentInfo alarmCurrentInfo, List<AlarmFilter> alarmFilterNewInfoList,
                                                     AlarmFilterRule alarmFilterRule) {
        AlarmFilterNewInfo alarmFilterNewInfo = new AlarmFilterNewInfo();
        alarmFilterNewInfo.setOrderDeviceInfo(orderDeviceInfo);
        alarmFilterNewInfo.setAlarmNameInfo(alarmNameInfo);
        alarmFilterNewInfo.setAlarmCurrentInfo(alarmCurrentInfo);
        alarmFilterNewInfo.setAlarmFilterNewInfoList(alarmFilterNewInfoList);
        alarmFilterNewInfo.setAlarmFilterRule(alarmFilterRule);
        return alarmFilterNewInfo;
    }

    /**
     * 设施上报告警解析,到这里只有一条告警信息,一个设施
     * 比如 1个设施，每个设施 4 个门，一共有 4 条告警信息
     *
     * @param alarmCurrentInfo 告警信息
     */
    @Override
    public void alarmAnalysis(AlarmCurrentInfo alarmCurrentInfo) {
        int alarmType = 1;
        // 设施上报告警只有一个设施
        OrderDeviceInfo orderDeviceInfo = alarmCurrentInfo.getOrderDeviceInfoList().get(0);
        // 根据设施ID调用设施服务的接口，查询设施ID,设施名称,区域ID，区域名称，设施类型，设施类型ID，单位ID，负责单位，地址
        DeviceInfoDto deviceInfo = deviceFeign.getDeviceById(orderDeviceInfo.getAlarmSource());
        if (null != deviceInfo && null != deviceInfo.getAreaInfo()) {
            //添加设施信息
            OrderDeviceInfo orderDeviceNewInfo = addDeviceInfoOfDevice(orderDeviceInfo, deviceInfo);
            if (orderDeviceNewInfo == null) {
                return;
            }
            if (ListUtil.isEmpty(orderDeviceNewInfo.getDoorInfoList())) {
                //跟门无关的告警
                noDoor(alarmCurrentInfo, orderDeviceNewInfo, alarmType);
            } else {
                //跟门有关的告警
                hasDoor(alarmCurrentInfo, orderDeviceNewInfo, alarmType);
            }
        }
    }

    /**
     * 添加设施信息
     *
     * @param orderDeviceInfo 设施信息
     * @param deviceInfo      设施信息
     * @return orderDeviceInfo
     */
    private OrderDeviceInfo addDeviceInfoOfDevice(OrderDeviceInfo orderDeviceInfo, DeviceInfoDto deviceInfo) {
        orderDeviceInfo.setAlarmSourceTypeId(deviceInfo.getDeviceType());
        orderDeviceInfo.setAreaId(deviceInfo.getAreaInfo().getAreaId());
        orderDeviceInfo.setAreaName(deviceInfo.getAreaInfo().getAreaName());
        orderDeviceInfo.setAddress(deviceInfo.getAddress());
        orderDeviceInfo.setAlarmObject(deviceInfo.getDeviceName());
        //添加单位ID
        Set<String> set = deviceInfo.getAreaInfo().getAccountabilityUnit();
        StringBuilder accountabilityUnitNames = deviceInfo.getAreaInfo().getAccountabilityUnitName();
        StringBuffer deptIds = new StringBuffer("");
        if (!ListUtil.isSetEmpty(set)) {
            for (String str : set) {
                deptIds.append(str + ",");
                orderDeviceInfo.setResponsibleDepartmentIds(deptIds);
                if (deptIds.length() == 0) {
                    deptIds.append(",");
                }
            }
            // 删除逗号
            deptIds.deleteCharAt(deptIds.lastIndexOf(","));
            orderDeviceInfo.setResponsibleDepartmentNames(new StringBuffer(accountabilityUnitNames));
        }
        return orderDeviceInfo;
    }

    /**
     * 设施没有门的场景
     *
     * @param alarmCurrentInfo 告警信息
     * @param orderDeviceInfo  设施信息
     * @param alarmType        告警类型，1 是设施告警，2 是工单告警
     */
    private void noDoor(AlarmCurrentInfo alarmCurrentInfo, OrderDeviceInfo orderDeviceInfo, int alarmType) {
        AlarmName alarmNameInfo = queryAlarmNameInfo(alarmCurrentInfo, alarmType);
        if (null != alarmNameInfo) {
            List<AlarmCurrent> alarmCurrentNewInfoList = new ArrayList<>();
            List<AlarmFilter> alarmFilterNewInfoList = new ArrayList<>();
            //查询当前告警信息是否被过滤
            AlarmFilterRule alarmFiltered = isAlarmFiltered(alarmCurrentInfo.getAlarmBeginTime(),
                    alarmNameInfo.getId(), orderDeviceInfo.getAlarmSource());
            if (null != alarmFiltered) {
                //被过滤，不用判断是否重复，插入告警过滤信息表
                AlarmFilterNewInfo alarmFilterNewInfo = getAlarmFilterNewInfo(orderDeviceInfo, alarmNameInfo,
                        alarmCurrentInfo, alarmFilterNewInfoList, alarmFiltered);
                alarmFilterNewInfoList = isRepeatedOnFilter(alarmFilterNewInfo, alarmType, null);
                mongoTemplate.insertAll(alarmFilterNewInfoList);
                log.info("key" + alarmFilterNewInfoList.size());
            } else {
                //没有被过滤，判断是否重复
                Query query = isRepeated(orderDeviceInfo, alarmNameInfo, null);
                List<AlarmCurrent> alarmRepeatOnCurrentList = mongoTemplate.find(query, AlarmCurrent.class);
                log.info(alarmCurrentInfo.getAlarmBeginTime().toString());
                // 传入时间必须大于告警最近发生时间
                if (alarmCurrentInfo.getAlarmBeginTime() > alarmRepeatOnCurrentList.get(0).getAlarmNearTime()) {
                    RepeatedOnNotFilter repeatedOnNotFilter = repeatedOnNotFilterList(alarmRepeatOnCurrentList, orderDeviceInfo,
                            alarmNameInfo, alarmCurrentInfo, alarmCurrentNewInfoList);
                    // 保存相关告警信息
                    alarmCurrentNewInfoList = isRepeatedOnNotFilter(repeatedOnNotFilter, query, alarmType, null);
                    //todo 以下时间log未实验代码，后期删除
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date nowDate = new Date();
                    log.info("新告警信息入库，时间：" + ft.format(nowDate));
                    //todo 以上时间log未实验代码，后期删除
                    log.info("key" + alarmCurrentNewInfoList.size());
                    mongoTemplate.insertAll(alarmCurrentNewInfoList);
                }
            }
        }
    }

    /**
     * 告警信息是否重复
     *
     * @param alarmRepeatOnCurrentList 告警信息
     * @param alarmCurrentNewInfoList  告警信息
     * @param orderDeviceInfo          设施信息
     * @param alarmNameInfo            告警名称信息
     * @param alarmCurrentInfo         告警信息
     * @return 判断结果
     */
    private RepeatedOnNotFilter repeatedOnNotFilterList(List<AlarmCurrent> alarmRepeatOnCurrentList,
                                                        OrderDeviceInfo orderDeviceInfo,
                                                        AlarmName alarmNameInfo, AlarmCurrentInfo alarmCurrentInfo,
                                                        List<AlarmCurrent> alarmCurrentNewInfoList) {
        RepeatedOnNotFilter repeatedOnNotFilter = new RepeatedOnNotFilter();
        repeatedOnNotFilter.setAlarmRepeatOnCurrentList(alarmRepeatOnCurrentList);
        repeatedOnNotFilter.setOrderDeviceInfo(orderDeviceInfo);
        repeatedOnNotFilter.setAlarmNameInfo(alarmNameInfo);
        repeatedOnNotFilter.setAlarmCurrentInfo(alarmCurrentInfo);
        repeatedOnNotFilter.setAlarmCurrentNewInfoList(alarmCurrentNewInfoList);
        return repeatedOnNotFilter;
    }

    /**
     * 设施有多个门的场景
     *
     * @param alarmCurrentInfo 告警信息
     * @param orderDeviceInfo  设施信息
     * @param alarmType        告警类型，1 是设施告警，2 是工单告警
     */
    private void hasDoor(AlarmCurrentInfo alarmCurrentInfo, OrderDeviceInfo orderDeviceInfo, int alarmType) {
        AlarmName alarmNameInfo = queryAlarmNameInfo(alarmCurrentInfo, alarmType);
        if (null != alarmNameInfo) {
            List<AlarmCurrent> alarmCurrentNewInfoList = new ArrayList<>();
            List<AlarmFilter> alarmFilterNewInfoList = new ArrayList<>();
            List<DoorInfo> doorInfoList = orderDeviceInfo.getDoorInfoList();
            //查询当前告警信息是否被过滤
            AlarmFilterRule alarmFiltered = isAlarmFiltered(alarmCurrentInfo.getAlarmBeginTime(),
                    alarmNameInfo.getId(), orderDeviceInfo.getAlarmSource());
            //遍历所有有告警的门
            List<DoorInfo> doorInfos = doorInfoList.stream().filter(doorInfoOne ->
                    !AppConstant.LINGS.equals(doorInfoOne.getDoorStatus())).collect(Collectors.toList());
            for (DoorInfo doorInfo : doorInfos) {
                if (null != alarmFiltered) {
                    //被过滤,不用判断是否重复，直接新增
                    AlarmFilterNewInfo alarmFilterNewInfo = getAlarmFilterNewInfo(orderDeviceInfo, alarmNameInfo,
                            alarmCurrentInfo, alarmFilterNewInfoList, alarmFiltered);
                    alarmFilterNewInfoList = isRepeatedOnFilter(alarmFilterNewInfo, alarmType, doorInfo);
                } else {
                    //没有被过滤，判断是否重复
                    Query query = isRepeated(orderDeviceInfo, alarmNameInfo, doorInfo);
                    List<AlarmCurrent> alarmRepeatOnCurrentList = mongoTemplate.find(query, AlarmCurrent.class);
                    RepeatedOnNotFilter repeatedOnNotFilter = repeatedOnNotFilterList(alarmRepeatOnCurrentList,
                            orderDeviceInfo, alarmNameInfo, alarmCurrentInfo, alarmCurrentNewInfoList);
                    alarmCurrentNewInfoList = isRepeatedOnNotFilter(repeatedOnNotFilter, query, alarmType, doorInfo);
                }
            }
            //todo 以下时间log未实验代码，后期删除
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date nowDate = new Date();
            log.info("新告警信息入库，时间：" + ft.format(nowDate));
            //todo 以上时间log未实验代码，后期删除
            if (!ListUtil.isEmpty(alarmCurrentNewInfoList)) {
                mongoTemplate.insertAll(alarmCurrentNewInfoList);
                ControlParam controlParam = controlFeign.getControlParamById(alarmCurrentNewInfoList.get(0).getControlId());
                log.info("-------aa-----" + controlParam.toString());
                //更新设施状态
                controlParam.setDeviceStatus(DeviceStatus.Alarm.getCode());
                controlFeign.updateDeviceStatusById(controlParam);
            }
            if (!ListUtil.isEmpty(alarmFilterNewInfoList)) {
                mongoTemplate.insertAll(alarmFilterNewInfoList);
            }
        }

    }

    /**
     * 判断是否重复的条件
     *
     * @param orderDeviceInfo 设施信息
     * @param alarmNameInfo   告警名称信息
     * @param doorInfo        门信息
     * @return query 查询条件
     */
    private Query isRepeated(OrderDeviceInfo orderDeviceInfo, AlarmName alarmNameInfo, DoorInfo doorInfo) {
        Query query = null;
        if (null == doorInfo) {
            //设施告警，根据设施ID，告警编码判断当前告警是否重复
            query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(orderDeviceInfo.getAlarmSource())
                    .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_CODE).is(alarmNameInfo.getAlarmCode()));
        } else {
            //设施告警，根据设施ID，告警编码，门编号判断当前告警是否重复
            query = new Query(Criteria.where(AppConstant.ALARM_SOURCE).is(orderDeviceInfo.getAlarmSource())
                    .and(AppConstant.ALARM_CLEAN_STATUS).is(LogFunctionCodeConstant.ALARM_STATUS_THREE)
                    .and(AppConstant.ALARM_CODE).is(alarmNameInfo.getAlarmCode())
                    .and(AppConstant.DOOR_NUMBER).is(doorInfo.getDoorNumber()));
        }
        return query;
    }

    /**
     * 查询告警名称信息
     *
     * @param alarmCurrentInfo 告警信息
     * @param alarmType        告警类型，1 是设施告警，2 是工单告警
     * @return alarmNameInfo
     */
    private AlarmName queryAlarmNameInfo(AlarmCurrentInfo alarmCurrentInfo, int alarmType) {
        AlarmName alarmNameInfo = null;
        if (alarmType == AppConstant.ORDER_ALARM) {
            // 工单超时告警，alarmCode 根据配置文件查出
            alarmNameInfo = alarmSetFeign.queryCurrentAlarmSetFeign(orderOutOfTime);
        } else if (alarmType == AppConstant.DEVICE_ALARM) {
            //设施上报告警，根据alarmCode查询当前告警信息的告警名称等信息
            alarmNameInfo = alarmSetFeign.queryCurrentAlarmSetFeign(alarmCurrentInfo.getAlarmCode());
        }
        return alarmNameInfo;
    }

    /**
     * 没有过滤，判断是否重复
     *
     * @param query 查询条件
     * @return alarmCurrentNewInfoList
     */
    private List<AlarmCurrent> isRepeatedOnNotFilter(RepeatedOnNotFilter repeatedOnNotFilter,
                                                     Query query, int alarmType, DoorInfo doorInfo) {
        List<AlarmCurrent> alarmRepeatOnCurrentList = repeatedOnNotFilter.getAlarmRepeatOnCurrentList();
        List<AlarmCurrent> alarmCurrentNewInfoList = repeatedOnNotFilter.getAlarmCurrentNewInfoList();
        if (ListUtil.isEmpty(alarmRepeatOnCurrentList)) {
            //没有重复,保存设施相关信息
            AlarmCurrent alarmCurrent =
                    addDeviceInfo(repeatedOnNotFilter.getOrderDeviceInfo(), doorInfo,
                            repeatedOnNotFilter.getAlarmNameInfo(), repeatedOnNotFilter.getAlarmCurrentInfo(), alarmType);
            //存入当前告警表
            alarmCurrentNewInfoList.add(alarmCurrent);
            //获取告警推送，告警远程通知，告警转工单信息
            AlarmInfo alarmInfo = alarmAdviceAndForward(alarmType, repeatedOnNotFilter.getAlarmCurrentInfo(), alarmCurrent);
            //告警推送，告警远程通知，告警转工单信息存入 kafka
            alarmMsgSender.sendAdvice(alarmInfo);
            log.info("kafka");
        } else {
            //重复，频次加1
            //todo 以下时间log未实验代码，后期删除
            SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date nowDate = new Date();
            log.info("告警频次加一，时间：" + ft.format(nowDate));
            //todo 以上时间log未实验代码，后期删除
            AlarmCurrent alarmCurrent = alarmRepeatOnCurrentList.get(0);
            int ceil = (int) (Math.ceil(System.currentTimeMillis()
                    - repeatedOnNotFilter.getAlarmCurrentInfo().getAlarmBeginTime()) / (60 * 60 * 1000));
            Update update = new Update().set(AppConstant.ALARM_HAPPEN_COUNT, alarmCurrent.getAlarmHappenCount()
                    + LogFunctionCodeConstant.ALARM_STATUS_ONE)
                    .set(AppConstant.ALARM_NEAR_TIME, repeatedOnNotFilter.getAlarmCurrentInfo().getAlarmBeginTime())
                    .set(AppConstant.ALARM_SYSTEM_NEAR_TIME, repeatedOnNotFilter.getAlarmCurrentInfo().getAlarmBeginTime())
                    .set(AppConstant.ALARM_CONTIOUSTIME, ceil);
            mongoTemplate.updateFirst(query, update, AlarmCurrent.class);
        }
        return alarmCurrentNewInfoList;
    }

    /**
     * 获取告警推送，告警远程通知，告警转工单信息
     *
     * @param alarmType        告警类型  1 设施告警   2  工单告警
     * @param alarmCurrentInfo 告警信息
     * @param alarmCurrent     当前告警实体信息
     * @return alarmInfo
     */
    private AlarmInfo alarmAdviceAndForward(int alarmType, AlarmCurrentInfo alarmCurrentInfo, AlarmCurrent alarmCurrent) {
        AlarmInfo alarmInfo = new AlarmInfo();
        //根据alarmCode查询当前告警信息的告警名称等信息
        AlarmName alarmName = alarmSetFeign.queryCurrentAlarmSetFeign(alarmCurrentInfo.getAlarmCode());
        if (null != alarmName) {
            //调用告警设置对外的接口queryAlarmLevelSet查当前告警信息的告警颜色，声音，播放次数等字段信息
            AlarmLevel alarmLevel = alarmSetFeign.queryAlarmLevelSetFeign(alarmName.getAlarmLevel());
            if (null != alarmLevel) {
                //1、告警灯推送，获取消息推送的信息 ，kafka异步
                AlarmMessage alarmMessage = getMessage(alarmLevel);
                alarmInfo.setAlarmMessage(alarmMessage);
                alarmInfo.setAlarmCurrent(alarmCurrent);
                //2、查询告警远程通知规则，判断是否推送，kafka异步
                List<AlarmForwardRule> alarmForwardRules = getAlarmForwardRule(alarmCurrent, alarmLevel);
                alarmInfo.setAlarmForwardRuleList(alarmForwardRules);
            }
            //如果不是工单告警（工单告警不能再次生成工单）
            if (alarmType == 1) {
                //3、查询告警转工单规则，判断是否生成工单，kafka异步
                AlarmOrderRule alarmOrderRule = getAlarmOrderRule(alarmCurrent, alarmName);
                alarmInfo.setAlarmOrderRule(alarmOrderRule);
            }
        }
        return alarmInfo;
    }

    /**
     * 获取告警转工单规则
     *
     * @param alarmCurrent 当前告警信息
     * @return 告警转工单规则信息
     */
    private AlarmOrderRule getAlarmOrderRule(AlarmCurrent alarmCurrent, AlarmName alarmName) {
        AlarmOrderCondition alarmOrderCondition = new AlarmOrderCondition();
        alarmOrderCondition.setAlarmName(alarmName.getId());
        alarmOrderCondition.setAreaId(alarmCurrent.getAreaId());
        alarmOrderCondition.setDeviceType(alarmCurrent.getAlarmSourceTypeId());
        List<AlarmOrderCondition> orderList = new ArrayList<>();
        orderList.add(alarmOrderCondition);
        //不存在返回null
        return alarmSetFeign.queryAlarmOrderRuleFeign(orderList);
    }

    /**
     * 获取告警远程通知规则
     *
     * @param alarmCurrent 当前告警信息
     * @return alarmForwardRule
     */
    private List<AlarmForwardRule> getAlarmForwardRule(AlarmCurrent alarmCurrent, AlarmLevel alarmLevel) {
        AlarmForwardCondition alarmForwardCondition = new AlarmForwardCondition();
        alarmForwardCondition.setAlarmLevel(alarmLevel.getAlarmLevelCode());
        alarmForwardCondition.setAreaId(alarmCurrent.getAreaId());
        alarmForwardCondition.setDeviceType(alarmCurrent.getAlarmSourceTypeId());
        List<AlarmForwardCondition> forwardList = new ArrayList<>();
        forwardList.add(alarmForwardCondition);
        //不存在返回null
        return alarmSetFeign.queryAlarmForwardRuleFeign(forwardList);
    }

    /**
     * 被过滤,不用判断是否重复,直接新增,也不用清除
     *
     * @param alarmFilterNewInfo 告警符合过滤信息
     * @param alarmType          是否开启
     * @param doorInfo           门信息
     * @return 判断结果
     */
    private List<AlarmFilter> isRepeatedOnFilter(AlarmFilterNewInfo alarmFilterNewInfo, int alarmType, DoorInfo doorInfo) {
        //保存设施相关信息
        AlarmFilterRule alarmFilterRule = alarmFilterNewInfo.getAlarmFilterRule();
        List<AlarmFilter> alarmFilterNewInfoList = alarmFilterNewInfo.getAlarmFilterNewInfoList();
        AlarmCurrent alarmCurrent = addDeviceInfo(alarmFilterNewInfo.getOrderDeviceInfo(), doorInfo,
                alarmFilterNewInfo.getAlarmNameInfo(), alarmFilterNewInfo.getAlarmCurrentInfo(), alarmType);
        AlarmFilter alarmFilter = new AlarmFilter();
        BeanUtils.copyProperties(alarmCurrent, alarmFilter);
        //如果存库，存入告警信息过滤表
        if (alarmFilterRule.getStored() == 1) {
            alarmFilterNewInfoList.add(alarmFilter);
        }
        return alarmFilterNewInfoList;
    }

    /**
     * 保存设施相关信息
     *
     * @param orderDeviceInfo  设施信息
     * @param alarmNameInfo    告警名称信息
     * @param alarmCurrentInfo 告警信息
     * @return alarmCurrent
     */
    private AlarmCurrent addDeviceInfo(OrderDeviceInfo orderDeviceInfo, DoorInfo doorInfo,
                                       AlarmName alarmNameInfo, AlarmCurrentInfo alarmCurrentInfo, int alarmType) {
        AlarmCurrent alarmCurrent = new AlarmCurrent();
        int param = LogFunctionCodeConstant.ALARM_STATUS_TWO;
        //根据alarmCode查询当前告警信息的告警名称等信息
        if (alarmType == param) {
            alarmCurrentInfo.setAlarmCode(orderOutOfTime);
        } else {
            alarmCurrent.setIsOrder(false);
        }
        if (null != alarmNameInfo) {
            //存入实体
            alarmCurrent.setId(NineteenUUIDUtils.uuid());
            alarmCurrent.setAlarmCode(alarmCurrentInfo.getAlarmCode());
            alarmCurrent.setAlarmType(alarmType);
            if (!StringUtils.isEmpty(alarmCurrentInfo.getOrderId())) {
                alarmCurrent.setAlarmSource(alarmCurrentInfo.getOrderId());
            } else {
                alarmCurrent.setAlarmSource(orderDeviceInfo.getAlarmSource());
            }
            alarmCurrent.setAlarmObject(orderDeviceInfo.getAlarmObject());
            alarmCurrent.setAlarmSourceTypeId(orderDeviceInfo.getAlarmSourceTypeId());
            alarmCurrent.setAreaId(orderDeviceInfo.getAreaId());
            alarmCurrent.setAreaName(orderDeviceInfo.getAreaName());
            alarmCurrent.setIsOrder(alarmCurrentInfo.getIsOrder());
            alarmCurrent.setAddress(orderDeviceInfo.getAddress());
            alarmCurrent.setAlarmFixedLevel(alarmNameInfo.getAlarmLevel());
            if (null != orderDeviceInfo.getResponsibleDepartmentIds()) {
                alarmCurrent.setResponsibleDepartmentId(orderDeviceInfo.getResponsibleDepartmentIds().toString());
            }
            if (null != orderDeviceInfo.getResponsibleDepartmentNames()) {
                alarmCurrent.setResponsibleDepartment(orderDeviceInfo.getResponsibleDepartmentNames().toString());
            }
            //保存其他信息
            if (!StringUtils.isEmpty(alarmNameInfo.getAlarmAutomaticConfirmation())
                    && alarmNameInfo.getAlarmAutomaticConfirmation().equals(AppConstant.YI)) {
                //如果设置了告警自动确认
                alarmCurrent.setAlarmConfirmStatus(LogFunctionCodeConstant.ALARM_STATUS_ONE);
                alarmCurrent.setAlarmConfirmPeopleId(AppConstant.SYSTEM);
                alarmCurrent.setAlarmConfirmPeopleNickname(AppConstant.SYSTEM);
                alarmCurrent.setAlarmConfirmTime(System.currentTimeMillis());
            } else {
                //未设置了告警自动确认
                alarmCurrent.setAlarmConfirmStatus(LogFunctionCodeConstant.ALARM_STATUS_TWO);
            }
            alarmCurrent.setAlarmCleanStatus(LogFunctionCodeConstant.ALARM_STATUS_THREE);
            alarmCurrent.setAlarmName(alarmNameInfo.getAlarmName());
            alarmCurrent.setAlarmNameId(alarmNameInfo.getId());
            alarmCurrent.setAlarmSystemTime(alarmCurrentInfo.getAlarmBeginTime());
            alarmCurrent.setAlarmBeginTime(alarmCurrentInfo.getAlarmBeginTime());
            alarmCurrent.setAlarmNearTime(alarmCurrentInfo.getAlarmBeginTime());
            alarmCurrent.setAlarmSystemNearTime(alarmCurrentInfo.getAlarmBeginTime());
            alarmCurrent.setAlarmHappenCount(1);
            alarmCurrent.setControlId(alarmCurrentInfo.getEquipmentId());
            //添加告警附加信息
            alarmCurrent = addExtraMsg(orderDeviceInfo, alarmCurrentInfo, alarmCurrent, doorInfo, alarmNameInfo);
            if (!ObjectUtils.isEmpty(doorInfo)) {
                alarmCurrent.setDoorName(doorInfo.getDoorName());
                alarmCurrent.setDoorNumber(doorInfo.getDoorNumber());
            }
        }
        return alarmCurrent;
    }

    /**
     * 添加告警附加信息
     *
     * @param orderDeviceInfo  设施信息
     * @param alarmCurrentInfo 告警信息
     * @param alarmCurrent     告警信息
     * @return alarmCurrent
     */
    private AlarmCurrent addExtraMsg(OrderDeviceInfo orderDeviceInfo, AlarmCurrentInfo alarmCurrentInfo,
                                     AlarmCurrent alarmCurrent, DoorInfo doorInfo, AlarmName alarmName) {
        if (alarmCurrentInfo.getIsOrder()) {
            //工单产生的告警
            alarmCurrent.setExtraMsg(I18nUtils.getString(AppConstant.ORDER) + alarmCurrentInfo.getOrderName());
        } else {
            //设施上报告警
            if (ListUtil.isEmpty(orderDeviceInfo.getDoorInfoList())) {
                //如果没有门
                String alarmCode = alarmName.getAlarmCode();
                String codeInfo = "";
                // 查询无需门信息的
                String selectCodeInfo = selectCodeInfo(alarmCode, codeInfo, alarmCurrentInfo);
                if (StringUtils.isEmpty(selectCodeInfo)) {
                    alarmCurrent.setExtraMsg(codeInfo);
                } else {
                    alarmCurrent.setExtraMsg(selectCodeInfo);
                }
            } else {
                //如果有门，保存门的信息
                String alarmCode = alarmName.getAlarmCode();
                String codeInfo = "";
                String selectCodeInfo = selectCodeInfo(alarmCode, codeInfo, alarmCurrentInfo);
                if (!StringUtils.isEmpty(selectCodeInfo)) {
                    alarmCurrent.setExtraMsg(selectCodeInfo);
                } else {
                    String selectCodes = selectCodes(alarmCode, codeInfo);
                    if (!StringUtils.isEmpty(selectCodes)) {
                        alarmCurrent.setExtraMsg(selectCodes + I18nUtils.getString(AppConstant.DOOR_NUMBER_NAME)
                                + doorInfo.getDoorNumber());
                    } else {
                        alarmCurrent.setExtraMsg(codeInfo);
                    }
                }
            }
        }
        return alarmCurrent;
    }

    /**
     * 查询告警对应的附加信息
     *
     * @param alarmCode        告警code
     * @param codeInfo         附加信息
     * @param alarmCurrentInfo 告警信息
     * @return 附加信息
     */
    private String selectCodeInfo(String alarmCode, String codeInfo, AlarmCurrentInfo alarmCurrentInfo) {
        if (AppConstant.HUMIDITY.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.HUMIDITY_NAME) + alarmCurrentInfo.getData() + "%";
        } else if (AppConstant.HIGHTEMPERATURE.equals(alarmCode) || AppConstant.LOWTEMPERATURE.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.TEMPERATURE_NAME) + alarmCurrentInfo.getData() + "℃";
        } else if (AppConstant.ELECTRICITY.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.ELECTRIC_NAME) + alarmCurrentInfo.getData() + "%";
        } else if (AppConstant.LEAN.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.INCLINE_NAME) + alarmCurrentInfo.getData() + "°";
        }
        return codeInfo;
    }

    /**
     * 查询告警对应的附加信息
     *
     * @param alarmCode 告警code
     * @param codeInfo  附加信息
     * @return 附加信息
     */
    private String selectCodes(String alarmCode, String codeInfo) {
        if (AppConstant.PRYDOOR.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.TO_BREAK_DOOR_NAME);
        } else if (AppConstant.NOTCLOSED.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.NOT_CLOSED);
        } else if (AppConstant.VIOLENCECLOSE.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.OPEN_DOOR);
        } else if (AppConstant.PRYLOCK.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.LOCKPICKING_NAME);
        } else if (AppConstant.UNLOCK.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.NOT_SHUT_NAME);
        } else if (AppConstant.EMERGENCYLOCK.equals(alarmCode)) {
            codeInfo = I18nUtils.getString(AppConstant.EMERGENCY_NAME);
        }
        return codeInfo;
    }

    /**
     * 判断告警是否被过滤
     *
     * @param alarmBeginTime 告警时间
     * @param alarmNameId    告警名称ID
     * @param alarmSourceId  告警设施ID
     * @return isFilter
     */
    private AlarmFilterRule isAlarmFiltered(Long alarmBeginTime, String alarmNameId, String alarmSourceId) {
        AlarmFilterCondition alarmFilterCondition = new AlarmFilterCondition();
        alarmFilterCondition.setStartTime(alarmBeginTime);
        alarmFilterCondition.setAlarmName(alarmNameId);
        alarmFilterCondition.setAlarmObject(alarmSourceId);
        List<AlarmFilterCondition> filterList = new ArrayList<>();
        filterList.add(alarmFilterCondition);
        return alarmSetFeign.queryAlarmIsIncludedFeign(filterList);
    }
}
