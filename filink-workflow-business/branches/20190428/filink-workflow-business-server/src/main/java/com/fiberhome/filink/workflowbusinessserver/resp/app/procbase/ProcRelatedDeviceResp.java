package com.fiberhome.filink.workflowbusinessserver.resp.app.procbase;

import com.fiberhome.filink.workflowbusinessserver.bean.procbase.ProcRelatedDevice;
import lombok.Data;

/**
 * <p>
 * app工单设施关系返回类
 * </p>
 *
 * @author chaofanrong@wistronits.com
 * @since 2019-04-15
 */
@Data
public class ProcRelatedDeviceResp extends ProcRelatedDevice {

    /**
     * 地址
     */
    private String address;

    /**
     * 经纬度
     */
    private String positionBase;

}
