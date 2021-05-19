import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {FacilityListComponent} from '../facility-list/facility-list.component';
import {ActivatedRoute, Router} from '@angular/router';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {NzI18nService} from 'ng-zorro-antd';
import {MapService} from '../../../core-module/api-service/index/map';
import {facilityStatusList} from '../facility';
import {Result} from '../../../shared-module/entity/result';
import {MapConfig, MAP_ICON_CONFIG} from '../config';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';

declare let google: any;
declare let MarkerClusterer: any;

@Component({
  selector: 'app-g-map',
  templateUrl: './g-map.component.html',
  styleUrls: ['./g-map.component.scss']
})
export class GMapComponent implements OnInit, AfterViewInit, OnDestroy {
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

  promptTitle: string;
  indexLanguage: IndexLanguageInterface;
  commonLanguage: CommonLanguageInterface;
  isShowInfoWindow = false;
  isShowFacilityPanel = false; // 是否展开设施详情面板
  isShowClustererFacilityTable = false;   // 展开显示聚合点设施详情
  clustererFacilityList = [];
  isShowPrompt = false;  // 是否显示弹框
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
  targetMarker;
  iconSize;
  facilityId;
  searchKey;
  geocoder;
  mapTypeId;
  timer;

  @ViewChild('facilityListComponent') facilityListComponent: FacilityListComponent;

  constructor(private $mapStoreService: MapStoreService,
              private $nzI18n: NzI18nService,
              private $mapService: MapService,
              private $message: FiLinkModalService,
              private $router: Router,
              private $activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.mapTypeId = 'roadmap';
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
    console.log(!google);
    if (google) {
      this.geocoder = new google.maps.Geocoder();
      this.setIconSize();
      this.initGoogleMap();
    } else {
    }
  }

  /**
   * 初始化谷歌地图
   */
  initGoogleMap() {
    this.map = new google.maps.Map(document.getElementById('container'), {
      zoom: MapConfig.defalutZoom,
      center: {lat: 30, lng: 120},
      mapTypeControl: false,
      mapTypeControlOptions: {
        style: google.maps.MapTypeControlStyle.DEFAULT,
        position: google.maps.ControlPosition.TOP_RIGHT
      },
      zoomControl: true,
      zoomControlOptions: {
        position: google.maps.ControlPosition.LEFT_TOP
      },
      scaleControl: true,
      // scaleControlControl: {
      //   position: google.maps.ControlPosition.CENTER_BOTTOM
      // },
      streetViewControl: false,
      streetViewControlOptions: {
        position: google.maps.ControlPosition.LEFT_TOP
      },
      fullscreenControl: false,
      clickableIcons: false
    });
    this.map.addListener('zoom_changed', () => {
      this.closeClustererFacilityTable();
      console.log(this.map.zoom);
      if (this.map.getZoom() >= MapConfig.gMaxZoom) {
        this.addListenerToClusterer();
      } else {
       // this.closeOverlayInfoWindow();
      }
    });
    this.map.addListener('dragend', () => {
      this.closeInfoWindow();
      this.closeOverlayInfoWindow();
    });

    this.map.addListener('click', e => {
      if (!e.info) {
        this.closeOverlayInfoWindow();
      } else {
        this.closeClustererFacilityTable();
      }
    });

    this.map.addListener('maptypeid_changed', () => {
      console.log(this.map.getMapTypeId());
    });

    this.setMapType('roadmap');
    this.setCenterAndZoom(116.3964, 39.9093, 3);
    // this.addLine();
    this.checkUser();
    if (this.$mapStoreService.isInitConfig) {
      setTimeout(() => {
        this.resetFilter();
      }, 500);
    } else {
      this.getAllMapConfig();
    }
  }

  checkUser() {
    if (sessionStorage.getItem('token') !== this.$mapStoreService.token) {
      this.$mapStoreService.isInitData = false;
      this.$mapStoreService.resetData();
    }
    this.$mapStoreService.token = sessionStorage.getItem('token');
  }

