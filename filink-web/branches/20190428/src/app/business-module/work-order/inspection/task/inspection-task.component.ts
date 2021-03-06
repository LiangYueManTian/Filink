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
import {getDeviceType} from '../../../facility/facility.config';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../../facility';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {PictureViewService} from '../../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';
import {AreaConfig} from '../../../../shared-module/component/alarm/alarmSelectorConfig';

@Component({
  selector: 'app-inspection-task',
  templateUrl: './inspection-task.component.html',
  styleUrls: ['./inspection-task.component.scss'],
})
export class InspectionTaskComponent implements OnInit {
  _dataSet = [];  // 巡检任务列表数据
  pageBean: PageBean = new PageBean(10, 1, 1); // 巡检任务列表页分页
  queryCondition: QueryCondition = new QueryCondition(); // 巡检分页条件
  associatedWorkOrderQueryCondition: QueryCondition = new QueryCondition(); // 关联工单分页条件
  completedQueryCondition: QueryCondition = new QueryCondition(); // 已完成巡检信息分页条件
  tableConfig: TableConfig; // 表格配置
  exportParams: ExportParams = new ExportParams(); // 导出
  public language: InspectionLanguageInterface; // 国际化
  facilityLanguage: FacilityLanguageInterface;
  areaName = ''; // 区域名称
  private areaNodes: any = []; // 区域数据存放
  areaSelectVisible: boolean = false;  // 区域弹框
  areaSelectorConfig: any = new TreeSelectorConfig(); // 区域配置
  hasData; // 检验是否有这条数据
  // 关联工单列表配置
  public isAssociatedWorkOrderVisible = false; // 关联工单弹框显示隐藏
  inspectionTaskTitle; // 关联工单标题
  associatedWorkOrder_dataSet = []; // 关联工单列表数据
  associatedWorkOrderPageBean: PageBean = new PageBean(10, 1, 1);  // 关联工单列表分页
  associatedWorkOrderTableConfig: TableConfig; // 关联工单列表配置
  id; // 关联工单对应的巡检任务id
  procId; // 关联工单id
  // 责任单位相关
  isVisible: boolean = false; // 责任单位的显示隐藏
  treeSelectorConfig: TreeSelectorConfig; // 责任单位选中器配置iid
  treeSetting; // 初始化单位选择器配置
  private filterValue: any;
  selectUnitName; // 选中责任单位名称
  treeNodes = [];
  scheduleIsVisible: boolean; // 进度弹框
  title; // title已完成巡检信息
  schedule_dataSet = []; // 进度列表数据存放
  schedulePageBean: PageBean = new PageBean(10, 1, 1); // 进度列表分页
  scheduleTableConfig: TableConfig; // 进度列表表单配置
  patroled; // 已巡检数量
  notInspected; // 未巡检数量
  roleArray = []; // 责任人存放数组
  areaConfig: AreaConfig; // 区域初始化配置
  // 工单状态国际化
  WORK_ORDER_STATUS = {
    'assigned': 'assigned',   // 待指派
    'pending': 'pending',   // 待处理
    'processing': 'processing',   // 处理中
    'completed': 'completed',   // 已完成
    'singleBack': 'singleBack',   // 已退单
    'turnProcessing': 'turnProcessing'  // 已转派'
  };
  // 工单状态对应的图标
  WORK_ORDER_STATUS_CLASS = {
    'assigned': 'fiLink-assigned-w statistics-assigned-color',
    'pending': 'fiLink-processed statistics-pending-color',
    'processing': 'fiLink-processing statistics-processing-color',
    'completed': 'fiLink-completed statistics-completed-color',
    'singleBack': 'fiLink-chargeback statistics-singleBack-color',
    'turnProcessing': 'fiLink-processing statistics-processing-color',
  };
  // 巡检任务状态转化
  INSPECTION_TASK_STATUS = {
    '1': 'notInspected',    // 未巡检
    '2': 'duringInspection', // 巡检中
    '3': 'completed',  // 已完成
  };
  // 巡检任务类型转化
  INSPECTION_TASK_TYPE = {
    '1': 'routineInspection',  // 例行巡检
  };
  // 已完成工单巡检结果转化显示
  WORK_ORDER_RESULT = {
    '0': 'normal',   // 正常
    '1': 'abnormal',   // 异常
  };
  // 区域
  areaList = {
    ids: [],
    name: ''
  };
  // 巡检周期筛选
  taskPeriodInputValue; // 巡检周期输入值
  taskPeriodSelectedValue = 'eq';
  // 期望用时筛选
  procPlanDateInputValue; // 期待完工用时输入值
  procPlanDateSelectedValue = 'eq';
  // 设施总数筛选
  deviceCountInputValue; // 设施总数筛选输入值
  deviceCountSelectedValue = 'eq';
  @ViewChild('templateStatus') templateStatusTemp: TemplateRef<any>; // 启用状态
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>; // 关联工单列中工单状态
  @ViewChild('UnitNameSearch') unitNameSearch: TemplateRef<any>; // 责任单位
  @ViewChild('areaSelector') private areaSelectorTemp; // 巡检区域
  @ViewChild('schedule') schedule: TemplateRef<any>; // 进度
  @ViewChild('taskPeriodPeTemp') taskPeriodPeTemp: TemplateRef<any>; // 巡检周期
  @ViewChild('procPlanDateTemp') procPlanDateTemp: TemplateRef<any>;     // 期待完工用时
  @ViewChild('deviceCountTemp') deviceCountTemp: TemplateRef<any>;  // 设施总数
  constructor(public $nzI18n: NzI18nService,
              private $activatedRoute: ActivatedRoute,
              private $inspection: InspectionService,
              private $facilityUtilService: FacilityUtilService,
              private $router: Router,
              private $message: FiLinkModalService,
              private $userService: UserService,
              private $pictureViewService: PictureViewService,
              private $imageViewService: ImageViewService,
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('inspection');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.initTableConfigAssociatedWorkOrder();
    this.refreshData();
    this.queryDeptList();
    this.initTreeSelectorConfig();
    this.scheduleInitTableConfig();
    this.getAllUser();
    this.initAreaConfig();  // 区域

  }


