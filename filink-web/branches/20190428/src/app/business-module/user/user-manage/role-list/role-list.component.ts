import { Component, OnInit, ViewChild, TemplateRef, Output } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService, NzFormatEmitEvent, NzModalService } from 'ng-zorro-antd';
import { UserService } from '../../../../core-module/api-service/user/user-manage/user.service';
import { Result } from '../../../../shared-module/entity/result';
import { RoleLanguageInterface } from '../../../../../assets/i18n/role/role-language.interface';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { rightTreeNodes } from './role.config';
import { getDeviceType } from '../../../facility/facility.config';
import { TreeSelectorConfig } from '../../../../shared-module/entity/treeSelectorConfig';
import { UserUtilService } from '../../user-util.service';

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
  filterObject = {};
  language: RoleLanguageInterface;
  flag: boolean = true;
  isVisible: boolean = false;
  roleId: string;
  leftNodes: any = [];
  rightNodes: any = [];
  treeSelectorConfig: TreeSelectorConfig;
  roleSelectorVisible = false;
  roleSelectorConfig = new TreeSelectorConfig();
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $userService: UserService,
    public $message: FiLinkModalService,
    public $nzModalService: NzModalService,
    private $userUtilService: UserUtilService
  ) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('role');
    this.initTableConfig();
    this.refreshData();
  }

  /**
   * ????????????????????????
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
   * ?????????????????????
   */
  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1200px', y: '600px' },
      noIndex: true,
      notShowPrint: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '62px' } }
        },
        {
          title: this.language.roleName, key: 'roleName', width: 500, isShowSort: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.remark, key: 'remark', width: 300, configurable: true, isShowSort: true,
          searchable: true,
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
          permissionCode: '01-3-1',
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
          permissionCode: '01-3-3',
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
        {
          text: this.language.roleSet,
          className: 'fiLink-collection-facilities',
          permissionCode: '01-3-4',
          handle: (currentIndex) => {
            this.$userService.queryRoleInfoById(currentIndex.id)
              .subscribe((res: Result) => {
                if (res.code === 0) {
                  const roleInfo = res.data;
                  this.roleId = roleInfo.id;
                  const permissionSelectData = roleInfo.permissionList;
                  const facilitySelectData = roleInfo.roleDevicetypeList;
                  const authorityIds = [];   // ????????????id
                  const facilityIds = [];    // ??????id
                  this.$userUtilService.queryTreeNodeListId(permissionSelectData, authorityIds);
                  this.$userUtilService.queryTreeNodeFacilityId(facilitySelectData, facilityIds);
                  this.getPermission().then((data) => {
                    this.leftNodes = data;
                    this.rightNodes = rightTreeNodes;
                    this.initRoleSelectorConfig(this.leftNodes, this.rightNodes);
                    // ???????????????????????????????????????
                    this.$userUtilService.setRoleTreeNodesStatus(this.leftNodes, authorityIds);
                    // ???????????????????????????????????????
                    this.$userUtilService.setFacilityTreeNodesStastus(this.rightNodes, facilityIds);
                    this.roleSelectorVisible = true;
                  });
                } else if (res.code === 120610) {
                  this.$message.info(this.language.roleExistTips);
                  this.goToFirstPage();
                }
              });
          }
        },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          permissionCode: '01-3-2',
          handle: (currentIndex) => {
            this.$userService.queryRoleInfoById(currentIndex.id).subscribe((res: Result) => {
              if (res.code === 0) {
                this.openModifyRole(currentIndex.id);
              } else if (res.code === 120610) {
                this.$message.info(this.language.roleExistTips);
                this.goToFirstPage();
              }
            });
          }
        },
        {
          text: this.language.deleteHandle,
          canDisabled: true,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          key: 'isShowDeleteIcon',
          permissionCode: '01-3-3',
          handle: (currentIndex) => {
            this.$userService.queryRoleInfoById(currentIndex.id).subscribe((res: Result) => {
              if (res.code === 0) {
                if (currentIndex.defaultRole === 1) {
                  this.$message.info(this.language.defaultRoleTips);
                } else if (currentIndex.defaultRole === 0) {
                  const params = { 'firstArrayParamter': [currentIndex.id] };
                  this.$userService.deleteRole(params).subscribe(ress => {
                    if (ress['code'] === 0) {
                      this.$message.success(ress['msg']);
                      this.goToFirstPage();
                    } else if (ress['code'] === 120270) {
                      this.$message.info(ress['msg']);
                    } else {
                      this.$message.error(ress['msg']);
                    }
                  });
                }
              } else if (res.code === 120610) {
                this.$message.info(this.language.roleExistTips);
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
        if (obj['sortProperties'] === 'roleName') {
          obj['sortProperties'] = 'role_name';
        }
        if (obj['sortProperties'] === 'roleDesc') {
          obj['sortProperties'] = 'role_desc';
        }
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


  /**
   * ????????????????????????
   */
  public openModifyRole(userId) {
    this.$router.navigate(['business/user/role-detail/update'], {
      queryParams: { id: userId }
    }).then();
  }



  // ????????????
  goToFirstPage() {
    this.queryCondition.pageCondition.pageNum = 1;
    this.refreshData();
  }


  initRoleSelectorConfig(leftNodes, rightNodes) {
    this.roleSelectorConfig = {
      width: '800px',
      height: '300px',
      title: this.language.permissionAndFacilities,
      treeLeftSetting: {
        check: {
          enable: true,
          chkStyle: 'checkbox',
          chkboxType: { 'Y': 'ps', 'N': 'ps' },
        },
        data: {
          simpleData: {
            enable: true,
            idKey: 'id',
          },
          key: {
            name: 'name',
            children: 'childPermissonList'
          },
        },
        view: {
          showIcon: false,
          showLine: false
        }
      },
      treeRightSetting: {
        check: {
          enable: true,
          chkStyle: 'checkbox',
          chkboxType: { 'Y': 'ps', 'N': 'ps' },
        },
        data: {
          simpleData: {
            enable: true,
            idKey: 'deviceTypeId',
          },
          key: {
            name: 'name',
            children: 'childDevieTypeList'
          },
        },
        view: {
          showIcon: false,
          showLine: false
        }
      },
      rightTitle: this.language.facilities,
      rightNodes: rightNodes,
      leftTitle: `${this.language.operate}${this.language.permission}`,
      leftNodes: leftNodes,
    };
  }


  selectDataChange(event) {
  }


  /**
  * ??????????????????
  */
  private getPermission() {
    return new Promise((resolve) => {
      this.$userService.queryTopPermission().subscribe((result: Result) => {
        const data = result.data || [];
        resolve(data);
      });
    });
  }


}
