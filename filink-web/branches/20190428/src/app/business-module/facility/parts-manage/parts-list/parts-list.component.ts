import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {TableConfig} from '../../../../shared-module/entity/tableConfig';
import {PageBean} from '../../../../shared-module/entity/pageBean';
import {Result} from '../../../../shared-module/entity/result';
import {FacilityLanguageInterface} from '../../../../../assets/i18n/facility/facility.language.interface';
import {DateHelperService, NzI18nService, NzModalService} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {FiLinkModalService} from '../../../../shared-module/service/filink-modal/filink-modal.service';
import {LockService} from '../../../../core-module/api-service/lock';
import {FacilityService} from '../../../../core-module/api-service/facility/facility-manage';
import {PartsService} from '../../../../core-module/api-service/facility/parts-manage/parts.service';
import {getPartsType} from '../../facility.config';
import {FilterCondition, QueryCondition, SortCondition} from '../../../../shared-module/entity/queryCondition';
import {DateFormatString} from '../../../../shared-module/entity/dateFormatString';

@Component({
  selector: 'app-parts-list',
  templateUrl: './parts-list.component.html',
  styleUrls: ['./parts-list.component.scss']
})
export class PartsListComponent implements OnInit {
  public _dataSet = [];
  public pageBean: PageBean = new PageBean(10, 1, 1);
  public tableConfig: TableConfig;
  public language: FacilityLanguageInterface;
  queryCondition: QueryCondition = new QueryCondition();

  constructor(private $nzI18n: NzI18nService,
              private $message: FiLinkModalService,
              private $modal: NzModalService,
              private $lockService: LockService,
              private $facilityService: FacilityService,
              private $partsService: PartsService,
              private $dateHelper: DateHelperService,
              private $router: Router) {
  }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
    this.initTableConfig();
    this.refreshData();
  }

  pageChange(event) {
    this.queryCondition.pageCondition.pageNum = event.pageIndex;
    this.queryCondition.pageCondition.pageSize = event.pageSize;
    this.refreshData();
  }

  private initTableConfig() {
    this.tableConfig = {
      isDraggable: true,
      isLoading: true,
      showSearchSwitch: true,
      showSizeChanger: true,
      showSearchExport: true,
      scroll: {x: '900px', y: '600px'},
      columnConfig: [
        {type: 'select', fixedStyle: {fixedLeft: false, style: {left: '0px'}}, width: 62},
        //  {type: 'serial-number', width: 62, title: this.language.serialNumber},
        {
          title: this.language.partName, key: 'partName', width: 124,
          configurable: false,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'},
          fixedStyle: {fixedLeft: true, style: {left: '124px'}}
        },
        {
          title: this.language.partType, key: 'partType', width: 200,
          configurable: true,
          isShowSort: true,
          searchable: true,
          searchConfig: {type: 'select',selectType: 'multiple', selectInfo: getPartsType(this.$nzI18n), label: 'label', value: 'code'}
        },
        {
          title: this.language.department, key: 'department', width: 100,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.depositary, key: 'trustee', width: 120,
          configurable: true,
          searchable: true,
          searchConfig: {type: 'input'}
        },
        {
          title: this.language.time, key: 'ctime', width: 280,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'dateRang'},
        },
        {
          title: this.language.remarks, key: 'remark', width: 180,
          configurable: true,
          searchable: true,
          isShowSort: true,
          searchConfig: {type: 'input'},
        },
        {
          title: this.language.operate, searchable: true,
          searchConfig: {type: 'operate'}, key: '', width: 120, fixedStyle: {fixedRight: false, style: {right: '0px'}}
        },
      ],
      showPagination: true,
      bordered: false,
      showSearch: false,
      topButtons: [
        {
          text: '+ ' + this.language.addParts,
          handle: (currentIndex) => {
            this.addParts();
          },
          permissionCode:'03-4-1-1'
        },
        {
          text: this.language.deleteHandle,
          btnType: 'danger',
          className: 'table-top-delete-btn',
          iconClassName: 'fiLink-delete',
          permissionCode: '03-4-1-3',
          needConfirm: true,
          canDisabled: true,
          handle: (data) => {
            const ids = data.map(item => item.partId);
            this.$partsService.deletePartsDyIds(ids).subscribe((result: Result) => {
              if (result.code === 0) {
                this.$message.success(result.msg);
                this.refreshData();
              } else {
                this.$message.error(result.msg);
              }
            });
          }
        }
      ],
      operation: [
        {
          text: this.language.update,
          className: 'fiLink-edit',
          permissionCode: '03-4-1-2',
          handle: (currentIndex) => {
            this.navigateToDetail('business/facility/parts-detail/update', {queryParams: {partId: currentIndex.partId}});
          }
        },
        {
          text: this.language.deleteHandle,
          needConfirm: true,
          className: 'fiLink-delete red-icon',
          permissionCode: '03-4-1-3',
          handle: (currentIndex) => {
            console.log(currentIndex);
            const id = [currentIndex.partId];
            this.$partsService.deletePartsDyIds(id).subscribe((result: Result) => {
              if (result.code === 0) {
                this.$message.success(result.msg);
                this.refreshData();
              } else {
                this.$message.error(result.msg);
              }
            });
          }
        },
      ],
      leftBottomButtons: [],
      sort: (event: SortCondition) => {
        console.log(event);
        this.queryCondition.sortCondition.sortField = event.sortField;
        this.queryCondition.sortCondition.sortRule = event.sortRule;
        this.refreshData();
      },
      handleSearch: (event) => {
        this.queryCondition.filterConditions = event;
        this.refreshData();
      },
      handleExport: (event) => {
        // ????????????
        const body = {
          queryCondition: new QueryCondition(),
          columnInfoList: event.columnInfoList,
          excelType: event.excelType
        };
        body.columnInfoList.forEach(item => {
          if (item.propertyName === "partType" || item.propertyName === 'ctime') {
            item.isTranslation = 1;
          }
        });
        // ?????????????????????
        if (event.selectItem.length > 0) {
          // body.queryCondition.filterConditions['partId'] = event.selectItem.map(item => item.partId);
          const ids = event.selectItem.map(item => item.partId);
          const filter = new FilterCondition('partId');
          filter.filterValue = ids;
          filter.operator = 'in';
          body.queryCondition.filterConditions.push(filter);
        } else {
          // ??????????????????
          body.queryCondition.filterConditions = event.queryTerm;
        }

        this.partExport(body);
      }
    };
  }


  refreshData() {
    this.tableConfig.isLoading = true;
    this.$partsService.partsListByPage(this.queryCondition).subscribe((result: Result) => {
      this.pageBean.Total = result.totalCount;
      this.tableConfig.isLoading = false;
      this._dataSet = result.data;
      this._dataSet.map(v => {
        if (v.ctime) {
          v.ctime = this.$dateHelper.format(new Date(v.ctime), DateFormatString.DATE_FORMAT_STRING);
        }
        if (v.partType) {
          v.partType = getPartsType(this.$nzI18n, v.partType);
        }
      });
    });
  }

  addParts() {
    this.navigateToDetail(`business/facility/parts-detail/add`);
  }


  /**
   * ???????????????
   * param url
   */
  navigateToDetail(url, extras = {}) {
    this.$router.navigate([url], extras).then();
  }

  /**
   *????????????
   */
  partExport(event) {
    this.$partsService.partsExport(event).subscribe((result: Result) => {
      if (result.code === 0) {
        this.$message.success(result.msg);
        this.refreshData();
      } else {
        this.$message.error(result.msg);
      }
    });
  }
}
