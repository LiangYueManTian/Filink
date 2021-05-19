import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { ActivatedRoute, Router } from '@angular/router';
import {DateHelperService, NzI18nService } from 'ng-zorro-antd';
import { AlarmService } from '../../../../core-module/api-service/alarm';
import { Result } from '../../../../shared-module/entity/result';
import { AlarmLanguageInterface } from '../../../../../assets/i18n/alarm/alarm-language.interface';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmStoreService} from '../../../../core-module/store/alarm.store.service';
import {DateFormatString} from '../../../../shared-module/entity/dateFormatString';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import { FormItem } from '../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../shared-module/component/form/form-opearte.service';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import { AreaConfig, AlarmNameConfig, AlarmObjectConfig, UnitConfig } from 'src/app/shared-module/component/alarm/alarmSelectorConfig';
import { ImageViewService } from '../../../../shared-module/service/picture-view/image-view.service';

@Component({
  selector: 'app-history-alarm',
  templateUrl: './history-alarm.component.html',
  styleUrls: ['./history-alarm.component.scss']
})
export class HistoryAlarmComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: AlarmLanguageInterface;
  alarmId = null;
  deviceId = null;
  filterEvent;
  display = {
    remarkTable: false,
  };
  // 修改备注
  checkRemark: any[];

  // 修改备注弹框
  formColumnRemark: FormItem[] = [];
  formStatusRemark: FormOperate;

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

  @ViewChild('alarmFixedLevelTemp') alarmFixedLevelTemp: TemplateRef<any>;
  @ViewChild('alarmCleanStatusTemp') alarmCleanStatusTemp: TemplateRef<any>;
  @ViewChild('alarmConfirmStatusTemp') alarmConfirmStatusTemp: TemplateRef<any>;
  @ViewChild('alarmSourceTypeTemp') alarmSourceTypeTemp: TemplateRef<any>;
  @ViewChild('alarmName') private alarmName;
  @ViewChild('areaSelector') private areaSelectorTemp;
  @ViewChild('department') private departmentTemp;
  @ViewChild('unitTemp') private unitTemp;
  @ViewChild('alarmContinueTimeTemp') private alarmContinueTimeTemp;

  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $alarmService: AlarmService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $alarmStoreService: AlarmStoreService,
    private $dateHelper: DateHelperService,
    private $ruleUtil: RuleUtil,
    private $imageViewService: ImageViewService
  ) {
    this.language = this.$nzI18n.getLocaleData('alarm');
  }

  ngOnInit() {
    this.initTableConfig();
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
        'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex },
        'filterConditions': [{
          'filterField': 'id',
          'operator': 'eq',
          'filterValue': this.alarmId
        }]
      };
      this.refreshData(obj);
    // } else if (this.deviceId) {
    //   const obj = {
    //     'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex },
    //     'filterConditions': [{
    //       'filterField': 'alarmSource',
    //       'operator': 'eq',
    //       'filterValue': this.deviceId
    //     }]
    //   };
    //   this.refreshData(obj);
    } else {
      this.refreshData({ 'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex } });
    }
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
    this.queryCondition.filterConditions = this.filterEvent;
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 获取历史告警列表信息
   */
  refreshData(body?) {
    this.tableConfig.isLoading = true;
    if (this.deviceId ) {
      const filterConditions = {
        'filterField': 'alarmSource',
        'operator': 'eq',
        'filterValue': this.deviceId
      };
      if ( body ) {
        // 当从首页进入后 将参数带入 翻页戴上
        if ( body.filterConditions && body.filterConditions.length ) {
          body.filterConditions.push(filterConditions);
        } else {
          body['filterConditions'] = [filterConditions];
        }
      } else {
          // 当从首页进入后 将参数带入 翻页戴上
          this.queryCondition.filterConditions = [filterConditions];
      }
    }
    this.$alarmService.queryAlarmHistoryList(body || this.queryCondition).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      this.pageBean.Total = res.totalCount;
      this.pageBean.pageIndex = res.pageNum;
      this.pageBean.pageSize = res.size;
      this._dataSet = res.data;
      this._dataSet = res.data.map(item => {
        item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
        return item;
      });
      this._dataSet.forEach(item => {
        // 告警持续时间
        item.alarmContinousTime = CommonUtil.setAlarmContinousTime(item.alarmBeginTime, item.alarmCleanTime,
          { year: this.language.year, month: this.language.month, day: this.language.day, hour: this.language.hour });
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
        item.isShowBuildOrder = item.alarmCode === 'orderOutOfTime' ?  'disabled' : true;
      });
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      noIndex: true,
      showSearchExport: true,
      scroll: { x: '1200px', y: '600px' },
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 60 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '60px' } }
        },
        {
          title: this.language.alarmName, key: 'alarmName', width: 200, isShowSort: true,
          searchable: true,
          // searchConfig: { type: 'input' },
          searchConfig: {
            type: 'render',
            renderTemplate: this.alarmName
          },
          fixedStyle: { fixedLeft: true, style: { left: '122px' } }
        },
        {
          title: this.language.alarmFixedLevel, key: 'alarmFixedLevel', width: 140, isShowSort: true,
          type: 'render',
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              { label: this.language.urgent, value: '1' },
              { label: this.language.main, value: '2' },
              { label: this.language.secondary, value: '3' },
              { label: this.language.prompt, value: '4' }
            ]
          },
          renderTemplate: this.alarmFixedLevelTemp
        },
        {
          title: this.language.alarmobject, key: 'alarmObject', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          // searchConfig: { type: 'input' }
          searchConfig: {
            type: 'render',
            renderTemplate: this.departmentTemp },
        },
        {
          title: this.language.area, key: 'areaName', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          // searchConfig: { type: 'input' }
          searchConfig: { type: 'render',
          // selectInfo: this.areaList.ids,
          renderTemplate: this.areaSelectorTemp },
        },
        {
          title: this.language.address, key: 'address', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.responsibleDepartment, key: 'responsibleDepartment', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
          // searchConfig: { type: 'render', renderTemplate: this.unitTemp }
        },
        {
          title: this.language.alarmSourceType, key: 'alarmSourceTypeId', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.alarmSourceTypeTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              { label: this.language.opticalBox, value: '001' },
              { label: this.language.well, value: '030' },
              { label: this.language.distributionFrame, value: '060' },
              { label: this.language.junctionBox, value: '090' },
              { label: this.language.splittingBox, value: '150' }
            ]
          }
        },
        {
          title: this.language.alarmHappenCount, key: 'alarmHappenCount', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmCleanStatus, key: 'alarmCleanStatus', width: 200, isShowSort: true,
          type: 'render',
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              { label: this.language.isClean, value: 1 },
              { label: this.language.deviceClean, value: 2 }
            ]
          },
          renderTemplate: this.alarmCleanStatusTemp
        },
        {
          title: this.language.alarmConfirmStatus, key: 'alarmConfirmStatus', width: 200, isShowSort: true,
          type: 'render',
          configurable: true,
          searchable: true,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: [
              { label: this.language.isConfirm, value: 1 },
              { label: this.language.noConfirm, value: 2 }
            ]
          },
          renderTemplate: this.alarmConfirmStatusTemp
        },
        {
          title: this.language.alarmBeginTime, key: 'alarmBeginTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          title: this.language.alarmNearTime, key: 'alarmNearTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          title: this.language.alarmContinousTime, key: 'alarmContinousTime',
          width: 280,
          // isShowSort: true,
          // configurable: true,
          // searchable: true,
          // searchConfig: { type: 'render', renderTemplate: this.alarmContinueTimeTemp }
        },
        {
          title: this.language.alarmConfirmTime, key: 'alarmConfirmTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          title: this.language.alarmCleanTime, key: 'alarmCleanTime', width: 280, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          title: this.language.alarmCleanPeopleNickname, key: 'alarmCleanPeopleNickname', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.alarmConfirmPeopleNickname, key: 'alarmConfirmPeopleNickname', width: 120, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.extraMsg, key: 'extraMsg', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          // type: 'render',
          searchConfig: { type: 'input' },
          // renderTemplate: this.alarmAppendTemp,
        },
        {
          title: this.language.alarmProcessing, key: 'alarmProcessing', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          searchable: true,
          configurable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 120, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      topButtons: [],
      operation: [
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
      ],
      leftBottomButtons: [
        {
          text: this.language.historyAlarmSet, handle: (e) => {
            this.$router.navigate(['business/alarm/history-alarm-set']).then();
          }
        },
        // build2功能
        {
          // 勾选备注
          text: this.language.updateRemark,
          canDisabled: true,
          handle: (data) => {
            if ( data && data.length ) {
              this.display.remarkTable = true;
              this.checkRemark = data;
              this.formStatusRemark.resetControlData('remark', '');
            } else {
              this.$message.info(this.language.pleaseCheckThe);
            }
          }
        }
      ],
      sort: (event: SortCondition) => {
        this.queryCondition.filterConditions = this.filterEvent;
        if ( event.sortField === 'alarmContinousTime' ) {
          // 当进行告警持续时间排序时 传给后台的是 alarmContinousTime 这个参数
          this.queryCondition.sortCondition.sortField = 'alarmContinousTime';
        } else {
          this.queryCondition.sortCondition.sortField = event.sortField;
        }
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        if ( !event.length ) {
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
          event.forEach( item => {
            switch (item.filterField) {
              case 'alarmHappenCount':
                // 频次
                filterEvent.push ({
                  'filterField': 'alarmHappenCount',
                  'filterValue': Number(item.filterValue) ? Number(item.filterValue) : 0,
                  'operator': 'eq',
                });
                break;
              case 'alarmName':
                // 告警名称
                if ( this._checkAlarmName.name ) {
                  filterEvent.push({
                    'filterField': 'alarmName',
                    'filterValue': this._checkAlarmName.name.split(','),
                    'operator': 'in',
                  });
                }
                break;
              case 'alarmObject':
                // 告警对象
                if ( this.checkAlarmObject.name ) {
                  filterEvent.push ({
                    'filterField': 'alarmObject',
                    'filterValue': this.checkAlarmObject.name.split(','),
                    'operator': 'in',
                  });
                }
                break;
              case 'areaName':
                // 区域
                if ( this.areaList.name ) {
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
            'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex }
          });
        }
      },
      handleExport: (event) => {
        console.log( '导出', event);
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
          excelType: event.excelType
        };
        // 处理选择的项目
        if (event.selectItem.length > 0) {
          // body.queryCondition.bizCondition['areaIds'] = event.selectItem.map(item => item.areaId);
          event.queryTerm['alarmIds'] = event.selectItem.map(item => item.id);
          body.queryCondition.filterConditions = [];
          body.queryCondition.filterConditions.push({filterField: 'id', operator: 'in', filterValue: event.queryTerm['alarmIds']});
        } else {
          if (this.deviceId ) {
            const filterConditions = {
              'filterField': 'alarmSource',
              'operator': 'eq',
              'filterValue': this.deviceId
            };
            if ( body ) {
              // 当从首页进入后 将参数带入 翻页戴上
              if ( body.queryCondition.filterConditions && body.queryCondition.filterConditions.length ) {
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
            } else if ( item.filterField === 'alarmObject') {
              // 告警对象
              item.filterValue = this.checkAlarmObject.name.split(','),
              item.operator = 'in';
            } else if ( item.filterField === 'alarmHappenCount' ) {
              // 频次
              item.filterValue = Number(item.filterValue);
              item.operator = 'eq';
            } else {
              item[item.filterField] = item.filterValue;
            }
            }),
          // 处理查询条件
          body.queryCondition.filterConditions = event.queryTerm;
        }

        this.$alarmService.exportHistoryAlarmList(body).subscribe((res: Result) => {
          if (res.code === 0) {
            this.$message.success(res.msg);
          } else {
            this.$message.error(res.msg);
          }
        });
      }
    };
  }


  /**
   * 计算两个时间戳 之间隔了多少个小时
   * 告警持续时间 筛选时 用到了
   *  */
  getInervalHour( startTime, endTime ) {
    const ms = endTime - startTime;
    return Math.floor(ms / 1000 / 60 / 60);
  }

  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  // 查看图片
  examinePicture(data) {
    //  '5cb7eb3918080cf0b453b67d' data.id
    // this.pictureSrc = '';
    this.$alarmService.examinePictureHistory(data.id).subscribe( (res: Result) => {
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
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  // 修改备注
  updateAlarmRemark() {
    const remarkData = this.formStatusRemark.getData().remark;
    const remark = remarkData ? remarkData : null;
    const data = this.checkRemark.map(item => {
      return { id: item.id, remark: remark };
    });
    this.$alarmService.updateHistoryAlarmRemark(data).subscribe((res: Result) => {
      if (res.code === 0) {
        // this.refreshData();
        // this.filterEvent = event;
        this.refreshData({
          'filterConditions': this.filterEvent,
          'pageCondition': { pageSize: this.pageBean.pageSize, pageNum: this.pageBean.pageIndex }
        });
        this.$message.success(res.msg);
      } else {
        this.$message.info(res.msg);
      }
      this.display.remarkTable = false;
    });
  }

  // 修改备注弹框
  formInstanceRemark(event) {
    this.formStatusRemark = event.instance;
  }

  /**
   * 历史告警设置
   */
  navigateToLevelSet() {
    this.$router.navigate(['business/alarm/history-alarm-set']).then();
  }

}