  customMap() {
    function CoordMapType(tileSize) {
      this.tileSize = tileSize;
    }

    CoordMapType.prototype.maxZoom = 19;
    CoordMapType.prototype['name'] = 'Tile #s';
    CoordMapType.prototype.alt = 'Tile Coordinate Map Type';

    CoordMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
      const div = ownerDocument.createElement('div');
      div.innerHTML = coord;
      div.style.width = this.tileSize.width + 'px';
      div.style.height = this.tileSize.height + 'px';
      div.style.fontSize = '10';
      div.style.borderStyle = 'solid';
      div.style.borderWidth = '1px';
      div.style.borderColor = '#AAAAAA';
      div.style.backgroundColor = '#E5E3DF';
      return div;
    };

    this.map = new google.maps.Map(document.getElementById('map'), {
      zoom: 10,
      center: {lat: 41.850, lng: -87.650},
      streetViewControl: false,
      mapTypeId: 'coordinate',
      mapTypeControlOptions: {
        mapTypeIds: ['coordinate', 'roadmap'],
        style: google.maps.MapTypeControlStyle.DROPDOWN_MENU
      }
    });

    this.map.addListener('maptypeid_changed', () => {
      const showStreetViewControl = this.map.getMapTypeId() !== 'coordinate';
      this.map.setOptions({
        streetViewControl: showStreetViewControl
      });
    });

