import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {ActivatedRoute, Router} from '@angular/router';
import {QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {Result} from '../../../../shared-module/entity/result';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {ExportParams} from '../../../../shared-module/entity/exportParams';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {PictureViewService} from '../../../../core-module/api-service/facility/picture-view-manage/picture-view.service';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../../facility';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';
import {getDeviceType} from '../../../facility/facility.config';

@Component({
  selector: 'app-finished-inspection-work-order',
  templateUrl: './finished-inspection-work-order.component.html',
  styleUrls: ['./finished-inspection-work-order.component.scss']
})
export class FinishedInspectionWorkOrderComponent implements OnInit {
  isVisible = false; // 已完成巡检信息列表
  responsibleUnitIsVisible = false; // 责任单位
  title; // title已完成巡检信息
  _dataSet = []; // 完工记录列表数据存放
  see_dataSet = []; // 已完成列表数据存放
  roleArray = []; // 获取责任人数据
  pageBean: PageBean = new PageBean(10, 1, 1); // 分页
  seePageBean: PageBean = new PageBean(10, 1, 1); // 分页
  tableConfig: TableConfig; // 完工记录列表
  seeTableConfig: TableConfig; // 已完成列表
  completedWorkOrderID; // 已完成巡检信息ID
  exportParams: ExportParams = new ExportParams(); // 导出
  InspectionLanguage: InspectionLanguageInterface; // 国际化
  facilityLanguage: FacilityLanguageInterface;
  queryCondition: QueryCondition = new QueryCondition();
  treeSetting;
  private filterValue: any;
  treeSelectorConfig: TreeSelectorConfig;
  treeNodes = [];
  selectUnitName;
  deviceCountInputValue; // 巡检数量input值
  deviceCountSelectValue = 'eq'; // 巡检数量input值
  WORK_ORDER_STATUS = {
    'assigned': 'assigned',   // 待指派
    'pending': 'pending',   // 待处理
    'processing': 'processing',   // 处理中
    'completed': 'completed',   // 已完成
    'singleBack': 'singleBack',   // 已退单
  };
  WORK_ORDER_STATUS_CLASS = { // 工单状态小图标样式
    'assigned': 'fiLink-assigned-w statistics-assigned-color',
    'pending': 'fiLink-processed statistics-pending-color',
    'processing': 'fiLink-processing statistics-processing-color',
    'completed': 'fiLink-completed statistics-completed-color',
    'singleBack': 'fiLink-chargeback statistics-singleBack-color',
  };

  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  @ViewChild('UnitNameSearch') UnitNameSearch: TemplateRef<any>;
  @ViewChild('roleTemp') roleTemp: TemplateRef<any>;
  @ViewChild('inspectionQuantityFilter') inspectionQuantityFilter: TemplateRef<any>;

  // @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;

  constructor(private $nzI18n: NzI18nService,
              private $router: Router,
              private $inspection: InspectionService,
              public $message: FiLinkModalService,
              private $pictureViewService: PictureViewService,
              private $activatedRoute: ActivatedRoute,
              private $facilityUtilService: FacilityUtilService,
              private $userService: UserService,
              private $imageViewService: ImageViewService) {
  }

  ngOnInit() {
    this.InspectionLanguage = this.$nzI18n.getLocaleData('inspection');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.refreshData();
    this.seeInitTableConfig();
    // this.setExportParamsColumnInfoList();
    this.queryDeptList();
    this.initTreeSelectorConfig();
    this.getAllUser();
  }

  /**
   * 显示巡检完工记录列表数据
   */
  public refreshData() {
    // 别的页面跳转过来参数拼接
    if ('id' in this.$activatedRoute.snapshot.queryParams) {
      if (!this.queryCondition.filterConditions.some(item => item.filterField === 'optObjId')) {
        this.queryCondition.bizCondition.procIds = [this.$activatedRoute.snapshot.queryParams.id];
      }
    } else {
      this.queryCondition.filterConditions = this.queryCondition.filterConditions.filter(item => item.filterField !== 'optObjId');
    }
    this.tableConfig.isLoading = true;
    this.$inspection.getFinishedList(this.queryCondition).subscribe((result: Result) => {
      if (result.code === 0) {
        this.pageBean.Total = result.totalCount;
        this.tableConfig.isLoading = false;
        const data = result.data;
        data.forEach(item => {
          item.statusName = this.getStatusName(item.status);
          item.statusClass = this.getStatusClass(item.status);
          // if (item.deviceType) {
          //   item['_deviceType'] = item.deviceType;
          //   item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
          //   item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
          // }
          if (item.status === 'singleBack') {
            item.isShowTurnBackConfirmIcon = true;
          }
        });
        this._dataSet = result.data;
        console.log(result.data);
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  searchChange(event, f) {
    if (event === 'lt' || event === 'gt') {
      event = '';
    }
    this.deviceCountInputValue = event || f.filterValue;
    // this.deviceCountSelectValue = f.operator;
  }

  /**
   * 显示已完成工单列表数据
   */
  public refreshCompleteData(id?) {
    this.seeTableConfig.isLoading = true;
    this.queryCondition.bizCondition.procId = id;
    this.$inspection.getUnfinishedCompleteList(this.queryCondition).subscribe((result: Result) => {
      for (let i = 0; i < result.data.data.length; i++) {
        if (result.data.data[i].result === '0') {
          result.data.data[i].result = '正常';
        } else if (result.data.data[i].result === '1') {
          result.data.data[i].result = '异常';
        }
      }
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this.seePageBean.Total = result.data.totalCount;
        this.seeTableConfig.isLoading = false;
        this.see_dataSet = result.data.data;
        console.log(result.data);
      }
    }, () => {
      this.seeTableConfig.isLoading = false;
    });
  }

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
        {// 工单名称
          title: this.InspectionLanguage.workOrderName, key: 'title', width: 200,
          fixedStyle: {fixedLeft: true, style: {left: '124px'}},
          configurable: false,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {// 工单状态
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
              {label: this.InspectionLanguage.completed, value: 'completed'},
              {label: this.InspectionLanguage.singleBack, value: 'singleBack'},
            ]
          },
          type: 'render',
          renderTemplate: this.statusTemp,
        },
        {// 巡检起始时间
          title: this.InspectionLanguage.inspectionStartTime, key: 'inspectionStartTime', width: 200,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'inspectionStartTime',
          searchConfig: {type: 'dateRang'}
        },
        {// 巡检结束时间
          title: this.InspectionLanguage.inspectionEndTime, key: 'inspectionEndTime', width: 200,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'inspectionEndTime',
          searchConfig: {type: 'dateRang'}
        },
        {// 创建时间
          title: this.InspectionLanguage.creationTime, key: 'cTime', width: 200,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'cTime',
          searchConfig: {type: 'dateRang'}
        },
        {// 实际完成时间
          title: this.InspectionLanguage.actualTime, key: 'rcTime', width: 200,
          pipe: 'date',
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'rcTime',
          searchConfig: {type: 'dateRang'}
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
          configurable: true,
          // type: 'render',
          searchable: true,
          searchKey: 'deviceTypeName',
          // renderTemplate: this.deviceTypeTemp,
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
        {// 备注
          title: this.InspectionLanguage.remark, key: 'remark', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {// 退单原因
          title: this.InspectionLanguage.retreatSingleReason, key: 'concatSingleBackReason', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchKey: 'concatSingleBackReason',
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.InspectionLanguage.FalsePositives, value: 'other'},
              {label: this.InspectionLanguage.other, value: 'FalsePositives'},
            ]
          },
        },
        {// 操作
          title: this.InspectionLanguage.operate, searchable: true, configurable: false,
          searchConfig: {type: 'operate'}, key: '', width: 180, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      operation: [
        {
          text: this.InspectionLanguage.viewDetail,
          permissionCode: '06-1-3-2',
          className: 'fiLink-view-detail',
          handle: (currentIndex) => {
            this.title = this.InspectionLanguage.completeInspectionInformation;
            this.isVisible = true;
            this.completedWorkOrderID = currentIndex.procId;
            const id = currentIndex.procId;
            this.refreshCompleteData(id);
          }
        },
        {
          text: this.InspectionLanguage.regenerate,
          permissionCode: '06-1-3-1',
          className: 'fiLink-turn-back-confirm',
          key: 'isShowTurnBackConfirmIcon',
          confirmContent: this.InspectionLanguage.isItRegenerated,
          handle: (currentIndex) => {
            const id = currentIndex.procId;
            const status = currentIndex.status;
            this.$router.navigate([`/business/work-order/inspection/unfinished/updates`],
              {queryParams: {procId: id, status: status}}).then();
          }
        }
      ],
      sort: (event) => {
        this.handleSort(event);
        this.refreshData();
      },
      handleSearch: (event) => {
        if (!event.accountabilityDeptList) {
          this.selectUnitName = '';
          this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, []);
        }
        if (!event.inspectionDeviceCount) {
          this.deviceCountInputValue = '';
          this.deviceCountSelectValue = 'eq';
        }
        this.handleSearch(event);
        this.refreshData();
      },
      handleExport: (event) => {
        this.exportParams.columnInfoList = event.columnInfoList;
        this.exportParams.columnInfoList.forEach(item => {
          if (item.propertyName === 'status' || item.propertyName === 'inspectionStartTime' ||
            item.propertyName === 'inspectionEndTime' || item.propertyName === 'cTime' || item.propertyName === 'rcTime') {
            item.isTranslation = 1;
          }
        });
        this.createExportParams(event);
        this.$inspection.completionRecordExport(this.exportParams).subscribe((result: Result) => {
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
   * 生成导出条件
   */
  createExportParams(event) {
    console.log(event);
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
  //       propertyName: 'title',
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
  //       columnName: this.InspectionLanguage.actualTime,
  //       propertyName: 'rcTime',
  //       isTranslation: 1
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
  //       propertyName: 'assignName',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.remark,
  //       propertyName: 'remark',
  //     },
  //     {
  //       columnName: this.InspectionLanguage.retreatSingleReason,
  //       propertyName: 'concatSingleBackReason',
  //     },
  //   ];
  // }

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
      searchReturnType: 'object',
      notShowPrint: true,
      scroll: {x: '1600px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {// 巡检设施
          title: this.InspectionLanguage.inspectionFacility, key: 'deviceName', width: 200,
          isShowSort: true,
          configurable: true,
          searchable: true,
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
        console.log(event);
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
   * 完工记录分页
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 已完工分页
   */
  seePageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshCompleteData(this.completedWorkOrderID);
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
   * 筛选列
   */
  handleSearch(event) {
    this.queryCondition.bizCondition = this.setBizCondition(event);
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
   * 筛选
   * param event
   */
  setBizCondition(event) {
    console.log(event);
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
    if (_bizCondition.rcTime) {
      _bizCondition.rcTimes = CommonUtil.deepClone(_bizCondition.rcTime).map(item => {
        return CommonUtil.getSeconds(item);
      });
      delete _bizCondition.rcTime;
    }
    if (_bizCondition.deviceTypeName) {
      _bizCondition.deviceTypes = CommonUtil.deepClone(_bizCondition.deviceTypeName);
      delete _bizCondition.deviceTypeName;
    }
    if (_bizCondition.concatSingleBackReason) {
      _bizCondition.concatSingleBackReasons = CommonUtil.deepClone(_bizCondition.concatSingleBackReason);
      delete _bizCondition.concatSingleBackReason;
    }
    console.log(_bizCondition);
    return _bizCondition;
  }

  /**
   * 隐藏弹框
   */
  close() {
    this.isVisible = false;
  }

  /**
   * 跳转到详情
   * param url
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  getStatusName(status) {
    return this.InspectionLanguage[this.WORK_ORDER_STATUS[status]];
  }

  /**
   * 工单类型小图标
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${this.WORK_ORDER_STATUS_CLASS[status]}`;
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
   * 查询所有的区域
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
}
