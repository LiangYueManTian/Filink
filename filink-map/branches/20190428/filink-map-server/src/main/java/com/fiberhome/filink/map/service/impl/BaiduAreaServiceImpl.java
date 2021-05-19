package com.fiberhome.filink.map.service.impl;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.map.bean.BaiduArea;
import com.fiberhome.filink.map.dao.BaiduAreaDao;
import com.fiberhome.filink.map.exception.MapSystemException;
import com.fiberhome.filink.map.service.BaiduAreaService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fiberhome.filink.map.utils.MapI18n;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 地区码表 服务实现类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-07
 */
@Service
public class BaiduAreaServiceImpl extends ServiceImpl<BaiduAreaDao, BaiduArea> implements BaiduAreaService {
    /**
     * 注入地图持久层
     */
    @Autowired
    private BaiduAreaDao baiduAreaDao;

    /**
     * 根据名称查看地区
     *
     * @param areaName
     * @return 地区
     */
    @Override
    public Result queryAreaByName(String areaName) {
        BaiduArea baiduArea = baiduAreaDao.queryAreaByName(areaName);
        if (StringUtils.isEmpty(baiduArea)) {
            throw new MapSystemException();
        }
        return ResultUtils.success(baiduArea);
    }

    /**
     * 根据名称查看其子地区
     *
     * @param areaName
     * @return 子地区集
     */
    @Override
    public Result querySonAreaByName(String areaName) {
        List<BaiduArea> baiduAreas = baiduAreaDao.querySonAreaByName(areaName);
        if (StringUtils.isEmpty(baiduAreas)) {
            throw new MapSystemException();
        }
        return ResultUtils.success(baiduAreas);
    }

    /**
     * 查询所有省
     *
     * @return 省地区集
     */
    @Override
    public Result queryAllProvince() {
        List<BaiduArea> baiduAreas = baiduAreaDao.queryAllProvince();
        if (StringUtils.isEmpty(baiduAreas)) {
            throw new MapSystemException();
        }
        return ResultUtils.success(baiduAreas);
    }

    /**
     * 查询所有的城市信息
     *
     * @return 城市信息集合
     */
    @Override
    public Result queryAllCityInfo() {
        List<BaiduArea> baiduAreas = baiduAreaDao.queryAllCityInfo();
        if (StringUtils.isEmpty(baiduAreas)) {
            throw new MapSystemException();
        }
        return ResultUtils.success(baiduAreas);
    }

    /**
     * 根据id更新地区
     *
     * @param area
     * @return  更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateAreaById(BaiduArea area) {
        //根据id更新数据
        Integer result = baiduAreaDao.updateById(area);
        if (null != result && 1 == result) {
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(MapI18n.MAP_UPDATE_SUCCESS));
        } else {
            throw new MapSystemException();
        }

    }
}