  /**
   *获取巡检任务列表
   */
  public refreshData() {
    this.tableConfig.isLoading = true;
    // 别的页面跳转过来id拼接
    if ('id' in this.$activatedRoute.snapshot.queryParams) {
      if (!this.queryCondition.filterConditions.some(item => item.filterField === 'optObjId')) {
        this.queryCondition.bizCondition.inspectionTaskIds = [this.$activatedRoute.snapshot.queryParams.id];
      }
    } else {
      this.queryCondition.filterConditions = this.queryCondition.filterConditions.filter(item => item.filterField !== 'optObjId');
    }
    this.$inspection.getWorkOrderList(this.queryCondition).subscribe((result: Result) => {
      this.pageBean.Total = result.totalCount;
      this.tableConfig.isLoading = false;
      const data = result.data;
      data.forEach(item => {
        item.inspectionTaskStatus = this.getInspectionTaskStatus(item.inspectionTaskStatus);
        item.inspectionTaskType = this.getInspectionTaskType(item.inspectionTaskType);
        this.setIconStatus(item);
      });
      this._dataSet = result.data;
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 获取的巡检任务状态
   */
  getInspectionTaskStatus(inspectionTaskStatus) {
    return this.language[this.INSPECTION_TASK_STATUS[inspectionTaskStatus]];
  }

  /**
   * 获取巡检任务类型
   */
  getInspectionTaskType(inspectionTaskType) {
    return this.language[this.INSPECTION_TASK_TYPE[inspectionTaskType]];
  }

  /**
   * 是否可操作编辑(按钮显灰)
   */
  setIconStatus(item) {
    // 未巡检状态可编辑
    item.isShowEditIcon = item.inspectionTaskStatus !== `${this.language.completed}` ? true : 'disabled';
    // item.isShowDeleteIcon = item.inspectionTaskStatus === `${this.language.duringInspection}` ? 'disabled' : true;
  }

  /**
   *巡检任务分页
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   *关联工单分页
   */
  pageChangeAssociatedWorkOrder(event) {
    this.associatedWorkOrderQueryCondition.pageCondition.pageNum = event.pageIndex;
    this.associatedWorkOrderQueryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshAssociatedWorkOrderData();
  }

  /**
   * 进度已完工列表分页显示
   */
  schedulePageChange(event) {
    this.completedQueryCondition.pageCondition.pageNum = event.pageIndex;
    this.completedQueryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshCompleteData();
  }

  /**
   * 显示关联工单列表数据
   */
  private refreshAssociatedWorkOrderData() {
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
          item.statusName = this.getStatusName(item.status);
          item.statusClass = this.getStatusClass(item.status);
        });
        this.associatedWorkOrder_dataSet = result.data;
      }
    }, () => {
      this.associatedWorkOrderTableConfig.isLoading = false;
    });
  }

  /**
   * 获取工单类型状态
   */
  getStatusName(status) {
    return this.language[this.WORK_ORDER_STATUS[status]];
  }

  /**
   * 工单类型小图标
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${this.WORK_ORDER_STATUS_CLASS[status]}`;
  }

  /**
   * 巡检任务列表筛选
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
   * 设置巡检任务列表查询条件
   */
  setPageCondition(event) {
    this.queryCondition.pageCondition.pageNum = 1;
  }

  /**
   * 筛选巡检任务列表下拉
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
   * 关联工单列表筛选
   * param event
   */
  handleSearchAssociatedWorkOrder(event) {
    this.associatedWorkOrderQueryCondition.bizCondition = this.setBizConditionAssociatedWorkOrder(event);
    this.setPageConditionAssociatedWorkOrder(event);
  }

  /**
   * 设置关联工单列表查询条件
   */
  setPageConditionAssociatedWorkOrder(event) {
    this.associatedWorkOrderQueryCondition.pageCondition.pageNum = 1;
  }

  /**
   * 关联工单筛选下拉
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
   * 完成巡检记录列表筛选
   * param event
   */
  handleSearchCompletedWorkOrder(event) {
    this.completedQueryCondition.bizCondition = this.setBizConditionCompletedWorkOrder(event);
    this.setPageConditionCompletedWorkOrder(event);
  }

  /**
   * 设置完成巡检记录列表查询条件
   */
  setPageConditionCompletedWorkOrder(event) {
    this.completedQueryCondition.pageCondition.pageNum = 1;
  }

  /**
   * 完成巡检记录列表列表下拉
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
   * 区域选择结果
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
   *区域配置
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
   *初始化表单配置
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      // showRowSelection: true,
      showSizeChanger: true,
      showSearchExport: true,
      searchReturnType: 'object',
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        { // 巡检任务名称
          title: this.language.inspectionTaskName, key: 'inspectionTaskName',
          fixedStyle: {fixedRight: true, style: {left: '124px'}}, width: 200,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        { // 巡检任务类型
          title: this.language.inspectionTaskType, key: 'inspectionTaskType', width: 200,
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: [
              {label: this.language.routineInspection, value: '1'},  // 例行巡检
            ]
          },
        },
        { // 巡检周期
          title: this.language.inspectionCycle, key: 'taskPeriod', width: 200,
          isShowSort: true,
          searchKey: 'taskPeriod',
          configurable: true,
          searchable: true,
          // searchConfig: {type: 'input'}
          searchConfig: {
            type: 'render',
            renderTemplate: this.taskPeriodPeTemp,
          }
        },
        { // 巡检工单期望用时
          title: this.language.inspectionWorkOrderExpectedTime, key: 'procPlanDate', width: 200,
          isShowSort: true, configurable: true, searchable: true,
          // searchConfig: {type: 'input'}
          searchConfig: {
            type: 'render',
            renderTemplate: this.procPlanDateTemp,
          }
        },
        { //  起始时间
          title: this.language.startTime, key: 'startTime', width: 200,
          pipe: 'date', isShowSort: true, configurable: true,
          searchable: true, searchConfig: {type: 'dateRang'},
        },
        { // 结束时间
          title: this.language.endTime, key: 'endTime', width: 200,
          pipe: 'date', isShowSort: true, configurable: true,
          searchable: true, searchConfig: {type: 'dateRang'},
        },
        { // 巡检区域
          title: this.language.inspectionArea, key: 'inspectionAreaName', width: 200,
          configurable: true, searchable: true,
          searchKey: 'inspectionAreaIds',
          searchConfig: {
            type: 'render',
            selectInfo: this.areaList.ids,
            renderTemplate: this.areaSelectorTemp },
        },
        { // 巡检设施总数
          title: this.language.totalInspectionFacilities, key: 'inspectionDeviceCount', width: 200,
          configurable: true, isShowSort: true, searchable: true,
          searchConfig: {
            type: 'render',
            renderTemplate: this.deviceCountTemp,
          }
        },
        { // 责任单位
          title: this.language.responsibleUnit, key: 'accountabilityDeptName', width: 200,
          configurable: true, searchable: true, searchKey: 'deptIds',
          searchConfig: {type: 'render', renderTemplate: this.unitNameSearch}
        },
        { // 巡检任务状态
          title: this.language.patrolTaskStatus, key: 'inspectionTaskStatus', width: 200,
          isShowSort: true, configurable: true, searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: [
              {label: this.language.notInspected, value: '1'},   // 未巡检
              {label: this.language.duringInspection, value: '2'}, // 巡检中
              {label: this.language.completed, value: '3'},      // 已完成
            ]
          },
        },
        { // 启用状态
          title: this.language.enabledState, key: 'isOpen', width: 200, minWidth: 110,
          isShowSort: true, configurable: true, searchable: true,
          type: 'render', renderTemplate: this.templateStatusTemp,
          searchConfig: {
            type: 'select',
            selectInfo: [
              {label: this.language.disable, value: '0'},  // 禁用
              {label: this.language.enable, value: '1'},   // 启用
            ]
          },
        },
        { // 操作
          title: this.language.operate,
          fixedStyle: {fixedRight: true, style: {right: '0px'}}, width: 150,
          isShowSort: true, searchable: true, searchConfig: {type: 'operate'},
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
        { // 批量删除
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
        { // 编辑
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
        { // 关联工单
          text: this.language.associatedWorkOrder, className: 'fiLink-work-order-m',
          permissionCode: '06-1-2-3',
          handle: (currentIndex) => {
            const id = [];
            id.push(currentIndex.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true) {
                // 打开关联工单弹框
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
        { // 巡检设施
          text: this.language.inspectionFacility, className: 'fiLink-inspection',
          permissionCode: '06-1-2-4',
          handle: (currentIndex) => {
            const id = [];
            id.push(currentIndex.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true) {
                // 巡检任务 关联设施跳转
                this.navigateToDetail('/business/work-order/inspection/task/inspectionDevice',
                  {queryParams: {inspectionTaskId: currentIndex.inspectionTaskId}});
              } else {
                this.$message.error(`${this.language.theInspectionTaskNoLongerExistsTip}`);
                this.refreshData();
              }
            });
          }
        },
        { // 单行删除
          text: this.language.delete,
          permissionCode: '06-1-2-5',
          className: 'fiLink-delete red-icon',
          canDisabled: true,
          needConfirm: true,
          handle: (data) => {
            const id = [];
            id.push(data.inspectionTaskId);
            this.checkData(id).then((bool) => {
              if (bool === true ) {
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
        { // 批量开启巡检任务状态
          text: this.language.enable,
          permissionCode: '06-1-2-6',
          canDisabled: true,
          handle: (data) => {
            if (data.length > 0) {
              const arrId = data.map(item => item.inspectionTaskId);
              this.checkData(arrId).then((bool) => {
                if (bool === true ) {
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
        { // 批量关闭巡检任务状态
          text: this.language.disable,
          permissionCode: '06-1-2-7',
          canDisabled: true,
          handle: (data) => {
            if (data.length > 0) {
              const allId = data.map(item => item.inspectionTaskId);
              this.checkData(allId).then((bool) => {
                if (bool === true ) {
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
      // 排序
      sort: (event: SortCondition) => {
        this.handleSort(event);
        this.refreshData();
      },
      // 筛选
      handleSearch: (event) => {
        this.taskPeriodInputValue = event.taskPeriod;
        this.procPlanDateInputValue = event.procPlanDate;
        this.deviceCountInputValue = event.inspectionDeviceCount;
        // 没有值的时候重置已选数据
        if (!event.deptIds) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        if (!event.inspectionAreaIds) {
          this.areaList = {
            ids: [],
            name: ''
          };
          // 区域
          this.initAreaConfig();
        } // 周期
        if (!event.taskPeriod) {
          this.taskPeriodInputValue = '';
          this.queryCondition.bizCondition.taskPeriod = '';
          this.taskPeriodSelectedValue = 'eq';
        }// 期望用时
        if (!event.procPlanDate) {
          this.procPlanDateInputValue = '';
          this.queryCondition.bizCondition.procPlanDate = '';
          this.procPlanDateSelectedValue = 'eq';
        } // 设施总数
        if (!event.inspectionDeviceCount) {
          this.deviceCountInputValue = '';
          this.queryCondition.bizCondition.inspectionDeviceCount = '';
          this.deviceCountSelectedValue = 'eq';
        }
        this.handleSearch(event);
        this.refreshData();
      },
      // 导出
      handleExport: (event) => {
        // 设置导出列
        this.exportParams.columnInfoList = event.columnInfoList;
        this.exportParams.columnInfoList.forEach(item => {
          if (item.propertyName === 'inspectionTaskType' || item.propertyName === 'startTime' ||
            item.propertyName === 'endTime' || item.propertyName === 'inspectionTaskStatus' || item.propertyName === 'isOpen') {
            item.isTranslation = 1;
          }
        });
        this.handleExport(event);
      }
    };
  }
  /**
   * 启用状态操作
   */
  clickSwitch(data) {
    const id = [];
    id.push(data.inspectionTaskId);
    this.checkData(id).then((bool) => {
      if (bool === true ) {
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
   * 启用状态操作模板
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
   * 禁用状态操作模板
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
   * 检验数据是否存在
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
   * 打开区域选择器
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
   * 获得所有的责任单位
   */
  private queryDeptList() {
    this.$userService.queryAllDepartment().subscribe((result: Result) => {
      this.treeNodes = result.data || [];
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
  /**
   * 巡检任务删除模板
   */
  deleteTemplate(inspectionTask) {
    this.$inspection.deleteWorkOrderByIds(inspectionTask).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        // 删除之后返回到第一页
        this.pageBean.pageIndex = 1;
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    });
  }

  /**
   * 关联工单初始化表单
   */
  private initTableConfigAssociatedWorkOrder() {
    this.associatedWorkOrderTableConfig = {
      isDraggable: true,
      showSearchSwitch: true,
      isLoading: false,
      showSizeChanger: true,
      // showRowSelection: true,
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
            selectInfo: [
              {label: this.language.assigned, value: 'assigned'},   // 待指派
              {label: this.language.pending, value: 'pending'}, // 待处理
              {label: this.language.processing, value: 'processing'},      // 处理中
              {label: this.language.completed, value: 'completed'},      // 已完成
              {label: this.language.singleBack, value: 'singleBack'}, // 已退单
              {label: this.language.turnProcessing, value: 'turnProcessing'}, // 已转派
            ]
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
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.responsibleUnit, key: 'accountabilityDeptName', width: 200,
          searchable: true, configurable: true,
          searchKey: 'accountabilityDeptList',
          searchConfig: {type: 'render', renderTemplate: this.unitNameSearch}
        },
        { // 责任人
          title: this.language.responsible, key: 'assignName', width: 200,
          searchKey: 'assigns',
          configurable: true, searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: this.roleArray,
            // renderTemplate: this.roleTemp
          }
        },
        { // 进度
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
      // 排序
      sort: (event: SortCondition) => {
        this.associatedWorkOrderQueryCondition.sortCondition.sortField = event.sortField;
        this.associatedWorkOrderQueryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshAssociatedWorkOrderData();
      },
      // 筛选
      handleSearch: (event) => {
        this.associatedWorkOrderQueryCondition.bizCondition = event;
        // 没有值的时候重置已选数据
        if (!event.accountabilityDeptList) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        this.handleSearchAssociatedWorkOrder(event);
        this.refreshAssociatedWorkOrderData();
      },
    };
  }

  /**
   * 进度：显示已完成工单列表数据
   */
  public refreshCompleteData(procId?) {
    this.scheduleTableConfig.isLoading = true;
    this.completedQueryCondition.bizCondition.inspectionTaskId = this.id;
    this.completedQueryCondition.bizCondition.procId = this.procId;
    const param = this.completedQueryCondition;
    this.$inspection.getUnfinishedCompleteList(param).subscribe((result: Result) => {
      if (result.code === 0) {
        this.scheduleTableConfig.isLoading = false;
        this.schedule_dataSet = result.data.data;
        const data = result.data.data;
        data.forEach(item => {
          item.result = this.getResultStatus(item.result);
        });
      }
    }, () => {
      this.scheduleTableConfig.isLoading = false;
    });
  }

  /**
   * 获取工单类型状态
   */
  getResultStatus(result) {
    return this.language[this.WORK_ORDER_RESULT[result]];
  }

  /**
   * 初始化已完成巡检信息列表配置
   */
  private scheduleInitTableConfig() {
    this.scheduleTableConfig = {
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
        {// 巡检设施
          title: this.language.inspectionFacility, key: 'deviceName', width: 200,
          searchable: true, configurable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {// 巡检结果
          title: this.language.inspectionResults, key: 'result', width: 200,
          searchable: true, configurable: true, isShowSort: true,
          searchConfig: {
            type: 'select',
            selectInfo: [
              {label: this.language.normal, value: '0'},  // 正常
              {label: this.language.abnormal, value: '1'},   // 异常
            ]
          },
        },
        {// 异常详细
          title: this.language.exceptionallyDetailed, key: 'exceptionDescription', width: 200,
          searchable: true, configurable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {// 巡检时间
          title: this.language.inspectionTime, key: 'inspectionTime', width: 200, pipe: 'date',
          searchable: true, configurable: true, searchKey: 'inspectionTimes',
          sortKey: 'inspectionTime',
          isShowSort: true, searchConfig: {type: 'dateRang'}
        },
        {// 责任人
          title: this.language.responsible, key: 'updateUserName', width: 200,
          searchKey: 'updateUser', isShowSort: true,
          configurable: true, searchable: true,
          searchConfig: {
            type: 'select',
            selectType: 'multiple',
            selectInfo: this.roleArray,
          }
        },
        {// 资源匹配情况
          title: this.language.matchingOfResources, key: 'resourceMatching', width: 200,
          searchable: true, configurable: true, isShowSort: true, searchConfig: {type: 'input'}
        },
        {// 关联图片
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
   * 进度显示已完工列表
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
   *巡检任务 新增、修改跳转
   */
  inspectionTaskDetail(type, inspectionTaskId?, isOpen?) {
    return this.$router.navigate([`/business/work-order/inspection/task/${type}`],
      {queryParams: {inspectionTaskId: inspectionTaskId, isOpen: isOpen}});
  }

  /**
   * 跳转到详情
   * param url
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
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
   * 生成导出条件
   */
  createExportParams(event) {
    this.exportParams.queryCondition = this.queryCondition;
    if (event.selectItem.length > 0) {
      this.exportParams.queryCondition.bizCondition.inspectionTaskIds = event.selectItem.map(item => item.inspectionTaskId);
    }
    this.exportParams.excelType = event.excelType;
  }
  /**
   * 导出
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
   * 隐藏关联工单弹框
   */
  close() {
    this.isAssociatedWorkOrderVisible = false;
  }
  /**
   * 隐藏进度弹框
   */
  scheduleClose() {
    this.scheduleIsVisible = false; // 进度弹框
  }
}
