import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MapConfig, MAP_ICON_CONFIG} from '../config';
import {facilityStatusList} from '../facility';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {NzI18nService} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import {MapService} from '../../../core-module/api-service/index/map';
import {Result} from '../../../shared-module/entity/result';
import {FacilityListComponent} from '../facility-list/facility-list.component';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';


declare let BMap: any;   // 一定要声明BMap，要不然报错找不到BMap
declare let BMapLib: any;
declare const BMAP_NORMAL_MAP: any;
declare const BMAP_HYBRID_MAP: any;
declare const BMAP_SATELLITE_MAP: any;
declare const BMAP_ANCHOR_BOTTOM_RIGHT: any;
declare const OBMap_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;
declare const BMAP_STATUS_SUCCESS: any;

@Component({
  selector: 'app-b-map',
  templateUrl: './b-map.component.html',
  styleUrls: ['./b-map.component.scss']
})

export class BMapComponent implements OnInit, AfterViewInit, OnDestroy {
  markers = [];   // 设施点集
  map;      // 地图
  scaleControl;    // 地图比例尺控件
  navigationControl;   // 地图缩放平移控件
  markerClusterer;
  selectedIndex = MapConfig.selectedIndex;
  selectFacilityId;   // 点击设施的id

  infoWindowLeft = '0px';
  infoWindowTop = '0px';
  control = {
    isExpand: false,                // 是否展开左侧组件
    isExpandLogicArea: false,      // 是否展开逻辑区域筛选组件
    isExpandFacilityList: false,   // 是否展开设施列表筛选组件
    isExpandMyCollection: false,   // 是否展开我的关注组件
    isExpandFacilityType: false,   // 是否展开设施类型筛选组件
    isExpandFacilityStatus: false, // 是否展开设施状态筛选组件
  };
  promptControl = {
    isShowMapConfig: false,  // 是否显示地图配置
    isShowFacilityTypeConfig: false   // 是否显示设施类型配置
  };
  isShowInfoWindow = false;
  isShowFacilityPanel = false; // 是否展开设施详情面板
  isShowClustererFacilityTable = false;   // 展开显示聚合点设施详情
  isShowPrompt = false;  // 是否显示弹框
  targetMarker;  // 点击的marker点
  promptTitle: string;   // 弹出框标题
  indexLanguage: IndexLanguageInterface;
  commonLanguage: CommonLanguageInterface;
  clustererFacilityTableLeft = '0px';
  clustererFacilityTableTop = '0px';
  clustererFacilityList = [];
  facilityStatusList;   // 设施状态集
  facilityTypeList;    // 设置类型集
  facilityTypeConfig;   // 设施类型配置
  facilityIconSizeConfig;   // 设施图标大小配置
  facilityIconSizeValue;   // 设施图标大小
  selectedFacilityTypeIdsArr = [];     // 选中的设施类型id集
  selectedFacilityStatusArr = [];   // 选中的设施状态id集
  selectedLogicAreaIdsArr = [];   // 选中的逻辑区域id集
  isFacilityTypeAllChecked = true;    // 设施类型是否全选
  isFacilityStatusAllChecked = true;  // 设施状态是否全选
  facilityStatusNameObj = {};
  facilityTypeNameObj = {};
  facilityTypeIconClassObj = {};
  maxLng;
  minLng;
  maxLat;
  minLat;
  infoData = {
    type: '',
    data: null
  };
  iconSize;
  facilityId;
  timer;
  cityTimer;
  isShowLeftComponents = true;
  @ViewChild('facilityListComponent') facilityListComponent: FacilityListComponent;

  constructor(private $mapStoreService: MapStoreService,
              private $nzI18n: NzI18nService,
              private $mapService: MapService,
              private $message: FiLinkModalService,
              private $router: Router,
              private $activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.facilityId = this.$activatedRoute.snapshot.queryParams.id;
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.facilityIconSizeConfig = MAP_ICON_CONFIG.iconConfig;
    this.facilityIconSizeValue = MAP_ICON_CONFIG.defalutIconSize;
    this.facilityStatusNameObj = this.$mapStoreService.getFacilityStatusNameObj();
    this.facilityTypeNameObj = this.$mapStoreService.getFacilityTypeNameObj();
    this.facilityTypeIconClassObj = this.$mapStoreService.getFacilityTypeIconClassObj();
  }

