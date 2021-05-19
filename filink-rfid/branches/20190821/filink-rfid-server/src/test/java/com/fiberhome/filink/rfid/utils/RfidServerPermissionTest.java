package com.fiberhome.filink.rfid.utils;

import com.fiberhome.filink.deviceapi.api.DeviceFeign;
import com.fiberhome.filink.deviceapi.bean.DeviceInfoDto;
import com.fiberhome.filink.userapi.api.UserFeign;
import com.fiberhome.filink.userapi.bean.Role;
import com.fiberhome.filink.userapi.bean.RoleDeviceType;
import com.fiberhome.filink.userapi.bean.User;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liyj
 * @date 2019/7/26
 */
@RunWith(JMockit.class)
public class RfidServerPermissionTest {
    /**
     * permission
     */
    @Tested
    private RfidServerPermission permission;
    /**
     * 用户Feign
     */
    @Injectable
    private UserFeign userFeign;

    /**
     * 设施Feign
     */
    @Injectable
    private DeviceFeign deviceFeign;


    @Test
    public void checkRfidPermission() throws Exception {
        List<String> deviceTypeList = Lists.newArrayList();
        deviceTypeList.add("012");
        Boolean aBoolean = permission.checkRfidPermission("001", deviceTypeList);
        Assert.assertTrue(aBoolean);
    }

    @Test
    public void checkRfidPermission001() throws Exception {
        List<String> deviceTypeList = Lists.newArrayList();
        deviceTypeList.add("001");
        Boolean aBoolean = permission.checkRfidPermission("001", deviceTypeList);
        Assert.assertTrue(aBoolean);
    }

    @Test
    public void checkRfidPermission030() throws Exception {
        List<String> deviceTypeList = Lists.newArrayList();
        deviceTypeList.add("030");
        Boolean aBoolean = permission.checkRfidPermission("030", deviceTypeList);
        Assert.assertTrue(aBoolean);
    }

    @Test
    public void checkRfidPermission060() throws Exception {
        List<String> deviceTypeList = Lists.newArrayList();
        ;
        deviceTypeList.add("060");
        Boolean aBoolean = permission.checkRfidPermission("060", deviceTypeList);
        Assert.assertTrue(aBoolean);
    }

    @Test
    public void checkRfidPermission090() throws Exception {
        List<String> deviceTypeList = Lists.newArrayList();
        deviceTypeList.add("090");
        Boolean aBoolean = permission.checkRfidPermission("090", deviceTypeList);
        Assert.assertTrue(aBoolean);
    }

    @Test
    public void checkRfidPermission150() throws Exception {
        List<String> deviceTypeList = Lists.newArrayList();
        deviceTypeList.add("150");
        Boolean aBoolean = permission.checkRfidPermission("150", deviceTypeList);
        Assert.assertTrue(aBoolean);
    }

    /**
     * 获取拥有智能标签业务权限信息
     *
     * @throws Exception
     */
    @Test
    public void getPermissionsInfoFoRfidServer() throws Exception {
        List<DeviceInfoDto> deviceInfoDtoList = Lists.newArrayList();
        DeviceInfoDto infoDto = new DeviceInfoDto();
        infoDto.setDeviceType("060");
        deviceInfoDtoList.add(infoDto);


        List<User> users = Lists.newArrayList();
        User user = new User();
        Role role = new Role();
        List<RoleDeviceType> roles = Lists.newArrayList();
        RoleDeviceType type = new RoleDeviceType();
        type.setDeviceTypeId("060");
        roles.add(type);
        role.setRoleDevicetypeList(roles);

        user.setRole(role);
        users.add(user);

        new Expectations() {
            {
                deviceFeign.getDeviceByIds((String[]) any);
                result = deviceInfoDtoList;

                userFeign.queryUserByIdList((List<String>) any);
                result = users;
            }
        };
        Set<String> deviceIds = new HashSet<>();
        deviceIds.add("deviceIds");
        permission.getPermissionsInfoFoRfidServer(deviceIds, "2");
    }


}