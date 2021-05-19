import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Router} from '@angular/router';
import {MapService} from '../../../../core-module/api-service/index/map';
import {IndexTable} from '../../index.table';
import {NzI18nService} from 'ng-zorro-antd';
import {Result} from '../../../../shared-module/entity/result';

/**
 * 操作记录工单组件
 */
@Component({
  selector: 'app-operation-record',
  templateUrl: './operation-record.component.html',
  styleUrls: ['./operation-record.component.scss']
})
export class OperationRecordComponent extends IndexTable implements OnInit, OnChanges {
  // 设施id
  @Input() facilityId: string;
  constructor(
    // 国际化
    public $nzI18n: NzI18nService,
    // 路由
    private $router: Router,
    // 地图服务
    private $mapService: MapService,
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

  /**
   * 刷新数据
   */
  refreshData() {
    this.createQueryConditions();
    this.tableConfig.isLoading = true;
    // 在表格查询过滤条件中添加optObjId
    this.queryCondition.filterConditions[0].filterValue = this.facilityId;
    this.queryCondition.filterConditions[0].filterField = 'optObjId';
    this.$mapService.findOperateLog(this.queryCondition).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data.map(_item => {
          if (_item.optResult === 'success') {
            _item.optResult = this.indexLanguage.success;
          } else {
            _item.optResult = this.indexLanguage.failure;
          }
          return _item;
        });
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

  /**
   * 初始化表格配置
   */
  initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: false,
      scroll: {x: '366px', y: '250px'},
      topButtons: [],
      noIndex: true,
      columnConfig: [
        {
          title: this.indexLanguage.operationPerson, key: 'optUserName', width: 86,
          searchable: false
        },
        {
          title: this.indexLanguage.operationTime, key: 'optTime', width: 85, pipe: 'date',
          searchable: false,
        },
        {
          title: this.indexLanguage.operationResult, key: 'optResult', width: 70,
          searchable: false,
        },
        {
          title: this.indexLanguage.detailInfo, searchable: false,
          searchConfig: {type: 'operate'}, key: 'detailInfo', width: 100, fixedStyle: {fixedRight: true, style: {right: '0px'}}
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

