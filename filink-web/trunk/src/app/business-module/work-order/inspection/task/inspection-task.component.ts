import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {ActivatedRoute, Router} from '@angular/router';
import {NzI18nService} from 'ng-zorro-antd';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';
import {Result} from '../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {ExportParams} from '../../../../shared-module/entity/exportParams';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../../facility';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {PictureViewService} from '../../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';
import {AreaConfig} from '../../../../shared-module/component/alarm/alarmSelectorConfig';
import {getDeviceType} from '../../../facility/facility.config';
import {
  workOrderStatus,
  taskStatus,
  taskType,
  workOrderResult,
  getEnableStatus,
  WorkOrderResult,
  workOrderStatusIcon,
  EnableStatus,
  EnablePermission,
  Operate
} from '../../work-order.config';
import {WORK_ORDER_NORMAL_AND_ABNORMAL, WORK_ORDER_UNFINISHED_INSPECTION_NUMBER} from '../../../../shared-module/const/work-order';
import {log} from 'util';
import {TableComponent} from '../../../../shared-module/component/table/table.component';

/**
 * ????????????????????????
 */
@Component({
  selector: 'app-inspection-task',
  templateUrl: './inspection-task.component.html',
  styleUrls: ['./inspection-task.component.scss'],
})

export class InspectionTaskComponent implements OnInit {
  @ViewChild('tableComponent') tableComponent: TableComponent;
  WorkOrder = WORK_ORDER_UNFINISHED_INSPECTION_NUMBER;
  // ???????????????????????????
  public language: InspectionLanguageInterface;
  // ???????????????????????????
  public facilityLanguage: FacilityLanguageInterface;
  // ????????????
  public areaSelectorConfig: any = new TreeSelectorConfig();
  // ?????????????????????
  areaConfig: AreaConfig;
  // ???????????????????????????id
  public treeSelectorConfig: TreeSelectorConfig;
  // ????????????????????????
  public tableConfig: TableConfig;
  // ????????????????????????
  public pageBean: PageBean = new PageBean(10, 1, 1);
  // ??????????????????????????????
  public queryCondition: QueryCondition = new QueryCondition();
  // ????????????????????????
  public associatedWorkOrderTableConfig: TableConfig;
  // ????????????????????????
  public associatedWorkOrderQueryCondition: QueryCondition = new QueryCondition();
  // ????????????????????????
  public associatedWorkOrderPageBean: PageBean = new PageBean(10, 1, 1);
  // ?????????????????????????????????
  public schedulePageBean: PageBean = new PageBean(10, 1, 1);
  // ???????????????????????????????????????
  public scheduleTableConfig: TableConfig;
  // ?????????????????????????????????
  public completedQueryCondition: QueryCondition = new QueryCondition();
  // ????????????
  public exportParams: ExportParams = new ExportParams();
  // ????????????
  @ViewChild('templateStatus') templateStatusTemp: TemplateRef<any>;
  // ??????????????????????????????
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  // ????????????
  @ViewChild('UnitNameSearch') unitNameSearch: TemplateRef<any>;
  // ??????
  @ViewChild('schedule') schedule: TemplateRef<any>;
  // ?????????
  @ViewChild('roleTemp') roleTemp: TemplateRef<any>;
  // ????????????
  @ViewChild('taskPeriodPeTemp') taskPeriodPeTemp: TemplateRef<any>;
  // ??????????????????
  @ViewChild('procPlanDateTemp') procPlanDateTemp: TemplateRef<any>;
  // ????????????
  @ViewChild('deviceCountTemp') deviceCountTemp: TemplateRef<any>;
  // ????????????????????????
  public _dataSet = [];
  // ????????????
  public areaName = '';
  // ??????????????????
  public areaNodes: any = [];
  // ????????????
  public areaSelectVisible: boolean = false;
  public filterValue: any;
  // ???????????????????????????
  public hasData;
  // ??????????????????????????????
  public isAssociatedWorkOrderVisible = false;
  // ??????????????????
  public inspectionTaskTitle;
  // ????????????????????????
  public associatedWorkOrder_dataSet = [];
  // ?????????????????????????????????id
  public id;
  // ????????????id
  public procId;
  // ???????????????????????????
  public isVisible: boolean = false;
  // ??????????????????????????????
  public treeSetting;
  // ????????????????????????
  public selectUnitName;
  public treeNodes = [];
  // ????????????
  public scheduleIsVisible: boolean;
  // title?????????????????????
  public title;
  // ????????????????????????
  public schedule_dataSet = [];
  // ???????????????
  public patroled;
  // ???????????????
  public notInspected;
  // ?????????????????????
  public roleArray = [];
  // ??????
  public areaList = {
    ids: [],
    name: ''
  };
  // ????????????
  public Status;
  // ??????code??????
  public Permission;
  // ???????????????????????????
  public taskPeriodInputValue;
  // ??????????????????????????????
  public taskPeriodSelectedValue = Operate.equal;
  // ?????????????????????????????????
  public procPlanDateInputValue;
  // ????????????????????????????????????
  public procPlanDateSelectedValue = Operate.equal;
  // ???????????????????????????
  public deviceCountInputValue;
  // ??????????????????????????????
  public deviceCountSelectedValue = Operate.equal;
  // ????????????
  @ViewChild('areaSelector') public areaSelectorTemp;

