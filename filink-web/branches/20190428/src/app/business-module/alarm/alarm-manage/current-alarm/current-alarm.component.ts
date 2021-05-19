import {Component, OnInit, Injectable, ViewChild, TemplateRef} from '@angular/core';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {ActivatedRoute, Router} from '@angular/router';
import {DateHelperService, NzI18nService} from 'ng-zorro-antd';
import {AlarmService} from '../../../../core-module/api-service/alarm';
import {Result} from '../../../../shared-module/entity/result';
import {AlarmLanguageInterface} from '../../../../../assets/i18n/alarm/alarm-language.interface';
import {QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmStoreService} from '../../../../core-module/store/alarm.store.service';
import {CurrAlarmServiceService} from './curr-alarm-service.service';
import {DateFormatString} from '../../../../shared-module/entity/dateFormatString';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {NzModalService} from 'ng-zorro-antd';
import {AreaConfig, AlarmNameConfig, AlarmObjectConfig} from 'src/app/shared-module/component/alarm/alarmSelectorConfig';
import {SessionUtil} from '../../../../shared-module/util/session-util';
import {TableService} from 'src/app/shared-module/component/table/table.service';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';

@Component({
  selector: 'app-current-alarm',
  templateUrl: './current-alarm.component.html',
  styleUrls: ['./current-alarm.component.scss'],
  providers: [TableService]
})

