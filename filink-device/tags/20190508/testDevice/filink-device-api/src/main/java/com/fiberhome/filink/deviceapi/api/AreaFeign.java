package com.fiberhome.filink.deviceapi.api;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.deviceapi.bean.AreaDeptInfo;
import com.fiberhome.filink.deviceapi.bean.AreaInfo;
import com.fiberhome.filink.deviceapi.bean.AreaInfoForeignDto;
import com.fiberhome.filink.deviceapi.fallback.AreaFeignFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 区域Feign
 *
 * @author qiqizhu@wistronits.com
 * @Date: 2019/1/25 16:44
 */
@FeignClient(name = "filink-device-server", path = "/areaInfo", fallback = AreaFeignFallback.class)
public interface AreaFeign {
    /**
     * 根据部门id批量删除与区域关系信息
     *
     * @param deptIds 部门id
     * @return 删除结果
     */
    @PostMapping("/deleteAreaDeptRelation")
    Boolean deleteAreaDeptRelation(@RequestBody List<String> deptIds);

    /**
     * 获取所有的区域信息
     *
     * @return
     */
    @GetMapping("/queryAreaListAll")
    Result queryAreaListAll();

    /**
     * 根据区域id集合查找区域信息
     *
     * @param areaIds 区域id集合
     * @return 区域id集合
     */
    @PostMapping("/selectAreaInfoByIds")
    List<AreaInfoForeignDto> selectAreaInfoByIds(@RequestBody List<String> areaIds);

    /**
     * 根据部门id获取区域信息
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaInfoByDeptIds")
    List<AreaInfo> selectAreaInfoByDeptIds(@RequestBody List<String> deptIds);

    /**
     * 根据区域id查找区域详情
     * @param areaId 区域 id
     * @return 区域信息
     */
    @GetMapping("/queryAreaById/{areaId}")
    Result queryAreaById(@PathVariable("areaId") String areaId);

    /**
     * 根据部门id集合获取区域id集合 用户服务调用
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaInfoIdsByDeptIds")
    List<String> selectAreaIdsByDeptIds(@RequestBody List<String> deptIds);
    /**
     * 根据部门id集合获取区域部门关系集合
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaDeptInfoByDeptIds")
    List<AreaDeptInfo> selectAreaDeptInfoByDeptIds(@RequestBody List<String> deptIds);
    /**
     * 根据区域id集合获取区域部门关系集合
     *
     * @param areaIds 区域id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaDeptInfoByAreaIdsForPageSelection")
    Result selectAreaDeptInfoByAreaIdsForPageSelection(@RequestBody List<String> areaIds);
}
