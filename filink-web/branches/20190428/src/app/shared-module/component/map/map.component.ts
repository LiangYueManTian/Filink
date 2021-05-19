import {
  AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges,
  ViewChild
} from '@angular/core';
import {CommonUtil} from '../../util/common-util';
import {NzI18nService} from 'ng-zorro-antd';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {FACILITY_TYPE_NAME} from '../../const/facility';
import {BMapService} from './b-map.service';
import {GMapService} from './g-map.service';
import {MapAbstract} from './map-abstract';
import {MapConfig as BMapConfig} from './b-map.config';
import {MapConfig as GMapConfig} from './g-map.config';
import {MapConfig} from './map.config';

declare let BMap: any;   // 一定要声明BMap，要不然报错找不到BMap
declare let BMapLib: any;

declare let google: any;
declare let MarkerClusterer: any;

@Component({
  selector: 'xc-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit, OnChanges, AfterViewInit, OnDestroy {
  @Input() mapConfig: MapConfig;    // 地图配置
  @Input() data: any[];    // 要渲染的设施数据
  @Input() iconSize: string;   // 图标大小   18-24
  @Input() facilityId: string;   // 选中设施id
  @Output() mapEvent = new EventEmitter();    // 地图事件回传
  @ViewChild('map') map: ElementRef;
  _iconSize;
  mapService: MapAbstract;
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
  fn: any;   // marker点事件
  cb: any;   // 百度地图聚合点回调
  maxZoom;   // 最大缩放级别
  defaultZoom;   // 默认缩放级别
  searchKey = '';  // 谷歌地图地理位置搜索框
  mapTypeId;  // 地图类别id
  mapType;   // 地图类型
  constructor(private $nzI18n: NzI18nService,
              private $el: ElementRef,) {
  }

  ngOnInit() {
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.mapTypeId = 'roadmap';
    this.initFn();
    this.initCb();
  }

  ngAfterViewInit() {
    if (!this.mapConfig) {
      console.warn('请传入地图配置信息');
      return;
    }
    if (!this.mapConfig.mapId) {
      console.warn('请传入id');
    } else {
      this.map.nativeElement.setAttribute('id', this.mapConfig.mapId);
    }
    this.mapType = this.mapConfig.mapType;
    if (this.mapType === 'google') {
      this.initGMap();
    } else if (this.mapType === 'baidu') {
      this.initBMap();
    } else {
      console.warn('不支持该类型地图');
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.mapService && changes.data && changes.data.currentValue) {
      if (changes.data.previousValue) {
        this.updateMarkers(this.data);
      } else {
        this.addMarkers(this.data);
      }
    }
    if (this.mapService && changes.iconSize && changes.iconSize.currentValue) {
      this.setIconSize(changes.iconSize.previousValue);
    }
    if (this.mapService && changes.facilityId) {
      // this.setIconSize();
      if (changes.facilityId.currentValue) {
        this.selectMarker();
      } else {
        this.resetTargetMarker();
      }
    }
  }

  ngOnDestroy() {
    this.mapService.clearMarkerMap();
  }

  initBMap() {
    this.mapType = 'baidu';
    try {
      if (!BMap || !BMapLib) {
        console.error('百度地图资源未加载');
        return;
      }
      this.mapService = new BMapService(this.mapConfig);
      this.mapService.cityListHook().subscribe(result => {
        this.mapEvent.emit(result);
      });
      this.addEventListenerToMap();
      this.addMarkers(this.data);
      this.maxZoom = BMapConfig.maxZoom;
      this.defaultZoom = BMapConfig.defaultZoom;
      const size = this.mapConfig.defaultIconSize.split('-');
      // this._iconSize = new BMap.Size(...size);
      this._iconSize = this.mapService.createSize(size[0], size[1]);
    } catch (e) {
      console.error('百度地图资源未加载');
    }
  }


  initGMap() {
    this.mapType = 'google';
    try {
      if (!google || !MarkerClusterer) {
        console.error('谷歌地图资源未加载');
        return;
      }
      this.mapService = new GMapService(this.mapConfig);
      this.addEventListenerToMap();
      this.maxZoom = GMapConfig.maxZoom;
      this.defaultZoom = GMapConfig.defalutZoom;
      const size = this.mapConfig.defaultIconSize.split('-');
      // this._iconSize = new BMap.Size(...size);
      this._iconSize = this.mapService.createSize(size[0], size[1]);
    } catch (e) {
      console.error('谷歌地图资源未加载');
    }
  }


  closeOverlayInfoWindow() {
    if (this.targetMarker) {
      this.resetTargetMarker();
    }
    this.targetMarker = null;
  }

  /**
   * 向地图中添加点
   * param {any[]} facilityData
   */
  private addMarkers(facilityData) {
    if (!facilityData) {
      return;
    }
    const arr = [];
    if (facilityData.length === 0 && !this.facilityId) {
      this.mapService.locateToUserCity(true);
      return;
    }
    facilityData.forEach(item => {
      arr.push(this.mapService.createMarker(item, this.fn
      ));
    });
    this.mapService.addMarkerClusterer(arr, this.cb);
    if (!this.facilityId) {
      // if (this.mapType === 'g') {
      //   this.mapService.fitMapToMarkers();
      // } else {
      this.mapService.setCenterPoint(this.defaultZoom);
      // }
    } else {
      this.selectMarker();
    }
  }

  /**
   * 更新marker点
   * param {any[]} facilityData
   */
  updateMarkers(facilityData: any[] = []) {
    // console.log(facilityData);
    const arr = facilityData.map(item => item.deviceId) || [];
    facilityData.forEach(item => {
      if (!this.mapService.getMarkerDataById(item.deviceId)) {
        this.mapService.updateMarker('add', item, this.fn);
      } else {
        this.mapService.updateMarker('update', item);
      }
    });
    this.mapService.hideOther(arr);
    this.mapService.redraw();
  }

  /**
   * marker点点击事件
   * param event
   */
  markerClickEvent(event) {
    console.log(event.target);
    this.resetTargetMarker();
    const id = event.target.customData.id;
    const data = this.mapService.getMarkerDataById(id);
    const imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, data.deviceType);
    const _icon = this.mapService.toggleIcon(imgUrl);
    event.target.setIcon(_icon);
    this.targetMarker = this.mapService.getMarkerById(id);
    // const centerPoint = this.mapService.getCenter();
    // const _dLng = Math.abs(parseFloat(centerPoint.lng) - parseFloat(data.lng));
    // const _dLat = Math.abs(parseFloat(centerPoint.lat) - parseFloat(data.lat));
    // const maxD = 0.00001;   // 大于此差值可判断为不在中心点
    // if (_dLng > maxD || _dLat > maxD) {
    //   this.hideInfoWindow();
    // }
    this.mapService.panTo(data.lng, data.lat);
    this.mapEvent.emit({type: 'selected', id: id});
  }

  /**
   * 聚合点点击事件
   */
  clustererClickEvent(event, markers) {
    const arr = [];
    markers.forEach(marker => {
      const info = this.mapService.getMarkerDataById(marker.customData.id);
      arr.push(info);
    });

    const centerPoint = this.mapService.getCenter();
    const clustererCenter = this.getClustererCenter(arr);
    // const _dLng = Math.abs(parseFloat(centerPoint.lng) - parseFloat(clustererCenter.lng));
    // const _dLat = Math.abs(parseFloat(centerPoint.lat) - parseFloat(clustererCenter.lat));
    // const maxD = 0.00001;   // 大于此差值可判断为不在中心点
    // if (_dLng > maxD || _dLat > maxD) {
    //   this.hideInfoWindow();
    // }
    this.mapService.panTo(clustererCenter.lng, clustererCenter.lat);
    clearTimeout(this.timer);
    this.timer = setTimeout(() => {
      this.mapEvent.emit({type: 'clickClusterer', data: arr});
    }, 0);
  }

  /**
   * 获取聚合点的中心点
   * param markers
   * returns {{lat: number; lng: number}}
   */
  getClustererCenter(markers) {
    let sumLng = 0, sumLat = 0;
    markers.forEach(info => {
      sumLng += parseFloat(info.lng);
      sumLat += parseFloat(info.lat);
    });
    const _avgLng = sumLng / markers.length;
    const _avgLat = sumLat / markers.length;
    return {
      lat: _avgLat,
      lng: _avgLng
    };
  }

  /**
   * 模拟title提示框  marker点
   * param e
   */
  openMInfoWindow(e, m) {
    let id;
    if (this.mapConfig.mapType === 'google') {
      id = e.target.customData.id;
    } else {
      id = e.currentTarget.customData.id;
    }
    const info = this.mapService.getMarkerDataById(id);
    const name = `${this.getFacilityTypeName(info.deviceType)} ${info.deviceName} ${info.areaName}`;
    this.infoData = {
      type: 'm',
      data: {
        name: name,
        number: info.deviceCode,
        address: info.address,
        className: CommonUtil.getFacilityIconClassName(info.deviceType)
      }
    };
    // console.log(info);
    this.showInfoWindow('m', info.lng, info.lat);
  }

  /**
   * 模拟title提示框  聚合点点
   * param e
   * param markers
   */
  openCInfoWindow(e, markers = []) {
    const obj = {};
    const _markers = [];
    markers.forEach(marker => {
      const info = this.mapService.getMarkerDataById(marker.customData.id);
      _markers.push(info);
      if (obj[info.deviceType]) {
        obj[info.deviceType].push(info);
      } else {
        obj[info.deviceType] = [info];
      }
    });
    const clustererCenter = this.getClustererCenter(_markers);
    const arr = [];
    Object.keys(obj).forEach(key => {
      arr.push({
        deviceType: key,
        className: CommonUtil.getFacilityIconClassName(key),
        deviceTypeName: this.getFacilityTypeName(key),
        count: `（${obj[key].length}）`
      });
    });
    this.infoData = {
      type: 'c',
      data: arr
    };
    this.showInfoWindow('c', clustererCenter.lng, clustererCenter.lat);
  }

  /**
   * 设置iconSize
   * param previousValue
   */
  setIconSize(previousValue) {
    const size = this.iconSize.split('-');
    this._iconSize = this.mapService.createSize(size[0], size[1]);
    console.log(this._iconSize);
    if (this.mapService && previousValue && previousValue !== this.iconSize) {
      this.mapService.changeAllIconSize(this.iconSize);
    }
    if (this.facilityId) {
      this.selectMarker();
    }
  }

  /**
   * 重置之前选中marker点样式
   */
  resetTargetMarker() {
    // console.log(this.targetMarker);
    if (this.targetMarker) {
      const data = this.mapService.getMarkerDataById(this.targetMarker.customData.id);
      const imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, data.deviceType, data.deviceStatus);
      const _icon = this.mapService.getIcon(imgUrl, this._iconSize);
      this.targetMarker.setIcon(_icon);
    }
  }

  /**
   * 鼠标移入显示信息
   * param info   设施点信息
   * param type   类型  c m
   */
  showInfoWindow(type, lng, lat) {
    const pixel = this.mapService.pointToOverlayPixel(lng, lat);
    const offset = this.mapService.getOffset();
    let _top = offset.offsetY + pixel.y;
    if (type === 'c') {
      _top = _top - 20;
    } else if (type === 'm') {
      const iconHeight = parseInt(this._iconSize.height, 10);
      _top = _top - iconHeight + 16;
      if (this.mapType === 'google') {
        _top = _top - 10;
      }
    } else {
    }
    this.infoWindowLeft = offset.offsetX + pixel.x + 'px';
    this.infoWindowTop = _top + 'px';
    this.isShowInfoWindow = true;
  }

  /**
   * 鼠标移出隐藏信息
   */
  hideInfoWindow() {
    this.isShowInfoWindow = false;
  }

  /**
   * 获取设施类型名称
   * param deviceType
   * returns {any | string}
   */
  getFacilityTypeName(deviceType) {
    return this.indexLanguage[FACILITY_TYPE_NAME[deviceType]] || '';
  }

  /**
   * 地图添加监听
   */
  addEventListenerToMap() {
    this.mapService.mapEventHook().subscribe(data => {
      const type = data.type;
      const target = data.target;
      if (target === 'c') {
        if (type === 'click') {
          this.clustererClickEvent(data.event, data.markers);
        } else if (type === 'mouseover') {
          this.openCInfoWindow(data.event, data.markers);
        } else if (type === 'mouseout') {
          this.hideInfoWindow();
        } else {
        }
      } else {
        if (type === 'zoomend') {
          this.hideInfoWindow();
        } else if (type === 'dragend') {
          this.resetTargetMarker();
          this.hideInfoWindow();
          this.mapEvent.emit({type: 'mapDrag'});
          this.closeOverlayInfoWindow();
        } else if (type === 'click') {
          this.resetTargetMarker();
          this.mapEvent.emit({type: 'mapClick'});
        }
      }
    });
  }

  /**
   * 设施选中
   */
  selectMarker() {
    // console.log(this.facilityId);
    const marker = this.mapService.getMarkerById(this.facilityId);
    if (!marker) {
      console.warn('该设施无法定位');
      return;
    }
    const data = this.mapService.getMarkerDataById(this.facilityId);
    this.mapService.setCenterAndZoom(data.lng, data.lat, this.maxZoom);
    const imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, data.deviceType);
    // console.log(imgUrl);
    const _icon = this.mapService.getIcon(imgUrl, this._iconSize);
    marker.setIcon(_icon);
    this.targetMarker = marker;
  }

  /**
   * 定位
   */
  location() {
    const key = this.searchKey.trim();
    if (!key) {
      return;
    }
    this.mapService.locationByAddress(key);
  }

  setMapType(type) {  // terrain  roadmap  hybrid  satellite
    this.mapTypeId = type;
    this.mapService.setMapTypeId(type);
  }

  initFn() {
    this.fn = [
      {
        eventName: 'click',
        eventHandler: (event) => {
          this.markerClickEvent(event);
        }
      },
      // 地图上的设施点悬浮显示信息面板
      {
        eventName: 'mouseover',
        eventHandler: (event, m) => {
          this.openMInfoWindow(event, m);
        }
      },
      {
        eventName: 'mouseout',
        eventHandler: () => {
          this.hideInfoWindow();
        }
      }
    ];
  }

  initCb() {
    this.cb = [
      {
        eventName: 'onmouseover',
        eventHandler: (event, markers) => {
          this.openCInfoWindow(event, markers);
        }
      },
      {
        eventName: 'onmouseout',
        eventHandler: (event) => {
          this.hideInfoWindow();
        }
      },
      {
        eventName: 'onclick',
        eventHandler: (event, markers) => {
          this.clustererClickEvent(event, markers);
        }
      }
    ];
  }
}
