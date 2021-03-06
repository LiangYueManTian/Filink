package com.fiberhome.filink.alarmsetserver.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleName;
import com.fiberhome.filink.alarmsetserver.bean.AlarmFilterRuleSource;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.AlarmFilterRuleDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmFilterRuleNameDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmFilterRuleSourceDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmFilterRuleService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-01
 */
@Slf4j
@Service
public class AlarmFilterRuleServiceImpl extends ServiceImpl<AlarmFilterRuleDao, AlarmFilterRule> implements AlarmFilterRuleService {

    /**
     * ??????dao
     */
    @Autowired
    private AlarmFilterRuleDao alarmFilterRuleDao;

    /**
     * ??????
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * ????????????dao
     */
    @Autowired
    private AlarmFilterRuleNameDao alarmFilterRuleNameDao;

    /**
     * ?????????dao
     */
    @Autowired
    private AlarmFilterRuleSourceDao alarmFilterRuleSourceDao;

    /**
     * ??????api
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * ????????????
     */
    @Autowired
    private AlarmNameDao alarmNameDao;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ??????????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmFilterRuleList(QueryCondition<AlarmFilterRuleDto> queryCondition) {
        AlarmFilterRuleDto alarmFilterRuleDto = queryCondition.getBizCondition();
        // ??????????????????
        String userId = RequestInfoUtils.getUserId();
        if (!userId.equals(AppConstant.ALARM_YI)) {
            alarmFilterRuleDto.setUserId(userId);
        }
        List<AlarmFilterRule> alarmFilterRules = alarmFilterRuleDao.queryAlarmFilterRuleList(alarmFilterRuleDto);
        // ??????????????????
        selectAlarmSource(alarmFilterRules);
        selectAlarmName(alarmFilterRules);
        List<String> list = new ArrayList<>();
        alarmFilterRules.forEach((AlarmFilterRule alarmFilterRule) -> {
            if (ListUtil.isEmpty(alarmFilterRule.getAlarmFilterRuleSourceName())) {
                list.add(alarmFilterRule.getId());
            }
        });
        if (!ListUtil.isEmpty(list)) {
            String[] strings = list.toArray(new String[list.size()]);
            this.batchDeleteAlarmFilterRule(strings);
        }
        List<AlarmFilterRule> collect = alarmFilterRules.stream().filter(alarmFilterRule ->
                alarmFilterRule.getAlarmFilterRuleSourceName() != null).collect(Collectors.toList());
        RedisUtils.set(AppConstant.ALARM_REDIS_FILTER, collect);
        return ResultUtils.success(collect);
    }

    /**
     * ??????id????????????????????????
     *
     * @param id ??????id
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmFilterRuleById(String id) {
        List<AlarmFilterRule> alarmFilterRules = new ArrayList<>();
        if (RedisUtils.hasKey(AppConstant.ALARM_REDIS_FILTER)) {
            alarmFilterRules = (List<AlarmFilterRule>) RedisUtils.get(AppConstant.ALARM_REDIS_FILTER);
            if (ListUtil.isEmpty(alarmFilterRules)) {
                // ???????????????????????????
                alarmFilterRules = queryAlarmFilterById(id);
            } else {
                List<AlarmFilterRule> collect = alarmFilterRules.stream().filter(alarmFilterRule ->
                        id.equals(alarmFilterRule.getId())).collect(Collectors.toList());
                alarmFilterRules = collect;
            }
        } else {
            // ???redis??????????????????????????????
            alarmFilterRules = queryAlarmFilterById(id);
        }
        return ResultUtils.success(alarmFilterRules);
    }

    /**
     * ??????id????????????????????????
     *
     * @param id ??????ID
     * @return ??????????????????
     */
    private List<AlarmFilterRule> queryAlarmFilterById(String id) {
        // ??????id????????????????????????
        List<AlarmFilterRule> alarmFilterRules = alarmFilterRuleDao.queryAlarmFilterRuleById(id);
        if (ListUtil.isEmpty(alarmFilterRules)) {
            return alarmFilterRules;
        }
        // ??????????????????
        selectAlarmSource(alarmFilterRules);
        selectAlarmName(alarmFilterRules);
        return alarmFilterRules;
    }

