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
import com.fiberhome.filink.parts.constant.*;
import com.fiberhome.filink.parts.dao.PartInfoDao;
import com.fiberhome.filink.parts.dto.PartInfoDto;
import com.fiberhome.filink.parts.service.PartInfoService;
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

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;
import static org.apache.commons.beanutils.BeanUtils.copyProperties;

/**
 * <p>
 * 服务实现类
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
     * 新增配件
     *
     * @param partInfo 配件信息
     * @return Result
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.INSERT_PART_FUNCTION_CODE,
            dataGetColumnName = "partName", dataGetColumnId = "partId")
    @Override
    public Result addPart(PartInfo partInfo) {
        // 校验参数是否为空
        if (checkPartsParams(partInfo)) {
            return ResultUtils.warn(PartsResultCode.PARTS_PARAM_ERROR, I18nUtils.getSystemString(PartsI18n.PARTS_PARAM_ERROR));
        }
        //参数格式化
        partInfo.parameterFormat();
        // 校验配件名合法性
        if (!partInfo.checkName()) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_ERROR, I18nUtils.getSystemString(PartsI18n.PARTS_NAME_ERROR));
        }
        // 校验配件名的唯一性
        if (checkPartsName(null, partInfo.getPartName())) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_SAME, I18nUtils.getSystemString(PartsI18n.PARTS_NAME_SAME));
        }
        // 获取编号
        String partsCode;
        while (!checkPartsCode(partsCode = serialNumber(unitCode, partsType))) {
            log.warn("重复的partsCode--------------" + partsCode);
        }
        log.warn("最终的partsCode-------------" + partsCode);
        partInfo.setPartCode(partsCode);

        //从缓存中获取当前用户信息
        String userId = RequestInfoUtils.getUserId();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        partInfo.setCreateUser(userId);
        partInfo.setUpdateUser(userId);
        partInfo.setCreateTime(timestamp);
        partInfo.setUpdateTime(timestamp);

        //查询用户的一级部门ID
        Department department = departmentFeign.queryDepartTreeByDeptId(userId);
        if (ObjectUtils.isEmpty(department)) {
            return ResultUtils.warn(PartsResultCode.USER_INFO_ERROR, I18nUtils.getSystemString(PartsI18n.USER_INFO_ERROR));
        }
        partInfo.setLevelOneDeptId(department.getId());

        // 存入数据库
        partInfo.setPartId(NineteenUUIDUtils.uuid());
        int result = partInfoDao.insertParts(partInfo);
        if (result != 1) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(PartsI18n.ADD_PARTS_FAIL));
        }
        partInfoDao.insertUnit(partInfo.getPartId(), partInfo.getAccountabilityUnit());

        //返回配件ID
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("partId", partInfo.getPartId());
        return ResultUtils.success(PartsResultCode.SUCCESS, I18nUtils.getSystemString(PartsI18n.ADD_PARTS_SUCCESS), jsonObject);
    }

    /**
     * 校验资产编号
     *
     * @param partsCode 配件资产编号
     * @return boolean
     */
    private boolean checkPartsCode(String partsCode) {
        List<PartInfo> partInfos = partInfoDao.checkPartsCode(partsCode);
        return partInfos == null || partInfos.size() == 0;
    }

    /**
     * 生成资产编号(单位编号+设施类型编号+7位流水号)
     *
     * @param unitCode  单位
     * @param partsType 设施类型
     * @return 资产编号
     */
    public String serialNumber(String unitCode, String partsType) {
        return unitCode + partsType + RandomUtil.getRandomOfServen();
    }

    /**
     * 校验必填参数
     *
     * @param partInfo 配件信息
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
     * 校验配件名是否重复
     *
     * @param partId   配件id
     * @param partName 配件名
     * @return boolean
     */
    @Override
    public boolean checkPartsName(String partId, String partName) {
        PartInfo partInfo = partInfoDao.selectByName(partName);
        // deviceId为空时，新增校验；不为空，修改校验
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
     * 修改配件
     *
     * @param partInfo 配件信息
     * @return Result
     */
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_OPERATE,
            functionCode = LogFunctionCodeConstant.UPDATE_PART_FUNCTION_CODE,
            dataGetColumnName = "partName", dataGetColumnId = "partId")
    @Override
    public Result updateParts(PartInfo partInfo) {
        // 校验参数是否为空
        if (checkPartsParams(partInfo) || ObjectUtils.isEmpty(partInfo.getPartId())) {
            return ResultUtils.warn(PartsResultCode.PARTS_PARAM_ERROR, I18nUtils.getSystemString(PartsI18n.PARTS_PARAM_ERROR));
        }
        //参数格式化
        partInfo.parameterFormat();
        // 校验名称合法性
        if (!partInfo.checkName()) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_ERROR, I18nUtils.getSystemString(PartsI18n.PARTS_NAME_ERROR));
        }
        // 校验配件名的唯一性
        if (checkPartsName(partInfo.getPartId(), partInfo.getPartName())) {
            return ResultUtils.warn(PartsResultCode.PARTS_NAME_SAME, I18nUtils.getSystemString(PartsI18n.PARTS_NAME_SAME));
        }

        //校验配件ID是否存在
        if (checkPartsIsExist(partInfo.getPartId())) {
            return ResultUtils.warn(PartsResultCode.PARTS_NOT_EXIST, I18nUtils.getSystemString(PartsI18n.PARTS_NOT_EXIST));
        }

        // 从缓存中获取用户信息
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader("userId");
        partInfo.setUpdateUser(userId);
        partInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

        //查询当前用户的一级部门ID
        Department department = departmentFeign.queryDepartTreeByDeptId(userId);
        if (department == null) {
            return ResultUtils.warn(PartsResultCode.USER_INFO_ERROR, I18nUtils.getSystemString(PartsI18n.USER_INFO_ERROR));
        }
        partInfo.setLevelOneDeptId(department.getId());

        int result = partInfoDao.updateById(partInfo);
        if (result != 1) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(PartsI18n.UPDATE_PARTS_FAIL));
        }
        // 更新关系表
        partInfoDao.deletePartDeptByPartId(partInfo.getPartId());
        partInfoDao.insertUnit(partInfo.getPartId(), partInfo.getAccountabilityUnit());

        return ResultUtils.success(PartsResultCode.SUCCESS, I18nUtils.getSystemString(PartsI18n.UPDATE_PARTS_SUCCESS), partInfo);
    }

    /**
     * 批量删除配件
     *
     * @param partsIds 配件id数组
     * @return Result
     */
    @Override
    public Result deletePartsByIds(String[] partsIds) {
        // 校验设施是否存在
        for (String partsId : partsIds) {
            if (checkPartsIsExist(partsId)) {
                return ResultUtils.warn(PartsResultCode.PARTS_NOT_EXIST, I18nUtils.getSystemString(PartsI18n.PARTS_NOT_EXIST));
            }
        }
        List<PartInfo> partInfos = partInfoDao.selectPartsByIds(partsIds);

        //校验部门权限
        String userId = RequestInfoUtils.getUserId();
        Department department = departmentFeign.queryDepartTreeByDeptId(userId);
        if (department == null) {
            return ResultUtils.warn(PartsResultCode.USER_INFO_ERROR, I18nUtils.getSystemString(PartsI18n.USER_INFO_ERROR));
        }
        for (PartInfo pi : partInfos) {
            if (!StringUtils.isEmpty(pi.getLevelOneDeptId()) && !pi.getLevelOneDeptId().equals(department.getId())) {
                return ResultUtils.warn(PartsResultCode.PARTS_NOT_AUTHORIZED,
                        I18nUtils.getSystemString(PartsI18n.PARTS_NOT_AUTHORIZED));
            }
        }

        //删除配件
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
        return ResultUtils.success(PartsResultCode.SUCCESS, I18nUtils.getSystemString(PartsI18n.DELETE_PARTS_SUCCESS));

    }

    /**
     * 获取配件详情
     *
     * @param partsId 配件id
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result findPartsById(String partsId) throws Exception {
        if (checkPartsIsExist(partsId)) {
            return ResultUtils.warn(PartsResultCode.PARTS_NOT_EXIST, I18nUtils.getSystemString(PartsI18n.PARTS_NOT_EXIST));
        }

        PartInfoDto partInfoDto = getPartInfoDtoById(partsId);

        return ResultUtils.success(partInfoDto);
    }

    /**
     * 模糊查询
     *
     * @param queryCondition 查询条件
     * @return Result
     * @throws Exception 异常
     */
    @Override
    public Result queryListByPage(QueryCondition<PartInfo> queryCondition, User user) {

        String userId;
        if (ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user.getId())) {
            userId = RequestInfoUtils.getUserId();
        } else {
            userId = user.getId();
        }
        List<FilterCondition> filterConditionList = queryCondition.getFilterConditions();

        //添加部门过滤权限
        if (!Constant.ADMIN.equals(userId)) {
            Department department = departmentFeign.queryDepartTreeByDeptId(userId);
            if (department == null) {
                return ResultUtils.warn(PartsResultCode.USER_INFO_ERROR, I18nUtils.getSystemString(PartsI18n.USER_INFO_ERROR));
            }
            addAuth(department.getId(), filterConditionList, userId);
        }
        convertPartsFilterCondition(filterConditionList);

        //若排序条件为空,默认时间排序
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

        //修改like过滤条件值
        alterLikeFilterCondition(queryCondition.getFilterConditions());

        //查询基本字段
        List<PartInfo> partInfoList = partInfoDao.selectPartsPage(queryCondition.getPageCondition(),
                queryCondition.getFilterConditions(), queryCondition.getSortCondition());

        // 转换为返回界面的数据
        List<PartInfoDto> partInfoDtoList = convertPartInfoListToDto(partInfoList);

        //用户ID改为用户名
        updatePartInfoUser(partInfoDtoList);

        Integer count = partInfoDao.selectPartsCount(queryCondition.getFilterConditions());
        // 构造分页条件
        Page page = myBatiesBuildPage(queryCondition);
        PageBean pageBean = myBatiesBuildPageBean(page, count, partInfoDtoList);
        return ResultUtils.pageSuccess(pageBean);

    }

    /**
     * 添加部门权限
     *
     * @param id
     * @param filterConditionList
     */
    private void addAuth(String id, List<FilterCondition> filterConditionList, String userId) {
        if (Constant.ADMIN.equals(userId)) {
            return;
        }
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setFilterField("levelOneDeptId");
        filterCondition.setOperator("eq");
        filterCondition.setFilterValue(id);
        filterConditionList.add(filterCondition);
    }

    /**
     * 查询配件数量
     *
     * @param queryCondition
     * @param user
     * @return
     */
    @Override
    public Integer queryPartsCount(QueryCondition<PartInfo> queryCondition, User user) {
        //转换部门和委托人名称查询条件
        List<FilterCondition> filterConditionList = queryCondition.getFilterConditions();

        //添加部门过滤权限
        Department department = departmentFeign.queryDepartTreeByDeptId(user.getId());
        if (department == null) {
            return 0;
        }
        addAuth(department.getId(), filterConditionList, user.getId());
        convertPartsFilterCondition(filterConditionList);

        //修改like过滤条件值
        alterLikeFilterCondition(filterConditionList);

        return partInfoDao.selectPartsCount(filterConditionList);
    }

    /**
     * 转换部门和委托人名称查询条件
     *
     * @param filterConditionList
     */
    private void convertPartsFilterCondition(List<FilterCondition> filterConditionList) {
        for (FilterCondition fc : filterConditionList) {
            if ("department".equals(fc.getFilterField()) && "like".equalsIgnoreCase(fc.getOperator())) {
                String departmentName = (String) fc.getFilterValue();
                if (!StringUtils.isEmpty(departmentName)) {
                    List<Department> departments = departmentFeign.queryDepartmentFeignByName(departmentName);
                    //部门可能为空
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
        //查询所有部门
        List<Department> departmentList = departmentFeign.queryAllDepartment();
        //转为部门list
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

            //填写部门名称
            String departmentName = "";
            for (String deptId : deptIds) {
                if (departmentMap.containsKey(deptId)) {
                    departmentName += ", " + departmentMap.get(deptId);
                }
            }
            if (!StringUtils.isEmpty(departmentName)) {
                departmentName = departmentName.substring(2);
            }

            // 添加时间戳
            fillCTimeUTime(partInfoDto, partInfo.getCreateTime(), partInfo.getUpdateTime());

            partInfoDto.setDepartment(departmentName);
            partInfoDtoList.add(partInfoDto);
        }

        return partInfoDtoList;
    }


    /**
     * 填充用户名
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
        //查询用户
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
     * 根据配件Id查询配件信息
     *
     * @param partsId 配件id
     * @return PartInfoDto
     * @throws Exception 异常
     */
    private PartInfoDto getPartInfoDtoById(String partsId) throws Exception {
        PartInfo partInfo = partInfoDao.selectPartsById(partsId);
        List<String> deptIds = partInfoDao.getDeptId(partsId);

        List<Department> departmentList = departmentFeign.queryAllDepartment();
        PartInfoDto partInfoDto = new PartInfoDto();
        copyProperties(partInfoDto, partInfo);
        partInfoDto.setAccountabilityUnit(deptIds);
        partInfoDto.setPartTypeName(PartType.getMsg(partInfo.getPartType()));

        //填写部门名称
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

        // 添加时间戳
        fillCTimeUTime(partInfoDto, partInfo.getCreateTime(), partInfo.getUpdateTime());
        return partInfoDto;
    }

    /**
     * 校验配件是否存在
     *
     * @param partId 配件id
     * @return boolean
     */
    private boolean checkPartsIsExist(String partId) {
        PartInfo partInfo = partInfoDao.selectPartsById(partId);
        return ObjectUtils.isEmpty(partInfo);
    }

    /**
     * 添加Long类型时间戳
     *
     * @param partInfoDto
     * @param createTime
     * @param updateTime
     */
    private void fillCTimeUTime(PartInfoDto partInfoDto, Timestamp createTime, Timestamp updateTime) {
        // 返回时间戳
        if (!ObjectUtils.isEmpty(updateTime)) {
            partInfoDto.setUtime(updateTime.getTime());
        }
        if (!ObjectUtils.isEmpty(createTime)) {
            partInfoDto.setCtime(createTime.getTime());
        }
    }
}
