import { Component, OnInit, ViewChild, TemplateRef, OnDestroy } from '@angular/core';
import { FormItem } from '../../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../../shared-module/component/form/form-opearte.service';
import { NzI18nService, NzMessageService, DateHelperService, NzModalService, bg_BG } from 'ng-zorro-antd';
import { FacilityAuthLanguageInterface } from '../../../../../../assets/i18n/facility-authorization/facilityAuth-language.interface';
import { UserService } from '../../../../../core-module/api-service/user/user-manage/user.service';
import { ActivatedRoute, Router, Params, NavigationStart } from '@angular/router';
import { Result } from '../../../../../shared-module/entity/result';
import { FiLinkModalService } from '../../../../../shared-module/service/filink-modal/filink-modal.service';
import { CommonUtil } from '../../../../../shared-module/util/common-util';
import { DateFormatString } from '../../../../../shared-module/entity/dateFormatString';
import { FacilityUtilService } from '../../../../facility/facility-util.service';
import { PageBean } from '../../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../../shared-module/entity/tableConfig';
import { FilterCondition, QueryCondition, SortCondition } from '../../../../../shared-module/entity/queryCondition';
import { UserLanguageInterface } from '../../../../../../assets/i18n/user/user-language.interface';
import { FacilityLanguageInterface } from '../../../../../../assets/i18n/facility/facility.language.interface';
import { FacilityService } from '../../../../../core-module/api-service/facility/facility-manage';
import { DeviceTypeCode, getDeployStatus, getDeviceStatus, getDeviceType } from '../../../../facility/facility.config';
import { RuleUtil } from '../../../../../shared-module/util/rule-util';
import { permissionId } from './permissionId';
import { UserUtilService } from '../../../user-util.service';
import { DoorNumber, getDoorNumber } from '../../../user.config';

@Component({
  selector: 'app-unified-details',
  templateUrl: './unified-details.component.html',
  styleUrls: ['./unified-details.component.scss']
})

export class UnifiedDetailsComponent implements OnInit, OnDestroy {
  language: FacilityAuthLanguageInterface;
  userLanguage: UserLanguageInterface;
  facilityLanguage: FacilityLanguageInterface;
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  isLoading: boolean = false;
  authInfo = {};
  authId: string = '';
  pageType = 'add';
  pageTitle: string;
  authEffectiveTime: any = null; // 权限生效时间(新增)
  authExpirationTime: any = null; // 权限失效时间(新增)
  effectiveTime: any = null; // 权限生效时间(修改)
  expirationTime: any = null; // 权限失效时间(修改)
  @ViewChild('effectiveTimeTemp') effectiveTimeTemp: TemplateRef<any>;
  @ViewChild('expirationTimeTemp') expirationTimeTemp: TemplateRef<any>;
  @ViewChild('authRangeTemp') authRangeTemp: TemplateRef<any>;
  @ViewChild('userTemp') userTemp: TemplateRef<any>;
  @ViewChild('userListTemp') userListTemp: TemplateRef<any>;
  @ViewChild('facilityListTemp') facilityListTemp: TemplateRef<any>;
  @ViewChild('xCTableComp') xCTableComp;
  @ViewChild('authXcTableComp') authXcTableComp;
  @ViewChild('deviceStatusTemp') deviceStatusTemp: TemplateRef<any>;
  @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;
  @ViewChild('radioTemp') radioTemp: TemplateRef<any>;
  @ViewChild('doorLocksTemp') doorLocksTemp: TemplateRef<any>;
  @ViewChild('thTemplate') thTemplate: TemplateRef<any>;
  authName: string = '';
  selectUserName: string = '';
  selectedUserId = null;
  userId: string = '';
  userName: string = '';
  deviceAndDoorData = [];
  concatParams = [];
  userDataSet = [];
  userPageBean: PageBean = new PageBean(10, 1, 1);
  userTableConfig: TableConfig;
  userQueryCondition: QueryCondition = new QueryCondition();
  filterObject = {};
  facilityDataSet = [];
  facilityPageBean: PageBean = new PageBean(10, 1, 1);
  facilityTableConfig: TableConfig;
  facilityQueryCondition: QueryCondition = new QueryCondition();
  checkList = [];  // 设施列表勾选数据
  deviceId: string;
  doorNum: string;
  constructor(
    private $nzI18n: NzI18nService,
    private $userService: UserService,
    private $activatedRoute: ActivatedRoute,
    private $router: Router,
    private $message: FiLinkModalService,
    private $dateHelper: DateHelperService,
    private $facilityUtilService: FacilityUtilService,
    private $modal: NzModalService,
    private $facilityService: FacilityService,
    private $ruleUtil: RuleUtil,
    private $userUtilService: UserUtilService
  ) {
    this.language = this.$nzI18n.getLocaleData('facilityAuth');
    this.userLanguage = this.$nzI18n.getLocaleData('user');
    this.facilityLanguage = this.$nzI18n.getLocaleData('facility');
  }

