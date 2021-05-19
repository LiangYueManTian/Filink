package com.fiberhome.filink.fdevice.dto;

import com.fiberhome.filink.bean.PageCondition;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *  查询设施日志 请求参数 for pda
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/24
 */
@Data
public class DeviceLogReqtForPda {
    /**
     * 日志类型
     * 枚举值：激活0 休眠1 升级2 箱门变化3 开锁 4 关锁5
     */
    private List<Integer> logType;
    /**
     * 设施类型
     */
    private List<String> deviceType;
    /**
     * 区域id
     */
    private List<String> areaId;
    /**
     * 设施id
     */
    private List<String> deviceId;
    /**
     * 起始时间
     */
    private Long startTime;
    /**
     * 终止时间
     */
    private Long endTime;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;

}
