import { Component, OnInit, ViewChild, TemplateRef, ElementRef } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService, NzModalService, DateHelperService } from 'ng-zorro-antd';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core-module/api-service/user';
import { Result } from '../../../../shared-module/entity/result';
import { UnitLanguageInterface } from '../../../../../assets/i18n/unit/unit-language.interface';
import { UserLanguageInterface } from '../../../../../assets/i18n/user/user-language.interface';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { DeptLevel, getDeptLevel } from '../../user.config';
import { DateFormatString } from '../../../../shared-module/entity/dateFormatString';

@Component({
  selector: 'app-unit-list',
  templateUrl: './unit-list.component.html',
  styleUrls: ['./unit-list.component.scss']
})
export class UnitListComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  filterObject = {};
  staffDataSet = [];
  staffPageBean: PageBean = new PageBean(10, 1, 1);
  staffTableConfig: TableConfig;
  _queryCondition: QueryCondition = new QueryCondition();
  userfilterObject = {};
  language: UnitLanguageInterface;
  userLanguage: UserLanguageInterface;
  deptLevel;
  deptId: string = '';
  roleArray = [];
  @ViewChild('deptLevelTemp') deptLevelTemp: TemplateRef<any>;
  @ViewChild('DeptNameTemp') DeptNameTemp: TemplateRef<any>;
  @ViewChild('subordinatesTemp') subordinatesTemp: TemplateRef<any>;
  @ViewChild('roleTemp') roleTemp: TemplateRef<any>;

  isVisible: boolean = false;
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $http: HttpClient,
    public $userService: UserService,
    public $message: FiLinkModalService,
    public $modal: NzModalService,
    private $dateHelper: DateHelperService
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('unit');
    this.userLanguage = this.$nzI18n.getLocaleData('user');
    this.initTableConfig();
    this._initTableConfig();
    this.queryAllRoles();
    this.deptLevel = DeptLevel;
    this.refreshData();
  }

  /**
   * 获取部门列表信息
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$userService.queryAllDept(this.queryCondition).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      this._dataSet = res.data.data;
      this.pageBean.Total = res.data.totalCount;
      this.pageBean.pageIndex = res.data.pageNum;
      this.pageBean.pageSize = res.data.size;
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 获取下属人员信息
   */
  subordinatesData(body?) {
    this.staffTableConfig.isLoading = true;
    this.$userService.queryUserLists(body || this._queryCondition).subscribe((res: Result) => {
      this.staffTableConfig.isLoading = false;
      this.staffDataSet = res.data.data;
      this.staffPageBean.Total = res.data.totalCount;
      this.staffPageBean.pageIndex = res.data.pageNum;
      this.staffPageBean.pageSize = res.data.size;
      this.staffDataSet.forEach(item => {
        // 账号有效期
        if (item.countValidityTime && item.createTime) {
          const validTime = item.countValidityTime;
          const createTime = item.createTime;
          const endVal = validTime.charAt(validTime.length - 1);
          const fontVal = validTime.substring(0, validTime.length - 1);
          const now = new Date(createTime);
          if (endVal === 'y') {
            const year_date = now.setFullYear(now.getFullYear() + Number(fontVal));
            item.countValidityTime = this.$dateHelper.format(new Date(year_date), DateFormatString.DATE_FORMAT_STRING_SIMPLE);
          } else if (endVal === 'm') {
            const week_date = now.setMonth(now.getMonth() + Number(fontVal));
            item.countValidityTime = this.$dateHelper.format(new Date(week_date), DateFormatString.DATE_FORMAT_STRING_SIMPLE);
          } else if (endVal === 'd') {
            const day_date = now.setDate(now.getDate() + Number(fontVal));
            item.countValidityTime = this.$dateHelper.format(new Date(day_date), DateFormatString.DATE_FORMAT_STRING_SIMPLE);
          }
        }
        // 上一次登录时间
        if (item.lastLoginTime) {
          item.lastLoginTime = this.$dateHelper.format(new Date(item.lastLoginTime), DateFormatString.DATE_FORMAT_STRING);
        }
      });
    }, () => {
      this.staffTableConfig.isLoading = false;
    });
  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  staffPageChange(event) {
    this._queryCondition.pageCondition.pageNum = event.pageIndex;
    this._queryCondition.pageCondition.pageSize = event.pageSize;
    const queryConditions = {  // 创建查询条件
      bizCondition: { deptId: this.deptId },
      filterConditions: [],
      pageCondition: {
        pageNum: this._queryCondition.pageCondition.pageNum,
        pageSize: this._queryCondition.pageCondition.pageSize
      },
      sortCondition: {}
    };
    this.subordinatesData(queryConditions);
  }


  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1500px', y: '340px' },
      noIndex: true,
      notShowPrint: true,
      columnConfig: [
        { type: 'expend', width: 30, expendDataKey: 'childDepartmentList', fixedStyle: { fixedLeft: true, style: { left: '0px' } } },
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '30px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '92px' } }
        },
        {
          title: this.language.deptName, key: 'deptName', width: 200, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '154px' } }
        },
        {
          title: this.language.parentDepartmentName, key: 'parmentDeparmentName', width: 180, isShowSort: true,
          type: 'render', configurable: true,
          renderTemplate: this.DeptNameTemp
        },
        {
          title: this.language.deptLevel, key: 'deptLevel', width: 100,
          configurable: true,
          type: 'render',
          searchable: true,
          searchConfig: {
            type: 'select', selectInfo: getDeptLevel(this.$nzI18n),
            notAllowClear: true,
            initialValue: '1', label: 'label', value: 'code'
          },
          renderTemplate: this.deptLevelTemp
        },
        {
          title: this.language.deptChargeUser, key: 'deptChargeuser', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.deptPhoneNum, key: 'deptPhonenum', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.address, key: 'address', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      topButtons: [
        {
          text: '+  ' + this.language.addUnit,
          permissionCode: '01-2-1',
          handle: (currentIndex) => {
            this.openUnitDetail();
          }
        },
        {
          text: this.language.deleteHandle,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          needConfirm: true,
          canDisabled: true,
          permissionCode: '01-2-3',
          handle: (currentIndex) => {
            const ids = currentIndex.map(item => item.id);
            const params = { 'firstArrayParamter': ids };
            this.$userService.deleteDept(params).subscribe((res: Result) => {
              if (res['code'] === 0) {
                this.$message.success(res['msg']);
                this.goToFirstPage();
              } else if (res['code'] === 120250) {
                this.$message.info(res['msg']);
              } else if (res['code'] === 120260) {
                this.$message.info(res['msg']);
              } else if (res['code'] === -1) {
                this.$message.error(res['msg']);
              } else {
                this.$message.info(res['msg']);
              }
            });
          }
        }
      ],
      operation: [
        {
          text: this.language.staff,
          className: 'fiLink-subordinates',
          permissionCode: '01-2-4',
          handle: (currentIndex) => {
            this._initTableConfig();
            this.deptId = currentIndex.id;
            this.$userService.queryDeptInfoById(currentIndex.id).subscribe((res: Result) => {
              if (res.code === 0) {
                const modal = this.$modal.create({
                  nzTitle: this.language.staff,
                  nzContent: this.subordinatesTemp,
                  nzOkText: this.language.cancelText,
                  nzCancelText: this.language.okText,
                  nzOkType: 'danger',
                  nzClassName: 'custom-create-modal',
                  nzWidth: '1000',
                  nzFooter: [
                    {
                      label: this.language.okText,
                      onClick: () => {
                        modal.destroy();
                      }
                    },
                    {
                      label: this.language.cancelText,
                      type: 'danger',
                      onClick: () => {
                        modal.destroy();
                      }
                    },
                  ]
                });
                const queryConditions = { // 创建查询条件
                  bizCondition: {
                    deptId: currentIndex.id   // 单位id
                  },
                  filterConditions: [],
                  pageCondition: {
                    pageNum: this._queryCondition.pageCondition.pageNum,
                    pageSize: this._queryCondition.pageCondition.pageSize
                  },
                  sortCondition: {}
                };
                this.subordinatesData(queryConditions);
              } else if (res.code === 120610) {
                this.$message.info(this.language.deptExistTips);
                this.goToFirstPage();
              }
            });
          }
        },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          permissionCode: '01-2-2',
          handle: (currentIndex) => {
            this.$userService.queryDeptInfoById(currentIndex.id).subscribe((res: Result) => {
              if (res.code === 0) {
                this.navigateToDetail('business/user/unit-detail/update', { queryParams: { id: currentIndex.id } });
              } else if (res.code === 120610) {
                this.$message.info(this.language.deptExistTips);
                this.goToFirstPage();
              }
            });

          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          permissionCode: '01-2-3',
          handle: (currentIndex) => {
            const params = { 'firstArrayParamter': [currentIndex.id] };
            this.$userService.queryDeptInfoById(currentIndex.id).subscribe((res: Result) => {
              if (res.code === 0) {
                this.$userService.deleteDept(params).subscribe((ress: Result) => {
                  if (ress['code'] === 0) {
                    this.$message.success(ress['msg']);
                    this.goToFirstPage();
                  } else if (ress['code'] === 120250) {
                    this.$message.info(ress['msg']);
                  } else if (ress['code'] === 120260) {
                    this.$message.info(ress['msg']);
                  } else if (ress['code'] === -1) {
                    this.$message.error(ress['msg']);
                  } else {
                    this.$message.info(ress['msg']);
                  }
                });
              } else if (res.code === 120610) {
                this.$message.info(this.language.deptExistTips);
                this.goToFirstPage();
              }
            });
          }
        },

      ],
      sort: (event: SortCondition) => {
        const obj = {};
        obj['sortProperties'] = event.sortField;
        obj['sort'] = event.sortRule;
        this.queryCondition.bizCondition = Object.assign(this.filterObject, obj);
        this.refreshData();
      },
      handleSearch: (event) => {
        const obj = {};
        event.forEach(item => {
          obj[item.filterField] = item.filterValue;
        });
        this.queryCondition.pageCondition.pageNum = 1;
        this.filterObject = obj;
        this.queryCondition.bizCondition = Object.assign(this.filterObject, obj);
        this.refreshData();
      }
    };
  }

  private _initTableConfig() {
    // 单位下属人员列表配置
    this.staffTableConfig = {
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1500px', y: '340px' },
      noIndex: true,
      notShowPrint: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.userLanguage.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '62px' } }
        },
        {
          title: this.userLanguage.userCode, key: 'userCode', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '124px' } }
        },
        {
          title: this.userLanguage.userName, key: 'userName', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.userNickname, key: 'userNickname', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.address, key: 'address', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.email, key: 'email', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.role, key: 'role', width: 150, isShowSort: true,
          searchable: true, configurable: true,
          type: 'render',
          renderTemplate: this.roleTemp,
          searchConfig: {
            type: 'select', selectType: 'multiple', selectInfo: this.roleArray
          }
        },
        {
          title: this.userLanguage.lastLoginTime, key: 'lastLoginTime', width: 280, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'dateRang' }
        },
        {
          title: this.userLanguage.lastLoginIp, key: 'lastLoginIp', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.userLanguage.maxUsers, key: 'maxUsers', width: 150, configurable: true, isShowSort: true,
        },
        {
          title: this.userLanguage.countValidityTime, key: 'countValidityTime', width: 150, configurable: true,
        },
        {
          title: this.userLanguage.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        }
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      // searchReturnType: 'Array',
      operation: [],
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
        let queryConditions_ = {  // 创建查询条件
          bizCondition: obj,
          filterConditions: [],
          pageCondition: {
            pageNum: this._queryCondition.pageCondition.pageNum,
            pageSize: this._queryCondition.pageCondition.pageSize
          },
          sortCondition: {}
        };
        queryConditions_.bizCondition['deptId'] = this.deptId;
        queryConditions_ = Object.assign(this.userfilterObject, queryConditions_);
        this.subordinatesData(queryConditions_);
      },
      handleSearch: (event) => {
        const obj = {};
        event.forEach(item => {
          if (item.operator === 'gte') {
            obj['lastLoginTime'] = item.filterValue;
          } else if (item.operator === 'lte') {
            obj['lastLoginTimeEnd'] = item.filterValue;
          } else if (item.filterField === 'role') {
            obj['roleNameList'] = item.filterValue;
          } else {
            obj[item.filterField] = item.filterValue;
          }
        });
        let queryConditions = {  // 创建查询条件
          bizCondition: obj,
          filterConditions: [],
          pageCondition: {
            pageNum: this._queryCondition.pageCondition.pageNum,
            pageSize: this._queryCondition.pageCondition.pageSize
          },
          sortCondition: {}
        };
        queryConditions.bizCondition['deptId'] = this.deptId;
        this._queryCondition.pageCondition.pageNum = 1;
        this.userfilterObject = queryConditions;
        queryConditions = Object.assign(this.userfilterObject, queryConditions);
        this.subordinatesData(queryConditions);

      }
    };
  }

  /**
   * 跳转新增用户页面
   */
  public openUnitDetail() {
    this.$router.navigate(['business/user/unit-detail/add']).then();
  }


  /**
   * 跳转到详情
   *
   */
  private navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }


  handleCancel() {
    this.isVisible = false;
  }


  handleOk() {
    this.isVisible = false;
  }


  // 跳第一页
  goToFirstPage() {
    this.queryCondition.pageCondition.pageNum = 1;
    this.refreshData();
  }

  /**
  * 查询角色
  */
  queryAllRoles() {
    this.$userService.queryAllRoles().subscribe((res: Result) => {
      const roleArr = res.data;
      if (roleArr) {
        roleArr.forEach(item => {
          this.roleArray.push({ 'label': item.roleName, 'value': item.roleName });
        });
      }

    });
  }
}

