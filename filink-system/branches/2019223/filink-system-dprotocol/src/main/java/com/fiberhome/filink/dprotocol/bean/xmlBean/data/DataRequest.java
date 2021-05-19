package com.fiberhome.filink.dprotocol.bean.xmlBean.data;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * xml请求帧实体类
 * @author CongcaiYu
 */
@Data
public class DataRequest implements Serializable {

    /**
     * data请求参数集合
     */
    private List<DataParamsChild> dataParams;

}
