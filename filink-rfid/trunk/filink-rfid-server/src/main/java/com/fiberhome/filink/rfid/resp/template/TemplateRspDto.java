package com.fiberhome.filink.rfid.resp.template;

import com.fiberhome.filink.rfid.bean.template.TemplateVO;
import lombok.Data;

import java.util.List;

/**
 * 模板rsp DTO
 *
 * @author liyj
 * @date 2019/6/2
 */
@Data
public class TemplateRspDto extends TemplateVO {

    /**
     * 模板List 给前台使用
     */
    private List<TemplateRspDto> childTemplateList;

    @Override
    public String toString() {
        return "TemplateRspDto{" +
                "childTemplateList=" + childTemplateList +
                '}';
    }
}
