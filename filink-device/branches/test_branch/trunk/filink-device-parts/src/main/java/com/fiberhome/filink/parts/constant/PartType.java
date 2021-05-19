package com.fiberhome.filink.parts.constant;

/**
 * 配件类型
 * @author zepenggao@wistronits.com
 * @date 2019/2/14 19:22
 */
public enum PartType {
	/**
	 * 蓝牙钥匙
	 */
	BLUETOOTHKEY("1", "BLUETOOTH_KEY"),

	/**
	 * 机械钥匙
	 */
	MECHANICALKEY("2", "MECHANICAL_KEY"),

	/**
	 * 标签枪
	 */
	LABELGUN("3", "LABEL_GUN");

	private String code;
	private String msg;

	PartType(String code, String msg) {
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
		for (PartType dt : PartType.values()) {
			if (code.equals(dt.code)) {
				return dt.msg;
			}
		}
		return null;
	}
}
