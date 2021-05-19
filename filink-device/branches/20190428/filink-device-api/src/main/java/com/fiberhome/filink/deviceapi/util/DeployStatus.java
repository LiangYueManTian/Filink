package com.fiberhome.filink.deviceapi.util;

/**
 * 布防状态
 * @author zepenggao@wistronits.com
 * @date 2019/2/14 20:14
 */
public enum DeployStatus {
	/**
	 * 已布防
	 */
	DEPLOYED("1", "DEPLOYED"),

	/**
	 * 未布防
	 */
	NODEFENCE("2", "NO_DEFENCE"),

	/**
	 * 停用
	 */
	NOTUSED("3", "NOT_USED"),

	/**
	 * 维护
	 */
	MAINTENANCE("4", "MAINTENANCE"),

	/**
	 * 拆除
	 */
	DISMANTLE("5", "DISMANTLE"),

	/**
	 * 部署中
	 */
	DEPLOYING("6", "DEPLOYING");

	private String code;
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
