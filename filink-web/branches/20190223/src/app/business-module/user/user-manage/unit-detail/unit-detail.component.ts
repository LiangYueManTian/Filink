import { Component, OnInit, ViewChild } from '@angular/core';
import { FormItem } from '../../../../shared-module/component/form/form-config';
import { FormOperate } from '../../../../shared-module/component/form/form-opearte.service';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { NzI18nService, NzTreeNode } from 'ng-zorro-antd';
import { UnitLanguageInterface } from '../../../../../assets/i18n/unit/unit-language.interface';
import { TreeSelectorConfig } from '../../../../shared-module/entity/treeSelectorConfig';
import { UserService } from '../../../../core-module/api-service/user/user-manage/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Result } from '../../../../shared-module/entity/result';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { QueryCondition } from '../../../../shared-module/entity/queryCondition';
import { UserUtilService } from '../../user-util.service';

@Component({
  selector: 'app-unit-detail',
  templateUrl: './unit-detail.component.html',
  styleUrls: ['./unit-detail.component.scss']
})
export class UnitDetailComponent implements OnInit {
  formColumn: FormItem[] = [];
  formStatus: FormOperate;
  queryCondition: QueryCondition = new QueryCondition();
  public language: UnitLanguageInterface;
  areaSelectorConfig: any = new TreeSelectorConfig();
  isLoading = false;
  pageType = 'add';
  pageTitle: string;
  unitId: string;
  areaName = '';
  unitInfo: any = {};
  @ViewChild('department') private departmentTep;
  public departmentList: Array<any> = [];
  private treeNodes: any = [];
  areaSelectVisible: boolean = false;
  deptName: string = '';
  selectUnitName: string = '';
  verifyFatherid = null;
  selectData = [];
  constructor(
    private $nzI18n: NzI18nService,
    private $userService: UserService,
    private $active: ActivatedRoute,
    private $router: Router,
    private $message: FiLinkModalService,
    private $userUtilService: UserUtilService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('unit');
    this.initColumn();
    this.pageType = this.$active.snapshot.params.type;
    this.pageTitle = this.getPageTitle(this.pageType);
    if (this.pageType !== 'add') {
      this.$active.queryParams.subscribe(params => {
        this.unitId = params.id;
        const unitId = this.unitId;
        this.getDept().then(() => {
          this.getUnitListById(unitId);
        });
      });
    } else {
      this.$userUtilService.getDept().then((data: NzTreeNode[]) => {
        this.treeNodes = data || [];
        this.initAreaSelectorConfig(data);
      });
    }


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
  deptSelectChange(event) {
    this.selectData = event;
    if (event[0]) {
      this.$userUtilService.setAreaNodesStatus(this.treeNodes, event[0].deptFatherid, this.unitInfo.id);
      this.selectUnitName = event[0].deptName;
      this.unitInfo.deptFatherid = event[0].id; // 单位名称验证传递的参数
    } else {
      this.$userUtilService.setAreaNodesStatus(this.treeNodes, null, this.unitInfo.id);
      this.selectUnitName = '';
      this.unitInfo.deptLevel = 1;
      this.unitInfo.deptFatherid = null;
    }
  }

  /**
 *新增、修改部门
 */
  submit() {
    this.isLoading = true;
    const unitObj = this.formStatus.getData();
    unitObj.deptFatherid = this.unitInfo.deptFatherid;
    if (this.pageType === 'add') {
      if (this.selectData.length === 1) {
        const level = Number(this.selectData[0].deptLevel) + 1;
        unitObj.deptLevel = String(level);
      } else {
        unitObj.deptLevel = '1';
      }
      this.$userService.verifyDeptInfo(unitObj).subscribe((result: Result) => {
        this.isLoading = false;
        if (result['code'] === 0) {
          if (result.data.length === 0) {
            this.$userService.addDept(unitObj).subscribe((res: Result) => {
              if (res['code'] === 0) {
                this.$message.success(this.language.addUnitTips);
                this.$router.navigate(['/business/user/unit-list']).then();
              } else {
                this.$message.error(res['msg']);
              }
            });
          } else if (result.data.length > 0) {
            this.$message.info(this.language.unitNameTips);
          }
        }
      });

    } else if (this.pageType === 'update') {
      if (this.selectData.length === 1) {
        const level = Number(this.selectData[0].deptLevel) + 1;
        unitObj.deptLevel = String(level);
      } else {
        unitObj.deptLevel = String(this.unitInfo.deptLevel);
      }
      unitObj.id = this.unitInfo.id;
      this.$userService.modifyDept(unitObj).subscribe((res: Result) => {
        this.isLoading = false;
        if (res['code'] === 0) {
          this.$message.success(this.language.modifyUnitTips);
          this.$router.navigate(['/business/user/unit-list']).then();
        } else {
          this.$message.error(res['msg']);
        }
      });
    }

  }

  private initColumn() {
    this.formColumn = [
      {
        label: this.language.deptName,
        key: 'deptName',
        type: 'input',
        require: true,
        width: 430,
        // labelHeight: 80,
        rule: [
          { required: true },
          { pattern: /^(?! )[A-Za-z0-9\u4e00-\u9fa5\-_ ]{6,32}$/, msg: this.language.deptNameTips }
        ],
        asyncRules: [
          {
            asyncRule: (control: FormControl) => {
              const params = {
                'deptName': control.value,
                // 'deptFatherid': this.verifyFatherid
              };
              return Observable.create(observer => {
                this.$userService.verifyDeptInfo(params).subscribe((res: Result) => {
                  if (res['code'] === 0) {
                    if (res.data.length === 0) {
                      observer.next(null);
                      observer.complete();
                    } else if (res.data.length === 1 && res.data[0].id === this.unitId) {
                      observer.next(null);
                      observer.complete();
                    } else {
                      observer.next({ error: true, duplicated: true });
                      observer.complete();
                    }
                  }
                });
              });
            },
            asyncCode: 'duplicated', msg: this.language.unitNameTips
          }
        ],
      },
      {
        label: this.language.deptChargeuser,
        key: 'deptChargeuser',
        type: 'input',
        require: false,
        width: 430,
        rule: [{ minLength: 2 }, { maxLength: 32 }],
        modelChange: (controls, event, key, formOperate) => {
        }
      },
      {
        label: this.language.deptPhonenum,
        key: 'deptPhonenum',
        type: 'input',
        require: false,
        width: 430,
        rule: [
          { pattern: /^[1][3,4,5,6,7,8,9][0-9]{9}$/, msg: this.language.phoneNumTips }],
        asyncRules: []
      },
      {
        label: this.language.address,
        key: 'address',
        type: 'input',
        require: false,
        width: 430,
        rule: [{ maxLength: 200 }],
        modelChange: (controls, event, key, formOperate) => {
        }
      },
      {
        label: this.language.deptFatherid,
        key: 'deptFatherid',
        type: 'custom',
        require: false,
        width: 430,
        rule: [],
        asyncRules: [],
        template: this.departmentTep
      },
      {
        label: this.language.remark,
        key: 'remark',
        type: 'textarea',
        require: false,
        width: 430,
        rule: [{ maxLength: 200 }]
      },
    ];

  }

  private initAreaSelectorConfig(nodes) {
    this.areaSelectorConfig = {
      width: '500px',
      height: '300px',
      title: this.language.unitSelect,
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


  private getPageTitle(type): string {
    let title;
    switch (type) {
      case 'add':
        title = `${this.language.addUnit}${this.language.unit}`;
        break;
      case 'update':
        title = `${this.language.update}${this.language.unit}`;
        break;
    }
    return title;
  }



  goBack() {
    this.$router.navigate(['/business/user/unit-list']).then();
  }


  /**
  * 获取单个部门信息
  *
  */
  public getUnitListById(unitId) {
    this.$userService.queryDeptInfoById(unitId).subscribe((res: Result) => {
      const unitInfo = res['data'];
      this.unitInfo = unitInfo;
      this.unitInfo.deptLevel = Number(this.unitInfo.deptLevel);
      this.selectUnitName = unitInfo.parmentDeparmentName;
      this.formStatus.resetData(unitInfo);
      // 递归设置部门的选择情况
      this.$userUtilService.setAreaNodesStatus(this.treeNodes, unitInfo.deptFatherid, this.unitInfo.id);
    });
  }


}
