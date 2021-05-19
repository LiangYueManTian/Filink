import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {ActivatedRoute, Router} from '@angular/router';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';
import {Result} from '../../../../shared-module/entity/result';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {ExportParams} from '../../../../shared-module/entity/exportParams';
import {PictureViewService} from '../../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {FacilityUtilService} from '../../../facility';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';
import {getDeviceType} from '../../../facility/facility.config';

@Component({
  selector: 'app-unfinished-inspection-work-order',
  templateUrl: './unfinished-inspection-work-order.component.html',
  styleUrls: ['./unfinished-inspection-work-order.component.scss']
})
export class UnfinishedInspectionWorkOrderComponent implements OnInit {
  isVisible = false; // 已完成巡检信息列表
  refundIsVisible = false; // 退单确认弹框
  singleBackConfirmModal;  // 退单确认modal
  title; // title已完成巡检信息
  _dataSet = []; // 未完工列表数据存放
  see_dataSet = []; // 已完成列表数据存放
  schedule_dataSet = []; // 进度列表数据存放
  pageBean: PageBean = new PageBean(10, 1, 1); // 未完工列表分页
  seePageBean: PageBean = new PageBean(10, 1, 1); // 已完工列表分页
  schedulePageBean: PageBean = new PageBean(10, 1, 1); // 进度列表分页
  tableConfig: TableConfig; // 未完工列表表单配置
  seeTableConfig: TableConfig; // 已完成列表表单配置
  scheduleTableConfig: TableConfig; // 已完成列表表单配置
  public InspectionLanguage: InspectionLanguageInterface; // 国际化
  public facilityLanguage: FacilityLanguageInterface;
  queryCondition: QueryCondition = new QueryCondition();  // 查询条件
  isUnitVisible: boolean = false; // 责任人单位
  treeSelectorConfig: TreeSelectorConfig;
  treeNodes = [];
  ecTime; // 剩余天数
  status; // 工单类型
  deviceAreaName; // 巡检区域
  inspectionDeviceCount; // 巡检数量
  accountabilityDeptName; // 责任单位
  progressSpeed; // 进度
  remark; // 备注
  turnReason; // 转派原因
  returnID; // 退单ID
  withdrawID; // 撤回ID
  procId; // 区域ID
  completedWorkOrderID; // 已完成工单ID
  private filterValue: any;
  responsibleUnitIsVisible = false; // 单位选择器筛选
  selectUnitName;
  scheduleIsVisible: boolean; // 进度弹框
  patroled; // 已巡检数量
  notInspected; // 未巡检数量
  filterTreeNodes; // 过滤后的责任单位
  roleArray = []; // 获取责任人数据
  isPresence: boolean; // 判断数据是否存在
  lastDaysInputValue; // 剩余天数input值
  lastDaySelectValue = 'eq'; // 剩余天数select值
  deviceCountInputValue; // 巡检数量input值
  deviceCountSelectValue = 'eq'; // 巡检数量input值
  exportParams: ExportParams = new ExportParams(); // 导出
  WORK_ORDER_STATUS = {
    'assigned': 'assigned',   // 待指派
    'pending': 'pending',   // 待处理
    'processing': 'processing',   // 处理中
    'turnProcessing': 'turnProcessing',   // 已完成
    'singleBack': 'singleBack',   // 已退单
  };
  WORK_ORDER_STATUS_CLASS = { // 工单类型小图标样式
    'assigned': 'fiLink-assigned-w statistics-assigned-color',
    'pending': 'fiLink-processed statistics-pending-color',
    'processing': 'fiLink-processing statistics-processing-color',
    'turnProcessing': 'fiLink-processing statistics-processing-color',
    'singleBack': 'fiLink-chargeback statistics-singleBack-color',
  };
  // 单位选择器配置
  private treeSetting: any;
  @ViewChild('tplFooter') public tplFooter;
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  @ViewChild('schedule') schedule: TemplateRef<any>;
  @ViewChild('UnitNameSearch') UnitNameSearch: TemplateRef<any>;
  @ViewChild('footerTemp') footerTemp: TemplateRef<any>;
  // @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;
  @ViewChild('roleTemp') roleTemp: TemplateRef<any>;
  @ViewChild('SingleBackTemp') SingleBackTemp: TemplateRef<any>;
  @ViewChild('remainingDaysFilter') remainingDaysFilter: TemplateRef<any>;
  @ViewChild('inspectionQuantityFilter') inspectionQuantityFilter: TemplateRef<any>;

