import {Component, OnInit, ViewChild} from '@angular/core';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {Observable} from 'rxjs';
import {FormControl} from '@angular/forms';
import {NzI18nService, NzModalService, NzTreeNode} from 'ng-zorro-antd';
import {ActivatedRoute, Router} from '@angular/router';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';
import {Result} from '../../../../shared-module/entity/result';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../../facility';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {AreaService} from '../../../../core-module/api-service/facility/area-manage';
import {MapSelectorConfig} from '../../../../shared-module/entity/mapSelectorConfig';
import {Area} from '../../../../core-module/entity/facility/area';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {differenceInCalendarDays} from 'date-fns';
import {MapService} from '../../../../core-module/api-service/index/map';
import {IsSelectAll} from '../../work-order.config';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {WORK_ORDER_UNFINISHED_INSPECTION_NUMBER} from '../../../../shared-module/const/work-order';

/**
 * 巡检任务新增.编辑组件
 */
@Component({
  selector: 'app-inspection-task-detail',
  templateUrl: './inspection-task-detail.component.html',
  styleUrls: ['./inspection-task-detail.component.scss'],
  providers: [FacilityUtilService]
})

export class InspectionTaskDetailComponent implements OnInit {
  WorkOrder = WORK_ORDER_UNFINISHED_INSPECTION_NUMBER;
  // 引用巡检国际化
  public language: InspectionLanguageInterface;
  // 引用设施国际化
  public facilityLanguage: FacilityLanguageInterface;
  // 区域信息
  public areaInfo: any = new Area();
  // 区域配置
  public areaSelectorConfig: any = new TreeSelectorConfig();
  // 设施map配置
  public mapSelectorConfig: MapSelectorConfig;
  // 责任单位初始化
  public treeSelectorConfig: any = new TreeSelectorConfig();
  // 表单列
  public formColumn: FormItem[] = [];
  // 表单操作
  public formStatus: FormOperate;
  // 是否加载
  public isLoading = false;
  // 巡检起始时间
  @ViewChild('taskStartTime') public taskStartTimeTemp;
  // 巡检结束时间
  @ViewChild('taskEndTime') public taskEndTimeTemp;
  // 区域
  @ViewChild('areaSelector') public areaSelectorTemp;
  // 责任单位
  @ViewChild('departmentSelector') public departmentSelectorTemp;
  // 巡检设施
  @ViewChild('inspectionFacilitiesSelector') public inspectionFacilitiesSelectorTemp;
  // 获取当前的时间
  public today = new Date();
  // 巡检起始时间
  public taskStartTime;
  // 巡检结束时间
  public taskEndTime;
  // 巡检任务ID
  public inspectionTaskId;
  // 是否开启巡检任务
  public isOpen;
  // 区域选中数据
  public selectorData: any = {parentId: '', accountabilityUnit: ''};
  // 责任单位id
  public accountabilityDept: string = '';
  // 责任单位名称
  public selectUnitName: string = '';
  // 设施名称
  public selectDeviceName: string = '';
  // 存放选择单位
  public deptInfo: any = {};
  // 新增或编辑页面
  public pageType;
  // 页面标题
  public pageTitle: string;
  // 设施id
  public deviceId: string;
  // 责任单位id
  public deptId: string;
  // 设施id和对应的区域id集合
  public deviceList = [];
  // 巡检区域Id
  public inspectionAreaId;
  // 巡检全集默认是状态
  public defaultStatus: string = '1';
  // 区域名称
  public areaName = '';
  // 编辑初始化区域名称
  public initAreaName = '';
  // 区域id查设施集合参数
  public areaDeviceId;
  // 区域弹框
  public areaSelectVisible: boolean = false;
  // 区域数据存放
  public areaNodes: any = [];
  // 责任单位数据存放
  public deptNodes: any = [];
  // 责任单位弹框
  public isUnitVisible: boolean = false;
  // 责任人单位模板button属性绑定
  public departmentSelectorDisabled: boolean;
  // 责任单位选中id集合
  public deptList = [];
  // 巡检设施弹框的显示隐藏
  public mapVisible = false;
  // 选中设施数量
  public selectDeviceNumber: string = null;
  // 责任单位名称
  public departmentSelectorName = '';
  // 巡检设施
  public inspectionFacilitiesSelectorName = '';  // 设施名称
  // 区域id
  public areaId: string = null;
  // 设施区域id
  public deviceAreaId: string = null;
  // 接收起始时间Value值
  public dateStart;
  // 接收结束时间Value值
  public dateEnd;
  // 巡检任务名称Value值
  public inspectionTaskName;
  // 巡检周期Value值
  public taskPeriod;
  // 接收期待完工时长Value值
  public procPlanDate;
  // 巡检设施模板button属性绑定
  public inspectionFacilitiesSelectorDisabled = true;
  // 区域是否可以操作
  public areaDisabled: boolean;
  // 巡检全集
  public isSelectAll: string = null;
  // 设施列表数据
  public deviceSet = [];
  // 是否为编辑
  public updateStatus;
  // 选择区域下设施数据存放
  public deviceData = [];
  // 区域回显
  areaSelectEvent = [];
  // 地图设施回显
  mapSelectEvent = [];

