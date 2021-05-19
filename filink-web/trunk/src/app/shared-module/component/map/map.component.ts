import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewChild
} from '@angular/core';
import {CommonUtil} from '../../util/common-util';
import {NzI18nService} from 'ng-zorro-antd';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {FACILITY_STATUS_COLOR, FACILITY_STATUS_NAME, FACILITY_TYPE_NAME} from '../../const/facility';
import {BMapService} from './b-map.service';
import {GMapService} from './g-map.service';
import {MapAbstract} from './map-abstract';
import {MapConfig as BMapConfig} from './b-map.config';
import {MapConfig as GMapConfig} from './g-map.config';
import {MapConfig} from './map.config';
import {AREA_LEVEL_COLOR, AREA_LEVEL_NAME} from '../../const/area';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {TableConfig} from '../../entity/tableConfig';
import {QueryCondition} from '../../entity/queryCondition';
import {PageBean} from '../../entity/pageBean';
import {Router} from '@angular/router';
import {FacilityService} from '../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../entity/result';
import {FacilityLanguageInterface} from '../../../../assets/i18n/facility/facility.language.interface';
import {InspectionLanguageInterface} from '../../../../assets/i18n/inspection-task/inspection.language.interface';
import {FiLinkModalService} from '../../service/filink-modal/filink-modal.service';
import {getCableLevel} from '../../../business-module/facility/facility.config';
import {index_facility_type} from '../../../business-module/index/index-const';
import {MapStoreService} from '../../../core-module/store/map.store.service';