    /**
     * ????????????????????????
     *
     * @param alarmFilterRule ??????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_ADD_FILTER,
            dataGetColumnName = "ruleName", dataGetColumnId = "id")
    @Override
    public Result addAlarmFilterRule(AlarmFilterRule alarmFilterRule) {
        List<AlarmFilterRule> list = alarmFilterRuleDao.queryAlarmFilterRuleNames();
        AlarmFilterRule alarmFilterRuleTwo = list.stream().filter(alarmFilterRuleOne ->
                alarmFilterRuleOne.getRuleName().equals(alarmFilterRule.getRuleName())).findFirst().orElse(null);
        if (alarmFilterRuleTwo != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_ADD_FALLS));
        }
        String id = NineteenUUIDUtils.uuid();
        alarmFilterRule.setId(id);
        // ??????????????????id
        String userId = RequestInfoUtils.getUserId();
        alarmFilterRule.setUserId(userId);
        // ??????????????????
        String userName = RequestInfoUtils.getUserName();
        alarmFilterRule.setOperationUser(userName);
        // ????????????
        long timeMillis = System.currentTimeMillis();
        alarmFilterRule.setCreateTime(timeMillis);
        alarmFilterRule.setCreateUser(userName);
        alarmFilterRule.setUpdateUser(userName);
        // ??????????????????
        Integer insert = alarmFilterRuleDao.insert(alarmFilterRule);
        if (insert != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FILTER_ADD_FAILED));
        }
        // ????????????????????????
        Set<String> alarmFilterRuleNameList = alarmFilterRule.getAlarmFilterRuleNameList();
        Integer addAlarmName = addAlarmName(alarmFilterRuleNameList, alarmFilterRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_NAME_FAILED));
        }
        // ???????????????id
        Set<String> alarmFilterRuleSourceList = alarmFilterRule.getAlarmFilterRuleSourceList();
        Integer addSource = addSource(alarmFilterRuleSourceList, alarmFilterRule);
        if (addSource == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_SOURES_FAILEDS));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_FILTER);
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_ADD_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param array ????????????id
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchDeleteAlarmFilterRule(String[] array) {
        Integer integer = alarmFilterRuleDao.deleteAlarmFilterRule(array);
        if (!integer.equals(array.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FILTER_DELETE_FAILED));
        }
        Integer ruleName = alarmFilterRuleNameDao.batchDeleteAlarmFilterRuleName(array);
        if (ruleName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_ALARM_NAME_FAILED));
        }
        Integer ruleSource = alarmFilterRuleSourceDao.batchDeleteAlarmFilterRuleSource(array);
        if (ruleSource == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_SOURES_FAILEDS));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_FILTER);
        // ????????????
        deleteLog(array, AppConstant.ALARM_LOG_TWO, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_DELETE_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param alarmFilterRule ??????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.UPDATE, logType = "1", functionCode = AppConstant.ALARM_LOG_UPDATE_FILTER,
            dataGetColumnName = "ruleName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmFilterRule(AlarmFilterRule alarmFilterRule) {
        List<AlarmFilterRule> list = alarmFilterRuleDao.queryAlarmFilterRuleName(alarmFilterRule.getId());
        AlarmFilterRule alarmFilterRuleTwo = list.stream().filter(alarmFilterRuleOne ->
                alarmFilterRuleOne.getRuleName().equals(alarmFilterRule.getRuleName())).findFirst().orElse(null);
        if (alarmFilterRuleTwo != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_ADD_FALLS));
        }
        // ??????????????????
        String userName = RequestInfoUtils.getUserName();
        alarmFilterRule.setUpdateUser(userName);
        // ????????????
        long timeMillis = System.currentTimeMillis();
        alarmFilterRule.setUpdateTime(timeMillis);
        Integer integer = alarmFilterRuleDao.updateById(alarmFilterRule);
        // ??????????????????
        Set<String> alarmFilterRuleNameList = alarmFilterRule.getAlarmFilterRuleNameList();
        alarmFilterRuleNameDao.deleteAlarmFilterRuleName(alarmFilterRule.getId());
        Integer addAlarmName = addAlarmName(alarmFilterRuleNameList, alarmFilterRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_NAME_FAILED));
        }
        // ????????????
        Set<String> alarmFilterRuleSourceList = alarmFilterRule.getAlarmFilterRuleSourceList();
        alarmFilterRuleSourceDao.deleteAlarmFilterRuleSource(alarmFilterRule.getId());
        Integer addSource = addSource(alarmFilterRuleSourceList, alarmFilterRule);
        if (addSource == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_SOURES_FAILEDS));
        }
        if (integer != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FILTER_UPDATE_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_FILTER);
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_UPDATE_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param stored  ??????
     * @param idArray ??????id
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmFilterRuleStored(Integer stored, String[] idArray) {
        Integer integer = alarmFilterRuleDao.updateAlarmFilterRuleStored(stored, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FILTER_STORED_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_FILTER);
        updateLog(idArray, AppConstant.ALARM_LOG_FOUR, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_STORED_SUCCESS));
    }

    /**
     * ??????????????????????????????
     *
     * @param status  ??????
     * @param idArray ??????id
     * @return ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmFilterRuleStatus(Integer status, String[] idArray) {
        Integer integer = alarmFilterRuleDao.updateAlarmFilterRuleStatus(status, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_FILTER_STUTES_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_FILTER);
        // ????????????
        updateLog(idArray, AppConstant.ALARM_LOG_THREE, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_FILTER_STUTES_SUCCESS));
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param alarmFilterConditionList ????????????
     * @return ??????????????????
     */
    @Override
    public List<AlarmFilterRule> queryAlarmIsIncludedFeign(List<AlarmFilterCondition> alarmFilterConditionList) {
        List<AlarmFilterRule> alarmFilterRuleList = new ArrayList<>();
        // ?????????????????????????????????
        List<AlarmFilterRule> list = alarmFilterRuleDao.queryAlarmFilterRuleLists();
        for (int i = 0; i < list.size(); i++) {
            String id = list.get(i).getId();
            for (AlarmFilterCondition alarmFilterCondition : alarmFilterConditionList) {
                alarmFilterCondition.setId(id);
                AlarmFilterRule alarmFilterRules = alarmFilterRuleDao.queryAlarmIsIncludedFeign(alarmFilterCondition);
                if (ObjectUtils.isEmpty(alarmFilterRules)) {
                    break;
                }
                alarmFilterRuleList.add(alarmFilterRules);
            }
        }
        if (ListUtil.isEmpty(alarmFilterRuleList)) {
            return null;
        }
        return alarmFilterRuleList;
    }

