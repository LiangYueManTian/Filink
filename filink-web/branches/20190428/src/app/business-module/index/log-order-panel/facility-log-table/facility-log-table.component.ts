import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {IndexTable} from '../../index.table';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {Result} from '../../../../shared-module/entity/result';

@Component({
  selector: 'app-facility-log-table',
  templateUrl: './facility-log-table.component.html',
  styleUrls: ['./facility-log-table.component.scss']
})
export class FacilityLogTableComponent extends IndexTable implements OnInit, OnChanges {
  @Input() facilityId: string;
  constructor(
    public $nzI18n: NzI18nService,
    private $router: Router,
    private $facilityService: FacilityService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.initTableConfig();
    this.refreshData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      this.refreshData();
    }
  }

  refreshData() {
    this.createQueryConditions();
    this.tableConfig.isLoading = true;
    this.$facilityService.queryDeviceLogListByPage(this.queryCondition).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data;
      } else {
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * 创建查询条件
   */
  createQueryConditions() {
    this.queryCondition.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // 时间降序
    this.queryCondition.sortCondition =  {
      sortField: 'currentTime',
      sortRule: 'desc'
    };

    // 设施id过滤
    this.queryCondition.filterConditions = [
      {
        operator: 'eq',
        filterField: 'deviceId',
        filterValue: this.facilityId
      }
    ];
  }


  /**
   * 跳转至对应日志
   * param id
   */
  goToFacilityLogById(id) {
    this.$router.navigate([`/business/facility/facility-log`], {queryParams: {logId: id}}).then();
  }

  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '366px', y: '250px'},
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.indexLanguage.logName, key: 'logName', width: 86,
          searchable: false
        },
        {
          title: this.indexLanguage.extraInfo, key: 'remarks', width: 85,
          searchable: false,
        },
        {
          title: this.indexLanguage.time, key: 'currentTime', width: 70, pipe: 'date',
          searchable: false,
        },
        {
          title: this.commonLanguage.operate, searchable: false,
          searchConfig: {type: 'operate'}, key: '', width: 100, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      operation: [
        {
          text: this.indexLanguage.viewLog,
          className: 'fiLink-log',
          handle: (currentIndex) => {
            this.goToFacilityLogById(currentIndex.logId);
          }
        },
      ]
    };
  }
}
