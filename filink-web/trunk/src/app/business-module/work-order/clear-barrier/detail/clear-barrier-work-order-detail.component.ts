import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {NzI18nService, NzModalService} from 'ng-zorro-antd';
import {AbstractControl, FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {Result} from '../../../../shared-module/entity/result';
import {ClearBarrierService} from '../../../../core-module/api-service/work-order/clear-barrier';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {WorkOrderConfig} from '../../work-order.config';
import {FormLanguageInterface} from '../../../../../assets/i18n/form/form.language.interface';
import {ClearBarrierWorkOrder} from '../../../../core-module/entity/work-order/clear-barrier-work-order';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {PageCondition, QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {AlarmLanguageInterface} from '../../../../../assets/i18n/alarm/alarm-language.interface';
import {AlarmService} from '../../../../core-module/api-service/alarm';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {differenceInCalendarDays} from 'date-fns';
import {CONST_NUMBER} from '../../../../shared-module/const/work-order';
import {getAlarmType} from '../../../facility/facility.config';

/**
 * 新增和编辑销账工单
 */
@Component({
  selector: 'app-clear-barrier-work-order-detail',
  templateUrl: './clear-barrier-work-order-detail.component.html',
  styleUrls: ['./clear-barrier-work-order-detail.component.scss']
})
export class ClearBarrierWorkOrderDetailComponent extends WorkOrderConfig implements OnInit {
  @ViewChild('accountabilityUnit') accountabilityUnit: TemplateRef<any>;
  @ViewChild('alarmTemp') alarmTemp: TemplateRef<any>;
  @ViewChild('alarmSelectorModalTemp') alarmSelectorModalTemp: TemplateRef<any>;
  @ViewChild('selectorModalTemp') selectorModalTemp: TemplateRef<any>;
  @ViewChild('radioTemp') radioTemp: TemplateRef<any>;
  @ViewChild('ecTimeTemp') ecTimeTemp: TemplateRef<any>;
  public formLanguage: FormLanguageInterface;
  public alarmLanguage: AlarmLanguageInterface;
  public unitDisabled: boolean;
  public alarmDisabled: boolean;
  public accountabilityUnitList = [];
  public isAllChecked = false;
  public isIndeterminate = false;
  public isLoading = false;
  private selectedAccountabilityUnitIdList = [];
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 0);
  tableConfig: TableConfig;
  queryCondition: QueryCondition;
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  pageTitle;
  pageType = '';
  selectUnitName = '';
  alarmName = '';
  params: ClearBarrierWorkOrder = new ClearBarrierWorkOrder();
  selectedAlarm = {};
  _selectedAlarm;
  selectedAlarmId = null;
  workOrderId;
  updateStatus;  // 表单状态  如果是编辑    1为重新生成 2为待指派 0为其他状态
  private constNumber;

  constructor(
    public $nzI18n: NzI18nService,
    private $activatedRoute: ActivatedRoute,
    private $router: Router,
    private $modal: NzModalService,
    private $clearBarrierService: ClearBarrierService,
    private $alarmService: AlarmService,
    private $message: FiLinkModalService,
    private $ruleUtil: RuleUtil
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.constNumber = CONST_NUMBER;
    this.formLanguage = this.$nzI18n.getLocaleData('form');
    this.alarmLanguage = this.$nzI18n.getLocaleData('alarm');
    this.procType = 'clear_failure';
    this.pageType = this.$activatedRoute.snapshot.url[1].path;
    this.pageTitle = this.getPageTitle(this.pageType);
    this.workOrderTypeListArr = this.workOrderTypeListArr.filter(item => item.value === 'clear_failure');
    if (this.pageType === 'add') {
    } else {
      this.workOrderId = this.$activatedRoute.snapshot.queryParams.id;
      this.getWorkOrderDetail();
    }
    this.initColumn();
    // this.initTableConfig();
  }

  /**
   * 获取标题
   * param type
   * returns {string}
   */
  private getPageTitle(type): string {
    let title;
    switch (type) {
      case 'add':
        title = this.workOrderLanguage.addClearBarrierWorkOrder;
        break;
      case 'update':
        title = this.workOrderLanguage.modifyClearBarrierWorkOrder;
        break;
      case 'view':
        title = this.workOrderLanguage.viewClearBarrierWorkOrder;
        break;
      case 'rebuild':
        title = '重新生成工单';
        break;
    }
    return title;
  }

  /**
   * 返回
   */
  goBack() {
    // this.$router.navigate(['/business/work-order/clear-barrier/unfinished']).then();
    window.history.back();
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  /**
   * 打开部门选择modal
   */
  showSelectorModal() {
    if (this.unitDisabled) {
      return;
    }
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
    this.selectUnitName = this.accountabilityUnitList.filter(item => item.checked).map(item => item.label).join(',');
    // this.formStatus.resetControlData('accountabilityDept', this.selectedAccountabilityUnitIdList);
    modal.destroy();
  }

  /**
   * 获取告警关联设施对应的单位
   */
  getAccountabilityUnitList() {
    this.$alarmService.queryDepartmentId([this.selectedAlarmId]).subscribe((result: Result) => {
      if (result.code === 0 && result.data.length > 0) {
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
   * 表单提交按钮检查
   */
  confirmButtonIsGray() {
    if (this.pageType === 'add' && this.formStatus.getValid()) {
      return true;
    } else {
      // if(this.formStatus.group.controls)
      // if (this.formStatus.group.controls['title'].valid && this.formStatus.group.controls['ectime'].valid
      //   && this.formStatus.group.controls['remark'].valid && this.formStatus.group.controls['refAlarm'].valid) {
      //   return true;
      // } else {
      //   return false;
      // }
    }
  }

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
    this.isIndeterminate = false;
    this.accountabilityUnitList.forEach(item => {
      item.checked = event;
    });
  }

  /**
   * 告警选择modal
   */
  showAlarmSelectorModal() {
    this.initTableConfig();
    this.refAlarmList();
    this.queryCondition = new QueryCondition();
    this.resetPageCondition();
    // if(this._selectedAlarm&&)
    if (this.unitDisabled) {
      return;
    }
    this.selectedAlarmId = this.selectedAlarm['id'];
    const modal = this.$modal.create({
      nzTitle: this.workOrderLanguage.refAlarm,
      nzContent: this.alarmSelectorModalTemp,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzWidth: 1000,
      nzFooter: [
        {
          label: this.commonLanguage.confirm,
          onClick: () => {
            this.selectAlarm(modal);
          }
        },
        {
          label: this.commonLanguage.cancel,
          type: 'danger',
          onClick: () => {
            this._dataSet = [];
            modal.destroy();
          }
        },
      ],
    });
    modal.afterOpen.subscribe(() => {
      this.refreshData();
    });
  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 选择告警     只能选单条
   * param modal
   */
  selectAlarm(modal) {
    if (this.selectedAlarmId) {
      modal.destroy();
      if (this._selectedAlarm) {
        this.selectedAlarm = CommonUtil.deepClone(this._selectedAlarm);
        this.selectUnitName = null;
        this.selectedAccountabilityUnitIdList = [];
        this.accountabilityUnitList = [];
        this.isAllChecked = false;
        this.alarmName = this.selectedAlarm['alarmName'];
        this.formStatus.resetControlData('refAlarm', this.selectedAlarm);
      } else {
        this.alarmName = this.selectedAlarm['alarmName'];
        this.formStatus.resetControlData('refAlarm', this.selectedAlarm);
      }
    } else {
      this.$message.warning(this.workOrderLanguage.alarmSelectedError);
    }
  }

  /**
   * 选择告警
   * param event
   * param data
   */
  selectedAlarmChange(event, data) {
    this._selectedAlarm = data;
  }

  /**
   * 获取当前告警
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$alarmService.queryAlarmCurrentPage(this.queryCondition).subscribe((res: Result) => {
      this.pageBean.Total = res.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = res.data;
      this._dataSet.forEach(item => {
        item.deviceTypeName = this.getFacilityTypeName(item.alarmSourceTypeId);
        item.alarmName = getAlarmType(this.$nzI18n, item.alarmCode);
      });
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 获取工单详情
   */
  getWorkOrderDetail() {
    this.$clearBarrierService.getWorkOrderDetailById(this.workOrderId).subscribe((result: Result) => {
      if (result.code === 0) {
        // this.setWorkOrderData(result.data);
        if (result.data.status === 'assigned') {
          this.updateStatus = this.constNumber.TWO;
        } else if (result.data.status === 'singleBack') {
          this.updateStatus = this.constNumber.ONE;
        } else {
          this.updateStatus = this.constNumber.THREE;
          this.unitDisabled = true;
          this.alarmDisabled = true;
          this.formStatus.group.controls['title'].disable();
          this.formStatus.group.controls['ecTime'].disable();
        }
        this.setWorkOrderData(result.data);
      }
    }, () => {
    });
  }

  /**
   * 设置数据
   * param data
   */
  setWorkOrderData(data) {
    this.selectedAlarmId = data.refAlarm;
    this.alarmName = data.refAlarmName;
    if (data.procRelatedDepartments) {
      this.selectedAccountabilityUnitIdList = data.procRelatedDepartments.map(item => item.accountabilityDept);
    }
    this.selectUnitName = data.accountabilityDeptName;
    this.formStatus.resetControlData('title', data['title']);
    this.formStatus.resetControlData('refAlarm', this.selectedAlarm);
    this.formStatus.resetControlData('remark', data['remark']);
    if (data['ecTime']) {
      this.formStatus.resetControlData('ecTime', new Date(CommonUtil.convertTime(data['ecTime'])));
    }
    this.selectedAlarm = {
      alarmName: data.refAlarmName,
      alarmSource: data.deviceId,
      alarmSourceTypeId: data.deviceType,
      alarmObject: data.deviceName,
      areaId: data.deviceAreaId,
      areaName: data.deviceAreaName,
      id: data.refAlarm,
      alarmCode: data.refAlarmCode,
      procId: data.procId
    };
  }

  /**
   * 提交
   */
  submit() {
    this.isLoading = true;
    const data = this.setSubmitData();
    const methodName = this.pageType === 'add' ? 'addWorkOrder' : 'updateWorkOrder';
    if (this.formStatus.group.controls.ecTime.value
      && CommonUtil.sendBackEndTime(new Date(this.formStatus.group.controls.ecTime.value).getTime()) < new Date().getTime()
      && (this.updateStatus !== 0 && this.formStatus.group.dirty)) {
      this.$message.warning(this.commonLanguage.expectCompleteTimeMoreThanNowTime);
      this.isLoading = false;
      // this.setWorkOrderData(result.data);
      return null;
    } else {
      if (this.updateStatus === this.constNumber.ONE) {
        data['regenerateId'] = data['procId'];
        data['procId'] = null;
        this.$clearBarrierService.RefundGeneratedAgain(data).subscribe((result: Result) => {
          if (result.code === 0) {
            this.$router.navigate(['/business/work-order/clear-barrier/unfinished-list']).then();
            if (this.pageType !== 'add') {
              this.$message.success(this.workOrderLanguage.regeneratedSuccessful);
            }
          } else {
            this.isLoading = false;
            this.$message.error(result.msg);
          }
        });
      } else {
        this.$clearBarrierService[methodName](data).subscribe((result: Result) => {
          if (result.code === 0) {
            this.$router.navigate(['/business/work-order/clear-barrier/unfinished-list']).then();
            if (this.pageType !== 'add') {
              this.$message.success(result.msg);
            }
          } else {
            this.isLoading = false;
            this.$message.error(result.msg);
          }
        }, () => {
          this.isLoading = false;
        });
      }
    }

  }

  /**
   * 设置提交数据
   */
  setSubmitData() {
    const formData = this.formStatus.group.getRawValue();
    const ids = this.selectedAccountabilityUnitIdList.map(item => {
      return {accountabilityDept: item};
    });
    const data = {
      'title': formData.title,
      'refAlarm': this.selectedAlarmId,
      'refAlarmName': this.alarmName,
      'deviceId': this.selectedAlarm['alarmSource'],
      'deviceName': this.selectedAlarm['alarmObject'],
      'deviceType': this.selectedAlarm['alarmSourceTypeId'],
      'deviceAreaId': this.selectedAlarm['areaId'],
      'deviceAreaName': this.selectedAlarm['areaName'],
      'accountabilityDeptList': ids,
      'remark': formData.remark,
      'refAlarmCode': this.selectedAlarm['alarmCode'],
    };
    if (formData.ecTime) {
      data['ecTime'] = CommonUtil.sendBackEndTime(new Date(formData.ecTime).getTime());
    }
    if (this.pageType !== 'add') {
      data['procId'] = this.workOrderId;
    }
    return data;
  }

  /**
   * form配置
   */
  private initColumn() {
    this.formColumn = [
      {
        label: this.workOrderLanguage.name,
        key: 'title',
        type: 'input',
        require: true,
        rule: [
          {required: true},
          RuleUtil.getNameMinLengthRule(),
          RuleUtil.getNameMaxLengthRule(),
          this.$ruleUtil.getNameRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
        asyncRules: [
          // {
          //   asyncRule: (control: FormControl) => {
          //     return Observable.create(observer => {
          //       const id = this.updateStatus ? null : this.workOrderId;
          //       this.$clearBarrierService.checkName(control.value, id).subscribe((result: Result) => {
          //         console.log(result);
          //         if (result.code === 160101) {   // 名称重复
          //           observer.next({error: true, duplicated: true});
          //           observer.complete();
          //         } else {
          //           observer.next(null);
          //           observer.complete();
          //         }
          //       });
          //     });
          //   },
          //   asyncCode: 'duplicated', msg: this.formLanguage.nameExistError
          // }
          // this.$ruleUtil.getNameAsyncRule(value => this.$clearBarrierService.checkName(value,
          //   this.updateStatus ? null : this.workOrderId),
          //   res => res.code === 0)
        ],
      },
      {
        label: this.workOrderLanguage.type, key: 'procType', type: 'input',
        disabled: true,
        initialValue: this.workOrderTypeListArr[0].label, rule: []
      },
      {
        label: this.workOrderLanguage.refAlarm, key: 'refAlarm', type: 'custom',
        require: true,
        template: this.alarmTemp,
        modelChange: (controls, $event, key) => {
        },
        rule: [{required: true}], asyncRules: []
      },
      {
        label: this.workOrderLanguage.accountabilityUnitName, key: 'accountabilityDept', type: 'custom',
        template: this.accountabilityUnit,
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {

        },
        rule: [], asyncRules: []
      },
      {
        label: this.workOrderLanguage.expectedCompleteTime,
        key: 'ecTime',
        type: 'custom',
        // type: 'time-picker',
        template: this.ecTimeTemp,
        require: true,
        rule: [{required: true}],
        customRules: [{
          code: 'isTime', msg: null, validator: (control: AbstractControl): { [key: string]: boolean } => {
            if (control.value && CommonUtil.sendBackEndTime(new Date(control.value).getTime()) < new Date().getTime()) {
              if (this.formStatus.group.controls['ecTime'].dirty) {
                this.$message.info(this.commonLanguage.expectCompleteTimeMoreThanNowTime);
                return {isTime: true};
              }
            } else {
              return null;
            }
          }
        }]
      },
      {
        label: this.workOrderLanguage.remark, key: 'remark', type: 'input',
        rule: [this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  disabledEndDate = (current: Date): boolean => {
    const nowTime = new Date();
    return differenceInCalendarDays(current, nowTime) < 0 || CommonUtil.checkTimeOver(current);
  };

  /**
   * 获取关联告警下拉框数据
   */
  refAlarmList() {
    const alarmQueryCondition = new QueryCondition();
    alarmQueryCondition.pageCondition = new PageCondition(1, 20);
    this.$alarmService.queryAlarmCurrentSetList(alarmQueryCondition).subscribe((result: Result) => {
      const data = result.data;
      const arr = data.map(item => {
        return {value: item.alarmName, label: item.alarmName};
      });
      // this.tableConfig.columnConfig[0]['searchConfig']['selectInfo'] = this.selectOption;
      this.tableConfig.columnConfig.forEach(item => {
        if (item.searchKey === 'alarmName') {
          item['searchConfig']['selectInfo'] = arr;
        }
      });
    });
  }

  /**
   * 关联告警选择table配置
   */
  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showRowSelection: false,
      showSizeChanger: false,
      notShowPrint: true,
      noIndex: true,
      simplePage: true,
      scroll: {x: '650px', y: '600px'},
      columnConfig: [
        {
          title: '',
          type: 'render',
          key: 'selectedAlarmId',
          renderTemplate: this.radioTemp,
          fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 42
        },
        {
          title: this.alarmLanguage.alarmName, key: 'alarmName', width: 150,
          searchable: true,
          searchKey: 'alarmName',
          isShowSort: true,
          searchConfig: {type: 'select', selectType: 'multiple'}
        },
        {
          title: this.alarmLanguage.alarmobject, key: 'alarmObject', width: 120,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.alarmLanguage.alarmSourceType, key: 'deviceTypeName', width: 120,
          searchable: true,
          searchKey: 'alarmSourceTypeId',
          isShowSort: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: this.facilityTypeListArr}
        },
        {
          title: this.alarmLanguage.areaName, key: 'areaName', width: 108,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.alarmLanguage.alarmBeginTime, key: 'alarmBeginTime', pipe: 'date', width: 163,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'dateRang'}
        },
        {
          title: this.alarmLanguage.alarmNearTime, key: 'alarmNearTime', pipe: 'date',
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'dateRang'}
        },
        {
          title: '', searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 75, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      sort: (event: SortCondition) => {
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        this.queryCondition.filterConditions = event;
        this.refreshData();
      }
    };
  }
}
