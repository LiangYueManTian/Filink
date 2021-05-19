package com.fiberhome.filink.userserver.service;

import com.aliyuncs.exceptions.ClientException;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.userserver.bean.DataPermission;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.bean.UserCount;
import com.fiberhome.filink.userserver.bean.UserParameter;

import java.util.List;
import java.util.Map;

/**
 * 部门和用户之间的服务类
 * @author xgong
 */
public interface UserExtraService {

    /**
     * 根据部门id集合获取部门人员信息
     * @param deptIdList    部门id集合
     * @return  人员信息
     */
    List<User> queryUserByDeptList(List<String> deptIdList);

    /**
     * 校验用户是否能够登陆
     * @param userParameter  登录参数
     * @return  code码
     */
    Integer validateUserLogin(UserParameter userParameter);

    /**
     * 导出用户列表信息
     * @param exportDto    导出用户列表的参数
     * @return  导出的结果信息
     */
    Result exportUserList(ExportDto exportDto);

    /**
     * 处理用户登录失败的逻辑
     * @param userParameter  用户信息
     * @return  code码
     */
    void dealLoginFail(UserParameter userParameter);


    /**
     *  根据权限信息查询在线用户
     * @param dataPermission    数据权限信息
     * @return  在线用户唯一标识
     */
    List<String> queryDeviceTypeByPermission(DataPermission dataPermission);

    /**
     * 发送短信信息
     * @param phoneNumber   手机号
     */
    Result sendMessage(String phoneNumber) throws ClientException;

    /**
     * 根据设施获取用户信息
     * @param dataPermissionList 设施信息列表
     * @return
     */
    Map<String,List<String>> queryUserByDevice(List<DataPermission> dataPermissionList);

    /**
     * 查询手机登录验证码
     * @param phoneNumber   手机号
     * @return  验证码
     */
    String getSmsMessage(String phoneNumber);

    /**
     * 根据手机号获取用户信息
     * @param phoneNumber
     * @return
     */
    User queryUserByPhone(String phoneNumber);

    /**
     * 根据用户id获取所有的token
     * @param userId    用户id
     * @return  token列表
     */
    List<String> queryTokenByUserId(String userId);

    /**
     * 根据用户id列表获取设备信息
     * @param idList    用户id列表
     * @return  设备id列表
     */
    List<String> queryPhoneIdByUserIds(List<String> idList);

    /**
     * 根据用户名获取用户id列表
     * @param userName  用户名
     * @return  用户id列表
     */
    List<String> queryUserIdByName(String userName);

    /**
     * 获取账号数和在线用户数
     * @return
     */
    UserCount queryUserNumber();

    /**
     * 根据用户id列表获取在线用户信息列表
     * @param idList    用户id列表
     * @return  用户列表
     */
    List<User> queryOnlineUserByIdList(List<String> idList);

    /**
     * 修改在线用户的手机设备id和appkey
     * @param user  修改的用户信息
     * @return  修改的结果
     */
    Result modifyUserPhoneIdAndAppKey(User user);

    /**
     * 获取所有的用户部门信息
     * @return  用户信息
     */
    Result queryAllUserInfo();

    /**
     * 根据权限信息查询用户信息
     * @param userParameter 查询参数
     * @return  用户列表信息
     */
    Result queryUserByPermission(QueryCondition<UserParameter> userParameter);

    /**
     * 根据部门和设施类型获取人员信息
     * @param dataPermission    部门和设施类型信息
     * @return  人员列表
     */
    Result queryUserInfoByDeptAndDeviceType(DataPermission dataPermission);
}
