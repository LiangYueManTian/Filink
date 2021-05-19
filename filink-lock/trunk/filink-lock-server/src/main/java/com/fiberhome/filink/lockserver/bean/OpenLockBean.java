package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 开锁请求对象
 *
 * @author CongcaiYu
 */
@Data
public class OpenLockBean {


    /**
     * 门编号
     */
    private String doorNum;

    /**
     * 设施id
     */
    private String deviceId;

    /**
     * 锁具编号集合
     */
    private List<String> doorNumList;

    /**
     * 检验pda通过设施id和门编号开锁传的参数
     */
    public void checkOpenLockBeanForPda() {
        if (this == null || this.getDeviceId() == null || this.getDoorNum() == null) {
            throw new FiLinkLockException("deviceId or doorNum is null>>>>>>>");
        }
    }

    /**
     * 检验网管通过设施id和门编号集合开锁传的参数
     */
    public void checkOpenLockBean() {
        if (this == null
                || StringUtils.isEmpty(this.getDeviceId())
                || this.getDoorNumList() == null
                || this.getDoorNumList().size() == 0) {
            throw new FiLinkLockException("deviceId or doorNumList is null>>>>>>>");
        }
    }
}
