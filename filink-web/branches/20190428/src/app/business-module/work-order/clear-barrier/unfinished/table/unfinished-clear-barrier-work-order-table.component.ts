import {Component, EventEmitter, OnInit, Output, TemplateRef, ViewChild} from '@angular/core';
import {PageCondition, QueryCondition, SortCondition} from '../../../../../shared-module/entity/queryCondition';
import {WorkOrderConfig} from '../../../work-order.config';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {Result} from '../../../../../shared-module/entity/result';
import {PictureViewService} from '../../../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {AlarmService} from '../../../../../core-module/api-service/alarm';
import {ActivatedRoute, Router} from '@angular/router';
import {ClearBarrierService} from '../../../../../core-module/api-service/work-order/clear-barrier';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {CommonUtil} from '../../../../../shared-module/util/common-util';
import {TreeSelectorConfig} from '../../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../../../facility';
import {UserService} from '../../../../../core-module/api-service/user/user-manage';
import {ImageViewService} from '../../../../../shared-module/service/picture-view/image-view.service';
import {InspectionService} from '../../../../../core-module/api-service/work-order/inspection';

@Component({
  selector: 'app-unfinished-clear-barrier-work-order-table',
  templateUrl: './unfinished-clear-barrier-work-order-table.component.html',
  styleUrls: ['./unfinished-clear-barrier-work-order-table.component.scss']
})
export class UnfinishedClearBarrierWorkOrderTableComponent extends WorkOrderConfig implements OnInit {
  selectOption;   //  设施类型下拉框
  dayTimes;     // 一天的毫秒数
  selectedAlarmId;   // 选中告警id
  selectedWorkOrderId;   // 指派工单对应id
  isAllChecked = false;
  isIndeterminate = false;
  roleArr = [];
  selectedAccountabilityUnitIdList = [];   // 选中单位id
  accountabilityUnitList = [];    // 单位列表
  singleBackConfirmModal;  // 退单确认modal
  // clickPicInfo: any = {};
  bigPicList = [];
  isVisible: boolean = false;
  treeSelectorConfig: TreeSelectorConfig;   // 树选择器配置
  treeNodes = [];     // 树节点
  selectUnitName;    // 选中单位名称
  treeSetting;
  refAlarmArr: any[];
  workOrderId;  // 跳转过来的ID
  deviceId; // 跳转过来的设施ID
  refAlarmId; // 跳转过来的告警ID
  alarmData;  // 告警数据
  isAssignLoading: boolean; // 点击指派后按钮的状态
  alarmLanguage;
  lastDaySelectValue = 'eq'; // 剩余天数select值
  lastDaysInputValue;
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  @ViewChild('selectorModalTemp') selectorModalTemp: TemplateRef<any>;
  @ViewChild('footerTemp') footerTemp: TemplateRef<any>;
  @ViewChild('UnitNameSearch') UnitNameSearch: TemplateRef<any>;
  @ViewChild('refAlarmTemp') refAlarmTemp: TemplateRef<any>;
  @ViewChild('showAlarmTemp') showAlarmTemp: TemplateRef<any>;
  @ViewChild('remainingDaysFilter') remainingDaysFilter: TemplateRef<any>;
  @ViewChild('singleBackTemp') singleBackTemp: TemplateRef<any>;
  @Output() workOrderEvent = new EventEmitter();
  private filterValue: any;


  constructor(
    public $nzI18n: NzI18nService,
    private $clearBarrierService: ClearBarrierService,
    private $router: Router,
    private $modal: NzModalService,
    private $alarmService: AlarmService,
    private $message: FiLinkModalService,
    private $pictureViewService: PictureViewService,
    private $facilityUtilService: FacilityUtilService,
    private $userService: UserService,
    private $active: ActivatedRoute,
    private $imageViewService: ImageViewService,
    private $inspection: InspectionService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.dayTimes = 1000 * 60 * 60 * 24;
    this.alarmLanguage = this.$nzI18n.getLocaleData('alarm');
    this.setSelectOption();
    this.getId();
    this.initTableConfig();
    this.refreshData();
    this.queryDeptList();
    this.refAlarmList();
    this.initTreeSelectorConfig();
    this.getAllUser();
  }