// 一定要声明BMap，要不然报错找不到BMap
declare let BMap: any;
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
  @Input() areaData: any[];   // 区域信息
  @Output() mapEvent = new EventEmitter();    // 地图事件回传
  @ViewChild('map') map: ElementRef;  // 地图模板
  @ViewChild('tplFooter') public tplFooter; // 弹出框底部
  @ViewChild('radioTemp') radioTemp: TemplateRef<any>; // 拓扑单选框
  public _iconSize;  // 图表大小
  public mapService: MapAbstract; // 地图方法
  public infoData = {  // 信息框
    type: '',
    data: null
  };
  public infoWindowLeft = '0';  // 信息框左边位置
  public infoWindowTop = '0';   // 信息框上边位置
  public isShowInfoWindow = false; // 模拟鼠标移上去时的提示框
  public facilityIconType = index_facility_type; // 设施类型
  public targetMarker;   // 目标标记点
  public timer;          // 定时器
  public inputValue;     // 搜索框值
  public options: string[] = [];  // 下拉框
  public indexLanguage: IndexLanguageInterface;  // 首页国际化
  public commonLanguage: CommonLanguageInterface;  // 公共国际化
  public searchTypeName;  // 搜索类型名称
  public IndexObj = {      // 地图类型
    facilityNameIndex: 1,
    bMapLocationSearch: -1,
    gMapLocationSearch: -1,
  };
  public fn: any;   // marker点事件
  public cb: any;   // 百度地图聚合点回调
  public maxZoom;   // 最大缩放级别
  public defaultZoom;   // 默认缩放级别
  public searchKey = '';  // 谷歌地图地理位置搜索框
  public mapTypeId;  // 地图类别id
  public mapType;   // 地图类型
  public areaDataMap = new Map();
  public isVisible = false; // 新增弹出框显示隐藏
  public title: string;  // 光缆标题
  public _dataSet = [];  // 数据级
  public pageBean: PageBean = new PageBean(10, 1, 1);  // 分页参数初始化
  public tableConfig: TableConfig; // 表格配置
  public queryCondition: QueryCondition = new QueryCondition(); // 传参条件
  public justId = ''; // 设施ID
  public language: FacilityLanguageInterface; // 国际化
  public InspectionLanguage: InspectionLanguageInterface; // 国际化
  public selectedAlarmId = null; // 选中ID
  public isLight = null;  // 判断是查看拓扑高亮（1） 还是查看拓扑（2）
  public gisData = []; // 显示点连线;
  public lightData = ''; // 高亮数数据
  public gisDataList = []; // gis点数据对象集合
  public typeLg; // 语言类型

  constructor(private $nzI18n: NzI18nService,
              private $mapStoreService: MapStoreService,
              private $el: ElementRef,
              private $router: Router,
              private $facilityService: FacilityService,
              private $modalService: FiLinkModalService,
  ) {
  }

  ngOnInit() {
    // 初始化国际化
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.InspectionLanguage = this.$nzI18n.getLocaleData('inspection');
    this.language = this.$nzI18n.getLocaleData('facility');
    // 语言类型
    this.typeLg = JSON.parse(localStorage.getItem('localLanguage'));
    this.searchTypeName = this.indexLanguage.searchDeviceName;
    this.mapTypeId = 'roadmap';
    this.title = this.indexLanguage.chooseFibre;
    this.initFn();
    this.initCb();
    this.initTableConfig();
  }

  ngAfterViewInit() {
    if (!this.mapConfig) {
      // 请传入地图配置信息
      return;
    }
    if (!this.mapConfig.mapId) {
      // 请传入id
    } else {
      this.map.nativeElement.setAttribute('id', this.mapConfig.mapId);
    }
    this.mapType = this.mapConfig.mapType;
    if (this.mapType === 'google') {
      this.initGMap();
    } else if (this.mapType === 'baidu') {
      this.initBMap();
    } else {
      // 不支持该类型地图
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    // 缩放级别大的时候，拿到缩放层级
    this.maxZoom = BMapConfig.maxZoom;
    this.defaultZoom = BMapConfig.defaultZoom;

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
    if (changes.areaData && changes.areaData.currentValue) {
      this.setAreaDataMap();
    }
    if (this.mapService && changes.facilityId) {
      if (changes.facilityId.currentValue) {
        this.selectMarker();
      } else {
       // 设施详情列表id和设施id切换的时候，选中颜色
       // this.resetTargetMarker();
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
        // 百度地图资源未加载
        return;
      }
      this.mapService = new BMapService(this.mapConfig);
      this.mapService.addLocationSearchControl('suggestId', 'searchResultPanel');
      this.mapService.cityListHook().subscribe(result => {
        this.mapEvent.emit(result);
      });
      this.addEventListenerToMap();
      this.maxZoom = BMapConfig.maxZoom;
      this.defaultZoom = BMapConfig.defaultZoom;
      const size = this.mapConfig.defaultIconSize.split('-');
      this._iconSize = this.mapService.createSize(size[0], size[1]);
      this.addMarkers(this.data);
    } catch (e) {
      // 百度地图资源未加载
    }
  }

  initGMap() {
    this.mapType = 'google';
    try {
      if (!google || !MarkerClusterer) {
        // 谷歌地图资源未加载
        return;
      }
      this.mapService = new GMapService(this.mapConfig);
      this.addEventListenerToMap();
      this.maxZoom = GMapConfig.maxZoom;
      this.defaultZoom = GMapConfig.defaultZoom;
      const size = this.mapConfig.defaultIconSize.split('-');
      this._iconSize = this.mapService.createSize(size[0], size[1]);
      this.addMarkers(this.data);
    } catch (e) {
      // 谷歌地图资源未加载
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
  public addMarkers(facilityData) {
    const arr = [];
    if (facilityData.length === 0 && !this.facilityId) {
      this.mapService.locateToUserCity(true);
    }
    facilityData.forEach(item => {
      arr.push(this.mapService.createMarker(item, this.fn));
    });
    this.mapService.addMarkerClusterer(arr, this.cb);
    if (!this.facilityId) {
      this.mapService.setCenterPoint(this.defaultZoom);
    } else {
      this.selectMarker();
    }
    this.mapEvent.emit({type: 'isInit'});
  }

  /**
   * 更新marker点
   * param {any[]} facilityData
   */
  updateMarkers(facilityData: any[] = []) {
    const arr = facilityData.map(item => item.deviceId) || [];
    const that = this;
    // 如果总数大于10万，解决卡顿问题之setTimeout
    if (facilityData.length > 100000) {
      setTimeout(function () {
        facilityData.forEach(item => {
          if (!that.mapService.getMarkerDataById(item.deviceId)) {
            that.mapService.updateMarker('add', item, that.fn);
          } else {
            that.mapService.updateMarker('update', item);
          }
        });
      }, 0);
    } else {
      facilityData.forEach(item => {
        if (!that.mapService.getMarkerDataById(item.deviceId)) {
          that.mapService.updateMarker('add', item, that.fn);
        } else {
          that.mapService.updateMarker('update', item);
        }
      });
    }

    this.mapService.hideOther(arr);
    this.mapService.redraw();
  }

  /**
   * marker点点击事件
   * param event
   */
  markerClickEvent(event) {
    this.resetTargetMarker();
    const id = event.target.customData.id;
    const data = this.mapService.getMarkerDataById(id);
    const imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, data.deviceType);
    const _icon = this.mapService.toggleIcon(imgUrl);
    event.target.setIcon(_icon);
    this.targetMarker = this.mapService.getMarkerById(id);
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
    const areaLevel = this.getAreaLevel(info.areaId);

    this.infoData = {
      type: 'm',
      data: {
        deviceName: info.deviceName,
        deviceStatusName: this.getFacilityStatusName(info.deviceStatus),
        deviceStatusColor: this.getDeviceStatusColor(info.deviceStatus),
        areaLevelName: this.getAreaLevelName(areaLevel),
        areaLevelColor: this.getAreaLevelColor(areaLevel),
        areaName: info.areaName,
        address: info.address,
        className: CommonUtil.getFacilityIconClassName(info.deviceType)
      }
    };

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
   * param type   类型  c：聚合点 m：marker点
   */
  showInfoWindow(type, lng, lat) {
    const pixel = this.mapService.pointToOverlayPixel(lng, lat);
    let offset = this.mapService.getOffset();
    let _top = offset.offsetY + pixel.y;
    if (type === 'c') {
      _top = _top - 20;
    } else if (type === 'm') {
      const iconHeight = parseInt(this._iconSize.height, 10);
      _top = _top - iconHeight + 16;
      if (this.mapType === 'google') {
        _top = _top - 10;
      }
    } else if (type === 'm') {
      _top = _top + 100;
      offset = offset - 150;
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
      // 聚合点
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
        // 标记点
        if (type === 'zoomend') {
          this.hideInfoWindow();
        } else if (type === 'dragend') {
          this.resetTargetMarker();
          this.hideInfoWindow();
          this.mapEvent.emit({type: 'mapDrag'});
          this.closeOverlayInfoWindow();
        } else if (type === 'click') {
          this.resetTargetMarker();
          this.mapEvent.emit({type: 'mapBlackClick'});
        }
      }
    });
  }

  /**
   * 设施选中
   */
  selectMarker() {
    // 重置之前的样式
    this.resetTargetMarker();
    // 拿到标记点
    let marker = this.mapService.getMarkerById(this.facilityId);
    let imgUrl;
    if (!marker) {
      // 该设施无法定位
      // 新增数据和地图上没有缓存的数据
      for (const [key, value] of this.$mapStoreService.mapData) {
        if (key === this.facilityId) {
          marker = value.info;
          this.mapService.createMarker(marker, this.fn);
          this.selectMarker();
        }
      }
      this.mapService.setCenterAndZoom(marker.lng, marker.lat, this.maxZoom);
      // this.$modalService.warning(this.indexLanguage.noDeviceSetting);
    } else {
      // 地图缓存的数据
      const data = this.mapService.getMarkerDataById(this.facilityId);
      this.mapService.setCenterAndZoom(data.lng, data.lat, this.maxZoom);
      imgUrl = CommonUtil.getFacilityIconUrl(this.iconSize, data.deviceType);
      const _icon = this.mapService.getIcon(imgUrl, this._iconSize);
      marker.setIcon(_icon);
      this.targetMarker = marker;
    }

  }

  /**
   * 获取地图中心点
   */
  centerAndZoom(data) {
    this.mapService.setCenterAndZoom(data.lng, data.lat, this.maxZoom);
  }

  /**
   * 地图缩小
   * 数字越小，级别越高
   */
  zoomOut() {
    this.mapService.zoomIn();

  }

  /**
   * 地图放大
   * 数字越大，级别越小
   */
  zoomIn() {
    this.mapService.zoomOut();
  }

  /**
   * 监听input
   */
  onInput(value: string): void {
    const _value = value.trim();
    this.options = this.data.filter(item => {
      return item.deviceName.includes(_value);
    });
  }

  /**
   * 键盘事件
   */
  keyDownEvent() {
    this.locationById(this.inputValue);
  }

  /**
   * 清除搜索事件
   */
  clearSearch() {
    this.inputValue = '';
    this.options = [];
  }


  /**
   * 下拉框事件
   */
  optionChange(event, id) {
    this.locationById(id);
  }

  /**
   * 定位通过id
   */
  locationById(id) {
    const data = this.mapService.getMarkerDataById(id);
    if (!data) {
      return;
    }
    this.facilityId = id;
    this.selectMarker();
    this.mapEvent.emit({type: 'selected', id});
  }

  /**
   * 设施名称搜索
   */
  searchFacilityName() {
    this.searchTypeName = this.indexLanguage.searchDeviceName;
    this.IndexObj = {
      facilityNameIndex: 1,
      bMapLocationSearch: -1,
      gMapLocationSearch: -1,
    };
  }

  /**
   * 地址搜索
   */
  searchAddress() {
    this.searchTypeName = this.indexLanguage.searchAddress;
    if (this.mapType === 'baidu') {
      this.IndexObj = {
        facilityNameIndex: -1,
        bMapLocationSearch: 1,
        gMapLocationSearch: -1,
      };
    } else if (this.mapType === 'google') {
      this.IndexObj = {
        facilityNameIndex: -1,
        bMapLocationSearch: -1,
        gMapLocationSearch: 1,
      };
    } else {
    }
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

  /**
   * 设置地图类型
   * terrain  roadmap  hybrid  satellite
   */
  setMapType(type) {
    this.mapTypeId = type;
    this.mapService.setMapTypeId(type);
  }

  /**
   * 获取设施状态名字
   */
  getFacilityStatusName(status) {
    return this.indexLanguage[FACILITY_STATUS_NAME[status]] || '';
  }

  /**
   * 获取区域级别名字
   */
  getAreaLevelName(level) {
    const levelName = this.commonLanguage[AREA_LEVEL_NAME[level]] || '';
    if (this.typeLg === 'US') {
      return `${this.commonLanguage.level}${levelName}`;
    } else {
      return `${levelName}${this.commonLanguage.level}`;
    }
  }

  /**
   * 通过id判断告警级别
   * param id
   * returns {string}
   */
  getAreaLevel(id) {
    const level = this.areaDataMap.get(id).areaLevel || '';
    return level.toString();
  }

  /**
   * 获取区域级别颜色
   */
  getAreaLevelColor(level) {
    return AREA_LEVEL_COLOR[level];
  }

  /**
   * 获取设施状态颜色
   */
  getDeviceStatusColor(status = '0') {
    return FACILITY_STATUS_COLOR[status];
  }

  /**
   * 设置地图区域数据
   */
  setAreaDataMap() {
    this.areaData.forEach(item => {
      this.areaDataMap.set(item.areaId, item);
    });
  }

  // 地图上关闭右键
  closeRightClick() {
    this.infoData.type = null;
  }

  /**
   * 设置地图事件
   */
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
      },
      {
        eventName: 'rightclick',
        eventHandler: (event) => {
          this.justId = event.target.customData.id;
          const info = this.mapService.getMarkerDataById(this.justId);
          // 光交箱 001，配线架 060，接头盒 090
          const e = this.facilityIconType;
          if (info.deviceType === e.patchPanel || info.deviceType === e.opticalBox || info.deviceType === e.jointClosure) {
            this.infoData = {
              type: 'r',
              data: null
            };
            this.showInfoWindow('r', event.point.lng, event.point.lat);
          }
        }
      }
    ];
  }

  /**
   * 设置地图聚合点
   */
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

  /**
   * 高亮
   */
  showLight() {
    this.isVisible = true;
    this.justRefreshData();
    this.isLight = 1;

  }

  /**
   * 查看拓扑
   */
  check() {
    this.isVisible = true;
    this.justRefreshData();
    this.isLight = 2;
  }

  /**
   * 关闭表格
   */
  modalCancel() {
    this.isVisible = false;
  }

  /**
   *选择光缆后点击确定
   */
  showTopology() {
    if (this.selectedAlarmId === null) {
      this.$modalService.warning(this.indexLanguage.chooseFibre);
    } else {
      if (this.isLight === 2) {
        this.isVisible = false;
        this.navigateToDetail(`business/topology`, {queryParams: {opticCableId: this.selectedAlarmId}});
      } else {
        this.isVisible = false;
        this.mapEvent.emit({type: 'showLight', data: this.lightData});
        this.showLightData(this.selectedAlarmId);

      }
    }
  }

  /**
   *点聚合列表点击跳转查看拓扑
   */
  isShowTopolgy(id) {
    this.isVisible = true;
    this.justId = id;
    this.justRefreshData();
    this.isLight = 2;
  }

  /**
   *点聚合列表点击跳转查看高亮
   */
  isShowLight(id) {
    this.isVisible = true;
    this.justId = id;
    this.justRefreshData();
    this.isLight = 1;
    this.infoData.type = null;
  }

  /**
   * 传参显示高亮
   */
  showLightData(id) {
    this.mapService.clearLine();
    this.gisDataList = [];
    const points = [];
    this.$facilityService.cableSectionId(id).subscribe((result: Result) => {
      if (result.data.length > 0) {
        result.data.forEach((itemOne) => { // 小数组
          this.gisData = itemOne;
          this.gisData.map(item => {
            item.lng = item.position.split(',')[0];
            item.lat = item.position.split(',')[1];
            points.push({'lng': item.lng, 'lat': item.lat});
          });
          this.mapService.newAddLine(this.gisData);
          if (this.mapType === 'google') {
            // google地图计算最佳中心点以及缩放级别
            this.mapService.getViewport(points);
          } else {
            // 根据后台返回的数据计算出最佳的中心点和缩放级别
            const view = this.mapService.getViewport(points);
            // baidu地图计算最佳中心点以及缩放级别
            // 最佳缩放级别
            const mapZoom = view.zoom;
            // 最佳中心点
            const centerPoint = view.center;
            this.mapService.setCenterAndZoom(centerPoint.lng, centerPoint.lat, mapZoom);
          }
          this.infoData.type = null;
        });
      } else {
          this.$modalService.warning(this.indexLanguage.noData);
      }
    });
  }

  // 退出高亮模式
  clearLight() {
    this.mapService.clearLine();

  }

  /**
   * 跳转到详情
   * param url
   */
  navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }


  // 光缆配置
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      showPagination: true,
      isLoading: false,
      showSearchSwitch: false,
      showSizeChanger: true,
      showSearchExport: false,
      noIndex: true,
      scroll: {x: '1000px', y: '600px'},
      columnConfig: [
        {
          title: '',
          type: 'render',
          key: 'selectedAlarmId',
          renderTemplate: this.radioTemp,
          width: 42
        },
        {
          title: this.language.cableName, key: 'opticCableName', width: 124,
        },
        {
          title: this.language.cableLevel, key: 'opticCableLevel', width: 80,
        },
        {
          title: this.language.localNetworkCode, key: 'localCode', width: 124,
        },
        {
          title: this.language.cableTopology, key: 'topology', width: 104,
        },
        {
          title: this.language.wiringType, key: 'wiringType', width: 80,
        },
        {
          title: this.language.cableCore, key: 'coreNum', width: 124,
        },
        {
          title: this.language.length, key: 'length', width: 70,
        },
        {
          title: this.language.businessInformation, key: 'bizId', width: 124,
        },
        {
          title: this.language.remarks, key: 'remark', width: 124,
        },

      ]
    };
  }

  /**
   *获取光缆信息列表
   */
  justRefreshData() {
    this.tableConfig.isLoading = true;
    this.queryCondition.pageCondition.pageSize = 10;
    this.queryCondition.bizCondition.deviceId = this.justId;
    this.$facilityService.getCableList(this.queryCondition).subscribe((result: Result) => {
      this.pageBean.Total = result.totalCount;
      this.tableConfig.isLoading = false;
      const data = result.data;
      data.forEach(item => {
        item.opticCableLevel = getCableLevel(this.$nzI18n, item.opticCableLevel);
      });
      this._dataSet = result.data;
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   *光缆信息列表分页
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.justRefreshData();
  }


  /**
   * 选择告警
   * param event
   * param data
   */
  selectedAlarmChange(event, data) {
    this.lightData = data;
  }
}
