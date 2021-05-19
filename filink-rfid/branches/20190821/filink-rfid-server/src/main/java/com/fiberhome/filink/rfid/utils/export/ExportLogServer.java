package com.fiberhome.filink.rfid.utils.export;

import com.fiberhome.filink.exportapi.bean.ExportDto;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.rfid.constant.CommonConstant;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;

/**
 * <p>
 * 导出日志公共服务
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019/6/21
 */
public class ExportLogServer {
    /**
     * 保存导出日志
     *
     * @param exportDto 导出类
     * @return Boolean
     */
    public static Boolean addLogByExport(ExportDto exportDto, String functionCode,LogProcess logProcess,SystemLanguageUtil systemLanguageUtil) {
        Boolean flag = false;
        systemLanguageUtil.querySystemLanguage();
        String logType = LogConstants.LOG_TYPE_OPERATE;
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId(CommonConstant.EXPORT);
        addLogBean.setDataName(CommonConstant.LIST_NAME);
        //获得操作对象id
        addLogBean.setOptObjId(CommonConstant.EXPORT);
        //操作为导出
        addLogBean.setDataOptType(CommonConstant.EXPORT);
        addLogBean.setOptObj(exportDto.getListName());
        addLogBean.setFunctionCode(functionCode);
        //新增操作日志
        logProcess.addOperateLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
        return flag;
    }

}
