package com.fiberhome.filink.userserver.dao;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.userserver.bean.DataPermission;
import com.fiberhome.filink.userserver.bean.PasswordDto;
import com.fiberhome.filink.userserver.bean.User;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.UserParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户表 Mapper 接口
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Repository
public interface UserDao extends BaseMapper<User> {

    /**
     * 查询用户列表信息
     *
     * @param page
     * @param entityWrapper
     * @return 用户列表信息
     */
    List<User> selectUserList(Page page, @Param("entityWrapper") EntityWrapper entityWrapper);

    /**
     * 删除多个用户
     *
     * @param userIdArray 用户id数组
     * @param userType
     * @return 删除结果
     * @throws Exception
     */
    Integer deleteUser(@Param("userIdArray") String[] userIdArray, @Param("userType") String userType);

    /**
     * 根据用户id查询用户详情
     *
     * @param id 用户id
     * @return user User用户详细信息
     */
    User queryUserById(@Param("id") String id);

    /**
     * 根据用户名查询用户详细信息
     *
     * @param userName 用户名称
     * @return 用户信息
     */
    User queryUserByName(@Param("userName") String userName);

    /**
     * 批量更改用户的状态
     *
     * @param userStatus
     * @param userIdArray
     * @return 修改的个数
     */
    Integer updateUserStatus(@Param("userStatus") int userStatus, @Param("userIdArray") String[] userIdArray);

    /**
     * 修改用户密码
     *
     * @param pwd
     * @return
     */
    Integer modifyPWD(@RequestBody PasswordDto pwd);

    /**
     * 根据部门id查询用户信息
     *
     * @param deptIdArray
     * @return
     */
    List<User> queryUserByDepts(@Param("deptIdArray") String[] deptIdArray);

    /**
     * 根据角色id查询用户信息
     *
     * @param roleIdArray
     * @return
     */
    List<User> queryUserByRoles(@Param("roleIdArray") String[] roleIdArray);

    /**
     * 根据条件查询用户信息
     *
     * @param userParameter
     * @return
     */
    List<User> queryUserByField(UserParameter userParameter);

    /**
     * 条件查询用户的数量
     *
     * @param userParameter
     * @return
     */
    Long queryUserNum(UserParameter userParameter);

    /**
     * 根据id查询用户详情
     *
     * @param userId
     * @return
     */
    User queryUserInfoById(@Param("userId") String userId);

    /**
     * 查询所有没被删除的用户
     *
     * @return
     */
    List<User> queryAllUser();

    /**
     * 更新用户的有效期
     *
     * @param user
     * @return
     */
    Integer updateUserValidityTime(User user);

    /**
     * 根据用户code查询用户信息
     *
     * @param user
     * @return
     */
    User queryUserByUserCode(User user);

    /**
     * 根据用户id数组，批量查询用户信息
     *
     * @param userIdArray 用户信息列表
     * @return
     */
    List<User> batchQueryUserByIds(@Param("userIdArray") String[] userIdArray);

    /**
     * 批量添加用户信息
     *
     * @param userList 用户列表
     * @return 添加的数量
     */
    Integer batchAddUser(List<User> userList);

    /**
     * 根据手机号查询用户信息
     *
     * @param phoneNumber 手机号
     * @return 用户信息
     */
    User queryUserByPhone(@Param("phoneNumber") String phoneNumber);

    /**
     * 根据id批量查询用户信息
     *
     * @param idList 用户id集合
     * @return 用户信息
     */
    List<User> queryUserByIdList(List<String> idList);

    /**
     * 根据部门id获取人员信息
     *
     * @param deptIdList 部门id集合
     * @return 人员信息
     */
    List<User> queryUserByDeptList(List<String> deptIdList);

    /**
     * 查询所有为被删除用户的详细信息
     *
     * @return 所有用户信息
     */
    List<User> queryAllUserDetailInfo();

    /**
     * 根据用户id更新用户的状态和解锁时间
     *
     * @param id         用户id
     * @param unlockTime 解锁时间
     * @param userStatus 用户状态
     * @return 解锁的数量
     */
    Integer updateStatusAndUnlockTime(@Param("id") String id,
                                      @Param("unlockTime") Long unlockTime, @Param("userStatus") String userStatus);

    /**
     * 根据id集合查询用户信息
     *
     * @param idList
     * @return
     */
    List<User> queryUserDetailByIds(List<String> idList);

    /**
     * 根据部门id和设施类型查询用户信息
     *
     * @param dataPermission 数据权限
     * @return 用户列表id
     */
    List<String> queryUserByDeptAndDeviceType(DataPermission dataPermission);

    /**
     * 批量更新用户的解锁时间
     *
     * @param userIdList 用户id列表
     * @return
     */
    Integer batchUpdateUnlockTime(List<String> userIdList);

    /**
     * 根据用户名获取用户id列表
     *
     * @param userName 用户名
     * @return 用户id列表
     */
    List<String> queryUserIdByName(@Param("userName") String userName);

    /**
     * 根据权限分页查询用户信息
     *
     * @param userParameter 查询条件
     * @return 用户列表
     */
    List<User> queryUserByPermission(UserParameter userParameter);

    /**
     * 根据权限查询符合条件的用户数量
     *
     * @param userParameter 查询条件
     * @return 满足条件的用户数量
     */
    Long queryUserNumberByPermission(UserParameter userParameter);

    /**
     * 根据部门信息和设施类型获取人员信息
     *
     * @param dataPermission 查询条件信息
     * @return 人员列表
     */
    List<User> queryUserInfoByDeptAndDeviceType(DataPermission dataPermission);

    /**
     * 根据id获取用户的详细信息
     *
     * @param id 用户id
     * @return 用户详细信息
     */
    User queryUserDetailById(@Param("id") String id);

    /**
     * 根据角色id和部门id列表获取符合要求的用户信息
     *
     * @param roleIdList 角色id列表
     * @param deptIdList 部门id列表
     * @return 用户列表
     */
    List<User> queryUserByRoleAndDepartment(@Param("roleIdList") List<String> roleIdList,
                                            @Param("deptIdList") List<String> deptIdList);
}
