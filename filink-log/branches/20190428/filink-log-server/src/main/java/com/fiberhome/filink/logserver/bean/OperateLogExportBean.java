package com.fiberhome.filink.logserver.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.logserver.utils.LogI18nCast;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志导出类
 * @author hedongwei@wistronits.com
 * @date 2019/5/27 10:59
 */
@Data
public class OperateLogExportBean extends OperateLog {

    /**
     * 获取导出显示的危险名称信息
     */
    @JsonIgnore
    public String getTranslationDangerLevel() {
        if (!ObjectUtils.isEmpty(this.getDangerLevel())) {
            //获取危险名称
            return LogI18nCast.getDangerLevel(this.getDangerLevel());
        } else {
            return "";
        }
    }


    /**
     * 获取操作日志导出实体类
     * @author hedongwei@wistronits.com
     * @date  2019/5/27 11:22
     * @param operateLogList 操作日志集合
     * @return 获取操作日志导出实体类
     */
    public static List<OperateLogExportBean> getOperateLogExportBeanForOperateLog(List<OperateLog> operateLogList) {
        List<OperateLogExportBean> exportBeanList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(operateLogList)) {
            OperateLogExportBean exportBean = new OperateLogExportBean();
            for (OperateLog operateLog : operateLogList) {
                exportBean = new OperateLogExportBean();
                BeanUtils.copyProperties(operateLog, exportBean);
                exportBeanList.add(exportBean);
            }
        }
        return exportBeanList;
    }

}
