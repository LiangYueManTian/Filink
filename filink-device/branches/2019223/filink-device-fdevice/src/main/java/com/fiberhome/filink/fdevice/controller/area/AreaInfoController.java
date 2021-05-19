package com.fiberhome.filink.fdevice.controller.area;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.fdevice.bean.area.AreaI18n;
import com.fiberhome.filink.fdevice.bean.area.AreaInfo;
import com.fiberhome.filink.fdevice.exception.FilinkAreaDateFormatException;
import com.fiberhome.filink.fdevice.service.area.AreaInfoService;
import com.fiberhome.filink.fdevice.utils.AreaRusultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
            return ResultUtils.warn(AreaRusultCode.AREA_NAME_NULL, I18nUtils.getString(AreaI18n.AREA_NAME_NULL));
        }
        if (!areaInfo.checkAreaName()) {
            return ResultUtils.warn(AreaRusultCode.AREA_NAME_FORMAT_IS_INCORRECT, I18nUtils.getString(AreaI18n.AREA_NAME_FORMAT_IS_INCORRECT));
        }
        return areaInfoService.queryAreaNameIsExist(areaInfo);
    }


    /**
     * 查询区域细节是否可以进行修改
     *
     * @param areaId 区域id
     * @return 查询结果
     */
    @GetMapping("/queryAreaDetailsCanChange/{areaId}")
    public Result queryAreaDetailsCanChange(@PathVariable String areaId) {
        if (StringUtils.isEmpty(areaId)) {
            return ResultUtils.warn(AreaRusultCode.PARAM_NULL, I18nUtils.getString(AreaI18n.PARAM_NULL));
        }
        Boolean flag = areaInfoService.queryAreaDetailsCanChange(areaId);
        if (flag) {
            return ResultUtils.success();
        } else {
            return ResultUtils.warn(AreaRusultCode.FALSE);
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
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader("userId");
        areaInfo.setCreateUser(userId);
        String areaName = areaInfo.getAreaName();
        //参数格式化 当前去空格
        areaInfo.parameterFormat();
        if (StringUtils.isEmpty(areaName)) {
            return ResultUtils.warn(AreaRusultCode.AREA_NAME_NULL, I18nUtils.getString(AreaI18n.AREA_NAME_NULL));
        }
        if (!areaInfo.checkAreaName()) {
            return ResultUtils.warn(AreaRusultCode.AREA_NAME_FORMAT_IS_INCORRECT, I18nUtils.getString(AreaI18n.AREA_NAME_FORMAT_IS_INCORRECT));
        }
        if (!areaInfo.checkParameterFormat()) {
            throw new FilinkAreaDateFormatException();
        }
        return areaInfoService.addArea(areaInfo);
    }

    /**
     * 获取区域列表
     *
     * @param queryCondition 查询条件
     * @return 区域信息
     */
    @PostMapping("/areaListByPage")
    public Result areaListByPage(@RequestBody QueryCondition queryCondition) {
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
            return ResultUtils.warn(AreaRusultCode.PARAM_NULL, I18nUtils.getString(AreaI18n.PARAM_NULL));
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
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userId = request.getHeader("userId");
        areaInfo.setUpdateUser(userId);
        String areaName = areaInfo.getAreaName();
        //参数格式化 当前去空格
        areaInfo.parameterFormat();
        if (StringUtils.isEmpty(areaName)) {
            return ResultUtils.warn(AreaRusultCode.AREA_NAME_NULL, I18nUtils.getString(AreaI18n.AREA_NAME_NULL));
        }
        if (!areaInfo.checkAreaName()) {
            return ResultUtils.warn(AreaRusultCode.AREA_NAME_FORMAT_IS_INCORRECT, I18nUtils.getString(AreaI18n.AREA_NAME_FORMAT_IS_INCORRECT));
        }
        String areaId = areaInfo.getAreaId();
        if (StringUtils.isEmpty(areaId)) {
            return ResultUtils.warn(AreaRusultCode.PARAM_NULL, I18nUtils.getString(AreaI18n.PARAM_NULL));
        }
        if (!areaInfo.checkParameterFormat()) {
            throw new FilinkAreaDateFormatException();
        }
        return areaInfoService.updateAreaInfo(areaInfo);
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
            return ResultUtils.warn(AreaRusultCode.PARAM_NULL, I18nUtils.getString(AreaI18n.PARAM_NULL));
        }
        Boolean flag = areaInfoService.setAreaDevice(map);
        if (flag) {
            return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getString(AreaI18n.ASSOCIATED_FACILITIES_SUCCESSFUL));
        }
        return ResultUtils.warn(AreaRusultCode.RELATED_FACILITIES_FAIL, I18nUtils.getString(AreaI18n.RELATED_FACILITIES_FAIL));
    }

    /**
     * 删除区域信息
     *
     * @param areaIds 区域id集合
     * @return 删除结果
     */
    @PostMapping("/deleteAreaByIds")
    public Result deleteAreaByIds(@RequestBody List<String> areaIds) {
        if (areaIds == null || areaIds.size() == 0) {
            return ResultUtils.warn(AreaRusultCode.PARAM_NULL, I18nUtils.getString(AreaI18n.PARAM_NULL));
        }
        return areaInfoService.deleteAreaByIds(areaIds);
    }

    /**
     * 首页获取区域列表
     *
     * @return 区域信息
     */
    @GetMapping("/queryAreaListAll")
    public Result queryAreaListAll() {
        return areaInfoService.queryAreaListAll();
    }

    /**
     * 删除区域部门关系
     *
     * @param deptIds 传入参数
     * @return 删除结果
     */
    @PostMapping("/deleteAreaDeptRelation")
    public Boolean deleteAreaDeptRelation(@RequestBody List<String> deptIds) {
        return areaInfoService.deleteAreaDeptRelation(deptIds);
    }
}
