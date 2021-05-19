package com.fiberhome.filink.filinklockapi.bean;

import lombok.Data;

import java.util.List;

/**
 * 删除主控
 *
 * @author CongcaiYu
 */
@Data
public class ControlDel {

    /**
     * 主控id
     */
    private List<String> hostIdList;

    /**
     * 操作 1:更新 2:删除
     */
    private String operate;
}
