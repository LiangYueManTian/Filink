package com.fiberhome.filink.map.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.map.bean.BaiduArea;
import com.fiberhome.filink.map.exception.MapParamsException;
import com.fiberhome.filink.map.service.BaiduAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 地区码表 前端控制器
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
@RestController
@RequestMapping("/map")
public class BaiduAreaController {
    /**
     * 注入地图逻辑层
     */
    @Autowired
    private BaiduAreaService baiduAreaService;

    /**
     * 根据名称查询地区
     *
     * @param baiduArea 参数bean
     * @return 地区
     */
    @PostMapping("/getArea")
    public Result queryAreaByName(@RequestBody BaiduArea baiduArea) {
        //检验参数
        if (baiduArea == null || StringUtils.isEmpty(baiduArea.getAreaName())) {
            throw new MapParamsException();
        }
        return baiduAreaService.queryAreaByName(baiduArea.getAreaName());

    }

    /**
     * 根据名称查询其子地区
     *
     * @param baiduArea 参数bean
     * @return 子地区集
     */
    @PostMapping("/getSonArea")
    public Result querySonAreaByName(@RequestBody BaiduArea baiduArea) {
        //检验参数
        if (baiduArea == null || StringUtils.isEmpty(baiduArea.getAreaName())) {
            throw new MapParamsException();
        }
        return baiduAreaService.querySonAreaByName(baiduArea.getAreaName());

    }

    /**
     * 查询所有省
     *
     * @return 省地区集
     */
    @GetMapping("/queryAllProvince")
    public Result queryAllProvince() {
        return baiduAreaService.queryAllProvince();

    }

    /**
     * 查询所有城市信息
     *
     * @return 城市信息集
     */
    @GetMapping("/queryAllCityInfo")
    public Result queryAllCityInfo() {
        return baiduAreaService.queryAllCityInfo();

    }

    /**
     * 根据id更新地区
     *
     * @param area
     * @return 更新结果
     */
    @PostMapping("/updateArea")
    public Result updateAreaById(@RequestBody BaiduArea area) {
        //检验参数
        if (StringUtils.isEmpty(area) || StringUtils.isEmpty(area.getAreaId())) {
            throw new MapParamsException();
        }
        return baiduAreaService.updateAreaById(area);
    }


}
