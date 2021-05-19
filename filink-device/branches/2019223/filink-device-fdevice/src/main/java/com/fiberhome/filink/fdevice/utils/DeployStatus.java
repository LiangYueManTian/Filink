package com.fiberhome.filink.fdevice.utils;

/**
 * 部署状态
 * @author zepenggao@wistronits.com
 * @date 2019/2/14 20:14
 */
public enum DeployStatus {
	/**
	 * 已布防
	 */
	DEPLOYED("1", "已布防"),

	/**
	 * 未布防
	 */
	NODEFENCE("2", "未布防"),

	/**
	 * 停用
	 */
	NOTUSED("3", "停用"),

	/**
	 * 维护
	 */
	MAIINTENANCE("4", "维护"),

	/**
	 * 拆除
	 */
	DISMANTLE("5", "拆除");

	/**状态编号*/
	private String code;
	/**状态名称*/
	private String msg;

	DeployStatus(String code, String msg) {
		this.msg = msg;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static String getMsg(String code) {
		for (DeployStatus dt : DeployStatus.values()) {
			if (code.equals(dt.code)) {
				return dt.msg;
			}
		}
		return null;
	}
}
