import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {TableComponent} from '../../../shared-module/component/table/table.component';
import {Router} from '@angular/router';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {NzI18nService, NzMessageService} from 'ng-zorro-antd';
import {MapService} from '../../../core-module/api-service/index/map';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {Result} from '../../../shared-module/entity/result';
import {IndexTable} from '../index.table';
import {FACILITY_STATUS_CODE} from '../../../shared-module/const/facility';

@Component({
  selector: 'app-my-collection',
  templateUrl: './my-collection.component.html',
  styleUrls: ['./my-collection.component.scss']
})
export class MyCollectionComponent extends IndexTable implements OnInit, OnDestroy {
  @Input() indexCacheData;
  @Output() myCollectionEvent = new EventEmitter();
  public more;
  dataSetAll = [];
  isShowFacilityTable = false;   // 是否显示右侧table
  facilityList = [];
  allShowData = {};
  firstFilterData = [];  // 初次过滤后的数据
  selectOption = [];
  selectedFacilityType = null;    // 选中的设施类型
  title;
  _facilities = [];   // 待过滤的设施集
  _data = [];   // 所有的设施关注数据
  cacheData = [];   // 缓存关注设施数据
  selectedFacilityTypeIdsArr;   // 首页选中的设施类型集合
  selectedFacilityStatusArr;    // 首页选中的设施状态集合
  selectedLogicAreaIdsArr;      // 首页选中的逻辑区域集合
  facilityAlarmCode;
  @ViewChild('areaLevelTemp') areaLevelTemp: TemplateRef<any>;
  @ViewChild('xcTable') xcTable: TableComponent;

