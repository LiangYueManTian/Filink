package com.fiberhome.filink.filinkoceanconnectserver.sender;


import com.fiberhome.filink.commonstation.constant.*;
import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkCommonHeader;
import com.fiberhome.filink.commonstation.entity.xmlbean.AbstractProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.Data;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.DataParam;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.DataParamsChild;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.sender.RequestResolver;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.commonstation.utils.VerifyUtil;
import com.fiberhome.filink.commonstation.utils.WellByteBufUtil;
import com.fiberhome.filink.filinkoceanconnectserver.entity.protocol.FiLinkReqOceanConnectParams;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * filink udp请求帧解析
 *
 * @author CongcaiYu
 */
@Slf4j
@Component("fiLinkWellUdpRequestHandler")
public class FiLinkWellUdpRequestResolver implements RequestResolver {


    /**
     * 获取udpRequest
     *
     * @param udpParams FiLinkReqParams
     * @return String
     */
    @Override
    public String resolveUdpReq(AbstractReqParams udpParams) {
        //请求帧buf
        ByteBuf reqBuf = WellByteBufUtil.createByteBuf();
        //头帧buf
        ByteBuf headBuf = WellByteBufUtil.createByteBuf();
        //消息体buf
        ByteBuf dataBuf = WellByteBufUtil.createByteBuf();
        FiLinkReqOceanConnectParams fiLinkReqParams;
        FiLinkProtocolBean fiLinkProtocolBean;
        try {
            fiLinkReqParams = (FiLinkReqOceanConnectParams) udpParams;
            //从redis中获取协议信息
            AbstractProtocolBean protocolBean = fiLinkReqParams.getProtocolBean();
            fiLinkProtocolBean = (FiLinkProtocolBean) protocolBean;
        } catch (Exception e) {
            throw new RequestException("filink request params parse exception>>>>>>");
        }
        try {
            //生成data
            if (udpParams.getParams() != null) {
                Map<String, Object> params = udpParams.getParams();
                List<Map<String, Object>> valueList = (List<Map<String, Object>>) params.get(WellConstant.PARAMS);
                if (valueList != null) {
                    Map<String, Object> paramMap = new HashMap<>(64);
                    for (Map<String, Object> valueMap : valueList) {
                        String dataClass = (String) valueMap.get(WellConstant.DATA_CLASS);
                        Object data = valueMap.get(ParamsKey.DATA);
                        paramMap.put(dataClass, data);
                    }
                    udpParams.setParams(paramMap);
                }
                dataBuf = getDataBuf(fiLinkReqParams, fiLinkProtocolBean);
            }
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
        //判断请求类型
        List<HeaderParam> commonHeaderList = fiLinkProtocolBean.getCommonHeader();
        //生成公共头参数
        FiLinkCommonHeader commonHeader = setHeader(fiLinkReqParams, dataBuf.readableBytes(), commonHeaderList);
        //写入公共头信息
        setHeaderBuf(commonHeaderList, commonHeader, headBuf);
        //将头信息写入byteBuf
        reqBuf.writeBytes(headBuf);
        //将消息data写入byteBuf
        reqBuf.writeBytes(dataBuf);
        //写入消息尾
        setEnd(reqBuf);
        byte[] lockOrder = new byte[reqBuf.readableBytes()];
        reqBuf.readBytes(lockOrder);
        String dataHex = HexUtil.bytesToHexString(lockOrder);
        log.info("send data: " + dataHex);
        ReferenceCountUtil.release(reqBuf);
        ReferenceCountUtil.release(headBuf);
        ReferenceCountUtil.release(dataBuf);
        return dataHex;

    }

    /**
     * 写入消息尾部
     *
     * @param byteBuf
     */
    private void setEnd(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes() - 1];
        byteBuf.getBytes(1, bytes);
        byte[] checkBytes = VerifyUtil.xorVerifyByte(bytes);
        byteBuf.writeByte(checkBytes[0]);
        byteBuf.writeByte(0xEF);
    }

