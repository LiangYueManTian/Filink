package com.fiberhome.filink.alarmsetserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.*;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.constant.AlarmSetResultCode;
import com.fiberhome.filink.alarmsetserver.dao.*;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmForwardRuleService;
import com.fiberhome.filink.alarmsetserver.service.AlarmOrderRuleService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@Slf4j
@Service
public class AlarmForwardRuleServiceImpl extends ServiceImpl<AlarmForwardRuleDao, AlarmForwardRule> implements AlarmForwardRuleService {

    /**
     * 远程通知dao
     */
    @Autowired
    private AlarmForwardRuleDao alarmForwardRuleDao;

    /**
     * 通知人dao
     */
    @Autowired
    private AlarmForwardRuleUserDao alarmForwardRuleUserDao;

    /**
     * 区域api
     */
    @Autowired
    private AlarmForwardRuleAreaDao alarmForwardRuleAreaDao;

    /**
     * 设施类型dao
     */
    @Autowired
    private AlarmForwardRuleDeviceTypeDao alarmForwardRuleDeviceTypeDao;

    /**
     * 告警级别
     */
    @Autowired
    private AlarmForwardRuleLevelDao alarmForwardRuleLevelDao;

    /**
     * 区域Api
     */
    @Autowired
    private AreaFeign areaFeign;

    /**
     * 用户feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 日志
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 告警转工单service
     */
    @Autowired
    private AlarmOrderRuleService alarmOrderRuleService;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警远程通知列表信息
     *
     * @param queryCondition 告警远程通知dto
     * @return 告警远程通知信息
     */
    @Override
    public Result queryAlarmForwardRuleList(QueryCondition<AlarmForwardRuleDto> queryCondition) {
        //查询条件
        AlarmForwardRuleDto alarmForwardRuleDto = queryCondition.getBizCondition();
        //权限过滤
        boolean flag = addAuto(alarmForwardRuleDto);
        List<AlarmForwardRule> alarmForwardRules = new ArrayList<>();
        if (flag) {
            //查询数据
            alarmForwardRules = alarmForwardRuleDao.queryAlarmForwardRuleList(alarmForwardRuleDto);
        }
        //有数据查询拼接列表信息
        if (!CollectionUtils.isEmpty(alarmForwardRules)) {
            alarmForwardRules = getAlarmForwardRules(alarmForwardRules);
        } else {
            alarmForwardRules = new ArrayList<>();
        }
        return ResultUtils.success(alarmForwardRules);
    }

    /**
     * 查询拼接列表信息
     * @param alarmForwardRules 告警远程通知信息
     * @return 告警远程通知信息
     */
    private List<AlarmForwardRule> getAlarmForwardRules(List<AlarmForwardRule> alarmForwardRules) {
        Set<String> ruleUserSet = new HashSet<>();
        Set<String> ruleAreaSet = new HashSet<>();
        //获取相关联的区域、用户ID
        for (AlarmForwardRule alarmForwardRule : alarmForwardRules) {
            ruleUserSet.addAll(alarmForwardRule.getAlarmForwardRuleUserList());
            ruleAreaSet.addAll(alarmForwardRule.getAlarmForwardRuleAreaList());
        }
        //查询区域信息
        List<AreaInfoForeignDto> areaInfoList = areaFeign.selectAreaInfoByIds(new ArrayList<>(ruleAreaSet));
        //查询用户信息
        List<Object> users = (List<Object>) userFeign.queryUserByIdList(new ArrayList<>(ruleUserSet));
        if (areaInfoList == null || users == null) {
            alarmForwardRules = new ArrayList<>();
        } else {
            setAlarmForwardRulesName(alarmForwardRules, areaInfoList, users);
        }
        return alarmForwardRules;
    }

    /**
     * 设置名称
     * @param alarmForwardRules 告警远程通知信息
     * @param areaInfoList 区域信息
     * @param users 用户信息
     */
    private void setAlarmForwardRulesName(List<AlarmForwardRule> alarmForwardRules,
                                          List<AreaInfoForeignDto> areaInfoList, List<Object> users) {
        Map<String, String> areaNameMap = new HashMap<>(64);
        Map<String, String> userNameMap = new HashMap<>(64);
        //将区域名称、用户名称取出
        getNameMap(areaInfoList, users, areaNameMap, userNameMap);
        //拼接信息
        for (AlarmForwardRule alarmForwardRule : alarmForwardRules) {
            setAlarmForwardRuleName(areaNameMap, userNameMap, alarmForwardRule);
        }
    }