  ngOnInit() {
    this.$activatedRoute.params.subscribe((params: Params) => {
      this.pageType = params.type;
    });
    this.pageTitle = this.getPageTitle(this.pageType);
    this.initColumn();
    if (this.pageType !== 'add') {
      this.$activatedRoute.queryParams.subscribe(params => {
        this.authId = params.id;
        this.queryAuthInfoById(this.authId);   // 查询单个统一授权信息
      });
    } else {
      // 从别的页面跳到新增页面
      this.$activatedRoute.queryParams.subscribe(params => {
        if (!(JSON.stringify(params) === '{}')) {
          this.deviceId = params.id;
          this.doorNum = params.slotNum;
          const doorNumArr = this.doorNum.split(',');
          this.checkList.push(this.deviceId);  // 缓存设施id,用于查看设施列表-设施的勾选状态
          doorNumArr.forEach(item => {
            this.deviceAndDoorData.push({   // 缓存门编号id，用于查看设施列表-门编号的勾选状态
              deviceId: this.deviceId,
              doorId: item
            });
          });
          const query_Conditions = [
            {
              filterField: 'deviceId',
              operator: 'in',
              filterValue: [this.deviceId],
            }
          ];
          query_Conditions.forEach(item => {
            this.facilityQueryCondition.filterConditions.push(item);
          });
          setTimeout(() => {
            this.formStatus.resetControlData('authDeviceList', this.deviceAndDoorData);
          }, 0);
        }
      });

    }
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }

  userPageChange(event) {
    this.userQueryCondition.pageCondition.pageNum = event.pageIndex;
    this.userQueryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshUserData();
  }


  facilityPageChange(event) {
    this.facilityQueryCondition.pageCondition.pageNum = event.pageIndex;
    this.facilityQueryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshFacilityData();
  }