    // Now attach the coordinate map type to the map's registry.
    this.map.mapTypes.set('coordinate', new CoordMapType(new google.maps.Size(256, 256)));
  }

  location() {
    console.log(this.searchKey);
    const key = this.searchKey.trimLeft().trimRight();
    if (!key) {
      return;
    }
    this.geocoder.geocode({'address': key}, (results, status) => {
      if (status === 'OK') {
        this.map.setCenter(results[0].geometry.location);
      } else {
      }
    });
  }

  /**
   * 获取地图配置信息
   */
  getAllMapConfig() {
    this.$mapStoreService.resetData();
    this.$mapService.getALLFacilityConfig().subscribe((result: Result) => {
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
            iconClass: this.facilityTypeIconClassObj[item.deviceType],
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
    }, () => {
    });
  }

  resetFilter(bol = true) {
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
        checked: bol ? true : item.checked,
        iconClass: item.iconClass,
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

  getALLFacilityList() {
    this.$message.loading(this.commonLanguage.loading, 1000 * 60);
    this.$mapService.getALLFacilityList().subscribe((result: Result) => {
      this.$message.remove();
      if (result.data.length === 0) {
        this.getLocation();
      } else {
        this.addMarkers(result.data);
        this.$mapStoreService.isInitData = true;
      }
    }, () => {
      this.$message.remove();
    });
  }

  getLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.setCenterAndZoom(position.coords.longitude, position.coords.latitude, MapConfig.defalutZoom);
      }, () => {
        console.log(this.map.getCenter());
      });
    } else {
      // Browser doesn't support Geolocation
      console.log(this.map.getCenter());
    }
  }


  getAllFacilityListFromStore() {
    this.facilityIconSizeValue = this.$mapStoreService.facilityIconSize;
    this.addMarkersFromStore();
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
    this.setCenterPoint();
    if (this.markerClusterer) {
      this.markerClusterer.clearMarkers();
    }
    const imgPath = 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m';
    this.markerClusterer = new MarkerClusterer(this.map, markers, {averageCenter: true, imagePath: imgPath});
    // this.addListenerToClusterer();
    this.$mapStoreService.isInitData = true;
    if (this.targetMarker) {
      this.openFacilityPanel(this.targetMarker);
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
    const imgPath = 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m';
    this.markerClusterer = new MarkerClusterer(this.map, markers, {averageCenter: true, imagePath: imgPath});
    // this.addListenerToClusterer();
    if (this.targetMarker) {
      this.openFacilityPanel(this.targetMarker);
    }
  }

  addListenerToClusterer() {
    if (!this.markerClusterer) {
      return;
    }
    google.maps.event.addListener(this.markerClusterer, 'mouseover', c => {
      // console.log('mouseover');
      const mapType = this.map.getMapTypeId();
      const bol = (mapType === 'roadmap' && this.map.getZoom() >= MapConfig.gMaxZoom) ||
        (mapType === 'satellite' && this.map.getZoom() >= MapConfig.gStarMaxZoom);
      if (bol) {
        this.openClustererInfoWindow(event, c.getMarkers());
      }
    });
    google.maps.event.addListener(this.markerClusterer, 'mouseout', c => {
      // console.log('mouseout');
      this.closeInfoWindow();
    });
    google.maps.event.addListener(this.markerClusterer, 'click', c => {
      const mapType = this.map.getMapTypeId();
      const bol = (mapType === 'roadmap' && this.map.getZoom() >= MapConfig.gMaxZoom) ||
        (mapType === 'satellite' && this.map.getZoom() >= MapConfig.gStarMaxZoom);
      if (bol) {
        const arr = c.getMarkers().map(item => item.info);
        this.clustererFacilityList = arr;
        // this.isShowClustererFacilityTable = true;
        clearTimeout(this.timer);
        this.timer = setTimeout(() => {
          this.isShowClustererFacilityTable = true; }, 100);
      }
    });
  }

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
    this.map.setCenter(new google.maps.LatLng(lat, lng));
    this.map.setZoom(zoom);
  }

  /**
   * 关掉覆盖物提示框
   */
  closeOverlayInfoWindow() {
    if (this.targetMarker ) {
      this.resetTargetMarker();
    }
    this.closeFacilityPanel();
    this.closeClustererFacilityTable();
    this.targetMarker = null;
  }

  closeClustererFacilityTable() {
    this.isShowClustererFacilityTable = false;
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
    // this.addListenerToClusterer();
    if (this.control.isExpandFacilityList) {
      this.refreshFacilityList();
    }
  }

  updateCenterPoint(point) {
    const lat = parseFloat(point.lat), lng = parseFloat(point.lng);
    this.maxLng = this.maxLng ? (lng > this.maxLng ? lng : this.maxLng) : lng;
    this.minLng = this.minLng ? (lng < this.minLng ? lng : this.minLng) : lng;
    this.maxLat = this.maxLat ? (lat > this.maxLat ? lat : this.maxLat) : lat;
    this.minLat = this.minLat ? (lat < this.minLat ? lat : this.minLat) : lat;
  }

  setCenterPoint() {
    if (this.facilityId) {
      const info = this.$mapStoreService.getMarker(this.facilityId);
      if (info) {
        this.setCenterAndZoom(info.lng, info.lat,  MapConfig.gMaxZoom);
        this.facilityId = null;
      } else {
        this.setCenterAndZoom((this.maxLng + this.minLng) / 2, (this.maxLat + this.minLat) / 2, MapConfig.defalutZoom);
      }
    } else {
        this.setCenterAndZoom((this.maxLng + this.minLng) / 2, (this.maxLat + this.minLat) / 2, MapConfig.defalutZoom);
    }
  }

  removeCenterPoint() {
    this.maxLat = null;
    this.maxLng = null;
    this.minLat = null;
    this.minLng = null;
  }

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

  openMarkerInfoWindow(target, e) {
    this.infoWindowLeft = e.va.clientX + 'px';
    this.infoWindowTop = e.va.clientY + 'px';
    const info = target.info;
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
    this.isShowInfoWindow = true;
  }


  openClustererInfoWindow(e, markers = []) {
    this.infoWindowLeft = e.x + 'px';
    this.infoWindowTop = e.y + 'px';
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
      arr.push({
        deviceType: key,
        deviceTypeName : this.getFacilityTypeName(key),
        count: `（${obj[key].length}）`,
        className: CommonUtil.getFacilityIconClassName(key),
      });
    });
    this.infoData = {
      type: 'c',
      data: arr
    };
    this.isShowInfoWindow = true;
  }

  closeInfoWindow() {
    this.isShowInfoWindow = false;
  }

  getFacilityStatusName(deviceStatus) {
    return this.indexLanguage[this.facilityStatusNameObj[deviceStatus]] || '';
  }

  getFacilityTypeName(deviceType) {
    return this.indexLanguage[this.facilityTypeNameObj[deviceType]] || '';
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
    google.maps.event.addListener(target, 'mouseover', e => {
      this.openMarkerInfoWindow(target, e);
    });
    google.maps.event.addListener(target, 'mouseout', e => {
      this.closeInfoWindow();
    });
    google.maps.event.addListener(target, 'click', e => {
      if (this.targetMarker) {
        this.resetTargetMarker();
      }
      this.changeIcon(target);
      this.selectedIndex = 0;
      this.openFacilityPanel(target);
      const centerPoint = this.map.getCenter();
      if (parseFloat(centerPoint.lng()).toFixed(4) !== parseFloat(target.info.lng).toFixed(4) ||
        parseFloat(centerPoint.lat()).toFixed(4) !== parseFloat(target.info.lat).toFixed(4)) {
        this.closeInfoWindow();
      }
      this.setCenterAndZoom(target.info.lng, target.info.lat,  MapConfig.gMaxZoom);
    });
  }

  resetTargetMarker() {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue,
      this.targetMarker.info.deviceType, this.targetMarker.info.deviceStatus);
    const icon = new google.maps.MarkerImage(iconUrl,  this.iconSize);
    this.targetMarker.setIcon(icon);
  }

  changeIcon(target) {
    const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue, target.info.deviceType);
    const icon = new google.maps.MarkerImage(iconUrl, this.iconSize);
    // icon.setImageSize(new BMap.Size(30, 30));
    target.setIcon(icon);
    this.targetMarker = target;
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

  refreshFacilityList() {
    this.facilityListComponent.getAllShowData();
    this.facilityListComponent.setSelectOption();
  }


  openFacilityList() {
    this.control.isExpandFacilityList = true;
    if (this.control.isExpandLogicArea) {
      this.control.isExpand = true;
    }
  }

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

  openLogicArea() {
    this.control.isExpandLogicArea = true;
    if (this.control.isExpandFacilityList) {
      this.control.isExpand = true;
    }
  }

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
   * param lng
   * param lat
   */
  createMarker(point) {
    const _lat = parseFloat(point.lat);
    const _lng = parseFloat(point.lng);
    const iconUrl = CommonUtil.getFacilityIconUrl(this.facilityIconSizeValue, point.deviceType, point.deviceStatus);
    const icon = new google.maps.MarkerImage(iconUrl,  this.iconSize);
    const _point = new google.maps.Marker({position: {lat: _lat, lng: _lng}, icon});
    _point.info = point;
    return _point;
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
    }, () => {
    });
  }

  confirm() {
    if (this.promptControl.isShowFacilityTypeConfig) {
      this.modifyFacilityTypeConfig();
    } else if (this.promptControl.isShowMapConfig) {
      this.modifyMapConfig();
    } else {
    }
  }

  closePrompt() {
    this.isShowPrompt = false;
    this.promptControl.isShowMapConfig = false;
    this.promptControl.isShowFacilityTypeConfig = false;
  }

  ngOnDestroy() {
    this.markers = [];
    this.$message.remove();
    this.map = null;
  }

  setIconSize() {
    const size = this.$mapStoreService.facilityIconSize.split('-');
    this.iconSize = new google.maps.Size(...size);
  }

  setMapType(type) {  // terrain  roadmap  hybrid  satellite
    this.mapTypeId = type;
    this.map.setMapTypeId(type);
  }
}
