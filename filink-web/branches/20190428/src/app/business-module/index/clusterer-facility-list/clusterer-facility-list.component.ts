import {
  Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef,
  ViewChild
} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {CommonUtil} from '../../../shared-module/util/common-util';
import {IndexTable} from '../index.table';

@Component({
  selector: 'app-clusterer-facility-list',
  templateUrl: './clusterer-facility-list.component.html',
  styleUrls: ['./clusterer-facility-list.component.scss']
})
export class ClustererFacilityListComponent extends IndexTable implements OnInit, OnChanges {
  @Input() facilityList: any[] = [];
  @Output() facilityListEvent = new EventEmitter();
  queryConditions = [];
  selectOption;
  infoWindowLeft = '0px';
  infoWindowTop = '0px';
  isShowInfoWindow = false;
  infoData = {};
  @ViewChild('facilityNameTemp') facilityNameTemp: TemplateRef<any>;
  constructor(
    public $nzI18n: NzI18nService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    // console.log(this.facilityList);
    this.selectOption = [];
    this.setSelectOption();
    this.initTableConfig();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityList) {
      this.filterData();
    }
  }

  /**
   * 设置设施类型下拉款选项
   */
  setSelectOption() {
    const obj = {};
    this.facilityList.forEach(item => {
      if (!obj[item.areaId]) {
        obj[item.areaId] = {
          value: item.areaId,
          label: item.areaName
        };
      }
    });
    this.selectOption = Object.values(obj);
  }

  /**
   * 过滤数据
   */
  filterData() {
    this._dataSet = this.facilityList.filter(item => this.checkFacility(item));
  }

  /**
   * 校检设施
   * param item
   * returns {boolean}
   */
  checkFacility(item) {
    let bol = true;
    this.queryConditions.forEach(q => {
      if (q.filterValue && q.operator === 'eq' && item[q.filterField] !== q.filterValue) {
        bol = false;
      } else if (q.filterValue && q.operator === 'like' && !item[q.filterField].includes(q.filterValue)) {
        bol = false;
      } else if (q.filterValue && q.operator === 'in' && (q.filterValue.indexOf(item[q.filterField]) < 0)) {
        bol = false;
      } else {
      }
    });
    return bol;
  }

  /**
   * 鼠标移入
   * param event
   * param data
   */
  itemMouseOver(event, data) {
    this.openInfoWindow(event, data);
  }

  /**
   * 鼠标移出
   * param event
   */
  itemMouseOut(event) {
    this.isShowInfoWindow = false;
  }

  /**
   * 打开设施提示框
   * param e
   * param data
   */
  openInfoWindow(e, data) {
    this.infoWindowLeft = e.clientX + 'px';
    this.infoWindowTop = e.clientY + 'px';
    const name = `${this.getFacilityTypeName(data.deviceType)}-${data.deviceName} ${data.areaName}`;
    this.infoData = {
      name: name,
      number: data.deviceCode,
      address: data.address,
      className: CommonUtil.getFacilityIconClassName(data.deviceType),
    };
    this.isShowInfoWindow = true;
  }

  /**
   * 打开设施详情面板
   * param data
   */
  openFacilityInfoPanel(data) {
    this.facilityListEvent.emit({type: 'location', id: data.deviceId});
  }

  /**
   * 初始化表格配置
   */
  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '280px', y: '250px'},
      outHeight: 250,
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.indexLanguage.areaName, key: 'areaName', width: 100,
          searchable: true,
          searchKey: 'areaId',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.selectOption}
        },
        {
          title: this.indexLanguage.deviceName, key: 'deviceName', width: 100,
          searchable: true,
          type: 'render',
          renderTemplate: this.facilityNameTemp,
          searchConfig: {type: 'input'}
        },
        {
          title: '', searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 60, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: true,
      operation: [
      ],
      sort: (e) => {
        // console.log(e);
      },
      handleSearch: (event) => {
        // console.log(event);
        this.queryConditions = event;
        this.filterData();
      }
    };
  }
}
