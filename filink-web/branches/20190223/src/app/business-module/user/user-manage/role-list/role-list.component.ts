import { Component, OnInit } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService } from 'ng-zorro-antd';
import { UserService } from '../../../../core-module/api-service/user/user-manage/user.service';
import { Result } from '../../../../shared-module/entity/result';
import { RoleLanguageInterface } from '../../../../../assets/i18n/role/role-language.interface';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';

@Component({
  selector: 'app-role-list',
  templateUrl: './role-list.component.html',
  styleUrls: ['./role-list.component.scss']
})

export class RoleListComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  public language: RoleLanguageInterface;
  flag: boolean = true;
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $userService: UserService,
    public $message: FiLinkModalService
  ) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('role');
    this.initTableConfig();
    this.refreshData();
  }

  /**
   * 获取角色列表信息
   */

  refreshData() {
    this.tableConfig.isLoading = true;
    this.$userService.queryRoles(this.queryCondition).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      this._dataSet = res.data.data;
      this.pageBean.Total = res.data.totalCount;
      this.pageBean.pageIndex = res.data.pageNum;
      this.pageBean.pageSize = res.data.size;
      this._dataSet.forEach(item => {
        if (item.defaultRole === 0) {
          item.isShowDeleteIcon = true;
        }
        if (item.defaultRole === 1) {
          item.isShowDeleteIcon = false;
        }
      });
      // console.log(this._dataSet);
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }


  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 初始化表格配置
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1200px', y: '340px' },
      noIndex: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '62px' } }
        },
        {
          title: this.language.roleName, key: 'roleName', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '124px' } }
        },
        {
          title: this.language.remark, key: 'remark', width: 300, configurable: true, isShowSort: true,
          searchable: true,
          remarkPipe: 'remark',
          searchConfig: { type: 'input' },
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
      topButtons: [
        {
          text: '+  ' + this.language.addUser,
          handle: (currentIndex) => {
            this.$router.navigate(['business/user/role-detail/add']).then();
          }
        },
        {
          text: this.language.batchDelete,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          needConfirm: true,
          canDisabled: true,
          handle: (currentIndex) => {
            this.flag = true;
            if (currentIndex.length > 0) {
              currentIndex.forEach(item => {
                if (item.defaultRole === 1) {
                  this.flag = false;
                }
              });
              if (!this.flag) {
                this.$message.info(this.language.defaultRoleTips);
              }
              if (this.flag) {
                const ids = currentIndex.map(item => item.id);
                const params = { 'firstArrayParamter': ids };
                this.$userService.deleteRole(params).subscribe(res => {
                  if (res['code'] === 0) {
                    this.$message.success(res['msg']);
                    this.goToFirstPage();
                  } else if (res['code'] === 120270) {
                    this.$message.info(res['msg']);
                  } else {
                    this.$message.error(res['msg']);
                  }
                });
              }
            } else {
              return;
            }
          }
        },
      ],
      operation: [
        // build2功能
        // {
        //   text: this.language.roleSet,
        //   className: 'icon_role_set',
        //   handle: (currentIndex) => {
        //   }
        // },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.openModifyRole(currentIndex.id);
          }
        },
        {
          text: this.language.deleteHandle,
          canDisabled: true,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          key: 'isShowDeleteIcon',
          handle: (currentIndex) => {
            if (currentIndex.defaultRole === 1) {
              this.$message.info(this.language.defaultRoleTips);
            } else if (currentIndex.defaultRole === 0) {
              const params = { 'firstArrayParamter': [currentIndex.id] };
              this.$userService.deleteRole(params).subscribe(res => {
                if (res['code'] === 0) {
                  this.$message.success(res['msg']);
                  this.goToFirstPage();
                } else if (res['code'] === 120270) {
                  this.$message.info(res['msg']);
                } else {
                  this.$message.error(res['msg']);
                }
              });
            }

          }
        },
      ],
      sort: (event: SortCondition) => {
        const obj = {};
        obj['sortProperties'] = event.sortField;
        obj['sort'] = event.sortRule;
        if (obj['sortProperties'] === 'roleName') {
          obj['sortProperties'] = 'role_name';
        }
        if (obj['sortProperties'] === 'roleDesc') {
          obj['sortProperties'] = 'role_desc';
        }
        this.queryCondition.bizCondition = obj;
        this.refreshData();
      },
      handleSearch: (event) => {
        const obj = {};
        event.forEach(item => {
          obj[item.filterField] = item.filterValue;
        });
        this.queryCondition.pageCondition.pageNum = 1;
        this.queryCondition.bizCondition = obj;
        this.refreshData();
      }
    };
  }


  /**
  * 跳转修改角色页面
  */
  public openModifyRole(userId) {
    this.$router.navigate(['business/user/role-detail/update'], {
      queryParams: { id: userId }
    }).then();
  }


  // 跳第一页
  goToFirstPage() {
    this.queryCondition.pageCondition.pageNum = 1;
    this.refreshData();
  }
}
