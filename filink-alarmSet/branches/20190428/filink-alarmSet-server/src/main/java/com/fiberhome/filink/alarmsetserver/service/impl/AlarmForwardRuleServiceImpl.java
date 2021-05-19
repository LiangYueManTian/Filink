package com.fiberhome.filink.alarmsetserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleArea;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDeviceType;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleLevel;
import com.fiberhome.filink.alarmsetserver.bean.AlarmForwardRuleUser;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.AlarmForwardRuleAreaDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmForwardRuleDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmForwardRuleDeviceTypeDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmForwardRuleLevelDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmForwardRuleUserDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmForwardRuleService;
import com.fiberhome.filink.alarmsetserver.service.AlarmOrderRuleService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 查询告警远程通知列表信息
     *
     * @param queryCondition 告警远程通知dto
     * @return 告警远程通知信息
     */
    @Override
    public Result queryAlarmForwardRuleList(QueryCondition<AlarmForwardRuleDto> queryCondition) {
        AlarmForwardRuleDto alarmForwardRuleDto = queryCondition.getBizCondition();
        addAuto(alarmForwardRuleDto);
        List<AlarmForwardRule> alarmForwardRules = alarmForwardRuleDao.queryAlarmForwardRuleList(alarmForwardRuleDto);
        // 查询通知人信息
        selectUserName(alarmForwardRules);
        // 查询区域
        selectAlarmAreaName(alarmForwardRules);
        // 查询设备类型
        selectDeviceType(alarmForwardRules);
        // 查询告警级别
        selectAlarmLevel(alarmForwardRules);
        return ResultUtils.success(alarmForwardRules);
    }

    /**
     * 获取用户数据权限信息
     *
     * @param alarmForwardRuleDto 封装条件
     */
    private void addAuto(AlarmForwardRuleDto alarmForwardRuleDto) {
        User user = alarmOrderRuleService.getUser();
        if (AppConstant.ALARM_YI.equals(user.getId())) {
            return;
        }
        List<String> deviceTypes = alarmOrderRuleService.getDeviceTypes(user);
        if (!ListUtil.isEmpty(deviceTypes)) {
            alarmForwardRuleDto.setDeviceTypeId(deviceTypes);
        }
        List<String> list = alarmOrderRuleService.getUserAreaIds(user);
        if (!ListUtil.isEmpty(list)) {
            Set<String> areaIds = new HashSet<>(list);
            alarmForwardRuleDto.setAlarmForwardRuleAreaList(areaIds);
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
        if (ListUtil.isEmpty(alarmForwardRules)) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(AppConstant.ALARM_FORWARD_FAILED));
        }
        // 查询通知人信息
        selectUserName(alarmForwardRules);
        // 查询区域
        selectAlarmAreaName(alarmForwardRules);
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
            dataGetColumnName = "id", dataGetColumnId = "id")
    @Override
    public Result addAlarmForwardRule(AlarmForwardRule alarmForwardRule) {
        String id = NineteenUUIDUtils.uuid();
        alarmForwardRule.setId(id);
        String userName = RequestInfoUtils.getUserName();
        alarmForwardRule.setCreateUser(userName);
        alarmForwardRule.setUpdateUser(userName);
        long date = System.currentTimeMillis();
        alarmForwardRule.setCreateTime(date);
        Integer insert = alarmForwardRuleDao.insert(alarmForwardRule);
        if (insert != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FORWARD_ADD_FAILED));
        }
        // 新增通知人
        Set<String> alarmForwardRuleUser = alarmForwardRule.getAlarmForwardRuleUserList();
        Integer addUser = addUser(alarmForwardRuleUser, alarmForwardRule);
        if (addUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_USER_FAILED));
        }
        // 新增区域
        Set<String> alarmForwardRuleArea = alarmForwardRule.getAlarmForwardRuleAreaList();
        Integer area = addArea(alarmForwardRuleArea, alarmForwardRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_AREA_FAILED));
        }
        // 新增设施类型
        Integer addDeviceType = addDeviceType(alarmForwardRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_DEVICE_FAILED));
        }
        // 新增告警级别
        Integer addAlarmLevel = addAlarmLevel(alarmForwardRule);
        if (addAlarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_LEVEL_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FORWARD_ADD_SUCCESS));
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
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FORWARD_DELETE_FAILED));
        }
        Integer area = alarmForwardRuleAreaDao.batchDeleteAlarmForwardRuleArea(array);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_AREA_FAILED));
        }
        Integer deviceType = alarmForwardRuleDeviceTypeDao.batchDeleteAlarmDeviceType(array);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_DEVICE_FAILED));
        }
        Integer ruleUser = alarmForwardRuleUserDao.batchDeleteAlarmForwardRuleUser(array);
        if (ruleUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_USER_FAILED));
        }
        Integer alarmLevel = alarmForwardRuleLevelDao.batchDeleteAlarmLevel(array);
        if (alarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_LEVEL_FAILEDS));
        }
        // 记录日志
        deleteLog(array, AppConstant.ALARM_LOG_FIVE, AppConstant.ALARM_ID);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FORWARD_DELETE_SUCCESS));
    }

    /**
     * 修改告警远程通知信息
     *
     * @param alarmForwardRule 告警远程通知信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_UPDATE_FORWARD,
            dataGetColumnName = "id", dataGetColumnId = "id")
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
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FORWARD_UPDATE_FAILED));
        }
        // 修改通知人
        Set<String> alarmForwardRuleUser = alarmForwardRule.getAlarmForwardRuleUserList();
        alarmForwardRuleUserDao.deleteAlarmForwardRuleUser(alarmForwardRule.getId());
        Integer addUser = addUser(alarmForwardRuleUser, alarmForwardRule);
        if (addUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_USER_FAILED));
        }
        // 修改区域
        Set<String> alarmForwardRuleArea = alarmForwardRule.getAlarmForwardRuleAreaList();
        alarmForwardRuleAreaDao.deleteAlarmForwardRuleArea(alarmForwardRule.getId());
        Integer area = addArea(alarmForwardRuleArea, alarmForwardRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_AREA_FAILED));
        }
        // 修改设施类型
        alarmForwardRuleDeviceTypeDao.deleteAlarmDeviceType(alarmForwardRule.getId());
        Integer deviceType = addDeviceType(alarmForwardRule);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_DEVICE_FAILED));
        }
        // 修改告警级别
        alarmForwardRuleLevelDao.deleteAlarmLevel(alarmForwardRule.getId());
        Integer alarmLevel = addAlarmLevel(alarmForwardRule);
        if (alarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_LEVEL_FAILED));
        }
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FORWARD_UPDATE_SUCCESS));
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
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FORWARD_STUTES_FAILED));
        }
        // 记录日志
        updateLog(idArray, AppConstant.ALARM_LOG_SIX, AppConstant.ALARM_ID);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FORWARD_STUTES_SUCCESS));
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
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FORWARD_PUSHTYPE_FAILED));
        }
        // 记录日志
        updateLog(idArray, AppConstant.ALARM_LOG_SEVEN, AppConstant.ALARM_ID);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FORWARD_PUSHTYPE_SUCCESS));
    }

    /**
     * 查询当前告警远程通知规则
     *
     * @param alarmForwardCondition 远程通知条件
     * @return 告警远程通知信息
     */
    @Override
    public List<AlarmForwardRule> queryAlarmForwardRuleFeign(List<AlarmForwardCondition> alarmForwardCondition) {
        List<AlarmForwardRule> alarmForwardRuleLists = new ArrayList<>();
        List<AlarmForwardRule> list = alarmForwardRuleDao.queryAlarmForwardRuleLists();
        for (int i = 0; i < list.size(); i++) {
            String id = list.get(i).getId();
            //判断是否符合当前远程通知规则，必须所有条件全部满足才算
            for (AlarmForwardCondition alarmForwardConditionOne : alarmForwardCondition) {
                alarmForwardConditionOne.setId(id);
                AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.queryAlarmForwardRuleFeign(alarmForwardConditionOne);
                if (alarmForwardRule == null) {
                    break;
                }
                alarmForwardRuleLists.add(alarmForwardRule);
            }
        }
        if (ListUtil.isEmpty(alarmForwardRuleLists)) {
            return null;
        }
        return alarmForwardRuleLists;
    }

    /**
     * 查询告警远程通知区域名称
     *
     * @param alarmForwardRules 告警远程通知信息
     */
    private void selectAlarmAreaName(List<AlarmForwardRule> alarmForwardRules) {
        alarmForwardRules.forEach((AlarmForwardRule alarmForwardRule) -> {
            Set<String> alarmForwardRuleAreaList = alarmForwardRule.getAlarmForwardRuleAreaList();
            if (ListUtil.isSetEmpty(alarmForwardRuleAreaList)) {
                return;
            }
            List<String> arrayList = new ArrayList<>(alarmForwardRuleAreaList);
            List<String> list = new ArrayList<>();
            List<AreaInfoForeignDto> areaInfos = areaFeign.selectAreaInfoByIds(arrayList);
            if (ListUtil.isEmpty(areaInfos)) {
                return;
            }
            areaInfos.forEach(areaInfo -> {
                list.add(areaInfo.getAreaName());
            });
            alarmForwardRule.setAlarmForwardRuleAreaName(list);
        });
    }

    /**
     * 查询通知人信息
     *
     * @param alarmForwardRules 告警远程通知信息
     */
    private void selectUserName(List<AlarmForwardRule> alarmForwardRules) {
        alarmForwardRules.forEach((AlarmForwardRule alarmForwardRule) -> {
            Set<String> alarmForwardRuleUserList = alarmForwardRule.getAlarmForwardRuleUserList();
            if (ListUtil.isSetEmpty(alarmForwardRuleUserList)) {
                return;
            }
            List<String> list = new ArrayList<>(alarmForwardRuleUserList);
            List<Object> users = (List<Object>) userFeign.queryUserByIdList(list);
            if (ListUtil.isEmpty(users)) {
                return;
            }
            List<User> userList = new ArrayList<>();
            List<String> arrayList = new ArrayList<>();
            users.forEach(userObject -> {
                User getUser = JSONArray.toJavaObject((JSON) JSONArray.toJSON(userObject), User.class);
                userList.add(getUser);
            });
            if (ListUtil.isEmpty(userList)) {
                return;
            }
            userList.forEach(user -> {
                arrayList.add(user.getUserName());
            });
            alarmForwardRule.setAlarmForwardRuleUserName(arrayList);
        });
    }

    /**
     * 查询设施类型
     *
     * @param alarmForwardRules 告警远程通知信息
     */
    private void selectDeviceType(List<AlarmForwardRule> alarmForwardRules) {
        alarmForwardRules.forEach((AlarmForwardRule alarmForwardRule) -> {
            List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypeList = alarmForwardRule.getAlarmForwardRuleDeviceTypeList();
            if (ListUtil.isEmpty(alarmForwardRuleDeviceTypeList)) {
                return;
            }
            List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceTypes =
                    alarmForwardRuleDeviceTypeDao.queryAlarmForwardRuleDeviceTypeById(alarmForwardRule.getId());
            alarmForwardRule.setAlarmForwardRuleDeviceTypeList(alarmForwardRuleDeviceTypes);
        });
    }

    /**
     * 查询设施类型
     *
     * @param alarmForwardRules 告警远程通知信息
     */
    private void selectAlarmLevel(List<AlarmForwardRule> alarmForwardRules) {
        alarmForwardRules.forEach((AlarmForwardRule alarmForwardRule) -> {
            List<AlarmForwardRuleLevel> alarmForwardRuleLevleList = alarmForwardRule.getAlarmForwardRuleLevels();
            if (ListUtil.isEmpty(alarmForwardRuleLevleList)) {
                return;
            }
            List<AlarmForwardRuleLevel> alarmForwardRuleLevels =
                    alarmForwardRuleLevelDao.queryAlarmFilterRuleAlarmLevelById(alarmForwardRule.getId());
            alarmForwardRule.setAlarmForwardRuleLevels(alarmForwardRuleLevels);
        });
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
        alarmForwardRuleDeviceType.forEach(alarmForwardRuleDeviceTypeOne -> {
            alarmForwardRuleDeviceTypeOne.setRuleId(alarmForwardRule.getId());
        });
        return alarmForwardRuleDeviceTypeDao.addAlarmForwardRuleDeviceType(alarmForwardRuleDeviceType);
    }

    /**
     * 新增告警级别
     *
     * @param alarmForwardRule 告警id
     */
    private Integer addAlarmLevel(AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleLevel> alarmLevels = alarmForwardRule.getAlarmForwardRuleLevels();
        alarmLevels.forEach(alarmForwardRuleLevel -> {
            alarmForwardRuleLevel.setRuleId(alarmForwardRule.getId());
        });
        return alarmForwardRuleLevelDao.addAlarmLevel(alarmLevels);
    }

    /**
     * 修改日志记录
     *
     * @param ids   用户id
     * @param model 模板
     * @param name  名称
     */
    private void updateLog(String[] ids, String model, String name) {
        for (String id : ids) {
            AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.selectById(id);
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmForwardRule.getId());
            // 操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 删除日志记录
     *
     * @param ids   用户id
     * @param model 模板
     * @param name  名称
     */
    private void deleteLog(String[] ids, String model, String name) {
        for (String id : ids) {
            AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.selectById(id);
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmForwardRule.getId());
            // 操作为删除
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

}
