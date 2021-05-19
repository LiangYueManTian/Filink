import { Component, OnInit, ViewChild } from '@angular/core';
import { FormItem } from '../../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../../shared-module/component/form/form-opearte.service';
import { NzI18nService } from 'ng-zorro-antd';
import { AlarmLanguageInterface } from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import { UserService } from '../../../../../core-module/api-service/user';
import { ActivatedRoute, Router } from '@angular/router';
import { Result } from '../../../../../shared-module/entity/result';
import { FiLinkModalService } from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import { AlarmService } from '../../../../../core-module/api-service/alarm';
import { FacilityService } from '../../../../../core-module/api-service/facility/facility-manage';
import { getDeviceType } from '../../../../facility/facility.config';
import {RuleUtil} from '../../../../../shared-module/util/rule-util';
import { AreaConfig, AlarmNameConfig } from 'src/app/shared-module/component/alarm/alarmSelectorConfig';

@Component({
  selector: 'app-work-order-add',
  templateUrl: './work-order-add.component.html',
  styleUrls: ['./work-order-add.component.scss']
})

export class WorkOrderAddComponent implements OnInit {

  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: AlarmLanguageInterface;
  // 工单类型 0 为 巡检工单  1 为 销障工单
  public workOrderTypeList: Array<any> = [];
  // 触发条件
  public triggerCondition: Array<any> = [];
  public pageType = 'add';
  // 启用状态 默认 启用
  public isNoStartData: boolean = true;
  isLoading = false;
  @ViewChild('isNoStartUsing') private isNoStartUsing;
  @ViewChild('alarmName') private alarmName;
  @ViewChild('areaSelector') private areaSelector;
  @ViewChild('deviceTypeTemp') private deviceTypeTemp;
  // 勾选的告警名称
  checkAlarmName = {
    name: '',
    ids: []
  };
  // 标题
  title: string = '';
  // 编辑id
  updateParamsId;
  // 区域
  areaList = {
    ids: [],
    name: ''
  };
  areaConfig: AreaConfig;
  alarmNameConfig: AlarmNameConfig;
  // 设施类型
  deviceTypeList: [];
  deviceTypeListValue;

  constructor(
    public $nzI18n: NzI18nService,
    public $userService: UserService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $router: Router,
    public $alarmService: AlarmService,
    public $facilityService: FacilityService,
    private $ruleUtil: RuleUtil,
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
  }

  ngOnInit() {
    this.initForm();
    // this.initTableConfig();
    this.pageType = this.$active.snapshot.params.type;
    // 设施类型
    this.deviceTypeList = getDeviceType(this.$nzI18n);
    if (this.pageType === 'add') {
      // 新建
      this.title = this.language.workOrderAdd;
      // 区域
      this.initAreaConfig();
      // 告警名称
      this.initAlarmName();
    } else {
      // 编辑
      this.title = this.language.workOrderUpdate;
      this.$active.queryParams.subscribe(params => {
        this.updateParamsId = params.id;
        this.getAlarmData(params.id);
      });
    }
  }

  // 告警名称
  initAlarmName() {
    this.alarmNameConfig = {
      type: 'form',
      initialValue: this.checkAlarmName,
      alarmName: (event) => {
        const alarmName = event.ids.map(id => {
          return { 'alarmNameId': id };
        } );
        this.formStatus.resetControlData('alarmOrderRuleNameList', event.ids);
      }
    };
  }

  // 区域
  initAreaConfig() {
    this.areaConfig = {
      type: 'form',
      initialValue: this.areaList,
      checkArea: (event) => {
        this.areaList = event;
        this.formStatus.resetControlData('alarmOrderRuleArea', this.areaList.ids);
      }
    };
  }

