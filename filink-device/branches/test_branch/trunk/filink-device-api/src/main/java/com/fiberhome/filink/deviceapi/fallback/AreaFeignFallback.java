package com.fiberhome.filink.deviceapi.fallback;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.api.AreaFeign;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yuanyao@wistronits.com
 * create on 2018/12/16 1:21 PM
 */
@Slf4j
@Component
public class AreaFeignFallback implements AreaFeign {

    private static final String INFO = "Regional service feign call blow》》》》》》》》》》";

    @Override
    public Boolean deleteAreaDeptRelation(List<String> deptIds) {
        log.error(INFO);
        return null;
    }

    @Override
    public Result queryAreaListAll() {
        log.error(INFO);
        return null;
    }

    @Override
    public List<AreaInfoForeignDto> selectAreaInfoByIds(List<String> areaIds) {
        log.error(INFO);
        return null;
    }

    @Override
    public List<AreaInfo> selectAreaInfoByDeptIds(List<String> deptIds) {
        log.error(INFO);
        return null;
    }

    @Override
    public Result queryAreaById(String areaId) {
        log.error(INFO);
        return null;
    }

    @Override
    public List<String> selectAreaIdsByDeptIds(List<String> deptIds) {
        log.error(INFO);
        return null;
    }

    @Override
    public List<AreaDeptInfo> selectAreaDeptInfoByDeptIds(List<String> deptIds) {
        log.error(INFO);
        return null;
    }

    @Override
    public Result selectAreaDeptInfoByAreaIdsForPageSelection(List<String> areaIds) {
        log.error(INFO);
        return null;
    }
}
