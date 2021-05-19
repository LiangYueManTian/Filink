package com.fiberhome.filink.dump.service;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpParam;

/**
 * <p>
 * 转储策略逻辑层
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/5/29
 */
public interface DumpService {
    /**
     * 更新转存策略
     *
     * @param dumpParam 策略参数
     * @return 结果
     */
    Result updateDump(DumpParam dumpParam);

    /**
     * 查询转储策略
     *
     * @param type 类型
     * @return 转储策略
     */
    DumpBean queryDump(String type);
}
