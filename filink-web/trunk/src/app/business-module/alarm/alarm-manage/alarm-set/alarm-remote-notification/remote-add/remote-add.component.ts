import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { FormItem } from '../../../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../../../shared-module/component/form/form-opearte.service';
import { NzI18nService } from 'ng-zorro-antd';
import { AlarmLanguageInterface } from '../../../../../../../assets/i18n/alarm/alarm-language.interface';
import { UserService } from '../../../../../../core-module/api-service/user';
import { ActivatedRoute, Router } from '@angular/router';
import { Result } from '../../../../../../shared-module/entity/result';
import { FiLinkModalService } from '../../../../../../shared-module/service/filink-modal/filink-modal.service';
import { QueryCondition } from '../../../../../../shared-module/entity/queryCondition';
import { AlarmService } from '../../../../../../core-module/api-service/alarm';
import { FacilityService } from '../../../../../../core-module/api-service/facility/facility-manage';
import { TableComponent } from 'src/app/shared-module/component/table/table.component';
import { getDeviceType, getAlarmLevel } from '../../../../../facility/facility.config';
import { NzTreeNode } from 'ng-zorro-antd';
import {RuleUtil} from '../../../../../../shared-module/util/rule-util';
import { AreaConfig, User } from 'src/app/shared-module/component/alarm/alarmSelectorConfig';
/**
 * 告警设置-告警远程通知 新增和编辑页面
 */
@Component({
  selector: 'app-remote-add',
  templateUrl: './remote-add.component.html',
  styleUrls: ['./remote-add.component.scss']
})
export class RemoteAddComponent implements OnInit {

  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;

  public pageType = 'add';
  // 是否存库 默认 是
  public defaultStatus: string = '1'; // 默认状态
  // 启用状态 默认 启用
  public isNoStartData: boolean = true;
  isLoading = false;
  @ViewChild('isNoStartUsing') private isNoStartUsing;
  @ViewChild('xCTableComp') private xCTableComp: TableComponent;
  @ViewChild('notifierTemp') notifierTemp: TemplateRef<any>;
  @ViewChild('alarmFixedLevelListTemp') alarmFixedLevelListTemp: TemplateRef<any>;
  @ViewChild('areaSelector') private areaSelector;
  @ViewChild('deviceTypeTemp') private deviceTypeTemp;

  areaConfig: AreaConfig;
  // 勾选的通知人
  checkAlarmNotifier = {
    name: '',
    ids: []
  };
  display = {
    deviceTypeDisplay: true,
    areadisplay: true
  };
  // 标题
  title: string = '';
  // 编辑状态下 告警id
  alarmId;
  // 基本类型
  fundamental: string;
  // 过滤条件
  filtration: string;
  // 编辑id
  updateParamsId;
  // 告警级别 多选值
  alarmFixedLevelListValue = [];
  // 告警级别 1 紧急  2 主要 3 次要 4 提示
  public alarmFixedLevelList;

