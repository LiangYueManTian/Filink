package com.fiberhome.filink.alarmsetserver.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmsetserver.bean.*;
import com.fiberhome.filink.alarmsetserver.constant.AppConstant;
import com.fiberhome.filink.alarmsetserver.dao.*;
import com.fiberhome.filink.alarmsetserver.exception.FilinkAlarmDelayException;
import com.fiberhome.filink.alarmsetserver.service.AlarmOrderRuleService;
import com.fiberhome.filink.alarmsetserver.utils.ListUtil;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
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
     * 告警feign
     */
    @Autowired
    private DepartmentFeign departmentFeign;

    /**
     * 告警名称
     */
    @Autowired
    private AlarmNameDao alarmNameDao;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警转工单列表信息
     *
     * @param queryCondition 条件
     * @return 告警转工单列表信息
     */
    @Override
    public Result queryAlarmOrderRuleList(QueryCondition<AlarmOrderRuleDto> queryCondition) {
        //查询条件
        AlarmOrderRuleDto alarmOrderRuleDto = queryCondition.getBizCondition();
        //权限过滤
        boolean auth = addAuth(alarmOrderRuleDto);
        List<AlarmOrderRule> alarmOrderRules = new ArrayList<>();
        if (auth) {
            //查询数据
            alarmOrderRules = alarmOrderRuleDao.queryAlarmOrderRuleList(alarmOrderRuleDto);
        }
        //有数据查询拼接列表信息
        if (!CollectionUtils.isEmpty(alarmOrderRules)) {
            alarmOrderRules = getAlarmOrderRules(alarmOrderRules);
        } else {
            alarmOrderRules = new ArrayList<>();
        }
        return ResultUtils.success(alarmOrderRules);
    }

    /**
     * 查询拼接列表信息
     * @param alarmOrderRules 告警转工单列表信息
     * @return 告警转工单列表信息
     */
    private List<AlarmOrderRule> getAlarmOrderRules(List<AlarmOrderRule> alarmOrderRules) {
        Set<String> areaIds = new HashSet<>();
        Set<String> departmentIds = new HashSet<>();
        //获取相关联的区域、部门ID
        for (AlarmOrderRule alarmOrderRule : alarmOrderRules) {
            areaIds.addAll(alarmOrderRule.getAlarmOrderRuleArea());
            departmentIds.add(alarmOrderRule.getDepartmentId());
        }
        //查询区域信息
        List<AreaInfoForeignDto> areaInfoList =
                areaFeign.selectAreaInfoByIds(new ArrayList<>(areaIds));
        //查询所有告警名称信息
        List<AlarmName> alarmNameList = alarmNameDao.selectAlarmNameAll();
        //查询部门信息
        List<Department> departments = departmentFeign.queryDepartmentFeignById(new ArrayList<>(departmentIds));
        if (areaInfoList == null || departments == null
                || CollectionUtils.isEmpty(alarmNameList)) {
            alarmOrderRules = new ArrayList<>();
        } else {
            setAlarmOrderRuleListName(alarmOrderRules, departments, areaInfoList, alarmNameList);
        }
        return alarmOrderRules;
    }

    /**
     * 设置名称
     * @param alarmOrderRules 告警转工单规则列表
     * @param departments 部门信息
     * @param areaInfoList 区域信息
     * @param alarmNameList 告警名称信息
     */
    private void setAlarmOrderRuleListName(List<AlarmOrderRule> alarmOrderRules, List<Department> departments,
                                           List<AreaInfoForeignDto> areaInfoList, List<AlarmName> alarmNameList) {
        Map<String, String> areaNameMap = new HashMap<>(64);
        Map<String, String> departmentMap = new HashMap<>(64);
        Map<String, String> alarmNameMap = new HashMap<>(32);
        Map<String, AlarmOrderRule> alarmOrderRuleMap = new HashMap<>(64);
        //将区域名称、告警名称、单位名称取出
        getNameMap(departments, areaInfoList, alarmNameList,
                areaNameMap, departmentMap, alarmNameMap);
        //拼接信息
        for (AlarmOrderRule alarmOrderRule : alarmOrderRules) {
            setAlarmOrderRuleName(areaNameMap, departmentMap, alarmNameMap, alarmOrderRule);
            alarmOrderRuleMap.put(alarmOrderRule.getId(), alarmOrderRule);
        }
        RedisUtils.hSetMap(AppConstant.ALARM_REDIS_ORDER, (Map) alarmOrderRuleMap);
    }

    /**
     * 拼接信息
     * @param areaNameMap 区域名称
     * @param departmentMap 单位名称
     * @param alarmNameMap 告警名称
     * @param alarmOrderRule 告警转工单规则
     */
    private void setAlarmOrderRuleName(Map<String, String> areaNameMap, Map<String, String> departmentMap,
                                       Map<String, String> alarmNameMap, AlarmOrderRule alarmOrderRule) {
        List<String> areaNameList = new ArrayList<>();
        List<String> alarmList = new ArrayList<>();
        //区域名称
        for (String areaId : alarmOrderRule.getAlarmOrderRuleArea()) {
            String areaName = areaNameMap.get(areaId);
            if (!StringUtils.isEmpty(areaName)) {
                areaNameList.add(areaName);
            }
        }
        //告警名称
        for (String alarmId : alarmOrderRule.getAlarmOrderRuleNameList()) {
            String alarm = alarmNameMap.get(alarmId);
            if (!StringUtils.isEmpty(alarm)) {
                alarmList.add(alarm);
            }
        }
        String departmentName = departmentMap.get(alarmOrderRule.getDepartmentId());
        if (StringUtils.isEmpty(departmentName)) {
            departmentName = "";
        }
        alarmOrderRule.setAlarmOrderRuleAreaName(areaNameList);
        alarmOrderRule.setAlarmOrderRuleNames(alarmList);
        alarmOrderRule.setDepartmentName(departmentName);
    }

    /**
     * 将区域名称、告警名称、单位名称取出
     * @param departments 部门信息
     * @param areaInfoList 区域信息
     * @param alarmNameList 告警名称信息
     * @param areaNameMap 区域名称
     * @param departmentMap 单位名称
     * @param alarmNameMap 告警名称
     */
    private void getNameMap(List<Department> departments, List<AreaInfoForeignDto> areaInfoList, List<AlarmName> alarmNameList, Map<String, String> areaNameMap, Map<String, String> departmentMap, Map<String, String> alarmNameMap) {
        //取出区域名称
        for (AreaInfoForeignDto areaInfoForeignDto : areaInfoList) {
            areaNameMap.put(areaInfoForeignDto.getAreaId(), areaInfoForeignDto.getAreaName());
        }
        //取出告警名称
        for (AlarmName alarmName : alarmNameList) {
            alarmNameMap.put(alarmName.getId(), alarmName.getAlarmCode());
        }
        //取出单位名称
        for (Department department : departments) {
            departmentMap.put(department.getId(), department.getDeptName());
        }
    }

    /**
     * 查询当前用户数据权限信息
     *
     * @param alarmOrderRuleDto 条件信息
     */
    private boolean addAuth(AlarmOrderRuleDto alarmOrderRuleDto) {
        if (AppConstant.ALARM_YI.equals(RequestInfoUtils.getUserId())) {
            return true;
        }
        User user = getUser();
        if (user == null) {
            return false;
        }
        // 获取用户设施类型
        List<String> deviceTypeIds = getDeviceTypes(user);
        // 获取用户区域
        List<String> list = getUserAreaIds(user);
        //判断权限
        if (ListUtil.isEmpty(deviceTypeIds) || ListUtil.isEmpty(list)) {
            return false;
        }
        checkUserPermission(alarmOrderRuleDto, deviceTypeIds, list);
        //查询条件包含数据权限，校验是否有权限
        return !alarmOrderRuleDto.checkPermission();
    }

    /**
     * 查询校验权限
     * @param alarmOrderRuleDto 查询条件
     * @param deviceTypeIds 设施类型权限
     * @param list 区域权限
     */
    private void checkUserPermission(AlarmOrderRuleDto alarmOrderRuleDto, List<String> deviceTypeIds, List<String> list) {
        //设施类型查询条件
        List<String> deviceTypeId = alarmOrderRuleDto.getDeviceTypeId();
        //区域查询条件
        Set<String> alarmOrderRuleArea = alarmOrderRuleDto.getAlarmOrderRuleArea();
        Set<String> areaIds = new HashSet<>(list);
        if (!ListUtil.isEmpty(deviceTypeId)) {
            List<String> deviceTypeIdList = deviceTypeId.stream().filter(
                    deviceTypeIds::contains).collect(toList());
            alarmOrderRuleDto.setDeviceTypeId(deviceTypeIdList);
        } else {
            alarmOrderRuleDto.setDeviceTypeId(deviceTypeIds);
        }
        if (!CollectionUtils.isEmpty(alarmOrderRuleArea)) {
            Set<String> areaIdSet =  alarmOrderRuleArea.stream().filter(
                    areaIds::contains).collect(toSet());
            alarmOrderRuleDto.setAlarmOrderRuleArea(areaIdSet);
        } else {
            alarmOrderRuleDto.setAlarmOrderRuleArea(areaIds);
        }
    }

    /**
     * 根据部门id集合查询是否存在当前告警转工单规则
     *
     * @param departmentIds 部门id集合
     * @return 是否存在
     */
    @Override
    public Result queryAlarmOrderRuleByDeptIds(List<String> departmentIds) {
        List<String> ids = alarmOrderRuleDao.queryAlarmOrderRuleByDeptIds(departmentIds);
        boolean isExist = !CollectionUtils.isEmpty(ids);
        return ResultUtils.success(isExist);
    }

    /**
     * 根据区域ID，部门id集合查询是否存在当前告警转工单规则
     *
     * @param alarmOrderRuleForArea 区域ID，部门id集合
     * @return 是否存在
     */
    @Override
    public Result queryAlarmOrderRuleByAreaDeptIds(AlarmOrderRuleForArea alarmOrderRuleForArea) {
        List<String> ids = alarmOrderRuleDao.queryAlarmOrderRuleByAreaDeptIds(alarmOrderRuleForArea.getAreaId(),
                alarmOrderRuleForArea.getDeptIdList());
        boolean isExist = !CollectionUtils.isEmpty(ids);
        return ResultUtils.success(isExist);
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
        roleDeviceTypes.forEach(roleDeviceType -> deviceTypes.add(roleDeviceType.getDeviceTypeId()));
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
        //查询缓存
        AlarmOrderRule alarmOrderRule = (AlarmOrderRule) RedisUtils.hGet(AppConstant.ALARM_REDIS_ORDER, id);
        if (alarmOrderRule == null || StringUtils.isEmpty(alarmOrderRule.getId())) {
            //缓存丢失查询数据库
            alarmOrderRules = queryAlarmInfoById(id);
        } else {
            alarmOrderRules.add(alarmOrderRule);
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
        //拼接信息
        alarmOrderRules = getAlarmOrderRules(alarmOrderRules);
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
        String userId = RequestInfoUtils.getUserId();
        alarmOrderRule.setCreateUser(userId);
        long date = System.currentTimeMillis();
        alarmOrderRule.setCreateTime(date);
        Integer insert = alarmOrderRuleDao.insert(alarmOrderRule);
        if (insert != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_ORDER_ADD_FAILED));
        }
        // 新增区域
        Set<String> alarmOrderRuleArea = alarmOrderRule.getAlarmOrderRuleArea();
        Integer integer = addArea(alarmOrderRuleArea, alarmOrderRule);
        if (integer == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_AREA_FAILED));
        }
        // 新增设施类型
        Integer addDeviceType = addDeviceType(alarmOrderRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_DEVICE_FAILED));
        }
        // 新增告警名称
        Set<String> alarmOrderRuleNameList = alarmOrderRule.getAlarmOrderRuleNameList();
        Integer addAlarmName = addAlarmName(alarmOrderRuleNameList, alarmOrderRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ADD_ALARM_NAME_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_ORDER);
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_ORDER_ADD_SUCCESS));
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
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_ORDER_DELETE_FAILED));
        }
        // 删除关联表
        Integer area = alarmOrderRuleAreaDao.batchDeleteAlarmOrderRuleArea(array);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_AREA_FAILED));
        }
        Integer deviceType = alarmOrderRuleDeviceTypeDao.batchDeleteAlarmOrderDeviceType(array);
        if (deviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_DEVICE_FAILED));
        }
        Integer ruleName = alarmOrderRuleNameDao.batchDeleteAlarmOrderRuleName(array);
        if (ruleName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.DELETE_ALARM_NAME_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_ORDER);
        // 记录日志
        deleteLog(array);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_ORDER_DELETE_SUCCESS));
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
        String userId = RequestInfoUtils.getUserId();
        alarmOrderRule.setUpdateUser(userId);
        long date = System.currentTimeMillis();
        alarmOrderRule.setUpdateTime(date);
        Integer integer = alarmOrderRuleDao.updateById(alarmOrderRule);
        if (integer != 1) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_ORDER_UPDATE_FAILED));
        }
        // 修改区域
        Set<String> alarmOrderRuleArea = alarmOrderRule.getAlarmOrderRuleArea();
        alarmOrderRuleAreaDao.deleteAlarmOrderRuleArea(alarmOrderRule.getId());
        Integer area = addArea(alarmOrderRuleArea, alarmOrderRule);
        if (area == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_AREA_FAILED));
        }
        // 修改设施类型
        alarmOrderRuleDeviceTypeDao.deleteAlarmOrderDeviceType(alarmOrderRule.getId());
        Integer addDeviceType = addDeviceType(alarmOrderRule);
        if (addDeviceType == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_DEVICE_FAILED));
        }
        // 修改告警名称
        Set<String> alarmOrderRuleNameList = alarmOrderRule.getAlarmOrderRuleNameList();
        alarmOrderRuleNameDao.deleteAlarmOrderRuleName(alarmOrderRule.getId());
        Integer addAlarmName = addAlarmName(alarmOrderRuleNameList, alarmOrderRule);
        if (addAlarmName == null) {
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.UPDATE_ALARM_NAME_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_ORDER);
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_ORDER_UPDATE_SUCCESS));
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
            throw new FilinkAlarmDelayException(I18nUtils.getSystemString(AppConstant.ALARM_ORDER_STUTES_FAILED));
        }
        RedisUtils.remove(AppConstant.ALARM_REDIS_ORDER);
        // 记录日志
        updateLog(idArray);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ALARM_ORDER_STUTES_SUCCESS));
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
        for (AlarmOrderCondition alarmOrderConditionOne : alarmOrderCondition) {
            List<AlarmOrderRule> alarmOrderRules = alarmOrderRuleDao.queryAlarmOrderRuleFeign(alarmOrderConditionOne);
            if (!ListUtil.isEmpty(alarmOrderRules)) {
                alarmOrderRule = alarmOrderRules.get(0);
                break;
            }
        }
        return alarmOrderRule;
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
        alarmOrderRuleDeviceTypeList.forEach((AlarmOrderRuleDeviceType alarmOrderRuleDeviceType) -> alarmOrderRuleDeviceType.setRuleId(alarmOrderRule.getId()));
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
     *  @param ids   用户id
     * */
    private void updateLog(String[] ids) {
        for (String id : ids) {
            AlarmOrderRule alarmOrderRule = alarmOrderRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(AppConstant.ALARM_ORDER);
            addLogBean.setFunctionCode(AppConstant.ALARM_LOG_EIGHT);
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
     *  @param ids   用户id
     * */
    private void deleteLog(String[] ids) {
        for (String id : ids) {
            AlarmOrderRule alarmOrderRule = alarmOrderRuleDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(AppConstant.ALARM_ORDER);
            addLogBean.setFunctionCode(AppConstant.ALARM_LOG_SERVEN);
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
