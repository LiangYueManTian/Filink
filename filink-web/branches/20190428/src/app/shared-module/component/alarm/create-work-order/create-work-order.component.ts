import {Component, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {FormItem} from '../../form/form-config';
import {FormOperate} from '../../form/form-opearte.service';
import {Result} from '../../../entity/result';
import {FiLinkModalService} from '../../../service/filink-modal/filink-modal.service';
import {AlarmService} from '../../../../core-module/api-service/alarm';
import {NzI18nService} from 'ng-zorro-antd';
import {AlarmLanguageInterface} from '../../../../../assets/i18n/alarm/alarm-language.interface';
import {RuleUtil} from '../../../util/rule-util';
import {AbstractControl} from '@angular/forms';
import { CommonLanguageInterface } from '../../../../../assets/i18n/common/common.language.interface';
import {differenceInCalendarDays} from 'date-fns';

@Component({
  selector: 'app-create-work-order',
  templateUrl: './create-work-order.component.html',
  styleUrls: ['./create-work-order.component.scss']
})
export class CreateWorkOrderComponent  {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  isLoading: boolean = false;
  display = {
    creationWorkOrder: false
  };
  ecTimeModel;
  // 创建工单的数据
  _creationWorkOrderData = {};
  public language: AlarmLanguageInterface;
  private firstChange: boolean = true;
  private commonLanguage: CommonLanguageInterface;
  @ViewChild('ecTimeTemp') private ecTimeTemp;
  constructor(
    public $message: FiLinkModalService,
    public $alarmService: AlarmService,
    public $nzI18n: NzI18nService,
    private $ruleUtil: RuleUtil,
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
  }
  @Input()
  set creationWorkOrderData( creationWorkOrderData ) {
    if ( creationWorkOrderData ) {
      this._creationWorkOrderData = creationWorkOrderData;
      setTimeout( () => {
        this.display.creationWorkOrder = true;
      }, 0);
      this.initForm();
    }
  }
  @Output() close = new EventEmitter();
  // 关闭弹框
  closePopUp() {
    this.display.creationWorkOrder = false;
    this.close.emit();
  }
  /**
   * 禁用时间
   * param {Date} current
   * returns {boolean}
   */
  disabledEndDate = (current: Date): boolean => {
    const nowTime = new Date();
    return differenceInCalendarDays(current, nowTime) < 0;
  }
  // 创建工单表单
  public initForm() {
    this.formColumn = [
      {
        // 工单名称
        label: this.language.workOrderName,
        key: 'createWork',
        type: 'input',
        require: true,
        col: 18,
        // width: 300,
        rule: [
          {required: true},
          RuleUtil.getNameMinLengthRule(),
          {maxLength: 32},
          this.$ruleUtil.getNameRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
      {
        // 期待完工时长(天)
        label: this.language.lastDays,
        key: 'ecTime',
        type: 'custom',
        template: this.ecTimeTemp,
        require: true,
        // col: 22,
        // width: 430,
        // customRules: [{
        //   code: 'isTime', msg: null, validator: (control: AbstractControl): { [key: string]: boolean } => {
        //     if (control.value && new Date(control.value).getTime() < new Date().getTime()) {
        //       if (!this.firstChange) {
        //         this.ecTimeModel = '';
        //         this.$message.info(this.commonLanguage.expectCompleteTimeMoreThanNowTime);
        //       }
        //       this.firstChange = false;
        //       return {isTime: true};
        //     } else {
        //       return null;
        //     }
        //   }
        // }],
        customRules: [{
          code: 'isTime', msg: null
          , validator: (control: AbstractControl): { [key: string]: boolean } => {
            if (control.value && new Date(control.value).getTime() < new Date().getTime()) {
              if (this.formStatus.group.controls['ecTime'].dirty) {
                this.$message.info(this.commonLanguage.expectCompleteTimeMoreThanNowTime);
                return {isTime: true};
              }
            } else {
              return null;
            }
          }
        }],
        rule: [{required: true}],
      }
    ];
  }
  // 创建工单弹框
  formInstance(event) {
    this.formStatus = event.instance;
  }

  // 创建工单 点击 确定
  submitWork() {
    const alarmObj = this.formStatus.getData();
    //  期待完工时间 不能小于当前时间
    const time = +new Date();
    if ( +alarmObj.ecTime < time ) {
      this.$message.info(this.language.ecTimeVerification);
      this.ecTimeModel = '';
      this.formStatus.resetControlData('ecTime', null);
    } else if ( this._creationWorkOrderData && !this._creationWorkOrderData['responsibleDepartmentId'] ) {
      this.$message.info(this.language.createWorkOrderMsg);
    } else {
        const creationWorkOrderData = this._creationWorkOrderData['responsibleDepartmentId'].split(',');
        const accountabilityDeptList = creationWorkOrderData.map(item => {
          return { 'accountabilityDept': item };
        });

      const worlOrderData = {
        'title': alarmObj.createWork,
        'refAlarm': this._creationWorkOrderData['id'],
        'refAlarmName': this._creationWorkOrderData['alarmName'],
        'refAlarmCode': this._creationWorkOrderData['alarmCode'],
        'ecTime': alarmObj.ecTime,
        // 'ecTime': 1555916540612,
        'deviceId': this._creationWorkOrderData['alarmSource'],
        'deviceName': this._creationWorkOrderData['alarmObject'],
        'deviceType': this._creationWorkOrderData['alarmSourceTypeId'],
        'deviceAreaId': this._creationWorkOrderData['areaId'],
        'deviceAreaName': this._creationWorkOrderData['areaName'],
        'accountabilityDeptList': accountabilityDeptList,
        'remark': this._creationWorkOrderData['remark'],
      };
      this.isLoading = true;
      this.$alarmService.addClearFailureProc(worlOrderData).subscribe((res: Result) => {
        this.isLoading = false;
        if ( res.code === 0 ) {
          this.closePopUp();
          this.$message.success(res.msg);
        } else {
          this.$message.error(res.msg);
        }
      });
    }
  }
  // ecTimeChange(event) {
  //   ;
  //   this.ecTimeModel = event;
  // }
  // 期待完工时间
  ecTimeOnOk(event) {
    if ( event ) { return; }
    //  期待完工时间 不能小于当前时间
    // const time = +new Date();
    // if ( this.ecTimeModel < time ) {
    //   this.$message.success('期待完工时间不能小于当前时间');
    //   this.ecTimeModel = '';
    //   this.formStatus.resetControlData('ecTime', null);
    // } else {
      this.formStatus.resetControlData('ecTime', this.ecTimeModel);
    // }
  }

}
