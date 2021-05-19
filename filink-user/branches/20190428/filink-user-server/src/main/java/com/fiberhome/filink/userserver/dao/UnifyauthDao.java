package com.fiberhome.filink.userserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.Unifyauth;
import com.fiberhome.filink.userserver.bean.UnifyAuthParameter;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 统一授权 Mapper 接口
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
public interface UnifyauthDao extends BaseMapper<Unifyauth> {

    /**
     * 条件查询统一授权信息
     * @param unifyAuthParameter 条件实体类
     * @return  统一授权列表
     */
    public List<Unifyauth> queryUnifyAuthByCondition(UnifyAuthParameter unifyAuthParameter);

    /**
     * 条件查询统一授权信息的数量
     * @param unifyAuthParameter 条件实体类
     * @return 统一授权数目
     */
    public Long queryUnifyAuthNumberByCondition(UnifyAuthParameter unifyAuthParameter);

    /**
     * 根据id查询统一授权信息
     * @param id 统一授权id
     * @return  统一授权信息
     */
    public Unifyauth queryUnifyAuthById(@Param("id") String id);

    /**
     * 更新统一授权用户信息
     * @param unifyauth 统一授权用户信息
     * @return  更新的条数
     */
    public Integer modifyUnifyAuth(Unifyauth unifyauth);

    /**
     * 删除指定的统一授权信息
     * @param id 统一授权id
     * @return  删除的结果
     */
    public Integer deleteUnifyAuthById(@Param("id") String id);

    /**
     * 批量删除统一授权信息
     * @param idArray   统一授权id数组
     * @return  删除的数量
     */
    public Integer batchDeleteUnifyAuth(@Param("idArray") String[] idArray);

    /**
     * 批量查询统一授权信息
     * @param idArray   统一授权id
     * @return  统一授权列表
     */
    public List<Unifyauth> batchQueryUnifyAuthByIds(@Param("idArray") String[] idArray);

    /**
     * 修改统一授权的状态
     * @param idArray   待修改的数据
     * @param authStatus    统一授权状态
     * @return
     */
    public Integer batchModifyUnifyAuthStatus(@Param("idArray") String[] idArray,@Param("authStatus") int authStatus);

    /**
     * 根据用户id获取统一授权信息
     * @param userId    用户id
     * @return  授权信息列表
     */
    public List<Unifyauth> queryUnifyAuthByUserId(@Param("userId") String userId);

    /**
     * 根据用户，设施，门锁信息查询统一授权信息
     * @param userAUthInfo
     * @return
     */
    List<Unifyauth> queryAuthInfoByUserIdAndDeviceAndDoor(UserAuthInfo userAUthInfo);

    /**
     * 根据设施信息获取统一授权信息
     * @param deviceInfo    设施信息
     * @return  统一授权信息
     */
    List<Unifyauth> queryUnifyAuthByDevice(DeviceInfo deviceInfo);

    /**
     * 根据名字查询统一授权信息
     * @param name 授权名字
     * @return  统一授权信息
     */
    List<Unifyauth> queryAuthByName(@Param("name") String name);
}