    /**
     * ????????????????????????
     *
     * @param alarmFilterRules ??????????????????
     */
    protected void selectAlarmSource(List<AlarmFilterRule> alarmFilterRules) {
        alarmFilterRules.forEach((AlarmFilterRule alarmFilterRuleOne) -> {
            Set<String> alarmFilterRuleSourceList = alarmFilterRuleOne.getAlarmFilterRuleSourceList();
            if (ListUtil.isSetEmpty(alarmFilterRuleSourceList)) {
                return;
            }
            // set?????????
            String[] strings = alarmFilterRuleSourceList.toArray(new String[alarmFilterRuleSourceList.size()]);
            List<String> list = new ArrayList<>();
            // ????????????????????????
            List<DeviceInfoDto> deviceByIds = deviceFeign.getDeviceByIds(strings);
            if (ListUtil.isEmpty(deviceByIds)) {
                return;
            }
            deviceByIds.forEach(deviceInfoDto -> {
                list.add(deviceInfoDto.getDeviceName());
            });
            alarmFilterRuleOne.setAlarmFilterRuleSourceName(list);
        });
    }

    /**
     * ??????????????????
     *
     * @param alarmFilterRules
     */
    protected void selectAlarmName(List<AlarmFilterRule> alarmFilterRules) {
        alarmFilterRules.forEach((AlarmFilterRule alarmFilterRule) -> {
            Set<String> alarmFilterRuleNameList = alarmFilterRule.getAlarmFilterRuleNameList();
            if (ListUtil.isSetEmpty(alarmFilterRuleNameList)) {
                return;
            }
            String[] strings = alarmFilterRuleNameList.toArray(new String[alarmFilterRuleNameList.size()]);
            List<AlarmName> alarmNameList = alarmNameDao.selectByIds(strings);
            if (ListUtil.isEmpty(alarmNameList)) {
                return;
            }
            List<String> list = new ArrayList<>();
            alarmNameList.forEach(alarmName -> {
                list.add(alarmName.getAlarmCode());
            });
            alarmFilterRule.setAlarmFilterRuleNames(list);
        });
    }

    /**
     * ??????????????????
     *
     * @param alarmFilterRuleNameList ????????????
     * @param alarmFilterRule         ??????????????????
     * @return ??????????????????
     */
    private Integer addAlarmName(Set<String> alarmFilterRuleNameList, AlarmFilterRule alarmFilterRule) {
        List<AlarmFilterRuleName> list = new ArrayList<>();
        alarmFilterRuleNameList.forEach(alarmFilterRuleName -> {
            AlarmFilterRuleName alarmFilterRuleNameOne = new AlarmFilterRuleName();
            alarmFilterRuleNameOne.setRuleId(alarmFilterRule.getId());
            alarmFilterRuleNameOne.setAlarmNameId(alarmFilterRuleName);
            list.add(alarmFilterRuleNameOne);
        });
        return alarmFilterRuleNameDao.addAlarmFilterRuleName(list);
    }

    /**
     * ???????????????
     *
     * @param alarmFilterRuleSourceList ?????????id
     * @param alarmFilterRule           ??????????????????
     */
    private Integer addSource(Set<String> alarmFilterRuleSourceList, AlarmFilterRule alarmFilterRule) {
        List<AlarmFilterRuleSource> listOne = new ArrayList<>();
        alarmFilterRuleSourceList.forEach(string -> {
            AlarmFilterRuleSource alarmFilterRuleSource = new AlarmFilterRuleSource();
            alarmFilterRuleSource.setRuleId(alarmFilterRule.getId());
            alarmFilterRuleSource.setAlarmSource(string);
            listOne.add(alarmFilterRuleSource);
        });
        return alarmFilterRuleSourceDao.addAlarmFilterRuleSource(listOne);
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
            AlarmFilterRule alarmFilterRule = alarmFilterRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // ??????????????????
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // ??????????????????
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmFilterRule.getRuleName());
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
            systemLanguageUtil.querySystemLanguage();
            AlarmFilterRule alarmFilterRule = alarmFilterRuleDao.selectById(id);
            // ??????????????????
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // ??????????????????
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmFilterRule.getRuleName());
            // ???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // ??????????????????
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }


}