    /**
     * 设置头参数信息
     *
     * @param fiLinkReqParams 请求参数信息
     * @param dataLength      消息体净荷
     * @return 公共头信息
     */
    private FiLinkCommonHeader setHeader(FiLinkReqOceanConnectParams fiLinkReqParams, int dataLength, List<HeaderParam> headerParams) {
        Integer cmdType = fiLinkReqParams.getCmdType();
        FiLinkCommonHeader commonHeader = new FiLinkCommonHeader();
        //获取cmdLen
        for (HeaderParam headerParam : headerParams) {
            String cmdLenStr = "cmdLen";
            if (cmdLenStr.equals(headerParam.getId())) {
                resolveScope(cmdType, headerParam, commonHeader, dataLength + 2);
            }
        }
        //设置公共头
        commonHeader.setCmdId(fiLinkReqParams.getCmdId());
        commonHeader.setCmdType(String.valueOf(cmdType));
        commonHeader.setEquipmentId(fiLinkReqParams.getEquipmentId());
        commonHeader.setFrameHead(String.valueOf(Head.WELL_FRAME_HEAD));
        commonHeader.setProtocolFlag(ProtocolType.BGMP);
        commonHeader.setSerialNumber(String.valueOf(fiLinkReqParams.getSerialNumber()));
        commonHeader.setLen(String.valueOf(dataLength));
        //请求类型
        return commonHeader;
    }

    /**
     * 设置cmdLen长度
     *
     * @param cmdType      请求类型
     * @param headerParam  头参数
     * @param commonHeader 公共头对象
     * @param dataLength   消息体长度
     */
    private void resolveScope(int cmdType, HeaderParam headerParam,
                              FiLinkCommonHeader commonHeader, int dataLength) {
        String scope = CmdType.REQUEST_TYPE == cmdType ? headerParam.getReqScope() : headerParam.getResScope();
        int cmdLen = Integer.parseInt(scope.split(" ")[0]);
        String bodyStr = "body";
        if (scope.contains(bodyStr)) {
            cmdLen += dataLength;
        }
        commonHeader.setCmdLen(String.valueOf(cmdLen));
    }


    /**
     * 生成请求头数据
     *
     * @param headerBuf    头部字节缓冲对象
     * @param headerParams 头参数信息
     * @param source       对象信息
     */
    private void setHeaderBuf(List<HeaderParam> headerParams, Object source, ByteBuf headerBuf) {
        for (HeaderParam headerParam : headerParams) {
            //数据类型
            String type = headerParam.getType();
            Integer length = headerParam.getLength();
            //获取该字段值
            Class<? extends Object> headerClass = source.getClass();
            //拼接get方法
            String id = headerParam.getId();
            String methodName = "get" + id.substring(0, 1).toUpperCase() + id.substring(1);
            try {
                Method method = headerClass.getDeclaredMethod(methodName);
                method.setAccessible(true);
                String dataSource = method.invoke(source).toString();
                //判断是否有处理类
                if (headerParam.getDataFormat() != null) {
                    executeHeaderFormat(headerParam.getDataFormat(), headerBuf, dataSource);
                } else {
                    WellByteBufUtil.setDataBuf(type, dataSource, length, headerBuf);
                }
            } catch (Exception e) {
                throw new RequestException("execute get method failed: " + headerParam.getId());
            }
        }
    }

    /**
     * 请求帧数据特殊处理
     *
     * @param dataFormat 数据处理类
     * @param headerBuf  请求数据
     * @param dataSource 元数据
     */
    private void executeHeaderFormat(DataFormat dataFormat, ByteBuf headerBuf, String dataSource) {
        Map<String, Object> headerParam = new HashMap<>(64);
        headerParam.put(ParamsKey.HEADER_DATA, dataSource);
        headerParam.put(ParamsKey.CMD_TYPE, CmdType.REQUEST_TYPE);
        dataFormat.setParam(headerParam);
        execute(dataFormat, headerBuf);
    }


