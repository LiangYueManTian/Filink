package com.fiberhome.filink.commonstation.utils;

import com.fiberhome.filink.commonstation.entity.xmlbean.FiLinkProtocolBean;
import com.fiberhome.filink.commonstation.entity.xmlbean.data.*;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormat;
import com.fiberhome.filink.commonstation.entity.xmlbean.format.DataFormatParam;
import com.fiberhome.filink.commonstation.entity.xmlbean.header.HeaderParam;
import com.fiberhome.filink.commonstation.exception.ProtocolException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class FiLinkProtocolResolver {

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
     * id 节点名称
     */
    private static final String HEADER_PARAM_ID_NODE = "id";
    /**
     * name 节点名称
     */
    private static final String HEADER_PARAM_NAME_NODE = "name";
    /**
     * type 节点名称
     */
    private static final String HEADER_PARAM_TYPE_NODE = "type";
    /**
     * length 节点名称
     */
    private static final String HEADER_PARAM_LENGTH_NODE = "length";
    /**
     * reserved 节点名称
     */
    private static final String HEADER_PARAM_RESERVED_NODE = "reserved";
    /**
     * reqScope 节点名称
     */
    private static final String HEADER_PARAM_REQ_SCOPE_NODE = "reqScope";
    /**
     * resScope 节点名称
     */
    private static final String HEADER_PARAM_RES_SCOPE_NODE = "resScope";
    /**
     * dataFormat 节点名称
     */
    private static final String HEADER_PARAM_DATA_FORMAT = "dataFormat";

    /**
     * resultMaps 节点名称
     */
    private static final String RESULT_MAP_NODE = "resultMaps";
    /**
     * id 节点名称
     */
    private static final String RESULT_MAP_ID_NODE = "id";
    /**
     * code 节点名称
     */
    private static final String RESULT_MAP_CODE_NODE = "code";
    /**
     * msg 节点名称
     */
    private static final String RESULT_MAP_MSG_NODE = "msg";

    /**
     * dataFormats 节点名称
     */
    private static final String DATA_FORMATS_NODE = "dataFormats";
    /**
     * id 节点名称
     */
    private static final String DATA_FORMAT_ID_NODE = "id";
    /**
     * className 节点名称
     */
    private static final String DATA_FORMAT_CLASS_NAME_NODE = "className";
    /**
     * resultMap 节点名称
     */
    private static final String DATA_FORMAT_RESULT_MAP_NODE = "resultMap";

    /**
     * id 节点名称
     */
    private static final String DATA_FORMAT_PARAM_ID_NODE = "id";
    /**
     * length 节点名称
     */
    private static final String DATA_FORMAT_PARAM_LENGTH_NODE = "length";
    /**
     * name 节点名称
     */
    private static final String DATA_FORMAT_PARAM_NAME_NODE = "name";
    /**
     * type 节点名称
     */
    private static final String DATA_FORMAT_PARAM_TYPE_NODE = "type";

    /**
     * chooseHandlers 节点名称
     */
    private static final String CHOOSE_HANDLERS_NODE = "chooseHandlers";
    /**
     * id 节点名称
     */
    private static final String CHOOSE_HANDLER_ID_NODE = "id";
    /**
     * id 节点名称
     */
    private static final String CHOOSE_ID_NODE = "id";
    /**
     * code 节点名称
     */
    private static final String CHOOSE_CODE_NODE = "code";
    /**
     * name 节点名称
     */
    private static final String CHOOSE_NAME_NODE = "name";

    /**
     * header 节点名称
     */
    private static final String COMMON_HEADER_NODE = "header";
    /**
     * requestHeader 节点名称
     */
    private static final String REQUEST_HEADER_NODE = "requestHeader";
    /**
     * responseHeader 节点名称
     */
    private static final String RESPONSE_HEADER_NODE = "responseHeader";

    /**
     * datas节点名称
     */
    private static final String DATA_LIST_NODE = "datas";

    /**
     * cmdId 节点名称
     */
    private static final String CMD_ID_NODE = "cmdId";
    /**
     * dataRequest 节点名称
     */
    private static final String DATA_REQUEST_NODE = "dataRequest";
    /**
     * dataResponse 节点名称
     */
    private static final String DATA_RESPONSE_NODE = "dataResponse";
    /**
     * dataParams 节点名称
     */
    private static final String DATA_PARAMS_NODE = "dataParams";
    /**
     * dataParam 节点名称
     */
    private static final String DATA_PARAM_NODE = "dataParam";
    /**
     * foreach 节点名称
     */
    private static final String FOREACH_NODE = "foreach";
    /**
     * countReference 节点名称
     */
    private static final String COUNT_REFERENCE_NODE = "countReference";

    /**
     * reserved 节点名称
     */
    private static final String DATA_PARAM_RESERVED_NODE = "reserved";
    /**
     * random 节点名称
     */
    private static final String DATA_PARAM_RANDOM_NODE = "random";
    /**
     * dataFormat 节点名称
     */
    private static final String DATA_PARAM_DATA_FORMAT_NODE = "dataFormat";
    /**
     * lengthReference 节点名称
     */
    private static final String DATA_PARAM_LENGTH_REFERENCE_NODE = "lengthReference";
    /**
     * chooseHandler 节点名称
     */
    private static final String DATA_PARAM_CHOOSE_HANDLER_NODE = "chooseHandler";
    /**
     * foreach 节点名称
     */
    private static final String DATA_PARAM_FOREACH_NODE = "foreach";
    /**
     * length 节点名称
     */
    private static final String DATA_PARAM_LENGTH_NODE = "length";
    /**
     * data 节点名称
     */
    private static final String DATA_PARAM_DATA_NODE = "data";

    /**
     * id 节点名称
     */
    private static final String DATA_PARAM_ID_NODE = "id";
    /**
     * name 节点名称
     */
    private static final String DATA_PARAM_NAME_NODE = "name";
    /**
     * type 节点名称
     */
    private static final String DATA_PARAM_TYPE_NODE = "type";
    /**
     * resultMap 节点名称
     */
    private static final String DATA_PARAM_RESULT_MAP_NODE = "resultMap";

    /**
     * hardwareVersion 节点名称
     */
    private static final String HARDWARE_VERSION_NODE = "hardwareVersion";
    /**
     * softwareVersion 节点名称
     */
    private static final String SOFTWARE_VERSION_NODE = "softwareVersion";

    /**
     * requestResolverName 节点名称
     */
    private static final String REQUEST_RESOLVER_NAME_NODE = "requestResolverName";
    /**
     * responseResolverName 节点名称
     */
    private static final String RESPONSE_RESOLVER_NAME_NODE = "responseResolverName";
    /**
     * businessHandlerName 节点名称
     */
    private static final String BUSINESS_HANDLER_NAME_NODE = "businessHandlerName";
    /**
     * instructSenderName 节点名称
     */
    private static final String INSTRUCT_SENDER_NAME_NODE = "instructSenderName";


    /**
     * 解析data体
     *
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
            fiLinkProtocolXmlBean.setHardwareVersion(root.elementText(HARDWARE_VERSION_NODE));
            fiLinkProtocolXmlBean.setSoftwareVersion(root.elementText(SOFTWARE_VERSION_NODE));
            //解析处理类名
            fiLinkProtocolXmlBean.setRequestResolverName(root.elementText(REQUEST_RESOLVER_NAME_NODE));
            fiLinkProtocolXmlBean.setResponseResolverName(root.elementText(RESPONSE_RESOLVER_NAME_NODE));
            fiLinkProtocolXmlBean.setBusinessHandlerName(root.elementText(BUSINESS_HANDLER_NAME_NODE));
            fiLinkProtocolXmlBean.setInstructSenderName(root.elementText(INSTRUCT_SENDER_NAME_NODE));
            //解析resultMap
            resolveResultMap(root);
            //解析dataFormats
            resolveDataFormats(root);
            //解析chooseHandlers
            resolveChooseHandlers(root);
            //解析公共头
            fiLinkProtocolXmlBean.setCommonHeader(resolveHeader(root, COMMON_HEADER_NODE));
            //解析请求头
            fiLinkProtocolXmlBean.setRequestHeader(resolveHeader(root, REQUEST_HEADER_NODE));
            //解析响应头
            fiLinkProtocolXmlBean.setResponseHeader(resolveHeader(root, RESPONSE_HEADER_NODE));
            //解析scope
            resolveScope(fiLinkProtocolXmlBean);
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
            log.info("resolve xml failed {}", e);
        }
        return null;
    }

    /**
     * 解析cmdLen范围
     *
     * @param fiLinkProtocolXmlBean 协议对象
     */
    private void resolveScope(FiLinkProtocolBean fiLinkProtocolXmlBean) {
        //获取xml头对象信息
        List<HeaderParam> commonHeader = fiLinkProtocolXmlBean.getCommonHeader();
        List<HeaderParam> requestHeader = fiLinkProtocolXmlBean.getRequestHeader();
        List<HeaderParam> responseHeader = fiLinkProtocolXmlBean.getResponseHeader();
        List<HeaderParam> reqHeader = new ArrayList<>(commonHeader);
        List<HeaderParam> resHeader = new ArrayList<>(commonHeader);
        if (requestHeader != null && requestHeader.size() > 0) {
            reqHeader.addAll(requestHeader);
        }
        if (responseHeader != null && responseHeader.size() > 0) {
            resHeader.addAll(responseHeader);
        }
        //获取cmdLen字段
        HeaderParam cmdLenHeaderParam = null;
        for (HeaderParam headerParam : commonHeader) {
            String cmdLenStr = "cmdLen";
            if (cmdLenStr.equals(headerParam.getId())) {
                cmdLenHeaderParam = headerParam;
                break;
            }
        }
        if (cmdLenHeaderParam == null) {
            throw new ProtocolException("cmdLen config error>>>>>>");
        }
        String reqScope = cmdLenHeaderParam.getReqScope();
        String resScope = cmdLenHeaderParam.getResScope();
        cmdLenHeaderParam.setReqScope(setScope(reqScope, reqHeader));
        cmdLenHeaderParam.setResScope(setScope(resScope, resHeader));
    }

    /**
     * 设置cmdLen范围
     *
     * @param scope 范围字符串
     * @param eader 头参数信息
     * @return 解析后的范围
     */
    private String setScope(String scope, List<HeaderParam> eader) {
        //获取索引位置
        String scopeIndex = scope.split(" ")[0];
        String splitStr = "-";
        String[] splitArr = scopeIndex.split(splitStr);
        int startIndex = Integer.parseInt(splitArr[0]);
        int endIndex = Integer.parseInt(splitArr[1]);
        int totalLength = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            totalLength += eader.get(i).getLength();
        }
        String bodyStr = "body";
        String length = String.valueOf(totalLength);
        if (scope.contains(bodyStr)) {
            length = length + " " + bodyStr;
        }
        return length;
    }


    /**
     * 解析请求头配置参数
     *
     * @param nodeName 节点名称
     * @param root     根节点
     * @return 头信息
     */
    private List<HeaderParam> resolveHeader(Element root, String nodeName) {
        List<HeaderParam> headerParams = new ArrayList<>();
        Element requestElement = root.element(nodeName);
        List<Element> requestHeaderElements = requestElement.elements();
        //节点参数为空
        if (requestHeaderElements == null || requestHeaderElements.size() == 0) {
            return null;
        }
        //循环headerParam标签
        for (Element requestHeaderElement : requestHeaderElements) {
            //构造header对象set参数
            HeaderParam headerParam = new HeaderParam();
            //保留长度
            String reserved = requestHeaderElement.attributeValue(HEADER_PARAM_RESERVED_NODE);
            //id
            String id = requestHeaderElement.attributeValue(HEADER_PARAM_ID_NODE);
            //名称
            String name = requestHeaderElement.attributeValue(HEADER_PARAM_NAME_NODE);
            //类型
            String type = requestHeaderElement.attributeValue(HEADER_PARAM_TYPE_NODE);
            //长度
            String length = requestHeaderElement.attributeValue(HEADER_PARAM_LENGTH_NODE);
            //作用域
            String reqScope = requestHeaderElement.attributeValue(HEADER_PARAM_REQ_SCOPE_NODE);
            String resScope = requestHeaderElement.attributeValue(HEADER_PARAM_RES_SCOPE_NODE);
            //数据处理类
            String dataFormat = requestHeaderElement.attributeValue(HEADER_PARAM_DATA_FORMAT);
            headerParam.setLength(Integer.parseInt(length));
            String trueStr = "true";
            //保留帧
            if (trueStr.equals(reserved)) {
                headerParam.setReserved(true);
            }
            //数据处理类是否为空
            if (!StringUtils.isEmpty(dataFormat)) {
                headerParam.setDataFormat(handlerParams.get(dataFormat));
            }
            headerParam.setReqScope(reqScope);
            headerParam.setResScope(resScope);
            headerParam.setId(id);
            headerParam.setName(name);
            headerParam.setType(type);
            headerParams.add(headerParam);
        }
        return headerParams;
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
                if (dataFormatsParams != null && dataFormatsParams.size() > 0) {
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
                }
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
            log.info(" 解析节点｛｝:request is null>>>>>>>>>", dataElement.attributeValue(CMD_ID_NODE));
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
            log.info(" 解析节点｛｝:response is null>>>>>>>>>", dataElement.attributeValue(CMD_ID_NODE));
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
            log.info("xml tag: ( {} ) data params list is null>>>>>>>>>>>", message);
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
            log.info("resolve foreach xml failed:{}>>>>>>>>>>>>", e.getMessage());
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
            if (!StringUtils.isEmpty(lengthStr)) {
                int length = Integer.parseInt(lengthStr);
                dataParam.setLength(length);
            }
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
                log.info("get resultMap is error: {}>>>>>>>>>>>>>", resultMapId);
            }
            dataParam.setResultMap(resultMap);
        }
        return dataParam;
    }
}
