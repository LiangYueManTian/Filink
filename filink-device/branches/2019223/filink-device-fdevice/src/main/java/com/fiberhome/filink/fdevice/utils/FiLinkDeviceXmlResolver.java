package com.fiberhome.filink.fdevice.utils;

import com.fiberhome.filink.fdevice.bean.config.*;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设施配置解析类
 *
 * @author CongcaiYu
 */
@Slf4j
public class FiLinkDeviceXmlResolver {
    /**
     * devices节点名称
     */
    private static final String DEVICE_NODE = "devices";
    /**
     * rules节点名称
     */
    private static final String RULES_NODE = "rules";

    /**
     * rule id节点名称
     */
    private static final String RULE_ID_NODE = "id";
    /**
     * ruleParam max节点名称
     */
    private static final String RULE_PARAM_MAX_NODE = "max";
    /**
     * ruleParam min节点名称
     */
    private static final String RULE_PARAM_MIN_NODE = "min";
    /**
     * ruleParam maxLength节点名称
     */
    private static final String RULE_PARAM_MAX_LENGTH_NODE = "maxLength";
    /**
     * ruleParam minLength节点名称
     */
    private static final String RULE_PARAM_MIN_LENGTH_NODE = "minLength";
    /**
     * ruleParam pattern节点名称
     */
    private static final String RULE_PARAM_PATTERN_NODE = "pattern";
    /**
     * ruleParam required节点名称
     */
    private static final String RULE_PARAM_REQUIRED_NODE = "required";
    /**
     * ruleParam msg节点名称
     */
    private static final String RULE_PARAM_MSG_NODE = "msg";
    /**
     * ruleParam code节点名称
     */
    private static final String RULE_PARAM_CODE_NODE = "code";

    /**
     * deviceType节点名称
     */
    private static final String DEVICE_TYPE_NODE = "deviceType";
    /**
     * code节点名称
     */
    private static final String CODE_NODE = "code";

    /**
     * details节点名称
     */
    private static final String DETAILS_NODE = "details";
    /**
     * id节点名称
     */
    private static final String DETAIL_PARAM_ID = "id";
    /**
     * name节点名称
     */
    private static final String DETAIL_PARAM_NAME = "name";

    /**
     * configurations节点名称
     */
    private static final String CONFIGURATIONS_NODE = "configurations";
    /**
     * id节点名称
     */
    private static final String CONFIGURATION_ID_NODE = "id";
    /**
     * name节点名称
     */
    private static final String CONFIGURATION_NAME_NODE = "name";

    /**
     * configurationParam id节点名称
     */
    private static final String CONFIGURATION_PARAM_ID_NODE = "id";
    /**
     * configurationParam name节点名称
     */
    private static final String CONFIGURATION_PARAM_NAME_NODE = "name";
    /**
     * configurationParam type节点名称
     */
    private static final String CONFIGURATION_PARAM_TYPE_NODE = "type";
    /**
     * configurationParam code节点名称
     */
    private static final String CONFIGURATION_PARAM_CODE_NODE = "code";
    /**
     * configurationParam unit节点名称
     */
    private static final String CONFIGURATION_PARAM_UNIT_NODE = "unit";
    /**
     * configurationParam placeholder节点名称
     */
    private static final String CONFIGURATION_PARAM_PLACEHOLDER_NODE = "placeholder";

    /**
     * selectParam id节点名称
     */
    private static final String SELECT_PARAM_ID_NODE = "id";
    /**
     * selectParam name节点名称
     */
    private static final String SELECT_PARAM_NAME_NODE = "name";

    /**
     * 正则map
     */
    private static Map<String,List<Map<String,Object>>> ruleMap = new HashMap<>();


