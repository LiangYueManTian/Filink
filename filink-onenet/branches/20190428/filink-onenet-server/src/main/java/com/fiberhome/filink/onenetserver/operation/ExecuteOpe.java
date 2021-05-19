package com.fiberhome.filink.onenetserver.operation;



import com.fiberhome.filink.onenetserver.bean.device.BaseCommonEntity;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import com.fiberhome.filink.onenetserver.utils.HttpSendCenter;
import org.json.JSONObject;

/**
 * <p>
 *   oneNet平台下发命令实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class ExecuteOpe extends BaseOpe {
    public ExecuteOpe(String accessKey) {
        super(accessKey);
    }

    @Override
    public OneNetResponse operation(BaseCommonEntity baseCommonEntity, JSONObject body) {
        return HttpSendCenter.post(accessKey, baseCommonEntity.toUrl(), body);
    }
}
