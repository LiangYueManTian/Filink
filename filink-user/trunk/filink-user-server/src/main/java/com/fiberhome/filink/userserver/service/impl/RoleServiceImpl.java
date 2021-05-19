package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageBean;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.mysql.MpQueryHelper;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.Parameters;
import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.RoleDeviceType;
import com.fiberhome.filink.userserver.bean.RoleParameter;
import com.fiberhome.filink.userserver.bean.RolePermission;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.constant.FunctionCodeConstant;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
import com.fiberhome.filink.userserver.dao.PermissionDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.RoleDeviceTypeDao;
import com.fiberhome.filink.userserver.dao.RolePermissionDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkRoleException;
import com.fiberhome.filink.userserver.service.RoleService;
import com.fiberhome.filink.userserver.stream.UpdateUserStream;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.NameUtils;
import com.fiberhome.filink.userserver.utils.RoleUtils;
import com.fiberhome.filink.userserver.utils.SendKafkaMsg;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPage;
import static com.fiberhome.filink.mysql.MpQueryHelper.myBatiesBuildPageBean;


/**
 * 角色Service
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

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private RoleDeviceTypeDao roleDeviceTypeDao;

    @Autowired
    private UpdateUserStream updateUserStream;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;


    /**
     * 查询角色列表信息
     *
     * @param roleQueryCondition 查询条件
     * @return 角色列表信息
     */
    @Override
    public Result queryRoleList(QueryCondition<Role> roleQueryCondition) {
        Page page = myBatiesBuildPage(roleQueryCondition);

        //当进入首页的时候，默认使用create_time降序排列
        if (roleQueryCondition.getSortCondition() == null
                || roleQueryCondition.getSortCondition().getSortField() == null) {
            SortCondition sortCondition = new SortCondition();
            sortCondition.setSortField("create_time");
            sortCondition.setSortRule("desc");
            roleQueryCondition.setSortCondition(sortCondition);
        }

        EntityWrapper entityWrapper = MpQueryHelper.myBatiesBuildQuery(roleQueryCondition);
        List<Role> roleList = roleDao.selectPage(page, entityWrapper);

        Integer count = roleDao.selectCount(entityWrapper);
        PageBean pageBean = myBatiesBuildPageBean(page, count, roleList);

        return ResultUtils.success(pageBean);
    }

    /**
     * 查询单个角色的信息
     *
     * @param roleId 角色id
     * @return 角色信息
     */
    @Override
    public Result queryRoleInfoById(String roleId) {

        Role role = roleDao.queryRoleInfoById(roleId);
        //如果查询出的角色信息为空
        if (role == null) {
            return ResultUtils.warn(UserResultCode.DATA_IS_NULL);
        }
        //将角色信息改成树结构
        List<Permission> permissionList = role.getPermissionList();
        if (permissionList != null) {
            List<Permission> treeRole = RoleUtils.getTreeRole(permissionList);
            role.setPermissionList(treeRole);
        }

        List<RoleDeviceType> roleDeviceTypeList = role.getRoleDevicetypeList();
        if (roleDeviceTypeList != null) {
            List<RoleDeviceType> treeDeviceType = RoleUtils.getTreeDeviceType(roleDeviceTypeList);
            role.setRoleDevicetypeList(treeDeviceType);
        }

        return ResultUtils.success(role);
    }

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 新增结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addRole(Role role) {
        //获取新建角色id的UUID值
        String roleId = UUIDUtil.getInstance().UUID32();
        if (role == null || role.getRoleName() == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.WRITE_FULL_ROLE_INFO));
        }

        //校验是否拥有相同角色名的角色
        Role verityRole = roleDao.verityRoleByName(role.getRoleName());
        if (verityRole != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.ROLE_NAME_HAS_USER));
        }

        String createUserId = RequestInfoUtils.getUserId();
        role.setCreateUser(createUserId);
        role.setCreateTime(new Date());
        role.setId(roleId);

        role.setRoleName(NameUtils.removeBlank(role.getRoleName()));
        role.setRemark(NameUtils.removeBlank(role.getRemark()));

        int result = roleDao.insert(role);
        if (result != 1) {
            throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
        }

        //添加权限和设施类型的数据
        addPermissionAndDeviceType(role, roleId);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.ADD_ROLE_SUCCESS));
    }

    /**
     * 添加权限和设施类型
     */
    public void addPermissionAndDeviceType(Role role, String roleId) {
        //添加角色和权限的关系
        List<String> permissionIds = role.getPermissionIds();
        List<RolePermission> rolePermissionList = new ArrayList<>();
        Integer addNumber = 0;
        StringBuffer permissionNameBuffer = new StringBuffer();
        if (CheckEmptyUtils.collectEmpty(permissionIds)) {
            permissionIds.forEach(permissionId -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(UUIDUtil.getInstance().UUID32());
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreateTime(new Date());
                rolePermissionList.add(rolePermission);
            });
            addNumber = rolePermissionDao.batchAddRolePermission(rolePermissionList);

            //获取权限名称
            List<Permission> permissions = permissionDao.queryPermissionByIds(permissionIds);
            permissions.forEach(permission -> {
                if (permission.getId().length() <= UserConstant.PERMISSION_NAME_LENGTH) {
                    permissionNameBuffer.append(permission.getName());
                    permissionNameBuffer.append(UserConstant.PERMISSION_NAME_SPLIT);
                }
            });
        }
        String permissionNames = "";
        if (permissionNameBuffer != null) {
            String permissionName = permissionNameBuffer.toString();
            if (StringUtils.isNotEmpty(permissionName)) {
                permissionNames = permissionName.substring(UserConstant.PERMISSION_NAME_START,
                        permissionNameBuffer.toString().length() - UserConstant.PERMISSION_NAME_SPLIT.length());
            }
        }
        addUniversalRoleLog(role, permissionNames, FunctionCodeConstant.ADD_ROLE_MODEL);

        if (addNumber != rolePermissionList.size()) {
            throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
        }

        //添加角色和设备类型的关系
        List<String> deviceTypeIds = role.getDeviceTypeIds();
        List<RoleDeviceType> roleDeviceTypes = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(deviceTypeIds)) {
            deviceTypeIds.forEach(deviceId -> {
                RoleDeviceType roleDevicetype = new RoleDeviceType();
                roleDevicetype.setId(UUIDUtil.getInstance().UUID32());
                roleDevicetype.setRoleId(roleId);
                roleDevicetype.setDeviceTypeId(deviceId);
                roleDevicetype.setCreateTime(System.currentTimeMillis());
                RoleUtils.dealDeviceType(roleDevicetype, deviceId);
                roleDeviceTypes.add(roleDevicetype);
            });

            Integer addDeviceTypeNum = roleDeviceTypeDao.batchAddRoleDeviceType(roleDeviceTypes);
            if (addDeviceTypeNum != roleDeviceTypes.size()) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
            }
        }
    }


    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 修改结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateRole(Role role) {
        String updateUserId = RequestInfoUtils.getUserId();

        role.setUpdateUser(updateUserId);
        role.setUpdateTime(new Date());

        //先检验更新的角色是否存在
        Role updateRole = roleDao.selectById(role.getId());
        if (updateRole == null) {
            return ResultUtils.warn(UserResultCode.ROLE_IS_NOT_EXIST, I18nUtils.getSystemString(UserI18n.ROLE_IS_NOT_EXIST));
        }

        //admin所属的角色不能被修改
//         User user = userDao.queryUserById(UserConstant.ADMIN_ID);
//         if(role.getId().equals(user.getRoleId())){
//             return ResultUtils.warn(UserResultCode.ADMIN_ROLE_IS_NOT_UPDATE,I18nUtils.getSystemString(UserI18n.ADMIN_ROLE_IS_NOT_UPDATE));
//         }
        //去除前后空格
        role.setRoleName(NameUtils.removeBlank(role.getRoleName()));
        role.setRemark(NameUtils.removeBlank(role.getRemark()));

        Integer result = roleDao.updateById(role);
        if (result != 1) {
            throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.UPDATE_ROLE_FAIL));
        }

        updatePermissionAndDeviceType(role);
        //发送异步信息，更新用户信息列表
        SendKafkaMsg.sendMessage(updateUserStream, UserConstant.UPDATE_USER_INFO);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.UPDATE_ROLE_SUCCESS));
    }

    /**
     * 更新角色的权限和设施类型
     */
    private void updatePermissionAndDeviceType(Role role) {
        StringBuffer permissionNames = new StringBuffer();
        Role permissionRole = roleDao.queryRoleInfoById(role.getId());
        List<Permission> permissionList = permissionRole.getPermissionList();
        if (CheckEmptyUtils.collectEmpty(permissionList)) {
//            permissionList.forEach(permission -> {
//                if (permission.getId().length() <= UserConstant.PERMISSION_NAME_LENGTH) {
//                    permissionNameBuffer.append(permission.getName());
//                    permissionNameBuffer.append(UserConstant.PERMISSION_NAME_SPLIT);
//                }
//            });

            //更新操作权限后添加到安全日志
            if (!(permissionList.size() == role.getPermissionIds().size() && permissionList.stream().
                    allMatch(u -> role.getPermissionIds().contains(u.getId())))) {
                permissionNames.append(UserConstant.USER_UPDATE_OPERATION_AUTHORITY).append(" ");
            }
        }

//        String permissionNames = "";
//        if (permissionNameBuffer != null) {
//            String permissionName = permissionNameBuffer.toString();
//            if (StringUtils.isNotEmpty(permissionName)) {
//                permissionNames = permissionName.substring(UserConstant.PERMISSION_NAME_START,
//                        permissionNameBuffer.toString().length() - UserConstant.PERMISSION_NAME_SPLIT.length());
//            }
//        }


        //删除旧的角色权限信息
        rolePermissionDao.batchDeleteByRoleId(role.getId());
        //添加角色和权限的关系
        List<String> permissionIds = role.getPermissionIds();
        List<RolePermission> rolePermissionList = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(permissionIds)) {
            permissionIds.forEach(permissionId -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(UUIDUtil.getInstance().UUID32());
                rolePermission.setRoleId(role.getId());
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreateTime(new Date());
                rolePermissionList.add(rolePermission);
            });

            //添加新的权限信息
            Integer addNumber = rolePermissionDao.batchAddRolePermission(rolePermissionList);
            if (addNumber != rolePermissionList.size()) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
            }
        }
        //删除旧的角色设备类型关系
        roleDeviceTypeDao.batchDeleteByRoleId(role.getId());
        //添加角色和设备类型的关系
        List<String> deviceTypeIds = role.getDeviceTypeIds();
        List<RoleDeviceType> roleDevicetypes = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(deviceTypeIds)) {
            deviceTypeIds.forEach(deviceId -> {
                RoleDeviceType roleDevicetype = new RoleDeviceType();
                roleDevicetype.setId(UUIDUtil.getInstance().UUID32());
                roleDevicetype.setRoleId(role.getId());
                roleDevicetype.setDeviceTypeId(deviceId);
                RoleUtils.dealDeviceType(roleDevicetype, deviceId);
                roleDevicetype.setCreateTime(System.currentTimeMillis());
                roleDevicetypes.add(roleDevicetype);

            });

            Integer addDeviceTypeNum = roleDeviceTypeDao.batchAddRoleDeviceType(roleDevicetypes);
            if (addDeviceTypeNum != roleDevicetypes.size()) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
            }
            //更新后添加到安全设施集日志
            List<RoleDeviceType> roleDeviceTypeList = permissionRole.getRoleDevicetypeList();
            if (!(roleDeviceTypeList.size() == deviceTypeIds.size() && roleDeviceTypeList.stream().
                    allMatch(u -> deviceTypeIds.contains(u.getDeviceTypeId())))) {
                permissionNames.append(UserConstant.USER_UPDATE_FACILITY_SET);
            }
        }
        //添加更新角色和权限的日志信息
        addUniversalRoleLog(role, permissionNames.toString(), FunctionCodeConstant.UPDATE_ROLE_MODEL);
    }


    /**
     * 删除多个角色
     *
     * @param parameters 角色id参数
     * @return 删除结果
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteRole(Parameters parameters) {
        if (parameters == null || parameters.getFirstArrayParameter() == null) {
            return ResultUtils.warn(UserResultCode.ROLE_IS_NOT_EXIST,
                    I18nUtils.getSystemString(UserI18n.ROLE_IS_NOT_EXIST));
        }

        //判断当前的角色有没有用户正在使用
        List<User> users = userDao.queryUserByRoles(parameters.getFirstArrayParameter());
        if (users != null && users.size() > 0) {
            return ResultUtils.warn(UserResultCode.USER_USE_ROLE, I18nUtils.getSystemString(UserI18n.USER_USE_ROLE));
        }

        //判断是否为默认的角色
        List<Role> deleteRoles = roleDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParameter()));
        Role defaultRole = deleteRoles.stream().filter(role -> role.getDefaultRole() == 1).findFirst().orElse(null);
        //如果不为默认的角色，则可以删除
        if (defaultRole == null) {
            addLogByRoles(deleteRoles);

            int result = roleDao.deleteRoles(parameters.getFirstArrayParameter());
            if (result != parameters.getFirstArrayParameter().length) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.DELETE_ROLE_FAIL));
            }
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_ROLE_SUCCESS));
        }
        return ResultUtils.warn(UserResultCode.NOT_ALLOW_DELETE_ADMIN, I18nUtils.getSystemString(UserI18n.NOT_ALLOW_DELETE_DEFAULT_ROLE));
    }

    /**
     * 条件查询角色信息，适合单表查询
     *
     * @param userQueryCondition 查询条件
     * @return 角色信息列表
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

        Page page = myBatiesBuildPage(userQueryCondition);
        EntityWrapper entityWrapper = MpQueryHelper.myBatiesBuildQuery(userQueryCondition);

        List<Role> roleList = roleDao.selectPage(page, entityWrapper);

        return roleList;
    }

    /**
     * 查询所有的角色信息
     *
     * @return 角色信息列表
     */
    @Override
    public Result queryAllRoles() {
        List<Role> roles = roleDao.queryAllRoles();
        return ResultUtils.success(ResultCode.SUCCESS, null, roles);
    }

    /**
     * 条件分页排序查询角色信息
     *
     * @param queryCondition 角色参数
     * @return 角色信息列表
     */
    @Override
    public Result queryRoleByFieldAndCondition(QueryCondition<RoleParameter> queryCondition) {
        //获取参数信息和分页信息
        RoleParameter roleParamter = queryCondition.getBizCondition();
        if (roleParamter == null) {
            roleParamter = new RoleParameter();
        }
        Page page = myBatiesBuildPage(queryCondition);

        roleParamter.setStartNum((queryCondition.getPageCondition().getPageNum() - 1)
                * queryCondition.getPageCondition().getPageSize());
        roleParamter.setPage(queryCondition.getPageCondition().getPageNum());
        roleParamter.setPageSize(queryCondition.getPageCondition().getPageSize());

        List<Role> roleList = roleDao.queryRoleByField(roleParamter);
        Long roleNumber = roleDao.queryRoleNumber(roleParamter);

        PageBean pageBean = myBatiesBuildPageBean(page, roleNumber.intValue(), roleList);
        return ResultUtils.success(pageBean);
    }


    /**
     * 添加角色日志方法
     *
     * @param roleList 角色信息列表
     */
    private void addLogByRoles(List<Role> roleList) {
        systemLanguageUtil.querySystemLanguage();
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //业务数据
        //遍历业务数据
        if (roleList.size() == 0) {
            return;
        }
        for (Role role : roleList) {
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

    /**
     * 添加角色日志方法
     *
     * @param permissionName 权限名
     * @param logModel       日志模板
     */
    private void addUniversalRoleLog(Role role, String permissionName, String logModel) {
        systemLanguageUtil.querySystemLanguage();
        //获取日志类型
        String logType = LogConstants.LOG_TYPE_SECURITY;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        //获得操作对象名称
        addLogBean.setFunctionCode(logModel);
        addLogBean.setDataName("roleName");
        addLogBean.setOptObjId(role.getId());
        if (StringUtils.isNotEmpty(permissionName)) {
            String roleAndPermissionName = role.getRoleName() + permissionName;
//                    + I18nUtils.getSystemString(UserI18n.PERMISSION_NAME_IS) + permissionName;
            //设置操作数据信息
            addLogBean.setOptObj(roleAndPermissionName);
        } else {
            addLogBean.setOptObj(role.getRoleName());
        }
        //操作为新增
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);

        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
