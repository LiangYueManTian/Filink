import { Component, OnInit, Input , ViewChild, TemplateRef, Output } from '@angular/core';
import { FacilityService } from '../../../../core-module/api-service/facility/facility-manage';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { getDeployStatus, getDeviceType, getDeviceStatus } from 'src/app/business-module/facility/facility.config';
import { NzI18nService } from 'ng-zorro-antd';
import { Result } from '../../../../shared-module/entity/result';
import { AlarmObjectConfig } from '../alarmSelectorConfig';
import { AlarmLanguageInterface } from 'src/assets/i18n/alarm/alarm-language.interface';
import { TableComponent } from 'src/app/shared-module/component/table/table.component';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import {CommonUtil} from '../../../util/common-util';

@Component({
  selector: 'app-alarm-object',
  templateUrl: './alarm-object.component.html',
  styleUrls: ['./alarm-object.component.scss']
})

export class AlarmObjectComponent implements OnInit {
  public language: AlarmLanguageInterface;
  // 勾选的告警对象
  checkAlarmObject = {
    name: '',
    ids: []
  };
  // 备注
  checkAlarmObjectBackups = {
    name: '',
    ids: []
  };
  display = {
    objTable: false,
  };
  pageBeanObject: PageBean = new PageBean(10, 1, 1);
  _dataSetObject = [];
  _type: 'form' | 'table'  = 'table';
  _alarmObjectConfig: AlarmObjectConfig;
  tableConfigObject: TableConfig;
  queryConditionObj: QueryCondition = new QueryCondition();
  constructor(
    public $nzI18n: NzI18nService,
    public $facilityService: FacilityService,
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
   }
  @ViewChild('xCTableComp') private xCTableComp: TableComponent;
  @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;
  @ViewChild('deviceStatusTemp') deviceStatusTemp: TemplateRef<any>;

  @Input()
  set alarmObjectConfig(alarmObjectConfig: AlarmObjectConfig) {
     if ( alarmObjectConfig ) {
       this._alarmObjectConfig = alarmObjectConfig;
       this.setData();
     }
  }
  @Input()
  filterValue;

  setData() {
    if ( this._alarmObjectConfig.type ) {
      this._type = this._alarmObjectConfig.type;
    }
    if ( this._alarmObjectConfig.initialValue && this._alarmObjectConfig.initialValue.ids
      &&  this._alarmObjectConfig.initialValue.ids.length ) {
        this.checkAlarmObject = this.clone(this._alarmObjectConfig.initialValue);
      this.checkAlarmObjectBackups = this.clone(this._alarmObjectConfig.initialValue);
    }
    if (this._alarmObjectConfig.clear) {
      this.checkAlarmObject = {
        name: '',
        ids: []
      };
      this.checkAlarmObjectBackups = {
        name: '',
        ids: []
      };
    }
  }

  // 告警对象请求列表数据
  refreshObjectData(body?) {
    this.$facilityService.deviceListByPage(body || this.queryConditionObj).subscribe((res: Result) => {
      if (res['code'] === 0) {
        this.pageBeanObject.Total = res.totalCount;
        this.pageBeanObject.pageIndex = res.pageNum;
        this.pageBeanObject.pageSize = res.size;
        // this._dataSetObject = res.data.map(item => {
        //   // const name = getDeviceType(this.$nzI18n, item.deviceType);
        //   item['_deviceType'] = item.deviceType;
        //   item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
        //   item['_deviceStatus'] = item.deviceStatus;
        //   item.deviceStatus = getDeviceStatus(this.$nzI18n, item.deviceStatus);
        //   item.deployStatus = getDeployStatus(this.$nzI18n, item.deployStatus);
        //   item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
        //   this.checkAlarmObjectBackups.ids.forEach(_item => {
        //     if (item.deviceId === _item) {
        //       item.checked = true;
        //     }
        //   });
        //   return item ;
        // });
        this._dataSetObject = res.data || [];
        this._dataSetObject.forEach(item => {
          item.areaName = item.areaInfo ? item.areaInfo.areaName : '';
          item['_deviceType'] = item.deviceType;
          item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
          item['_deviceStatus'] = item.deviceStatus;
          item.deviceStatus = getDeviceStatus(this.$nzI18n, item.deviceStatus);
          item.deployStatus = getDeployStatus(this.$nzI18n, item.deployStatus);
          item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
            this.checkAlarmObjectBackups.ids.forEach(_item => {
              if (item.deviceId === _item) {
                item.checked = true;
              }
            });
        });
      }
    });
  }