  constructor(public $nzI18n: NzI18nService,
              private $message: NzMessageService,
              private $router: Router,
              private $mapService: MapService,
              private $mapStoreService: MapStoreService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.title = this.indexLanguage.myCollection;
    this.more = this.commonLanguage.more;
    this.facilityAlarmCode = FACILITY_STATUS_CODE.alarm;
    this.setSelectOption();
    this.selectedFacilityTypeIdsArr = this.$mapStoreService.facilityTypeList.filter(item => item.checked)
      .map(item => item.value);
    this.selectedFacilityStatusArr = this.$mapStoreService.facilityStatusList.filter(item => item.checked)
      .map(item => item.value);
    this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.checked)
      .map(item => item.areaId);
    this.initTableConfig();
    if (!this.indexCacheData) {
      this.getAllShowData();
    } else {
      this._data = this.indexCacheData;
      this.cacheData = CommonUtil.deepClone(this.indexCacheData);
      this.allShowData = {};
      this.firstFilter();
    }
  }

  /**
   * 设置table中下拉选择框内容
   */
  public setSelectOption() {
    this.selectOption = this.$mapStoreService.facilityStatusList.filter(item => item.checked);
    if (this.tableConfig && this.tableConfig.columnConfig[0]) {
      this.tableConfig.columnConfig[0]['searchConfig']['selectInfo'] = this.selectOption;
      if (this.isShowFacilityTable) {
        this.xcTable.handleRest();
      }
    }
  }

  /**
   * 实时推送更新
   * param type
   * param items
   */
  update(type, items) {
    const index = this.cacheData.findIndex(item => item.deviceId === items.deviceId);
    if (type === 'focusDevice') {
      if (index > -1) {
        this.cacheData.splice(index, 1, items);
      } else {
        this.cacheData.push(items);
      }
    } else if (type === 'unFollowDevice') {
      if (index > -1) {
        this.cacheData.splice(index, 1);
      }
    }
    this.refreshLeftMenu();
    this.myCollectionEvent.emit({type: 'update', data: this.cacheData});
  }

  /**
   * 刷新左侧设施类型统计
   */
  refreshLeftMenu() {
    this._data = CommonUtil.deepClone(this.cacheData);
    this.allShowData = {};
    this.selectedFacilityTypeIdsArr = this.$mapStoreService.facilityTypeList.filter(item => item.checked)
      .map(item => item.value);
    this.selectedFacilityStatusArr = this.$mapStoreService.facilityStatusList.filter(item => item.checked)
      .map(item => item.value);
    this.selectedLogicAreaIdsArr = this.$mapStoreService.logicAreaList.filter(item => item.checked)
      .map(item => item.areaId);
    this.firstFilter();
  }

  /**
   * 获取所有未过滤的数据
   */
  public getAllShowData() {
    this.$mapService.getAllCollectFacilityList().subscribe((result: Result) => {
      console.log(result);
      if (result.code === 0) {
        this._data = result.data;
        this.cacheData = CommonUtil.deepClone(result.data);
        this.allShowData = {};
        this.firstFilter();
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 根据首页的几个筛选组件过滤
   */
  firstFilter() {
    this.firstFilterData = this._data.filter(item => this.checkFacility(item));
    this.firstFilterData.forEach(item => {
      if (!this.allShowData[item['deviceType']]) {
        this.allShowData[item['deviceType']] = [];
      }
      item['deviceStatusName'] = this.getFacilityStatusName(item.deviceStatus);
      this.allShowData[item['deviceType']].push(item);
    });
    this.showLeftList();
  }

  /**
   * 显示左侧列表
   */
  showLeftList() {
    const arr = this.$mapStoreService.facilityTypeList.filter(item => item.checked).map(item => {
      return {
        typeName: item.label,
        typeId: item.value,
        count: this.allShowData[item.value] ? this.allShowData[item.value].length : 0,
        iconClass: CommonUtil.getFacilityIconClassName(item.value)
      };
    });
    const totalCount = arr.reduce((first, second) => {
      return {count: first.count + second.count};
    }, {count: 0});
    arr.unshift({
      typeName: this.indexLanguage.all,
      typeId: '0',
      count: totalCount.count,
      iconClass: CommonUtil.getFacilityIconClassName('0')
    });
    this.facilityList = arr;
    if (this.isShowFacilityTable) {
      const bol = this.facilityList.find(item => item.typeId === this.selectedFacilityType);
      if (bol) {
        this.ShowFacilityTable(this.selectedFacilityType);
      } else {
        this.isShowFacilityTable = false;
      }
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
   * 二次过滤判断 （表格内过滤）
   * param item
   * returns {boolean}
   */
  tableCheckFacility(item) {
    const filter = this.queryCondition.filterConditions;
    if (!filter) {
      return true;
    }
    if (filter['deviceStatus'] && filter['deviceStatus'].length > 0 && filter['deviceStatus'].indexOf(item.deviceStatus) < 0) {
      return false;
    }
    if (filter['deviceName'] && !(item.deviceName.includes(filter['deviceName']))) {
      return false;
    }
    if (filter['deviceCode'] && !(item.deviceCode.includes(filter['deviceCode']))) {
      return false;
    }
    if (filter['address'] && !(item.address.includes(filter['address']))) {
      return false;
    }
    return true;
  }

  /**
   * 显示设施表
   * param id
   * constructor
   */
  ShowFacilityTable(id) {
    if (this.selectedFacilityType === id) {
      this.isShowFacilityTable = false;
      this.selectedFacilityType = null;
    } else {
      this.selectedFacilityType = id;
      this.isShowFacilityTable = true;
      this.queryCondition.filterConditions = [];
      // TODO 目前未提供对应api
      // this.xcTable.tableService.resetFilterConditions(this.xcTable.queryTerm);
      this.setData(id);
    }
  }

  private setData(id) {
    this._facilities = [];   // 待过滤的设施
    if (id === '0') {  // 全部
      Object.keys(this.allShowData).forEach(key => {
        this._facilities = this._facilities.concat(this.allShowData[key]);
      });
    } else {  // 某一类设施
      this._facilities = this.allShowData[id] ? this.allShowData[id] : [];
    }
    console.log(this._facilities);
    this.refreshData();
  }

  refreshData() {
    this.dataSetAll = [];
    this._facilities.forEach(item => {
      if (this.tableCheckFacility(item)) {
        this.dataSetAll.push(item);
      }
    });
    this.sortData();
  }


  /**
   * 将有告警的设施放前面
   */
  sortData() {
    this.dataSetAll = this.dataSetAll.sort((pre, next) => {
      let res;
      if (pre['deviceStatus'] !== this.facilityAlarmCode && next['deviceStatus'] === this.facilityAlarmCode) {
        res = 1;
      } else if (pre['deviceStatus'] === this.facilityAlarmCode && next['deviceStatus'] !== this.facilityAlarmCode) {
        res = -1;
      } else {
        res = 0;
      }
      return res;
    });
    this.showData();
  }

  /**
   * 展示数据
   */
  showData() {
    this.pageBean.Total = this.dataSetAll.length;
    const startIndex = this.pageBean.pageSize * (this.pageBean.pageIndex - 1);
    const endIndex = startIndex + this.pageBean.pageSize - 1;
    this._dataSet = this.dataSetAll.filter((item, index) => {
      return index >= startIndex && index <= endIndex;
    });
    console.log(this._dataSet);
  }

  close() {
    this.myCollectionEvent.emit({type: 'close'});
  }

  ngOnDestroy() {
    this.isShowFacilityTable = false;
    this.selectedFacilityType = null;
  }

  /**
   * 初始化表格配置
   */
  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '440px', y: '250px'},
      topButtons: [],
      simplePage: true,
      noIndex: true,
      searchReturnType: 'object',
      columnConfig: [
        {
          title: this.facilityLanguage.deviceStatus, key: 'deviceStatusName', width: 100, searchKey: 'deviceStatus',
          searchable: true,
          searchConfig: {type: 'select', selectType: 'multiple',  selectInfo: this.selectOption}
        },
        {
          title: this.facilityLanguage.deviceName, key: 'deviceName', width: 80,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.facilityLanguage.deviceCode, key: 'deviceCode', width: 80,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.facilityLanguage.address, key: 'address',
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.commonLanguage.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 90, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: true,
      operation: [
        {
          text: this.indexLanguage.location,
          className: 'fiLink-location',
          handle: (currentIndex) => {
            this.myCollectionEvent.emit({type: 'location', id: currentIndex.deviceId});
          }
        },
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        this.pageBean.pageIndex = 1;
        this.queryCondition.filterConditions = event;
        this.refreshData();
      },

    };
  }
}
