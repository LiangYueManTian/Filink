package com.fiberhome.filink.userserver.controller;


import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.userserver.bean.Tempauth;
import com.fiberhome.filink.userserver.bean.TempAuthParameter;
import com.fiberhome.filink.userserver.service.TempauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 临时授权表 前端控制器
 * </p>
 *
 * @author xgong103@fiberhome.com
 * @since 2019-03-25
 */
@RestController
@RequestMapping("/tempauth")
public class TempauthController {

    @Autowired
    private TempauthService tempauthService;

    /**
     * 条件查询临时授权信息
     *
     * @param queryCondition 临时授权的条件类
     * @return 临时授权列表
     */
    @PostMapping("/queryTempAuthByCondition")
    public Result queryTempAuthByCondition(@RequestBody QueryCondition<TempAuthParameter> queryCondition) {

        return tempauthService.queryTempAuthByCondition(queryCondition);
    }

    /**
     * 单个审核临时授权信息
     *
     * @param tempauth 临时授权信息
     * @return 审核的结果
     */
    @PostMapping("/audingTempAuth")
    public Result audingTempAuthById(@RequestBody Tempauth tempauth) {

        return tempauthService.audingTempAuthById(tempauth);
    }

    /**
     * 批量审核临时授权信息
     *
     * @param tempAuthParameter 待审核临时授权信息参数类
     * @return 审核结果
     */
    @PostMapping("/batchAudingTempAuthByIds")
    public Result batchAudingTempAuthByIds(@RequestBody TempAuthParameter tempAuthParameter) {

        Tempauth tempauth = new Tempauth();
        tempauth.setAuditingDesc(tempAuthParameter.getAuditingDesc());
        tempauth.setAuthStatus(tempAuthParameter.getAuthStatus());
        String[] idList = tempAuthParameter.getIdList();

        return tempauthService.batchAudingTempAuthByIds(idList, tempauth,tempAuthParameter.getUserIdList());
    }

    /**
     * 根据指定id删除临时授权信息
     *
     * @param id 待删除的临时授权id
     * @return 删除结果
     */
    @GetMapping("/deleteTempAuthById/{id}")
    public Result deleteTempAuthById(@PathVariable("id") String id) {

        return tempauthService.deleteTempAuthById(id);
    }

    /**
     * 批量删除临时授权信息
     *
     * @param ids 待删除的临时授权id数组
     * @return 删除的结果
     */
    @PostMapping("/batchDeleteTempAuth")
    public Result batchDeleteTempAuth(@RequestBody String[] ids) {

        return tempauthService.batchDeleteTempAuth(ids);
    }

    /**
     * 添加临时授权信息
     *
     * @param tempauth 临时授权信息
     * @return 添加的结果
     */
    @PostMapping("/addTempAuth")
    public Result addTempAuth(@RequestBody Tempauth tempauth) {

        return tempauthService.addTempAuth(tempauth);
    }

    /**
     * 根据id查询临时授权信息
     * @param id
     * @return
     */
    @GetMapping("/queryTempAuthById/{id}")
    public Result queryTempAuthById(@PathVariable("id") String id){

        return tempauthService.queryTempAuthById(id);
    }
}
