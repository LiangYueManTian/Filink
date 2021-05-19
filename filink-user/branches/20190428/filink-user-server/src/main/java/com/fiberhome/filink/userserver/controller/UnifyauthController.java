package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.userserver.bean.AuthInfo;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.UnifyAuthParameter;
import com.fiberhome.filink.userserver.bean.Unifyauth;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;
import com.fiberhome.filink.userserver.service.UnifyauthService;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 统一授权 前端控制器
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@RestController
@RequestMapping("/unifyauth")
public class UnifyauthController {

    @Autowired
    private UnifyauthService unifyauthService;

    /**
     * 添加统一授权信息
     *
     * @param unifyauth 统一授权实体信息
     * @return 授权结果
     */
    @PostMapping("/addUnifyAuth")
    public Result addUnifyAuth(@RequestBody Unifyauth unifyauth) {

        return unifyauthService.addUnifyAuth(unifyauth);
    }


    /**
     * 条件查询统一授权信息
     *
     * @param unifyAuthCondition 统一授权的条件类
     * @return 统一授权列表
     */
    @PostMapping("/queryUnifyAuthByCondition")
    public Result queryUnifyAuthByCondition(@RequestBody QueryCondition<UnifyAuthParameter> unifyAuthCondition) {

        return unifyauthService.queryUnifyAuthByCondition(unifyAuthCondition);
    }

    /**
     * 修改统一授权信息
     *
     * @param unifyauth 统一授权实体信息
     * @return 修改授权信息的结果
     */
    @PostMapping("/modifyUnifyAuth")
    public Result modifyUnifyAuth(@RequestBody Unifyauth unifyauth) {

        return unifyauthService.modifyUnifyAuth(unifyauth);
    }

    /**
     * 根据id查询统一授权的详细信息
     *
     * @param id 统一授权id
     * @return 统一授权信息
     */
    @GetMapping("/queryUnifyAuthById/{id}")
    public Result queryUnifyAuthById(@PathVariable("id") String id) {

        return unifyauthService.queryUnifyAuthById(id);
    }

    /**
     * 根据指定id删除统一授权信息
     *
     * @param id 待删除的统一授权id
     * @return 删除结果
     */
    @PostMapping("/deleteUnifyAuthById/{id}")
    public Result deleteUnifyAuthById(@PathVariable("id") String id) {

        return unifyauthService.deleteUnifyAuthById(id);
    }

    /**
     * 批量删除统一授权id
     *
     * @param ids 待删除的统一授权id数组
     * @return 删除的结果
     */
    @PostMapping("/batchDeleteUnifyAuth")
    public Result batchDeleteUnifyAuth(@RequestBody String[] ids) {

        return unifyauthService.batchDeleteUnifyAuth(ids);
    }

    /**
     * 修改统一授权状态
     *
     * @param unifyAuthParameter 统一授权参数类
     * @return 修改的结果
     */
    @PostMapping("/batchModifyUnifyAuthStatus")
    public Result batchModifyUnifyAuthStatus(@RequestBody UnifyAuthParameter unifyAuthParameter) {

        return unifyauthService.batchModifyUnifyAuthStatus(unifyAuthParameter);
    }

    /**
     * 获取授权信息
     *
     * @return
     */
    @GetMapping("/queryUserAuthInfoById")
    public Result queryUserAuthInfoById() {

        return unifyauthService.queryUserAuthInfoById();
    }

    /**
     * 根据用户信息，门锁id和设施类型来查询授权信息,后台使用
     *
     * @return
     */
    @PostMapping("/queryAuthInfoByUserIdAndDeviceAndDoor")
    public boolean queryAuthInfoByUserIdAndDeviceAndDoor(@RequestBody UserAuthInfo userAUthInfo) {

        userAUthInfo.setCurrentTime(System.currentTimeMillis());
        AuthInfo authInfo = unifyauthService.queryAuthInfoByUserIdAndDeviceAndDoor(userAUthInfo);
        return CheckEmptyUtils.collectEmpty
                (authInfo.getTempauthList()) || CheckEmptyUtils.collectEmpty(authInfo.getUnifyauthList());
    }

    /**
     * 根据用户信息，门锁id和设施类型来查询授权信息,前端使用
     *
     * @return
     */
    @PostMapping("/queryAuthByUserAndDeviceAndDoorInfo")
    public Result queryAuthByUserAndDeviceAndDoorInfo(@RequestBody UserAuthInfo userAUthInfo) {

        userAUthInfo.setCurrentTime(System.currentTimeMillis());
        AuthInfo authInfo = unifyauthService.queryAuthInfoByUserIdAndDeviceAndDoor(userAUthInfo);
        return ResultUtils.success(authInfo);
    }

    /**
     * 根据设施信息删除授权信息
     *
     * @return
     */
    @PostMapping("/deleteAuthByDevice")
    public Integer deleteAuthByDevice(@RequestBody DeviceInfo deviceInfo) {

        return unifyauthService.deleteAuthByDevice(deviceInfo);
    }

    /**
     * 根据授权名称获取统一授权信息
     * @param unifyauth 统一授权参数
     * @return  统一授权列表信息
     */
    @PostMapping("/queryAuthByName")
    public Result queryAuthByName(@RequestBody Unifyauth unifyauth){

        return unifyauthService.queryAuthByName(unifyauth);
    }
}