  ngAfterViewInit() {
    console.log(!BMap);
    if (BMap) {
      this.setIconSize();
      this.initBMap();
    } else {
    }
  }

  /**
   * 初始化百度地图
   */
  initBMap() {
    this.map = new BMap.Map('container', {enableMapClick: false});
    // this.setMapStyle();
    this.map.enableScrollWheelZoom();
    this.map.addEventListener('zoomend', () => {
      // this.closeOverlayInfoWindow();
      console.log('地图缩放至：' + this.map.getZoom() + '级');
      if (this.map.getZoom() >= MapConfig.showLineZoom) {
        // this.addLine();
      } else {
      }
    });

    this.map.addEventListener('dragend', () => {
      this.closeOverlayInfoWindow();
    });

    this.map.addEventListener('click', e => {
      const type = e.overlay ? e.overlay.toString() : '';
      console.log(e);
      if (type !== '[object Marker]') {
        this.closeOverlayInfoWindow();
      } else {
        this.closeClustererFacilityTable();
      }
    });

    this.map.centerAndZoom(new BMap.Point(116.3964, 39.9093), 3);
    this.addNavigationControl();
    // this.addLine();
    this.addMapTypeControl();
    this.checkUser();
    if (this.$mapStoreService.isInitConfig) {
      this.resetFilter();
    } else {
      this.getAllMapConfig();
    }
    setTimeout(() => {
      this.addCityListControl();
    }, 0);
  }

  checkUser() {
    if (sessionStorage.getItem('token') !== this.$mapStoreService.token) {
      this.$mapStoreService.isInitData = false;
      this.$mapStoreService.resetData();
    }
    this.$mapStoreService.token = sessionStorage.getItem('token');
  }

  /**
   * 关掉覆盖物提示框
   */
  closeOverlayInfoWindow() {
    if (this.targetMarker) {
      this.resetTargetMarker();
    }
    this.closeFacilityPanel();
    this.closeClustererFacilityTable();
    this.targetMarker = null;
  }

