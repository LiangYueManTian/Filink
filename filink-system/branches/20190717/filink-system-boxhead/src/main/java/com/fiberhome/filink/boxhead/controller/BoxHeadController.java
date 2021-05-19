package com.fiberhome.filink.boxhead.controller;


import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.boxhead.bean.BoxHead;
import com.fiberhome.filink.boxhead.constant.BoxHeadI18n;
import com.fiberhome.filink.boxhead.constant.BoxHeadResultCode;
import com.fiberhome.filink.boxhead.service.BoxHeadService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wanzhao zhang
 * @since 2019-05-21
 */
@RestController
@RequestMapping("/boxHead")
public class BoxHeadController {
    /**
     * 注入逻辑层
     */
    @Autowired
    private BoxHeadService boxHeadService;

    /**
     * 根据用户id查询表头栏设置
     *
     * @return 查询结果
     */
    @GetMapping("/query")
    public Result queryBoxHead() {
        List<BoxHead> boxHeadList = boxHeadService.queryBoxHead();
        return ResultUtils.success(boxHeadList);
    }

    /**
     * 保存表头设置
     *
     * @param boxHead
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result saveBoxHead(@RequestBody BoxHead boxHead) {
        if(boxHead == null || boxHead.getMenuId() == null || boxHead.getCustom() == null){
            return ResultUtils.warn(BoxHeadResultCode.BOX_HEAD_PARAM_EMPTY,I18nUtils.getSystemString(BoxHeadI18n.BOX_HEAD_PARAM_EMPTY));
        }
        return boxHeadService.saveBoxHead(boxHead);
    }

}
