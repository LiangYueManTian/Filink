package com.fiberhome.filink.onenetserver.operation;



import com.fiberhome.filink.onenetserver.bean.device.BaseCommonEntity;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import com.fiberhome.filink.onenetserver.utils.HttpSendCenter;
import org.json.JSONObject;

/**
 * <p>
 *   oneNet平台订阅实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
public class ObserveOpe extends BaseOpe {
    public ObserveOpe(String accessKey) {
        super(accessKey);
    }
    @Override
    public OneNetResponse operation(BaseCommonEntity baseCommonEntity, JSONObject body) {
        return HttpSendCenter.postNotBody(accessKey, baseCommonEntity.toUrl());
    }
}
