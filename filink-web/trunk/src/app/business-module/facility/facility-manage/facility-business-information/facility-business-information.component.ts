import {Component, OnInit, ViewChild} from '@angular/core';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {NzI18nService} from 'ng-zorro-antd';
import {ActivatedRoute} from '@angular/router';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {LabelTypeEnum, SmartLabelConfig, UploadTypeEnum} from '../../smart-label.config';
import {DeviceTypeCode, getTemplateCodeRule, getTemplateTrend} from '../../facility.config';
import {SmartService} from '../../../../core-module/api-service/facility/smart/smart.service';
import {Result} from '../../../../shared-module/entity/result';
import {TemplateReqDto} from '../../../../core-module/entity/facility/template';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';

/**
 * 配置业务信息组件
 */
@Component({
  selector: 'app-facility-business-information',
  templateUrl: './facility-business-information.component.html',
  styleUrls: ['./facility-business-information.component.scss']
})
export class FacilityBusinessInformationComponent implements OnInit {
  // 页面是否加载
  pageLoading: boolean = false;
  // 表单提交按钮是否加载
  isLoading: boolean;
  // 设施语言包
  language: FacilityLanguageInterface;
  // 表单配置
  formColumn: FormItem[] = [];
  // 表单状态
  formStatus: FormOperate;
  // 设施id
  deviceId: string;
  // 设施类型
  deviceType: string;
  // 设施名称
  deviceName: string;
  // 智能标签模板
  @ViewChild('smartLabelTemp') private smartLabelTemp;
  // 模板选择显示隐藏
  templateShow: boolean = false;
  // 模板信息
  smartLabelInfo: any = {};
  // 提交类型
  private uploadType: number = UploadTypeEnum.add; // 0为新增 2 为修改
  // 模板实体
  public templateReqDto;
  // 模板禁用
  templateDisabled;

