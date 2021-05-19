package com.fiberhome.filink.userserver.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.userserver.bean.AuthDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 统一授权临时授权和设施对应的中间表 Mapper 接口
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
public interface AuthDeviceDao extends BaseMapper<AuthDevice> {

    /**
     * 批量添加设施门数据
     * @param authDeviceList    设施门数据列表
     * @return  添加的数量
     */
    Integer batchAuthDevice(List<AuthDevice> authDeviceList);

    /**
     * 根据授权id删除设施信息
     * @param authId
     * @return
     */
    Integer batchDeleteByAuthId(@Param("authId") String authId);

    /**
     * 根据id删除中间表信息
     * @param authDeviceIdList id集合
     * @return  删除的数量
     */
    Integer batchDeleteAuthDevice(List<String> authDeviceIdList);
}
