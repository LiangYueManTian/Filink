package com.fiberhome.filink.logserver.controller;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.logserver.bean.FilterTemplate;
import com.fiberhome.filink.logserver.constant.I18nConstants;
import com.fiberhome.filink.logserver.service.TemplateService;
import com.fiberhome.filink.logserver.utils.LogResultCode;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 查询模板控制器
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/31
 */
@RestController
@RequestMapping("/logTemplate")
public class TemplateController {

    /**
     * 注入逻辑层
     */
    @Autowired
    private TemplateService templateService;

    /**
     * 新增模板
     * @param filterTemplate 模板
     * @return 新增结果
     */
    @PostMapping("/insert")
    public Result insertTemplate(@RequestBody FilterTemplate filterTemplate) {
        if (null == filterTemplate || filterTemplate.getName() == null) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return templateService.insertTemplate(filterTemplate);
    }

    /**
     * 更新模板
     * @param filterTemplate 模板
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result updateTemplate(@RequestBody FilterTemplate filterTemplate) {
        if (null == filterTemplate || filterTemplate.getName() == null) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return templateService.updateTemplate(filterTemplate);
    }

    /**
     * 删除模板
     * @param filterTemplate 模板
     * @return 删除结果
     */
    @PostMapping("/delete")
    public Result deleteTemplate(@RequestBody FilterTemplate filterTemplate) {
        if (null == filterTemplate || filterTemplate.getId() == null) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return templateService.deleteTemplate(filterTemplate);
    }
    /**
     * 查询模板
     *
     * @param id 模板id
     * @return 查询结果
     */
    @GetMapping("/query/{id}")
    public Result queryTemplate(@PathVariable("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return templateService.queryTemplate(id);
    }

    /**
     * 查询模板列表
     *
     * @param queryCondition 查询条件
     * @return 查询结果
     */
    @PostMapping("/queryList")
    public Result queryList(@RequestBody QueryCondition queryCondition){
        //参数校验
        if (queryCondition == null || queryCondition.getPageCondition() == null || queryCondition.getFilterConditions() == null) {
            return ResultUtils.warn(LogResultCode.PARAM_NULL, I18nUtils.getSystemString(I18nConstants.PARAM_NULL));
        }
        return templateService.queryList(queryCondition);
    }

}
