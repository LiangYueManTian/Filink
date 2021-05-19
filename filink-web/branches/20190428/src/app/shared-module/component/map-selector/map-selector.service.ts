/**
 * Created by xiaoconghu on 2019/1/8.
 */
import {DEFAUT_ZOOM, iconSize, iconUrl} from './map.config';
import {MapAbstract} from './map-abstract';
import {MapDrawingService} from './map-drawing.service';
import {CommonUtil} from '../../util/common-util';
import {FacilityInfo} from '../../../core-module/entity/facility/facility';
import {MapConfig} from '../map/b-map.config';

declare const BMap: any;
declare const BMapLib: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_RIGHT: any;

export class MapSelectorService extends MapAbstract {
  mapInstance;
  mapDrawUtil: MapDrawingService;
  markersMap = new Map();


  constructor(documentId, simpleConfig?) {
    super();
    this.mapInstance = this.createMap(documentId);

    // 增加城市控件
    if (!simpleConfig) {
      this.initCityControl();
      this.mapInstance.enableScrollWheelZoom(true);
      this.mapInstance.addControl(new BMap.NavigationControl());
    } else {
      this.mapInstance.disableDragging();
    }
  }

  createMap(documentId) {
    const mapInstance = new BMap.Map(documentId, {enableMapClick: false, maxZoom: MapConfig.maxZoom,});  // 创建Map实例
    mapInstance.setMapStyle({styleJson: []});   // 采用v1版本样式配置
    // 添加地图类型控件
    // mapInstance.setCurrentCity('北京');          // 设置地图显示的城市 此项是必须设置的
    // const point = new BMap.Point(116.331398, 39.897445);
    // mapInstance.centerAndZoom(point, 15);
    this.mapDrawUtil = new MapDrawingService(mapInstance);
    return mapInstance;
  }

  /**
   * 废弃
   * param point
   * param id
   * param fn
   */
  addMarker(point, id, fn?) {
    const icon = this.toggleIcon(iconUrl);
    const marker = new BMap.Marker(this.createPoint(point.position[0], point.position[1]), {
      icon: icon
    });
    marker.customData = {id: point.deviceId};
    if (fn) {
      marker.addEventListener(fn.eventName, fn.eventHandler);
    }
    this.markersMap.set(point.deviceId, {marker: marker, data: point});
    this.mapInstance.addOverlay(marker);
  }

  /**
   * 创建覆盖物
   * param point
   * param fn
   * returns {BMap.Marker}
   */
  createMarker(point, fn?) {
    const status = point.checked ? '0' : '1';
    const url = CommonUtil.getFacilityIconUrl(iconSize, point.deviceType, status);
    const icon = this.toggleIcon(url);
    const position = point.positionBase.split(',');
    const _lng = parseFloat(position[0]);
    const _lat = parseFloat(position[1]);
    super.updateCenterPoint(_lng, _lat);
    const marker = new BMap.Marker(this.createPoint(_lng, _lat), {
      icon: icon
    });
    marker.customData = {id: point.deviceId};
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
  addMarkerClusterer(markers, fn?): any {
    const eventMap = new Map();
    if (fn && fn.length > 0) {
      fn.forEach(item => {
        eventMap.set(item.eventName, item.eventHandler);
        // markerClusterer.addEventListener(item.eventName, item.eventHandler);
      });
    }
    const markerClusterer = new BMapLib.MarkerClusterer(this.mapInstance, {markers: markers, maxZoom: MapConfig.maxZoom}, (event, data) => {
      if (eventMap.get(event.type)) {
        eventMap.get(event.type)(event, data);
      }
    });
    // markerClusterer.setMinClusterSize(2);
    return markerClusterer;
  }

  /**
   * 清除覆盖物
   * param overlay
   */
  clearOverlay(overlay) {
    this.mapInstance.removeOverlay(overlay);
  }

  /**
   * 判断点是否在所选区域里面
   * param pt  具体的点
   * param poly 所选区域
   * returns {boolean}
   */
  isInsidePolygon(pt, poly) {
    let c = false;
    for (let i = -1, l = poly.length, j = l - 1; ++i < l; j = i) {
      if (((poly[i].lat <= pt.lat && pt.lat < poly[j].lat) || (poly[j].lat <= pt.lat && pt.lat < poly[i].lat)) &&
        (pt.lng < (poly[j].lng - poly[i].lng) * (pt.lat - poly[i].lat) / (poly[j].lat - poly[i].lat) + poly[i].lng)) {
        c = !c;
      }
    }
    return c;
  }

  /**
   * 通过id获取marker
   * param id
   * returns {any}
   */
  getMarkerById(id) {
    return this.markersMap.get(id).marker;
  }

  /**
   * 通过id获取marker对应的数据
   * param id
   */
  getMarkerDataById(id) {
    return this.markersMap.get(id).data;
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
    const icon = new BMap.Icon(url, new BMap.Size(18, 24));
    icon.setImageSize(new BMap.Size(18, 24));
    return icon;
  }

  /**
   * 添加城市控件
   */
  initCityControl() {
    // 增加城市控件
    this.mapInstance.addControl(new BMap.CityListControl({
      anchor: BMAP_ANCHOR_TOP_LEFT,
      offset: new BMap.Size(70, 10),
      // 切换城市之间事件
      onChangeBefore: function () {
      },
      // 切换城市之后事件
      onChangeAfter: function () {
      }
    }));
  }

  /**
   * 设置中心点
   */
  setCenterPoint(lat?, lng?, zoom?) {
    this.setCenterAndZoom(lng || (this.maxLng + this.minLng) / 2, lat || (this.maxLat + this.minLat) / 2, zoom || DEFAUT_ZOOM);
  }

  /**
   * 通过经纬度设置地图中心点及缩放级别
   * param lng
   * param lat
   * param {number} zoom
   */
  private setCenterAndZoom(lng, lat, zoom = 0) {
    const point = new BMap.Point(lng, lat);
    if (zoom) {
      this.mapInstance.centerAndZoom(point, zoom);
    } else {
      this.mapInstance.setCenter(point);
    }
  }

  /**
   * 定位到当前城市
   */
  locateToUserCity() {
    const myFun = (result) => {
      const cityName = result.name;
      this.mapInstance.centerAndZoom(cityName, DEFAUT_ZOOM);
    };
    const myCity = new BMap.LocalCity();
    myCity.get(myFun);
  }

  mockData() {
    for (let i = 0; i < 5; i++) {
      const a = new FacilityInfo();
      a.deviceId = i + '';
      a.positionBase = '113.323685, 23.130523';
      this.createMarker(a);
    }
  }

  getLocation(overlays, fn) {
    const geoCoder = new BMap.Geocoder();
    geoCoder.getLocation(overlays.point, fn);
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

  addZoomEnd(fn) {
    this.mapInstance.addEventListener('zoomend', fn);
  }
}
