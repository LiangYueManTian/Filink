export enum MapConfig {
  maxZoom = 19,    // 最大缩放级别,大于此缩放级别不再以聚合点展示
  gMaxZoom = 22,    // 谷歌地图最大缩放级别
  gStarMaxZoom = 20,    // 谷歌卫星地图最大缩放级别
  defalutZoom = 8,   // 默认缩放级别
  showLineZoom = 16,  // 大于或等于此缩放级别显示连线
  selectedIndex = 0,  // 设施详情页展示第几个页签
}

export const MAP_ICON_CONFIG = {
  defalutIconSize: '18-24',  // 默认设施图标大小
  iconConfig: [{
    value: '18-24',
    label: '18*24'
  }, {
    value: '24-32',
    label: '24*32'
  }]
};