  /**
   * 刷新图表
   */
  refreshChart() {
    this.workOrderEvent.emit(true);
  }

  /**
   * 获取跳转页面的id
   */
  getId() {
    if (this.$active.snapshot.queryParams.id) {
      this.workOrderId = this.$active.snapshot.queryParams.id;
      this.queryCondition.bizCondition.procIds = [this.workOrderId];
    }
    if (this.$active.snapshot.queryParams.deviceId) {
      this.deviceId = this.$active.snapshot.queryParams.deviceId;
      this.queryCondition.bizCondition.deviceIds = [this.deviceId];
    }
    if (this.$active.snapshot.queryParams.alarmId) {
      this.refAlarmId = this.$active.snapshot.queryParams.alarmId;
      this.queryCondition.bizCondition.refAlarm = this.refAlarmId;
    }
  }

  /**
   * 获取未完工工单列表
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$clearBarrierService.getUnfinishedWorkOrderList(this.queryCondition).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this.pageBean.Total = result.totalCount;
        const data = result.data;
        const nowTimeStamp = new Date().getTime();
        data.forEach(item => {
          item.statusName = this.getStatusName(item.status);
          if (item.lastDays && item.lastDays > 3) {
          } else if (item.lastDays < 0) {
            item.rowStyle = {color: 'red'};
          } else if (item.lastDays && item.lastDays <= 3 && item.lastDays >= 0) {
            item.rowStyle = {color: '#d07d23'};
          } else {
            item.lastDaysClass = '';
          }
          item.deviceTypeName = this.getFacilityTypeName(item.deviceType);
          item.statusClass = this.getStatusClass(item.status);
          this.setIconStatus(item);
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
   * 设置表格操作图标样式
   * param item
   */
  setIconStatus(item) {
    // 只有待指派能删
    item.isShowDeleteIcon = item.status === 'assigned' ? true : 'disabled';
    // 已退单不可编辑
    item.isShowEditIcon = item.status !== 'singleBack';
    // 待处理可以撤回;
    item.isShowRevertIcon = item.status === 'pending' ? true : 'disabled';
    // 待指派可以指派
    item.isShowAssignIcon = item.status === 'assigned' ? true : 'disabled';
    // 工单状态为已退单且未确认   isCheckSingleBack = 0 未确认  1已确认
    // item.isShowTurnBackConfirmIcon = true;
    item.isShowTurnBackConfirmIcon = (item.status === 'singleBack' && item.isCheckSingleBack !== 1);
  }

  /**
   * 新增销障工单
   */
  private addWorkOrder() {
    this.navigateToDetail(`business/work-order/clear-barrier/unfinished/add`);
  }

  /**
   * 跳转
   * param url
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  /**
   * 设置设施类型下拉款选项
   */
  setSelectOption() {
    this.selectOption = this.workOrderStatusListArr.filter(item => {
      // 未确认的已退单的工单也会出现在未完工列表
      return item.value !== 'completed';
    });
  }

  /**
   * 工单类型过滤
   * param status
   */
  filterByStatus(status) {
    if (status) {
      status = [status];
      this.queryCondition.bizCondition.statuss = status;
      this.tableConfig.columnConfig.forEach(item => {
        if (item.key === 'statusName') {
          item.searchConfig.initialValue = status;
        }
      });
    } else {
      this.queryCondition.bizCondition = {};
    }
    this.refreshData();
  }

