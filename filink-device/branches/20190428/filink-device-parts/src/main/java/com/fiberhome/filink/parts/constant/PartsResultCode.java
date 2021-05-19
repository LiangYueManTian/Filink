package com.fiberhome.filink.parts.constant;

import com.fiberhome.filink.bean.ResultCode;

/**
 * @author zepenggao@wistronits.com
 * @date 2019/2/12 15:16
 */
public class PartsResultCode extends ResultCode {
	/**
	 * 配件参数错误
	 */
	public static final Integer PARTS_PARAM_ERROR = 130301;

	/**
	 * 配件名称重复
	 */
	public static final Integer PARTS_NAME_SAME = 130302;

	/**
	 * 缺少配件id
	 */
	public static final Integer PARTS_ID_LOSE = 130303;

	/**
	 * 配件不存在
	 */
	public static final Integer PARTS_NOT_EXIST = 130304;

	/**
	 * 输入参数为空
	 */
	public static final Integer EXPORT_PARAM_NULL = 130305;


	/**
	 * 创建导出任务失败
	 */
	public static final Integer FAILED_TO_CREATE_EXPORT_TASK = 130306;

	/**
	 * 导出超过最大限制
	 */
	public static final Integer EXPORT_DATA_TOO_LARGE = 130307;

	/**
	 * 没有数据导出
	 */
	public static final int EXPORT_NO_DATA = 130308;

	/**
	 * 当前用户超过最大任务数量
	 */
	public static final int EXCEED_THE_MAXIMUM_NUMBER_OF_TASKS = 130309;

	/**
	 * 配件名称不合法
	 */
	public static final int PARTS_NAME_ERROR = 130310;
}
