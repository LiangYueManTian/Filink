package com.fiberhome.filink.alarmsetserver.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.AlarmName;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderCondition;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRule;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleArea;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDeviceType;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleDto;
import com.fiberhome.filink.alarmsetserver.bean.AlarmOrderRuleName;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.AlarmNameDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleAreaDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleDeviceTypeDao;
import com.fiberhome.filink.alarmsetserver.dao.AlarmOrderRuleNameDao;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
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
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 告警转工单服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-02-27
 */
@Service
public class AlarmOrderRuleServiceImpl extends ServiceImpl<AlarmOrderRuleDao, AlarmOrderRule> implements AlarmOrderRuleService {

    /**
     * 告警转工单规则dao层
     */
    @Autowired
    private AlarmOrderRuleDao alarmOrderRuleDao;

    /**
     * 区域dao
     */
    @Autowired
    private AlarmOrderRuleAreaDao alarmOrderRuleAreaDao;

    /**
     * 告警名称dao
     */
    @Autowired
    private AlarmOrderRuleNameDao alarmOrderRuleNameDao;

    /**
     * 设施类型dao
     */
    @Autowired
    private AlarmOrderRuleDeviceTypeDao alarmOrderRuleDeviceTypeDao;

    /**
     * 区域Api
     */
    @Autowired
    private AreaFeign areaFeign;

    /**
     * 日志
     */
    @Autowired
    private LogProcess logProcess;

    /**
     * 用户feign
     */
    @Autowired
    private UserFeign userFeign;

    /**
     * 告警名称
     */
    @Autowired
    private AlarmNameDao alarmNameDao;

    /**
     * 查询告警转工单列表信息
     *
     * @param queryCondition 条件
     * @return 告警转工单列表信息
     */
    @Override
    public Result queryAlarmOrderRuleList(QueryCondition<AlarmOrderRuleDto> queryCondition) {
        AlarmOrderRuleDto alarmOrderRuleDto = queryCondition.getBizCondition();
        addAuth(alarmOrderRuleDto);
        List<AlarmOrderRule> alarmOrderRules = alarmOrderRuleDao.queryAlarmOrderRuleList(alarmOrderRuleDto);
        // 查询区域名称
        selectAlarmAreaName(alarmOrderRules);
        //查询告警名称
        selectAlarmName(alarmOrderRules);
        //查询设施类型
        selectDeviceType(alarmOrderRules);
        RedisUtils.set(AppConstant.REDIS_ORDER, alarmOrderRules);
        return ResultUtils.success(alarmOrderRules);
    }

    /**
     * 查询设施类型
     *
     * @param alarmOrderRules 告警远程通知信息
     */
    private void selectDeviceType(List<AlarmOrderRule> alarmOrderRules) {
        alarmOrderRules.forEach((AlarmOrderRule alarmOrderRule) -> {
            List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypeList = alarmOrderRule.getAlarmOrderRuleDeviceTypeList();
            if (ListUtil.isEmpty(alarmOrderRuleDeviceTypeList)) {
                return;
            }
            List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypes =
                    alarmOrderRuleDeviceTypeDao.queryAlarmOrderRuleDeviceTypeById(alarmOrderRule.getId());
            alarmOrderRule.setAlarmOrderRuleDeviceTypeList(alarmOrderRuleDeviceTypes);
        });
    }

    /**
     * 查询告警名称
     *
     * @param alarmOrderRules 告警转工单
     */
    private void selectAlarmName(List<AlarmOrderRule> alarmOrderRules) {
        alarmOrderRules.forEach((alarmOrderRule) -> {
            Set<String> alarmOrderRuleNameList = alarmOrderRule.getAlarmOrderRuleNameList();
            if (ListUtil.isSetEmpty(alarmOrderRuleNameList)) {
                return;
            }
            String[] strings = alarmOrderRuleNameList.toArray(new String[alarmOrderRuleNameList.size()]);
            List<AlarmName> alarmNameList = alarmNameDao.selectByIds(strings);
            if (ListUtil.isEmpty(alarmNameList)) {
                return;
            }
            List<String> list = new ArrayList<>();
            alarmNameList.forEach(alarmName -> {
                list.add(alarmName.getAlarmName());
            });
            alarmOrderRule.setAlarmOrderRuleNames(list);
        });
    }

