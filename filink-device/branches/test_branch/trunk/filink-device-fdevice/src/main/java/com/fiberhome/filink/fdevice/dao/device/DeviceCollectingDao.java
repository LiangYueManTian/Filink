package com.fiberhome.filink.fdevice.dao.device;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fiberhome.filink.fdevice.bean.device.DeviceCollecting;
import com.fiberhome.filink.fdevice.dto.DeviceAttentionDto;
import com.fiberhome.filink.fdevice.dto.DeviceParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * <p>
 * 我的关注 Mapper 接口
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-14
 */
public interface DeviceCollectingDao extends BaseMapper<DeviceCollecting> {
    /**
     * 获取我的关注的统计数据
     *
     * @param userId 用户id
     * @return 关注统计数据
     */
    List<DeviceAttentionDto> attentionCount(String userId);

    /**
     * 我的关注 模糊查询 分页
     *
     * @param rowBounds 分页查询条件（可以为 RowBounds.DEFAULT）
     * @param wrapper   实体对象封装操作类（可以为 null）
     * @return 关注分页数据
     */
    List<DeviceAttentionDto> selectAttentionPage(RowBounds rowBounds, @Param("ew") EntityWrapper<DeviceAttentionDto> wrapper);

    /**
     * <p>
     * 根据 Wrapper 条件，查询总记录数
     * </p>
     *
     * @param wrapper 实体对象
     * @return int 关注总数
     */
    Integer selectAttentionCount(@Param("ew") EntityWrapper<DeviceAttentionDto> wrapper);

    /**
     * 根据用户ID和设施ID查询关注数量
     *
     * @param deviceId 设施ID
     * @param userId   用户ID
     * @return 关注总数
     */
    int selectAttentionDeviceCount(@Param("deviceId") String deviceId, @Param("userId") String userId);

    /**
     * 获取我的关注列表 无分页
     *
     * @param  userId 用户id
     * @param deviceParam 设施参数
     * @return 关注列表
     */
    List<DeviceAttentionDto> selectAttentionList(@Param("userId") String userId, @Param("req") DeviceParam deviceParam);

    /**
     *根据设施id查询我的关注的一条设施信息
     *
     * @param deviceCollecting
     * @return 设施信息
     */
    DeviceAttentionDto selectOneAttentionByDeviceId(DeviceCollecting deviceCollecting);

    /**
     * 根据设施id删除关注
     *
     * @param deviceIdList
     */
    void deleteAttentionByDeviceIds(List<String> deviceIdList);
}
