package com.fiberhome.filink.onenetserver.bean.device;



import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *   oneNet平台删除设备实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DeleteDevice extends BaseCommonEntity {
    /**设备ID 必填*/
    private String deviceId;
    /**空构造函数*/
    public DeleteDevice() {}
    /**
     * 空构造函数
     * @param deviceId 设备ID 必填
     * @param productId 产品ID，必填参数
     * @param accessKey 核心秘钥，必填参数
     */
    public DeleteDevice(String deviceId, String productId, String accessKey) {
        this.deviceId = deviceId;
        this.productId = productId;
        this.accessKey = accessKey;
    }
    /**
     * 校验必填参数是否有空值
     * @return true有 false没有
     */
    public boolean check()  {
        return StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(productId);
    }
    /**
     * 输出 url
     *
     * @return url
     */
    @Override
    public String toUrl() {
        return host + "/devices/" + deviceId;
    }
}
