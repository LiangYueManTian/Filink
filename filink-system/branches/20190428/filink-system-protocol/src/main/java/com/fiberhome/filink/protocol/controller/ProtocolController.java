package com.fiberhome.filink.protocol.controller;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.protocol.dto.CertificateFile;
import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.protocol.exception.ProtocolParamsErrorException;
import com.fiberhome.filink.protocol.service.ProtocolService;
import com.fiberhome.filink.protocol.utils.ProtocolCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 通信协议  前端控制器
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-02-20
 */
@RestController
@RequestMapping("/protocol")
public class ProtocolController {
    @Autowired
    private ProtocolService protocolService;

    /**
     * 更新协议内容(Json数据)
     *
     * @param protocolParams
     * @param file
     * @return 更新结果
     */
    @PostMapping("/updateProtocol")
    public Result updateProtocol(ProtocolParams protocolParams, ProtocolField protocolField, CertificateFile certificateFile, @RequestBody(required = false) MultipartFile file) {
        //将协议字段跟协议文件赋值给协议参数对象
        protocolParams.setProtocolField(protocolField);
        protocolField.setCertificateFile(certificateFile);
        //检验请求参数,ProtocolParams对象
        if (!ProtocolCheckUtil.checkProtocolParams(protocolParams)) {
            //请求参数错误
            throw new ProtocolParamsErrorException();
        }
        return protocolService.updateProtocol(protocolParams, file);
    }

    /**
     * 查询协议内容
     *
     * @param type 协议类型
     * @return 协议内容
     */
    @GetMapping("/feign/{type}")
    public ProtocolField queryProtocol(@PathVariable String type){
        return protocolService.queryProtocol(type);
    }

}
