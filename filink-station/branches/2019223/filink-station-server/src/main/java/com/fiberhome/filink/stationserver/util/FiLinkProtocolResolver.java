package com.fiberhome.filink.stationserver.util;

import com.fiberhome.filink.protocol.bean.xmlBean.FiLinkProtocolBean;
import com.fiberhome.filink.protocol.bean.xmlBean.data.*;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormat;
import com.fiberhome.filink.protocol.bean.xmlBean.format.DataFormatParam;
import com.fiberhome.filink.protocol.bean.xmlBean.header.ResponseHeader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析xml
 *
 * @author CongcaiYu
 */
@Component
public class FiLinkProtocolResolver {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理结果集map
     */
    private Map<String, Map<String, String>> resultMaps = null;
    /**
     * 数据处理类map
     */
    private Map<String, DataFormat> handlerParams = null;
    /**
     * dataClass处理map
     */
    private Map<String, Map<String, Choose>> chooseHandlerMap = null;

    /**
     * resultMap节点名称
     */
    private final String RESULT_MAP_NODE = "resultMaps";
    /**
     * resultMapId节点名称
     */
    private final String RESULT_MAP_ID_NODE = "id";
    /**
     * resultMapCode节点名称
     */
    private final String RESULT_MAP_CODE_NODE = "code";
    /**
     * resultMapMsg节点名称
     */
    private final String RESULT_MAP_MSG_NODE = "msg";

    /**
     * dataFormats节点名称
     */
    private final String DATA_FORMATS_NODE = "dataFormats";
    /**
     * dataFormatId节点名称
     */
    private final String DATA_FORMAT_ID_NODE = "id";
    /**
     * dataFormatClassName节点名称
     */
    private final String DATA_FORMAT_CLASS_NAME_NODE = "className";
    /**
     * dataFormatResultMap节点名称
     */
    private final String DATA_FORMAT_RESULT_MAP_NODE = "resultMap";

    /**
     * dataFormatParam id节点名称
     */
    private final String DATA_FORMAT_PARAM_ID_NODE = "id";
    /**
     * dataFormatParam length节点名称
     */
    private final String DATA_FORMAT_PARAM_LENGTH_NODE = "length";
    /**
     * dataFormatParam name节点名称
     */
    private final String DATA_FORMAT_PARAM_NAME_NODE = "name";
    /**
     * dataFormatParam type节点名称
     */
    private final String DATA_FORMAT_PARAM_TYPE_NODE = "type";

    /**
     * chooseHandlers 节点名称
     */
    private final String CHOOSE_HANDLERS_NODE = "chooseHandlers";
    /**
     * chooseHandler id节点名称
     */
    private final String CHOOSE_HANDLER_ID_NODE = "id";
    /**
     * choose id节点名称
     */
    private final String CHOOSE_ID_NODE = "id";
    /**
     * choose code节点名称
     */
    private final String CHOOSE_CODE_NODE = "code";
    /**
     * choose name节点名称
     */
    private final String CHOOSE_NAME_NODE = "name";

    /**
     * request节点名称
     */
    private final String REQUEST_NODE = "request";
    /**
     * response节点名称
     */
    private final String RESPONSE_NODE = "response";

    /**
     * datas节点名称
     */
    private final String DATA_LIST_NODE = "datas";
    /**
     * response success节点名称
     */
    private final String RESPONSE_SUCCESS_NODE = "success";
    /**
     * resoonse failed节点名称
     */
    private final String RESPONSE_FAILED_NODE = "failed";

    /**
     * cmdId节点名称
     */
    private final String CMD_ID_NODE = "cmdId";
    /**
     * data request节点名称
     */
    private final String DATA_REQUEST_NODE = "dataRequest";
    /**
     * data response节点名称
     */
    private final String DATA_RESPONSE_NODE = "dataResponse";
    /**
     * dataParams节点名称
     */
    private final String DATA_PARAMS_NODE = "dataParams";
    /**
     * dataParam节点名称
     */
    private final String DATA_PARAM_NODE = "dataParam";
    /**
     * foreach节点名称
     */
    private final String FOREACH_NODE = "foreach";
    /**
     * countReference节点名称
     */
    private final String COUNT_REFERENCE_NODE = "countReference";

