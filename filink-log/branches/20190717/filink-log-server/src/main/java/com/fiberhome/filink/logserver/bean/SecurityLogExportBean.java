package com.fiberhome.filink.logserver.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.logserver.utils.ExportLogI18nCast;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全日志导出类
 * @author hedongwei@wistronits.com
 * @date 2019/5/27 10:59
 */
@Data
public class SecurityLogExportBean extends SecurityLog {

    /**
     * 获取导出显示的危险名称信息
     */
    @JsonIgnore
    public String getTranslationDangerLevel() {
        if (!ObjectUtils.isEmpty(this.getDangerLevel())) {
            //获取危险名称
            return ExportLogI18nCast.getDangerLevel(this.getDangerLevel());
        } else {
            return "";
        }
    }

    /**
     * 获取操作类型
     */
    @JsonIgnore
    public String getTranslationOptType() {
        if (!ObjectUtils.isEmpty(this.getOptType())) {
            //获取操作类型
            return ExportLogI18nCast.getOptType(this.getOptType());
        } else {
            return "";
        }
    }

    /**
     * 获取操作结果
     */
    @JsonIgnore
    public String getTranslationOptResult() {
        if (!ObjectUtils.isEmpty(this.getOptResult())) {
            //获取操作结果
            return ExportLogI18nCast.getOptResult(this.getOptResult());
        } else {
            return "";
        }
    }


    /**
     * 获取安全日志导出实体类
     * @author hedongwei@wistronits.com
     * @date  2019/5/27 11:22
     * @param securityLogList 安全日志集合
     * @return 获取安全日志导出实体类
     */
    public static List<SecurityLogExportBean> getSecurityLogExportBeanForSecurityLog(List<SecurityLog> securityLogList) {
        List<SecurityLogExportBean> exportBeanList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(securityLogList)) {
            SecurityLogExportBean exportBean = new SecurityLogExportBean();
            for (SecurityLog securityLog : securityLogList) {
                exportBean = new SecurityLogExportBean();
                BeanUtils.copyProperties(securityLog, exportBean);
                exportBeanList.add(exportBean);
            }
        }
        return exportBeanList;
    }

}
