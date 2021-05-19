import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {WorkOrderConfig} from '../../../work-order.config';
import {PageCondition, QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {Result} from '../../../../../shared-module/entity/result';
import {ClearBarrierService} from '../../../../../core-module/api-service/work-order/clear-barrier';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {PictureViewService} from '../../../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {CommonUtil} from '../../../../../shared-module/util/common-util';
import {TreeSelectorConfig} from '../../../../../shared-module/entity/treeSelectorConfig';
import {UserService} from '../../../../../core-module/api-service/user/user-manage';
import {FacilityUtilService} from '../../../../facility';
import {AlarmService} from '../../../../../core-module/api-service/alarm/alarm-manage';
import {ActivatedRoute, Router} from '@angular/router';
import {ImageViewService} from '../../../../../shared-module/service/picture-view/image-view.service';
import {InspectionService} from '../../../../../core-module/api-service/work-order/inspection';

@Component({
  selector: 'app-history-clear-barrier-work-order-table',
  templateUrl: './history-clear-barrier-work-order-table.component.html',
  styleUrls: ['./history-clear-barrier-work-order-table.component.scss']
})
export class HistoryClearBarrierWorkOrderTableComponent extends WorkOrderConfig implements OnInit {
  alarmData; // 关联告警modal内容数据
  selectOption;   //  设施类型下拉框
  dayTimes;    // 一天的毫秒数
  isVisible: boolean = false;
  treeSelectorConfig: TreeSelectorConfig;
  treeNodes = [];
  selectUnitName;
  treeSetting;
  roleArr = [];
  alarmLanguage;
  private filterValue: any;
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  @ViewChild('UnitNameSearch') UnitNameSearch: TemplateRef<any>;
  @ViewChild('refAlarmTemp') refAlarmTemp: TemplateRef<any>;
  @ViewChild('showAlarmTemp') showAlarmTemp: TemplateRef<any>;

  constructor(
    public $nzI18n: NzI18nService,
    private $clearBarrierService: ClearBarrierService,
    private $message: FiLinkModalService,
    private $pictureViewService: PictureViewService,
    private $facilityUtilService: FacilityUtilService,
    private $userService: UserService,
    private $alarmService: AlarmService,
    private $router: Router,
    private $imageViewService: ImageViewService,
    private $modal: NzModalService,
    private $inspection: InspectionService,
    private $active: ActivatedRoute
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.getId();
    this.dayTimes = 1000 * 60 * 60 * 24;
    this.alarmLanguage = this.$nzI18n.getLocaleData('alarm');
    this.setSelectOption();
    this.initTableConfig();
    this.refreshData();
    // this.setExportParamsColumnInfoList();
    this.queryDeptList();
    this.initTreeSelectorConfig();
    this.refAlarmList();
    this.getAllUser();
  }

  refreshData() {
    this.tableConfig.isLoading = true;
    this.$clearBarrierService.getHistoryWorkOrderList(this.queryCondition).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this.pageBean.Total = result.totalCount;
        const data = result.data;
        data.forEach(item => {
          item.statusName = this.getStatusName(item.status);
          item.deviceTypeName = this.getFacilityTypeName(item.deviceType);
          item.statusClass = this.getStatusClass(item.status);
          if (item.status === 'singleBack') {
            item.isShowTurnBackConfirmIcon = true;
          }
        });
        this._dataSet = data;
      } else {
        this.$message.error(result.msg);
      }
    }, err => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 获取剩余天数
   * param start
   * param end
   * returns {any}
   */
  // getLastDays(start, end) {
  //   if (!start || !end) {
  //     return '';
  //   }
  //   const times = parseInt(end, 10) - parseInt(start, 10);
  //   return Math.round(times / this.dayTimes);
  // }

  /**
   * 获得所有的责任人
   */
  getAllUser() {
    this.$inspection.queryAllUser(null).subscribe((result: Result) => {
      result.data.forEach(item => {
        this.roleArr.push({'label': item.userName, 'value': item.id});
      });
    });
  }

  /**
   * 设置设施类型下拉款选项
   */
  setSelectOption() {
    this.selectOption = this.workOrderStatusListArr.filter(item => {
      return item.value === 'completed' || item.value === 'singleBack';
    });
  }

  /**
   * 查看图片
   * param ids
   */
  getPicUrlByAlarmIdAndDeviceId(procId, deviceId) {
    this.$pictureViewService.getPicUrlByAlarmIdAndDeviceId(procId, deviceId).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.length === 0) {
          this.$message.warning(this.workOrderLanguage.noPictureNow);
        } else {
          this.$imageViewService.showPictureView(result.data);
        }
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 搜索
   * param event
   */
  handleSearch(event) {
    this.queryCondition.bizCondition = this.setBizCondition(event);
    this.setPageCondition(event);
    this.refreshData();
  }

  setBizCondition(event) {
    console.log(event);
    const _bizCondition = CommonUtil.deepClone(event);
    if (_bizCondition.accountabilityDeptName) {
      _bizCondition.accountabilityDeptList = CommonUtil.deepClone(_bizCondition.accountabilityDeptName);
      delete _bizCondition.accountabilityDeptName;
    }
    if (_bizCondition.status) {
      _bizCondition.statusList = CommonUtil.deepClone(_bizCondition.status);
      delete _bizCondition.status;
    }
    if (_bizCondition.deviceType) {
      _bizCondition.deviceTypes = CommonUtil.deepClone(_bizCondition.deviceType);
      delete _bizCondition.deviceType;
    }
    if (_bizCondition.cTime) {
      _bizCondition.cTimes = CommonUtil.deepClone(_bizCondition.cTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.cTime;
    }
    if (_bizCondition.ecTime) {
      _bizCondition.ecTimes = CommonUtil.deepClone(_bizCondition.ecTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.ecTime;
    }
    if (_bizCondition.rcTime) {
      _bizCondition.rcTimes = CommonUtil.deepClone(_bizCondition.rcTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.rcTime;
    }
    console.log(_bizCondition);
    return _bizCondition;
  }

  /**
   * 打开关联告警modal
   */
  showRefAlarmModal(data) {
    const refAlarmQueryCondition = new QueryCondition();
    refAlarmQueryCondition.filterConditions.push({filterField: 'id', operator: 'eq', filterValue: data.refAlarm});
    this.$alarmService.queryCurrentAlarmList(refAlarmQueryCondition).subscribe((result: Result) => {
      if (result.code === 0 && result.data.length > 0) {
        this.alarmData = result.data[0];
        Object.keys(this.alarmData).forEach(item => {
          if (item === 'alarmContinousTime') {
            this.alarmData['alarmContinousTime'] = CommonUtil.setAlarmContinousTime(this.alarmData['alarmBeginTime'],
              this.alarmData['alarmCleanTime'],
              {month: this.alarmLanguage.month, day: this.alarmLanguage.day, hour: this.alarmLanguage.hour});
          }
        });
        const modal = this.$modal.create({
          nzTitle: this.workOrderLanguage.refAlarm,
          nzContent: this.showAlarmTemp,
          nzWidth: 700,
          nzFooter: [
            {
              label: this.commonLanguage.confirm,
              type: 'primary',
              onClick: () => {
                modal.destroy();
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
      } else {
        this.$alarmService.queryAlarmHistoryList(refAlarmQueryCondition).subscribe((result_his: Result) => {
          if (result_his.code === 0 && result_his.data.length > 0) {
            this.alarmData = result_his.data[0];
            Object.keys(this.alarmData).forEach(item => {
              if (item === 'alarmContinousTime') {
                this.alarmData['alarmContinousTime'] = CommonUtil.setAlarmContinousTime(this.alarmData['alarmBeginTime'],
                  this.alarmData['alarmCleanTime'],
                  {month: this.alarmLanguage.month, day: this.alarmLanguage.day, hour: this.alarmLanguage.hour});
              }
            });
            const modal = this.$modal.create({
              nzTitle: this.workOrderLanguage.refAlarm,
              nzContent: this.showAlarmTemp,
              nzWidth: 700,
              nzFooter: [
                {
                  label: this.commonLanguage.confirm,
                  type: 'primary',
                  onClick: () => {
                    modal.destroy();
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
          } else {
            this.$message.info('暂无数据');
            return;
          }
        });
      }
    });

  }

  /**
   * 根据ID跳转
   */
  getId() {
    if (this.$active.snapshot.queryParams.id) {
      const workOrderId = this.$active.snapshot.queryParams.id;
      this.queryCondition.bizCondition.procIds = [workOrderId];
    }
  }

  /**
   * 导出
   */
  handleExport(event) {
    this.createExportParams(event);
    this.$clearBarrierService.exportHistoryWorkOrder(this.exportParams).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 生成导出条件
   */
  createExportParams(event) {
    this.exportParams.queryCondition = new QueryCondition();
    if (event.selectItem.length > 0) {
      this.exportParams.queryCondition.bizCondition.procIds = event.selectItem.map(item => item.procId);
    } else {
      this.exportParams.queryCondition.bizCondition = this.queryCondition.bizCondition;
    }
    this.exportParams.excelType = event.excelType;
  }

  /**
   * 设置导出列
   */
  setExportParamsColumnInfoList() {
    this.exportParams.columnInfoList = [
      {
        columnName: this.workOrderLanguage.name,
        propertyName: 'title'
      },
      {
        columnName: this.workOrderLanguage.status,
        propertyName: 'status',
        isTranslation: 1,
      },
      {
        columnName: this.workOrderLanguage.deviceName,
        propertyName: 'deviceName'
      },
      {
        columnName: this.workOrderLanguage.deviceType,
        propertyName: 'deviceTypeName',
      },
      {
        columnName: this.workOrderLanguage.deviceArea,
        propertyName: 'deviceAreaName'
      },
      {
        columnName: this.workOrderLanguage.createTime,
        propertyName: 'cTime',
        isTranslation: 1
      },
      {
        columnName: this.workOrderLanguage.refAlarm,
        propertyName: 'refAlarmName',
      },
      {
        columnName: this.workOrderLanguage.accountabilityUnitName,
        propertyName: 'accountabilityDeptName',
      },
      {
        columnName: this.workOrderLanguage.expectedCompleteTime,
        propertyName: 'ecTime',
        isTranslation: 1
      },
      {
        columnName: this.workOrderLanguage.realCompleteTime,
        propertyName: 'rcTime',
        isTranslation: 1
      },
      {
        columnName: this.workOrderLanguage.singleBackReason,
        propertyName: 'concatSingleBackReason',
      },
      {
        columnName: this.workOrderLanguage.errorReason,
        propertyName: 'concatErrorReason',
      },
      {
        columnName: this.workOrderLanguage.processingScheme,
        propertyName: 'concatProcessingScheme',
      },
      {
        columnName: this.workOrderLanguage.remark,
        propertyName: 'remark',
      },

    ];
  }

  /**
   * 打开责任单位选择器
   */
  showModal(filterValue) {
    this.filterValue = filterValue;
    if (!this.filterValue['filterValue']) {
      this.filterValue['filterValue'] = [];
    }
    this.treeSelectorConfig.treeNodes = this.treeNodes;
    this.isVisible = true;
  }

  /**
   * 初始化单位选择器配置
   */
  private initTreeSelectorConfig() {
    this.treeSetting = {
      check: {
        enable: true,
        chkStyle: 'checkbox',
        chkboxType: {'Y': '', 'N': ''},
      },
      data: {
        simpleData: {
          enable: false,
          idKey: 'id',
        },
        key: {
          name: 'deptName',
          children: 'childDepartmentList'
        },
      },
      view: {
        showIcon: false,
        showLine: false
      }
    };
    this.treeSelectorConfig = {
      title: `${this.facilityLanguage.selectUnit}`,
      width: '1000px',
      height: '300px',
      treeNodes: this.treeNodes,
      treeSetting: this.treeSetting,
      onlyLeaves: false,
      selectedColumn: [
        {
          title: this.facilityLanguage.deptName, key: 'deptName', width: 100,
        },
        {
          title: this.facilityLanguage.deptLevel, key: 'deptLevel', width: 100,
        },
        {
          title: this.facilityLanguage.parentDept, key: 'parmentDeparmentName', width: 100,
        }
      ]
    };
  }

  /**
   * 责任单位选择结果
   * param event
   */
  selectDataChange(event) {
    let selectArr = [];
    this.selectUnitName = '';
    if (event.length > 0) {
      selectArr = event.map(item => {
        this.selectUnitName += `${item.deptName},`;
        return item.id;
      });
    } else {
    }
    this.selectUnitName = this.selectUnitName.substring(0, this.selectUnitName.length - 1);
    if (selectArr.length === 0) {
      this.filterValue['filterValue'] = null;
    } else {
      this.filterValue['filterValue'] = selectArr;
    }
    this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, selectArr);
  }

  /**
   * 查询所有的区域
   */
  private queryDeptList() {
    this.$userService.queryAllDepartment().subscribe((result: Result) => {
      this.treeNodes = result.data || [];
    });
  }

  /**
   * 获取关联告警下拉框数据
   */
  refAlarmList() {
    const alarmQueryCondition = new QueryCondition();
    alarmQueryCondition.pageCondition = new PageCondition(1, 20);
    this.$alarmService.queryAlarmCurrentSetList(alarmQueryCondition).subscribe((result: Result) => {
      const data = result.data;
      const arr = data.map(item => {
        return {value: item.alarmCode, label: item.alarmName};
      });
      // this.tableConfig.columnConfig[0]['searchConfig']['selectInfo'] = this.selectOption;
      this.tableConfig.columnConfig.forEach(item => {
        if (item.searchKey === 'refAlarmCodes') {
          item['searchConfig']['selectInfo'] = arr;
        }
      });
    });
  }

  /**
   * 初始化表格配置
   */
  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      searchReturnType: 'object',
      showSearchExport: true,
      scroll: {x: '1800px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          title: this.workOrderLanguage.name, key: 'title', width: 200,
          configurable: false,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '124px'}},
        },
        {
          title: this.workOrderLanguage.status, key: 'status', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'status',
          minWidth: 100,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.selectOption},
          type: 'render',
          renderTemplate: this.statusTemp,
        },
        {
          title: this.workOrderLanguage.deviceName, key: 'deviceName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.workOrderLanguage.deviceType, key: 'deviceTypeName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'deviceType',
          sortKey: 'deviceType',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.facilityTypeListArr}
        },
        {
          title: this.workOrderLanguage.deviceArea, key: 'deviceAreaName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.workOrderLanguage.createTime, key: 'cTime', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          pipe: 'date',
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.workOrderLanguage.refAlarm, key: 'refAlarmName', width: 200,
          configurable: true,
          type: 'render',
          renderTemplate: this.refAlarmTemp,
          isShowSort: true,
          searchable: true,
          searchKey: 'refAlarmCodes',
          searchConfig: {type: 'select', selectType: 'multiple'}
        },
        {
          title: this.workOrderLanguage.accountabilityUnitName, key: 'accountabilityDeptName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'render', renderTemplate: this.UnitNameSearch}
        },
        {
          title: this.workOrderLanguage.assignName, key: 'assignName', width: 200,
          configurable: true,
          searchable: true,
          searchKey: 'assigns',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.roleArr}
        },
        {
          title: this.workOrderLanguage.expectedCompleteTime, key: 'ecTime', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          pipe: 'date',
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.workOrderLanguage.realCompleteTime, key: 'rcTime', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          pipe: 'date',
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.workOrderLanguage.singleBackReason, key: 'concatSingleBackReason', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'concatSingleBackReasons',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.singleBackReasonListArr}
        },
        {
          title: this.workOrderLanguage.errorReason, key: 'concatErrorReason', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'concatErrorReasons',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.errorReasonListArr}
        },
        {
          title: this.workOrderLanguage.processingScheme, key: 'concatProcessingScheme', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'concatProcessingSchemes',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.processingSchemeListArr}
        },
        {
          title: this.workOrderLanguage.remark, key: 'remark', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },

        {
          title: this.commonLanguage.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 80, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [],
      operation: [
        {
          text: this.commonLanguage.rebuild,
          key: 'isShowTurnBackConfirmIcon',
          className: 'fiLink-turn-back-confirm',
          needConfirm: true,
          permissionCode: '06-2-2-2',
          confirmContent: this.workOrderLanguage.turnBackConfirmContent,
          handle: (currentIndex) => {
            const id = currentIndex.procId;
            this.$router.navigate(['business/work-order/clear-barrier/unfinished/update'], {queryParams: {id}}).then();
          }
        },
        {
          text: this.commonLanguage.viewPhoto,
          className: 'fiLink-view-photo',
          permissionCode: '06-2-2-1',
          handle: (currentIndex) => {
            // this.mapVisible = true;
            this.getPicUrlByAlarmIdAndDeviceId(currentIndex.procId, currentIndex.deviceId);
          }
        },
      ],
      sort: (event: SortCondition) => {
        this.handleSort(event);
      },
      handleSearch: (event) => {
        if (!event.accountabilityDeptName) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        this.handleSearch(event);
      },
      handleExport: (event) => {
        this.exportParams.columnInfoList = event.columnInfoList;
        this.exportParams.columnInfoList.forEach(item => {
          if (item.propertyName === 'status'
            || item.propertyName === 'cTime' || item.propertyName === 'ecTime' || item.propertyName === 'rcTime') {
            item.isTranslation = 1;
          }
        });
        this.handleExport(event);
      }
    };
  }
}
