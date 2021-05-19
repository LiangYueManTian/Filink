import {
  Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, TemplateRef,
  ViewChild
} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {PageBean} from '../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../shared-module/entity/tableConfig';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {CommonUtil} from '../../../shared-module/util/common-util';

@Component({
  selector: 'app-clusterer-facility-list',
  templateUrl: './clusterer-facility-list.component.html',
  styleUrls: ['./clusterer-facility-list.component.scss']
})
export class ClustererFacilityListComponent implements OnInit, OnChanges {
  @Input() facilityList: any[] = [];
  @Output() facilityListEvent = new EventEmitter();
  _dataSet = [];
  pageBean: PageBean = new PageBean(5, 1, 0);
  tableConfig: TableConfig;
  queryConditions = [];
  selectOption;
  language: IndexLanguageInterface;
  infoWindowLeft = '0px';
  infoWindowTop = '0px';
  isShowInfoWindow = false;
  infoData = {};
  @ViewChild('facilityNameTemp') facilityNameTemp: TemplateRef<any>;
  constructor(
    private $nzI18n: NzI18nService,
  ) { }

  ngOnInit() {
    console.log(this.facilityList);
    this.language = this.$nzI18n.getLocaleData('index');
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
   * 初始化表格配置
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '280px', y: '250px'},
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.language.areaName, key: 'areaName', width: 100,
          searchable: true,
          searchKey: 'areaId',
          searchConfig: {type: 'select', selectInfo: this.selectOption}
        },
        {
          title: this.language.deviceName, key: 'deviceName',
          searchable: true,
          type: 'render',
          renderTemplate: this.facilityNameTemp,
          searchConfig: {type: 'input'}
        },
        {
          title: '', searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 90, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: true,
      operation: [
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        console.log(event);
        this.queryConditions = event;
        this.filterData();
      }
    };
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
    const name = `${data.deviceTypeName}-${data.deviceName}`;
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
    this.facilityListEvent.emit({type: 'location', info: data});
  }
}
