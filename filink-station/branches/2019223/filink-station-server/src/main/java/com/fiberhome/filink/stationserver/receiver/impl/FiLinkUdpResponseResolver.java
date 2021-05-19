package com.fiberhome.filink.stationserver.receiver.impl;

import com.fiberhome.filink.protocol.bean.xmlBean.AbstractProtocolBean;
import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.protocol.bean.xmlBean.data.*;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.stationserver.entity.param.AbstractResInputParams;
import com.fiberhome.filink.stationserver.entity.param.AbstractResOutputParams;
import com.fiberhome.filink.stationserver.entity.protocol.Bgmp;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResInputParams;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResOutputParams;
import com.fiberhome.filink.stationserver.entity.protocol.FiLinkResponse;
import com.fiberhome.filink.stationserver.handler.DataHandler;
import com.fiberhome.filink.stationserver.exception.ResponseException;
import com.fiberhome.filink.stationserver.receiver.ResponseResolver;
import com.fiberhome.filink.stationserver.util.HexUtil;
import com.fiberhome.filink.stationserver.util.lockenum.CmdType;
import com.fiberhome.filink.stationserver.util.lockenum.DataType;
import com.fiberhome.filink.stationserver.util.lockenum.Head;
import com.fiberhome.filink.stationserver.util.lockenum.ParamsKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filink 响应帧解析
 *
 * @author CongcaiYu
 */
