package com.fiberhome.filink.userserver.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.bean.FiLinkTimeUtils;
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
import com.fiberhome.filink.userserver.consts.UserConst;
import com.fiberhome.filink.userserver.consts.UserI18n;
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
 * ??????Service
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
     * ????????????????????????
     *
     * @param roleQueryCondition ????????????
     * @return ??????????????????
     */
    @Override
    public Result queryRoleList(QueryCondition<Role> roleQueryCondition) {
        Page page = myBatiesBuildPage(roleQueryCondition);

        //???????????????????????????????????????create_time????????????
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
     * ???????????????????????????
     *
     * @param roleId ??????id
     * @return ????????????
     */
    @Override
    public Result queryRoleInfoById(String roleId) {

        Role role = roleDao.queryRoleInfoById(roleId);
        //????????????????????????????????????
        if (role == null) {
            return ResultUtils.warn(UserConst.DATA_IS_NULL);
        }
        //??????????????????????????????
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
     * ????????????
     *
     * @param role ????????????
     * @return ????????????
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addRole(Role role) {
        //??????????????????id???UUID???
        String roleId = UUIDUtil.getInstance().UUID32();
        if (role == null || role.getRoleName() == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.WRITE_FULL_ROLE_INFO));
        }

        //??????????????????????????????????????????
        Role verityRole = roleDao.verityRoleByName(role.getRoleName());
        if (verityRole != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.ROLE_NAME_HAS_USER));
        }

        String createUserId = RequestInfoUtils.getUserId();
        role.setCreateUser(createUserId);
        role.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
        role.setId(roleId);

        role.setRoleName(NameUtils.removeBlank(role.getRoleName()));
        role.setRemark(NameUtils.removeBlank(role.getRemark()));

        int result = roleDao.insert(role);
        if (result != 1) {
            throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
        }

        //????????????????????????????????????
        addPermissionAndDeviceType(role, roleId);

        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.ADD_ROLE_SUCCESS));
    }

    /**
     * ???????????????????????????
     */
    public void addPermissionAndDeviceType(Role role, String roleId) {
        //??????????????????????????????
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
                rolePermission.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                rolePermissionList.add(rolePermission);
            });
            addNumber = rolePermissionDao.batchAddRolePermission(rolePermissionList);

            //??????????????????
            List<Permission> permissions = permissionDao.queryPermissionByIds(permissionIds);
            permissions.forEach(permission -> {
                if (permission.getId().length() <= UserConst.PERMISSION_NAME_LENGTH) {
                    permissionNameBuffer.append(permission.getName());
                    permissionNameBuffer.append(UserConst.PERMISSION_NAME_SPLIT);
                }
            });
        }
        String permissionNames = "";
        if (permissionNameBuffer != null) {
            String permissionName = permissionNameBuffer.toString();
            if (StringUtils.isNotEmpty(permissionName)) {
                permissionNames = permissionName.substring(UserConst.PERMISSION_NAME_START,
                        permissionNameBuffer.toString().length() - UserConst.PERMISSION_NAME_SPLIT.length());
            }
        }
        addUniversalRoleLog(role, permissionNames, UserConst.ADD_ROLE_MODEL);

        if (addNumber != rolePermissionList.size()) {
            throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
        }

        //????????????????????????????????????
        List<String> deviceTypeIds = role.getDeviceTypeIds();
        List<RoleDeviceType> roleDeviceTypes = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(deviceTypeIds)) {
            deviceTypeIds.forEach(deviceId -> {
                RoleDeviceType roleDevicetype = new RoleDeviceType();
                roleDevicetype.setId(UUIDUtil.getInstance().UUID32());
                roleDevicetype.setRoleId(roleId);
                roleDevicetype.setDeviceTypeId(deviceId);
                roleDevicetype.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
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
     * ????????????
     *
     * @param role ????????????
     * @return ????????????
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateRole(Role role) {
        String updateUserId = RequestInfoUtils.getUserId();

        role.setUpdateUser(updateUserId);
        role.setUpdateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());

        //????????????????????????????????????
        Role updateRole = roleDao.selectById(role.getId());
        if (updateRole == null) {
            return ResultUtils.warn(UserConst.ROLE_IS_NOT_EXIST, I18nUtils.getSystemString(UserI18n.ROLE_IS_NOT_EXIST));
        }

        //admin??????????????????????????????
//         User user = userDao.queryUserById(UserConst.ADMIN_ID);
//         if(role.getId().equals(user.getRoleId())){
//             return ResultUtils.warn(UserConst.ADMIN_ROLE_IS_NOT_UPDATE,I18nUtils.getSystemString(UserI18n.ADMIN_ROLE_IS_NOT_UPDATE));
//         }
        //??????????????????
        role.setRoleName(NameUtils.removeBlank(role.getRoleName()));
        role.setRemark(NameUtils.removeBlank(role.getRemark()));

        Integer result = roleDao.updateById(role);
        if (result != 1) {
            throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.UPDATE_ROLE_FAIL));
        }

        updatePermissionAndDeviceType(role);
        //?????????????????????????????????????????????
        SendKafkaMsg.sendMessage(updateUserStream, UserConst.UPDATE_USER_INFO);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.UPDATE_ROLE_SUCCESS));
    }

    /**
     * ????????????????????????????????????
     */
    private void updatePermissionAndDeviceType(Role role) {
        StringBuffer permissionNames = new StringBuffer();
        Role permissionRole = roleDao.queryRoleInfoById(role.getId());
        List<Permission> permissionList = permissionRole.getPermissionList();
        if (CheckEmptyUtils.collectEmpty(permissionList)) {
//            permissionList.forEach(permission -> {
//                if (permission.getId().length() <= UserConst.PERMISSION_NAME_LENGTH) {
//                    permissionNameBuffer.append(permission.getName());
//                    permissionNameBuffer.append(UserConst.PERMISSION_NAME_SPLIT);
//                }
//            });

            //??????????????????????????????????????????
            if (!(permissionList.size() == role.getPermissionIds().size() && permissionList.stream().
                    allMatch(u -> role.getPermissionIds().contains(u.getId())))) {
                permissionNames.append(UserConst.USER_UPDATE_OPERATION_AUTHORITY).append(" ");
            }
        }

//        String permissionNames = "";
//        if (permissionNameBuffer != null) {
//            String permissionName = permissionNameBuffer.toString();
//            if (StringUtils.isNotEmpty(permissionName)) {
//                permissionNames = permissionName.substring(UserConst.PERMISSION_NAME_START,
//                        permissionNameBuffer.toString().length() - UserConst.PERMISSION_NAME_SPLIT.length());
//            }
//        }


        //??????????????????????????????
        rolePermissionDao.batchDeleteByRoleId(role.getId());
        //??????????????????????????????
        List<String> permissionIds = role.getPermissionIds();
        List<RolePermission> rolePermissionList = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(permissionIds)) {
            permissionIds.forEach(permissionId -> {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(UUIDUtil.getInstance().UUID32());
                rolePermission.setRoleId(role.getId());
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                rolePermissionList.add(rolePermission);
            });

            //????????????????????????
            Integer addNumber = rolePermissionDao.batchAddRolePermission(rolePermissionList);
            if (addNumber != rolePermissionList.size()) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
            }
        }
        //????????????????????????????????????
        roleDeviceTypeDao.batchDeleteByRoleId(role.getId());
        //????????????????????????????????????
        List<String> deviceTypeIds = role.getDeviceTypeIds();
        List<RoleDeviceType> roleDevicetypes = new ArrayList<>();
        if (CheckEmptyUtils.collectEmpty(deviceTypeIds)) {
            deviceTypeIds.forEach(deviceId -> {
                RoleDeviceType roleDevicetype = new RoleDeviceType();
                roleDevicetype.setId(UUIDUtil.getInstance().UUID32());
                roleDevicetype.setRoleId(role.getId());
                roleDevicetype.setDeviceTypeId(deviceId);
                RoleUtils.dealDeviceType(roleDevicetype, deviceId);
                roleDevicetype.setCreateTime(FiLinkTimeUtils.getUtcZeroTimeStamp());
                roleDevicetypes.add(roleDevicetype);

            });

            Integer addDeviceTypeNum = roleDeviceTypeDao.batchAddRoleDeviceType(roleDevicetypes);
            if (addDeviceTypeNum != roleDevicetypes.size()) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.ADD_ROLE_FAIL));
            }
            //???????????????????????????????????????
            List<RoleDeviceType> roleDeviceTypeList = permissionRole.getRoleDevicetypeList();
            if (!(roleDeviceTypeList.size() == deviceTypeIds.size() && roleDeviceTypeList.stream().
                    allMatch(u -> deviceTypeIds.contains(u.getDeviceTypeId())))) {
                permissionNames.append(UserConst.USER_UPDATE_FACILITY_SET);
            }
        }
        //??????????????????????????????????????????
        addUniversalRoleLog(role, permissionNames.toString(), UserConst.UPDATE_ROLE_MODEL);
    }


    /**
     * ??????????????????
     *
     * @param parameters ??????id??????
     * @return ????????????
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteRole(Parameters parameters) {
        if (parameters == null || parameters.getFirstArrayParameter() == null) {
            return ResultUtils.warn(UserConst.ROLE_IS_NOT_EXIST,
                    I18nUtils.getSystemString(UserI18n.ROLE_IS_NOT_EXIST));
        }

        //????????????????????????????????????????????????
        List<User> users = userDao.queryUserByRoles(parameters.getFirstArrayParameter());
        if (users != null && users.size() > 0) {
            return ResultUtils.warn(UserConst.USER_USE_ROLE, I18nUtils.getSystemString(UserI18n.USER_USE_ROLE));
        }

        //??????????????????????????????
        List<Role> deleteRoles = roleDao.selectBatchIds(Arrays.asList(parameters.getFirstArrayParameter()));
        Role defaultRole = deleteRoles.stream().filter(role -> role.getDefaultRole() == 1).findFirst().orElse(null);
        //?????????????????????????????????????????????
        if (defaultRole == null) {
            addLogByRoles(deleteRoles);

            int result = roleDao.deleteRoles(parameters.getFirstArrayParameter());
            if (result != parameters.getFirstArrayParameter().length) {
                throw new FilinkRoleException(I18nUtils.getSystemString(UserI18n.DELETE_ROLE_FAIL));
            }
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.DELETE_ROLE_SUCCESS));
        }
        return ResultUtils.warn(UserConst.NOT_ALLOW_DELETE_ADMIN, I18nUtils.getSystemString(UserI18n.NOT_ALLOW_DELETE_DEFAULT_ROLE));
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param userQueryCondition ????????????
     * @return ??????????????????
     */
    @Override
    public List<Role> queryRoleByField(QueryCondition<Role> userQueryCondition) {

        List<FilterCondition> filterConditions = userQueryCondition.getFilterConditions();
        FilterCondition filterCondition = new FilterCondition();

        //???????????????????????????
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
     * ???????????????????????????
     *
     * @return ??????????????????
     */
    @Override
    public Result queryAllRoles() {
        List<Role> roles = roleDao.queryAllRoles();
        return ResultUtils.success(ResultCode.SUCCESS, null, roles);
    }

    /**
     * ????????????????????????????????????
     *
     * @param queryCondition ????????????
     * @return ??????????????????
     */
    @Override
    public Result queryRoleByFieldAndCondition(QueryCondition<RoleParameter> queryCondition) {
        //?????????????????????????????????
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
     * ????????????????????????
     *
     * @param roleList ??????????????????
     */
    private void addLogByRoles(List<Role> roleList) {
        systemLanguageUtil.querySystemLanguage();
        List<AddLogBean> addLogBeanList = new ArrayList<AddLogBean>();
        //????????????
        //??????????????????
        if (roleList.size() == 0) {
            return;
        }
        for (Role role : roleList) {
            //??????????????????
            String logType = LogConstants.LOG_TYPE_SECURITY;
            AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
            addLogBean.setDataId("id");
            addLogBean.setDataName("roleName");
            //????????????????????????
            addLogBean.setFunctionCode("1502103");
            //??????????????????id
            addLogBean.setOptObjId(role.getId());
            addLogBean.setOptObj(role.getRoleName());
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
     * ????????????????????????
     *
     * @param permissionName ?????????
     * @param logModel       ????????????
     */
    private void addUniversalRoleLog(Role role, String permissionName, String logModel) {
        systemLanguageUtil.querySystemLanguage();
        //??????????????????
        String logType = LogConstants.LOG_TYPE_SECURITY;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        //????????????????????????
        addLogBean.setFunctionCode(logModel);
        addLogBean.setDataName("roleName");
        addLogBean.setOptObjId(role.getId());
        if (StringUtils.isNotEmpty(permissionName)) {
            String roleAndPermissionName = role.getRoleName() + permissionName;
//                    + I18nUtils.getSystemString(UserI18n.PERMISSION_NAME_IS) + permissionName;
            //????????????????????????
            addLogBean.setOptObj(roleAndPermissionName);
        } else {
            addLogBean.setOptObj(role.getRoleName());
        }
        //???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_DELETE);

        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
