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
import {TableComponent} from '../table/table.component';
import {PageBean} from '../../entity/pageBean';
import {MapSelectorConfig} from '../../entity/mapSelectorConfig';
import {MapDrawingService} from './map-drawing.service';
import {MapSelectorService} from './map-selector.service';
import {BMAP_ARROW, BMAP_DRAWING_RECTANGLE, iconSize} from './map.config';
import {MapService} from '../../../core-module/api-service/index/map';
import {Result} from '../../entity/result';
import {getDeviceType} from '../../../business-module/facility/facility.config';
import {NzI18nService} from 'ng-zorro-antd';
import {GMapSelectorService} from './g-map-selector.service';
import {CommonUtil} from '../../util/common-util';
import {FiLinkModalService} from '../../service/filink-modal/filink-modal.service';
import {TableConfig} from '../../entity/tableConfig';
import {AreaService} from '../../../core-module/api-service/facility';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {InspectionService} from '../../../core-module/api-service/work-order/inspection';

declare const BMap: any;
declare const BMapLib: any;
declare const BMAP_ANCHOR_TOP_LEFT: any;
declare const BMAP_ANCHOR_TOP_RIGHT: any;
declare const MAP_TYPE;

@Component({
  selector: 'xc-map-selector',
  templateUrl: './map-selector.component.html',
  styleUrls: ['./map-selector.component.scss']
})
export class MapSelectorComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy {
  @Input()
  mapSelectorConfig: MapSelectorConfig;
  @Output() xcVisibleChange = new EventEmitter<boolean>();
  @Output() selectDataChange = new EventEmitter<any[]>();
  @ViewChild(TableComponent) childCmp: TableComponent;
  @ViewChild('handleTemp') handleTemp: TemplateRef<any>;
  @ViewChild('mapSelectorDom') mapSelectorDom: ElementRef<any>;
  selectPageBean: PageBean = new PageBean(10, 1, 0);
  @Input()
  areaId;
  selectorConfig: TableConfig;
  selectData = [];
  selectPageData = [];
  mapInstance;
  mapDrawUtil: MapDrawingService;
  mapService: MapSelectorService | GMapSelectorService;
  overlays = [];
  mapData = [];
  treeNodeSum;
  facilityData = [];
  fristData = [];
  public drawType: string = BMAP_ARROW;
  public mapType: string = 'baidu';
  isLoading = false;
  searchKey;
  public areaNotHasDevice: boolean;
  isShowInfoWindow: boolean = false;
  infoWindowLeft;
  infoWindowTop;
  infoData = {type: '', data: {}};
  language: CommonLanguageInterface;
  isShowProgressBar;
  percent;
  public increasePercent: number;
  public timer: any;
  constructor(public $mapService: MapService,
              public $areaService: AreaService,
              public $modalService: FiLinkModalService,
              public $i18n: NzI18nService) {
  }

  _xcVisible = false;

  get xcVisible() {
    return this._xcVisible;
  }

  @Input()
  set xcVisible(params) {
    this._xcVisible = params;
    this.xcVisibleChange.emit(this._xcVisible);
  }

  ngOnInit() {
    this.mapType = MAP_TYPE;
    this.language = this.$i18n.getLocaleData('common');
    this.initSelectorConfig();
  }

  ngAfterViewInit(): void {
    this.initMap();
    this.getALLFacilityList();
    // 测试数据
    // this.mapService.mockData();
    // this.mapService.setCenterPoint();
  }

  ngOnChanges(changes: SimpleChanges): void {
  }

  chooseUtil(event) {
    this.drawType = event;
    if (event === BMAP_DRAWING_RECTANGLE) {
      this.mapDrawUtil.open();
      this.mapDrawUtil.setDrawingMode(BMAP_DRAWING_RECTANGLE);
    } else if (event === BMAP_ARROW) {
      this.mapDrawUtil.setDrawingMode(null);
      this.mapDrawUtil.close();
    }

  }

  handleCancel() {
    this.xcVisible = false;
  }

  handleOk() {
    this.setAreaDevice();
  }

  /**
   * 佐罗弹框加载地图方式
   */
  afterModelOpen() {
    if (!this.mapInstance) {
      this.initMap();
    }
  }