  // 区域
  areaList = {
    ids: [],
    name: ''
  };
  // 设施类型
  deviceTypeListValue = [];
  deviceTypeList = [];
  allDeviceTypeList = [];
  alarmUserConfig: User;
  /**
   *  通知人默认值
   *  1 deptList 部门
   *  2 deviceTypes 为设施类型
   *  */
  alarmNotifierInitialValue = {
    'deptList': [],
    'deviceTypes': [],
  };
  // 登录用户
  userInfo;
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
    // 所有设施类型
    this.allDeviceTypeList = getDeviceType(this.$nzI18n);
    // 告警级别
    this.alarmFixedLevelList = getAlarmLevel(this.$nzI18n);
  }

  ngOnInit() {
    this.initForm();
    this.fundamental = this.language.fundamental;
    this.filtration = this.language.filtration;
    this.pageType = this.$active.snapshot.params.type;
    if (this.pageType === 'add') {
      // 新建
      this.title = this.language.remoteNotificationAdd;
      // 区域
      this.initAreaConfig();
      // 通知人
      this.initAlarmUserConfig();
    } else {
      // 编辑
      this.title = this.language.remoteNotificationUpdate;
      this.$active.queryParams.subscribe(params => {
        this.updateParamsId = params.id;
        this.getAlarmData(params.id);
      });
    }
  }

  // 通知人
  initAlarmUserConfig() {
    const clear = this.checkAlarmNotifier.ids.length ? false : true;
    this.alarmUserConfig = {
      type: 'form',
      clear: clear,
      disabled: this.display.areadisplay,
      condition: this.alarmNotifierInitialValue,
      initialValue: this.checkAlarmNotifier,
      checkUser: (event) => {
        this.checkAlarmNotifier = event;
        this.formStatus.resetControlData('alarmForwardRuleUserList', this.checkAlarmNotifier.ids);
      }
    };
  }

  getAlarmData(id) {
    this.$alarmService.queryAlarmRemoteById([id]).subscribe((res: Result) => {
      if (res['code'] === 0) {
        const alarmData = res.data[0];
        this.display = {
          deviceTypeDisplay: false,
          areadisplay: false
        };
        // 区域
        if ( alarmData.alarmForwardRuleAreaList && alarmData.alarmForwardRuleAreaList.length
          && alarmData.alarmForwardRuleAreaName && alarmData.alarmForwardRuleAreaName.length ) {
          this.areaList = {
            ids: alarmData.alarmForwardRuleAreaList,
            name: alarmData.alarmForwardRuleAreaName.join(',')
          };
          // this.alarmNotifierInitialValue.deptList
          // 区域
          this.initAreaConfig();
          // 通过区域获取单位
          this.areaGtUnit();
          // 通过区域获取设施类型
          this.getDeviceType();
          // this.initAreaConfig();
        }
        setTimeout(() => {
          // 设施类型
          if ( alarmData.alarmForwardRuleDeviceTypeList && alarmData.alarmForwardRuleDeviceTypeList.length ) {
            this.deviceTypeListValue = alarmData.alarmForwardRuleDeviceTypeList.map(deviceType => deviceType.deviceTypeId );
            this.changeDeviceType();
          }
        }, 0);
        setTimeout(() => {
          // 给通知人赋值
          if ( alarmData.alarmForwardRuleUserList && alarmData.alarmForwardRuleUserList.length
            && alarmData.alarmForwardRuleUserName && alarmData.alarmForwardRuleUserName.length
            && this.alarmNotifierInitialValue.deptList.length && this.alarmNotifierInitialValue.deviceTypes.length) {
            this.checkAlarmNotifier = {
              name: alarmData.alarmForwardRuleUserName.join(','),
              ids: alarmData.alarmForwardRuleUserList
            };
            alarmData.alarmForwardRuleUserList = alarmData.alarmForwardRuleUserList;
            // 通知人
            this.initAlarmUserConfig();
          }
        }, 1000);
        // 告警级别
        if ( alarmData.alarmForwardRuleLevels && alarmData.alarmForwardRuleLevels.length ) {
          this.alarmFixedLevelListValue = alarmData.alarmForwardRuleLevels.map(level => level.alarmLevelId );
          alarmData.alarmForwardRuleLevels = this.alarmFixedLevelListValue;
        }
        // 推送方式
        alarmData.pushType = alarmData.pushType + '';
        // 启用 禁用状态
        if (alarmData.status) { this.isNoStartData = alarmData.status === 1 ? true : false; }
        this.formStatus.resetData(alarmData);
      }
    });
  }

  // 区域
  initAreaConfig() {
    const clear = this.areaList.ids.length ? false : true;
    this.areaConfig = {
      clear: clear,
      type: 'form',
      initialValue: this.areaList,
      checkArea: (event) => {
        this.areaList = event;
        this.areaGtUnit();
        this.formStatus.resetControlData('alarmForwardRuleAreaList', this.areaList.ids);
        if ( this.areaList && this.areaList.ids && this.areaList.ids.length ) {
          this.display.deviceTypeDisplay = false;
          // 通过区域获取设施类型
          this.getDeviceType();
          // 当区域的值改变时, 设施类型的值 重新选择
          this.deviceTypeListValue = [];
          this.formStatus.resetControlData('alarmForwardRuleDeviceTypeList', this.deviceTypeListValue);
          // 勾选的通知人
          this.checkAlarmNotifier = {
            name: '',
            ids: []
          };
          this.display.areadisplay = true;
          this.formStatus.resetControlData('alarmForwardRuleUserList', this.checkAlarmNotifier.ids);
          this.initAlarmUserConfig();
        } else {
          // 当区域为空时 设施类型 通知人禁 空
          this.empty();
        }
      }
    };
  }

  // 清空区域
  empty() {
    this.display = {
      deviceTypeDisplay: true,
      areadisplay: true
    };
    this.deviceTypeListValue = [];
    this.checkAlarmNotifier = {
      name: '',
      ids: []
    };
    this.initAlarmUserConfig();
  }
  // 通过区域获取设施类型
  getDeviceType() {
    this.$alarmService.getDeviceType(this.areaList.ids).subscribe((data: NzTreeNode[]) => {
      if ( data['code'] === 0) {
        const deviceTypeList = [];
        this.allDeviceTypeList.forEach(item => {
          data['data'].forEach(id => {
            if ( id === item.code ) {
              deviceTypeList.push(item);
            }
          });
        });
        this.deviceTypeList = deviceTypeList;
      }
    });
  }

  // 通过选择的区域 获取单位
  areaGtUnit() {
    this.$alarmService.areaGtUnit(this.areaList.ids).subscribe((data: NzTreeNode[]) => {
      if ( data['code'] === 0) {
        if ( data['data'] && data['data'].length ) {
          this.alarmNotifierInitialValue.deptList = data['data'].map(item => item.deptId);
        } else {
          setTimeout( () => {
            this.areaList = {
              ids: [],
              name: ''
            };
            this.initAreaConfig();
            this.display.deviceTypeDisplay = true;
            this.deviceTypeListValue = [];
            // 勾选的通知人
            this.checkAlarmNotifier = {
              name: '',
              ids: []
            };
            this.display.areadisplay = true;
            this.initAlarmUserConfig();
          }, 0);
          this.$message.info(this.language.noResponsibleEntityIsAssociatedWithTheSelectedArea);
        }
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
        label: this.language.name,
        key: 'ruleName',
        type: 'input',
        require: true,
        width: 280,
        rule: [
          { required: true },
          { maxLength: 32 },
          this.$ruleUtil.getNameRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
      {
        // 区域
        label: this.language.area,
        key: 'alarmForwardRuleAreaList', type: 'custom',
        width: 430,
        template: this.areaSelector,
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 设施类型
        label: this.language.alarmSourceType, key: 'alarmForwardRuleDeviceTypeList',
        type: 'custom',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        template: this.deviceTypeTemp,
      },
      {
        // 通知人
        label: this.language.notifier,
        key: 'alarmForwardRuleUserList',
        type: 'custom',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        template: this.notifierTemp,
      },
      {
        // 告警级别
        label: this.language.alarmFixedLevel,
        key: 'alarmForwardRuleLevels',
        type: 'custom',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        template: this.alarmFixedLevelListTemp,
        initialValue: this.alarmFixedLevelList
      },
      {
        // 是否启用
        label: this.language.openStatus,
        key: 'status',
        type: 'custom',
        template: this.isNoStartUsing,
        initialValue: this.isNoStartData,
        require: true,
        rule: [{ required: true }],
        asyncRules: [],
        radioInfo: {
          type: 'select', selectInfo: [
            { label: this.language.disable, value: 2 },
            { label: this.language.enable, value: 1 }
          ]
        },
      },
      {
        // 推送方式
        label: this.language.pushType,
        key: 'pushType',
        type: 'radio',
        require: true,
        width: 430,
        rule: [{ required: true }],
        initialValue: this.defaultStatus,
        radioInfo: {
          data: [
            { label: this.language.mail, value: '2' },
            { label: this.language.note, value: '1' },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, event, key, formOperate) => { }
      },
      {
        label: this.language.remark,
        key: 'remark',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          // { required: true },
          this.$ruleUtil.getRemarkMaxLengthRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  cancel() {
    this.$router.navigate(['business/alarm/alarm-remote-notification']).then();
  }

  onChangeLevel() {
    setTimeout( () => {
       if ( this.alarmFixedLevelListValue && !this.alarmFixedLevelListValue.length ) {
         this.formStatus.resetControlData('alarmForwardRuleLevels', []);
       }
    }, 0);
  }
  // 告警级别
  changeLevel() {
    this.formStatus.resetControlData('alarmForwardRuleLevels', this.alarmFixedLevelListValue.map(item => {
      return {'alarmLevelId': item };
    }));
  }

  onSearchDeviceType() {
    setTimeout( () => {
      if ( this.deviceTypeListValue && !this.deviceTypeListValue.length ) {
        this.formStatus.resetControlData('alarmForwardRuleDeviceTypeList', null);
        this.display.areadisplay = true;
        // 当设施类型为空时
        this.checkAlarmNotifier = {
          name: '',
          ids: []
        };
        this.formStatus.resetControlData('alarmForwardRuleUserList', this.checkAlarmNotifier.ids);
        this.initAlarmUserConfig();
      }
    }, 0);
  }
  // 设施类型
  changeDeviceType() {
    // 将值传递给 通知人
    this.alarmNotifierInitialValue = {
      'deptList': this.alarmNotifierInitialValue.deptList,
      'deviceTypes': this.deviceTypeListValue,
    };
    setTimeout( () => {
      // 设施类型选择后 开启通知人
      if ( this.deviceTypeListValue && this.deviceTypeListValue.length ) {
        this.display.areadisplay = false;
        this.formStatus.resetControlData('alarmForwardRuleDeviceTypeList',
          this.deviceTypeListValue.map(deviceTypeId => {
            return { 'deviceTypeId': deviceTypeId };
          }));
      } else {
        this.formStatus.resetControlData('alarmForwardRuleDeviceTypeList', null);
        this.display.areadisplay = true;
        // 当设施类型为空时
        this.checkAlarmNotifier = {
          name: '',
          ids: []
        };
        // this.initAlarmUserConfig();
      }
      this.initAlarmUserConfig();
    }, 0);
  }

  /**
   *新增告警
   */
  submit() {
    this.isLoading = true;
    const alarmObj = this.formStatus.getData();
    alarmObj.ruleName = alarmObj.ruleName.trim();
    alarmObj.remark = alarmObj.remark && alarmObj.remark.trim();
    // 禁启用状态 需要通过转化
    alarmObj.status = this.isNoStartData ? 1 : 2;
    // 设施类型
    alarmObj.alarmForwardRuleDeviceTypeList = this.deviceTypeListValue.map(deviceTypeId => {
      return { 'deviceTypeId': deviceTypeId };
    });
    // 告警级别
    alarmObj.alarmForwardRuleLevels  = this.alarmFixedLevelListValue.map(item => {
      return {'alarmLevelId': item };
    });
    if (this.pageType === 'add') {
      this.$alarmService.addAlarmRemote(alarmObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res && res.code === 0) {
          this.$message.success(res.msg);
          this.$router.navigate(['business/alarm/alarm-remote-notification']).then();
        }
      });
    } else {
      alarmObj.id = this.updateParamsId;
      this.$alarmService.updateAlarmRemarklist(alarmObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res && res.code === 0) {
          this.$message.success(res.msg);
          this.$router.navigate(['business/alarm/alarm-remote-notification']).then();
        }
      });
    }
  }

}