    /**
     * dataParam reserved节点名称
     */
    private final String DATA_PARAM_RESERVED_NODE = "reserved";
    /**
     * dataParam random节点名称
     */
    private final String DATA_PARAM_RANDOM_NODE = "random";
    /**
     * dataFormat节点名称
     */
    private final String DATA_PARAM_DATA_FORMAT_NODE = "dataFormat";
    /**
     * dataParam lengthReference节点名称
     */
    private final String DATA_PARAM_LENGTH_REFERENCE_NODE = "lengthReference";
    /**
     * chooseHandler节点名称
     */
    private final String DATA_PARAM_CHOOSE_HANDLER_NODE = "chooseHandler";
    /**
     * dataParam foreach节点名称
     */
    private final String DATA_PARAM_FOREACH_NODE = "foreach";
    /**
     * dataParam length节点名称
     */
    private final String DATA_PARAM_LENGTH_NODE = "length";
    /**
     * dataParam data节点名称
     */
    private final String DATA_PARAM_DATA_NODE = "data";

    /**
     * dataParam id节点名称
     */
    private final String DATA_PARAM_ID_NODE = "id";
    /**
     * dataParam name节点名称
     */
    private final String DATA_PARAM_NAME_NODE = "name";
    /**
     * dataParam type节点名称
     */
    private final String DATA_PARAM_TYPE_NODE = "type";
    /**
     * dataParam resultMap 节点名称
     */
    private final String DATA_PARAM_RESULT_MAP_NODE = "resultMap";

    /**
     * 硬件版本
     */
    private final String HARDWARE_VERSION = "hardwareVersion";
    /**
     * 软件版本
     */
    private final String SOFTWARE_VERSION = "softwareVersion";


    /**
     * 解析data体
     * @param inputStream InputStream
     * @return List
     */
    public FiLinkProtocolBean resolve(InputStream inputStream) {
        try {
            FiLinkProtocolBean fiLinkProtocolXmlBean = new FiLinkProtocolBean();
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            Element root = document.getRootElement();
            //解析软硬件版本
            fiLinkProtocolXmlBean.setHardwareVersion(root.elementText(HARDWARE_VERSION));
            fiLinkProtocolXmlBean.setSoftwareVersion(root.elementText(SOFTWARE_VERSION));
            //解析resultMap
            resolveResultMap(root);
            //解析dataFormats
            resolveDataFormats(root);
            //解析chooseHandlers
            resolveChooseHandlers(root);
            //解析请求帧
            Element requestElement = root.element(REQUEST_NODE);
            //解析响应帧
            Element responseElement = root.element(RESPONSE_NODE);
            ResponseHeader responseHeader = resolveResponse(responseElement);
            fiLinkProtocolXmlBean.setResponseHeader(responseHeader);
            //解析data
            Map<String, Data> dataMap = new HashMap<>(64);
            List<Element> dataElementList = root.element(DATA_LIST_NODE).elements();
            for (Element dataElement : dataElementList) {
                Data data = resolveData(dataElement);
                dataMap.put(data.getCmdId(), data);
            }
            fiLinkProtocolXmlBean.setDataMap(dataMap);
            return fiLinkProtocolXmlBean;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("resolve xml failed>>>>>>>>" + e.toString());
        }
        return null;
    }

