package com.fiberhome.filink.commonstation.entity.xmlbean.data;

import lombok.Data;

import java.util.List;

/**
 * xml循环体
 * @author CongcaiYu
 */
@Data
public class ForEach extends DataParamsChild {
    /**
     * 引用id
     */
    private String referenceId;
    /**
     * 参数集合
     */
    private List<DataParamsChild> itemList;

}
