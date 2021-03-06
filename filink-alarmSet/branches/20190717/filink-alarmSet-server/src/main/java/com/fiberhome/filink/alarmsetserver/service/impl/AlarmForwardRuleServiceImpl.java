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
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
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
 * ???????????????
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-25
 */
@Slf4j
@Service
public class AlarmForwardRuleServiceImpl extends ServiceImpl<AlarmForwardRuleDao, AlarmForwardRule> implements AlarmForwardRuleService {

    /**
     * ????????????dao
     */
    @Autowired
    private AlarmForwardRuleDao alarmForwardRuleDao;

    /**
     * ?????????dao
     */
    @Autowired
    private AlarmForwardRuleUserDao alarmForwardRuleUserDao;

    /**
     * ??????api
     */
    @Autowired
    private AlarmForwardRuleAreaDao alarmForwardRuleAreaDao;

    /**
     * ????????????dao
     */
    @Autowired
    private AlarmForwardRuleDeviceTypeDao alarmForwardRuleDeviceTypeDao;

    /**
     * ????????????
     */
    @Autowired
    private AlarmForwardRuleLevelDao alarmForwardRuleLevelDao;

    /**
     * ??????Api
     */
    @Autowired
    private AreaFeign areaFeign;

    /**
     * ??????feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * ??????
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ???????????????service
     */
    @Autowired
    private AlarmOrderRuleService alarmOrderRuleService;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ????????????????????????????????????
     *
     * @param queryCondition ??????????????????dto
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmForwardRuleList(QueryCondition<AlarmForwardRuleDto> queryCondition) {
        AlarmForwardRuleDto alarmForwardRuleDto = queryCondition.getBizCondition();
        boolean flag = addAuto(alarmForwardRuleDto);
        List<AlarmForwardRule> alarmForwardRules = new ArrayList<>();
        if (flag) {
            alarmForwardRules = alarmForwardRuleDao.queryAlarmForwardRuleList(alarmForwardRuleDto);
            // ?????????????????????
            selectUserName(alarmForwardRules);
            // ????????????
            selectAlarmAreaName(alarmForwardRules);
            // ??????????????????
            selectDeviceType(alarmForwardRules);
            // ??????????????????
            selectAlarmLevel(alarmForwardRules);
        }
        return ResultUtils.success(alarmForwardRules);
    }

    /**
     * ??????????????????????????????
     *
     * @param alarmForwardRuleDto ????????????
     */
    private boolean addAuto(AlarmForwardRuleDto alarmForwardRuleDto) {
        User user = alarmOrderRuleService.getUser();
        if (AppConstant.ALARM_YI.equals(user.getId())) {
            return true;
        }
        List<String> deviceTypes = alarmOrderRuleService.getDeviceTypes(user);
        if (!ListUtil.isEmpty(deviceTypes)) {
            alarmForwardRuleDto.setDeviceTypeId(deviceTypes);
        } else {
            return false;
        }
        List<String> list = alarmOrderRuleService.getUserAreaIds(user);
        if (!ListUtil.isEmpty(list)) {
            Set<String> areaIds = new HashSet<>(list);
            alarmForwardRuleDto.setAlarmForwardRuleAreaList(areaIds);
        } else {
            return false;
        }
        return true;
    }

