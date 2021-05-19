import {MapAbstract} from './map-abstract';
import {CommonUtil} from '../../util/common-util';
import {Observable, Subject} from 'rxjs';
import {MapConfig as BMapConfig} from './b-map.config';
import {MapConfig} from './map.config';

declare let BMap: any;   // 一定要声明BMap，要不然报错找不到BMap
declare let BMapLib: any;
declare const BMAP_NORMAL_MAP: any;
declare const BMAP_SATELLITE_MAP: any;
declare const BMAP_ANCHOR_BOTTOM_RIGHT: any;
declare const OBMap_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;

export class BMapService extends MapAbstract {
  mapInstance;
  markersMap = new Map();
  cityTimer;
  _iconSize;
  iconSize;
  markerClusterer;
  private cityListStatus: Subject<any> = new Subject<any>();
  private mapEvent: Subject<any> = new Subject<any>();

  constructor(mapConfig: MapConfig) {
    super();
    this.mapInstance = this.createMap(mapConfig);
    // this.locateToUserCity();
    this.iconSize = mapConfig.defaultIconSize;
    this.setIconSize();
    // 启用滚轮放大缩小，默认禁用
    this.mapInstance.enableScrollWheelZoom();
    // 禁止双击放大
    this.mapInstance.disableDoubleClickZoom();
    // 增加城市控件
    this.addCityListControl();
    this.addNavigationControl();
    this.addMapTypeControl();
    this.addEventListenerToCityListControl();
    setTimeout(() => {
      this.addEventListenerToMap();
    }, 0);
  }

  createMap(mapConfig: MapConfig) {
    // 创建Map实例
    const mapInstance = new BMap.Map(mapConfig.mapId, {enableMapClick: false, maxZoom: BMapConfig.maxZoom});
    // 添加地图类型控件
    const point = new BMap.Point(116.331398, 39.897445);
    mapInstance.centerAndZoom(point, 8);
    const style = mapConfig.mapStyle ? mapConfig.mapStyle : [];
      // mapInstance.setMapStyleV2({styleJson: mapConfig.mapStyle});  // v2版本样式自定义时与地图类型切换插件有冲突
    mapInstance.setMapStyle({styleJson: style});   // 采用v1版本样式配置
    return mapInstance;
  }

  private cityListChange(data): void {
    this.cityListStatus.next(data);
  }

  public cityListHook(): Observable<any> {
    return this.cityListStatus.asObservable();
  }

  private mapEventEmitter(data): void {
    this.mapEvent.next(data);
  }

  public mapEventHook(): Observable<any> {
    return this.mapEvent.asObservable();
  }

  /**
   * 创建marker点
   * param point
   * param fn
   * returns {BMap.Marker}
   */
  createMarker(point, fn?) {
    const url = CommonUtil.getFacilityIconUrl(this.iconSize, point.deviceType, point.deviceStatus);
    const icon = this.toggleIcon(url);
    super.updateCenterPoint(point.lng, point.lat);
    const marker = new BMap.Marker(this.createPoint(point.lng, point.lat), {
      icon: icon
    });
    marker.customData = {id: point.deviceId};
    marker.isShow = {id: point.deviceId};
    if (fn) {
      if (fn.length > 0) {
        fn.forEach(item => {
          marker.addEventListener(item.eventName, item.eventHandler);
        });
      }
    }
    this.markersMap.set(point.deviceId, {marker: marker, data: point});
    return marker;
  }

  /**
   * 创建点
   * param lng
   * param lat
   * returns {BMap.Point}
   */
  createPoint(lng, lat) {
    return new BMap.Point(lng, lat);
  }

  /**
   * 聚合点
   * param markers
   */
  addMarkerClusterer(markers, fn?) {
    const eventMap = new Map();
    if (fn && fn.length > 0) {
      fn.forEach(item => {
        eventMap.set(item.eventName, item.eventHandler);
        // markerClusterer.addEventListener(item.eventName, item.eventHandler);
      });
    }
    this.markerClusterer = new BMapLib.MarkerClusterer(this.mapInstance, {
      markers: markers,
      maxZoom: BMapConfig.maxZoom,
      isAverageCenter: true,
    }, (event, data) => {
      if (eventMap.get(event.type)) {
        eventMap.get(event.type)(event, data);
      }
    });
  }

