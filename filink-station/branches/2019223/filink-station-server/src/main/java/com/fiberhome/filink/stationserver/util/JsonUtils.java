package com.fiberhome.filink.stationserver.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Json转换工具
 * 
 * @author yaoyuan
 */
public class JsonUtils {

	/**
	 * 复杂json转map
	 * 
	 * @param jsonStr String
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> jsonToMap(String jsonStr) {
		Map<String, Object> map = new HashMap<>(8);
		if (jsonStr != null && !"".equals(jsonStr)) {
			// 最外层解析
			JSONObject json = JSONObject.parseObject(jsonStr);
			for (Object k : json.keySet()) {
				Object v = json.get(k);
				// 如果内层还是数组的话，继续解析
				if (v instanceof JSONArray) {
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					Iterator<Object> it = ((JSONArray) v).iterator();
					while (it.hasNext()) {
						JSONObject json2 = (JSONObject) it.next();
						list.add(jsonToMap(json2.toString()));
					}
					map.put(k.toString(), list);
				} else {
					map.put(k.toString(), v);
				}
			}
			return map;
		} else {
			return null;
		}
	}

	/**
	 * map转换json
	 * 
	 * @param map 集合
	 * @return json字符串
	 */
	public static String mapToJson(Map<String, Object> map) {
		Set<String> keys = map.keySet();
		String key = "";
		String value = "";
		StringBuffer jsonBuffer = new StringBuffer();
		jsonBuffer.append("{");
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			key = it.next();
			value = (String) map.get(key);
			jsonBuffer.append(key + ":" + "\"" + value + "\"");
			if (it.hasNext()) {
				jsonBuffer.append(",");
			}
		}
		jsonBuffer.append("}");
		return jsonBuffer.toString();
	}

	/**
	 * 指令集追加key-value
	 * 
	 * @param map 集合
	 * @return map 集合
	 */
	public static Map<String, Object> mapAddmap(Map<String, Object> map) {
		// BMC固件升级和BIOS固件升级默认为SNMP
		map.put("bmcFirmwareProtocol", "SNMP");
		map.put("biosFirmwareProtocol", "SNMP");
		return map;
	}
}
