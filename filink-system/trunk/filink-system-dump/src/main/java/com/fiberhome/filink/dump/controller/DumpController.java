package com.fiberhome.filink.dump.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpParam;
import com.fiberhome.filink.dump.constant.DumpI18n;
import com.fiberhome.filink.dump.constant.DumpResultCode;
import com.fiberhome.filink.dump.service.DumpService;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 存储策略控制器
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/29
 */
@RestController
@RequestMapping("/dump")
public class DumpController {

    /**
     * 存储策略逻辑层
     */
    @Autowired
    private DumpService dumpService;

    /**
     * 更新存储策略
     *
     * @param dumpParam 策略
     * @return 结果
     */
    @PostMapping("/update")
    public Result updateDump(@RequestBody DumpParam dumpParam) {
        //检验参数
        if (dumpParam == null
                || dumpParam.getParamId() == null
                || dumpParam.getParamType() == null
                || dumpParam.getDumpBean() == null) {
            return ResultUtils.warn(DumpResultCode.DUMP_PRAMS_ERROR, I18nUtils.getSystemString(DumpI18n.DUMP_PRAMS_ERROR));
        }
        return dumpService.updateDump(dumpParam);
    }

    /**
     * 查询转储策略
     *
     * @param type 类型
     * @return 转储策略
     */
    @GetMapping("/feign/{type}")
    public DumpBean queryDump(@PathVariable String type) {
        return dumpService.queryDump(type);
    }
}
