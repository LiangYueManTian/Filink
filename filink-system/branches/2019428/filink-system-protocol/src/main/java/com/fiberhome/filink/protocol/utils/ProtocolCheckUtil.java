package com.fiberhome.filink.protocol.utils;

import com.fiberhome.filink.protocol.dto.ProtocolField;
import com.fiberhome.filink.protocol.dto.ProtocolParams;
import com.fiberhome.filink.system_commons.utils.ParamTypeRedisEnum;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>
 * 通信协议  参数检验类
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/2/28
 */
public class ProtocolCheckUtil {

    /**
     * 匹配IPv4
     */
    private static String IPV4_REGEX = "^(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))$";
    /**
     * 匹配端口
     */
    private static String PORT_REGEX = "^([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
    /**
     * 匹配超时时间
     */
    private static String MAX_WAIT_REGEX = "^([3-9]\\d{1}|[1-5]\\d{2}|600)$";
    /**
     * 匹配启用限制
     */
    private static String ENABLED_REGEX = "^([01])$";
    /**
     * 匹配连接数
     */
    private static String MAX_ACTIVE_REGEX = "^([1-9]\\d{1,3}|10000)$";

    /**
     * StringClass
     */
    private static String String_Class = "class java.lang.String";

    /**
     * 协议不启用
     */
    private static String DISABLE = "0";

    /**
     * 检验协议参数格式是否正确
     *
     * @param protocolParams
     * @return true 格式正确 false 格式错误
     */
    public static boolean checkProtocolParams(ProtocolParams protocolParams) {
        //检验ProtocolParams属性是否为空
        if (StringUtils.isEmpty(protocolParams) || StringUtils.isEmpty(protocolParams.getParamId()) || StringUtils.isEmpty(protocolParams.getParamType()) || StringUtils.isEmpty(protocolParams.getProtocolField())) {
            //为空，返回false
            return false;
        }
        //检查ProtocolField三个共有属性(ip,port,maxWait)是否为空
        ProtocolField protocolField = protocolParams.getProtocolField();
        String protocolType= protocolParams.getParamType().trim();

        return checkParamByProtocolName(protocolField, protocolType);


    }


    /**
     * 根据正则表达式，检验字符串格式是否正确
     * 检验前去掉字符串前后空格
     *
     * @param str   字符串
     * @param regex 正则表达式
     * @return true 格式正确 false 格式错误
     */
    public static boolean checkStrWithRegex(String str, String regex) {
        String defineStr = str.trim();
        if (StringUtils.isEmpty(defineStr)) {
            return false;
        }
        if (!defineStr.matches(regex)) {
            return false;
        }
        return true;
    }

    /**
     * 根据协议类型检验ProtocolField参数格式是否正确
     *
     * @param protocolField  协议内容
     * @param protocolName  协议类型
     * @return false ： 错误  true ：正确
     */
    public static boolean checkParamByProtocolName(ProtocolField protocolField, String protocolName) {
        if (!checkProtocolName(protocolName)) {
            return false;
        }
        ParamTypeRedisEnum protocolNameEnum = ParamTypeRedisEnum.valueOf(ParamTypeRedisEnum.getKeyByType(protocolName));
        if (ParamTypeRedisEnum.HTTP_PROTOCOL == protocolNameEnum || ParamTypeRedisEnum.HTTPS_PROTOCOL == protocolNameEnum) {
            //检验通用属性是否符合规则
            boolean b = checkStrWithRegex(protocolField.getIp(), IPV4_REGEX)
                    && checkStrWithRegex(protocolField.getPort(), PORT_REGEX)
                    && checkStrWithRegex(protocolField.getMaxWait(), MAX_WAIT_REGEX)
                    && checkStrWithRegex(protocolField.getEnabled(), ENABLED_REGEX);
            if (DISABLE.equals(protocolField.getEnabled().trim())) {
                return b;
            } else {
                //当协议启用连接限制时，需要检验最大连接数
                return b && checkStrWithRegex(protocolField.getMaxActive(), MAX_ACTIVE_REGEX);
            }

        } else if (ParamTypeRedisEnum.WEBSERVICE_PROTOCOL == protocolNameEnum) {
            return checkStrWithRegex(protocolField.getIp(), IPV4_REGEX)
                    && checkStrWithRegex(protocolField.getPort(), PORT_REGEX)
                    && checkStrWithRegex(protocolField.getMaxWait(), MAX_WAIT_REGEX);
        }
        return true;
    }


    /**
     * 检查协议类型是否存在
     *
     * @param protocolName  协议类型
     * @return false -不存在 ，true -存在
     */
    public static boolean checkProtocolName(String protocolName) {

        if (StringUtils.isEmpty(protocolName)) {
            return false;
        }

        return ParamTypeRedisEnum.hasType(protocolName);


    }

    /**
     * 将类中类型为String的属性值，去掉前后空格
     *
     * @param model 要转换的类
     * @throws Exception
     */
    public static void trimBean(Object model) {
        Class clazz = model.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                if (String_Class.equals(field.getGenericType().toString())) {
                    Method getMethod = clazz.getMethod("get" + getMethodName(field.getName()));
                    // 调用getter方法获取属性值
                    String value = (String) getMethod.invoke(model);
                    //属性值不为空,去掉首尾空格
                    if (!StringUtils.isEmpty(value)) {
                        value = value.trim();
                        field.setAccessible(true);
                        field.set(model, value);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把一个字符串的第一个字母大写
     *
     * @param fildeName
     * @return
     * @throws Exception
     */
    private static String getMethodName(String fildeName) throws Exception {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }


}
