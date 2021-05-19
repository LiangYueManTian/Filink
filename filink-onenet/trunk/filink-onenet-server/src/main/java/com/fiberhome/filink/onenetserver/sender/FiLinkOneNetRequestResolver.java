package com.fiberhome.filink.onenetserver.sender;

import com.fiberhome.filink.commonstation.constant.*;
import com.fiberhome.filink.commonstation.entity.param.AbstractReqParams;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkCommonHeader;
import com.fiberhome.filink.commonstation.entity.protocol.FiLinkResponse;
import com.fiberhome.filink.commonstation.entity.xmlbean.AbstractProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.*;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.exception.RequestException;
import com.fiberhome.filink.commonstation.handler.DataHandler;
import com.fiberhome.filink.commonstation.sender.RequestResolver;
import com.fiberhome.filink.commonstation.utils.ByteBufUtil;
import com.fiberhome.filink.commonstation.utils.HexUtil;
import com.fiberhome.filink.onenetserver.bean.protocol.FiLinkOneNetConnectParams;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * filink udp请求帧解析
 *
 * @author CongcaiYu
 */
@Slf4j
@Component("fiLinkRequestHandler")
public class FiLinkOneNetRequestResolver implements RequestResolver {


    /**
     * 获取udpRequest
     *
     * @param udpParams FiLinkReqOceanConnectParams
     * @return String
     */
    @Override
    public String resolveUdpReq(AbstractReqParams udpParams) {
        //请求帧buf
        ByteBuf reqBuf = ByteBufUtil.createByteBuf();
        //头帧buf
        ByteBuf headBuf = ByteBufUtil.createByteBuf();
        //消息体buf
        ByteBuf dataBuf;
        FiLinkOneNetConnectParams fiLinkOneNetConnectParams;
        FiLinkProtocolBean fiLinkProtocolBean;
        try {
            fiLinkOneNetConnectParams = (FiLinkOneNetConnectParams) udpParams;
            //从redis中获取协议信息
            AbstractProtocolBean protocolBean = fiLinkOneNetConnectParams.getProtocolBean();
            fiLinkProtocolBean = (FiLinkProtocolBean) protocolBean;
        } catch (Exception e) {
            throw new RequestException("filink request params parse exception");
        }
        try {
            //生成data
            dataBuf = getDataBuf(fiLinkOneNetConnectParams, fiLinkProtocolBean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(e.getMessage());
        }
        //判断请求类型
        List<HeaderParam> commonHeaderList = fiLinkProtocolBean.getCommonHeader();
        //生成公共头参数
        FiLinkCommonHeader commonHeader = setHeader(fiLinkOneNetConnectParams, dataBuf.readableBytes(), commonHeaderList);
        //写入公共头信息
        setHeaderBuf(commonHeaderList, commonHeader, headBuf);
        //判断请求类型
        Integer cmdType = fiLinkOneNetConnectParams.getCmdType();
        if (CmdType.RESPONSE_TYPE == cmdType) {
            List<HeaderParam> requestHeader = fiLinkProtocolBean.getResponseHeader();
            FiLinkResponse fiLinkResponse = new FiLinkResponse();
            fiLinkResponse.setCmdOk(String.valueOf(CmdOk.SUCCESS));
            setHeaderBuf(requestHeader, fiLinkResponse, headBuf);
        }
        //将头信息写入byteBuf
        reqBuf.writeBytes(headBuf);
        //将消息data写入byteBuf
        reqBuf.writeBytes(dataBuf);
        byte[] lockOrder = new byte[reqBuf.readableBytes()];
        reqBuf.readBytes(lockOrder);
        String dataHex = HexUtil.bytesToHexString(lockOrder);
        log.info("send data: {}", dataHex);
        //netty释放
        ReferenceCountUtil.release(reqBuf);
        ReferenceCountUtil.release(headBuf);
        ReferenceCountUtil.release(dataBuf);
        return dataHex;
    }

    /**
     * 设置头参数信息
     *
     * @param fiLinkReqOceanConnectParams 请求参数信息
     * @param dataLength      消息体净荷
     * @return 公共头信息
     */
    private FiLinkCommonHeader setHeader(FiLinkOneNetConnectParams fiLinkReqOceanConnectParams, int dataLength, List<HeaderParam> headerParams) {
        Integer cmdType = fiLinkReqOceanConnectParams.getCmdType();
        FiLinkCommonHeader commonHeader = new FiLinkCommonHeader();
        //获取cmdLen
        for (HeaderParam headerParam : headerParams) {
            String cmdLenStr = "cmdLen";
            if (cmdLenStr.equals(headerParam.getId())) {
                resolveScope(cmdType, headerParam, commonHeader, dataLength);
            }
        }
        //设置公共头
        commonHeader.setCmdId(fiLinkReqOceanConnectParams.getCmdId());
        commonHeader.setCmdType(String.valueOf(cmdType));
        commonHeader.setEquipmentId(fiLinkReqOceanConnectParams.getEquipmentId());
        commonHeader.setFrameHead(String.valueOf(Head.ORDER));
        commonHeader.setProtocolFlag(ProtocolType.BGMP);
        commonHeader.setSerialNumber(String.valueOf(fiLinkReqOceanConnectParams.getSerialNumber()));
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
            //是否是保留字段
            if (headerParam.isReserved()) {
                setReservedLen(length, headerBuf, 0);
                continue;
            }
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
                }else {
                    ByteBufUtil.setDataBuf(type, dataSource, length, headerBuf);
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
     * @param fiLinkReqOceanConnectParams FiLinkReqOceanConnectParams
     * @return ByteBuf
     */
    private ByteBuf getDataBuf(FiLinkOneNetConnectParams fiLinkReqOceanConnectParams, FiLinkProtocolBean fiLinkProtocolXmlBean) throws Exception {
        String cmdId = fiLinkReqOceanConnectParams.getCmdId();
        Integer cmdType = fiLinkReqOceanConnectParams.getCmdType();
        ByteBuf dataBuf = ByteBufUtil.createByteBuf();
        //获取data请求帧
        Map<String, Data> dataMap = fiLinkProtocolXmlBean.getDataMap();
        Data data = dataMap.get(cmdId);
        List<DataParamsChild> dataParams;
        if (CmdType.REQUEST_TYPE == cmdType) {
            dataParams = data.getDataRequest().getDataParams();
        } else {
            dataParams = data.getDataResponse().getDataParams();
        }
        if (dataParams != null && dataParams.size() > 0) {
            writeDataParamChild(dataParams, dataBuf, fiLinkReqOceanConnectParams.getParams());
        }
        return dataBuf;
    }


    /**
     * 写入dataParam子节点
     *
     * @param dataParams      List<DataParamsChild>
     * @param dataBuf         ByteBuf
     * @param fiLinkReqParams FiLinkReqOceanConnectParams
     */
    private void writeDataParamChild(List<DataParamsChild> dataParams, ByteBuf dataBuf, Map<String, Object> fiLinkReqParams) throws Exception {
        for (DataParamsChild dataParamsChild : dataParams) {
            if (dataParamsChild instanceof DataParam) {
                DataParam dataParam = (DataParam) dataParamsChild;
                setBufData(dataBuf, fiLinkReqParams, dataParam);
            } else if (dataParamsChild instanceof ForEach) {
                ForEach forEach = (ForEach) dataParamsChild;
                writeForeach(forEach, fiLinkReqParams, dataBuf);
            }
        }
    }


    /**
     * 写入循环体信息
     *
     * @param forEach         ForEach
     * @param fiLinkReqParams Map<String,Object>
     * @param dataBuf         ByteBuf
     */
    private void writeForeach(ForEach forEach, Map<String, Object> fiLinkReqParams, ByteBuf dataBuf) throws Exception {
        List<DataParamsChild> itemList = forEach.getItemList();
        //获取循环参数
        List<Map<String, Object>> foreachParams = (List<Map<String, Object>>) fiLinkReqParams.get(ParamsKey.PARAMS_KEY);
        int count = foreachParams.size();
        for (int i = 0; i < count; i++) {
            //根据dataClass code获取dataParam
            writeDataParamChild(itemList, dataBuf, foreachParams.get(i));
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
            throw new RequestException("execute " + dataFormat.getClassName() + "failed");
        }
    }


    /**
     * 设置随机数
     *
     * @param length  Integer
     * @param dataBuf ByteBuf
     */
    private void setRandomLen(Integer length, ByteBuf dataBuf) {
        Random random = new Random();
        dataBuf.writeByte(random.nextInt(127));
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
            //是否是保留字段
            if (dataParam.isReserved()) {
                setReservedLen(dataParam.getLength(), dataBuf, 0);
                return;
            }
            //随机数
            if (dataParam.isRandom()) {
                setRandomLen(dataParam.getLength(), dataBuf);
                return;
            }
            //判断是否有数据格式化
            DataFormat dataFormat = dataParam.getDataFormat();
            if (dataFormat != null) {
                dataFormat.setParam(fiLinkReqParams.get(dataParam.getId()));
                execute(dataFormat, dataBuf);
                return;
            }
            //判断有无data
            String data = dataParam.getData();
            if (!StringUtils.isEmpty(data)) {
                ByteBufUtil.setDataBuf(type, data, length, dataBuf);
                return;
            }
            Object dataSource;
            Map<String, Choose> chooseMap = dataParam.getChooseMap();
            //是否是循环次数
            if (dataParam.isForeach()) {
                List<Map<String, Object>> params = (List<Map<String, Object>>) fiLinkReqParams.get(ParamsKey.PARAMS_KEY);
                dataSource = params.size();
            } else if (chooseMap != null) {
                //dataClass根据key值获取对应的指令码
                String key = fiLinkReqParams.get(dataParam.getId()).toString();
                dataSource = getDataCodeByKey(key, chooseMap);
            } else {
                //获取请求参数
                dataSource = fiLinkReqParams.get(dataParam.getId());
            }
            //判断有无resultMap进行数据转换
            if(dataParam.getResultMap() != null){
                dataSource = dataParam.getResultMap().get(dataSource.toString());
            }
            //判断参数为空
            if(StringUtils.isEmpty(dataSource)){
                throw new RequestException("data source is null: "+dataParam.getId());
            }
            //将数据写入byteBuf
            ByteBufUtil.setDataBuf(type, dataSource.toString(), length, dataBuf);
            //处理集map,判断是否是dataClass
            if (chooseMap != null) {
                Choose choose = chooseMap.get(dataSource);
                List<DataParamsChild> dataParams = choose.getDataParams();
                writeDataParamChild(dataParams, dataBuf, fiLinkReqParams);
            }
        } catch (Exception e) {
            throw new RequestException("setBufData: " + dataParam.getId() + " failed");
        }
    }

    /**
     * 根据key值获取code
     *
     * @param key       String
     * @param chooseMap Map<String, Choose>
     * @return String
     */
    private String getDataCodeByKey(String key, Map<String, Choose> chooseMap) {
        for (Map.Entry<String, Choose> entry : chooseMap.entrySet()) {
            Choose choose = entry.getValue();
            if (choose == null) {
                throw new RequestException("getDataCodeByKey choose is null");
            }
            if (key.equals(choose.getId())) {
                return choose.getCode();
            }
        }
        log.error("there is no code mapped to the key: {}", key);
        throw new RequestException("there is no code mapped to the key: " + key);
    }


    /**
     * 设置保留字节
     *
     * @param length  int
     * @param dataBuf ByteBuf
     * @param data    int
     */
    private void setReservedLen(int length, ByteBuf dataBuf, int data) {
        for (int i = 0; i < length; i++) {
            dataBuf.writeByte(data);
        }
    }

}
