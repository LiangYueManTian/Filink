import {Component, OnInit, ViewChild} from '@angular/core';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {NzI18nService, NzModalService, NzTreeNode} from 'ng-zorro-antd';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';
import {FormControl} from '@angular/forms';
import {Result} from '../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {TreeSelectorConfig} from '../../../../shared-module/entity/treeSelectorConfig';
import {FacilityUtilService} from '../../../facility';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';
import {AreaService} from '../../../../core-module/api-service/facility/area-manage';
import {MapSelectorConfig} from '../../../../shared-module/entity/mapSelectorConfig';
import {UserService} from '../../../../core-module/api-service/user/user-manage';
import {Area} from '../../../../core-module/entity/facility/area';
import {MapSelectorInspectionComponent} from '../../../../shared-module/component/map-selector/map-selector-inspection/map-selector-inspection.component';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {differenceInCalendarDays} from 'date-fns';
import {ClearBarrierService} from '../../../../core-module/api-service/work-order/clear-barrier';
import {MapService} from '../../../../core-module/api-service/index/map';
import {getDeviceType} from '../../../facility/facility.config';

@Component({
  selector: 'app-inspection-work-order-detail',
  templateUrl: './inspection-work-order-detail.component.html',
  styleUrls: ['./inspection-work-order-detail.component.scss']
})
export class InspectionWorkOrderDetailComponent implements OnInit {
  remarkDisabled; // 备注是否可修改
  updateDeptList = []; // 修改时存放责任单位
  updataDeviceList = []; // 修改时存放巡检设施
  today = new Date(); // 获取当前日期
  formColumn: FormItem[] = []; // form表单配置
  formStatus: FormOperate;
  InspectionLanguage: InspectionLanguageInterface; // 国际化
  facilityLanguage: FacilityLanguageInterface;
  areaSelectorConfig: any = new TreeSelectorConfig(); // 单位选择器
  treeSelectorConfig: TreeSelectorConfig;
  isUnitVisible: boolean = false; // 责任单位弹框
  treeNodes = []; // 树
  departmentSelectorName = ''; // 责任人单位title值
  inspectionFacilitiesSelectorName = ''; // 巡检设施title值
  inspectionFacilitiesSelectorNameTwo = ''; // 巡检设施title值
  departmentSelectorDisabled: boolean = false; // 责任人单位模版button属性绑定
  inspectionFacilitiesSelectorDisabled = true; // 巡检设施模版button属性绑定
  pageTitle: string; // 页面标题
  pageType; // 截取URL地址
  InquireDeviceTypes = [];
  deviceType = '';
  isLoading = false; // 列表初始加载图标
  areaName = ''; // 区域名称
  areaId: string = null; // 区域ID
  InquireAreaId; // 区域ID
  _areaId: string; //    区域id备份
  _areaIdDevice: string; //    区域id备份供巡检设施使用
  deviceId; // 设施Id
  deptList = []; // 责任单位
  deviceList = []; // 巡检设施
  mapSelectorConfig: MapSelectorConfig; // 巡检设施列表
  mapVisible = false; // 巡检设施弹框
  areaSelectVisible: boolean = false; // 区域选择器弹框
  procId; // 工单ID
  status; // 工单状态
  dateStart; // 接收起始时间Value值
  dateEnd; // 接受结束时间Value值
  inspectionAreaId; // 巡检区域ID
  disabledIf: boolean; // 判断页面是否可修改
  selectArr; // 责任单位名称集合
  isSelectAll = '1';  // 是否巡检全集 // 1:是  0:否 // 默认状态
  inspectionStartTime;
  inspectionEndTime;
  lastDays; // 剩余天数
  inspectionName; // 工单名称
  facilityData; // 存储双重过滤后的设施数据
  public areaInfo: any = new Area(); // 区域信息
  public selectUnitName: string = '';
  private treeSetting: any;
  areaDisabled: boolean;
  @ViewChild('inspectionStartDate') public inspectionStartDate;
  @ViewChild('inspectionEndDate') public inspectionEndDate;
  @ViewChild('areaSelector') public areaSelector;
  @ViewChild('departmentSelector') public departmentSelector;
  @ViewChild('inspectionFacilitiesSelector') public inspectionFacilitiesSelector;
  @ViewChild('mapSelectorInspection') public mapSelectorInspection: MapSelectorInspectionComponent;
  private areaNodes: any = [];
  public _deviceType: any;
  private changeDevice: any;