    /**
     * 获取data参数
     *
     * @param fiLinkReqParams FiLinkReqParams
     * @return ByteBuf
     */
    private ByteBuf getDataBuf(FiLinkReqOceanConnectParams fiLinkReqParams, FiLinkProtocolBean fiLinkProtocolXmlBean) throws Exception {
        String cmdId = fiLinkReqParams.getCmdId();
        ByteBuf dataBuf = WellByteBufUtil.createByteBuf();
        //获取data请求帧
        Map<String, Data> dataMap = fiLinkProtocolXmlBean.getDataMap();
        Data data = dataMap.get(cmdId);
        List<DataParamsChild> dataParams;
        dataParams = data.getDataRequest().getDataParams();
        Map<String, Object> params = fiLinkReqParams.getParams();
        params.put(ParamsKey.EQUIPMENT_ID, fiLinkReqParams.getEquipmentId());
        if (dataParams != null && dataParams.size() > 0) {
            writeDataParamChild(dataParams, dataBuf, fiLinkReqParams.getParams());
        }
        return dataBuf;
    }


    /**
     * 写入dataParam子节点
     *
     * @param dataParams      List<DataParamsChild>
     * @param dataBuf         ByteBuf
     * @param fiLinkReqParams FiLinkReqParams
     */
    private void writeDataParamChild(List<DataParamsChild> dataParams, ByteBuf dataBuf, Map<String, Object> fiLinkReqParams) throws Exception {
        for (DataParamsChild dataParamsChild : dataParams) {
            if (dataParamsChild instanceof DataParam) {
                DataParam dataParam = (DataParam) dataParamsChild;
                setBufData(dataBuf, fiLinkReqParams, dataParam);
            }
        }
    }


    /**
     * 执行handler
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     */
    private Object execute(DataFormat dataFormat, ByteBuf byteBuf) {
        try {
            Class<?> dataFormatClazz = Class.forName(dataFormat.getClassName());
            DataHandler handler = (DataHandler) dataFormatClazz.newInstance();
            return handler.handle(dataFormat, byteBuf);
        } catch (Exception e) {
            throw new RequestException("execute " + dataFormat.getClassName() + "failed>>>>>");
        }

    }

    /**
     * 设置数据包参数长度
     *
     * @param dataBuf         ByteBuf
     * @param fiLinkReqParams Map<String,Object>
     * @param dataParam       DataParam
     */
    private void setBufData(ByteBuf dataBuf, Map<String, Object> fiLinkReqParams, DataParam dataParam) {
        try {
            //数据类型
            String type = dataParam.getType();
            Integer length = dataParam.getLength();
            //判断是否有数据格式化
            DataFormat dataFormat = dataParam.getDataFormat();
            if (dataFormat != null) {
                dataFormat.setParam(fiLinkReqParams);
                execute(dataFormat, dataBuf);
                return;
            }
            //判断有无data
            String data = dataParam.getData();
            if (!StringUtils.isEmpty(data)) {
                WellByteBufUtil.setDataBuf(type, data, length, dataBuf);
                return;
            }
            //获取请求参数
            Object dataSource = fiLinkReqParams.get(dataParam.getId());
            //判断参数为空
            if (StringUtils.isEmpty(dataSource)) {
                switch (length) {
                    case 1:
                        dataSource = 0xFF;
                        break;
                    case 2:
                        dataSource = 0xFFFF;
                        break;
                    case 4:
                        dataSource = 0xFFFFFFFF;
                        break;
                    default:
                        break;
                }
            }
            //判断有无resultMap进行数据转换
            if (dataParam.getResultMap() != null) {
                dataSource = dataParam.getResultMap().get(dataSource.toString());
            }
            //将数据写入byteBuf
            WellByteBufUtil.setDataBuf(type, dataSource.toString(), length, dataBuf);
        } catch (Exception e) {
            throw new RequestException("setBufData: " + dataParam.getId() + " failed>>>>>>");
        }
    }



}
