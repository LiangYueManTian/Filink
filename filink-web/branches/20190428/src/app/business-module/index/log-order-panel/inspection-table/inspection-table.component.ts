import {Component, Input, OnChanges, OnInit, SimpleChanges, TemplateRef, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {Result} from '../../../../shared-module/entity/result';
import {NzI18nService} from 'ng-zorro-antd';
import {IndexTable} from '../../index.table';
import {InspectionService} from '../../../../core-module/api-service/work-order/inspection';
import {WORK_ORDER_STATUS_CLASS} from '../../../../shared-module/const/work-order';
import {InspectionLanguageInterface} from '../../../../../assets/i18n/inspection-task/inspection.language.interface';

@Component({
  selector: 'app-inspection-table',
  templateUrl: './inspection-table.component.html',
  styleUrls: ['./inspection-table.component.scss']
})
export class InspectionTableComponent extends IndexTable implements OnInit, OnChanges {
  @Input() facilityId: string;
  @ViewChild('inspectionResultTemp') inspectionResultTemp: TemplateRef<any>;
  @ViewChild('statusTemp') statusTemp: TemplateRef<any>;
  private inspectionLanguage: InspectionLanguageInterface;
  constructor(
    public $nzI18n: NzI18nService,
    private $router: Router,
    private $inspectionService: InspectionService,
  ) {
    super($nzI18n);
  }

  ngOnInit() {
    this.inspectionLanguage = this.$nzI18n.getLocaleData('inspection');
    this.initTableConfig();
    this.refreshData();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.facilityId && changes.facilityId.previousValue) {
      this.refreshData();
    }
  }

  refreshData() {
    // this.createQueryConditions();
    this.tableConfig.isLoading = true;
    this.$inspectionService.getFiveWorkOrder(this.facilityId).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data;
        this._dataSet.forEach(item => {
          item.statusName = this.getWorkOrderStatusName(item.status);
          item.statusClass = this.getStatusClass(item.status);
          if (item.procInspectionRecordVoList) {
            const deviceResult = item.procInspectionRecordVoList.filter(facility => facility.deviceId === this.facilityId);
            if (deviceResult.length > 0) {
              item.result = this.getResult(deviceResult[0].result);
              // ???????????????????????????????????????
              if (deviceResult[0].result === '1' && deviceResult[0].exceptionDescription) {
                item.exceptionDesp = deviceResult[0].exceptionDescription;
              }
            } else {
              item.result = '';
            }
          }
        });
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }

  /**
   * ??????????????????
   * param result     // 0??????  1??????  null?????????
   */
  getResult(result) {
    let _result;
    if (result === '0') {
      _result = this.inspectionLanguage.normal;
    } else if (result === '1') {
      _result = this.inspectionLanguage.abnormal;
    } else {
      _result = '';
    }
    return _result;
  }

  /**
   * ??????????????????
   */
  createQueryConditions() {
    this.queryCondition.pageCondition = {
      pageNum: this.pageBean.pageIndex,
      pageSize: this.pageBean.pageSize
    };

    // ????????????
    this.queryCondition.sortCondition =  {
      sortField: 'currentTime',
      sortRule: 'desc'
    };

    // ??????id??????
    this.queryCondition.bizCondition = {
      deviceIds: [this.facilityId]
    };
  }

  /**
   * ???????????????????????????
   * param id
   */
  goToFacilityLogById(id) {
    this.$router.navigate([`/business/work-order/inspection/unfinished`], {queryParams: {id: id}}).then();
  }

  /**
   * ????????????????????????
   * param status
   * returns {string}
   */
  getStatusClass(status) {
    return `iconfont icon-fiLink ${WORK_ORDER_STATUS_CLASS[status]}`;
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
          title: this.workOrderLanguage.name, key: 'title', width: 76,
          searchable: false
        },
        {
          title: this.workOrderLanguage.status, key: 'statusName', width: 73,
          searchable: false,
          type: 'render',
          renderTemplate: this.statusTemp,
        },
        {
          title: this.workOrderLanguage.expectedCompleteTime, key: 'cTime', width: 72, pipe: 'date',
          searchable: false,
        },
        {
          title: this.workOrderLanguage.accountabilityUnitName, key: 'accountabilityDeptName', width: 70,
          searchable: false,
        },
        {
          title: this.workOrderLanguage.inspectionResult, key: 'result', width: 100,
          type: 'render',
          renderTemplate: this.inspectionResultTemp,
        },
        // {
        //   title: this.commonLanguage.operate, searchable: false,
        //   searchConfig: {type: 'operate'}, key: '', width: 90, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        // },
      ],
      showPagination: false,
      bordered: false,
      showSearch: false,
      operation: [
        {
          text: this.indexLanguage.viewLog,
          className: 'fiLink-log',
          handle: (currentIndex) => {
            this.goToFacilityLogById(currentIndex.procId);
          }
        },
      ]
    };
  }
}

