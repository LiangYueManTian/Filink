package com.fiberhome.filink.alarmcurrentserver.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.esotericsoftware.minlog.Log;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmContinous;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmCurrent;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplate;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmQueryTemplateDto;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateArea;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateDepartment;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateDevice;
import com.fiberhome.filink.alarmcurrentserver.bean.AlarmTemplateName;
import com.fiberhome.filink.alarmcurrentserver.constant.AlarmCurrent18n;
import com.fiberhome.filink.alarmcurrentserver.constant.AppConstant;
import com.fiberhome.filink.alarmcurrentserver.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmContinousDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmQueryTemplateDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateAreaDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateDepartmentDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateDeviceDao;
import com.fiberhome.filink.alarmcurrentserver.dao.AlarmTemplateNameDao;
import com.fiberhome.filink.alarmcurrentserver.exception.FilinkAlarmCurrentException;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmCurrentService;
import com.fiberhome.filink.alarmcurrentserver.service.AlarmQueryTemplateService;
import com.fiberhome.filink.alarmcurrentserver.utils.ListUtil;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userapi.bean.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * ???????????????????????????
 * </p>
 *
 * @author wtao103@fiberhome.com
 * @since 2019-04-08
 */
@Service
public class AlarmQueryTemplateServiceImpl extends ServiceImpl
        <AlarmQueryTemplateDao, AlarmQueryTemplate> implements AlarmQueryTemplateService {

    /**
     * ????????????dao
     */
    @Autowired
    private AlarmQueryTemplateDao alarmQueryTemplateDao;

    /**
     * ????????????service
     */
    @Autowired
    private AlarmCurrentService alarmCurrentService;

    /**
     * ??????????????????dao
     */
    @Autowired
    private AlarmTemplateNameDao alarmTemplateNameDao;

    /**
     * ??????????????????dao
     */
    @Autowired
    private AlarmTemplateDeviceDao alarmTemplateDeviceDao;

    /**
     * ??????????????????dao
     */
    @Autowired
    private AlarmTemplateDepartmentDao alarmTemplateDepartmentDao;

    /**
     * ??????????????????dao
     */
    @Autowired
    private AlarmTemplateAreaDao alarmTemplateAreaDao;

    /**
     * ????????????????????????dao
     */
    @Autowired
    private AlarmContinousDao alarmContinousDao;

    /**
     * ??????api
     */
    @Autowired
    private LogProcess logProcess;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ????????????????????????
     */
    @Override
    public Result queryAlarmTemplateList(QueryCondition<AlarmQueryTemplateDto> queryCondition) {

        AlarmQueryTemplateDto alarmQueryTemplateDto = queryCondition.getBizCondition();
        // ????????????
        String userName = RequestInfoUtils.getUserName();
        alarmQueryTemplateDto.setCreateUser(userName);
        List<AlarmQueryTemplate> alarmQueryTemplates = alarmQueryTemplateDao.queryAlarmTemplateList(alarmQueryTemplateDto);
        return ResultUtils.success(alarmQueryTemplates);
    }

    /**
     * ??????????????????????????????
     *
     * @param array ??????id
     * @return ????????????
     */
    @Override
    public Result batchDeleteAlarmTemplate(String[] array) {
        Integer integer = alarmQueryTemplateDao.batchDeleteAlarmTemplate(array);
        Log.debug("id???" + array);
        if (!integer.equals(array.length)) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer alarmTemplateName = alarmTemplateNameDao.batchDeleteAlarmTemplateName(array);
        if (alarmTemplateName == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer alarmTemplateDevice = alarmTemplateDeviceDao.batchDeleteAlarmTemplateDevice(array);
        if (alarmTemplateDevice == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer alarmTemplateDepartment = alarmTemplateDepartmentDao.batchDeleteAlarmTemplateDepartment(array);
        if (alarmTemplateDepartment == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer alarmTemplateArea = alarmTemplateAreaDao.batchDeleteAlarmTemplateArea(array);
        if (alarmTemplateArea == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }

        //??????????????????????????????
        Integer alarmContinous = alarmContinousDao.batchDeleteAlarmContinous(array);
        if (alarmContinous == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_FAILED));
        }


        deleteLog(array, AppConstant.ALARM_LOG_TWELVE, "templateName");
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.DELETE_TEMPLATE_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param id ??????id
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmTemplateById(String id) {
        AlarmQueryTemplate alarmQueryTemplate = alarmQueryTemplateDao.queryAlarmTemplateById(id);


        if (alarmQueryTemplate == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_TEMPLATE_FAILED));
        }

        //????????????
        AlarmContinous alarmContinous = alarmContinousDao.queryAlarmContinousById(id);
        if (alarmContinous != null) {
            alarmQueryTemplate.setAlarmContinous(alarmContinous);
        }

        //????????????
        List<AlarmTemplateName> alarmNameList = alarmTemplateNameDao.queryAlarmNameById(id);
        if (alarmNameList != null) {
            alarmQueryTemplate.setAlarmNameList(alarmNameList);
        }


        //????????????
        List<AlarmTemplateDevice> alarmObjectList = alarmTemplateDeviceDao.queryAlarmDeviceById(id);
        if (alarmObjectList != null) {
            alarmQueryTemplate.setAlarmObjectList(alarmObjectList);
        }

        //????????????
        List<AlarmTemplateDepartment> departmentList = alarmTemplateDepartmentDao.queryAlarmDepartmentById(id);
        if (departmentList != null) {
            alarmQueryTemplate.setDepartmentList(departmentList);
        }

        //????????????

        List<AlarmTemplateArea> areaNameList = alarmTemplateAreaDao.queryAlarmAreaById(id);
        if (areaNameList != null) {
            alarmQueryTemplate.setAreaNameList(areaNameList);
        }

        return ResultUtils.success(alarmQueryTemplate);
    }

    /**
     * ????????????????????????
     *
     * @param id             ??????id
     * @param queryCondition ??????
     * @return ??????????????????
     */
    @Override
    public Result queryAlarmQueryTemplateById(String id, QueryCondition queryCondition) {
        AlarmQueryTemplate alarmQueryTemplate = (AlarmQueryTemplate) queryAlarmTemplateById(id).getData();
        if (alarmQueryTemplate == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(AppConstant.ALARM_TEMPLATE_FAILED));
        }
        List<FilterCondition> filterConditions = queryCondition.getFilterConditions();
        // ??????
        filterConditions = new ArrayList<>();
        //????????????????????????
        filterConditions = copy1QueryTemplateById(filterConditions, alarmQueryTemplate);
        filterConditions = copy2QueryTemplateById(filterConditions, alarmQueryTemplate);


        //?????????????????????????????????
        setAlarmConditionTime(queryCondition, alarmQueryTemplate, filterConditions);
        User user = alarmCurrentService.getUser();
        PageBean result = alarmCurrentService.queryAlarmCurrentList(queryCondition, user, true);
        List<AlarmCurrent> data = (List<AlarmCurrent>) result.getData();
        if (ListUtil.isEmpty(data)) {
            return ResultUtils.warn(LogFunctionCodeConstant.NO_DATA, I18nUtils.getSystemString(AppConstant.NO_DATA));
        }
        return ResultUtils.pageSuccess(result);
    }

    public void setAlarmConditionTime(QueryCondition queryCondition, AlarmQueryTemplate alarmQueryTemplate,
                                      List<FilterCondition> filterConditions) {
        // ??????????????????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmBeginFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmBeginQueenTime())) {
            this.selectConditionTime("alarmBeginTime", "lte", alarmQueryTemplate.getAlarmBeginQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmBeginFrontTime(), filterConditions);
        }

        // ????????????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmQueenTime())) {
            this.selectConditionTime("alarmConfirmTime", "lte", alarmQueryTemplate.getAlarmConfirmQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmConfirmFrontTime(), filterConditions);
        }

        //????????????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmCleanFrontTime())
                || !ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmCleanQueenTime())) {
            this.selectConditionTime("alarmCleanTime", "lte", alarmQueryTemplate.getAlarmCleanQueenTime(),
                    "gte", alarmQueryTemplate.getAlarmCleanFrontTime(), filterConditions);
        }

        // ??????????????????
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
        // ????????????list
        if (!ListUtil.isEmpty(alarmQueryTemplate.getAlarmNameList())) {
            FilterCondition filterCondition =
                    selectCondition("alarmName", "in", alarmQueryTemplate.getAlarmNameList().stream().map(u ->
                            u.getAlarmName()).collect(Collectors.toList()));
            filterConditions.add(filterCondition);
        }
        // ??????
        if (!StringUtils.isEmpty(alarmQueryTemplate.getRemark())) {
            FilterCondition filterCondition =
                    selectCondition("remark", "like", alarmQueryTemplate.getRemark());
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmFixedLevel())) {
            FilterCondition filterCondition =
                    selectCondition("alarmFixedLevel", "like",
                            alarmQueryTemplate.getAlarmFixedLevel());
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmCleanStatus())) {
            FilterCondition filterCondition = selectCondition("alarmCleanStatus", "eq",
                    alarmQueryTemplate.getAlarmCleanStatus());
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmStatus())) {
            FilterCondition filterCondition = selectCondition("alarmConfirmStatus", "eq",
                    alarmQueryTemplate.getAlarmConfirmStatus());
            filterConditions.add(filterCondition);
        }
        // ????????????list
        if (!ListUtil.isEmpty(alarmQueryTemplate.getAlarmObjectList())) {
            FilterCondition filterCondition = selectCondition("alarmObject", "in",
                    alarmQueryTemplate.getAlarmObjectList().stream().map(u -> u.getDeviceName()).collect(Collectors.toList()));
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!ListUtil.isEmpty(alarmQueryTemplate.getAreaNameList())) {
            FilterCondition filterCondition = selectCondition("areaName", "in",
                    alarmQueryTemplate.getAreaNameList().stream().map(u -> u.getAreaName()).collect(Collectors.toList()));
            filterConditions.add(filterCondition);
        }
        // ????????????
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
        // ????????????id
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmSourceTypeId())) {
            FilterCondition filterCondition = selectCondition("alarmSourceTypeId", "eq",
                    alarmQueryTemplate.getAlarmSourceTypeId());
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmContinous().getAlarmContinousTime())) {
            FilterCondition filterCondition = selectCondition("alarmContinousTime", alarmQueryTemplate.getAlarmContinous().getAlarmCmpare(),
                    alarmQueryTemplate.getAlarmContinous().getAlarmContinousTime());
            filterConditions.add(filterCondition);

        }
        // ????????????
        if (!StringUtils.isEmpty(alarmQueryTemplate.getExtraMsg())) {
            FilterCondition filterCondition = selectCondition("extraMsg", "like",
                    alarmQueryTemplate.getExtraMsg());
            filterConditions.add(filterCondition);
        }
        // ??????????????????
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmProcessing())) {
            FilterCondition filterCondition = selectCondition("alarmProcessing", "like",
                    alarmQueryTemplate.getAlarmProcessing());
            filterConditions.add(filterCondition);
        }
        // ??????
        if (!ObjectUtils.isEmpty(alarmQueryTemplate.getAlarmHappenCount())) {
            FilterCondition filterCondition = selectCondition("alarmHappenCount", "eq",
                    alarmQueryTemplate.getAlarmHappenCount());
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmConfirmPeopleNickname())) {
            FilterCondition filterCondition = selectCondition("alarmConfirmPeopleNickname", "like",
                    alarmQueryTemplate.getAlarmConfirmPeopleNickname());
            filterConditions.add(filterCondition);
        }
        // ????????????
        if (!StringUtils.isEmpty(alarmQueryTemplate.getAlarmCleanPeopleNickname())) {
            FilterCondition filterCondition = selectCondition("alarmCleanPeopleNickname", "like",
                    alarmQueryTemplate.getAlarmCleanPeopleNickname());
            filterConditions.add(filterCondition);
        }
        return filterConditions;
    }


    /**
     * ??????????????????
     *
     * @param name      ????????????
     * @param condition ?????????
     * @param string    ?????????
     * @return ????????????
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
     * ??????????????????
     *
     * @param name       ????????????
     * @param condition  ?????????
     * @param lt         ???????????????
     * @param conditions ?????????
     * @param gt         ???????????????
     * @return ????????????
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
     * ????????????????????????
     *
     * @param alarmQueryTemplate ??????????????????
     * @return ????????????
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

        //??????????????????
        Integer updateTemplateName = addTemplateName(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateName == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //??????????????????
        Integer updateTemplateDevice = addTemplateDevice(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateDevice == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //??????????????????
        Integer updateTemplateDepartment = addTemplateDepartment(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateDepartment == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //??????????????????
        Integer updateTemplateArea = addTemplateArea(alarmQueryTemplate, AppConstant.OPT_TYPE);
        if (updateTemplateArea == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer batchUpdateAlarmContinous = batchUpdateAlarmContinous(alarmQueryTemplate);
        if (batchUpdateAlarmContinous == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.UPDATE_TEMPLATE_SUCCESS));
    }

    /**
     * ??????????????????
     *
     * @param alarmQueryTemplate ??????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = AlarmCurrent18n.ADD, logType = "1", functionCode = AppConstant.ALARM_LOG_ELEVEN,
            dataGetColumnName = "templateName", dataGetColumnId = "id")
    @Override
    public Result addAlarmTemplate(AlarmQueryTemplate alarmQueryTemplate) {
        //??????????????????Id
        alarmQueryTemplate.setId(NineteenUUIDUtils.uuid());
        // ????????????
        long time = System.currentTimeMillis();
        alarmQueryTemplate.setCreateTime(time);
        // ????????????
        String userName = RequestInfoUtils.getUserName();
        alarmQueryTemplate.setCreateUser(userName);
        Integer insert = alarmQueryTemplateDao.insert(alarmQueryTemplate);

        if (insert != 1) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer addTemplateName = addTemplateName(alarmQueryTemplate, null);
        if (addTemplateName == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer addTemplateDevice = addTemplateDevice(alarmQueryTemplate, null);
        if (addTemplateDevice == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer addTemplateDepartment = addTemplateDepartment(alarmQueryTemplate, null);
        if (addTemplateDepartment == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //????????????????????????
        Integer addTemplateArea = addTemplateArea(alarmQueryTemplate, null);
        if (addTemplateArea == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }

        //??????????????????????????????
        Integer addAlarmContinous = addAlarmContinous(alarmQueryTemplate);
        if (addAlarmContinous == null) {
            throw new FilinkAlarmCurrentException(I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_FAILED));
        }
        systemLanguageUtil.querySystemLanguage();
        return ResultUtils.warn(ResultCode.SUCCESS, I18nUtils.getSystemString(AppConstant.ADD_TEMPLATE_SUCCESS));
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
            AlarmQueryTemplate alarmQueryTemplate = alarmQueryTemplateDao.selectById(id);
            systemLanguageUtil.querySystemLanguage();
            // ??????????????????
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId(AppConstant.ALARM_ID);
            addLogBean.setDataName(name);
            addLogBean.setFunctionCode(model);
            // ??????????????????
            addLogBean.setOptObjId(id);
            addLogBean.setOptObj(alarmQueryTemplate.getTemplateName());
            // ???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            // ??????????????????
            logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * ??????????????????
     *
     * @param alarmQueryTemplate ??????????????????
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
     * ??????????????????
     *
     * @param alarmQueryTemplate ??????????????????
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
     * ??????????????????
     *
     * @param alarmQueryTemplate ??????????????????
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
     * ??????????????????
     *
     * @param alarmQueryTemplate ??????????????????
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
     * ????????????????????????
     *
     * @param alarmQueryTemplate ??????????????????
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
     * ????????????????????????
     *
     * @param alarmQueryTemplate ??????????????????
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
