package com.fiberhome.filink.map.dao;

import com.fiberhome.filink.map.bean.BaiduArea;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 地区码表 Mapper 接口
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
@Mapper
public interface BaiduAreaDao extends BaseMapper<BaiduArea> {
    /**
     * 根据名称查看地区
     *
     * @param areaName
     * @return 地区
     */
    BaiduArea queryAreaByName(String areaName);

    /**
     * 根据名称查看其子地区
     *
     * @param areaName
     * @return 子地区集
     */
    List<BaiduArea> querySonAreaByName(String areaName);

    /**
     * 查询所有省
     *
     * @return 省地区集
     */
    List<BaiduArea> queryAllProvince();


    /**
     * 查询所有城市信息
     *
     * @return 城市集
     */
    List<BaiduArea> queryAllCityInfo();
}
