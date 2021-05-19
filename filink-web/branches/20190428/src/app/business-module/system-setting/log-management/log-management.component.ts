import {Component, OnInit, ViewChild} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {ColumnConfigService} from '../column-config.service';
import {ActivatedRoute, UrlSegment} from '@angular/router';
import {LogManageService} from '../../../core-module/api-service/system-setting/log-manage/log-manage.service';
import {Result} from '../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {BasicConfig} from '../../basic-config';
import {QueryCondition} from '../../../shared-module/entity/queryCondition';

@Component({
  selector: 'app-log-management',
  templateUrl: './log-management.component.html',
  styleUrls: ['./log-management.component.scss']
})
export class LogManagementComponent extends BasicConfig implements OnInit {
  // 日志类型
  optType = 'operate';
  // 导出日志类型
  exportLog: string;
  @ViewChild('dangerLevel') private dangerLevel;
  @ViewChild('optResult') private optResult;
  @ViewChild('optType') private optTypeTem;

  constructor(public $nzI18n: NzI18nService,
              private $columnConfigService: ColumnConfigService,
              private $message: FiLinkModalService,
              private $logManageService: LogManageService,
              private $activatedRoute: ActivatedRoute,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    console.log(this.queryConditions);
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      showSearchExport: true,
      scroll: {x: '2000px', y: '325px'},
      columnConfig: this.$columnConfigService.getLogManagementColumnConfig(
        {dangerLevel: this.dangerLevel, optResult: this.optResult, optType: this.optTypeTem}),
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [],
      sort: (e) => {
        this.queryConditions.sortCondition = e;
        this.createQueryConditions(true);
        this.searchList();
      },
      handleSearch: (event) => {
        this.createQueryConditions(true);
        this.pageBean.pageIndex = 1;
        this.queryConditions.pageCondition['pageNum'] = 1;
        this.handleSearch(event);
      },
      // 导出任务
      handleExport: (event) => {
        event.columnInfoList.map(item => {
          if (item.propertyName === 'optTime') {
            return item.isTranslation = 1;
          }
        });
        const body = {
          queryCondition: new QueryCondition(),
          columnInfoList: event.columnInfoList,
          excelType: event.excelType,
        };
        body.columnInfoList.forEach(item => {
          if (item.propertyName === 'dangerLevel') {
            item.isTranslation = 1;
          }
        });
        // body.queryCondition.pageCondition.pageSize = this.pageBean.pageSize;
        // body.queryCondition.pageCondition.pageNum = this.pageBean.pageIndex;
        // 处理选择的项目
        if (event.selectItem.length > 0) {
          event.queryTerm['logIds'] = event.selectItem.map(item => item.logId);
          body.queryCondition.filterConditions = [];
          body.queryCondition.filterConditions.push({filterField: 'logId', operator: 'in', filterValue: event.queryTerm['logIds']});
        } else {
          body.queryCondition.filterConditions = this.queryConditions.filterConditions;
        }
        this.$logManageService[this.exportLog](body).subscribe((result: Result) => {
          if (result.code === 0) {
            this.$message.success(result.msg);
          } else {
            this.$message.info(result.msg);
          }
        });
      }
    };
    // 判断页面  用户日志和系统日志 没有操作类型
    this.$activatedRoute.url.subscribe((urlSegmentList: Array<UrlSegment>) => {
      if (urlSegmentList.find(urlSegment => urlSegment.path === 'security' || urlSegment.path === 'system')) {
        if (urlSegmentList.find(urlSegment => urlSegment.path === 'security')) {
          this.optType = 'security';
        } else {
          this.optType = 'system';
        }
        this.tableConfig.columnConfig = this.tableConfig.columnConfig.filter(item => item.key !== 'optType');
      }
    });
    this.$activatedRoute.queryParams.subscribe(result => {
      this.createQueryConditions(false);
      this.searchList();
    });
    // this.createQueryConditions(false);
    // this.searchList();
  }


  /**
   * 查询日志列表
   */
  searchList() {
    let httpName = '';
    if (this.optType === 'security') {
      // 安全日志
      httpName = 'findSecurityLog';
      this.exportLog = 'exportSecurityLogExport';
    } else if (this.optType === 'system') {
      // 系统日志
      httpName = 'findSystemLog';
      this.exportLog = 'exportSysLogExport';
    } else {
      // 操作日志
      httpName = 'findOperateLog';
      this.exportLog = 'exportOperateLogExport';
    }
    this.tableConfig.isLoading = true;
    this.$logManageService[httpName](this.queryConditions).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data;
        this.pageBean.Total = result.totalCount;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 创建查询条件 分页
   */
  pageChange(event) {
    this.queryConditions.pageCondition = {
      pageNum: event.pageIndex,
      pageSize: event.pageSize
    };
    this.searchList();
  }

  /**
   * 创建查询条件  主要是条件拼接
   */
  createQueryConditions(isHandleSearch?: boolean) {
    this.queryConditions.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // 默认操作时间降序
    if (!this.queryConditions.sortCondition['sortField']) {
      this.queryConditions.sortCondition = {
        sortField: 'optTime',
        sortRule: 'desc'
      };
    }

    // 别的页面跳转过来参数拼接
    if (isHandleSearch) {
      if (!this.queryConditions.filterConditions.some(item => item.filterField === 'optObjId')) {
        this.queryConditions.filterConditions = this.queryConditions.filterConditions.filter(item => item.filterField !== 'optObjId');
      }
    } else {
      if ('id' in this.$activatedRoute.snapshot.queryParams) {
        const ids = this.$activatedRoute.snapshot.queryParams.id;
        if (ids instanceof Array) {
          if (!this.queryConditions.filterConditions.some(item => item.filterField === 'optObjId')) {
            this.queryConditions.filterConditions.push({
              filterField: 'optUserCode',
              filterValue: ids,
              operator: 'in'
            });
          }
        } else {
          if (!this.queryConditions.filterConditions.some(item => item.filterField === 'optObjId')) {
            this.queryConditions.filterConditions.push({
              filterField: 'optObjId',
              filterValue: this.$activatedRoute.snapshot.queryParams.id,
              operator: 'eq'
            });
          }
        }

      } else {
        this.queryConditions.filterConditions = this.queryConditions.filterConditions.filter(item => item.filterField !== 'optObjId');
      }
    }

  }
}