    /**
     * ??????id????????????????????????
     *
     * @param id ??????id
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmForwardId(String id) {
        List<AlarmForwardRule> alarmForwardRules = alarmForwardRuleDao.queryAlarmForwardId(id);
        if (ListUtil.isEmpty(alarmForwardRules)) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_FAILED));
        }
        // ?????????????????????
        selectUserName(alarmForwardRules);
        // ????????????
        selectAlarmAreaName(alarmForwardRules);
        return ResultUtils.success(alarmForwardRules);
    }

    /**
     * ??????????????????????????????
     *
     * @param alarmForwardRule ????????????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_ADD_FORWARD,
            dataGetColumnName = "ruleName", dataGetColumnId = "id")
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
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_ADD_FAILED));
        }
        // ???????????????
        Set<String> alarmForwardRuleUser = alarmForwardRule.getAlarmForwardRuleUserList();
        Integer addUser = addUser(alarmForwardRuleUser, alarmForwardRule);
        if (addUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_USER_FAILED));
        }
        // ????????????
        Set<String> alarmForwardRuleArea = alarmForwardRule.getAlarmForwardRuleAreaList();
        Integer area = addArea(alarmForwardRuleArea, alarmForwardRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_AREA_FAILED));
        }
        // ??????????????????
        Integer addDeviceType = addDeviceType(alarmForwardRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_DEVICE_FAILED));
        }
        // ??????????????????
        Integer addAlarmLevel = addAlarmLevel(alarmForwardRule);
        if (addAlarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_LEVEL_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_ADD_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param array ??????????????????id
     * @return ????????????
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
        // ????????????
        deleteLog(array, AppConstant.ALARM_LOG_FIVE, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_DELETE_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param alarmForwardRule ????????????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_UPDATE_FORWARD,
            dataGetColumnName = "ruleName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmForwardRule(AlarmForwardRule alarmForwardRule) {
        // ??????????????????
        String userName = RequestInfoUtils.getUserName();
        alarmForwardRule.setUpdateUser(userName);
        long date = System.currentTimeMillis();
        alarmForwardRule.setUpdateTime(date);
        // ????????????????????????
        Integer integer = alarmForwardRuleDao.updateById(alarmForwardRule);
        if (integer != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_UPDATE_FAILED));
        }
        // ???????????????
        Set<String> alarmForwardRuleUser = alarmForwardRule.getAlarmForwardRuleUserList();
        alarmForwardRuleUserDao.deleteAlarmForwardRuleUser(alarmForwardRule.getId());
        Integer addUser = addUser(alarmForwardRuleUser, alarmForwardRule);
        if (addUser == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_USER_FAILED));
        }
        // ????????????
        Set<String> alarmForwardRuleArea = alarmForwardRule.getAlarmForwardRuleAreaList();
        alarmForwardRuleAreaDao.deleteAlarmForwardRuleArea(alarmForwardRule.getId());
        Integer area = addArea(alarmForwardRuleArea, alarmForwardRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_AREA_FAILED));
        }
        // ??????????????????
        alarmForwardRuleDeviceTypeDao.deleteAlarmDeviceType(alarmForwardRule.getId());
        Integer deviceType = addDeviceType(alarmForwardRule);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_DEVICE_FAILED));
        }
        // ??????????????????
        alarmForwardRuleLevelDao.deleteAlarmLevel(alarmForwardRule.getId());
        Integer alarmLevel = addAlarmLevel(alarmForwardRule);
        if (alarmLevel == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_LEVEL_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_UPDATE_SUCCESS));
    }

    /**
     * ????????????????????????????????????
     *
     * @param status  ??????????????????????????????
     * @param idArray ??????id
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmForwardRuleStatus(Integer status, String[] idArray) {
        Integer integer = alarmForwardRuleDao.updateAlarmForwardRuleStatus(status, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_STUTES_FAILED));
        }
        // ????????????
        updateLog(idArray, AppConstant.ALARM_LOG_SIX, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_STUTES_SUCCESS));
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param pushType ??????????????????
     * @param idArray  ??????id
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmForwardRulePushType(Integer pushType, String[] idArray) {
        Integer integer = alarmForwardRuleDao.updateAlarmForwardRulePushType(pushType, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_PUSHTYPE_FAILED));
        }
        // ????????????
        updateLog(idArray, AppConstant.ALARM_LOG_SEVEN, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FORWARD_PUSHTYPE_SUCCESS));
    }

    /**
     * ????????????????????????????????????
     *
     * @param alarmForwardCondition ??????????????????
     * @return ????????????????????????
     */
    @Override
    public List<AlarmForwardRule> queryAlarmForwardRuleFeign(List<AlarmForwardCondition> alarmForwardCondition) {
        List<AlarmForwardRule> alarmForwardRuleLists = new ArrayList<>();
        List<AlarmForwardRule> list = alarmForwardRuleDao.queryAlarmForwardRuleLists();
        for (int i = 0; i < list.size(); i++) {
            String id = list.get(i).getId();
            //?????????????????????????????????????????????????????????????????????????????????
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
     * ????????????????????????????????????
     *
     * @param alarmForwardRules ????????????????????????
     */
    protected void selectAlarmAreaName(List<AlarmForwardRule> alarmForwardRules) {
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
     * ?????????????????????
     *
     * @param alarmForwardRules ????????????????????????
     */
    protected void selectUserName(List<AlarmForwardRule> alarmForwardRules) {
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
     * ??????????????????
     *
     * @param alarmForwardRules ????????????????????????
     */
    protected void selectDeviceType(List<AlarmForwardRule> alarmForwardRules) {
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
     * ??????????????????
     *
     * @param alarmForwardRules ????????????????????????
     */
    protected void selectAlarmLevel(List<AlarmForwardRule> alarmForwardRules) {
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
     * ???????????????
     *
     * @param alarmForwardRuleUser ?????????id
     * @param alarmForwardRule     ????????????????????????
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
     * ????????????
     *
     * @param alarmForwardRuleArea ??????id
     * @param alarmForwardRule     ????????????????????????
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
     * ??????????????????
     *
     * @param alarmForwardRule ????????????????????????
     */
    private Integer addDeviceType(AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleDeviceType> alarmForwardRuleDeviceType = alarmForwardRule.getAlarmForwardRuleDeviceTypeList();
        alarmForwardRuleDeviceType.forEach(alarmForwardRuleDeviceTypeOne -> {
            alarmForwardRuleDeviceTypeOne.setRuleId(alarmForwardRule.getId());
        });
        return alarmForwardRuleDeviceTypeDao.addAlarmForwardRuleDeviceType(alarmForwardRuleDeviceType);
    }

    /**
     * ??????????????????
     *
     * @param alarmForwardRule ??????id
     */
    private Integer addAlarmLevel(AlarmForwardRule alarmForwardRule) {
        List<AlarmForwardRuleLevel> alarmLevels = alarmForwardRule.getAlarmForwardRuleLevels();
        alarmLevels.forEach(alarmForwardRuleLevel -> {
            alarmForwardRuleLevel.setRuleId(alarmForwardRule.getId());
        });
        return alarmForwardRuleLevelDao.addAlarmLevel(alarmLevels);
    }

    /**
     * ??????????????????
     *
     * @param ids   ??????id
     * @param model ??????
     * @param name  ??????
     */
    private void updateLog(String[] ids, String model, String name) {
        for (String id : ids) {
            AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // ??????????????????
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // ??????????????????
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmForwardRule.getRuleName());
            // ???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
            // ??????????????????
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * ??????????????????
     *
     * @param ids   ??????id
     * @param model ??????
     * @param name  ??????
     */
    private void deleteLog(String[] ids, String model, String name) {
        for (String id : ids) {
            AlarmForwardRule alarmForwardRule = alarmForwardRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // ??????????????????
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // ??????????????????
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmForwardRule.getRuleName());
            // ???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // ??????????????????
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

}
