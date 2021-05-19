package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkControlException;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 配置部署状态实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/17
 */
@Data
public class SetDeployStatusBean {
    /**
     * 设施序列id集合
     */
    private List<String> deviceIds;

    /**
     * 部署状态
     */
    private String deployStatus;

    public void checkDepoyStatusBean() {
        if (this == null
                || this.getDeviceIds() == null
                || this.getDeviceIds().size() == 0
                || this.getDeployStatus() == null) {
            throw new FiLinkControlException("deviceIds or deployStaus is null>>");
        }
    }
}