  constructor(private $activatedRoute: ActivatedRoute,
              private $nzI18n: NzI18nService,
              private $modelService: NzModalService,
              private $facilityUtilService: FacilityUtilService,
              private $inspectionService: InspectionService,
              private $modalService: FiLinkModalService,
              private $areaService: AreaService,
              private $userService: UserService,
              private $clearBarrierService: ClearBarrierService,
              private $ruleUtil: RuleUtil,
              private $mapService: MapService,
              private $router: Router) {
  }

  ngOnInit() {
    this.InspectionLanguage = this.$nzI18n.getLocaleData('inspection');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
    this.judgePageJump();
    this.initTreeSelectorConfig();
    this.initMapSelectorConfig();
  }

  private initColumn() {
    this.formColumn = [
      { // 工单名称
        label: this.InspectionLanguage.workOrderName,
        key: 'title',
        type: 'input',
        require: true,
        disabled: this.disabledIf,
        placeholder: this.InspectionLanguage.pleaseEnter,
        rule: [
          RuleUtil.getNameMinLengthRule(),
          RuleUtil.getNameMaxLengthRule(),
          this.$ruleUtil.getNameRule()
        ],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              return Observable.create(observer => {
                this.inspectionName = control.value;
                observer.next(null);
                observer.complete();
              });
            },
            asyncCode: 'duplicated', msg: this.InspectionLanguage.thisNameExistsTip
          }
        ],
      },
      {// 工单类型
        label: this.InspectionLanguage.typeOfWorkOrder,
        key: 'procResourceType',
        type: 'input',
        disabled: true,
        initialValue: this.InspectionLanguage.inspection,
        require: true,
        rule: [],
        asyncRules: [],
      },
      {// 巡检起始时间
        label: this.InspectionLanguage.inspectionStartTime,
        key: 'inspectionStartDate',
        type: 'custom',
        require: true,
        template: this.inspectionStartDate,
        disabled: this.disabledIf,
        rule: [],
        asyncRules: [{
          asyncRule: (control: any) => {
            return Observable.create(observer => {
              this.dateStart = control.value;
              console.log(control.value);
              if (true) {
                observer.next(null);
                observer.complete();
              } else {
                observer.next({error: true, duplicated: true});
                observer.complete();
              }
            });
          },
        }],
      },
      {// 巡检结束时间
        label: this.InspectionLanguage.inspectionEndTime,
        key: 'inspectionEndDate',
        type: 'custom',
        require: true,
        template: this.inspectionEndDate,
        disabled: this.disabledIf,
        rule: [],
        asyncRules: [{
          asyncRule: (control: any) => {
            this.dateEnd = control.value;
            return Observable.create(observer => {
              if (this.dateEnd < this.dateStart && this.dateEnd !== null) {
                this.$modalService.info(`${this.InspectionLanguage.endTimeTip}`);
              }
              if (control.value !== null && control.value > this.dateStart) {
                if (this.dateStart === null || this.dateStart > this.dateEnd) {
                  this.$modalService.info(`${this.InspectionLanguage.firstSelectEndDateTip}`);
                }
                observer.next(null);
                observer.complete();
              } else {
                observer.next({error: true, duplicated: true});
                observer.complete();
              }
              const nowDate = new Date().getTime();
              if (this.dateEnd) {
                this.lastDays = Math.ceil(((this.dateEnd - nowDate) / 86400 / 1000) - 1);
                this.formStatus.resetControlData('lastDays', this.lastDays);
              }
            });
          },
        }],
      },
      {// 剩余天数
        label: this.InspectionLanguage.daysRemaining,
        disabled: true,
        key: 'lastDays',
        type: 'input',
        initialValue: '0',
        rule: [],
      },
      {// 巡检区域
        label: this.InspectionLanguage.inspectionArea,
        key: 'inspectionAreaName',
        type: 'custom',
        disabled: this.disabledIf,
        require: true,
        inputType: '',
        rule: [],
        template: this.areaSelector,
      },
      {// 设施类型
        label: this.InspectionLanguage.deviceType,
        key: 'deviceType',
        type: 'select',
        disabled: this.disabledIf,
        require: true,
        selectInfo: {
          data: [
            {label: this.InspectionLanguage.patchPanel, value: '060'},
            {label: this.InspectionLanguage.opticalBox, value: '001'},
            {label: this.InspectionLanguage.manWell, value: '030'},
            {label: this.InspectionLanguage.jointClosure, value: '090'},
            {label: this.InspectionLanguage.fiberBox, value: '150'}
          ],
          label: 'label',
          value: 'value',
        },
        modelChange: (controls, event, key, formOperate) => {
          if (!this.areaName && this.pageType === 'add') {
            this.$modalService.info(this.InspectionLanguage.pleaseSelectTheAreaInformationFirstTip);
          }
          this.inspectionFacilitiesSelectorName = '';
          this.deviceType = event;
          this._deviceType = event;
          this.changeDevice = event;
          this.InquireDeviceTypes = [];
          this.InquireDeviceTypes.push(event);
          if (this.changeDevice) {
            this.changeInspectionFacilities(this.isSelectAll);
          }
        },
        rule: [],
      },
      { // 是否巡检全集
        label: this.InspectionLanguage.whetherCompleteWorks,
        key: 'isSelectAll',
        type: 'radio',
        require: true,
        disabled: this.disabledIf,
        rule: [{required: true}],
        initialValue: this.isSelectAll,
        radioInfo: {
          data: [
            {label: this.InspectionLanguage.right, value: '1'}, // 是
            {label: this.InspectionLanguage.deny, value: '0'}, // 否
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, event, key, formOperate) => {
          this.inspectionFacilitiesSelectorName = '';
          if ((this.pageType === 'update' || this.pageType === 'updates') && this.inspectionFacilitiesSelectorNameTwo !== undefined) {
            this.inspectionFacilitiesSelectorName = this.inspectionFacilitiesSelectorNameTwo;
            delete this.inspectionFacilitiesSelectorNameTwo;
          }
          this.isSelectAll = event;
          this.changeInspectionFacilities(event);
        }
      },
      {// 巡检设施
        label: this.InspectionLanguage.inspectionFacility,
        key: 'deviceList',
        type: 'custom',
        disabled: this.disabledIf,
        require: true,
        template: this.inspectionFacilitiesSelector,
        rule: [{required: true}],
      },
      {// 责任单位
        label: this.InspectionLanguage.responsibleUnit,
        key: 'deptList',
        type: 'custom',
        template: this.departmentSelector,
        disabled: this.disabledIf,
        rule: []
      },
      {// 备注
        label: this.InspectionLanguage.remark,
        disabled: this.remarkDisabled,
        key: 'remark',
        type: 'input',
        rule: [{minLength: 0}, {maxLength: 255}]
      },
    ];
  }

  /**
   * 页面切换 新增/修改/重新生成
   */
  pageSwitching() {
    if (this.pageType === 'update' || this.pageType === 'updates') {
      // 显示修改列表页数据
      this.$inspectionService.getUpdateWorkUnfinished(this.procId).subscribe((result: Result) => {
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, null);
        if (result.code === 0) {
          result.data.procResourceType = '巡检';
          this.formStatus.resetData(JSON.parse(JSON.stringify(result.data)));
          this.departmentSelectorName = result.data.accountabilityDeptName;
          const deviceListData = result.data.procRelatedDevices;
          for (let i = 0; i < deviceListData.length; i++) {
            this.updataDeviceList.push({
              'deviceId': deviceListData[i].deviceId,
              'deviceName': deviceListData[i].deviceName,
              'deviceType': deviceListData[i].deviceType,
              'deviceAreaId': deviceListData[i].deviceAreaId,
              'deviceAreaName': deviceListData[i].deviceAreaName
            });
          }
          if (result.data.procRelatedDepartments) {
            this.updateDeptList = [{'accountabilityDept': result.data.procRelatedDepartments[0].accountabilityDept}];
          }
          const deviceLists = result.data.procRelatedDevices.map(item => item.deviceName);
          this.inspectionFacilitiesSelectorName = deviceLists.join(',');
          this.inspectionFacilitiesSelectorNameTwo = deviceLists.join(',');
          this.areaName = result.data.deviceAreaName;
          this.inspectionAreaId = deviceListData[0].deviceAreaId;
          this._areaId = deviceListData[0].deviceAreaId;
          this._areaIdDevice = deviceListData[0].deviceAreaId;
          this.deviceType = deviceListData[0].deviceType;
          this.inspectionStartTime = result.data.inspectionStartTime;
          this.inspectionEndTime = result.data.inspectionEndTime;
          console.log(result);
          // this.queryDeptList(this._areaId);
          // 递归设置责任单位的选择情况
          this.queryDeptList(this._areaId).then((deptData: NzTreeNode[]) => {
            this.treeNodes = deptData;
            this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, [result.data.procRelatedDepartments[0].accountabilityDept]);
          });
          this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
            this.areaNodes = data;
            // 递归设置区域的选择情况
            this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, this._areaId);
            this.initAreaSelectorConfig(data);
          });
        }
      });
    } else {
      this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
        this.areaNodes = data;
        // 递归设置区域的选择情况
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, this._areaId);
        this.initAreaSelectorConfig(data);
      });
    }
  }

  /**
   * 页面title切换
   */
  private getPageTitle(type): string {
    let title;
    switch (type) {
      case 'add':
        title = `${this.InspectionLanguage.addArea}${this.InspectionLanguage.patrolInspectionSheet}`;
        break;
      case 'update':
        title = `${this.InspectionLanguage.edit}${this.InspectionLanguage.patrolInspectionSheet}`;
        break;
      case 'updates':
        title = `${this.InspectionLanguage.edit}${this.InspectionLanguage.patrolInspectionSheet}`;
        break;
    }
    return title;
  }

  /**
   * 接受表单传进来的参数并赋值
   * param event
   */
  formInstance(event) {
    this.formStatus = event.instance;
    // event.instance.column[1].initialValue = '巡检';
  }

  /**
   * 返回
   */
  goBack() {
    window.history.back();
  }

  /**
   * 添加/修改/重新生成操作
   */
  add() {
    const nowDate = new Date();
    if (new Date(this.dateEnd) < nowDate) {
      this.$modalService.info(this.InspectionLanguage.expectedCompletionTimeMustBeGreaterThanCurrentTime);
    } else {
      this.isLoading = true;
      const data = this.formStatus.group.getRawValue();
      const updateStartTime = new Date(data.inspectionStartDate);
      const updateEndTime = new Date(data.inspectionEndDate);
      data.deviceList = this.deviceList;
      data.deptList = this.deptList;
      data.procResourceType = '1'; // 表示工单的来源类型是手动新增的
      if (this.pageType === 'update' || this.pageType === 'updates') {
        if (this.deviceList.length !== 0) {
          data.deviceList = this.deviceList;
        } else {
          data.deviceList = this.updataDeviceList;
        }
        data.deptList = this.updateDeptList;
        console.log(this.inspectionStartTime);
        data.inspectionStartDate = updateStartTime.getTime() || this.inspectionStartTime;
        data.inspectionEndDate = updateEndTime.getTime() || this.inspectionEndTime;
      } else {
        data.inspectionStartDate = updateStartTime.getTime();
        data.inspectionEndDate = updateEndTime.getTime();
      }
      data.inspectionAreaName = this.areaName;
      data.inspectionAreaId = this.inspectionAreaId;
      if (this.pageType === 'add') {
        data.procId = this.procId;
        this.$inspectionService.addWorkUnfinished(data).subscribe((result: Result) => {
          this.isLoading = false;
          if (result.code === 0) {
            this.goBack();
            this.$modalService.success(result.msg);
          } else {
            this.$modalService.error(result.msg);
          }
        }, () => {
          this.isLoading = false;
        });
      } else if (this.pageType === 'update') {
        data.procId = this.procId;
        this.$inspectionService.updateWorkUnfinished(data).subscribe((result: Result) => {
          this.isLoading = false;
          if (result.code === 0) {
            this.goBack();
            this.$modalService.success(result.msg);
          } else {
            this.$modalService.error(result.msg);
          }
        }, () => {
          this.isLoading = false;
        });
      } else if (this.pageType === 'updates') {
        data.regenerateId = this.procId;
        this.$inspectionService.inspectionRegenerate(data).subscribe((result: Result) => {
          this.isLoading = false;
          if (result.code === 0) {
            this.$router.navigate(['/business/work-order/inspection/unfinished']).then();
            this.$modalService.success(this.InspectionLanguage.CreateTheInspectionWorkOrderSuccessfully);
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
    if (!this.disabledIf) {
      this.areaSelectorConfig.treeNodes = this.areaNodes;
      this.areaSelectVisible = true;
    }
  }

  /**
   * 区域选中结果
   * param event
   */
  areaSelectChange(event) {
    this.departmentSelectorName = '';
    this.inspectionFacilitiesSelectorName = '';
    this.deptList = [];
    this.deviceList = [];
    if (event[0]) {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, event[0].areaId, this.areaInfo.areaId);
      this.areaName = event[0].areaName;
      this.inspectionAreaId = event[0].areaId;
      this.areaId = event[0].areaId;
    } else {
      this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, this.areaInfo.areaId);
      this.areaName = '';
    }
    this.queryDeptList(event[0].areaId);
    // if (this._deviceType) {
    //   this.changeInspectionFacilities(this.isSelectAll);
    // }
    this.formStatus.group.controls['deviceType'].reset();
  }

  /**
   * 责任单位选择结果
   * param event
   */
  selectDataChange(event) {
    this.selectUnitName = '';
    this.selectArr = event.map(item => {
      this.selectUnitName += `${item.deptName}`;
      return item;
    });
    if (event.length !== 0) {
      for (let i = 0; i < event.length; i++) {
        this.deptList.push({'accountabilityDept': event[i].id});
        this.updateDeptList.push({'accountabilityDept': event[i].id});
      }
      this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, [event[0].id]);
    } else {
      this.updateDeptList = [];
      this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, this.selectArr);
    }
    this.departmentSelectorName = this.selectUnitName;
    this.formStatus.resetControlData('deptList', this.selectArr);
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
      title: this.InspectionLanguage.responsibleUnit,
      width: '500px',
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
   * 打开责任单位选择器
   */
  showDepartmentSelectorModal() {
    console.log(this.areaName);
    if (this.areaName === '') {
      this.$modalService.info(this.InspectionLanguage.pleaseSelectTheAreaInformationFirstTip);
    } else {
      if (!this.disabledIf) {
        this.treeSelectorConfig.treeNodes = this.treeNodes;
        if (this.selectArr) {
          this.areaSelectorConfig.treeNodes = this.selectArr;
        }
      }
      this.isUnitVisible = true;
    }
  }

  /**
   * 根据区域ID查询责任单位
   */
  private queryDeptList(areaId?) {
    return new Promise((resolve, reject) => {
      if (this.areaId !== '') {
        const id = [areaId || this.areaId || this._areaId];
        this.$inspectionService.queryResponsibilityUnit(id).subscribe((result: Result) => {
          if (result.code === 0) {
            const arrDept = result.data || [];
            const deptData = arrDept.filter(item => {
              if (item.hasThisArea === true) {
                return item;
              }
            });
            this.treeNodes = deptData;
            resolve(deptData);
          }
        });
      } else {
        this.isUnitVisible = false;
        this.$modalService.info(`${this.InspectionLanguage.pleaseSelectTheAreaInformationFirstTip}`);
      }
    });
  }

  /**
   * 初始化区域选择器配置
   * param nodes
   */
  private initAreaSelectorConfig(nodes) {
    this.areaSelectorConfig = {
      width: '500px',
      height: '300px',
      title: `${this.facilityLanguage.select}${this.facilityLanguage.area}`,
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
   * 打开巡检设施选择器
   */
  showInspectionFacilitiesSelectorModal() {
    if (this.areaName === '') {
      this.$modalService.info(this.InspectionLanguage.pleaseSelectTheAreaInformationFirstTip);
    } else if (this.deviceType === '') {
      this.$modalService.info(this.InspectionLanguage.pleaseSelectTheTypeOfInspectionFacilityFirst);
    } else {
      if (!this.disabledIf) {
        this.mapVisible = true;
        this._deviceType = this.formStatus.getData('deviceType');
        console.log(this._deviceType, this._areaId);
      } else {
        this.$modalService.info(this.InspectionLanguage.pleaseSelectTheAreaInformationFirstTip);
      }
    }
  }

  /**
   * 巡检设施所选结果
   * param event
   */
  mapSelectDataChange(event) {
    this.inspectionFacilitiesSelectorName = '';
    if (event.length !== 0) {
      this.deviceList = [];
      for (let i = 0; i < event.length; i++) {
        this.inspectionFacilitiesSelectorName += event[i].deviceName + ',';
        this.deviceList.push({
          'deviceId': event[i].deviceId, 'deviceName': event[i].deviceName, 'deviceType': event[i].deviceType,
          'deviceAreaId': this.inspectionAreaId, 'deviceAreaName': this.areaName
        });
      }
    } else {
      this.$modalService.info(this.InspectionLanguage.selectDeviceTip);
    }
  }

  /**
   * 巡检设施默认所选结果
   */
  mapSelectDataChanges(event) {
    console.log(event);
    this.inspectionFacilitiesSelectorName = '';
    this.deviceList = [];
    const data = event;
    if (this.isSelectAll === '1') {
      for (let i = 0; i < data.length; i++) {
        this.inspectionFacilitiesSelectorName += data[i].deviceName + ',';
        this.deviceList.push({
          'deviceId': data[i].deviceId, 'deviceName': data[i].deviceName, 'deviceType': data[i].deviceType,
          'deviceAreaId': this.inspectionAreaId, 'deviceAreaName': this.areaName
        });
      }
    }
  }

  /**
   * 巡检设施
   */
  private initMapSelectorConfig() {
    this.mapSelectorConfig = {
      title: this.InspectionLanguage.setDevice,
      width: '1100px',
      height: '465px',
      mapData: [],
      selectedColumn: [
        {
          title: this.InspectionLanguage.deviceName, key: 'deviceName', width: 100
        },
        {
          title: this.InspectionLanguage.assetNumber, key: 'deviceCode', width: 100,
        },
        {
          title: this.InspectionLanguage.area, key: '_deviceType', width: 100,
        },
        {
          title: this.InspectionLanguage.facilityType, key: 'areaName', width: 100,
        },
        {
          title: this.InspectionLanguage.address, key: 'areaName', width: 100,
        }
      ]
    };
  }

  /**
   * 根据id查询未完成工单
   * param id
   */
  private getUpdateWorkUnfinished(id) {
    this.$inspectionService.getUpdateWorkUnfinished(id).subscribe((result: Result) => {
      if (result.code === 0) {
        result.data.procResourceType = '巡检';
        this.formStatus.resetData(result.data);
      }
    });
  }

  /**
   * 根据是否巡检全集改变巡检设施
   */
  changeInspectionFacilities(event) {
    if (event === '0') {
      this.inspectionFacilitiesSelectorDisabled = false;
    } else if (event === '1') {
      this.inspectionFacilitiesSelectorDisabled = true;
      // 根据区域ID查询设施
      this.InquireAreaId = [this.areaId];
      this.$mapService.getALLFacilityList().subscribe((result: Result) => {
        const arrTemp = result.data || [];
        this.facilityData = arrTemp.filter(item => {
            // 当传入 areaId 和deviceType 双重过滤
            if ((this.areaId || this._areaId) && this.deviceType && item.areaId === (this.areaId || this._areaId) &&
              item.deviceType === this.deviceType) {
              return item;
            } else if (this.areaId && !this.deviceType && item.areaId === this.areaId) {
              return item;
            }
          }
        );
        this.mapSelectDataChanges(this.facilityData);
      }, () => {
      });
    }
  }

  /**
   * 判断页面跳转
   */
  judgePageJump() {
    this.$activatedRoute.queryParams.subscribe(params => {
      if (params.procId) {
        this.procId = params.procId;
        this.queryDeptList().then(() => {
          this.$facilityUtilService.getArea().then((data) => {
            this.areaNodes = data;
            // 递归设置区域的选择情况
            this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, null);
            this.initAreaSelectorConfig(data);
            this.getUpdateWorkUnfinished(this.procId);
          });
        });
        const url = window.location.href;
        this.pageType = url.indexOf('updates') >= 0 ? 'updates' : 'update';
        const str = url.lastIndexOf('=');
        this.status = url.substring(str + 1, url.length);
        if (this.status === 'assigned' || this.pageType === 'updates' && this.status === 'singleBack') {
          this.disabledIf = false;
        } else {
          this.disabledIf = true;
          this.remarkDisabled = false;
          this.departmentSelectorDisabled = true;
        }
      } else {
        this.pageType = 'add';
      }
      this.pageTitle = this.getPageTitle(this.pageType);
      this.initColumn();
      this.pageSwitching();
    });
  }

  /**
   *  起始日期不可选择小于当前日期
   */
  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) < 0;
  }
  /**
   *  期望完工日期不可选择小于起始日期
   */
  disabledEndDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.dateStart) < 0;
  }

  /**
   * 表单提交按钮检查
   */
  confirmButtonIsGray() {
    const newDate = new Date();
    if (this.pageType === 'update' && this.status !== 'assigned') {
      return true;
    } else {
      if (this.dateEnd && this.dateStart && this.inspectionName && this.areaName && this.deviceType &&
        new Date(this.dateStart) < new Date(this.dateEnd)) {
        if ((this.isSelectAll === '1' || this.isSelectAll === '0') && this.inspectionFacilitiesSelectorName) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    }
  }
}