  private initColumn() {
    this.formColumn = [
      {
        label: this.language.name,
        key: 'name',
        type: 'input',
        require: true,
        rule: [{ required: true }, { maxLength: 32 },
        this.$ruleUtil.getNameRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
        asyncRules: [
          this.$ruleUtil.getNameAsyncRule(value => this.$userService.queryAuthByName(
            { name: value }),
            res => {
              if (res['code'] === 0) {
                if (res.data.length === 0) {
                  return true;
                } else if (res.data[0].id === this.authId) {
                  return true;
                }
              } else {
                return false;
              }
            })
        ]
      },
      {
        label: this.language.user,
        key: 'userId',
        type: 'custom',
        template: this.userTemp,
        require: true,
        rule: [{ required: true }],
        asyncRules: []
      },
      {
        label: this.language.authEffectiveTime,
        key: 'authEffectiveTime',
        type: 'custom',
        template: this.effectiveTimeTemp,
        require: false,
        rule: [],
        asyncRules: []
      },
      {
        label: this.language.authExpirationTime,
        key: 'authExpirationTime',
        type: 'custom',
        template: this.expirationTimeTemp,
        require: false,
        rule: [],
        asyncRules: []
      },
      {
        label: this.language.authStatus,
        key: 'authStatus',
        type: 'radio',
        require: true,
        col: 24,
        radioInfo: {
          data: [
            { label: this.language.takeEffect, value: 2 },
            { label: this.language.prohibit, value: 1 },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key, formOperate: FormOperate) => {
        },
        rule: [{ required: true }],
        asyncRules: []
      },
      {
        label: this.language.unifiedAuthRange,
        key: 'authDeviceList',
        type: 'custom',
        template: this.authRangeTemp,
        require: false,
        rule: [],
        asyncRules: []
      },
      {
        label: this.language.remark,
        key: 'remark',
        type: 'input',
        require: false,
        rule: [this.$ruleUtil.getRemarkMaxLengthRule()],
        customRules: [this.$ruleUtil.getNameCustomRule()],
      }
    ];
  }


  private initTableConfig() {
    // 用户列表配置
    this.userTableConfig = {
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1400px', y: '340px' },
      noIndex: true,
      showSearchExport: false,
      notShowPrint: true,
      columnConfig: [
        {
          title: '',
          type: 'render',
          renderTemplate: this.radioTemp,
          fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 42
        },
        {
          type: 'serial-number', width: 62, title: this.userLanguage.serialNumber,
        },
        {
          title: this.userLanguage.userCode, key: 'userCode', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
        },
        {
          title: this.userLanguage.userName, key: 'userName', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.userNickname, key: 'userNickname', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.address, key: 'address', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.email, key: 'email', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.lastLoginIp, key: 'lastLoginIp', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.maxUsers, key: 'maxUsers', width: 120, isShowSort: true
        },
        {
          title: this.userLanguage.userDesc, key: 'userdesc', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        }
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      sort: (event: SortCondition) => {
        const obj = {};
        obj['sortProperties'] = event.sortField;
        obj['sort'] = event.sortRule;
        if (obj['sortProperties'] === 'userNickname') {
          obj['sortProperties'] = 'user_nickname';
        }
        if (obj['sortProperties'] === 'userName') {
          obj['sortProperties'] = 'user_name';
        }
        if (obj['sortProperties'] === 'userCode') {
          obj['sortProperties'] = 'user_code';
        }
        if (obj['sortProperties'] === 'userStatus') {
          obj['sortProperties'] = 'user_status';
        }
        if (obj['sortProperties'] === 'loginType') {
          obj['sortProperties'] = 'login_type';
        }
        if (obj['sortProperties'] === 'lastLoginTime') {
          obj['sortProperties'] = 'last_login_time';
        }
        if (obj['sortProperties'] === 'lastLoginIp') {
          obj['sortProperties'] = 'last_login_ip';
        }
        if (obj['sortProperties'] === 'maxUsers') {
          obj['sortProperties'] = 'max_users';
        }
        // this.userQueryCondition.bizCondition = obj;
        this.userQueryCondition.bizCondition = Object.assign(this.filterObject, obj);
        this.refreshUserData();
      },
      handleSearch: (event) => {
        const obj = {};
        event.forEach(item => {
          obj[item.filterField] = item.filterValue;
        });
        this.userQueryCondition.pageCondition.pageNum = 1;
        this.filterObject = obj;
        // this.userQueryCondition.bizCondition = obj;
        this.userQueryCondition.bizCondition = Object.assign(this.filterObject, obj);
        this.refreshUserData();
      }
    };
  }

  private initFacilityTableConfig() {
    // 设施列表配置
    this.facilityTableConfig = {
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1304px', y: '340px' },
      noIndex: true,
      showSearchExport: false,
      notShowPrint: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '62px' } }
        },
        {
          title: this.facilityLanguage.deviceName, key: 'deviceName', width: 150,
          isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '124px' } }
        },
        {
          title: this.facilityLanguage.deviceType, key: 'deviceType', width: 200,
          type: 'render',
          renderTemplate: this.deviceTypeTemp,
          searchable: true,
          isShowSort: true,
          searchConfig: { type: 'select', selectType: 'multiple', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code' }
        },
        {
          title: this.facilityLanguage.deviceStatus, key: 'deviceStatus', width: 120,
          type: 'render',
          renderTemplate: this.deviceStatusTemp,
          isShowSort: true,
          searchable: true,
          minWidth: 80,
          searchConfig: { type: 'select', selectType: 'multiple', selectInfo: getDeviceStatus(this.$nzI18n), label: 'label', value: 'code' }
        },
        {
          title: this.facilityLanguage.deviceCode, key: 'deviceCode', width: 200,
          searchable: true,
          isShowSort: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.facilityLanguage.parentId, key: 'areaName', width: 200,
          searchable: true,
          isShowSort: true,
          searchConfig: { type: 'input' },
        },
        {
          title: this.facilityLanguage.deployStatus, key: 'deployStatus', width: 150,
          isShowSort: true,
          searchable: true,
          searchConfig: { type: 'select', selectType: 'multiple', selectInfo: getDeployStatus(this.$nzI18n), label: 'label', value: 'code' }
        },
        {
          title: this.facilityLanguage.remarks, key: 'remarks', width: 100,
          searchable: true,
          isShowSort: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.lockList, key: '_lockList', width: 340,
          type: 'render',
          minWidth: 340,
          // thTemplate: this.thTemplate,
          renderTemplate: this.doorLocksTemp
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      sort: (event: SortCondition) => {
        this.facilityQueryCondition.sortCondition.sortField = event.sortField;
        this.facilityQueryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshFacilityData();
      },
      handleSearch: (event) => {
        if (event.length === 0) {
          this.deviceAndDoorData = [];
          this.checkList = [];
          this.formStatus.group.controls['authDeviceList'].reset(null); // 重置清空
        }
        this.facilityQueryCondition.pageCondition.pageNum = 1;
        this.facilityQueryCondition.filterConditions = event;
        this.refreshFacilityData();
      },
      handleSelect: (event, currentItem) => {
        if (currentItem.checked) {
          // 勾选
          this.checkList.push(currentItem.deviceId);
        } else {
          // 取消勾选
          this.checkList = this.checkList.filter(item => {
            return currentItem.deviceId !== item && item;
          });
        }
      }
    };
  }

