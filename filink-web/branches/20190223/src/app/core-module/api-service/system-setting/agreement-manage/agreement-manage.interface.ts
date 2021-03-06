export interface AgreementManageInterface {
  /**
   * 新增设施协议
   * param params
   */
  addDeviceProtocol(params);

  /**
   * 修改设施协议
   * param params
   */
  updateDeviceProtocol(params);

  /**
   * 修改设施协议名称
   * param params
   */
  updateProtocolName(params);

  /**
   * 删除设施协议
   * param protocolId
   */
  deleteDeviceProtocol(params);

  /**
   * 查询设施列表
   * param params
   */
  queryDeviceProtocolList();

  /**
   * 实施协议文件校验
   * param params
   */
  queryFileLimit();
}
