package com.fiberhome.filink.onenetserver.sender;

import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.filinklockapi.feign.ControlFeign;
import com.fiberhome.filink.onenetserver.bean.device.OneNetResponse;
import com.fiberhome.filink.onenetserver.bean.device.Write;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetConnectParams;
import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import com.fiberhome.filink.onenetserver.exception.FilinkOneNetException;
import com.fiberhome.filink.onenetserver.operation.WriteOpe;
import com.fiberhome.filink.onenetserver.utils.HostUtil;
import com.fiberhome.filink.onenetserver.utils.OneNetToken;
import com.fiberhome.filink.onenetserver.utils.ProductInfoUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令下发方法
 *
 * @author CongcaiYu
 */
@Log4j
@Component
public class SendUtil {
    /**ISPO标准中的Object ID*/
    @Value("${oneNet.device.obj_id}")
    private Integer objId;
    /**ISPO标准中的Object Instance ID*/
    @Value("${oneNet.device.obj_inst_id}")
    private Integer objInstId;
    /**ISPO标准中的Resource ID*/
    @Value("${oneNet.device.res_id}")
    private Integer resId;
    @Value("${oneNet.device.mode}")
    private Integer mode;
    @Value("${oneNet.device.timeout}")
    private Integer timeout;

    @Autowired
    private ControlFeign controlFeign;
    @Autowired
    private ProductInfoUtil productInfoUtil;
    @Autowired
    private HostUtil hostUtil;

    /**
     * 发送请求到平台
     *
     * @param data               消息
     * @param oceanConnectParams 请求参数
     */
    public void sendData(FiLinkOneNetConnectParams oceanConnectParams, String data) {
        String productId = oceanConnectParams.getAppId();
        String imei = oceanConnectParams.getImei();
        log.info("oneNet connect id : " + productId);
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
            Map<String, String> parasMap = new HashMap<>(64);
            byte[] bytes = HexUtil.hexStringToByte(data);
            //将数据进行base64加密
            String base64Str = new String(Base64.encodeBase64(bytes));
            log.info("send message to oneNet : " + base64Str);
            String token = OneNetToken.assembleToken(OneNetHttpConstants.VERSION, resourceName, expirationTime, accessKey);
            Write write = new Write(imei, mode, timeout, objId, objInstId, resId, base64Str);
            //获取oneNet域名
            write.setHost(hostUtil.getOneNetHost());
            WriteOpe writeOpe = new WriteOpe(token);
            OneNetResponse oneNetResponse = writeOpe.operation(write, write.toJsonObject());
            log.info("Write Device, response content: " + oneNetResponse);
        } catch (Exception e) {
            throw new FilinkOneNetException(e.getMessage());
        }
    }
}
