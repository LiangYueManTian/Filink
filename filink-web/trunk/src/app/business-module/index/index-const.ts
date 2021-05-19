/**
 * 首页大数据标识
 */
export const index_huge_data = '1';
/**
 * 首页设施状态颜色
 */
export const index_device_status_color = '0';
/**
 * 首页设施类型
 */
export const index_facility_type = {
  /**
   * 配线架
   */
  patchPanel : '060',
  /**
   * 光交箱
   */
  opticalBox : '001',
  /**
   * 人井
   */
  manWell : '030',
  /**
   * 接头盒
   */
  jointClosure : '090',
  /**
   * 室外柜
   */
  outDoorCabinet : '210',
};

/**
 * 首页左侧面板三个tab页
 */
export const index_left_panel = {
  /**
   * 设施列表
   */
  facilitiesList : 0,
  /**
   * 我的关注
   */
  myCollection : 1,
  /**
   * 拓扑高亮
   */
  toLogicalHighLighting : 2,
};

/**
 * 首页左侧面板告警chat
 */
export const index_alarm_chat = {
  /**
   * 一周
   */
  oneWeek : 1,
  /**
   * 一月
   */
  oneMonth : 2,
  /**
   * 三月
   */
  threeMonth : 3,
};

/**
 * 首页左侧面板告警chat
 */
export const index_day_number = {
  /**
   * 一周
   */
  oneWeek : 7,
  /**
   * 一月
   */
  oneMonth : 30,
  /**
   * 三月
   */
  threeMonth : 90,
};

/**
 * 首页设施详情四个tab页
 */
export const index_facility_panel = {

  /**
   * 设施详情
   */
  facilityDetail : 0,
  /**
   * 设施告警
   */
  facilityAlarm : 1,
  /**
   * 日志和工单
   */
  logAndOrderTab : 2,
  /**
   * 实景图
   */
  RealSceneTab : 3,
};

/**
 * 首页更新数据类型
 */
export const index_update_type = {
  /**
   * 没有修改的更新
   */
  noUpdate : 1,
  /**
   * 有修改更新
   */
  haveUpdate : 2,

  /**
   * 推送修改更新
   */
  webUpdate : 3,
};

/**
 * 首页大数据标识
 * 0 无效  1异常  2正常
 */
export const lock_status_type = {
  /**
   * 无效
   */
  invalid : '0',
  /**
   * 异常
   */
  unusual : '1',

  /**
   * 正常
   */
  normal : '2',

}

/**
 * 首页设施类型配置
 */
export const index_facility_config = {
  /**
   * 没有修改的更新
   */
  noChecked : '0',
  /**
   * 有修改更新
   */
  checked : '1',
};

/**
 * 首页卡片类型
 */
export const index_card_type = {
  /**
   * 设施总数
   */
  deviceCount : 0,
  /**
   * 类型总数
   */
  typeCount : 1,
  /**
   * 设施状态
   */
  deviceStatus : 2,
  /**
   * 告警总数
   */
  alarmCount : 3,
  /**
   * 告警增量
   */
  alarmIncrement : 4,
  /**
   * 工单增量
   */
  workIncrement : 5,
  /**
   * 繁忙TOP
   */
  busyTop : 6,
  /**
   * 告警TOP
   */
  alarmTop : 7,
};



/**
 * 首页地图点击事件回传
 */
export const index_map_type = {
  /**
   * 地图点击事件
   */
  mapClick : 'mapClick',
  /**
   * 点击聚合点
   */
  clickClusterer : 'clickClusterer',
  /**
   * 点击设施
   */
  selected : 'selected',
  /**
   * 点击地图空白
   */
  mapBlackClick: 'mapBlackClick',
  /**
   * 城市控件打开与关闭
   */
  cityListControlStatus : 'cityListControlStatus',
  /**
   * 城市切换
   */
  cityChange : 'cityChange',
  /**
   * 地图拖动
   */
  mapDrag : 'mapDrag',
  /**
   * 重置设施id
   */
  resetFacilityId : 'resetFacilityId',
  /**
   * 拓扑高亮
   */
  showLight : 'showLight',
};

/**
 * 首页设施类型
 */
export const index_facility_event = {
  /**
   * 关闭
   */
  close: 'close',
  /**
   * 设置
   */
  setting: 'setting',
  /**
   * 设置
   */
  refresh: 'refresh',
  /**
   * 更新
   */
  update: 'update',
  /**
   * 定位
   */
  location: 'location',
  /**
   * 关注设施
   */
  focusDevice: 'focusDevice',
  /**
   * 不关注设施
   */
  unFollowDevice: 'unFollowDevice',
  /**
   * 是否是拓扑高亮
   */
  isTopog: 'isTopog'
}


