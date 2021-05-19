package com.fiberhome.filink.filinkoceanconnectserver.utils;

import com.fiberhome.filink.filinkoceanconnectserver.constant.RedisKey;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.MethodBean;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.Platform;
import com.fiberhome.filink.filinkoceanconnectserver.entity.profile.ServiceBean;
import com.fiberhome.filink.filinkoceanconnectserver.exception.OceanException;
import com.fiberhome.filink.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * profile工具类
 *
 * @author CongcaiYu
 */
@Slf4j
@Component
public class ProfileUtil {


    /**
     * id节点信息
     */
    private final String ID_NODE = "id";

    /**
     * platforms节点信息
     */
    private final String PLATFORMS_NODE = "platforms";

    /**
     * appId节点信息
     */
    private final String PLATFORM_APP_ID_NODE = "appId";

    /**
     * upload节点信息
     */
    private final String UPLOAD_NODE = "upload";

    /**
     * down节点信息
     */
    private final String DOWN_NODE = "down";

    /**
     * length节点信息
     */
    private final String LENGTH_NODE = "length";

    /**
     * data节点信息
     */
    private final String DATA_NODE = "data";

    /**
     * method节点信息
     */
    private final String METHOD_NODE = "method";

    /**
     * isBase64节点信息
     */
    private final String IS_BASE64_NODE = "isBase64";

    /**
     * service节点信息
     */
    private final String SERVICE_NODE = "service";



    /**
     * 解析profile
     *
     * @param inputStream 输入流
     * @throws Exception 异常
     */
    private Platform resolver(InputStream inputStream) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element config = document.getRootElement();
        //解析platforms节点
        List<Element> platformsElement = config.element(PLATFORMS_NODE).elements();
        return resolvePlatform(platformsElement);
    }


    /**
     * 解析platform信息
     *
     * @param platformsElement platform节点
     * @return platform对象信息
     */
    private Platform resolvePlatform(List<Element> platformsElement) {
        Platform platform = new Platform();
        Map<String, ServiceBean> downMap = new HashMap<>(64);
        Map<String, ServiceBean> uploadMap = new HashMap<>(64);
        for (Element platformElement : platformsElement) {
            //appId
            String appId = platformElement.attributeValue(PLATFORM_APP_ID_NODE);
            //解析upload
            Element uploadElement = platformElement.element(UPLOAD_NODE);
            resolveService(uploadElement, uploadMap, appId, UPLOAD_NODE);
            //解析down
            Element downElement = platformElement.element(DOWN_NODE);
            resolveService(downElement, downMap, appId, DOWN_NODE);
        }
        platform.setDownMap(downMap);
        platform.setUploadMap(uploadMap);
        return platform;
    }


    /**
     * 解析service节点
     *
     * @param element    节点对象
     * @param serviceMap service map
     * @param appId      产品id
     * @param nodeName   节点名称
     */
    private void resolveService(Element element, Map<String, ServiceBean> serviceMap,
                                String appId, String nodeName) {
        if (element == null) {
            log.error("the " + nodeName + " of the appId : " + appId + " is null>>>>>>>>>>>>");
            return;
        }
        //service
        Element serviceElement = element.element(SERVICE_NODE);
        if (serviceElement == null) {
            log.error("the " + nodeName + " service node of the appId : " + appId + " is null>>>>>>>>>>>>");
            return;
        }
        ServiceBean service = new ServiceBean();
        //serviceId
        String serviceId = serviceElement.attributeValue(ID_NODE);
        service.setId(serviceId);
        //是否base64加密
        String isBase64 = serviceElement.attributeValue(IS_BASE64_NODE);
        String trueStr = "true";
        if (!StringUtils.isEmpty(isBase64) && trueStr.equals(isBase64)) {
            service.setBase64(true);
        }
        //length
        String length = serviceElement.elementText(LENGTH_NODE);
        service.setLength(length);
        //data
        String data = serviceElement.elementText(DATA_NODE);
        service.setData(data);
        //解析method
        Element methodElement = serviceElement.element(METHOD_NODE);
        if (methodElement == null) {
            serviceMap.put(serviceId, service);
            log.info("method element is null>>>>>>>");
            return;
        }
        MethodBean method = new MethodBean();
        //methodId
        String methodId = methodElement.attributeValue(ID_NODE);
        method.setId(methodId);
        //length
        String methodLength = methodElement.elementText(LENGTH_NODE);
        method.setLength(methodLength);
        //data
        String methodData = methodElement.elementText(DATA_NODE);
        method.setData(methodData);
        service.setMethod(method);
        serviceMap.put(appId, service);
    }


    /**
     * 获取profile对象
     *
     * @return profile配置类
     */
    public Platform getProfileConfig() {
        log.info("get profile>>>");
        Platform platform = (Platform) RedisUtils.get(RedisKey.PROFILE_KEY);
        if (platform == null) {
            log.info("profile of the redis is null , init profile");
            InputStream fileInputStream = null;
            try {
                //重新读取profile
                ClassPathResource classPathResource = new ClassPathResource("profileConfig.xml");
                fileInputStream = classPathResource.getInputStream();
                platform = resolver(classPathResource.getInputStream());
                RedisUtils.set(RedisKey.PROFILE_KEY, platform);
            } catch (Exception e) {
                log.error("init profile failed>>>>>>>>>");
                throw new OceanException("init profile failed");
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        log.error("inputStream close failed");
                    }
                }
            }
        }
        return platform;
    }

}
