import {Component, Injectable, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {ActivatedRoute, Router} from '@angular/router';
import {DateHelperService, NzI18nService, NzModalService} from 'ng-zorro-antd';
import {AlarmService} from '../../../../core-module/api-service/alarm';
import {Result} from '../../../../shared-module/entity/result';
import {AlarmLanguageInterface} from '../../../../../assets/i18n/alarm/alarm-language.interface';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {AlarmStoreService} from '../../../../core-module/store/alarm.store.service';
import {CurrAlarmServiceService} from './curr-alarm-service.service';
import {CommonUtil} from '../../../../shared-module/util/common-util';
import {FormItem} from '../../../../shared-module/component/form/form-config';
import {RuleUtil} from '../../../../shared-module/util/rule-util';
import {FormOperate} from '../../../../shared-module/component/form/form-opearte.service';
import {AlarmNameConfig, AlarmObjectConfig, AreaConfig} from 'src/app/shared-module/component/alarm/alarmSelectorConfig';
import {SessionUtil} from '../../../../shared-module/util/session-util';
import {TableService} from 'src/app/shared-module/component/table/table.service';
import {ImageViewService} from '../../../../shared-module/service/picture-view/image-view.service';
import {
  alarmCleanStatus,
  getAlarmCleanStatus,
  getAlarmLevel,
  getAlarmType,
  getDeviceType,
  getIsConfirm
} from '../../../facility/facility.config';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import * as CurrentAlarmUtil from './current-alarm-util';
import {FilterCondition, QueryCondition} from '../../../../shared-module/entity/queryCondition';
import {TableComponent} from '../../../../shared-module/component/table/table.component';
import {AlarmHintList} from './const/alarm-hint-list.const';
import {AlarmLevel} from './const/alarm-level.const';

/**
 * ??????????????????
 * ??????: ?????????TS??????????????????????????? ???current-alarm-util???????????????????????? ??????????????????
 */
@Component({
  selector: 'app-current-alarm',
  templateUrl: './current-alarm.component.html',
  styleUrls: ['./current-alarm.component.scss'],
  providers: [TableService]
})

@Injectable()
export class CurrentAlarmComponent implements OnInit {
  // ????????????
  _dataSet = [];
  // ??????????????????
  pageBean: PageBean = new PageBean(10, 1, 1);
  // ????????????
  tableConfig: TableConfig;
  // ????????????
  queryCondition: QueryCondition = new QueryCondition();
  // ?????????????????????
  public language: AlarmLanguageInterface;
  // ????????????
  ifSpin = false;
  // ??????id
  alarmId = null;
  // ??????id
  deviceId = null;
  // token
  token: string = '';
  // ????????????
  userInfo = {};
  // ??????id
  userId: string = '';
  // ??????????????????
  confirmFlag = true;
  // ??????????????????
  cleanFlag = true;
  // ????????????
  alarmType = [];
  // ??????????????????????????????????????????
  display = {
    remarkTable: false,
    templateTable: false,
    creationWorkOrder: false,
    nameTable: false
  };
  // ????????????
  checkRemark: any[];
  isLoading: boolean = false;

  // ??????????????????
  formColumnRemark: FormItem[] = [];
  formStatusRemark: FormOperate;
  // ??????????????????
  alarmIds = [];
  // ?????????????????????
  creationWorkOrderData = {};
  // ??????????????????
  alarmNameConfig: AlarmNameConfig;
  // ?????????????????????
  _checkAlarmName = {
    name: '',
    ids: []
  };
  // ????????????
  areaConfig: AreaConfig;
  // ??????
  areaList = {
    ids: [],
    name: ''
  };
  // ??????????????????
  alarmObjectConfig: AlarmObjectConfig;
  checkAlarmObject = {
    ids: [],
    name: ''
  };
  // ??????ID
  templateId: any;
  // ????????????
  sliderConfig = [];
  // ??????????????????
  alarmHintList = [];
  // ????????????????????????
  alarmHintValue = 1;
  // ?????????slide????????????
  isClickSlider = false;
  // ??????????????????
  private viewLoading: boolean = false;
  // ????????????????????????
  @ViewChild('alarmFixedLevelTemp') alarmFixedLevelTemp: TemplateRef<any>;
  // ??????????????????
  @ViewChild('table') table: TableComponent;
  // ????????????????????????
  @ViewChild('isCleanTemp') isCleanTemp: TemplateRef<any>;
  // ????????????????????????
  @ViewChild('alarmSourceTypeTemp') alarmSourceTypeTemp: TemplateRef<any>;
  // ????????????????????????
  @ViewChild('isConfirmTemp') isConfirmTemp: TemplateRef<any>;
  // ????????????
  @ViewChild('alarmName') private alarmName;
  // ????????????
  @ViewChild('areaSelector') private areaSelectorTemp;
  // ????????????
  @ViewChild('department') private departmentTemp;
  @ViewChild('alarmContinueTimeTemp') private alarmContinueTimeTemp;
  areaId;
  // ????????????????????????????????? ??????????????????????????????
  hasPrompt: boolean = false;

  constructor(public $router: Router,
              public $nzI18n: NzI18nService,
              public $alarmService: AlarmService,
              public $message: FiLinkModalService,
              public $active: ActivatedRoute,
              public $alarmStoreService: AlarmStoreService,
              public $currServive: CurrAlarmServiceService,
              private $dateHelper: DateHelperService,
              private $ruleUtil: RuleUtil,
              private modalService: NzModalService,
              private $facilityService: FacilityService,
              private $imageViewService: ImageViewService) {
    this.language = this.$nzI18n.getLocaleData('alarm');
  }

  ngOnInit() {
    // ???????????????????????????
    this.alarmHintList = [
      {label: this.language.displayAlarmLevel, code: AlarmHintList.alarmLevelCode},
      {label: this.language.displayAlarmObjType, code: AlarmHintList.alarmObjTypeCode}
    ];
    // ?????????????????????
    CurrentAlarmUtil.initTableConfig(this);
    // ??????????????????
    if (SessionUtil.getToken()) {
      this.token = SessionUtil.getToken();
      this.userInfo = SessionUtil.getUserInfo();
      this.userId = this.userInfo['id'];
    }
    // ????????????id
    if (this.$active.snapshot.queryParams.id) {
      this.alarmId = this.$active.snapshot.queryParams.id;
      const filter = new FilterCondition('id');
      filter.operator = 'eq';
      filter.filterValue = this.alarmId;
      this.queryCondition.filterConditions = [filter];
    }
    // ????????????deviceId
    if (this.$active.snapshot.queryParams.deviceId) {
      this.deviceId = this.$active.snapshot.queryParams.deviceId;
      const filter = new FilterCondition('alarmSource');
      filter.operator = 'eq';
      filter.filterValue = this.deviceId;
      this.queryCondition.filterConditions = [filter];
    }
    this.queryCondition.pageCondition.pageSize = this.pageBean.pageSize;
    this.queryCondition.pageCondition.pageNum = this.pageBean.pageIndex;
    this.refreshData();
    // ??????????????????
    this.initFormRemark();
    // ??????
    this.initAreaConfig();
    // ????????????
    this.initAlarmName();
    // ????????????
    this.initAlarmObjectConfig();
    // ????????????, ????????????????????????
    this.queryDeviceTypeCount(AlarmHintList.alarmLevelCode);
  }

  /**
   * ??????????????????
   */
  initAlarmObjectConfig() {
    this.alarmObjectConfig = {
      clear: !this.checkAlarmObject.ids.length,
      alarmObject: (event) => {
        this.checkAlarmObject = event;
      }
    };
  }

  /**
   * ????????????
   */
  initAreaConfig() {
    this.areaConfig = {
      clear: !this.areaList.ids.length,
      checkArea: (event) => {
        this.areaList = event;
      }
    };
  }

  /**
   *  ??????????????????
   */
  initAlarmName() {
    this.alarmNameConfig = {
      clear: !this._checkAlarmName.ids.length,
      alarmName: (event) => {
        this._checkAlarmName = event;
      }
    };
  }

  /**
   * ????????????
   */
  pageChange(event) {
    if (!this.templateId) {
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

  /**
   * ????????????
   */
  public initFormRemark() {
    this.formColumnRemark = [
      {
        // ??????
        label: this.language.remark,
        key: 'remark',
        type: 'textarea',
        width: 1000,
        rule: [
          this.$ruleUtil.getRemarkMaxLengthRule()
        ],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      },
    ];
  }

  /**
   * ????????????????????????
   */
  formInstanceRemark(event) {
    this.formStatusRemark = event.instance;
  }

  /**
   * ??????????????????????????????
   */
  refreshData() {
    // this.ifSpin = true;
    this.tableConfig.isLoading = true;
    this.$alarmService.queryCurrentAlarmList(this.queryCondition).subscribe((res: Result) => {
      // this.ifSpin = false;
      this.tableConfig.isLoading = false;
      this.giveList(res);
    }, () => {
      // this.ifSpin = false;
      this.tableConfig.isLoading = false;

    });
  }

  /**
   * ????????????????????? ???????????????
   */
  giveList(res) {
    this.pageBean.Total = res.totalCount;
    this.tableConfig.isLoading = false;
    this.pageBean.pageIndex = res.pageNum;
    this.pageBean.pageSize = res.size;
    this._dataSet = res.data || [];
    // ????????????????????????????????????????????????????????????
    const hasId = this.$active.snapshot.queryParams.id || this.$active.snapshot.queryParams.deviceId;
    if ((!this.hasPrompt) && this._dataSet.length === 0 && hasId) {
      this.hasPrompt = true;
      this.$message.info(this.language.noCurrentAlarmData);
    }
    this._dataSet.forEach(item => {
      // ???????????????????????????????????????????????????
      item.alarmContinousTime = CommonUtil.setAlarmContinousTime(item.alarmBeginTime, item.alarmCleanTime,
        {year: this.language.year, month: this.language.month, day: this.language.day, hour: this.language.hour});
      // ?????????????????? ?????????
      item.isShowBuildOrder = item.alarmCode === 'orderOutOfTime' ? 'disabled' : true;
      item.isShowLocationIcon = false;
      item.isShowUpdateIcon = false;
      item.isShowViewIcon = false;
      item.isShowBuildOrderIcon = false;
    });
    this._dataSet = res.data.map(item => {
      item.style = this.$alarmStoreService.getAlarmColorByLevel(item.alarmFixedLevel);
      item.alarmFixedLevelName = getAlarmLevel(this.$nzI18n, item.alarmFixedLevel);
      item.alarmCleanStatusName = getAlarmCleanStatus(this.$nzI18n, item.alarmCleanStatus);
      item.alarmSourceTypeName = getDeviceType(this.$nzI18n, item.alarmSourceTypeId);
      item.alarmConfirmStatusName = getIsConfirm(this.$nzI18n, item.alarmConfirmStatus);
      item.alarmCode = getAlarmType(this.$nzI18n, item.alarmCode);
      return item;
    });
  }

  /**
   *  ??????????????????
   */
  exportAlarm(body) {
    this.$alarmService.exportAlarmList(body).subscribe((res: Result) => {
      if (res.code === 0) {
        this.$message.success(res.msg);
      } else {
        this.$message.error(res.msg);
      }
    });
  }

  /**
   * ????????????
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  /**
   * ????????????
   */
  examinePicture(data) {
    // ?????????????????????
    if (this.viewLoading) {
      return;
    }
    this.viewLoading = true;
    this.$alarmService.examinePicture(data.id).subscribe((res: Result) => {
      this.viewLoading = false;
      if (res.code === 0) {
        if (res.data.length === 0) {
          this.$message.warning(this.language.noPicturesYet);
        } else {
          this.$imageViewService.showPictureView(res.data);
        }
      } else {
        this.$message.error(res.msg);
      }
    }, () => {
      this.viewLoading = false;
    });
  }

  /**
   * ????????????
   */
  templateTable(event) {
    this.display.templateTable = false;
    if (!event) {
      return;
    }
    // ????????????????????????????????????
    this.table.searchDate = {};
    this.table.rangDateValue = {};
    this.table.tableService.resetFilterConditions(this.table.queryTerm);
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

  /**
   * ???????????? ??????
   */
  templateList(data) {
    // ??????????????????
    this.$alarmService.alarmQueryTemplateById(this.templateId, data).subscribe(res => {
      if (res['code'] === 0) {
        this.giveList(res);
      } else if (res['code'] === 170219) {
        this._dataSet = [];
        this.tableConfig.isLoading = false;
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * ????????????
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
          if (item.alarmConfirmStatus === 2) {
            alarmIds.push({'id': item.id});
          }
        });
        if (alarmIds.length > 0) {
          this.alarmIds = alarmIds;
          this.popUpConfirm();
        } else {
          this.$message.info(this.language.confirmAgain);
        }
      }
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
    }
  }

  /**
   *  ???????????? ??????
   */
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

  /**
   * ???????????? ??????
   */
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

  /**
   * ?????? ??????
   */
  confirmationBoxConfirm(type: string) {
    // this.ifSpin = true;
    this.tableConfig.isLoading = true;
    if (type === 'affirm') {
      // ????????????
      this.$alarmService.updateAlarmConfirmStatus(this.alarmIds).subscribe((res: Result) => {
        if (res.code === 0) {
          this.$message.success(res.msg);
          this.refreshData();
        } else {
          // this.ifSpin = false;
          this.tableConfig.isLoading = true;
          this.$message.info(res.msg);
        }
      }, () => {
        this.tableConfig.isLoading = false;
      });
    } else {
      // ????????????
      this.$alarmService.updateAlarmCleanStatus(this.alarmIds).subscribe((res: Result) => {
        if (res.code === 0) {
          this.$message.success(res.msg);
          this.refreshData();
          // ????????????????????????
          this.queryDeviceTypeCount(this.alarmHintValue);
          this.$currServive.sendMessage(2);
        } else {
          // this.ifSpin = false;
          this.tableConfig.isLoading = true;
          this.$message.info(res.msg);
        }
      }, () => {
        this.tableConfig.isLoading = false;
      });
    }
  }

  /**
   * ????????????
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
          if (item.alarmCleanStatus === 3) {
            alarmIds.push({'id': item.id});
          }
        });
        if (alarmIds.length > 0) {
          this.alarmIds = alarmIds;
          this.popUpClean();
        } else {
          this.$message.info(this.language.cleanAgain);
        }
      }
      if (this.cleanFlag) {
        const ids = data.map(item => item.id);
        this.alarmIds = [];
        ids.forEach(item => {
          this.alarmIds.push({'id': item});
        });
        this.popUpClean();
      }
    } else {
      this.$message.info(this.language.pleaseCheckThe);
      return;
    }
  }

  /**
   * ????????????
   */
  updateAlarmRemark() {
    const remarkData = this.formStatusRemark.getData().remark;
    const remark = remarkData ? remarkData : null;
    const data = this.checkRemark.map(item => {
      return {id: item.id, remark: remark};
    });
    this.tableConfig.isLoading = true;
    this.$alarmService.updateAlarmRemark(data).subscribe((res: Result) => {
      if (res.code === 0) {
        this.refreshData();
        this.$message.success(res.msg);
      } else {
        this.$message.info(res.msg);
        this.tableConfig.isLoading = false;
      }
      this.display.remarkTable = false;
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * ???????????????????????????
   * alarmLevel ???????????????
   * deviceType ???????????????
   */
  sliderChange(event) {
    // ??????????????????
    CurrentAlarmUtil.clearData(this);
    // ????????????????????????????????????,
    this.table.tableService.resetFilterConditions(this.table.queryTerm);
    // ?????????slide???????????????
    this.isClickSlider = true;
    if (this.deviceId) {
      const filter = new FilterCondition('alarmSource');
      filter.operator = 'eq';
      filter.filterValue = this.deviceId;
      this.table.queryTerm.set('deviceId', filter);
    }
    if (this.alarmId) {
      const filter = new FilterCondition('id');
      filter.operator = 'eq';
      filter.filterValue = this.alarmId;
      this.table.queryTerm.set('alarmId', filter);
    }
    this.table.handleSetControlData('alarmCleanStatus', [alarmCleanStatus.noClean]);
    if (event.label === this.language.alarmSum) {
      this.table.handleSearch();
    } else {
      // ???????????????????????????????????????????????????
      // ????????????????????????????????????
      this.table.searchDate = {};
      this.table.rangDateValue = {};
      if (event.type === 'alarmLevel') {
        this.table.handleSetControlData('alarmFixedLevel', [event.levelCode]);
      } else {
        this.table.handleSetControlData('alarmSourceTypeId', [event.levelCode]);
      }
      this.table.handleSearch();
    }
  }

  /**
   * ????????????
   * param event
   */
  slideShowChange(event) {
    if (event) {
      this.tableConfig.outHeight = 108;
    } else {
      this.tableConfig.outHeight = 8;
    }
    this.table.calcTableHeight();
  }

  /**
   * ???????????????????????????????????????
   */
  alarmHintValueModelChange() {
    if (this.alarmHintValue === AlarmHintList.alarmLevelCode) {
      this.queryDeviceTypeCount(AlarmHintList.alarmLevelCode);
    } else {
      this.queryDeviceTypeCount(AlarmHintList.alarmObjTypeCode);
    }
  }

  /**
   * ?????????????????? ??????????????????
   * 1. ???????????????
   * 2. ????????????
   */
  private queryDeviceTypeCount(selectType: number) {
    this.sliderConfig = [];
    this.alarmType = [];
    if (selectType === AlarmHintList.alarmLevelCode) {
      // ????????????????????????
      if (this.alarmId) {
        // ??????ID
        this.$alarmService.queryAlarmIdHonePage({id: this.alarmId}).subscribe(res => {
          if (res['code'] === 0) {
            CurrentAlarmUtil.lineUpGive(this, 'level', res['data']);
          }
        });
      } else if (this.deviceId) {
        // ????????????????????????
        this.$alarmService.queryAlarmDeviceIdHonePage({deviceId: this.deviceId}).subscribe(res => {
          if (res['code'] === 0) {
            CurrentAlarmUtil.lineUpGive(this, 'level', res['data']);
          }
        });
      } else {
        const urgentAlarm = CurrentAlarmUtil.cardDataAnalysis(this, 'urgentAlarmCount', 0);
        const mainAlarm = CurrentAlarmUtil.cardDataAnalysis(this, 'mainAlarmCount', 0);
        const secondaryAlarm = CurrentAlarmUtil.cardDataAnalysis(this, 'minorAlarmCount', 0);
        const promptAlarm = CurrentAlarmUtil.cardDataAnalysis(this, 'hintAlarmCount', 0);
        this.alarmType = [urgentAlarm, mainAlarm, secondaryAlarm, promptAlarm];
        CurrentAlarmUtil.assignmentCard(this, 'level');
        // ????????????????????????
        this.$alarmService.queryEveryAlarmCount(AlarmLevel.urgentAlarmCode).subscribe(res => {
          if (res['code'] === 0) {
            this.sliderConfig[1]['sum'] = res['data'];
            this.sliderConfig[0]['sum'] += res['data'];
            this.sliderConfig[1]['color'] = this.$alarmStoreService.getAlarmColorByLevel(AlarmLevel.urgentAlarmCode).backgroundColor;
          }
        });
        this.$alarmService.queryEveryAlarmCount(AlarmLevel.mainAlarmCode).subscribe(res => {
          if (res['code'] === 0) {
            this.sliderConfig[2]['sum'] = res['data'];
            this.sliderConfig[0]['sum'] += res['data'];
            this.sliderConfig[2]['color'] = this.$alarmStoreService.getAlarmColorByLevel(AlarmLevel.mainAlarmCode).backgroundColor;
          }
        });
        this.$alarmService.queryEveryAlarmCount(AlarmLevel.minorAlarmCode).subscribe(res => {
          if (res['code'] === 0) {
            this.sliderConfig[3]['sum'] = res['data'];
            this.sliderConfig[0]['sum'] += res['data'];
            this.sliderConfig[3]['color'] = this.$alarmStoreService.getAlarmColorByLevel(AlarmLevel.minorAlarmCode).backgroundColor;
          }
        });
        this.$alarmService.queryEveryAlarmCount(AlarmLevel.hintAlarmCode).subscribe(res => {
          if (res['code'] === 0) {
            this.sliderConfig[4]['sum'] = res['data'];
            this.sliderConfig[0]['sum'] += res['data'];
            this.sliderConfig[4]['color'] = this.$alarmStoreService.getAlarmColorByLevel(AlarmLevel.hintAlarmCode).backgroundColor;
          }
        });
      }
    } else {
      if (this.alarmId) {
        this.$alarmService.queryAlarmIdCountHonePage({id: this.alarmId}).subscribe((result: Result) => {
          // ?????????
          if (result.code === 0) {
            CurrentAlarmUtil.lineUpGive(this, 'device', result.data);
          }
        });
      } else if (this.deviceId) {
        this.$alarmService.queryAlarmObjectCountHonePage({deviceId: this.deviceId}).subscribe((result: Result) => {
          // ?????????
          if (result.code === 0) {
            CurrentAlarmUtil.lineUpGive(this, 'device', result.data);
          }
        });
      } else {
        const Optical_Box = CurrentAlarmUtil.cardDataAnalysis(this, 'opticalBok', 0);
        const Well = CurrentAlarmUtil.cardDataAnalysis(this, 'well', 0);
        const Distribution_Frame = CurrentAlarmUtil.cardDataAnalysis(this, 'distributionFrame', 0);
        const Junction_Box = CurrentAlarmUtil.cardDataAnalysis(this, 'junctionBox', 0);
        const OUTDOOR_CABINET = CurrentAlarmUtil.cardDataAnalysis(this, 'splittingBox', 0);
        this.sliderConfig = [Optical_Box, Well, Distribution_Frame, Junction_Box, OUTDOOR_CABINET];
        CurrentAlarmUtil.assignmentCard(this, 'device');
        this.$alarmService.queryAlarmObjectCount('001').subscribe((result: Result) => {
          // ?????????
          if (result.code === 0) {
            this.sliderConfig[1]['sum'] = result.data;
            this.sliderConfig[0]['sum'] += result.data;
          }
        });
        this.$alarmService.queryAlarmObjectCount('030').subscribe((result: Result) => {
          // ??????
          if (result.code === 0) {
            this.sliderConfig[2]['sum'] = result.data;
            this.sliderConfig[0]['sum'] += result.data;
          }
        });
        this.$alarmService.queryAlarmObjectCount('060').subscribe((result: Result) => {
          // ?????????
          if (result.code === 0) {
            this.sliderConfig[3]['sum'] = result.data;
            this.sliderConfig[0]['sum'] += result.data;
          }
        });
        this.$alarmService.queryAlarmObjectCount('090').subscribe((result: Result) => {
          // ?????????
          if (result.code === 0) {
            this.sliderConfig[4]['sum'] = result.data;
            this.sliderConfig[0]['sum'] += result.data;
          }
        });
        this.$alarmService.queryAlarmObjectCount('210').subscribe((result: Result) => {
          // ?????????
          if (result.code === 0) {
            this.sliderConfig[5]['sum'] = result.data;
            this.sliderConfig[0]['sum'] += result.data;
          }
        });
      }
    }
  }
}
