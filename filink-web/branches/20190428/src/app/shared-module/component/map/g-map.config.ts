/**
 * 说明：
 * 谷歌地图普通地图最大支持缩放级别为22，卫星地图最大支持缩放级别为20
 */


export enum MapConfig {
  maxZoom = 20,    // 最大缩放级别
  defalutZoom = 8,   // 默认缩放级别
  showLineZoom = 12,  // 大于或等于此缩放级别显示连线
}
