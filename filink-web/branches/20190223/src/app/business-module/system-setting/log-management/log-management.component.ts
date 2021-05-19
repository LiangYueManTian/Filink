import {Component, OnInit, ViewChild} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {ColumnConfigService} from '../column-config.service';
import {ActivatedRoute, UrlSegment} from '@angular/router';
import {LogManageService} from '../../../core-module/api-service/system-setting/log-manage/log-manage.service';
import {Result} from '../../../shared-module/entity/result';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';
import {BasicConfig} from '../../basic-config';
import {PageBean} from '../../../shared-module/entity/pageBean';
import {CommonUtil} from '../../../shared-module/util/common-util';

@Component({
  selector: 'app-log-management',
  templateUrl: './log-management.component.html',
  styleUrls: ['./log-management.component.scss']
})
export class LogManagementComponent extends BasicConfig implements OnInit {
  // 日志类型
  optType = 'operate';

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
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: {x: '2000px', y: '325px'},
      columnConfig: this.$columnConfigService.getLogManagementColumnConfig(
        {dangerLevel: this.dangerLevel, optResult: this.optResult, optType: this.optTypeTem}),
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [],
      sort: (e) => {
        this.queryConditions.sortCondition = e;
        this.searchLogList();
      },
      handleSearch: (event) => {
        this.queryConditions.filterConditions = event;
        this.pageBean = new PageBean(this.pageBean.pageSize, 1, 1);
        this.searchLogList();
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
      this.searchLogList();
    });
    this.searchLogList();
  }

  /**
   * 监听页面切换
   * param event
   */
  pageChange(event) {
    this.pageBean.pageIndex = event.pageIndex;
    this.pageBean.pageSize = event.pageSize;
    this.searchLogList();
  }

  /**
   * 查询日志列表
   */
  searchLogList() {
    this.createQueryConditions();
    let httpName = '';
    if (this.optType === 'security') {
      httpName = 'findSecurityLog';
    } else if (this.optType === 'system') {
      httpName = 'findSystemLog';
    } else {
      httpName = 'findOperateLog';
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
   * 创建查询条件  主要是条件拼接
   */
  createQueryConditions() {
    this.queryConditions.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // 默认操作时间降序
    if (!this.queryConditions.sortCondition['sortField']) {
      this.queryConditions.sortCondition =  {
        sortField: 'optTime',
        sortRule: 'desc'
      };
    }

    // 默认查询最近一周
    if (this.queryConditions.filterConditions.length === 0) {
      this.queryConditions.filterConditions = [
        {
          filterField: 'optTime',
          filterValue: (new Date(CommonUtil.funDate(-7))).getTime(),
          operator: 'gt'
        }
      ];
    }
    // 别的页面跳转过来参数拼接
    if ('id' in this.$activatedRoute.snapshot.queryParams) {
      if (!this.queryConditions.filterConditions.some(item => item.filterField === 'optObjId')) {
        this.queryConditions.filterConditions.push({
          filterField: 'optObjId',
          filterValue: this.$activatedRoute.snapshot.queryParams.id,
          operator: 'eq'
        });
      }
    } else {
      this.queryConditions.filterConditions = this.queryConditions.filterConditions.filter(item => item.filterField !== 'optObjId');
    }
  }
}
