package com.fiberhome.filink.userserver.service.impl;

import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userserver.bean.Permission;
import com.fiberhome.filink.userserver.dao.PermissionDao;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class PermissionServiceImplTest {

    @Tested
    private PermissionServiceImpl permissionService;

    @Injectable
    private PermissionDao permissionDao;

    @Mocked
    private RedisUtils redisUtils;

    @Mocked
    private RequestInfoUtils requestInfoUtils;

    @Mocked
    private I18nUtils i18nUtils;


    @Test
    public void queryTopPermission() throws Exception {

        new Expectations() {
            {
                List<Permission> permissions = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("123");
                permissions.add(permission);
                permissionDao.queryTopPermission();
                result = permissions;
            }
        };
        permissionService.queryTopPermission();
    }

    @Test
    public void queryPermissionByParentId() throws Exception {

        new Expectations() {
            {
                List<Permission> permissions = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("4256");
                permissions.add(permission);
                permissionDao.queryPermissionByParentId(anyString);
                result = permissions;
            }
        };
        permissionService.queryPermissionByParentId("123");
    }

    @Test
    public void queryPermissionByUserIds() throws Exception {

        List<String> userIdList = new ArrayList<>();
        userIdList.add("123");

        new Expectations() {
            {
                List<Permission> permissions = new ArrayList<>();
                Permission permission = new Permission();
                permission.setId("456");
                permissions.add(permission);
                permissionDao.queryPermissionByUserIds(userIdList);
                result = permissions;
            }
        };
        permissionService.queryPermissionByUserIds(userIdList);
    }

}