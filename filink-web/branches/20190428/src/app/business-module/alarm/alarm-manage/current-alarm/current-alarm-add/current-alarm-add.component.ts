import {Component, OnInit, ViewChild, TemplateRef} from '@angular/core';
import {FormItem} from '../../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../../shared-module/component/form/form-opearte.service';
import {AlarmLanguageInterface} from '../../../../../../assets/i18n/alarm/alarm-language.interface';
import {UserService} from '../../../../../core-module/api-service/user';
import {ActivatedRoute, Router} from '@angular/router';
import {Result} from '../../../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmService} from '../../../../../core-module/api-service/alarm';
import {FacilityService} from '../../../../../core-module/api-service/facility/facility-manage';
import {TableComponent} from 'src/app/shared-module/component/table/table.component';
import {RuleUtil} from '../../../../../shared-module/util/rule-util';
import {NzI18nService} from 'ng-zorro-antd';
import {getDeviceType} from '../../../../facility/facility.config';
import {QueryCondition} from '../../../../../shared-module/entity/queryCondition';
import {TreeSelectorConfig} from '../../../../../shared-module/entity/treeSelectorConfig';
import {AreaConfig, AlarmNameConfig, AlarmObjectConfig, UnitConfig} from 'src/app/shared-module/component/alarm/alarmSelectorConfig';

@Component({
  selector: 'app-current-alarm-add',
  templateUrl: './current-alarm-add.component.html',
  styleUrls: ['./current-alarm-add.component.scss']
})
export class CurrentAlarmAddComponent implements OnInit {

  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  public pageType = 'add';
  // 是否存库 默认 是
  public defaultStatus: string = '1'; // 默认状态
  // 启用状态 默认 启用
  public isNoStartData: string = '1';
  // 勾选的责任单位
  checkUnit = {
    name: '',
    ids: []
  };
  // 勾选的告警对象
  checkAlarmObject = {
    name: '',
    ids: []
  };
  // 勾选的告警名称
  checkAlarmName = {
    name: '',
    ids: []
  };

