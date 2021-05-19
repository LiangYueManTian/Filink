package com.fiberhome.filink.userserver.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.service.PermissionService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class PermissionControllerTest {

    @Tested
    private PermissionController permissionController;

    @Injectable
    private PermissionService permissionService;



    @Test
    public void queryTopPermission() throws Exception {

        new Expectations(){
            {
                permissionService.queryTopPermission();
                result = ResultUtils.success();
            }
        };

        Result result = permissionController.queryTopPermission();
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryPermissionByParentId() throws Exception {

        String parentId = "123";
        new Expectations(){
            {
                permissionService.queryPermissionByParentId(parentId);
                result = ResultUtils.success();
            }
        };

        Result result = permissionController.queryPermissionByParentId(parentId);
        Assert.assertTrue(result.getCode() == ResultUtils.success().getCode());
    }

    @Test
    public void queryPermissionByUserIds() throws Exception {

        List<String> userIds = new ArrayList<>();
        new Expectations(){
            {
                permissionService.queryPermissionByUserIds(userIds);
                result = ResultUtils.success();
            }
        };

        permissionController.queryPermissionByUserIds(userIds);
    }

}