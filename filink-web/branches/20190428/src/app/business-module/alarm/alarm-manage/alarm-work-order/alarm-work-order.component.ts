import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { ActivatedRoute, Router } from '@angular/router';
import {NzI18nService, DateHelperService, NzModalService} from 'ng-zorro-antd';
import { AlarmService } from '../../../../core-module/api-service/alarm';
import { Result } from '../../../../shared-module/entity/result';
import { AlarmLanguageInterface } from '../../../../../assets/i18n/alarm/alarm-language.interface';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { AlarmStoreService } from '../../../../core-module/store/alarm.store.service';
import {DateFormatString} from '../../../../shared-module/entity/dateFormatString';
import { getDeviceType } from '../../../facility/facility.config';
import { AreaConfig, AlarmNameConfig } from 'src/app/shared-module/component/alarm/alarmSelectorConfig';

@Component({
  selector: 'app-alarm-work-order',
  templateUrl: './alarm-work-order.component.html',
  styleUrls: ['./alarm-work-order.component.scss']
})

export class AlarmWorkOrderComponent implements OnInit {

  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  filterEvent;
  // 区域
  areaList = {
    ids: [],
    name: ''
  };
  areaConfig: AreaConfig;
  alarmNameConfig: AlarmNameConfig;
  // 勾选的告警名称
  _checkAlarmName = {
    name: '',
    ids: []
  };
  checkDisableEnableData;
  @ViewChild('isNoStartTemp') isNoStartTemp: TemplateRef<any>;
  @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;
  @ViewChild('areaSelector') private areaSelectorTemp;
  @ViewChild('alarmName') private alarmName;
  @ViewChild('completionTime') private completionTime;
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $alarmService: AlarmService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $alarmStoreService: AlarmStoreService,
    private $dateHelper: DateHelperService,
    private modalService: NzModalService,
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      noIndex: true,
      notShowPrint: true,
      scroll: { x: '1200px', y: '600px' },
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '60px' } }
        },
        {
          // 名称
          title: this.language.name, key: 'orderName',
          width: 100,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '122px' } }
        },
        {
          // 告警名称
          title: this.language.alarmName, key: 'alarmName',
          width: 200,
          configurable: true,
          searchable: true,
          // searchConfig: { type: 'input' },
          searchConfig: {
            type: 'render',
            selectInfo: this.areaList.ids,
            renderTemplate: this.alarmName
          }
        },
        {
          // 工单类型
          title: this.language.workOrderType, key: 'orderType',
          width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              // 1 是巡检工单 2 是销障工单
              // { label: this.language.pollingWork, value: 1 },
              { label: this.language.eliminateWork, value: 2 },
            ]
          },
        },
        {
          // 区域
          title: this.language.area,
          key: 'areaName',
          width: 200,
          configurable: true,
          searchable: true,
          // searchConfig: { type: 'input' },
          searchConfig: { type: 'render',
            selectInfo: this.areaList.ids,
            renderTemplate: this.areaSelectorTemp },
        },
        {
          // 设施类型
          title: this.language.alarmSourceType, key: 'alarmOrderRuleDeviceTypeList', width: 280, isShowSort: true,
          type: 'render',
          configurable: true,
          renderTemplate: this.deviceTypeTemp,
          searchable: true,
          searchConfig: { type: 'select', selectType: 'multiple',
            selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          // 触发条件
          title: this.language.triggerCondition, key: 'triggerType', isShowSort: true,
          width: 180,
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              // 0 是告警发生时触发 1 是启用状态时触发
              { label: this.language.alarmHappenedTrigger, value: 0 },
              // { label: this.language.startUsingTrigger, value: 1 },
            ]
          },
        },
        {
          // 创建时间
          title: this.language.createTime, key: 'createTime',
          width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          // 期待完工时长(天)
          title: this.language.expectAccomplishTime, key: 'completionTime', isShowSort: true,
          width: 200,
          configurable: true,
          searchable: true,
          // searchConfig: { type: 'render', renderTemplate: this.completionTime },
          searchConfig: { type: 'input', }
        },
        {
          // 启用 和 禁用
          title: this.language.openStatus, key: 'status', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          type: 'render',
          renderTemplate: this.isNoStartTemp,
          searchConfig: {
            type: 'select', selectInfo: [
              // 1 是启用 2 是关闭
              { label: this.language.disable, value: '2' },
              { label: this.language.enable, value: '1' }
            ]
          },
          handleFilter: ($event) => {},
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '',
          width: 120, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      // searchReturnType: 'object',
      operation: [
        {
          // 编辑
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.$router.navigate(['business/alarm/alarm-work-order/update'], {
              queryParams: { id: currentIndex.id }
            }).then();
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          handle: (data) => {
            if (data.status === '1') {
              this.$message.warning(this.language.openStateDisableDelect);
            } else {
              const ids = data.id;
              if (ids) {
                this.delTemplate([ids]);
              }
            }
          }
        }
      ],
      topButtons: [
        {
          // 新增
          text: '+      ' + this.language.add,
          permissionCode: '02-5-1',
          handle: () => {
            this.$router.navigate(['business/alarm/alarm-work-order/add']).then();
          }
        }, {
          // 删除
          text: this.language.delete,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          needConfirm: true,
          canDisabled: true,
          permissionCode: '02-5-5',
          handle: (data) => {
            if (data.find(item => item.status === '1')) {
              this.$message.warning(this.language.openStateDisableDelect);
            } else {
              const ids = data.map(item => item.id);
              if (ids) {
                this.delTemplate(ids);
              }
            }
          }
        }
      ],
      leftBottomButtons: [
        {
          text: this.language.enable,
          permissionCode: '02-5-2',
          canDisabled: true,
          handle: (e) => {
            if ( e && e.length ) {
              this.checkDisableEnableData = e;
              this.enablePopUpConfirm();
            } else {
              this.$message.info(this.language.pleaseCheckThe);
            }
          }
        },
        {
          text: this.language.disable,
          permissionCode: '02-5-3',
          canDisabled: true,
          handle: (e) => {
            if ( e && e.length ) {
              // this.checkDisableEnable(e, 2);
              this.checkDisableEnableData = e;
              this.disablePopUpConfirm();
            } else {
              this.$message.info(this.language.pleaseCheckThe);
            }
          }
        }
      ],
      sort: (event: SortCondition) => {
        // this.queryCondition.filterConditions = this.filterEvent;
        if (event.sortField === 'alarmOrderRuleDeviceTypeList') {
          this.queryCondition.sortCondition.sortField = 'deviceTypeId';
        } else {
          this.queryCondition.sortCondition.sortField = event.sortField;
        }
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        // this.refreshData();
        this.refreshData(this.filterEvent);
      },
      handleSearch: (event) => {
        //  console.log(this.complateionTimeValue);
            console.log(event);
            // 重置告警名称
            if ( !event.length ) {
              // 清除告警名称和区域
              this._checkAlarmName = {
                name: '',
                ids: []
              };
              this.initAlarmName();
              this.areaList = {
                ids: [],
                name: ''
              };
              // 区域
              this.initAreaConfig();
              this.refreshData();
            } else {
              let filterEvent = {};
              event.forEach( ( item, index) => {
                filterEvent[item.filterField] = item.filterValue;
                if ( item.filterField === 'alarmOrderRuleDeviceTypeList' ) {
                  // 设施类型
                  filterEvent = {
                    deviceTypeId: filterEvent['alarmOrderRuleDeviceTypeList']
                  };
                }
                if ( item.filterField === 'createTime' ) {
                  //  创建时间
                  filterEvent['createTime'] = event[0].filterValue;
                  filterEvent['createTimeEnd'] = event[1].filterValue;
                  delete event[index + 1];
                }
                if ( item.filterField === 'orderType' ) {
                  // 工单类型
                  filterEvent['orderTypeList'] = item.filterValue;
                  delete  filterEvent['orderType'];
                }
                if ( item.filterField === 'status' ) {
                  // 是否启用
                  filterEvent['statusArray'] = [item.filterValue];
                  delete  filterEvent['status'];
                }
                if ( item.filterField === 'triggerType' ) {
                  // 触发条件
                  filterEvent['triggerTypeArray'] = item.filterValue;
                  delete  filterEvent['triggerType'];
                }
                // 期待完工时长
                if ( item.filterField === 'completionTime' ) {
                  //  Number(item.filterValue);
                   if ( !Number.isInteger(item.filterValue) ) {
                    filterEvent['completionTime'] = 0;
                   }
                }
              });
              if ( this.areaList && this.areaList.ids && this.areaList.ids.length ) {
                // 区域
                filterEvent['alarmOrderRuleArea'] = this.areaList.ids;
                delete filterEvent['areaName'];
              }
              if ( this._checkAlarmName && this._checkAlarmName.ids && this._checkAlarmName.ids.length ) {
                // 告警名称
                filterEvent['alarmOrderRuleNameList'] = this._checkAlarmName.ids;
              }
              this.filterEvent = filterEvent;
              this.refreshData(filterEvent);
            }
      }
    };
  }

  /**
   * 删除模板
   * param ids
   */
  delTemplate(ids) {
    this.$alarmService.deleteAlarmWork(ids).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.pageBean.pageIndex = 1;
        // this.refreshData();
        this.refreshData(this.filterEvent);
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 获取告警列表信息
   */
  refreshData(filterEvent?) {
    this.tableConfig.isLoading = true;
    const data = filterEvent ? { 'bizCondition': filterEvent } : { 'bizCondition': {} };
    data.bizCondition = { ...data.bizCondition,
      'sortProperties': this.queryCondition.sortCondition.sortField,
      'sort': this.queryCondition.sortCondition.sortRule };
    this.$alarmService.queryAlarmWorkOrder(data).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      if (res.code === 0) {
        this.pageBean.Total = res.totalCount;
        this.pageBean.pageSize = res.data.length;
        this._dataSet = res.data.map( item => {
          item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
          if (item.createTime) {
            item.createTime = this.$dateHelper.format(new Date(item.createTime), DateFormatString.DATE_FORMAT_STRING);
          }
          // 工单类型
          if ( item.orderType === 2 ) {
            item.orderType = this.language.eliminateWork;
          } else if (item.orderType === 1) {
            item.orderType = this.language.pollingWork;
          }
          // 触发条件
          if ( item.triggerType === 0 ) {
              item.triggerType = this.language.alarmHappenedTrigger;
          // } else if (item.triggerType === 1) {
          //     item.triggerType = this.language.startUsingTrigger;
          }
          item.status = item.status + '';
          item.pushType = item.pushType + '';
          // 告警名称
          if (item.alarmOrderRuleNames && item.alarmOrderRuleNames.length ) {
            item.alarmName = item.alarmOrderRuleNames.join();
          }
          // 设施类型
          if ( item.alarmOrderRuleDeviceTypeList && item.alarmOrderRuleDeviceTypeList[0]
            && item.alarmOrderRuleDeviceTypeList[0].deviceTypeId) {
            item.alarmOrderRuleDeviceTypeList.forEach( type => {
              const deviceType = type.deviceTypeId;
              // type['_deviceType'] = deviceType;
              type.deviceType = getDeviceType(this.$nzI18n, deviceType);
              // type['iconClass'] = CommonUtil.getFacilityIconClassName(type._deviceType);
            });
          }
          // 区域
          if ( item.alarmOrderRuleAreaName && item.alarmOrderRuleAreaName.length ) {
            item.areaName = item.alarmOrderRuleAreaName.join(',');
          }
          return item;
        });
      } else {
        // 请求错误
        this.$message.success(res.msg);
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  // 启用弹框 弹框
  enablePopUpConfirm() {
    this.modalService.confirm({
      nzTitle: this.language.prompt,
      nzContent: this.language.isNoAllEnable,
      nzOkText: this.language.cancelText,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: () => {},
      nzCancelText: this.language.okText,
      nzOnCancel: () => {
        // this.confirmationBoxConfirm('affirm');
        this.checkDisableEnable(1);
      },
    });
  }
  // 禁用弹框
  disablePopUpConfirm() {
    this.modalService.confirm({
      nzTitle: this.language.prompt,
      nzContent: this.language.isNoAllDisable,
      nzOkText: this.language.cancelText,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: () => {},
      nzCancelText: this.language.okText,
      nzOnCancel: () => {
        // this.confirmationBoxConfirm('affirm');
        this.checkDisableEnable(2);
      },
    });
  }
  // 批量禁用与启用
  checkDisableEnable( type: 1 | 2 ) {
    const ids = this.checkDisableEnableData.map(item => item.id );
    this.$alarmService.updateWorkStatus(type, ids)
      .subscribe((res: Result) => {
        // this.refreshData();
        this.refreshData(this.filterEvent);
      });
  }

  // 禁启状态
  clickSwitch(data) {
    if (data && data.id) {
      let statusValue;
      this._dataSet = this._dataSet.map(item => {
        if (data.id === item.id) {
          item.clicked = true;
          if (data.status === '1') {
            item.status = '2';
          } else if (data.status === '2') {
            item.status = '1';
          }
          statusValue = item.status;
          return item;
        } else {
          return item;
        }
      });
      this.$alarmService.updateWorkStatus(statusValue, [data.id])
        .subscribe((res: Result) => {
          // this.refreshData();
        });
    }
  }

  pageChange(event) {}

  // 区域
  initAreaConfig() {
    const clear = this.areaList.ids.length ? false : true;
    this.areaConfig = {
      clear: clear,
      checkArea: (event) => {
        this.areaList = event;
      }
    };
  }
  // 告警名称
  initAlarmName() {
     const clear = this._checkAlarmName.ids.length ? false : true;
     this.alarmNameConfig = {
       clear: clear,
       alarmName: (event) => {
         this._checkAlarmName = event;
       }
     };
  }
  ngOnInit() {
    this.initTableConfig();
    this.refreshData();
    // 区域
    this.initAreaConfig();
    // 告警名称
    this.initAlarmName();
  }

}
