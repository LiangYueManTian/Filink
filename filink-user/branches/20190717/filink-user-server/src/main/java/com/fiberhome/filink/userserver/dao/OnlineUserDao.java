package com.fiberhome.filink.userserver.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.OnlineParameter;
import com.fiberhome.filink.userserver.bean.OnlineUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  onlineUser Mapper 接口
 *
 * @author wtao103@fiberhome.com
 * @since 2019-03-08
 */
public interface OnlineUserDao extends BaseMapper<OnlineUser> {

    /**
     * 查询所有在心用户信息
     * @return
     */
    List<String> queryAllOnlineUser();

    /**
     * 批量添加在线用户信息
     * @param onlineUsers
     * @return
     */
    Long batchAddOnlineUser(@Param("onlineUsers") List<OnlineUser> onlineUsers);

    /**
     * 条件查询在线用户信息
     * @param onlineParameter
     * @return
     */
    List<OnlineUser> queryOnlineUserList(OnlineParameter onlineParameter);

    /**
     * 查询在线用户的数量
     * @param onlineParameter
     * @return
     */
    Long queryOnlineUserNumber(OnlineParameter onlineParameter);
}
