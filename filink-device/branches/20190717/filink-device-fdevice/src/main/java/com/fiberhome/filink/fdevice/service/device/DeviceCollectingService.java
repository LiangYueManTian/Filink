package com.fiberhome.filink.fdevice.service.device;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.device.DeviceCollecting;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 我的关注 服务类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-14
 */
public interface DeviceCollectingService extends IService<DeviceCollecting> {
    /**
     * 添加设施关注
     *
     * @param deviceCollecting
     * @return 返回结果
     */
    Result addDeviceCollecting(DeviceCollecting deviceCollecting);


    /**
     * 删除设施关注
     *
     * @param deviceCollecting
     * @return 返回结果
     */
    Result delDeviceCollecting(DeviceCollecting deviceCollecting);

    /**
     * 获取我的关注列表
     *
     * @param queryCondition
     * @return 关注列表
     */
    Result queryDeviceCollectingList(QueryCondition<DeviceCollecting> queryCondition);

    /**
     * 按设施类型统计我的关注数据
     *
     * @return 统计数据
     */
    Result queryDeviceCollectingCountByType();

    /**
     * 获取我的关注列表 无分页
     *
     * @return 关注列表
     */
    Result queryAttentionList();

}
