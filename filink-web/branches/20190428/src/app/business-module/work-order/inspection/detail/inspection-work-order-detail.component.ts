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
  remarkDisabled; // ?????????????????????
  updateDeptList = []; // ???????????????????????????
  updataDeviceList = []; // ???????????????????????????
  today = new Date(); // ??????????????????
  formColumn: FormItem[] = []; // form????????????
  formStatus: FormOperate;
  InspectionLanguage: InspectionLanguageInterface; // ?????????
  facilityLanguage: FacilityLanguageInterface;
  areaSelectorConfig: any = new TreeSelectorConfig(); // ???????????????
  treeSelectorConfig: TreeSelectorConfig;
  isUnitVisible: boolean = false; // ??????????????????
  treeNodes = []; // ???
  departmentSelectorName = ''; // ???????????????title???
  inspectionFacilitiesSelectorName = ''; // ????????????title???
  inspectionFacilitiesSelectorNameTwo = ''; // ????????????title???
  departmentSelectorDisabled: boolean = false; // ?????????????????????button????????????
  inspectionFacilitiesSelectorDisabled = true; // ??????????????????button????????????
  pageTitle: string; // ????????????
  pageType; // ??????URL??????
  InquireDeviceTypes = [];
  deviceType = '';
  isLoading = false; // ????????????????????????
  areaName = ''; // ????????????
  areaId: string = null; // ??????ID
  InquireAreaId; // ??????ID
  _areaId: string; //    ??????id??????
  _areaIdDevice: string; //    ??????id???????????????????????????
  deviceId; // ??????Id
  deptList = []; // ????????????
  deviceList = []; // ????????????
  mapSelectorConfig: MapSelectorConfig; // ??????????????????
  mapVisible = false; // ??????????????????
  areaSelectVisible: boolean = false; // ?????????????????????
  procId; // ??????ID
  status; // ????????????
  dateStart; // ??????????????????Value???
  dateEnd; // ??????????????????Value???
  inspectionAreaId; // ????????????ID
  disabledIf: boolean; // ???????????????????????????
  selectArr; // ????????????????????????
  isSelectAll = '1';  // ?????????????????? // 1:???  0:??? // ????????????
  inspectionStartTime;
  inspectionEndTime;
  lastDays; // ????????????
  inspectionName; // ????????????
  facilityData; // ????????????????????????????????????
  public areaInfo: any = new Area(); // ????????????
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
      { // ????????????
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
      {// ????????????
        label: this.InspectionLanguage.typeOfWorkOrder,
        key: 'procResourceType',
        type: 'input',
        disabled: true,
        initialValue: this.InspectionLanguage.inspection,
        require: true,
        rule: [],
        asyncRules: [],
      },
      {// ??????????????????
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
      {// ??????????????????
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
      {// ????????????
        label: this.InspectionLanguage.daysRemaining,
        disabled: true,
        key: 'lastDays',
        type: 'input',
        initialValue: '0',
        rule: [],
      },
      {// ????????????
        label: this.InspectionLanguage.inspectionArea,
        key: 'inspectionAreaName',
        type: 'custom',
        disabled: this.disabledIf,
        require: true,
        inputType: '',
        rule: [],
        template: this.areaSelector,
      },
      {// ????????????
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
      { // ??????????????????
        label: this.InspectionLanguage.whetherCompleteWorks,
        key: 'isSelectAll',
        type: 'radio',
        require: true,
        disabled: this.disabledIf,
        rule: [{required: true}],
        initialValue: this.isSelectAll,
        radioInfo: {
          data: [
            {label: this.InspectionLanguage.right, value: '1'}, // ???
            {label: this.InspectionLanguage.deny, value: '0'}, // ???
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
      {// ????????????
        label: this.InspectionLanguage.inspectionFacility,
        key: 'deviceList',
        type: 'custom',
        disabled: this.disabledIf,
        require: true,
        template: this.inspectionFacilitiesSelector,
        rule: [{required: true}],
      },
      {// ????????????
        label: this.InspectionLanguage.responsibleUnit,
        key: 'deptList',
        type: 'custom',
        template: this.departmentSelector,
        disabled: this.disabledIf,
        rule: []
      },
      {// ??????
        label: this.InspectionLanguage.remark,
        disabled: this.remarkDisabled,
        key: 'remark',
        type: 'input',
        rule: [{minLength: 0}, {maxLength: 255}]
      },
    ];
  }

  /**
   * ???????????? ??????/??????/????????????
   */
  pageSwitching() {
    if (this.pageType === 'update' || this.pageType === 'updates') {
      // ???????????????????????????
      this.$inspectionService.getUpdateWorkUnfinished(this.procId).subscribe((result: Result) => {
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, null, null);
        if (result.code === 0) {
          result.data.procResourceType = '??????';
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
          // ???????????????????????????????????????
          this.queryDeptList(this._areaId).then((deptData: NzTreeNode[]) => {
            this.treeNodes = deptData;
            this.$facilityUtilService.setTreeNodesStatus(this.treeNodes, [result.data.procRelatedDepartments[0].accountabilityDept]);
          });
          this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
            this.areaNodes = data;
            // ?????????????????????????????????
            this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, this._areaId);
            this.initAreaSelectorConfig(data);
          });
        }
      });
    } else {
      this.$facilityUtilService.getArea().then((data: NzTreeNode[]) => {
        this.areaNodes = data;
        // ?????????????????????????????????
        this.$facilityUtilService.setAreaNodesStatus(this.areaNodes, this._areaId);
        this.initAreaSelectorConfig(data);
      });
    }
  }

  /**
   * ??????title??????
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
   * ???????????????????????????????????????
   * param event
   */
  formInstance(event) {
    this.formStatus = event.instance;
    // event.instance.column[1].initialValue = '??????';
  }

  /**
   * ??????
   */
  goBack() {
    window.history.back();
  }

  /**
   * ??????/??????/??????????????????
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
      data.procResourceType = '1'; // ?????????????????????????????????????????????
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
   * ?????????????????????
   */
  showAreaSelectorModal() {
    if (!this.disabledIf) {
      this.areaSelectorConfig.treeNodes = this.areaNodes;
      this.areaSelectVisible = true;
    }
  }

  /**
   * ??????????????????
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
   * ????????????????????????
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
   * ??????????????????????????????
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
   * ???????????????????????????
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
   * ????????????ID??????????????????
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
   * ??????????????????????????????
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
   * ???????????????????????????
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
   * ????????????????????????
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
   * ??????????????????????????????
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
   * ????????????
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
   * ??????id?????????????????????
   * param id
   */
  private getUpdateWorkUnfinished(id) {
    this.$inspectionService.getUpdateWorkUnfinished(id).subscribe((result: Result) => {
      if (result.code === 0) {
        result.data.procResourceType = '??????';
        this.formStatus.resetData(result.data);
      }
    });
  }

  /**
   * ??????????????????????????????????????????
   */
  changeInspectionFacilities(event) {
    if (event === '0') {
      this.inspectionFacilitiesSelectorDisabled = false;
    } else if (event === '1') {
      this.inspectionFacilitiesSelectorDisabled = true;
      // ????????????ID????????????
      this.InquireAreaId = [this.areaId];
      this.$mapService.getALLFacilityList().subscribe((result: Result) => {
        const arrTemp = result.data || [];
        this.facilityData = arrTemp.filter(item => {
            // ????????? areaId ???deviceType ????????????
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
   * ??????????????????
   */
  judgePageJump() {
    this.$activatedRoute.queryParams.subscribe(params => {
      if (params.procId) {
        this.procId = params.procId;
        this.queryDeptList().then(() => {
          this.$facilityUtilService.getArea().then((data) => {
            this.areaNodes = data;
            // ?????????????????????????????????
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
   *  ??????????????????????????????????????????
   */
  disabledDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.today) < 0;
  }
  /**
   *  ????????????????????????????????????????????????
   */
  disabledEndDate = (current: Date): boolean => {
    return differenceInCalendarDays(current, this.dateStart) < 0;
  }

  /**
   * ????????????????????????
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
