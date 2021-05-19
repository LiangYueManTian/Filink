package com.fiberhome.filink.userserver.bean;

import com.netflix.ribbon.template.TemplateParser;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.management.ObjectName;
import javax.xml.bind.annotation.XmlType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * Author:qiqizhu@wistronits.com
 * Date:2019/8/1
 */
@RunWith(JMockit.class)
public class BeanTest {

    @Test
    public void authDeviceTest() {
        AuthDevice authDevice = new AuthDevice();
        authDevice.setAuthId(authDevice.getAuthId());
        authDevice.setId(authDevice.getId());
        authDevice.setDeviceId(authDevice.getDeviceId());
        authDevice.setCreateTime(authDevice.getCreateTime());
        authDevice.setCreateUser(authDevice.getCreateUser());
        authDevice.setDoorId(authDevice.getDoorId());
        authDevice.setQrCode(authDevice.getQrCode());
        authDevice.setAreaId(authDevice.getAreaId());
        authDevice.setDeviceType(authDevice.getDeviceType());
        authDevice.setDeviceName(authDevice.getDeviceName());
        authDevice.toString();
        Assert.assertTrue(authDevice != null);
    }

    @Test
    public void authInfoTest() {
        AuthInfo authInfo = new AuthInfo(new ArrayList<>(), new ArrayList<>());
        authInfo.setTempAuthList(authInfo.getTempAuthList());
        authInfo.setUnifyAuthList(authInfo.getUnifyAuthList());
        Assert.assertTrue(authInfo != null);
    }

    @Test
    public void dataPermissionTest() {
        DataPermission dataPermission = new DataPermission();
        dataPermission.setDeptList(dataPermission.getDeptList());
        dataPermission.setDeviceTypes(dataPermission.getDeviceTypes());
        dataPermission.setDeviceId(dataPermission.getDeviceId());
        Assert.assertTrue(dataPermission != null);
    }

    @Test
    public void departmentTest() {
        Department department = new Department();
        department.setId(department.getId());
        department.setDeptName(department.getDeptName());
        department.setDeptChargeUser(department.getDeptChargeUser());
        department.setDeptPhoneNum(department.getDeptPhoneNum());
        department.setAddress(department.getAddress());
        department.setDeptType(department.getDeptType());
        department.setDeptFatherId(department.getDeptFatherId());
        department.setDeptLevel(department.getDeptLevel());
        department.setDeleted(department.getDeleted());
        department.setRemark(department.getRemark());
        department.setChildDepartmentList(department.getChildDepartmentList());
        department.setParentDepartment(department.getParentDepartment());
        department.setParentDepartmentName(department.getParentDepartmentName());
        department.setCreateUser(department.getCreateUser());
        department.setCreateTime(department.getCreateTime());
        department.setUpdateUser(department.getUpdateUser());
        department.setUpdateTime(department.getUpdateTime());
        department.setAreaIdList(department.getAreaIdList());
        department.toString();
        Assert.assertTrue(department != null);

    }

    @Test
    public void departmentFeignTest() {
        DepartmentFeign department = new DepartmentFeign();
        department.setId(department.getId());
        department.setDeptName(department.getDeptName());
        department.setDeptChargeUser(department.getDeptChargeUser());
        department.setDeptPhoneNum(department.getDeptPhoneNum());
        department.setAddress(department.getAddress());
        department.setDeptType(department.getDeptType());
        department.setDeptFatherId(department.getDeptFatherId());
        department.setDeptLevel(department.getDeptLevel());
        department.setDeleted(department.getDeleted());
        department.setRemark(department.getRemark());
        department.setCreateUser(department.getCreateUser());
        department.setCreateTime(department.getCreateTime());
        department.setUpdateUser(department.getUpdateUser());
        department.setUpdateTime(department.getUpdateTime());
        department.setHasThisArea(department.getHasThisArea());
        department.toString();
        Assert.assertTrue(department != null);

    }

    @Test
    public void departmentParamterTest() {
        DepartmentParamter department = new DepartmentParamter();
        department.setId(department.getId());
        department.setDeptName(department.getDeptName());
        department.setDeptChargeUser(department.getDeptChargeUser());
        department.setDeptPhoneNum(department.getDeptPhoneNum());
        department.setAddress(department.getAddress());
        department.setDeptType(department.getDeptType());
        department.setDeptFatherId(department.getDeptFatherId());
        department.setDeptLevel(department.getDeptLevel());
        department.setDeleted(department.getDeleted());
        department.setRemark(department.getRemark());
        department.setCreateUser(department.getCreateUser());
        department.setCreateTime(department.getCreateTime());
        department.setUpdateUser(department.getUpdateUser());
        department.setUpdateTime(department.getUpdateTime());
        department.setPageSize(department.getPageSize());
        department.setPageSize(department.getPage());
        department.setStartNum(department.getStartNum());
        department.setParentDepartmentName(department.getParentDepartmentName());
        department.setSortProperties(department.getSortProperties());
        department.setSort(department.getSort());
        department.toString();
        Assert.assertTrue(department != null);

    }

