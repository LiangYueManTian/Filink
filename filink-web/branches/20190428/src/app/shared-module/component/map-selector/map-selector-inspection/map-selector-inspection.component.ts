import {AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MapSelectorComponent} from '../map-selector.component';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {AreaService} from '../../../../core-module/api-service/facility';
import {NzI18nService} from 'ng-zorro-antd';
import {MapService} from '../../../../core-module/api-service/index/map';
import {Result} from '../../../entity/result';
import {getDeviceType} from '../../../../business-module/facility/facility.config';
import {CommonUtil} from '../../../util/common-util';
import {iconSize} from '../map.config';

declare const MAP_TYPE;

@Component({
  selector: 'xc-map-selector-inspection',
  templateUrl: './map-selector-inspection.component.html',
  styleUrls: ['../map-selector.component.scss']
})
export class MapSelectorInspectionComponent extends MapSelectorComponent implements OnInit, OnChanges, AfterViewInit {
  // 传入的设施集合
  @Input()
  deviceSet: any[];
  // 选择器类型 inspection 巡检 setDevice 关联设施（默认不传）
  @Input()
  selectorType: string;
  // 是否关联全集
  @Input()
  isSelectAll;
  @Input()
  noEdit: boolean;
  @Input()
  areaId;
  @Input()
  deviceType: string;
  @Input() isHiddenButton = false;
  @Input() switchHiddenButton = true;
  @Input() selectHiddenButton = false;
  @Input() mapBoxSelect = false;
  private markerClusterer: any;
  private markersArr: any[] = [];


  constructor(public $mapService: MapService,
              public $areaService: AreaService,
              public $modalService: FiLinkModalService,
              public $i18n: NzI18nService) {
    super($mapService, $areaService, $modalService, $i18n);
  }

  ngOnChanges(changes: SimpleChanges): void {
    // if (this.selectorType === 'inspection' && this.deviceType || this.selectorType === 'inspectionTask') {
    //   this.getFacilityFilterByAreaId(this.areaId, this.deviceType);
    // }
    // if (this.selectorType === 'inspection' && changes.areaId && changes.areaId.currentValue) {
    //   this.getFacilityFilterByAreaId(changes.areaId.currentValue, this.deviceType);
    // }
    if (this.xcVisible) {
      this.getFacilityFilterByAreaId(this.areaId, this.deviceType);
    }
    if ((this.selectorType === 'inspection' || this.selectorType === 'inspectionTask') && this.facilityData.length > 0) {
      this.restSelectData();
    }
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  handleOk() {
    if (this.selectorType === 'inspection' || this.selectorType === 'inspectionTask') {
      this.selectDataChange.emit(this.selectData);
      this.handleCancel();
    } else {
      // 改为不传数据 组件内部关联设施 (关联设施时使用)
    }
  }

  ngOnInit() {
    this.mapType = MAP_TYPE;
    this.language = this.$i18n.getLocaleData('common');
    this.initSelectorConfig();
  }

  /**
   * 去选
   * param currentItem
   */
  handleDelete(currentItem) {
    if (currentItem) {
      // if (this.checkFacilityCanDelete(currentItem)) {
      //   return;
      // }
      if (this.isSelectAll === '1' || this.noEdit) {
        return false;
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

  public getFacilityFilterByAreaId(areaId, deviceType?) {
    if (areaId) {
      this.showProgressBar();
      // this.$modalService.loading('加载中', 1000 * 60);
      this.$mapService.getALLFacilityList().subscribe((result: Result) => {
        this.hideProgressBar();
        this.$modalService.remove();
        // 每次切换数据先把原来的数据清空
        this.selectData = [];
        this.clearAll();
        const arrTemp = result.data || [];
        this.facilityData = arrTemp.filter(item => {
            // 当传入 areaId 和deviceType 双重过滤
            if (areaId && deviceType && item.areaId === areaId && item.deviceType === deviceType) {
              return item;
            } else if (areaId && !deviceType && item.areaId === areaId) {
              return item;
            }
          }
        );
        this.treeNodeSum = this.facilityData.length;
        // 默认该区域下没有设施
        this.areaNotHasDevice = true;
        this.facilityData.forEach(item => {
          item._deviceType = getDeviceType(this.$i18n, item.deviceType);
          // 选中全集
          this.areaNotHasDevice = false;
          if (this.isSelectAll === '0') {
            item.checked = true;
            this.fristData.push(item);
            this.pushToTable(item);
          } else {
            // 找出属于传入设施集合的数据加入右边表格
            if (this.deviceSet.includes(item.deviceId)) {
              item.checked = true;
              this.fristData.push(item);
              this.pushToTable(item);
            }
          }
        });
        this.refreshSelectPageData();
        // 先清除上一次的点
        if (this.mapType === 'baidu' && this.markerClusterer) {
          this.markerClusterer.clearMarkers(this.markersArr);
        }
        this.addMarkers(this.facilityData);
        // 该区域下没有设施定位到用户登陆到位置
        if (this.areaNotHasDevice) {
          this.mapService.locateToUserCity();
        }
      }, () => {
        this.hideProgressBar();
        this.$modalService.remove();
      });
    }
  }

  /**
   * 向地图中添加点
   * param {any[]} facilityData
   */
  public addMarkers(facilityData: any[]) {
    this.markersArr = [];
    facilityData.forEach(item => {
      this.markersArr.push(this.mapService.createMarker(item,
        [
          {
            eventName: 'click',
            eventHandler: (event, __event) => {
              console.log(event.target);
              if (this.isSelectAll === '1' || this.noEdit) {
                return false;
              }
              const icon = event.target.getIcon();
              let _icon;
              const data = this.mapService.getMarkerDataById(event.target.customData.id);
              const imgUrl = CommonUtil.getFacilityIconUrl(iconSize, data.deviceType, '1');
              if (icon.imageUrl === imgUrl || icon.url === imgUrl) {
                const _imgUrl = CommonUtil.getFacilityIconUrl(iconSize, data.deviceType);
                _icon = this.mapService.toggleIcon(_imgUrl);
                this.pushToTable(data);
              } else {
                // if (this.checkFacilityCanDelete(data)) {
                //   return;
                // }
                // _icon = this.mapService.toggleIcon(imgUrl);
                // this.deleteFormTable(data);
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
                this.infoWindowLeft = ((400) / 2 + __event.pixel.x) + 'px';
                this.infoWindowTop = ((359) / 2 + __event.pixel.y) + 'px';
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
    this.markerClusterer = this.mapService.addMarkerClusterer(this.markersArr, [
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
          this.clearAll(); // 清除地图所有覆盖值
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

}