  constructor(private $nzI18n: NzI18nService,
              private $userService: UserService,
              private $inspection: InspectionService,
              private $router: Router,
              public $message: FiLinkModalService,
              private $pictureViewService: PictureViewService,
              private $activatedRoute: ActivatedRoute,
              private $facilityUtilService: FacilityUtilService,
              private $imageViewService: ImageViewService,
              private $modal: NzModalService) {
  }

  ngOnInit() {
    this.InspectionLanguage = this.$nzI18n.getLocaleData('inspection');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.getId();
    this.queryAllDeptList();
    this.initTableConfig();
    this.initTreeSelectorConfig();
    this.seeInitTableConfig();
    this.scheduleInitTableConfig();
    this.refreshData();
    // this.setExportParamsColumnInfoList();
    this.getAllUser();
  }

  /**
   * 责任单位选择结果
   * param event
   */
  selectDataChange(event) {
    const departmentList = [];
    for (let i = 0; i < event.length; i++) {
      departmentList.push({'accountabilityDept': event[i].id});
    }
    this.assignWorkOrder(this.procId, departmentList);
  }

  /**
   * 部门筛选选择结果
   */
  departmentSelectDataChange(event) {
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
  getId() {
    // 别的页面跳转过来参数拼接
    // if ('id' in this.$activatedRoute.snapshot.queryParams) {
    //   if (!this.queryCondition.filterConditions.some(item => item.filterField === 'optObjId')) {
    //     this.queryCondition.bizCondition.procIds = [this.$activatedRoute.snapshot.queryParams.id];
    //   }
    // } else if ('deviceId' in this.$activatedRoute.snapshot.queryParams) {
    //   if (!this.queryCondition.filterConditions.some(item => item.filterField === 'optObjId')) {
    //     this.queryCondition.bizCondition.deviceIds = [this.$activatedRoute.snapshot.queryParams.deviceId];
    //   }
    // } else {
    //   this.queryCondition.filterConditions = this.queryCondition.filterConditions.filter(item => item.filterField !== 'optObjId');
    // }
    if (this.$activatedRoute.snapshot.queryParams.id) {
      this.procId = this.$activatedRoute.snapshot.queryParams.id;
      this.queryCondition.bizCondition.procIds = [this.procId];
    }
    if (this.$activatedRoute.snapshot.queryParams.deviceId) {
      this.queryCondition.bizCondition.deviceIds = [this.$activatedRoute.snapshot.queryParams.deviceId];
    }
  }
  /**
   * 显示未完成工单列表数据
   */
  public refreshData() {
    this.tableConfig.isLoading = true;
    // 显示未完工列表页数据
    this.$inspection.getUnfinishedList(this.queryCondition).subscribe((result: Result) => {
      if (result.code === 0) {
        this.pageBean.Total = result.totalCount;
        this.tableConfig.isLoading = false;
        const data = result.data;
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
          item.statusClass = this.getStatusClass(item.status);
          // if (item.deviceType) {
          //   item['_deviceType'] = item.deviceType;
          //   item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
          //   item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
          // }
          this.setIconStatus(item);
        });
        this._dataSet = result.data;
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  getStatusName(status) {
    return this.InspectionLanguage[this.WORK_ORDER_STATUS[status]];
  }

  /**
   * 是否可操作(按钮显灰)
   */
  setIconStatus(item) {
    // 只有待指派能删
    item.isShowDeleteIcon = item.status === 'assigned' ? true : 'disabled';
    // 已退单不可编辑
    item.isShowEditIcon = item.status === 'singleBack' ? false : true;
    // 待处理可以撤回;
    item.isShowRevertIcon = item.status === 'pending' ? true : 'disabled';
    // 待指派可以指派
    item.isShowAssignIcon = item.status === 'assigned' ? true : 'disabled';
    // 退单确认状态为已退单   isCheckSingleBack = 0 未确认  1未确认
    item.isShowTurnBackConfirmIcon = (item.status === 'singleBack' && item.isCheckSingleBack !== 1);
  }

  /**
   * 工单类型小图标
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${this.WORK_ORDER_STATUS_CLASS[status]}`;
  }

  /**
   * 显示已完成工单列表数据
   */
  public refreshCompleteData(id?) {
    this.seeTableConfig.isLoading = true;
    this.scheduleTableConfig.isLoading = true;
    this.queryCondition.bizCondition.procId = id;
    // 初始化已完成工单列表从第一页开始显示
    this.queryCondition.pageCondition.pageNum = 1;
    this.$inspection.getUnfinishedCompleteList(this.queryCondition).subscribe((result: Result) => {
      if (result.code === 0) {
        for (let i = 0; i < result.data.data.length; i++) {
          if (result.data.data[i].result === '0') {
            result.data.data[i].result = '正常';
          } else if (result.data.data[i].result === '1') {
            result.data.data[i].result = '异常';
          }
        }
        this.seePageBean.Total = result.data.totalCount;
        this.seeTableConfig.isLoading = false;
        this.scheduleTableConfig.isLoading = false;
        this.see_dataSet = result.data.data;
        this.schedule_dataSet = result.data.data;
      }
    }, () => {
      this.seeTableConfig.isLoading = false;
      this.scheduleTableConfig.isLoading = false;
    });
  }

  /**
   * 分页显示
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 分页显示
   */
  seePageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshCompleteData();
  }

  /**
   * 分页显示
   */
  schedulePageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshCompleteData();
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
    this.responsibleUnitIsVisible = true;
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
      title: this.InspectionLanguage.assign,
      width: '400px',
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
   * 初始化未完成巡检工单列表配置
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showRowSelection: false,
      showSizeChanger: true,
      showSearchExport: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          title: this.InspectionLanguage.workOrderName, key: 'title', width: 200,
          fixedStyle: {fixedLeft: true, style: {left: '124px'}},
          configurable: false,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.InspectionLanguage.workOrderStatus, key: 'status', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'status',
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.InspectionLanguage.assigned, value: 'assigned'},
              {label: this.InspectionLanguage.pending, value: 'pending'},
              {label: this.InspectionLanguage.processing, value: 'processing'},
              {label: this.InspectionLanguage.turnProcessing, value: 'turnProcessing'},
              {label: this.InspectionLanguage.singleBack, value: 'singleBack'},
            ]
          },
          type: 'render',
          renderTemplate: this.statusTemp,
        },
        {
          title: this.InspectionLanguage.inspectionStartTime, key: 'inspectionStartTime', width: 290,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'inspectionStartTime',
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.InspectionLanguage.inspectionEndTime, key: 'inspectionEndTime', width: 290,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'inspectionEndTime',
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.InspectionLanguage.creationTime, key: 'cTime', width: 280,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'cTime',
          searchConfig: {type: 'dateRang'}
        },
        {// 剩余天数
          title: this.InspectionLanguage.daysRemaining, key: 'lastDays', width: 200,
          // pipe: 'surplusData',
          configurable: true,
          searchKey: 'lastDays',
          isShowSort: true,
          searchable: true,
          // searchConfig: {type: 'input'}
          searchConfig: {type: 'render', renderTemplate: this.remainingDaysFilter}
        },
        {// 巡检区域
          title: this.InspectionLanguage.inspectionArea, key: 'deviceAreaName', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {// 设施类型
          title: this.InspectionLanguage.facilityType, key: 'deviceTypeName', width: 200,
          // type: 'render',
          configurable: true,
          searchable: true,
          searchKey: 'deviceTypeName',
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.InspectionLanguage.patchPanel, value: '060'},
              {label: this.InspectionLanguage.opticalBox, value: '001'},
              {label: this.InspectionLanguage.manWell, value: '030'},
              {label: this.InspectionLanguage.jointClosure, value: '090'},
              {label: this.InspectionLanguage.fiberBox, value: '150'},
            ]
          },
        },
        {// 巡检数量
          title: this.InspectionLanguage.numberOfInspections, key: 'inspectionDeviceCount', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          // searchConfig: {type: 'input'}
          searchConfig: {type: 'render', renderTemplate: this.inspectionQuantityFilter}
        },
        {// 责任单位
          title: this.InspectionLanguage.responsibleUnit, key: 'accountabilityDeptName', width: 200,
          configurable: true,
          searchable: true,
          searchKey: 'accountabilityDeptList',
          searchConfig: {type: 'render', renderTemplate: this.UnitNameSearch}
        },
        {// 责任人
          title: this.InspectionLanguage.responsible, key: 'assignName', width: 200,
          configurable: true,
          searchable: true,
          searchKey: 'assigns',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.roleArray, renderTemplate: this.roleTemp}
        },
        {// 进度
          title: this.InspectionLanguage.speedOfProgress, key: 'progressSpeed', width: 200,
          configurable: true,
          isShowFilter: false,
          searchable: false,
          searchConfig: {type: 'input'},
          type: 'render',
          renderTemplate: this.schedule,
        },
        {// 备注
          title: this.InspectionLanguage.remark, key: 'remark', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {// 转派原因
          title: this.InspectionLanguage.reasonsForTransfer, key: 'turnReason', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {// 操作
          title: this.InspectionLanguage.operate, key: '', width: 200,
          configurable: false,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'operate'},
          fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [
        {
          text: '+  ' + this.InspectionLanguage.addArea,
          permissionCode: '06-1-1-1',
          handle: (currentIndex) => {
            this.addInspectionWorkOrder('add', null);
          }
        },
        {
          text: this.InspectionLanguage.delete,
          permissionCode: '06-1-1-3',
          btnType: 'danger',
          canDisabled: true,
          needConfirm: true,
          className: 'table-top-delete-btn',
          iconClassName: 'icon-delete',
          handle: (currentIndex) => {
            const arr = [];
            currentIndex.map(item => {
              arr.push(item.procId);
            });
            this.checkData(arr).then((bool) => {
              if (bool === true) {
                const params = new Array();
                currentIndex.forEach((item) => {
                  params.push(item.procId);
                });
                this.$inspection.deleteUnfinishedOrderByIds(params).subscribe((result: Result) => {
                  if (result.code === 0) {
                    this.$message.success(result.msg);
                    this.refreshData();
                  } else {
                    this.$message.error(result.msg);
                  }
                });
              } else {
                this.$message.error(this.InspectionLanguage.thereAreDeletedWorkOrders);
                this.refreshData();
              }
            });
          }
        },
      ],
      operation: [
        {
          text: this.InspectionLanguage.turnBackConfirm,
          permissionCode: '06-1-1-6',
          key: 'isShowTurnBackConfirmIcon',
          className: 'fiLink-turn-back-confirm',
          handle: (currentIndex) => {
            // this.refundIsVisible = true;
            // this.title = this.InspectionLanguage.turnBackConfirm;
            this.returnID = currentIndex.procId;
            this.openSingleBackConfirmModal();
          }
        },
        {
          text: this.InspectionLanguage.edit,
          permissionCode: '06-1-1-2',
          key: 'isShowEditIcon',
          className: 'fiLink-edit',
          disabledClassName: 'fiLink-edit disabled-icon',
          handle: (currentIndex) => {
            const arr = [currentIndex.procId];
            this.checkData(arr).then((bool) => {
              if (bool === true) {
                this.addInspectionWorkOrder('update', currentIndex.procId, currentIndex.status);
              } else {
                this.$message.error(this.InspectionLanguage.theWorkOrderDoesNotExist);
                this.refreshData();
              }
            });
          }
        },
        {
          text: this.InspectionLanguage.withdraw,
          permissionCode: '06-1-1-4',
          key: 'isShowRevertIcon',
          className: 'fiLink-revert',
          disabledClassName: 'fiLink-revert disabled-icon',
          handle: (currentIndex) => {
            this.withdrawID = currentIndex.procId;
            this.withdrawWorkOrder();
          }
        },
        {
          text: this.InspectionLanguage.assign,
          permissionCode: '06-1-1-5',
          key: 'isShowAssignIcon',
          className: 'fiLink-assigned',
          disabledClassName: 'fiLink-assigned disabled-icon',
          handle: (currentIndex) => {
            this.queryDeptList(currentIndex.deviceAreaId);
            this.procId = currentIndex.procId;
          }
        },
        {
          text: this.InspectionLanguage.viewDetail,
          permissionCode: '06-1-1-7',
          className: 'fiLink-view-detail',
          handle: (currentIndex) => {
            this.isVisible = true;
            this.completedWorkOrderID = currentIndex.procId;
            const id = currentIndex.procId;
            this.refreshCompleteData(id);
            this.title = this.InspectionLanguage.completeInspectionInformation;
          }
        }, {
          text: this.InspectionLanguage.delete,
          permissionCode: '06-1-1-3',
          key: 'isShowDeleteIcon',
          canDisabled: true,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          disabledClassName: 'fiLink-delete disabled-red-icon',
          handle: (currentIndex) => {
            const arr = [currentIndex.procId];
            this.checkData(arr).then((bool) => {
              if (bool === true) {
                const params = [currentIndex.procId];
                this.$inspection.deleteUnfinishedOrderByIds(params).subscribe(res => {
                  if (res['code'] === 0) {
                    this.$message.success(res['msg']);
                    this.pageBean.pageIndex = 1;
                    this.refreshData();
                  } else if (res['code'] === 120270) {
                    this.$message.info(res['msg']);
                  } else {
                    this.$message.error(res['msg']);
                  }
                });
              } else {
                this.$message.error(this.InspectionLanguage.thisWorkOrderHasBeenDeleted);
                this.refreshData();
              }
            });
          }
        },
      ],
      sort: (event) => {
        this.handleSort(event);
        this.refreshData();
      },
      handleSearch: (event) => {
        this.lastDaysInputValue = event.lastDays;
        this.deviceCountInputValue = event.inspectionDeviceCount;
        console.log(event);
        if (!event.accountabilityDeptList) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        if (!event.lastDays) {
          this.lastDaysInputValue = '';
          this.queryCondition.bizCondition.lastDays = '';
          this.lastDaySelectValue = 'eq';
        }
        if (!event.inspectionDeviceCount) {
          this.deviceCountInputValue = '';
          this.queryCondition.bizCondition.inspectionDeviceCount = '';
          this.deviceCountSelectValue = 'eq';
        }
        this.handleSearch(event);
        this.refreshData();
      },
      handleExport: (event) => {
        this.exportParams.columnInfoList = event.columnInfoList;
        this.exportParams.columnInfoList.forEach(item => {
          if (item.propertyName === 'status' || item.propertyName === 'inspectionStartTime' ||
            item.propertyName === 'inspectionEndTime' || item.propertyName === 'cTime') {
            item.isTranslation = 1;
          }
        });
        this.createExportParams(event);
        this.$inspection.unfinishedExport(this.exportParams).subscribe((result: Result) => {
          if (result.code === 0) {
            this.$message.success(result.msg);
          } else {
            this.$message.error(result.msg);
          }
        }, () => {
        });
      }
    };
  }

  /**
   * 检查数据是否存在
   */
  checkData(ids) {
    return new Promise((resolve, reject) => {
      this.$inspection.getUnfinishedList(this.queryCondition).subscribe((result: Result) => {
        for (let j = 0; j < ids.length; j++) {
          for (let i = 0; i < result.data.length; i++) {
            if (result.data[i].procId === ids[j]) {
              this.isPresence = true;
              break;
            } else {
              this.isPresence = false;
            }
          }
        }
        if (this.isPresence === true) {
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
   * 排序
   * param event
   */
  handleSort(event) {
    this.queryCondition.sortCondition.sortField = event.sortField;
    this.queryCondition.sortCondition.sortRule = event.sortRule;
  }
  /**
   * 筛选
   */
  handleSearch(event) {
    this.queryCondition.bizCondition = this.setBizCondition(event);
    if (this.lastDaysInputValue || this.lastDaySelectValue) {
      this.queryCondition.bizCondition.lastDays = this.lastDaysInputValue;
      this.queryCondition.bizCondition.lastDaysOperate = this.lastDaySelectValue;
    }
    if (this.deviceCountInputValue || this.deviceCountSelectValue) {
      this.queryCondition.bizCondition.inspectionDeviceCount = this.deviceCountInputValue;
      this.queryCondition.bizCondition.inspectionDeviceCountOperate = this.deviceCountSelectValue;
    }
    this.setPageCondition(event);
  }

  /**
   * 设置查询条件
   */
  setPageCondition(event) {
    // this.pageBean = new PageBean(this.pageBean.pageSize, 1, 0);
    this.queryCondition.pageCondition.pageNum = 1;
  }

  /**
   * 打开退单确认modal
   */
  openSingleBackConfirmModal() {
    this.singleBackConfirmModal = this.$modal.create({
      nzTitle: '退单确认',
      nzContent: this.SingleBackTemp,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzFooter: this.footerTemp,
    });
  }

  /**
   * 退单确认
   * param ids
   */
  singleBackConfirm(id) {
    this.$inspection.singleBackToConfirm(id).subscribe((result: Result) => {
      if (result.code === 0) {
        this.closeSingleBackConfirmModal();
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 关闭退单确认modal
   */
  closeSingleBackConfirmModal() {
    this.singleBackConfirmModal.destroy();
  }


  /**
   * 重新生成
   */
  regenerate() {
    this.closeSingleBackConfirmModal();
    this.addInspectionWorkOrder('updates', this.returnID, 'assigned');
  }

  /**
   * 生成导出条件
   */
  createExportParams(event) {
    // this.queryCondition.sortCondition = new SortCondition();
    this.exportParams.queryCondition = this.queryCondition;
    if (event.selectItem.length > 0) {
      this.exportParams.queryCondition.bizCondition.procIds = event.selectItem.map(item => item.procId);
    }
    this.exportParams.excelType = event.excelType;
  }

  // /**
  //  * 设置导出列
  //  */
  // setExportParamsColumnInfoList() {
  //   this.exportParams.columnInfoList = [
  //     {
  //       columnName: this.InspectionLanguage.workOrderName,
  //       propertyName: 'title'
  //     },
  //     {
  //       columnName: this.InspectionLanguage.workOrderStatus,
  //       propertyName: 'status',
  //       isTranslation: 1
  //     },
  //     {
  //       columnName: this.InspectionLanguage.inspectionStartTime,
  //       propertyName: 'inspectionStartTime',
  //       isTranslation: 1
  //     },
  //     {
  //       columnName: this.InspectionLanguage.inspectionEndTime,
  //       propertyName: 'inspectionEndTime',
  //       isTranslation: 1
  //     },
  //     {
  //       columnName: this.InspectionLanguage.creationTime,
  //       propertyName: 'cTime',
  //       isTranslation: 1
  //     },
  //     {
  //       columnName: this.InspectionLanguage.daysRemaining,
  //       propertyName: 'lastDays',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.inspectionArea,
  //       propertyName: 'deviceAreaName',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.facilityType,
  //       propertyName: 'deviceTypeName',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.numberOfInspections,
  //       propertyName: 'inspectionDeviceCount',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.responsibleUnit,
  //       propertyName: 'accountabilityDeptName',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.responsible,
  //       propertyName: 'updateUserName',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.speedOfProgress,
  //       propertyName: 'progressSpeed',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.remark,
  //       propertyName: 'remark',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.reasonsForTransfer,
  //       propertyName: 'turnReason',
  //     },
  //   ];
  // }

  /**
   * 筛选下拉
   * param event
   */
  setBizCondition(event) {
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
    if (_bizCondition.cTime) {
      _bizCondition.cTimes = CommonUtil.deepClone(_bizCondition.cTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.cTime;
    }
    if (_bizCondition.deviceTypeName) {
      _bizCondition.deviceTypes = CommonUtil.deepClone(_bizCondition.deviceTypeName);
      delete _bizCondition.deviceTypeName;
    }
    return _bizCondition;
  }

  /**
   * 指派工单
   * param ids
   */
  assignWorkOrder(id, modal) {
    const data = {
      'procId': id,
      'departmentList': modal
    };
    this.$inspection.assignedUnfinished(data).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.refreshData();
      } else if (data.departmentList.length === 0) {
        this.$message.warning('请选择单位!');
      } else {
        this.$message.error('工单指派失败!');
      }
    }, () => {
    });
  }

  /**
   * 未完工工单撤回
   */
  withdrawWorkOrder() {
    const pid = {'procId': this.withdrawID};
    this.$inspection.unfinishedWorkOrderWithdrawal(pid).subscribe((result: Result) => {
      if (result.code === 0) {
        this.refreshData();
        this.close();
        this.$message.success(result.msg);
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
    });
  }

  /**
   * 初始化已完成巡检信息列表配置
   */
  private seeInitTableConfig() {
    this.seeTableConfig = {
      isDraggable: false,
      isLoading: false,
      showSearchSwitch: true,
      showRowSelection: false,
      showSizeChanger: true,
      showSearchExport: false,
      notShowPrint: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {// 巡检设施
          title: this.InspectionLanguage.inspectionFacility, key: 'deviceName', width: 200,
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {// 巡检结果
          title: this.InspectionLanguage.inspectionResults, key: 'result', width: 200,
          searchable: true, configurable: true, isShowSort: true,
          searchConfig: {
            type: 'select',
            selectInfo: [
              {label: this.InspectionLanguage.normal, value: '0'},  // 正常
              {label: this.InspectionLanguage.abnormal, value: '1'},   // 异常
            ]
          },
        },
        {// 异常详情
          title: this.InspectionLanguage.exceptionallyDetailed, key: 'exceptionDescription', width: 200,
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {// 巡检时间
          title: this.InspectionLanguage.inspectionTime, key: 'inspectionTime', width: 200,
          pipe: 'date',
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'dateRang'}
        },
        {// 责任人
          title: this.InspectionLanguage.responsible, key: 'updateUserName', width: 200,
          configurable: true,
          searchable: true,
          searchKey: 'updateUser',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.roleArray, renderTemplate: this.roleTemp}
        },
        {// 资源匹配情况
          title: this.InspectionLanguage.matchingOfResources, key: 'resourceMatching', width: 200,
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {// 关联图片
          title: this.InspectionLanguage.relatedPictures, searchable: true,
          searchConfig: {type: 'operate'}, width: 200, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: true,
      showSearch: false,
      operation: [
        {
          text: this.InspectionLanguage.viewDetail,
          className: 'fiLink-view-photo',
          handle: (currentIndex) => {
            this.getPicUrlByAlarmIdAndDeviceId(currentIndex.procId, currentIndex.deviceId);
          }
        },
      ],
      sort: (event) => {
        this.handleSort(event);
        this.refreshCompleteData(this.completedWorkOrderID);
      },
      handleSearch: (event) => {
        if (!event.accountabilityUnit) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        this.handleSearch(event);
        this.refreshCompleteData(this.completedWorkOrderID);
      }
    };
  }

  /**
   * 初始化已完成巡检信息列表配置
   */
  private scheduleInitTableConfig() {
    this.scheduleTableConfig = {
      isDraggable: false,
      isLoading: false,
      showSearchSwitch: true,
      showRowSelection: false,
      showSizeChanger: true,
      showSearchExport: false,
      notShowPrint: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {// 巡检设施
          title: this.InspectionLanguage.inspectionFacility, key: 'deviceName', width: 200,
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {// 巡检结果
          title: this.InspectionLanguage.inspectionResults, key: 'result', width: 200,
          searchable: true, configurable: true, isShowSort: true,
          searchConfig: {
            type: 'select',
            selectInfo: [
              {label: this.InspectionLanguage.normal, value: '0'},  // 正常
              {label: this.InspectionLanguage.abnormal, value: '1'},   // 异常
            ]
          },
        },
        {// 异常详情
          title: this.InspectionLanguage.exceptionallyDetailed, key: 'exceptionDescription', width: 200,
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {// 巡检时间
          title: this.InspectionLanguage.inspectionTime, key: 'inspectionTime', width: 200,
          pipe: 'date',
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'dateRang'}
        },
        {// 责任人
          title: this.InspectionLanguage.responsible, key: 'updateUserName', width: 200,
          configurable: true,
          searchable: true,
          searchKey: 'updateUser',
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.roleArray, renderTemplate: this.roleTemp}
        },
        {// 资源匹配情况
          title: this.InspectionLanguage.matchingOfResources, key: 'resourceMatching', width: 200,
          searchable: true,
          configurable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {// 关联图片
          title: this.InspectionLanguage.relatedPictures, searchable: true,
          searchConfig: {type: 'operate'}, width: 200, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: true,
      showSearch: false,
      operation: [
        {
          text: this.InspectionLanguage.viewDetail,
          className: 'fiLink-view-photo',
          handle: (currentIndex) => {
            this.getPicUrlByAlarmIdAndDeviceId(currentIndex.procId, currentIndex.deviceId);
          }
        },
      ],
      sort: (event) => {
        this.handleSort(event);
        this.refreshCompleteData(this.completedWorkOrderID);
      },
      handleSearch: (event) => {
        if (!event.accountabilityUnit) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        this.handleSearch(event);
        this.refreshCompleteData(this.completedWorkOrderID);
      }
    };
  }

  /**
   * 关联图片
   */
  getPicUrlByAlarmIdAndDeviceId(procId, deviceId) {
    this.$pictureViewService.getPicUrlByAlarmIdAndDeviceId(procId, deviceId).subscribe((result: Result) => {
      if (result.code === 0) {
        if (result.data.length === 0) {
          this.$message.warning('暂无图片');
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
   *添加/修改路由跳转
   */
  addInspectionWorkOrder(type, procId?, status?) {
    return this.$router.navigate([`/business/work-order/inspection/unfinished/${type}`], {queryParams: {procId: procId, status: status}});
  }

  /**
   * 隐藏弹框
   */
  close() {
    this.isVisible = false;
    this.refundIsVisible = false;
    this.scheduleIsVisible = false;
    // this.withdrawIsVisible = false;
  }

  /**
   * 查询所有的单位
   */
  private queryAllDeptList() {
    this.$userService.queryAllDepartment().subscribe((result: Result) => {
      this.treeNodes = result.data || [];
    });
  }

  /**
   * 根据区域ID查询责任单位
   */
  queryDeptList(id?) {
    // const startLoad = new Date().getTime();
    const ids = [id];
    // console.log('接口加载时间:' + this.latencyLoad);
    this.$message.loading('正在加载责任单位信息...');
    this.$inspection.queryResponsibilityUnit(ids).subscribe((result: Result) => {
      const deptData = [];
      for (let i = 0; i < result.data.length; i++) {
        if (result.data[i].hasThisArea === true) {
          deptData.push(result.data[i]);
        }
      }
      this.filterTreeNodes = deptData;
      this.treeSelectorConfig.treeNodes = this.filterTreeNodes;
      this.isUnitVisible = true;
      // const nowLoad = new Date().getTime();
      // this.latencyLoad = nowLoad - startLoad;
    });
  }

  /**
   * 进度显示已完工列表
   */
  showCompleted(data) {
    this.refreshCompleteData(data.procId);
    this.title = this.InspectionLanguage.completeInspectionInformation;
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
   * 获得所有的责任人
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

