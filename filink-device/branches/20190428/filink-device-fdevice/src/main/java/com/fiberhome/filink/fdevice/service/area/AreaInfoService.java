package com.fiberhome.filink.fdevice.service.area;


import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;

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
    Result queryAreaListByItem(QueryCondition<AreaInfoDto> queryCondition);

    /**
     * 根据id查找区域信息
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    Result queryAreaById(String areaId);

    /**
     * 根据id查找区域信息 for pda
     *
     * @param areaId 区域id
     * @return 区域信息
     */
    AreaInfo queryAreaByIdForPda(String areaId);

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
     * @return
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
     * @param deptIds
     * @return 删除结果
     */
    Boolean deleteAreaDeptRelation(List<String> deptIds);

    /**
     * 查询区域细节是否可以进行修改
     *
     * @param areaId 区域id
     * @return
     */
    Boolean queryAreaDetailsCanChange(String areaId);

    /**
     * 根据区域id集合查找区域信息
     *
     * @param areaIds 区域id集合
     * @return 区域id集合
     */
    List<AreaInfoForeignDto> selectAreaInfoByIds(List<String> areaIds);

    /**
     * 根据部门id获取区域信息
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    List<AreaInfo> selectAreaInfoByDeptIds(List<String> deptIds);

    /**
     * 创建导出区域列表任务
     *
     * @param exportDto 传入信息
     * @return 创建任务结果
     */
    Result exportArea(ExportDto exportDto);

    /**
     * 查询区域树结构，用于选择所属区域
     *
     * @param userIds
     * @return
     */
    Result selectForeignAreaInfo(List<String> userIds);

    /**
     * 查询对外区域平行结构
     *
     * @return 查询结果
     */
    Result selectSimultaneousForeignAreaInfo();

    /**
     * 根据部门id集合获取区域id集合
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    List<String> selectAreaIdsByDeptIds(List<String> deptIds);

    /**
     * 根据部门id获取区域部门关系信息
     *
     * @param deptIds 部门id集合
     * @return 查询结果
     */
    List<AreaDeptInfo> selectAreaDeptInfosByDeptIds(List<String> deptIds);

    /**
     * 根据区域id集合获取区域部门关系集合
     *
     * @param areaIds 区域id集合
     * @return 返回查询结果
     */
    Result selectAreaDeptInfoByAreaIds(List<String> areaIds);
}
