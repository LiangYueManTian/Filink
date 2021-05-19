package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.logapi.annotation.AddLogAnnotation;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.logapi.utils.LogConstants;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.server_common.utils.MpQueryHelper;
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
import com.fiberhome.filink.userserver.bean.*;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkRoleException;
import com.fiberhome.filink.userserver.service.RoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPage;
import static com.fiberhome.filink.server_common.utils.MpQueryHelper.MyBatiesBuildPageBean;

/**
 *   角色Service
 *
 * @author xuangong
 * @since 2019-01-03
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private UserDao userDao;

    /**
     * 查询角色列表信息
     * @param roleQueryCondition 查询条件
     * @return 角色列表信息
     */
    @Override
    public Result queryRoleList(QueryCondition<Role> roleQueryCondition) {
        Page page = MyBatiesBuildPage(roleQueryCondition);

        //当进入首页的时候，默认使用create_time降序排列
        if(roleQueryCondition.getSortCondition() == null ||
                roleQueryCondition.getSortCondition().getSortField() == null){
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("create_time");
            sortCondition.setSortRule("desc");
            roleQueryCondition.setSortCondition(sortCondition);
        }

        EntityWrapper entityWrapper = MpQueryHelper.MyBatiesBuildQuery(roleQueryCondition);
        List<Role> roleList = roleDao.selectPage(page, entityWrapper);

        Integer count = roleDao.selectCount(entityWrapper);
        PageBean pageBean = MyBatiesBuildPageBean(page, count, roleList);

        return ResultUtils.success(pageBean);
    }

    /**
     * 查询单个角色的信息
     * @param roleId 角色id
     * @return 角色信息
     */
    @Override
    public Result queryRoleInfoById(String roleId) {

        Role role = roleDao.selectById(roleId);
        return  ResultUtils.success(role);
    }

    /**
     * 新增角色
     * @param role 角色信息
     * @return 新增结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "add", logType = "2", functionCode = "1502101", dataGetColumnName = "roleName", dataGetColumnId = "id")
    public Result addRole(Role role) {

        //获取新建角色id的UUID值
        String roleId = UUIDUtil.getInstance().UUID32();
        if(role == null || role.getRoleName() == null){
            return ResultUtils.warn(ResultCode.FAIL,I18nUtils.getString(UserI18n.WRITE_FULL_ROLE_INFO));
        }

        //校验是否拥有相同角色名的角色
        Role verityRole = roleDao.verityRoleByName(role.getRoleName());
        if(verityRole != null){
            return ResultUtils.warn(ResultCode.FAIL,I18nUtils.getString(UserI18n.ROLE_NAME_HAS_USER));
        }

        String createUserId = RequestInfoUtils.getUserId();
        role.setCreateUser(createUserId);
        role.setCreateTime(new Date());
        role.setId(roleId);

        int result =roleDao.insert(role);
        if(result !=1) {
            throw new FilinkRoleException(I18nUtils.getString(UserI18n.ADD_ROLE_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS,I18nUtils.getString(UserI18n.ADD_ROLE_SUCCESS));
    }

    /**
     *  修改角色
     * @param role 角色信息
     * @return 修改结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @AddLogAnnotation(value = "update", logType = "2", functionCode = "1502102", dataGetColumnName = "roleName", dataGetColumnId = "id")
    public Result updateRole(Role role) {

        String updateUserId = RequestInfoUtils.getUserId();

        role.setUpdateUser(updateUserId);
        role.setUpdateTime(new Date());

        //先检验更新的角色是否存在
        Role updateRole = roleDao.selectById(role.getId());
        if(updateRole == null){
            return ResultUtils.warn(UserConst.ROLE_IS_NOT_EXIST,I18nUtils.getString(UserI18n.ROLE_IS_NOT_EXIST));
        }

        Integer result = roleDao.updateById(role);
        if(result != 1) {
            throw new FilinkRoleException(I18nUtils.getString(UserI18n.UPDATE_ROLE_FAIL));
        }
        return ResultUtils.success(ResultCode.SUCCESS,I18nUtils.getString(UserI18n.UPDATE_ROLE_SUCCESS));
    }

    /**
     * 删除多个角色
     * @param parameters 角色id参数
     * @return 删除结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteRole(Parameters parameters) {

        if(parameters == null || parameters.getFirstArrayParamter() == null){
            return ResultUtils.warn(UserConst.ROLE_IS_NOT_EXIST,
                    I18nUtils.getString(UserI18n.ROLE_IS_NOT_EXIST));
        }

        //判断当前的角色有没有用户正在使用
        List<User> users = userDao.queryUserByRoles(parameters.getFirstArrayParamter());
        if(users != null && users.size() > 0){
            return ResultUtils.warn(UserConst.USER_USE_ROLE,I18nUtils.getString(UserI18n.USER_USE_ROLE));
        }

        //判断是否为默认的角色
        List<Role> deleteRoles = roleDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParamter()));
        Role defaultRole = deleteRoles.stream().filter(role -> role.getDefaultRole() == 1).findFirst().orElse(null);
        //如果不为默认的角色，则可以删除
        if(defaultRole == null){
            addLogByRoles(deleteRoles);

            int result =roleDao.deleteRoles(parameters.getFirstArrayParamter());
            if( result != parameters.getFirstArrayParamter().length) {
                throw new FilinkRoleException(I18nUtils.getString(UserI18n.DELETE_ROLE_FAIL));
            }
            return ResultUtils.success(ResultCode.SUCCESS,I18nUtils.getString(UserI18n.DELETE_ROLE_SUCCESS));
        }
        return ResultUtils.warn(UserConst.NOT_ALLOW_DELETE_ADMIN,I18nUtils.getString(UserI18n.NOT_ALLOW_DELETE_DEFAULT_ROLE));
    }

    /**
     * 条件查询角色信息，适合单表查询
     * @param userQueryCondition 查询条件
     * @return  角色信息列表
     */
    @Override
    public List<Role> queryRoleByField(QueryCondition<Role> userQueryCondition) {

        List<FilterCondition> filterConditions = userQueryCondition.getFilterConditions();
        FilterCondition filterCondition = new FilterCondition();

        //查询未被删除的用户
        filterCondition.setFilterField("is_deleted");
        filterCondition.setOperator("eq");
        filterCondition.setFilterValue("0");
        filterConditions.add(filterCondition);
        userQueryCondition.setFilterConditions(filterConditions);

        Page page = MyBatiesBuildPage(userQueryCondition);
        EntityWrapper entityWrapper = MpQueryHelper.MyBatiesBuildQuery(userQueryCondition);

        List<Role> roleList = roleDao.selectPage(page, entityWrapper);

        return roleList;
    }

    /**
     * 查询所有的角色信息
     * @return  角色信息列表
     */
    @Override
    public Result queryAllRoles() {
        List<Role> roles = roleDao.queryAllRoles();
        return ResultUtils.success(ResultCode.SUCCESS,null,roles);
    }

    /**
     * 条件分页排序查询角色信息
     * @param queryCondition  角色参数
     * @return  角色信息列表
     */
    @Override
    public Result queryRoleByFieldAndCondition(QueryCondition<RoleParamter> queryCondition) {

        //获取参数信息和分页信息
        RoleParamter roleParamter = queryCondition.getBizCondition();
        if(roleParamter == null){
            roleParamter = new RoleParamter();
        }
        Page page = MyBatiesBuildPage(queryCondition);

        roleParamter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1) *
                queryCondition.getPageCondition().getPageSize());
        roleParamter.setPage(queryCondition.getPageCondition().getPageNum());
        roleParamter.setPageSize(queryCondition.getPageCondition().getPageSize());
        
        List<Role> roleList = roleDao.queryRoleByField(roleParamter);
        Long roleNumber = roleDao.queryRoleNumber(roleParamter);

        PageBean pageBean = MyBatiesBuildPageBean(page, roleNumber.intValue(), roleList);
        return ResultUtils.success(pageBean);
    }


    /**
     * 添加角色日志方法
     * @param roleList  角色信息列表
     */
    private void addLogByRoles(List<Role> roleList) {
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //业务数据
        //遍历业务数据
        if (roleList.size() == 0) {
            return;
        }
        for ( Role role : roleList) {
            //获取日志类型
            String logType = LogConstants.LOG_TYPE_SECURITY;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("id");
            addLogBean.setDataName("roleName");
            //获得操作对象名称
            addLogBean.setFunctionCode("1502103");
            //获得操作对象id
            addLogBean.setOptObjId(role.getId());
            addLogBean.setOptObj(role.getRoleName());
            //操作为新增
            addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);
            addLogBeanList.add(addLogBean);
        }
        if (0 < addLogBeanList.size()) {
            //新增操作日志
            logProcess.addSecurityLogBatchInfoToCall(addLogBeanList, LogConstants.ADD_LOG_LOCAL_FILE);
        }
    }
}
