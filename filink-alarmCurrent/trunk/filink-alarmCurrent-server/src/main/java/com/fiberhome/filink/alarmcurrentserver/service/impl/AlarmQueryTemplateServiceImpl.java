package com.fiberhome.filink.alarmcurrentserver.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentserver.bean.*;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrentResultCode;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.*;
import com.fiberhome.filink.alarmcurrentserver.exception.FilinkAlarmCurrentException;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 告警模板服务实现类
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-08
 */
@Service
public class AlarmQueryTemplateServiceImpl extends ServiceImpl
        <AlarmQueryTemplateDao, AlarmQueryTemplate> implements AlarmQueryTemplateService {

    /**
     * 告警模板dao
     */
    @Autowired
    private AlarmQueryTemplateDao alarmQueryTemplateDao;

    /**
     * 当前告警service
     */
    @Autowired
    private AlarmCurrentService alarmCurrentService;

    /**
     * 告警模板名称dao
     */
    @Autowired
    private AlarmTemplateNameDao alarmTemplateNameDao;

    /**
     * 告警模板对象dao
     */
    @Autowired
    private AlarmTemplateDeviceDao alarmTemplateDeviceDao;

    /**
     * 告警模板单位dao
     */
    @Autowired
    private AlarmTemplateDepartmentDao alarmTemplateDepartmentDao;

    /**
     * 告警模板区域dao
     */
    @Autowired
    private AlarmTemplateAreaDao alarmTemplateAreaDao;

    /**
     * 告警模板持续时间dao
     */
    @Autowired
    private AlarmContinousDao alarmContinousDao;

    /**
     * 日志api
     */
    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * 查询告警模板列表信息
     *
     * @param queryCondition 封装条件
     * @return 告警模板列表信息
     */
    @Override
    public Result queryAlarmTemplateList(QueryCondition<AlarmQueryTemplateDto> queryCondition) {

        AlarmQueryTemplateDto alarmQueryTemplateDto = queryCondition.getBizCondition();
        // 获取用户
        String userId = RequestInfoUtils.getUserId();
        if (!AppConstant.ONE.equals(userId)) {
            alarmQueryTemplateDto.setResponsibleId(userId);
        }
        List<AlarmQueryTemplate> alarmQueryTemplates = alarmQueryTemplateDao.queryAlarmTemplateList(alarmQueryTemplateDto);
        return ResultUtils.success(alarmQueryTemplates);
    }

    /**
     * 批量删除告警模板信息
     *
     * @param array 告警id
     * @return 判断结果
     */
    @Override
    public Result batchDeleteAlarmTemplate(String[] array) {
        Integer integer = alarmQueryTemplateDao.batchDeleteAlarmTemplate(array);
        if (!integer.equals(array.length)) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //删除告警模板名称
        Integer alarmTemplateName = alarmTemplateNameDao.batchDeleteAlarmTemplateName(array);
        if (alarmTemplateName == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //删除告警模板对象
        Integer alarmTemplateDevice = alarmTemplateDeviceDao.batchDeleteAlarmTemplateDevice(array);
        if (alarmTemplateDevice == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //删除告警模板单位
        Integer alarmTemplateDepartment = alarmTemplateDepartmentDao.batchDeleteAlarmTemplateDepartment(array);
        if (alarmTemplateDepartment == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //删除告警模板区域
        Integer alarmTemplateArea = alarmTemplateAreaDao.batchDeleteAlarmTemplateArea(array);
        if (alarmTemplateArea == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //删除告警模板持续时间
        Integer alarmContinous = alarmContinousDao.batchDeleteAlarmContinous(array);
        if (alarmContinous == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }


        deleteLog(array, AppConstant.ALARM_LOG_TWELVE, "templateName");
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_SUCCESS));
    }

    /**
     * 查询告警模板信息
     *
     * @param id 告警id
     * @return 告警模板信息
     */
    @Override
    public Result queryAlarmTemplateById(String id) {
        AlarmQueryTemplate alarmQueryTemplate = alarmQueryTemplateDao.queryAlarmTemplateById(id);


        if (alarmQueryTemplate == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_TEMPLATE_FAILED));
        }

        //持续时间
        AlarmContinous alarmContinous = alarmContinousDao.queryAlarmContinousById(id);
        if (alarmContinous != null) {
            alarmQueryTemplate.setAlarmContinous(alarmContinous);
        }

        //告警名称
        List<AlarmTemplateName> alarmNameList = alarmTemplateNameDao.queryAlarmNameById(id);
        if (alarmNameList != null) {
            alarmQueryTemplate.setAlarmNameList(alarmNameList);
        }


        //告警对象
        List<AlarmTemplateDevice> alarmObjectList = alarmTemplateDeviceDao.queryAlarmDeviceById(id);
        if (alarmObjectList != null) {
            alarmQueryTemplate.setAlarmObjectList(alarmObjectList);
        }

        //告警单位
        List<AlarmTemplateDepartment> departmentList = alarmTemplateDepartmentDao.queryAlarmDepartmentById(id);
        if (departmentList != null) {
            alarmQueryTemplate.setDepartmentList(departmentList);
        }

        //告警区域

        List<AlarmTemplateArea> areaNameList = alarmTemplateAreaDao.queryAlarmAreaById(id);
        if (areaNameList != null) {
            alarmQueryTemplate.setAreaNameList(areaNameList);
        }

        return ResultUtils.success(alarmQueryTemplate);
    }

    /**
     * 查询告警模板信息
     *
     * @param id             告警id
     * @param queryCondition 条件
     * @return 告警模板信息
     */
    @Override
    public Result queryAlarmQueryTemplateById(String id, QueryCondition queryCondition) {
        AlarmQueryTemplate alarmQueryTemplate = (AlarmQueryTemplate) queryAlarmTemplateById(id).getData();
        if (alarmQueryTemplate == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_TEMPLATE_FAILED));
        }

        List<FilterCondition> filterConditions = new ArrayList<>();
        //获取新的模板数据
        filterConditions = copy1QueryTemplateById(filterConditions, alarmQueryTemplate);
        filterConditions = copy2QueryTemplateById(filterConditions, alarmQueryTemplate);

        //将时间条件放入查询条件
        setAlarmConditionTime(queryCondition, alarmQueryTemplate, filterConditions);
        User user = alarmCurrentService.getUser();
        PageBean result = alarmCurrentService.queryAlarmCurrentList(queryCondition, user, true);
        List<AlarmCurrent> data = (List<AlarmCurrent>) result.getData();
        if (ListUtil.isEmpty(data)) {
            return ResultUtils.warn(AlarmCurrentResultCode.NO_DATA, I18nUtils.getSystemString(AppConstant.NO_DATA));
        }
        return ResultUtils.pageSuccess(result);
    }

    public void setAlarmConditionTime(QueryCondition queryCondition, AlarmQueryTemplate alarmQueryTemplate,
                                      List<FilterCondition> filterConditions) {
        // 首次发生时间
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmBeginFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmBeginQueenTime())) {
            this.selectConditionTime("alarmBeginTime", "lte", alarmQueryTemplate.getAlarmBeginQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmBeginFrontTime(), filterConditions);
        }

        // 确认时间
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmQueenTime())) {
            this.selectConditionTime("alarmConfirmTime", "lte", alarmQueryTemplate.getAlarmConfirmQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmConfirmFrontTime(), filterConditions);
        }

        //清除时间
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmCleanFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmCleanQueenTime())) {
            this.selectConditionTime("alarmCleanTime", "lte", alarmQueryTemplate.getAlarmCleanQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmCleanFrontTime(), filterConditions);
        }

        // 最近发生时间
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmNearFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmNearQueenTime())) {
            this.selectConditionTime("alarmNearTime", "lte", alarmQueryTemplate.getAlarmNearQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmNearFrontTime(), filterConditions);
        }
        queryCondition.setFilterConditions(filterConditions);
    }


    /**
     * @param filterConditions
     * @param alarmQueryTemplate
     * @return
     */
    public List<FilterCondition> copy1QueryTemplateById(List<FilterCondition> filterConditions, AlarmQueryTemplate alarmQueryTemplate) {
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAddress())) {
            FilterCondition filterCondition =
                    selectCondition("address", "like", alarmQueryTemplate.getAddress());
            filterConditions.add(filterCondition);
        }
        // 告警名称list
        if (!ListUtil.isEmpty(alarmQueryTemplate.getAlarmNameList())) {
            FilterCondition filterCondition =
                    selectCondition("alarmNameId", "in", alarmQueryTemplate.getAlarmNameList().stream().map(u ->
                            u.getAlarmNameId()).collect(Collectors.toList()));
            filterConditions.add(filterCondition);
        }
        // 备注
        if (!StringUtils.isEmpty(alarmQueryTemplate.getRemark())) {
            FilterCondition filterCondition =
                    selectCondition("remark", "like", alarmQueryTemplate.getRemark());
            filterConditions.add(filterCondition);
        }
        // 告警级别
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmFixedLevel())) {
            String alarmFixedLevel = alarmQueryTemplate.getAlarmFixedLevel();
            String[] alarmFixedLevels = alarmFixedLevel.split(",");
            List<String> alarmFixedLevelList = Arrays.asList(alarmFixedLevels);
            FilterCondition filterCondition =
                    selectCondition("alarmFixedLevel", "in",
                            alarmFixedLevelList);
            filterConditions.add(filterCondition);
        }
        // 清除状态
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmCleanStatus())) {
            FilterCondition filterCondition = selectCondition("alarmCleanStatus", "eq",
                    alarmQueryTemplate.getAlarmCleanStatus());
            filterConditions.add(filterCondition);
        }
        // 确认状态
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmStatus())) {
            FilterCondition filterCondition = selectCondition("alarmConfirmStatus", "eq",
                    alarmQueryTemplate.getAlarmConfirmStatus());
            filterConditions.add(filterCondition);
        }
        // 告警对象list
        if (!ListUtil.isEmpty(alarmQueryTemplate.getAlarmObjectList())) {
            FilterCondition filterCondition = selectCondition("alarmObject", "in",
                    alarmQueryTemplate.getAlarmObjectList().stream().map(u -> u.getDeviceName()).collect(Collectors.toList()));
            filterConditions.add(filterCondition);
        }
        // 区域名称
        if (!ListUtil.isEmpty(alarmQueryTemplate.getAreaNameList())) {
            FilterCondition filterCondition = selectCondition("areaName", "in",
                    alarmQueryTemplate.getAreaNameList().stream().map(u -> u.getAreaName()).collect(Collectors.toList()));
            filterConditions.add(filterCondition);
        }
        // 单位名称
        if (!StringUtils.isEmpty(alarmQueryTemplate.getDepartmentList().get(0).getDepartmentName())) {
            FilterCondition filterCondition = selectCondition("responsibleDepartment", "like",
                    alarmQueryTemplate.getDepartmentList().get(0).getDepartmentName());
            filterConditions.add(filterCondition);
        }
        return filterConditions;
    }

    /**
     * @param filterConditions
     * @param alarmQueryTemplate
     * @return
     */
    public List<FilterCondition> copy2QueryTemplateById(List<FilterCondition> filterConditions, AlarmQueryTemplate alarmQueryTemplate) {
        // 设施类型id
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmSourceTypeId())) {
            String alarmSourceTypeId = alarmQueryTemplate.getAlarmSourceTypeId();
            String[] alarmSourceTypeIds = alarmSourceTypeId.split(",");
            List<String> alarmSourceTypeIdList = Arrays.asList(alarmSourceTypeIds);
            FilterCondition filterCondition = selectCondition("alarmSourceTypeId", "in",
                    alarmSourceTypeIdList);
            filterConditions.add(filterCondition);
        }
        // 持续时间
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmContinous().getAlarmContinousTime())) {
            FilterCondition filterCondition = selectCondition("alarmContinousTime", alarmQueryTemplate.getAlarmContinous().getAlarmCmpare(),
                    alarmQueryTemplate.getAlarmContinous().getAlarmContinousTime());
            filterConditions.add(filterCondition);

        }
        // 附加消息
        if (!StringUtils.isEmpty(alarmQueryTemplate.getExtraMsg())) {
            FilterCondition filterCondition = selectCondition("extraMsg", "like",
                    alarmQueryTemplate.getExtraMsg());
            filterConditions.add(filterCondition);
        }
        // 告警处理建议
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmProcessing())) {
            FilterCondition filterCondition = selectCondition("alarmProcessing", "like",
                    alarmQueryTemplate.getAlarmProcessing());
            filterConditions.add(filterCondition);
        }
        // 频次
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmHappenCount())) {
            FilterCondition filterCondition = selectCondition("alarmHappenCount", "eq",
                    alarmQueryTemplate.getAlarmHappenCount());
            filterConditions.add(filterCondition);
        }
        // 确认用户
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmPeopleNickname())) {
            FilterCondition filterCondition = selectCondition("alarmConfirmPeopleNickname", "like",
                    alarmQueryTemplate.getAlarmConfirmPeopleNickname());
            filterConditions.add(filterCondition);
        }
        // 清除用户
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmCleanPeopleNickname())) {
            FilterCondition filterCondition = selectCondition("alarmCleanPeopleNickname", "like",
                    alarmQueryTemplate.getAlarmCleanPeopleNickname());
            filterConditions.add(filterCondition);
        }
        return filterConditions;
    }


    /**
     * 传递普通参数
     *
     * @param name      过滤字段
     * @param condition 操作符
     * @param string    过滤值
     * @return 参数信息
     */
    @Override
    public FilterCondition selectCondition(String name, String condition, Object string) {
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField(name);
        filterCondition.setOperator(condition);
        filterCondition.setFilterValue(string);
        return filterCondition;
    }

    /**
     * 时间参数信息
     *
     * @param name       过滤字段
     * @param condition  操作符
     * @param lt         小于过滤值
     * @param conditions 操作符
     * @param gt         大于过滤值
     * @return 参数信息
     */
    public void selectConditionTime(String name, String condition, long lt,
                                    String conditions, long gt, List<FilterCondition> filterConditions) {
        FilterCondition filterConditionOne = new FilterCondition();
        filterConditionOne.setFilterField(name);
        filterConditionOne.setOperator(condition);
        filterConditionOne.setFilterValue(lt);
        filterConditionOne.setExtra("LT_AND_GT");
        FilterCondition filterConditionTwo = new FilterCondition();
        filterConditionTwo.setFilterField(name);
        filterConditionTwo.setOperator(conditions);
        filterConditionTwo.setFilterValue(gt);
        filterConditionTwo.setExtra("LT_AND_GT");
        filterConditions.add(filterConditionOne);
        filterConditions.add(filterConditionTwo);
    }

    /**
     * 修改告警模板信息
     *
     * @param alarmQueryTemplate 告警模板信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AlarmCurrent18n.UPDATE, logType = "1", functionCode = AppConstant.ALARM_LOG_TEN,
            dataGetColumnName = "templateName", dataGetColumnId = "id")
    @Override
    public Result updateAlarmTemplate(AlarmQueryTemplate alarmQueryTemplate) {
        alarmQueryTemplate.setIsDeleted("0");
        Integer integer = alarmQueryTemplateDao.updateById(alarmQueryTemplate);

        if (integer != 1) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //修改告警名称
        Integer updateTemplateName = addTemplateName(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateName == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //修改告警对象
        Integer updateTemplateDevice = addTemplateDevice(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateDevice == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //修改告警单位
        Integer updateTemplateDepartment = addTemplateDepartment(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateDepartment == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //修改告警区域
        Integer updateTemplateArea = addTemplateArea(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateArea == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //修改告警持续时间
        Integer batchUpdateAlarmContinous = batchUpdateAlarmContinous(alarmQueryTemplate);
        if (batchUpdateAlarmContinous == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_SUCCESS));
    }

    /**
     * 新增告警模板
     *
     * @param alarmQueryTemplate 告警模板信息
     * @return 判断结果
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AlarmCurrent18n.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_ELEVEN,
            dataGetColumnName = "templateName", dataGetColumnId = "id")
    @Override
    public Result addAlarmTemplate(AlarmQueryTemplate alarmQueryTemplate) {
        //设置告警模板Id
        alarmQueryTemplate.setId(NineteenUUIDUtils.uuid());
        // 获取时间
        long time = System.currentTimeMillis();
        alarmQueryTemplate.setCreateTime(time);
        // 获取用户
        String userName = RequestInfoUtils.getUserName();
        String userId = RequestInfoUtils.getUserId();
        alarmQueryTemplate.setCreateUser(userName);
        alarmQueryTemplate.setResponsibleId(userId);
        Integer insert = alarmQueryTemplateDao.insert(alarmQueryTemplate);

        if (insert != 1) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //新增告警模板名称
        Integer addTemplateName = addTemplateName(alarmQueryTemplate, null);
        if (addTemplateName == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //新增告警模板对象
        Integer addTemplateDevice = addTemplateDevice(alarmQueryTemplate, null);
        if (addTemplateDevice == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //新增告警模板单位
        Integer addTemplateDepartment = addTemplateDepartment(alarmQueryTemplate, null);
        if (addTemplateDepartment == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //新增告警区域
        Integer addTemplateArea = addTemplateArea(alarmQueryTemplate, null);
        if (addTemplateArea == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //新增告警模板持续时间
        Integer addAlarmContinous = addAlarmContinous(alarmQueryTemplate);
        if (addAlarmContinous == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.warn(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_SUCCESS));
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
            AlarmQueryTemplate alarmQueryTemplate = alarmQueryTemplateDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // 获取日志类型
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // 获取操作对象
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmQueryTemplate.getTemplateName());
            // 操作为删除
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // 新增操作日志
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 新增告警名称
     *
     * @param alarmQueryTemplate 告警模板信息
     */
    private Integer addTemplateName(AlarmQueryTemplate alarmQueryTemplate, String optType) {
        int flag = 0;
        List<AlarmTemplateName> list = alarmQueryTemplate.getAlarmNameList();
        if (!ListUtil.isEmpty(list)) {
            if (optType == AppConstant.OPT_TYPE) {
                alarmTemplateNameDao.batchDeleteAlarmTemplateName((new String[]{alarmQueryTemplate.getId()}));
            }
            list.forEach(alarmTemplateName -> alarmTemplateName.setTemplateId(alarmQueryTemplate.getId()));
            flag = alarmTemplateNameDao.addAlarmTemplateName(list);
        } else if (optType == AppConstant.OPT_TYPE) {
            alarmTemplateNameDao.batchDeleteAlarmTemplateName((new String[]{alarmQueryTemplate.getId()}));
        }
        return flag;
    }


    /**
     * 新增告警对象
     *
     * @param alarmQueryTemplate 告警模板信息
     */
    private Integer addTemplateDevice(AlarmQueryTemplate alarmQueryTemplate, String optType) {
        int flag = 0;
        List<AlarmTemplateDevice> list = alarmQueryTemplate.getAlarmObjectList();
        if (!ListUtil.isEmpty(list)) {
            list.forEach(alarmTemplateDevice -> alarmTemplateDevice.setTemplateId(alarmQueryTemplate.getId()));
            if (optType == AppConstant.OPT_TYPE) {
                alarmTemplateDeviceDao.batchDeleteAlarmTemplateDevice((new String[]{alarmQueryTemplate.getId()}));
            }
            flag = alarmTemplateDeviceDao.addAlarmTemplateDevice(list);
        } else if (optType == AppConstant.OPT_TYPE) {
            alarmTemplateDeviceDao.batchDeleteAlarmTemplateDevice((new String[]{alarmQueryTemplate.getId()}));
        }
        return flag;
    }

    /**
     * 新增告警单位
     *
     * @param alarmQueryTemplate 告警模板信息
     */
    private Integer addTemplateDepartment(AlarmQueryTemplate alarmQueryTemplate, String optType) {
        int flag = 0;
        List<AlarmTemplateDepartment> list = alarmQueryTemplate.getDepartmentList();
        if (!ListUtil.isEmpty(list)) {
            list.forEach(alarmTemplateDepartment -> alarmTemplateDepartment.setTemplateId(alarmQueryTemplate.getId()));
            if (optType == AppConstant.OPT_TYPE) {
                alarmTemplateDepartmentDao.batchDeleteAlarmTemplateDepartment((new String[]{alarmQueryTemplate.getId()}));
            }
            flag = alarmTemplateDepartmentDao.addAlarmTemplateDepartment(list);
        }
        return flag;
    }

    /**
     * 新增告警区域
     *
     * @param alarmQueryTemplate 告警模板信息
     */
    private Integer addTemplateArea(AlarmQueryTemplate alarmQueryTemplate, String optType) {
        int flag = 0;
        List<AlarmTemplateArea> list = alarmQueryTemplate.getAreaNameList();
        if (!ListUtil.isEmpty(list)) {
            list.forEach(alarmTemplateArea -> alarmTemplateArea.setTemplateId(alarmQueryTemplate.getId()));
            if (optType == AppConstant.OPT_TYPE) {
                alarmTemplateAreaDao.batchDeleteAlarmTemplateArea((new String[]{alarmQueryTemplate.getId()}));
            }
            flag = alarmTemplateAreaDao.addAlarmTemplateArea(list);
        } else if (optType == AppConstant.OPT_TYPE) {
            alarmTemplateAreaDao.batchDeleteAlarmTemplateArea((new String[]{alarmQueryTemplate.getId()}));
        }
        return flag;
    }


    /**
     * 新增告警持续时间
     *
     * @param alarmQueryTemplate 告警模板信息
     */
    private Integer addAlarmContinous(AlarmQueryTemplate alarmQueryTemplate) {
        int flag = 0;
        if (alarmQueryTemplate.getAlarmContinous() != null) {
            AlarmContinous alarmContinous = alarmQueryTemplate.getAlarmContinous();
            alarmContinous.setId(alarmQueryTemplate.getId());
            flag = alarmContinousDao.addAlarmContinous(alarmContinous);
        }
        return flag;
    }

    /**
     * 修改告警持续时间
     *
     * @param alarmQueryTemplate 告警模板信息
     */
    private Integer batchUpdateAlarmContinous(AlarmQueryTemplate alarmQueryTemplate) {
        int flag = 0;
        if (alarmQueryTemplate.getAlarmContinous() != null) {
            AlarmContinous alarmContinous = alarmQueryTemplate.getAlarmContinous();
            alarmContinous.setId(alarmQueryTemplate.getId());
            flag = alarmContinousDao.batchUpdateAlarmContinous(alarmContinous);
        }
        return flag;
    }

}
