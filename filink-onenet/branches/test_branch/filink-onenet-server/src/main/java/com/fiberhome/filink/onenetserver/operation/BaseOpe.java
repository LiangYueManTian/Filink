package com.fiberhome.filink.onenetserver.operation;



import com.fiberhome.filink.onenetserver.bean.device.BaseCommonEntity;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import lombok.Data;
import org.json.JSONObject;

/**
 * <p>
 *   oneNet平台通用操作实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
public abstract class BaseOpe {
    protected String accessKey;
    public BaseOpe(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * 操作
     * @param baseCommonEntity 通用信息
     * @param body 参数信息
     * @return 返回参数
     */
    public abstract OneNetResponse operation(BaseCommonEntity baseCommonEntity, JSONObject body);
}
