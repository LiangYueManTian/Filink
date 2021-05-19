import {Component, OnInit, TemplateRef, ViewChild, AfterViewInit, OnDestroy} from '@angular/core';
import {MapControl} from './map-control';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {MapService} from '../../../core-module/api-service/index/map';
import {ActivatedRoute, Router} from '@angular/router';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {Result} from '../../../shared-module/entity/result';
import {facilityStatusList} from '../facility';
import {MAP_ICON_CONFIG} from './config';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent extends MapControl implements OnInit, AfterViewInit, OnDestroy {
  mapId;
  facilityId = '';
  selectFacilityId;
  mapType;
  mapConfig;
  facilityTypeConfig;
  facilityIconSizeValue;
  facilityIconSizeConfig;
  iconSize;
  data;
  selectedFacilityTypeIdsArr;
  selectedFacilityStatusArr;
  selectedLogicAreaIdsArr;

  clustererFacilityTableLeft = '0px';
  clustererFacilityTableTop = '0px';
  clustererFacilityList = [];

  @ViewChild('facilityTypeConfigTemp') facilityTypeConfigTemp: TemplateRef<any>;
  @ViewChild('MapConfigTemp') MapConfigTemp: TemplateRef<any>;
  constructor(
    private $mapStoreService: MapStoreService,
    public $nzI18n: NzI18nService,
    private $mapService: MapService,
    private $message: FiLinkModalService,
    private $router: Router,
    private $modal: NzModalService,
    private $activatedRoute: ActivatedRoute
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.mapId = 'index-map';
    this.mapType = localStorage.getItem('mapType') === 'baidu' ? 'b' : 'g';
    this.$mapStoreService.mapType = this.mapType;
    this.facilityId = this.$activatedRoute.snapshot.queryParams.id;
    // this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.facilityIconSizeConfig = MAP_ICON_CONFIG.iconConfig;
    this.iconSize = MAP_ICON_CONFIG.defalutIconSize;
    this.facilityIconSizeValue = MAP_ICON_CONFIG.defalutIconSize;
    // this.facilityId = '808c821e3e2211e9b3520242ac110003';
    // this.showFacilityPanel();
    this.facilityTypeConfig = this.facilityTypeListArr;
    this.checkUser();
    this.getAllMapConfig();
  }

  ngAfterViewInit() {
    this.getALLFacilityList();
  }

  ngOnDestroy() {

  }

  checkUser() {
    if (sessionStorage.getItem('token') !== this.$mapStoreService.token) {
      this.$mapStoreService.isInitData = false;
      this.$mapStoreService.resetData();
    }
    this.$mapStoreService.token = sessionStorage.getItem('token');
  }

  refresh() {
    this.getAllMapConfig(true);
    this.foldLeftComponents();
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
      console.log(this.selectedFacilityTypeIdsArr);
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
      console.log(this.selectedFacilityStatusArr);
    } else {
    }
  }

  /**
   * 设施列表回传
   * param event
   */
  facilityListEvent(event) {
    this.hideFacilityPanel();
    if (event.type === 'location') {
      const info = event.info;
      // this.setCenterAndZoom(info.lng, info.lat,  MapConfig.maxZoom);
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
      // this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.checked).map(item => item.areaId);
      // if (event.refresh) {
      //   this.updateMarkers();
      // }
    } else {
    }
  }

  /**
   * 区域列表回传
   * param event
   */
  clustererFacilityListEvent(event) {
    this.hideFacilityPanel();
    if (event.type === 'close') {
      this.foldLogicArea();
    } else if (event.type === 'update') {
      // this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.checked).map(item => item.areaId);
      // if (event.refresh) {
      //   this.updateMarkers();
      // }
    } else {
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

  resetFilter() {

  }

  /**
   * 获取设施列表
   */
  getALLFacilityList() {
    this.$message.loading(this.commonLanguage.loading);
    this.$mapService.getALLFacilityList().subscribe((result: Result) => {
      console.log(result);
      this.$message.remove();
      this.data = result.data;
      this.$mapStoreService.isInitData = true;
    }, err => {
      this.$message.remove();
      this.$message.error(err.msg);
    });
  }

  /**
   * 获取地图配置信息
   */
  getAllMapConfig(bol = false) {
    this.$mapService.getALLFacilityConfig().subscribe((result: Result) => {
      console.log(result);
      this.$mapStoreService.resetData();
      if (result.code === 0) {
        if (result.data.deviceIconSize) {
          this.facilityIconSizeValue = result.data.deviceIconSize;
          this.$mapStoreService.facilityIconSize = result.data.deviceIconSize;
          this.iconSize = result.data.deviceIconSize;
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
        // this.resetFilter(true);
        if (bol) {
          this.getALLFacilityList();
        }
      } else {
        this.$message.error(result.msg);
      }
    },  err => {
      this.$message.error(err.msg);
    });
  }

  mapEvent(event) {
    if (event.type === 'close') {
      this.hideFacilityPanel();
    } else if (event.type === 'prompt') {
      console.log(event.data);
      this.clustererFacilityList = event.data;
      this.showClustererFacilityTable();
    } else if (event.type === 'cityListControlStatus') {
      if (event.value) {
        this.hideLeftComponents();
      } else {
        this.showLeftComponents();
      }
    } else if (event.type === 'cityChange') {
      this.showLeftComponents();
    } else {

    }
  }

  /**
   * 打开设施详情面板
   * param target
   */
  openFacilityPanel(target) {
    this.showFacilityPanel();
    console.log(target);
    this.selectFacilityId = target.info.deviceId;
  }

  /**
   * 修改用户设施类型配置
   */
  modifyFacilityTypeConfig(modal) {
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
        modal.destroy();
        this.resetFilter();
      } , 500);
    }, err => {
      this.$message.remove();
      this.$message.error(err.msg);
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
      console.log(result);
      this.$message.remove();
      this.$message.success(this.commonLanguage.operateSuccess);
      if (this.$mapStoreService.facilityIconSize !== this.facilityIconSizeValue) {
        this.$mapStoreService.facilityIconSize = this.facilityIconSizeValue;
        // this.setIconSize();
        // this.getAllFacilityListFromStore();
      }
      setTimeout(() => {
        modal.destroy();
        this.$message.remove();
      } , 500);
    }, err => {
      this.$message.remove();
      this.$message.error(err.msg);
    });
  }

}
