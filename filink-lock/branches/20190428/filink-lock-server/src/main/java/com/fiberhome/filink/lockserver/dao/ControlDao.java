package com.fiberhome.filink.lockserver.dao;

import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.lockserver.bean.ControlParam;
import com.fiberhome.filink.lockserver.bean.ControlReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 主控信息Dao
 *
 * @author CongcaiYu
 */
public interface ControlDao {

    /**
     * 根据设施id获取主控信息
     *
     * @param deviceId 设施id
     * @return 主控信息
     */
    List<ControlParam> getControlByDeviceId(String deviceId);

    /**
     * 根据设施id和主控名称查询主控信息
     *
     * @param controlParam 主控信息参数
     * @return 主控信息
     */
    ControlParam getControlByDeviceIdAndControlName(ControlParam controlParam);

    /**
     * 更新主控的设施状态或部署状态
     *
     * @param controlParam 主控信息
     */
    void updateControlStatus(ControlParam controlParam);

    /**
     * 根据设施id更新主控的部署状态
     *
     * @param deviceIds    设施id集合
     * @param deployStatus 部署状态
     */
    void updateDeployStatusByDeviceId(@Param("deviceIds") List<String> deviceIds,
                                      @Param("deployStatus") String deployStatus);

    /**
     * 根据主控id获取设施id
     *
     * @param controlId 主控id
     * @return 设施id
     */
    String getDeviceIdByControlId(String controlId);

    /**
     * 根据主控id获取该主控关联设施的最高级别的设施状态
     *
     * @param controlId 主控id
     * @return 查询结果
     */
    ControlParam getDeviceStatusById(String controlId);

    /**
     * 根据主控id获取该主控关联设施的部署状态
     *
     * @param controlId 主控id
     * @return 查询结果
     */
    List<ControlParam> getDeployStatusById(String controlId);

    /**
     * 根据主控id获取主控信息
     *
     * @param controlId 主控id
     * @return 主控信息
     */
    ControlParam getControlParamById(String controlId);

    /**
     * 查询电子锁主控信息
     *
     * @param controlReq 主控信息请求对象
     * @return 查询结果
     */
    List<ControlParam> queryControlParam(ControlReq controlReq);

    /**
     * 保存主控信息
     *
     * @param controlParam 主控信息
     */
    void addControlParams(ControlParam controlParam);

    /**
     * 根据主控id更新主控信息
     *
     * @param controlParam 主控信息
     */
    void updateControlParams(ControlParam controlParam);


    /**
     * 批量修改设施配置策略
     *
     * @param configValue 配置参数值
     * @param deviceIds   设施id集合
     * @param syncStatus  同步状态
     * @return int
     */
    int batchSetConfig(@Param("deviceIds") List<String> deviceIds,
                       @Param("configValue") String configValue,
                       @Param("syncStatus") String syncStatus);

    /**
     * 根据主控id删除电子锁主控
     *
     * @param controlId 主控id
     */
    void deleteLockAndContorlByControlId(String controlId);

    /**
     * 根据设施id删除电子锁主控
     *
     * @param deviceIds 设施id集合
     */
    void deleteControlByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    /**
     * 根据分页条件查询出有主控信息及门锁信息的设施的id集合
     *
     * @param pageCondition 分页条件
     * @param deviceIds     设施id集合
     * @return 查询结果
     */
    List<String> deviceIdListByPage(@Param("pageCondition") PageCondition pageCondition, @Param("deviceIds") List<String> deviceIds);

    /**
     * 查询出有主控信息及门锁信息的设施的id集合
     *
     * @return 查询结果
     */
    List<String> deviceIdList();

}
