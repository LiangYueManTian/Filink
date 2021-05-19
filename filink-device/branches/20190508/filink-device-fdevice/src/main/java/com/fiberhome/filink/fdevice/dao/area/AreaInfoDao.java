package com.fiberhome.filink.fdevice.dao.area;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.ToTopAreaInfo;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
public interface AreaInfoDao extends BaseMapper<AreaInfo> {
    /**
     * 根据区域名查询数据
     *
     * @param areaInfo 查询名称
     * @return 查询结果
     */
    AreaInfo selectAreaInfoByName(AreaInfo areaInfo);

    /**
     * 新增区域
     *
     * @param areaInfo 区域信息
     * @return 新增结果
     */
    Integer addAreaInfo(AreaInfo areaInfo);

    /**
     * 获取区域列表
     *
     * @param areaInfoDto 查询条件
     * @return 区域信息
     */
    List<AreaInfo> queryAreaListByItem(AreaInfoDto areaInfoDto);

    /**
     * 根据id查找区域信息
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    AreaInfo selectAreaInfoById(String areaId);

    /**
     * 根据父类id查找区域信息
     *
     * @param parentId 区域id
     * @return 查询结果
     */
    List<String> selectAreaIdByParentId(String parentId);

    /**
     * 根据id批量删除区域信息
     *
     * @param areaIds 要删除的id集合
     * @return 删除条数
     */
    Integer deleteAreaInfoByIds(List<String> areaIds);

    /**
     * 向上查询区域信息
     *
     * @param parentId 父级id
     * @return 查询结果
     */
    ToTopAreaInfo selectTopArea(String parentId);

    /**
     * 根据id修改名称
     * @param areaInfo 接收实体
     * @return 修改结果
     */
    Boolean updateAreaNameById(AreaInfo areaInfo);

    /**
     *查找当前级别及子级别的数据
     * @param level 级别
     * @return 查询结果
     */
    List<AreaInfo> selectAreaInfoByLevel(int level);
    /**
     * 根据区域id集合查找区域信息
     * @param areaIds 区域id集合
     * @return 区域id集合
     */
    List<AreaInfo> selectAreaInfoByIds(List<String> areaIds);

    /**
     * 根据部门id获取区域信息
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    List<AreaInfo> selectAreaInfoByDeptIds(List<String> deptIds);

    /**
     * 根据区域id修改区域信息
     * @param areaInfo
     * @return
     */
    Integer updateAreaInfoById(AreaInfo areaInfo);

    /**
     * 查找区域列表count
     * @param areaInfoDto
     * @return
     */
    List<AreaInfo> queryAreaListByItemCount(AreaInfoDto areaInfoDto);

    /**
     * 根据区域id查找dto
     * @param areaId id
     * @return 查找结果
     */
    AreaInfoForeignDto selectAreaInfoForeignDtoById(String areaId);

    /**
     * 查询对外区域信息
     * @return 查询结果
     */
    List<AreaInfoForeignDto> queryForeignAreaListAll();
}