    /**
     * 拼接信息
     * @param areaNameMap 区域名称
     * @param userNameMap 用户名称
     * @param alarmForwardRule 告警远程通知信息
     */
    private void setAlarmForwardRuleName(Map<String, String> areaNameMap,
                                         Map<String, String> userNameMap, AlarmForwardRule alarmForwardRule) {
        List<String> areaNameList = new ArrayList<>();
        List<String> userNameList = new ArrayList<>();
        //区域名称
        for (String userId : alarmForwardRule.getAlarmForwardRuleUserList()) {
            String userName = userNameMap.get(userId);
            if (!StringUtils.isEmpty(userName)) {
                userNameList.add(userName);
            }
        }
        //用户名称
        for (String areaId : alarmForwardRule.getAlarmForwardRuleAreaList()) {
            String areaName = areaNameMap.get(areaId);
            if (!StringUtils.isEmpty(areaName)) {
                areaNameList.add(areaName);
            }
        }
        alarmForwardRule.setAlarmForwardRuleAreaName(areaNameList);
        alarmForwardRule.setAlarmForwardRuleUserName(userNameList);
    }

    /**
     * //将区域名称、用户名称取出
     * @param areaInfoList 区域信息
     * @param users 用户信息
     * @param areaNameMap 区域名称
     * @param userNameMap 用户名称
     */
    private void getNameMap(List<AreaInfoForeignDto> areaInfoList, List<Object> users, Map<String, String> areaNameMap, Map<String, String> userNameMap) {
        for (AreaInfoForeignDto areaInfoForeignDto : areaInfoList) {
            areaNameMap.put(areaInfoForeignDto.getAreaId(), areaInfoForeignDto.getAreaName());
        }
        for (Object user : users) {
            User getUser = JSONArray.toJavaObject((JSON) JSONArray.toJSON(user), User.class);
            userNameMap.put(getUser.getId(), getUser.getUserName());
        }
    }

