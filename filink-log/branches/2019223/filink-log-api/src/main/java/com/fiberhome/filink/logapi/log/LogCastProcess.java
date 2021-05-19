package com.fiberhome.filink.logapi.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fiberhome.filink.logapi.bean.AddLogAspectBean;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.bean.XmlParseBean;
import com.fiberhome.filink.logapi.req.AddOperateLogReq;
import com.fiberhome.filink.logapi.req.AddSecurityLogReq;
import com.fiberhome.filink.logapi.req.AddSystemLogReq;
import com.fiberhome.filink.logapi.utils.LogConstants;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author hedongwei@wistronits.com
 * description 日志转换过程类
 * date 2019/1/24 21:00
 */
@Component
public class LogCastProcess {

    @Autowired
    WebApplicationContext webApplicationConnect;

    /**
     * 语言配置文件名称
     */
    public static final String LANGUAGE_CONFIG = "languageConfig";

    /**
     * 环境属性
     */
    public static final String ENVIRONMENT = "environment";

    /**
     * @author hedongwei@wistronits.com
     * description 将对象转换成业务对象
     * date 21:04 2019/1/24
     * param [addLogAspectBean, dataId, dataName, jsonObject]
     */
    public AddLogAspectBean castBizToLog(AddLogAspectBean addLogAspectBean , String dataId, String dataName, JSONObject jsonObject) throws Exception{

        //数据名称
        if (null != jsonObject.get(dataName)) {
            addLogAspectBean.setOptObj(jsonObject.getString(dataName));
        } else {
            return null;
        }

        //数据编号
        if (null != jsonObject.get(dataId)) {
            addLogAspectBean.setOptObjId(jsonObject.getString(dataId));
        } else {
            return null;
        }

        //操作用户名称
        String optUserName = addLogAspectBean.getOptUserName();
        //获得编码方式
        String language = this.getLanguage();
        //解析文件
        XmlParseBean xmlParseBean = dom4jParseXml(addLogAspectBean.getFunctionCode(), language);
        String detailInfo = xmlParseBean.getDetailInfoTemplate();
        if (null != detailInfo) {
            //替换数据名称
            detailInfo = detailInfo.replace("${" + dataName + "}", addLogAspectBean.getOptObj());
            //替换用户名称
            detailInfo = detailInfo.replace("${optUserName}", optUserName);
            addLogAspectBean.setDetailInfo(detailInfo);
        }
        //操作名称
        String optName = xmlParseBean.getOptName();
        if (null != optName) {
            addLogAspectBean.setOptName(optName);
        }
        return addLogAspectBean;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 将对象转换成新增操作日志的对象
     * date 21:04 2019/1/24
     * param [addLogAspectBean]
     */
    public AddOperateLogReq castInfoToAddOperateLog(AddLogBean addLogBean) throws Exception {
        addLogBean = this.castToCallAddLog(addLogBean);
        String addLogInfo = JSON.toJSONString(addLogBean, SerializerFeature.WRITE_MAP_NULL_FEATURES);
        JSONObject addLogObject = JSONObject.parseObject(addLogInfo);
        AddOperateLogReq addOperateLogReq = JSONObject.toJavaObject(addLogObject, AddOperateLogReq.class);
        return addOperateLogReq;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 将对象转换成新增安全日志的对象
     * date 21:04 2019/1/24
     * param [addLogAspectBean]
     */
    public AddSecurityLogReq castInfoToAddSecurityLog(AddLogBean addLogBean) throws Exception {
        addLogBean = this.castToCallAddLog(addLogBean);
        String addLogInfo = JSON.toJSONString(addLogBean, SerializerFeature.WRITE_MAP_NULL_FEATURES);
        JSONObject addLogObject = JSONObject.parseObject(addLogInfo);
        AddSecurityLogReq addSecurityLogReq = JSONObject.toJavaObject(addLogObject, AddSecurityLogReq.class);
        return addSecurityLogReq;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 将对象转换成新增系统日志的对象
     * date 21:04 2019/1/24
     * param [addLogAspectBean]
     */
    public AddSystemLogReq castInfoToAddSystemLog(AddLogBean addLogBean) throws Exception {
        addLogBean = this.castToCallAddLog(addLogBean);
        String addLogInfo = JSON.toJSONString(addLogBean, SerializerFeature.WRITE_MAP_NULL_FEATURES);
        JSONObject addLogObject = JSONObject.parseObject(addLogInfo);
        AddSystemLogReq addSystemLogReq = JSONObject.toJavaObject(addLogObject, AddSystemLogReq.class);
        return addSystemLogReq;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 手动调用新增日志的接口
     * date 9:54 2019/1/29
     * param [addLogAspectBean]
     */
    public AddLogBean castToCallAddLog(AddLogBean addLogBean) throws Exception {
        //操作用户名称
        String optUserName = addLogBean.getOptUserName();
        //获得编码方式
        String language = this.getLanguage();
        //解析文件
        XmlParseBean xmlParseBean = dom4jParseXml(addLogBean.getFunctionCode(), language);
        String detailInfo = xmlParseBean.getDetailInfoTemplate();
        if (null != detailInfo) {
            //替换数据名称
            detailInfo = detailInfo.replace("${" + addLogBean.getDataName() + "}", addLogBean.getOptObj());
            //替换用户名称
            detailInfo = detailInfo.replace("${optUserName}", optUserName);
            addLogBean.setDetailInfo(detailInfo);
        }
        //操作名称
        String optName = xmlParseBean.getOptName();
        if (null != optName) {
            addLogBean.setOptName(optName);
        }
        return addLogBean;
    }

    /**
     * @author hedongwei@wistronits.com
     * description 对象解析xml
     * date 21:04 2019/1/24
     * param [functionCode, language]
     */
    public XmlParseBean dom4jParseXml(String functionCode, String language) throws Exception{
        XmlParseBean retXmlParesBean = new XmlParseBean();
        //模板名称
        String detailInfoTemplate = "";
        //操作名称
        String optName = "";
        //获得功能项配置文件信息
        //File file = new File("D://FunctionConfig.xml");
        ClassPathResource resource = new ClassPathResource(LogConstants.FUNCTION_CONFIG_FILE_PATH);
        InputStream fileInputStream = resource.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(fileInputStream);
        Element root = document.getRootElement();
        List<Element> childElements = root.elements();
        for (Element ele : childElements) {
            //找到功能编号
            if (functionCode.equals(ele.attributeValue("functionId"))) {
                //获取语言信息
                List<Element> languageElementList = ele.elements();
                for (Element languageOne: languageElementList) {
                    languageOne.getText();
                    if (language.toUpperCase().equals(languageOne.attributeValue("type"))) {
                        //获取功能模板信息
                        List<Element> configInfoList = languageOne.elements();
                        if (0 < configInfoList.size()) {
                            for (Element configOne: configInfoList) {
                                if ("detailInfoTemplate".equals(configOne.getName())) {
                                    detailInfoTemplate = configOne.getText();
                                } else if("optName".equals(configOne.getName())){
                                    optName = configOne.getText();
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(detailInfoTemplate);
        System.out.println(optName);
        retXmlParesBean.setDetailInfoTemplate(detailInfoTemplate);
        retXmlParesBean.setOptName(optName);
        return retXmlParesBean;
    }

    /**
     *  获取语言信息
     * @author hedongwei@wistronits.com
     * @date 2019/2/14
     */
    public String getLanguage() {
        String language = "";
        try {
            Object languageObject = webApplicationConnect.getBean(LogCastProcess.LANGUAGE_CONFIG);
            Field languageField = languageObject.getClass().getDeclaredField(LogCastProcess.ENVIRONMENT);
            languageField.setAccessible(true);
            language = (String)languageField.get(languageObject);
            if (!StringUtils.isEmpty(language)) {
                language = language.toUpperCase();
            }
        } catch (Exception e) {
            language = "";
        }
        return language;
    }
}
