package com.fiberhome.filink.dprotocol.bean.xmlBean.data;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * xml响应帧实体类
 * @author CongcaiYu
 */
@Data
public class DataResponse implements Serializable {

    /**
     * data响应参数集合
     */
    private List<DataParamsChild> dataParams;

}