@Component("fiLinkUdpResponseResolver")
public class FiLinkUdpResponseResolver implements ResponseResolver {

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
        FiLinkResInputParams resInputParams;
        FiLinkProtocolBean fiLinkProtocolBean;
        try {
            resInputParams = (FiLinkResInputParams) inputParams;
            AbstractProtocolBean protocolBean = resInputParams.getProtocolBean();
            fiLinkProtocolBean = (FiLinkProtocolBean) protocolBean;
        } catch (Exception e) {
            throw new ResponseException("inputParams parse exception>>>>>>>>>>>");
        }
        String hexDataSource = resInputParams.getDataSource();
        //将16进制数据包转换成byteBuf
        byte[] bytes = HexUtil.hexStringToByte(hexDataSource);
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        FiLinkResponse response = new FiLinkResponse();
        //参数信息
        Map<String, Object> dataResponseMap = new HashMap<>(64);
        FiLinkResOutputParams resParams = new FiLinkResOutputParams();
        //帧地址头
        ByteBuf headBuf = buf.readBytes(2);
        int head = HexUtil.bufToInt(headBuf);
        //心跳帧
        if (Head.HEART_BEAT == head) {
            resParams.setHeartBeat(true);
            logger.info("heart beat data>>>>>>>>>>>>>");
            return resParams;
        }
        //命令长度
        ByteBuf cmdLenBuf = buf.readBytes(2);
        int cmdLen = HexUtil.bufToInt(cmdLenBuf);
        response.setFrameHead(head);
        response.setCmdLen(cmdLen);
        //bgmp
        ByteBuf bgmpBuf = buf.readBytes(32);
        Bgmp bgmp = resolveBgmp(bgmpBuf);
        response.setBgmp(bgmp);
        //cmdOk
        if (CmdType.RESPONSE_TYPE == bgmp.getCmdType()) {
            ByteBuf cmdOkBuf = buf.readBytes(4);
            int cmdOk = HexUtil.bufToInt(cmdOkBuf);
            response.setCmdOk(cmdOk);
            if (response.getCmdOk() == null) {
                logger.info("response error>>>>>>>>>>>>");
                return resParams;
            }
        }
        ByteBuf dataBuf = buf.readBytes(buf.readableBytes());
        Map<String, Object> data = resolveData(dataBuf, bgmp, fiLinkProtocolBean, dataResponseMap);
        response.setDataBody(data);
        convertDataToUdpParams(response, resParams);
        return resParams;
    }


    /**
     * 转换对象
     *
     * @param response  FiLinkResponse
     * @param resParams FiLinkResOutputParams
     */
    private void convertDataToUdpParams(FiLinkResponse response, FiLinkResOutputParams resParams) {
        Bgmp bgmp = response.getBgmp();
        resParams.setDeviceId(String.valueOf(bgmp.getEquipmentId()));
        Integer cmdSequenceId = bgmp.getCmdSequenceId();
        resParams.setCmdId(HexUtil.intToHexInt(cmdSequenceId));
        Map<String, Object> dataBody = response.getDataBody();
        resParams.setParams(dataBody);
    }

    /**
     * 解析data数据体
     *
     * @param dataBuf            ByteBuf
     * @param bgmp               Bgmp
     * @param fiLinkProtocolBean FiLinkProtocolBean
     * @param dataResponseMap    Map<String,Object>
     * @return Map<String       ,       String>
     */
    private Map<String, Object> resolveData(ByteBuf dataBuf, Bgmp bgmp, FiLinkProtocolBean fiLinkProtocolBean,
                                            Map<String, Object> dataResponseMap) {
        //从redis中获取协议信息
        Map<String, Data> dataMap = fiLinkProtocolBean.getDataMap();
        Data data = dataMap.get(HexUtil.intToHexInt(bgmp.getCmdSequenceId()));
        if (data == null) {
            logger.info("data is not exist>>>>>>>>>>>>>>");
            return dataResponseMap;
        }
        List<DataParamsChild> dataParams = null;
        if (CmdType.REQUEST_TYPE == bgmp.getCmdType()) {
            dataParams = data.getDataRequest().getDataParams();
        } else if (CmdType.RESPONSE_TYPE == bgmp.getCmdType()) {
            dataParams = data.getDataResponse().getDataParams();
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
     * @param dataBuf         ByteBuf
     * @param dataResponseMap Map<String, Object>
     * @param dataParams      List<DataParamsChild>
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
     * @param forEach         ForEach
     * @param dataResponseMap Map<String, Object>
     * @param byteBuf         ByteBuf
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
     * @param dataParam       DataParam
     * @param dataBuf         ByteBuf
     * @param dataResponseMap Map<String, Object>
     */
    private void resolveDataParam(DataParam dataParam, ByteBuf dataBuf, Map<String, Object> dataResponseMap) {
        try {
            //如果为保留字节
            if (dataParam.isReserved()) {
                dataBuf.readBytes(dataParam.getLength());
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
                param = String.valueOf(HexUtil.bufToInt(dataParamBuf));
                //String类型
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
                dataResponseMap.put(dataParam.getId(), choose.getId());
                //循环dataClass参数
                List<DataParamsChild> dataParams = choose.getDataParams();
                resolveDataParamsChild(dataBuf, dataResponseMap, dataParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("resolve dataParam failed : " + dataParam.getName());
            throw new ResponseException("resolve dataParam failed : " + dataParam.getName());
        }

    }

    /**
     * 执行handler
     *
     * @param dataFormat DataFormat
     * @param byteBuf    ByteBuf
     * @return Object
     * @throws Exception Exception
     */
    private Object execute(DataFormat dataFormat, ByteBuf byteBuf) throws Exception {
        Class<?> dataFormatClazz = Class.forName(dataFormat.getClassName());
        DataHandler handler = (DataHandler) dataFormatClazz.newInstance();
        return handler.handle(dataFormat, byteBuf);
    }


    /**
     * 解析BGMP
     *
     * @param bgmpBuf ByteBuf
     * @return Bgmp
     */
    private Bgmp resolveBgmp(ByteBuf bgmpBuf) {
        Bgmp bgmp = new Bgmp();
        //协议标识
        String protocolFlag = HexUtil.bufToStr(bgmpBuf.readBytes(4));
        //命令序列号
        int cmdId = HexUtil.bufToInt(bgmpBuf.readBytes(4));
        //设备id
        int equipmentId = HexUtil.bufToInt(bgmpBuf.readBytes(4));
        //命令帧
        int cmdType = HexUtil.bufToInt(bgmpBuf.readBytes(2));
        //保留16byte
        bgmpBuf.readBytes(16);
        //净荷长度
        int len = HexUtil.bufToInt(bgmpBuf.readBytes(2));
        bgmp.setLen(len);
        bgmp.setCmdSequenceId(cmdId);
        bgmp.setCmdType(cmdType);
        bgmp.setEquipmentId(equipmentId);
        bgmp.setProtocolFlag(protocolFlag);
        return bgmp;
    }

}
