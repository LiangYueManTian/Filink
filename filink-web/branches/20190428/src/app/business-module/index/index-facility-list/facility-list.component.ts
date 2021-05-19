import {Component, EventEmitter, OnDestroy, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {NzI18nService, NzMessageService} from 'ng-zorro-antd';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {TableComponent} from '../../../shared-module/component/table/table.component';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {Router} from '@angular/router';
import {FACILITY_STATUS_CODE} from '../../../shared-module/const/facility';
import {IndexTable} from '../index.table';

@Component({
  selector: 'app-facility-list',
  templateUrl: './facility-list.component.html',
  styleUrls: ['./facility-list.component.scss']
})
export class FacilityListComponent extends IndexTable implements OnInit, OnDestroy {
  @Output() facilityListEvent = new EventEmitter();
  public more;
  dataSetAll = [];
  queryConditions = {};
  isShowFacilityTable = false;
  facilityList = [];
  allShowData = {};

  selectOption = [];
  selectedFacilityType = -1;
  title;
  _facilities = [];   // 待过滤的设施集
  facilityAlarmCode;
  @ViewChild('areaLevelTemp') areaLevelTemp: TemplateRef<any>;
  @ViewChild('xcTable') xcTable: TableComponent;

  constructor(public $nzI18n: NzI18nService,
              private $message: NzMessageService,
              private $router: Router,
              private $mapStoreService: MapStoreService,
              ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.facilityAlarmCode = FACILITY_STATUS_CODE.alarm;
    this.title = this.indexLanguage.facilitiesList;
    this.more = this.commonLanguage.more;
    this.setSelectOption();
    this.initTableConfig();
    this.getAllShowData();
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

  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
    this.showData();
  }

  /**
   * 获取所有未过滤的数据
   */
  public getAllShowData() {
    this.allShowData = {};
    this.isShowFacilityTable = false;
    this.selectedFacilityType = null;
    for (const [key, value] of this.$mapStoreService.mapData) {
      if (value['isShow']) {
        const facility = value['info'];
        if (!this.allShowData[facility['deviceType']]) {
          this.allShowData[facility['deviceType']] = [];
        }
        facility['deviceStatusName'] = this.getFacilityStatusName(facility.deviceStatus);
        this.allShowData[facility['deviceType']].push(facility);
      }
    }
    // console.log(this.allShowData);
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
      iconClass:  CommonUtil.getFacilityIconClassName('0')
    });
    this.facilityList = arr;
    if (this.isShowFacilityTable) {
      const bol = this.facilityList.find(item => item.typeId === this.selectedFacilityType);
      if (bol) {
        this.setData(this.selectedFacilityType);
      } else {
        this.isShowFacilityTable = false;
      }
    }
  }

  /**
   * 显示设施表
   * param id
   * constructor
   */
  ShowFacilityTable(id) {
    if (this.selectedFacilityType === id) {
      this.isShowFacilityTable = false;
      this.selectedFacilityType = -1;
    } else {
      this.queryConditions = {};
      this.selectedFacilityType = id;
      this.isShowFacilityTable = true;
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
    // console.log(this._facilities);
    this.filterData();
  }

  filterData() {
    this.dataSetAll = [];
    this._facilities.forEach(item => {
      if (this.checkFacility(item)) {
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
  }

  /**
   * 过滤判断
   * param item
   * returns {boolean}
   */
  checkFacility(item) {
    const filter = this.queryConditions;
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
    return !filter['address'] || item.address.includes(filter['address']);
  }

  close() {
    this.facilityListEvent.emit({type: 'close'});
  }

  ngOnDestroy() {
    this.queryConditions = {};
    this.isShowFacilityTable = false;
    this.selectedFacilityType = null;
  }

  goToFacilityList() {
    this.$router.navigate([`/business/facility/facility-list`], {}).then();
  }

  update(type, ids) {
    this.getAllShowData();
  }

  /**
   * 初始化表格配置
   */
  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '440px', y: '250px'},
      searchReturnType: 'object',
      topButtons: [],
      simplePage: true,
      noIndex: true,
      columnConfig: [
        {
          title: this.facilityLanguage.deviceStatus, key: 'deviceStatusName', width: 100, searchKey: 'deviceStatus',
          searchable: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.selectOption}
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
            this.facilityListEvent.emit({type: 'location', id: currentIndex.deviceId});
          }
        },
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        this.pageBean.pageIndex = 1;
        this.queryConditions = event;
        this.filterData();
      },
    };
  }
}
