package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
import com.fiberhome.filink.bean.FiLinkTimeUtils;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.DepartmentFeign;
import com.fiberhome.filink.userserver.bean.DepartmentParamter;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.DepartmentFeignDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkDepartmentException;
import com.fiberhome.filink.userserver.service.DepartmentService;
import com.fiberhome.filink.userserver.stream.UpdateUserStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.NameUtils;
import com.fiberhome.filink.userserver.utils.SendKafkaMsg;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import com.fiberhome.filink.workflowbusinessapi.api.inspectiontask.InspectionTaskFeign;
import com.fiberhome.filink.workflowbusinessapi.api.procbase.ProcBaseFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * ??????Service???
 *
 * @author xuangong
 * @since 2019-01-03
 */
@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentDao, Department> implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private DepartmentFeignDao departmentFeignDao;

    @Autowired
    private AreaFeign areaFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UpdateUserStream updateUserStream;

    @Autowired
    private ProcBaseFeign procBaseFeign;

    @Autowired
    private AlarmCurrentFeign alarmCurrentFeign;

    @Autowired
    private InspectionTaskFeign inspectionTaskFeign;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;

    /**
     * ???????????????????????????
     *
     * @param depeId ??????id
     * @return ????????????
     */
    @Override
    public Result queryDeptInfoById(String depeId) {

        //??????????????????
        Department deptment = departmentDao.selectById(depeId);
        //????????????????????????,????????????????????????????????????
        if (deptment == null || UserConst.DEPARTMENT_DELETED_FLAG.equals(deptment.getDeleted())) {
            return ResultUtils.warn(UserConst.DATA_IS_NULL);
        }

        if (deptment.getDeptFatherId() != null) {
            //????????????????????????????????????????????????????????????????????????????????????
            Department parentDept = departmentDao.selectById(deptment.getDeptFatherId());
            deptment.setParentDepartmentName(parentDept.getDeptName());
            deptment.setParentDepartment(parentDept);
        }

        //????????????????????????
        String[] ids = {depeId};
        List<Department> childDepartment = departmentDao.queryDeptByParentIds(ids);
        if (childDepartment != null) {
            deptment.setChildDepartmentList(childDepartment);
        }

        return ResultUtils.success(deptment);
    }

    /**
     * ????????????
     *
     * @param dept ????????????
     * @return ????????????
     */
    @Override
    @AddLogAnnotation(value = "add", logType = "2", functionCode = "1503101", dataGetColumnName = "deptName", dataGetColumnId = "id")
    public Result addDept(Department dept) {
        systemLanguageUtil.querySystemLanguage();
        //??????????????????id???UUID???
        String deptId = UUIDUtil.getInstance().UUID32();

        String createUserId = RequestInfoUtils.getUserId();
        dept.setCreateUser(createUserId);
        dept.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        dept.setId(deptId);
        dept.setDeleted(UserConst.DEFAULT_DELETED);

        //?????????????????????????????????????????????null??????????????????????????????????????????????????????????????????????????????
        String deptFatherId = dept.getDeptFatherId();
        if (StringUtils.isEmpty(deptFatherId)) {
            dept.setDeptLevel(UserConst.DEFAULT_LEVEL);
        } else {
            Department department = departmentDao.selectById(deptFatherId);
            String parentLevel = department.getDeptLevel();
            int deptLevel = Integer.parseInt(parentLevel);
            dept.setDeptLevel(Integer.toString(deptLevel + 1));
        }

        //??????????????????
        dept.setDeptName(NameUtils.removeBlank(dept.getDeptName()));
        dept.setDeptChargeUser(NameUtils.removeBlank(dept.getDeptChargeUser()));
        dept.setAddress(NameUtils.removeBlank(dept.getAddress()));
        dept.setRemark(NameUtils.removeBlank(dept.getRemark()));

        //???????????????????????????,????????????????????????
        String lockKey = UserConst.LOCK_ADD_DEPARTMENT;
        //?????????????????????????????????ms
        int acquireTimeout = UserConst.ACQUIRE_TIME_OUT;
        //????????????????????????
        int timeout = UserConst.TIME_OUT;
        //???????????????
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (org.apache.commons.lang.StringUtils.isEmpty(lockIdentifier)) {
            throw new FilinkDepartmentException(I18nUtils.getSystemString(UserI18n.ERROR_GETTING_REDIS_LOCK));
        }

        departmentDao.insert(dept);

        //?????????
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ????????????
     *
     * @param dept ????????????
     * @return ????????????
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "2", functionCode = "1503102", dataGetColumnName = "deptName", dataGetColumnId = "id")
    public Result updateDept(Department dept) {
        systemLanguageUtil.querySystemLanguage();
        String updateUserId = RequestInfoUtils.getUserId();
        dept.setUpdateUser(updateUserId);
        dept.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());

        //????????????????????????????????????
        dept.setDeptName(NameUtils.removeBlank(dept.getDeptName()));
        dept.setDeptChargeUser(NameUtils.removeBlank(dept.getDeptChargeUser()));
        dept.setAddress(NameUtils.removeBlank(dept.getAddress()));
        dept.setRemark(NameUtils.removeBlank(dept.getRemark()));

        //????????????????????????????????????
        Department updateDepart = departmentDao.selectById(dept.getId());
        if (updateDepart == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.DEPART_IS_NOT_EXIST));
        }

        //???????????????????????????????????????
        if (dept.getDeptFatherId() != null && updateDepart.getDeptFatherId() != null) {
            if (!dept.getDeptFatherId().equals(updateDepart.getDeptFatherId())) {
                int num = departmentDao.queryDeptByParentIds(new String[]{dept.getId()}).size();
                if (num > 0) {
                    return ResultUtils.warn(UserConst.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL,
                            I18nUtils.getSystemString(UserI18n.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL));
                }
            }
        } else if (dept.getDeptFatherId() == null && updateDepart.getDeptFatherId() != null) {
            int num = departmentDao.queryDeptByParentIds(new String[]{dept.getId()}).size();
            if (num > 0) {
                return ResultUtils.warn(UserConst.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL,
                        I18nUtils.getSystemString(UserI18n.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL));
            }
        } else if (dept.getDeptFatherId() != null && updateDepart.getDeptFatherId() == null) {
            int num = departmentDao.queryDeptByParentIds(new String[]{dept.getId()}).size();
            if (num > 0) {
                return ResultUtils.warn(UserConst.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL,
                        I18nUtils.getSystemString(UserI18n.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL));
            }
        }

        int result = departmentDao.updateById(dept);
        //???????????????id??????????????????????????????????????????
        Integer updateParentNum = departmentDao.updateDepartmentParentId(dept.getDeptFatherId(), dept.getId());

        if (result != 1 || updateParentNum != 1) {
            throw new FilinkDepartmentException(I18nUtils.getSystemString(UserI18n.UPDATE_DEPART_FAIL));
        }
        //?????????????????????????????????????????????
        SendKafkaMsg.sendMessage(updateUserStream, UserConst.UPDATE_USER_INFO);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * ????????????
     *
     * @param parameters ??????id??????
     * @return ????????????
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDept(Parameters parameters) {

        if (parameters == null || parameters.getFirstArrayParameter() == null) {
            return ResultUtils.warn(UserConst.DEPART_IS_NOT_EXIST,
                    I18nUtils.getSystemString(UserI18n.DEPART_IS_NOT_EXIST));
        }

        List<String> idList = Arrays.asList(parameters.getFirstArrayParameter());
        //??????????????????????????????
        List<Department> departments = departmentDao.queryDeptByParentIds(parameters.getFirstArrayParameter());
        if (departments != null && departments.size() > 0) {
            return ResultUtils.warn(UserConst.DEPT_HAS_CHILD_DEPT,
                    I18nUtils.getSystemString(UserI18n.DEPT_HAS_CHILD_DEPT));
        }

        //???????????????????????????????????????
        List<User> userList = userDao.queryUserByDepts(parameters.getFirstArrayParameter());
        if (userList != null && userList.size() > 0) {
            return ResultUtils.warn(UserConst.USER_USE_DEPT,
                    I18nUtils.getSystemString(UserI18n.USER_USE_DEPT));
        }

        //?????????????????????
        Object workFlow = procBaseFeign.queryProcIdListByDeptIds(Arrays.asList(parameters.getFirstArrayParameter()));
        if (workFlow != null) {
            Map map = (Map) workFlow;
            if (!map.isEmpty()) {
                return ResultUtils.warn(UserConst.DEPARTMENT_HAS_WORK_ORDER,
                        I18nUtils.getSystemString(UserI18n.DEPARTMENT_HAS_WORK_ORDER));
            }
        }
        //?????????????????????????????????
        boolean hasAlarmCurrent = alarmCurrentFeign.queryAlarmDepartmentFeign(Arrays.asList(parameters.getFirstArrayParameter()));
        if (hasAlarmCurrent) {
            return ResultUtils.warn(UserConst.DEPARTMENT_HAS_CURRENT_ALARM,
                    I18nUtils.getSystemString(UserI18n.DEPARTMENT_HAS_CURRENT_ALARM));
        }

        //??????????????????????????????
        Object hasInspection = inspectionTaskFeign.queryInspectionTaskListByDeptIds(Arrays.asList(parameters.getFirstArrayParameter()));
        if (hasInspection != null) {
            Map mapHasInspection = (Map) hasInspection;
            if (!mapHasInspection.isEmpty()) {
                return ResultUtils.warn(UserConst.DEPARTMENT_HAS_INSPECTION,
                        I18nUtils.getSystemString(UserI18n.DEPARTMENT_HAS_INSPECTION));
            }

        }
        //???????????????????????????
        List<Department> departList = departmentDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParameter()));
        addLogByDepts(departList);

        Integer deleteNum = departmentDao.deleteDepartment(parameters.getFirstArrayParameter());
        if (deleteNum != parameters.getFirstArrayParameter().length) {
            throw new FilinkDepartmentException(I18nUtils.getSystemString(UserI18n.DELETE_DEPART_FAIL));
        }

        //??????????????????????????????????????????????????????????????????
        Boolean deleteAreaFlag = areaFeign.deleteAreaDeptRelation(idList);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_DEPART_SUCCESS));
    }

    /**
     * ????????????????????????
     *
     * @param deptList ??????????????????
     */

    private void addLogByDepts(List<Department> deptList) {
        systemLanguageUtil.querySystemLanguage();
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //????????????
        //??????????????????
        if (deptList.size() == 0) {
            return;
        }
        for (Department dept : deptList) {
            //??????????????????
            String logType = LogConstants.LOG_TYPE_SECURITY;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("id");
            addLogBean.setDataName("deptName");
            //????????????????????????
            addLogBean.setFunctionCode("1503103");
            //??????????????????id
            addLogBean.setOptObjId(dept.getId());
            addLogBean.setOptObj(dept.getDeptName());
            //???????????????
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //??????????????????
            logProcess.addSecurityLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param departId ??????id
     * @return ??????????????????
     */
    @Override
    public List<Department> queryFullDepartMent(String departId) {
        List<Department> departList = departmentDao.selectFullDepartMent(departId);
        return departList;
    }

    /**
     * ???????????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public List<DepartmentFeign> queryAllDepartment() {
        List<DepartmentFeign> departmentList = departmentFeignDao.queryAllDepartmentFeign();
        return departmentList;
    }

    /**
     * ?????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryTotalDepartment() {
        List<Department> departmentList = departmentDao.queryAllDepartment();
        for (Department department : departmentList) {
            String deptFatherId = department.getDeptFatherId();
            if (StringUtils.isEmpty(deptFatherId)) {
                continue;
            }
            for (Department department1 : departmentList) {
                if (deptFatherId.equals(department1.getId())) {
                    department.setParentDepartmentName(department1.getDeptName());
                }
            }
        }
        return ResultUtils.success(departmentList);
    }

    /**
     * ??????????????????????????????
     *
     * @param queryCondition ??????????????????
     * @return ??????????????????
     */
    @Override
    public Result queryDepartmentList(QueryCondition<DepartmentParamter> queryCondition) {

        //?????????????????????????????????
        DepartmentParamter departmentParamter = queryCondition.getBizCondition();
        if (departmentParamter == null) {
            departmentParamter = new DepartmentParamter();
        }
        Page page = myBatiesBuildPage(queryCondition);

        departmentParamter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        departmentParamter.setPage(queryCondition.getPageCondition().getPageNum());
        departmentParamter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<Department> deptList = departmentDao.queryDepartmentByField(departmentParamter);
        departmentListToTree(deptList);
        Long deptSize = departmentDao.queryDepartmentNumber(departmentParamter);

        PageBean pageBean = myBatiesBuildPageBean(page, deptSize.intValue(), deptList);
        return ResultUtils.success(pageBean);
    }

    /**
     * ????????????????????????
     *
     * @param departmentParamter ????????????
     * @return ??????????????????
     */
    @Override
    public List<Department> queryDepartmenttByField(DepartmentParamter departmentParamter) {

        List<Department> deptList = departmentDao.verifyDepartmentByField(departmentParamter);
        return deptList;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result conditionDepartment() {
        List<Department> departmentList = departmentDao.queryToltalDepartment();
        return ResultUtils.success(departmentList);
    }

    /**
     * ????????????????????????
     *
     * @param ids ??????id
     * @return ????????????
     */
    @Override
    public List<DepartmentFeign> queryDepartmentFeignById(List<String> ids) {

        return departmentFeignDao.queryDepartmentFeignByIds(ids);
    }

    /**
     * ????????????id????????????????????????????????????????????????
     *
     * @param areaIds ??????id??????
     * @return ????????????
     */
    @Override
    public Result queryAllDepartmentForPageSelection(List<String> areaIds) {
        Result<List<Object>> result = areaFeign.selectAreaDeptInfoByAreaIdsForPageSelection(areaIds);
        List<String> deptIds = new ArrayList<>();
        List<Object> data = (List<Object>) result.getData();
        for (Object o : data) {
            AreaDeptInfo areaDeptInfo = JSONArray.toJavaObject((JSON) JSONArray.toJSON(o), AreaDeptInfo.class);
            deptIds.add(areaDeptInfo.getDeptId());
        }
        List<DepartmentFeign> departmentFeigns = departmentFeignDao.queryAllDepartmentFeign();
        for (DepartmentFeign departmentFeign : departmentFeigns) {
            if (deptIds.contains(departmentFeign.getId())) {
                departmentFeign.setHasThisArea(true);
            }
        }
        return ResultUtils.success(departmentFeigns);
    }

    /**
     * ?????????????????????????????????
     *
     * @param deptName ????????????
     * @return ????????????
     */
    @Override
    public List<DepartmentFeign> queryDepartmentFeignByName(String deptName) {

        return departmentFeignDao.queryDepartmentFeignByName(deptName);
    }

    @Override
    public Department queryDepartTree(String userId) {

        User user = userDao.queryUserById(userId);
        String deptId = user.getDeptId();
        Department department = departmentDao.selectById(deptId);

        Department parentDepartment = queryAllParentDepartment(department);
        List<Department> departmentList = new ArrayList<>();
        departmentList.add(parentDepartment);
        departmentListToTree(departmentList);
        return parentDepartment;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param department
     */
    private Department queryAllParentDepartment(Department department) {

        String parentId = department.getDeptFatherId();
        if (StringUtils.isNotBlank(parentId)) {
            Department parentDeparment = departmentDao.selectById(parentId);
            return queryAllParentDepartment(parentDeparment);
        }
        return department;
    }


    /**
     * ???????????????????????????
     *
     * @param departmentList ????????????????????????????????????
     */
    public void departmentListToTree(List<Department> departmentList) {

        //?????????????????????????????????
        List<Department> departmentListAll = departmentDao.queryTotalDepartment();
        if (CheckEmptyUtils.collectEmpty(departmentListAll)) {
            departmentListAll.forEach(department -> {
                List<Department> childList = departmentListAll.stream().filter(singleDepart ->
                        department.getId().equals(singleDepart.getDeptFatherId())).collect(Collectors.toList());
                if (CheckEmptyUtils.collectEmpty(childList)) {
                    childList.forEach(childDept -> {
                        childDept.setParentDepartmentName(department.getDeptName());
                    });
                }
                department.setChildDepartmentList(childList);
            });
        }

        //????????????????????????????????????
        departmentList.forEach(department -> {
            if (CheckEmptyUtils.collectEmpty(departmentListAll)) {
                Department fullDept = departmentListAll.stream().filter(dept -> dept.getId().
                        equals(department.getId())).findFirst().orElse(null);
                if (fullDept != null) {
                    department.setChildDepartmentList(fullDept.getChildDepartmentList());
                }
            }
        });
    }
} 
