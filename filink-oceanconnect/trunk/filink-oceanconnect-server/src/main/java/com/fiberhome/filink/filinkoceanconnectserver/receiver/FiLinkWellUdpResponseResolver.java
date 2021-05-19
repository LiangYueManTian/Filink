package com.fiberhome.filink.filinkoceanconnectserver.receiver;


import com.fiberhome.filink.commonstation.constant.DataType;
import com.fiberhome.filink.commonstation.constant.WellConstant;
import com.fiberhome.filink.commonstation.entity.param.AbstractResInputParams;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkCommonHeader;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkResponse;
import com.fiberhome.filink.commonstation.entity.xmlbean.AbstractProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.Data;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.DataParam;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.DataParamsChild;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.receiver.ResponseResolver;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.utils.WellByteBufUtil;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResInputParams;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import com.fiberhome.filink.filinkoceanconnectserver.utils.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filink 响应帧解析
 *
 * @author CongcaiYu
 */
@Slf4j
@Component("fiLinkWellUdpResponseResolver")
public class FiLinkWellUdpResponseResolver implements ResponseResolver {

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 解析响应帧
     *
     * @param inputParams AbstractResInputParams
     * @return FiLinkResponse
     */
    @Override
    public AbstractResOutputParams resolveRes(AbstractResInputParams inputParams) {
        //将参数转换成filink实体
        FiLinkOceanResInputParams resInputParams;
        FiLinkProtocolBean fiLinkProtocolBean;
        try {
            resInputParams = (FiLinkOceanResInputParams) inputParams;
            AbstractProtocolBean protocolBean = resInputParams.getProtocolBean();
            fiLinkProtocolBean = (FiLinkProtocolBean) protocolBean;

        } catch (Exception e) {
            throw new ResponseException("inputParams parse exception>>>>>>>>>>>");
        }
        String hexDataSource = resInputParams.getDataSource();
        //将16进制数据包转换成byteBuf
        byte[] bytes = HexUtil.hexStringToByte(hexDataSource);
        ByteBuf buf = WellByteBufUtil.createByteBuf();
        buf.writeBytes(bytes);
        //解析请求头参数
        FiLinkCommonHeader commonHeader = new FiLinkCommonHeader();
        resolveHeader(buf, fiLinkProtocolBean.getCommonHeader(), commonHeader);
        //参数信息
        FiLinkOceanResOutputParams resParams = new FiLinkOceanResOutputParams();
        ByteBuf dataBuf = buf.readBytes(buf.readableBytes());
        Map<String, Object> data = resolveData(dataBuf, commonHeader, fiLinkProtocolBean);
        //netty释放
        ReferenceCountUtil.release(buf);
        ReferenceCountUtil.release(dataBuf);
        //set响应输出参数
        resParams.setParams(data);
        resParams.setEquipmentId(commonHeader.getEquipmentId());
        resParams.setSerialNumber(Integer.parseInt(commonHeader.getSerialNumber()));
        Integer cmdSequenceId = Integer.decode(commonHeader.getCmdId());
        resParams.setCmdId(HexUtil.intToHexInt(cmdSequenceId));
        resParams.setCmdType(commonHeader.getCmdType());
        return resParams;
    }


    /**
     * 解析响应头
     *
     * @param dataBuf         数据流
     * @param responseHeaders 响应头配置信息
     */
    private void resolveHeader(ByteBuf dataBuf, List<HeaderParam> responseHeaders, Object source) {
        if (responseHeaders == null || responseHeaders.size() == 0) {
            return;
        }
        for (HeaderParam headerParam : responseHeaders) {
            //如果为保留字节
            if (headerParam.isReserved()) {
                ByteBuf byteBuf = dataBuf.readBytes(headerParam.getLength());
                ReferenceCountUtil.release(byteBuf);
                continue;
            }
            //数据类型
            String dataType = headerParam.getType();
            //数据长度
            Integer length = headerParam.getLength();
            //读取dataParam数据
            ByteBuf dataParamBuf = dataBuf.readBytes(length);
            //int
            String paramValue;
            if (WellConstant.NORMAL_INT.equalsIgnoreCase(dataType)) {
                paramValue = String.valueOf(HexUtil.bufToInt(dataParamBuf));
            } else if (WellConstant.HEX_STRING.equalsIgnoreCase(dataType)) {
                paramValue = HexUtil.bufToHexStr(dataParamBuf);
            } else if (DataType.UNSIGNED_INT.equalsIgnoreCase(dataType)) {
                paramValue = WellByteBufUtil.getIntDataSource(length, dataParamBuf);
            } else if (DataType.STRING.equalsIgnoreCase(dataType)) {
                paramValue = HexUtil.bufToStr(dataParamBuf);
            } else {
                paramValue = commonUtil.executeHeader(headerParam, dataParamBuf);
            }
            ReferenceCountUtil.release(dataParamBuf);
            //将属性set到对象中
            Class<? extends Object> sourceClass = source.getClass();
            //拼接set方法
            String id = headerParam.getId();
            String methodName = "set" + id.substring(0, 1).toUpperCase() + id.substring(1);
            try {
                Method method = sourceClass.getDeclaredMethod(methodName, String.class);
                method.setAccessible(true);
                method.invoke(source, paramValue);
            } catch (Exception e) {
                throw new RequestException("execute set method failed: " + headerParam.getId());
            }
        }
    }


