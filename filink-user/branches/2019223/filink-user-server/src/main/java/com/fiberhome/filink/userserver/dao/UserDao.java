package com.fiberhome.filink.userserver.dao;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fiberhome.filink.userserver.bean.PasswordDto;
import com.fiberhome.filink.userserver.bean.User;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.UserParameter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 *  用户表 Mapper 接口
 *
 * @author wtao103@fiberhome.com
 * @since 2019-01-03
 */
@Repository
public interface UserDao extends BaseMapper<User> {

    /**
     * 查询用户列表信息
     * @param page
     * @param entityWrapper
     * @return 用户列表信息
     */
    List<User> selectUserList(Page page,@Param("entityWrapper") EntityWrapper entityWrapper);
    /**
     * 删除多个用户
     * @param userIdArray 用户id数组
     * @param userType
     * @return 删除结果
     * @throws Exception
     */
    Integer deleteUser(@Param("userIdArray") String[] userIdArray,@Param("userType") String userType);

    /**
     * 根据用户id查询用户详情
     * @param id 用户id
     * @return user User用户详细信息
     */
    User queryUserById(@Param("id") String id);

    /**
     * 根据用户名查询用户详细信息
     * @param userName 用户名称
     * @return 用户信息
     */
    User queryUserByNmae(@Param("userName") String userName);

    /**
     * 批量更改用户的状态
     * @param userStatus
     * @param userIdArray
     * @return 修改的个数
     */
    Integer updateUserStatus(@Param("userStatus") int userStatus,@Param("userIdArray") String[] userIdArray);

    /**
     * 修改用户密码
     * @param pwd
     * @return
     */
    Integer modifyPWD(@RequestBody PasswordDto pwd);

    /**
     * 根据部门id查询用户信息
     * @param deptIdArray
     * @return
     */
    List<User> queryUserByDepts(@Param("deptIdArray") String[] deptIdArray);

    /**
     * 根据角色id查询用户信息
     * @param roleIdArray
     * @return
     */
    List<User> queryUserByRoles(@Param("roleIdArray") String[] roleIdArray);

    /**
     * 根据条件查询用户信息
     * @param userParameter
     * @return
     */
    List<User> queryUserByField(UserParameter userParameter);

    /**
     * 条件查询用户的数量
     * @param userParameter
     * @return
     */
    Long queryUserNum(UserParameter userParameter);

    /**
     * 根据id查询用户详情
     * @param userId
     * @return
     */
    User queryUserInfoById(@Param("userId") String userId);

    /**
     * 查询所有没被删除的用户
     * @return
     */
    List<User> queryAllUser();

    /**
     * 更新用户的有效期
     * @param user
     * @return
     */
    Integer updateUserValidityTime(User user);

    /**
     * 根据用户code查询用户信息
     * @param user
     * @return
     */
    User queryUserByUserCode(User user);
}