  updateMarker(type, data, fn?) {
    if (type === 'add') {
      // 新增
      this.addMarker(this.createMarker(data, fn));
    } else if (type === 'update') {
      // 更新
      const marker = this.getMarkerById(data.deviceId);
      const imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, data.deviceType, data.deviceStatus);
      const _icon = this.getIcon(imgUrl, this._iconSize);
      marker.setIcon(_icon);
      marker.show();
      marker.isShow = true;
      this.markersMap.set(data.deviceId, {marker: marker, data: data});
    } else if (type === 'delete') {
      // 删除
      const marker = this.getMarkerById(data.deviceId);
      this.markerClusterer.removeMarker(marker);
      this.markersMap.delete(data.deviceId);
    } else if (type === 'hide') {
      // 隐藏
      this.hideMarker(data.deviceId);
    } else if (type === 'show') {
      // 显示
      this.showMarker(data.deviceId);
    } else {
    }
  }

  /**
   * 重汇聚合点
   */
  redraw() {
    if (this.markerClusterer) {
      this.markerClusterer.redraw();
    }
  }

  /**
   * 隐藏其他的marker点
   * param data
   */
  hideOther(data) {
    for (const [key, value] of this.markersMap) {
      if (data.indexOf(key) < 0) {
        value.marker.hide();
        value.marker.isShow = false;
      }
    }
  }

  /**
   * 清除覆盖物
   * param overlay
   */
  removeOverlay(overlay) {
    this.mapInstance.removeOverlay(overlay);
  }

  /**
   * 添加marker点
   * param point
   * param id
   * param fn
   */
  addMarker(marker) {
    this.markerClusterer.addMarker(marker);
  }

  /**
   * 通过id获取marker
   * param id
   * returns {any}
   */
  getMarkerById(id) {
    if (this.markersMap.get(id)) {
      return this.markersMap.get(id).marker;
    } else {
      return null;
    }
  }

  /**
   * 通过id获取marker对应的数据
   * param id
   */
  getMarkerDataById(id) {
    if (this.markersMap.get(id)) {
      return this.markersMap.get(id).data;
    } else {
      return null;
    }
  }

  getMarkerMap(): Map<string, any> {
    return this.markersMap;
  }

  /**
   * 切换图标
   * param url
   * returns {BMap.Icon}
   */
  toggleIcon(url) {
    const icon = new BMap.Icon(url, this._iconSize);
    icon.setImageSize(this._iconSize);
    return icon;
  }

  createSize(width, height) {
    return new BMap.Size(width, height);
  }

  setIconSize() {
    const size = this.iconSize.split('-');
    this._iconSize = this.createSize(size[0], size[1]);
  }

  getIcon(url, size) {
    const icon = new BMap.Icon(url, size);
    icon.setImageSize(size);
    return icon;
  }

  fitMapToMarkers() {

  }

  /**
   * 添加城市切换控件
   */
  addCityListControl() {
    const size = this.createSize(70, 20);
    console.log(BMap);
    this.mapInstance.addControl(new BMap.CityListControl({
      anchor: BMAP_ANCHOR_TOP_LEFT,
      offset: size,
      // 切换城市之间事件
      // onChangeBefore: function (){
      //    alert('before');
      // },
      // 切换城市之后事件
      onChangeAfter: () => {
        if (document.getElementById('selCityTip').style.display === 'none') {
          this.cityListChange({type: 'cityChange'});
        }
      }
    }));
  }

  /**
   * 城市切换插件添加监听（插件未提供打开关闭监听）
   */
  addEventListenerToCityListControl() {
    const $el = document.getElementsByClassName('ui_city_change_top')[0];
    if ($el) {
      clearTimeout(this.cityTimer);
      $el.addEventListener('click', () => {
        setTimeout(() => {
          const bol = $el.classList.contains('ui_city_change_click');
          this.cityListChange({type: 'cityListControlStatus', value: bol});
        }, 100);
      });
      document.getElementById('popup_close').addEventListener('click', () => {
        this.cityListChange({type: 'cityListControlStatus', value: false});
      });
    } else {
      this.cityTimer = setTimeout(() => {
        this.addEventListenerToCityListControl();
      }, 200);
    }
  }

  /**
   * 设置中心点
   */
  setCenterPoint(zoom = null) {
    const {lng, lat} = this.getCenterPoint();
    this.setCenterAndZoom(lng, lat, zoom);
  }

  getZoom() {
    return this.mapInstance.getZoom();
  }

  /**
   * 通过经纬度设置地图中心点及缩放级别
   * param lng
   * param lat
   * param {number} zoom
   */
  public setCenterAndZoom(lng, lat, zoom?) {
    const point = this.createPoint(lng, lat);
    if (zoom) {
      this.mapInstance.centerAndZoom(point, zoom);
    } else {
      this.mapInstance.setCenter(point);
    }
  }

  panTo(lng, lat, bol = false) {
    this.mapInstance.panTo(this.createPoint(lng, lat), {noAnimation: bol});
  }

  /**
   * 定位到当前城市
   */
  locateToUserCity(bol = false) {
    if (bol) {
      const myFun = (result) => {
        const cityName = result.name;
        console.log('当前所在城市: ' + cityName);
        this.setCenterByCityName(cityName);
      };
      const myCity = new BMap.LocalCity();
      myCity.get(myFun);
    }
  }

  /**
   * 通过城市名称设置地图中心
   * param {string} cityName
   */
  setCenterByCityName(cityName: string) {
    this.mapInstance.centerAndZoom(cityName);
  }

  getLocation(lng, lat, fn) {
    // 创建地址解析器实例
    const geoCoder = new BMap.Geocoder();
    // 将地址解析结果显示在地图上，并调整地图视野
    const point = new BMap.Point(lng, lat);
    geoCoder.getLocation(point, fn);
  }

  addOverlay(marker) {
    const overlay = new BMap.Marker(marker);
    this.mapInstance.addOverlay(overlay);
    return overlay;
  }

  addMarkerMap(marker) {
    this.mapInstance.addOverlay(marker);
  }

  searchLocation(key, fn) {
  }

  changeAllIconSize(iconSize) {
    this.iconSize = iconSize;
    this.setIconSize();
    for (const [key, value] of this.markersMap) {
      const imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, value.data.deviceType, value.data.deviceStatus);
      const _icon = this.getIcon(imgUrl, this._iconSize);
      value.marker.setIcon(_icon);
    }
  }

  /**
   * 添加连线
   */
  addLine() {
    const polyline = new BMap.Polyline(
      [
        new BMap.Point(114.399, 26.910),
        new BMap.Point(113.905, 26.920),
        new BMap.Point(112.985037, 23.15046)
      ],
      {strokeColor: 'blue', strokeWeight: 2, strokeOpacity: 0.5}
    );   // 创建折线
    this.mapInstance.addOverlay(polyline);   // 增加折线
  }

  /**
   * 添加地图类型控件
   */
  addMapTypeControl() {
    this.mapInstance.addControl(new BMap.MapTypeControl({
        anchor: BMAP_ANCHOR_BOTTOM_RIGHT,
        mapTypes: [
          BMAP_NORMAL_MAP,
          BMAP_SATELLITE_MAP
        ]
      })
    );
  }

  hideMarker(id) {
    this.getMarkerById(id).hide();
    this.getMarkerById(id).isShow = false;
  }

  showMarker(id) {
    this.getMarkerById(id).show();
    this.getMarkerById(id).isShow = true;
  }

  locationByAddress(name) {

  }

  setMapTypeId(type) {

  }

  /**
   * 给地图添加事件
   */
  addEventListenerToMap() {
    this.mapInstance.addEventListener('zoomend', () => {
      console.log('地图缩放至：' + this.getZoom() + '级');
      this.mapEventEmitter({type: 'zoomend'});
    });

    this.mapInstance.addEventListener('dragend', () => {
      this.mapEventEmitter({type: 'dragend'});
    });

    this.mapInstance.addEventListener('click', e => {
      const type = e.overlay ? e.overlay.toString() : '';
      console.log(e);
      if (type !== '[object Marker]') {
        this.mapEventEmitter({type: 'click'});
      } else {
      }
    });
  }

  clearMarkerMap() {
    // this.markerClusterer.clearMarkers();
    this.markersMap.clear();
  }

  /**
   * 获取中心点
   * returns {any}
   */
  getCenter() {
    return this.mapInstance.getCenter();
  }

  getBounds() {
    const bounds = this.mapInstance.getBounds();
    return {
      topLat: bounds.getNorthEast().lat,
      bottomLat: bounds.getSouthWest().lat,
      leftLng: bounds.getSouthWest().lng,
      rightLng: bounds.getNorthEast().lng,
    };
  }

  getOffset() {
    return {
      offsetX: this.mapInstance.offsetX,
      offsetY: this.mapInstance.offsetY,
    };
  }

  pointToOverlayPixel(lng, lat) {
    console.log(lng);
    console.log(lat);
    return this.mapInstance.pointToOverlayPixel(this.createPoint(lng, lat));
  }

  /**
   * 添加缩放平移控件
   */
  addNavigationControl() {
    // 左上角，添加默认缩放平移控件
    this.mapInstance.addControl(new BMap.NavigationControl());
  }
}
