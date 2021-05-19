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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
 * @since 2019-03-01
 */
@Slf4j
@Service
public class AlarmFilterRuleServiceImpl extends ServiceImpl<AlarmFilterRuleDao, AlarmFilterRule> implements AlarmFilterRuleService {

    /**
     * 注入dao
     */
    @Autowired
    private AlarmFilterRuleDao alarmFilterRuleDao;

    /**
     * 日志
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 告警名称dao
     */
    @Autowired
    private AlarmFilterRuleNameDao alarmFilterRuleNameDao;

    /**
     * 告警源dao
     */
    @Autowired
    private AlarmFilterRuleSourceDao alarmFilterRuleSourceDao;

    /**
     * 设施api
     */
    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 告警名称
     */
    @Autowired
    private AlarmNameDao alarmNameDao;

    /**
     * 查询告警过滤列表信息
     *
     * @param queryCondition 查询条件信息
     * @return 告警过滤列表信息
     */
    @Override
    public Result queryAlarmFilterRuleList(QueryCondition<AlarmFilterRuleDto> queryCondition) {
        AlarmFilterRuleDto alarmFilterRuleDto = queryCondition.getBizCondition();
        // 获取当前用户
        String userId = RequestInfoUtils.getUserId();
        if (!userId.equals(AppConstant.ALARM_YI)) {
            alarmFilterRuleDto.setUserId(userId);
        }
        List<AlarmFilterRule> alarmFilterRules = alarmFilterRuleDao.queryAlarmFilterRuleList(alarmFilterRuleDto);
        // 查询告警设施
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
        RedisUtils.set(AppConstant.REDIS_FILTER, collect);
        return ResultUtils.success(collect);
    }

    /**
     * 查询告警过滤列表信息
     *
     * @return 告警过滤列表信息
     */
    @Override
    public List<AlarmFilterRule> queryAlarmFilterRuleListFeign() {
        AlarmFilterRuleDto alarmFilterRuleDto = new AlarmFilterRuleDto();
        // 获取当前用户
        String userId = RequestInfoUtils.getUserId();
        if (!userId.equals(AppConstant.ALARM_YI)) {
            alarmFilterRuleDto.setUserId(userId);
        }
        List<AlarmFilterRule> alarmFilterRules = alarmFilterRuleDao.queryAlarmFilterRuleList(alarmFilterRuleDto);
        // 查询告警设施
        selectAlarmSource(alarmFilterRules);
        selectAlarmName(alarmFilterRules);
        return alarmFilterRules;
    }

    /**
     * 根据id查询告警过滤信息
     *
     * @param id 过滤id
     * @return 告警过滤信息
     */
    @Override
    public Result queryAlarmFilterRuleById(String id) {
        List<AlarmFilterRule> alarmFilterRules = new ArrayList<>();
        if (RedisUtils.hasKey(AppConstant.REDIS_FILTER)) {
            alarmFilterRules = (List<AlarmFilterRule>) RedisUtils.get(AppConstant.REDIS_FILTER);
            if (ListUtil.isEmpty(alarmFilterRules)) {
                // 查询告警转工单信息
                alarmFilterRules = queryAlarmFilterById(id);
            } else {
                List<AlarmFilterRule> collect = alarmFilterRules.stream().filter(alarmFilterRule ->
                        id.equals(alarmFilterRule.getId())).collect(Collectors.toList());
                alarmFilterRules = collect;
            }
        } else {
            // 当redis为空的时候查询数据库
            alarmFilterRules = queryAlarmFilterById(id);
        }
        return ResultUtils.success(alarmFilterRules);
    }

    /**
     * 根据id查询告警过滤信息
     *
     * @param id 告警ID
     * @return 告警过滤信息
     */
    private List<AlarmFilterRule> queryAlarmFilterById(String id) {
        // 根据id查询告警过滤信息
        List<AlarmFilterRule> alarmFilterRules = alarmFilterRuleDao.queryAlarmFilterRuleById(id);
        if (ListUtil.isEmpty(alarmFilterRules)) {
            return alarmFilterRules;
        }
        // 查询告警设施
        selectAlarmSource(alarmFilterRules);
        selectAlarmName(alarmFilterRules);
        return alarmFilterRules;
    }

