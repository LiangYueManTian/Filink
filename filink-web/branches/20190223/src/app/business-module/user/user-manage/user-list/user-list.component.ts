import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService, NzModalService } from 'ng-zorro-antd';
import { UserService } from '../../../../core-module/api-service/user';
import { Result } from '../../../../shared-module/entity/result';
import { UserLanguageInterface } from '../../../../../assets/i18n/user/user-language.interface';
import { FilterCondition, QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { FormItem } from '../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../shared-module/component/form/form-opearte.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})

export class UserListComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: UserLanguageInterface;
  public idArray: Array<string>;
  public deptArray: Array<any> = [];
  isVisible: boolean = false;
  userId: string = '';
  flag: boolean = true;
  defaultPassWord: null;
  @ViewChild('userStatusTemp') userStatusTemp: TemplateRef<any>;
  @ViewChild('areaNameTemp') areaNameTemp: TemplateRef<any>;
  @ViewChild('departmentTemp') departmentTemp: TemplateRef<any>;
  @ViewChild('roleTemp') roleTemp: TemplateRef<any>;
  @ViewChild('loginTypeTemp') loginTypeTemp: TemplateRef<any>;
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $userService: UserService,
    public $message: FiLinkModalService,
    public $nzModalService: NzModalService
  ) {
  }


  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('user');
    this.initTableConfig();
    this.refreshData();
    this.queryAllDept();
    this.queryUserPassWord();

  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }


  /**
   * 获取用户列表信息
   */
  refreshData() {
    this.tableConfig.isLoading = true;
    this.$userService.queryUserLists(this.queryCondition).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      this._dataSet = res.data.data;
      this.pageBean.Total = res.data.totalCount;
      this.pageBean.pageIndex = res.data.pageNum;
      this.pageBean.pageSize = res.data.size;
      // console.log(this._dataSet);
      this._dataSet.forEach(item => {
        // 账号有效期
        if (item.countValidityTime && item.createTime) {
          const validTime = item.countValidityTime;
          const createTime = item.createTime;
          const endVal = validTime.charAt(validTime.length - 1);
          const fontVal = validTime.substring(0, validTime.length - 1);
          const now = new Date(createTime);
          if (endVal === 'y') {
            const year_date = now.setFullYear(now.getFullYear() + Number(fontVal));
            item.countValidityTime = this.$nzI18n.formatDateCompatible(new Date(year_date), 'yyyy-MM-dd');
          } else if (endVal === 'm') {
            const week_date = now.setMonth(now.getMonth() + Number(fontVal));
            item.countValidityTime = this.$nzI18n.formatDateCompatible(new Date(week_date), 'yyyy-MM-dd');
          } else if (endVal === 'd') {
            const day_date = now.setDate(now.getDate() + Number(fontVal));
            item.countValidityTime = this.$nzI18n.formatDateCompatible(new Date(day_date), 'yyyy-MM-dd');
          }
        }
        // 上一次登录时间
        if (item.lastLoginTime) {
          item.lastLoginTime = this.$nzI18n.formatDateCompatible(new Date(item.lastLoginTime), 'yyyy-MM-dd HH:mm:ss');
        }
      });
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '2200px', y: '340px' },
      noIndex: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '62px' } }
        },
        {
          title: this.language.userNickname, key: 'userNickname', width: 150, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '124px' } }
        },
        {
          title: this.language.userName, key: 'userName', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.userCode, key: 'userCode', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.userStatus, key: 'userStatus', width: 120, isShowSort: true,
          configurable: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.userStatusTemp,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.disable, value: '0' },
              { label: this.language.enable, value: '1' }
            ]
          },
          handleFilter: ($event) => {
          },
        },
        {
          title: this.language.unit, key: 'department', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.departmentTemp,
          searchConfig: {
            type: 'select', selectInfo: this.deptArray
          },
          handleFilter: ($event) => {
          }
        },
        {
          title: this.language.address, key: 'address', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.phonenumber, key: 'phonenumber', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.email, key: 'email', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.role, key: 'role', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.roleTemp,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.area, key: 'area', width: 100, isShowSort: true,
          configurable: true
        },
        {
          title: this.language.lastLoginTime, key: 'lastLoginTime', width: 280, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'date' }
        },
        {
          title: this.language.lastLoginIp, key: 'lastLoginIp', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.loginType, key: 'loginType', width: 120, configurable: true, isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.loginTypeTemp,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.singleUser, value: '1' },
              { label: this.language.multiUser, value: '2' }
            ]
          },
          handleFilter: ($event) => { },
        },
        {
          title: this.language.maxUsers, key: 'maxUsers', width: 120, configurable: true, isShowSort: true,
        },
        {
          title: this.language.countValidityTime, key: 'countValidityTime', width: 150, configurable: true,
        },
        {
          title: this.language.userdesc, key: 'userdesc', width: 150, configurable: true, isShowSort: true,
          searchable: true,
          remarkPipe: 'remark',
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 100, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        }
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      topButtons: [
        {
          text: '+  ' + this.language.addUser,
          handle: (currentIndex) => {
            this.openAddUser();
          }
        },
        {
          text: this.language.deleteHandle,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          needConfirm: true,
          canDisabled: true,
          handle: (data) => {
            this.flag = true;
            if (data.length > 0) {
              data.forEach(item => {
                if (item.userName === 'admin') {
                  this.flag = false;
                }
              });
              if (!this.flag) {
                this.$message.info(this.language.defaultUserTips);
              }
              if (this.flag) {
                const ids = data.map(item => item.id);
                const params = { 'firstArrayParamter': ids };
                this.$userService.deleteUser(params).subscribe((res: Result) => {
                  if (res['code'] === 120210) {
                    this.$nzModalService.confirm({
                      nzTitle: this.language.deleteOnlineUserTips,
                      nzContent: '',
                      nzMaskClosable: false,
                      nzOkText: this.language.cancelText,
                      nzCancelText: this.language.okText,
                      nzOkType: 'danger',
                      nzOnOk: () => {
                      },
                      nzOnCancel: () => {
                        const onlineParams = { 'firstArrayParamter': ids, 'flag': true };
                        this.$userService.deleteUser(onlineParams).subscribe((result: Result) => {
                          if (result.code === 0) {
                            this.$message.success(this.language.deleteOnlineUserSuccessTips);
                            this.goToFirstPage();
                          } else {
                            this.$message.error(this.language.deleteOnlineUserFailTips);
                          }
                        });
                      }
                    });
                  } else if (res['code'] === 120220) {
                    this.$message.info(this.language.defaultUserTips);
                  } else if (res['code'] === 120280) {
                    this.$message.info(res.msg);
                  } else if (res['code'] === 0) {
                    this.$message.success(this.language.deleteUserSuccess);
                    this.goToFirstPage();
                  } else {
                    this.$message.error(this.language.deleteUserFail);
                  }
                });
              }
            } else {
              this.$message.info(this.language.usersDeleteTips);
              return;
            }
          }
        }
      ],
      operation: [
        {
          text: this.language.resetPassword,
          className: 'fiLink-password-reset',
          handle: (data) => {
            this.userId = data.id;
            const params = { 'userId': this.userId };
            this.$nzModalService.confirm({
              nzTitle: this.language.resetPasswordTitle,
              nzContent: this.language.resetPasswordContent + this.defaultPassWord,
              nzMaskClosable: false,
              nzOkText: this.language.cancelText,
              nzCancelText: this.language.okText,
              nzOkType: 'danger',
              nzOnOk: () => {
              },
              nzOnCancel: () => {
                this.$userService.restPassword(params).subscribe((result: Result) => {
                  if (result.code === 0) {
                    this.$message.success(this.language.resetSuccessTips);
                    this.goToFirstPage();
                  } else {
                    this.$message.error(this.language.resetFailTips);
                  }
                });
              }
            });
          }
        },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.openModifyUser(currentIndex.id);
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          handle: (currentIndex) => {
            const userName = currentIndex.userName;
            if (userName && userName === 'admin') {
              this.$message.info(this.language.defaultUserTips);
              return;
            } else {
              const params = { 'firstArrayParamter': [currentIndex.id] };
              this.$userService.deleteUser(params).subscribe((res: Result) => {
                if (res['code'] === 120210) {
                  this.$nzModalService.confirm({
                    nzTitle: this.language.deleteOnlineUserTips,
                    nzContent: '',
                    nzMaskClosable: false,
                    nzOkText: this.language.cancelText,
                    nzCancelText: this.language.okText,
                    nzOkType: 'danger',
                    nzOnOk: () => {
                    },
                    nzOnCancel: () => {
                      const onlineParams = { 'firstArrayParamter': [currentIndex.id], 'flag': true };
                      this.$userService.deleteUser(onlineParams).subscribe((result: Result) => {
                        if (result.code === 0) {
                          this.$message.success(this.language.deleteOnlineUserSuccessTips);
                          this.goToFirstPage();
                        } else {
                          this.$message.error(this.language.deleteOnlineUserFailTips);
                        }
                      });
                    }
                  });
                } else if (res['code'] === 120220) {
                  this.$message.info(this.language.defaultUserTips);
                } else if (res['code'] === 120280) {
                  this.$message.info(res.msg);
                } else if (res['code'] === 0) {
                  this.$message.success(this.language.deleteUserSuccess);
                  this.goToFirstPage();
                } else {
                  this.$message.error(this.language.deleteUserFail);
                }
              });
            }

          }
        }
      ],
      leftBottomButtons: [
        // build2功能
        // {
        //   text: '启用', handle: (e) => {
        //   }
        // },
        // {
        //   text: '停用', handle: (e) => {
        //   }
        // }
      ],
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
        this.queryCondition.bizCondition = obj;
        this.refreshData();
      },
      handleSearch: (event) => {
        const obj = {};
        event.forEach(item => {
          if (item.filterField === 'lastLoginTime') {
            obj['lastLoginTime'] = item.filterValue;
            obj['lastLoginTimeRelation'] = item.operator;
          } else {
            obj[item.filterField] = item.filterValue;
          }
        });
        this.queryCondition.pageCondition.pageNum = 1;
        this.queryCondition.bizCondition = obj;
        this.refreshData();
      }
    };
  }


  /**
   * 跳转新增用户页面
   */
  public openAddUser() {
    this.$router.navigate(['business/user/add-user/add']).then();
  }

  /**
   * 跳转修改用户页面
   */
  public openModifyUser(userId) {
    this.$router.navigate(['business/user/modify-user/update'], {
      queryParams: { id: userId }
    }).then();
  }


  /**
   * 用户状态操作
   */
  clickSwitch(data) {
    if (data && data.id) {
      let statusValue;
      this._dataSet = this._dataSet.map(item => {
        if (data.id === item.id) {
          item.clicked = true;
          if (data.userStatus === '1') {
            item.userStatus = '0';
            statusValue = item.userStatus;
          } else if (data.userStatus === '0') {
            item.userStatus = '1';
            statusValue = item.userStatus;
          }
          return item;
        } else {
          return item;
        }
      });
      this.$userService.updateUserStatus(statusValue, [data.id])
        .subscribe((res: Result) => {
          // this.refreshData();
        });
    }
  }


  /**
   * 查询所有部门
   */
  queryAllDept() {
    this.$userService.queryTotalDepartment().subscribe((res: Result) => {
      if (res.data) {
        res.data.forEach(item => {
          this.deptArray.push({ 'label': item.deptName, 'value': item.deptName });
        });
      }
    });
  }

  /**
   * 查询用户默认密码
   */
  queryUserPassWord() {
    this.$userService.queryPassword().subscribe((res: Result) => {
      this.defaultPassWord = res.data;
    });
  }


  // 跳第一页
  goToFirstPage() {
    this.queryCondition.pageCondition.pageNum = 1;
    this.refreshData();
  }
}
