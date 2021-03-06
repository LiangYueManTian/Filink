package com.fiberhome.filink.parts.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.parts.bean.PartInfo;
import com.fiberhome.filink.parts.constant.LogFunctionCodeConstant;
import com.fiberhome.filink.parts.dao.PartInfoDao;
import com.fiberhome.filink.parts.dto.PartInfoDto;
import com.fiberhome.filink.parts.service.PartInfoService;
import com.fiberhome.filink.parts.constant.PartType;
import com.fiberhome.filink.parts.constant.PartsI18n;
import com.fiberhome.filink.parts.constant.PartsResultCode;
import com.fiberhome.filink.parts.utils.RandomUtil;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.api.DepartmentFeign;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.myBatiesBuildPageBean;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author gzp
 * @since 2019-02-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PartInfoServiceImpl extends ServiceImpl<PartInfoDao, PartInfo> implements PartInfoService {

    @Autowired
    private PartInfoDao partInfoDao;

    @Autowired
    private DepartmentFeign departmentFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private LogProcess logProcess;

    @Value("${unitCode}")
    private String unitCode;

    @Value("${partsType}")
    private String partsType;

    /**
     * ????????????
     *
     * @param partInfo ????????????
     * @return Result
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.INSERT_PART_FUNCTION_CODE,
            dataGetColumnName = "partName", dataGetColumnId = "partId")
    @Override
    public Result addPart(PartInfo partInfo) {
        // ????????????????????????
        if (checkPartsParams(partInfo)) {
            return ResultUtils.warn(PartsResultCode.PARTS_PARAM_ERROR, I18nUtils.getString(PartsI18n.PARTS_PARAM_ERROR));
        }
        //???????????????
        partInfo.parameterFormat();
        // ????????????????????????
        if (!partInfo.checkName()) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_ERROR, I18nUtils.getString(PartsI18n.PARTS_NAME_ERROR));
        }
        // ???????????????????????????
        if (checkPartsName(null, partInfo.getPartName())) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_SAME, I18nUtils.getString(PartsI18n.PARTS_NAME_SAME));
        }
        // ????????????
        String partsCode;
        while (!checkPartsCode(partsCode = serialNumber(unitCode, partsType))) {
            log.warn("?????????partsCode--------------" + partsCode);
        }
        log.warn("?????????partsCode-------------" + partsCode);
        partInfo.setPartCode(partsCode);

        //????????????????????????????????????
        String userId = RequestInfoUtils.getUserId();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        partInfo.setCreateUser(userId);
        partInfo.setUpdateUser(userId);
        partInfo.setCreateTime(timestamp);
        partInfo.setUpdateTime(timestamp);

        // ???????????????
        partInfo.setPartId(NineteenUUIDUtils.uuid());
        int result = partInfoDao.insertParts(partInfo);
        if (result != 1) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(PartsI18n.ADD_PARTS_FAIL));
        }
        partInfoDao.insertUnit(partInfo.getPartId(), partInfo.getAccountabilityUnit());

        //????????????ID
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("partId", partInfo.getPartId());
        return ResultUtils.success(PartsResultCode.SUCCESS, I18nUtils.getString(PartsI18n.ADD_PARTS_SUCCESS), jsonObject);
    }

    /**
     * ??????????????????
     *
     * @param partsCode ??????????????????
     * @return boolean
     */
    private boolean checkPartsCode(String partsCode) {
        List<PartInfo> partInfos = partInfoDao.checkPartsCode(partsCode);
        return partInfos == null || partInfos.size() == 0;
    }

    /**
     * ??????????????????(????????????+??????????????????+7????????????)
     *
     * @param unitCode  ??????
     * @param partsType ????????????
     * @return ????????????
     */
    public String serialNumber(String unitCode, String partsType) {
        return unitCode + partsType + RandomUtil.getRandomOfServen();
    }

    /**
     * ??????????????????
     *
     * @param partInfo ????????????
     * @return boolean
     */
    private boolean checkPartsParams(PartInfo partInfo) {
        if (ObjectUtils.isEmpty(partInfo.getPartName()) || ObjectUtils.isEmpty(partInfo.getPartType())
                || ObjectUtils.isEmpty(partInfo.getTrustee()) || ObjectUtils.isEmpty(partInfo.getAccountabilityUnit())) {
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @param partId   ??????id
     * @param partName ?????????
     * @return boolean
     */
    @Override
    public boolean checkPartsName(String partId, String partName) {
        PartInfo partInfo = partInfoDao.selectByName(partName);
        // deviceId???????????????????????????????????????????????????
        if (StringUtils.isEmpty(partId)) {
            if (!ObjectUtils.isEmpty(partInfo)) {
                return true;
            }
        } else {
            if (!ObjectUtils.isEmpty(partInfo)) {
                if (!partInfo.getPartId().equals(partId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param partInfo ????????????
     * @return Result
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.UPDATE_PART_FUNCTION_CODE,
            dataGetColumnName = "partName", dataGetColumnId = "partId")
    @Override
    public Result updateParts(PartInfo partInfo) {
        // ????????????????????????
        if (checkPartsParams(partInfo) || ObjectUtils.isEmpty(partInfo.getPartId())) {
            return ResultUtils.warn(PartsResultCode.PARTS_PARAM_ERROR, I18nUtils.getString(PartsI18n.PARTS_PARAM_ERROR));
        }
        //???????????????
        partInfo.parameterFormat();
        // ?????????????????????
        if (!partInfo.checkName()) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_ERROR, I18nUtils.getString(PartsI18n.PARTS_NAME_ERROR));
        }
        // ???????????????????????????
        if (checkPartsName(partInfo.getPartId(), partInfo.getPartName())) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_SAME, I18nUtils.getString(PartsI18n.PARTS_NAME_SAME));
        }

        //????????????ID????????????
        if (checkPartsIsExist(partInfo.getPartId())) {
            return ResultUtils.warn(PartsResultCode.PARTS_NOT_EXIST, I18nUtils.getString(PartsI18n.PARTS_NOT_EXIST));
        }

        // ??????????????????????????????
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader("userId");
        partInfo.setUpdateUser(userId);
        partInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        int result = partInfoDao.updateById(partInfo);
        if (result != 1) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getString(PartsI18n.UPDATE_PARTS_FAIL));
        }
        // ???????????????
        partInfoDao.deletePartDeptByPartId(partInfo.getPartId());
        partInfoDao.insertUnit(partInfo.getPartId(), partInfo.getAccountabilityUnit());

        return ResultUtils.success(PartsResultCode.SUCCESS, I18nUtils.getString(PartsI18n.UPDATE_PARTS_SUCCESS), partInfo);
    }

    /**
     * ??????????????????
     *
     * @param partsIds ??????id??????
     * @return Result
     */
    @Override
    public Result deletePartsByIds(String[] partsIds) {
        // ????????????????????????
        for (String partsId : partsIds) {
            if (checkPartsIsExist(partsId)) {
                return ResultUtils.warn(PartsResultCode.PARTS_NOT_EXIST, I18nUtils.getString(PartsI18n.PARTS_NOT_EXIST));
            }
        }
        List<PartInfo> partInfos = partInfoDao.selectPartsByIds(partsIds);

        partInfoDao.deletePartByIds(partsIds);
        for (String partsId : partsIds) {
            partInfoDao.deletePartDeptByPartId(partsId);
        }

        List list = new ArrayList();
        for (PartInfo partInfo : partInfos) {
            String logType = LogConstants.LOG_TYPE_OPERATE;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("partId");
            addLogBean.setDataName("partName");
            addLogBean.setOptObj(partInfo.getPartName());
            addLogBean.setFunctionCode(LogFunctionCodeConstant.DELETE_PART_FUNCTION_CODE);
            addLogBean.setOptObjId(partInfo.getPartId());
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            list.add(addLogBean);
        }
        logProcess.addOperateLogBatchInfoToCall(list, LogConstants.ADD_LOG_LOCAL_FILE);
        return ResultUtils.success(PartsResultCode.SUCCESS, I18nUtils.getString(PartsI18n.DELETE_PARTS_SUCCESS));

    }

    /**
     * ??????????????????
     *
     * @param partsId ??????id
     * @return Result
     * @throws Exception ??????
     */
    @Override
    public Result findPartsById(String partsId) throws Exception {
        if (checkPartsIsExist(partsId)) {
            return ResultUtils.warn(PartsResultCode.PARTS_NOT_EXIST, I18nUtils.getString(PartsI18n.PARTS_NOT_EXIST));
        }

        PartInfoDto partInfoDto = getPartInfoDtoById(partsId);

        return ResultUtils.success(partInfoDto);
    }

    /**
     * ????????????
     *
     * @param queryCondition ????????????
     * @return Result
     * @throws Exception ??????
     */
    @Override
    public Result queryListByPage(QueryCondition<PartInfo> queryCondition, User user) {

        List<FilterCondition> filterConditionList = queryCondition.getFilterConditions();
        convertPartsFilterCondition(filterConditionList);

        //?????????????????????,??????????????????
        SortCondition sortCondition = queryCondition.getSortCondition();
        if (sortCondition == null || StringUtils.isEmpty(sortCondition.getSortField())
                || StringUtils.isEmpty(sortCondition.getSortRule())) {
            sortCondition = new SortCondition();
            sortCondition.setSortRule("desc");
            sortCondition.setSortField("createTime");
            queryCondition.setSortCondition(sortCondition);
        }

        Integer begin = (queryCondition.getPageCondition().getPageNum() - 1) * queryCondition.getPageCondition().getPageSize();
        queryCondition.getPageCondition().setBeginNum(begin);

        //??????like???????????????
        alterLikeFilterCondition(queryCondition.getFilterConditions());

        //??????????????????
        List<PartInfo> partInfoList = partInfoDao.selectPartsPage(queryCondition.getPageCondition(),
                queryCondition.getFilterConditions(), queryCondition.getSortCondition());

        // ??????????????????????????????
        List<PartInfoDto> partInfoDtoList = convertPartInfoListToDto(partInfoList);

        //??????ID???????????????
        updatePartInfoUser(partInfoDtoList);

        Integer count = partInfoDao.selectPartsCount(queryCondition.getFilterConditions());
        // ??????????????????
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page, count, partInfoDtoList);
        return ResultUtils.pageSuccess(pageBean);

    }

    /**
     * ??????????????????
     *
     * @param queryCondition
     * @param user
     * @return
     */
    @Override
    public Integer queryPartsCount(QueryCondition<PartInfo> queryCondition, User user) {
        //??????????????????????????????????????????
        List<FilterCondition> filterConditionList = queryCondition.getFilterConditions();
        convertPartsFilterCondition(filterConditionList);

        //??????like???????????????
        alterLikeFilterCondition(filterConditionList);

        return partInfoDao.selectPartsCount(filterConditionList);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param filterConditionList
     */
    private void convertPartsFilterCondition(List<FilterCondition> filterConditionList) {
        for (FilterCondition fc : filterConditionList) {
            if ("department".equals(fc.getFilterField()) && "like".equalsIgnoreCase(fc.getOperator())) {
                String departmentName = (String) fc.getFilterValue();
                if (!StringUtils.isEmpty(departmentName)) {
                    List<Department> departments = departmentFeign.queryDepartmentFeignByName(departmentName);
                    //??????????????????
                    List<String> departmentIdList = new ArrayList<>();
                    if (departments != null) {
                        for (Department department : departments) {
                            departmentIdList.add(department.getId());
                        }
                    }
                    fc.setFilterField("departmentIdList");
                    fc.setOperator("in");
                    fc.setFilterValue(departmentIdList);
                }
            } else if ("trustee".equals(fc.getFilterField()) && "like".equalsIgnoreCase(fc.getOperator())) {
                String trusteeName = (String) fc.getFilterValue();
                if (!StringUtils.isEmpty(trusteeName)) {
                    List<String> trusteeIdList = userFeign.queryUserIdByName(trusteeName);
                    fc.setFilterField("trusteeIdList");
                    fc.setOperator("in");
                    fc.setFilterValue(trusteeIdList);
                }
            }
        }
    }

    private void alterLikeFilterCondition(List<FilterCondition> filterConditionList) {
        for (FilterCondition filterCondition : filterConditionList) {
            if (StringUtils.equalsIgnoreCase("like", filterCondition.getOperator())) {
                String value = (String) filterCondition.getFilterValue();
                value = value.replace("\\", "\\\\");
                value = value.replace("_", "\\_");
                value = value.replace("%", "\\%");
                value = value.replace("'", "\\'");
                filterCondition.setFilterValue(value);
            }
        }
    }

    private List<PartInfoDto> convertPartInfoListToDto(List<PartInfo> partInfoList) {
        //??????????????????
        List<Department> departmentList = departmentFeign.queryAllDepartment();
        //????????????list
        Map<String, String> departmentMap = new HashMap<>(64);
        for (Department department : departmentList) {
            departmentMap.put(department.getId(), department.getDeptName());
        }

        List<PartInfoDto> partInfoDtoList = new ArrayList<>();

        for (PartInfo partInfo : partInfoList) {
            PartInfoDto partInfoDto = new PartInfoDto();
            try {
                copyProperties(partInfoDto, partInfo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            List<String> deptIds = partInfoDao.getDeptId(partInfo.getPartId());
            partInfoDto.setAccountabilityUnit(deptIds);
            partInfoDto.setPartTypeName(PartType.getMsg(partInfo.getPartType()));

            //??????????????????
            String departmentName = "";
            for (String deptId : deptIds) {
                if (departmentMap.containsKey(deptId)) {
                    departmentName += ", " + departmentMap.get(deptId);
                }
            }
            if (!StringUtils.isEmpty(departmentName)) {
                departmentName = departmentName.substring(2);
            }

            // ???????????????
            fillCTimeUTime(partInfoDto, partInfo.getCreateTime(), partInfo.getUpdateTime());

            partInfoDto.setDepartment(departmentName);
            partInfoDtoList.add(partInfoDto);
        }

        return partInfoDtoList;
    }


    /**
     * ???????????????
     *
     * @param partInfoDtoList
     */
    private void updatePartInfoUser(List<PartInfoDto> partInfoDtoList) {
        if (ObjectUtils.isEmpty(partInfoDtoList)) {
            return;
        }
        Set<String> userIdSet = new HashSet<>();
        for (PartInfoDto dto : partInfoDtoList) {
            if (!StringUtils.isEmpty(dto.getCreateUser())) {
                userIdSet.add(dto.getCreateUser());
            }
            if (!StringUtils.isEmpty(dto.getUpdateUser())) {
                userIdSet.add(dto.getUpdateUser());
            }
            if (!StringUtils.isEmpty(dto.getTrustee())) {
                userIdSet.add(dto.getTrustee());
            }
        }
        //????????????
        List<Object> objList = (List<Object>) userFeign.queryUserByIdList(new ArrayList<>(userIdSet));
        if (objList == null || objList.size() == 0) {
            return;
        }
        Map<String, String> userMap = new HashMap<>(64);
        for (Object obj : objList) {
            User user = JSONArray.toJavaObject((JSON) JSONArray.toJSON(obj), User.class);
            userMap.put(user.getId(), user.getUserName());
        }

        for (PartInfoDto dto : partInfoDtoList) {
            if (!StringUtils.isEmpty(dto.getCreateUser())) {
                dto.setCreateUser(userMap.get(dto.getCreateUser()));
            }
            if (!StringUtils.isEmpty(dto.getUpdateUser())) {
                dto.setUpdateUser(userMap.get(dto.getUpdateUser()));
            }
            if (!StringUtils.isEmpty(dto.getTrustee())) {
                dto.setTrustee(userMap.get(dto.getTrustee()));
            }
        }
    }

    /**
     * ????????????Id??????????????????
     *
     * @param partsId ??????id
     * @return PartInfoDto
     * @throws Exception ??????
     */
    private PartInfoDto getPartInfoDtoById(String partsId) throws Exception {
        PartInfo partInfo = partInfoDao.selectPartsById(partsId);
        List<String> deptIds = partInfoDao.getDeptId(partsId);

        List<Department> departmentList = departmentFeign.queryAllDepartment();
        PartInfoDto partInfoDto = new PartInfoDto();
        copyProperties(partInfoDto, partInfo);
        partInfoDto.setAccountabilityUnit(deptIds);
        partInfoDto.setPartTypeName(PartType.getMsg(partInfo.getPartType()));

        //??????????????????
        String departmentName = "";
        for (String deptId : deptIds) {
            for (Department department : departmentList) {
                if (StringUtils.equals(deptId, department.getId())) {
                    if (departmentName.isEmpty()) {
                        departmentName += department.getDeptName();
                    } else {
                        departmentName += ", " + department.getDeptName();
                    }
                }
            }
        }
        partInfoDto.setDepartment(departmentName);

        // ???????????????
        fillCTimeUTime(partInfoDto, partInfo.getCreateTime(), partInfo.getUpdateTime());
        return partInfoDto;
    }

    /**
     * ????????????????????????
     *
     * @param partId ??????id
     * @return boolean
     */
    private boolean checkPartsIsExist(String partId) {
        PartInfo partInfo = partInfoDao.selectPartsById(partId);
        return ObjectUtils.isEmpty(partInfo);
    }

    /**
     * ??????Long???????????????
     *
     * @param partInfoDto
     * @param createTime
     * @param updateTime
     */
    private void fillCTimeUTime(PartInfoDto partInfoDto, Timestamp createTime, Timestamp updateTime) {
        // ???????????????
        if (!ObjectUtils.isEmpty(updateTime)) {
            partInfoDto.setUtime(updateTime.getTime());
        }
        if (!ObjectUtils.isEmpty(createTime)) {
            partInfoDto.setCtime(createTime.getTime());
        }
    }
}
