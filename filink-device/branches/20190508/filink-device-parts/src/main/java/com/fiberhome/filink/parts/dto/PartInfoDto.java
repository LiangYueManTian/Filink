package com.fiberhome.filink.parts.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fiberhome.filink.exportapi.utils.ExportApiUtils;
import com.fiberhome.filink.parts.constant.PartType;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/13 11:10
 */
@Data
public class PartInfoDto {
    private String partId;

    private String partName;

    private String partType;

    private String partTypeName;

    private String partCode;

    private String remark;

    private String trustee;

    private String isDeleted;

    private String createUser;

    private String updateUser;

    private String department;

    private Timestamp createTime;

    private Timestamp updateTime;

    private long cTime;

    private long uTime;

    private List<String> accountabilityUnit;

    @JsonIgnore
    public String getTranslationCtime() {
        return ExportApiUtils.getZoneTime(cTime);
    }

    @JsonIgnore
    public String getTranslationUtime() {
        return ExportApiUtils.getZoneTime(uTime);
    }


    public long getCtime() {
        return cTime;
    }

    public void setCtime(long cTime) {
        this.cTime = cTime;
    }

    public long getUtime() {
        return uTime;
    }

    public void setUtime(long uTime) {
        this.uTime = uTime;
    }

    @JsonIgnore
    public String getTranslationPartType() {
        return I18nUtils.getString(ExportApiUtils.getExportLocales(),PartType.getMsg(partType));
    }
}
