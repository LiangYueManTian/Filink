package com.fiberhome.filink.fdevice.controller.area;


import com.fiberhome.filink.bean.*;
import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.fdevice.bean.area.AreaDeptInfo;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.constant.area.AreaConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaI18nConstant;
import com.fiberhome.filink.fdevice.constant.area.AreaResultCode;
import com.fiberhome.filink.fdevice.dto.AreaInfoDto;
import com.fiberhome.filink.fdevice.dto.AreaInfoForeignDto;
import com.fiberhome.filink.fdevice.exception.FiLinkAreaDateFormatException;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.utils.CheckUtil;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qiqizhu@wistronits.com
 * @since 2019-01-03
 */
@RestController
@RequestMapping("/areaInfo")
public class AreaInfoController {
    /**
     * 自动注入区域service
     */
    @Autowired
    private AreaInfoService areaInfoService;

    /**
     * 查询区域名称是否存在
     *
     * @param areaInfo 区域名称
     * @return 查询结果
     */
    @PostMapping("/queryAreaNameIsExist")
    public Result queryAreaNameIsExist(@RequestBody AreaInfo areaInfo) {
        if (StringUtils.isEmpty(areaInfo.getAreaName())) {
            return ResultUtils.warn(AreaResultCode.AREA_NAME_NULL, I18nUtils.getSystemString(AreaI18nConstant.AREA_NAME_NULL));
        }
        if (!areaInfo.checkAreaName()) {
            return ResultUtils.warn(AreaResultCode.AREA_NAME_FORMAT_IS_INCORRECT, I18nUtils.getSystemString(AreaI18nConstant.AREA_NAME_FORMAT_IS_INCORRECT));
        }
        return areaInfoService.queryAreaNameIsExist(areaInfo);
    }


    /**
     * 查询区域细节是否可以进行修改
     *
     * @param areaId 区域id
     * @return
     */
    @GetMapping("/queryAreaDetailsCanChange/{areaId}")
    public Result queryAreaDetailsCanChange(@PathVariable String areaId) {
        if (StringUtils.isEmpty(areaId)) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        Boolean flag = areaInfoService.queryAreaDetailsCanChange(areaId);
        if (flag) {
            return ResultUtils.success();
        } else {
            return ResultUtils.warn(AreaResultCode.FALSE);
        }
    }

    /**
     * 新增区域
     *
     * @param areaInfo 区域信息
     * @return 新增结果
     */
    @PostMapping("/addArea")
    public Result addArea(@RequestBody AreaInfo areaInfo) {
        String userId = RequestInfoUtils.getUserId();
        areaInfo.setCreateUser(userId);
        //名称校验 参数格式化
        Result result = CheckUtil.checkParameterAndFormat(areaInfo);
        if (result != null) {
            return result;
        }
        //参数格式校验
        if (!areaInfo.checkParameterFormat()) {
            throw new FiLinkAreaDateFormatException();
        }
        //添加区域的时候加锁
        String lockKey = AreaConstant.ADD_AREA_LOCK;
        //等待获取锁的时间，单位ms
        int acquireTimeout = 10000;
        //拿到锁的超时时间
        int timeout = 5000;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (StringUtils.isEmpty(lockIdentifier)) {
            return ResultUtils.warn(AreaResultCode.BUSY_SERVER, I18nUtils.getSystemString(AreaI18nConstant.BUSY_SERVER));
        }
        result = areaInfoService.addArea(areaInfo);
        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return result;
    }

    /**
     * 获取区域列表
     *
     * @param queryCondition 查询条件
     * @return 区域信息
     */
    @PostMapping("/areaListByPage")
    public Result areaListByPage(@RequestBody QueryCondition<AreaInfoDto> queryCondition) {
        return areaInfoService.queryAreaListByItem(queryCondition);
    }

