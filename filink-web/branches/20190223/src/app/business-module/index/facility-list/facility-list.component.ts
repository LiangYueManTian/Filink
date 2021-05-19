import {Component, EventEmitter, OnDestroy, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {NzI18nService, NzMessageService} from 'ng-zorro-antd';
import {PageBean} from '../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../shared-module/entity/tableConfig';
import {MapStoreService} from '../../../core-module/store/map.store.service';
import {FacilityStatusCode} from '../facility';
import {TableComponent} from '../../../shared-module/component/table/table.component';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {Router} from '@angular/router';

@Component({
  selector: 'app-facility-list',
  templateUrl: './facility-list.component.html',
  styleUrls: ['./facility-list.component.scss']
})
export class FacilityListComponent implements OnInit, OnDestroy {
  @Output() facilityListEvent = new EventEmitter();
  public language;
  public more;
  dataSetAll = [];
  _dataSet = [];
  pageBean: PageBean = new PageBean(5, 1, 0);
  tableConfig: TableConfig;
  queryConditions = {};
  isShowFacilityTable = false;
  facilityList = [];
  allShowData = {};

  selectOption = [];
  selectedFacilityType = -1;
  title;
  _facilities = [];   // 待过滤的设施集
  private facilityStatusNameObj = {};
  private facilityTypeNameObj = {};
  @ViewChild('areaLevelTemp') areaLevelTemp: TemplateRef<any>;
  @ViewChild('xcTable') xcTable: TableComponent;

  constructor(private $message: NzMessageService,
              private $nzI18n: NzI18nService,
              private $router: Router,
              private $mapStoreService: MapStoreService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocale();
    this.title = this.language.index.facilitiesList;
    this.more = this.language.common.more;
    this.facilityStatusNameObj = this.$mapStoreService.getFacilityStatusNameObj();
    this.facilityTypeNameObj = this.$mapStoreService.getFacilityTypeNameObj();
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

  selectDataChange(event) {

  }

  mapSelectDataChange($event) {
    console.log($event);
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
      typeName: this.language.index.all,
      typeId: '0',
      count: totalCount.count,
      iconClass: ''
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
   * 初始化表格配置
   */
  private initTableConfig() {
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
          title: this.language.facility.deviceStatus, key: 'deviceStatusName', width: 100, searchKey: 'deviceStatus',
          searchable: true,
          searchConfig: {type: 'select', selectInfo: this.selectOption}
        },
        {
          title: this.language.facility.deviceName, key: 'deviceName', width: 80,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.facility.deviceCode, key: 'deviceCode', width: 80,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.facility.address, key: 'address',
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.facility.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 90, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: true,
      operation: [
        {
          text: this.language.index.location,
          className: 'fiLink-location',
          handle: (currentIndex) => {
            console.log(currentIndex);
            this.facilityListEvent.emit({type: 'location', info: currentIndex});
          }
        },
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        console.log(event);
        this.pageBean.pageIndex = 1;
        this.queryConditions = event;
        this.filterData();
      },

    };
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
    console.log(this._facilities);
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
      if (pre['deviceStatus'] !== FacilityStatusCode.alarm && next['deviceStatus'] === FacilityStatusCode.alarm) {
        res = 1;
      } else if (pre['deviceStatus'] === FacilityStatusCode.alarm && next['deviceStatus'] !== FacilityStatusCode.alarm) {
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
    if (this.queryConditions['deviceStatus'] && this.queryConditions['deviceStatus'] !== item.deviceStatus) {
      return false;
    }
    if (this.queryConditions['deviceName'] && !(item.deviceName.includes(this.queryConditions['deviceName']))) {
      return false;
    }
    if (this.queryConditions['deviceCode'] && !(item.deviceCode.includes(this.queryConditions['deviceCode']))) {
      return false;
    }
    if (this.queryConditions['address'] && !(item.address.includes(this.queryConditions['address']))) {
      return false;
    }
    return true;
  }

  getFacilityStatusName(deviceStatus) {
    return this.language.index[this.facilityStatusNameObj[deviceStatus]] || '';
  }

  getFacilityTypeName(deviceType) {
    return this.language.index[this.facilityTypeNameObj[deviceType]] || '';
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
}
