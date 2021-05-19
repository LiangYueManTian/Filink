package com.fiberhome.filink.onenetserver.sender;

import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import com.fiberhome.filink.onenetserver.bean.device.Write;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetConnectParams;
import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import com.fiberhome.filink.onenetserver.exception.FilinkOneNetException;
import com.fiberhome.filink.onenetserver.operation.WriteOpe;
import com.fiberhome.filink.onenetserver.utils.HostUtil;
import com.fiberhome.filink.onenetserver.utils.OneNetToken;
import com.fiberhome.filink.onenetserver.utils.ProductInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 指令下发方法
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class SendUtil {
    /**
     * 自动注入
     */
    @Autowired
    private ProductInfoUtil productInfoUtil;
    @Autowired
    private HostUtil hostUtil;
    /**I
     * SPO标准中的Object ID
     */
    @Value("${oneNet.device.obj_id}")
    private Integer objId;
    /**
     * ISPO标准中的Object Instance ID
     */
    @Value("${oneNet.device.obj_inst_id}")
    private Integer objInstId;
    /**
     * ISPO标准中的Resource ID
     */
    @Value("${oneNet.device.res_id}")
    private Integer resId;
    /**
     * write的模式，只能是1或者2
     */
    @Value("${oneNet.device.mode}")
    private Integer mode;
    /**
     * 请求超时时间,不填写平台默认25(单位：秒)，取值范围[5, 40]
     */
    @Value("${oneNet.device.timeout}")
    private Integer timeout;
    /**
     * 发送请求到平台
     *
     * @param data               消息
     * @param oneNetConnectParams 请求参数
     */
    public void sendData(FiLinkOneNetConnectParams oneNetConnectParams, String data) {
        String productId = oneNetConnectParams.getAppId();
        String imei = oneNetConnectParams.getImei();
        log.info("oneNet connect id : {}", productId);
        if (StringUtils.isEmpty(productId)) {
            log.error("send data oneNet id is null>>>");
            return;
        }
        //根据产品ID获取秘钥
        String accessKey = productInfoUtil.findAccessKeyByProductId(productId);
        // 通过产品ID访问产品API
        String resourceName = "products/" + productId;
        //用户自定义token过期时间
        String expirationTime = String.valueOf(System.currentTimeMillis() + 1800);
        try {
            byte[] bytes = HexUtil.hexStringToByte(data);
            //将数据进行base64加密
            String base64Str = new String(Base64.encodeBase64(bytes));
            log.info("send message to oneNet : {}", base64Str);
            String token = OneNetToken.assembleToken(
                    OneNetHttpConstants.VERSION, resourceName, expirationTime, accessKey);
            Write write = new Write(imei, mode, timeout, objId, objInstId, resId, base64Str);
            //获取oneNet域名
            write.setHost(hostUtil.getOneNetHost());
            WriteOpe writeOpe = new WriteOpe(token);
            OneNetResponse oneNetResponse = writeOpe.operation(write, write.toJsonObject());
            log.info("Write Device, response errorNo: {}, error: {}",
                    oneNetResponse.getErrorno(), oneNetResponse.getError());
        } catch (Exception e) {
            log.error("send to OneNET error>>>", e);
            throw new FilinkOneNetException(e.getMessage());
        }
    }
}
