package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.alarmcurrentapi.api.AlarmCurrentFeign;
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
import com.fiberhome.filink.userserver.constant.FunctionCodeConstant;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * 部门Service层
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
     * 查询单个部门的信息
     *
     * @param depeId 部门id
     * @return 部门信息
     */
    @Override
    public Result queryDeptInfoById(String depeId) {

        //查询部门信息
        Department deptment = departmentDao.selectById(depeId);
        //如果为被删除状态,或者不存在，则直接返回空
        if (deptment == null || UserConstant.DEPARTMENT_DELETED_FLAG.equals(deptment.getDeleted())) {
            return ResultUtils.warn(UserResultCode.DATA_IS_NULL);
        }

        if (deptment.getDeptFatherId() != null) {
            //如果有上级部门信息，则查询上级部门信息更新到该部门信息中
            Department parentDept = departmentDao.selectById(deptment.getDeptFatherId());
            deptment.setParentDepartmentName(parentDept.getDeptName());
            deptment.setParentDepartment(parentDept);
        }

        //获取下级部门信息
        String[] ids = {depeId};
        List<Department> childDepartment = departmentDao.queryDeptByParentIds(ids);
        if (childDepartment != null) {
            deptment.setChildDepartmentList(childDepartment);
        }

        return ResultUtils.success(deptment);
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 新增结果
     */
    @Override
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_ADD, logType = LogConstants.LOG_TYPE_SECURITY, functionCode = FunctionCodeConstant.ADD_DEPT_FUNCTION_CODE, dataGetColumnName = "deptName", dataGetColumnId = "id")
    public Result addDept(Department dept) {
        systemLanguageUtil.querySystemLanguage();
        //获取新建部门id的UUID值
        String deptId = UUIDUtil.getInstance().UUID32();

        String createUserId = RequestInfoUtils.getUserId();
        dept.setCreateUser(createUserId);
        dept.setCreateTime(new Date());
        dept.setId(deptId);
        dept.setDeleted(UserConstant.DEFAULT_DELETED);

        //查看父节点的级别，如果父节点为null，则当前级别为一级，如果不为空，则在父节点级别上加一
        String deptFatherId = dept.getDeptFatherId();
        if (StringUtils.isEmpty(deptFatherId)) {
            dept.setDeptLevel(UserConstant.DEFAULT_LEVEL);
        } else {
            Department department = departmentDao.selectById(deptFatherId);
            String parentLevel = department.getDeptLevel();
            int deptLevel = Integer.parseInt(parentLevel);
            dept.setDeptLevel(Integer.toString(deptLevel + 1));
        }

        //去除前后空格
        dept.setDeptName(NameUtils.removeBlank(dept.getDeptName()));
        dept.setDeptChargeUser(NameUtils.removeBlank(dept.getDeptChargeUser()));
        dept.setAddress(NameUtils.removeBlank(dept.getAddress()));
        dept.setRemark(NameUtils.removeBlank(dept.getRemark()));

        //添加部门的时候加锁,给一个对应的锁名
        String lockKey = UserConstant.LOCK_ADD_DEPARTMENT;
        //等待获取锁的时间，单位ms
        int acquireTimeout = UserConstant.ACQUIRE_TIME_OUT;
        //拿到锁的超时时间
        int timeout = UserConstant.TIME_OUT;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (org.apache.commons.lang.StringUtils.isEmpty(lockIdentifier)) {
            throw new FilinkDepartmentException(I18nUtils.getSystemString(UserI18n.ERROR_GETTING_REDIS_LOCK));
        }

        departmentDao.insert(dept);

        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 修改结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = LogConstants.DATA_OPT_TYPE_UPDATE, logType = LogConstants.LOG_TYPE_SECURITY, functionCode = FunctionCodeConstant.UPDATE_DEPT_FUNCTION_CODE, dataGetColumnName = "deptName", dataGetColumnId = "id")
    public Result updateDept(Department dept) {
        systemLanguageUtil.querySystemLanguage();
        String updateUserId = RequestInfoUtils.getUserId();
        dept.setUpdateUser(updateUserId);
        dept.setUpdateTime(new Date());

        //去除输入框中，前后的空格
        dept.setDeptName(NameUtils.removeBlank(dept.getDeptName()));
        dept.setDeptChargeUser(NameUtils.removeBlank(dept.getDeptChargeUser()));
        dept.setAddress(NameUtils.removeBlank(dept.getAddress()));
        dept.setRemark(NameUtils.removeBlank(dept.getRemark()));

        //检测被修改的部门是否存在
        Department updateDepart = departmentDao.selectById(dept.getId());
        if (updateDepart == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.DEPART_IS_NOT_EXIST));
        }

        //不能修改有子单位的单位级别
        if (dept.getDeptFatherId() != null && updateDepart.getDeptFatherId() != null) {
            if (!dept.getDeptFatherId().equals(updateDepart.getDeptFatherId())) {
                int num = departmentDao.queryDeptByParentIds(new String[]{dept.getId()}).size();
                if (num > 0) {
                    return ResultUtils.warn(UserResultCode.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL,
                            I18nUtils.getSystemString(UserI18n.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL));
                }
            }
        } else if (dept.getDeptFatherId() == null && updateDepart.getDeptFatherId() != null) {
            int num = departmentDao.queryDeptByParentIds(new String[]{dept.getId()}).size();
            if (num > 0) {
                return ResultUtils.warn(UserResultCode.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL,
                        I18nUtils.getSystemString(UserI18n.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL));
            }
        } else if (dept.getDeptFatherId() != null && updateDepart.getDeptFatherId() == null) {
            int num = departmentDao.queryDeptByParentIds(new String[]{dept.getId()}).size();
            if (num > 0) {
                return ResultUtils.warn(UserResultCode.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL,
                        I18nUtils.getSystemString(UserI18n.NOT_UPDATE_FATHER_DEPARTMENT_LEVEL));
            }
        }

        int result = departmentDao.updateById(dept);
        //更新父节点id，存在修改后父节点为空的情况
        Integer updateParentNum = departmentDao.updateDepartmentParentId(dept.getDeptFatherId(), dept.getId());

        if (result != 1 || updateParentNum != 1) {
            throw new FilinkDepartmentException(I18nUtils.getSystemString(UserI18n.UPDATE_DEPART_FAIL));
        }
        //发送异步信息，更新用户信息列表
        SendKafkaMsg.sendMessage(updateUserStream, UserConstant.UPDATE_USER_INFO);
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 删除部门
     *
     * @param parameters 部门id参数
     * @return 删除结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDept(Parameters parameters) {

        if (parameters == null || parameters.getFirstArrayParameter() == null) {
            return ResultUtils.warn(UserResultCode.DEPART_IS_NOT_EXIST,
                    I18nUtils.getSystemString(UserI18n.DEPART_IS_NOT_EXIST));
        }

        List<String> idList = Arrays.asList(parameters.getFirstArrayParameter());
        //如果有子单位不能删除
        List<Department> departments = departmentDao.queryDeptByParentIds(parameters.getFirstArrayParameter());
        if (departments != null && departments.size() > 0) {
            return ResultUtils.warn(UserResultCode.DEPT_HAS_CHILD_DEPT,
                    I18nUtils.getSystemString(UserI18n.DEPT_HAS_CHILD_DEPT));
        }

        //如果有用户调用，不能被删除
        List<User> userList = userDao.queryUserByDepts(parameters.getFirstArrayParameter());
        if (userList != null && userList.size() > 0) {
            return ResultUtils.warn(UserResultCode.USER_USE_DEPT,
                    I18nUtils.getSystemString(UserI18n.USER_USE_DEPT));
        }

        //有工单不能删除
        Object workFlow = procBaseFeign.queryProcIdListByDeptIds(Arrays.asList(parameters.getFirstArrayParameter()));
        if (workFlow != null) {
            Map map = (Map) workFlow;
            if (!map.isEmpty()) {
                return ResultUtils.warn(UserResultCode.DEPARTMENT_HAS_WORK_ORDER,
                        I18nUtils.getSystemString(UserI18n.DEPARTMENT_HAS_WORK_ORDER));
            }
        }
        //有当前告警信息的不能删
        boolean hasAlarmCurrent = alarmCurrentFeign.queryAlarmDepartmentFeign(Arrays.asList(parameters.getFirstArrayParameter()));
        if (hasAlarmCurrent) {
            return ResultUtils.warn(UserResultCode.DEPARTMENT_HAS_CURRENT_ALARM,
                    I18nUtils.getSystemString(UserI18n.DEPARTMENT_HAS_CURRENT_ALARM));
        }

        //有巡检任务的不能删除
        Object hasInspection = inspectionTaskFeign.queryInspectionTaskListByDeptIds(Arrays.asList(parameters.getFirstArrayParameter()));
        if (hasInspection != null) {
            Map mapHasInspection = (Map) hasInspection;
            if (!mapHasInspection.isEmpty()) {
                return ResultUtils.warn(UserResultCode.DEPARTMENT_HAS_INSPECTION,
                        I18nUtils.getSystemString(UserI18n.DEPARTMENT_HAS_INSPECTION));
            }

        }
        //添加删除的日志信息
        List<Department> departList = departmentDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParameter()));
        addLogByDepts(departList);

        Integer deleteNum = departmentDao.deleteDepartment(parameters.getFirstArrayParameter());
        if (deleteNum != parameters.getFirstArrayParameter().length) {
            throw new FilinkDepartmentException(I18nUtils.getSystemString(UserI18n.DELETE_DEPART_FAIL));
        }

        //删除部门信息的时候同时删除部门和区域的关系表
        Boolean deleteAreaFlag = areaFeign.deleteAreaDeptRelation(idList);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_DEPART_SUCCESS));
    }

    /**
     * 部门信息日志方法
     *
     * @param deptList 部门信息列表
     */

    private void addLogByDepts(List<Department> deptList) {
        systemLanguageUtil.querySystemLanguage();
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //业务数据
        //遍历业务数据
        if (deptList.size() == 0) {
            return;
        }
        for (Department dept : deptList) {
            //获取日志类型
            String logType = LogConstants.LOG_TYPE_SECURITY;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("id");
            addLogBean.setDataName("deptName");
            //获得操作对象名称
            addLogBean.setFunctionCode("1503103");
            //获得操作对象id
            addLogBean.setOptObjId(dept.getId());
            addLogBean.setOptObj(dept.getDeptName());
            //操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //新增安全日志
            logProcess.addSecurityLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }

    /**
     * 获取部门信息以及下级部门
     *
     * @param departId 部门id
     * @return 部门信息列表
     */
    @Override
    public List<Department> queryFullDepartMent(String departId) {
        List<Department> departList = departmentDao.selectFullDepartMent(departId);
        return departList;
    }

    /**
     * 获取所有没被删除的部门信息
     *
     * @return 部门信息列表
     */
    @Override
    public List<DepartmentFeign> queryAllDepartment() {
        List<DepartmentFeign> departmentList = departmentFeignDao.queryAllDepartmentFeign();
        return departmentList;
    }

    /**
     * 查询所有的一级部门信息
     *
     * @return 部门信息列表
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
     * 分页条件查询部门信息
     *
     * @param queryCondition 查询的条件类
     * @return 部门信息列表
     */
    @Override
    public Result queryDepartmentList(QueryCondition<DepartmentParamter> queryCondition) {

        //获取参数信息和分页信息
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
     * 校验用户部门信息
     *
     * @param departmentParamter 查询条件
     * @return 部门信息列表
     */
    @Override
    public List<Department> queryDepartmenttByField(DepartmentParamter departmentParamter) {

        List<Department> deptList = departmentDao.verifyDepartmentByField(departmentParamter);
        return deptList;
    }

    /**
     * 部门查询条件中显示的部门信息
     *
     * @return 部门信息列表
     */
    @Override
    public Result conditionDepartment() {
        List<Department> departmentList = departmentDao.queryToltalDepartment();
        return ResultUtils.success(departmentList);
    }

    /**
     * 查询指定部门信息
     *
     * @param ids 部门id
     * @return 部门信息
     */
    @Override
    public List<DepartmentFeign> queryDepartmentFeignById(List<String> ids) {
        return departmentFeignDao.queryDepartmentFeignByIds(ids);
    }

    /**
     * 根据区域id查询所有的部门信息并设置是否可用
     *
     * @param areaIds 区域id集合
     * @return 部门信息
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
     * 根据部门名查询部门信息
     *
     * @param deptName 部门名称
     * @return 部门列表
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
     * 查询一个部门的所有上级部门信息
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
     * 将多个部门转换为树
     *
     * @param departmentList 待转化为树的部门列表信息
     */
    public void departmentListToTree(List<Department> departmentList) {

        //将所有部门设置为树信息
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

        //设置每个部门的子部门信息
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