  /**
   * 清空数据
   */
  restSelectData() {
    this.selectData.forEach(item => {
      const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, item.deviceType, '1');
      const icon = this.mapService.toggleIcon(imgUrl);
      this.mapService.getMarkerById(item.deviceId).setIcon(icon);
    });
    this.fristData.forEach(item => {
      const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, item.deviceType, '0');
      const icon = this.mapService.toggleIcon(imgUrl);
      this.mapService.getMarkerById(item.deviceId).setIcon(icon);
    });
    this.selectData = this.fristData;
    this.refreshSelectPageData();

  }

  /**
   * 刷新数据
   */
  refreshSelectPageData() {
    this.selectPageBean.Total = this.selectData.length;
    this.selectPageData = this.selectData.slice(this.selectPageBean.pageSize * (this.selectPageBean.pageIndex - 1),
      this.selectPageBean.pageIndex * this.selectPageBean.pageSize);
  }

  /**
   * 左边表格数据变化
   * param event
   */
  selectPageChange(event) {
    this.selectPageBean.pageIndex = event.pageIndex;
    this.selectPageBean.pageSize = event.pageSize;
    this.selectPageData = this.selectData.slice(this.selectPageBean.pageSize * (this.selectPageBean.pageIndex - 1),
      this.selectPageBean.pageIndex * this.selectPageBean.pageSize);
  }

  location() {
    console.log(this.searchKey);
    const key = this.searchKey.trimLeft().trimRight();
    if (!key) {
      return;
    }
    this.mapService.searchLocation(key, (results, status) => {
      if (status === 'OK') {
        this.mapInstance.setCenter(results[0].geometry.location);
      } else {
        // this.$modalService.error('无结果');
      }
    });
  }

  /**
   * 清除所有的覆盖物
   */
  public clearAll() {
    for (let i = 0; i < this.overlays.length; i++) {
      this.mapService.clearOverlay(this.overlays[i]);
    }
    this.overlays.length = 0;
  }

  /**
   * 从列表中删除
   * param currentItem
   */
  public deleteFormTable(currentItem) {
    const index = this.selectData.findIndex(item => item.deviceId === currentItem.deviceId);
    this.selectData.splice(index, 1);
    this.childCmp.checkStatus();
  }

  /**
   * 添加到列表
   * param item
   */
  public pushToTable(item) {
    const index = this.selectData.findIndex(_item => item.deviceId === _item.deviceId);
    if (index === -1) {
      item.checked = true;
      if (item.areaId && item.areaId !== this.areaId) {
        // item.remarks = `当前选择属于${item.areaName}区`;
        item.rowActive = true;
      }
      this.selectData = this.selectData.concat([item]);
    } else {
    }
  }

  /**
   * 去选
   * param currentItem
   */
  handleDelete(currentItem) {
    if (currentItem) {
      if (this.checkFacilityCanDelete(currentItem)) {
        return;
      }
      // 找到要删除的项目
      const index = this.selectData.findIndex(item => item.deviceId === currentItem.deviceId);
      this.selectData.splice(index, 1);
      this.childCmp.checkStatus();
      // 删除完刷新被选数据
      this.refreshSelectPageData();
      const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, currentItem.deviceType, '1');
      const icon = this.mapService.toggleIcon(imgUrl);
      this.mapService.getMarkerById(currentItem.deviceId).setIcon(icon);
    }
  }

  initSelectorConfig() {
    this.selectorConfig = {
      noIndex: true,
      isDraggable: false,
      isLoading: false,
      showSearchSwitch: false,
      searchTemplate: null,
      scroll: {x: '420px'},
      columnConfig: [
        {type: 'render', renderTemplate: this.handleTemp, width: 30},
        {type: 'serial-number', width: 62, title: this.language.serialNumber},
      ].concat(this.mapSelectorConfig.selectedColumn),
      showPagination: true,
      bordered: false,
      showSearch: false,
      showSizeChanger: false,
      simplePage: true,
      hideOnSinglePage: true,
      handleSelect: (data, currentItem) => {
        // 加入被选容器
        console.log(data);
        console.log(currentItem);
        if (currentItem) {
          if (this.checkFacilityCanDelete(currentItem)) {
            return;
          }
          // 找到要删除的项目
          const index = this.selectData.findIndex(item => item.deviceId === currentItem.deviceId);
          this.selectData.splice(index, 1);
          this.childCmp.checkStatus();
          // 删除完刷新被选数据
          this.refreshSelectPageData();
          const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, currentItem.deviceType, '1');
          const icon = this.mapService.toggleIcon(imgUrl);
          this.mapService.getMarkerById(currentItem.deviceId).setIcon(icon);
        }
        if (data && data.length === 0) {
          this.restSelectData();
        }
      },
    };
  }

  /**
   * 获取框选的点
   */
  private getOverlayPath() {
    const box = this.overlays[this.overlays.length - 1];

    if (box.getPath && this.mapType === 'baidu') {
      const pointArray = box.getPath();
      // this.mapInstance.setViewport(pointArray); // 调整视野
      const bound = this.mapInstance.getBounds(); // 地图可视区域
      this.mapService.getMarkerMap().forEach(value => {
        if (bound.containsPoint(value.marker.point) === true) {
          if (this.mapService.isInsidePolygon(value.marker.point, pointArray)) {
            const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, value.data.deviceType);
            const icon = this.mapService.toggleIcon(imgUrl);
            this.mapService.getMarkerById(value.data.deviceId).setIcon(icon);
            this.pushToTable(value.data);
          }
        }
      });
    } else {
      // 谷歌地图
      const point = box.getBounds();
      this.mapService.getMarkerMap().forEach(value => {
        if (point.contains(value.marker.position)) {
          const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, value.data.deviceType);
          const icon = this.mapService.toggleIcon(imgUrl);
          this.mapService.getMarkerById(value.data.deviceId).setIcon(icon);
          this.pushToTable(value.data);
        }
      });
    }
    this.refreshSelectPageData();
  }

  afterModelClose() {

  }

  /**
   * 初始化地图
   */
  public initMap() {
    // 实例化地图服务类
    if (this.mapType === 'baidu') {
      this.mapService = new MapSelectorService('_mapContainer');
      this.mapService.setCenterPoint();
    } else {
      this.mapService = new GMapSelectorService('_mapContainer');
    }
    // 添加缩放结束事件
    this.mapService.addZoomEnd(() => {
      this.isShowInfoWindow = false;

      // if (this.mapService.mapInstance.getZoom() >= MapConfig.showLineZoom) {
      //   // this.addLine();
      // } else {
      //   this.isShowInfoWindow = false;
      // }
    });
    // 实例化鼠标绘制工具
    this.mapDrawUtil = this.mapService.mapDrawUtil;
    this.mapInstance = this.mapService.mapInstance;
    this.mapDrawUtil.setDrawingMode(null);
    // 添加鼠标绘制工具监听事件，用于获取绘制结果
    this.mapDrawUtil.addEventListener('overlaycomplete', (e) => {
      this.overlays.push(e.overlay);
      this.getOverlayPath();
      this.clearAll();
      this.drawType = BMAP_ARROW;
      this.mapDrawUtil.close();
      this.mapDrawUtil.setDrawingMode(null);
    });
  }

  /**
   * 向地图中添加点
   * param {any[]} facilityData
   */
  public addMarkers(facilityData: any[]) {
    const arr = [];
    facilityData.forEach(item => {
      arr.push(this.mapService.createMarker(item,
        [
          {
            eventName: 'click',
            eventHandler: (event, __event) => {
              console.log(event.target);
              const icon = event.target.getIcon();
              let _icon;
              const data = this.mapService.getMarkerDataById(event.target.customData.id);
              const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, data.deviceType, '1');
              if (icon.imageUrl === imgUrl || icon.url === imgUrl) {
                const _imgUrl = CommonUtil.getFacilityIconUrl(iconSize, data.deviceType);
                _icon = this.mapService.toggleIcon(_imgUrl);
                this.pushToTable(data);
              } else {
                if (this.checkFacilityCanDelete(data)) {
                  return;
                }
                _icon = this.mapService.toggleIcon(imgUrl);
                this.deleteFormTable(data);
              }
              this.refreshSelectPageData();
              event.target.setIcon(_icon);
            }
          },
          // 地图上的设施点悬浮显示信息面板
          {
            eventName: 'mouseover',
            eventHandler: (event, __event) => {
              console.log(__event);
              console.log(this.mapSelectorDom);
              this.isShowInfoWindow = true;
              if (this.mapType === 'baidu') {
                this.infoWindowLeft = (event.pixel.x + 30) + 'px';
                this.infoWindowTop = (event.pixel.y) + 'px';
              } else {
                // google 坐标系在右下角 取当前定位div大小进行坐标换算
                this.infoWindowLeft = ((document.getElementById('_mapContainer').clientWidth) / 2 + 20 + __event.pixel.x) + 'px';
                this.infoWindowTop = ((document.getElementById('_mapContainer').clientHeight) / 2 + __event.pixel.y) + 'px';
              }
              // 从map中拿到设施数据
              const data = this.mapService.getMarkerDataById(event.target.customData.id);
              const deviceTypeName = getDeviceType(this.$i18n, data.deviceType);
              this.infoData = {
                type: 'm',
                data: {
                  name: `${deviceTypeName}-${data.deviceName}`,
                  number: data.deviceCode,
                  address: data.address,
                  className: CommonUtil.getFacilityIconClassName(data.deviceType)
                }
              };
            }
          },
          {
            eventName: 'mouseout',
            eventHandler: () => {
              this.isShowInfoWindow = false;
            }
          }
        ]
      ));
    });
    this.mapService.addMarkerClusterer(arr, [
      {
        eventName: 'onmouseover',
        eventHandler: (event, markers) => {
          console.log(event);
          console.log(markers);
          this.isShowInfoWindow = true;
          if (this.mapType === 'baidu') {
            this.infoWindowLeft = (event.pixel.x + 30) + 'px';
            this.infoWindowTop = (event.pixel.y) + 'px';
          } else {
            // google地图聚合点事件没有提供event位置对象使用原生的event(向上偏移60)
            this.infoWindowLeft = (event.clientX - this.mapSelectorDom.nativeElement.offsetLeft) + 'px';
            this.infoWindowTop = (event.clientY - this.mapSelectorDom.nativeElement.offsetTop - 60) + 'px';
          }
          const obj = {};
          markers.forEach(marker => {
            const info = this.mapService.getMarkerDataById(marker.customData.id);
            if (obj[info.deviceType]) {
              obj[info.deviceType].push(info);
            } else {
              obj[info.deviceType] = [info];
            }
          });
          const arrInfo = [];
          Object.keys(obj).forEach(key => {
            console.log(CommonUtil.getFacilityIconClassName(key));
            arrInfo.push({
              deviceType: key,
              className: CommonUtil.getFacilityIconClassName(key),
              deviceTypeName: getDeviceType(this.$i18n, key),
              count: `（${obj[key].length}）`
            });
          });
          this.infoData = {
            type: 'c',
            data: arrInfo
          };
        }
      },
      {
        eventName: 'onmouseout',
        eventHandler: (event) => {
          console.log(event);
          this.isShowInfoWindow = false;
        }
      },
      {
        eventName: 'onclick',
        eventHandler: (event, markers) => {
          console.log(event);
          console.log(markers);
          markers.forEach(marker => {
            const info = this.mapService.getMarkerDataById(marker.customData.id);
            this.pushToTable(info);
          });
          this.refreshSelectPageData();
        }
      }
    ]);
    this.mapService.setCenterPoint();
  }

  /**
   * 获取所有的设备点
   */
  private getALLFacilityList() {
    // this.$modalService.loading(this.language.loading, 1000 * 60);
    this.showProgressBar();
    this.$mapService.getALLFacilityList().subscribe((result: Result) => {
      // this.$modalService.remove();
      this.hideProgressBar();
      this.facilityData = result.data || [];
      this.treeNodeSum = this.facilityData.length;
      // 默认该区域下没有设施
      this.areaNotHasDevice = true;
      this.facilityData.forEach(item => {
        item._deviceType = getDeviceType(this.$i18n, item.deviceType);
        if (item.areaId === this.areaId) {
          item.checked = true;
          // 该区域下有设施
          this.areaNotHasDevice = false;
          this.fristData.push(item);
          this.pushToTable(item);
        }
      });
      this.refreshSelectPageData();
      this.addMarkers(this.facilityData);
      // 该区域下没有设施定位到用户登陆到位置
      if (this.areaNotHasDevice) {
        this.mapService.locateToUserCity();
      }
    }, () => {
      // this.$modalService.remove();
      this.hideProgressBar();
    });
  }


  /**
   * 检查设备是否能被解除关联
   * param facility
   */
  public checkFacilityCanDelete(facility) {
    if (facility.areaId === this.areaId) {
      this.$modalService.error(this.language.setAreaMsg);
      return true;
    }
  }

  /**
   * 关联设施
   * param body
   */
  private setAreaDevice() {
    this.isLoading = true;
    const list = [];
    // 去除已经属于该区域的设施
    this.selectData.map(item => {
      if (item.areaId !== this.areaId) {
        list.push(item.deviceId);
      }
    });
    const obj = {};
    obj[this.areaId] = list;
    this.$areaService.setAreaDevice(obj).subscribe((result: Result) => {
      this.isLoading = false;
      if (result.code === 0) {
        this.$modalService.success(result.msg);
        this.handleCancel();
      } else {
        this.$modalService.error(result.msg);
      }
    }, () => {
      this.isLoading = false;
    });
  }

  ngOnDestroy(): void {
    this.$modalService.remove();
  }
  /**
   * 显示加载进度条
   */
  showProgressBar() {
    this.percent = 0;
    this.increasePercent = 5;
    this.isShowProgressBar = true;
    this.timer = setInterval(() => {
      if (this.percent >= 100) {
        clearInterval(this.timer);
      } else {
        this.percent += this.increasePercent;
        if (this.percent === 50) {
          this.increasePercent = 2;
        } else if (this.percent === 80) {
          this.increasePercent = 1;
        } else if (this.percent === 99) {
          this.increasePercent = 0;
        }
      }
    }, 500);
  }

  /**
   * 隐藏加载进度条
   */
  hideProgressBar() {
    this.percent = 100;
    setTimeout(() => {
      this.isShowProgressBar = false;
    }, 1000);
  }

}