  constructor(public $activatedRoute: ActivatedRoute,
              public $message: FiLinkModalService,
              public $nzI18n: NzI18nService,
              public $active: ActivatedRoute,
              public $modelService: NzModalService,
              public $inspectionService: InspectionService,
              public $modalService: FiLinkModalService,
              public $facilityUtilService: FacilityUtilService,
              public $userService: UserService,
              public $areaService: AreaService,
              public $ruleUtil: RuleUtil,
              public $mapService: MapService,
              public $router: Router) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('inspection');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.initColumn();
    this.initMapSelectorConfig();
    this.judgePageJump();
  }

  /**
   * 页面切换 新增/修改
   */
  judgePageJump() {
    this.$activatedRoute.queryParams.subscribe(params => {
      if (params.inspectionTaskId) {
        this.pageType = 'update';
        this.inspectionTaskId = params.inspectionTaskId;
        this.isOpen = params.isOpen;
        this.queryDeptList().then((deptData: NzTreeNode[]) => {
          this.deptNodes = deptData;
          this.initTreeSelectorConfig(deptData);
          this.$facilityUtilService.getArea().then((data) => {
            this.areaNodes = data;
            this.initAreaSelectorConfig(data);
            this.getUpdateInspectionTask(this.inspectionTaskId);
          });
        });
      } else {
        this.pageType = 'add';
        this.isSelectAll = IsSelectAll.right;
        this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
          this.areaNodes = data;
          // 递归设置区域的选择情况
          this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, null);
          this.initAreaSelectorConfig(data);
        });
        this.queryDeptList().then((deptData: NzTreeNode[]) => {
          this.deptNodes = deptData;
          this.initTreeSelectorConfig(deptData);
        });
      }
      this.pageTitle = this.getPageTitle(this.pageType);
    });
  }

  /**
   * 获取页面标题类型
   * param type
   * returns {string}
   */
  public getPageTitle(type): string {
    let title;
    switch (type) {
      case'add':
        title = `${this.language.addArea}` + ' ' + `${this.language.inspectionTask}`;
        break;
      case 'update':
        title = `${this.language.edit}` + ' ' + `${this.language.inspectionTask}`;
        break;
    }
    return title;
  }

  /**
   * 巡检设施弹框的显示隐藏
   */
  showInspectionFacilitiesSelectorModal() {
    // if (this.inspectionFacilitiesSelectorName !== '') {
    //   this.deviceSet = this.mapSelectEvent.map(item => item.deviceId);
    // }
    this.deviceSet = this.deviceList.map(item => item.deviceId);
    if (this.areaName === '') {
      this.$modalService.info(`${this.language.pleaseSelectTheAreaInformationFirstTip}`);
    }
    if (this.areaName !== '' && this.isSelectAll === IsSelectAll.deny) {
      this.mapVisible = true;
    } else {
      this.mapVisible = false;
    }
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  /**
   * mapSelect所选结果
   * param event
   */
  mapSelectDataChange(event) {
    if (this.isSelectAll === IsSelectAll.deny) {
      this.selectDeviceName = '';
      event.map(item => {
        this.selectDeviceName += `${item.deviceName},`;
        return item;
      });
      this.inspectionFacilitiesSelectorName = this.selectDeviceName;
      this.deviceList = [];
      for (let i = 0; i < event.length; i++) {
        this.deviceId = event[i].deviceId;
        this.deviceAreaId = event[i].areaId;
        this.deviceList.push({'deviceId': this.deviceId, 'deviceAreaId': this.deviceAreaId});
      }
      const deviceCount = event.length + '';
      this.formStatus.resetControlData('inspectionDeviceCount', deviceCount);
      if (deviceCount === '0') {
        this.$modalService.info(`${this.language.selectDeviceTip}`);
      }
    }
    // this.mapSelectEvent = CommonUtil.deepClone(event);
    // console.log(this.mapSelectEvent);
    // console.log(this.inspectionFacilitiesSelectorName);
  }

  /**
   * 取消后退
   */
  goBack() {
    window.history.go(-1);
  }

  /**
   * 详情页确定按钮是否去灰
   */
  isConfirmButton() {
    if (
      this.inspectionTaskName && this.taskPeriod && this.taskPeriod < 37 &&
      this.procPlanDate && this.procPlanDate < 366 && this.dateStart && this.areaName && this.departmentSelectorName
    ) {
      if (this.isSelectAll === IsSelectAll.deny && this.inspectionFacilitiesSelectorName === '') {
        return false;
      }
      if (this.dateEnd !== null) {
        if (this.dateStart > this.dateEnd) {
          return false;
        }
      } else {
        return true;
      }
      return true;
    }
    return false;
  }

  /**
   * 巡检设施右边表格配置
   * param event
   */
  public initMapSelectorConfig() {
    this.mapSelectorConfig = {
      title: this.language.setDevice,
      width: '1100px',
      height: '465px',
      mapData: [],
      selectedColumn: [
        {
          title: this.language.deviceName, key: 'deviceName', width: 80
        },
        {
          title: this.language.deviceCode, key: 'deviceCode', width: 80,
        },
        {
          title: this.language.deviceType, key: '_deviceType', width: 60,
        },
        {
          title: this.language.parentId, key: 'areaName', width: 80,
        }
      ]
    };
  }

  /**
   * 新增和编辑巡检任务
   * param event
   */
  inspectionTaskDetail() {
    const newDate = new Date();
    if (this.dateEnd !== null && this.dateEnd < newDate) {
      this.$modalService.info(`${this.language.endTimeIsGreaterThanCurrentTime}`);
    } else {
      this.isLoading = true;
      const data = this.formStatus.group.getRawValue();
      data.deviceList = this.deviceList;
      data.departmentList = this.deptList;
      data.inspectionAreaId = this.inspectionAreaId;
      data.inspectionAreaName = this.areaName;
      data.deviceName = this.inspectionFacilitiesSelectorName;
      data.accountabilityDeptName = this.departmentSelectorName;
      data.inspectionDeviceCount = this.selectDeviceNumber;
      data.isSelectAll = this.isSelectAll;
      if (this.pageType === 'add') {
        data.isOpen = '1';
        data.taskStartDate = new Date(data.taskStartTime).getTime();
        if (data.taskEndTime) {
          data.taskEndDate = new Date(data.taskEndTime).getTime();
        } else {
          data.taskEndDate = null;
        }
        data['taskStartTime'] = CommonUtil.sendBackEndTime(data.taskStartDate);
        data['taskEndTime'] = CommonUtil.sendBackEndTime(data.taskEndDate);
        this.$inspectionService.insertWorkOrder(data).subscribe((result: Result) => {
          this.isLoading = false;
          if (result.code === 0) {
            this.$router.navigate(['/business/work-order/inspection/task-list']).then();
            this.$modalService.success(result.msg);
          } else {
            this.$modalService.error(result.msg);
          }
        }, () => {
          this.isLoading = false;
        });
      } else if (this.pageType === 'update') {
        this.inspectionFacilitiesSelectorName = data.deviceName;
        data['inspectionTaskId'] = this.inspectionTaskId;
        // 结束时间
        if (data.taskEndTime === null) {
          data.taskEndDate = null;
        } else if (data.taskEndTime === this.taskEndTime) {
          data.taskEndDate = new Date(this.taskEndTime).getTime();
        } else {
          data.taskEndDate = new Date(data.taskEndTime).getTime();
        }
        // 开始时间
        if (data.taskStartTime === this.taskStartTime) {
          data.taskStartDate = new Date(this.taskStartTime).getTime();
        } else {
          data.taskStartDate = new Date(data.taskStartTime).getTime();
        }
        data['taskEndTime'] = CommonUtil.sendBackEndTime(data.taskEndDate);
        data['taskStartTime'] = CommonUtil.sendBackEndTime(data.taskStartDate);
        data.isOpen = this.isOpen;
        this.$inspectionService.updateInspectionTask(data).subscribe((result: Result) => {
          this.isLoading = false;
          if (result.code === 0) {
            this.$router.navigate(['/business/work-order/inspection/task-list']).then();
            this.$modalService.success(result.msg);
          } else {
            this.$modalService.error(result.msg);
          }
        }, () => {
          this.isLoading = false;
        });
      }
    }
  }

  /**
   * 打开区域选择器
   */
  showAreaSelectorModal() {
    if (this.areaDisabled) {
      return;
    }
    this.areaSelectorConfig.treeNodes = this.areaNodes;
    this.areaSelectVisible = true;
  }

  /**
   *责任单位显示隐藏
   */
  showDepartmentSelectorModal() {
    if (this.areaName === '') {
      this.isUnitVisible = false;
      this.treeSelectorConfig.treeNodes = this.deptNodes;
      this.$modalService.info(`${this.language.pleaseSelectTheAreaInformationFirstTip}`);
    } else {
      this.treeSelectorConfig.treeNodes = this.deptNodes;
      this.isUnitVisible = true;
    }
  }

  /**
   * 区域选中结果
   * param event
   * queryDeviceAreaList
   */
  areaSelectChange(event) {
    // this.areaSelectEvent = CommonUtil.deepClone(event);
    this.inspectionFacilitiesSelectorName = '';
    this.departmentSelectorName = '';
    if (event[0]) {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, event[0].areaId, this.areaInfo.areaId);
      this.areaName = event[0].areaName;
      this.inspectionAreaId = event[0].areaId;
      this.selectorData.parentId = event[0].areaId;
      this.areaId = event[0].areaId;
      //  获取区域id下的所有设施参数
      this.getFacilityFilterByAreaId(this.areaId);
      this.queryDeptList();
    } else {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, this.areaInfo.areaId);
      this.areaName = '';
      this.selectorData.parentId = null;
    }
    this.formStatus.resetControlData('isSelectAll', this.WorkOrder.StrOne);
  }

  public initColumn() {
    this.formColumn = [
      { // 巡检任务名称
        label: this.language.inspectionTaskName, labelWidth: 180,
        key: 'inspectionTaskName',
        type: 'input',
        require: true,
        rule: [{required: true},
          RuleUtil.getNameMinLengthRule(),
          RuleUtil.getNameMaxLengthRule(),
          this.$ruleUtil.getNameRule()
        ],
        asyncRules: [
          this.$ruleUtil.getNameAsyncRule(value => this.$inspectionService.checkName(value,
            this.updateStatus ? this.inspectionTaskId : null),
            res => res.code === 0)
        ],
        modelChange: (controls, event, key, formOperate) => {
          this.inspectionTaskName = event;
        }
      },
      { // 巡检周期
        label: this.language.inspectionCycle, labelWidth: 180,
        key: 'taskPeriod',
        type: 'input',
        require: true,
        rule: [{
          required: true,
        },
          this.$ruleUtil.getTaskPeriodRule()
        ],
        modelChange: (controls, event, key, formOperate) => {
          this.taskPeriod = event;
        }
      },
      { // 巡检工单期望用时
        label: this.language.taskExpectedTime, labelWidth: 180,
        key: 'procPlanDate',
        type: 'input',
        require: true,
        rule: [{
          required: true,
        },
          this.$ruleUtil.getProcPlanDateRule()
        ],
        modelChange: (controls, event, key, formOperate) => {
          this.procPlanDate = event;
        }
      },
      { // 起始时间
        label: this.language.startTime, labelWidth: 180,
        key: 'taskStartTime',
        type: 'custom',
        require: true,
        template: this.taskStartTimeTemp,
        rule: [],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              if (control.value !== null) {
                this.dateStart = new Date(control.value);
              } else {
                this.dateStart = control.value;
              }
              return Observable.create(observer => {
                this.today = new Date();
                if (control.value !== null && control.value > this.today) {
                  if (this.dateEnd !== null && this.dateStart > this.dateEnd) {
                    this.$modalService.info(`${this.language.startTimeTip}`);
                  }
                  observer.next(null);
                  observer.complete();
                } else {
                  observer.next({error: true, duplicated: true});
                  observer.complete();
                }
              });
            },
          }
        ],
      },
      { // 结束时间
        label: this.language.endTime, labelWidth: 180,
        key: 'taskEndTime',
        type: 'custom',
        template: this.taskEndTimeTemp,
        rule: [],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              if (control.value !== null) {
                this.dateEnd = new Date(control.value);
              } else {
                this.dateEnd = control.value;
              }
              return Observable.create(observer => {
                if (this.dateEnd < this.dateStart && this.dateEnd !== null) {
                  this.$modalService.info(`${this.language.endTimeTip}`);
                }
                if (control.value !== null && control.value > this.dateStart) {
                  observer.next(null);
                  observer.complete();
                } else {
                  observer.next({error: true, duplicated: true});
                  observer.complete();
                }
              });
            },
          }
        ],
      },
      {// 巡检区域
        label: this.language.inspectionArea, labelWidth: 180,
        key: 'inspectionAreaName',
        type: 'custom',
        template: this.areaSelectorTemp,
        require: true,
        rule: [],
      },
      { // 是否巡检全集
        label: this.language.whetherCompleteWorks, labelWidth: 180,
        key: 'isSelectAll', type: 'radio',
        require: true,
        rule: [],
        initialValue: this.defaultStatus,
        radioInfo: {
          data: [
            {label: this.language.right, value: IsSelectAll.right}, // 表示巡检全集
            {label: this.language.deny, value: IsSelectAll.deny}, // 表示不巡检全集
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, event, key, formOperate) => {
          this.isSelectAll = event;
          this.inspectionFacilitiesSelectorName = '';
          this.formStatus.resetControlData('inspectionDeviceCount', '');
          this.formStatus.resetControlData('deviceName', this.selectDeviceName);
          if (this.isSelectAll === IsSelectAll.deny && this.areaName === '') {
            this.areaDeviceId = '';
          }
          // 编辑中只改巡检全集
          if (this.pageType === 'update' && this.areaName !== '') {
            this.getFacilityFilterByAreaId(this.areaId);
          }
          // 新增只在区域选择时调用设施区域关联接口
          if (this.pageType === 'add' && this.isSelectAll === IsSelectAll.right && this.areaName !== '') {
            // 设施总数
            this.selectDeviceNumber = this.deviceData.length + '';
            this.formStatus.resetControlData('inspectionDeviceCount', this.selectDeviceNumber);
            this.inspectionFacilitiesSelectorName = this.selectDeviceName;
          }
          if (event === this.WorkOrder.StrZero) {
            this.inspectionFacilitiesSelectorDisabled = false;
          } else if (event === this.WorkOrder.StrOne) {
            this.inspectionFacilitiesSelectorDisabled = true;
          }
        }
      },
      { // 巡检设施
        label: this.language.inspectionFacility, labelWidth: 180,
        key: 'deviceName', type: 'custom',
        require: true,
        template: this.inspectionFacilitiesSelectorTemp,
        rule: [],
      },
      { // 巡检设施总数
        label: this.language.totalInspectionFacilities, labelWidth: 180,
        key: 'inspectionDeviceCount', type: 'input',
        disabled: true, require: true,
        placeholder: this.language.automaticGenerated,
        rule: []
      },
      { // 责任单位
        label: this.language.responsibleUnit, labelWidth: 180,
        key: 'departmentList', type: 'custom',
        template: this.departmentSelectorTemp,
        require: true,
        rule: []
      }
    ];
  }

  /**
   * 责任单位选择结果
   * param event
   */
  selectDataChange(event) {
    if (event[0]) {
      this.deptInfo.deptId = event[0].id;
      this.$facilityUtilService.setTreeNodesStatus(this.deptNodes, [event[0].id]);
      this.selectUnitName = event[0].deptName;
      this.deptList = [];
      for (let i = 0; i < event.length; i++) {
        this.deptList.push({'accountabilityDept': event[i].id});
        this.deptId = event[i].id;
      }
      this.departmentSelectorName = this.selectUnitName;
      this.formStatus.resetControlData('departmentList', this.deptInfo.deptId);
    } else {
      this.$facilityUtilService.setTreeNodesStatus(this.deptNodes, []);
      this.selectUnitName = '';
      this.departmentSelectorName = this.selectUnitName;
      this.formStatus.resetControlData('departmentList', null);
    }
  }

  /**
   * 获取区域id下的单位
   */
  public queryDeptList(areaId?) {
    return new Promise((resolve, reject) => {
      if (this.areaId !== '') {
        const id = [this.areaId];
        this.$inspectionService.queryResponsibilityUnit(id).subscribe((result: Result) => {
          if (result.code === 0) {
            const arrDept = result.data || [];
            const deptData = arrDept.filter(item => {
              if (item.hasThisArea === true) {
                return item;
              }
            });
            this.deptNodes = deptData;
            resolve(deptData);
          }
        });
      } else {
        this.isUnitVisible = false;
        this.$modalService.info(`${this.language.pleaseSelectTheAreaInformationFirstTip}`);
      }
    });
  }

  /**
   * 根据id查询巡检任务
   * param id
   */
  public getUpdateInspectionTask(id) {
    this.$inspectionService.inquireInspectionTask(id).subscribe((result: Result) => {
      let inspectionInfo;
      if (result.code === 0) {
        this.updateStatus = true;
        inspectionInfo = result.data;
        this.formStatus.resetData(inspectionInfo);
        this.areaName = result.data.inspectionAreaName;
        this.initAreaName = result.data.inspectionAreaName;
        this.isSelectAll = result.data.isSelectAll;
        this.inspectionFacilitiesSelectorName = result.data.deviceName;
        this.departmentSelectorName = result.data.accountabilityDeptName;
        this.inspectionAreaId = result.data.inspectionAreaId;
        this.taskStartTime = result.data.taskStartTime;
        this.taskEndTime = result.data.taskEndTime;
        if (inspectionInfo['taskStartTime']) {
          this.formStatus.resetControlData('taskStartTime',
            new Date(CommonUtil.convertTime(new Date(inspectionInfo['taskStartTime']).getTime())));
        }
        if (inspectionInfo['taskEndTime'] !== null) {
          this.formStatus.resetControlData('taskEndTime',
            new Date(CommonUtil.convertTime(new Date(inspectionInfo['taskEndTime']).getTime())));
        }
        if (result.data.deviceList.length > 0 && this.isSelectAll === IsSelectAll.deny) {
          this.areaId = result.data.inspectionAreaId;
          this.deviceSet = result.data.deviceList.map(item => item.deviceId);
        }
      }
      const deptList = result.data.deptList;
      for (let i = 0; i < deptList.length; i++) {
        this.deptList.push({'accountabilityDept': deptList[i].accountabilityDept});
      }
      const deviceList = result.data.deviceList;
      for (let i = 0; i < deviceList.length; i++) {
        this.deviceList.push(
          {'deviceId': deviceList[i].deviceId, 'deviceAreaId': deviceList[i].deviceAreaId}
        );
      }
      this.areaId = result.data.inspectionAreaId;
      this.deptId = result.data.deptList[0].accountabilityDept;
      // 递归设置区域的选择情况
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, result.data.inspectionAreaId);
      // 递归设置单位树的节点状态
      this.queryDeptList(this.areaId).then((deptData: NzTreeNode[]) => {
        this.deptNodes = deptData;
        this.$facilityUtilService.setTreeNodesStatus(this.deptNodes, [deptList[0].accountabilityDept]);
      });
    });
  }

  /**
   * 初始化单位选择器配置
   */
  public initTreeSelectorConfig(nodes) {
    this.treeSelectorConfig = {
      width: '500px',
      height: '300px',
      title: this.language.responsibleUnit,
      treeSetting: {
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
          },
        },
        view: {
          showIcon: false,
          showLine: false
        }
      },
      treeNodes: nodes
    };
  }

  /**
   * 初始化区域选择器配置
   * param nodes
   */
  public initAreaSelectorConfig(nodes) {
    this.areaSelectorConfig = {
      width: '500px',
      height: '300px',
      title: this.language.selectInspectionArea,
      treeSetting: {
        check: {
          enable: true,
          chkStyle: 'checkbox',
          chkboxType: {'Y': '', 'N': ''},
        },
        data: {
          simpleData: {
            enable: true,
            idKey: 'areaId',
          },
          key: {
            name: 'areaName'
          },
        },

        view: {
          showIcon: false,
          showLine: false
        }
      },
      treeNodes: nodes
    };
  }

  /**
   * 结束时间大于起始时间
   */
  disabledEndDate = (current: Date): boolean => {
    if (this.dateStart !== null) {
      return differenceInCalendarDays(current, this.dateStart) < 0 || CommonUtil.checkTimeOver(current);
    } else {
      this.today = new Date();
      return differenceInCalendarDays(current, this.today) < 0 || CommonUtil.checkTimeOver(current);
    }
  };


  /**
   * 起始时间当天之前的时间不能选
   */
  disabledStartDate = (current: Date): boolean => {
    this.today = new Date();
    return differenceInCalendarDays(current, this.today) < 0 || CommonUtil.checkTimeOver(current);
  };

  /**
   * 获取区域id巡检全集的所有设施
   */
  public getFacilityFilterByAreaId(areaId, deviceType?) {
    if (areaId !== '') {
      this.$mapService.getALLFacilityList().subscribe((result: Result) => {
        if (result.code === 0) {
          const arrTemp = result.data || [];
          const facilityData = arrTemp.filter(item => {
            // 当传入areaId过滤
            if (areaId && item.areaId === areaId) {
              return item;
            }
          });
          this.deviceData = facilityData;
          this.deviceSet = [];
          this.deviceSet = facilityData.map(item => item.deviceId);
          // 设施名称集合
          this.selectDeviceName = '';
          facilityData.map(item => {
            this.selectDeviceName += `${item.deviceName},`;
            return item;
          });
          if (this.isSelectAll === IsSelectAll.right) {
            // 设施总数
            this.selectDeviceNumber = facilityData.length + '';
            this.formStatus.resetControlData('inspectionDeviceCount', this.selectDeviceNumber);
            this.deviceList = [];
            for (let i = 0; i < facilityData.length; i++) {
              this.deviceList.push(
                {'deviceId': facilityData[i].deviceId, 'deviceAreaId': this.areaId}
              );
            }
            this.inspectionFacilitiesSelectorName = this.selectDeviceName;
          }
        }
      });
    }
  }
}