    /**
     * 解析chooseHandlers
     *
     * @param root 根节点
     */
    private void resolveChooseHandlers(Element root) {
        List<Element> chooseHandlersElement = root.element(CHOOSE_HANDLERS_NODE).elements();
        if (chooseHandlersElement != null && chooseHandlersElement.size() > 0) {
            chooseHandlerMap = new HashMap<>(64);
            //循环handler
            for (Element chooseHandlerElement : chooseHandlersElement) {
                String chooseHandlerId = chooseHandlerElement.attributeValue(CHOOSE_HANDLER_ID_NODE);
                List<Element> choosesElement = chooseHandlerElement.elements();
                //循环handler参数
                Map<String, Choose> chooseHandlerParamMap = new HashMap<>(64);
                for (Element chooseElement : choosesElement) {
                    Choose choose = new Choose();
                    choose.setId(chooseElement.attributeValue(CHOOSE_ID_NODE));
                    String code = chooseElement.attributeValue(CHOOSE_CODE_NODE);
                    choose.setCode(code);
                    choose.setName(chooseElement.attributeValue(CHOOSE_NAME_NODE));
                    List<DataParamsChild> dataParams = new ArrayList<>();
                    //解析choose的dataParam
                    List<Element> chooseDataParamsElement = chooseElement.elements();
                    for (Element chooseDataParam : chooseDataParamsElement) {
                        DataParam dataParam = resolveDataParam(chooseDataParam);
                        dataParams.add(dataParam);
                    }
                    choose.setDataParams(dataParams);
                    chooseHandlerParamMap.put(code, choose);
                }
                chooseHandlerMap.put(chooseHandlerId, chooseHandlerParamMap);
            }
        }
    }

    /**
     * 解析dataFormats
     *
     * @param root 根节点
     */
    private void resolveDataFormats(Element root) {
        List<Element> dataFormatsElement = root.element(DATA_FORMATS_NODE).elements();
        if (dataFormatsElement != null && dataFormatsElement.size() > 0) {
            handlerParams = new HashMap<>(64);
            for (Element dataFormatElement : dataFormatsElement) {
                DataFormat dataFormat = new DataFormat();
                String formatId = dataFormatElement.attributeValue(DATA_FORMAT_ID_NODE);
                String className = dataFormatElement.attributeValue(DATA_FORMAT_CLASS_NAME_NODE);
                String resultMapId = dataFormatElement.attributeValue(DATA_FORMAT_RESULT_MAP_NODE);
                dataFormat.setResultMap(resultMaps.get(resultMapId));
                dataFormat.setId(formatId);
                dataFormat.setClassName(className);
                List<Element> dataFormatsParams = dataFormatElement.elements();
                //循环handler参数
                List<DataFormatParam> dataFormatParamList = new ArrayList<>();
                for (Element dataFormatParam : dataFormatsParams) {
                    DataFormatParam formatParam = new DataFormatParam();
                    formatParam.setId(dataFormatParam.attributeValue(DATA_FORMAT_PARAM_ID_NODE));
                    formatParam.setLength(Integer.parseInt(dataFormatParam.attributeValue(DATA_FORMAT_PARAM_LENGTH_NODE)));
                    formatParam.setName(dataFormatParam.attributeValue(DATA_FORMAT_PARAM_NAME_NODE));
                    formatParam.setType(dataFormatParam.attributeValue(DATA_FORMAT_PARAM_TYPE_NODE));
                    dataFormatParamList.add(formatParam);
                }
                dataFormat.setDataFormatParams(dataFormatParamList);
                handlerParams.put(formatId, dataFormat);
            }
        }
    }

    /**
     * 解析resultMap
     *
     * @param root 根节点
     */
    private void resolveResultMap(Element root) {
        List<Element> resultMapsElement = root.element(RESULT_MAP_NODE).elements();
        if (resultMapsElement != null && resultMapsElement.size() > 0) {
            //resultMaps
            resultMaps = new HashMap<>(64);
            for (Element resultMapElement : resultMapsElement) {
                String resultMapId = resultMapElement.attributeValue(RESULT_MAP_ID_NODE);
                //遍历dataResult
                List<Element> dataResultsElement = resultMapElement.elements();
                Map<String, String> resultMap = new HashMap<>(64);
                for (Element dataResultElement : dataResultsElement) {
                    String code = dataResultElement.attributeValue(RESULT_MAP_CODE_NODE);
                    String msg = dataResultElement.attributeValue(RESULT_MAP_MSG_NODE);
                    resultMap.put(code, msg);
                }
                resultMaps.put(resultMapId, resultMap);
            }
        }
    }


