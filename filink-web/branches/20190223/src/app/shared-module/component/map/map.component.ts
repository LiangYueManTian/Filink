import {Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {MapDefaultConfig} from './config';
import {MapConfig} from '../../../business-module/index/config';
import {CommonUtil} from '../../util/common-util';
import {NzI18nService} from 'ng-zorro-antd';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {MapStoreService} from '../../../core-module/store/map.store.service';

declare let BMap: any;   // 一定要声明BMap，要不然报错找不到BMap
declare let BMapLib: any;
declare const BMAP_NORMAL_MAP: any;
declare const BMAP_SATELLITE_MAP: any;
declare const BMAP_ANCHOR_BOTTOM_RIGHT: any;
declare const OBMap_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;

declare let google: any;
declare let MarkerClusterer: any;

@Component({
  selector: 'xc-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnChanges {
  @Input() mapId: string;
  @Input() mapType: string = 'b';
  @Input() mapConfig: any;
  @Input() data: any[];
  @Input() iconSize: string;
  @Output() mapEvent = new EventEmitter();
  @ViewChild('map') map: ElementRef;
  mapInstance;
  markerClusterer;
  scaleControl;    // 地图比例尺控件
  navigationControl;   // 地图缩放平移控件
  maxLng;
  minLng;
  maxLat;
  minLat;
  infoData = {
    type: '',
    data: null
  };
  infoWindowLeft = '0';
  infoWindowTop = '0';
  // 模拟鼠标移上去时的提示框
  isShowInfoWindow = false;
  targetMarker;
  timer;
  indexLanguage: IndexLanguageInterface;
  facilityStatusNameObj;
  facilityTypeNameObj;
  cityTimer;
  constructor(
    private $nzI18n: NzI18nService,
    private $mapStoreService: MapStoreService,
  ) {
  }

  ngOnInit() {
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.facilityStatusNameObj = this.$mapStoreService.getFacilityStatusNameObj();
    this.facilityTypeNameObj = this.$mapStoreService.getFacilityTypeNameObj();
    if (!this.mapId) {
      console.warn('请传入id');
    } else {
      this.map.nativeElement.setAttribute('id', this.mapId);
    }
    this.mapType === 'b' ? this.initBMap() : this.initGMap();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.mapInstance && changes.data && changes.data.currentValue) {
      console.log(this.data);
      this.addMarkers(this.data);
    }
    if (this.mapInstance && changes.iconSize && changes.iconSize.currentValue) {
      console.log(this.iconSize);
    }
  }

  initBMap() {
    if (!BMap || !BMapLib) {
      console.error('百度地图资源未加载');
      return;
    }
    this.mapInstance = new BMap.Map(this.mapId, {enableMapClick: false});
    this.mapInstance.enableScrollWheelZoom();
    this.getLocalCity();
    this.addNavigationControl();
    // this.addLine();
    this.addMapTypeControl();
    this.addCityListControl();
    this.mapInstance.addEventListener('zoomend', () => {
      // this.closeOverlayInfoWindow();
      console.log('地图缩放至：' + this.mapInstance.getZoom() + '级');
      if (this.mapInstance.getZoom() >= MapConfig.showLineZoom) {
        // this.addLine();
      } else {
        this.hideInfoWindow();
      }
    });

    this.mapInstance.addEventListener('dragend', () => {
      this.closeOverlayInfoWindow();
    });

    this.mapInstance.addEventListener('click', e => {
      const type = e.overlay ? e.overlay.toString() : '';
      console.log(e);
      if (type !== '[object Marker]') {
        this.closeOverlayInfoWindow();
      } else {
      }
    });
    this.mapInstance.addEventListener('load', e => {
      console.log(e);
      setTimeout(() => {
        this.addEventListenerToCityListControl();
      }, 200);
    });
  }


  getLocation(lng, lat) {
    // 创建地址解析器实例
    const myGeo = new BMap.Geocoder();
// 将地址解析结果显示在地图上，并调整地图视野
    const point = new BMap.Point(lng, lat);
    myGeo.getLocation(point, function(r) {
      console.log(r);
    });
  }

  /**
   * 通过ip获取当前所在城市
   */
  getLocalCity() {
    const myFun = (result) => {
      const cityName = result.name;
      this.setCenterByCityName(cityName);
      console.log('当前定位城市: ' + cityName);
    };
    const myCity = new BMap.LocalCity();
    myCity.get(myFun);
  }

  initGMap() {
    if (!google || !MarkerClusterer) {
      console.error('谷歌地图资源未加载');
      return;
    }
  }

  /**
   * 通过城市名称设置地图中心
   * param {string} cityNmae
   */
  setCenterByCityName(cityName: string) {
    this.mapInstance.centerAndZoom(cityName);
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

  /**
   * 添加城市切换控件
   */
  addCityListControl() {
    const size = new BMap.Size(70, 20);
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
        this.mapEvent.emit({type: 'cityChange'});
      }
    }));
  }

  /**
   * 添加比例尺控件
   */
  addScaleControl() {
    this.scaleControl = new BMap.ScaleControl({anchor: OBMap_ANCHOR_TOP_LEFT}); // 左上角，添加比例尺
    this.mapInstance.addControl(this.scaleControl);
  }

  /**
   * 移除比例尺控件
   */
  removeScaleControl() {
    this.mapInstance.removeControl(this.scaleControl);
  }

  /**
   * 添加缩放平移控件
   */
  addNavigationControl() {
    this.navigationControl = new BMap.NavigationControl();  // 左上角，添加默认缩放平移控件
    this.mapInstance.addControl(this.navigationControl);
  }

  closeOverlayInfoWindow() {
    if (this.targetMarker) {
      this.resetTargetMarker();
    }
    this.targetMarker = null;
  }

  addEventListenerToCityListControl() {
    const $el =  document.getElementsByClassName('ui_city_change_top')[0];
    if ($el) {
      clearTimeout(this.cityTimer);
      $el.addEventListener('click', () => {
          setTimeout(() => {
          const bol = $el.classList.contains('ui_city_change_click');
          this.mapEvent.emit({type: 'cityListControlStatus', value: bol});
        }, 100);
      });
    } else {
      this.cityTimer = setTimeout(() => {
        this.addEventListenerToCityListControl();
      }, 200);
    }
  }

  /**
   * 更新中心点
   * param point
   */
  updateCenterPoint(point) {
    const lat = parseFloat(point.lat), lng = parseFloat(point.lng);
    this.maxLng = this.maxLng ? (lng > this.maxLng ? lng : this.maxLng) : lng;
    this.minLng = this.minLng ? (lng < this.maxLng ? lng : this.minLng) : lng;
    this.maxLat = this.maxLat ? (lat > this.maxLat ? lat : this.maxLat) : lat;
    this.minLat = this.minLat ? (lat < this.minLat ? lat : this.minLat) : lat;
  }

  setIconSize() {

  }


  /**
   * 添加数据
   */
  addMarkers(data) {
    console.log('addMarkers');
    this.removeCenterPoint();
    const markers = [];
    for (let i = 0; i < data.length; i++) {
      const point = data[i];
      point.lng = point.positionBase.split(',')[0];
      point.lat = point.positionBase.split(',')[1];
      point.deviceTypeName = this.getFacilityTypeName(point.deviceType);
      point.deviceStatusName = this.getFacilityStatusName(point.deviceStatus);
      if (!point.lng || !point.lat) {
        continue;
      }
      this.updateCenterPoint(point);
      const marker = this.createMarker(point);
      this.addEventListener(marker);
      markers.push(marker);
    }
    // 最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
    // console.log(markers);
    this.setCenterPoint();
    if (this.markerClusterer) {
      this.markerClusterer.clearMarkers();
    }
    this.markerClusterer = new BMapLib.MarkerClusterer(this.mapInstance, {
      markers: markers,
      maxZoom: MapConfig.maxZoom
    }, this.callback);
    this.getOverlays();
  }

  getOverlays() {
    console.log(this.mapInstance.getOverlays());
  }

  /**
   * 移除最大最小经纬度
   */
  removeCenterPoint() {
    this.maxLat = null;
    this.maxLng = null;
    this.minLat = null;
    this.minLng = null;
  }

  /**
   * 设置中心点
   */
  setCenterPoint() {
    // if (this.facilityId) {
    //   const info = this.$mapStoreService.getMarker(this.facilityId);
    //   if (info) {
    //     this.setCenterAndZoom(info.lng, info.lat,  MapConfig.maxZoom);
    //     this.getOverlays();
    //     this.facilityId = null;
    //   } else {
    //     this.setCenterAndZoom((this.maxLng + this.minLng) / 2, (this.maxLat + this.minLat) / 2, MapConfig.defalutZoom);
    //   }
    // } else {
      this.setCenterAndZoom((this.maxLng + this.minLng) / 2, (this.maxLat + this.minLat) / 2, MapConfig.defalutZoom);
    // }
  }

  /**
   * 通过经纬度设置地图中心点及缩放级别
   * param lng
   * param lat
   * param {number} zoom
   */
  setCenterAndZoom(lng, lat, zoom = 0) {
    const point = new BMap.Point(lng, lat);
    if (zoom) {
      this.mapInstance.centerAndZoom(point, zoom);
    } else {
      this.mapInstance.setCenter(point);
    }
  }

  /**
   * 自定义的聚合点事件回调
   * event
   * markers
   * map
   */
  callback = (event, markers) => {
    const type = event.type;
    if (type === 'onclick') {
      const centerPoint = this.mapInstance.getCenter();
      const arr = markers.map(item => item.info);
      if (parseFloat(centerPoint.lng) !== parseFloat(arr[0].lng) || parseFloat(centerPoint.lat) !== parseFloat(arr[0].lat)) {
        // this.closeInfoWindow();
      }
      this.setCenterAndZoom(arr[0].lng, arr[0].lat, MapConfig.maxZoom);
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.mapEvent.emit({type: 'prompt', data: arr});
         }, 0);
    } else if (type === 'onmouseover') {
      // console.log('onmouseover');
      this.openInfoWindow('c', event, markers);
    } else if (type === 'onmouseout') {
      // console.log('onmouseout');
      this.hideInfoWindow();
    } else {
      // console.log('未知事件');
    }
  }

  /**
   * marker添加事件
   * target
   */
  addEventListener(target) {
    target.addEventListener('mouseover', e => {
      // console.log('mouseover');
      this.openInfoWindow('m', e);
    });
    target.addEventListener('mouseout', e => {
      // console.log('mouseout');
      this.hideInfoWindow();
    });
    target.addEventListener('click', e => {
      // console.log('click');
      if (this.targetMarker) {
        this.resetTargetMarker();
      }
      this.changeIcon(target);
      // this.openFacilityPanel(target);
      // this.markerClusterer.removeMarker(target);
    });
  }

  /**
   * 模拟title提示框
   * param type
   * param e
   * param {any[]} markers
   */
  openInfoWindow(type, e, markers = []) {
    this.infoWindowLeft = e.pixel.x + 'px';
    this.infoWindowTop = e.pixel.y + 'px';
    if (type === 'm') {
      const info = e.target.info;
      const name = `${info.deviceTypeName}-${info.deviceName}`;
      this.infoData = {
        type: 'm',
        data: {
          name: name,
          number: info.deviceCode,
          address: info.address,
          className: CommonUtil.getFacilityIconClassName(info.deviceType)
        }
      };
    } else if (type === 'c') {
      // console.log(e);
      const obj = {};
      markers.forEach(marker => {
        const info = marker.info;
        if (obj[info.deviceType]) {
          obj[info.deviceType].push(info);
        } else {
          obj[info.deviceType] = [info];
        }
      });
      const arr = [];
      Object.keys(obj).forEach(key => {
        console.log(CommonUtil.getFacilityIconClassName(key));
        arr.push({
          deviceType: key,
          className: CommonUtil.getFacilityIconClassName(key),
          deviceTypeName : this.getFacilityTypeName(key),
          count: `（${obj[key].length}）`
        });
      });
      this.infoData = {
        type: 'c',
        data: arr
      };
    }
    this.showInfoWindow();
  }

  /**
   * 生成标记点
   * param point
   * returns {BMap.Marker}
   */
  createMarker(point) {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.iconSize, point.deviceType, point.deviceStatus);
    const icon = new BMap.Icon(iconUrl, this.iconSize);
    icon.setImageSize(this.iconSize);
    const marker = new BMap.Marker(new BMap.Point(point.lng, point.lat), {icon: icon});
    marker.info = point;
    return marker;
  }

  /**
   * 改变marker图标
   * param target
   */
  changeIcon(target) {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.iconSize, target.info.deviceType);
    const icon = new BMap.Icon(iconUrl, this.iconSize);
    icon.setImageSize(this.iconSize);
    target.setIcon(icon);
    this.targetMarker = target;
  }

  /**
   * 重置之前选中marker点样式
   */
  resetTargetMarker() {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.iconSize,
      this.targetMarker.info.deviceType, this.targetMarker.info.deviceStatus);
    const icon = new BMap.Icon(iconUrl, this.iconSize);
    this.targetMarker.setIcon(icon);
  }

  /**
   * 鼠标移入显示信息
   */
  showInfoWindow() {
    this.isShowInfoWindow = true;
  }

  /**
   * 鼠标移出隐藏信息
   */
  hideInfoWindow() {
    this.isShowInfoWindow = false;
  }

  /**
   * 获取设施状态名称
   * param deviceStatus
   * returns {any | string}
   */
  getFacilityStatusName(deviceStatus) {
    return this.indexLanguage[this.facilityStatusNameObj[deviceStatus]] || '';
  }

  /**
   * 获取设施类型名称
   * param deviceType
   * returns {any | string}
   */
  getFacilityTypeName(deviceType) {
    return this.indexLanguage[this.facilityTypeNameObj[deviceType]] || '';
  }


}
