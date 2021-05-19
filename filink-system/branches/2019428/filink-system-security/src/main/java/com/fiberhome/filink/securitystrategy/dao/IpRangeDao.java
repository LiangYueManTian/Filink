package com.fiberhome.filink.securitystrategy.dao;

import com.fiberhome.filink.securitystrategy.bean.IpRange;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chaofang@wistronits.com
 * @since 2019-02-28
 */
public interface IpRangeDao extends BaseMapper<IpRange> {
    /**
     * 查询所有启用IP范围
     * @return IpRange集合List
     */
    List<IpRange> queryIpRangeAll();
    /**
     * 根据IP类型查询所有IP范围
     * @param ipRange IP类型
     * @return IpRange集合List
     */
    List<IpRange> queryRangesByType(IpRange ipRange);
    /**
     * 新增IP范围
     * @param ipRange IP范围
     * @return 数目
     */
    Integer addIpRange(IpRange ipRange);
    /**
     * 根据ID查询IP范围
     * @param rangeIds ID集合LIST
     * @return IpRange集合LIST
     */
    List<IpRange> queryRangesById(List<String> rangeIds);
    /**
     * 根据ID查询IP范围
     * @param rangeId ID
     * @return IpRange
     */
    IpRange queryIpRangeById(String rangeId);
    /**
     * 批量删除或恢复IP范围
     * @param rangeIds ID集合LIST
     * @param isDeleted 删除或恢复
     * @param updateUser 更新人
     * @return 数目
     */
    Integer deleteRangesByIds(@Param("list") List<String> rangeIds, @Param("isDeleted") String isDeleted, @Param("updateUser") String updateUser);

    /**
     * 全部启用/禁用
     * @param ipRange 启用/禁用
     * @return 数目
     */
    Integer updateAllRangesStatus(IpRange ipRange);

    /**
     * 启用/禁用
     * @param rangeIds ID集合LIST
     * @param rangeStatus 启用/禁用
     * @param updateUser 更新人
     * @return 数目
     */
    Integer updateRangesStatus(@Param("list") List<String> rangeIds, @Param("rangeStatus") String rangeStatus, @Param("updateUser") String updateUser);
}
