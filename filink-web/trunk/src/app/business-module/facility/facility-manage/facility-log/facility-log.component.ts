import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {FilterCondition, QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {DateHelperService, NzI18nService} from 'ng-zorro-antd';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {getDeviceType, getLogType, getNodeObjectType} from '../../facility.config';
import {Result} from '../../../../shared-module/entity/result';
import {FacilityLog} from '../../../../core-module/entity/facility/facility-log';
import {ActivatedRoute, Router} from '@angular/router';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {CommonUtil} from '../../../../shared-module/util/common-util';

/**
 * 设施日志列表组件
 */
@Component({
  selector: 'app-facility-log',
  templateUrl: './facility-log.component.html',
  styleUrls: ['./facility-log.component.scss']
})
export class FacilityLogComponent implements OnInit {
  // 列表数据
  _dataSet: FacilityLog[] = [];
  // 列表分页
  pageBean: PageBean = new PageBean(10, 1, 1);
  // 列表配置
  tableConfig: TableConfig;
  // 设施语言包
  public language: FacilityLanguageInterface;
  // 列表查询条件
  queryCondition: QueryCondition = new QueryCondition();
  // 设施名称模板
  @ViewChild('deviceName') deviceName: TemplateRef<any>;
  // 设施编号模板
  @ViewChild('deviceCode') deviceCode: TemplateRef<any>;
  // 设施类型模板
  @ViewChild('deviceTypeTemp') deviceTypeTemp: TemplateRef<any>;
  // 设施id
  deviceId;
  // 日志id
  logId;

  constructor(private $nzI18n: NzI18nService,
              private $active: ActivatedRoute,
              private $dateHelper: DateHelperService,
              private $router: Router,
              private $message: FiLinkModalService,
              private $facilityService: FacilityService) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    if (this.$active.snapshot.queryParams.id) {
      this.deviceId = this.$active.snapshot.queryParams.id;
      const filter = new FilterCondition('deviceId');
      filter.operator = 'eq';
      filter.filterValue = this.$active.snapshot.queryParams.id;
      this.queryCondition.filterConditions = [filter];
    }
    if (this.$active.snapshot.queryParams.logId) {
      this.logId = this.$active.snapshot.queryParams.logId;
      const filter = new FilterCondition('logId');
      filter.operator = 'eq';
      filter.filterValue = this.$active.snapshot.queryParams.logId;
      this.queryCondition.filterConditions = [filter];
    }
    this.refreshData();
  }

  /**
   * 表格翻页事件
   * param event
   */
  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  /**
   * 跳转到详情页
   * param data
   */
  navigateToDetail(data) {
    this.$router.navigate(['business/facility/facility-detail-view'],
      {queryParams: {id: data.deviceId, deviceType: data._deviceType}}).then();
  }

  /**
   * 初始化表格配置
   */
  private initTableConfig() {
    this.tableConfig = {
      primaryKey: '03-5',
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      scroll: {x: '1000px', y: '340px'},
      noIndex: true,
      showSearchExport: true,
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: true, style: {left: '0px'}}, width: 62},
        {
          type: 'serial-number', width: 62, title: this.language.serialNumber,
          fixedStyle: {fixedLeft: true, style: {left: '62px'}}
        },
        {
          title: this.language.deviceLogName,
          isShowSort: true,
          key: 'logName', width: 100,
          searchable: true,
          fixedStyle: {fixedLeft: true, style: {left: '124px'}},
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.deviceLogType, key: 'logType', width: 100, searchable: true,
          isShowSort: true,
          configurable: true,
          hidden: true,
          searchConfig: {type: 'select', selectInfo: getLogType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceType_a, key: 'deviceType', width: 150,
          configurable: true,
          type: 'render',
          renderTemplate: this.deviceTypeTemp,
          minWidth: 150,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'select', selectType: 'multiple', selectInfo: getDeviceType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.deviceCode_a, key: 'deviceCode', width: 150,
          configurable: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.deviceCode,
          isShowSort: true,
          hidden: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.deviceName_a, key: 'deviceName', width: 124,
          configurable: true,
          isShowSort: true,
          searchable: true,
          type: 'render',
          renderTemplate: this.deviceName,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.nodeObject, key: 'nodeObject', width: 125,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.parentId, key: 'areaName', width: 100,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'},
        },
        {
          title: this.language.createTime, key: 'currentTime',
          configurable: true,
          minWidth: 180,
          width: 180,
          searchable: true,
          isShowSort: true,
          pipe: 'date',
          searchConfig: {type: 'dateRang'},
        },
        {
          title: this.language.extraRemarks, key: 'remarks', configurable: true,
          width: 100,
          searchable: true,
          isShowSort: true,
          hidden: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.operate,
          searchable: true,
          searchConfig: {
            type: 'operate',
          }, key: '', width: 100, fixedStyle: {fixedRight: true, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [],
      operation: [],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        this.logId = null;
        this.deviceId = null;
        this.queryCondition.pageCondition.pageNum = 1;
        this.queryCondition.filterConditions = event;
        this.refreshData();
      },
      handleExport: (event) => {
        // 处理参数
        const body = {
          queryCondition: new QueryCondition(),
          columnInfoList: event.columnInfoList,
          excelType: event.excelType
        };
        body.columnInfoList.forEach(item => {
          if (item.propertyName === 'deviceType'
            || item.propertyName === 'logType'
            || item.propertyName === 'currentTime'
          ) {
            item.isTranslation = 1;
          }
        });
        // 处理选择的项目
        if (event.selectItem.length > 0) {
          const ids = event.selectItem.map(item => item.logId);
          const filter = new FilterCondition('logId');
          filter.filterValue = ids;
          filter.operator = 'in';
          body.queryCondition.filterConditions.push(filter);
        } else {
          // 处理查询条件
          body.queryCondition.filterConditions = event.queryTerm;
          if (this.deviceId) {
            const filter = new FilterCondition('deviceId');
            filter.operator = 'eq';
            filter.filterValue = this.deviceId;
            body.queryCondition.filterConditions.push(filter);
          }
          if (this.logId) {
            const filter = new FilterCondition('logId');
            filter.operator = 'eq';
            filter.filterValue = this.logId;
            body.queryCondition.filterConditions.push(filter);
          }
        }
        this.$facilityService.exportLogList(body).subscribe((res: Result) => {
          if (res.code === 0) {
            this.$message.success(res.msg);
          } else {
            this.$message.error(res.msg);
          }
        });
      }
    };
  }

  /**
   * 刷新表格数据
   */
  private refreshData() {
    this.tableConfig.isLoading = true;
    this.$facilityService.queryDeviceLogListByPage(this.queryCondition).subscribe((result: Result) => {
      this.tableConfig.isLoading = false;
      if (result.code === 0) {
        this._dataSet = result.data || [];
        this._dataSet.forEach(item => {
          if (item.deviceType) {
            item['_deviceType'] = item.deviceType;
            item.deviceType = getDeviceType(this.$nzI18n, item.deviceType);
            item['iconClass'] = CommonUtil.getFacilityIconClassName(item['_deviceType']);
          }
          if (item.logType) {
            item.logType = getLogType(this.$nzI18n, item.logType) as string;
          }
        });
        this.pageBean.Total = result.totalCount;
        this.pageBean.pageSize = result.size;
        this.pageBean.pageIndex = result.pageNum;
      } else {
        this.$message.error(result.msg);
      }
    }, () => {
      this.tableConfig.isLoading = false;
    });
  }
}