  constructor(public $nzI18n: NzI18nService,
              public $activatedRoute: ActivatedRoute,
              public $inspection: InspectionService,
              public $facilityUtilService: FacilityUtilService,
              public $router: Router,
              public $message: FiLinkModalService,
              public $userService: UserService,
              public $pictureViewService: PictureViewService,
              public $imageViewService: ImageViewService,
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('inspection');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.initTableConfigAssociatedWorkOrder();
    this.Status = EnableStatus;
    this.Permission = EnablePermission;
    this.refreshData();
    this.initTreeSelectorConfig();
    this.scheduleInitTableConfig();
    this.initAreaConfig();  // ??????
  }

  /**
   *?????????????????????????????????
   */
  public initTableConfig() {
    this.tableConfig = {
      primaryKey: '06-1-2',
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      showSearchExport: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        { // ??????????????????
          title: this.language.inspectionTaskName, key: 'inspectionTaskName',
          fixedStyle: {fixedRight: true, style: {left: '124px'}}, width: 150,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        { // ??????????????????
          title: this.language.patrolTaskStatus, key: 'inspectionTaskStatus', width: 120,
          isShowSort: true, configurable: true, searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: taskStatus(this.$nzI18n), label: 'label', value: 'code'
          },
        },
        { // ????????????
          title: this.language.enabledState, key: 'isOpen', width: 120, minWidth: 120,
          isShowSort: true, configurable: true, searchable: true,
          type: 'render', renderTemplate: this.templateStatusTemp,
          searchConfig: {
            type: 'select',
            selectInfo: getEnableStatus(this.$nzI18n), label: 'label', value: 'code'
          },
        },
        { // ????????????????????????
          title: this.language.taskExpectedTime, key: 'procPlanDate', width: 150,
          isShowSort: true, configurable: true, searchable: true,
          searchConfig: {
            type: 'render',
            renderTemplate: this.procPlanDateTemp,
          }
        },
        { // ??????????????????
          title: this.language.inspectionTaskType, key: 'inspectionTaskType', width: 150,
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: taskType(this.$nzI18n), label: 'label', value: 'code'
          },
        },
        { // ????????????
          title: this.language.inspectionCycle, key: 'taskPeriod', width: 150,
          isShowSort: true,
          searchKey: 'taskPeriod',
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'render',
            renderTemplate: this.taskPeriodPeTemp,
          }
        },
        { //  ????????????
          title: this.language.startTime, key: 'startTime', width: 180,
          pipe: 'date', isShowSort: true, configurable: true,
          searchable: true, searchConfig: {type: 'dateRang'},
        },
        { // ????????????
          title: this.language.endTime, key: 'endTime', width: 180,
          pipe: 'date', isShowSort: true, configurable: true,
          searchable: true, searchConfig: {type: 'dateRang'},
        },
        { // ????????????
          title: this.language.inspectionArea, key: 'inspectionAreaName', width: 120,
          configurable: true, searchable: true,
          searchKey: 'inspectionAreaIds',
          searchConfig: {
            type: 'render',
            selectInfo: this.areaList.ids,
            renderTemplate: this.areaSelectorTemp
          },
        },
        { // ??????????????????
          title: this.language.totalInspectionFacilities, key: 'inspectionDeviceCount', width: 150,
          configurable: true, isShowSort: true, searchable: true,
          searchConfig: {
            type: 'render',
            renderTemplate: this.deviceCountTemp,
          }
        },
        { // ????????????
          title: this.language.responsibleUnit, key: 'accountabilityDeptName', width: 200,
          configurable: true, searchable: true, searchKey: 'deptIds',
          searchConfig: {type: 'render', renderTemplate: this.unitNameSearch}
        },
        { // ??????
          title: this.language.operate,
          fixedStyle: {fixedRight: true, style: {right: '0px'}}, width: 150,
          searchable: true, searchConfig: {type: 'operate'},
        },
      ],
      showPagination: true, bordered: false, showSearch: false,
      topButtons: [
        {
          text: '+  ' + this.language.addArea,
          permissionCode: '06-1-2-1',
          handle: () => {
            this.inspectionTaskDetail('add');
          }
        },
        { // ????????????
          text: this.language.delete, permissionCode: '06-1-2-5', btnType: 'danger',
          className: 'table-top-delete-btn', iconClassName: 'icon-delete',
          canDisabled: true, needConfirm: true,
          handle: (data) => {
            const ids = data.map(item => item.inspectionTaskId);
            this.checkData(ids).then((bool) => {
              if (bool === true) {
                const inspectionTask = {
                  isDeleted: '1',
                  inspectionTaskIds: ids,
                };
                this.deleteTemplate(inspectionTask);
              } else {
                this.$message.error(`${this.language.hasDeletedInspectionTask}`);
                this.refreshData();
              }
            });
          }
        },
      ],
      operation: [
        { // ??????
          text: this.language.edit, permissionCode: '06-1-2-2', canDisabled: true,
          key: 'isShowEditIcon', className: 'fiLink-edit',
          disabledClassName: 'fiLink-edit disabled-icon',
          handle: (currentIndex) => {
            const id = [];
            id.push(currentIndex.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true) {
                this.inspectionTaskDetail('update', currentIndex.inspectionTaskId, currentIndex.isOpen);
              } else {
                this.$message.error(`${this.language.theInspectionTaskNoLongerExistsTip}`);
                this.refreshData();
              }
            });
          }
        },
        { // ????????????
          text: this.language.associatedWorkOrder, className: 'fiLink-work-order-m',
          permissionCode: '06-1-2-3',
          handle: (currentIndex) => {
            const id = [];
            id.push(currentIndex.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true) {
                // ????????????????????????
                this.inspectionTaskTitle = this.language.associatedInspectionWorkOrder;
                this.isAssociatedWorkOrderVisible = true;
                this.id = currentIndex.inspectionTaskId;
                this.refreshAssociatedWorkOrderData();
              } else {
                this.$message.error(`${this.language.theInspectionTaskNoLongerExistsTip}`);
                this.refreshData();
              }
            });
          }
        },
        { // ????????????
          text: this.language.inspectionFacility, className: 'fiLink-inspection',
          permissionCode: '06-1-2-4',
          handle: (currentIndex) => {
            const id = [];
            id.push(currentIndex.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true) {
                // ???????????? ??????????????????
                this.navigateToDetail('/business/work-order/inspection/task_device',
                  {queryParams: {id: currentIndex.inspectionTaskId}});
              } else {
                this.$message.error(`${this.language.theInspectionTaskNoLongerExistsTip}`);
                this.refreshData();
              }
            });
          }
        },
        { // ????????????
          text: this.language.delete,
          permissionCode: '06-1-2-5',
          className: 'fiLink-delete red-icon',
          canDisabled: true,
          needConfirm: true,
          handle: (data) => {
            const id = [];
            id.push(data.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true) {
                const inspectionTask = {
                  isDeleted: '1',
                  inspectionTaskIds: [data.inspectionTaskId]
                };
                this.deleteTemplate(inspectionTask);
              } else {
                this.$message.error(`${this.language.theInspectionTaskNoLongerExistsTip}`);
                this.refreshData();
              }
            });
          }
        },
      ],
      leftBottomButtons: [
        { // ??????????????????????????????
          text: this.language.enable,
          permissionCode: '06-1-2-6',
          canDisabled: true,
          handle: (data) => {
            if (data.length > 0) {
              const arrId = data.map(item => item.inspectionTaskId);
              this.checkData(arrId).then((bool) => {
                if (bool === true) {
                  const ids = [];
                  const newArray = data.filter(item => item.isOpen === '0');
                  newArray.forEach(item => {
                    ids.push(item.inspectionTaskId);
                  });
                  const params = new Object();
                  params['inspectionTaskIds'] = ids;
                  if (ids.length === 0) {
                    this.$message.info(`${this.language.enabledTip}`);
                  } else {
                    this.enableStatus(params);
                  }
                } else {
                  this.$message.error(`${this.language.hasDeletedInspectionTask}`);
                  this.refreshData();
                }
              });
            } else {
              return;
            }
          }
        },
        { // ??????????????????????????????
          text: this.language.disable,
          permissionCode: '06-1-2-7',
          canDisabled: true,
          handle: (data) => {
            if (data.length > 0) {
              const allId = data.map(item => item.inspectionTaskId);
              this.checkData(allId).then((bool) => {
                if (bool === true) {
                  const ids = [];
                  const newArray = data.filter(item => item.isOpen === '1');
                  newArray.forEach(item => {
                    ids.push(item.inspectionTaskId);
                  });
                  const params = new Object();
                  params['inspectionTaskIds'] = ids;
                  if (ids.length === 0) {
                    this.$message.info(`${this.language.disableTip}`);
                  } else {
                    this.disableStatus(params);
                  }
                } else {
                  this.$message.error(`${this.language.hasDeletedInspectionTask}`);
                  this.refreshData();
                }
              });
            } else {
              return;
            }
          }
        },
      ],
      // ??????
      sort: (event: SortCondition) => {
        this.handleSort(event);
        this.refreshData();
      },
      // ??????
      handleSearch: (event) => {
        this.taskPeriodInputValue = event.taskPeriod;
        this.procPlanDateInputValue = event.procPlanDate;
        this.deviceCountInputValue = event.inspectionDeviceCount;
        // ????????????????????????????????????
        if (!event.deptIds) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        if (!event.inspectionAreaIds) {
          this.areaList = {
            ids: [],
            name: ''
          };
          // ??????
          this.initAreaConfig();
        } // ??????
        if (!event.taskPeriod) {
          this.taskPeriodInputValue = '';
          this.queryCondition.bizCondition.taskPeriod = '';
          this.taskPeriodSelectedValue = Operate.equal;
        }// ????????????
        if (!event.procPlanDate) {
          this.procPlanDateInputValue = '';
          this.queryCondition.bizCondition.procPlanDate = '';
          this.procPlanDateSelectedValue = Operate.equal;
        } // ????????????
        if (!event.inspectionDeviceCount) {
          this.deviceCountInputValue = '';
          this.queryCondition.bizCondition.inspectionDeviceCount = '';
          this.deviceCountSelectedValue = Operate.equal;
        }
        this.handleSearch(event);
        this.refreshData();
      },
      // ??????
      handleExport: (event) => {
        // ???????????????
        this.exportParams.columnInfoList = event.columnInfoList;
        this.exportParams.columnInfoList.forEach(item => {
          if (item.propertyName === 'inspectionTaskType' || item.propertyName === 'startTime' ||
            item.propertyName === 'endTime' || item.propertyName === 'inspectionTaskStatus' ||
            item.propertyName === 'isOpen') {
            item.isTranslation = 1;
          }
        });
        this.handleExport(event);
      }
    };
  }

  /**
   *????????????????????????
   */
  public refreshData() {
    this.tableConfig.isLoading = true;
    // ????????????????????????id??????
    if ('id' in this.$activatedRoute.snapshot.queryParams) {
      if (!this.queryCondition.filterConditions.some(item => item.filterField === 'optObjId')) {
        this.queryCondition.bizCondition.inspectionTaskIds = [this.$activatedRoute.snapshot.queryParams.id];
      }
    } else {
      this.queryCondition.filterConditions = this.queryCondition.filterConditions.filter(item => item.filterField !== 'optObjId');
    }
    this.$inspection.getWorkOrderList(this.queryCondition).subscribe((result: Result) => {
      this.pageBean.Total = result.totalCount;
      this.pageBean.pageIndex = result.pageNum;
      this.pageBean.pageSize = result.size;
      this.tableConfig.isLoading = false;
      const data = result.data;
      data.forEach(item => {
        item.inspectionTaskStatus = taskStatus(this.$nzI18n, item.inspectionTaskStatus);
        item.inspectionTaskType = taskType(this.$nzI18n, item.inspectionTaskType);
        this.setIconStatus(item);
      });
      this._dataSet = result.data;
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   *??????????????????
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * ????????????????????????
   * param event
   */
  handleSearch(event) {
    this.queryCondition.bizCondition = this.setBizCondition(event);
    if (this.taskPeriodInputValue || this.taskPeriodSelectedValue) {
      this.queryCondition.bizCondition.taskPeriod = this.taskPeriodInputValue;
      this.queryCondition.bizCondition.taskPeriodOperate = this.taskPeriodSelectedValue;
    }
    if (this.procPlanDateInputValue || this.procPlanDateSelectedValue) {
      this.queryCondition.bizCondition.procPlanDate = this.procPlanDateInputValue;
      this.queryCondition.bizCondition.procPlanDateOperate = this.procPlanDateSelectedValue;
    }
    if (this.deviceCountInputValue || this.deviceCountSelectedValue) {
      this.queryCondition.bizCondition.inspectionDeviceCount = this.deviceCountInputValue;
      this.queryCondition.bizCondition.inspectionDeviceCountOperate = this.deviceCountSelectedValue;
    }
    this.setPageCondition(event);
  }

  /**
   * ????????????????????????????????????
   */
  setPageCondition(event) {
    this.queryCondition.pageCondition.pageNum = 1;
  }

  /**
   * ??????????????????????????????
   */
  setBizCondition(event) {
    const _bizCondition = CommonUtil.deepClone(event);
    if (_bizCondition.inspectionTaskStatus) {
      _bizCondition.inspectionTaskStatusList = CommonUtil.deepClone(_bizCondition.inspectionTaskStatus);
      delete _bizCondition.inspectionTaskStatus;
    }
    if (_bizCondition.startTime) {
      _bizCondition.startTimes = CommonUtil.deepClone(_bizCondition.startTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.startTime;
    }
    if (_bizCondition.endTime) {
      _bizCondition.endTimes = CommonUtil.deepClone(_bizCondition.endTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.endTime;
    }
    if (_bizCondition.inspectionTaskType) {
      _bizCondition.inspectionTaskTypes = CommonUtil.deepClone(_bizCondition.inspectionTaskType);
      delete _bizCondition.inspectionTaskType;
    }
    return _bizCondition;
  }

  /**
   * ??????????????????
   */
  clickSwitch(data) {
    const id = [];
    id.push(data.inspectionTaskId);
    this.checkData(id).then((bool) => {
      if (bool === true) {
        const inspectionTaskId = {
          inspectionTaskIds: [data.inspectionTaskId]
        };
        data.clicked = false;
        data.isOpen === '0' ? this.enableStatus(inspectionTaskId) : this.disableStatus(inspectionTaskId);
        data.clicked = true;
      } else {
        this.$message.error(`${this.language.theInspectionTaskNoLongerExistsTip}`);
        this.refreshData();
      }
    });
  }

  /**
   * ????????????????????????
   */
  enableStatus(id) {
    this.$inspection.enableInspectionTask(id).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * ????????????????????????
   */
  disableStatus(id) {
    this.$inspection.disableInspectionTask(id).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * ????????????????????????
   */
  deleteTemplate(inspectionTask) {
    this.$inspection.deleteWorkOrderByIds(inspectionTask).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        // ??????????????????????????????
        // this.pageBean.pageIndex = 1;
        this.queryCondition.pageCondition.pageNum = 1;
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   *???????????? ?????????????????????
   */
  inspectionTaskDetail(type, inspectionTaskId?, isOpen?) {
    return this.$router.navigate([`/business/work-order/inspection/task-detail/${type}`],
      {queryParams: {inspectionTaskId: inspectionTaskId, isOpen: isOpen}});
  }

  /**
   * ???????????????
   * param url
   */
  public navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  /**
   * ????????????????????????
   * param event
   */
  handleSort(event) {
    this.queryCondition.sortCondition.sortField = event.sortField;
    this.queryCondition.sortCondition.sortRule = event.sortRule;
  }

  /**
   * ????????????????????????????????????
   */
  createExportParams(event) {
    this.exportParams.queryCondition = this.queryCondition;
    if (event.selectItem.length > 0) {
      this.exportParams.queryCondition.bizCondition.inspectionTaskIds = event.selectItem.map(item => item.inspectionTaskId);
    }
    this.exportParams.excelType = event.excelType;
  }

  /**
   * ????????????????????????
   */
  handleExport(event) {
    this.createExportParams(event);
    this.$inspection.exportInspectionTask(this.exportParams).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * ?????????????????????(????????????)
   */
  setIconStatus(item) {
    item.isShowEditIcon = item.inspectionTaskStatus !== `${this.language.completed}` ? true : false;
  }

  /**
   * ???????????????????????????
   */
  public initTableConfigAssociatedWorkOrder() {
    this.associatedWorkOrderTableConfig = {
      isDraggable: true,
      primaryKey: '06-1-2-3',
      showSearchSwitch: true,
      isLoading: false,
      showSizeChanger: true,
      notShowPrint: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          title: this.language.workOrderName, key: 'title', width: 200,
          fixedStyle: {fixedLeft: true, style: {left: '124px'}},
          searchable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {
          title: this.language.workOrderStatus, key: 'status', width: 200,
          searchable: true, type: 'render', isShowSort: true, configurable: true,
          searchKey: 'status', renderTemplate: this.statusTemp,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: workOrderStatus(this.$nzI18n), label: 'label', value: 'code'
          },
        },
        {
          title: this.language.inspectionStartTime, key: 'inspectionStartTime', width: 200, pipe: 'date',
          searchable: true, isShowSort: true, configurable: true,
          searchKey: 'inspectionStartTime', searchConfig: {type: 'dateRang'}
        },
        {
          title: this.language.inspectionEndTime, key: 'inspectionEndTime', width: 200, pipe: 'date',
          searchable: true, isShowSort: true, configurable: true,
          searchKey: 'inspectionEndTime',
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.language.deviceType, key: 'deviceTypeName', width: 200,
          searchable: true, configurable: true, searchKey: 'deviceTypes',
          searchConfig: {
            type: 'select', selectType: 'multiple',
            selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'
          }
        },
        {
          title: this.language.responsibleUnit, key: 'accountabilityDeptName', width: 200,
          searchable: true, configurable: true,
          searchKey: 'accountabilityDeptList',
          searchConfig: {type: 'render', renderTemplate: this.unitNameSearch}
        },
        { // ?????????
          title: this.language.responsible, key: 'assignName', width: 200,
          searchKey: 'assigns',
          configurable: true, searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: this.roleArray,
          }
        },
        { // ??????
          title: this.language.schedule, key: 'progressSpeed', width: 200,
          configurable: true,
          searchConfig: {type: 'input'}, type: 'render', renderTemplate: this.schedule,
        },
        {
          title: this.language.remarks, key: 'remark', width: 200,
          fixedStyle: {fixedRight: true, style: {right: '0px'}},
          isShowSort: true, searchable: true, searchConfig: {type: 'operate'}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      // ??????
      sort: (event: SortCondition) => {
        this.associatedWorkOrderQueryCondition.sortCondition.sortField = event.sortField;
        this.associatedWorkOrderQueryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshAssociatedWorkOrderData();
      },
      // ??????
      handleSearch: (event) => {
        this.associatedWorkOrderQueryCondition.bizCondition = event;
        // ????????????????????????????????????
        if (!event.accountabilityDeptList) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        this.handleSearchAssociatedWorkOrder(event);
        this.refreshAssociatedWorkOrderData();
      },
      openTableSearch: (event) => {
        this.getAllUser();
      },
    };
  }

  /**
   *??????????????????
   */
  pageChangeAssociatedWorkOrder(event) {
    this.associatedWorkOrderQueryCondition.pageCondition.pageNum = event.pageIndex;
    this.associatedWorkOrderQueryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshAssociatedWorkOrderData();
  }

  /**
   * ??????????????????????????????
   */
  public refreshAssociatedWorkOrderData() {
    this.associatedWorkOrderTableConfig.isLoading = true;
    this.associatedWorkOrderQueryCondition.bizCondition.inspectionTaskId = this.id;
    const param = this.associatedWorkOrderQueryCondition;
    this.$inspection.associatedWorkOrder(param).subscribe((result: Result) => {
      this.associatedWorkOrderTableConfig.isLoading = false;
      if (result.code === 0) {
        this.associatedWorkOrderPageBean.Total = result.totalCount;
        this.associatedWorkOrderTableConfig.isLoading = false;
        const data = result.data;
        data.forEach(item => {
          item.statusName = workOrderStatus(this.$nzI18n, item.status);
          item.statusClass = this.getStatusClass(item.status);
        });
        this.associatedWorkOrder_dataSet = result.data;
      }
    }, () => {
      this.associatedWorkOrderTableConfig.isLoading = false;
    });
  }

  /**
   * ????????????????????????
   * param event
   */
  handleSearchAssociatedWorkOrder(event) {
    this.associatedWorkOrderQueryCondition.bizCondition = this.setBizConditionAssociatedWorkOrder(event);
    this.setPageConditionAssociatedWorkOrder(event);
  }

  /**
   * ????????????????????????????????????
   */
  setPageConditionAssociatedWorkOrder(event) {
    this.associatedWorkOrderQueryCondition.pageCondition.pageNum = 1;
  }

  /**
   * ????????????????????????
   */
  setBizConditionAssociatedWorkOrder(event) {
    const _bizCondition = CommonUtil.deepClone(event);
    if (_bizCondition.status) {
      _bizCondition.statusList = CommonUtil.deepClone(_bizCondition.status);
      delete _bizCondition.status;
    }
    if (_bizCondition.inspectionStartTime) {
      _bizCondition.inspectionStartTimes = CommonUtil.deepClone(_bizCondition.inspectionStartTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.inspectionStartTime;
    }
    if (_bizCondition.inspectionEndTime) {
      _bizCondition.inspectionEndTimes = CommonUtil.deepClone(_bizCondition.inspectionEndTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.inspectionEndTime;
    }
    if (_bizCondition.deviceType) {
      _bizCondition.deviceTypes = CommonUtil.deepClone(_bizCondition.deviceTypeName);
      delete _bizCondition.deviceType;
    }
    return _bizCondition;
  }

  /**
   * ?????????????????????
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${workOrderStatusIcon[status]}`;
  }

  /**
   * ????????????????????????
   */
  getResultStatus(result) {
    return this.language[WorkOrderResult[result]];
  }

  /**
   * ????????????
   */
  getPicUrlByAlarmIdAndDeviceId(procId, deviceId) {
    this.$pictureViewService.getPicUrlByAlarmIdAndDeviceId(procId, deviceId).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.length === 0) {
          this.$message.warning(`${this.language.noPicture}`);
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
   * ????????????????????????????????????
   */
  showCompleted(data) {
    this.procId = data.procId;
    this.refreshCompleteData(data.procId);
    this.title = this.language.completeInspectionInformation;
    this.scheduleIsVisible = true;
    const ids = {'procId': data.procId};
    this.$inspection.queryProcInspectionByProcInspectionId(ids).subscribe((result: Result) => {
      if (result.code === 0) {
        this.patroled = result.data.inspectionCompletedCount;
        this.notInspected = result.data.inspectionProcessCount;
      } else {
      }
    });
  }

  /**
   * ????????????????????????
   */
  close() {
    this.isAssociatedWorkOrderVisible = false;
    this.associatedWorkOrderPageBean = new PageBean(10, 1, 1);
    this.tableComponent.handleRest();
   // this.initTableConfigAssociatedWorkOrder();
  }

  /**
   * ??????????????????
   */
  scheduleClose() {
    this.scheduleIsVisible = false; // ????????????
  }

  /**
   * ??????????????????????????????????????????
   */
  public scheduleInitTableConfig() {
    this.scheduleTableConfig = {
      primaryKey: '06-1-3',
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      showSearchExport: false,
      notShowPrint: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {// ????????????
          title: this.language.inspectionFacility, key: 'deviceName', width: 200,
          searchable: true, configurable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {// ????????????
          title: this.language.inspectionResults, key: 'result', width: 200,
          searchable: true, configurable: true, isShowSort: true,
          searchConfig: {
            type: 'select',
            selectInfo: [
              {label: this.language.normal, value: this.WorkOrder.StrZero},  // ??????
              {label: this.language.abnormal, value: this.WorkOrder.StrOne},   // ??????
            ]
          },
        },
        {// ????????????
          title: this.language.exceptionallyDetailed, key: 'exceptionDescription', width: 200,
          searchable: true, configurable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {// ????????????
          title: this.language.inspectionTime, key: 'inspectionTime', width: 200, pipe: 'date',
          searchable: true, configurable: true, searchKey: 'inspectionTimes',
          sortKey: 'inspectionTime',
          isShowSort: true, searchConfig: {type: 'dateRang'}
        },
        {// ?????????
          title: this.language.responsible, key: 'updateUserName', width: 200,
          searchKey: 'updateUser', isShowSort: true,
          configurable: true, searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: this.roleArray,
          }
        },
        {// ??????????????????
          title: this.language.matchingOfResources, key: 'resourceMatching', width: 200,
          searchable: true, configurable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {// ????????????
          title: this.language.relatedPictures, searchable: true,
          searchConfig: {type: 'operate'}, width: 200,
          fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      operation: [
        {
          text: this.language.viewDetail,
          className: 'fiLink-view-photo',
          handle: (currentIndex) => {
            this.getPicUrlByAlarmIdAndDeviceId(currentIndex.procId, currentIndex.deviceId);
          }
        },
      ],
      sort: (event) => {
        this.completedQueryCondition.sortCondition.sortField = event.sortField;
        this.completedQueryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshCompleteData();
      },
      handleSearch: (event) => {
        this.handleSearchCompletedWorkOrder(event);
        this.refreshCompleteData();
      },
      openTableSearch: (event) => {
        this.getAllUser();
      },
    };
  }

  /**
   * ???????????????????????????????????????
   */
  public refreshCompleteData(procId?) {
    this.scheduleTableConfig.isLoading = true;
    this.completedQueryCondition.bizCondition.inspectionTaskId = this.id;
    this.completedQueryCondition.bizCondition.procId = this.procId;
    const param = this.completedQueryCondition;
    this.$inspection.getUnfinishedCompleteList(param).subscribe((result: Result) => {
      if (result.code === 0) {
        for (let i = 0; i < result.data.data.length; i++) {
          if (result.data.data[i].result === this.WorkOrder.StrZero) {
            result.data.data[i].result = this.language.normal;
          } else if (result.data.data[i].result === this.WorkOrder.StrOne) {
            result.data.data[i].result = this.language.abnormal;
          }
        }
        this.scheduleTableConfig.isLoading = false;
        this.schedule_dataSet = result.data.data;
        // const data = result.data.data;
        // data.forEach(item => {
        //   item.result = this.getResultStatus(item.result);
        // });
      }
    }, () => {
      this.scheduleTableConfig.isLoading = false;
    });
  }

  /**
   * ???????????????????????????????????????
   */
  schedulePageChange(event) {
    this.completedQueryCondition.pageCondition.pageNum = event.pageIndex;
    this.completedQueryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshCompleteData();
  }

  /**
   * ??????????????????????????????
   * param event
   */
  handleSearchCompletedWorkOrder(event) {
    this.completedQueryCondition.bizCondition = this.setBizConditionCompletedWorkOrder(event);
    this.setPageConditionCompletedWorkOrder(event);
  }

  /**
   * ??????????????????????????????????????????
   */
  setPageConditionCompletedWorkOrder(event) {
    this.completedQueryCondition.pageCondition.pageNum = 1;
  }

  /**
   * ????????????????????????????????????
   */
  setBizConditionCompletedWorkOrder(event) {
    const _bizCondition = CommonUtil.deepClone(event);
    if (_bizCondition.inspectionTime) {
      _bizCondition.inspectionTimes = CommonUtil.deepClone(_bizCondition.inspectionTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.inspectionTime;
    }
    return _bizCondition;
  }

  /**
   * ??????????????????
   */
  areaSelectChange(event) {
    if (event[0]) {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, event[0].areaId);
      this.areaName = event[0].areaName;
      const areaId = [];
      areaId.push(event[0].areaId);
      this.filterValue['filterValue'] = areaId;
    } else {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null);
      this.areaName = '';
    }
  }

  /**
   *????????????
   */
  initAreaConfig() {
    const clear = this.areaList.ids.length ? false : true;
    this.areaConfig = {
      clear: clear,
      checkArea: (event) => {
        this.areaList = event;
      }
    };
  }

  /**
   * ????????????????????????
   */
  checkData(id) {
    return new Promise((resolve, reject) => {
      this.$inspection.getWorkOrderList(this.queryCondition).subscribe((result: Result) => {
        for (let j = 0; j < id.length; j++) {
          for (let i = 0; i < result.data.length; i++) {
            if (id[j] === result.data[i].inspectionTaskId) {
              this.hasData = true;
              break;
            } else {
              this.hasData = false;
            }
          }
        }
        if (this.hasData === true) {
          resolve(true);
        } else {
          resolve(false);
        }
      }, (error) => {
        reject(error);
      });
    });
  }

  /**
   * ?????????????????????
   */
  showAreaSelectorModal(filterValue) {
    this.filterValue = filterValue;
    if (!this.filterValue['filterValue']) {
      this.filterValue['filterValue'] = [];
    }
    this.treeSelectorConfig.treeNodes = this.areaNodes;
    this.areaSelectVisible = true;
  }

  /**
   * ???????????????????????????
   */
  showModal(filterValue) {
    if (this.treeNodes.length === 0) {
      this.queryDeptList().then((bool) => {
        if (bool === true) {
          this.filterValue = filterValue;
          if (!this.filterValue['filterValue']) {
            this.filterValue['filterValue'] = [];
          }
          this.treeSelectorConfig.treeNodes = this.treeNodes;
          this.isVisible = true;
        }
      });
    } else {
      this.isVisible = true;
    }
  }

  /**
   * ????????????????????????
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
   * ??????????????????????????????
   */
  public initTreeSelectorConfig() {
    this.treeSetting = {
      check: {
        enable: true,
        chkStyle: 'checkbox',
        chkboxType: {'Y': '', 'N': ''},
      },
      data: {
        simpleData: {
          enable: true,
          idKey: 'id',
          pIdKey: 'deptFatherId',
          rootPid: null
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
          title: this.facilityLanguage.parentDept, key: 'parentDepartmentName', width: 100,
        }
      ]
    };
  }

  /**
   * ???????????????????????????
   */
  public queryDeptList() {
    return new Promise((resolve, reject) => {
      this.$userService.queryAllDepartment().subscribe((result: Result) => {
        this.treeNodes = result.data || [];
        resolve(true);
      }, (error) => {
        reject(error);
      });
    });
  }

  /**
   * ????????????????????????
   */
  getAllUser() {
    this.$inspection.queryAllUser(null).subscribe((result: Result) => {
      const roleArr = result.data;
      if (roleArr) {
        roleArr.forEach(item => {
          this.roleArray.push({'label': item.userName, 'value': item.id});
        });
      }
    });
  }
}