    /**
     * 查询当前用户数据权限信息
     *
     * @param alarmOrderRuleDto 条件信息
     */
    private void addAuth(AlarmOrderRuleDto alarmOrderRuleDto) {
        User user = getUser();
        if (AppConstant.ALARM_YI.equals(user.getId())) {
            return;
        }
        // 获取用户设施类型
        List<String> deviceTypeIds = getDeviceTypes(user);
        if (!ListUtil.isEmpty(deviceTypeIds)) {
            alarmOrderRuleDto.setDeviceTypeId(deviceTypeIds);
        }
        // 获取用户区域
        List<String> list = getUserAreaIds(user);
        if (!ListUtil.isEmpty(list)) {
            Set<String> areaIds = new HashSet<>(list);
            alarmOrderRuleDto.setAlarmOrderRuleArea(areaIds);
        }
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @Override
    public User getUser() {
        String userId = RequestInfoUtils.getUserId();
        String token = RequestInfoUtils.getToken();
        // 获取用户信息
        Object object = userFeign.queryCurrentUser(userId, token);
        return JSONArray.toJavaObject((JSON) JSONArray.toJSON(object), User.class);
    }

    /**
     * 获取用户区域信息
     *
     * @param user 用户
     * @return 用户区域信息
     */
    @Override
    public List<String> getUserAreaIds(User user) {
        return user.getDepartment().getAreaIdList();
    }

    /**
     * 获取用户区设施类型信息
     *
     * @param user 用户
     * @return 用户区设施类型信息
     */
    @Override
    public List<String> getDeviceTypes(User user) {
        List<String> deviceTypes = new ArrayList<>();
        List<RoleDeviceType> roleDeviceTypes = user.getRole().getRoleDevicetypeList();
        roleDeviceTypes.forEach(roleDeviceType -> {
            deviceTypes.add(roleDeviceType.getDeviceTypeId());
        });
        return deviceTypes;
    }

    /**
     * 查询告警转工单信息
     *
     * @param id 告警id
     * @return 告警转工单信息
     */
    @Override
    public Result queryAlarmOrderRule(String id) {
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        if (RedisUtils.hasKey(AppConstant.REDIS_ORDER)) {
            alarmOrderRules = (List<AlarmOrderRule>) RedisUtils.get(AppConstant.REDIS_ORDER);
            if (ListUtil.isEmpty(alarmOrderRules)) {
                // 查询告警转工单信息
                alarmOrderRules = queryAlarmInfoById(id);
            } else {
                List<AlarmOrderRule> collect = alarmOrderRules.stream().filter(alarmOrderRule ->
                        id.equals(alarmOrderRule.getId())).collect(Collectors.toList());
                alarmOrderRules = collect;
            }
        } else {
            // 当redis为空的时候查询数据库
            alarmOrderRules = queryAlarmInfoById(id);
        }
        return ResultUtils.success(alarmOrderRules);
    }

    /**
     * 通过告警Id查询告警转工单信息
     *
     * @param id 告警ID
     * @return 告警转工单信息
     */
    private List<AlarmOrderRule> queryAlarmInfoById(String id) {
        // 通过ID查询告警转工单信息
        List<AlarmOrderRule> alarmOrderRules = alarmOrderRuleDao.queryAlarmOrderRuleById(id);
        if (ListUtil.isEmpty(alarmOrderRules)) {
            return alarmOrderRules;
        }
        // 查询区域名称
        selectAlarmAreaName(alarmOrderRules);
        return alarmOrderRules;
    }

    /**
     * 新增告警转工单信息
     *
     * @param alarmOrderRule 告警转工单信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_ADD_ORDER,
            dataGetColumnName = "orderName", dataGetColumnId = "id")
    @Override
    public Result addAlarmOrderRule(AlarmOrderRule alarmOrderRule) {
        // 获取id
        String id = NineteenUUIDUtils.uuid();
        alarmOrderRule.setId(id);
        // 获取当前用户
        String userName = RequestInfoUtils.getUserName();
        alarmOrderRule.setCreateUser(userName);
        alarmOrderRule.setUpdateUser(userName);
        long date = System.currentTimeMillis();
        alarmOrderRule.setCreateTime(date);
        Integer insert = alarmOrderRuleDao.insert(alarmOrderRule);
        if (insert != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_ORDER_ADD_FAILED));
        }
        // 新增区域
        Set<String> alarmOrderRuleArea = alarmOrderRule.getAlarmOrderRuleArea();
        Integer integer = addArea(alarmOrderRuleArea, alarmOrderRule);
        if (integer == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_AREA_FAILED));
        }
        // 新增设施类型
        Integer addDeviceType = addDeviceType(alarmOrderRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_DEVICE_FAILED));
        }
        // 新增告警名称
        Set<String> alarmOrderRuleNameList = alarmOrderRule.getAlarmOrderRuleNameList();
        Integer addAlarmName = addAlarmName(alarmOrderRuleNameList, alarmOrderRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ADD_ALARM_NAME_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_ORDER);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_ORDER_ADD_SUCCESS));
    }

    /**
     * 删除告警转工单信息
     *
     * @param array 告警转工单id
     * @return 判断信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteAlarmOrderRule(String[] array) {
        Integer integer = alarmOrderRuleDao.deleteAlarmOrderRule(array);
        if (!integer.equals(array.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_ORDER_DELETE_FAILED));
        }
        // 删除关联表
        Integer area = alarmOrderRuleAreaDao.batchDeleteAlarmOrderRuleArea(array);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_AREA_FAILED));
        }
        Integer deviceType = alarmOrderRuleDeviceTypeDao.batchDeleteAlarmOrderDeviceType(array);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_DEVICE_FAILED));
        }
        Integer ruleName = alarmOrderRuleNameDao.batchDeleteAlarmOrderRuleName(array);
        if (ruleName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.DELETE_ALARM_NAME_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_ORDER);
        // 记录日志
        deleteLog(array, AppConstant.ALARM_LOG_SERVEN, AppConstant.ALARM_ORDER);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_ORDER_DELETE_SUCCESS));
    }

    /**
     * 修改告警转工单信息
     *
     * @param alarmOrderRule 告警转工单信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AppConstant.UPDATE, logType = "1", functionCode = AppConstant.ALARM_LOG_UPDATE_ORDER,
            dataGetColumnName = "orderName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmOrderRule(AlarmOrderRule alarmOrderRule) {
        String userName = RequestInfoUtils.getUserName();
        alarmOrderRule.setUpdateUser(userName);
        long date = System.currentTimeMillis();
        alarmOrderRule.setUpdateTime(date);
        Integer integer = alarmOrderRuleDao.updateById(alarmOrderRule);
        if (integer != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_ORDER_UPDATE_FAILED));
        }
        // 修改区域
        Set<String> alarmOrderRuleArea = alarmOrderRule.getAlarmOrderRuleArea();
        alarmOrderRuleAreaDao.deleteAlarmOrderRuleArea(alarmOrderRule.getId());
        Integer area = addArea(alarmOrderRuleArea, alarmOrderRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_AREA_FAILED));
        }
        // 修改设施类型
        alarmOrderRuleDeviceTypeDao.deleteAlarmOrderDeviceType(alarmOrderRule.getId());
        Integer addDeviceType = addDeviceType(alarmOrderRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_DEVICE_FAILED));
        }
        // 修改告警名称
        Set<String> alarmOrderRuleNameList = alarmOrderRule.getAlarmOrderRuleNameList();
        alarmOrderRuleNameDao.deleteAlarmOrderRuleName(alarmOrderRule.getId());
        Integer addAlarmName = addAlarmName(alarmOrderRuleNameList, alarmOrderRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.UPDATE_ALARM_NAME_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_ORDER);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_ORDER_UPDATE_SUCCESS));
    }

    /**
     * 修改告警转工单状态信息
     *
     * @param status  告警转工单状态信息
     * @param idArray 用户id
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateAlarmOrderRuleStatus(Integer status, String[] idArray) {
        Integer integer = alarmOrderRuleDao.updateAlarmOrderRuleStatus(status, idArray);
        if (!integer.equals(idArray.length)) {
            throw new FilinkAlarmDelayException(I18nUtils.getString(AppConstant.ALARM_ORDER_STUTES_FAILED));
        }
        RedisUtils.remove(AppConstant.REDIS_ORDER);
        // 记录日志
        updateLog(idArray, AppConstant.ALARM_LOG_EIGHT, AppConstant.ALARM_ORDER);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AppConstant.ALARM_ORDER_STUTES_SUCCESS));
    }

    /**
     * 查询当前告警转工单规则
     *
     * @param alarmOrderCondition 告警转工单条件
     * @return 告警转工单规则信息
     */
    @Override
    public AlarmOrderRule queryAlarmOrderRuleFeign(List<AlarmOrderCondition> alarmOrderCondition) {
        AlarmOrderRule alarmOrderRule = null;
        // 查询开启的告警转工单信息
        List<AlarmOrderRule> list = alarmOrderRuleDao.queryAlarmOrderRuleLists();
        for (int i = 0; i < list.size(); i++) {
            boolean flag = true;
            String id = list.get(i).getId();
            for (AlarmOrderCondition alarmOrderConditionOne : alarmOrderCondition) {
                alarmOrderConditionOne.setId(id);
                List<AlarmOrderRule> alarmOrderRules =
                        alarmOrderRuleDao.queryAlarmOrderRuleFeign(alarmOrderConditionOne);
                if (ListUtil.isEmpty(alarmOrderRules)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                alarmOrderRule = list.get(i);
                break;
            }
        }
        return alarmOrderRule;
    }

    /**
     * 查询转工单的区域名称信息
     *
     * @param alarmOrderRules 告警转工单列表信息
     */
    private void selectAlarmAreaName(List<AlarmOrderRule> alarmOrderRules) {
        alarmOrderRules.forEach((AlarmOrderRule alarmOrderRule) -> {
            Set<String> alarmOrderRuleArea = alarmOrderRule.getAlarmOrderRuleArea();
            if (ListUtil.isSetEmpty(alarmOrderRuleArea)) {
                return;
            }
            List<String> arrayList = new ArrayList<>(alarmOrderRuleArea);
            List<String> list = new ArrayList<>();
            List<AreaInfoForeignDto> areaInfos = areaFeign.selectAreaInfoByIds(arrayList);
            if (ListUtil.isEmpty(areaInfos)) {
                return;
            }
            areaInfos.forEach(areaInfo -> {
                list.add(areaInfo.getAreaName());
            });
            alarmOrderRule.setAlarmOrderRuleAreaName(list);
        });
    }

    /**
     * 新增区域信息
     *
     * @param alarmOrderRuleArea 区域信息
     * @param alarmOrderRule     告警远程通知信息
     */
    private Integer addArea(Set<String> alarmOrderRuleArea, AlarmOrderRule alarmOrderRule) {
        List<AlarmOrderRuleArea> list = new ArrayList<>();
        alarmOrderRuleArea.forEach(areaId -> {
            AlarmOrderRuleArea alarmOrderRuleAreaOne = new AlarmOrderRuleArea();
            alarmOrderRuleAreaOne.setRuleId(alarmOrderRule.getId());
            alarmOrderRuleAreaOne.setAreaId(areaId);
            list.add(alarmOrderRuleAreaOne);
        });
        return alarmOrderRuleAreaDao.addAlarmOrderRuleArea(list);

    }

    /**
     * 新增设施类型信息
     *
     * @param alarmOrderRule 告警远程通知信息
     */
    private Integer addDeviceType(AlarmOrderRule alarmOrderRule) {
        List<AlarmOrderRuleDeviceType> alarmOrderRuleDeviceTypeList = alarmOrderRule.getAlarmOrderRuleDeviceTypeList();
        alarmOrderRuleDeviceTypeList.forEach((AlarmOrderRuleDeviceType alarmOrderRuleDeviceType) -> {
            alarmOrderRuleDeviceType.setRuleId(alarmOrderRule.getId());
        });
        return alarmOrderRuleDeviceTypeDao.addAlarmOrderDeviceType(alarmOrderRuleDeviceTypeList);
    }

    /**
     * 新增告警名称
     *
     * @param alarmOrderRule 告警远程通知信息
     */
    private Integer addAlarmName(Set<String> alarmOrderRuleNameList, AlarmOrderRule alarmOrderRule) {
        List<AlarmOrderRuleName> list = new ArrayList<>();
        alarmOrderRuleNameList.forEach(alarmOrderRuleName -> {
            AlarmOrderRuleName alarmOrderRuleNameOne = new AlarmOrderRuleName();
            alarmOrderRuleNameOne.setRuleId(alarmOrderRule.getId());
            alarmOrderRuleNameOne.setAlarmNameId(alarmOrderRuleName);
            list.add(alarmOrderRuleNameOne);
        });
        return alarmOrderRuleNameDao.addAlarmOrderRuleName(list);
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
            AlarmOrderRule alarmOrderRule = alarmOrderRuleDao.selectById(id);
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmOrderRule.getOrderName());
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
            AlarmOrderRule alarmOrderRule = alarmOrderRuleDao.selectById(id);
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmOrderRule.getOrderName());
            // 操作为删除
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

}