  private getPageTitle(type): string {
    let title;
    switch (type) {
      case 'add':
        title = `${this.language.add}${this.language.unifiedAuthorization}`;
        break;
      case 'update':
        title = `${this.language.modify}${this.language.unifiedAuthorization}`;
        break;
    }
    return title;
  }


  /**
   * 权限生效时间
   */
  effectiveOnChange(result: Date): void {
    this.effectiveTime = result;
    this.authInfo['authEffectiveTime'] = CommonUtil.getTimeStamp(this.effectiveTime);
  }

  effectiveOnOk(result: Date): void {
    this.effectiveTime = result;
    this.authInfo['authEffectiveTime'] = CommonUtil.getTimeStamp(this.effectiveTime);
  }


  /**
   * 权限失效时间
   */
  expirationOnChange(result: Date): void {
    this.expirationTime = result;
    this.authInfo['authExpirationTime'] = CommonUtil.getTimeStamp(this.expirationTime);
  }

  expirationOnOk(result: Date): void {
    this.expirationTime = result;
    this.authInfo['authExpirationTime'] = CommonUtil.getTimeStamp(this.expirationTime);
  }


  /**
   * 新增、修改统一授权
   */
  submit() {
    this.isLoading = true;
    const nowTime = CommonUtil.getTimeStamp(new Date());
    if (this.pageType === 'add') {
      const authObj = this.formStatus.getData();
      authObj.authEffectiveTime = CommonUtil.getTimeStamp(this.authEffectiveTime);
      authObj.authExpirationTime = CommonUtil.getTimeStamp(this.authExpirationTime);
      const effective_Time = authObj.authEffectiveTime;
      const expiration_Time = authObj.authExpirationTime;
      if (expiration_Time > 0 && effective_Time > 0 && expiration_Time < effective_Time) {
        this.$message.info(this.language.expirationTimeTips);
        this.isLoading = false;
      } else {
        this.addUnifiedAuth(authObj);
      }
    } else if (this.pageType === 'update') {
      const authObj = this.formStatus.getData();
      authObj.id = this.authId;
      authObj.authEffectiveTime = this.authInfo['authEffectiveTime'];
      authObj.authExpirationTime = this.authInfo['authExpirationTime'];
      const effectiveTime = authObj.authEffectiveTime;
      const expirationTime = authObj.authExpirationTime;
      if (expirationTime > 0 && effectiveTime > 0 && expirationTime < effectiveTime) {
        this.$message.info(this.language.expirationTimeTips);
        this.isLoading = false;
      } else {
        this.$userService.queryUnifyAuthById(this.authId).subscribe((res: Result) => {
          if (res.code === 0) {
            this.updateUnifiedAuth(authObj);
          } else if (res.code === 120610) {
            this.isLoading = false;
            this.$message.info(this.language.AuthExistTips);
            this.$router.navigate(['/business/user/unified-authorization']).then();
          }
        });
      }
    }

  }



  /**
   * 新增统一授权
   */
  addUnifiedAuth(authObj) {
    this.$userService.addUnifyAuth(authObj).subscribe((res: Result) => {
      this.isLoading = false;
      if (res.code === 0) {
        this.$message.success(res.msg);
        this.$router.navigate(['/business/user/unified-authorization']).then();
      } else if (res.code === 120340) {
        this.$message.info(res.msg);
      } else if (res.code === 120350) {
        this.$message.info(res.msg);
      } else {
        this.$message.error(res.msg);
      }
    });
  }

  /**
   *  修改统一授权
   */
  updateUnifiedAuth(authObj) {
    this.$userService.modifyUnifyAuth(authObj).subscribe((res: Result) => {
      this.isLoading = false;
      if (res.code === 0) {
        this.$message.success(res.msg);
        this.$router.navigate(['/business/user/unified-authorization']).then();
      } else if (res.code === 120340) {
        this.$message.info(res.msg);
      } else if (res.code === 120350) {
        this.$message.info(res.msg);
      } else {
        this.$message.error(res.msg);
      }
    });
  }


