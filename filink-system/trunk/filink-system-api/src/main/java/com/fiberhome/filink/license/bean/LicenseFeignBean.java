package com.fiberhome.filink.license.bean;

import com.fiberhome.filink.license.enums.OperationTarget;
import com.fiberhome.filink.license.enums.OperationWay;
import lombok.Data;

/**
 * 增删feign调用类
 *
 * @author zepenggao@wistronits.com
 * @date 2019/2/20 16:49
 */
@Data
public class LicenseFeignBean {
    /**
     * 操作数量
     */
    public int num;

    /**
     * 操作对象(user, online, device)
     */
    public OperationTarget operationTarget;

    /**
     * 操作方式(add, delete)
     */
    public OperationWay operationWay;

    public LicenseFeignBean() {}

    public LicenseFeignBean(int num, OperationTarget operationTarget, OperationWay operationWay) {
        this.num = num;
        this.operationTarget = operationTarget;
        this.operationWay = operationWay;
    }
}
