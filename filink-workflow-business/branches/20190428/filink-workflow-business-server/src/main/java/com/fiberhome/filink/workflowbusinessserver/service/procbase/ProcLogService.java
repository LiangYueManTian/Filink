package com.fiberhome.filink.workflowbusinessserver.service.procbase;

import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcBase;
import com.fiberhome.filink.workflowbusinessserver.resp.app.procbase.ProcBaseRespForApp;

import java.util.List;

/**
 * 工单日志service
 * @author hedongwei@wistronits.com
 * @date 2019/4/24 13:21
 */

public interface ProcLogService {
    /**
     * 新增工单操作日志参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/11 23:03
     * @param procBase 工单参数
     * @param functionCode 功能项
     * @param dataOptType 数据操作
     * @return 新增工单操作日志参数
     */
    AddLogBean getAddProcOperateLogParam(ProcBase procBase, String functionCode, String dataOptType);



    /**
     * 获取app操作日志的参数
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 8:51
     * @param procBase 工单主表
     * @param functionCode 功能项编码
     * @param dataOptType 数据操作类型
     * @return app操作日志的参数
     */
    AddLogBean getAddProcOperateLogParamForApp(ProcBase procBase, String functionCode, String dataOptType);

    /**
     * app批量新增操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/4/16 9:30
     * @param procBaseList 工单集合
     */
    void addOperateLogBatchForApp(List<ProcBaseRespForApp> procBaseList);

    /**
     * 删除工单操作日志
     * @author hedongwei@wistronits.com
     * @date  2019/4/24 13:44
     * @param list 工单集合
     */
    void insertDeleteProcLog(List list);

    /**
     * 列表导出记录日志
     *
     * @param exportDto 导出dto
     *
     * @param functionCode 功能项编码
     */
    void addLogByExport(ExportDto exportDto, String functionCode);

}