@Injectable()
export class CurrentAlarmComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;

  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;

  alarmId = null;
  deviceId = null;
  token: string = ''; // token
  userInfo = {}; // 用户信息
  userId: string = ''; // 用户id
  confirmFlag = true;
  cleanFlag = true;
  filterEvent;
  // 告警类型
  alarmType = [];
  display = {
    remarkTable: false,
    templateTable: false,
    creationWorkOrder: false,
    nameTable: false
  };
  // 修改备注
  checkRemark: any[];
  isLoading: boolean = false;

  // 修改备注弹框
  formColumnRemark: FormItem[] = [];
  formStatusRemark: FormOperate;
  // 点击告警确认
  alarmIds = [];
  // 创建工单的数据
  creationWorkOrderData = {};
  alarmNameConfig: AlarmNameConfig;
  // 勾选的告警名称
  _checkAlarmName = {
    name: '',
    ids: []
  };
  areaConfig: AreaConfig;
  // 区域
  areaList = {
    ids: [],
    name: ''
  };
  alarmObjectConfig: AlarmObjectConfig;
  checkAlarmObject = {
    ids: [],
    name: ''
  };
  // 模板ID
  templateId: any;
  @ViewChild('alarmFixedLevelTemp') alarmFixedLevelTemp: TemplateRef<any>;
  @ViewChild('table') table;
  @ViewChild('isCleanTemp') isCleanTemp: TemplateRef<any>;
  @ViewChild('alarmSourceTypeTemp') alarmSourceTypeTemp: TemplateRef<any>;
  @ViewChild('isConfirmTemp') isConfirmTemp: TemplateRef<any>;
  @ViewChild('alarmName') private alarmName;
  @ViewChild('areaSelector') private areaSelectorTemp;
  @ViewChild('department') private departmentTemp;
  @ViewChild('alarmContinueTimeTemp') private alarmContinueTimeTemp;

  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $alarmService: AlarmService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $alarmStoreService: AlarmStoreService,
    public $currServive: CurrAlarmServiceService,
    private $dateHelper: DateHelperService,
    private $ruleUtil: RuleUtil,
    private modalService: NzModalService,
    private $imageViewService: ImageViewService
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
  }

  ngOnInit() {
    this.initTableConfig();
    // 获取用户信息
    if (SessionUtil.getToken()) {
      this.token = SessionUtil.getToken();
      // this.userInfo = JSON.parse(localStorage.getItem('userInfo'));
      this.userInfo = SessionUtil.getUserInfo();
      this.userId = this.userInfo['id'];
    }
    // 获取告警id
    if (this.$active.snapshot.queryParams.id) {
      this.alarmId = this.$active.snapshot.queryParams.id;
    }
    // 获取设备deviceId
    if (this.$active.snapshot.queryParams.deviceId) {
      this.deviceId = this.$active.snapshot.queryParams.deviceId;
    }
    if (this.alarmId) {
      const obj = {
        'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex},
        'filterConditions': [{
          'filterField': 'id',
          'operator': 'eq',
          'filterValue': this.alarmId
        }]
      };
      this.refreshData(obj);
    } else {
      this.refreshData({'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex}});
    }
    this.alarmType = [
      {name: this.language.alarmSum, icon: 'iconfont fiLink-alarm-all', number: '89', color: 'statistics-all-color'},
      {name: this.language.urgentAlarm, icon: 'iconfont fiLink-alarm-urgency', number: '28', color: 'statistics-urgencyAlarm-color'},
      {name: this.language.mainAlarm, icon: 'iconfont fiLink-alarm-secondary', number: '16', color: 'statistics-mainAlarm-color'},
      {name: this.language.secondaryAlarm, icon: 'iconfont fiLink-alarm-secondary', number: '21', color: 'statistics-minorAlarm-color'},
      {name: this.language.promptAlarm, icon: 'iconfont fiLink-alarm-prompt', number: '10', color: 'statistics-hintAlarm-color'},
    ];
    // 修改备注弹框
    this.initFormRemark();
    // 区域
    this.initAreaConfig();
    // 告警名称
    this.initAlarmName();
    // 告警对象
    this.initAlarmObjectConfig();
  }

  // 告警对象
  initAlarmObjectConfig() {
    const clear = this.checkAlarmObject.ids.length ? false : true;
    this.alarmObjectConfig = {
      clear: clear,
      alarmObject: (event) => {
        this.checkAlarmObject = event;
      }
    };
  }

  // 区域
  initAreaConfig() {
    const clear = this.areaList.ids.length ? false : true;
    this.areaConfig = {
      clear: clear,
      checkArea: (event) => {
        this.areaList = event;
      }
    };
  }

  // 告警名称
  initAlarmName() {
    const clear = this._checkAlarmName.ids.length ? false : true;
    this.alarmNameConfig = {
      clear: clear,
      alarmName: (event) => {
        this._checkAlarmName = event;
      }
    };
  }

  pageChange(event) {
    if (!this.templateId) {
      this.queryCondition.filterConditions = this.filterEvent;
      this.queryCondition.pageCondition.pageNum = event.pageIndex;
      this.queryCondition.pageCondition.pageSize = event.pageSize;
      this.refreshData();
    } else {
      const data = {
        queryCondition: {},
        pageCondition: {
          'pageNum': event.pageIndex,
          'pageSize': this.pageBean.pageSize
        }
      };
      this.templateList(data);
    }
  }


  // 备注
  public initFormRemark() {
    this.formColumnRemark = [
      {
        // 备注
        label: this.language.remark,
        key: 'remark',
        type: 'textarea',
        // require: true,
        width: 1000,
        rule: [
          // { maxLength: 255 },
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  // 修改备注弹框
  formInstanceRemark(event) {
    this.formStatusRemark = event.instance;
  }

  /**
   * 获取当前告警列表信息
   */
  refreshData(body?) {
    this.tableConfig.isLoading = true;
    if (this.deviceId) {
      const filterConditions = {
        'filterField': 'alarmSource',
        'operator': 'eq',
        'filterValue': this.deviceId
      };
      if (body) {
        // 当从首页进入后 将参数带入 翻页戴上
        if (body.filterConditions && body.filterConditions.length) {
          body.filterConditions.push(filterConditions);
        } else {
          body['filterConditions'] = [filterConditions];
        }
      } else {
        // 当从首页进入后 将参数带入 翻页戴上
        this.queryCondition.filterConditions = [filterConditions];
      }
    }
    this.$alarmService.queryCurrentAlarmList(body || this.queryCondition).subscribe((res: Result) => {
      this.giveList(res);
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  // 请求过来的数据 赋值到列表
  giveList(res) {
    this.pageBean.Total = res.totalCount;
    this.tableConfig.isLoading = false;
    this.pageBean.pageIndex = res.pageNum;
    this.pageBean.pageSize = res.size;
    this._dataSet = res.data;
    this._dataSet.forEach(item => {
      // 通过首次发生时间计算出告警持续时间
      // +item.alarmContinousTime
      // item.alarmBeginTime
      item.alarmContinousTime = CommonUtil.setAlarmContinousTime(item.alarmBeginTime, item.alarmCleanTime,
        {year: this.language.year, month: this.language.month, day: this.language.day, hour: this.language.hour});
      if (item.alarmBeginTime) {
        item.alarmBeginTime = this.$dateHelper.format(new Date(Number(item.alarmBeginTime)), DateFormatString.DATE_FORMAT_STRING);
      }
      if (item.alarmNearTime) {
        item.alarmNearTime = this.$dateHelper.format(new Date(Number(item.alarmNearTime)), DateFormatString.DATE_FORMAT_STRING);
      }
      if (item.alarmConfirmTime) {
        item.alarmConfirmTime = this.$dateHelper.format(new Date(Number(item.alarmConfirmTime)), DateFormatString.DATE_FORMAT_STRING);
      }
      if (item.alarmCleanTime) {
        item.alarmCleanTime = this.$dateHelper.format(new Date(Number(item.alarmCleanTime)), DateFormatString.DATE_FORMAT_STRING);
      }
      // 判断创建工单 禁启用
      item.isShowBuildOrder = item.alarmCode === 'orderOutOfTime' ? 'disabled' : true;
      item.isShowLocationIcon = false;
      item.isShowUpdateIcon = false;
      item.isShowViewIcon = false;
      item.isShowBuildOrderIcon = false;
    });
    this._dataSet = res.data.map(item => {
      item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
      return item;
    });
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      // noIndex: true,
      showSearchExport: true,
      searchReturnType: 'array',
      scroll: {x: '1200px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        // {
        //   type: 'serial-number', width: 62, title: this.language.serialNumber,
        //   fixedStyle: {fixedLeft: true, style: {left: '62px'}}
        // },
        {
          title: this.language.alarmName, key: 'alarmName', width: 200, isShowSort: true,
          searchable: true,
          // searchConfig: {type: 'input'},
          searchConfig: {
            type: 'render',
            renderTemplate: this.alarmName
          },
          fixedStyle: {fixedLeft: true, style: {left: '124px'}}
        },
        {
          // 告警级别
          title: this.language.alarmFixedLevel, key: 'alarmFixedLevel', width: 200, isShowSort: true,
          type: 'render',
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.language.urgent, value: '1'},
              {label: this.language.main, value: '2'},
              {label: this.language.secondary, value: '3'},
              {label: this.language.prompt, value: '4'}
            ]
          },
          renderTemplate: this.alarmFixedLevelTemp
        },
        {
          // 告警对象
          title: this.language.alarmobject, key: 'alarmObject', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          // searchConfig: {type: 'input'}
          searchConfig: {
            type: 'render',
            renderTemplate: this.departmentTemp
          },
        },
        {
          // 区域
          title: this.language.area, key: 'areaName', width: 120, isShowSort: true,
          configurable: true,
          searchable: true,
          // searchConfig: {type: 'input'}
          searchConfig: {
            type: 'render',
            // selectInfo: this.areaList.ids,
            renderTemplate: this.areaSelectorTemp
          },
        },
        {
          title: this.language.address, key: 'address', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.responsibleDepartment, key: 'responsibleDepartment', width: 120, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
          // searchConfig: { type: 'render', renderTemplate: this.unitTemp }
        },
        {
          // 设施类型
          title: this.language.alarmSourceType, key: 'alarmSourceTypeId', width: 120,
          configurable: true,
          searchable: true,
          isShowSort: true,
          type: 'render',
          renderTemplate: this.alarmSourceTypeTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.language.opticalBox, value: '001'},
              {label: this.language.well, value: '030'},
              {label: this.language.distributionFrame, value: '060'},
              {label: this.language.junctionBox, value: '090'},
              {label: this.language.splittingBox, value: '150'}
            ]
          }
        },
        {
          // 频次
          title: this.language.alarmHappenCount, key: 'alarmHappenCount', width: 100, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'input'}
        },
        {
          // 清除状态
          title: this.language.alarmCleanStatus, key: 'alarmCleanStatus', width: 120, isShowSort: true,
          type: 'render',
          configurable: true,
          searchable: true,
          renderTemplate: this.isCleanTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.language.noClean, value: 3},
              {label: this.language.isClean, value: 1},
              {label: this.language.deviceClean, value: 2}
            ]
          }
        },
        {
          // 确认状态
          title: this.language.alarmConfirmStatus, key: 'alarmConfirmStatus', width: 120, isShowSort: true,
          type: 'render',
          configurable: true,
          searchable: true,
          renderTemplate: this.isConfirmTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              {label: this.language.isConfirm, value: 1},
              {label: this.language.noConfirm, value: 2}
            ]
          }
        },
        {
          // 首次发生时间
          title: this.language.alarmBeginTime, key: 'alarmBeginTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'dateRang'}
        },
        {
          // 最近发生时间
          title: this.language.alarmNearTime, key: 'alarmNearTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'dateRang'}
        },
        {
          // 告警持续时间
          title: this.language.alarmContinousTime, key: 'alarmContinousTime', width: 280,
          // isShowSort: true,
          // searchable: true,
          // configurable: true,
          // searchConfig: { type: 'render', renderTemplate: this.alarmContinueTimeTemp }
        },
        {
          // 确认时间
          title: this.language.alarmConfirmTime, key: 'alarmConfirmTime', width: 280, isShowSort: true,
          searchable: true,
          searchConfig: {type: 'dateRang'}
        },
        {
          // 清除时间
          title: this.language.alarmCleanTime, key: 'alarmCleanTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'dateRang'}
        },
        {
          // 清除用户
          title: this.language.alarmCleanPeopleNickname, key: 'alarmCleanPeopleNickname', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'input'}
        },
        {
          // 确认用户
          title: this.language.alarmConfirmPeopleNickname, key: 'alarmConfirmPeopleNickname', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'input'}
        },
        {
          // 告警附加信息
          title: this.language.alarmAdditionalInformation, key: 'extraMsg', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          // type: 'render',
          searchConfig: {type: 'input'},
          // renderTemplate: this.alarmAppendTemp,
        },
        {
          // 告警处理建议
          title: this.language.alarmProcessing, key: 'alarmProcessing', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: {
            type: 'operate', customSearchHandle: () => {
              this.display.templateTable = true;
            }
          }, key: '', width: 150, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [],
      operation: [
        // build2功能
        {
          // 定位
          text: this.language.location,
          key: 'isShowBuildOrder',
          className: 'fiLink-location',
          disabledClassName: 'fiLink-location disabled-icon',
          handle: (e) => {
            console.log('定位', e);
            this.navigateToDetail('business/index', {queryParams: {id: e.alarmSource}});
          }
        },
        {
          // 修改备注
          text: this.language.updateRemark,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            // this.remarkValue = currentIndex.remark;
            this.formStatusRemark.resetControlData('remark', currentIndex.remark);
            this.display.remarkTable = true;
            this.checkRemark = [currentIndex];
          }
        },
        {
          // 查看图片
          text: this.language.viewPicture,
          className: 'fiLink-view-photo',
          // key: 'isShowViewIcon',
          handle: (e) => {
            // 查看图片
            this.examinePicture(e);
          }
        },
        {
          // 创建工单
          text: this.language.buildOrder,
          className: 'fiLink-create',
          key: 'isShowBuildOrder',
          disabledClassName: 'fiLink-create disabled-icon',
          // disabledClassName: ''
          handle: (e) => {
            this.display.creationWorkOrder = true;
            // this.creationWorkOrder(e);
            this.creationWorkOrderData = e;
          }
        }
      ],
      leftBottomButtons: [
        {
          // 告警确认
          text: this.language.alarmConfirm,
          canDisabled: true,
          permissionCode: '02-1-1',
          handle: (data) => {
            this.alarmComfirm(data);
          }
        },
        {
          // 告警清除
          text: this.language.alarmClean,
          canDisabled: true,
          permissionCode: '02-1-2',
          handle: (data) => {
            this.alarmClean(data);
          }
        },
        // build2功能
        {
          // 勾选备注
          text: this.language.updateRemark,
          permissionCode: '02-1-4',
          canDisabled: true,
          handle: (data) => {
            if (data && data.length) {
              this.display.remarkTable = true;
              this.checkRemark = data;
              this.formStatusRemark.resetControlData('remark', '');
            } else {
              this.$message.info(this.language.pleaseCheckThe);
            }
            // this.remarkValue = '';
          }
        }
      ],
      sort: (event: SortCondition) => {
        this.queryCondition.filterConditions = this.filterEvent;
        if (event.sortField === 'alarmContinousTimeName') {
          // 当进行告警持续时间排序时 传给后台的是 alarmContinousTime 这个参数
          this.queryCondition.sortCondition.sortField = 'alarmContinousTime';
        } else {
          this.queryCondition.sortCondition.sortField = event.sortField;
        }
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        if (!event.length) {
          this.templateId = undefined;
          this.filterEvent = [];
          //  告警名称 区域  告警对象 清空
          this.areaList = {
            ids: [],
            name: ''
          };
          this._checkAlarmName = {
            name: '',
            ids: []
          };
          this.checkAlarmObject = {
            ids: [],
            name: ''
          };
          // 区域
          this.initAreaConfig();
          // 告警名称
          this.initAlarmName();
          // 告警对象
          this.initAlarmObjectConfig();
          this.refreshData({'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex}});
        } else {
          const filterEvent = [];
          event.forEach(item => {
            switch (item.filterField) {
              case 'alarmHappenCount':
                // 频次
                filterEvent.push({
                  'filterField': 'alarmHappenCount',
                  'filterValue': Number(item.filterValue) ? Number(item.filterValue) : 0,
                  'operator': 'eq',
                });
                break;
              case 'alarmName':
                // 告警名称
                if (this._checkAlarmName.name) {
                  filterEvent.push({
                    'filterField': 'alarmName',
                    'filterValue': this._checkAlarmName.name.split(','),
                    'operator': 'in',
                  });
                }
                break;
              case 'alarmObject':
                // 告警对象
                if (this.checkAlarmObject.name) {
                  filterEvent.push({
                    'filterField': 'alarmObject',
                    'filterValue': this.checkAlarmObject.name.split(','),
                    'operator': 'in',
                  });
                }
                break;
              case 'areaName':
                // 区域
                if (this.areaList.name) {
                  filterEvent.push({
                    'filterField': 'areaName',
                    'filterValue': this.areaList.name.split(','),
                    'operator': 'in',
                  });
                }
                break;
              default:
                filterEvent.push(item);
            }
          });
          this.pageBean = new PageBean(this.queryCondition.pageCondition.pageSize, 1, 1);
          this.filterEvent = filterEvent;
          this.refreshData({
            'filterConditions': filterEvent,
            'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex}
          });
        }
      },
      handleExport: (event) => {
        for (let i = 0; i < event.columnInfoList.length; i++) {
          // if (event.columnInfoList[i].propertyName === 'areaName') {
          //   event.columnInfoList.splice(i, 1);
          //   i--;
          // }
          if (event.columnInfoList[i].propertyName === 'alarmFixedLevel') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmSourceTypeId') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmCleanStatus') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmConfirmStatus') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmBeginTime') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmNearTime') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmConfirmTime') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmCleanTime') {
            event.columnInfoList[i].isTranslation = 1;
          }
          if (event.columnInfoList[i].propertyName === 'alarmContinousTime') {
            event.columnInfoList[i].isTranslation = 1;
          }
        }
        // 处理参数
        const body = {
          queryCondition: new QueryCondition(),
          columnInfoList: event.columnInfoList,
          excelType: event.excelType,
          // bizCondition: event.queryTerm
        };
        // 处理选择的项目
        if (event.selectItem.length > 0) {
          event.queryTerm['alarmIds'] = event.selectItem.map(item => item.id);
          body.queryCondition.filterConditions = [];
          body.queryCondition.filterConditions.push({filterField: 'id', operator: 'in', filterValue: event.queryTerm['alarmIds']});
        } else {
          if (this.deviceId) {
            const filterConditions = {
              'filterField': 'alarmSource',
              'operator': 'eq',
              'filterValue': this.deviceId
            };
            if (body) {
              // 当从首页进入后 将参数带入 翻页戴上
              if (body.queryCondition.filterConditions && body.queryCondition.filterConditions.length) {
                body.queryCondition.filterConditions.push(filterConditions);
              } else {
                body.queryCondition['filterConditions'] = [filterConditions];
              }
            } else {
              // 当从首页进入后 将参数带入 翻页戴上
              this.queryCondition.filterConditions = [filterConditions];
            }
          }
          event.queryTerm.forEach(item => {
            if (item.filterField === 'alarmName') {
              // 告警名称
              item.filterValue = this._checkAlarmName.name.split(','),
                item.operator = 'in';
            } else if (item.filterField === 'alarmObject') {
              // 告警对象
              item.filterValue = this.checkAlarmObject.name.split(','),
                item.operator = 'in';
            } else if (item.filterField === 'alarmHappenCount') {
              // 频次
              item.filterValue = Number(item.filterValue);
              item.operator = 'eq';
            } else {
              item[item.filterField] = item.filterValue;
            }
          });
          if (this.templateId) {
            // 按照模板查询
            event.queryTerm.push({filterField: 'templateId', filterValue: this.templateId, operator: 'in'});
          }
          // 处理查询条件
          body.queryCondition.filterConditions = event.queryTerm;
        }
        this.$alarmService.exportAlarmList(body).subscribe((res: Result) => {
          if (res.code === 0) {
            this.$message.success(res.msg);
          } else {
            this.$message.error(res.msg);
          }
        });

      }
    };
  }

  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  // 查看图片
  examinePicture(data) {
    //  'c6c1517b8cc14600813cd7be8b070296  data.id'
    this.$alarmService.examinePicture(data.id).subscribe((res: Result) => {
      if (res.code === 0) {
        if (res.data.length === 0) {
          this.$message.warning('暂无图片');
        } else {
          this.$imageViewService.showPictureView(res.data);
        }
      } else {
        this.$message.error(res.msg);
      }
    });
  }

  // 模板查询
  templateTable(event) {
    this.display.templateTable = false;
    if (!event) {
      return;
    }
    this.table.handleRest();
    const data = {
      queryCondition: {},
      pageCondition: {
        'pageNum': 1,
        'pageSize': this.pageBean.pageSize
      }
    };
    if (event) {
      this.tableConfig.isLoading = true;
      this.templateId = event.id;
      this.templateList(data);
    }
  }

  // 模板查询 请求
  templateList(data) {
    // 点击确认进入
    this.$alarmService.alarmQueryTemplateById(this.templateId, data).subscribe(res => {
      if (res['code'] === 0) {
        this.giveList(res);
      } else if (res['code'] === 170219) {
        // this.$message.info(res['msg']);
        this._dataSet = [];
        this.tableConfig.isLoading = false;
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 告警确认
   */
  alarmComfirm(data) {
    this.confirmFlag = true;
    if (data.length > 0) {
      data.forEach(item => {
        if (item.alarmConfirmStatus === 1) {
          this.confirmFlag = false;
        }
      });

      if (!this.confirmFlag) {
        const alarmIds = [];
        data.forEach(item => {
          // if (item.alarmConfirmStatus === 2) {
            alarmIds.push({'id': item.id});
          // }
        });
        // if (alarmIds.length > 0) {
          this.alarmIds = alarmIds;
          this.popUpConfirm();
        // } else {
        //   this.$message.info(this.language.confirmAgain);
        // }
      }
      /////////////////////
      if (this.confirmFlag) {
        const ids = data.map(item => item.id);
        // const alarmIds = [];
        this.alarmIds = [];
        ids.forEach(item => {
          this.alarmIds.push({'id': item});
        });
        this.popUpConfirm();
      }
    } else {
      this.$message.info(this.language.pleaseCheckThe);
      return;
    }
  }

  // 告警确认 弹框
  popUpConfirm() {
    this.modalService.confirm({
      nzTitle: this.language.prompt,
      nzContent: this.language.alarmAffirm,
      nzOkText: this.language.cancelText,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: () => {
      },
      nzCancelText: this.language.okText,
      nzOnCancel: () => {
        this.confirmationBoxConfirm('affirm');
      },
    });
  }

  // 告警清除 弹框
  popUpClean() {
    this.modalService.confirm({
      nzTitle: this.language.prompt,
      nzContent: this.language.alarmAffirmClear,
      nzOkText: this.language.cancelText,
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: () => {
      },
      nzCancelText: this.language.okText,
      nzOnCancel: () => {
        this.confirmationBoxConfirm('cancel');
      },
    });
  }

  // 点击 确认
  confirmationBoxConfirm(type: 'affirm' | 'cancel') {
    if (type === 'affirm') {
      // 告警确认
      this.$alarmService.updateAlarmConfirmStatus(this.alarmIds).subscribe((res: Result) => {
        if (res.code === 0) {
          this.$message.success(res.msg);
          // this.table.handleRest();
          this.refreshData({
            'filterConditions': this.filterEvent,
            'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex}
          });
        } else {
          this.$message.error(res.msg);
        }
      });
    } else {
      // 告警清除
      this.$alarmService.updateAlarmCleanStatus(this.alarmIds).subscribe((res: Result) => {
        if (res.code === 0) {
          this.$message.success(res.msg);
          // this.table.handleRest();
          this.refreshData({
            'filterConditions': this.filterEvent,
            'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex}
          });
          this.$currServive.sendMessage(2);
        } else {
          this.$message.error(res.msg);
        }
      });
    }
  }

  /**
   * 告警清除
   */
  alarmClean(data) {
    this.cleanFlag = true;
    if (data.length > 0) {
      data.forEach(item => {
        if (item.alarmCleanStatus === 1 || item.alarmCleanStatus === 2) {
          this.cleanFlag = false;
        }
      });
      if (!this.cleanFlag) {
        const alarmIds = [];
        data.forEach(item => {
          // if (item.alarmCleanStatus === 3) {
            alarmIds.push({'id': item.id});
          // }
        });
        // if (alarmIds.length > 0) {
          this.alarmIds = alarmIds;
          // this.markedLanguage = this.language.alarmAffirmClear;
          // this.disabled.affirm = true;
          this.popUpClean();
        // } else {
        //   this.$message.info(this.language.cleanAgain);
        // }
      }
      if (this.cleanFlag) {
        const ids = data.map(item => item.id);
        // const alarmIds = [];
        this.alarmIds = [];
        ids.forEach(item => {
          this.alarmIds.push({'id': item});
        });
        // this.disabled.affirm = true;
        // this.markedLanguage = this.language.alarmAffirmClear;
        this.popUpClean();
      }
    } else {
      this.$message.info(this.language.pleaseCheckThe);
      return;
    }
  }

  // 修改备注
  updateAlarmRemark() {
    const remarkData = this.formStatusRemark.getData().remark;
    const remark = remarkData ? remarkData : null;
    const data = this.checkRemark.map(item => {
      return {id: item.id, remark: remark};
    });
    this.$alarmService.updateAlarmRemark(data).subscribe((res: Result) => {
      if (res.code === 0) {
        // this.refreshData();
        this.refreshData({
          'filterConditions': this.filterEvent,
          'pageCondition': {pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex}
        });
        this.$message.success(res.msg);
      } else {
        this.$message.info(res.msg);
      }
      this.display.remarkTable = false;
    });
  }

}



