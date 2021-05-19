import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService } from 'ng-zorro-antd';
import { UserService } from '../../../../core-module/api-service/user';
import { Result } from '../../../../shared-module/entity/result';
import { OnlineLanguageInterface } from '../../../../../assets/i18n/online/online-language.interface';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';

@Component({
  selector: 'app-online-list',
  templateUrl: './online-list.component.html',
  styleUrls: ['./online-list.component.scss']
})
export class OnlineListComponent implements OnInit {
  _dataSet = [];
  pageBean: PageBean = new PageBean(10, 1, 1);
  tableConfig: TableConfig;
  queryCondition: QueryCondition = new QueryCondition();
  language: OnlineLanguageInterface;

  @ViewChild('roleTemp') roleTemp: TemplateRef<any>;
  @ViewChild('deptTemp') deptTemp: TemplateRef<any>;
  @ViewChild('loginSourseTemp') loginSourseTemp: TemplateRef<any>;
  roleArray = [];
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $userService: UserService,
    public $message: FiLinkModalService
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('online');
    this.initTableConfig();
    this.refreshData();
    this.queryAllRoles();

  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  private refreshData() {
    this.tableConfig.isLoading = true;
    this.$userService.getOnLineUser(this.queryCondition).subscribe((res: Result) => {
      this.tableConfig.isLoading = false;
      this._dataSet = res.data.data;
      this.pageBean.Total = res.data.totalCount;
      this.pageBean.pageIndex = res.data.pageNum;
      this.pageBean.pageSize = res.data.size;
      this._dataSet.forEach(item => {
        if (item.loginTime) {
          item.loginTime = this.$nzI18n.formatDateCompatible(new Date(item.loginTime), 'yyyy-MM-dd HH:mm:ss');
        }
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
      scroll: { x: '1600px', y: '340px' },
      noIndex: true,
      columnConfig: [
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '0px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '62px' } }
        },
        {
          title: this.language.userNickname, key: 'userNickname', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '124px' } }
        },
        {
          title: this.language.userCode, key: 'userCode', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.userName, key: 'userName', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.department, key: 'deptName', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.address, key: 'address', width: 200, isShowSort: true,
          isCustomFilter: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.phonenumber, key: 'phoneNumber', width: 200, isShowSort: true,
          isCustomFilter: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.email, key: 'email', width: 200, isShowSort: true,
          isCustomFilter: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' },
        },
        {
          title: this.language.role, key: 'roleName', width: 150, isShowSort: true,
          configurable: true,
          searchable: true,
          // searchConfig: { type: 'input' },
          searchConfig: {
            type: 'select', selectInfo: this.roleArray
          },
          handleFilter: ($event) => {
          }
        },
        {
          title: this.language.loginTime, key: 'loginTime', width: 280, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'date' }
        },
        {
          title: this.language.lastLoginIp, key: 'loginIp', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.loginSourse, key: 'loginSource', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.loginSourseTemp,
          searchConfig: {
            type: 'select', selectInfo: [
              { label: this.language.pcTerminal, value: '1' },
              { label: this.language.mobileTerminal, value: '0' }
            ]
          },
          handleFilter: ($event) => {
          }
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: { type: 'operate' }, key: '', width: 150, fixedStyle: { fixedRight: true, style: { right: '0px' } }
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      searchReturnType: 'Array',
      topButtons: [
        {
          text: this.language.offline,
          needConfirm: true,
          canDisabled: true,
          confirmContent: this.language.mandatoryOfflineTips,
          handle: (data) => {
            const params = {};
            data.forEach(item => {
              params[item.id] = item['userId'];
            });
            this.$userService.offline(params).subscribe(res => {
              if (res['code'] === 0) {
                this.$message.success(res['msg']);
                this.goToFirstPage();
              } else if (res['code'] === 120290) {
                this.$message.info(res['msg']);
              } else {
                this.$message.error(res['msg']);
              }
            });
          }
        },
        {
          text: this.language.refresh,
          className: 'fiLink-refresh',
          handle: (currentIndex) => {
            this.refreshData();
          }
        }
      ],
      operation: [
        {
          text: this.language.offline,
          className: 'fiLink-lost',
          needConfirm: true,
          confirmContent: this.language.mandatoryOfflineTips,
          handle: (currentIndex) => {
            const _params = {};
            _params[currentIndex.id] = currentIndex.userId;
            this.$userService.offline(_params).subscribe(res => {
              if (res['code'] === 0) {
                this.$message.success(res['msg']);
                this.goToFirstPage();
              } else if (res['code'] === 120290) {
                this.$message.info(res['msg']);
              } else {
                this.$message.error(res['msg']);
              }
            });
          }
        }
      ],
      sort: (event: SortCondition) => {
        const obj = {};
        obj['sortProperties'] = event.sortField;
        obj['sort'] = event.sortRule;
        this.queryCondition.bizCondition = obj;
        this.refreshData();
      },
      handleSearch: (event) => {
        const obj = {};
        event.forEach(item => {
          if (item.filterField === 'loginTime') {
            obj['loginTime'] = item.filterValue;
            obj['relation'] = item.operator;
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


  // 跳第一页
  goToFirstPage() {
    this.queryCondition.pageCondition.pageNum = 1;
    this.refreshData();
  }
}
