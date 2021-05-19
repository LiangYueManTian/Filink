package com.fiberhome.filink.onenetserver.bean.device;



import com.fiberhome.filink.onenetserver.constant.OneNetHttpConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <p>
 *   oneNet平台写设备资源实体类
 * </p>
 *
 * @author chaofang@fiberhome.com
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Write extends BaseCommonEntity {
    /**
     * write的模式，只能是1或者2
     */
    private Integer mode;
    /**
     * 请求超时时间
     */
    private Integer timeout;
    /**
     * 命令
     */
    private String val;
    /**空构造函数*/
    public Write() {}
    /**
     * 空构造函数
     * @param imei 设备IMEI
     * @param objId 写对象ID
     * @param objInstId 写实例ID
     * @param mode 写的模式（1：replace，2：partial update）
     * @param timeout 请求超时时间，默认25(单位：秒)，取值范围[5, 40]
     * @param resId 指定write操作的资源id
     * @param val 命令
     */
    public Write(String imei, Integer mode, Integer timeout, Integer objId, Integer objInstId, Integer resId, String val) {
        this.imei = imei;
        this.mode = mode;
        this.timeout = timeout;
        this.objId = objId;
        this.objInstId = objInstId;
        this.resId = resId;
        this.val = val;
    }

    /**
     * 输出Json
     * @return JSONObject
     */
    @Override
    public JSONObject toJsonObject(){
        JSONObject body = new JSONObject();
        JSONArray bodyArray = new JSONArray();
        JSONObject arrayBody = new JSONObject();
        arrayBody.put("res_id", resId);
        arrayBody.put("val", val);
        bodyArray.put(arrayBody);
        body.put("data", bodyArray);
        return body;
    }
    /**
     * 输出 url
     * @return url
     */
    @Override
    public String toUrl() {
        StringBuilder url = new StringBuilder(host);
        url.append("/nbiot?imei=").append(this.imei);
        url.append("&obj_id=").append(this.objId);
        url.append("&obj_inst_id=").append(this.objInstId);
        url.append("&mode=").append(this.mode);
        url.append("&timeout=").append(this.timeout);
        return url.toString();
    }
}
