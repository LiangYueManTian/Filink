package com.fiberhome.filink.userserver.service;

import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.AuthInfo;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.UnifyAuth;
import com.fiberhome.filink.userserver.bean.UnifyAuthParameter;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;

/**
 * <p>
 * 统一授权 服务类
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
public interface UnifyAuthService extends IService<UnifyAuth> {

    /**
     * 添加统一授权服务
     * @param unifyauth 统一授权实体信息
     * @return  授权结果
     */
    Result addUnifyAuth(UnifyAuth unifyauth);

    /**
     * 条件查询统一授权信息
     * @param unifyAuthCondition 统一授权条件信息
     * @return 统一授权列表
     */
    Result queryUnifyAuthByCondition(QueryCondition<UnifyAuthParameter> unifyAuthCondition);

    /**
     * 修改统一授权信息
     * @param unifyauth 授权信息实体类
     * @return  修改的结果
     */
    Result modifyUnifyAuth(UnifyAuth unifyauth);

    /**
     * 通过id查询统一授权的详细信息
     * @param id 统一授权的id
     * @return 统一授权信息
     */
    Result queryUnifyAuthById(String id);

    /**
     * 删除单个统一授权信息
     * @param id
     * @return
     */
    Result deleteUnifyAuthById(String id);

    /**
     * 批量删除统一授权信息
     * @param ids 待删除的统一授权id
     * @return  删除的结果
     */
    Result batchDeleteUnifyAuth(String[] ids);

    /**
     * 修改统一授权的状态
     * @param unifyAuthParameter 修改参数类
     * @return  修改的结果
     */
    Result batchModifyUnifyAuthStatus(UnifyAuthParameter unifyAuthParameter);

    /**
     * 获取当前登录人的授权信息
     * @return  授权信息
     */
    Result queryUserAuthInfoById();

    /**
     * 根据用户，设施，门锁信息获取授权信息
     * @return
     * @param userAUthInfo  用户，设施，门锁信息
     */
    AuthInfo queryAuthInfoByUserIdAndDeviceAndDoor(UserAuthInfo userAUthInfo);

    /**
     * 根据设施信息删除授权信息
     * @param deviceInfo    设施信息
     * @return  删除的数量
     */
    Integer deleteAuthByDevice(DeviceInfo deviceInfo);

    /**
     * 根据授权名称获取统一授权信息
     * @param unifyauth 统一授权信息
     * @return  统一授权列表信息
     */
    Result queryAuthByName(UnifyAuth unifyauth);
}
