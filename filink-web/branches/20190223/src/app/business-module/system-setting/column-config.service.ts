import {NzI18nService} from 'ng-zorro-antd';
import {ColumnConfig} from '../../shared-module/entity/tableConfig';
import {Injectable} from '@angular/core';
import {FormItem} from '../../shared-module/component/form/form-config';
import {BasicConfig} from '../basic-config';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';

@Injectable()
export class ColumnConfigService extends BasicConfig {
  constructor(public $nzI18n: NzI18nService) {
    super($nzI18n);
  }

  nameReg =  {
    asyncRule: (control: FormControl) => {
      return Observable.create(observer => {
        if (/^(?!_)[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(control.value)) {
          observer.next(null);
          observer.complete();
        } else {
          observer.next({error: true, duplicated: true});
          observer.complete();
        }
      });
    },
    asyncCode: 'duplicated', msg: '名称输入格式不正确'
  };

  /**
   * 获取菜单管理列表列配置
   */
  getSystemSettingColumnConfig  (tempalteObj: any): Array<ColumnConfig> {
    const config: Array<ColumnConfig> = [
      {type: 'select',  width: 60, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
      {
        title: this.language.systemSetting.menuTemplateName,
        key: 'templateName',
        configurable: true,
        width: 150,
        searchable: true,
        isShowSort: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.systemSetting.menuTemplateStatus,
        key: 'templateStatus',
        configurable: true,
        width: 150,
        searchable: true,
        isShowSort: true,
        type: 'render',
        renderTemplate: tempalteObj.templateStatus,
        searchConfig: {type: 'select', selectInfo: [{label: '禁用', value: '0'}, {label: '启用', value: '1'}]}
      },
      {
        title: this.language.systemSetting.cdate,
        key: 'createTimeTimestamp',
        configurable: true,
        pipe: 'date',
        width: 200,
        searchKey: 'createTime',
        isShowSort: true,
        searchable: true,
        searchConfig: {type: 'date'},
        handleFilter: ($event) => {
          console.log($event);
        },
      },
      {
        title: this.language.systemSetting.remark,
        key: 'remark',
        configurable: true,
        width: 200,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.table.operate, searchable: true,
        searchConfig: {type: 'operate'}, key: '', width: 150, fixedStyle: {fixedRight: true, style: {right: '0px'}}
      },
    ];
    return config;
  }

  /**
   * 获取菜单管理表单列配置
   */
  getSystemSettingAddColumn (ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: this.language.systemSetting.menuTemplateName,
        key: 'templateName',
        type: 'input',
        initialValue: ininDate.templateName || '',
        require: true,
        rule: [{required: true}, {maxLength: 255}],
        asyncRules: [this.nameReg]
      },
      {
        label: this.language.systemSetting.menuId,
        key: 'menuInfoTrees',
        type: 'custom',
        labelHeight: 400,
        require: true,
        rule: [],
        asyncRules: [],
        template: ininDate.menuTreeTemplate
      },
      {
        label: this.language.systemSetting.remark, key: 'remark', type: 'input',
        initialValue: ininDate.remark || '',
        rule: [{maxLength: 1024}]
      },
      {
        label: this.language.systemSetting.enable, key: 'templateStatus', type: 'radio',
        initialValue: ininDate.templateStatus || '1',
        radioInfo: {
          data: [{
            label: '启用',
            value: '1'
          },
            {
              label: '禁用',
              value: '0'
            }],
          label: 'label',
          value: 'value'
        },
        rule: [], asyncRules: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取设施协议列表配置
   * param tempalteObj
   */
  getLogManagementColumnConfig  (tempalteObj: any): Array<ColumnConfig> {
    const config: Array<ColumnConfig> = [
      {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
      {
        title: this.language.systemSetting.optName,
        configurable: true,
        key: 'optName',
        width: 150,
        isShowSort: true,
        searchable: true,
        searchConfig: {type: 'input'},
        fixedStyle: {fixedLeft: true, style: {left: '124px'}}
      },
      {
        title: this.language.systemSetting.optType,
        configurable: true,
        key: 'optType',
        width: 150,
        searchable: true,
        isShowSort: true,
        type: 'render',
        renderTemplate: tempalteObj.optType,
        searchConfig: {
          type: 'select',
          selectInfo: [{
            label: this.language.log.web,
            value: this.language.log.web
          }, {
            label: this.language.log.pda,
            value: this.language.log.pda
          }]
        }
      },
      {
        title: this.language.systemSetting.dangerLevel,
        configurable: true,
        key: 'dangerLevel',
        width: 150,
        isShowSort: true,
        searchable: true,
        type: 'render',
        renderTemplate: tempalteObj.dangerLevel,
        searchConfig: {
          type: 'select',
          selectInfo: [{
            label: this.language.log.danger,
            value: this.language.log.danger
          }, {
            label: this.language.log.prompt,
            value: this.language.log.prompt
          }, {
            label: this.language.log.general,
            value: this.language.log.general
          }]
        }
      },
      {
        title: this.language.systemSetting.optUserName,
        configurable: true,
        key: 'optUserName',
        width: 150,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.systemSetting.optTerminal,
        configurable: true,
        key: 'optTerminal',
        width: 150,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.systemSetting.optTime,
        key: 'optTime',
        width: 280,
        configurable: true,
        pipe: 'date',
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'date'}
      },
      {
        title: this.language.systemSetting.optObj,
        configurable: true,
        key: 'optObj',
        width: 150,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.systemSetting.optResult,
        configurable: true,
        key: 'optResult',
        width: 150,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        type: 'render',
        renderTemplate: tempalteObj.optResult,
        searchConfig: {
          type: 'select',
          selectInfo: [{
            label: this.language.log.failure,
            value: this.language.log.failure
          }, {
            label: this.language.log.success,
            value: this.language.log.success
          }]
        }
      },
      {
        title: this.language.systemSetting.detailInfo,
        configurable: true,
        key: 'detailInfo',
        width: 150,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.systemSetting.remark,
        configurable: true,
        key: 'remark',
        width: 150,
        isShowSort: true,
        isCustomFilter: true,
        searchable: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.table.operate, searchable: true,
        searchConfig: {type: 'operate'}, key: '', width: 80, fixedStyle: {fixedRight: true, style: {right: '0px'}}
      },
    ];
    return config;
  }

  /**
   * 获取设施协议列配置
   */
  getFacilityColumnConfig (tempalteObj: any) {
    const config: Array<ColumnConfig> = [
      {type: 'select',  width: 60, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
      {
        title: this.language.agreement.protocolName,
        key: 'protocolName',
        configurable: true,
        width: 250,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.agreement.hardwareVersion,
        key: 'hardwareVersion',
        configurable: true,
        width: 250,
        searchConfig: {
          type: 'input',
        }
      },
      {
        title: this.language.agreement.softwareVersion,
        key: 'softwareVersion',
        configurable: true,
        width: 250,
        isCustomFilter: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.agreement.fileName,
        key: 'fileName',
        configurable: true,
        type: 'render',
        renderTemplate: tempalteObj.fileName,
        isCustomFilter: true,
        searchConfig: {type: 'input'},
      },
      {
        title: this.language.table.operate,
        searchConfig: {type: 'operate'}, key: '', width: 100, fixedStyle: {fixedRight: true, style: {right: '0px'}}
      },
    ];
    return config;
  }

  /**
   * 获取安全策略列表
   * param tempalteObj
   */
  getAccessControlColumnConfig(tempalteObj: any) {
    const config: Array<ColumnConfig> = [
      {type: 'select',  width: 60, fixedStyle: {fixedLeft: true, style: {left: '0px'}}},
      {
        title: this.language.systemSetting.indexNo,
        key: 'code',
        searchable: true,
        width: 150,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.systemSetting.ipScope,
        key: 'ipScope',
        searchable: true,
        width: 150,
        searchConfig: {
          type: 'input',
        }
      },
      {
        title: this.language.systemSetting.statue,
        key: 'statue',
        width: 150,
        isCustomFilter: true,
        type: 'render',
        searchable: true,
        renderTemplate: tempalteObj.statue,
        searchConfig: {type: 'select', selectInfo: [{label: '禁用', value: '0'}, {label: '启用', value: '1'}]}
      },
      {
        title: this.language.systemSetting.mask,
        key: 'mask',
        searchable: true,
        width: 150,
        isCustomFilter: true,
        searchConfig: {type: 'input'}
      },
      {
        title: this.language.table.operate,
        searchConfig: {type: 'operate'}, key: '', width: 150, fixedStyle: {fixedRight: true, style: {right: '0px'}}
      },
    ];
    return config;
  }

  /**
   * 获取安全策略表单配置
   * param ininDate
   */
  getAccessControlFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: this.language.systemSetting.indexNo,
        key: 'code',
        type: 'input',
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: this.language.systemSetting.startIp,
        key: 'startIp',
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: this.language.systemSetting.endIp,
        key: 'endIp',
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: this.language.systemSetting.mask,
        key: 'mask',
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取账号安全策略表单配置
   * param ininDate
   */
  getIDAccessControlFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '用户名最小长度',
        key: 'code',
        type: 'input',
        col: 24,
        labelWidth: 200,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '非法登入允许次数',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '登入失败次数最大时间间隔',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '启用账号锁定策略',
        key: 'mask',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '账号锁定时间',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '启用账号禁用策略',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '账号连续未登陆时间',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '无操作登出时间',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取密码安全策略表格配置
   * param ininDate
   */
  getPasswordAccessControlFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '用户名最小长度',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '密码至少包含一个大写字母',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '密码至少包含一个小写字母',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '密码至少包含一个数字',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '密码至少包含一个特殊字符',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '启用密码过期策略',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '密码有效期',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取系统参数 显示设置表单配置
   * param ininDate
   */
  getShowSystemParameterFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '启用大屏',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '大屏滚动',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '大屏滚动时间间隔',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '系统logo',
        key: 'logo',
        labelWidth: 200,
        type: 'custom',
        template: ininDate.logo,
        col: 24,
        rule: []
      },
      {
        label: '时间设置',
        key: 'timer',
        labelWidth: 200,
        type: 'radio',
        initialValue: ininDate.mask || '1',
        radioInfo: {
          data: [{
            label: '本地时间',
            value: '1'
          },
            {
              label: 'UTC时间',
              value: '0'
            }],
          label: 'label',
          value: 'value'
        },
        col: 24,
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取系统参数 消息通知表单配置
   * param ininDate
   */
  getMsgSystemParameterFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '启用消息提醒',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '消息保留时间',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '启用声音提醒',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '选中提醒音',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取系统参数 邮件服务器表单配置
   * param ininDate
   */
  getEmailSystemParameterFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '服务接口地址',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '服务端口',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '用户名',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '密码',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取系统参数 短信服务表单配置
   * param ininDate
   */
  getNoteSystemParameterFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '服务接口地址',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '服务端口',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '用户名',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '密码',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取系统参数 推送服务表单配置
   * param ininDate
   */
  getPushSystemParameterFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: '服务接口地址',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '服务端口',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '用户名',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '密码',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];

    return formColumn;
  }

  /**
   * 获取系统参数 FTP服务表单配置
   * param ininDate
   */
  getFTPSystemParameterFormConfig(ininDate): FormItem[] {
    const formColumn: FormItem[] = [
      {
        label: 'FTP地址',
        key: 'code',
        type: 'input',
        labelWidth: 200,
        col: 24,
        initialValue: ininDate.code || '',
        rule: [],
      },
      {
        label: '端口',
        key: 'startIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.startIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '用户名',
        key: 'endIp',
        labelWidth: 200,
        col: 24,
        type: 'input',
        initialValue: ininDate.endIp || '',
        rule: [],
        asyncRules: [],
      },
      {
        label: '密码',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
      {
        label: '路径',
        key: 'mask',
        labelWidth: 200,
        type: 'input',
        col: 24,
        initialValue: ininDate.mask || '',
        rule: []
      },
    ];
    return formColumn;
  }

}
