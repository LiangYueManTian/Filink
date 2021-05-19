package com.fiberhome.filink.fdevice.service.area;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Map;

/**
 * <p>
 * 区域信息服务类
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
public interface AreaInfoService extends IService<AreaInfo> {
    /**
     * 查询区域名是否存在
     *
     * @param areaInfo 查询名称
     * @return 查询结果
     */
    Result queryAreaNameIsExist(AreaInfo areaInfo);

    /**
     * 新增区域
     *
     * @param areaInfo 区域信息
     * @return 新增结果
     */
    Result addArea(AreaInfo areaInfo);

    /**
     * 获取区域列表
     *
     * @param queryCondition 查询条件
     * @return 区域信息
     */
    Result queryAreaListByItem(QueryCondition queryCondition);

    /**
     * 根据id查找区域信息
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    Result queryAreaById(String areaId);

    /**
     * 根据id修改区域信息
     *
     * @param areaInfo 区域信息
     * @return 修改结果
     */
    Result updateAreaInfo(AreaInfo areaInfo);

    /**
     * 删除区域信息
     *
     * @param areaIds 区域id集合
     * @return 删除结果
     */
    Result deleteAreaByIds(List<String> areaIds);

    /**
     * 关联设施
     *
     * @param map 关联设施信息
     * @return 操作结果
     */
    Boolean setAreaDevice(Map<String, List<String>> map);

    /**
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    Result queryAreaListAll();

    /**
     * 删除区域部门关系
     *
     * @param deptIds 传入id集合
     * @return 删除结果
     */
    Boolean deleteAreaDeptRelation(List<String> deptIds);

    /**
     * 查询区域细节是否可以进行修改
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    Boolean queryAreaDetailsCanChange(String areaId);
}
