package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * 查询电子锁主控信息 请求对象
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/1
 */
@Data
public class ControlReq implements Serializable {

    /**
     * 设施ID
     */
    private String deviceId;
    /**
     * 主控ID
     */
    private String controlId;

    /**
     * 设施id不能
     */
    public void checkDeviceIdIsNull() {
        if (null == this || StringUtils.isEmpty(this.getDeviceId())) {
            throw new FiLinkLockException("deviceId is empty>>>>>>");
        }
    }

    /**
     * 主控id不能为空
     */
    public void checkControlIdIsNull() {
        if (null == this || StringUtils.isEmpty(this.getControlId())) {
            throw new FiLinkControlException("controlId is empty>>>>>>");
        }
    }
}