  constructor(private $nzI18n: NzI18nService,
              private $active: ActivatedRoute,
              private $smartService: SmartService,
              private $message: FiLinkModalService,
              private $ruleUtil: RuleUtil) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.$active.queryParams.subscribe(params => {
      this.deviceId = params.id;
      this.deviceType = params.deviceType;
      this.deviceName = params.deviceName;
      this.initFormColumn();
      this.queryFacilityBusinessInfoById();
    });
  }

  /**
   * 表单实例
   * param event
   */
  formInstance(event) {
    this.formStatus = event.instance;
  }

  goBack() {
    window.history.go(-1);
  }

  /**
   * 模版选择器结果
   * param event
   */
  selectedTemplate(event) {
    this.smartLabelInfo = event;
    this.templateShow = false;
    this.formStatus.group.controls['boxName'].reset(event.name);
  }

  /**
   * 保存设施业务信息
   */
  saveInfo() {
    this.isLoading = true;
    const body: any = this.formStatus.group.getRawValue();
    const obj: any = {boxList: [body]};
    if (this.smartLabelInfo.id) {
      obj.boxTemplateId = this.smartLabelInfo.id;
    }
    // 当标签类型为无的时候置空标签状态
    if (body.labelType === LabelTypeEnum.NONE) {
      body.labelState = '';
    }
    obj.deviceId = this.deviceId;
    body.deviceId = this.deviceId;
    body.deviceName = this.deviceName;
    obj.deviceType = this.deviceType;
    obj.uploadType = this.uploadType;
    obj.boxTrend = body.boxTrend;
    obj.boxCodeRule = body.boxCodeRule;
    obj.frameTrend = body.frameTrend;
    obj.frameCodeRule = body.frameCodeRule;
    obj.discTrend = body.discTrend;
    obj.discCodeRule = body.discCodeRule;
    obj.boxName = body.boxName;
    obj.boardList = [];
    obj.portList = [];
    this.$smartService.uploadFacilityBusinessInfo(obj).subscribe((result: Result) => {
      this.isLoading = false;
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.goBack();
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
      this.isLoading = false;
    });
  }

  /**
   * 查询设施业务信息
   */
  queryFacilityBusinessInfoById() {
    this.pageLoading = true;
    this.$smartService.queryFacilityBusInfoById(this.deviceId, this.deviceType).subscribe((result: Result) => {
      this.pageLoading = false;
      if (result.code === 0) {
        if (result.data.boxList && result.data.boxList.length > 0) {
          this.uploadType = UploadTypeEnum.update;
          this.formStatus.resetData(result.data.boxList[0]);
          if (this.deviceType === DeviceTypeCode.Optical_Box || this.deviceType === DeviceTypeCode.Distribution_Frame) {
            const {boxTrend, boxCodeRule, frameTrend, frameCodeRule, discTrend, discCodeRule, boxName} = result.data;
            const obj = {boxTrend, boxCodeRule, frameTrend, frameCodeRule, discTrend, discCodeRule, boxName};
            this.smartLabelInfo.name = result.data.boxName;
            this.formStatus.resetData(Object.assign(obj, result.data.boxList[0]));
            this.disableFormItem();
          }
        } else {
          this.uploadType = UploadTypeEnum.add;
        }
      }
    }, (error: any) => {
      if (error.code === 9999) {
        history.go(-1);
      }
    });
  }

  /**
   * 打开模板选择器并且传入所有箱架盘信息
   */
  openSmartLabel() {
    const body: TemplateReqDto = this.formStatus.group.getRawValue() as TemplateReqDto;
    if (this.formStatus.getValid('boxTrend') &&
      this.formStatus.getValid('boxCodeRule') &&
      this.formStatus.getValid('frameTrend') &&
      this.formStatus.getValid('frameCodeRule') &&
      this.formStatus.getValid('discTrend') &&
      this.formStatus.getValid('discCodeRule')
    ) {
      this.templateReqDto = body;
      this.templateShow = true;
    } else {
      this.$message.error(this.language.pleaseGetAllInfoToRackAndTray);
    }
  }

  /**
   * 初始化表单配置
   */
  private initFormColumn() {
    // 公共
    const formColumn_common = [
      // {
      //   // 别名
      //   label: this.language.otherName, key: 'alias', col: 24, type: 'input',
      //   rule: [
      //     RuleUtil.getNameMaxLengthRule(),
      //     this.$ruleUtil.getNameRule()
      //   ],
      // },
      {
        // 标签ID
        label: this.language.tagID, key: 'boxLabel', col: 24, type: 'input',
        disabled: true,
        rule: [
          RuleUtil.getNameMaxLengthRule(),
          this.$ruleUtil.getNameRule()
        ],
      },
      // {
      //   // 智能标签信息
      //   label: this.language.intelligenceLabelInfo, key: 'labelInfo', col: 24, type: 'input', require: true,
      //   rule: [{required: true},
      //     RuleUtil.getNameMaxLengthRule(),
      //     this.$ruleUtil.getNameRule()
      //   ],
      // },
      {
        // 制造商
        label: this.language.manufacturer, key: 'producer', col: 24, type: 'input',
        rule: [
          RuleUtil.getNameMaxLengthRule(),
          this.$ruleUtil.getNameRule()
        ],
      },
      {
        // 标签类型
        label: this.language.labelType, key: 'labelType',
        type: 'select',
        col: 24,
        selectInfo: {
          data: SmartLabelConfig.getLabelTypeEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        modelChange: (controls, $event, key, formOperate: FormOperate) => {
          const labelStateColumn = formOperate.getColumn('labelState').item;
          // 标签类型为无的时候标签状态隐藏
          if ($event === LabelTypeEnum.NONE) {
            labelStateColumn.hidden = true;
          } else {
            labelStateColumn.hidden = false;
          }
        },
        require: true,
        rule: [
          {required: true}
        ],
      },
      {
        // 标签状态
        label: this.language.labelState, key: 'labelState',
        type: 'select',
        col: 24,
        hidden: true,
        selectInfo: {
          data: SmartLabelConfig.getLabelStateEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        rule: [],
      },
    ];
    // 配线架
    const formColumn_distributionFrame = [
      {
        // 设施形态
        label: this.language.deviceForm, key: 'deviceForm',
        type: 'select',
        col: 24,
        selectInfo: {
          data: SmartLabelConfig.getDeviceFormEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}],
      },
      {
        // 机架行号
        label: this.language.rackLineNumber, key: 'lineNum', col: 24, type: 'input',
        rule: [RuleUtil.getNameMaxLengthRule()],
      },
      {
        // 机架列号
        label: this.language.rackColumnNumber, key: 'columnNum', col: 24, type: 'input',
        rule: [RuleUtil.getNameMaxLengthRule()],
      },
      {
        // 最大纤芯数
        label: this.language.fiberCoreNum, key: 'maxFiberNum', col: 24, type: 'input',
        require: true,
        rule: [
          {required: true},
          this.$ruleUtil.getMaxFiberNumRule(),
        ],
      },
      {
        // 走向-箱架信息
        label: this.language.trend, key: 'boxTrend', type: 'select', title: this.language.BoxRackInformation,
        col: 24,
        selectInfo: {
          data: getTemplateTrend(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 编号规则
        label: this.language.codeRule, key: 'boxCodeRule', type: 'select',
        col: 24,
        selectInfo: {
          data: getTemplateCodeRule(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 走向-框架信息
        label: this.language.trend, key: 'frameTrend', type: 'select', title: this.language.frameInformation,
        col: 24,
        selectInfo: {
          data: getTemplateTrend(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 编号规则
        label: this.language.codeRule, key: 'frameCodeRule', type: 'select',
        col: 24,
        selectInfo: {
          data: getTemplateCodeRule(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 走向-盘信息
        label: this.language.trend, key: 'discTrend', type: 'select', title: this.language.diskInformation,
        col: 24,
        selectInfo: {
          data: getTemplateTrend(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 编号规则
        label: this.language.codeRule, key: 'discCodeRule', type: 'select',
        col: 24,
        selectInfo: {
          data: getTemplateCodeRule(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 箱架模版
        label: this.language.boxTemplate, key: 'boxName', col: 24,
        require: true,
        type: 'custom',
        template: this.smartLabelTemp,
        rule: [
          {required: true},
        ],
      },
      {
        label: this.language.remarks, col: 24, key: 'memo', type: 'textarea',
        rule: [this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
    // 光交箱
    const formColumn_opticalBox = [
      {
        // 最大纤芯数
        label: this.language.fiberCoreNum, key: 'maxFiberNum', col: 24, type: 'input',
        require: true,
        rule: [
          {required: true},
          this.$ruleUtil.getMaxFiberNumRule(),
        ],
      },
      {
        // 走向-箱架信息
        label: this.language.trend, key: 'boxTrend', type: 'select', title: this.language.BoxRackInformation,
        col: 24,
        selectInfo: {
          data: getTemplateTrend(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 编号规则
        label: this.language.codeRule, key: 'boxCodeRule', type: 'select',
        col: 24,
        selectInfo: {
          data: getTemplateCodeRule(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 走向-框架信息
        label: this.language.trend, key: 'frameTrend', type: 'select', title: this.language.frameInformation,
        col: 24,
        selectInfo: {
          data: getTemplateTrend(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 编号规则
        label: this.language.codeRule, key: 'frameCodeRule', type: 'select',
        col: 24,
        selectInfo: {
          data: getTemplateCodeRule(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 走向-盘信息
        label: this.language.trend, key: 'discTrend', type: 'select', title: this.language.diskInformation,
        col: 24,
        selectInfo: {
          data: getTemplateTrend(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 编号规则
        label: this.language.codeRule, key: 'discCodeRule', type: 'select',
        col: 24,
        selectInfo: {
          data: getTemplateCodeRule(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        require: true,
        rule: [{required: true}], asyncRules: []
      },
      {
        // 箱架模版
        label: this.language.boxTemplate, key: 'boxName', col: 24,
        require: true,
        type: 'custom',
        template: this.smartLabelTemp,
        rule: [
          {required: true},
        ],
      },
      // {
      //   // 安装方式
      //   label: this.language.installationMethod, key: 'installationMode',
      //   type: 'select',
      //   col: 24,
      //   selectInfo: {
      //     data: SmartLabelConfig.getInstallationModeEnum(this.$nzI18n),
      //     label: 'label',
      //     value: 'code'
      //   },
      //   rule: [
      //     RuleUtil.getNameMaxLengthRule(),
      //     this.$ruleUtil.getNameRule()
      //   ],
      // },
      {
        label: this.language.remarks, col: 24, key: 'memo', type: 'textarea',
        rule: [this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
    // 接头盒
    const formColumn_junctionBox = [
      // 烽火提出暂时去掉
      // {
      //   // 接头盒类型
      //   label: this.language.apparatusType, key: 'apparatusType',
      //   type: 'select',
      //   col: 24,
      //   selectInfo: {
      //     data: SmartLabelConfig.getApparatusTypeEnum(this.$nzI18n),
      //     label: 'label',
      //     value: 'code'
      //   },
      //   rule: [],
      // },
      // {
      //   // 安装方式
      //   label: this.language.installationMethod, key: 'installationMode',
      //   type: 'select',
      //   col: 24,
      //   selectInfo: {
      //     data: SmartLabelConfig.getInstallationModeEnum(this.$nzI18n),
      //     label: 'label',
      //     value: 'code'
      //   },
      //   rule: [
      //     RuleUtil.getNameMaxLengthRule(),
      //     this.$ruleUtil.getNameRule()
      //   ],
      // },
      {
        // 最大纤芯数
        label: this.language.fiberCoreNum, key: 'maxFiberNum', col: 24, type: 'input',
        require: true,
        rule: [
          {required: true},
          this.$ruleUtil.getMaxFiberNumRule(),
        ],
      },
      {
        // 密封方式
        label: this.language.sealMode, key: 'sealMode',
        type: 'select',
        col: 24,
        selectInfo: {
          data: SmartLabelConfig.getSealModeEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        rule: [],
      },
      {
        // 敷设方式
        label: this.language.layMode, key: 'layMode',
        type: 'select',
        col: 24,
        selectInfo: {
          data: SmartLabelConfig.getLayModeEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        rule: [],
      },
      {
        // 规格说明
        label: this.language.standard, key: 'standard',
        type: 'select',
        col: 24,
        selectInfo: {
          data: SmartLabelConfig.getStandardEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        rule: [],
      },
      {
        // 接续信息
        label: this.language.follow, key: 'follow',
        type: 'select',
        col: 24,
        selectInfo: {
          data: SmartLabelConfig.getFollowEnum(this.$nzI18n),
          label: 'label',
          value: 'code'
        },
        rule: [],
      },
      {
        label: this.language.remarks, col: 24, key: 'memo', type: 'textarea',
        rule: [this.$ruleUtil.getRemarkMaxLengthRule(), this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];

    // 光交箱
    if (this.deviceType === DeviceTypeCode.Optical_Box) {
      this.formColumn = this.formColumn.concat(formColumn_common, formColumn_opticalBox);
      // 配线架
    } else if (this.deviceType === DeviceTypeCode.Distribution_Frame) {
      this.formColumn = this.formColumn.concat(formColumn_common, formColumn_distributionFrame);

      // 接头盒
    } else if (this.deviceType === DeviceTypeCode.Junction_Box) {
      this.formColumn = this.formColumn.concat(formColumn_common, formColumn_junctionBox);

    }
  }

  /**
   * 禁用模板相关字段
   */
  private disableFormItem() {
    this.formStatus.group.controls['boxTrend'].disable();
    this.formStatus.group.controls['boxCodeRule'].disable();
    this.formStatus.group.controls['frameTrend'].disable();
    this.formStatus.group.controls['frameCodeRule'].disable();
    this.formStatus.group.controls['discTrend'].disable();
    this.formStatus.group.controls['discCodeRule'].disable();
    // this.formStatus.group.controls['boxLabel'].disable();
    this.templateDisabled = true;
  }
}
