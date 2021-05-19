import { Component, OnInit, ViewChild } from '@angular/core';
import { FormItem } from '../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../shared-module/component/form/form-opearte.service';
import { NzI18nService, NzTreeNode } from 'ng-zorro-antd';
import { UserLanguageInterface } from '../../../../../assets/i18n/user/user-language.interface';
import { TreeSelectorConfig } from '../../../../shared-module/entity/treeSelectorConfig';
import { UserService } from '../../../../core-module/api-service/user';
import { ActivatedRoute, Router } from '@angular/router';
import { Result } from '../../../../shared-module/entity/result';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { QueryCondition } from '../../../../shared-module/entity/queryCondition';
import { UserUtilService } from '../../user-util.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss']
})

export class AddUserComponent implements OnInit {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  queryCondition: QueryCondition = new QueryCondition();
  areaSelectorConfig: any = new TreeSelectorConfig();
  public language: UserLanguageInterface;
  public departmentId: string;
  public departmentList: Array<any> = [];
  public roleList: Array<any> = [];
  public pageType = 'add';
  public pageTitle: string;
  public defaultStatus: string = '1'; // 默认状态
  public treeNodes: any = [];
  isLoading = false;
  timeValue: number = null;
  timeType: string = 'day';
  @ViewChild('accountLimit') private accountLimitTemp;
  @ViewChild('department') private departmentTep;
  areaSelectVisible: boolean = false;
  deptName: string = '';
  selectUnitName: string = '';
  selectorData: any;
  userInfo: any = {};
  maxUsers: number;
  loginType: string;
  constructor(
    public $nzI18n: NzI18nService,
    public $userService: UserService,
    public $message: FiLinkModalService,
    public $active: ActivatedRoute,
    public $router: Router,
    public $userUtilService: UserUtilService
  ) { }


  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('user');
    this.queryAllRoles();
    this.initForm();
    this.pageType = this.$active.snapshot.params.type;
    this.pageTitle = this.getPageTitle(this.pageType);
    this.getDept();
  }

  getDept() {
    return new Promise((resolve, reject) => {
      this.$userUtilService.getDept().then((data: NzTreeNode[]) => {
        this.treeNodes = data || [];
        this.initAreaSelectorConfig(data);
        resolve();
      });
    });
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }


  /**
 * 打开部门选择器
 */
  showDeptSelectorModal() {
    this.areaSelectorConfig.treeNodes = this.treeNodes;
    this.areaSelectVisible = true;
  }


  /**
  * 部门选中结果
  * param event
  */
  areaSelectChange(event) {
    this.userInfo.deptId = event[0].id;
    if (event[0]) {
      this.$userUtilService.setAreaNodesStatus(this.treeNodes, event[0].id);
      this.selectUnitName = event[0].deptName;
      this.formStatus.resetControlData('deptId', this.userInfo.deptId);
    } else {
      this.$userUtilService.setAreaNodesStatus(this.treeNodes, null);
      this.selectUnitName = '';
      this.formStatus.resetControlData('deptId', null);
    }
  }

  public initForm() {
    this.formColumn = [
      {
        label: this.language.userCode,
        key: 'userCode',
        type: 'input',
        require: true,
        width: 430,
        // labelHeight: 80,
        rule: [
          { required: true },
          { pattern: /^(?! )[A-Za-z0-9\u4e00-\u9fa5\-_ ]{6,32}$/, msg: this.language.userCodeTips1 }
        ],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              const params = {
                'pageCondition': {
                  'pageNum': 1,
                  'pageSize': 10
                },
                'filterConditions': [{
                  'filterField': 'userCode',
                  'operator': 'eq',
                  'filterValue': control.value
                }]
              };
              return Observable.create(observer => {
                this.$userService.verifyUserInfo(params).subscribe((res: Result) => {
                  if (res['code'] === 0) {
                    if (res.data.length === 0) {
                      observer.next(null);
                      observer.complete();
                    } else if (res.data.length > 0) {
                      observer.next({ error: true, duplicated: true });
                      observer.complete();
                    }
                  }
                });
              });
            },
            asyncCode: 'duplicated', msg: this.language.userCodeTips2
          }
        ],
      },
      {
        label: this.language.userName,
        key: 'userName',
        type: 'input',
        require: true,
        width: 430,
        rule: [
          { required: true },
          { pattern: /^(?! )[A-Za-z0-9\u4e00-\u9fa5\-_ ]{1,32}$/, msg: this.language.nameLengthTips }
        ],
        modelChange: (controls, event, key, formOperate) => { }
      },
      {
        label: this.language.userNickname,
        key: 'userNickname',
        type: 'input',
        require: true,
        width: 430,
        rule: [
          { required: true },
          { pattern: /^(?! )[A-Za-z0-9\u4e00-\u9fa5\-_ ]{1,32}$/, msg: this.language.nameLengthTips }
        ],
        asyncRules: []
      },
      {
        label: this.language.userStatus,
        key: 'userStatus',
        type: 'radio',
        require: true,
        width: 430,
        rule: [{ required: true }],
        initialValue: this.defaultStatus,
        radioInfo: {
          data: [
            { label: this.language.enable, value: '1' },
            { label: this.language.disable, value: '0' },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, event, key, formOperate) => { }
      },
      {
        label: this.language.deptId,
        key: 'deptId',
        type: 'custom',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        template: this.departmentTep
      },
      {
        label: this.language.roleId,
        key: 'roleId',
        type: 'select',
        require: true,
        width: 430,
        rule: [{ required: true }],
        asyncRules: [],
        selectInfo: {
          data: this.roleList,
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => { }
      },
      {
        label: this.language.address,
        key: 'address',
        type: 'input',
        require: false,
        width: 430,
        rule: [{ maxLength: 64 }],
        asyncRules: []
      },
      {
        label: this.language.phonenumber,
        key: 'phonenumber',
        type: 'input',
        require: false,
        width: 430,
        rule: [{ pattern: /^[1][3,4,5,6,7,8,9][0-9]{9}$/, msg: this.language.phoneNumberTip1 }],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              const params = {
                'pageCondition': {
                  'pageNum': 1,
                  'pageSize': 10
                },
                'filterConditions': [{
                  'filterField': 'phonenumber',
                  'operator': 'eq',
                  'filterValue': control.value
                }]
              };
              return Observable.create(observer => {
                this.$userService.verifyUserInfo(params).subscribe((res: Result) => {
                  if (res['code'] === 0) {
                    if (res.data.length === 0) {
                      observer.next(null);
                      observer.complete();
                    } else if (res.data.length > 0) {
                      observer.next({ error: true, duplicated: true });
                      observer.complete();
                    }
                  }
                });
              });
            },
            asyncCode: 'duplicated', msg: this.language.phoneNumberTips2
          }
        ]
      },
      {
        label: this.language.email,
        key: 'email',
        type: 'input',
        require: false,
        width: 430,
        rule: [{ pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/, msg: this.language.emailTips }],
        asyncRules: [],
        modelChange: (controls, event, key, formOperate) => {
        }
      },
      {
        label: this.language.countValidityTime,
        key: 'countValidityTime',
        type: 'custom',
        require: false,
        width: 430,
        col: 24,
        rule: [],
        asyncRules: [],
        template: this.accountLimitTemp,
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {
        }
      },
      {
        label: this.language.loginType,
        key: 'loginType',
        type: 'radio',
        require: true,
        width: 430,
        rule: [{ required: true }],
        initialValue: '1',
        radioInfo: {
          data: [
            { label: this.language.singleUser, value: '1' },
            { label: this.language.multiUser, value: '2' },
          ],
          label: 'label',
          value: 'value'
        },
        modelChange: (controls, event, key, formOperate) => {
          if (event === '1') {
            this.formStatus.group.controls['maxUsers'].disable();
            this.formStatus.resetControlData('maxUsers', 1);
          } else {
            this.formStatus.group.controls['maxUsers'].enable();
            this.formStatus.resetControlData('maxUsers', null);
          }
        }
      },
      {
        label: this.language.maxUsers,
        key: 'maxUsers',
        type: 'input',
        require: false,
        width: 430,
        initialValue: 1,
        rule: [
          { pattern: /^([2-9]|[1-9]\d|2|100)$/, msg: this.language.maxUsersTips },
        ],
        asyncRules: []
      },
      {
        label: this.language.userdesc,
        key: 'userdesc',
        type: 'textarea',
        require: false,
        width: 430,
        rule: [{ maxLength: 200 }],
        asyncRules: []
      },
    ];
    setTimeout(() => {
      this.formStatus.group.controls['maxUsers'].disable();
    }, 0);

  }

  private initAreaSelectorConfig(nodes) {
    this.areaSelectorConfig = {
      width: '500px',
      height: '300px',
      title: this.language.departmentSelect,
      treeSetting: {
        check: {
          enable: true,
          chkStyle: 'checkbox',
          chkboxType: { 'Y': '', 'N': '' },
        },
        data: {
          simpleData: {
            enable: true,
            idKey: 'id',
          },
          key: {
            name: 'deptName',
            children: 'childDepartmentList'
          },
        },
        view: {
          showIcon: false,
          showLine: false
        }
      },
      treeNodes: nodes
    };
  }


  /**
 *新增用户
 */
  submit() {
    this.isLoading = true;
    const userObj = this.formStatus.getData();
    if (userObj.loginType === '1') {
      userObj.maxUsers = 1;
    }
    this.$userService.addUser(userObj).subscribe((res: Result) => {
      this.isLoading = false;
      if (res['code'] === 0) {
        this.$router.navigate(['business/user/user-list']).then();
        this.$message.success(this.language.addUserSuccess);
      } else if (res['code'] === 120200) {
        this.$message.info(res['msg']);
      } else if (res['code'] === 120420) {
        this.$message.info(res['msg']);
      } else if (res['code'] === 120430) {
        this.$message.info(res['msg']);
      } else {
        this.$message.error(this.language.addUserFail);
      }
    });
  }

  cancel() {
    this.$router.navigate(['business/user/user-list']).then();
  }

  /**
   * 获取所有角色
   */
  queryAllRoles() {
    this.$userService.queryAllRoles().subscribe((res: Result) => {
      if (res['code'] === 0) {
        const roleArray = res['data'];
        if (roleArray) {
          for (let i = 0; i < roleArray.length; i++) {
            this.roleList.push({ 'label': roleArray[i].roleName, 'value': roleArray[i].id });
          }
        }
      }
    });
  }



  private getPageTitle(type): string {
    let title;
    switch (type) {
      case 'add':
        title = `${this.language.addUser}${this.language.user}`;
        break;
      case 'update':
        title = `${this.language.update}${this.language.user}`;
        break;
    }
    return title;
  }

  /**
   * 时间类型下拉框
   */
  timeTypeChange() {
    this.timeValue = null;  // 当发生改变时为空
  }

  /**
   * 输入框输入事件
   */
  onKey(event) {
    const inputValue = String(event.target.value);
    if (!inputValue) {
      this.formStatus.resetControlData('countValidityTime', null);
    } else {
      if (this.timeValue !== null) {
        if (this.timeType === 'year') {
          this.formStatus.resetControlData('countValidityTime', inputValue + 'y');

        } else if (this.timeType === 'month') {
          this.formStatus.resetControlData('countValidityTime', inputValue + 'm');

        } else if (this.timeType === 'day') {
          this.formStatus.resetControlData('countValidityTime', inputValue + 'd');
        }
      }
    }
  }



}
