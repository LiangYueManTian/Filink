package com.fiberhome.filink.protocol.bean.xmlBean.header;


import com.fiberhome.filink.protocol.bean.xmlBean.data.DataParamsChild;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * filink协议实体类
 * @author CongcaiYu
 */
@Data
public class ResponseHeader implements Serializable {

    /**
     * 成功应答
     */
    private List<DataParamsChild> successResponse;
    /**
     * 失败应答
     */
    private List<DataParamsChild> failedResponse;

}
