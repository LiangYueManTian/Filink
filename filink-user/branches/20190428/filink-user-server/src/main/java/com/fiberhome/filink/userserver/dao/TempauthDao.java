package com.fiberhome.filink.userserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.DeviceInfo;
import com.fiberhome.filink.userserver.bean.Tempauth;
import com.fiberhome.filink.userserver.bean.TempAuthParameter;
import com.fiberhome.filink.userserver.bean.UserAuthInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 临时授权表 Mapper 接口
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
public interface TempauthDao extends BaseMapper<Tempauth> {

    /**
     * 条件查询临时授权列表
     * @param tempAuthParameter 临时授权参数类
     * @return  临时授权实体类
     */
    List<Tempauth> queryTempAuthByCondition(TempAuthParameter tempAuthParameter);

    /**
     * 条件查询临时授权数量
     * @param tempAuthParameter 临时授权参数类
     * @return  临时授权的数量
     */
    Long queryTempAuthNumberByCondition(TempAuthParameter tempAuthParameter);

    /**
     * 根据id查询临时授权信息
     * @param id    临时授权id
     * @return  临时授权信息
     */
    Tempauth queryTempAuthById(@Param("id") String id);

    /**
     * 批量查询临时授权信息
     * @param idArray   临时授权列表
     * @return  临时授权集合
     */
    List<Tempauth> batchQueryTempAuthByIds(@Param("idArray") String[] idArray);

    /**
     * 单个审核临时授权信息
     * @param tempauth 临时授权信息
     * @return  修改的结果
     */
    Integer modifyTempAuthStatus(Tempauth tempauth);

    /**
     * 批量审核临时授权信息
     * @param idArray    临时授权列表
     * @param tempAuth  审核的信息
     * @return  审核的结果
     */
    Integer batchModifyTempAuthStatus(@Param("idArray") String[] idArray,@Param("tempAuth") Tempauth tempAuth);

    /**
     * 批量查询临时授权信息
     * @param idArray   临时授权id
     * @return  临时授权列表信息
     */
    List<Tempauth> batchQueryTempAuthByIdArray(@Param("idArray") String[] idArray);

    /**
     * 删除单个临时授权信息
     * @param id    待删除临时授权信息
     * @return  删除的结果
     */
    Integer deleteTempAuthById(@Param("id") String id);

    /**
     * 批量删除临时授权信息
     * @param idArray   临时授权数组
     * @return  删除的数量
     */
    Integer batchDeleteUnifyAuth(@Param("idArray") String[] idArray);

    /**
     * 获取临时授权信息
     * @param userId    用户id
     * @return  临时授权信息列表
     */
    List<Tempauth> queryTempAuthByUserId(@Param("userId") String userId);

    /**
     * 根据用户，设施，门锁信息查询授权信息
     * @param userAUthInfo
     * @return
     */
    List<Tempauth> queryAuthInfoByUserIdAndDeviceAndDoor(UserAuthInfo userAUthInfo);

    /**
     * 根据设施信息获取临时授权信息
     * @param deviceInfo    设施信息
     * @return  临时授权信息
     */
    List<Tempauth> queryTempAuthByDevice(DeviceInfo deviceInfo);

}
