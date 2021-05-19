package com.fiberhome.filink.stationserver.sender.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.protocol.bean.xmlBean.data.*;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.redis.RedisUtils;
import com.fiberhome.filink.stationserver.entity.param.AbstractReqParams;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkReqParams;
import com.fiberhome.filink.stationserver.handler.DataHandler;
import com.fiberhome.filink.stationserver.exception.RequestException;
import com.fiberhome.filink.stationserver.sender.RequestResolver;
import com.fiberhome.filink.stationserver.util.ByteBufUtil;
import com.fiberhome.filink.stationserver.util.HexUtil;
import com.fiberhome.filink.stationserver.util.lockenum.*;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * filink udp请求帧解析
 *
 * @author CongcaiYu
 */
@Component("fiLinkUdpRequestHandler")
public class FiLinkUdpRequestResolver implements RequestResolver {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取udpRequest
     *
     * @param udpParams udp入参
     * @return String
     */
    @Override
    public String resolveUdpReq(AbstractReqParams udpParams) {
        FiLinkReqParams fiLinkReqParams;
        FiLinkProtocolBean fiLinkProtocolBean;
        try {
            fiLinkReqParams = (FiLinkReqParams) udpParams;
            //从redis中获取协议信息
            AbstractProtocolBean protocolBean = fiLinkReqParams.getProtocolBean();
            fiLinkProtocolBean = (FiLinkProtocolBean) protocolBean;
        } catch (Exception e) {
            throw new RequestException("filink request params parse exception>>>>>>");
        }
        int equipmentId = Integer.parseInt(udpParams.getDeviceId());
        ByteBuf headBuf = ByteBufUtil.createByteBuf();
        ByteBuf dataBuf;
        try {
            dataBuf = getDataBuf(fiLinkReqParams, fiLinkProtocolBean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RequestException(e.getMessage());
        }
        ByteBuf bgmpBuf = getBgmp(equipmentId, fiLinkReqParams, dataBuf.readableBytes(), fiLinkProtocolBean);
        //帧地址头 2byte
        headBuf.writeShort(Head.ORDER);
        //命令长度 2byte
        headBuf.writeShort(bgmpBuf.readableBytes() + dataBuf.readableBytes());
        //BGMP帧
        headBuf.writeBytes(bgmpBuf);
        //消息体data
        headBuf.writeBytes(dataBuf);
        byte[] lockOrder = new byte[headBuf.readableBytes()];
        headBuf.readBytes(lockOrder);
        String dataHex = HexUtil.bytesToHexString(lockOrder);
        logger.info("send data: " + dataHex);
        return dataHex;

    }


    /**
     * 获取BGMP参数
     *
     * @param fiLinkReqParams       udp入参
     * @param fiLinkProtocolXmlBean 协议实体
     * @param equipmentId           设施装置id
     * @param dataLength            消息体净荷
     * @return ByteBuf
     */
    private ByteBuf getBgmp(int equipmentId, FiLinkReqParams fiLinkReqParams, int dataLength, FiLinkProtocolBean fiLinkProtocolXmlBean) {
        ByteBuf bgmpBuf = ByteBufUtil.createByteBuf();
        //协议标志位 4byte 字符串
        bgmpBuf.writeBytes(ProtocolType.BGMP.getBytes());
        //命令序列号 4byte 正整数
        bgmpBuf.writeInt(Integer.decode(fiLinkReqParams.getCmdId()));
        //设备ID 4byte 正整数
        bgmpBuf.writeInt(equipmentId);
        //命令类型 2byte 正整数
        bgmpBuf.writeShort(fiLinkReqParams.getCmdType());
        //保留16byte
        bgmpBuf.writeLong(0);
        bgmpBuf.writeLong(0);
        //净荷长度 2byte
        bgmpBuf.writeShort(dataLength);
        return bgmpBuf;
    }


    /**
     * 获取data参数
     *
     * @param fiLinkReqParams       请求帧参数
     * @param fiLinkProtocolXmlBean 协议实体
     * @return ByteBuf
     */
    private ByteBuf getDataBuf(FiLinkReqParams fiLinkReqParams, FiLinkProtocolBean fiLinkProtocolXmlBean) throws Exception {
        String deviceId = fiLinkReqParams.getDeviceId();
        String cmdId = fiLinkReqParams.getCmdId();
        Integer cmdType = fiLinkReqParams.getCmdType();
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
            writeDataParamChild(dataParams, dataBuf, fiLinkReqParams.getParams(), deviceId);
        }
        return dataBuf;
    }


