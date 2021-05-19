export const FACILITY_TYPE_NAME = {
  '060': 'patchPanel',   // 配线架
  '001': 'opticalBox',   // 光交箱
  '030': 'manWell',   // 人井
  '090': 'jointClosure',   // 接头盒
  // '150': 'fiberBox',   // 分纤箱
  '210' : 'outDoorCabinet' // 室外柜
};

export const FACILITY_STATUS_NAME = {
  '2': 'normal',    // 正常
  '3': 'alarm',     // 告警
  '1': 'unknown',   // 未知
  '5': 'lost',      // 失联
  '4': 'offline',   // 离线
  '0': 'selected',   // 自定义状态，页面选中
};

export const FACILITY_STATUS_CODE = {
  alarm: '3',
};

export const FACILITY_STATUS_COLOR = {
  // '1': '#36cfc9',
  // '2': '#fb7257',
  // '3': '#959595',
  // '4': '#f8be32',
  // '5': '#36a9cf',
  '2': '#36D1C9', // 正常
  '3': '#FB7356', // 告警
  '4': '#959595', // 离线
  '5': '#F8C032', // 失联
  '1': '#35AACE', // 未配置
  '0': '#8588e7',
};
export const FACILITY_TYPE_ICON_CLASS = {
  '060': 'icon-patchPanel',
  '001': 'icon-opticalBox',
  '030': 'icon-manWell',
  '090': 'icon-jointClosure',
  // '150': 'icon-fiberBox',
  '210': 'icon-outDoorCabinet',
};

export const MAP_ICON_CONFIG = {
  defaultIconSize: '18-24',  // 默认设施图标大小
  iconConfig: [{
    value: '18-24',
    label: '18*24'
  }, {
    value: '24-32',
    label: '24*32'
  }]
};