  /**
   * 查询单个统一授权信息
   */
  queryAuthInfoById(authId) {
    this.$userService.queryUnifyAuthById(authId).subscribe((res: Result) => {
      const authInfo = res.data;
      this.authInfo = authInfo;
      if (res.data.authDeviceList.length > 0) {
        res.data.authDeviceList.forEach(item => {
          this.checkList.push(item.deviceId);  // 缓存设施id,用于查看设施列表-设施的勾选状态
          this.deviceAndDoorData.push({   // 缓存门编号id，用于查看设施列表-门编号的勾选状态
            deviceId: item.deviceId,
            doorId: item.doorId
          });
        });
      }
      if (authInfo.authEffectiveTime) {
        this.authEffectiveTime = this.$dateHelper.format(new Date(authInfo.authEffectiveTime), DateFormatString.DATE_FORMAT_STRING);
      }
      if (authInfo.authExpirationTime) {
        this.authExpirationTime = this.$dateHelper.format(new Date(authInfo.authExpirationTime), DateFormatString.DATE_FORMAT_STRING);
      }
      this.formStatus.resetData(authInfo);
      this.selectUserName = authInfo.user.userName;
    });
  }


  goBack() {
    this.$router.navigate(['/business/user/unified-authorization']).then();
  }


  /**
   * 被授权用户列表
   */
  showUserListModal() {
    this.initTableConfig();
    for (const key in this.userQueryCondition.bizCondition) {
      if (this.userQueryCondition.bizCondition[key]) {
        delete this.userQueryCondition.bizCondition[key];  // 清空查询对象缓存
      }
    }
    const modal = this.$modal.create({
      nzTitle: this.language.selectingAuthorizedUsers,
      nzContent: this.userListTemp,
      nzOkText: this.language.cancel,
      nzCancelText: this.language.confirm,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzMaskClosable: false,
      nzWidth: '1000',
      nzFooter: [
        {
          label: this.language.confirm,
          onClick: () => {
            this.selectUserName = this.userName;
            this.formStatus.resetControlData('userId', this.userId);
            this.selectedUserId = null;
            modal.destroy();
          }
        },
        {
          label: this.language.cancel,
          type: 'danger',
          onClick: () => {
            this.selectUserName = '';
            this.formStatus.resetControlData('userId', null);
            this.selectedUserId = null;
            modal.destroy();
          }
        },
      ]
    });
    this.refreshUserData();
  }


  /**
   * 获取用户列表信息
   */
  refreshUserData() {
    this.userTableConfig.isLoading = true;
    this.userQueryCondition.bizCondition.permissionId = permissionId; // 带上权限id
    this.$userService.queryUserByPermission(this.userQueryCondition).subscribe((res: Result) => {
      this.userTableConfig.isLoading = false;
      this.userDataSet = res.data.data;
      this.userPageBean.Total = res.data.totalCount;
      this.userPageBean.pageIndex = res.data.pageNum;
      this.userPageBean.pageSize = res.data.size;
    }, () => {
      this.userTableConfig.isLoading = false;
    });

  }

  /**
   * 获取设施列表信息
   */
  private refreshFacilityData() {
    this.facilityTableConfig.isLoading = true;
    this.$facilityService.deviceListByPage(this.facilityQueryCondition).subscribe((result: Result) => {
      this.facilityPageBean.Total = result.totalCount;
      this.facilityPageBean.pageIndex = result.pageNum;
      this.facilityPageBean.pageSize = result.size;
      this.facilityTableConfig.isLoading = false;
      this.facilityDataSet = result.data;
      this.facilityDataSet.forEach(item => {
        // 门锁
        item._lockList = this.getLockList(item.lockList);
        item.areaName = item.areaInfo ? item.areaInfo.areaName : '';
        item['_deviceType'] = item.deviceType;
        item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
        item['_deviceStatus'] = item.deviceStatus;
        item.deviceStatus = getDeviceStatus(this.$nzI18n, item.deviceStatus);
        item.deployStatus = getDeployStatus(this.$nzI18n, item.deployStatus);
        item['iconClass'] = CommonUtil.getFacilityIconClassName(item._deviceType);
        if (this.checkList.includes(item.deviceId)) {
          item.checked = true;  // 翻页记住上次设施勾选数据
        }
        setTimeout(() => {
          if (this.deviceAndDoorData.length > 0 && item._lockList && item._lockList.length > 0) {
            for (let i = 0; i < this.deviceAndDoorData.length; i++) {
              const iDeviceId = this.deviceAndDoorData[i].deviceId;
              const iDoorId = this.deviceAndDoorData[i].doorId;
              for (let t = 0; t < item._lockList.length; t++) {
                if (iDeviceId === item._lockList[t].deviceId && iDoorId === item._lockList[t].value) {
                  item._lockList[t].checked = true;   // 翻页记住上次门编号勾选数据
                }
              }
            }
          }
        }, 0);
      });
    }, () => {
      this.facilityTableConfig.isLoading = false;
    });
  }

