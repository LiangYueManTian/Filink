package com.fiberhome.filink.protocol.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fiberhome.filink.protocol.exception.ProtocolSystemException;

/**
 * <p>
 * 操作json的封装方法
 * use:jackson
 * 这里只提供两个简单的
 * 复杂的自己写JSONChange.getInstance().方法
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/3/1
 */
public class JsonChange {

	private static ObjectMapper instance;


	/**
	 * 获得对象实例，单例模式
	 * @return ObjectMapper单例
	 */
	public static ObjectMapper getInstance(){
		if(instance == null){
             makeInstance();
		}
		return instance;
	}
	/**
	 * 同步方法,保证同一时间只能有一个实例
	 */
	private static synchronized void makeInstance(){
		if(instance == null){
			instance = new  ObjectMapper();
			//序列化的时候忽略对象为空的属性
			instance.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
			//反序列化的时候如果多了其他属性,不抛出异常
			instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//如果是空对象的时候,不抛异常
			instance.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		}
	}


	/**
	 * 对象转换成json
	 * @param:传入对象
	 * @return:json字符串
	 */
	public static String objToJson(Object obj){
		String s;
		try {
			s = getInstance().writeValueAsString(obj);
		}catch (Exception e){
			throw new ProtocolSystemException();
		}
		return s;
	}
}