  timeModel = {
    firstTimeModel: [],
    notarizeTimeModel: [],
    recentlyTimeModel: [],
    clearTimeModel: []
  };
  // 标题
  title: string = '';
  // 编辑id
  updateParamsId;
  isLoading: boolean = false;
  // 区域
  areaSelectorConfig: any = new TreeSelectorConfig();
  areaList = {
    ids: [],
    name: ''
  };
  areaConfig: AreaConfig;
  alarmNameConfig: AlarmNameConfig;
  alarmObjectConfig: AlarmObjectConfig;
  unitConfig: UnitConfig;
  createData;
  @ViewChild('firstTimeTemp') private firstTimeTemp;
  @ViewChild('notarizeTimeTemp') private notarizeTimeTemp;
  @ViewChild('recentlyTimeTemp') private recentlyTimeTemp: TableComponent;
  @ViewChild('clearTimeTemp') private clearTimeTemp: TableComponent;
  @ViewChild('alarmName') private alarmName;
  @ViewChild('department') private departmentTep;
  @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;
  @ViewChild('areaSelector') private areaSelector;
  @ViewChild('unitTemp') private unitTemp;
  @ViewChild('alarmContinueTimeTemp') private alarmContinueTimeTemp;

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
    this.pageType = this.$active.snapshot.params.type;
    if (this.pageType === 'add') {
      // 新建
      this.title = this.language.addAtPresentAlarm;
    } else {
      // 编辑
      this.title = this.language.updateAtPresentAlarm;
      this.$active.queryParams.subscribe(params => {
        this.updateParamsId = params.id;
        this.getAlarmData(params.id);
      });
    }
    // 区域
    this.initAreaConfig();
    // 告警名称
    this.initAlarmName();
    // 告警对象
    this.initAlarmObjectConfig();
    // 责任单位
    this.initUnitConfig();
  }

  // 责任单位
  initUnitConfig() {
    this.unitConfig = {
      type: 'form',
      initialValue: this.checkUnit,
      checkUnit: (event) => {
        this.checkUnit = event;
        // const name = this.checkUnit.name.split(',').map(item => {
        //   return { 'departmentName': item };
        // });
        const names = this.checkUnit.name.split(',');
        const departmentList = this.checkUnit.ids.map((id, index) => {
          return {'departmentName': names[index], 'departmentId': id};
        });
        this.formStatus.resetControlData('departmentList', departmentList);
      }
    };
  }

  // 告警对象
  initAlarmObjectConfig() {
    this.alarmObjectConfig = {
      type: 'form',
      initialValue: this.checkAlarmObject, // 默认值
      alarmObject: (event) => {
        this.checkAlarmObject = event;
        // const name = this.checkAlarmObject.name.split(',').map(item => {
        //   return { 'deviceName': item };
        // });
        const names = this.checkAlarmObject.name.split(',');
        const alarmObjectList = this.checkAlarmObject.ids.map((id, index) => {
          return {'deviceName': names[index], 'deviceId': id};
        });
        this.formStatus.resetControlData('alarmObjectList', alarmObjectList);
      }
    };
  }

  // 告警名称
  initAlarmName() {
    this.alarmNameConfig = {
      type: 'form',
      initialValue: this.checkAlarmName,
      alarmName: (event) => {
        this.checkAlarmName = event;
        // const name = this.checkAlarmName.name.split(',').map(item => {
        //   return { 'alarmName': item };
        // });
        const names = this.checkAlarmName.name.split(',');
        const alarmNameList = this.checkAlarmName.ids.map((id, index) => {
          return {'alarmName': names[index], 'alarmNameId': id};
        });
        this.formStatus.resetControlData('alarmNameList', alarmNameList);
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
        // const name = this.areaList.name.split(',').map(item => {
        //   return { 'areaName': item };
        // });
        const names = this.areaList.name.split(',');
        const areaNameList = this.areaList.ids.map((id, index) => {
          return {'areaName': names[index], 'areaId': id};
        });
        this.formStatus.resetControlData('areaNameList', areaNameList);
      }
    };
  }

  // 表单
  public initForm() {
    this.formColumn = [
      {
        // 模板名称
        label: this.language.templateName,
        key: 'templateName',
        type: 'input',
        require: true,
        width: 430,
        rule: [
          {required: true},
          RuleUtil.getNameMaxLengthRule(),
        ],
      },
      {
        // 频次
        label: this.language.alarmHappenCount,
        key: 'alarmHappenCount',
        type: 'input',
        width: 430,
        rule: [
          {maxLength: 8},
          {pattern: /^\+?[1-9][0-9]*$/, msg: this.language.enterAlarmHappenCount},
          {min: 1, msg: this.language.enterAlarmHappenCount},
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
      {
        // 告警级别
        label: this.language.alarmFixedLevel,
        key: 'alarmFixedLevel',
        type: 'select',
        width: 430,
        rule: [],
        asyncRules: [],
        allowClear: true,
        selectInfo: {
          data: [
            {label: this.language.urgent, value: '1'},
            {label: this.language.main, value: '2'},
            {label: this.language.secondary, value: '3'},
            {label: this.language.prompt, value: '4'}
          ],
          label: 'label',
          value: 'value'
        },
      },
      {
        // 首次发生时间
        label: this.language.alarmBeginTime,
        key: 'alarmBeginTime',
        type: 'custom',
        template: this.firstTimeTemp,
        // require: true,
        rule: [],
        asyncRules: []
      },
      {
        // 设施类型
        label: this.language.alarmSourceType,
        key: 'alarmSourceTypeId',
        type: 'select',
        // require: true,
        width: 430,
        selectInfo: {
          data: getDeviceType(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        allowClear: true,
        rule: [],
      },
      {
        // 确认时间
        label: this.language.alarmConfirmTime,
        key: 'alarmConfirmTime',
        type: 'custom',
        template: this.notarizeTimeTemp,
        // require: true,
        rule: [],
        asyncRules: []
      },
      {
        // 确认状态
        label: this.language.alarmConfirmStatus,
        key: 'alarmConfirmStatus',
        type: 'select',
        // require: true,
        width: 430,
        allowClear: true,
        selectInfo: {
          data: [
            {label: this.language.isConfirm, value: 1},
            {label: this.language.noConfirm, value: 2},
          ],
          label: 'label',
          value: 'value'
        },
        rule: [
          // { required: true },
          // RuleUtil.getNameMaxLengthRule(),
          RuleUtil.getNamePatternRule()
          // { pattern: /^(?! )[A-Za-z0-9\u4e00-\u9fa5\-_ ]{1,128}$/, msg: this.language.remarkMaxLength }
        ],
      },
      {
        // 确认用户
        label: this.language.alarmConfirmPeopleNickname,
        key: 'alarmConfirmPeopleNickname',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 清除用户
        label: this.language.alarmCleanPeopleNickname,
        key: 'alarmCleanPeopleNickname',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 告警名称
        label: this.language.alarmName,
        key: 'alarmNameList',
        width: 430,
        type: 'custom',
        rule: [],
        asyncRules: [],
        template: this.alarmName
      },
      {
        // 备注
        label: this.language.remark,
        key: 'remark',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
      {
        // 区域
        label: this.language.area,
        key: 'areaNameList',
        type: 'custom',
        width: 430,
        rule: [],
        asyncRules: [],
        template: this.areaSelector,
      },
      {
        // 责任单位
        label: this.language.responsibleDepartment,
        key: 'departmentList',
        type: 'input',
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 告警对象
        label: this.language.alarmobject,
        key: 'alarmObjectList',
        type: 'custom',
        width: 430,
        rule: [],
        asyncRules: [],
        template: this.departmentTep
      },
      {
        // 清除状态
        label: this.language.alarmCleanStatus,
        key: 'alarmCleanStatus',
        type: 'select',
        width: 430,
        allowClear: true,
        selectInfo: {
          data: [
            {label: this.language.noClean, value: 3},
            {label: this.language.isClean, value: 1},
            {label: this.language.deviceClean, value: 2}
          ],
          label: 'label',
          value: 'value'
        },
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 详细地址
        label: this.language.address,
        key: 'address',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 最近发生时间
        label: this.language.alarmNearTime,
        key: 'alarmNearTime',
        type: 'custom',
        template: this.recentlyTimeTemp,
        // require: true,
        rule: [],
        asyncRules: []
      },
      {
        // 告警附加信息
        label: this.language.alarmAdditionalInformation,
        key: 'extraMsg',
        type: 'input',
        // require: true,
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 告警处理建议
        label: this.language.alarmProcessing,
        key: 'alarmProcessing',
        type: 'input',
        width: 430,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
      },
      {
        // 清除时间
        label: this.language.alarmCleanTime,
        key: 'alarmCleanTime',
        type: 'custom',
        template: this.clearTimeTemp,
        // require: true,
        rule: [],
        asyncRules: []
      },
    ];
  }

  // 编辑通过ID 请求数据
  getAlarmData(id) {
    this.$alarmService.queryAlarmTemplateById([id]).subscribe((res: Result) => {
      if (res.code === 0) {
        const alarmData = res.data;
        this.createData = {
          createTime: alarmData.createTime,
          createUser: alarmData.createUser
        };
        // 首次发生时间
        if (alarmData.alarmBeginFrontTime && alarmData.alarmBeginQueenTime) {
          alarmData.alarmBeginTime = [this.timeAnalysis(alarmData.alarmBeginFrontTime), this.timeAnalysis(alarmData.alarmBeginQueenTime)];
          this.timeModel.firstTimeModel = [this.timeAnalysis(alarmData.alarmBeginFrontTime),
            this.timeAnalysis(alarmData.alarmBeginQueenTime)];
        }
        // 确认时间
        if (alarmData.alarmConfirmFrontTime && alarmData.alarmConfirmFrontTime) {
          alarmData.alarmConfirmTime = [this.timeAnalysis(alarmData.alarmConfirmFrontTime),
            this.timeAnalysis(alarmData.alarmConfirmQueenTime)];
          this.timeModel.notarizeTimeModel = [this.timeAnalysis(alarmData.alarmConfirmFrontTime),
            this.timeAnalysis(alarmData.alarmConfirmQueenTime)];
        }
        // 最近发生时间
        if (alarmData.alarmNearFrontTime && alarmData.alarmNearQueenTime) {
          alarmData.alarmNearTime = [this.timeAnalysis(alarmData.alarmNearFrontTime),
            this.timeAnalysis(alarmData.alarmNearQueenTime)];
          this.timeModel.recentlyTimeModel = [this.timeAnalysis(alarmData.alarmNearFrontTime),
            this.timeAnalysis(alarmData.alarmNearQueenTime)];
        }
        // 清除时间
        if (alarmData.alarmCleanFrontTime && alarmData.alarmCleanQueenTime) {
          alarmData.alarmData = [this.timeAnalysis(alarmData.alarmCleanFrontTime), this.timeAnalysis(alarmData.alarmCleanQueenTime)],
            this.timeModel.clearTimeModel = [this.timeAnalysis(alarmData.alarmCleanFrontTime),
              this.timeAnalysis(alarmData.alarmCleanQueenTime)];
        }
        // 告警名称
        if (alarmData.alarmNameList && alarmData.alarmNameList.length) {
          this.checkAlarmName = {
            ids: alarmData.alarmNameList.map(item => item.alarmNameId),
            name: alarmData.alarmNameList.map(item => item.alarmName).join(',')
          };
          this.initAlarmName();
        }
        // 区域
        if (alarmData.areaNameList && alarmData.areaNameList.length) {
          this.areaList = {
            ids: alarmData.areaNameList.map(item => item.areaId),
            name: alarmData.areaNameList.map(item => item.areaName).join(',')
          };
          this.initAreaConfig();
        }
        // 告警对象
        if (alarmData.alarmObjectList && alarmData.alarmObjectList.length) {
          this.checkAlarmObject = {
            ids: alarmData.alarmObjectList.map(item => item.deviceId),
            name: alarmData.alarmObjectList.map(item => item.deviceName).join(',')
          };
          this.initAlarmObjectConfig();
        }
        // 责任单位
        if (alarmData.departmentList && alarmData.departmentList[0]) {
          alarmData.departmentList = alarmData.departmentList[0].departmentName;
        }
        this.formStatus.resetData(alarmData);
      }
    });
  }

  // 时间戳转化为时间格式
  timeAnalysis(time: number) {
    return new Date(time);
  }

  // 时间格式转换时间戳
  timestamp(time: Date) {
    return +new Date(time);
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  cancel() {
    this.$router.navigate(['business/alarm/current-alarm']).then();
  }

  // 首次发生时间
  firstTimeChange(event) {
    this.timeModel.firstTimeModel = event;
  }

  firstTimeOnOpenChange(event) {
    if (event) {
      return;
    }
    if (+this.timeModel.firstTimeModel[0] > +this.timeModel.firstTimeModel[1]) {
      // this.timeModel.firstTimeModel[0] = this.timeModel.firstTimeModel[1];
      this.timeModel.firstTimeModel = [];
      this.$message.warning(this.language.timeMsg);
    }
    // 关闭时 避免时间控件的一些非常规操作 重新赋值下
    this.timeModel.firstTimeModel = this.timeModel.firstTimeModel.slice();
  }

  // 确认时间
  notarizeTimeChange(event) {
    this.timeModel.notarizeTimeModel = event;
  }

  notarizeTimeOnOpenChange(event) {
    if (event) {
      return;
    }
    if (+this.timeModel.notarizeTimeModel[0] > +this.timeModel.notarizeTimeModel[1]) {
      // this.timeModel.notarizeTimeModel[0] = this.timeModel.notarizeTimeModel[1];
      this.timeModel.notarizeTimeModel = [];
      this.$message.warning(this.language.timeMsg);
    }
    // 关闭时 避免时间控件的一些非常规操作 重新赋值下
    this.timeModel.notarizeTimeModel = this.timeModel.notarizeTimeModel.slice();
  }

  // 最近发生时间
  recentlyTimeChange(event) {
    this.timeModel.recentlyTimeModel = event;
  }

  recentlyTimeOnOpenChange(event) {
    if (event) {
      return;
    }
    if (+this.timeModel.recentlyTimeModel[0] > +this.timeModel.recentlyTimeModel[1]) {
      // this.timeModel.recentlyTimeModel[0] = this.timeModel.recentlyTimeModel[1];
      this.timeModel.recentlyTimeModel = [];
      this.$message.warning(this.language.timeMsg);
    }
    // 关闭时 避免时间控件的一些非常规操作 重新赋值下
    this.timeModel.recentlyTimeModel = this.timeModel.recentlyTimeModel.slice();
  }

  // 清除时间
  clearTimeChange(event) {
    this.timeModel.clearTimeModel = event;
  }

  clearTimeOnOpenChange(event) {
    if (event) {
      return;
    }
    if (+this.timeModel.clearTimeModel[0] > +this.timeModel.clearTimeModel[1]) {
      // this.timeModel.clearTimeModel[0] = this.timeModel.clearTimeModel[1];
      this.timeModel.clearTimeModel = [];
      this.$message.warning(this.language.timeMsg);
    }
    // 关闭时 避免时间控件的一些非常规操作 重新赋值下
    this.timeModel.clearTimeModel = this.timeModel.clearTimeModel.slice();
  }

  // 提交
  submit() {
    this.isLoading = true;
    const alarmObj = this.formStatus.getData();
    alarmObj.templateName = alarmObj.templateName && alarmObj.templateName.trim();
    // alarmObj.alarmHappenCount = alarmObj.alarmHappenCount.trim();
    alarmObj.alarmConfirmPeopleNickname = alarmObj.alarmConfirmPeopleNickname && alarmObj.alarmConfirmPeopleNickname.trim();
    alarmObj.alarmCleanPeopleNickname = alarmObj.alarmCleanPeopleNickname && alarmObj.alarmCleanPeopleNickname.trim();
    alarmObj.remark = alarmObj.remark && alarmObj.remark.trim();
    alarmObj.departmentList = alarmObj.departmentList && alarmObj.departmentList.length && alarmObj.departmentList.trim();
    alarmObj.address = alarmObj.address && alarmObj.address.trim();
    alarmObj.extraMsg = alarmObj.extraMsg && alarmObj.extraMsg.trim();
    alarmObj.alarmProcessing = alarmObj.alarmProcessing && alarmObj.alarmProcessing.trim();
    // 首次发生时间
    if (this.timeModel.firstTimeModel.length) {
      alarmObj.alarmBeginFrontTime = this.timestamp(this.timeModel.firstTimeModel[0]);
      alarmObj.alarmBeginQueenTime = this.timestamp(this.timeModel.firstTimeModel[1]);
    } else {
      alarmObj.alarmBeginFrontTime = null;
      alarmObj.alarmBeginQueenTime = null;
      alarmObj.alarmBeginTime = null;
    }
    // 确认时间
    if (this.timeModel.notarizeTimeModel.length) {
      alarmObj.alarmConfirmFrontTime = this.timestamp(this.timeModel.notarizeTimeModel[0]);
      alarmObj.alarmConfirmQueenTime = this.timestamp(this.timeModel.notarizeTimeModel[1]);
    } else {
      alarmObj.alarmConfirmFrontTime = null;
      alarmObj.alarmConfirmQueenTime = null;
      alarmObj.alarmConfirmTime = null;
    }
    // 最近发生时间
    if (this.timeModel.recentlyTimeModel.length) {
      alarmObj.alarmNearFrontTime = this.timestamp(this.timeModel.recentlyTimeModel[0]);
      alarmObj.alarmNearQueenTime = this.timestamp(this.timeModel.recentlyTimeModel[1]);
    } else {
      alarmObj.alarmNearFrontTime = null;
      alarmObj.alarmNearQueenTime = null;
      alarmObj.alarmNearTime = null;
    }
    // 清除时间
    if (this.timeModel.clearTimeModel.length) {
      alarmObj.alarmCleanFrontTime = this.timestamp(this.timeModel.clearTimeModel[0]);
      alarmObj.alarmCleanQueenTime = this.timestamp(this.timeModel.clearTimeModel[1]);
    } else {
      alarmObj.alarmCleanFrontTime = null;
      alarmObj.alarmCleanQueenTime = null;
      alarmObj.alarmCleanTime = null;
    }
    // 频次
    alarmObj.alarmHappenCount = alarmObj.alarmHappenCount ? Number(alarmObj.alarmHappenCount) : null;
    // 责任单位
    alarmObj.departmentList = [{'departmentName': alarmObj.departmentList}];
    if (this.pageType === 'add') {
      this.$alarmService.addAlarmTemplate(alarmObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res && res.code === 0) {
          this.$message.success(res.msg);
          this.$router.navigate(['business/alarm/current-alarm']).then();
        }
      });
    } else {
      alarmObj.id = this.updateParamsId;
      alarmObj.createTime = this.createData.createTime;
      alarmObj.createUser = this.createData.createUser;
      this.$alarmService.updataAlarmTemplate(alarmObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res && res.code === 0) {
          this.$message.success(res.msg);
          this.$router.navigate(['business/alarm/current-alarm']).then();
        }
      });
    }
  }
}