    /**
     * 解析响应帧
     *
     * @param responseElement Element
     * @return ResponseHeader
     */
    private ResponseHeader resolveResponse(Element responseElement) {
        ResponseHeader responseHeader = new ResponseHeader();
        //解析 成功的响应帧参数
        Element responseSuccess = responseElement.element(RESPONSE_SUCCESS_NODE);
        List<DataParamsChild> responseSuccessList = resolveParams(responseSuccess, "responseHeader success");
        //解析 失败的响应帧参数
        Element responseFailed = responseElement.element(RESPONSE_FAILED_NODE);
        List<DataParamsChild> responseFailedList = resolveParams(responseFailed, "responseHeader failed");
        responseHeader.setFailedResponse(responseFailedList);
        responseHeader.setSuccessResponse(responseSuccessList);
        return responseHeader;
    }

    /**
     * 解析每个data体
     *
     * @param dataElement Element
     * @return Data
     */
    private Data resolveData(Element dataElement) {
        Data data = new Data();
        String cmdId = dataElement.attributeValue(CMD_ID_NODE);
        data.setCmdId(cmdId);
        //解析request
        DataRequest dataRequest = resolveDataRequest(dataElement);
        data.setDataRequest(dataRequest);
        //解析response
        DataResponse dataResponse = resolveDataResponse(dataElement);
        data.setDataResponse(dataResponse);
        return data;
    }

    /**
     * 解析data体的request
     *
     * @param dataElement Element
     * @return DataRequest
     */
    private DataRequest resolveDataRequest(Element dataElement) {
        DataRequest dataRequest = new DataRequest();
        Element requestElement = dataElement.element(DATA_REQUEST_NODE);
        if (requestElement == null || requestElement.elements() == null || requestElement.elements().size() == 0) {
            logger.info(dataElement.attributeValue(CMD_ID_NODE) + " :request is null>>>>>>>>>");
            return dataRequest;
        }
        //解析params
        List<DataParamsChild> dataParams = resolveParams(requestElement, "dataRequest");
        dataRequest.setDataParams(dataParams);
        return dataRequest;
    }


    /**
     * 解析data体的response
     *
     * @param dataElement Element
     * @return DataResponse
     */
    private DataResponse resolveDataResponse(Element dataElement) {
        DataResponse dataResponse = new DataResponse();
        Element responseElement = dataElement.element(DATA_RESPONSE_NODE);
        if (responseElement == null || responseElement.elements() == null || responseElement.elements().size() == 0) {
            logger.info(dataElement.attributeValue(CMD_ID_NODE) + " :response is null>>>>>>>>>");
            return dataResponse;
        }
        //解析params
        List<DataParamsChild> dataParams = resolveParams(responseElement, "dataResponse");
        dataResponse.setDataParams(dataParams);
        return dataResponse;
    }

    /**
     * 解析具体参数param
     *
     * @param resElement Element
     * @return List
     */
    private List<DataParamsChild> resolveParams(Element resElement, String message) {
        Element dataParamsElement = resElement.element(DATA_PARAMS_NODE);
        List<Element> dataParamList = dataParamsElement.elements();
        if (dataParamList == null || dataParamList.size() == 0) {
            logger.info("xml tag: ( " + message + " ) data params list is null>>>>>>>>>>>");
            return null;
        }
        return resolveParam(dataParamList);
    }

    /**
     * 处理param
     *
     * @param dataParamList List<Element>
     * @return List<DataParam>
     */
    private List<DataParamsChild> resolveParam(List<Element> dataParamList) {
        List<DataParamsChild> dataParams = new ArrayList<>();
        //解析param
        for (Element dataParamElement : dataParamList) {
            String qName = dataParamElement.getQName().getName();
            //dataParam标签
            if (DATA_PARAM_NODE.equalsIgnoreCase(qName)) {
                DataParam dataParam = resolveDataParam(dataParamElement);
                dataParams.add(dataParam);
            } else if (FOREACH_NODE.equalsIgnoreCase(qName)) {
                //有循环体的部分
                ForEach forEach = new ForEach();
                String countReference = dataParamElement.attributeValue(COUNT_REFERENCE_NODE);
                forEach.setReferenceId(countReference);
                List<Element> foreachDataParamElement = dataParamElement.elements();
                List<DataParamsChild> dataParamsChildList = resolveForeachDataParam(foreachDataParamElement);
                forEach.setItemList(dataParamsChildList);
                dataParams.add(forEach);
            }
        }
        return dataParams;
    }


