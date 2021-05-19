package com.fiberhome.filink.dump.service;

import com.fiberhome.filink.dump.bean.DumpBean;
import com.fiberhome.filink.dump.bean.DumpData;
import com.fiberhome.filink.dump.bean.ExportDto;

/**
 * 转储逻辑层接口
 * @author hedongwei@wistronits.com
 * @date 2019/7/6 20:47
 */

public interface DumpService {

    /**
     * 查询转储数据
     * @author hedongwei@wistronits.com
     * @date  2019/7/6 21:03
     * @param dumpBean 转储实体类
     * @param dumpData 转储数据
     * @param exportDto 导出数据
     *
     */
    void searchDumpData(DumpBean dumpBean, DumpData dumpData, ExportDto exportDto);
}
