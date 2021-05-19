package com.fiberhome.filink.parts.dao;

import com.fiberhome.filink.bean.FilterCondition;
import com.fiberhome.filink.bean.PageCondition;
import com.fiberhome.filink.bean.SortCondition;
import com.fiberhome.filink.parts.bean.PartInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author gzp
 * @since 2019-02-12
 */
public interface PartInfoDao extends BaseMapper<PartInfo> {

    /**
     * 根据配件名查询配件信息
     *
     * @param partName 配件名
     * @return 配件列表
     */
    PartInfo selectByName(String partName);

    /**
     * 校验配件名是否重复
     *
     * @param partsCode 配件号
     * @return 配件列表
     */
    List<PartInfo> checkPartsCode(String partsCode);

    /**
     * 新增配件
     *
     * @param partInfo 配件信息
     * @return 新增数量返回
     */
    int insertParts(PartInfo partInfo);

    /**
     * 新增配件和单位关系
     *
     * @param partId             配件ID
     * @param accountabilityUnit 单位列表
     */
    void insertUnit(@Param("partId") String partId, @Param("units") List<String> accountabilityUnit);

    /**
     * 根据配件ID删除配件单位
     *
     * @param partsId
     */
    void deletePartDeptByPartId(String partsId);

    /**
     * 批量删除配件
     *
     * @param partIds 配件ID数组
     */
    void deletePartByIds(String[] partIds);

    /**
     * 根据配件Id查询配件信息
     *
     * @param partId 配件ID
     * @return 配件信息
     */
    PartInfo selectPartsById(String partId);

    /**
     * 根据配件ID查询关联的部门单位
     *
     * @param partsId 配件ID
     * @return 单位信息
     */
    List<String> getDeptId(String partsId);

    /**
     * 根据配件ID数组查询配件列表
     *
     * @param partsIds 配件ID数组
     * @return 配件列表
     */
    List<PartInfo> selectPartsByIds(String[] partsIds);

    /**
     * 分页查询配件
     *
     * @param pageCondition
     * @param filterConditionList
     * @param sortCondition
     * @return
     */
    List<PartInfo> selectPartsPage(@Param("page") PageCondition pageCondition,
                                   @Param("filterList") List<FilterCondition> filterConditionList,
                                   @Param("sort") SortCondition sortCondition);

    /**
     * 查询配件数量
     *
     * @param filterConditionList
     * @return
     */
    Integer selectPartsCount(@Param("filterList") List<FilterCondition> filterConditionList);
}
