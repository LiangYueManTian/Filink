package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.device_api.api.AreaFeign;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.DepartmentFeignDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkDepartmentException;
import com.fiberhome.filink.userserver.service.DepartmentService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.Query;
import java.util.*;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPageBean;

/**
 *   部门Service层
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

    /**
     * 查询单个部门的信息
     * @param depeId 部门id
     * @return 部门信息
     */
    @Override
    public Result queryDeptInfoById(String depeId) {

        //查询部门信息
        Department deptment = departmentDao.selectById(depeId);

        if (deptment.getDeptFatherid() != null) {
            //如果有上级部门信息，则查询上级部门信息更新到该部门信息中
            Department parentDept = departmentDao.selectById(deptment.getDeptFatherid());
            deptment.setParmentDeparmentName(parentDept.getDeptName());
            deptment.setParmentDepartment(parentDept);
        }

        //获取下级部门信息
        String[] ids = {depeId};
        List<Department> childDepartment = departmentDao.queryDeptByParentIds(ids);
        if(childDepartment != null){
            deptment.setChildDepartmentList(childDepartment);
        }

        return  ResultUtils.success(deptment);
    }

    /**
     * 新增部门
     * @param dept 部门信息
     * @return 新增结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "add", logType = "2", functionCode = "1503101", dataGetColumnName = "deptName", dataGetColumnId = "id")
    public Result addDept(Department dept) {

        //获取新建部门id的UUID值
        String deptId = UUIDUtil.getInstance().UUID32();

        String createUserId = RequestInfoUtils.getUserId();
        dept.setCreateUser(createUserId);
        dept.setCreateTime(new Date());
        dept.setId(deptId);
        dept.setDeleted(UserConst.DEFAULT_DELETED);

        //查看父节点的级别，如果父节点为null，则当前级别为一级，如果不为空，则在父节点级别上加一
        String deptFatherId = dept.getDeptFatherid();
        if(StringUtils.isEmpty(deptFatherId)){
            dept.setDeptLevel(UserConst.DEFAULT_LEVEL);
        }else{
            Department department = departmentDao.selectById(deptFatherId);
            String parentLevel = department.getDeptLevel();
            int deptLevel = Integer.parseInt(parentLevel);
            dept.setDeptLevel(Integer.toString(deptLevel + 1));
        }

        int result =departmentDao.insert(dept);
        if(result != 1) {
            throw new FilinkDepartmentException(I18nUtils.getString(UserI18n.ADD_DEPARTMENT_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     *  修改部门
     * @param dept 部门信息
     * @return 修改结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "2", functionCode = "1503102", dataGetColumnName = "deptName", dataGetColumnId = "id")
    public Result updateDept(Department dept) {

        String updateUserId = RequestInfoUtils.getUserId();
        dept.setUpdateUser(updateUserId);
        dept.setUpdateTime(new Date());

        //检测被修改的部门是否存在
        Department updateDepart = departmentDao.selectById(dept.getId());
        if(updateDepart == null){
            return ResultUtils.warn(ResultCode.FAIL,I18nUtils.getString(UserI18n.DEPART_IS_NOT_EXIST));
        }

        int result =departmentDao.updateById(dept);
        //更新父节点id，存在修改后父节点为空的情况
        Integer updateParentNum = departmentDao.updateDepartmentParentId(dept.getDeptFatherid(), dept.getId());

        if(result != 1 || updateParentNum != 1) {
            throw new FilinkDepartmentException(I18nUtils.getString(UserI18n.UPDATE_DEPART_FAIL));
        }

        return ResultUtils.success(ResultCode.SUCCESS);
    }

    /**
     * 删除部门
     * @param parameters 部门id参数
     * @return 删除结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteDept(Parameters parameters){

        if(parameters == null || parameters.getFirstArrayParamter() == null){
            return ResultUtils.warn(UserConst.DEPART_IS_NOT_EXIST,
                    I18nUtils.getString(UserI18n.DEPART_IS_NOT_EXIST));
        }

        List<String> idList = Arrays.asList(parameters.getFirstArrayParamter());
        //如果有子单位不能删除
        List<Department> departments = departmentDao.queryDeptByParentIds(parameters.getFirstArrayParamter());
        if(departments != null && departments.size() > 0){
            return ResultUtils.warn(UserConst.DEPT_HAS_CHILD_DEPT,
                    I18nUtils.getString(UserI18n.DEPT_HAS_CHILD_DEPT));
        }

        //如果有用户调用，不能被删除
        List<User> userList = userDao.queryUserByDepts(parameters.getFirstArrayParamter());
        if(userList != null && userList.size() > 0){
            return ResultUtils.warn(UserConst.USER_USE_DEPT,
                    I18nUtils.getString(UserI18n.USER_USE_DEPT));
        }

        List<Department> departList = departmentDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParamter()));
        addLogByDepts(departList);

        Integer deleteNum = departmentDao.deleteDepartment(parameters.getFirstArrayParamter());
        if(deleteNum != parameters.getFirstArrayParamter().length){
            throw new FilinkDepartmentException(I18nUtils.getString(UserI18n.DELETE_DEPART_FAIL));
        }

        //删除部门信息的时候同时删除部门和区域的关系表
        Boolean deleteAreaFlag = areaFeign.deleteAreaDeptRelation(idList);
        return ResultUtils.success(ResultCode.SUCCESS,I18nUtils.getString(UserI18n.DELETE_DEPART_SUCCESS));
    }

    /**
     * 部门信息日志方法
     * @param deptList 部门信息列表
     */
    private void addLogByDepts(List<Department> deptList) {
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //业务数据
        //遍历业务数据
        if (deptList.size() == 0) {
            return;
        }
        for ( Department dept : deptList) {
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
     * @param departId 部门id
     * @return  部门信息列表
     */
    @Override
    public List<Department> queryFullDepartMent(String departId) {
        List<Department> departList = departmentDao.selectFullDepartMent(departId);
        return departList;
    }

    /**
     * 获取所有没被删除的部门信息
     * @return  部门信息列表
     */
    @Override
    public List<DepartmentFeign> queryAllDepartment() {
        List<DepartmentFeign> departmentList = departmentFeignDao.queryAllDepartmentFeign();
        return departmentList;
    }

    /**
     * 查询所有的一级部门信息
     * @return 部门信息列表
     */
    @Override
    public Result queryTotalDepartment() {
        List<Department> departmentList = departmentDao.queryAllDepartment();
        return ResultUtils.success(departmentList);
    }

    /**
     * 分页条件查询部门信息
     * @param queryCondition  查询的条件类
     * @return  部门信息列表
     */
    @Override
    public Result queryDepartmentList(QueryCondition<DepartmentParamter> queryCondition) {

        //获取参数信息和分页信息
        DepartmentParamter departmentParamter = queryCondition.getBizCondition();
        if(departmentParamter == null){
            departmentParamter = new DepartmentParamter();
        }
        Page page = MyBatiesBuildPage(queryCondition);

        departmentParamter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1) *
                queryCondition.getPageCondition().getPageSize());
        departmentParamter.setPage(queryCondition.getPageCondition().getPageNum());
        departmentParamter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<Department> deptList = departmentDao.queryDepartmentByField(departmentParamter);
        Long deptSize = departmentDao.queryDepartmentNumber(departmentParamter);

        PageBean pageBean = MyBatiesBuildPageBean(page, deptSize.intValue(), deptList);
        return ResultUtils.success(pageBean);
    }

    /**
     * 校验用户部门信息
     * @param departmentParamter 查询条件
     * @return  部门信息列表
     */
    @Override
    public List<Department> queryDepartmenttByField(DepartmentParamter departmentParamter) {

        List<Department> deptList = departmentDao.verifyDepartmentByField(departmentParamter);
        return deptList;
    }

    /**
     * 部门查询条件中显示的部门信息
     * @return  部门信息列表
     */
    @Override
    public Result conditionDepartment() {
        List<Department> departmentList = departmentDao.queryToltalDepartment();
        return ResultUtils.success(departmentList);
    }


} 
