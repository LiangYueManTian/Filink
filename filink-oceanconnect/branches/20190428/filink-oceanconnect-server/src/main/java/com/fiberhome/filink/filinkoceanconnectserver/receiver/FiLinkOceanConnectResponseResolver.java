package com.fiberhome.filink.filinkoceanconnectserver.receiver;

import com.fiberhome.filink.commonstation.constant.CmdOk;
import com.fiberhome.filink.commonstation.constant.CmdType;
import com.fiberhome.filink.commonstation.constant.DataType;
import com.fiberhome.filink.commonstation.constant.ParamsKey;
import com.fiberhome.filink.commonstation.entity.param.AbstractResInputParams;
import com.fiberhome.filink.commonstation.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkCommonHeader;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkResponse;
import com.fiberhome.filink.commonstation.entity.xmlbean.AbstractProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.*;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.receiver.ResponseResolver;
import com.fiberhome.filink.commonstation.utils.ByteBufUtil;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResInputParams;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkOceanResOutputParams;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filink 响应帧解析
 *
 * @author CongcaiYu
 */
@Component("fiLinkResponseResolver")
public class FiLinkOceanConnectResponseResolver implements ResponseResolver {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


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
        ByteBuf buf = ByteBufUtil.createByteBuf();
        buf.writeBytes(bytes);
        //解析请求头参数
        FiLinkCommonHeader commonHeader = new FiLinkCommonHeader();
        resolveHeader(buf, fiLinkProtocolBean.getCommonHeader(), commonHeader);
        //参数信息
        FiLinkOceanResOutputParams resParams = new FiLinkOceanResOutputParams();
        ByteBuf dataBuf = buf.readBytes(buf.readableBytes());
        Map<String, Object> data = resolveData(dataBuf, commonHeader, fiLinkProtocolBean, resParams);
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
            if (DataType.INT.equalsIgnoreCase(dataType)) {
                paramValue = ByteBufUtil.getIntDataSource(length, dataParamBuf);
                //无符号
            } else if (DataType.UNSIGNED_INT.equalsIgnoreCase(dataType)) {
                paramValue = String.valueOf(HexUtil.bufToInt(dataParamBuf));
            } else if (DataType.STRING.equalsIgnoreCase(dataType)) {
                paramValue = HexUtil.bufToStr(dataParamBuf);
            } else {
                paramValue = executeHeader(headerParam, dataParamBuf);
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
     * 响应帧数据特殊处理
     *
     * @param headerParam 响应头参数信息
     * @param dataBuf     缓冲流
     * @return 处理后数据
     */
    private String executeHeader(HeaderParam headerParam, ByteBuf dataBuf) {
        //判断有无处理类
        DataFormat dataFormat = headerParam.getDataFormat();
        if (dataFormat != null) {
            Map<String, Object> headerParamMap = new HashMap<>(64);
            headerParamMap.put(ParamsKey.CMD_TYPE, CmdType.RESPONSE_TYPE);
            dataFormat.setParam(headerParamMap);
            return execute(dataFormat, dataBuf).toString();
        } else {
            throw new ResponseException("data type is null: " + headerParam.getId());
        }
    }


    /**
     * 解析data数据体
     *
     * @param dataBuf            字节缓冲
     * @param commonHeader       公共头信息
     * @param fiLinkProtocolBean 协议实体
     * @param resOutputParams    响应参数
     * @return 解析后数据
     */
    private Map<String, Object> resolveData(ByteBuf dataBuf, FiLinkCommonHeader commonHeader,
                                            FiLinkProtocolBean fiLinkProtocolBean, FiLinkOceanResOutputParams resOutputParams) {
        Map<String, Object> dataResponseMap = new HashMap<>(64);
        //从redis中获取协议信息
        Map<String, Data> dataMap = fiLinkProtocolBean.getDataMap();
        //cmdId
        Integer cmdId = Integer.decode(commonHeader.getCmdId());
        Data data = dataMap.get(HexUtil.intToHexInt(cmdId));
        if (data == null) {
            throw new ResponseException("data is not exist>>>>>>>>>>>>>>");
        }
        //响应类型
        Integer cmdType = Integer.decode(commonHeader.getCmdType());
        List<DataParamsChild> dataParams = null;
        //响应帧
        if (CmdType.RESPONSE_TYPE == cmdType) {
            //获取响应帧信息
            FiLinkResponse fiLinkResponse = new FiLinkResponse();
            resolveHeader(dataBuf, fiLinkProtocolBean.getResponseHeader(), fiLinkResponse);
            Integer cmdOk = Integer.decode(fiLinkResponse.getCmdOk());
            resOutputParams.setCmdOk(cmdOk);
            if (CmdOk.SUCCESS == cmdOk) {
                //成功应答
                dataParams = data.getDataResponse().getDataParams();
            } else {
                //失败应答 todo
                dataParams = fiLinkProtocolBean.getErrorSet();
            }
        } else {
            //请求帧
            dataParams = data.getDataRequest().getDataParams();
        }

        if (dataParams == null || dataParams.size() == 0) {
            logger.info(data.getCmdId() + " : dataParams is null>>>>>>>>>>");
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
     * @param dataParams      dataParam节点信息
     */
    private void resolveDataParamsChild(ByteBuf dataBuf, Map<String, Object> dataResponseMap, List<DataParamsChild> dataParams) {
        for (DataParamsChild dataParamsChild : dataParams) {
            if (dataParamsChild instanceof DataParam) {
                DataParam dataParam = (DataParam) dataParamsChild;
                resolveDataParam(dataParam, dataBuf, dataResponseMap);
            } else if (dataParamsChild instanceof ForEach) {
                ForEach forEach = (ForEach) dataParamsChild;
                resolveForEach(forEach, dataResponseMap, dataBuf);
            }
        }
    }


    /**
     * 解析循环体
     *
     * @param forEach         循环体信息
     * @param dataResponseMap 解析后数据
     * @param byteBuf         字节缓冲流
     */
    private void resolveForEach(ForEach forEach, Map<String, Object> dataResponseMap, ByteBuf byteBuf) {
        String referenceId = forEach.getReferenceId();
        Object countObj = dataResponseMap.get(referenceId);
        int count = Integer.parseInt(countObj.toString());
        List<Map<String, Object>> foreachMaps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, Object> foreachMap = new HashMap<>(64);
            resolveDataParamsChild(byteBuf, foreachMap, forEach.getItemList());
            foreachMaps.add(foreachMap);
        }
        dataResponseMap.put(ParamsKey.PARAMS_KEY, foreachMaps);
    }

    /**
     * 解析dataParam
     *
     * @param dataParam       dataParam节点对象信息
     * @param dataBuf         字节缓冲流
     * @param dataResponseMap 解析后数据
     */
    private void resolveDataParam(DataParam dataParam, ByteBuf dataBuf, Map<String, Object> dataResponseMap) {
        //如果为保留字节
        if (dataParam.isReserved()) {
            ByteBuf byteBuf = dataBuf.readBytes(dataParam.getLength());
            ReferenceCountUtil.release(byteBuf);
            return;
        }
        String dataType = dataParam.getType();
        //获取长度
        Integer length = dataParam.getLength();
        if (length == null) {
            String referenceId = dataParam.getLengthRef();
            if (StringUtils.isEmpty(referenceId)) {
                logger.info(dataParam.getName() + " : reference is error>>>>>>>>>>>>>");
                return;
            }
            length = Integer.parseInt(dataResponseMap.get(referenceId).toString());
        }
        //读取dataParam数据
        ByteBuf dataParamBuf = dataBuf.readBytes(length);

        //int类型
        Object param;
        if (DataType.INT.equalsIgnoreCase(dataType)) {
            param = ByteBufUtil.getIntDataSource(length, dataParamBuf);
            //String类型
        } else if (DataType.UNSIGNED_INT.equalsIgnoreCase(dataType)) {
            param = String.valueOf(HexUtil.bufToInt(dataParamBuf));
        } else if (DataType.STRING.equalsIgnoreCase(dataType)) {
            param = HexUtil.bufToStr(dataParamBuf);
        } else {
            //判断有无dataFormat
            DataFormat dataFormat = dataParam.getDataFormat();
            if (dataFormat != null) {
                param = execute(dataFormat, dataParamBuf);
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
        //处理结果集
        Map<String, Choose> chooseMap = dataParam.getChooseMap();
        if (chooseMap != null) {
            String code = HexUtil.intToHexInt(Integer.parseInt(param.toString()));
            Choose choose = chooseMap.get(code);
            if (choose == null) {
                throw new ResponseException("code: " + code + "is null>>>>>>>>>>");
            }
            dataResponseMap.put(dataParam.getId(), choose.getId());
            //循环dataClass参数
            List<DataParamsChild> dataParams = choose.getDataParams();
            resolveDataParamsChild(dataBuf, dataResponseMap, dataParams);
        }
    }

    /**
     * 执行handler
     *
     * @param dataFormat 数据处理类
     * @param byteBuf    字节缓冲流
     * @return 处理后数据
     */
    private Object execute(DataFormat dataFormat, ByteBuf byteBuf) {
        try {
            Class<?> dataFormatClazz = Class.forName(dataFormat.getClassName());
            DataHandler handler = (DataHandler) dataFormatClazz.newInstance();
            return handler.handle(dataFormat, byteBuf);
        } catch (Exception e) {
            throw new ResponseException("data format execute failed: " + dataFormat.getId());
        }
    }


}