  /**
   * 重置之前选中marker点样式
   */
  resetTargetMarker() {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue,
      this.targetMarker.info.deviceType, this.targetMarker.info.deviceStatus);
    const icon = new BMap.Icon(iconUrl, this.iconSize);
    this.targetMarker.setIcon(icon);
  }

  /**
   * 获取地图配置信息
   */
  getAllMapConfig() {
    this.$mapService.getALLFacilityConfig().subscribe((result: Result) => {
      console.log(result);
      this.$mapStoreService.resetData();
      if (result.code === 0) {
        if (result.data.deviceIconSize) {
          this.facilityIconSizeValue = result.data.deviceIconSize;
          this.$mapStoreService.facilityIconSize = result.data.deviceIconSize;
          this.setIconSize();
        }
        this.$mapStoreService.facilityTypeConfig = result.data.deviceConfig.map(item => {
          return {
            value: item.deviceType,
            label: this.indexLanguage[this.facilityTypeNameObj[item.deviceType]],
            checked: item.configValue === '1',
            iconClass: CommonUtil.getFacilityIconClassName(item.deviceType),
          };
        });
        this.$mapStoreService.isInitConfig = true;
        this.$mapStoreService.facilityTypeList =  this.$mapStoreService.facilityTypeConfig.filter(item => item.checked);
        this.$mapStoreService.facilityStatusList =  facilityStatusList().map(item => {
          item.label = this.indexLanguage[item.label];
          return item;
        });
        this.resetFilter(true);
      } else {
        this.$message.error(result.msg);
      }
    },  () => {
    });
  }

  /**
   * 重置设施状态和设施类型过滤插件
   * param {boolean} bol  // 是否重置为true
   */
  resetFilter(bol = false) {
    this.facilityStatusList = this.$mapStoreService.facilityStatusList.map(item => {
      return {
        value: item.value,
        label: item.label,
        bgColor: item.bgColor,
        checked: bol ? true : item.checked,
      };
    });
    this.facilityTypeList = this.$mapStoreService.facilityTypeList.map(item => {
      return {
        value: item.value,
        label: item.label,
        iconClass: item.iconClass,
        checked: bol ? true : item.checked,
      };
    });
    this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.checked)
      .map(item => item.areaId);
    this.isFacilityTypeAllChecked = this.facilityTypeList.every(item => item.checked);
    this.isFacilityStatusAllChecked = this.facilityStatusList.every(item => item.checked);
    this.selectedFacilityStatusArr = this.facilityStatusList.filter(item => item.checked).map(item => item.value);
    this.selectedFacilityTypeIdsArr = this.facilityTypeList.filter(item => item.checked).map(item => item.value);
    this.$mapStoreService.isInitData ? this.getAllFacilityListFromStore() : this.getALLFacilityList();
  }

  /**
   * 获取设施列表
   */
  getALLFacilityList() {
    this.$message.loading(this.commonLanguage.loading, 1000 * 60);
    this.$mapService.getALLFacilityList().subscribe((result: Result) => {
      this.$message.remove();
      if (result.data.length === 0) {
        this.getLocalCity();
      } else {
        this.addMarkers(result.data);
        this.$mapStoreService.isInitData = true;
      }
    }, () => {
      this.$message.remove();
    });
  }

  getLocation() {
    // 创建地址解析器实例
    const myGeo = new BMap.Geocoder();
// 将地址解析结果显示在地图上，并调整地图视野
    const point = new BMap.Point(116.331398, 39.897445);
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

  /**
   * 从服务缓存中获取设施列表数据
   */
  getAllFacilityListFromStore() {
    this.facilityIconSizeValue = this.$mapStoreService.facilityIconSize;
    this.addMarkersFromStore();
  }

  /**
   * 设置图片大小
   */
  setIconSize() {
    const size = this.$mapStoreService.facilityIconSize.split('-');
    this.iconSize = new BMap.Size(...size);
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
      point.deviceTypeName = this.getFacilityTypeName(point.deviceType);
      point.deviceStatusName = this.getFacilityStatusName(point.deviceStatus);
      point.lng = point.positionBase.split(',')[0];
      point.lat = point.positionBase.split(',')[1];
      if (point.lng && point.lat) {
        if (this.checkFacility(point)) {
          this.updateCenterPoint(point);
          const marker = this.createMarker(point);
          this.addEventListener(marker);
          if (point.deviceId === this.facilityId) {
            this.changeIcon(marker);
          }
          markers.push(marker);
          this.$mapStoreService.updateMarker(point, true);
        } else {
          this.$mapStoreService.updateMarker(point, false);
        }
      } else {
        console.log(point);
      }
    }
    // 最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
    this.setCenterPoint();
    if (this.markerClusterer) {
      this.markerClusterer.clearMarkers();
    }
    this.markerClusterer = new BMapLib.MarkerClusterer(this.map, {
      markers: markers,
      maxZoom: MapConfig.maxZoom
    }, this.callback);
    if (this.targetMarker) {
      this.openFacilityPanel(this.targetMarker);
    }
  }

  addEventListenerToCityListControl() {
    const $el =  document.getElementsByClassName('ui_city_change_top')[0];
    if ($el) {
      clearTimeout(this.cityTimer);
      $el.addEventListener('click', () => {
        setTimeout(() => {
          const bol = $el.classList.contains('ui_city_change_click');
          this.changeLeftComponents(bol);
        }, 100);
      });
      document.getElementById('popup_close').addEventListener('click', () => {
        this.changeLeftComponents(false);
      });
    } else {
      this.cityTimer = setTimeout(() => {
        this.addEventListenerToCityListControl();
      }, 200);
    }
  }

  changeLeftComponents(bol) {
    if (bol) {
      this.isShowLeftComponents = false;
    } else {
      this.isShowLeftComponents = true;
    }
  }

  /**
   * 从缓存添加数据
   */
  addMarkersFromStore() {
    console.log('addMarkers');
    const markers = [];
    this.removeCenterPoint();
    for (const [key, value] of this.$mapStoreService.mapData) {
      const point = value['info'];
      if (this.checkFacility(point)) {
        this.updateCenterPoint(point);
        const marker = this.createMarker(point);
        this.addEventListener(marker);
        if (point.deviceId === this.facilityId) {
          this.changeIcon(marker);
        }
        markers.push(marker);
        this.$mapStoreService.updateMarker(point, true);
      } else {
        this.$mapStoreService.updateMarker(point, false);
      }
    }
    // 最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
    // console.log(markers);
    this.setCenterPoint();
    if (this.markerClusterer) {
      this.markerClusterer.clearMarkers();
    }
    this.markerClusterer = new BMapLib.MarkerClusterer(this.map, {
      markers: markers,
      maxZoom: MapConfig.maxZoom
    }, this.callback);
    if (this.targetMarker) {
      this.openFacilityPanel(this.targetMarker);
    }
  }

  /**
   * 校检设施
   * param item
   * returns {boolean}
   */
  checkFacility(item) {
    if (this.selectedFacilityTypeIdsArr.indexOf(item.deviceType) < 0) {
      return false;
    }
    if (this.selectedFacilityStatusArr.indexOf(item.deviceStatus) < 0) {
      return false;
    }
    if (this.$mapStoreService.isInitLogicAreaData && this.selectedLogicAreaIdsArr.indexOf(item.areaId) < 0) {
      return false;
    }
    return true;
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
      this.map.centerAndZoom(point, zoom);
    } else {
      this.map.setCenter(point);
    }
  }

  /**
   * 通过城市名称设置地图中心
   * param {string} cityNmae
   */
  setCenterByCityName(cityName: string) {
    this.map.centerAndZoom(cityName);
  }

  /**
   * 更细标记点
   */
  updateMarkers() {
    const markers = [];
    for (const [key, value] of this.$mapStoreService.mapData) {
      const point = value['info'];
      if (this.checkFacility(point)) {
        const marker = this.createMarker(point);
        this.addEventListener(marker);
        markers.push(marker);
        this.$mapStoreService.updateMarker(point, true);
      } else {
        this.$mapStoreService.updateMarker(point, false);
      }
    }
    // 最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
    // console.log(markers);
    // this.map.clearOverlays();
    // console.log(markers);
    this.markerClusterer.clearMarkers();
    this.markerClusterer.addMarkers(markers);
    if (this.control.isExpandFacilityList) {
      this.refreshFacilityList();
    }
  }

  /**
   * 更新中心点
   * param point
   */
  updateCenterPoint(point) {
    const lat = parseFloat(point.lat), lng = parseFloat(point.lng);
    this.maxLng = this.maxLng ? (lng > this.maxLng ? lng : this.maxLng) : lng;
    this.minLng = this.minLng ? (lng < this.minLng ? lng : this.minLng) : lng;
    this.maxLat = this.maxLat ? (lat > this.maxLat ? lat : this.maxLat) : lat;
    this.minLat = this.minLat ? (lat < this.minLat ? lat : this.minLat) : lat;
  }

  /**
   * 设置中心点
   */
  setCenterPoint() {
    if (this.facilityId) {
      const info = this.$mapStoreService.getMarker(this.facilityId);
      if (info) {
        this.setCenterAndZoom(info.lng, info.lat,  MapConfig.maxZoom);
        this.facilityId = null;
      } else {
        this.setCenterAndZoom((this.maxLng + this.minLng) / 2, (this.maxLat + this.minLat) / 2, MapConfig.defalutZoom);
      }
    } else {
      this.setCenterAndZoom((this.maxLng + this.minLng) / 2, (this.maxLat + this.minLat) / 2, MapConfig.defalutZoom);
    }
  }

  getOverlays() {
    console.log(this.map.getOverlays());
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
   * 展开或是收起，控制设施列表，区域列表
   */
  expandAndFold() {
    this.closeFacilityPanel();
    this.control.isExpand = !this.control.isExpand;
    if (this.control.isExpand) {
      this.control.isExpandLogicArea = true;
      this.control.isExpandFacilityList = true;
    } else {
      this.foldLeftComponents();
    }
  }

  /**
   * 收起左侧组件
   */

  foldLeftComponents() {
    this.control.isExpand = false;
    this.control.isExpandLogicArea = false;
    this.control.isExpandFacilityList = false;
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
      const centerPoint = this.map.getCenter();
      const arr = markers.map(item => item.info);
      if (parseFloat(centerPoint.lng) !== parseFloat(arr[0].lng) || parseFloat(centerPoint.lat) !== parseFloat(arr[0].lat)) {
        this.closeInfoWindow();
      }
      this.setCenterAndZoom(arr[0].lng, arr[0].lat, MapConfig.maxZoom);
      this.clustererFacilityList = arr;
      this.clustererFacilityTableLeft = event.pixel.x + 'px';
      this.clustererFacilityTableTop = event.pixel.y + 'px';
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.isShowClustererFacilityTable = true; }, 0);
    } else if (type === 'onmouseover') {
      // console.log('onmouseover');
      this.openInfoWindow('c', event, markers);
    } else if (type === 'onmouseout') {
      // console.log('onmouseout');
      this.closeInfoWindow();
    } else {
      // console.log('未知事件');
    }
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
      const name = `${info.deviceTypeName}-${info.deviceName} ${info.areaName}`;
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
    this.isShowInfoWindow = true;
  }

  /**
   * 关闭提示框
   */
  closeInfoWindow() {
    this.isShowInfoWindow = false;
  }

  /**
   * 关闭聚合点提示的设施table
   */
  closeClustererFacilityTable() {
    this.isShowClustererFacilityTable = false;
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
    this.map.addOverlay(polyline);   // 增加折线
  }

  /**
   * 添加地图类型控件
   */
  addMapTypeControl() {
    this.map.addControl(new BMap.MapTypeControl({
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
    this.map.addControl(new BMap.CityListControl({
      anchor: BMAP_ANCHOR_TOP_LEFT,
      offset: size,
      // 切换城市之间事件
      // onChangeBefore: function (){
      //    alert('before');
      // },
      // 切换城市之后事件
      onChangeAfter: (event) => {
        if (document.getElementById('selCityTip').style.display === 'none') {
          this.closeFacilityPanel();
          this.closeOverlayInfoWindow();
          this.changeLeftComponents(false);
        }
      }
    }));
    this.addEventListenerToCityListControl();
  }

  /**
   * 添加比例尺控件
   */
  addScaleControl() {
    this.scaleControl = new BMap.ScaleControl({anchor: OBMap_ANCHOR_TOP_LEFT}); // 左上角，添加比例尺
    this.map.addControl(this.scaleControl);
  }

  /**
   * 移除比例尺控件
   */
  removeScaleControl() {
    this.map.removeControl(this.scaleControl);
  }

  /**
   * 添加缩放平移控件
   */
  addNavigationControl() {
    this.navigationControl = new BMap.NavigationControl();  // 左上角，添加默认缩放平移控件
    this.map.addControl(this.navigationControl);
  }

  /**
   * 设施类型过滤
   * event
   * id
   */
  facilityTypeChange(event, id) {
    const arr = [];
    if (id === 0) {
      if (event) {  // 全选
        this.facilityTypeList.forEach(item => {
          item.checked = true;
        });
      } else {  // 全不选
        this.facilityTypeList.forEach(item => {
          item.checked = false;
        });
      }
    } else {
      this.isFacilityTypeAllChecked = this.facilityTypeList.every(item => item.checked);
    }
    this.facilityTypeList.forEach(item => {
      if (item.checked) {
        arr.push(item.value + '');
      }
    });
    this.$mapStoreService.facilityTypeList = this.facilityTypeList;
    this.selectedFacilityTypeIdsArr = arr;
    this.updateMarkers();
    this.closeFacilityPanel();
    this.closeOverlayInfoWindow();
  }

  /**
   * 设施状态过滤
   * event
   */
  facilityStatusChange(event, id) {
    const arr = [];
    if (id === 0) {
      if (event) {  // 全选
        this.facilityStatusList.forEach(item => {
          item.checked = true;
        });
      } else {  // 全不选
        this.facilityStatusList.forEach(item => {
          item.checked = false;
        });
      }
    } else {
      this.isFacilityStatusAllChecked = this.facilityStatusList.every(item => item.checked);
    }
    this.$mapStoreService.facilityStatusList = this.facilityStatusList;
    this.facilityStatusList.forEach(item => {
      if (item.checked) {
        arr.push(item.value + '');
      }
    });
    this.selectedFacilityStatusArr = arr;
    this.updateMarkers();
    this.closeFacilityPanel();
    this.closeOverlayInfoWindow();
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
      this.closeInfoWindow();
    });
    target.addEventListener('click', e => {
      // console.log('click');
      this.selectedIndex = 0;
      if (this.targetMarker) {
        this.resetTargetMarker();
      }
      this.changeIcon(target);
      this.openFacilityPanel(target);
      const centerPoint = this.map.getCenter();
      if (parseFloat(centerPoint.lng) !== parseFloat(target.info.lng) || parseFloat(centerPoint.lat) !== parseFloat(target.info.lat)) {
        this.closeInfoWindow();
      }
      this.setCenterAndZoom(target.info.lng, target.info.lat,  MapConfig.maxZoom);
      // this.markerClusterer.removeMarker(target);
    });
  }

  /**
   * 改变marker图标
   * param target
   */
  changeIcon(target) {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue, target.info.deviceType);
    const icon = new BMap.Icon(iconUrl, this.iconSize);
    icon.setImageSize(this.iconSize);
    target.setIcon(icon);
    this.targetMarker = target;
  }

  /**
   * 移除缩放平移控件
   */
  removeNavigationControl() {
    this.map.removeControl(this.scaleControl);
  }

  /**
   * 切换tab
   * tab
   */
  changeTab(tab) {
    console.log(tab);
  }

  /**
   * 关闭设施详情
   */
  closeFacilityPanel() {
    this.isShowFacilityPanel = false;
  }

  /**
   * 刷新数据
   */
  refresh() {
    this.foldLeftComponents();
    this.closeOverlayInfoWindow();
    this.getAllMapConfig();
  }

  /**
   * 刷新设施列表
   */
  refreshFacilityList() {
    this.facilityListComponent.getAllShowData();
    this.facilityListComponent.setSelectOption();
  }

  /**
   * 打开设施列表
   */
  openFacilityList() {
    this.control.isExpandFacilityList = true;
    if (this.control.isExpandLogicArea) {
      this.control.isExpand = true;
    }
  }

  /**
   * 设施列表回传
   * param event
   */
  facilityListEvent(event) {
    this.closeFacilityPanel();
    if (event.type === 'location') {
      const info = event.info;
      this.setCenterAndZoom(info.lng, info.lat,  MapConfig.maxZoom);
    } else if (event.type === 'close') {
      this.control.isExpandFacilityList = false;
      if (!this.control.isExpandLogicArea) {
        this.control.isExpand = false;
      }
    }
  }

  /**
   * 打开区域列表
   */
  openLogicArea() {
    this.control.isExpandLogicArea = true;
    if (this.control.isExpandFacilityList) {
      this.control.isExpand = true;
    }
  }

  /**
   * 区域列表回传
   * param event
   */
  logicAreaEvent(event) {
    this.closeFacilityPanel();
    if (event.type === 'close') {
      this.control.isExpandLogicArea = false;
      if (!this.control.isExpandFacilityList) {
        this.control.isExpand = false;
      }
    } else if (event.type === 'update') {
      this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.checked).map(item => item.areaId);
       if (event.refresh) {
        this.updateMarkers();
      }
    } else {
    }
  }

  /**
   * 打开地图设置
   */
  openMapSetting() {
    this.promptTitle = this.indexLanguage.mapConfigTitle;
    this.promptControl.isShowMapConfig = true;
    this.isShowPrompt = true;
    this.closeFacilityPanel();
    this.closeOverlayInfoWindow();
    this.foldLeftComponents();
  }

  /**
   * 打开设施类型配置
   */
  openFacilityTypeSetting() {
    this.foldLeftComponents();
    this.closeOverlayInfoWindow();
    this.facilityTypeConfig = this.$mapStoreService.facilityTypeConfig.map(item => {
      return {
        value: item.value,
        label: item.label,
        checked: item.checked,
      };
    });
    this.promptTitle = this.indexLanguage.facilityTypeConfigTitle;
    this.promptControl.isShowFacilityTypeConfig = true;
    this.isShowPrompt = true;
  }

  /**
   * 生成标记点
   * param point
   * returns {BMap.Marker}
   */
  createMarker(point) {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue, point.deviceType, point.deviceStatus);
    const icon = new BMap.Icon(iconUrl, this.iconSize);
    icon.setImageSize(this.iconSize);
    const marker = new BMap.Marker(new BMap.Point(point.lng, point.lat), {icon: icon});
    marker.info = point;
    return marker;
  }


  /**
   * 打开设施详情面板
   * param target
   */
  openFacilityPanel(target) {
    this.isShowFacilityPanel = true;
    console.log(target);
    this.selectFacilityId = target.info.deviceId;
  }

  /**
   * 修改用户设施类型配置
   */
  modifyFacilityTypeConfig() {
    console.log(this.facilityTypeConfig);
    const data = this.facilityTypeConfig.map(item => {
      return {
        deviceType: item.value,
        configValue: item.checked ? '1' : '0'
      };
    });
    this.$message.loading(this.commonLanguage.saving);
    this.$mapService.modifyFacilityTypeConfig(data).subscribe((result: Result) => {
      console.log(result);
      if (result.code !== 0) {
        this.$message.remove();
        this.$message.error(result.msg);
        return;
      }
      this.$message.remove();
      this.$message.success(this.commonLanguage.operateSuccess);
      this.$mapStoreService.facilityTypeConfig = this.facilityTypeConfig.map(item => {
        return {
          value: item.value,
          label: item.label,
          checked: item.checked
        };
      });
      this.$mapStoreService.facilityTypeList =  this.$mapStoreService.facilityTypeConfig.filter(item => item.checked);
      setTimeout(() => {
        this.$message.remove();
        this.closePrompt();
        this.resetFilter();
      } , 500);
    }, () => {
      this.$message.remove();
    });
  }

  /**
   * 修改地图配置
   */
  modifyMapConfig() {
    const data = {configValue: this.facilityIconSizeValue};
    this.$message.loading(this.commonLanguage.saving);
    this.$mapService.modifyFacilityIconSize(data).subscribe((result: Result) => {
      if (result.code !== 0) {
        this.$message.remove();
        this.$message.error(result.msg);
        return;
      }
      console.log(result);
      this.$message.remove();
      this.$message.success(this.commonLanguage.operateSuccess);
      if (this.$mapStoreService.facilityIconSize !== this.facilityIconSizeValue) {
        this.$mapStoreService.facilityIconSize = this.facilityIconSizeValue;
        this.setIconSize();
        this.getAllFacilityListFromStore();
      }
      setTimeout(() => {
        this.$message.remove();
        this.closePrompt();
      } , 500);
    }, err => {
      this.$message.remove();
    });
  }

  /**
   * 弹出框确认按钮点击事件
   */
  confirm() {
    if (this.promptControl.isShowFacilityTypeConfig) {
      this.modifyFacilityTypeConfig();
    } else if (this.promptControl.isShowMapConfig) {
      this.modifyMapConfig();
    } else {
    }
  }

  /**
   * 关闭弹出框
   */
  closePrompt() {
    this.isShowPrompt = false;
    this.promptControl.isShowMapConfig = false;
    this.promptControl.isShowFacilityTypeConfig = false;
  }

  /**
   * 设置地图样式
   */
  setMapStyle() {
    const styleJson = [{
      'featureType': 'poilabel',
      'elementType': 'labels.icon',
      'stylers': {
        'visibility': 'off'
      }
    }];
    this.map.setMapStyle({styleJson: styleJson});
  }

  ngOnDestroy() {
    this.markers = [];
    this.$message.remove();
    this.map = null;
  }
}
