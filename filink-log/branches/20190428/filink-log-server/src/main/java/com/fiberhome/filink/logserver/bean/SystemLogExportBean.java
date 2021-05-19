package com.fiberhome.filink.logserver.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.logserver.utils.LogI18nCast;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统日志导出类
 * @author hedongwei@wistronits.com
 * @date 2019/5/27 10:59
 */
@Data
public class SystemLogExportBean extends SystemLog {

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
     * 获取系统日志导出实体类
     * @author hedongwei@wistronits.com
     * @date  2019/5/27 11:22
     * @param systemLogList 系统日志集合
     * @return 获取系统日志导出实体类
     */
    public static List<SystemLogExportBean> getSystemLogExportBeanForSystemLog(List<SystemLog> systemLogList) {
        List<SystemLogExportBean> exportBeanList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(systemLogList)) {
            SystemLogExportBean exportBean = new SystemLogExportBean();
            for (SystemLog systemLog : systemLogList) {
                exportBean = new SystemLogExportBean();
                BeanUtils.copyProperties(systemLog, exportBean);
                exportBeanList.add(exportBean);
            }
        }
        return exportBeanList;
    }

}