    /**
     * 根据id查找区域信息
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    @GetMapping("/queryAreaById/{areaId}")
    public Result queryAreaById(@PathVariable String areaId) {
        if (StringUtils.isEmpty(areaId)) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        return areaInfoService.queryAreaById(areaId);
    }

    /**
     * 根据id修改区域信息
     *
     * @param areaInfo 区域信息
     * @return 修改结果
     */
    @PutMapping("/updateAreaById")
    public Result updateAreaById(@RequestBody AreaInfo areaInfo) {
        String userId = RequestInfoUtils.getUserId();
        areaInfo.setUpdateUser(userId);
        //名称校验  参数格式化
        Result result = CheckUtil.checkParameterAndFormat(areaInfo);
        if (result != null) {
            return result;
        }
        String areaId = areaInfo.getAreaId();
        if (StringUtils.isEmpty(areaId)) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        //参数格式校验
        if (!areaInfo.checkParameterFormat()) {
            throw new FiLinkAreaDateFormatException();
        }
        //更新区域的时候加锁
        String lockKey = AreaConstant.UPDATE_AREA_LOCK;
        //等待获取锁的时间，单位ms
        int acquireTimeout = 10000;
        //拿到锁的超时时间
        int timeout = 5000;
        //获取时间锁
        String lockIdentifier = RedisUtils.lockWithTimeout(lockKey, acquireTimeout, timeout);
        if (StringUtils.isEmpty(lockIdentifier)) {
            return ResultUtils.warn(AreaResultCode.BUSY_SERVER, I18nUtils.getSystemString(AreaI18nConstant.BUSY_SERVER));
        }
        result = areaInfoService.updateAreaInfo(areaInfo);
        //释放锁
        RedisUtils.releaseLock(lockKey, lockIdentifier);
        return result;
    }