  // 告警对象弹框
  private initTableConfig() {
    this.tableConfigObject = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      noIndex: true,
      notShowPrint: true,
      scroll: { x: '1200px', y: '340px' },
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 42 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
        },
        {
          title: this.language.type, key: 'deviceType',
          configurable: true,
          type: 'render',
          renderTemplate: this.deviceTypeTemp,
          minWidth: 90,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          // 名称
          title: this.language.name, key: 'deviceName', width: 170,
          searchable: true,
          searchConfig: { type: 'input' },
        },
        {
          // 状态
          title: this.language.status, key: 'deviceStatus',
          width: 200,
          type: 'render',
          renderTemplate: this.deviceStatusTemp,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeviceStatus(this.$nzI18n),
            label: 'label', value: 'code' }
        },
        {
          title: this.language.assetCode, key: 'deviceCode', width: 200,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.address, key: 'address', width: 170,
          searchable: true,
          searchConfig: { type: 'input' },
        },
        {
          title: this.language.deployStatus, key: 'deployStatus', configurable: true, width: 150,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeployStatus(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          // 备注
          title: this.language.remark,
          key: 'remarks', width: 200,
          searchable: true,
          searchConfig: { type: 'input' },
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '',
          width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      topButtons: [],
      operation: [],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
        // this.queryConditionObj.filterConditions = this.filterEvent;
        this.queryConditionObj.sortCondition.sortField = event.sortField;
        this.queryConditionObj.sortCondition.sortRule = event.sortRule;
        this.refreshObjectData();
      },
      handleSelect: (data, currentItem) => {
        if ( !currentItem ) {
          // 当前页面全选 获取全部取消时
          if ( data && data.length ) {
            data.forEach(checkData => {
              if ( this.checkAlarmObjectBackups.ids.indexOf(checkData.deviceId) === -1 ) {
                  // 不存在时 添加进去
                  this.checkData(checkData);
              }
            });
          } else {
            // 取消当前页面的全部勾选
            this._dataSetObject.forEach(item => {
              if ( this.checkAlarmObjectBackups.ids.indexOf(item.deviceId) !== -1 ) {
                // 当该条数据存在于 勾选信息中时 将其移除
                this.cancelCheck(item);
              }
            });
          }
        } else {
          if ( currentItem.checked ) {
            this.checkData(currentItem);
          } else {
            this.cancelCheck(currentItem);
          }
        }
        // this._alarmObjectConfig.alarmObject(this.checkAlarmObject);
      },
      handleSearch: (event) => {
        // this.pageBeanObject['_pageIndex'] = 1;
        // this.pageBeanObject['_pageSize'] = this.queryConditionObj.pageCondition.pageSize;
        if (event.length) {
          const obj = {};
          event.forEach( (item , index ) => {
            obj[item.filterField] = item.filterValue;
            if ( item.filterField === 'deviceNames') {
              item.filterField = 'deviceType';
            }
            // 对于类型里面的 可能为空数组的情况作出特殊处理
            if ( item.filterField === 'deviceStatus' && !item.filterValue.length ) {
               event.splice(index, 1);
            }
          });
        }
        // this.filterEventObject = event;
        this.queryConditionObj.pageCondition.pageNum = 1;
        this.queryConditionObj.pageCondition.pageSize = this.queryConditionObj.pageCondition.pageSize;
        this.refreshObjectData({'filterConditions': event, 'pageCondition': this.queryConditionObj.pageCondition});
      }
    };
  }

  // 勾选数据时
  checkData(currentItem) {
      // 勾选
      this.checkAlarmObjectBackups.ids.push(currentItem.deviceId);
      const names = this.checkAlarmObjectBackups.name + ',' + currentItem.deviceName;
      this.checkAlarmObjectBackups.name = this.checkAlarmObjectBackups.name === '' ? currentItem.deviceName : names;
  }

  // 取消勾选
  cancelCheck(currentItem) {
    // 取消勾选
    this.checkAlarmObjectBackups.ids = this.checkAlarmObjectBackups.ids.filter(id => {
      return currentItem.deviceId !== id && id;
    });
    const names = this.checkAlarmObjectBackups.name.split(',');
    this.checkAlarmObjectBackups.name = names.filter(name => currentItem.deviceName !== name && name ).join(',');
  }

  // 告警对象列表弹框分页
  pageObjectChange(event) {
    // this.queryConditionObj.filterConditions = this.filterEventObject;
    this.queryConditionObj.pageCondition.pageNum = event.pageIndex;
    this.queryConditionObj.pageCondition.pageSize = event.pageSize;
    this.refreshObjectData();
  }

  closeObj() {
    this.checkAlarmObjectBackups = this.clone(this.checkAlarmObject);
    this.display.objTable = false;
    this.pageBeanObject = new PageBean(10, 1, 1);
  }

  objConfirm() {
    this.checkAlarmObject = this.clone(this.checkAlarmObjectBackups);
    if ( this._type === 'table' ) {
      this.filterValue['filterValue'] = this.checkAlarmObject.ids;
    }
    this.display.objTable = false;
    this.pageBeanObject = new PageBean(10, 1, 1);
    this._alarmObjectConfig.alarmObject(this.checkAlarmObject);
  }
  // 克隆数据
  clone(data) {
    return JSON.parse(JSON.stringify(data));
  }

  ngOnInit() {
    this.refreshObjectData();
    this.initTableConfig();
  }

}
