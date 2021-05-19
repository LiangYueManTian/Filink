package com.fiberhome.filink.lockserver.dao;

import com.fiberhome.filink.lockserver.bean.Control;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主控信息Dao
 * @author CongcaiYu
 */
public interface MasterControlDao {

    /**
     * 根据设施id获取主控信息
     * @param deviceId 设施id
     * @return 主控信息
     */
    Control getControlByDeviceId(String deviceId);

    /**
     * 保存主控信息
     * @param control 主控信息
     */
    void saveControlParams(Control control);

    /**
     * 根据设施id修改主控信息
     * @param control 主控信息
     * @return int
     */
    int updateControlParamsByDeviceId(Control control);

    /**
     * 批量修改设施配置策略
     * @param configValue 配置参数值
     * @param deviceIds 设施id集合
     * @param syncStatus 同步状态
     * @return int
     */
    int batchSetConfig(@Param("deviceIds") List<String> deviceIds,
                       @Param("configValue") String configValue,
                       @Param("syncStatus") String syncStatus);

    /**
     * 根据设施id查询主控个数
     *
     * @param deviceIds 设施id集合
     * @return long
     */
    long getControlCountByDeviceIds(List<String> deviceIds);
}