  getAlarmData(id) {
    this.$alarmService.queryAlarmWorkById([id]).subscribe((res: Result) => {
      if (res['code'] === 0) {
        const alarmData = res.data[0];
        // 设置告警名称
        if (alarmData.alarmOrderRuleNameList && alarmData.alarmOrderRuleNameList.length &&
          alarmData.alarmOrderRuleNames && alarmData.alarmOrderRuleNames.length ) {
          const alarmName: string = alarmData.alarmOrderRuleNames.join(',');
          this.checkAlarmName = {
            name: alarmName,
            ids: alarmData.alarmOrderRuleNameList,
          };
          const checkAlarmNameIds = alarmData.alarmOrderRuleNameList.map(item => {
            return { 'alarmNameId': item.alarmNameId };
          });
          this.formStatus.resetControlData('alarmOrderRuleNameList', alarmData.alarmOrderRuleNameList);
          // 告警名称
          this.initAlarmName();
        }
        // 设施类型
        if ( alarmData.alarmOrderRuleDeviceTypeList && alarmData.alarmOrderRuleDeviceTypeList.length ) {
            this.deviceTypeListValue = alarmData.alarmOrderRuleDeviceTypeList.map(deviceType => deviceType.deviceTypeId );
            alarmData.deviceType = this.deviceTypeListValue;
        }
        // 启用 禁用状态
        if (alarmData.status) { this.isNoStartData = alarmData.status === 1 ? true : false; }
        // 区域
        if ( alarmData.alarmOrderRuleArea && alarmData.alarmOrderRuleArea.length
          && alarmData.alarmOrderRuleAreaName && alarmData.alarmOrderRuleAreaName.length ) {
          this.areaList = {
            ids: alarmData.alarmOrderRuleArea,
            name: alarmData.alarmOrderRuleAreaName.join(',')
          };
          // 区域
          this.initAreaConfig();
        }
        this.formStatus.resetData(alarmData);
      }
    });
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  // 表单
  public initForm() {
    this.formColumn = [
      {
        // 名称
        label: this.language.name,
        key: 'orderName',
        type: 'input',
        require: true,
        width: 430,
        // rule: [
        //   { required: true },
        //   { msg: this.language.name }
        // ],
        rule: [
          { required: true },
          { maxLength: 32 },
          this.$ruleUtil.getNameRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
      {
        // 告警名称
        label: this.language.alarmName,
        key: 'alarmOrderRuleNameList',
        type: 'custom',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        template: this.alarmName
      },
      {
        // 工单类型
        label: this.language.workOrderType,
        key: 'orderType',
        type: 'select',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        selectInfo: {
          data: this.workOrderTypeList = [
            // { label: this.language.pollingWork, value: 1 },
            { label: this.language.eliminateWork, value: 2 },
          ],
          label: 'label',
          value: 'value'
        }
      },
      {
        // 区域
        label: this.language.area,
        key: 'alarmOrderRuleArea', type: 'custom',
        template: this.areaSelector,
        require: true,
        width: 430,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 设施类型
        label: this.language.alarmSourceType, key: 'deviceType',
        type: 'custom',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        template: this.deviceTypeTemp,
        initialValue: getDeviceType(this.$nzI18n)
      },
      {
        // 触发条件  triggerCondition
        label: this.language.triggerCondition,
        key: 'triggerType',
        type: 'select',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        selectInfo: {
          data: this.triggerCondition = [
            { label: this.language.alarmHappenedTrigger, value: 0 },
            // { label: this.language.startUsingTrigger, value: 1 },
          ],
          label: 'label',
          value: 'value'
        }
      },
      {
        // 期待完工时长(天)
        label: this.language.expectAccomplishTime,
        key: 'completionTime',
        type: 'input',
        require: true,
        width: 430,
        initialValue: 3,
        rule: [
          { required: true },
          { min: 1 },
          { max: 365 },
          { pattern: /^\+?[1-9][0-9]*$/,
            msg: this.language.positiveInteger }
        ],
      },
      {
        // 是否启用
        label: this.language.openStatus,
        key: 'status',
        type: 'custom',
        template: this.isNoStartUsing,
        initialValue: this.isNoStartData,
        require: true,
        rule: [],
        asyncRules: [],
        radioInfo: {
          type: 'select', selectInfo: [
            { label: this.language.disable, value: 2 },
            { label: this.language.enable, value: 1 }
          ]
        },
      },
      {
        label: this.language.remark,
        key: 'remark',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          // { required: true },
          // { maxLength: 255 },
          this.$ruleUtil.getRemarkMaxLengthRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  cancel() {
    this.$router.navigate(['business/alarm/alarm-work-order']).then();
  }

  /**
   *新增告警
  */
  submit() {
    this.isLoading = true;
    const alarmObj = this.formStatus.getData();
    alarmObj.orderName = alarmObj.orderName.trim();
    alarmObj.remark = alarmObj.remark && alarmObj.remark.trim();
    // 禁启用状态 需要通过转化
    alarmObj.status = this.isNoStartData ? 1 : 2;
    alarmObj.alarmOrderRuleDeviceTypeList = this.deviceTypeListValue.map(deviceTypeId => {
      return {'deviceTypeId': deviceTypeId };
    });
    // 区域
    // const areaName = this.areaList.name.split(',');
    // alarmObj.alarmOrderRuleAreaList = areaName.map(name => {
    //   return {'area': name  };
    // });
    // alarmObj.alarmOrderRuleAreaList
    if (this.pageType === 'add') {
      this.$alarmService.addAlarmWork(alarmObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res && res.code === 0) {
          this.$message.success(res.msg);
          this.$router.navigate(['business/alarm/alarm-work-order']).then();
        }
      });
    } else {
      alarmObj.id = this.updateParamsId;
      this.$alarmService.updateAlarmWork(alarmObj).subscribe((res: Result) => {
        if (res && res.code === 0) {
          this.$message.success(res.msg);
          this.$router.navigate(['business/alarm/alarm-work-order']).then();
        }
      });
    }
  }

}