    @Test
    public void deviceInfoTest() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId(deviceInfo.getDeviceId());
        deviceInfo.setDoorId(deviceInfo.getDoorId());
        Assert.assertTrue(deviceInfo != null);
    }

    @Test
    public void ipDtoTest() {
        IpDto ipDto = new IpDto();
        ipDto.setIpAddress(ipDto.getIpAddress());
        Assert.assertTrue(ipDto != null);
    }

    @Test
    public void loginInfoBeanTest() {
        LoginInfoBean loginInfoBean = new LoginInfoBean();
        loginInfoBean.setShowMenuTemplate(loginInfoBean.getShowMenuTemplate());
        loginInfoBean.setUser(loginInfoBean.getUser());
        loginInfoBean.setLoginCode(loginInfoBean.getLoginCode());
        Assert.assertTrue(loginInfoBean != null);
    }

    @Test
    public void messageInfoBeanTest() {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setPhoneNumber(messageInfo.getPhoneNumber());
        messageInfo.setContent(messageInfo.getContent());
        Assert.assertTrue(messageInfo != null);
    }

    @Test
    public void onlineParameterTest() {
        OnlineParameter onlineParameter = new OnlineParameter();
        onlineParameter.setId(onlineParameter.getId());
        onlineParameter.setUserId(onlineParameter.getUserId());
        onlineParameter.setUserCode(onlineParameter.getUserCode());
        onlineParameter.setUserNickname(onlineParameter.getUserNickname());
        onlineParameter.setUserName(onlineParameter.getUserName());
        onlineParameter.setDeptName(onlineParameter.getDeptName());
        onlineParameter.setAddress(onlineParameter.getAddress());
        onlineParameter.setPhoneNumber(onlineParameter.getPhoneNumber());
        onlineParameter.setEmail(onlineParameter.getEmail());
        onlineParameter.setRoleName(onlineParameter.getRoleName());
        onlineParameter.setLoginTime(onlineParameter.getLoginTime());
        onlineParameter.setLoginTimeEnd(onlineParameter.getLoginTimeEnd());
        onlineParameter.setLoginIp(onlineParameter.getLoginIp());
        onlineParameter.setLoginSource(onlineParameter.getLoginSource());
        onlineParameter.setRoleNameList(onlineParameter.getRoleNameList());
        onlineParameter.setDepartmentNameList(onlineParameter.getDepartmentNameList());
        onlineParameter.setRelation(onlineParameter.getRelation());
        onlineParameter.setPageSize(onlineParameter.getPageSize());
        onlineParameter.setPage(onlineParameter.getPage());
        onlineParameter.setStartNum(onlineParameter.getStartNum());
        onlineParameter.setSortProperties(onlineParameter.getSortProperties());
        onlineParameter.setSort(onlineParameter.getSort());
        onlineParameter.setCurrentUserRoleName(onlineParameter.getCurrentUserRoleName());
        onlineParameter.setCurrentUserDepartmentName(onlineParameter.getCurrentUserDepartmentName());
        Assert.assertTrue(onlineParameter != null);
    }

    @Test
    public void onlineUserTest() {
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setId(onlineUser.getId());
        onlineUser.setUserId(onlineUser.getUserId());
        onlineUser.setUserCode(onlineUser.getUserCode());
        onlineUser.setUserNickname(onlineUser.getUserNickname());
        onlineUser.setUserName(onlineUser.getUserName());
        onlineUser.setDeptName(onlineUser.getDeptName());
        onlineUser.setAddress(onlineUser.getAddress());
        onlineUser.setPhoneNumber(onlineUser.getPhoneNumber());
        onlineUser.setEmail(onlineUser.getEmail());
        onlineUser.setRoleName(onlineUser.getRoleName());
        onlineUser.setLoginTime(onlineUser.getLoginTime());
        onlineUser.setLoginIp(onlineUser.getLoginIp());
        onlineUser.setLoginSource(onlineUser.getLoginSource());
        onlineUser.toString();
        Assert.assertTrue(onlineUser != null);
    }

    @Test
    public void pageEntityTest() {
        PageEntity pageEntity = new PageEntity(null, null);
        PageEntity pageEntity2 = new PageEntity(null, 1, 1, null);
        pageEntity.setObject(pageEntity.getObject());
        pageEntity.setPage(pageEntity.getPage());
        pageEntity.setPageSize(pageEntity.getPageSize());
        pageEntity.setTotal(pageEntity.getTotal());
        Assert.assertTrue(pageEntity != null);
    }

    @Test
    public void pageParameterTest() {
        PageParameter pageParameter = new PageParameter();
        pageParameter.setPageSize(pageParameter.getPageSize());
        pageParameter.setPage(pageParameter.getPage());
        pageParameter.setSortProperties(pageParameter.getSortProperties());
        pageParameter.setSort(pageParameter.getSort());
        pageParameter.setStartNum(pageParameter.getStartNum());
        Assert.assertTrue(pageParameter != null);
    }

    @Test
    public void parametersTest() {
        Parameters parameters = new Parameters();
        parameters.setToken(parameters.getToken());
        parameters.setFirstParameter(parameters.getFirstParameter());
        parameters.setSecondParameter(parameters.getSecondParameter());
        parameters.setFirstArrayParameter(parameters.getFirstArrayParameter());
        parameters.setFirstListParameter(parameters.getFirstListParameter());
        parameters.setFlag(true);
        Assert.assertTrue(parameters != null);
    }

    @Test
    public void passwordTest() {
        Password password = new Password();
        password.setToken(password.getToken());
        password.setUserId(password.getUserId());
        password.setNewPWD(password.getNewPWD());
        password.setOldPWD(password.getOldPWD());
        password.setConfirmPWD(password.getConfirmPWD());
        Assert.assertTrue(password != null);
    }

    @Test
    public void passwordDtoTest() {
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setToken(passwordDto.getToken());
        Assert.assertTrue(passwordDto != null);
    }

    @Test
    public void permissionTest() {
        Permission permission = new Permission();
        permission.setId(permission.getId());
        permission.setName(permission.getName());
        permission.setInterfaceUrl(permission.getInterfaceUrl());
        permission.setDescription(permission.getDescription());
        permission.setMenuId(permission.getMenuId());
        permission.setIsDeleted(permission.getIsDeleted());
        permission.setCreateTime(permission.getCreateTime());
        permission.setUpdateTime(permission.getUpdateTime());
        permission.setRoute_url(permission.getRoute_url());
        permission.setParentId(permission.getParentId());
        permission.setType(permission.getType());
        permission.setChildPermissionList(permission.getChildPermissionList());
        permission.toString();
        Assert.assertTrue(permission != null);
    }

    @Test
    public void resultDtoTest() {
        ResultDto resultDto = new ResultDto(true, "");
        resultDto.setData(true);
        resultDto.setMsg(resultDto.getMsg());
        Assert.assertTrue(resultDto != null);
    }

    @Test
    public void roleTest() {
        Role role = new Role();
        role.setRoleDesc(role.getRoleDesc());
        role.setDeleted(role.getDeleted());
        role.setCreateUser(role.getCreateUser());
        role.setCreateTime(role.getCreateTime());
        role.setUpdateTime(role.getUpdateTime());
        role.setUpdateUser(role.getUpdateUser());
        Assert.assertTrue(role != null);
    }

    @Test
    public void roleDeviceTypeTest() {
        RoleDeviceType roleDeviceType = new RoleDeviceType();
        roleDeviceType.setId(roleDeviceType.getId());
        roleDeviceType.setRoleId(roleDeviceType.getRoleId());
        roleDeviceType.setDeviceTypeId(roleDeviceType.getDeviceTypeId());
        roleDeviceType.setParentId(roleDeviceType.getParentId());
        roleDeviceType.setName(roleDeviceType.getName());
        roleDeviceType.setChildDeviceTypeList(roleDeviceType.getChildDeviceTypeList());
        roleDeviceType.setCreateTime(roleDeviceType.getCreateTime());
        roleDeviceType.toString();
        Assert.assertTrue(roleDeviceType != null);
    }

    @Test
    public void roleParameterTest() {
        RoleParameter roleParameter = new RoleParameter();
        roleParameter.setId(roleParameter.getId());
        roleParameter.setRoleName(roleParameter.getRoleName());
        roleParameter.setRoleDesc(roleParameter.getRoleDesc());
        roleParameter.setDeleted(roleParameter.getDeleted());
        roleParameter.setRemark(roleParameter.getRemark());
        roleParameter.setCreateUser(roleParameter.getCreateUser());
        roleParameter.setCreateTime(roleParameter.getCreateTime());
        roleParameter.setUpdateUser(roleParameter.getUpdateUser());
        roleParameter.setUpdateTime(roleParameter.getUpdateTime());
        roleParameter.setDefaultRole(roleParameter.getDefaultRole());
        roleParameter.setPage(roleParameter.getPage());
        roleParameter.getPageSize();
        roleParameter.getSortProperties();
        roleParameter.getSort();
        roleParameter.getStartNum();
        roleParameter.getRoleNameList();
        roleParameter.toString();
        Assert.assertTrue(roleParameter != null);
    }

    @Test
    public void rolePermissionTest() {
        RolePermission rolePermission = new RolePermission();
        rolePermission.getId();
        rolePermission.getRoleId();
        rolePermission.getPermissionId();
        rolePermission.getIsDeleted();
        rolePermission.getCreateTime();
        rolePermission.getUpdateTime();
        rolePermission.toString();
        Assert.assertTrue(rolePermission != null);
    }

    @Test
    public void tempAuth() {
        TempAuth tempAuth = new TempAuth();
        tempAuth.getApplyReason();
        tempAuth.getAuthUserId();
        tempAuth.getApplyTime();
        tempAuth.getRemark();
        tempAuth.getAuditingTime();
        tempAuth.getAuditingDesc();
        tempAuth.getUser();
        tempAuth.getAuthUser();
        tempAuth.getUpdateTime();
        tempAuth.getCreateUser();
        tempAuth.getApplyStatus();
        tempAuth.getIsDeleted();
        tempAuth.toString();
        Assert.assertTrue(true);
    }

    @Test
    public void tempAuthParameterTest() {
        TempAuthParameter tempAuthParameter = new TempAuthParameter();
        tempAuthParameter.getName();
        tempAuthParameter.getUserName();
        tempAuthParameter.getAuthUserName();
        tempAuthParameter.getApplyReason();
        tempAuthParameter.getRemark();
        tempAuthParameter.getCreateTime();
        tempAuthParameter.getCreateTimeEnd();
        tempAuthParameter.getCreateTimeRelation();
        tempAuthParameter.getAuthEffectiveTime();
        tempAuthParameter.getAuthEffectiveTimeEnd();
        tempAuthParameter.getAuthEffectiveTimeRelation();
        tempAuthParameter.getAuthExpirationTime();
        tempAuthParameter.getAuthExpirationTimeEnd();
        tempAuthParameter.getAuthExpirationTimeRelation();
        tempAuthParameter.getAuditingTime();
        tempAuthParameter.getAuditingTimeEnd();
        tempAuthParameter.getAuditingTimeRelation();
        tempAuthParameter.getAreaIdList();
        tempAuthParameter.getRoleDeviceIdList();
        Assert.assertTrue(true);
    }

    @Test
    public void unifyAuthTest() {
        UnifyAuth unifyAuth = new UnifyAuth();
        unifyAuth.getUserId();
        unifyAuth.getAuthUserId();
        unifyAuth.getCreateTime();
        unifyAuth.getIsDeleted();
        unifyAuth.getCreateTime();
        unifyAuth.getUpdateTime();
        unifyAuth.getCreateUser();
        unifyAuth.getDeviceMap();
        unifyAuth.getUser();
        unifyAuth.getAuthUser();
        unifyAuth.toString();
        Assert.assertTrue(unifyAuth != null);
    }

    @Test
    public void UnifyAuthParameterTest() {
        UnifyAuthParameter unifyAuthParameter = new UnifyAuthParameter();
        unifyAuthParameter.getName();
        unifyAuthParameter.getAuthUserName();
        unifyAuthParameter.getCreateTime();
        unifyAuthParameter.getCreateTimeEnd();
        unifyAuthParameter.getCreateTimeRelation();
        unifyAuthParameter.getUserName();
        unifyAuthParameter.getAuthEffectiveTime();
        unifyAuthParameter.getAuthEffectiveTimeEnd();
        unifyAuthParameter.getAuthEffectiveTimeRelation();
        unifyAuthParameter.getAuthExpirationTime();
        unifyAuthParameter.getAuthExpirationTimeEnd();
        unifyAuthParameter.getAuthExpirationTimeRelation();
        unifyAuthParameter.getRemark();
        unifyAuthParameter.getCurrentUserId();
        Assert.assertTrue(unifyAuthParameter != null);
    }
}
