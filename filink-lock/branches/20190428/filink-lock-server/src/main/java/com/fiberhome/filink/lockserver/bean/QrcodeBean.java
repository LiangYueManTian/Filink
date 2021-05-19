package com.fiberhome.filink.lockserver.bean;

import com.fiberhome.filink.lockserver.exception.FiLinkLockException;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 二维码实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/1
 */
@Data
public class QrcodeBean {
    /**
     * 二维码
     */
    private String qrcode;


    /**
     * 检查二维码是否为空
     */
    public void checkQrcode() {
        if (StringUtils.isEmpty(this) || StringUtils.isEmpty(this.getQrcode())) {
            throw new FiLinkLockException("qrcode  is null>>>>>>>");
        }
    }


}