    /**
     * 解析遍历项
     *
     * @param foreachDataParamElement List<Element>
     * @return List<DataParamsChild>
     */
    private List<DataParamsChild> resolveForeachDataParam(List<Element> foreachDataParamElement) {
        try {
            List<DataParamsChild> dataParamsChildList = new ArrayList<>();
            for (Element foreachElement : foreachDataParamElement) {
                DataParam dataParam = resolveDataParam(foreachElement);
                dataParamsChildList.add(dataParam);
            }
            return dataParamsChildList;
        } catch (Exception e) {
            logger.info("resolve foreach xml failed>>>>>>>>>>>>");
        }
        return null;
    }

    /**
     * 解析dataParam
     *
     * @param dataParamElement Element
     * @return DataParam
     */
    private DataParam resolveDataParam(Element dataParamElement) {
        DataParam dataParam = new DataParam();
        //获取reserved
        String reserved = dataParamElement.attributeValue(DATA_PARAM_RESERVED_NODE);
        String random = dataParamElement.attributeValue(DATA_PARAM_RANDOM_NODE);
        String dataFormat = dataParamElement.attributeValue(DATA_PARAM_DATA_FORMAT_NODE);
        String lengthReference = dataParamElement.attributeValue(DATA_PARAM_LENGTH_REFERENCE_NODE);
        String resultHandler = dataParamElement.attributeValue(DATA_PARAM_CHOOSE_HANDLER_NODE);
        String foreach = dataParamElement.attributeValue(DATA_PARAM_FOREACH_NODE);
        String data = dataParamElement.attributeValue(DATA_PARAM_DATA_NODE);
        //判断有无引用lengthReference
        if (!StringUtils.isEmpty(lengthReference)) {
            dataParam.setLengthRef(lengthReference);
        } else {
            //获取长度
            String lengthStr = dataParamElement.attributeValue(DATA_PARAM_LENGTH_NODE);
            if (StringUtils.isEmpty(lengthStr)) {
                logger.info(dataParamElement.attributeValue("name") + " : length is null>>>>>>>>>>>");
                return null;
            }
            int length = Integer.parseInt(lengthStr);
            dataParam.setLength(length);
        }
        String trueStr = "true";
        //保留帧
        if (trueStr.equals(reserved)) {
            dataParam.setReserved(true);
            return dataParam;
        }
        //随机数
        if (trueStr.equals(random)) {
            dataParam.setRandom(true);
        }
        //是否是循环次数
        if (trueStr.equals(foreach)) {
            dataParam.setForeach(true);
        }
        //获取data
        if (!StringUtils.isEmpty(data)) {
            dataParam.setData(data);
        }
        //获取dataFormat
        if (!StringUtils.isEmpty(dataFormat)) {
            dataParam.setDataFormat(handlerParams.get(dataFormat));
        }
        //获取chooseHandler
        if (!StringUtils.isEmpty(resultHandler)) {
            dataParam.setChooseMap(chooseHandlerMap.get(resultHandler));
        }
        //获取id
        String id = dataParamElement.attributeValue(DATA_PARAM_ID_NODE);
        dataParam.setId(id);
        //获取name
        String name = dataParamElement.attributeValue(DATA_PARAM_NAME_NODE);
        dataParam.setName(name);
        //获取type
        String type = dataParamElement.attributeValue(DATA_PARAM_TYPE_NODE);
        dataParam.setType(type);
        //解析result
        String resultMapId = dataParamElement.attributeValue(DATA_PARAM_RESULT_MAP_NODE);
        if (!StringUtils.isEmpty(resultMapId)) {
            Map<String, String> resultMap = resultMaps.get(resultMapId);
            if (resultMap == null) {
                logger.info("get resultMap is error: " + resultMapId + ">>>>>>>>>>>>>");
            }
            dataParam.setResultMap(resultMap);
        }
        return dataParam;
        //判断有没有嵌套dataParam
//            List<Element> innerDataParamsElement = dataParamElement.elements();
//            if (innerDataParamsElement != null && innerDataParamsElement.size() > 0) {
//                recurseResolveParam(dataParam,innerDataParamsElement);
//            }
    }
}
