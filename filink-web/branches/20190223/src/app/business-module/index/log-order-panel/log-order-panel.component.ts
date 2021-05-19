import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {TableConfig} from '../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../shared-module/entity/pageBean';
import {NzI18nService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {Result} from '../../../shared-module/entity/result';
import {QueryCondition} from '../../../shared-module/entity/queryCondition';
import {FacilityService} from '../../../core-module/api-service/facility/facility-manage';
import {IndexLanguageInterface} from '../../../../assets/i18n/index/index.language.interface';
import {CommonLanguageInterface} from '../../../../assets/i18n/common/common.language.interface';
import {FiLinkModalService} from '../../../shared-module/service/filink-modal/filink-modal.service';

@Component({
  selector: 'app-log-order-panel',
  templateUrl: './log-order-panel.component.html',
  styleUrls: ['./log-order-panel.component.scss']
})
export class LogOrderPanelComponent implements OnInit, OnChanges {
  @Input() facilityId: string;
  pageBean: PageBean = new PageBean(5, 1, 0);
  facilityLogDataSet = [];
  facilityLogTableConfig: TableConfig;
  indexLanguage: IndexLanguageInterface;
  commonLanguage: CommonLanguageInterface;
  queryCondition: QueryCondition = new QueryCondition();
  constructor(
    private $nzI18n: NzI18nService,
    private $router: Router,
    private $facilityService: FacilityService,
    private $message: FiLinkModalService,
  ) { }

  ngOnInit() {
    this.indexLanguage = this.$nzI18n.getLocaleData('index');
    this.commonLanguage = this.$nzI18n.getLocaleData('common');
    this.initFacilityLogTableConfig();
    this.getLogList();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      console.log(this.facilityId);
      this.getLogList();
    }
  }

  initFacilityLogTableConfig() {
    this.facilityLogTableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '440px', y: '250px'},
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.indexLanguage.logName, key: 'logName', width: 100,
          searchable: false
        },
        {
          title: this.indexLanguage.extraInfo, key: 'remarks', width: 80,
          searchable: false,
        },
        {
          title: this.indexLanguage.time, key: 'currentTime', width: 80, pipe: 'date',
          searchable: false,
        },
        {
          title: this.commonLanguage.operate, searchable: false,
          searchConfig: {type: 'operate'}, key: '', width: 90, fixedStyle: {fixedRight: true, style: {right: '0px'}}
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
      ],
      sort: (e) => {
        console.log(e);
      },
      handleSearch: (event) => {
        console.log(event);
      }
    };
  }

  getLogList() {
    this.createQueryConditions();
    this.facilityLogTableConfig.isLoading = true;
    this.$facilityService.queryDeviceLogListByPage(this.queryCondition).subscribe((result: Result) => {
      this.facilityLogTableConfig.isLoading = false;
      this.facilityLogDataSet = result.data;
    }, () => {
      this.facilityLogTableConfig.isLoading = false;
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

  goToFacilityLogList() {
    this.$router.navigate([`/business/facility/facility-log`], {queryParams: {id: this.facilityId}}).then();
  }

  goToFacilityLogById(id) {
    this.$router.navigate([`/business/facility/facility-log`], {queryParams: {logId: id}}).then();
  }

}
