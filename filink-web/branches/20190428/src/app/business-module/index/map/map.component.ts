import {Component, OnInit, TemplateRef, ViewChild, AfterViewInit, OnDestroy} from '@angular/core';
import {MapControl} from './map-control';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {MapService} from '../../../core-module/api-service/index/map';
import {ActivatedRoute, Router} from '@angular/router';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {Result} from '../../../shared-module/entity/result';
import {IndexedDBService} from '../../../core-module/store/IndexedDB.service';
import {MyCollectionComponent} from '../my-collection/my-collection.component';
import {FacilityListComponent} from '../index-facility-list/facility-list.component';
import {FacilityTypeComponent} from '../facility-type/facility-type.component';
import {IndexMissionService} from '../../../core-module/mission/index.mission.service';
import {FacilityDetailPanelComponent} from '../facility-detail-panel/facility-detail-panel.component';
import {SessionUtil} from '../../../shared-module/util/session-util';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {BMapStyleConfig} from './b-map.style.config';
import {MapConfig} from '../../../shared-module/component/map/map.config';
import {LogicAreaComponent} from '../logic-area/logic-area.component';
import {MAP_ICON_CONFIG} from '../../../shared-module/const/facility';

declare const MAP_TYPE;

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent extends MapControl implements OnInit, AfterViewInit, OnDestroy {
  facilityId;   // 选中的设施id
  mapFacilityId;   // 路由上的id
  mapType;    // 地图类型
  mapConfig: MapConfig;   // 地图配置
  facilityTypeConfig;     // 设施类型配置
  facilityIconSizeValue;   // 地图设施图标大小
  facilityIconSizeConfig;  // 地图设施图标大小配置
  iconSize;      // 设施图标大小
  data;    // 设施数据
  selectedFacilityTypeIdsArr;    // 选中的设施类型id集
  selectedFacilityStatusArr;     // 选中的设施状态id集
  selectedLogicAreaIdsArr;       // 选中的逻辑区域id集
  selectedIndex = 0;           // 设施详情tab页选中的index
  clustererFacilityList = [];     // 聚合点设施list
  tables;
  isShowFacilityDetailTab = true;   // 是否显示设施详情tab
  isShowFacilityAlarmTab = false;   // 是否显示告警tab
  isShowFacilityLogAndOrderTab = false;   // 是否显示日志工单tab
  facilityNoticeArr = ['focusDevice', 'unFollowDevice'];    // 设施收藏相关推送
  isShowProgressBar = false;   // 是否显示加载进度条
  percent;     // 进度条初始进度
  increasePercent; // 进度条增长百分比
  timer;
  indexMyCollectionCacheData = null;   // 首页设施收藏数据缓存
  @ViewChild('facilityTypeConfigTemp') facilityTypeConfigTemp: TemplateRef<any>;
  @ViewChild('MapConfigTemp') MapConfigTemp: TemplateRef<any>;
  @ViewChild('facilityListComponent') facilityListComponent: FacilityListComponent;
  @ViewChild('myCollectionComponent') myCollectionComponent: MyCollectionComponent;
  @ViewChild('facilityTypeComponent') facilityTypeComponent: FacilityTypeComponent;
  @ViewChild('logicAreaComponent') logicAreaComponent: LogicAreaComponent;
  @ViewChild('facilityDetailPanelComponent') facilityDetailPanelComponent: FacilityDetailPanelComponent;

  constructor(private $mapStoreService: MapStoreService,
              public $nzI18n: NzI18nService,
              private $mapService: MapService,
              private $message: FiLinkModalService,
              private $router: Router,
              private $modal: NzModalService,
              private $activatedRoute: ActivatedRoute,
              private $indexedDBService: IndexedDBService,
              private $indexMissionService: IndexMissionService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.facilityNoticeArr = ['focusDevice', 'unFollowDevice'];
    this.mapType = MAP_TYPE;
    // const style = this.mapType === 'baidu' ? BMapStyleConfig : null;      // v1版风格样式不影响设施图标不需要去除商家信息
    this.mapConfig = new MapConfig('index-map', this.mapType, MAP_ICON_CONFIG.defaultIconSize, []);
    this.$mapStoreService.mapType = this.mapType;
    this.mapFacilityId = this.$activatedRoute.snapshot.queryParams.id;
    this.facilityId = this.mapFacilityId;
    this.facilityIconSizeConfig = MAP_ICON_CONFIG.iconConfig;
    this.iconSize = MAP_ICON_CONFIG.defaultIconSize;
    this.facilityIconSizeValue = MAP_ICON_CONFIG.defaultIconSize;
    this.facilityTypeConfig = this.facilityTypeListArr;
    this.checkUser();
    this.getAllAreaList();
    // this.tables =  [
    //   {
    //     name: 'facility',
    //     options: {
    //       autoIncrement: true,
    //       keyPath: 'id'
    //     },
    //     indexs: [
    //       {
    //         name: 'deviceId',
    //         prop: 'deviceId',
    //         option: { unique: true}
    //       },
    //       {
    //         name: 'address',
    //         prop: 'address',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'deviceName',
    //         prop: 'deviceName',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'areaId',
    //         prop: 'areaId',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'areaName',
    //         prop: 'areaName',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'deviceCode',
    //         prop: 'deviceCode',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'deviceStatus',
    //         prop: 'deviceStatus',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'deviceType',
    //         prop: 'deviceType',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'lng',
    //         prop: 'lng',
    //         option: {unique: false}
    //       },
    //       {
    //         name: 'lat',
    //         prop: 'lat',
    //         option: {unique: false}
    //       },
    //     ]
    //   }
    // ];
    // this.$indexedDBService.init('index_db', 1);
    // this.$indexedDBService.open(this.tables[0]);
    // 推送监听，实现实时刷新
    this.$indexMissionService.facilityChangeHook.subscribe(data => {
      console.log('data--->' + data);
      const {type, items} = data;
      if (type === 'unlock') {
        if (this.isShowFacilityPanel) {
          this.facilityDetailPanelComponent.getLockInfo(this.facilityId);
        }
      } else if (this.facilityNoticeArr.indexOf(type) > -1) {   // 设施收藏更新
        if (this.isExpandMyCollection) {
          this.myCollectionComponent.update(type, items);
        }
        if (this.isShowFacilityPanel && items.deviceId === this.facilityId) {
          this.facilityDetailPanelComponent.updateCollectionStatus(type);
        }
      } else {     // 设施更新
        this.updateMarkers();
        if (type === 'addDevice') {
        } else if (type === 'updateDevice') {
          if (items.indexOf(this.facilityId) > -1) {
            this.facilityDetailPanelComponent.getFacilityDetail(this.facilityId);
          }
        } else if (type === 'deleteDevice') {
          if (items.indexOf(this.facilityId) > -1) {
            this.hideFacilityPanel();
          }
        } else {
        }
        if (this.isExpandFacilityList) {
          this.facilityListComponent.update(type, items);
        }
      }
    });
  }

  ngAfterViewInit() {
    this.getAllMapConfig();
    this.getMyCollectionData();
  }

  ngOnDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }

  /**
   * 检查用户是否改变
   */
  checkUser() {
    if (SessionUtil.getToken() !== this.$mapStoreService.token) { // 用户改变，清空缓存
      this.$mapStoreService.resetData();
      this.$mapStoreService.resetConfig();
    }
    this.$mapStoreService.token = SessionUtil.getToken();
  }

  /**
   * 刷新
   */
  refresh() {
    if (this.isShowProgressBar) {
      this.$message.warning(this.indexLanguage.loadingMsg);
      return;
    }
    this.$mapStoreService.resetData();
    this.refreshALLFacilityList();
    this.hideFacilityPanel();
    this.hideClustererFacilityTable();
  }

  /**
   * 获取区域列表
   */
  getAllAreaList() {
    const logicAreaListMap = new Map();
    this.$mapStoreService.logicAreaList.forEach(item => {
      logicAreaListMap.set(item.areaId, item);
    });
    this.$mapService.getALLAreaListForCurrentUser().subscribe((result: Result) => {
      console.log(result);
      if (result.code === 0) {
        this.$mapStoreService.logicAreaList = result.data.map(item => {
          if (logicAreaListMap.get(item.areaId)) {
            item.checked = logicAreaListMap.get(item.areaId).checked;
          } else {
            item.checked = true;
          }
          item.hasPermissions = item.hasPermissions;
          return item;
        });
        this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.hasPermissions && item.checked)
          .map(item => item.areaId);
        this.$mapStoreService.isInitLogicAreaData = true;
        if (this.isExpandLogicArea) {
          this.logicAreaComponent.getAllAreaListFromStore();
        }
      } else {
        this.$message.error(result.msg);
      }
    }, err => {
    });
  }

  /**
   * 获取我的关注数据
   */
  getMyCollectionData() {
    this.$mapService.getAllCollectFacilityList().subscribe((result: Result) => {
      console.log(result);
      if (result.code === 0) {
        this.indexMyCollectionCacheData = CommonUtil.deepClone(result.data);
      } else {
      }
    }, () => {
    });
  }

  /**
   * 刷新设施数据
   */
  refreshALLFacilityList() {
    this.showProgressBar();
    this.getAllAreaList();
    this.$mapService.getALLFacilityList().subscribe((result: Result) => {
      // console.log(result);
      this.$message.remove();
      if (result.code === 0) {
        this.$mapStoreService.resetData();
        this.$mapStoreService.isInitData = true;
        const markers = [];
        result.data.forEach(item => {
          const position = item.positionBase.split(',');
          item.lng = parseFloat(position[0]);
          item.lat = parseFloat(position[1]);
          delete item.positionBase;
          if (this.checkFacility(item)) {
            this.$mapStoreService.updateMarker(item, true);
            markers.push(item);
          } else {
            this.$mapStoreService.updateMarker(item, false);
          }
        });
        this.hideProgressBar();
        this.data = markers;
        if (this.isExpandFacilityList) { // 设施列表展开时刷新设施列表
          this.facilityListComponent.setSelectOption();
          this.facilityListComponent.getAllShowData();
        }
        if (this.isExpandMyCollection) { // 我的收藏展开时收起我的收藏
          this.myCollectionComponent.setSelectOption();
          this.myCollectionComponent.refreshLeftMenu();
        }
      } else {
        this.$message.error(result.msg);
        this.hideProgressBar();
      }
    }, err => {
      this.$message.remove();
      this.hideProgressBar();
    });
  }

  /**
   * tabs页签选中变更
   */
  selectedIndexChange(event) {
    // console.log(event);
    if (event === 0) {
      this.isShowFacilityDetailTab = true;
    } else if (event === 1) {
      this.isShowFacilityAlarmTab = true;
    } else if (event === 2) {
      this.isShowFacilityLogAndOrderTab = true;
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
    if (this.$mapStoreService.isInitLogicAreaData
      && this.selectedLogicAreaIdsArr
      && this.selectedLogicAreaIdsArr.indexOf(item.areaId) < 0) {
      return false;
    }
    return true;
  }

  /**
   * 打开设施类型配置modal
   */
  openFacilityTypeConfigModal() {
    const modal = this.$modal.create({
      nzTitle: this.indexLanguage.facilityTypeConfigTitle,
      nzContent: this.facilityTypeConfigTemp,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzFooter: [
        {
          label: this.commonLanguage.confirm,
          onClick: () => {
            this.modifyFacilityTypeConfig(modal);
          }
        },
        {
          label: this.commonLanguage.cancel,
          type: 'danger',
          onClick: () => {
            modal.destroy();
          }
        },
      ]
    });
  }

  /**
   * 更新点
   */
  updateMarkers() {
    console.log('addMarkers');
    const markers = [];
    // console.log(this.$mapStoreService.mapData);
    for (const [key, value] of this.$mapStoreService.mapData) {
      const point = value['info'];
      if (this.checkFacility(point)) {
        this.$mapStoreService.updateMarker(point, true);
        markers.push(point);
      } else {
        this.$mapStoreService.updateMarker(point, false);
      }
    }
    console.log(this.$mapStoreService.mapData);
    this.data = markers;
    if (this.isExpandFacilityList) { // 设施列表展开时刷新设施列表
      this.facilityListComponent.setSelectOption();
      this.facilityListComponent.getAllShowData();
    }
    if (this.isExpandMyCollection) { // 我的收藏展开时收起我的收藏
      this.myCollectionComponent.setSelectOption();
      this.myCollectionComponent.refreshLeftMenu();
    }
    if (this.isShowClustererFacilityTable) { // 聚合点详情点击打开table
      this.hideClustererFacilityTable();
    }
    if (this.facilityId) {
      this.openFacilityPanel();
    } else {
      this.hideFacilityPanel();
    }
    this.$message.remove();
  }

  /**
   * 打开地图配置modal
   */
  openMapSettingModal() {
    const modal = this.$modal.create({
      nzTitle: this.indexLanguage.mapConfigTitle,
      nzContent: this.MapConfigTemp,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzClassName: 'custom-create-modal',
      nzFooter: [
        {
          label: this.commonLanguage.confirm,
          onClick: () => {
            this.modifyMapConfig(modal);
          }
        },
        {
          label: this.commonLanguage.cancel,
          type: 'danger',
          onClick: () => {
            modal.destroy();
          }
        },
      ]
    });
  }

  /**
   * 获取设施列表
   */
  getALLFacilityList() {
    this.showProgressBar();
    this.$mapService.getALLFacilityList().subscribe((result: Result) => {
      // console.log(result);
      if (result.code === 0) {
        this.$message.remove();
        this.cacheData(result.data);
      } else {
        this.$message.error(result.msg);
      }
    }, err => {
      this.$message.remove();
    });
  }

  /**
   * 缓存数据
   * param data
   */
  cacheData(data) {
    this.$mapStoreService.isInitData = true;
    data.forEach(item => {
      const position = item.positionBase.split(',');
      item.lng = parseFloat(position[0]);
      item.lat = parseFloat(position[1]);
      delete item.positionBase;
      this.$mapStoreService.updateMarker(item, true);
      // console.log(item);
      // this.$indexedDBService.add(item, this.tables[0].name);
    });
    // setTimeout(() => {
    //   console.log(this.$indexedDBService.readAll(this.tables[0].name));
    // }, 100);
    this.hideProgressBar();
    this.data = data.filter(item => {
      return this.checkFacility(item);
    });
    if (this.facilityId) {
      this.openFacilityPanel();
    }
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

  /**
   * 获取地图配置信息
   */
  getAllMapConfig() {
    this.$mapService.getALLFacilityConfig().subscribe((result: Result) => {
      // console.log(result);
      this.$mapStoreService.resetConfig();
      if (result.code === 0) {
        if (result.data.deviceIconSize) {
          this.facilityIconSizeValue = result.data.deviceIconSize;
          this.$mapStoreService.facilityIconSize = result.data.deviceIconSize;
          this.iconSize = result.data.deviceIconSize;
        }
        this.$mapStoreService.facilityTypeConfig = result.data.deviceConfig.map(item => {
          return {
            value: item.deviceType,
            label: this.getFacilityTypeName(item.deviceType),
            checked: item.configValue === '1',
            iconClass: this.getFacilityTypeIconClass(item.deviceType),
          };
        });
        this.facilityTypeConfig = CommonUtil.deepClone(this.$mapStoreService.facilityTypeConfig);
        this.$mapStoreService.isInitConfig = true;
        this.$mapStoreService.facilityTypeList = this.$mapStoreService.facilityTypeConfig.filter(item => item.checked);
        this.$mapStoreService.facilityStatusList = this.facilityStatusList();
        this.selectedFacilityTypeIdsArr = this.$mapStoreService.facilityTypeList.map(item => item.value);
        this.selectedFacilityStatusArr = this.$mapStoreService.facilityStatusList.map(item => item.value);
        // this.resetFilter(true);
        this.$mapStoreService.isInitData ? this.updateMarkers() : this.getALLFacilityList();
      } else {
        this.data = [];
        this.$message.error(result.msg);
      }
    }, err => {
      this.data = [];
      this.$message.remove();
    });
  }

  /**
   * 打开设施详情面板
   * param target
   */
  openFacilityPanel() {
    this.isShowFacilityDetailTab = true;
    this.selectedIndex = 0;
    this.showFacilityPanel();
  }

  /**
   * 修改用户设施类型配置
   */
  modifyFacilityTypeConfig(modal) {
    // console.log(this.facilityTypeConfig);
    const data = this.facilityTypeConfig.map(item => {
      return {
        deviceType: item.value,
        configValue: item.checked ? '1' : '0'
      };
    });
    this.$message.loading(this.commonLanguage.saving);
    this.$mapService.modifyFacilityTypeConfig(data).subscribe((result: Result) => {
      // console.log(result);
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
      this.$mapStoreService.facilityTypeList = this.$mapStoreService.facilityTypeConfig.filter(item => item.checked);
      this.selectedFacilityTypeIdsArr = this.$mapStoreService.facilityTypeList.map(item => item.value);
      this.updateMarkers();
      if (this.isExpandFacilityType) {
        this.facilityTypeComponent.initData();
      }
      setTimeout(() => {
        this.$message.remove();
        modal.destroy();
      }, 500);
    }, err => {
      this.$message.remove();
    });
  }

  /**
   * 修改地图配置
   */
  modifyMapConfig(modal) {
    const data = {configValue: this.facilityIconSizeValue};
    this.$message.loading(this.commonLanguage.saving);
    this.$mapService.modifyFacilityIconSize(data).subscribe((result: Result) => {
      if (result.code !== 0) {
        this.$message.remove();
        this.$message.error(result.msg);
        return;
      }
      // console.log(result);
      this.$message.remove();
      this.$message.success(this.commonLanguage.operateSuccess);
      if (this.$mapStoreService.facilityIconSize !== this.facilityIconSizeValue) {
        this.$mapStoreService.facilityIconSize = this.facilityIconSizeValue;
        this.iconSize = this.facilityIconSizeValue;
      }
      setTimeout(() => {
        modal.destroy();
        this.$message.remove();
      }, 500);
    }, err => {
      this.$message.remove();
    });
  }

  /**
   * 地图事件回传
   * param event
   */
  mapEvent(event) {
    if (event.type === 'mapClick') {   // 关闭设施详情面板
      this.hideFacilityPanel();
      this.hideClustererFacilityTable();
    } else if (event.type === 'clickClusterer') {   // 打开聚合点table
      // console.log(event.data);
      this.clustererFacilityList = event.data;
      this.showClustererFacilityTable();
    } else if (event.type === 'cityListControlStatus') {  // 城市控件打开与关闭
      if (event.value) {
        this.hideLeftComponents();
      } else {
        this.showLeftComponents();
      }
    } else if (event.type === 'cityChange') {  // 城市切换
      this.showLeftComponents();
    } else if (event.type === 'selected') {   // 设施选中，打开设施详情面板
      this.facilityId = event.id;
      this.openFacilityPanel();
    } else if (event.type === 'mapDrag') {   // 地图拖动
      this.hideFacilityPanel();
      this.facilityId = null;
      this.hideClustererFacilityTable();
    } else if (event.type === 'resetFacilityId') {   // 重置设施id
      this.mapFacilityId = null;
    } else {

    }
  }

  /**
   * 设施类型回传
   * param event
   */
  facilityTypeEvent(event) {
    if (event.type === 'close') {
      this.foldFacilityType();
    } else if (event.type === 'setting') {
      this.openFacilityTypeConfigModal();
    } else if (event.type === 'update') {
      this.selectedFacilityTypeIdsArr = this.$mapStoreService.facilityTypeList.filter(item => item.checked)
        .map(item => item.value);
      // console.log(this.selectedFacilityTypeIdsArr);
      this.hideFacilityPanel();
      this.updateMarkers();
    } else {
    }
  }

  /**
   * 设施状态回传
   * param event
   */
  facilityStatusEvent(event) {
    if (event.type === 'close') {
      this.foldFacilityStatus();
    } else if (event.type === 'update') {
      this.selectedFacilityStatusArr = this.$mapStoreService.facilityStatusList.filter(item => item.checked)
        .map(item => item.value);
      // console.log(this.selectedFacilityStatusArr);
      this.hideFacilityPanel();
      this.updateMarkers();
    } else {
    }
  }

  /**
   * 设施列表回传
   * param event
   */
  facilityListEvent(event) {
    // this.hideFacilityPanel();
    if (event.type === 'location') {
      this.location(event.id);
    } else if (event.type === 'close') {
      this.foldFacilityList();
    }
  }

  /**
   * 区域列表回传
   * param event
   */
  logicAreaEvent(event) {
    this.hideFacilityPanel();
    if (event.type === 'close') {
      this.foldLogicArea();
    } else if (event.type === 'update') {
      this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.hasPermissions && item.checked)
        .map(item => item.areaId);
      this.hideFacilityPanel();
      this.updateMarkers();
    } else {
    }
  }

  /**
   * 我的收藏回传
   * param event
   */
  myCollectionEvent(event) {
    // this.hideFacilityPanel();
    if (event.type === 'location') {
      this.location(event.id);
    } else if (event.type === 'close') {
      this.foldMyCollection();
    } else if (event.type === 'update') {
      this.indexMyCollectionCacheData = CommonUtil.deepClone(event.data);
    } else {
    }
  }

  /**
   * 设施详情回传
   * param event
   */
  facilityDetailEvent(event) {
    const index = this.indexMyCollectionCacheData.findIndex(item => item.deviceId === event.data.deviceId);
    console.log(this.indexMyCollectionCacheData);
    console.log(event);
    const _data = event.data;
    const _item = {
      address: _data.address,
      areaId: _data.areaId,
      deviceCode: _data.deviceCode,
      deviceId: _data.deviceId,
      deviceName: _data.deviceName,
      deviceStatus: _data.deviceStatus,
      deviceStatusName: this.getFacilityStatusName(_data.deviceStatus),
      deviceType: _data.deviceType,
    };
    if (event.type === 'focusDevice') {
      if (index > -1) {
        this.indexMyCollectionCacheData.splice(index, 1, _item);
      } else {
        this.indexMyCollectionCacheData.push(_item);
      }
    } else if (event.type === 'unFollowDevice') {
      if (index > -1) {
        this.indexMyCollectionCacheData.splice(index, 1);
      }
    }
    if (this.isExpandMyCollection) {
      this.myCollectionComponent.update(event.type, _item);
    }
  }

  /**
   * 聚合点table回传
   * param event
   */
  clustererFacilityListEvent(event) {
    this.hideFacilityPanel();
    if (event.type === 'close') {
      this.foldLogicArea();
    } else if (event.type === 'location') {
      this.facilityId = event.id;
      this.openFacilityPanel();
    } else {
    }
  }

  /**
   * 定位设施
   * param id
   * param {boolean} bol  是否判断聚合点table打开
   */
  location(id, bol = true) {
    this.facilityId = id;
    this.mapFacilityId = this.facilityId;
    if (bol && this.isShowClustererFacilityTable) {
      this.hideClustererFacilityTable();
    }
    if (!this.$mapStoreService.getMarker(this.facilityId)) {
      this.$message.warning(this.indexLanguage.facilityNotExist);
      return;
    }
    this.openFacilityPanel();
  }

  /**
   * 隐藏设施列表
   */
  hideFacilityPanel() {
    this.isShowFacilityPanel = false;
    this.facilityId = null;
    this.mapFacilityId = this.facilityId;
    // this.isShowFacilityAlarmTab = false;
    // this.isShowFacilityLogAndOrderTab = false;
  }

}
