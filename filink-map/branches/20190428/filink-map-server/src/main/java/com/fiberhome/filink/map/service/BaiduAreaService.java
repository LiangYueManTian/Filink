package com.fiberhome.filink.map.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.map.bean.BaiduArea;
import com.baomidou.mybatisplus.service.IService;


/**
 * <p>
 * 地区码表 服务类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
public interface BaiduAreaService extends IService<BaiduArea> {
    /**
     * 根据名称查看地区
     *
     * @param areaName
     * @return 地区
     */
    Result queryAreaByName(String areaName);

    /**
     * 根据名称查看其子地区
     *
     * @param areaName
     * @return 子地区集
     */
    Result querySonAreaByName(String areaName);

    /**
     * 根据id更新地区
     *
     * @param area
     * @return 更新结果
     */
    Result updateAreaById(BaiduArea area);

    /**
     * 查询所有省
     *
     * @return 省地区集
     */
    Result queryAllProvince();

    /**
     * 查询所有的城市信息
     *
     * @return 城市信息集合
     */
    Result queryAllCityInfo();
}