  /**
   * 删除工单
   * param ids
   */
  deleteWorkOrder(ids) {
    this.$clearBarrierService.deleteWorkOrder(ids).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.resetPageCondition();
        this.refreshData();
        this.refreshChart();
      } else {
        this.$message.error(result.msg);
        this.resetPageCondition();
        this.refreshData();
        this.refreshChart();
      }
    }, () => {
    });
  }

  /**
   * 指派工单
   * param ids
   */
  assignWorkOrder(id, modal) {
    const arr = this.selectedAccountabilityUnitIdList.map(item => {
      return {accountabilityDept: item};
    });
    this.isAllChecked = false;
    this.accountabilityUnitList = [];
    this.selectedAccountabilityUnitIdList = [];
    this.isAssignLoading = false;
    modal.destroy();
    modal.loading = true;
    if (arr.length > 0) {
      this.$clearBarrierService.assignWorkOrder(id, arr).subscribe((result: Result) => {
        if (result.code === 0) {
          this.$message.success(result.msg);
          this.refreshData();
          this.refreshChart();
        } else {
          this.$message.error(result.msg);
          this.refreshData();
          this.refreshChart();
        }
      }, () => {
      });
    } else {
      this.$message.warning(this.workOrderLanguage.pleaseSelectUnit);
      modal.destroy();
    }
  }

  /**
   * 撤回工单
   * param ids
   */
  revokeWorkOrder(id) {
    this.$clearBarrierService.revokeWorkOrder(id).subscribe((result: Result) => {
      if (result.code === 0) {
        this.isAllChecked = false;
        this.accountabilityUnitList = [];
        this.selectedAccountabilityUnitIdList = [];
        this.$message.success(result.msg);
        this.refreshData();
        this.refreshChart();
      } else {
        this.$message.error(result.msg);
        this.refreshData();
        this.refreshChart();
      }
    }, () => {
    });
  }

  /**
   * 打开退单确认modal
   */
  openSingleBackConfirmModal() {
    this.singleBackConfirmModal = this.$modal.create({
      nzTitle: this.workOrderLanguage.singleBackConfirm,
      nzContent: this.singleBackTemp,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzFooter: this.footerTemp
    });
  }

  /**
   * 关闭退单确认modal
   */
  closeSingleBackConfirmModal() {
    this.singleBackConfirmModal.destroy();
  }

  /**
   * 退单确认
   * param ids
   */
  singleBackConfirm() {
    this.$clearBarrierService.singleBackConfirm(this.selectedWorkOrderId).subscribe((result: Result) => {
      if (result.code === 0) {
        this.closeSingleBackConfirmModal();
        this.refreshData();
        this.refreshChart();
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 重新编辑
   */
  rebuild() {
    this.closeSingleBackConfirmModal();
    this.goToDetail(this.selectedWorkOrderId);
  }

  /**
   * 查看图片
   * param ids
   */
  // getPicUrlByAlarmIdAndDeviceId(procId, deviceId) {
  //   this.$pictureViewService.getPicUrlByAlarmIdAndDeviceId(procId, deviceId).subscribe((result: Result) => {
  //     if (result.code === 0) {
  //       if (result.data.length === 0) {
  //         this.$message.warning(this.workOrderLanguage.noPictureNow);
  //       } else {
  //         this.$imageViewService.showPictureView(result.data);
  //       }
  //     } else {
  //       this.$message.error(result.msg);
  //       this.refreshData();
  //     }
  //   }, () => {
  //   });
  // }

  /**
   * 跳转至详情页面进行编辑
   * param id
   */
  goToDetail(id) {
    this.navigateToDetail('business/work-order/clear-barrier/unfinished/update', {queryParams: {id}});
  }

  /**
   * 导出
   */
  handleExport(event) {
    this.createExportParams(event);
    this.$clearBarrierService.exportUnfinishedWorkOrder(this.exportParams).subscribe((result: Result) => {
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
    console.log(event);
    this.exportParams.queryCondition = new QueryCondition();
    if (event.selectItem.length > 0) {
      this.exportParams.queryCondition.bizCondition.procIds = event.selectItem.map(item => item.procId);
    } else {
      this.exportParams.queryCondition.bizCondition = this.queryCondition.bizCondition;
    }
    this.exportParams.excelType = event.excelType;
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
   * 打开部门选择modal
   */
  showSelectorModal() {
    if (!this.selectedAlarmId) {   // 先选告警
      this.$message.warning(this.workOrderLanguage.alarmSelectedError);
      return;
    }
    const modal = this.$modal.create({
      nzTitle: this.workOrderLanguage.accountabilityUnitName,
      nzContent: this.selectorModalTemp,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzFooter: [
        {
          label: this.commonLanguage.confirm,
          onClick: () => {
            this.selectAccountabilityUnit(modal);
          }
        },
        {
          label: this.commonLanguage.cancel,
          type: 'danger',
          onClick: () => {
            modal.destroy();
          }
        },
      ],
    });
    modal.afterOpen.subscribe(() => {
      this.getAccountabilityUnitList();
    });
  }

  /**
   * 选择单位
   */
  selectAccountabilityUnit(modal) {
    this.selectedAccountabilityUnitIdList = this.accountabilityUnitList.filter(item => item.checked).map(item => item.value);
    this.assignWorkOrder(this.selectedWorkOrderId, modal);
  }

  /**
   * 获取告警关联设施对应的单位
   */
  getAccountabilityUnitList() {
    this.$alarmService.queryDepartmentId([this.selectedAlarmId]).subscribe((result: Result) => {
      if (result.code === 0 && result.data.length > 0) {
        console.log(result);
        this.accountabilityUnitList = result.data.map(item => {
          return {
            value: item.responsibleDepartmentId,
            checked: this.selectedAccountabilityUnitIdList.indexOf(item.responsibleDepartmentId) > -1,
            label: item.responsibleDepartment,
          };
        });
      } else {
        this.$alarmService.queryDepartmentHistory([this.selectedAlarmId]).subscribe((result_his: Result) => {
          if (result.code === 0) {
            this.accountabilityUnitList = result_his.data.map(item => {
              return {
                value: item.responsibleDepartmentId,
                checked: this.selectedAccountabilityUnitIdList.indexOf(item.responsibleDepartmentId) > -1,
                label: item.responsibleDepartment,
              };
            });
          } else {
            this.$message.error(result.msg);
          }
        });
      }
    }, () => {
    });
  }

  /**
   * 单位选择改变
   */
  change() {
    const _isAllChecked = this.accountabilityUnitList.every(item => item.checked);
    const _isAllUnchecked = this.accountabilityUnitList.every(item => !item.checked);
    if (_isAllChecked) {
      this.isAllChecked = true;
      this.isIndeterminate = false;
    } else if (_isAllUnchecked) {
      this.isAllChecked = false;
      this.isIndeterminate = false;
    } else {
      this.isAllChecked = false;
      this.isIndeterminate = true;
    }
  }

  /**
   * 全选
   * param event
   */
  checkAll(event) {
    console.log(event);
    this.isIndeterminate = false;
    this.accountabilityUnitList.forEach(item => {
      item.checked = event;
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

  /**
   * 设置过滤条件
   * param event
   * returns {any}
   */
  setBizCondition(event) {
    console.log(event);
    const _bizCondition = CommonUtil.deepClone(event);
    Object.keys(_bizCondition).forEach(item => {
      if (_bizCondition[item] === '') {
        _bizCondition[item] = null;
      }
    });
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
    if (_bizCondition.lastDay) {
      _bizCondition.lastDays = _bizCondition.lastDay;
      _bizCondition.lastDaysOperate = this.lastDaySelectValue;
      delete _bizCondition.lastDay;
    }
    return _bizCondition;
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
        // {
        //   title: '工单ID', key: 'procId', width: 200,
        //   configurable: true,
        //   searchable: true,
        //   searchConfig: {type: 'select', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        // },
        {
          title: this.workOrderLanguage.name, key: 'title', width: 200,
          configurable: false,
          isShowSort: true,
          searchable: true,
          fixedStyle: {fixedLeft: true, style: {left: '124px'}},
          searchConfig: {type: 'input'}
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
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.refAlarmArr}
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
          title: this.workOrderLanguage.lastDays, key: 'lastDays', width: 150,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchKey: 'lastDay',
          minWidth: 125,
          searchConfig: {type: 'render', renderTemplate: this.remainingDaysFilter}
        },
        {
          title: this.workOrderLanguage.remark, key: 'remark', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.workOrderLanguage.turnReason, key: 'turnReason', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.commonLanguage.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 180, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [
        {
          text: '+  ' + this.workOrderLanguage.addWorkOrder,
          permissionCode: '06-2-1-1',
          handle: (currentIndex) => {
            this.addWorkOrder();
          }
        },
        {
          text: this.commonLanguage.deleteBtn,
          btnType: 'danger',
          canDisabled: true,
          needConfirm: true,
          permissionCode: '06-2-1-5',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          handle: (data) => {
            const ids = data.filter(item => item.checked).map(item => item.procId);
            this.deleteWorkOrder(ids);
          }
        }
      ],
      operation: [
        {
          text: this.commonLanguage.turnBackConfirm,
          key: 'isShowTurnBackConfirmIcon',
          className: 'fiLink-turn-back-confirm',
          permissionCode: '06-2-1-6',
          handle: (currentIndex) => {
            this.selectedWorkOrderId = currentIndex.procId;
            this.openSingleBackConfirmModal();
          }
        },
        {
          text: this.commonLanguage.edit,
          permissionCode: '06-2-1-2',
          className: 'fiLink-edit',
          key: 'isShowEditIcon',
          handle: (currentIndex) => {
            // this.mapVisible = true;
            // 如果工单不存在，则不允许编辑
            this.$clearBarrierService.getWorkOrderDetailById(currentIndex.procId).subscribe((result: Result) => {
              if (result.code === 0) {
                this.goToDetail(currentIndex.procId);
              } else {
                this.$message.error(result.msg);
                this.refreshData();
              }
            });

          },
        },
        {
          text: this.commonLanguage.revert,
          permissionCode: '06-2-1-3',
          key: 'isShowRevertIcon',
          className: 'fiLink-revert',
          needConfirm: true,
          confirmContent: this.workOrderLanguage.isRevertWorkOrder,
          disabledClassName: 'fiLink-revert disabled-icon',
          handle: (currentIndex) => {
            this.revokeWorkOrder(currentIndex.procId);
          }
        },
        {
          text: this.commonLanguage.assign,
          key: 'isShowAssignIcon',
          className: 'fiLink-assigned',
          permissionCode: '06-2-1-4',
          disabledClassName: 'fiLink-assigned disabled-icon',
          handle: (currentIndex) => {
            this.selectedAlarmId = currentIndex.refAlarm;
            this.selectedWorkOrderId = currentIndex.procId;
            this.showSelectorModal();
          }
        },
        {
          text: this.commonLanguage.deleteBtn,
          key: 'isShowDeleteIcon',
          permissionCode: '06-2-1-5',
          className: 'fiLink-delete red-icon',
          disabledClassName: 'fiLink-delete disabled-red-icon',
          needConfirm: true,
          handle: (currentIndex) => {
            const ids = [];
            ids.push(currentIndex.procId);
            this.deleteWorkOrder(ids);
          }
        }
        // , {
        //   text: this.commonLanguage.viewPhoto,
        //   permissionCode: '06-2-1-8',
        //   className: 'fiLink-view-photo',
        //   handle: (currentIndex) => {
        //     this.getPicUrlByAlarmIdAndDeviceId(currentIndex.procId, currentIndex.deviceId);
        //   }
        // },

      ],
      sort: (event: SortCondition) => {
        console.log(event);
        this.handleSort(event);
      },
      handleSearch: (event) => {
        console.log(event);
        if (!event.accountabilityDeptName) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        if (event.lastDay !== null) {
        }
        if (!event.lastDay) {
          this.lastDaysInputValue = '';
          this.queryCondition.bizCondition.lastDays = '';
          this.lastDaySelectValue = 'eq';
        }
        this.handleSearch(event);
      },
      handleExport: (event) => {
        console.log(event);
        this.exportParams.columnInfoList = event.columnInfoList;
        this.exportParams.columnInfoList.forEach(item => {
          if (item.propertyName === 'status' || item.propertyName === 'cTime' || item.propertyName === 'ecTime') {
            item.isTranslation = 1;
          }
        });
        this.handleExport(event);
      }
    };
  }
}