    /**
     * 获取用户数据权限信息
     *
     * @param alarmForwardRuleDto 封装条件
     */
    private boolean addAuto(AlarmForwardRuleDto alarmForwardRuleDto) {
        if (AppConstant.ALARM_YI.equals(RequestInfoUtils.getUserId())) {
            return true;
        }
        User user = alarmOrderRuleService.getUser();
        if (user == null) {
            return false;
        }
        // 获取用户区域
        List<String> userAreaIds = alarmOrderRuleService.getUserAreaIds(user);
        // 获取用户设施类型
        List<String> deviceTypes = alarmOrderRuleService.getDeviceTypes(user);
        //判断权限
        if (ListUtil.isEmpty(userAreaIds) || ListUtil.isEmpty(deviceTypes)) {
            return false;
        }
        checkForwardRuleUserPermission(alarmForwardRuleDto, deviceTypes, userAreaIds);
        //查询条件包含数据权限，校验是否有权限
        return !alarmForwardRuleDto.checkPermission();
    }
    /**
     * 查询校验权限
     * @param alarmForwardRuleDto 查询条件
     * @param deviceTypeIds 设施类型权限
     * @param list 区域权限
     */
    private void checkForwardRuleUserPermission(AlarmForwardRuleDto alarmForwardRuleDto, List<String> deviceTypeIds, List<String> list) {
        //设施类型查询条件
        List<String> deviceTypeId = alarmForwardRuleDto.getDeviceTypeId();
        //区域查询条件
        Set<String> alarmForwardRuleAreaList = alarmForwardRuleDto.getAlarmForwardRuleAreaList();
        Set<String> areaIdSet = new HashSet<>(list);
        if (!ListUtil.isEmpty(deviceTypeId)) {
            List<String> deviceTypeIdList = deviceTypeId.stream().filter(
                    deviceTypeIds::contains).collect(toList());
            alarmForwardRuleDto.setDeviceTypeId(deviceTypeIdList);
        } else {
            alarmForwardRuleDto.setDeviceTypeId(deviceTypeIds);
        }
        if (!CollectionUtils.isEmpty(alarmForwardRuleAreaList)) {
            Set<String> areaIds =  alarmForwardRuleAreaList.stream().filter(
                    areaIdSet::contains).collect(toSet());
            alarmForwardRuleDto.setAlarmForwardRuleAreaList(areaIds);
        } else {
            alarmForwardRuleDto.setAlarmForwardRuleAreaList(areaIdSet);
        }
    }
    /**
     * 根据id查询远程通知信息
     *
     * @param id 告警id
     * @return 远程通知信息
     */
    @Override
    public Result queryAlarmForwardId(String id) {
        List<AlarmForwardRule> alarmForwardRules = alarmForwardRuleDao.queryAlarmForwardId(id);
        //信息不存在
        if (ListUtil.isEmpty(alarmForwardRules)) {
            return ResultUtils.warn(AlarmSetResultCode.ALARM_FORWARD_FAILED,
                    I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_FAILED));
        }
        // 查询通知人信息 区域
        alarmForwardRules = getAlarmForwardRules(alarmForwardRules);
        return ResultUtils.success(alarmForwardRules);
    }

    /**
     * 新增告警远程通知信息
     *
     * @param alarmForwardRule 告警远程通知信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_ADD_FORWARD,
            dataGetColumnName = "ruleName", dataGetColumnId = "id")
    @Override
    public Result addAlarmForwardRule(AlarmForwardRule alarmForwardRule) {
        String id = NineteenUUIDUtils.uuid();
        alarmForwardRule.setId(id);
        String userId = RequestInfoUtils.getUserId();
        alarmForwardRule.setCreateUser(userId);
        alarmForwardRule.setUpdateUser(userId);
        long date = System.currentTimeMillis();
        alarmForwardRule.setCreateTime(date);
        Integer insert = alarmForwardRuleDao.insert(alarmForwardRule);
        if (insert != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_ADD_FAILED));
        }
        // 新增通知人
        Set<String> alarmForwardRuleUser = alarmForwardRule.getAlarmForwardRuleUserList();
        Integer addUser = addUser(alarmForwardRuleUser, alarmForwardRule);
        if (addUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_USER_FAILED));
        }
        // 新增区域
        Set<String> alarmForwardRuleArea = alarmForwardRule.getAlarmForwardRuleAreaList();
        Integer area = addArea(alarmForwardRuleArea, alarmForwardRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_AREA_FAILED));
        }
        // 新增设施类型
        Integer addDeviceType = addDeviceType(alarmForwardRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_DEVICE_FAILED));
        }
        // 新增告警级别
        Integer addAlarmLevel = addAlarmLevel(alarmForwardRule);
        if (addAlarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_LEVEL_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_ADD_SUCCESS));
    }

    /**
     * 删除告警远程通知信息
     *
     * @param array 告警远程通知id
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteAlarmForwardRule(String[] array) {
        Integer integer = alarmForwardRuleDao.deleteAlarmForwardRule(array);
        if (!integer.equals(array.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_DELETE_FAILED));
        }
        Integer area = alarmForwardRuleAreaDao.batchDeleteAlarmForwardRuleArea(array);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_AREA_FAILED));
        }
        Integer deviceType = alarmForwardRuleDeviceTypeDao.batchDeleteAlarmDeviceType(array);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_DEVICE_FAILED));
        }
        Integer ruleUser = alarmForwardRuleUserDao.batchDeleteAlarmForwardRuleUser(array);
        if (ruleUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_USER_FAILED));
        }
        Integer alarmLevel = alarmForwardRuleLevelDao.batchDeleteAlarmLevel(array);
        if (alarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_LEVEL_FAILEDS));
        }
        // 记录日志
        deleteLog(array);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_DELETE_SUCCESS));
    }

    /**
     * 修改告警远程通知信息
     *
     * @param alarmForwardRule 告警远程通知信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_UPDATE_FORWARD,
            dataGetColumnName = "ruleName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmForwardRule(AlarmForwardRule alarmForwardRule) {
        // 获取当前用户
        String userName = RequestInfoUtils.getUserName();
        alarmForwardRule.setUpdateUser(userName);
        long date = System.currentTimeMillis();
        alarmForwardRule.setUpdateTime(date);
        // 修改告警远程通知
        Integer integer = alarmForwardRuleDao.updateById(alarmForwardRule);
        if (integer != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_UPDATE_FAILED));
        }
        // 修改通知人
        Set<String> alarmForwardRuleUser = alarmForwardRule.getAlarmForwardRuleUserList();
        alarmForwardRuleUserDao.deleteAlarmForwardRuleUser(alarmForwardRule.getId());
        Integer addUser = addUser(alarmForwardRuleUser, alarmForwardRule);
        if (addUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_USER_FAILED));
        }
        // 修改区域
        Set<String> alarmForwardRuleArea = alarmForwardRule.getAlarmForwardRuleAreaList();
        alarmForwardRuleAreaDao.deleteAlarmForwardRuleArea(alarmForwardRule.getId());
        Integer area = addArea(alarmForwardRuleArea, alarmForwardRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_AREA_FAILED));
        }
        // 修改设施类型
        alarmForwardRuleDeviceTypeDao.deleteAlarmDeviceType(alarmForwardRule.getId());
        Integer deviceType = addDeviceType(alarmForwardRule);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_DEVICE_FAILED));
        }
        // 修改告警级别
        alarmForwardRuleLevelDao.deleteAlarmLevel(alarmForwardRule.getId());
        Integer alarmLevel = addAlarmLevel(alarmForwardRule);
        if (alarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_LEVEL_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_UPDATE_SUCCESS));
    }

    /**
     * 修改告警远程通知状态信息
     *
     * @param status  告警远程通知状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmForwardRuleStatus(Integer status, String[] idArray) {
        Integer integer = alarmForwardRuleDao.updateAlarmForwardRuleStatus(status, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_STUTES_FAILED));
        }
        // 记录日志
        updateLog(idArray, AppConstant.ALARM_LOG_SIX);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_STUTES_SUCCESS));
    }

    /**
     * 修改告警远程通知推送类型信息
     *
     * @param pushType 推送类型信息
     * @param idArray  用户id
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmForwardRulePushType(Integer pushType, String[] idArray) {
        Integer integer = alarmForwardRuleDao.updateAlarmForwardRulePushType(pushType, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_PUSHTYPE_FAILED));
        }
        // 记录日志
        updateLog(idArray, AppConstant.ALARM_LOG_SEVEN);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_PUSHTYPE_SUCCESS));
    }

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForwardCondition 远程通知条件
     * @return 告警远程通知信息
     */
    @Override
    public List<AlarmForwardRule> queryAlarmForwardRuleFeign(List<AlarmForwardCondition> alarmForwardCondition) {
        List<AlarmForwardRule> alarmForwardRules = new ArrayList<>();
        //判断是否符合当前远程通知规则，必须所有条件全部满足才算
        for (AlarmForwardCondition alarmForwardConditionOne : alarmForwardCondition) {
            List<AlarmForwardRule> alarmForwardRuleList = alarmForwardRuleDao.queryAlarmForwardRuleFeign(alarmForwardConditionOne);
            if (!CollectionUtils.isEmpty(alarmForwardRuleList)) {
                alarmForwardRules.addAll(alarmForwardRuleList);
            }
        }
        return alarmForwardRules;
    }



    /**
     * 新增通知人
     *
     * @param alarmForwardRuleUser 通知人id
     * @param alarmForwardRule     告警远程通知信息
     */
    private Integer addUser(Set<String> alarmForwardRuleUser, AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleUser> alarmForwardRuleUsers = new ArrayList<>();
        alarmForwardRuleUser.forEach(userId -> {
            AlarmForwardRuleUser alarmForwardRuleUserOne = new AlarmForwardRuleUser();
            alarmForwardRuleUserOne.setRuleId(alarmForwardRule.getId());
            alarmForwardRuleUserOne.setUserId(userId);
            alarmForwardRuleUsers.add(alarmForwardRuleUserOne);
        });
        return alarmForwardRuleUserDao.addAlarmForwardRuleUser(alarmForwardRuleUsers);
    }

    /**
     * 新增区域
     *
     * @param alarmForwardRuleArea 区域id
     * @param alarmForwardRule     告警远程通知信息
     */
    private Integer addArea(Set<String> alarmForwardRuleArea, AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleArea> alarmForwardRuleAreaList = new ArrayList<>();
        alarmForwardRuleArea.forEach(areaId -> {
            AlarmForwardRuleArea alarmForwardRuleAreaOne = new AlarmForwardRuleArea();
            alarmForwardRuleAreaOne.setRuleId(alarmForwardRule.getId());
            alarmForwardRuleAreaOne.setAreaId(areaId);
            alarmForwardRuleAreaList.add(alarmForwardRuleAreaOne);
        });
        return alarmForwardRuleAreaDao.addAlarmForwardRuleArea(alarmForwardRuleAreaList);
    }

    /**
     * 新增设施类型
     *
     * @param alarmForwardRule 告警远程通知信息
     */
    private Integer addDeviceType(AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceType = alarmForwardRule.getAlarmForwardRuleDeviceTypeList();
        alarmForwardRuleDeviceType.forEach(alarmForwardRuleDeviceTypeOne -> alarmForwardRuleDeviceTypeOne.setRuleId(alarmForwardRule.getId()));
        return alarmForwardRuleDeviceTypeDao.addAlarmForwardRuleDeviceType(alarmForwardRuleDeviceType);
    }

    /**
     * 新增告警级别
     *
     * @param alarmForwardRule 告警id
     */
    private Integer addAlarmLevel(AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleLevel> alarmLevels = alarmForwardRule.getAlarmForwardRuleLevels();
        alarmLevels.forEach(alarmForwardRuleLevel -> alarmForwardRuleLevel.setRuleId(alarmForwardRule.getId()));
        return alarmForwardRuleLevelDao.addAlarmLevel(alarmLevels);
    }

    /**
     * 修改日志记录
     *  @param ids   用户id
     * @param model 模板
     */
    private void updateLog(String[] ids, String model) {
        for (String id : ids) {
            AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(AppConstant.ALARM_RULE);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmForwardRule.getRuleName());
            // 操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 删除日志记录
     *  @param ids   用户id
     * */
    private void deleteLog(String[] ids) {
        for (String id : ids) {
            AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(AppConstant.ALARM_RULE);
            addLogBean.setFunctionCode(AppConstant.ALARM_LOG_FIVE);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmForwardRule.getRuleName());
            // 操作为删除
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

}
