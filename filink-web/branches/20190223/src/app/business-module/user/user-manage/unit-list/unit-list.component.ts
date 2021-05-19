import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { PageBean } from '../../../../shared-module/entity/pageBean';
import { TableConfig } from '../../../../shared-module/entity/tableConfig';
import { Router } from '@angular/router';
import { NzI18nService } from 'ng-zorro-antd';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../../../core-module/api-service/user';
import { Result } from '../../../../shared-module/entity/result';
import { UnitLanguageInterface } from '../../../../../assets/i18n/unit/unit-language.interface';
import { QueryCondition, SortCondition } from '../../../../shared-module/entity/queryCondition';
import { FiLinkModalService } from '../../../../shared-module/service/filink-modal/filink-modal.service';
import { DeptLevel, getDeptLevel } from '../../user.config';

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
  public language: UnitLanguageInterface;
  public deptLevel;
  @ViewChild('deptLevelTemp') deptLevelTemp: TemplateRef<any>;
  @ViewChild('DeptNameTemp') DeptNameTemp: TemplateRef<any>;
  constructor(
    public $router: Router,
    public $nzI18n: NzI18nService,
    public $http: HttpClient,
    public $userService: UserService,
    public $message: FiLinkModalService
  ) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('unit');
    this.initTableConfig();
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

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: { x: '1500px', y: '340px' },
      noIndex: true,
      columnConfig: [
        { type: 'expend', width: 30, expendDataKey: 'childDepartmentList', fixedStyle: { fixedLeft: true, style: { left: '0px' } } },
        { type: 'select', fixedStyle: { fixedLeft: true, style: { left: '30px' } }, width: 62 },
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: { fixedLeft: true, style: { left: '92px' } }
        },
        {
          title: this.language.deptName, key: 'deptName', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' },
          fixedStyle: { fixedLeft: true, style: { left: '154px' } }
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
          title: this.language.deptChargeuser, key: 'deptChargeuser', width: 200, isShowSort: true,
          configurable: true,
          searchable: true,
          searchConfig: { type: 'input' }
        },
        {
          title: this.language.deptPhonenum, key: 'deptPhonenum', width: 200, isShowSort: true,
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
          title: this.language.parmentDeparmentName, key: 'parmentDeparmentName', width: 180, isShowSort: true,
          type: 'render', configurable: true,
          renderTemplate: this.DeptNameTemp
        },
        {
          title: this.language.remark, key: 'remark', width: 200, isShowSort: true,
          isCustomFilter: true,
          configurable: true,
          searchable: true,
          remarkPipe: 'remark',
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
              } else {
                this.$message.error(res['msg']);
              }
            });
          }
        }
      ],
      operation: [
        // build2功能
        // {
        //   text: this.language.staff,
        //   className: 'icon_staff',
        //   handle: (currentIndex) => {
        //   }
        // },
        {
          text: this.language.update,
          className: 'fiLink-edit',
          handle: (currentIndex) => {
            this.navigateToDetail('business/user/unit-detail/update', { queryParams: { id: currentIndex.id } });
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          handle: (currentIndex) => {
            const params = { 'firstArrayParamter': [currentIndex.id] };
            this.$userService.deleteDept(params).subscribe((res: Result) => {
              if (res['code'] === 0) {
                this.$message.success(res['msg']);
                this.goToFirstPage();
              } else if (res['code'] === 120250) {
                this.$message.info(res['msg']);
              } else if (res['code'] === 120260) {
                this.$message.info(res['msg']);
              } else {
                this.$message.error(res['msg']);
              }
            });
          }
        },

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
          obj[item.filterField] = item.filterValue;
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


  // 跳第一页
  goToFirstPage() {
    this.queryCondition.pageCondition.pageNum = 1;
    this.refreshData();
  }
}