    /**
     * 关联设施
     *
     * @param map 关联设施信息
     * @return 操作结果
     */
    @PutMapping("/setAreaDevice")
    public Result setAreaDevice(@RequestBody Map<String, List<String>> map) {
        if (map == null || map.size() == 0) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        Boolean flag = areaInfoService.setAreaDevice(map);
        if (flag) {
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(AreaI18nConstant.ASSOCIATED_FACILITIES_SUCCESSFUL));
        }
        return ResultUtils.warn(AreaResultCode.RELATED_FACILITIES_FAIL, I18nUtils.getSystemString(AreaI18nConstant.RELATED_FACILITIES_FAIL));
    }

    /**
     * 删除区域信息
     *
     * @param areaIds 区域id集合
     * @return
     */
    @PostMapping("/deleteAreaByIds")
    public Result deleteAreaByIds(@RequestBody List<String> areaIds) {
        if (areaIds == null || areaIds.size() == 0) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        return areaInfoService.deleteAreaByIds(areaIds);
    }

    /**
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    @GetMapping("/queryAreaListForPageSelection")
    public Result queryAreaListForPageSelection() {
        return areaInfoService.queryAreaListAll();
    }

    /**
     * 删除区域部门关系
     */
    @PostMapping("/deleteAreaDeptRelation")
    public Boolean deleteAreaDeptRelation(@RequestBody List<String> deptIds) {
        if (deptIds.size() == 0) {
            return false;
        }
        return areaInfoService.deleteAreaDeptRelation(deptIds);
    }

    /**
     * 根据区域id集合查找区域信息
     *
     * @param areaIds 区域id集合
     * @return 区域id集合
     */
    @PostMapping("/selectAreaInfoByIds")
    public List<AreaInfoForeignDto> selectAreaInfoByIds(@RequestBody List<String> areaIds) {
        if (areaIds.size() == 0) {
            return null;
        }
        return areaInfoService.selectAreaInfoByIds(areaIds);
    }

    /**
     * 给前端 根据区域id集合查找区域信息
     *
     * @param areaIds 区域id集合
     * @return 区域id集合
     */
    @PostMapping("/selectAreaInfoByIdsForView")
    public Result selectAreaInfoByIdsForView(@RequestBody List<String> areaIds) {
        if (areaIds.size() == 0) {
            return null;
        }
        return ResultUtils.success(areaInfoService.selectAreaInfoByIds(areaIds));
    }

    /**
     * 根据部门id获取区域信息
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaInfoByDeptIds")
    public List<AreaInfo> selectAreaInfoByDeptIds(@RequestBody List<String> deptIds) {
        if (deptIds.size() == 0) {
            return null;
        }
        return areaInfoService.selectAreaInfoByDeptIds(deptIds);
    }

    /**
     * 给前端根据部门id获取区域信息
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaInfoByDeptIdsForView")
    public Result selectAreaInfoByDeptIdsForView(@RequestBody List<String> deptIds) {
        if (deptIds.size() == 0) {
            return null;
        }
        List<AreaInfo> areaInfoList = areaInfoService.selectAreaInfoByDeptIds(deptIds);
        return ResultUtils.success(areaInfoList);
    }

    /**
     * 创建导出区域列表任务
     *
     * @param exportDto 传入信息
     * @return 创建任务结果
     */
    @PostMapping("/exportData")
    public Result exportData(@RequestBody ExportDto<AreaInfoDto> exportDto) {
        if (!exportDto.checkParam()) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        return areaInfoService.exportArea(exportDto);
    }

    /**
     * 查询对外区域列表
     * 用户id集合
     *
     * @return 树结构区域列表
     */
    @PostMapping("/selectForeignAreaInfoForPageSelection")
    public Result selectForeignAreaInfoForPageSelection(@RequestBody List<String> userIds) {
        return areaInfoService.selectForeignAreaInfo(userIds);
    }

    /**
     * 查询对外区域列表
     *
     * @return 平行区域列表
     */
    @GetMapping("/selectSimultaneousForPageSelection")
    public Result selectSimultaneousForPageSelection() {
        return areaInfoService.selectSimultaneousForeignAreaInfo();
    }

    /**
     * 根据部门id集合获取区域id集合
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaInfoIdsByDeptIds")
    public List<String> selectAreaIdsByDeptIds(@RequestBody List<String> deptIds) {
        if (deptIds.size() == 0) {
            return null;
        }
        return areaInfoService.selectAreaIdsByDeptIds(deptIds);
    }

    /**
     * 根据部门id集合获取区域部门关系集合
     *
     * @param deptIds 部门id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaDeptInfoByDeptIds")
    public List<AreaDeptInfo> selectAreaDeptInfoByDeptIds(@RequestBody List<String> deptIds) {
        if (deptIds.size() == 0) {
            return null;
        }
        return areaInfoService.selectAreaDeptInfosByDeptIds(deptIds);
    }

    /**
     * 根据区域id集合获取区域部门关系集合
     *
     * @param areaIds 区域id集合
     * @return 返回查询结果
     */
    @PostMapping("/selectAreaDeptInfoByAreaIdsForPageSelection")
    public Result selectAreaDeptInfoByAreaIdsForPageSelection(@RequestBody List<String> areaIds) {
        if (areaIds.size() == 0) {
            return ResultUtils.warn(AreaResultCode.PARAM_NULL, I18nUtils.getSystemString(AreaI18nConstant.PARAM_NULL));
        }
        return areaInfoService.selectAreaDeptInfoByAreaIds(areaIds);
    }

    /**
     * 通过区域id  获取公共部门信息
     *
     * @param areaIds areaIds
     * @return Result
     */
    @PostMapping("/getCommonDeptByAreaId")
    public Result getCommonDeptByAreaId(@RequestBody List<String> areaIds) {
        return ResultUtils.success(areaInfoService.queryDeptCommonByAreaId(areaIds));
    }

    @GetMapping("/test")
    public String addAreaTest() {
        AreaInfo areaInfo = new AreaInfo();
        for (int i = 493; i < 2840; i++) {
            areaInfo.setAreaName("大数据测试区域" + i);
            areaInfo.setRemarks("大数据测试区域");
            //放入区域id 如果造设施关联的区域 id为gis开头 从gis01-gis30
            areaInfo.setAreaId(NineteenUUIDUtils.uuid());
            areaInfoService.addArea(areaInfo);
            System.out.println("新增大数据测试区域" + i);
        }
        return "新增成功";
    }
}