    /**
     * 写入dataParam子节点
     *
     * @param dataParams      xml dataParam集合
     * @param dataBuf         消息体字节缓冲
     * @param fiLinkReqParams 请求帧参数
     * @param deviceId        设施id
     */
    private void writeDataParamChild(List<DataParamsChild> dataParams, ByteBuf dataBuf, Map<String, Object> fiLinkReqParams, String deviceId) throws Exception {
        for (DataParamsChild dataParamsChild : dataParams) {
            if (dataParamsChild instanceof DataParam) {
                DataParam dataParam = (DataParam) dataParamsChild;
                setBufData(dataBuf, fiLinkReqParams, dataParam, deviceId);
            } else if (dataParamsChild instanceof ForEach) {
                ForEach forEach = (ForEach) dataParamsChild;
                logger.info("for each");
                writeForeach(forEach, fiLinkReqParams, dataBuf, deviceId);
            }
        }
    }


    /**
     * 写入循环体信息
     *
     * @param forEach         循环体对象
     * @param fiLinkReqParams 请求帧参数
     * @param deviceId        设施id
     * @param dataBuf         ByteBuf
     */
    private void writeForeach(ForEach forEach, Map<String, Object> fiLinkReqParams, ByteBuf dataBuf, String deviceId) throws Exception {
        List<DataParamsChild> itemList = forEach.getItemList();
        //获取循环参数
        List<Map<String, Object>> foreachParams = (List<Map<String, Object>>) fiLinkReqParams.get(ParamsKey.PARAMS_KEY);
        int count = foreachParams.size();
        for (int i = 0; i < count; i++) {
            //根据dataClass code获取dataParam
            writeDataParamChild(itemList, dataBuf, foreachParams.get(i), deviceId);
        }
    }


    /**
     * 执行handler
     *
     * @param dataFormat 数据格式化
     * @param byteBuf    字节缓存流
     * @return 处理后结果
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
     * 设置随机数
     *
     * @param length   长度
     * @param dataBuf  ByteBuf
     * @param deviceId 设施id
     */
    private void setRandomLen(Integer length, ByteBuf dataBuf, String deviceId) {

        Object obj = RedisUtils.hGet(RedisKey.RANDOM_KEY, deviceId);
        if (obj == null) {
            //生成随机数并存到redis中
            RedisUtils.hSet(RedisKey.RANDOM_KEY, deviceId, 1);
            dataBuf.writeByte(1);
        } else {
            int redisRandom = Integer.parseInt(obj.toString());
            RedisUtils.hSet(RedisKey.RANDOM_KEY, deviceId, 1 == redisRandom ? 2 : 1);
            dataBuf.writeByte(1 == redisRandom ? 2 : 1);
        }
    }

    /**
     * 设置数据包参数长度
     *
     * @param dataBuf         字节缓冲
     * @param fiLinkReqParams 请求帧参数
     * @param dataParam       xml dataParam对象信息
     */
    private void setBufData(ByteBuf dataBuf, Map<String, Object> fiLinkReqParams, DataParam dataParam, String deviceId) {
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
                setRandomLen(dataParam.getLength(), dataBuf, deviceId);
                return;
            }
            //判断是否有数据格式化
            DataFormat dataFormat = dataParam.getDataFormat();
            if (dataFormat != null) {
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
            //将数据写入byteBuf
            ByteBufUtil.setDataBuf(type, dataSource.toString(), length, dataBuf);
            //处理集map,判断是否是dataClass
            if (chooseMap != null) {
                Choose choose = chooseMap.get(dataSource);
                List<DataParamsChild> dataParams = choose.getDataParams();
                writeDataParamChild(dataParams, dataBuf, fiLinkReqParams, deviceId);
            }
        } catch (Exception e) {
            throw new RequestException("setBufData: " + dataParam.getId() + " failed>>>>>>");
        }
    }

    /**
     * 根据key值获取code
     *
     * @param key       key值
     * @param chooseMap dataClass处理类
     * @return String
     */
    private String getDataCodeByKey(String key, Map<String, Choose> chooseMap) {
        for (Map.Entry<String, Choose> entry : chooseMap.entrySet()) {
            Choose choose = entry.getValue();
            if (choose == null) {
                throw new RequestException("getDataCodeByKey choose is null>>>>>>>>>>>>>>");
            }
            if (key.equals(choose.getId())) {
                return choose.getCode();
            }
        }
        throw new RequestException("there is no code mapped to the key: " + key);
    }


    /**
     * 设置保留字节
     *
     * @param length  长度
     * @param dataBuf 字节缓冲
     * @param data    int
     */
    private void setReservedLen(int length, ByteBuf dataBuf, int data) {
        for (int i = 0; i < length; i++) {
            dataBuf.writeByte(data);
        }
    }


}