    /**
     * 解析data数据体
     *
     * @param dataBuf            字节缓冲
     * @param commonHeader       公共头信息
     * @param fiLinkProtocolBean 协议实体
     * @return 解析后数据
     */
    private Map<String, Object> resolveData(ByteBuf dataBuf, FiLinkCommonHeader commonHeader, FiLinkProtocolBean fiLinkProtocolBean) {
        Map<String, Object> dataResponseMap = new HashMap<>(64);
        //从redis中获取协议信息
        Map<String, Data> dataMap = fiLinkProtocolBean.getDataMap();
        //cmdId
        Integer cmdId = Integer.decode(commonHeader.getCmdId());
        Data data = dataMap.get(HexUtil.intToHexInt(cmdId));
        if (data == null) {
            throw new ResponseException("data is not exist>>>>>>>>>>>>>>");
        }
        List<DataParamsChild> dataParams;
        FiLinkResponse fiLinkResponse = new FiLinkResponse();
        resolveHeader(dataBuf, fiLinkProtocolBean.getResponseHeader(), fiLinkResponse);
        dataParams = data.getDataResponse().getDataParams();
        if (dataParams == null || dataParams.size() == 0) {
            log.info(data.getCmdId() + " : dataParams is null>>>>>>>>>>");
            return dataResponseMap;
        }
        //解析dataParam
        resolveDataParamsChild(dataBuf, dataResponseMap, dataParams);
        return dataResponseMap;
    }


    /**
     * 解析params子节点
     *
     * @param dataBuf         字节缓冲流
     * @param dataResponseMap 解析后数据
     */
    private void resolveDataParamsChild(ByteBuf dataBuf, Map<String, Object> dataResponseMap, List<DataParamsChild> dataParams) {
        for (DataParamsChild dataParamsChild : dataParams) {
            if (dataParamsChild instanceof DataParam) {
                DataParam dataParam = (DataParam) dataParamsChild;
                resolveDataParam(dataParam, dataBuf, dataResponseMap);
            }
        }
    }


    /**
     * 解析dataParam
     *
     * @param dataParam       dataParam节点对象信息
     * @param dataBuf         字节缓冲流
     * @param dataResponseMap 解析后数据
     */
    private void resolveDataParam(DataParam dataParam, ByteBuf dataBuf, Map<String, Object> dataResponseMap) {
        String dataType = dataParam.getType();
        //获取长度
        Integer length = dataParam.getLength();

        //读取dataParam数据
        ByteBuf dataParamBuf = null;
        try {
            dataParamBuf = dataBuf.readBytes(length);
        } catch (Exception e) {
            log.error(dataParam.getId());
        }

        //int类型
        Object param;
        if (DataType.INT.equalsIgnoreCase(dataType)) {
            param = WellByteBufUtil.getIntDataSource(length, dataParamBuf);
            //String类型
        } else if (DataType.UNSIGNED_INT.equalsIgnoreCase(dataType)) {
            param = String.valueOf(WellByteBufUtil.bytes2ToInt(dataParamBuf, length));
        } else if (DataType.STRING.equalsIgnoreCase(dataType)) {
            param = HexUtil.bufToStr(dataParamBuf);
        } else {
            //判断有无dataFormat
            DataFormat dataFormat = dataParam.getDataFormat();
            if (dataFormat != null) {
                param = commonUtil.execute(dataFormat, dataParamBuf);
            } else {
                throw new ResponseException("id: " + dataParam.getId() + " data is error");
            }
        }
        ReferenceCountUtil.release(dataParamBuf);
        //设置结果集
        Map<String, String> resultMap = dataParam.getResultMap();
        if (resultMap != null && resultMap.size() > 0) {
            String msg = resultMap.get(param);
            dataResponseMap.put(dataParam.getId(), msg);
        } else {
            dataResponseMap.put(dataParam.getId(), param);
        }

    }


}