    /**
     * 新增告警过滤信息
     *
     * @param alarmFilterRule 告警过滤信息
     * @return 判断结果
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
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(AppConstant.ALARM_FILTER_ADD_FALLS));
        }
        String id = NineteenUUIDUtils.uuid();
        alarmFilterRule.setId(id);
        // 获取当前用户id
        String userId = RequestInfoUtils.getUserId();
        alarmFilterRule.setUserId(userId);
        // 获取当前用户
        String userName = RequestInfoUtils.getUserName();
        alarmFilterRule.setOperationUser(userName);
        // 创建时间
        long timeMillis = System.currentTimeMillis();
        alarmFilterRule.setCreateTime(timeMillis);
        alarmFilterRule.setCreateUser(userName);
        alarmFilterRule.setUpdateUser(userName);
        // 新增告警过滤
        Integer insert = alarmFilterRuleDao.insert(alarmFilterRule);
        if (insert != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FILTER_ADD_FAILED));
        }
        // 新增告警名称信息
        Set<String> alarmFilterRuleNameList = alarmFilterRule.getAlarmFilterRuleNameList();
        Integer addAlarmName = addAlarmName(alarmFilterRuleNameList, alarmFilterRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_NAME_FAILED));
        }
        // 新增告警源id
        Set<String> alarmFilterRuleSourceList = alarmFilterRule.getAlarmFilterRuleSourceList();
        Integer addSource = addSource(alarmFilterRuleSourceList, alarmFilterRule);
        if (addSource == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_SOURES_FAILEDS));
        }
        RedisUtils.remove(AppConstant.REDIS_FILTER);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FILTER_ADD_SUCCESS));
    }

    /**
     * 删除告警过滤信息
     *
     * @param array 告警过滤id
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchDeleteAlarmFilterRule(String[] array) {
        Integer integer = alarmFilterRuleDao.deleteAlarmFilterRule(array);
        if (!integer.equals(array.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FILTER_DELETE_FAILED));
        }
        Integer ruleName = alarmFilterRuleNameDao.batchDeleteAlarmFilterRuleName(array);
        if (ruleName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_ALARM_NAME_FAILED));
        }
        Integer ruleSource = alarmFilterRuleSourceDao.batchDeleteAlarmFilterRuleSource(array);
        if (ruleSource == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_SOURES_FAILEDS));
        }
        RedisUtils.remove(AppConstant.REDIS_FILTER);
        // 记录日志
        deleteLog(array, AppConstant.ALARM_LOG_TWO, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FILTER_DELETE_SUCCESS));
    }

    /**
     * 修改告警过滤信息
     *
     * @param alarmFilterRule 告警过滤信息
     * @return 判断结果
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
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(AppConstant.ALARM_FILTER_ADD_FALLS));
        }
        // 获取用户信息
        String userName = RequestInfoUtils.getUserName();
        alarmFilterRule.setUpdateUser(userName);
        // 修改时间
        long timeMillis = System.currentTimeMillis();
        alarmFilterRule.setUpdateTime(timeMillis);
        Integer integer = alarmFilterRuleDao.updateById(alarmFilterRule);
        // 修改告警名称
        Set<String> alarmFilterRuleNameList = alarmFilterRule.getAlarmFilterRuleNameList();
        alarmFilterRuleNameDao.deleteAlarmFilterRuleName(alarmFilterRule.getId());
        Integer addAlarmName = addAlarmName(alarmFilterRuleNameList, alarmFilterRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_NAME_FAILED));
        }
        // 修改设施
        Set<String> alarmFilterRuleSourceList = alarmFilterRule.getAlarmFilterRuleSourceList();
        alarmFilterRuleSourceDao.deleteAlarmFilterRuleSource(alarmFilterRule.getId());
        Integer addSource = addSource(alarmFilterRuleSourceList, alarmFilterRule);
        if (addSource == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_SOURES_FAILEDS));
        }
        if (integer != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FILTER_UPDATE_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_FILTER);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FILTER_UPDATE_SUCCESS));
    }

    /**
     * 修改告警过滤存库信息
     *
     * @param stored  存库
     * @param idArray 用户id
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmFilterRuleStored(Integer stored, String[] idArray) {
        Integer integer = alarmFilterRuleDao.updateAlarmFilterRuleStored(stored, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FILTER_STORED_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_FILTER);
        updateLog(idArray, AppConstant.ALARM_LOG_FOUR, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FILTER_STORED_SUCCESS));
    }

    /**
     * 修改告警过滤状态信息
     *
     * @param status  状态
     * @param idArray 用户id
     * @return 判断结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchUpdateAlarmFilterRuleStatus(Integer status, String[] idArray) {
        Integer integer = alarmFilterRuleDao.updateAlarmFilterRuleStatus(status, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_FILTER_STUTES_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_FILTER);
        // 记录日志
        updateLog(idArray, AppConstant.ALARM_LOG_THREE, AppConstant.ALARM_RULE);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_FILTER_STUTES_SUCCESS));
    }

    /**
     * 查询当前告警信息是否过被过滤
     *
     * @param alarmFilterConditionList 过滤条件
     * @return 告警过滤信息
     */
    @Override
    public AlarmFilterRule queryAlarmIsIncludedFeign(List<AlarmFilterCondition> alarmFilterConditionList) {
        AlarmFilterRule alarmFilterRule = null;
        // 查询启动的告警过滤信息
        List<AlarmFilterRule> list = alarmFilterRuleDao.queryAlarmFilterRuleLists();
        for (int i = 0; i < list.size(); i++) {
            Boolean flag = true;
            String id = list.get(i).getId();
            for (AlarmFilterCondition alarmFilterCondition : alarmFilterConditionList) {
                alarmFilterCondition.setId(id);
                List<AlarmFilterRule> alarmFilterRules = alarmFilterRuleDao.queryAlarmIsIncludedFeign(alarmFilterCondition);
                if (ListUtil.isEmpty(alarmFilterRules)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                alarmFilterRule = list.get(i);
                break;
            }
        }
        return alarmFilterRule;
    }

    /**
     * 查询告警设施信息
     *
     * @param alarmFilterRules 告警过滤信息
     */
    private void selectAlarmSource(List<AlarmFilterRule> alarmFilterRules) {
        alarmFilterRules.forEach((AlarmFilterRule alarmFilterRuleOne) -> {
            Set<String> alarmFilterRuleSourceList = alarmFilterRuleOne.getAlarmFilterRuleSourceList();
            if (ListUtil.isSetEmpty(alarmFilterRuleSourceList)) {
                return;
            }
            // set转数组
            String[] strings = alarmFilterRuleSourceList.toArray(new String[alarmFilterRuleSourceList.size()]);
            List<String> list = new ArrayList<>();
            // 批量查询设施信息
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
     * 查询告警名称
     *
     * @param alarmFilterRules
     */
    private void selectAlarmName(List<AlarmFilterRule> alarmFilterRules) {
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
                list.add(alarmName.getAlarmName());
            });
            alarmFilterRule.setAlarmFilterRuleNames(list);
        });
    }

    /**
     * 新增告警名称
     *
     * @param alarmFilterRuleNameList 告警名称
     * @param alarmFilterRule         告警过滤信息
     * @return 告警名称信息
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
     * 新增告警源
     *
     * @param alarmFilterRuleSourceList 告警源id
     * @param alarmFilterRule           告警过滤信息
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
     * 修改日志记录
     *
     * @param ids   用户id
     * @param model 模板
     * @param name  名称
     */
    private void updateLog(String[] ids, String model, String name) {
        for (String id : ids) {
            AlarmFilterRule alarmFilterRule = alarmFilterRuleDao.selectById(id);
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmFilterRule.getRuleName());
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
            AlarmFilterRule alarmFilterRule = alarmFilterRuleDao.selectById(id);
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmFilterRule.getRuleName());
            // 操作为删除
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }


}
