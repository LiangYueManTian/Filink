package com.fiberhome.filink.fdevice.dao.area;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfoTree;
import com.fiberhome.filink.fdevice.bean.area.ToTopAreaInfo;
import com.fiberhome.filink.fdevice.dto.HomeAreaInfoDto;

import java.util.List;
import java.util.Map;

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
     * @param map 查询条件
     * @return 区域信息
     */
    List<AreaInfoTree> queryAreaListByItem(Map map);

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
    List<String> selectAreaIdbyParentId(String parentId);

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
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    List<HomeAreaInfoDto> queryAreaListAll();

    /**
     * 根据id 修改名称
     *
     * @param areaInfo 修改实体
     * @return 修改结果
     */
    Boolean updateAreaNameById(AreaInfo areaInfo);

    /**
     * 查询大于等于等前级别的数据
     *
     * @param level 传入级别
     * @return 查询结果
     */
    List<AreaInfoTree> selectAreaInfoByLevel(int level);

    /**
     * 根据区域id修改区域信息
     *
     * @param areaInfo
     * @return
     */
    Integer updateAreaInfoById(AreaInfo areaInfo);
}