    /**
     * 解析设施配置文件
     *
     * @param inputStream 文件流
     * @throws Exception Exception
     */
    public static List<FiLinkDeviceConfig> resolveDeviceXml(InputStream inputStream) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element root = document.getRootElement();
        //解析校验规则
        resolveRules(root);
        List<FiLinkDeviceConfig> deviceConfigs = new ArrayList<>();
        //获取根节点
        List<Element> deviceElements = root.element(DEVICE_NODE).elements();
        for (Element deviceElement : deviceElements) {
            //解析每个device节点
            FiLinkDeviceConfig deviceConfig = resolveDeviceElement(deviceElement);
            deviceConfigs.add(deviceConfig);
        }
        return deviceConfigs;
    }

    /**
     * 解析校验规则
     *
     * @param rootElement Element
     */
    private static void resolveRules(Element rootElement) {
        //获取rules节点
        List<Element> rulesElement = rootElement.element(RULES_NODE).elements();
        for (Element ruleElement : rulesElement) {
            //获取rule id
            String ruleId = ruleElement.attributeValue(RULE_ID_NODE);
            //解析rule param
            List<Element> ruleParamsElement = ruleElement.elements();
            List<Map<String,Object>> ruleParamMapList = new ArrayList<>();
            for (Element ruleParamElement : ruleParamsElement) {
                Map<String,Object> ruleParamMap = new HashMap<>(64);
                //是否必填
                String required = ruleParamElement.attributeValue(RULE_PARAM_REQUIRED_NODE);
                if("true".equals(required)){
                    ruleParamMap.put(RULE_PARAM_REQUIRED_NODE,true);
                }
                //最大长度
                String maxLength = ruleParamElement.attributeValue(RULE_PARAM_MAX_LENGTH_NODE);
                if(!StringUtils.isEmpty(maxLength)){
                    ruleParamMap.put(RULE_PARAM_MAX_LENGTH_NODE,maxLength);
                }
                //最小长度
                String minLength = ruleParamElement.attributeValue(RULE_PARAM_MIN_LENGTH_NODE);
                if(!StringUtils.isEmpty(minLength)){
                    ruleParamMap.put(RULE_PARAM_MIN_LENGTH_NODE,minLength);
                }
                //最大值
                String max = ruleParamElement.attributeValue(RULE_PARAM_MAX_NODE);
                if(!StringUtils.isEmpty(max)){
                    ruleParamMap.put(RULE_PARAM_MAX_NODE,max);
                }
                //最小值
                String min = ruleParamElement.attributeValue(RULE_PARAM_MIN_NODE);
                if(!StringUtils.isEmpty(min)){
                    ruleParamMap.put(RULE_PARAM_MIN_NODE,min);
                }
                //正则校验
                String pattern = ruleParamElement.attributeValue(RULE_PARAM_PATTERN_NODE);
                if(!StringUtils.isEmpty(pattern)){
                    ruleParamMap.put(RULE_PARAM_PATTERN_NODE,pattern);
                }
                //code码
                String code = ruleParamElement.attributeValue(RULE_PARAM_CODE_NODE);
                if(!StringUtils.isEmpty(code)){
                    ruleParamMap.put(RULE_PARAM_CODE_NODE,code);
                }
                //错误消息
                String msg = ruleParamElement.attributeValue(RULE_PARAM_MSG_NODE);
                if(!StringUtils.isEmpty(msg)){
                    ruleParamMap.put(RULE_PARAM_MSG_NODE,msg);
                }
                ruleParamMapList.add(ruleParamMap);
            }
            ruleMap.put(ruleId,ruleParamMapList);
        }
    }



    /**
     * 解析device节点信息
     *
     * @param deviceElement device节点
     * @return device配置
     */
    private static FiLinkDeviceConfig resolveDeviceElement(Element deviceElement) {
        FiLinkDeviceConfig fiLinkDeviceConfig = new FiLinkDeviceConfig();
        //获取设施类型
        String deviceType = deviceElement.attributeValue(DEVICE_TYPE_NODE);
        String code = deviceElement.attributeValue(CODE_NODE);
        fiLinkDeviceConfig.setCode(code);
        fiLinkDeviceConfig.setDeviceType(deviceType);
        Element detailsElement = deviceElement.element(DETAILS_NODE);
        List<Element> detailParamList = detailsElement.elements();
        //循环模板详情参数
        List<DetailParam> details = new ArrayList<>();
        for (Element detailParamElement : detailParamList) {
            DetailParam detailParam = new DetailParam();
            String detailParamId = detailParamElement.attributeValue(DETAIL_PARAM_ID);
            String detailParamName = detailParamElement.attributeValue(DETAIL_PARAM_NAME);
            detailParam.setId(detailParamId);
            detailParam.setName(detailParamName);
            details.add(detailParam);
        }
        fiLinkDeviceConfig.setDetailParams(details);
        //解析配置策略参数
        List<Configuration> configurations = new ArrayList<>();
        Element configurationsElement = deviceElement.element(CONFIGURATIONS_NODE);
        if(configurationsElement == null){
            log.info("device: "+fiLinkDeviceConfig.getDeviceType()+" configuration node is null>>>>>>");
            return fiLinkDeviceConfig;
        }
        List<Element> configurationsListElement = configurationsElement.elements();
        //解析configuration节点
        for (Element configurationElement : configurationsListElement) {
            configurations.add(resolveConfiguration(configurationElement));
        }
        fiLinkDeviceConfig.setConfigurations(configurations);
        return fiLinkDeviceConfig;
    }

    /**
     * 解析configuration节点
     * @param configurationElement Element
     * @return configuration对象
     */
    private static Configuration resolveConfiguration(Element configurationElement){
        Configuration configuration = new Configuration();
        String configurationId = configurationElement.attributeValue(CONFIGURATION_ID_NODE);
        String configurationName = configurationElement.attributeValue(CONFIGURATION_NAME_NODE);
        configuration.setId(configurationId);
        configuration.setName(configurationName);
        //解析配置参数项
        List<ConfigParam> configParams = new ArrayList<>();
        List<Element> configurationParamsElement = configurationElement.elements();
        for (Element configurationParamElement : configurationParamsElement) {
            configParams.add(resolveConfigParam(configurationParamElement));
        }
        configuration.setConfigParams(configParams);
        return configuration;
    }

    /**
     * 解析配置项
     *
     * @param configurationParamElement Element
     * @return 配置项实体
     */
    private static ConfigParam resolveConfigParam(Element configurationParamElement) {
        ConfigParam configParam = new ConfigParam();
        String configurationParamId = configurationParamElement.attributeValue(CONFIGURATION_PARAM_ID_NODE);
        String configurationParamName = configurationParamElement.attributeValue(CONFIGURATION_PARAM_NAME_NODE);
        String configurationParamType = configurationParamElement.attributeValue(CONFIGURATION_PARAM_TYPE_NODE);
        String configurationParamCode = configurationParamElement.attributeValue(CONFIGURATION_PARAM_CODE_NODE);
        String configurationParamUnit = configurationParamElement.attributeValue(CONFIGURATION_PARAM_UNIT_NODE);
        String configurationParamPlaceholder = configurationParamElement.attributeValue(CONFIGURATION_PARAM_PLACEHOLDER_NODE);
        configParam.setId(configurationParamId);
        configParam.setCode(configurationParamCode);
        configParam.setName(configurationParamName);
        configParam.setType(configurationParamType);
        configParam.setUnit(configurationParamUnit);
        configParam.setPlaceholder(configurationParamPlaceholder);
        //设置校验规则
        configParam.setRules(ruleMap.get(configurationParamId));
        //如果该配置项为下拉框，则解析下拉框参数
        List<Element> selectParamsElement = configurationParamElement.elements();
        if (selectParamsElement != null && selectParamsElement.size() > 0) {
            List<SelectParam> selectParams = new ArrayList<>();
            for (Element selectParamElement : selectParamsElement) {
                SelectParam selectParam = new SelectParam();
                String selectParamId = selectParamElement.attributeValue(SELECT_PARAM_ID_NODE);
                String selectParamName = selectParamElement.attributeValue(SELECT_PARAM_NAME_NODE);
                selectParam.setId(selectParamId);
                selectParam.setName(selectParamName);
                selectParams.add(selectParam);
            }
            configParam.setSelectParams(selectParams);
        }
        return configParam;
    }
}
