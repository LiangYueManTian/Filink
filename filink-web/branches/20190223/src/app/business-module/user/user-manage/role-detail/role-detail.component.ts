import { Component, OnInit } from '@angular/core';
import { FormItem } from '../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../shared-module/component/form/form-opearte.service';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { NzI18nService } from 'ng-zorro-antd';
import { RoleLanguageInterface } from '../../../../../assets/i18n/role/role-language.interface';
import { UserService } from '../../../../core-module/api-service/user/user-manage/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Result } from '../../../../shared-module/entity/result';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';

@Component({
  selector: 'app-role-detail',
  templateUrl: './role-detail.component.html',
  styleUrls: ['./role-detail.component.scss']
})

export class RoleDetailComponent implements OnInit {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  public language: RoleLanguageInterface;
  isVisible = false;
  pageType = 'add';
  pageTitle: string;
  roleId: string;
  isLoading: boolean = false;
  roleNameDisable: boolean;
  roleInfo = {};
  constructor(
    private $nzI18n: NzI18nService,
    private $userService: UserService,
    private $active: ActivatedRoute,
    private $router: Router,
    private $message: FiLinkModalService) {
  }


  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('role');
    this.pageType = this.$active.snapshot.params.type;
    this.pageTitle = this.getPageTitle(this.pageType);
    if (this.pageType !== 'add') {
      this.$active.queryParams.subscribe(params => {
        this.roleId = params.id;
        const roleId = this.roleId;
        // 获取单个角色信息
        this.$userService.queryRoleInfoById(roleId).subscribe((res: Result) => {
          const roleInfo = res.data;
          this.roleInfo = roleInfo;
          // console.log(roleInfo);
          this.formStatus.resetData(roleInfo);
          if (roleInfo.defaultRole === 1) {
            this.roleNameDisable = true;
          }
          if (roleInfo.defaultRole === 0) {
            this.roleNameDisable = false;
          }
        });
      });
    }
    this.initColumn();
  }

  formInstance(event) {
    this.formStatus = event.instance;
  }


  private initColumn() {
    this.formColumn = [
      {
        label: this.language.roleName,
        key: 'roleName',
        type: 'input',
        require: true,
        width: 430,
        // labelHeight: 70,
        rule: [
          { required: true },
          { pattern: /^(?! )[A-Za-z0-9\u4e00-\u9fa5\-_ ]{6,32}$/, msg: this.language.roleNameTips1 }
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
                  'filterField': 'role_name',
                  'operator': 'eq',
                  'filterValue': control.value
                }]
              };
              return Observable.create(observer => {
                this.$userService.verifyRoleInfo(params).subscribe((res: Result) => {
                  if (res['code'] === 0) {
                    if (res.data.length === 0) {
                      observer.next(null);
                      observer.complete();
                    } else if (res.data.length > 0) {
                      if (res.data[0].id === this.roleId) {
                        observer.next(null);
                        observer.complete();
                      } else {
                        observer.next({ error: true, duplicated: true });
                        observer.complete();
                      }
                    }
                  }
                });
              });
            },
            asyncCode: 'duplicated', msg: this.language.roleNameTips2
          }
        ],
        modelChange: (controls, $event, key) => {
        },
        openChange: (a, b, c) => {
        },
      },
      {
        label: this.language.remark,
        key: 'remark',
        type: 'textarea',
        require: false,
        width: 430,
        rule: [{ maxLength: 200 }],
        asyncRules: []
      }
    ];
    setTimeout(() => {
      if (this.roleNameDisable) {
        this.formStatus.group.controls['roleName'].disable();
      } else {
        this.formStatus.group.controls['roleName'].enable();
      }
    }, 380);

  }


  private getPageTitle(type): string {
    let title;
    switch (type) {
      case 'add':
        title = `${this.language.addUser}${this.language.role}`;
        break;
      case 'update':
        title = `${this.language.update}${this.language.role}`;
        break;
    }
    return title;
  }



  goBack() {
    this.$router.navigate(['/business/user/role-list']).then();
  }

  /**
  *新增、修改角色
  */
  submit() {
    this.isLoading = true;
    if (this.pageType === 'add') {
      const roleObj = this.formStatus.getData();
      this.$userService.addRole(roleObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res['code'] === 0) {
          this.$message.success(res['msg']);
          this.$router.navigate(['/business/user/role-list']).then();
        } else {
          this.$message.error(res['msg']);
        }
      });

    } else if (this.pageType === 'update') {
      const roleObj = this.formStatus.getData();
      if (this.roleInfo['defaultRole'] === 1) {
        roleObj.defaultRole = 1;
      } else {
        roleObj.defaultRole = 0;
      }
      roleObj.id = this.roleId;
      // console.log(roleObj);
      this.$userService.modifyRole(roleObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res['code'] === 0) {
          this.$message.success(res['msg']);
          this.$router.navigate(['/business/user/role-list']).then();
        } else {
          this.$message.error(res['msg']);
        }
      });
    }

  }

}