  /**
   * 打开设施列表
   */
  showFacilityListModal() {
    this.initFacilityTableConfig();
    if (!this.deviceId) {
      this.facilityQueryCondition.filterConditions = [];
    }
    const modal = this.$modal.create({
      nzTitle: this.language.selectingFacilities,
      nzContent: this.facilityListTemp,
      nzOkText: this.language.cancel,
      nzCancelText: this.language.confirm,
      nzOkType: 'danger',
      nzClassName: 'custom-create-modal',
      nzWidth: '1000',
      nzFooter: [
        {
          label: this.language.confirm,
          onClick: () => {
            this.concatParams = [];  // 置空
            let deviceAndDoorParams = [];
            if (this.checkList.length === 0) {
              deviceAndDoorParams = [];
              this.deviceAndDoorData = [];
            } else {
              this.checkList.forEach(item => {
                if (item) {
                  deviceAndDoorParams.push({
                    deviceId: item
                  });
                }
              });
            }
            if (deviceAndDoorParams.length > 0 && this.deviceAndDoorData.length > 0) {
              for (let i = 0; i < deviceAndDoorParams.length; i++) {
                for (let t = 0; t < this.deviceAndDoorData.length; t++) {
                  if (deviceAndDoorParams[i]['deviceId'] === this.deviceAndDoorData[t]['deviceId']) {
                    deviceAndDoorParams.splice(i, 1);
                    i--;
                    break;
                  }
                }
              }
            }
            this.concatParams = this.deviceAndDoorData.concat(deviceAndDoorParams);
            this.formStatus.resetControlData('authDeviceList', this.concatParams);
            this.facilityQueryCondition.pageCondition.pageNum = 1;
            modal.destroy();
          }
        },
        {
          label: this.language.cancel,
          type: 'danger',
          onClick: () => {
            this.facilityDataSet = [];
            if (!this.deviceId) {
              this.deviceAndDoorData = [];
              this.checkList = [];
            }
            this.facilityQueryCondition.pageCondition.pageNum = 1;
            modal.destroy();
          }
        },
      ]
    });
    this.refreshFacilityData();
  }

  /**
   * 选择门锁
   */
  checkOptions(item) {
    this.deviceAndDoorData.push({
      deviceId: item.deviceId,
      doorId: item.value
    });
    if (this.deviceAndDoorData.length > 1) {
      for (let i = 0; i < this.deviceAndDoorData.length; i++) {
        for (let j = i + 1; j < this.deviceAndDoorData.length; j++) {
          const iDeviceId = this.deviceAndDoorData[j].deviceId;
          const iDoorId = this.deviceAndDoorData[j].doorId;
          if (this.deviceAndDoorData[i].deviceId === iDeviceId && this.deviceAndDoorData[i].doorId === iDoorId) {
            this.deviceAndDoorData.splice(j, 1);
            this.deviceAndDoorData.splice(i, 1);
          }
        }
      }
    }
  }


  checkOptionChange(event) {
    // console.log(event);
  }


  /**
   *设施下的门锁
   */
  getLockList(data) {
    if (data && data.length > 0) {
      const lockData = [];
      data.forEach(e => {
        lockData.push(
          {
            label: getDoorNumber(this.$nzI18n, e.doorNum),  // 翻译门编号名称
            value: e.doorNum,
            checked: false,
            deviceId: e.deviceId
          }
        );
      });
      lockData.sort(this.$userUtilService.sort);  // 排序(升序)
      return lockData;
    }
  }


  /**
   * 单选框，选择用户
   */
  selectedUserChange(event, data) {
    this.userName = data.userName;
    this.userId = data.id;
    this.selectUserName = data.userName;
    this.formStatus.resetControlData('userId', data.id);
  }


  ngOnDestroy() {
  }

}
